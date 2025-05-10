package com.jlox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jlox.TokenType.*;


public class Scanner {

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }

    private int start = 0;
    private int current = 0;
    private int line = 1;

    final String script;
    List<Token> tokens = new ArrayList<>();

    

    Scanner(String script) {
        this.script = script;
    }

    private boolean isAtEnd() {
        return current >= script.length();
    }

    private char advance() {
        return script.charAt(current++);
        
    }

    //single char tokens dont need a literal param
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String lexem = script.substring(start, current);
        tokens.add(new Token(type, lexem, literal, line));
    }

    //for multi-char types(logical operators)
    private boolean match(char c) {
        if (isAtEnd() || peek() != c) {
            return false;
        } 
        
        advance();
        return true;
        
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return script.charAt(current);
    }

    private void string() {
        while (peek() != '"') {
            if (peek() == '\n') {
                line++;
            }

            if (isAtEnd()) {
                Lox.error(line, "Non-terminated String");
                return;
            }

            advance();
        }

        advance(); //advance once more for the terminating "

        //trim off the ""
        addToken(STRING, script.substring(start + 1, current - 1));
    }

    private boolean isDigit(char c) {
        if (c >= '0' && c <= '9') {
            return true;
        }

        return false;
    }

    private char peekAhead() {
        if (current + 1 >= script.length()) return '\0';
        return script.charAt(current + 1);
    }

    private void number() {
        while (isDigit(peek())) {
            advance();
        }

        //check for decimals
        if (peek() == '.' && isDigit(peekAhead())) {
            //consume the .
            advance();

            //consume the nums following the .
            while (isDigit(peek())) advance();
        }

        addToken(NUMBER, Double.parseDouble(script.substring(start, current)));
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c == '_');
    }

    private boolean isAlphaNumeric(char c) {
        
        return isAlpha(c) || isDigit(c);
        
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        addToken(IDENTIFIER, script.substring(start, current));

    }

    private void keyWord() {
        //if identifier matches a keyword, remove that token and replace it with a keyword token
        if (keywords.containsKey(tokens.getLast().lexum)) {

          Token unaddedToken = tokens.removeLast();
          addToken(keywords.get(unaddedToken.lexum) , unaddedToken.lexum);
        }
        
    }

    public void scanToken() {
        char c = advance();
        //all are chars that dont need to check fro the next char
        switch (c) {
            
            case ' ': 
            case '\r': 
            case '\t': break;
            case '\n': line++; break;
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break; 
            case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
            case '/': if (match('/')) {
                //handles single line comments
                while (peek() != '\n' && !isAtEnd()) advance();
            } else if (match('*')) {
                int errorLine = line; // this line track the openeing comment line for error tracking


                //handles block comments, nested doesnt work 
                while (peek() != '\0') {
                    if (advance() == '\n') {
                        line++;
                    }

                    if (peek() == '*' && peekAhead() == '/') {
                        break;
                    }
                }
                
                if (peek() == '\0') {
                    Lox.error(errorLine, "Non-terminated Comment");
                    return;
                }

                //advance twice to consume terminating comments
                advance();
                advance();
            } else {
                addToken(SLASH);
            } break;
            case '"': string(); break;
            default : 
                //hanlde number literals in default so we dont need a seperate case for each num
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                    keyWord();
                } else {
                    Lox.error(line, "Unexpected character."); break;
                }
                
        }

        //could make a method called checkNextChar that takes in the token type and then checks the next char 
        //and calls addToken from there 
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
            //maybe create a buffer that holdes each char until we run into a space, then check if its a valid lexum
            

        }
        //mark end of file after we get to the end
        addToken(EOF);
        return tokens;
    }

    public String getScript() {
        return script;
    }

    public List<Token> getTokens() {
        // for (int i = 0; i < script.length(); i++) {
        //     char c = script.charAt(i);
        //     //Token token = new Token(null, script, script, i)
        //     System.out.println(c);
        // }
        return tokens;
    }

}
