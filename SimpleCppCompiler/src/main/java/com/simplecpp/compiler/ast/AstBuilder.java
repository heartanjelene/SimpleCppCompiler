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

    @Override public Stmt visitBlock(MiniCppParser.BlockContext ctx) {
        Block block = new Block(pos(ctx.start));
        for (var s : ctx.stmt()) block.statements.add((Stmt) visit(s));
        return block;
    }

    @Override public Stmt visitDeclStmt(MiniCppParser.DeclStmtContext ctx) {
        ValueType type = toType(ctx.typeSpec());
        String name = ctx.ID().getText();
        Expr init = (ctx.expr() != null) ? (Expr) visit(ctx.expr()) : null;
        return new Decl(type, name, init, pos(ctx.start));
    }

    private ValueType toType(MiniCppParser.TypeSpecContext ctx) {
        if (ctx.INT_KW() != null) return ValueType.INT;
        if (ctx.BOOL_KW() != null) return ValueType.BOOL;
        throw new IllegalStateException("Unknown type token: " + ctx.getText());
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

    @Override public Stmt visitIfStmt(MiniCppParser.IfStmtContext ctx) {
        Expr cond = (Expr) visit(ctx.expr());
        Stmt thenBranch = (Stmt) visit(ctx.stmt(0));
        Stmt elseBranch = (ctx.stmt().size() > 1) ? (Stmt) visit(ctx.stmt(1)) : null;
        return new If(cond, thenBranch, elseBranch, pos(ctx.start));
    }

    @Override public Stmt visitWhileStmt(MiniCppParser.WhileStmtContext ctx) {
        Expr cond = (Expr) visit(ctx.expr());
        Stmt body = (Stmt) visit(ctx.stmt());
        return new While(cond, body, pos(ctx.start));
    }

    /* ======= expressions ======= */

    @Override public Expr visitLogicalOr(MiniCppParser.LogicalOrContext ctx) {
        Expr e = (Expr) visit(ctx.logicalAnd(0));
        for (int i = 1; i < ctx.logicalAnd().size(); i++) {
            Expr r = (Expr) visit(ctx.logicalAnd(i));
            e = new Binary(Binary.Op.OR, e, r, pos(ctx.start));
        }
        return e;
    }

    @Override public Expr visitLogicalAnd(MiniCppParser.LogicalAndContext ctx) {
        Expr e = (Expr) visit(ctx.equality(0));
        for (int i = 1; i < ctx.equality().size(); i++) {
            Expr r = (Expr) visit(ctx.equality(i));
            e = new Binary(Binary.Op.AND, e, r, pos(ctx.start));
        }
        return e;
    }

    @Override public Expr visitEquality(MiniCppParser.EqualityContext ctx) {
        Expr e = (Expr) visit(ctx.comparison(0));
        for (int i = 1; i < ctx.comparison().size(); i++) {
            Expr r = (Expr) visit(ctx.comparison(i));
            Token op = ctx.getChild(2 * i - 1).getPayload() instanceof Token t ? t : ctx.start;
            Binary.Op bop = (op.getType() == MiniCppParser.EQ) ? Binary.Op.EQ : Binary.Op.NEQ;
            e = new Binary(bop, e, r, pos(op));
        }
        return e;
    }

    @Override public Expr visitComparison(MiniCppParser.ComparisonContext ctx) {
        Expr e = (Expr) visit(ctx.additive(0));
        for (int i = 1; i < ctx.additive().size(); i++) {
            Expr r = (Expr) visit(ctx.additive(i));
            Token op = ctx.getChild(2 * i - 1).getPayload() instanceof Token t ? t : ctx.start;
            Binary.Op bop;
            switch (op.getType()) {
                case MiniCppParser.LT -> bop = Binary.Op.LT;
                case MiniCppParser.LTE -> bop = Binary.Op.LTE;
                case MiniCppParser.GT -> bop = Binary.Op.GT;
                case MiniCppParser.GTE -> bop = Binary.Op.GTE;
                default -> throw new IllegalStateException("Unexpected comparison op");
            }
            e = new Binary(bop, e, r, pos(op));
        }
        return e;
    }

    @Override public Expr visitAdditive(MiniCppParser.AdditiveContext ctx) {
        Expr e = (Expr) visit(ctx.multiplicative(0));
        for (int i = 1; i < ctx.multiplicative().size(); i++) {
            Expr r = (Expr) visit(ctx.multiplicative(i));
            Token op = ctx.getChild(2 * i - 1).getPayload() instanceof Token t ? t : ctx.start;
            Binary.Op bop = (op.getType() == MiniCppParser.PLUS) ? Binary.Op.ADD : Binary.Op.SUB;
            e = new Binary(bop, e, r, pos(op));
        }
        return e;
    }

    @Override public Expr visitMultiplicative(MiniCppParser.MultiplicativeContext ctx) {
        Expr e = (Expr) visit(ctx.unary(0));
        for (int i = 1; i < ctx.unary().size(); i++) {
            Expr r = (Expr) visit(ctx.unary(i));
            Token op = ctx.getChild(2 * i - 1).getPayload() instanceof Token t ? t : ctx.start;
            Binary.Op bop;
            switch (op.getType()) {
                case MiniCppParser.STAR -> bop = Binary.Op.MUL;
                case MiniCppParser.SLASH -> bop = Binary.Op.DIV;
                case MiniCppParser.PERCENT -> bop = Binary.Op.MOD;
                default -> throw new IllegalStateException("Unexpected multiplicative op");
            }
            e = new Binary(bop, e, r, pos(op));
        }
        return e;
    }

    @Override public Expr visitUnary(MiniCppParser.UnaryContext ctx) {
        if (ctx.unary() != null) {
            if (ctx.NOT() != null) return new Unary(Unary.Op.NOT, (Expr) visit(ctx.unary()), pos(ctx.start));
            if (ctx.MINUS() != null) return new Unary(Unary.Op.NEG, (Expr) visit(ctx.unary()), pos(ctx.start));
        }
        return (Expr) visit(ctx.primary());
    }

    @Override public Expr visitPrimary(MiniCppParser.PrimaryContext ctx) {
        if (ctx.INT_LIT() != null) return new IntLit(Integer.parseInt(ctx.INT_LIT().getText()), pos(ctx.start));
        if (ctx.BOOL_LIT() != null) return new BoolLit(Boolean.parseBoolean(ctx.BOOL_LIT().getText()), pos(ctx.start));
        if (ctx.STRING_LIT() != null) {
            String s = unescape(ctx.STRING_LIT().getText());
            return new StringLit(s, pos(ctx.start));
        }
        if (ctx.ID() != null) return new Id(ctx.ID().getText(), pos(ctx.start));
        return (Expr) visit(ctx.expr());
    }

    private String unescape(String token) {
        String raw = token.substring(1, token.length() - 1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            char ch = raw.charAt(i);
            if (ch == '\\' && i + 1 < raw.length()) {
                char next = raw.charAt(++i);
                switch (next) {
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    default -> sb.append(next);
                }
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }
}