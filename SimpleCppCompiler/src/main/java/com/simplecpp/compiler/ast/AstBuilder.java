package com.simplecpp.compiler.ast;

import com.simplecpp.compiler.ast.AstNodes.*;
import com.simplecpp.compiler.grammar.MiniCppBaseVisitor;
import com.simplecpp.compiler.grammar.MiniCppParser;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

/** Convert ANTLR parse tree → our compact AST. */
public class AstBuilder extends MiniCppBaseVisitor<Object> {
    private final CommonTokenStream tokens; // kept for future use (e.g., text)
    public AstBuilder(CommonTokenStream tokens) { this.tokens = tokens; }

    private Pos pos(Token t) { return new Pos(t.getLine(), t.getCharPositionInLine()); }

    @Override public AstProgram visitProgram(MiniCppParser.ProgramContext ctx) {
        Program p = new Program(new Pos(1, 0));
        for (var s : ctx.stmt()) p.statements.add((Stmt) visit(s));
        return new AstProgram(p);
    }

    @Override public Stmt visitDeclStmt(MiniCppParser.DeclStmtContext ctx) {
        String name = ctx.ID().getText();
        Expr init = (ctx.expr() != null) ? (Expr) visit(ctx.expr()) : null;
        return new Decl(name, init, pos(ctx.start));
    }

    @Override public Stmt visitAssignStmt(MiniCppParser.AssignStmtContext ctx) {
        return new Assign(ctx.ID().getText(), (Expr) visit(ctx.expr()), pos(ctx.start));
    }

    @Override public Stmt visitCinStmt(MiniCppParser.CinStmtContext ctx) {
        Cin c = new Cin(pos(ctx.start));
        for (var idTok : ctx.ID()) c.names.add(idTok.getText());
        return c;
    }

    @Override public Stmt visitCoutStmt(MiniCppParser.CoutStmtContext ctx) {
        Cout c = new Cout(pos(ctx.start));
        for (var e : ctx.expr()) c.items.add((Expr) visit(e));
        return c;
    }

    @Override public Object visitAddExpr(MiniCppParser.AddExprContext ctx) {
        Expr e = (Expr) visit(ctx.unary(0));
        for (int i = 1; i < ctx.unary().size(); i++) {
            Expr r = (Expr) visit(ctx.unary(i));
            e = new Add(e, r, pos(ctx.start));
        }
        return e;
    }

    @Override public Object visitUnary(MiniCppParser.UnaryContext ctx) {
        if (ctx.MINUS() != null) return new Neg((Expr) visit(ctx.unary()), pos(ctx.start));
        return visit(ctx.primary());
    }

    @Override public Expr visitPrimary(MiniCppParser.PrimaryContext ctx) {
        if (ctx.INT_LIT() != null) return new IntLit(Integer.parseInt(ctx.INT_LIT().getText()), pos(ctx.start));
        if (ctx.STRING_LIT() != null) {
            // Strip quotes and unescape \" and \\ minimally for demo purposes
            String raw = ctx.STRING_LIT().getText();
            String s = raw.substring(1, raw.length() - 1).replace("\\\"", "\"").replace("\\\\", "\\");
            return new StringLit(s, pos(ctx.start));
        }
        if (ctx.ID() != null) return new Id(ctx.ID().getText(), pos(ctx.start));
        return (Expr) visit(ctx.expr());
    }
}
// === END OF FILE ===
