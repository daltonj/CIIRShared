package edu.umass.ciir.models;

import java.util.Comparator;

/**
 * Notice the sort order is inverted in terms of "higher" and "lower".
 * We want the entry with the highest score to be seen first.
 */
public class TermEntryWeightComparator implements Comparator<TermEntry> {

	public int compare(TermEntry o1, TermEntry o2) {
		double w1 = o1.getWeight();
		double w2 = o2.getWeight();

		if (w1 < w2) {
			return 1;
		} else if (w1 > w2) {
			return -1;
		} else {
			return 0;	
		}
	}
}
