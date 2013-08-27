package edu.umass.ciir.galago

import java.io.{StringReader, File, IOException}
import org.lemurproject.galago.core.index.AggregateReader
import org.lemurproject.galago.core.retrieval.query.{AnnotatedNode, StructuredQuery, Node}
import org.lemurproject.galago.tupleflow.Parameters
import org.lemurproject.galago.core.parse.Document

import scala.collection.JavaConversions._
import org.lemurproject.galago.core.retrieval.{Retrieval, RetrievalFactory, ScoredPassage, ScoredDocument}
import collection.mutable
import java.util
import com.google.common.cache.{CacheLoader, CacheBuilder, LoadingCache}
import org.lemurproject.galago.core.index.AggregateReader.NodeStatistics
import util.concurrent.TimeUnit

object GalagoSearcher {
  def apply(p: Parameters): GalagoSearcher = {
    new GalagoSearcher(p)
  }
  def apply(index:String, p: Parameters): GalagoSearcher = {
    p.set("index",index)
    new GalagoSearcher(p)
  }

  def apply(index: String): GalagoSearcher = {
    val p = new Parameters
    p.set("index", index)
    new GalagoSearcher(p)
  }

  def apply(jsonConfigFile: File): GalagoSearcher = {
    val p = Parameters.parse(jsonConfigFile)
    new GalagoSearcher(p)
  }

  def apply(server: String, port: Int): GalagoSearcher = {
    val p = new Parameters
    val remoteIndex = "http://" + server + ":" + port
    p.set("index", remoteIndex)
    new GalagoSearcher(p)
  }

}

object GalagoParamTools{
  def myParamCopyFrom(toParams:Parameters,fromParams:Parameters):Parameters = {
    for(key <- fromParams.getKeys) {
      if (fromParams.isBoolean(key)) toParams.set(key, fromParams.getBoolean(key))
      else if (fromParams.isDouble(key)) toParams.set(key, fromParams.getDouble(key))
      else if (fromParams.isLong(key)) toParams.set(key, fromParams.getLong(key))
      else if (fromParams.isString(key)) toParams.set(key, fromParams.getString(key))
      else if (fromParams.isMap(key)) toParams.set(key, fromParams.getMap(key))
      else if  (fromParams.isList(key)) toParams.set(key, fromParams.getAsList(key))
      else {
        throw new RuntimeException("Try to copy params: errornous key "+key+" has unknown type. "+fromParams.toPrettyString)
      }

      //      else if (fromParams.isMap(key)){
      //        val mparams = new Parameters()
      //        fromParams.getMap(key).copyTo(mparams)
      //        toParams.set(key,mparams)
      //    }
    }
    toParams
  }
}

class GalagoSearcher(globalParameters: Parameters) {
  import GalagoParamTools.myParamCopyFrom

  if (globalParameters.isString("index")) println("** Loading index from: " + globalParameters.getString("index"))

  val queryParams = new Parameters
  val m_searcher = RetrievalFactory.instance(globalParameters)


  val documentCache: LoadingCache[(String, Parameters), Document] = CacheBuilder.newBuilder()
                                                                  .maximumSize(100)
                                                                  .expireAfterWrite(10, TimeUnit.MINUTES)
                                                                  .build(
                                                                          new CacheLoader[(String, Parameters), Document]() {
                                                                            def load(key: (String,Parameters)): Document = {
                                                                              pullDocument(key._1, key._2)
                                                                            }
                                                                          })
  def resetDocumentCache() { documentCache.cleanUp() }

  def getDocument(documentName: String, params: Parameters = new Parameters()): Document = {
    documentCache.get(Pair(documentName, params))
  }

  def pullDocument(documentName: String, params: Parameters = new Parameters()): Document = {
    val p = new Parameters()
    myParamCopyFrom(p,globalParameters)
    myParamCopyFrom(p,params)
    getDocument_(documentName, p)
  }

  def getDocuments(documentNames: Seq[String], params: Parameters = new Parameters()): Map[String, Document] = {
    val p = new Parameters()
    myParamCopyFrom(p,globalParameters)
    myParamCopyFrom(p,params)
    getDocuments_(documentNames, p)
  }

  private def getDocuments_(identifier: Seq[String], p: Parameters, tries: Int = 5): Map[String, Document] = {
    try {
      val docmap = m_searcher.getDocuments(identifier, p)
      docmap.toMap
    } catch {
      case ex: NullPointerException => {
        println("NPE while fetching documents " + identifier)
        throw ex
      }
      case ex: IOException => {
        if (tries > 0) {
          try {
            Thread.sleep(100)
          } catch {
            case e: InterruptedException => {}
          }
          return getDocuments_(identifier, p, tries - 1)
        } else {
          throw ex
        }
      }
    }
  }

  private def getDocument_(identifier: String, p: Parameters, tries: Int = 5): Document = {
    try {
      m_searcher.getDocument(identifier, p)
    } catch {
      case ex: NullPointerException => {
        println("NPE while fetching documents " + identifier)
        throw ex
      }
      case ex: IOException => {
        if (tries > 0) {
          try {
            Thread.sleep(100)
          } catch {
            case e: InterruptedException => {}
          }
          return getDocument_(identifier, p, tries - 1)
        } else {
          throw ex
        }
      }
    }
  }

  def getStatistics(query: String): AggregateReader.NodeStatistics = {
    try {
//      println("getStatistics "+query)
//      print(query+" ")

      val root = StructuredQuery.parse(query)
      root.getNodeParameters.set("queryType", "count")
      val transformed = m_searcher.transformQuery(root, queryParams)
      m_searcher.getNodeStatistics(transformed)
    } catch {
      case e: Exception => {
        println("Error getting statistics for query: " + query)
        throw e
      }
    }
  }

