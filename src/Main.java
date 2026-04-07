import lexer.Lexer;
import lexer.Token;

import java.util.List;

public class Main {

    public static void main(String[] args) {


        String source = """
                func int fatorial(int n) {
                    if (n <= 1) {
                        return 1;
                    }
                    return n * fatorial(n - 1);
                }
                
                func void main() {
                    int x = 5;
                    int resultado = fatorial(x);
                    print(resultado);
                
                
                    float pi = 3.14;
                    bool ok = true;
                    string msg = "Olá mundo\\n";
                
                    while (x > 0) {
                        x = x - 1;
                    }
                }
                """;

        System.out.println("═══════════════════════════════════════════════");
        System.out.println("         noLang Compiler - Fase 1: Lexer     ");
        System.out.println("═══════════════════════════════════════════════");
        System.out.println();

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        for (Token token : tokens) {
            System.out.println(token);
        }

        System.out.println();
        System.out.println("Total de tokens: " + tokens.size());
    }
}