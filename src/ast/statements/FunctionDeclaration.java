package ast.statements;

import lexer.Token;

import java.util.List;

// Representa a declaração de uma função, por exemplo:
//
//   func somar(int a, int b) {
//       return a + b;
//   }
//
// - name:       o nome da função (somar)
// - params:     lista de parâmetros — cada parâmetro é um par (tipo, nome)
// - body:       os statements dentro do bloco { ... }
//
// Usamos um record simples pra representar cada parâmetro:
//   Param(Token type, Token name)
//   ex: type="int", name="a"
public class FunctionDeclaration extends Statement {

    // Record é uma forma compacta de criar uma classe só pra guardar dados
    // equivale a uma classe com tipo e nome, construtor, getters — tudo automático
    public record Param(Token type, Token name) {}

    public final Token name;
    public final List<Param> params;
    public final List<Statement> body;

    public FunctionDeclaration(Token name, List<Param> params, List<Statement> body) {
        this.name = name;
        this.params = params;
        this.body = body;
    }
}