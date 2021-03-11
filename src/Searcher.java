import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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
import java.util.Scanner;


public class Searcher {

    public Searcher() {
        try {
            String indexLocation = ("index"); //define where the index is stored
            String field = "contents"; //define which field will be searched

            //Access the index using indexReaderFSDirectory.open(Paths.get(index))
            IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation))); //IndexReader is an abstract class, providing an interface for accessing an index.
            IndexSearcher indexSearcher = new IndexSearcher(indexReader); //Creates a searcher searching the provided index, Implements search over a single IndexReader.//
            indexSearcher.setSimilarity(new BM25Similarity());

            //Search the index using indexSearcher
            search(indexSearcher, field);

            //Close indexReader
            indexReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

            System.out.println("\n\tStart of synonym list\n");

            try {
                File myObj = new File("w2vec-final.txt");
                Scanner myReader = new Scanner(myObj);
                int i = 0, j = 0;
                String[] list = new String[40];
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    if (data.replaceAll(",|\\[|\\]", "").equals(""))
                        continue;
                    list[i] = data.replaceAll(",|\\[|\\]", "");
                    System.out.println(list[i]);
                    i++;
                }
                System.out.println("\n\tEnd of synonym list\n");
                myReader.close();

                for (int c = 0; c < 10; c++) {
                    queries[c]=queries[c].toLowerCase();
                    while (true) {
                        if (!(list[j] == null || list[j] == "")) {
                            String[] temp = list[j].split(" ", 2);
                            if (queries[c].contains(temp[0])) {
                                queries[c] += " " + temp[1];
                                j++;
                            } else if (!(queries[c].contains(temp[0]))) {
                                break;
                            }
                        } else if (list[j] == "") {
                            continue;
                        } else if (list[j] == null) {
                            break;
                        }
                    }
                    queries[c].trim().replaceAll(" +", " ");
                    System.out.println(queries[c]);
                }
                System.out.println("\n\tEnd of queries list and Start of Results log\n");



            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            for (int j = 0; j < 3; j++) {
                int qnumber = 1;
                // parse the query according to QueryParser
                try {
                    File testfile = new File("myresults" + k[j] + "t.test");
                    if (testfile.createNewFile()) {
                        System.out.println("File created: " + testfile.getName());
                    } else {
                        System.out.println("\nFile "+testfile+ " already exists. Overwriting...");
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
                        System.out.println("Search term:\t" + query.toString(field) + " \n\t\t\t\t// having " +numTotalHits +" hits and saving the top \'"+k[j]+"\' of them to the file");
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
