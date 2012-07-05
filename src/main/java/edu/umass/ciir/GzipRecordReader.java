package edu.umass.ciir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

public abstract class GzipRecordReader<T> extends FileRecordReader<T> {

	/**
	 * Constructor, opens the file for reading.  If this successfully completes the client must
	 * call close() to ensure the file is closed!
	 * 
	 * @param fileToRead
	 */
	public GzipRecordReader(File fileToRead, boolean catchParseExceptions, PrefixFilter prefixFilter) throws Exception {
		super(catchParseExceptions, prefixFilter);
		InputStreamReader fileReader = new InputStreamReader(new GZIPInputStream(new FileInputStream(fileToRead)));
		m_bufReader = new BufferedReader(fileReader);
	}


}
