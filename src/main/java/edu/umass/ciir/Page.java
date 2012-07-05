package edu.umass.ciir;

public class Page implements Comparable<Page>{
	
	int m_number;
	String m_text;
	
	
	public Page(int number, String text) {
		m_number = number;
		m_text = text;
	}


	public int getNumber() {
		return m_number;
	}


	public String getText() {
		return m_text;
	}


	/**
	 * Compares pages by their number.
	 */
	public int compareTo(Page o) {
		if (m_number < o.getNumber()) {
			return -1;
		} else if (m_number > o.getNumber()) {
			return 1;
		} else {
			return 0;
		}
	}

	
}
