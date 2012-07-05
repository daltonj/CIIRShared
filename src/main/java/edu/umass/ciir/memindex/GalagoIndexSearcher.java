package edu.umass.ciir.memindex;

import java.util.ArrayList;
import java.util.List;

import org.lemurproject.galago.core.index.corpus.CorpusReader;
import org.lemurproject.galago.core.index.mem.MemoryIndex;
import org.lemurproject.galago.core.retrieval.LocalRetrieval;
import org.lemurproject.galago.core.retrieval.Retrieval;
import org.lemurproject.galago.core.retrieval.ScoredDocument;
import org.lemurproject.galago.core.retrieval.query.Node;
import org.lemurproject.galago.core.retrieval.query.StructuredQuery;
import org.lemurproject.galago.tupleflow.Parameters;

import edu.umass.ciir.CiirProperties;

public class GalagoIndexSearcher implements SearcherI {

	protected Parameters queryParams = new Parameters();
	
	Retrieval m_retrieval;
	private final Corpus m_corpus;
	
	public GalagoIndexSearcher(MemoryIndex index, Corpus corpus) 
	throws Exception {
		m_corpus = corpus;
		queryParams.set("mu", CiirProperties.getPropertyAsDouble("Searcher.mu", 2500d));
		m_retrieval = new LocalRetrieval(index, queryParams);
	}
	
	public GalagoIndexSearcher(String indexPath, String corpusPath) 
	throws Exception {
		m_corpus = null;
		queryParams.set("mu", CiirProperties.getPropertyAsDouble("Searcher.mu", 2500d));
		m_retrieval = new LocalRetrieval(indexPath, queryParams);
		CorpusReader cReader = null;
	}
	
	public List<Result> runQuery(Query query) 
	throws Exception {

		boolean limitDocs = query.getNumResults() > -1 ? true : false;
		
		queryParams.set("count", ""+query.getNumResults());

		ArrayList<Result> results = new ArrayList<Result>();
    	Node root = StructuredQuery.parse(query.getRawQuery());
        Node transformed = m_retrieval.transformQuery(root, queryParams);

		boolean useStemmming = CiirProperties.getPropertyAsBoolean("Searcher.performStemming", true);
		if (!useStemmming) {
			queryParams.set("stemming", false);
		}
		
		boolean debug = false;
		if (debug) {
			System.err.println("Input:" + query.getRawQuery());
			System.err.println("Parsed:" + root.toString());
			System.err.println("Transformed:" + transformed.toString());
		}

		ScoredDocument[] galagoResults = m_retrieval.runQuery(transformed, queryParams);
		int numHits = galagoResults.length;
		for (int i = 0; i < galagoResults.length; i++) {
			double score = galagoResults[i].score;
			int rank = i + 1;

			String name = galagoResults[i].documentName;
			if (debug) {
                System.out.format("%s Q0 %s %d %s galago\n", query.getQueryNum(), name, rank,
                        formatScore(score));
            }
			
			results.add(new Result(name, score));
			
		}

		if (debug) {
			System.out.println("Running query: " + query.getRawQuery() + " num hits:" + numHits + " num results:" + results.size());
		}
		
		if (query.fetchDocs()) {
			for (Result r : results) {
				r.setOriginalDoc(m_corpus.lookupDocument(r.getDocId()));
				// fill in original doc
			}
		}
		
		return results;
	}

	public static String formatScore(double score) {
		double difference = Math.abs(score - (int) score);

		if (difference < 0.00001) {
			return Integer.toString((int) score);
		}
		return String.format("%10.8f", score);
	}

}
