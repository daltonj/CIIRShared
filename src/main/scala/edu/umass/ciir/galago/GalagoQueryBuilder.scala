package edu.umass.ciir.galago

import org.lemurproject.galago.tupleflow.Parameters

/**
 * User: dietz
 * Date: 3/29/13
 * Time: 3:39 PM
 */
object GalagoQueryBuilder {
  def seqdep(query:String, seqdepParams:Option[(Double,Double,Double)]=None):ParametrizedQuery = {
    val param = new Parameters()
    if(seqdepParams.isDefined) GalagoQueryLib.paramSeqDep(param,seqdepParams.get)
    ParametrizedQuery (GalagoQueryLib.buildSeqDepForString(query), param)
  }

  def weightedMultiSeqdep(weightedqueries:Seq[(String,Double)], seqdepParams:Option[(Double,Double,Double)]=None):ParametrizedQuery = {
    val param = new Parameters()
    if(seqdepParams.isDefined) GalagoQueryLib.paramSeqDep(param,seqdepParams.get)
    val weightedSeqDeps =
      for((query, weight) <- weightedqueries) yield (GalagoQueryLib.buildSeqDepForString(query), weight)
    val rawQuery = GalagoQueryLib.buildWeightedCombine(weightedSeqDeps)
    ParametrizedQuery (rawQuery, param)
  }

  def passageRetrieval(initialQuery:ParametrizedQuery, workingSet:List[String], passageSize:Int, passageShift:Int):ParametrizedQuery = {
    val param = new Parameters()
    param.copyFrom(initialQuery.parameters)
    GalagoQueryLib.paramPassageRetrieval(param, workingSet, passageSize, passageShift)
    ParametrizedQuery(initialQuery.queryStr, param)
  }

  def seqdepPassage(question:String, workingSet:List[String], passageSize:Int, passageShift:Int, seqdepParams:Option[(Double,Double,Double)]=None):ParametrizedQuery = {
    val param = new Parameters()
    if(seqdepParams.isDefined) GalagoQueryLib.paramSeqDep(param,seqdepParams.get)
    GalagoQueryLib.paramPassageRetrieval(param, workingSet, passageSize, passageShift)
    ParametrizedQuery (GalagoQueryLib.buildSeqDepForString(question), param)
  }

  def expandQuery(origQuery:ParametrizedQuery, expansionTerms:Seq[(String, Double)], origWeight:Double):ParametrizedQuery = {
    val queryStr =
      GalagoQueryLib.buildWeightedCombine(Seq(
        origQuery.queryStr -> origWeight,
        GalagoQueryLib.buildWeightedCombine(expansionTerms) -> (1.0 - origWeight)
      ))
    ParametrizedQuery(queryStr, origQuery.parameters)
  }

}
case class ParametrizedQuery(queryStr:String, parameters:Parameters)
