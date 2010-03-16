package edu.umass.ciir.trec.types;

public class TRECJudgment implements Comparable<TRECJudgment> {
    private int m_topic;
    private int m_run;
    private String m_label;
    private int m_judgment;

    public TRECJudgment(int t, int r, String l, int j) {
	this.m_topic = t;
	this.m_run = r;
	this.m_label = l;
	this.m_judgment = j;
    }
    
    public int getTopic() { return m_topic; }
    public int getRun() { return m_run; }
    public String getLabel() { return m_label; }
    public int getJudgment() { return m_judgment; }

    public int compareTo(TRECJudgment o) {
	if (this.getTopic() < o.getTopic()) return -1;
	if (this.getTopic() > o.getTopic()) return 1;
	if (this.getRun() < o.getRun()) return -1;
	if (this.getRun() > o.getRun()) return 1;
	
	int labelComp = this.getLabel().compareTo(o.getLabel());
	if (labelComp != 0) return labelComp;
	
	if (this.getJudgment() < o.getJudgment()) return -1;
	if (this.getJudgment() > o.getJudgment()) return 1;

	return 0;	
    }

}