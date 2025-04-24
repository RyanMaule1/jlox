package com.jlox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {

    public static boolean hadError = false;


    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
          System.out.println("Usage: jlox [script]");
          System.exit(64); 
        } else if (args.length == 1) {
          runFile(args[0]);
        } else {
          runPrompt();
        }
    }

    //this function reads the file as binary(more efficient then char by char) and converts that info to a string to then be lexed
    public static void runFile(String path) throws IOException {
        //read file as binary
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        String str = new String(bytes, Charset.defaultCharset()); //specifies ascii encodeing based on jvm
        run(str); //lexes our string
        if (hadError) System.exit(65);
    }

    //make an interactive repl for the user
    public static void runPrompt() throws IOException {
        //use inputStreamReader for binary 
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);


        while (true) {
            System.out.println("> ");
            String line = reader.readLine();

            //ctrl + D, will signal line to = null
            if (line == null) {                
                break;
            } 

            else {
                runFile(line);
                //reset hadError before it exits the repl
                hadError = false;
            }
        }
    }

    public static List<Token> run(String script) {

        //will take in our script as a string and have a an attribute with a list of tokens
        Scanner scanner = new Scanner(script);

        List<Token> tokens = scanner.scanTokens();

        // For now, just print the tokens.
        for (Token token : tokens) {
            System.out.println(token.lexum + ": " + token.type);
        }

        // for (int i = 0; i < script.length(); i++) {
        //     System.out.println(script.charAt(i));
        // }

        return tokens;
    }

    static void error(int line, String message) {
        report(line, "", message);
      }
    
    private static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error" + where + ": " + message);
        hadError = true;
    }
}
