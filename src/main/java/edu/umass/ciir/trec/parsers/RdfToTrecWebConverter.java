package edu.umass.ciir.trec.parsers;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.text.StringEscapeUtils;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

public class RdfToTrecWebConverter {

  int m_curDoc = 0;
  private File m_curFile;
  private final File m_outputDir;
  private PrintWriter m_outputWriter;

  private HashSet<String> m_fields = new HashSet<String>();
  private HashSet<String> m_ontologies = new HashSet<String>();
  private PrintWriter m_fieldWriter;
  private PrintWriter m_ontologyWriter;
  private PrintWriter m_docNoToUrlWriter;

  public RdfToTrecWebConverter(File outputDir) throws Exception {
    m_outputDir = outputDir;

    m_ontologyWriter = new PrintWriter(new File(m_outputDir + "/ontologies"));


  }

  public void parse(File file)
          throws Exception {
    m_curFile = file;

    String outputXmlPath = m_outputDir + "/" + m_curFile.getName();
    outputXmlPath = outputXmlPath.replace("-urified.gz", "");
    outputXmlPath = outputXmlPath + "";

    String fieldPath = m_outputDir + "/fields/" + m_curFile.getName();
    fieldPath = fieldPath.replace("-urified.gz", "");
    fieldPath = fieldPath + "";

    m_outputWriter = new PrintWriter(outputXmlPath);
    m_fieldWriter = new PrintWriter(new File(fieldPath + "_fields"));

    String docNoToUrlPath = fieldPath.replaceAll("fields", "docNoToUrl");
    m_docNoToUrlWriter = new PrintWriter(new File(docNoToUrlPath + ".name"));

    System.out.println("Now processing file:" + m_curFile.getName());
    InputStream is = new FileInputStream(file);
    try {
      NxParser nxp = new NxParser();
      BufferedInputStream bis = new BufferedInputStream(is);
      CompressorInputStream input = new CompressorStreamFactory().createCompressorInputStream(bis);
      BufferedReader br2 = new BufferedReader(new InputStreamReader(input));
      nxp.parse(br2);
      Node subj = null;
      HashMap<Node,Node> attrs = new HashMap<Node,Node>();

      // max number of records per input file to process.
      int limit = 100000000;

      int numSubjects = 0;

      while (nxp.hasNext() && numSubjects < limit) {

        Node[] ns = nxp.next();

        Node curSubject = ns[0];
        Node predicate = ns[1];
        Node object = ns[2];

        if (subj !=null && !subj.equals(curSubject) ) {
          if (numSubjects % 1000 ==0 && numSubjects > 0 ) {
            System.out.println("Parsing tuple:" + numSubjects);
          }
          attributesToXml(subj, attrs);
          numSubjects++;
          attrs = new HashMap<Node,Node>();
        }

        m_ontologies.add(stripAnchor(clean(predicate.toString())));
        String field = toIndriSafeFieldString(clean(predicate.toString()));
        m_fields.add(field);

        subj = curSubject;
        attrs.put(predicate, object);
      }

      // be sure to convert the last subject.
      attributesToXml(subj, attrs);

      writeFields();
      //writeOntologies();
    } finally {
      is.close();
      m_fieldWriter.close();
      m_ontologyWriter.close();
      m_outputWriter.close();
      m_docNoToUrlWriter.close();
    }

  }

  private String stripAnchor(String predicateUrl) {
    int anchorIdx = predicateUrl.indexOf("#");
    if (anchorIdx > -1) {
      return predicateUrl.substring(0,anchorIdx);
    } else {
      return predicateUrl;
    }
  }

  private void writeFields() throws Exception {
    for (String field : m_fields) {
      m_fieldWriter.println("<field><name>" + field + "</name></field>");
    }
    m_fields.clear();
  }

  private void writeOntologies() throws Exception {

    String[] sortedOntologies = m_ontologies.toArray(new String[0]);
    Arrays.sort(sortedOntologies);
    for (String ontology : sortedOntologies) {
      m_ontologyWriter.println(ontology);
    }

    m_ontologies.clear();
  }

