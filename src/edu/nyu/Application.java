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
        for(int i = 0; i < args.length; i++) {
            TranscationManager tm = new TranscationManager();
            String FileName = args[i];
            String transcationInput = readFile(FileName);
            tm.runTest(transcationInput);
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

