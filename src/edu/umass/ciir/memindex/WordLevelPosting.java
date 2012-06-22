package edu.umass.ciir.memindex;

public class WordLevelPosting extends DocLevelPosting {

	private final int[] m_positions;

	public WordLevelPosting(int docId, int[] positions) {
		super(docId, positions.length);
		m_positions = positions;
		
	}

	public int[] getPositions() {
		return m_positions;
	}
	
	

}
