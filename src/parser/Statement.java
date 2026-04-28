package parser;

// Assim como Expression é a base pra expressões (1+2, somar()),
// Statement é a base pra instruções completas (int x = 10; / print(x);)
// É abstrata porque nunca vamos criar um "Statement genérico" —
// sempre vai ser um tipo específico (VarDeclaration, Print, etc.)

public abstract class Statement {
}