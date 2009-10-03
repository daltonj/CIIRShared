package edu.umass.ciir;

import java.io.File;
import edu.umass.ciir.models.TermEntry;

public class LanguageModelRecordReader extends FileRecordReader<TermEntry> {

	private long m_collectionFrequency;

	public long getCollectionFrequency() {
		return m_collectionFrequency;
	}

	public LanguageModelRecordReader(File fileToRead) throws Exception {
		super(fileToRead, false, null);
		String first = m_bufReader.readLine();
		try {
			m_collectionFrequency = Long.parseLong(first);
		} catch (Exception e) {
			throw new Exception("Invalid term entry record file.");
		}
	}

	@Override
	protected TermEntry parseLine(String input) throws Exception {
		String[] fields = input.split("\t");
		if (fields.length != 3) {
			throw new Exception("Invalid number of fields");
		}
		String term = fields[0];
		long frequency = Long.parseLong(fields[1]);
		int numTokens = Integer.parseInt(fields[2]);
		TermEntry te = new TermEntry(term, frequency, numTokens);
		double probability = ((double) frequency) / m_collectionFrequency;
		te.setProbability(probability);
		return te;
	}

}
