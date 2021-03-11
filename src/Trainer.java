import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.deeplearning4j.models.embeddings.learning.impl.elements.SkipGram;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

//import org.apache.lucene.analysis.util.CharArraySet;

public class Trainer {

    /**
     * Configures IndexWriter.
     * Creates a lucene's inverted index.
     *
     * @throws Exception
     */
    public Trainer() throws IOException {
        String fileToTrain = "C:\\Users\\Default.DESKTOP-V5TCGDG\\Desktop\\IR2020-3.1\\IR2020_clean.txt";
        word2vec(fileToTrain);
    }

    /**
     * Trainer
     *
     * @param filepath: The relative path of the collection file.
     */
    public void word2vec(String filepath) {
        try {
            SentenceIterator iter = new BasicLineIterator(filepath);
            TokenizerFactory t = new DefaultTokenizerFactory();
            t.setTokenPreProcessor(new CommonPreprocessor());

            SkipGram MLP = new SkipGram();

            org.deeplearning4j.models.word2vec.Word2Vec vec = new org.deeplearning4j.models.word2vec.Word2Vec.Builder()
                    .minWordFrequency(4)
                    .layerSize(400)//TODO
                    .seed(42)
                    .windowSize(20)//TODO
                    .epochs(10)
                    .elementsLearningAlgorithm(MLP)
                    .tokenizerFactory(new LuceneTokenizerFactory(new WhitespaceAnalyzer()))//TODO
                    .iterate(iter)
                    .build();
            vec.fit();

            String[] words = new String[]{"multimodal", "travel", "services", "big", "data", "mobility", "european", "logistics", "applications",
                    "architectures", "big", "data", "analytics", "architecture", "industrial", "iot", "mobility-as-a-service", "tools",
                    "fragmentation", "iot", "federation", "seamless", "efficient", "european", "travelling", "cross-domain",
                    "orchestration", "services", "community", "networks"};

            try {
                File myObj = new File("w2vec.txt");
                if (myObj.createNewFile()) {
                    System.out.println("File created: " + myObj.getName());
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }

            try {
                FileWriter myWriter = new FileWriter("w2vec.txt");
                int numberofwords = 15; //TODO
                for (String w : words) {
                    Collection<String> lst = vec.wordsNearest(w, numberofwords);
                    System.out.println(numberofwords + " Words closest to '" + w + "': " + lst);
                    myWriter.write(w + " " + String.valueOf(lst) + "\n");
                }
                myWriter.close();
                System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
