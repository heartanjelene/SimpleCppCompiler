package com.simplecpp.compiler.optimize;

import com.simplecpp.compiler.ast.AstNodes.*;
import com.simplecpp.compiler.ast.AstProgram;

public class ConstantFolder {

    public static void fold(AstProgram p) {
        for (Stmt s : p.root.statements) foldStmt(s);
    }

    private static void foldStmt(Stmt s) {
        if (s instanceof Block b) {
            for (Stmt inner : b.statements) foldStmt(inner);
        } else if (s instanceof Decl d) {
            if (d.initOrNull != null) d.initOrNull = foldExpr(d.initOrNull);
        } else if (s instanceof Assign a) {
            a.value = foldExpr(a.value);
        } else if (s instanceof Cout c) {
            for (int i = 0; i < c.items.size(); i++) c.items.set(i, foldExpr(c.items.get(i)));
        } else if (s instanceof If i) {
            i.condition = foldExpr(i.condition);
            foldStmt(i.thenBranch);
            if (i.elseBranch != null) foldStmt(i.elseBranch);
        } else if (s instanceof While w) {
            w.condition = foldExpr(w.condition);
            foldStmt(w.body);
        }
    }

    private static Expr foldExpr(Expr e) {
        if (e instanceof IntLit || e instanceof BoolLit || e instanceof StringLit || e instanceof Id) return e;
        if (e instanceof Unary u) {
            Expr inner = foldExpr(u.inner);
            u.inner = inner;
            if (inner instanceof IntLit il && u.op == Unary.Op.NEG) {
                return new IntLit(-il.value, u.pos());
            }
            if (inner instanceof BoolLit bl && u.op == Unary.Op.NOT) {
                return new BoolLit(!bl.value, u.pos());
            }
            return u;
        }
        if (e instanceof Binary b) {
            Expr left = foldExpr(b.left);
            Expr right = foldExpr(b.right);
            b.left = left;
            b.right = right;
            if (left instanceof IntLit li && right instanceof IntLit ri) {
                return switch (b.op) {
                    case ADD -> new IntLit(li.value + ri.value, b.pos());
                    case SUB -> new IntLit(li.value - ri.value, b.pos());
                    case MUL -> new IntLit(li.value * ri.value, b.pos());
                    case DIV -> ri.value == 0 ? b : new IntLit(li.value / ri.value, b.pos());
                    case MOD -> ri.value == 0 ? b : new IntLit(li.value % ri.value, b.pos());
                    case LT -> new BoolLit(li.value < ri.value, b.pos());
                    case LTE -> new BoolLit(li.value <= ri.value, b.pos());
                    case GT -> new BoolLit(li.value > ri.value, b.pos());
                    case GTE -> new BoolLit(li.value >= ri.value, b.pos());
                    case EQ -> new BoolLit(li.value == ri.value, b.pos());
                    case NEQ -> new BoolLit(li.value != ri.value, b.pos());
                    case AND -> new BoolLit((li.value != 0) && (ri.value != 0), b.pos());
                    case OR -> new BoolLit((li.value != 0) || (ri.value != 0), b.pos());
                };
            }
            if (left instanceof BoolLit lb && right instanceof BoolLit rb) {
                return switch (b.op) {
                    case AND -> new BoolLit(lb.value && rb.value, b.pos());
                    case OR -> new BoolLit(lb.value || rb.value, b.pos());
                    case EQ -> new BoolLit(lb.value == rb.value, b.pos());
                    case NEQ -> new BoolLit(lb.value != rb.value, b.pos());
                    default -> b;
                };
            }
            return b;
        }
        return e;
    }
}
