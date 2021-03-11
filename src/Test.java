import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        System.out.println("Test here");

        File myObj = new File("documents.txt");
        Scanner myReader = null;
        try {
            myReader = new Scanner(myObj);
            String[] words = new String[]{"multimod", "travel", "servic", "big", "data", "mobil", "european",
                    "logist", "applic", "architectur", "big", "data", "analyt", "architectur", "industri", "iot",
                    "mobil", "servic", "tool", "fragment", "iot", "feder", "seamless", "effici", "european", "travel",
                    "cross", "domain", "orchestr", "servic", "commun", "network"};
            ArrayList<Tester> w = new ArrayList<>();
            for (int i = 0; i < words.length; i++) {
                w.add(new Tester(words[i]));
            }

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                data.toLowerCase();
                for (int i = 0; i < words.length; i++) {
                    if (data.contains(words[i]))
                        w.get(i).setCounter(w.get(i).getCounter() + 1);
                }
            }

            for (int i = 0; i < words.length; i++) {
                System.out.println(w.get(i));
            }
            myReader.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }


    }
}

