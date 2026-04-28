package parser;

// Erro customizado do parser, igual ao LexerException do lexer.
// Guarda a mensagem + linha e coluna onde o erro aconteceu.

public class ParserException extends RuntimeException {
    public final int line;
    public final int column;

    public ParserException(String message, int line, int column) {
        super("[linha " + line + ", col " + column + "] ERRO: " + message);
        this.line = line;
        this.column = column;
    }
}