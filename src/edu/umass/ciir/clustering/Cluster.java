package edu.umass.ciir.clustering;

import edu.umass.ciir.models.LanguageModel;
import edu.umass.ciir.models.SimilarityMeasure;
import edu.umass.ciir.models.KLDivergenceSimilarity;
import edu.umass.ciir.models.TermEntry;

import java.util.ArrayList;

public class Cluster {
	
	private LanguageModel m_clusterCenter;
	private ArrayList<LanguageModel> m_docs;
	
	public Cluster() { 
		m_docs = new ArrayList(10);
	}
	
	public double compareToDoc(LanguageModel doc) {
	
		KLDivergenceSimilarity klDiv1 = new KLDivergenceSimilarity(null,2500);
		SimilarityMeasure sm1 = klDiv1.calculateSimilarity(this.getClusterCenter(), doc, true);
		KLDivergenceSimilarity klDiv2 = new KLDivergenceSimilarity(null, 2500);
		SimilarityMeasure sm2 = klDiv2.calculateSimilarity(doc, this.getClusterCenter(), true);
		return Math.abs(sm1.getSimilarity()) + Math.abs(sm2.getSimilarity());
	}
	
	public LanguageModel buildLMOfAllDocs() {
		LanguageModel lm = new LanguageModel();
		
		for (LanguageModel doc : this.getDocs()) {
			lm = LanguageModel.unionModel(doc, lm);
		}
		
		return lm;
	}
	
	public LanguageModel getClusterCenter() {
		return m_clusterCenter;
	}
	
	public ArrayList<LanguageModel> getDocs() {
		return m_docs;
	}
	
	public void setClusterCenter(LanguageModel cc) {
		this.m_clusterCenter = cc;
	}
	
	public void addDoc(LanguageModel cc) {
		this.m_docs.add(cc);
	}
	
	public void resetClusterDocs() {
		this.m_docs.clear();
	}

}
