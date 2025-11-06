package com.simplecpp.compiler.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Tiny, readable AST node set for the MiniCpp subset.
 */
public class AstNodes {

    /** Supported static types in the toy language. */
    public enum ValueType { INT, BOOL, STRING }

    /** Source position for friendly messages. */
    public static class Pos {
        public final int line, col;
        public Pos(int line, int col) { this.line = line; this.col = col; }
    }

    public interface Node { Pos pos(); }

    /* ======= Statements and program ======= */

    public static class Program implements Node {
        public final List<Stmt> statements = new ArrayList<>();
        private final Pos pos;
        public Program(Pos pos) { this.pos = pos; }
        @Override public Pos pos() { return pos; }
    }

    public interface Stmt extends Node {}

    public static class Block implements Stmt {
        public final List<Stmt> statements = new ArrayList<>();
        private final Pos pos;
        public Block(Pos pos) { this.pos = pos; }
        @Override public Pos pos() { return pos; }
    }

    public static class Decl implements Stmt {
        public final ValueType type;
        public final String name;
        public Expr initOrNull;         // may be null
        private final Pos pos;
        public Decl(ValueType type, String name, Expr initOrNull, Pos pos) {
            this.type = Objects.requireNonNull(type);
            this.name = name;
            this.initOrNull = initOrNull;
            this.pos = pos;
        }
        @Override public Pos pos() { return pos; }
    }

    public static class Assign implements Stmt {
        public final String name;
        public Expr value;
        private final Pos pos;
        public Assign(String name, Expr value, Pos pos) {
            this.name = name; this.value = value; this.pos = pos;
        }
        @Override public Pos pos() { return pos; }
    }

    public static class Cin implements Stmt {
        public final List<String> names = new ArrayList<>();
        private final Pos pos;
        public Cin(Pos pos) { this.pos = pos; }
        @Override public Pos pos() { return pos; }
    }

    public static class Cout implements Stmt {
        public final List<Expr> items = new ArrayList<>();
        private final Pos pos;
        public Cout(Pos pos) { this.pos = pos; }
        @Override public Pos pos() { return pos; }
    }

    public static class If implements Stmt {
        public Expr condition;
        public Stmt thenBranch;
        public Stmt elseBranch; // may be null
        private final Pos pos;
        public If(Expr condition, Stmt thenBranch, Stmt elseBranch, Pos pos) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
            this.pos = pos;
        }
        @Override public Pos pos() { return pos; }
    }

    public static class While implements Stmt {
        public Expr condition;
        public Stmt body;
        private final Pos pos;
        public While(Expr condition, Stmt body, Pos pos) {
            this.condition = condition;
            this.body = body;
            this.pos = pos;
        }
        @Override public Pos pos() { return pos; }
    }

    /* ======= Expressions ======= */

    public interface Expr extends Node {}

    public static class IntLit implements Expr {
        public final int value; private final Pos pos;
        public IntLit(int value, Pos pos) { this.value = value; this.pos = pos; }
        @Override public Pos pos() { return pos; }
    }

    public static class BoolLit implements Expr {
        public final boolean value; private final Pos pos;
        public BoolLit(boolean value, Pos pos) { this.value = value; this.pos = pos; }
        @Override public Pos pos() { return pos; }
    }

    public static class StringLit implements Expr {
        public final String value; private final Pos pos;
        public StringLit(String value, Pos pos) { this.value = value; this.pos = pos; }
        @Override public Pos pos() { return pos; }
    }

    public static class Id implements Expr {
        public final String name; private final Pos pos;
        public Id(String name, Pos pos) { this.name = name; this.pos = pos; }
        @Override public Pos pos() { return pos; }
    }

    public static class Unary implements Expr {
        public enum Op { NEG, NOT }
        public Op op; public Expr inner; private final Pos pos;
        public Unary(Op op, Expr inner, Pos pos) { this.op = op; this.inner = inner; this.pos = pos; }
        @Override public Pos pos() { return pos; }
    }

    public static class Binary implements Expr {
        public enum Op {
            ADD, SUB, MUL, DIV, MOD,
            EQ, NEQ,
            LT, LTE, GT, GTE,
            AND, OR
        }
        public Op op; public Expr left, right; private final Pos pos;
        public Binary(Op op, Expr left, Expr right, Pos pos) {
            this.op = op; this.left = left; this.right = right; this.pos = pos;
        }
        @Override public Pos pos() { return pos; }
    }
}