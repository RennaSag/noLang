package parser;

import ast.expressions.*;
import ast.statements.*;
import lexer.Token;
import lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<Token> tokens;
    private int current = 0;

    // Lista de erros encontrados — assim o parser não trava no primeiro erro,
    // ele coleta todos e mostra de uma vez no final
    private final List<ParserException> errors = new ArrayList<>();

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<ParserException> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }


    public List<Statement> parseProgram() {
        List<Statement> statements = new ArrayList<>();

        while (!isAtEnd()) {
            Statement stmt = parseSafe();
            if (stmt != null) statements.add(stmt);
        }

        return statements;
    }

    // Tenta parsear um statement com segurança.
    // Se der erro: salva o erro, avança até o próximo ponto seguro e continua.
    private Statement parseSafe() {
        try {
            return parseStatement();
        } catch (ParserException e) {
            errors.add(e);
            synchronize();
            return null;
        }
    }

    // ─────────────────────────────────────────────
    // SINCRONIZAÇÃO — recuperação de erros
    // Quando acha um erro, avança tokens até um ponto seguro
    // pra poder continuar parseando o resto do programa
    // ─────────────────────────────────────────────
    private void synchronize() {
        while (!isAtEnd()) {
            if (peek().type == TokenType.SEMICOLON) {
                advance();
                return;
            }
            switch (peek().type) {
                case FUNC, BINCHMIN, FLOAT, BOOL, STRING, IF, WHILE, RETURN, PRINT -> {
                    return;
                }
                default -> advance();
            }
        }
    }

    // ─────────────────────────────────────────────
    // STATEMENTS
    // ─────────────────────────────────────────────
    private Statement parseStatement() {

        // Declaração de variável: int x = 10;
        if (check(TokenType.BINCHMIN) || check(TokenType.FLOAT) ||
                check(TokenType.BOOL)     || check(TokenType.STRING)) {
            return parseVarDeclaration();
        }

        // print(x);
        if (check(TokenType.PRINT)) {
            return parsePrint();
        }

        // if (cond) { } else { }
        if (check(TokenType.IF)) {
            return parseIf();
        }

        // while (cond) { }
        if (check(TokenType.WHILE)) {
            return parseWhile();
        }

        // return expr;
        if (check(TokenType.RETURN)) {
            return parseReturn();
        }

        // func nome(params) { }
        if (check(TokenType.FUNC)) {
            return parseFunctionDeclaration();
        }

        throw error("Statement inválido. Token inesperado: '" + peek().value + "'");
    }

    // ─────────────────────────────────────────────
    // DECLARAÇÃO DE VARIÁVEL
    // int x = 10;
    // ─────────────────────────────────────────────
    private Statement parseVarDeclaration() {
        Token type = advance();
        Token name = consume(TokenType.IDENTIFIER,
                "Esperado nome da variável após '" + type.value + "'");
        consume(TokenType.ASSIGN,
                "Esperado '=' após nome da variável '" + name.value + "'");
        Expression value = parseExpression();
        consume(TokenType.SEMICOLON,
                "Esperado ';' após declaração de '" + name.value + "'");

        return new VarDeclarationStatement(type, name, value);
    }

    // ─────────────────────────────────────────────
    // PRINT
    // print(x);
    // ─────────────────────────────────────────────
    private Statement parsePrint() {
        advance(); // consome PRINT
        consume(TokenType.PARENTELEFT, "Esperado '(' após 'print'");
        Expression value = parseExpression();
        consume(TokenType.PARENTERIGHT, "Esperado ')' após expressão do print");
        consume(TokenType.SEMICOLON, "Esperado ';' após print(...)");

        return new PrintStatement(value);
    }

    // ─────────────────────────────────────────────
    // IF / ELSE
    // if (x > 5) { ... } else { ... }
    // ─────────────────────────────────────────────
    private Statement parseIf() {
        advance(); // consome IF

        consume(TokenType.PARENTELEFT, "Esperado '(' após 'if'");
        Expression condition = parseExpression();
        consume(TokenType.PARENTERIGHT, "Esperado ')' após condição do if");

        List<Statement> thenBranch = parseBlock();

        // else é opcional
        List<Statement> elseBranch = null;
        if (check(TokenType.ELSE)) {
            advance(); // consome ELSE
            elseBranch = parseBlock();
        }

        return new IfStatement(condition, thenBranch, elseBranch);
    }

    // ─────────────────────────────────────────────
    // WHILE
    // while (x > 0) { ... }
    // ─────────────────────────────────────────────
    private Statement parseWhile() {
        advance(); // consome WHILE

        consume(TokenType.PARENTELEFT, "Esperado '(' após 'while'");
        Expression condition = parseExpression();
        consume(TokenType.PARENTERIGHT, "Esperado ')' após condição do while");

        List<Statement> body = parseBlock();

        return new WhileStatement(condition, body);
    }

    // ─────────────────────────────────────────────
    // RETURN
    // return a + b;
    // return;  (void)
    // ─────────────────────────────────────────────
    private Statement parseReturn() {
        advance(); // consome RETURN

        Expression value = null;
        if (!check(TokenType.SEMICOLON)) {
            value = parseExpression();
        }

        consume(TokenType.SEMICOLON, "Esperado ';' após return");

        return new ReturnStatement(value);
    }

    // ─────────────────────────────────────────────
    // DECLARAÇÃO DE FUNÇÃO
    // func somar(int a, int b) { return a + b; }
    // ─────────────────────────────────────────────
    private Statement parseFunctionDeclaration() {
        advance(); // consome FUNC

        Token name = consume(TokenType.IDENTIFIER, "Esperado nome da função após 'func'");

        consume(TokenType.PARENTELEFT, "Esperado '(' após nome da função '" + name.value + "'");

        // Parâmetros: int a, int b, float c ...
        List<FunctionDeclaration.Param> params = new ArrayList<>();
        if (!check(TokenType.PARENTERIGHT)) {
            do {
                Token paramType = advance();
                Token paramName = consume(TokenType.IDENTIFIER,
                        "Esperado nome do parâmetro após '" + paramType.value + "'");
                params.add(new FunctionDeclaration.Param(paramType, paramName));
            } while (match(TokenType.COMMA));
        }

        consume(TokenType.PARENTERIGHT, "Esperado ')' após parâmetros da função");

        List<Statement> body = parseBlock();

        return new FunctionDeclaration(name, params, body);
    }

    // ─────────────────────────────────────────────
    // BLOCO { ... }
    // Usado por if, while e func
    // ─────────────────────────────────────────────
    private List<Statement> parseBlock() {
        consume(TokenType.LBRACE, "Esperado '{' para abrir bloco");

        List<Statement> statements = new ArrayList<>();
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            Statement stmt = parseSafe();
            if (stmt != null) statements.add(stmt);
        }

        consume(TokenType.RBRACE, "Esperado '}' para fechar bloco");

        return statements;
    }

    // ─────────────────────────────────────────────
    // EXPRESSÕES
    // Ordem de precedência (menor → maior):
    //   or → and → equality → comparison → addition → multiplication → unary → primary
    // ─────────────────────────────────────────────
    public Expression parseExpression() {
        return parseOr();
    }

    // ||
    private Expression parseOr() {
        Expression expr = parseAnd();
        while (match(TokenType.OR)) {
            Token op = previous();
            expr = new BinaryExpression(expr, op, parseAnd());
        }
        return expr;
    }

    // &&
    private Expression parseAnd() {
        Expression expr = parseEquality();
        while (match(TokenType.AND)) {
            Token op = previous();
            expr = new BinaryExpression(expr, op, parseEquality());
        }
        return expr;
    }

    // == e !=
    private Expression parseEquality() {
        Expression expr = parseComparison();
        while (match(TokenType.EQ, TokenType.NEQ)) {
            Token op = previous();
            expr = new BinaryExpression(expr, op, parseComparison());
        }
        return expr;
    }

    // <, <=, >, >=
    private Expression parseComparison() {
        Expression expr = parseAddition();
        while (match(TokenType.LT, TokenType.LTE, TokenType.GT, TokenType.GTE)) {
            Token op = previous();
            expr = new BinaryExpression(expr, op, parseAddition());
        }
        return expr;
    }

    // + e -
    private Expression parseAddition() {
        Expression expr = parseMultiplication();
        while (match(TokenType.MAES, TokenType.PAI)) {
            Token op = previous();
            expr = new BinaryExpression(expr, op, parseMultiplication());
        }
        return expr;
    }

    // *, / e %
    private Expression parseMultiplication() {
        Expression expr = parseUnary();
        while (match(TokenType.STAR, TokenType.SLASH, TokenType.PERCENT)) {
            Token op = previous();
            expr = new BinaryExpression(expr, op, parseUnary());
        }
        return expr;
    }

    // ! (negação)
    private Expression parseUnary() {
        if (match(TokenType.NOT)) {
            Token op = previous();
            return new UnaryExpression(op, parseUnary());
        }
        return parsePrimary();
    }

    // Valores base
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
            return new LiteralExpression(previous().value.equals("true"));
        }
        if (match(TokenType.IDENTIFIER)) {
            Token name = previous();

            // Chamada de função: somar(1, 2)
            if (match(TokenType.PARENTELEFT)) {
                List<Expression> args = new ArrayList<>();
                if (!check(TokenType.PARENTERIGHT)) {
                    do {
                        args.add(parseExpression());
                    } while (match(TokenType.COMMA));
                }
                consume(TokenType.PARENTERIGHT,
                        "Esperado ')' após argumentos de '" + name.value + "'");
                return new CallExpression(name, args);
            }

            return new VariableExpression(name);
        }

        // Expressão entre parênteses: (1 + 2)
        if (match(TokenType.PARENTELEFT)) {
            Expression expr = parseExpression();
            consume(TokenType.PARENTERIGHT, "Esperado ')' para fechar expressão");
            return expr;
        }

        throw error("Expressão inválida. Token inesperado: '" + peek().value + "'");
    }

    // ─────────────────────────────────────────────
    // UTILITÁRIOS
    // ─────────────────────────────────────────────
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

    private ParserException error(String message) {
        Token t = peek();
        return new ParserException(message, t.line, t.column);
    }
}