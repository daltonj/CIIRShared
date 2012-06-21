package edu.umass.ciir.models;

import java.util.Collection;
import java.util.Iterator;

public class KLDivergenceSimilarity implements LanguageModelSimilarity {
    
	private double smoothingAlpha;
	private final LanguageModel background;

	/**
	 * Constructor
	 */
    
    public KLDivergenceSimilarity(LanguageModel background, double smoothingParam) {
    	this.background = background;
		smoothingAlpha = smoothingParam;
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

    	Collection<TermEntry> vocab = lm1.getEntries();
    	double total1 = (double) lm1.getCollectionFrequency();
    	
    	if (lm2.getCollectionFrequency() == 0) {
    		return new SimilarityMeasure(Double.MAX_VALUE, "No information found");
    	}
    	double total2 = (double) lm2.getCollectionFrequency();
    	double divergence = 0.0;

    	Iterator<TermEntry> vIter = vocab.iterator();
    	while (vIter.hasNext()) {
    		TermEntry te1 = vIter.next();
    		TermEntry te2 = lm2.getTermEntry(te1.getTerm());
			TermEntry bgTerm =  background.getTermEntry(te1.getTerm());

			if (bgTerm == null) {
				System.out.println("Unable to get background model for term: " + te1.getTerm());
			}
    		if (useProbabilities) {
    			p1 = te1.getProbability();
    		} else {
    			p1 = ((double) te1.getFrequency() / total1);
    		}
    		if (te2 != null) {
    			if (useProbabilities) {
    				p2 = te2.getProbability();
    				if (p2 < 0.0000001) {
    					p2 = smoothingAlpha / total2;
    				}
    			} else {
    				p2 = ((double) te2.getFrequency() 
    						+ (smoothingAlpha * bgTerm.getProbability())) 
    						/ (smoothingAlpha + total2);
    			}
    		} else {
    			p2 = (smoothingAlpha * bgTerm.getProbability()) 
				/ (smoothingAlpha + total2);
    		}
    		double logPart = Math.log(p1 / p2);
    		divergence += p1 * logPart;
    	}

    	SimilarityMeasure sm = new SimilarityMeasure(divergence, "No information found");
    	return sm;
    }
}
