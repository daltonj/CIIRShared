package edu.umass.ciir.galago

import org.lemurproject.galago.tupleflow.Parameters

/**
 * User: dietz
 * Date: 3/29/13
 * Time: 3:39 PM
 */
object GalagoQueryBuilder {
  case class SeqDep(term:Double, bigram:Double, windowedBigram:Double){
    def asTuple = (term, bigram, windowedBigram)
  }

  import GalagoParamTools.myParamCopyFrom

  def seqdep(query:String, seqdepParams:Option[SeqDep]=None, mu:Option[Double]=None,fields:Seq[(String,Double)]=Seq.empty):ParametrizedQuery = {
    val param = new Parameters()
    if(seqdepParams.isDefined) GalagoQueryLib.paramSeqDep(param,seqdepParams.get.asTuple)
    if (mu.isDefined) GalagoQueryLib.paramSmoothingMu(param, mu.get)
    ParametrizedQuery (GalagoQueryLib.buildSeqDepForString(query, fields), param)
  }

  def weightedMultiSeqdep(weightedqueries:Seq[(String,Double)], seqdepParams:Option[SeqDep]=None, mu:Option[Double]=None):ParametrizedQuery = {
    val param = new Parameters()
    if(seqdepParams.isDefined) GalagoQueryLib.paramSeqDep(param,seqdepParams.get.asTuple)
    if (mu.isDefined) GalagoQueryLib.paramSmoothingMu(param, mu.get)
    val weightedSeqDeps =
      for((query, weight) <- weightedqueries) yield (GalagoQueryLib.buildSeqDepForString(query), weight)
    val rawQuery = GalagoQueryLib.buildWeightedCombine(weightedSeqDeps)
    ParametrizedQuery (rawQuery, param)
  }

  def passageRetrieval(initialQuery:ParametrizedQuery, workingSet:List[String], passageSize:Int, passageShift:Int, seqdepParams:Option[SeqDep]=None, mu:Option[Double]=None):ParametrizedQuery = {
    val param = new Parameters()
    myParamCopyFrom(param, initialQuery.parameters)
    if(seqdepParams.isDefined) GalagoQueryLib.paramSeqDep(param,seqdepParams.get.asTuple)
    if (mu.isDefined) GalagoQueryLib.paramSmoothingMu(param, mu.get)
    GalagoQueryLib.paramPassageRetrieval(param, workingSet, passageSize, passageShift)
    ParametrizedQuery(initialQuery.queryStr, param)
  }

  def seqdepPassage(question:String, workingSet:List[String], passageSize:Int, passageShift:Int, seqdepParams:Option[SeqDep]=None, mu:Option[Double]=None):ParametrizedQuery = {
    val param = new Parameters()
    if(seqdepParams.isDefined) GalagoQueryLib.paramSeqDep(param,seqdepParams.get.asTuple)
    if (mu.isDefined) GalagoQueryLib.paramSmoothingMu(param, mu.get)
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
