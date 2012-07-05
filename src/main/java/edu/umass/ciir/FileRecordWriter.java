package edu.umass.ciir;

import java.io.File;
import java.io.PrintWriter;

public abstract class FileRecordWriter<T> {
	
	protected PrintWriter m_outputWriter;
	
	public FileRecordWriter(File fileToWrite) 
	throws Exception {
		m_outputWriter = new PrintWriter(fileToWrite);
	}
	
	public abstract void write(T t);
	
	public void close() {
		m_outputWriter.flush();
		m_outputWriter.close();
	}
}
