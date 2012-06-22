package edu.umass.ciir;

import java.io.File;
import java.io.FileReader;

import edu.umass.ciir.FileRecordReader;
import edu.umass.ciir.PrefixFilter;

public class TabFileRecordReader extends FileRecordReader<String[]> {

	public TabFileRecordReader(File fileToRead,
			boolean catchParseExceptions, PrefixFilter prefixFilter)
			throws Exception {
		super(new FileReader(fileToRead), catchParseExceptions, prefixFilter);
	}
	
	@Override
	protected String[] parseLine(String input) throws Exception {
		String[] fields = input.split("\t");
		return fields;
	}

}
