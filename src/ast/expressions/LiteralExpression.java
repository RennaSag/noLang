package ast.expressions;

public class LiteralExpression extends Expression {
    public final Object value;

    public LiteralExpression(Object value) {
        this.value = value;
    }
}