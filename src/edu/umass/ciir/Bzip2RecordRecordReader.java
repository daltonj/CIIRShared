package edu.umass.ciir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.tools.bzip2.CBZip2InputStream;

public abstract class Bzip2RecordRecordReader<T> extends FileRecordReader<T> {

		/**
		 * Constructor, opens the file for reading.  If this successfully completes the client must
		 * call close() to ensure the file is closed!
		 * 
		 * @param fileToRead
		 */
		public Bzip2RecordRecordReader(File fileToRead, boolean catchParseExceptions, PrefixFilter prefixFilter) throws Exception {
			super(catchParseExceptions, prefixFilter);
			InputStreamReader fileReader = new InputStreamReader(new CBZip2InputStream(new FileInputStream(fileToRead)));
			m_bufReader = new BufferedReader(fileReader);
		}
}
