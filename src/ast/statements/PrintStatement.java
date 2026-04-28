package ast.statements;

// Representa um print, por exemplo:
//   print(x);
//   print(1 + 2);
//   print("oi");
//
// Guarda só 1 coisa:
//   - value: a expressão que vai ser impressa


import ast.expressions.Expression;

public class PrintStatement extends Statement {

    public final Expression value; // o que vai ser impresso

    public PrintStatement(Expression value) {
        this.value = value;
    }
}