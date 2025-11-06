package com.simplecpp.compiler.sema;

import com.simplecpp.compiler.ast.AstNodes.*;
import com.simplecpp.compiler.ast.AstProgram;
import com.simplecpp.compiler.util.Diagnostics;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class SemanticAnalyzer {

    private static class Symbol {
        final ValueType type;
        boolean initialized;
        Symbol(ValueType type, boolean initialized) { this.type = type; this.initialized = initialized; }
    }

    private final Diagnostics diags;
    private final Deque<Map<String, Symbol>> scopes = new ArrayDeque<>();

    public SemanticAnalyzer(Diagnostics diags) { this.diags = diags; }

    public void analyze(AstProgram program) {
        pushScope();
        for (Stmt s : program.root.statements) analyzeStmt(s);
        popScope();
    }

    private void pushScope() { scopes.push(new HashMap<>()); }
    private void popScope() { scopes.pop(); }

    private Map<String, Symbol> currentScope() { return scopes.peek(); }

    private Symbol lookup(String name) {
        for (Map<String, Symbol> scope : scopes) {
            Symbol sym = scope.get(name);
            if (sym != null) return sym;
        }
        return null;
    }

    private void analyzeStmt(Stmt s) {
        if (s instanceof Block b) {
            pushScope();
            for (Stmt inner : b.statements) analyzeStmt(inner);
            popScope();
        } else if (s instanceof Decl d) {
            analyzeDecl(d);
        } else if (s instanceof Assign a) {
            analyzeAssign(a);
        } else if (s instanceof Cin c) {
            analyzeCin(c);
        } else if (s instanceof Cout c) {
            analyzeCout(c);
        } else if (s instanceof If i) {
            analyzeIf(i);
        } else if (s instanceof While w) {
            analyzeWhile(w);
        }
    }

    private void analyzeDecl(Decl d) {
        if (currentScope().containsKey(d.name)) {
            diags.error(d.pos().line, d.pos().col, "Duplicate declaration of '" + d.name + "'.");
            return;
        }
        Symbol sym = new Symbol(d.type, false);
        currentScope().put(d.name, sym);
        if (d.initOrNull != null) {
            ValueType initType = typeOf(d.initOrNull);
            if (initType != d.type) {
                diags.error(d.pos().line, d.pos().col, "Initializer type mismatch for '" + d.name + "'.");
            } else {
                sym.initialized = true;
            }
        }
    }

    private void analyzeAssign(Assign a) {
        ValueType valueType = typeOf(a.value);
        Symbol sym = lookup(a.name);
        if (sym == null) {
            if (valueType == ValueType.STRING) {
                diags.error(a.pos().line, a.pos().col,
                        "Implicitly declared variables cannot store string values.");
                return;
            }
            sym = new Symbol(valueType, false);
            currentScope().put(a.name, sym);
        } else if (valueType != sym.type) {
            diags.error(a.pos().line, a.pos().col,
                    "Cannot assign " + valueType + " to variable of type " + sym.type + ".");
            return;
        }
        sym.initialized = (valueType != ValueType.STRING);
    }

    private void analyzeCin(Cin c) {
        for (String name : c.names) {
            Symbol sym = lookup(name);
            if (sym == null) {
                sym = new Symbol(ValueType.INT, true);
                currentScope().put(name, sym);
            } else if (sym.type != ValueType.INT && sym.type != ValueType.BOOL) {
                diags.error(c.pos().line, c.pos().col, "cin does not support type '" + sym.type + "'.");
            } else {
                sym.initialized = true;
            }
        }
    }

    private void analyzeCout(Cout c) {
        for (Expr e : c.items) {
            ValueType t = typeOf(e);
            if (t != ValueType.INT && t != ValueType.BOOL && t != ValueType.STRING) {
                diags.error(e.pos().line, e.pos().col, "cout cannot print value of type " + t + ".");
            }
        }
    }

    private void analyzeIf(If i) {
        ValueType cond = typeOf(i.condition);
        if (cond != ValueType.BOOL) {
            diags.error(i.condition.pos().line, i.condition.pos().col, "if condition must be bool.");
        }
        pushScope();
        analyzeStmt(i.thenBranch);
        popScope();
        if (i.elseBranch != null) {
            pushScope();
            analyzeStmt(i.elseBranch);
            popScope();
        }
    }

    private void analyzeWhile(While w) {
        ValueType cond = typeOf(w.condition);
        if (cond != ValueType.BOOL) {
            diags.error(w.condition.pos().line, w.condition.pos().col, "while condition must be bool.");
        }
        pushScope();
        analyzeStmt(w.body);
        popScope();
    }

    private ValueType typeOf(Expr e) {
        if (e instanceof IntLit) return ValueType.INT;
        if (e instanceof BoolLit) return ValueType.BOOL;
        if (e instanceof StringLit) return ValueType.STRING;
        if (e instanceof Id id) {
            Symbol sym = lookup(id.name);
            if (sym == null) {
                diags.error(id.pos().line, id.pos().col, "Use of undeclared identifier '" + id.name + "'.");
                return ValueType.INT;
            }
            if (!sym.initialized) {
                diags.error(id.pos().line, id.pos().col, "Variable '" + id.name + "' may be uninitialized here.");
            }
            return sym.type;
        }
        if (e instanceof Unary u) {
            ValueType inner = typeOf(u.inner);
            if (u.op == Unary.Op.NEG) {
                if (inner != ValueType.INT) diags.error(u.pos().line, u.pos().col, "Unary '-' requires int.");
                return ValueType.INT;
            } else {
                if (inner != ValueType.BOOL) diags.error(u.pos().line, u.pos().col, "Logical '!' requires bool.");
                return ValueType.BOOL;
            }
        }
        if (e instanceof Binary b) {
            ValueType left = typeOf(b.left);
            ValueType right = typeOf(b.right);
            return switch (b.op) {
                case ADD, SUB, MUL, DIV, MOD -> {
                    if (left != ValueType.INT || right != ValueType.INT)
                        diags.error(b.pos().line, b.pos().col, "Arithmetic operators require ints.");
                    yield ValueType.INT;
                }
                case LT, LTE, GT, GTE -> {
                    if (left != ValueType.INT || right != ValueType.INT)
                        diags.error(b.pos().line, b.pos().col, "Comparison operators require ints.");
                    yield ValueType.BOOL;
                }
                case EQ, NEQ -> {
                    if (left != right) diags.error(b.pos().line, b.pos().col, "Operands must have matching types.");
                    yield ValueType.BOOL;
                }
                case AND, OR -> {
                    if (left != ValueType.BOOL || right != ValueType.BOOL)
                        diags.error(b.pos().line, b.pos().col, "Logical operators require bool operands.");
                    yield ValueType.BOOL;
                }
            };
        }
        return ValueType.INT;
    }
}