  /**
   * Write a subject as XML with all of its predicates and objects.
   *
   * @param subject
   * @param attributeMap
   * @throws Exception
   */
  public void attributesToXml(Node subject, HashMap<Node,Node> attributeMap)
          throws Exception {
    startDoc();
    String url = clean(subject.toString());
    writeDocNo(url);
    writeUrl(url);

    if (url.startsWith("http://dbpedia.org")) {
      // grab the end of it as the title.
      int lastSegment = url.lastIndexOf('/');
      String value = url.substring(lastSegment+1, url.length()).toLowerCase();
      addField("dbpedia-title", value);
    }

    //System.out.println("\ndocid = "+ clean(subject.toN3()) + " ; num attributes:" + attributeMap.size());

    boolean hasType = false;

    String title = null;
    String name = null;
    StringBuilder text = new StringBuilder();
    for (Node key : attributeMap.keySet()) {
      String field = toIndriSafeFieldString(clean(key.toString()));
      String value = clean(attributeMap.get(key).toString());
      //System.out.println(field + ":" + value);

      String fieldLower = field.toLowerCase();
      if (fieldLower.endsWith("type")) {
        hasType = true;
        addField("objectType", extractType(value));
      }

      if (fieldLower.endsWith("title") && title == null) {
        if (value.toLowerCase().startsWith("http")) {
          int lastSegment = url.lastIndexOf('/');
          title = url.substring(lastSegment+1, url.length()).toLowerCase();
        } else {
          title = value.toLowerCase();
        }
        addField("object-title", title);
      }

      if (fieldLower.endsWith("name") && name == null) {
        if (value.toLowerCase().startsWith("http")) {
          int lastSegment = url.lastIndexOf('/');
          name = url.substring(lastSegment+1, url.length()).toLowerCase();
        } else {
          name = value.toLowerCase();
        }
        addField("object-name", name);
      }

      if (!value.toLowerCase().startsWith("http") && value.indexOf(' ') > -1) {
        text.append(value);
      }

      addField(field, value);
    }

    addField("text-value", text.toString());

    if (!hasType) {
      // lump it as a miscellaneous object type
      addField("objectType", "object-other");
    }
    endDoc();
    m_curDoc++;
  }


  private String extractType(String url) {
    // the last segment of the URL is the type.
    int lastSegment = url.lastIndexOf('/');
    String value = url.substring(lastSegment+1, url.length()).toLowerCase();
    int anchorIdx = value.indexOf('#');
    if ( anchorIdx > -1) {
      value = value.substring(anchorIdx+1, value.length());
    }

    value = value.replace('"', ' ').trim();

    return value;
  }

  private void writeDocNo(String url) {

    String docNo = m_curFile.getName().replace("-urified.gz", "");
    docNo = docNo + "_" + m_curDoc;
    m_docNoToUrlWriter.println(docNo + "\t" + url);
    m_outputWriter.print("<DOCNO>");
    m_outputWriter.print(docNo);
    m_outputWriter.print("</DOCNO>\n<DOCHDR>\n" + url + "\n</DOCHDR>\n");

  }

  private String clean(String str) {
    String newString = str.replace("<", "");
    newString = newString.replace(">", "");
    return newString;
  }

  private String toIndriSafeFieldString(String str) {

    String value = str;
    Pattern pattern = Pattern.compile("[^a-zA-Z]");
    value = pattern.matcher(value).replaceAll("");
    return value;
  }

  private String toIndriSafeTextString(String str) {

    String value = str;
    Pattern pattern = Pattern.compile("[^a-zA-Z]");
    value = pattern.matcher(value).replaceAll(" ");
    return value;
  }

  private void startDoc() {
    m_outputWriter.println("\n<DOC>");
  }

  private void endDoc() {
    m_outputWriter.println("\n</DOC>");
  }

  private void writeUrl(String url) {
    m_outputWriter.println("<url>");
    m_outputWriter.print(StringEscapeUtils.escapeXml11(url));
    m_outputWriter.print("</url>");
  }

  private void addField(String field, String value) {
    m_outputWriter.println("<" + field + ">");
    m_outputWriter.print(StringEscapeUtils.escapeXml11(value));
    m_outputWriter.println("\n</" + field + ">");
  }


  public static void main(String[] args)
          throws Exception {
    System.out.println("arguments: " + args[0] + " " + args[1]);
    File inputFile = new File(args[0]);
    File outputDir = new File(args[1]);
    RdfToTrecWebConverter reader = new RdfToTrecWebConverter(outputDir);

    if (inputFile.isDirectory()) {
      cleanDirectory(outputDir);

      FilenameFilter filter = new FilenameFilter() {
        public boolean accept(File dir, String name) {
          return name.endsWith(".gz");
        }
      }; // End of anonymous inner class

      File[] xmlFiles = inputFile.listFiles(filter);
      for (File xmlFile : xmlFiles) {
        reader.parse(xmlFile);
      }

    } else if (inputFile.getName().endsWith(".gz")) {
      reader.parse(inputFile);
    }

  }

  private static void cleanDirectory(File dir) throws Exception {
    if (dir.exists()) {
      File[] xmlFiles = dir.listFiles();
      for (File file : xmlFiles) {
        file.delete();
      }
    } else {
      dir.mkdir();
      if (!dir.exists()) {
        throw new Exception("Unable to create output directory.");
      }
    }
  }
}

