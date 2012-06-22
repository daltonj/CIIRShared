package edu.umass.ciir.memindex;

import java.util.HashMap;

public class Corpus {
	
	public Corpus() {
		
	}
	
	
	private HashMap<String, Document> m_docMap = new HashMap<String, Document>();

	public void addDocument(String docId, Document originalDocument) {
		m_docMap.put(docId, originalDocument);
	}
	
	public Document lookupDocument(String docId) {
		Document doc = m_docMap.get(docId);
		if (doc==null) {
			return null;
		}
		return doc;
	}
	
	public int maxDoc() {
		return m_docMap.size();
	}

}
