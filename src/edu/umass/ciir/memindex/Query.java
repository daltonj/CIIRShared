package edu.umass.ciir.memindex;

public class Query {

	String m_query;
	
	int m_queryNum;
	
	/**
	 * -1 indicates all hits
	 */
	int m_numRequestedResults;
	
	boolean m_fetchDocs = false;
	
	public Query(String query, int numResults) {
		m_query = query;
		m_numRequestedResults = numResults;
	}

	public String getQuery() {
		return m_query;
	}

	public int getNumResults() {
		return m_numRequestedResults;
	}

	public boolean fetchDocs() {
		return m_fetchDocs;
	}
	
	public int getQueryNum() {
		return m_queryNum;
	}
	
	public String toString() {
		return m_query;
	}
	
}
