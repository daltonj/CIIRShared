package edu.umass.ciir.memindex;

import java.util.ArrayList;
import java.util.List;

public class Document {

	String m_docId;
	Utf8Text m_originalDocument;
	
	private final int m_docLenTok;
	private final long m_startOffsetInCollection;
	
	ArrayList<String> m_docTerms;
	private List<String> m_tags;
	
	public Document(String docId, String originalDocument,
			ArrayList<String> docTerms, int docLenTok, long startTokOffsetInCollection) {
		super();
		m_docId = docId;
		m_originalDocument = new Utf8Text(originalDocument);
		m_docTerms = docTerms;
		m_docLenTok = docLenTok;
		m_startOffsetInCollection = startTokOffsetInCollection;
	}
	
	public String getDocId() {
		return m_docId;
	}
	public String getOriginalDocument() {
		return m_originalDocument.toString();
	}
	public ArrayList<String> getDocTerms() {
		return m_docTerms;
	}

	public int getDocLenTok() {
		return m_docLenTok;
	}

	public long getStartOffsetInCollection() {
		return m_startOffsetInCollection;
	}
	
	public String toString() {
		return "docId: " + m_docId + " content:" + m_originalDocument + " len:" + m_docLenTok;
	}
	
	public List<String> getTags() {
		return m_tags;
	}

	public void setTags(List<String> tags) {
		m_tags = tags;
		
	}
	
	
}
