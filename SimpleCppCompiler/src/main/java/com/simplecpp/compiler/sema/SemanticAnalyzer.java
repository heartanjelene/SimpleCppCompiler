package com.simplecpp.compiler.sema;

import com.simplecpp.compiler.ast.AstNodes.*;
import com.simplecpp.compiler.ast.AstProgram;
import com.simplecpp.compiler.util.Diagnostics;

import java.util.HashMap;
import java.util.Map;

public class SemanticAnalyzer {

    public enum Type { INT, STRING_LIT }

    private final Diagnostics diags;
    private final Map<String, Type> symbols = new HashMap<>();
    private final Map<String, Boolean> initialized = new HashMap<>();

    public SemanticAnalyzer(Diagnostics diags) { this.diags = diags; }

    public void analyze(AstProgram program) {
        for (Stmt s : program.root.statements) {
            if (s instanceof Decl d) analyzeDecl(d);
            else if (s instanceof Assign a) analyzeAssign(a);
            else if (s instanceof Cin c) analyzeCin(c);
            else if (s instanceof Cout c) analyzeCout(c);
        }
    }

    private void analyzeDecl(Decl d) {
        if (symbols.containsKey(d.name)) {
            diags.error(d.pos().line, d.pos().col, "Duplicate declaration of '" + d.name + "'.");
            return;
        }
        symbols.put(d.name, Type.INT);
        boolean init = false;
        if (d.initOrNull != null) {
            Type t = typeOf(d.initOrNull);
            if (t != Type.INT) diags.error(d.pos().line, d.pos().col, "Initializer for '" + d.name + "' must be int.");
            init = (t == Type.INT);
        }
        initialized.put(d.name, init);
    }

    private void analyzeAssign(Assign a) {
        if (!symbols.containsKey(a.name)) {
            // allow implicit declaration-by-assignment for this toy language
            symbols.put(a.name, Type.INT);
            initialized.put(a.name, false);
        }
        Type t = typeOf(a.value);
        if (t != Type.INT) diags.error(a.pos().line, a.pos().col, "Assignment to '" + a.name + "' must be int.");
        initialized.put(a.name, t == Type.INT);
    }

    private void analyzeCin(Cin c) {
        for (String name : c.names) {
            symbols.putIfAbsent(name, Type.INT);
            initialized.put(name, true); // reading from cin initializes
        }
    }

    private void analyzeCout(Cout c) {
        for (Expr e : c.items) {
            Type t = typeOf(e);
            if (t != Type.INT && t != Type.STRING_LIT) {
                diags.error(c.pos().line, c.pos().col, "cout supports only int expressions and string literals.");
            }
        }
    }

    private Type typeOf(Expr e) {
        if (e instanceof IntLit) return Type.INT;
        if (e instanceof StringLit) return Type.STRING_LIT;
        if (e instanceof Id id) {
            if (!symbols.containsKey(id.name)) {
                diags.error(id.pos().line, id.pos().col, "Use of undeclared identifier '" + id.name + "'.");
                return Type.INT; // continue as int to avoid error flood
            }
            if (!initialized.getOrDefault(id.name, false)) {
                diags.error(id.pos().line, id.pos().col, "Variable '" + id.name + "' may be uninitialized here.");
            }
            return symbols.get(id.name);
        }
        if (e instanceof Add a) {
            Type l = typeOf(a.left), r = typeOf(a.right);
            if (l != Type.INT || r != Type.INT) {
                diags.error(a.pos().line, a.pos().col, "Operator '+' is only defined for int + int.");
            }
            return Type.INT;
        }
        if (e instanceof Neg n) {
            Type t = typeOf(n.inner);
            if (t != Type.INT) {
                diags.error(n.pos().line, n.pos().col, "Unary '-' requires an int.");
            }
            return Type.INT;
        }
        return Type.INT;
    }
}

