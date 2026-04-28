package ast.statements;

import ast.expressions.Expression;
import lexer.Token;

// Representa uma declaração de variável, por exemplo:
//   int x = 10;
//   float y = 3.14;
//   string nome = "João";
//
// Guarda 3 coisas:
//   - type:  o token do tipo (INT, FLOAT, STRING, BOOL)
//   - name:  o token com o nome da variável (x, y, nome)
//   - value: a expressão do lado direito do = (10, 3.14, "João")

public class VarDeclarationStatement extends Statement {

    public final Token type;   // ex: token INT
    public final Token name;   // ex: token "x"
    public final Expression value; // ex: LiteralExpression(10)

    public VarDeclarationStatement(Token type, Token name, Expression value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }
}