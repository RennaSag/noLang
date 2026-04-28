package ast.statements;

import ast.expressions.Expression;

import java.util.List;

// Representa um while, por exemplo:
//
//   while (x > 0) {
//       print(x);
//   }
//
// - condition: a expressão de controle (x > 0)
// - body:      os statements dentro do bloco { ... }
public class WhileStatement extends Statement {

    public final Expression condition;
    public final List<Statement> body;

    public WhileStatement(Expression condition, List<Statement> body) {
        this.condition = condition;
        this.body = body;
    }
}