  /**
   * Select a delimiter character that is not contained in the query, so that we can instruct galago to leave special
   * characters in our query by wrapping it. Say that delim = '.', we wrap it in
   *
   * @.query.
   *
   * @param query
   * @return
   */
  def selectDelim(query:String):Char = {
//    val delimSymbols = Seq('\"','.','!').iterator
    val delimSymbols = Seq('\"').iterator
    var found:Boolean = false
    var delim:Char= ' '
    while(!found && delimSymbols.hasNext){
      val posDelim = delimSymbols.next()
      if (query.indexOf(posDelim) < 0) {
        delim = posDelim
        found = true
      }
    }

    if (!found){
      // we are getting desparate here
      val delim2Symbols = (Char.MinValue to Char.MaxValue).view.filter(_.isLetterOrDigit).iterator
      while(!found && delim2Symbols.hasNext){
        val posDelim = delim2Symbols.next()
        if (query.indexOf(posDelim) < 0) {
          delim = posDelim
          found = true
        }
      }
    }
    if (!found) {
      throw new RuntimeException(" failed to find delimiter char that is not contained in query "+query)
    }
    delim
  }



  def getFieldTermCount(cleanTerm: String, field: String): Long = {
    if (cleanTerm.length > 0 || cleanTerm.indexOf('#')>=0) {
      val delim = selectDelim(cleanTerm)
      val transformedText = "@"+delim + cleanTerm+delim+"" + "." + field
      val statistics = getStatistics(transformedText)
//      println(statistics.nodeFrequency.toString+" = field term count for \""+cleanTerm+"\" in "+field+" (delim:"+delim)
      statistics.nodeFrequency
    } else {
      0
    }
  }
  // LD: this is the old version. instead of dropping terms with weird symbols, we escape everything with a delimiter.
//  def getFieldTermCount(cleanTerm: String, field: String): Long = {
//    if (cleanTerm.length > 0 && (cleanTerm.indexOf('@') == 0)) {
//      val transformedText = "\"" + cleanTerm.replaceAllLiterally("\"","") + "\"" + "." + field
//      val statistics = getStatistics(transformedText)
//      statistics.nodeFrequency
//    } else {
//      0
//    }
//  }


  def retrieveAnnotatedScoredDocuments(query: String, params: Parameters, resultCount: Int, debugQuery: ((Node, Node) => Unit) = ((x, y) => {})): Seq[(ScoredDocument, AnnotatedNode)] = {
    params.set("annotate", true)
    for (scoredAnnotatedDoc <- retrieveScoredDocuments(query, Some(params), resultCount, debugQuery)) yield {
      (scoredAnnotatedDoc, scoredAnnotatedDoc.annotation)
    }
  }

  def retrieveScoredDocuments(query: String, params: Option[Parameters] = None, resultCount: Int, debugQuery: ((Node, Node) => Unit) = ((x, y) => {})): Seq[ScoredDocument] = {
    val p = new Parameters()
    myParamCopyFrom(p,globalParameters)
    params match {
      case Some(params) => myParamCopyFrom(p,params)
      case None => {}
    }
    p.set("startAt", 0)
    p.set("resultCount", resultCount)
    p.set("requested", resultCount)
    val root = StructuredQuery.parse(query)
    val transformed = m_searcher.transformQuery(root, p)
    debugQuery(root, transformed)
    val results = m_searcher.runQuery(transformed, p)
    // galago should not be returning null.
    if (results == null) {
      Seq()
    } else {
      results
    }
  }

  def retrieveScoredPassages(query: String, params: Option[Parameters] = None, resultCount: Int, debugQuery: ((Node, Node) => Unit) = ((x, y) => {})): Seq[ScoredPassage] = {
    retrieveScoredDocuments(query, params, resultCount, debugQuery).map(_.asInstanceOf[ScoredPassage])
  }

  /**
   * Maintains the order of the search results but augments them with Document instances
   * @param resultList
   * @return
   */
  def fetchDocuments(resultList: Seq[ScoredDocument]): Seq[FetchedScoredDocument] = {
    val docNames = resultList.map(_.documentName)
    val docs = getDocuments(docNames)
    for (scoredDoc <- resultList) yield {
      FetchedScoredDocument(scoredDoc,
        docs.getOrElse(scoredDoc.documentName, {
          throw new DocumentNotInIndexException(scoredDoc.documentName)
        })
      )
    }
  }

  /**
   * Maintains the order of the search results but augments them with Document instances
   * @param resultList
   * @return
   */
  def fetchPassages(resultList: Seq[ScoredPassage]): Seq[FetchedScoredPassage] = {
    val docNames = resultList.map(_.documentName)
    val docs = getDocuments(docNames)
    for (scoredPassage <- resultList) yield {
      FetchedScoredPassage(scoredPassage,
        docs.getOrElse(scoredPassage.documentName, {
          throw new DocumentNotInIndexException(scoredPassage.documentName)
        })
      )
    }
  }

  def getUnderlyingRetrieval(): Retrieval = {
    m_searcher
  }


  def close() {
    m_searcher.close()
  }
}

case class FetchedScoredDocument(scored: ScoredDocument, doc: Document)

case class FetchedScoredPassage(scored: ScoredPassage, doc: Document)

class DocumentNotInIndexException(val docName: String) extends RuntimeException

