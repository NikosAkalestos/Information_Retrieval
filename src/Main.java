/**
 * Initializes an Indexer
 * Trainer with the Word2Vec
 * Searcher with the extended from Word2Vec synonyms
 */

public class Main{
    public static void main(String[] args) {
        try {
//            Indexer indexerDemo = new Indexer();
//            Trainer trainer = new Trainer(); // TODO
            Searcher searcher = new Searcher();
            System.out.println("\n\tResults are ready");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
