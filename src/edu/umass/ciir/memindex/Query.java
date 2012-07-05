package edu.umass.ciir.memindex;

public class Query {

	private final String m_rawQuery;
	
	private String m_processedQuery;
	
	private final String m_queryNum;
	
	int m_numRequestedResults;
	
	boolean m_fetchDocs = false;
	
	
	public Query(String queryId, String rawQuery, int numResults) {
		m_rawQuery = rawQuery;
		m_queryNum = queryId;
		m_numRequestedResults = numResults;
	}

	public String getRawQuery() {
		return m_rawQuery;
	}

	public int getNumResults() {
		return m_numRequestedResults;
	}

	public boolean fetchDocs() {
		return m_fetchDocs;
	}
	
	public String getQueryNum() {
		return m_queryNum;
	}
	
	public String toString() {
		return m_rawQuery;
	}
	
}
