package ast.statements;

import ast.expressions.Expression;

import java.util.List;

// Representa um if/else, por exemplo:
//
//   if (x > 5) {
//       print(x);
//   } else {
//       print(0);
//   }
//
// - condition:  a expressão dentro do if (x > 5)
// - thenBranch: os statements dentro do bloco if  { ... }
// - elseBranch: os statements dentro do bloco else { ... } — pode ser null se não tiver else
public class IfStatement extends Statement {

    public final Expression condition;
    public final List<Statement> thenBranch;
    public final List<Statement> elseBranch; // null se não tiver else

    public IfStatement(Expression condition, List<Statement> thenBranch, List<Statement> elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
}