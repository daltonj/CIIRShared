package edu.umass.ciir.models;

public interface LanguageModelSimilarity {
	
    public SimilarityMeasure calculateSimilarity(LanguageModel lm1, LanguageModel lm2, boolean useProbabilities);
}
