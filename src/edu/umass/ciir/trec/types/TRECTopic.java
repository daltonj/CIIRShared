package edu.umass.ciir.trec.types;

public class TRECTopic implements Comparable<TRECTopic> {

    private int m_number;
    private String m_title;
    private String m_description;
    private String m_narrative;

    public TRECTopic(int n, String t, String d, String na) {
	this.m_number = n;
	this.m_title = t;
	this.m_description = d;
	this.m_narrative = na;
    }

    public int getNumber() { return m_number; }
    public  String getTitle() { return m_title; }
    public String getDescription() { return m_description; }
    public String getNarrative() { return m_narrative; }

    
    /**
     * Compares topic number, and if they are equals, returns lexographical comparison of the title fields.
     */
    public int compareTo(TRECTopic o) {
	if (this.getNumber() < o.getNumber()) return -1;
	if (this.getNumber() > o.getNumber()) return 1;	
	return (this.getTitle().compareTo(o.getTitle()));
    }
}