package edu.umass.ciir.models;

public class SimilarityMeasure {
    
    private double m_similarity;
    private String m_reason;

    public SimilarityMeasure(double similarity, String reason) {
    	m_similarity = similarity;
    	m_reason = reason;
    }

    public double getSimilarity() {
    	return m_similarity;
    }

    public String getReason() {
    	return m_reason;
    }
    
    public String toString() {
    	return m_similarity +"";
    }
}
