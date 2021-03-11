import org.apache.lucene.analysis.core.LetterTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.en.EnglishPossessiveFilterFactory;
import org.apache.lucene.analysis.en.PorterStemFilterFactory;
import org.apache.lucene.analysis.miscellaneous.KeywordRepeatFilterFactory;
import org.apache.lucene.analysis.miscellaneous.RemoveDuplicatesTokenFilterFactory;
import org.apache.lucene.analysis.miscellaneous.StemmerOverrideFilterFactory;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilterFactory;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.apache.lucene.analysis.synonym.SynonymGraphFilterFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class WordNet {

    public WordNet() {
        try {
            String indexLocation = ("index"); //define where the index is stored
            String field = "contents"; //define which field will be searched

            //Access the index using indexReaderFSDirectory.open(Paths.get(index))
            IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation))); //IndexReader is an abstract class, providing an interface for accessing an index.
            IndexSearcher indexSearcher = new IndexSearcher(indexReader); //Creates a searcher searching the provided index, Implements search over a single IndexReader.//
            indexSearcher.setSimilarity(new BM25Similarity());//todo

            //Search the index using indexSearcher
            search(indexSearcher, field);

            //Close indexReader
            indexReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize a Searcher
     */
    public static void main(String[] args) {

        if (new File("wn_s2.pl").exists()) {
            String filenameOR = "wn_s.pl";
            String filenameCR = "wn_s2.pl";
            // Read the content from file
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filenameOR))) {
                String line = bufferedReader.readLine();
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filenameCR))) {
                    while (line != null) {
                        if (line.contains(",v,")) {
                            line = bufferedReader.readLine(); //skip verb line
                        }
                        bufferedWriter.write(line + "\n");
                        line = bufferedReader.readLine();
                    }
                    bufferedWriter.close();
                }
                bufferedReader.close();
            } catch (FileNotFoundException e) {
                // Exception handling
            } catch (IOException e) {
                // Exception handling
            }
        }
        WordNet searchwithwordnet = new WordNet();
    }

    private static CustomAnalyzer customAnalyzerForQueryExpansion() throws IOException {
        //Read synonyms from wn_s.pl file
//        File currentDir = new File(".");
//        File parentDir = currentDir.getParentFile();
//        File newFile = new File(new File("."),"wn_s2.pl");;
        Map<String, String> sffargs = new HashMap<>();
        sffargs.put("synonyms","wn_s2.pl");
        sffargs.put("format", "wordnet");


        //    Create custom analyzer for analyzing query text.
        //    Custom analyzer should analyze query text like the EnglishAnalyzer and have
        //    an extra filter for finding the synonyms of each token from the Map sffargs
        //    and add them to the query.
        CustomAnalyzer.Builder builder = CustomAnalyzer.builder()

                .addTokenFilter(LowerCaseFilterFactory.class)
                .addTokenFilter(StopFilterFactory.class)
                .addTokenFilter(WordDelimiterGraphFilterFactory.class)
                .addTokenFilter(StandardFilterFactory.class)
                .addTokenFilter(EnglishPossessiveFilterFactory.class)
                .addTokenFilter(PorterStemFilterFactory.class)
                .addTokenFilter(SynonymGraphFilterFactory.class, sffargs)
                .addTokenFilter(RemoveDuplicatesTokenFilterFactory.class)
                .withTokenizer(StandardTokenizerFactory.class);

        CustomAnalyzer analyzer = builder.build();
        return analyzer;
    }

    /**
     * Searches the index given a specific user query.
     */
    private void search(IndexSearcher indexSearcher, String field) {
        try {
            // define which analyzer to use for the normalization of user's query
            CustomAnalyzer query_analyzer = customAnalyzerForQueryExpansion();//TODO

            // create a query parser on the field "contents"
            QueryParser parser = new QueryParser(field, query_analyzer);

            String result = null;
            String[] queries = new String[10];
            queries[0] = "multimodal travel services";
            queries[1] = "Big Data for Mobility";
            queries[2] = "European logistics applications";
            queries[3] = "Architectures for Big Data Analytics";
            queries[4] = "Architecture for Industrial IoT";
            queries[5] = "Mobility-as-a-Service tools";
            queries[6] = "fragmentation of IoT through federation";
            queries[7] = "Seamless Efficient European Travelling";
            queries[8] = "cross-domain orchestration of services";
            queries[9] = "Community networks";
            int[] k = new int[]{20, 30, 50};

            for (int j = 0; j < 3; j++) {
                int qnumber = 1;
                // parse the query according to QueryParser
                try {
                    File testfile = new File("my2ndresults" + k[j] + ".test");
                    if (testfile.createNewFile()) {
                        System.out.println("File created: " + testfile.getName());
                    } else {
                        System.out.println("File already exists. Overwriting...");
                    }
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
                try {
                    PrintWriter myWriter = new PrintWriter("my2ndresults" + k[j] + ".test", "UTF-8");

                    for (String q : queries) {
                        Query query = parser.parse(q);

                        int hitsPerPage = k[j];
                        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
                        indexSearcher.search(query, collector);
                        ScoreDoc[] hits = collector.topDocs().scoreDocs;
                        System.out.println("Search term: " + query.toString(field) + "\n\t|||having " + hits.length + " hits and saving the top \'" + k[j] + "\' of them to the file");
                        //save results string
                        for (int i = 0; i < hits.length; i++) {
                            Document hitDoc = indexSearcher.doc(hits[i].doc);
                            if (qnumber < 10) {
                                result = "Q0" + qnumber + "\tQ0\t" + hitDoc.get("id") + "\t0\t" + hits[i].score + "\tFirstTry";
                            }
                            if (qnumber == 10) {
                                result = "Q" + qnumber + "\tQ0\t" + hitDoc.get("id") + "\t0\t" + hits[i].score + "\tFirstTry";
                            }
                            myWriter.println(result);
                        }
                        qnumber++;
                    }
                    myWriter.close();
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
