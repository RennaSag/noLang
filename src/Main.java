import lexer.Lexer;
import lexer.Token;
import parser.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        // Testando declaração de variável + print
        String source = """
                int x = 10;
                float y = 3.14;
                string nome = "João";
                print(x);
                print(nome);
                """;

        System.out.println("=== noLang Compilador ===");
        System.out.println();

        // ── FASE 1: LEXER ──
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        System.out.println("--- Tokens ---");
        for (Token token : tokens) {
            System.out.println(token);
        }

        System.out.println();
        System.out.println("Total de tokens: " + tokens.size());
        System.out.println();

        // ── FASE 2: PARSER ──
        Parser parser = new Parser(tokens);
        List<Statement> statements = parser.parseProgram();

        System.out.println("--- Statements parseados ---");
        for (Statement stmt : statements) {

            // VarDeclarationStatement → mostra tipo, nome e valor
            if (stmt instanceof VarDeclarationStatement v) {
                System.out.println("VarDecl: " + v.type.value + " " + v.name.value + " = " + describeExpr(v.value));
            }

            // PrintStatement → mostra o que vai ser impresso
            else if (stmt instanceof PrintStatement p) {
                System.out.println("Print: " + describeExpr(p.value));
            }
        }

        System.out.println();
        System.out.println("Parse feito com sucesso!");
    }

    // Helper simples pra descrever uma expressão em texto
    static String describeExpr(parser.Expression expr) {
        if (expr instanceof LiteralExpression l) {
            return String.valueOf(l.value);
        }
        if (expr instanceof VariableExpression v) {
            return v.name.value;
        }
        if (expr instanceof BinaryExpression b) {
            return describeExpr(b.left) + " " + b.operator.value + " " + describeExpr(b.right);
        }
        if (expr instanceof CallExpression c) {
            return c.callee.value + "(...)";
        }
        return "?";
    }
}