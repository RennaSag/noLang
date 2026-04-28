package ast.statements;

import ast.expressions.Expression;

// Representa um return, por exemplo:
//
//   return a + b;
//   return 0;
//
// - value: a expressão que vai ser retornada
//          pode ser null no caso de funções void: return;
public class ReturnStatement extends Statement {

    public final Expression value; // null se for só "return;"

    public ReturnStatement(Expression value) {
        this.value = value;
    }
}
