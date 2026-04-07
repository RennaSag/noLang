package parser;

import lexer.Token;
import java.util.List;

public class CallExpression extends Expression {
    public final Token callee;
    public final List<Expression> arguments;

    public CallExpression(Token callee, List<Expression> arguments) {
        this.callee = callee;
        this.arguments = arguments;
    }
}