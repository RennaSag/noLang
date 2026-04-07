package parser;

import lexer.Token;

public class VariableExpression extends Expression {
    public final Token name;

    public VariableExpression(Token name) {
        this.name = name;
    }
}