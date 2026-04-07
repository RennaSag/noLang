package lexer;

public enum TokenType {

    INT_LITERAL,
    FLOAT_LITERAL,
    STRING_LITERAL,
    BOOL_LITERAL,

    IDENTIFIER,

    BINCHMIN, // isso daqui é pra ser um int :D
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

    MÃES, // +
    PAI, // -
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

    PARENTELEFT, //parenteses
    PARENTERIGHT,
    LBRACE,
    RBRACE,
    COMMA,
    SEMICOLON,
    COLON,

    EOF,
    UNKNOWN
}
