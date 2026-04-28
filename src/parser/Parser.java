package parser;

import lexer.Token;
import lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int current = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }


    // ENTRY POINT PRINCIPAL
    // Lê o programa inteiro e retorna uma lista de statements
    // Ex: ["int x = 10;", "print(x);"]

    public List<Statement> parseProgram() {
        List<Statement> statements = new ArrayList<>();

        // Fica lendo statements até chegar no fim do arquivo
        while (!isAtEnd()) {
            statements.add(parseStatement());
        }

        return statements;
    }


    private Statement parseStatement() {

        // Se começa com int / float / bool / string é declaração de variável
        if (check(TokenType.BINCHMIN) || check(TokenType.FLOAT) ||
                check(TokenType.BOOL) || check(TokenType.STRING)) {
            return parseVarDeclaration();
        }

        // Se começa com print → é um print statement
        if (check(TokenType.PRINT)) {
            return parsePrint();
        }

        throw error("Statement inválido. Esperado declaração de variável ou print");
    }


    // DECLARAÇÃO DE VARIÁVEL
    // Formato: int x = 10;
    //          ^tipo ^nome ^valor

    private Statement parseVarDeclaration() {
        Token type = advance(); // consome o tipo (int, float, etc.)

        // Depois do tipo tem que vir o nome da variável
        Token name = consume(TokenType.IDENTIFIER, "Esperado nome da variável após '" + type.value + "'");

        // Depois do nome tem que vir o '='
        consume(TokenType.ASSIGN, "Esperado '=' após nome da variável '" + name.value + "'");

        // Depois do '=' vem a expressão do valor
        Expression value = parseExpression();

        // Todo statement termina com ';'
        consume(TokenType.SEMICOLON, "Esperado ';' após declaração de variável");

        return new VarDeclarationStatement(type, name, value);
    }


    private Statement parsePrint() {
        advance(); // consome o token PRINT

        consume(TokenType.PARENTELEFT, "Esperado '(' após print");
        Expression value = parseExpression(); // o que vai ser impresso
        consume(TokenType.PARENTERIGHT, "Esperado ')' após expressão do print");
        consume(TokenType.SEMICOLON, "Esperado ';' após print(...)");

        return new PrintStatement(value);
    }


    public Expression parseExpression() {
        return parseAddition();
    }

    private Expression parseAddition() {
        Expression expr = parseMultiplication();

        while (match(TokenType.MAES, TokenType.PAI)) {
            Token operator = previous();
            Expression right = parseMultiplication();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression parseMultiplication() {
        Expression expr = parsePrimary();

        while (match(TokenType.STAR, TokenType.SLASH, TokenType.PERCENT)) {
            Token operator = previous();
            Expression right = parsePrimary();
            expr = new BinaryExpression(expr, operator, right);
        }

        return expr;
    }

    private Expression parsePrimary() {

        if (match(TokenType.INT_LITERAL)) {
            return new LiteralExpression(Integer.parseInt(previous().value));
        }

        if (match(TokenType.FLOAT_LITERAL)) {
            return new LiteralExpression(Double.parseDouble(previous().value));
        }

        if (match(TokenType.STRING_LITERAL)) {
            return new LiteralExpression(previous().value);
        }

        if (match(TokenType.BOOL_LITERAL)) {
            // "true" vira true, qualquer outra coisa vira false
            return new LiteralExpression(previous().value.equals("true"));
        }

        if (match(TokenType.IDENTIFIER)) {
            Token name = previous();

            // Se depois do nome vier '(' é chamada de função: somar(1, 2)
            if (match(TokenType.PARENTELEFT)) {
                List<Expression> args = new ArrayList<>();

                if (!check(TokenType.PARENTERIGHT)) {
                    do {
                        args.add(parseExpression());
                    } while (match(TokenType.COMMA));
                }

                consume(TokenType.PARENTERIGHT, "Esperado ')' após argumentos");
                return new CallExpression(name, args);
            }

            return new VariableExpression(name);
        }

        if (match(TokenType.PARENTELEFT)) {
            Expression expr = parseExpression();
            consume(TokenType.PARENTERIGHT, "Esperado ')'");
            return expr;
        }

        throw error("Expressão inválida");
    }


    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
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

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(message);
    }

    private RuntimeException error(String message) {
        Token t = peek();
        return new RuntimeException("[linha " + t.line + ", col " + t.column + "] " + message);
    }
}