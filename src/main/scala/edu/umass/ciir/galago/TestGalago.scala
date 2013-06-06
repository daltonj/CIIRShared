package edu.umass.ciir.galago

import scala.collection.JavaConversions._
import org.lemurproject.galago.core.retrieval.query.Node
import org.lemurproject.galago.tupleflow.Parameters
import java.io.File

/**
 * User: dietz
 * Date: 3/29/13
 * Time: 3:38 PM
 */
object TestGalago {
  def main(args:Array[String]) {
    println("Plain sequential dependence model")
    seqDep()
    println("\n\n\n\n")

    println("multiple seqdeps, with weights")
    multiSeqDep()
    println("\n\n\n\n")

    println("multiple seqdeps as 2-pass passage retrieval")
    passageMultiSeqDep()
  }

  val searchSentenceJson = Parameters.parse(new File("params/aristo-search-sentence.json"))
  val searchSentencePassageJson= Parameters.parse(new File("params/aristo-search-sentence-passage.json"))
  
  def seqDep() {
    val galago = new GalagoSearcher(searchSentenceJson)

    val paramQuery = GalagoQueryBuilder.seqdep("when does the economy collapse?", Some((0.5, 0.4, 0.1)))
    val result = galago.retrieveScoredDocuments(resultCount= 20, query = paramQuery.queryStr, params = paramQuery.parameters)

    for(scoredDoc <- result) {
      println(scoredDoc.rank +": "+ scoredDoc.documentName)
    }

    val resultWithDocs = galago.fetchDocuments(result)
    for(FetchedScoredDocument(scoredDoc, doc) <- resultWithDocs) {
      println(scoredDoc.rank +": "+ scoredDoc.documentName+"\n"+doc.text)
    }

  }

  def multiSeqDep() {
    val galago = new GalagoSearcher(searchSentenceJson)

    val weightedQuestions = Seq(
      "when does the economy collapse?" -> 0.5,
      "fanny mae and freddy mac" -> 0.3,
      "burst of the housing bubble" -> 0.2
    )
    val paramQuery = GalagoQueryBuilder.weightedMultiSeqdep(weightedQuestions, Some((0.5, 0.4, 0.1)))
    val result = galago.retrieveScoredDocuments(resultCount= 20, query = paramQuery.queryStr, params = paramQuery.parameters)

    for(scoredDoc <- result) {
      println(scoredDoc.rank +": "+ scoredDoc.documentName)
    }

    val resultWithDocs = galago.fetchDocuments(result)
    for(FetchedScoredDocument(scoredDoc, doc) <- resultWithDocs) {
      println(scoredDoc.rank +": "+ scoredDoc.documentName+"\n"+doc.text)
    }

  }
  def passageMultiSeqDep() {
    val galago = new GalagoSearcher(searchSentenceJson)
    val galagoPassage = new GalagoSearcher(searchSentencePassageJson)

    val weightedQuestions = Seq(
      "when does The Economy Collapse?" -> 0.5,
      "fanny Mae and Freddy Mac" -> 0.3,
      "Burst of the Housing Bubble" -> 0.2
    )

    def queryDebugger(root:Node, transformed:Node) {
      println("Running Query \n"+root)
    }

    val paramQuery1 = GalagoQueryBuilder.weightedMultiSeqdep(weightedQuestions, Some((0.5, 0.4, 0.1)))
    val result1 = galago.retrieveScoredDocuments(resultCount= 20, query = paramQuery1.queryStr, params = paramQuery1.parameters, debugQuery = queryDebugger)

    val paramQuery = GalagoQueryBuilder.passageRetrieval(paramQuery1, result1.map(_.documentName).toList, 50, 25)
    val result = galagoPassage.retrieveScoredPassages(resultCount= 20, query = paramQuery.queryStr, params = paramQuery.parameters)

    for(scoredPassage <- result) {
      println(scoredPassage.rank +": "+ scoredPassage.documentName+" ("+scoredPassage.begin+" - "+scoredPassage.end+")")
    }

    val resultWithDocs = galago.fetchPassages(result)
    for(FetchedScoredPassage(scoredPassage, doc) <- resultWithDocs) {
      val passageText = doc.terms.slice(scoredPassage.begin,scoredPassage.end).mkString(" ")
      println(scoredPassage.rank +": "+ scoredPassage.documentName+" ("+scoredPassage.begin+" - "+scoredPassage.end+")\n"+passageText)
    }

  }

}
