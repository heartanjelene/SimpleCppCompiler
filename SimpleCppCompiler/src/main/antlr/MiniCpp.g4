grammar MiniCpp;

// lexer and parser rules. ANTLR 4 = Flex/Bison sa Java.

@header { package com.simplecpp.compiler.grammar; }

// ---------- Grammar for our tiny C++ subset ----------
program  : stmt* EOF ;
stmt     : declStmt | assignStmt | ioStmt ;

declStmt   : INT_KW ID (ASSIGN expr)? SEMI ;
assignStmt : ID ASSIGN expr SEMI ;

ioStmt   : cinStmt | coutStmt ;
cinStmt  : CIN (SHIFT_IN ID)+ SEMI ;
coutStmt : COUT (SHIFT_OUT expr)+ SEMI ;

expr     : addExpr ;
addExpr  : unary (PLUS unary)* ;
unary    : MINUS unary | primary ;
primary  : INT_LIT | STRING_LIT | ID | LPAREN expr RPAREN ;

// ---------- Lexer rules ----------
CIN      : 'cin' ;
COUT     : 'cout' ;
INT_KW   : 'int' ;
SHIFT_IN  : '>>' ;
SHIFT_OUT : '<<' ;
PLUS      : '+' ;
MINUS     : '-' ;
ASSIGN    : '=' ;
SEMI      : ';' ;
LPAREN    : '(' ;
RPAREN    : ')' ;
ID       : [a-zA-Z_][a-zA-Z_0-9]* ;
INT_LIT  : [0-9]+ ;
STRING_LIT : '"' ( '\\' . | ~["\\] )* '"' ;
WS       : [ \t\r\n]+ -> skip ;
LINE_COMMENT : '//' ~[\r\n]* -> skip ;
BLOCK_COMMENT: '/*' .*? '*/' -> skip ;

