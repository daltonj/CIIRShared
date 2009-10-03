package edu.umass.ciir;

public class PrefixFilter {
	
	private final Character m_start;
	private final Character m_end;

	public PrefixFilter(Character start, Character end) {
		m_start = start;
		m_end = end;
		
	}
	
	public boolean filterOut(String stringToTest) {
		char f = stringToTest.charAt(0);
		
		
		if (m_start == null) {
			// only based on end.
			if (m_end.compareTo(f) < 0) {
				return true;
			}
		} else {
			if (m_start.compareTo(f) > 0 || m_end.compareTo(f) < 0) {
				return true;
			}
		}
		
		return false;
	}
	
	public static void main(String[] args) {
		PrefixFilter filter = new PrefixFilter('a', 'a');
		System.out.println(filter.filterOut("abc"));
		System.out.println(filter.filterOut("baby"));
		System.out.println(filter.filterOut("c"));
		System.out.println(filter.filterOut("dog"));
		System.out.println(filter.filterOut("emo"));
		System.out.println(filter.filterOut("faf"));
	}

}
