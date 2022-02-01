// EVANGELOS PIPILIKAS | 3180157

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class ReadFile {

    public ReadFile() {
    }

    public int getNumberOfLines(String filename) {
        String line;
        BufferedReader reader = null;
        int fileLines = 0;
        try {
            reader = new BufferedReader(new FileReader(new File(filename)));
            line = reader.readLine();
            while (line != null) {
                fileLines++;
                line = reader.readLine();
            }
            reader.close();
        }
        catch (IOException ex) {
            System.err.println(">!< Error reading the file. >!<");
        }

        return fileLines;
    }

    public char[][] extractData(String filename, int numberOfFeatures, int numberOfLines, int n) {
        char[][] data = initializeTable(numberOfFeatures - n, numberOfLines);
        int lineNumber = 0;
        String line;
        BufferedReader reader = null;
        StringTokenizer tokenizer;
        String token;
        int rate;
        int feature;

        try {
            reader = new BufferedReader(new FileReader(new File(filename)));
            line = reader.readLine();

            while (line != null) {
                tokenizer = new StringTokenizer(line, " ");
                token = tokenizer.nextToken();
                rate = Integer.parseInt(token);
                if (rate > 5) {
                    data[lineNumber][0] = '1';
                }
                else {
                    data[lineNumber][0] = '0';
                }

                while (tokenizer.hasMoreTokens()) {
                    token = tokenizer.nextToken();
                    feature = Integer.parseInt(token.split(":")[0]);
                    if (feature < (numberOfFeatures) && feature >= n) {
                        data[lineNumber][feature - n + 1] = '1';
                    }
                }
                line = reader.readLine();
                lineNumber++;
            }
            reader.close();
        }
        catch (IOException ex) {
            System.err.println(">!< Error reading the file and getting the data. >!<");
        }
        return data;
    }

    public String[] extractFeatureData(String filename, int numberOfFeatures, int n) {
        String[] data = new String[numberOfFeatures - n];
        int lineNumber = 0;
        int lineRead = 0;
        String line;
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(new File(filename)));
            line = reader.readLine();

            while (line != null) {
                // skip the first n lines
                if  (lineRead >= n) {
                    data[lineNumber] = line;
                    lineNumber++;
                }
                line = reader.readLine();
                lineRead++;
            }
            reader.close();
        }
        catch (IOException ex) {
            System.err.println(">!< Error reading the file and getting the feature data. >!<");
        }
        return data;
    }

    private char[][] initializeTable(int numberOfFeatures, int numberOfLines) {
        char[][] data = new char[numberOfLines][numberOfFeatures + 1];

        for (int i = 0; i < numberOfLines; i++) {
            for (int j = 1; j < numberOfFeatures + 1; j++) {
                data[i][j] = '0';
            }
        }
        return data;
    }

    // public static void main(String[] args){
    //     ReadFile read = new ReadFile();
    //     int sum = read.getNumberOfLines("testfeat.txt");
    //     int sum1 = read.getNumberOfLines("test.txt");
    //     System.out.println(sum);
    //     String[] data = read.extractFeatureData("testfeat.txt", sum);
    //     char[][] data1 = read.extractData("test.txt", sum, sum1);
    //     for (int i = 0; i < sum; i++) {
    //         System.out.println(data[i]);
    //     }

    //     for (int i = 0; i < sum1; i++) {
    //         for (int j = 0; j < sum + 1; j++) {
    //             System.out.print(data1[i][j] + " ");
    //         }
    //         System.out.println("");
    //     }
    //     System.out.println(data1[0].length);
    // }
}