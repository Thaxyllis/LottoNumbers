package Lotto;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;

public class Main extends Application {

    public static List<Numbers> listNumbers = new LinkedList<>();

    public static Integer numCnt = 0;
    public static int mode = 0;
    public static String sPrint = "";

    /**
     * Starting method for JAVAFX.
     * @param primaryStage Main stage object.
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Lotto.fxml"));
        primaryStage.setTitle("Asystent lotto");
        primaryStage.setScene(new Scene(root, 800, 580));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Switch for different database file URL's.
     * Depends on global mode variable in Main Class.
     * @throws Exception
     */
    public static void buildDatabase() throws Exception {
        URLReader reader = new URLReader();
        if(mode == 0) {
            reader.main("http://thax.pl/lotto/dl.txt");
        } else if(mode == 1) {
            reader.main("http://thax.pl/lotto/dl_plus.txt");
        } else if(mode == 2) {
            reader.main("http://thax.pl/lotto/el.txt");
        }
    }

    /**
     * Setting global mode variable when choiceBox is used and clears the collections.
     * @param m Mode variable: 0 - Lotto, 1 - Plus, 2 - Mini Lotto.
     */
    public static void setMode(int m) {

        mode = m;
        listNumbers.removeAll(Main.listNumbers);
        numCnt = 0;
        sPrint = "";
    }

    /**
     * Main method
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

/*
        int cnt2 = 0;
        for (Numbers d : listNumbers) {
            for(Numbers d2 : listNumbers) {
                if(Arrays.equals(d.getNumbers(), d2.getNumbers()) && (d.getID() != d2.getID()) && (!d.getNumbers().equals(d2.getNumbers())) && !d2.getWasRolled()) {
                    ++cnt2;
                    d2.setWasRolled(true);
                }
            }
        }

        System.out.println("DUPLICATED: "+cnt2);
*/
        launch(args);
    }

}

/**
 * URL Reader Class. Loads data from remote files to listNumbers
 */
class URLReader {
    /**
     * Main Reader method. Loads data from remote files. As result every line should look like "num.date,numbers"
     * @param url Remote URL to data file
     * @throws Exception
     */
    public static void main(String url) throws Exception {

        URL datafile = new URL(url);
        BufferedReader in = new BufferedReader(
        new InputStreamReader(datafile.openStream()));

        String inputLine;
        String[] exploded;
        String[] explodedNum;

        while ((inputLine = in.readLine()) != null) {
            exploded = inputLine.split(" ");
            explodedNum = exploded[2].split(",");
            if(Main.mode != 2) {
                Main.listNumbers.add(new Numbers(new int[]{Integer.parseInt(explodedNum[0]), Integer.parseInt(explodedNum[1]), Integer.parseInt(explodedNum[2]), Integer.parseInt(explodedNum[3]), Integer.parseInt(explodedNum[4]), Integer.parseInt(explodedNum[5])}, exploded[1]));
            } else {
                Main.listNumbers.add(new Numbers(new int[]{Integer.parseInt(explodedNum[0]), Integer.parseInt(explodedNum[1]), Integer.parseInt(explodedNum[2]), Integer.parseInt(explodedNum[3]), Integer.parseInt(explodedNum[4])}, exploded[1]));
            }
        }
        in.close();
        Collections.reverse(Main.listNumbers);
    }
}
/**
 * Object Class for single combination storing
 */
class Numbers
{
    private int[] numSet;
    private String date;
    private int id;

    /**
     * Main Class with single combination data
     * @param n int array with combination of 5 or 6 numbers
     * @param d string with date (we don't need date format)
     */
    public Numbers(int[] n, String d) {
        int count;
        if(Main.mode != 2) {
            numSet = new int[6];
            count = 6;
        } else {
            numSet = new int[5];
            count = 5;
        }

        for (int i = 0; i < count; i++) {
            numSet[i] = n[i];
        }
        date = d;
        id = ++Main.numCnt;
        Arrays.sort(numSet);
    }

    /**
     * Set of numbers
     * @return stored numbers
     */
    public int[] getNumbers() {
        return numSet;
    }

