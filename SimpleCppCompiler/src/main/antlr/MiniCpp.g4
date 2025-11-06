grammar MiniCpp;

// lexer and parser rules.

@header { package com.simplecpp.compiler.grammar; }

// ---------- Grammar for our expanded C++ subset ----------
program   : stmt* EOF ;
stmt      : declStmt
          | assignStmt
          | ioStmt
          | ifStmt
          | whileStmt
          | block
          ;

block     : LBRACE stmt* RBRACE ;

declStmt  : typeSpec ID (ASSIGN expr)? SEMI ;
assignStmt: ID ASSIGN expr SEMI ;

typeSpec  : INT_KW
          | BOOL_KW
          ;

ioStmt    : cinStmt | coutStmt ;
cinStmt   : CIN (SHIFT_IN ID)+ SEMI ;
coutStmt  : COUT (SHIFT_OUT expr)+ SEMI ;

ifStmt    : IF LPAREN expr RPAREN stmt (ELSE stmt)? ;
whileStmt : WHILE LPAREN expr RPAREN stmt ;

expr          : logicalOr ;
logicalOr     : logicalAnd (OR logicalAnd)* ;
logicalAnd    : equality (AND equality)* ;
equality      : comparison ((EQ | NEQ) comparison)* ;
comparison    : additive ((LT | LTE | GT | GTE) additive)* ;
additive      : multiplicative ((PLUS | MINUS) multiplicative)* ;
multiplicative: unary ((STAR | SLASH | PERCENT) unary)* ;
unary         : (NOT | MINUS) unary | primary ;
primary       : INT_LIT
              | BOOL_LIT
              | STRING_LIT
              | ID
              | LPAREN expr RPAREN
              ;

// ---------- Lexer rules ----------
CIN        : 'cin' ;
COUT       : 'cout' ;
INT_KW     : 'int' ;
BOOL_KW    : 'bool' ;
IF         : 'if' ;
ELSE       : 'else' ;
WHILE      : 'while' ;
SHIFT_IN   : '>>' ;
SHIFT_OUT  : '<<' ;
PLUS       : '+' ;
MINUS      : '-' ;
STAR       : '*' ;
SLASH      : '/' ;
PERCENT    : '%' ;
ASSIGN     : '=' ;
SEMI       : ';' ;
LPAREN     : '(' ;
RPAREN     : ')' ;
LBRACE     : '{' ;
RBRACE     : '}' ;
EQ         : '==' ;
NEQ        : '!=' ;
LTE        : '<=' ;
GTE        : '>=' ;
LT         : '<' ;
GT         : '>' ;
AND        : '&&' ;
OR         : '||' ;
NOT        : '!' ;
BOOL_LIT   : 'true' | 'false' ;
ID         : [a-zA-Z_][a-zA-Z_0-9]* ;
INT_LIT    : [0-9]+ ;
STRING_LIT : '"' ( '\\' . | ~["\\] )* '"' ;
WS         : [ \t\r\n]+ -> skip ;
LINE_COMMENT : '//' ~[\r\n]* -> skip ;
BLOCK_COMMENT: '/*' .*? '*/' -> skip ;