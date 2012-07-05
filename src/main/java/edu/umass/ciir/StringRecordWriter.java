package edu.umass.ciir;

import java.io.File;

public class StringRecordWriter extends FileRecordWriter<String> {

	public StringRecordWriter(File fileToWrite) throws Exception {
		super(fileToWrite);
	}

	@Override
	public void write(String t) {
		m_outputWriter.println(t);
	}

}
