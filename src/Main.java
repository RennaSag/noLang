import ast.expressions.*;
import ast.statements.*;

import lexer.Lexer;
import lexer.LexerException;
import lexer.Token;
import parser.*;


import java.util.List;

public class Main {

    public static void main(String[] args) {

        String source = """
                func somar(int a, int b) {
                    return a + b;
                }
                
                int x = 10;
                int y = 20;
                
                if (x > 5) {
                    print(somar(x, y));
                } else {
                    print(0);
                }
                
                while (x > 0) {
                    print(x);
                }
                """;

        System.out.println("=== noLang Compilador ===");
        System.out.println();

        // ── FASE 1: LEXER ──
        List<Token> tokens;
        try {
            Lexer lexer = new Lexer(source);
            tokens = lexer.tokenize();
            System.out.println("Lexer: OK — " + tokens.size() + " tokens");
        } catch (LexerException e) {
            System.out.println("ERRO no Lexer: " + e.getMessage());
            return;
        }

        System.out.println();

        // ── FASE 2: PARSER ──
        Parser parser = new Parser(tokens);
        List<Statement> statements = parser.parseProgram();

        if (parser.hasErrors()) {
            System.out.println("=== ERROS ENCONTRADOS ===");
            for (ParserException e : parser.getErrors()) {
                System.out.println(e.getMessage());
            }
            System.out.println();
        }

        System.out.println("--- Statements parseados ---");
        for (Statement stmt : statements) {
            printStatement(stmt, 0);
        }

        if (!parser.hasErrors()) {
            System.out.println();
            System.out.println("Parse feito com sucesso!");
        }
    }

    static void printStatement(Statement stmt, int indent) {
        String pad = "  ".repeat(indent);

        if (stmt instanceof VarDeclarationStatement v) {
            System.out.println(pad + "VarDecl: " + v.type.value + " " + v.name.value + " = " + describeExpr(v.value));
        } else if (stmt instanceof PrintStatement p) {
            System.out.println(pad + "Print: " + describeExpr(p.value));
        } else if (stmt instanceof ReturnStatement r) {
            String val = r.value != null ? describeExpr(r.value) : "void";
            System.out.println(pad + "Return: " + val);
        } else if (stmt instanceof IfStatement i) {
            System.out.println(pad + "If: " + describeExpr(i.condition));
            System.out.println(pad + "  Then:");
            for (Statement s : i.thenBranch) printStatement(s, indent + 2);
            if (i.elseBranch != null) {
                System.out.println(pad + "  Else:");
                for (Statement s : i.elseBranch) printStatement(s, indent + 2);
            }
        } else if (stmt instanceof WhileStatement w) {
            System.out.println(pad + "While: " + describeExpr(w.condition));
            for (Statement s : w.body) printStatement(s, indent + 1);
        } else if (stmt instanceof FunctionDeclaration f) {
            String params = f.params.stream()
                    .map(p -> p.type().value + " " + p.name().value)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            System.out.println(pad + "Func: " + f.name.value + "(" + params + ")");
            for (Statement s : f.body) printStatement(s, indent + 1);
        }
    }

    static String describeExpr(Expression expr) {
        if (expr instanceof LiteralExpression l)  return String.valueOf(l.value);
        if (expr instanceof VariableExpression v) return v.name.value;
        if (expr instanceof BinaryExpression b)   return describeExpr(b.left) + " " + b.operator.value + " " + describeExpr(b.right);
        if (expr instanceof UnaryExpression u)    return u.operator.value + describeExpr(u.operand);
        if (expr instanceof CallExpression c) {
            String args = c.arguments.stream()
                    .map(Main::describeExpr)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            return c.callee.value + "(" + args + ")";
        }
        return "?";
    }
}