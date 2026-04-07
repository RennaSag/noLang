import lexer.Lexer;
import lexer.Token;
import parser.Parser;
import parser.Expression;

import java.util.List;

public class Main {

    public static void main(String[] args) {


        String source = """

                func int somar(int n1, int n2){
                    return n1 + n2;
                }
     
                func void main() {
      
                    int y = 111;
                    int soma = somar(y, y);
                    print(soma);
                
                    string msg = "Teste de compilador\\n";
                
        
                }
                """;



        System.out.println("noLang Compilador Baseado - Fase 1: Lexer");
        System.out.println();

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenize();

        for (Token token : tokens) {
            System.out.println(token);
        }

        System.out.println();
        System.out.println("Total de tokens: " + tokens.size());

        Parser parser = new Parser(tokens);
        Expression expr = parser.parseExpression();

        System.out.println("Parse feito com sucesso!");
    }
}