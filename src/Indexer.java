import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

public class Indexer {

    /**
     * Configures IndexWriter.
     * Creates a lucene's inverted index.
     */
    public Indexer() throws Exception {

        String txtfile = "C:\\Users\\Default.DESKTOP-V5TCGDG\\Desktop\\IR2020-3.1\\documents.txt"; //txt file to be parsed and indexed, it contains one document per line
        String indexLocation = ("index"); //define were to store the index

        Date start = new Date();
        try {
            System.out.println("Indexing to directory '" + indexLocation + "'...");

            Directory dir = FSDirectory.open(Paths.get(indexLocation));
            // define which analyzer to use for the normalization of documents
            Analyzer analyzer = new EnglishAnalyzer(); //TODO
            Similarity similarity = new BM25Similarity();
            // configure IndexWriter
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setSimilarity(similarity);

            // Create a new index in the directory, removing any
            // previously indexed documents:
            iwc.setOpenMode(OpenMode.CREATE);

            // create the IndexWriter with the configuration as above
            IndexWriter indexWriter = new IndexWriter(dir, iwc);

            // parse txt document using TXT parser and index it
            List<Doc> docs = DocsParser.parser(txtfile);
            for (Doc doc : docs) {
                indexDoc(indexWriter, doc);
            }

            indexWriter.close();

            Date end = new Date();
            System.out.println(end.getTime() - start.getTime() + " total milliseconds");

        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass() +
                    "\n with message: " + e.getMessage());
        }


    }

    /**
     * Creates a Doc by adding Fields in it and
     * indexes the Doc with the IndexWriter
     *
     * @param indexWriter the indexWriter that will index Doc
     * @param mydoc       the document to be indexed
     */
    private void indexDoc(IndexWriter indexWriter, Doc mydoc) {

        try {

            // make a new, empty document
            Document doc = new Document();

            // create the fields of the document and add them to the document
            StoredField id = new StoredField("id", mydoc.getId());
            doc.add(id);
            StoredField title = new StoredField("title", mydoc.getTitle());
            doc.add(title);
            StoredField data = new StoredField("data", mydoc.getData());
            doc.add(data);
            String fullSearchableText = mydoc.getTitle() + " " + mydoc.getData();
            TextField contents = new TextField("contents", fullSearchableText, Field.Store.NO);
            doc.add(contents);

            if (indexWriter.getConfig().getOpenMode() == OpenMode.CREATE) {
                // New index, so we just add the document (no old document can be there):
                indexWriter.addDocument(doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
