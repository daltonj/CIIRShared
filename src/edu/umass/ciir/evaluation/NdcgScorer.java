package edu.umass.ciir.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class NdcgScorer {

	public double computeNdcg(List<Document> rankedList, List<Judgment> judgments) {
		return normalizedDiscountedCumulativeGain(rankedList, judgments );
	}

	public double computeNdcg(List<Document> retrievedList, List<Judgment> judgments, int cutoff) {
		List<Document> truncatedRetrieved = retrievedList;
		if (retrievedList.size() > cutoff ) {
			truncatedRetrieved = retrievedList.subList(0, cutoff);
		}
		
		return normalizedDiscountedCumulativeGain(truncatedRetrieved, judgments );

	}
	
	 /** 
     * <p>Normalized Discounted Cumulative Gain </p>
     *
     * This measure was introduced in Jarvelin, Kekalainen, "IR Evaluation Methods
     * for Retrieving Highly Relevant Documents" SIGIR 2001.  I copied the formula
     * from Vassilvitskii, "Using Web-Graph Distance for Relevance Feedback in Web
     * Search", SIGIR 2006.
     *
     * Score = N \sum_i (2^{r(i)} - 1) / \log(1 + i)
     *
     * Where N is such that the score cannot be greater than 1.  We compute this
     * by computing the DCG (unnormalized) of a perfect ranking.
     */                     
     
    public double normalizedDiscountedCumulativeGain(List<Document> retrieved, List<Judgment> judgments) {
        // first, compute the gain from an optimal ranking  
        double normalizer = normalizationTermNDCG(retrieved, judgments);
        if (normalizer == 0) return 0;
        
        // now, compute the NDCG of the ranking and return that
        double dcg = 0;
        
        HashMap<String, Judgment> judgmentMap = new HashMap<String, Judgment>();
        for (Judgment judgment : judgments) {
        	judgmentMap.put(judgment.getDocumentIdentifier(), judgment);
        }
     //   System.out.println("\nCalculating DCG");
        for (Document document : retrieved) {
            Judgment judgment = judgmentMap.get(document.documentNumber);
            
            if (judgment != null && judgment.getJudgment() > 0) {
            	double score = (Math.pow(2, judgment.getJudgment()) - 1.0) / Math.log( 1 + document.rank );
                dcg += score;
            //    System.out.println(document.rank + " relevance:" + judgment.getJudgment() + " " + score + " sum:" + dcg);
            } else {
          //  	System.out.println("not found or not relevant doc: " + document.documentNumber);
            }
            
        }    
        
        return dcg / normalizer;
    }           
    
    protected double normalizationTermNDCG(List<Document> retrieved, List<Judgment> judgments) {
        TreeMap<Integer, Integer> relevanceCounts = new TreeMap<Integer, Integer>();
        
  //      System.out.println("\n*** calculating ideal dcg ****");
        
        // the normalization term represents the highest possible DCG score
        // that could possibly be attained.  we compute this by taking the relevance
        // judgments, ordering them by relevance value (highly relevant documents first)
        // then calling that the ranked list, and computing its DCG value.
                                      
        // we use negative judgment values so they come out of the map
        // in order from highest to lowest relevance
        for ( Judgment judgment : judgments ) {
            if (judgment.getJudgment() == 0 )
                continue;
                
            if (!relevanceCounts.containsKey(-judgment.getJudgment())) {
                relevanceCounts.put( -judgment.getJudgment(), 0 );
            }                                               
            
            relevanceCounts.put( -judgment.getJudgment(), relevanceCounts.get( -judgment.getJudgment() ) + 1 );
        }                                                                                          
                
        double normalizer = 0;
        int documentsProcessed = 0; 
        
        for (Integer negativeRelevanceValue : relevanceCounts.keySet()) {        
            int relevanceCount = (int)relevanceCounts.get( negativeRelevanceValue );
            int relevanceValue = -negativeRelevanceValue;
            relevanceCount = Math.min( relevanceCount,  retrieved.size() - documentsProcessed );
            
            for (int i = 1; i <= relevanceCount; i++) {
            	double dcg = (Math.pow(2, relevanceValue) - 1.0) / Math.log( 1 + i + documentsProcessed );
                normalizer += dcg; 
      //      	System.out.println("i:" + (i + documentsProcessed) + " " + relevanceValue + " " + dcg + " sum:" + normalizer);
            }
            
            documentsProcessed += relevanceCount;
            if( documentsProcessed >=  retrieved.size() )
                break;
        }                              
        
        return normalizer;
    }
    
    public static void main(String[] args) {
    	NdcgScorer scorer = new NdcgScorer();
    	
    	Document d1 = new Document("d1");
    	d1.rank = 1;
    	
    	Document d2 = new Document("d2");
    	d2.rank = 2;
    	
    	Document d3 = new Document("d3");
    	d3.rank = 3;
    	
    	Document d4 = new Document("d4");
    	d4.rank = 4;
    	
    	Document d5 = new Document("d5");
    	d5.rank = 5;
    	
    	Document d6 = new Document("d6");
    	d6.rank = 6;
    	
    	ArrayList<Document> retrieved = new ArrayList<Document>();
    	retrieved.add(d1);
    	retrieved.add(d2);
    	retrieved.add(d3);
    	retrieved.add(d4);
    	retrieved.add(d5);
    	retrieved.add(d6);
    	
    	Judgment j1 = new Judgment("d1", 3);
    	Judgment j2 = new Judgment("d2", 2);
    	Judgment j3 = new Judgment("d3", 3);
    	Judgment j4 = new Judgment("d4", 0);
    	Judgment j5 = new Judgment("d5", 1);
    	Judgment j6 = new Judgment("d6", 2);
    	
    	ArrayList<Judgment> judgments = new ArrayList<Judgment>();
    	judgments.add(j1);
    	judgments.add(j2);
    	judgments.add(j3);
    	judgments.add(j4);
    	judgments.add(j5);
    	judgments.add(j6);
    	
    	System.out.println(scorer.computeNdcg(retrieved, judgments));
    	
    	d1.rank = 1;
    	d3.rank = 2;
    	d2.rank = 3;
    	d6.rank = 4;
    	d5.rank = 5;
    	d4.rank = 6;
    
    	
    	
    	System.out.println(scorer.computeNdcg(retrieved, judgments));
    	
    	
    }
}
