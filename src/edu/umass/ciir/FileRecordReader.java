package edu.umass.ciir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Allows a file of records, one per line, to be read entry by entry via
 * subsequent calls to read().
 * 
 * This is useful for processing a large file of records that will not fit 
 * into memory.
 * 
 * Note: Clients must call close() to ensure the file is closed!
 * 
 * @author Jeff Dalton
 *
 * @param <T>
 */
public abstract class FileRecordReader<T> {

	/**
	 * If true, all lines starting with # will be ignored.
	 */
	private boolean m_ignoreComments = true;
	
	/**
	 * Empty lines will be ignored.
	 */
	private boolean m_skipBlank = true;

	/**
	 * If true, individual errors parsing lines will be ignored.
	 */
	protected final boolean m_catchParseExceptions;

	/**
	 * The underlying buffered reader.
	 */
	protected BufferedReader m_bufReader;

	/**
	 * A simple prefix filter.  Terms after this will be ignored.
	 */
	protected final PrefixFilter m_prefixFilter;
	
	/**
	 * Constructor, opens the file for reading.  If this successfully completes the client must
	 * call close() to ensure the file is closed!
	 * 
	 * @param fileToRead
	 */
	public FileRecordReader(File fileToRead, boolean catchParseExceptions, PrefixFilter prefixFilter) throws Exception {
		m_prefixFilter = prefixFilter;
		FileReader fileReader = new FileReader(fileToRead);
		m_bufReader = new BufferedReader(fileReader);
		m_catchParseExceptions = catchParseExceptions;
	}
	
	/**
	 * Constructor, opens the file for reading.  If this successfully completes the client must
	 * call close() to ensure the file is closed!
	 * 
	 * @param fileToRead
	 */
	protected FileRecordReader(boolean catchParseExceptions, PrefixFilter prefixFilter) throws Exception {
		m_prefixFilter = prefixFilter;
		m_catchParseExceptions = catchParseExceptions;
	}
	
	/**
	 * Reads a file and parses it, line by line.
	 * 
	 * @return T object parsed from the line, or null if there are no more entries
	 * @throws Exception 
	 */
	public T read() 
	throws Exception {
		T obj = null;
		String input = null;
		while ((obj == null) && (input = m_bufReader.readLine()) != null) {
			if (m_ignoreComments && input.startsWith("#") || 
					(m_skipBlank && input.length() == 0) || 
					(m_prefixFilter != null && m_prefixFilter.filterOut(input))) {
				// skip comments or blank lines when appropriate
			} else {
				try {
					obj =  parseLine(input);
				} catch (Exception e) {
					System.out.println("Error parsing input:" + input);
					if (!m_catchParseExceptions) {
						throw e;
					}
				} 
			}
		}
		return obj;
	}
	
	protected abstract T parseLine(String input) throws Exception ;
	
	/**
	 * This MUST be closed.
	 */
	public void close() throws Exception {
		m_bufReader.close();
	}

	public void setIgnoreComments(boolean ignoreComments) {
		m_ignoreComments = ignoreComments;
	}

	public void setSkipBlank(boolean skipBlank) {
		m_skipBlank = skipBlank;
	}

	public boolean getIgnoreComments() {
		return m_ignoreComments;
	}

	public boolean getSkipBlank() {
		return m_skipBlank;
	}
}
