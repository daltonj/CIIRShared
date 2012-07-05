package edu.umass.ciir;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads a file of records into memory.  This is particularly
 * useful for parsing files of records stored one per line.
 * 
 * The entire file is read into memory and a list of the objects
 * can be access as a list.
 * 
 * Subclasses override processLine, which parses lines and adds
 * to the records (this allows more than one record per line, as well
 * as filtering of records).
 * 
 * @author Jeff Dalton
 *
 */
public abstract class FileRecordLoader <T> {
	
	/**
	 * If true, all lines starting with # will be ignored.
	 */
	private boolean m_ignoreComments = true;
	
	/**
	 * Empty lines will be ignored.
	 */
	private boolean m_skipBlank = true;
	
	/**
	 * The entries read from the file.
	 */
	protected List<T> m_records;

	/**
	 * The file to load
	 */
	private final File m_fileToLoad;
	
	
	public FileRecordLoader(File fileToLoad) {
		m_fileToLoad = fileToLoad;
		m_records = new ArrayList<T>();
	}
	

	/**
	 * Reads a set of records from a file into memory. The records can be accessed
	 * as a list after processing.
	 * 
	 * @param file
	 * @throws Exception
	 */
	public void load() 
	throws Exception {
		FileReader fileReader = new FileReader(m_fileToLoad);
		BufferedReader bufReader = new BufferedReader(fileReader);
		try {
			String input = null;
			while ((input = bufReader.readLine()) != null) {
				if (m_ignoreComments && input.startsWith("#") || (m_skipBlank && input.length() == 0)) {
					// skip comments or blank lines when appropriate
				} else {
					try {
						processLine(input);
					} catch (Exception e) {
						System.out.println("Error processing input:" + input);
						throw e;
					} 
				}
			}
			finish();
		} finally {
			bufReader.close();
		}
	}
	
	/**
	 * Parses a line of input into a structured record(s) and adds record(s)
	 * to m_records.
	 * 
	 * @param input line
	 * @return structured object
	 * @throws Exception
	 */
	protected abstract void processLine(String input) throws Exception ;
	
	
	/**
	 * Optional hook at the end of processing (normal processing).
	 * 
	 */
	protected void finish(){}

	
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
	
	/**
	 * After loading, this contains the records from the file.
	 * @return
	 */
	public List<T> getRecords() {
		return m_records;
	}

}
