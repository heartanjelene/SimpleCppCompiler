package com.simplecpp.compiler.optimize;

import com.simplecpp.compiler.ast.AstNodes.*;
import com.simplecpp.compiler.ast.AstProgram;


public class ConstantFolder {

    public static void fold(AstProgram p) {
        for (int i = 0; i < p.root.statements.size(); i++) {
            Stmt s = p.root.statements.get(i);
            if (s instanceof Decl d && d.initOrNull != null) d.initOrNull = foldExpr(d.initOrNull);
            else if (s instanceof Assign a) a.value = foldExpr(a.value);
            else if (s instanceof Cout c)
                for (int k = 0; k < c.items.size(); k++) c.items.set(k, foldExpr(c.items.get(k)));
        }
    }

    private static Expr foldExpr(Expr e) {
        if (e instanceof IntLit || e instanceof StringLit || e instanceof Id) return e;
        if (e instanceof Neg n) {
            Expr inner = foldExpr(n.inner);
            if (inner instanceof IntLit il) return new IntLit(-il.value, n.pos());
            return new Neg(inner, n.pos());
        }
        if (e instanceof Add a) {
            Expr l = foldExpr(a.left), r = foldExpr(a.right);
            if (l instanceof IntLit li && r instanceof IntLit ri) return new IntLit(li.value + ri.value, a.pos());
            return new Add(l, r, a.pos());
        }
        return e;
    }
}