package edu.umass.ciir.memindex;

/**
 * A data point for evaluating query effectiveness.
 * It represents the results of evaluating a single query.
 * 
 * @author jdalton
 *
 */
public class EvaluationStatEntry {

	/**
	 * Unique query identifier
	 */
	private String m_qid;
	
	/**
	 * Optional description field
	 */
	private String m_description;
	
	private int m_numResults;
	private int m_numRelevantReturned;
	private int m_numRelevant;
	private double m_averagePrecision;
	
	public EvaluationStatEntry(String qid, int numResults, int numRelevantReturned, int numRelevant, double averagePrecision) {
		m_qid = qid;
		m_numResults = numResults;
		m_numRelevantReturned = numRelevantReturned;
		m_numRelevant = numRelevant;
		m_averagePrecision = averagePrecision;
	}

	public String getQid() {
		return m_qid;
	}

	public String getDescription() {
		return m_description;
	}

	public int getNumResults() {
		return m_numResults;
	}

	public int getNumRelevantReturned() {
		return m_numRelevantReturned;
	}

	public int getNumRelevant() {
		return m_numRelevant;
	}

	public double getAveragePrecision() {
		return m_averagePrecision;
	}
	
	public double getPrecision() {
		double precision = 0;
		if (m_numRelevantReturned > 0) {
			precision =  m_numRelevantReturned / (double)m_numResults;
		}
		
		return precision;
	}

	public void setDescription(String description) {
		m_description = description;
	}
	
}
