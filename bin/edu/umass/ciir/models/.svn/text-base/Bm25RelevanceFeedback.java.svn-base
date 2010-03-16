package edu.umass.ciir.models;

/**
 * Substitutes a Relevance Weight for the IDF weight.  The relevance weight used here is the RSJ weight
 * @author Jeremy Brody
 *
 */
public class Bm25RelevanceFeedback extends Bm25 {

	/**
	 * Big R
	 * 
	 * The number of known relevant documents for the request
	 */
	private int m_numRelevantDocuments;
	
	/**
	 * Little r
	 * 
	 * The number of known relevant documents containing the given term
	 */
	private int m_numRelevantDocumentsWithTerm;

	public Bm25RelevanceFeedback(long numberOfDocumentsInCollection, double avgDocLength, int numRelevantDocuments) {
		super(numberOfDocumentsInCollection, avgDocLength);
		m_numRelevantDocuments = numRelevantDocuments;
		
	}
	
	/**
	 * This is a modified relevance feedback formula for weighting using outside documents.
	 * It is from Personalizing Search via Automated Analysis of Interests and Activities
	 */
	@Override
	protected double cfw(long documentFrequency) {
		double numerator = (m_numRelevantDocumentsWithTerm+0.5) * (m_numberOfDocumentsInCollection - documentFrequency +0.5);
		double denominator = (documentFrequency+0.5) * (m_numRelevantDocuments-m_numRelevantDocumentsWithTerm+0.5) ;
		return Math.log (numerator / denominator) / Math.log(2.0d);
	}
	
	public void setNumRelevantDocumentsWithTerm(int numRelevantDocumentsWithTerm) {
		m_numRelevantDocumentsWithTerm = numRelevantDocumentsWithTerm;
	}
}