    /**
     * Date string
     * @return date of rolling
     */
    public String getDate() {
        return date;
    }
}

/**
 * Class for manipulation on number sets
 */
class Lotto {
    /**
     * Unique number sets generator. Ensures that generated numbers never occured.
     * @param count How many sets to generate
     * @return generated set
     */
    public static int[] genUnique(int count) {
        int balls;
        if(Main.mode != 2) balls = 50; else balls = 43;
        ArrayList<Integer> list = new ArrayList<>();
        int[] numbers = new int[count];

        for(int c = 1; c < 5; c++) {
            for (int i = 1; i < balls; i++) {
                list.add(new Integer(i));
            }
            Collections.shuffle(list);
            for (int i = 0; i < count; i++) {
                numbers[i] = list.get(i);
            }
            Arrays.sort(numbers);
            if(Lotto.checkDuplication(numbers) == false) {
                break;
            }
        }
        return numbers;
    }

    /**
     * Checking if given set of numbers ever occured
     * @param numbers set of numbers
     * @return state of occurance in history
     */
    public static boolean checkDuplication(int[] numbers) {
        boolean state = false;
        for (Numbers d : Main.listNumbers) {
            if(Arrays.equals(d.getNumbers(), numbers)) {
                state = true;
            }
        }
        return state;
    }

    /**
     * Checking when the numbers occured
     * @param numbers
     * @return
     */
    public static String checkDuplicationDate(int[] numbers) {
        String date = "";
        for (Numbers d : Main.listNumbers) {
            if(Arrays.equals(d.getNumbers(), numbers)) {
                date = d.getDate();
            }
        }
        return date;
    }

    /**
     * Counts every number in every set in history as statistic data
     * @return Map of data to present sorted by descending
     */
    public static Map<String, Integer> countNumbers() {
        int[] nums = { 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
        int balls;
        if(Main.mode != 2) balls = 49; else balls = 42;
        /**
         * Data Collection
         */
        for (int i = 1; i < 50; i++) {
            for (Numbers n : Main.listNumbers) {
                final int iR = i;
                if(IntStream.of(n.getNumbers()).anyMatch(x -> x == iR)) {
                    nums[i-1]++;
                }
            }
        }
        /**
         * Generate Map for further sorting
         */
        Map<String, Integer> rawNums = new HashMap<>();

        for (int i=0; i < balls; i++) {
            int tempKey = i + 10;
            rawNums.put(tempKey+"&"+Integer.toString(i+1), nums[i]);
        }
        /**
         * Sorting Map by value to achive descending view
         */
        Map<String, Integer> sorted = rawNums
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));

        return sorted;
    }

    /**
     * Method for printing occurance of every number in history
     * @return value of rolling times
     */
    public static String printCountedNumbers() {
        String print = "";
        Map<String , Integer> numMap = Lotto.countNumbers();
        Iterator iterator = numMap.keySet().iterator();

        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            Integer value = numMap.get(key);
            print += "Ilość wylosowań:   " + value + "     dla liczby:   " + key.substring(3) + "\n";
        }
        return print;
    }

    /**
     * Checking method if user entered number are actualy numbers.
     * @param strNum user entered numbers.
     * @return
     */
    public static boolean isNumeric(String strNum) {
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException | NullPointerException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Checking method for user entered numbers as a whole set. Mode variable dependant.
     * @param n1
     * @param n2
     * @param n3
     * @param n4
     * @param n5
     * @param n6
     * @return
     */
    public static boolean isNumericMulti(String n1, String n2, String n3, String n4 ,String n5,String n6) {
        if(Main.mode != 2) {
            if (Lotto.isNumeric(n1) && Lotto.isNumeric(n2) && Lotto.isNumeric(n3) && Lotto.isNumeric(n4) && Lotto.isNumeric(n5) && Lotto.isNumeric(n6)) {
                return true;
            } else {
                return false;
            }
        } else {
            if (Lotto.isNumeric(n1) && Lotto.isNumeric(n2) && Lotto.isNumeric(n3) && Lotto.isNumeric(n4) && Lotto.isNumeric(n5)) {
                return true;
            } else {
                return false;
            }
        }
    }
}