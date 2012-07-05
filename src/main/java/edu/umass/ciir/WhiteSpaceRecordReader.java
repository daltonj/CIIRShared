package edu.umass.ciir;

import java.io.File;
import java.io.FileReader;

public class WhiteSpaceRecordReader extends FileRecordReader<String[]> {

	public WhiteSpaceRecordReader(File fileToRead,
			boolean catchParseExceptions, PrefixFilter prefixFilter)
			throws Exception {
		super(new FileReader(fileToRead), catchParseExceptions, prefixFilter);
	}
	
	@Override
	protected String[] parseLine(String input) throws Exception {
		String[] fields = input.split("\\s+");
		return fields;
	}

}
