import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DocsParser {

    public static List<Doc> parser(String file) {
        try {
            File myfile = new File(file);
            Scanner myReader = new Scanner(myfile);
            String id = null;
            String[] saver = null;
            int lineholder = 0;
            List<Doc> myDocs = new ArrayList<Doc>();

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.matches("[0-9]+") && data.length() < 8) {
                    id = data;
                    lineholder = 0;
                }
                if (lineholder == 1) {
                    saver = data.split(":", 2);
                }
                if (lineholder > 1 && !data.contains("///")) {
                    saver[1] = saver[1] + data;
                }
                if ((data.contains("///") && data.length() < 6) || myReader.hasNextLine() == false) {
                    myDocs.add(new Doc(id, saver[0], saver[1]));
                }
                lineholder++;
            }//while
            myReader.close();
            return myDocs;

        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        return null;
    }
}
