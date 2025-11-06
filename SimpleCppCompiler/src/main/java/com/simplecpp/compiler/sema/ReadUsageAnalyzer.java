package com.simplecpp.compiler.sema;

import com.simplecpp.compiler.ast.AstNodes.*;
import com.simplecpp.compiler.ast.AstProgram;

import java.util.HashSet;
import java.util.Set;

public class ReadUsageAnalyzer {
    private final Set<String> read = new HashSet<>();

    public Set<String> collect(AstProgram p) {
        for (Stmt s : p.root.statements) {
            if (s instanceof Assign a) visitExpr(a.value);
            else if (s instanceof Cout c) for (Expr e : c.items) visitExpr(e);
            else if (s instanceof Decl d && d.initOrNull != null) visitExpr(d.initOrNull);
        }
        return read;
    }

    private void visitExpr(Expr e) {
        if (e instanceof Id id) read.add(id.name);
        else if (e instanceof Add a) { visitExpr(a.left); visitExpr(a.right); }
        else if (e instanceof Neg n) visitExpr(n.inner);
    }
}
