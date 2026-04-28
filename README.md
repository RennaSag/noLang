# noLang

Uma linguagem de programação própria sendo construída do zero em Java.

---

## O que é
noLang é um compilador sendo desenvolvido passo a passo, começando pelo zero. O objetivo é entender como linguagens de programação funcionam por 
baixo dos panos: como um texto vira tokens, tokens viram uma árvore, e a árvore vira um programa executável.

---

## Status atual

| Fase | Descrição | Status |
|------|-----------|--------|
| Lexer | Quebra o código em tokens | Feito |
| Parser de expressões | Entende `1 + 2`, `somar(1, 2)` | Feito |
| Parser de statements | Entende `int x = 10;`, `print(x);` | Feito |
| `if` / `else` / `while` | Controle de fluxo | Em breve |
| Declaração de funções | `func somar(int a, int b) { }` | Em breve |
| Analisador semântico | Verificação de tipos e escopos | Em breve |
| Interpretador | Executa o código de verdade | Em breve |

---

## Como a linguagem vai parecer

```
func somar(int a, int b) {
    return a + b;
}

int x = 10;
int y = 20;
print(somar(x, y));
```

---

## Tipos suportados

- `int` — números inteiros
- `float` — números decimais
- `string` — texto
- `bool` — verdadeiro ou falso

---

## Estrutura do projeto

```
noLang/
├── lexer/
│   ├── Lexer.java               # Lê o código e gera tokens
│   ├── Token.java               # Representa um token
│   ├── TokenType.java           # Enum com todos os tipos de token
│   └── LexerException.java      # Erros léxicos
│
├── parser/
│   ├── Parser.java              # Lê tokens e monta a árvore
│   ├── Expression.java          # Base para expressões
│   ├── Statement.java           # Base para statements
│   ├── BinaryExpression.java    # ex: a + b
│   ├── LiteralExpression.java   # ex: 42, "texto"
│   ├── VariableExpression.java  # ex: x
│   ├── CallExpression.java      # ex: somar(1, 2)
│   ├── VarDeclarationStatement.java  # ex: int x = 10;
│   └── PrintStatement.java      # ex: print(x);
│
└── Main.java                    # Ponto de entrada
```

---

> Projeto em desenvolvimento ativo. Tudo pode mudar.
