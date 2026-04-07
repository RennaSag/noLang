package lexer;

public enum TokenType {

    INT_LITERAL,
    FLOAT_LITERAL,
    STRING_LITERAL,
    BOOL_LITERAL,

    IDENTIFIER,

    INT,
    FLOAT,
    BOOL,
    STRING,
    IF,
    ELSE,
    WHILE,
    RETURN,
    FUNC,
    VOID,
    PRINT,


    PLUS,
    MINUS,
    STAR,
    SLASH,
    PERCENT,


    EQ,
    NEQ,
    LT,
    LTE,
    GT,
    GTE,


    AND,
    OR,
    NOT,


    ASSIGN,


    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    COMMA,
    SEMICOLON,
    COLON,


    EOF,
    UNKNOWN
}
