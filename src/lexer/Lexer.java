package lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {


    private static final Map<String, TokenType> KEYWORDS = new HashMap<>();

    static {
        KEYWORDS.put("int", TokenType.BINCHMIN);
        KEYWORDS.put("float", TokenType.FLOAT);
        KEYWORDS.put("bool", TokenType.BOOL);
        KEYWORDS.put("string", TokenType.STRING);
        KEYWORDS.put("if", TokenType.IF);
        KEYWORDS.put("else", TokenType.ELSE);
        KEYWORDS.put("while", TokenType.WHILE);
        KEYWORDS.put("return", TokenType.RETURN);
        KEYWORDS.put("func", TokenType.FUNC);
        KEYWORDS.put("void", TokenType.VOID);
        KEYWORDS.put("print", TokenType.PRINT);
        KEYWORDS.put("true", TokenType.BOOL_LITERAL);
        KEYWORDS.put("false", TokenType.BOOL_LITERAL);
    }

    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private int line = 1;
    private int lineStart = 0;

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> tokenize() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", line, column()));
        return tokens;
    }

    private void scanToken() {
        char c = advance();

        switch (c) {
            case '(' -> addToken(TokenType.PARENTELEFT);
            case ')' -> addToken(TokenType.PARENTERIGHT);
            case '{' -> addToken(TokenType.LBRACE);
            case '}' -> addToken(TokenType.RBRACE);
            case ',' -> addToken(TokenType.COMMA);
            case ';' -> addToken(TokenType.SEMICOLON);
            case ':' -> addToken(TokenType.COLON);

            case '+' -> addToken(TokenType.MAES);
            case '-' -> addToken(TokenType.PAI);
            case '*' -> addToken(TokenType.STAR);
            case '%' -> addToken(TokenType.PERCENT);

            case '/' -> {
                if (match('/')) {

                    while (peek() != '\n' && !isAtEnd()) advance();
                } else if (match('*')) {

                    blockComment();
                } else {
                    addToken(TokenType.SLASH);
                }
            }

            case '!' -> addToken(match('=') ? TokenType.NEQ : TokenType.NOT);
            case '=' -> addToken(match('=') ? TokenType.EQ : TokenType.ASSIGN);
            case '<' -> addToken(match('=') ? TokenType.LTE : TokenType.LT);
            case '>' -> addToken(match('=') ? TokenType.GTE : TokenType.GT);

            case '&' -> {
                if (match('&')) addToken(TokenType.AND);
                else addToken(TokenType.UNKNOWN);
            }
            case '|' -> {
                if (match('|')) addToken(TokenType.OR);
                else addToken(TokenType.UNKNOWN);
            }

            case '"' -> string();

            case ' ', '\r', '\t' -> { /* whitespace */ }
            case '\n' -> {
                line++;
                lineStart = current;
            }

            default -> {
                if (isDigit(c)) number();
                else if (isAlpha(c)) identifier();
                else addToken(TokenType.UNKNOWN);
            }
        }
    }


    private void number() {
        while (isDigit(peek())) advance();


        if (peek() == '.' && isDigit(peekNext())) {
            advance();
            while (isDigit(peek())) advance();
            addToken(TokenType.FLOAT_LITERAL);
        } else {
            addToken(TokenType.INT_LITERAL);
        }
    }


    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = KEYWORDS.getOrDefault(text, TokenType.IDENTIFIER);
        addToken(type);
    }


    private void string() {
        StringBuilder sb = new StringBuilder();

        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
                lineStart = current;
            }

            if (peek() == '\\') {
                advance();
                char esc = advance();
                switch (esc) {
                    case 'n' -> sb.append('\n');
                    case 't' -> sb.append('\t');
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    default -> sb.append(esc);
                }
            } else {
                sb.append(advance());
            }
        }

        if (isAtEnd()) {
            throw new LexerException("String não fechada na linha " + line);
        }
        advance();


        tokens.add(new Token(TokenType.STRING_LITERAL, sb.toString(), line, column()));
        return;
    }


    private void blockComment() {
        while (!isAtEnd()) {
            if (peek() == '\n') {
                line++;
                lineStart = current;
            }
            if (peek() == '*' && peekNext() == '/') {
                advance();
                advance();
                return;
            }
            advance();
        }
        throw new LexerException("Comentário de bloco não fechado (faltou */)");
    }


    private char advance() {
        return source.charAt(current++);
    }


    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }


    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }


    private boolean match(char expected) {
        if (isAtEnd() || source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private int column() {
        return start - lineStart + 1;
    }


    private void addToken(TokenType type) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, line, column()));
    }
}