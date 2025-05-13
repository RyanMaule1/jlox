package com.jlox;
import java.util.List;

import static com.jlox.TokenType.*;

//This class handles the grammer rules, each rule becomes a method
public class Parser {
    
    //Just basically renaming runtimeexception for error handling
    private static class ParseError extends RuntimeException{}


    //list of tokens to be parsed
    private final List<Token> tokens;
    private int current = 0;


    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    //helper functions
    private boolean match(TokenType...args) {

        for (TokenType arg : args) {
            if (check(arg)) {
                advance();
                return true;
            }

        }
        return false;
    }   

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token consume(TokenType expectedType, String msg) {
        if (check(expectedType)) return advance();
        throw error(peek(), msg);
    }

    private ParseError error(Token token, String msg) {
        Lox.error(token, msg);
        return new ParseError();
    }

    private void synchronize() {
        advance();

        //discard until we reach a semi colon followed by a starter keyword
        while (!isAtEnd()) {

            if (previous().type == SEMICOLON) return;
            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            advance();
        }   

        
    }


    //highest precedence, handles literals 
    private Expr primary() {
       if (match(TRUE)) return new Expr.Literal(true);
       if (match(FALSE)) return new Expr.Literal(false);
       if (match(NIL)) return new Expr.Literal(null);

       if (match(STRING, NUMBER)) {
            return new Expr.Literal(previous().literal);
       }

       if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Right ) expected");
            return new Expr.Grouping(expr);
       }

       throw error(peek(), "Expression Expected");
    }

    //unary  → ( "!" | "-" ) unary | primary ;
    private Expr unary() {
        
        if (match(BANG, MINUS)) {
           Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    private Expr factor() {
        Expr expr = unary();

        while (match(SLASH, STAR)) {
            Token operator = previous();
            Expr right = unary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr term() {
        Expr expr = factor();

        while (match(PLUS, MINUS)) {
            Token operator = previous();
            Expr right = factor();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    //comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    private Expr comparison() {
        Expr expr = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {

            Token operator = previous();
            Expr right = term();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }


    //equality       → comparison ( ( "!=" | "==" ) comparison )* ;
    private Expr equality() {

       Expr expr = comparison();
       

       while(match(EQUAL_EQUAL, BANG_EQUAL)) {

            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
       }

       return expr;

    }

    //comma -> 
    private Expr comma() {

    }

    //Expression simply evaluates to equality
    private Expr expression() {
       return equality();
    }


    //starts off this whole process
    public Expr parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }
}
