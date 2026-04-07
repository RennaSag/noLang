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

        if (match(TokenType.IDENTIFIER)) {
            Token name = previous();

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
        return new RuntimeException(message + " em: " + peek());
    }
}