package edu.umass.ciir;

import java.io.File;
import java.io.FileReader;

import edu.umass.ciir.models.TermEntry;

public class LanguageModelRecordReader extends FileRecordReader<TermEntry> {

	private long m_collectionFrequency;
	
	public LanguageModelRecordReader(File fileToRead) throws Exception {
		super(new FileReader(fileToRead), false, null);
		String first = m_bufReader.readLine();
		try {
			m_collectionFrequency = Long.parseLong(first);
		} catch (Exception e) {
			throw new Exception("Error loading LM, unable to read CF.  File:" + fileToRead.getAbsolutePath());
		}
	}

	@Override
	protected TermEntry parseLine(String input) throws Exception {
		String[] fields = input.split("\t");
		if (fields.length != 5) {
			throw new Exception("Invalid number of fields");
		}
		String term = fields[0];
		long frequency = Long.parseLong(fields[1]);
		double probability = Double.parseDouble(fields[2]);
		long documentFrequency = Integer.parseInt(fields[3]);
		int numTokens = Integer.parseInt(fields[4]);
		TermEntry te = new TermEntry(term, frequency, numTokens, documentFrequency);
		te.setProbability(probability);
		return te;
	}
	
	public long getCollectionFrequency() {
		return m_collectionFrequency;
	}

}
