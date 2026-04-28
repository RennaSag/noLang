package ast.expressions;

import lexer.Token;

// Representa uma operação com um único operando, por exemplo:
//   !verdadeiro
//   !ativo
//
// - operator: o token do operador (!)
// - operand:  a expressão que vai ser negada
public class UnaryExpression extends Expression {

    public final Token operator;
    public final Expression operand;

    public UnaryExpression(Token operator, Expression operand) {
        this.operator = operator;
        this.operand = operand;
    }
}