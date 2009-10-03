package edu.umass.ciir.evaluation;

/**
 * This class represents a relevance judgment of a particular document
 * for a specific query.
 */
public class Judgment {
	
	/** The document identifier. */
	public String m_documentIdentifier;


	/** The relevance judgment for this document, where positive values mean relevant, 
	 * and zero means not relevant. */
	public int m_judgment;


	/**
	 * Constructs a new Judgment instance.
	 *
	 * @param documentNumber The document identifier.
	 * @param judgment The relevance judgment for this document, where positive values mean relevant, and zero means not relevant.
	 */
	public Judgment( String docIdentifier, int judgment ) {
		m_documentIdentifier = docIdentifier;
		m_judgment = judgment;
	}

	public String getDocumentIdentifier() {
		return m_documentIdentifier;
	}


	public int getJudgment() {
		return m_judgment;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(m_documentIdentifier);
		sb.append("\t");
		sb.append(m_judgment);
		sb.append("\t");
		return sb.toString();
	}
}
