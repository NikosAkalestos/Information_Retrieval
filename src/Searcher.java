import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

import java.io.*;
import java.nio.file.Paths;


public class Searcher {

    public Searcher() {
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
        Searcher searcher = new Searcher();
    }

    /**
     * Searches the index given a specific user query.
     */
    private void search(IndexSearcher indexSearcher, String field) {
        try {
            // define which analyzer to use for the normalization of user's query
            Analyzer analyzer = new EnglishAnalyzer(); //TODO

            // create a query parser on the field "contents"
            QueryParser parser = new QueryParser(field, analyzer);

            // read user's query from stdin
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
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
                    File testfile = new File("myresults" + k[j] + "t.test");
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
                    PrintWriter myWriter = new PrintWriter("myresults" + k[j] + "t.test", "UTF-8");

                    for (String q : queries) {
                        Query query = parser.parse(q);
                        TopDocs results = indexSearcher.search(query, k[j]);
                        ScoreDoc[] hits = results.scoreDocs;
                        long numTotalHits = results.totalHits;
                        System.out.println("Search term: " + query.toString(field) + " ||| having " +numTotalHits +" hits and saving the top \'"+k[j]+"\' of them to the file");
                        // search the index using the indexSearcher

//                        System.out.println(numTotalHits + " total matching documents");
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
