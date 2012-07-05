package edu.umass.ciir;

import java.io.File;
import java.io.FileReader;

public class DoubleRecordReader extends FileRecordReader<Double> {

	public DoubleRecordReader(File fileToRead,
			boolean catchParseExceptions, PrefixFilter prefixFilter)
			throws Exception {
		super(new FileReader(fileToRead), catchParseExceptions, prefixFilter);
	}
	
	@Override
	protected Double parseLine(String input) throws Exception {
		if (input == null) {
			throw new IllegalArgumentException("Cannot parse null input");
		}
		try {
			return Double.parseDouble(input);
		} catch (Exception e) {
			if (!m_catchParseExceptions) {
				throw e;
			} else {
				// seems odd to return null on a parse error.
				return null;
			}
		}
	}

}
