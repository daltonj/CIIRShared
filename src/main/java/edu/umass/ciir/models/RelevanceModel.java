package edu.umass.ciir.models;

import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.prf.WeightedTerm;
import org.lemurproject.galago.core.retrieval.query.Node;
import org.lemurproject.galago.tupleflow.Parameters;

import java.io.IOException;
import java.util.*;

public class RelevanceModel {

    Retrieval m_retrieval;
    
    Parameters parameters;
    
    private HashMap<String, Integer> lengths;

    public RelevanceModel(Retrieval r) {
        m_retrieval = r;
        Parameters p1 = new Parameters();
        p1.set("terms", true);
        p1.set("tags", true);
        parameters = p1;
    }
    
    public static class Gram implements WeightedTerm {

        public String term;
        public double score;

        public Gram(String t) {
          term = t;
          score = 0.0;
        }

        public String getTerm() {
          return term;
        }

        public double getWeight() {
          return score;
        }

        // The secondary sort is to have defined behavior for statistically tied samples.
        public int compareTo(WeightedTerm other) {
          Gram that = (Gram) other;
          int result = this.score > that.score ? -1 : (this.score < that.score ? 1 : 0);
          if (result != 0) {
            return result;
          }
          result = (this.term.compareTo(that.term));
          return result;
        }

        public String toString() {
          return "<" + term + "," + score + ">";
        }
      }
        
    public ArrayList<WeightedTerm> generateGrams(List<ScoredDocument> initialResults) throws IOException {
        HashMap<String, Double> scores = logsToPosteriors2(initialResults);
        HashMap<String, HashMap<String, Integer>> counts = countGrams(initialResults);

        //for(String term : counts.keySet()) {
        //    HashMap<String,Integer> tfs = counts.get(term);
        //    for(String docName : tfs.keySet()) {
        //        long docLen = m_retrieval.getDocumentLength(docName);

        //        long docFreq = 0;
        //        try {
        //            docFreq = m_retrieval.getNodeStatistics(new Node("counts", term)).nodeDocumentCount;
        //        } catch (Exception e) {
        //            e.printStackTrace();
        //        }

        //        int tf = tfs.get(docName);
        //        double score = ((double) tf) / ((double) docLen);
        //        System.out.println(":LM:\t"+docName+"\t"+docLen+"\t"+term+"\t"+score+"\t"+tf+"\t"+docFreq+"\t"+1.0/((double) docFreq));
        //    }
        //}

        ArrayList<WeightedTerm> scored = scoreGrams(counts, scores);
        Collections.sort(scored);
        return scored;
   }
    
    
    // Implementation here is identical to the Relevance Model unigram normalization in Indri.
    // See RelevanceModel.cpp for details
    public static final HashMap<String, Double> logsToPosteriors(List<ScoredDocument> results) {
      HashMap<String, Double> scores = new HashMap<String, Double>();
      if (results.size() == 0) {
        return scores;
      }

      // For normalization
      double K = results.get(0).score;

      // First pass to get the sum
      double sum = 0;
      for (ScoredDocument sd : results) {
        double recovered = Math.exp(K + sd.score);
        scores.put(sd.documentName, recovered);
        sum += recovered;
      }

      // Normalize
      for (Map.Entry<String, Double> entry : scores.entrySet()) {
        entry.setValue(entry.getValue() / sum);
      }
      return scores;
    }
    
 
    /**
     * This is a "fixed" version that computes normalized log posteriors.  It uses the
     * log-sum-exp trick to avoid underflow.
     * 
     * @param results
     * @return
     */
    public static final HashMap<String, Double> logsToPosteriors2(List<ScoredDocument> results) {
      HashMap<String, Double> scores = new HashMap<String, Double>();
      if (results.size() == 0) {
        return scores;
      }

      // For normalization
      double K = results.get(0).score;

      // First pass to get the sum
      double sum = 0;
      for (ScoredDocument sd : results) {
        double recovered = Math.exp(sd.score - K);
        scores.put(sd.documentName, sd.score);
        sum += recovered;
      }
      double logNorm = K + Math.log(sum);
      
      // Normalize
      for (Map.Entry<String, Double> entry : scores.entrySet()) {
        entry.setValue(Math.exp(entry.getValue() - logNorm));
      }
      return scores;
    }

    protected HashMap<String, HashMap<String, Integer>> countGrams(List<ScoredDocument> results) throws IOException {
      lengths = new HashMap<String, Integer>();
      HashMap<String, HashMap<String, Integer>> counts = new HashMap<String, HashMap<String, Integer>>();
      HashMap<String, Integer> termCounts;
      Document doc;
      String term;
      for (ScoredDocument sd : results) {
        Document.DocumentComponents docArgs = new Document.DocumentComponents();
        docArgs.tokenize = true;
        doc = m_retrieval.getDocument(sd.documentName, docArgs);
        if (doc != null) {
        for (String s : doc.terms) {
            term = s;
          if (!counts.containsKey(term)) {
            counts.put(term, new HashMap<String, Integer>());
          }
          termCounts = counts.get(term);
          if (termCounts.containsKey(sd.documentName)) {
            termCounts.put(sd.documentName, termCounts.get(sd.documentName) + 1);
          } else {
            termCounts.put(sd.documentName, 1);
          }
          lengths.put(sd.documentName, doc.terms.size());
        }
        }
      }
      return counts;
    }

    protected ArrayList<WeightedTerm> scoreGrams(HashMap<String, HashMap<String, Integer>> counts,
                             HashMap<String, Double> scores) throws IOException {
      ArrayList<WeightedTerm> grams = new ArrayList<WeightedTerm>();
      HashMap<String, Integer> termCounts;

      for (String term : counts.keySet()) {
        Gram g = new Gram(term);
        termCounts = counts.get(term);
        for (String docID : termCounts.keySet()) {
          int length = lengths.get(docID);
          int count = termCounts.get(docID);
          Double score = scores.get(docID);
          if (score == null) {
              System.out.println("WTF!");
          }
          // this performs the probability computation in real space... possibly leading
          // to underflow for small scores.
          // consider doing this in log space.
          g.score +=  score * (count / (double) length);
        }
        // 1 / fbDocs from the RelevanceModel source code
        // WHY?!?  we rescale the values anyway
        g.score *= (1.0 / scores.size());
        grams.add(g);
      }

      return grams;
    } 
}
