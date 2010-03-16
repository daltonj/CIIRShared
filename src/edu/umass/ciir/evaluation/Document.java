package edu.umass.ciir.evaluation;

/**
 * This class represents a document returned by a retrieval
 * system.  It can be subclassed if you want to add system-dependent
 * information to the document representation.
 */

public class Document {
	
    /** The rank of the document in a retrieved ranked list. */
    public int rank;
    
    /** The document identifier. */
    public String documentNumber;
    
    /** The score given to this document by the retrieval system. */
    public double score;
    
    /**
     * Constructs a new Document object.
     *
     * @param documentNumber The document identifier.
     * @param rank The rank of the document in a retrieved ranked list.
     * @param score The score given to this document by the retrieval system.
     */
    
    public Document(String documentNumber, int rank, double score) {
        this.documentNumber = documentNumber;
        this.rank = rank;
        this.score = score;
    }
    
    /**
     * Constructs a new Document object.
     *
     * @param documentNumber The document identifier.
     */
    
    public Document(String documentNumber) {
        this.documentNumber = documentNumber;
        this.rank = Integer.MAX_VALUE;
        this.score = Double.NEGATIVE_INFINITY;
    }
    
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(documentNumber);
		sb.append("\t");
		sb.append(rank);
		sb.append("\t");
		sb.append(score);
		return sb.toString();
	}

}
