package edu.umass.ciir.clustering;

import edu.umass.ciir.models.LanguageModel;

import java.util.ArrayList;
import java.util.Random;

/* this class uses symmetric KL divergence as the distance metric. The actual distance metric used in K-Means Clustering is cosine similarity or similar. */

public class KMeansClustering {
	
	private int m_numberOfClusters;
	private int m_numberOfIterations;
	private ArrayList<LanguageModel> m_documentLMs;
	private ArrayList<Cluster> m_clusters;


	public KMeansClustering() {
		this(2, new ArrayList<LanguageModel>(), 10);
	}
	
	public KMeansClustering(int k, ArrayList<LanguageModel> docs, int iterations) {
		m_numberOfClusters = k;
		m_documentLMs = docs;
		m_numberOfIterations = iterations;
		m_clusters = new ArrayList(m_numberOfClusters);
		for(int i=0; i< m_numberOfClusters; i++) m_clusters.add(new Cluster());
	}
	
	public ArrayList<Cluster> cluster()  {

	   try {
	   	if(getDocumentLMs().isEmpty()) throw new Exception("no documents given for clustering");

		// initial setting
	   	setRandomClusterCenters();

		// stopping criterium is the number of iterations
	   	int iteration = 0;
		while(iteration != getNumberOfIterations()) {
			resetAllClusters();
			
			// assign each doc to a cluster
			for(LanguageModel currentDoc : getDocumentLMs()) {
			   	int bestCluster = 0;
	   			double bestResult = Double.POSITIVE_INFINITY;
	   			for(int i=0; i< getClusters().size(); i++) {
					Cluster c = (Cluster) getClusters().get(i);
					double result = c.compareToDoc(currentDoc);
					System.out.println("comparing the cluster center " + (i+1) + " to a document: " + result);
					if(result<bestResult) {
						bestResult = result;
						bestCluster = i;
					}
				}
				// assign this document to the best cluster
				(getClusters().get(bestCluster)).addDoc(currentDoc);
			}
			
			// recompute the centroids: average (union) language model from all the docs in the cluster			
			for(Cluster c : getClusters()) {
				LanguageModel newClusterCenter = c.buildLMOfAllDocs();
				c.setClusterCenter(newClusterCenter);
			}
			iteration++;	
	   	}
	   	return getClusters();
	   	
	   } catch (Exception e) {
	   	e.printStackTrace(System.out);
	   }
	   return null;
	}
	
	private void setRandomClusterCenters() {
	
		ArrayList<Integer> usedNumbers = new ArrayList();
		for(Cluster c : getClusters()) {
			int random = 0;
			
			do { random = new Random().nextInt(getNumberOfClusters());
			} while(usedNumbers.contains(new Integer(random)));
			
			c.setClusterCenter(getDocumentLMAt(random));
			usedNumbers.add(random);
		}
	}
	
	public LanguageModel getDocumentLMAt(int i) {
		return this.m_documentLMs.get(i);
	}
	
	public void resetAllClusters() {
		for(Cluster c : getClusters())
			c.resetClusterDocs();
	}
	
	public int getNumberOfClusters() {
		return m_numberOfClusters;
	}
	
	public ArrayList<LanguageModel> getDocumentLMs() {
		return m_documentLMs;
	}
	
	public int getNumberOfIterations() {
		return m_numberOfIterations;
	}
	
	public ArrayList<Cluster> getClusters() {
		return m_clusters;
	}
	
	public void setDocumentLMs(ArrayList<LanguageModel> docs) {
		this.m_documentLMs = docs;
	}
	
	public void setNumberOfClusters(int k) {
		this.m_numberOfClusters = k;
	}

	public void setClusters(ArrayList<Cluster> c) {
		this.m_clusters = c;
	}
	
	public void setNumberOfIterations(int i) {
		this.m_numberOfIterations = i;
	}
}
