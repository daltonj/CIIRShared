package edu.umass.ciir.models;

import java.util.Collection;
import java.util.Iterator;

public class KLDivergenceSimilarity implements LanguageModelSimilarity {
    
	/**
	 * Constructor
	 */
    public KLDivergenceSimilarity() {
    	
    }

    /**
     * This iterates over the vocabulary of the first argument passed in, and produces a Kullback-Leibler
     * divergence measure. This is mathematically equivalent to KL(lm1 || lm2)
     *
     * @param lm1 The language model to compare against the reference language model.
     * @param lm2 The reference language model. 
     */
    public SimilarityMeasure calculateSimilarity(LanguageModel lm1, LanguageModel lm2, boolean useProbabilities) {
    	double p1, p2;

    	Collection<TermEntry> vocab = lm1.getVocabulary();
    	double total1 = (double) lm1.getCollectionFrequency();
    	double total2 = (double) lm2.getCollectionFrequency();
    	double divergence = 0.0;

    	Iterator<TermEntry> vIter = vocab.iterator();
    	while (vIter.hasNext()) {
    		TermEntry te1 = vIter.next();
    		TermEntry te2 = lm2.getTermEntry(te1.getTerm());
    		if (useProbabilities) {
    			p1 = te1.getProbability();
    		} else {
    			p1 = ((double) te1.getFrequency()) / total1;
    		}
    		if (te2 != null) {
    			if (useProbabilities) {
    				p2 = te2.getProbability();
    			} else {
    				p2 = ((double) te2.getFrequency()) / total2;
    			}
    		} else {
    			p2 = 1.0 / total2;
    		}
    		double logPart = Math.log(p1 / p2);
    		divergence += p1 * logPart;
    	}

    	SimilarityMeasure sm = new SimilarityMeasure(divergence, "No information found");
    	return sm;
    }
}
