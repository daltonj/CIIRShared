package edu.umass.ciir.galago

import java.io.{File, IOException}
import org.lemurproject.galago.core.retrieval.query.{AnnotatedNode, StructuredQuery, Node}
import org.lemurproject.galago.tupleflow.Parameters
import org.lemurproject.galago.core.parse.Document

import scala.collection.JavaConversions._
import org.lemurproject.galago.core.retrieval.{Retrieval, RetrievalFactory, ScoredPassage, ScoredDocument}
import org.lemurproject.galago.core.index.stats.NodeStatistics
import org.lemurproject.galago.core.parse.Document.DocumentComponents

class GalagoSearcher(globalParameters: Parameters) {

  //  val globalParameters = Parameters.parse(new File(jsonConfigFile))
  //
  //  if (!galagoUseLocalIndex) {
  //    val remoteIndex = "http://" + galagoSrv + ":" + galagoPort
  //    globalParameters.set("index", remoteIndex)
  //  }
  if (globalParameters.isString("index")) println("** Loading index from: " + globalParameters.getString("index"))

  val queryParams = new Parameters
  val m_searcher = RetrievalFactory.instance(globalParameters)


  def getDocuments(documentNames: Seq[String], params: Parameters = new Parameters()): Map[String, Document] = {
    val p = new Parameters()
    p.copyFrom(globalParameters)
    p.copyFrom(params)
    getDocuments_(documentNames, p)
  }

  private def getDocuments_(identifier: Seq[String], p: Parameters, tries: Int = 5): Map[String, Document] = {
    try {
      val docmap = m_searcher.getDocuments(identifier, new DocumentComponents(p))
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


  def getStatistics(query: String): NodeStatistics = {
    try {
      val root = StructuredQuery.parse(query)
      root.getNodeParameters.set("queryType", "count")
      val transformed = m_searcher.transformQuery(root, queryParams)
      m_searcher.getNodeStatistics(transformed)
      //m_searcher.nodeStatistics(transformed)
    } catch {
      case e: Exception => {
        println("Error getting statistics for query: " + query)
        throw e
      }
    }
  }


  def getFieldTermCount(cleanTerm: String, field: String): Long = {
    if (cleanTerm.length > 0) {
      val transformedText = "\"" + cleanTerm + "\"" + "." + field
      val statistics = getStatistics(transformedText)
      statistics.nodeFrequency
    } else {
      0
    }
  }


  def retrieveAnnotatedScoredDocuments(query: String, params: Parameters, resultCount: Int, debugQuery: ((Node, Node) => Unit) = ((x, y) => {})): Seq[(ScoredDocument, AnnotatedNode)] = {
    params.set("annotate", true)
    for (scoredAnnotatedDoc <- retrieveScoredDocuments(query, params, resultCount, debugQuery)) yield {
      (scoredAnnotatedDoc, scoredAnnotatedDoc.annotation)
    }
  }

  def retrieveScoredDocuments(query: String, params: Parameters, resultCount: Int, debugQuery: ((Node, Node) => Unit) = ((x, y) => {})): Seq[ScoredDocument] = {
    val p = new Parameters()
    p.copyFrom(globalParameters)
    p.copyFrom(params)
    p.set("startAt", 0)
    p.set("resultCount", resultCount)
    p.set("requested", resultCount)
    val root = StructuredQuery.parse(query)
    val transformed = m_searcher.transformQuery(root, p)
    debugQuery(root, transformed)
    val results = m_searcher.executeQuery(transformed, p).scoredDocuments
    if (results != null) {
      results
    } else {
      Seq()
    }
  }

  def retrieveScoredPassages(query: String, params: Parameters, resultCount: Int, debugQuery: ((Node, Node) => Unit) = ((x, y) => {})): Seq[ScoredPassage] = {
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
  }
}

case class FetchedScoredDocument(scored: ScoredDocument, doc: Document)

case class FetchedScoredPassage(scored: ScoredPassage, doc: Document)

class DocumentNotInIndexException(val docName: String) extends RuntimeException

