package edu.umass.ciir.models;

public class Bm25 {
	
	public static final double DEFAULT_B = 0.75d;
	public static final double DEFAULT_K1 = 1.2d;
	
	protected double m_k1 = DEFAULT_K1;
	protected double m_b = DEFAULT_B;

	protected long m_numberOfDocumentsInCollection;
	protected final double m_avgDocumentLength;
	
	/**
	 * Constructor
	 * 
	 * @param numberOfDocumentsInCollection
	 */
	public Bm25(long numberOfDocumentsInCollection, double avgDocLength) {
		m_numberOfDocumentsInCollection = numberOfDocumentsInCollection;
		m_avgDocumentLength = avgDocLength;
		
	}
	
	public double score(double tf, double docLength, long documentFrequency) {
		double denominator = m_k1 * ((1 - m_b) + m_b * (docLength / m_avgDocumentLength)) + tf;
		double numerator = cfw(documentFrequency) * tf * (m_k1+1);
		return numerator / denominator;
		
	}
	
	protected double cfw(long documentFrequency) {
		return Math.log((m_numberOfDocumentsInCollection - documentFrequency + 0.5d) / (documentFrequency + 0.5d)) / Math.log(2.0d); 
	}
	
}
