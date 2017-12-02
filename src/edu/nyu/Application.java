package edu.nyu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Application class contains main class which reads input from commandline args
 * and passes args to the transaction manager.
 */
public class Application {
    public static void main(String[] args) {
        for(int i = 18; i <= 19; i++) {
            TransactionManager tm = new TransactionManager();
            String FileName = args[i-1];
            String transactionInput = readFile(FileName);
            System.out.print("\nTest " + i);
            tm.runTest(transactionInput);
        }
    }
    public static String readFile(String fileName){
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                sb.append(line + "\n");
            }
            bufferedReader.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        String input  = sb.toString().trim();
        return input;
    }
}

