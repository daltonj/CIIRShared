package edu.umass.ciir.memindex;

public class Result {
	
	double m_score;
	String m_docId;
	Document m_originalDoc;
	
	public Result(String docId, double score) {
		m_docId = docId;
		m_score = score;
	}

	
	public double getScore() {
		return m_score;
	}


	public String getDocId() {
		return m_docId;
	}


	public Document getOriginalDoc() {
		return m_originalDoc;
	}


	public void setScore(double score) {
		m_score = score;
	}

	public void setDocId(String docId) {
		m_docId = docId;
	}

	public void setOriginalDoc(Document originalDoc) {
		m_originalDoc = originalDoc;
	}
	
	
}
