package edu.umass.ciir.clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import org.lemurproject.galago.core.parse.Document;
import org.lemurproject.galago.core.parse.TagTokenizer;

import edu.umass.ciir.crawling.SimpleContentCrawler;
import edu.umass.ciir.models.KLDivergenceSimilarity;
import edu.umass.ciir.models.LanguageModel;
import edu.umass.ciir.models.SimilarityMeasure;

public class Clustering {
	
	/*** path to document */
	public static String path = "../data/axes/data/";
		
	
	/*** Builds a LM for an axis */
	public static LanguageModel buildLMFromAxis(String axis, int windowSize, int minNgram) throws Exception {
		
		// get docs for the axis
		String[] dirs = new File(path + "/" + axis).list();
		ArrayList<File> files= new ArrayList<File>();
		for(String dir : dirs) {	
			File[] ff = new File(path + "/" + axis + "/" + dir).listFiles(new FilenameFilter() {
			       public boolean accept(File f, String s) {return s.endsWith(".txt"); }});
			if (ff!=null) files.addAll(Arrays.asList(ff));
		}
				
		// build LMs
		LanguageModel lm = new LanguageModel(windowSize);
		lm.setMinNgramLength(minNgram);
		
		for (File f : files) {
			String docText="";
			BufferedReader documentBuf= new BufferedReader(new FileReader(f));
			StringBuffer text = new StringBuffer();

			while ((docText=documentBuf.readLine())!=null) {
				text.append(docText);
			}

			Document d = new Document("", text.toString());
			new TagTokenizer().process(d);
			lm.addDocument(d, true);		
		}
		
		lm.calculateProbabilities();
		return lm;
	}

	public static String getDocumentContent(String url)  throws Exception {
		try {
			SimpleContentCrawler contentFetcher = new SimpleContentCrawler();
			return contentFetcher.fetchUrl(url);
		} catch (UnknownHostException e) {	}
		return null;
	}
	
	public static LanguageModel keepGeneratingLMFromContent(String content, LanguageModel useThis) throws Exception {
		if (content != null) {
			Document d = new Document("", content);
			new TagTokenizer().process(d);
			useThis.addDocument(d, true);
		}
		return useThis;
	}
	

	
	public static double symmetricKLDivergence(LanguageModel lm1, LanguageModel lm2) {
	
		KLDivergenceSimilarity klDiv1 = new KLDivergenceSimilarity(null,2500);
		SimilarityMeasure sm1 = klDiv1.calculateSimilarity(lm1, lm2, true);
		KLDivergenceSimilarity klDiv2 = new KLDivergenceSimilarity(null, 2500);
		SimilarityMeasure sm2 = klDiv2.calculateSimilarity(lm2, lm1, true);
		return Math.abs(sm1.getSimilarity()) + Math.abs(sm2.getSimilarity());
	}	   
}
