package com.simplecpp.compiler.sema;

import com.simplecpp.compiler.ast.AstNodes.*;
import com.simplecpp.compiler.ast.AstProgram;

import java.util.HashSet;
import java.util.Set;

public class ReadUsageAnalyzer {
    private final Set<String> read = new HashSet<>();

    public Set<String> collect(AstProgram p) {
        for (Stmt s : p.root.statements) visitStmt(s);
        return read;
    }

    private void visitStmt(Stmt s) {
        if (s instanceof Block b) {
            for (Stmt inner : b.statements) visitStmt(inner);
        } else if (s instanceof Decl d) {
            if (d.initOrNull != null) visitExpr(d.initOrNull);
        } else if (s instanceof Assign a) {
            visitExpr(a.value);
        } else if (s instanceof Cout c) {
            for (Expr e : c.items) visitExpr(e);
        } else if (s instanceof If i) {
            visitExpr(i.condition);
            visitStmt(i.thenBranch);
            if (i.elseBranch != null) visitStmt(i.elseBranch);
        } else if (s instanceof While w) {
            visitExpr(w.condition);
            visitStmt(w.body);
        }
    }

    private void visitExpr(Expr e) {
        if (e instanceof Id id) read.add(id.name);
        else if (e instanceof Unary u) visitExpr(u.inner);
        else if (e instanceof Binary b) {
            visitExpr(b.left);
            visitExpr(b.right);
        }
    }
}