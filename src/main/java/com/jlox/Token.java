package com.jlox;

public class Token {
    final TokenType type; //see TokenType class
    final String lexum; //raw string data from source code
    final Object literal; //raw values for integers or strings, could be of any type
    final int line; //used for error handling


    Token (TokenType type, String lexum, Object literal, int line) {
        this.type = type;
        this.lexum = lexum;
        this.literal = literal;
        this.line = line;
    }

    public String toString() {
        return type + " " + lexum + " " + literal;
    }
}
