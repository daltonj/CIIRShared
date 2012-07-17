package edu.umass.ciir.models;


public class JS_KLDivergenceSimilarity implements LanguageModelSimilarity {
    
    
    private double smoothingAlpha;
    private final LanguageModel background;
    
    public JS_KLDivergenceSimilarity(LanguageModel background, double smoothingParam) {
        this.background = background;
        smoothingAlpha = smoothingParam;
    }

    /**
     * This calculates the Jensen-Shannon KL Divergence: 0.5 * KL(P1||P_both) + 0.5 * KL(P2||P_both), 
     * where P_both= 0.5 (P1+P2).
     *
     * @param lm1 The first language model
     * @param lm2 The second language model
     */
    public SimilarityMeasure calculateSimilarity(LanguageModel lm1, LanguageModel lm2, boolean useProbabilities) {

        KLDivergenceSimilarity klSimilarity = new KLDivergenceSimilarity(background, smoothingAlpha);
        
    	double divergence1 = Math.abs(klSimilarity.calculateSimilarity(lm1, lm2, useProbabilities).getSimilarity());
    	if (divergence1 == Double.POSITIVE_INFINITY) {
    	    divergence1 = Double.MAX_VALUE;
    	}
    	
    	double divergence2 = Math.abs(klSimilarity.calculateSimilarity(lm2, lm1, useProbabilities).getSimilarity());
    	if (divergence2 == Double.POSITIVE_INFINITY) {
            divergence2 = Double.MAX_VALUE;
        }
    	
    	SimilarityMeasure sm = new SimilarityMeasure((0.5 * divergence1) + (0.5 * divergence2), "No information found");
    	return sm;
    }
}
