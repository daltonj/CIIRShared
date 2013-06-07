package edu.umass.ciir.memindex;

import java.util.ArrayList;
import java.util.List;

import org.lemurproject.galago.core.index.mem.MemoryIndex;
import org.lemurproject.galago.core.parse.Tag;

public class GalagoMemIndexer {

	 MemoryIndex m_index;
	 Corpus m_corpus;
	 
	 long m_curDoc = 0;
	 
	 public GalagoMemIndexer(MemoryIndex index) {
			m_index = index;
			m_curDoc = index.documentsInIndex();
		}
		
		public GalagoMemIndexer(MemoryIndex index, Corpus corpus) {
			m_index = index;
			m_corpus = corpus;
			m_curDoc = index.documentsInIndex();
		}
		
		public void addDocument(Document doc) 
		throws Exception {
			
			m_index.process(convertDoc(doc));
			
			if (m_corpus != null) {
				m_corpus.addDocument(doc.getDocId(), doc);
			}
		}
		
		public void addToCorpus(Document doc) {
			m_corpus.addDocument(doc.getDocId(), doc);
		}
		
		public void addIndexOnly(Document doc) 
		throws Exception {
			m_index.process(convertDoc(doc));
		}
		
		private org.lemurproject.galago.core.parse.Document convertDoc(Document doc) {
			org.lemurproject.galago.core.parse.Document galagoDoc = new org.lemurproject.galago.core.parse.Document(doc.getDocId(), doc.getOriginalDocument());
			
			// TODO:  This could be bad for passage level indices with > 2 billion passages!
			galagoDoc.identifier = (int) m_curDoc;
			galagoDoc.terms = (ArrayList<String>) doc.getDocTerms();
			List<String> tags = doc.getTags();
			
			ArrayList<Tag> galagoTags = new ArrayList<Tag>();
			for (int i=0; i < tags.size(); i++) {
				String tag = tags.get(i);
				if (tag != null) {
					galagoTags.add(new Tag(tag, null, i, i));
				}
			}
			galagoDoc.tags = galagoTags;
			m_curDoc++;
			return galagoDoc;
		}
		
		public MemoryIndex getIndex() {
			return m_index;
		}
		
}
