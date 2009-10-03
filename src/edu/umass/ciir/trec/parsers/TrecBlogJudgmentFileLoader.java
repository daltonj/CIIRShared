package edu.umass.ciir.trec.parsers;

import java.io.File;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.umass.ciir.FileRecordLoader;
import edu.umass.ciir.trec.types.TRECJudgment;

public class TrecBlogJudgmentFileLoader extends FileRecordLoader<TRECJudgment> {

	private static Pattern m_judgmentsPattern = Pattern.compile("(\\d+) (\\d+) (\\S+) (\\d+)");

	/**
	 * List of topics to load from the judgments file.
	 */
	private final HashSet<Integer> m_topicFilter;
	
	/**
	 * Constructor
	 * @param topicFilter optional (may be null) filter for topics to load
	 */
	public TrecBlogJudgmentFileLoader(File judgmentFile, HashSet<Integer> topicFilter) {
		super(judgmentFile);
		m_topicFilter = topicFilter;
	}
	
	@Override
	protected void processLine(String input) 
	throws Exception {
		Matcher m = m_judgmentsPattern.matcher(input);

		if (m.matches()) {	 
			int topicId = Integer.parseInt(m.group(1));
			if (m_topicFilter == null || m_topicFilter.contains(topicId)) {
				m_records.add(new TRECJudgment(topicId,
						Integer.parseInt(m.group(2)),
						m.group(3),
						Integer.parseInt(m.group(4))));	
			}
				
			
		} else {
			throw new Exception("Invalid record format, line:" + input);
		}	
	}
	
	

}
