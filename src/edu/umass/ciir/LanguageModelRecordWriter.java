package edu.umass.ciir;

import java.io.File;

import edu.umass.ciir.models.TermEntry;

public class LanguageModelRecordWriter extends FileRecordWriter<TermEntry> {

	public LanguageModelRecordWriter(File fileToWrite, long collectionTermFrequency) throws Exception {
		super(fileToWrite);
		m_outputWriter.println(collectionTermFrequency);
	}

	@Override
	public void write(TermEntry t) {
		m_outputWriter.println(t.getTerm() + "\t" + t.getFrequency() + "\t" + t.getNumTokens());
	}

}
