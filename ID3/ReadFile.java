//package ID3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

public class ReadFile {
    
    private String fileName;
    private int numOfLines = 0;
    private char[][] data;

    public ReadFile(String filename) {
        this.fileName = filename;
    }

    public int getNumberOfLines() {
        String line;
        BufferedReader reader = null;
        int fileLines = 0;
        try {
            reader = new BufferedReader(new FileReader(new File(this.fileName)));
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

    public char[][] getData() {
        char[][] data = new char[this.numOfLines][];
        int lineNumber = 0;
        String line;
        BufferedReader reader = null;
        StringTokenizer tokenizer;
        String token;
        int rate;
        int feature;

        try {
            reader = new BufferedReader(new FileReader(new File(this.fileName)));
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
                    data[lineNumber][feature] = '1';
                }
                lineNumber++;
            }
        }
        catch (IOException ex) {
            System.err.println(">!< Error reading the file and getting the data. >!<");
        }
        return data;
    }

    public static void main(String[] args){
        ReadFile read = new ReadFile("test.txt");
        System.out.println(read.getNumberOfLines());
    }
}
