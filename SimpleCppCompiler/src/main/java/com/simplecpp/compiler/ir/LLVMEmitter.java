package com.simplecpp.compiler.ir;

import com.simplecpp.compiler.ast.AstNodes.*;
import com.simplecpp.compiler.ast.AstProgram;

import java.util.*;

/**
 * Emits very simple LLVM IR:
 * - main(): allocas for local ints
 * - scanf("%d", &x), printf("%d", v), printf("%s", str)
 * - + and unary -
 *
 * Assumes LLVM toolchain (llc, clang) is on PATH.
 */
public class LLVMEmitter {

    private final StringBuilder globals = new StringBuilder();
    private final StringBuilder body = new StringBuilder();
    private int tmp = 0, strId = 0;

    private final Map<String, String> localAlloca = new LinkedHashMap<>();
    private final Map<String, String> stringGlobals = new LinkedHashMap<>();

    private String fmtIntName, fmtStrName;
    private final Set<String> varsEverRead;

    public LLVMEmitter(Set<String> varsEverRead) {
        this.varsEverRead = (varsEverRead == null) ? Set.of() : varsEverRead;
    }

    private String fresh(String hint) { return "%" + hint + (++tmp); }

    public String emitModule(AstProgram p) {
        StringBuilder out = new StringBuilder();
        out.append("declare i32 @printf(ptr, ...)\n");
        out.append("declare i32 @scanf(ptr, ...)\n\n");

        // Create (or reuse) global format strings once
        fmtIntName = ensureGlobalCString("%d");
        fmtStrName = ensureGlobalCString("%s");

        body.append("define i32 @main() {\n");
        for (Stmt s : p.root.statements) {
            if (s instanceof Decl d) emitDecl(d);
            else if (s instanceof Assign a) emitAssign(a);
            else if (s instanceof Cin c) emitCin(c);
            else if (s instanceof Cout c) emitCout(c);
        }
        body.append("  ret i32 0\n}\n");

        out.append(globals).append("\n").append(body);
        return out.toString();
    }

    /* ================= statements ================= */

    private void emitDecl(Decl d) {
        ensureLocalAlloca(d.name);
        if (d.initOrNull != null) {
            String v = evalIntExpr(d.initOrNull);
            body.append("  store i32 ").append(v).append(", ptr ").append(localAlloca.get(d.name)).append("\n");
        }
    }

    private void emitAssign(Assign a) {
        // tiny DCE: if the var is never read, skip the store entirely
        if (!varsEverRead.contains(a.name)) return;
        ensureLocalAlloca(a.name);
        String v = evalIntExpr(a.value);
        body.append("  store i32 ").append(v).append(", ptr ").append(localAlloca.get(a.name)).append("\n");
    }

    private void emitCin(Cin c) {
        for (String name : c.names) {
            ensureLocalAlloca(name);
            String fmtPtr = gepToCString(fmtIntName, 3); // "%d\0"
            String call = fresh("scanf");
            body.append("  ").append(call).append(" = call i32 @scanf(ptr ")
                    .append(fmtPtr).append(", ptr ").append(localAlloca.get(name)).append(")\n");
        }
    }

    private void emitCout(Cout c) {
        for (Expr e : c.items) {
            if (e instanceof StringLit s) {
                String g = ensureGlobalCString(s.value);
                String fmtPtr = gepToCString(fmtStrName, 3);
                String strPtr = gepToCString(g, llStringByteLen(s.value));
                String call = fresh("printfs");
                body.append("  ").append(call).append(" = call i32 @printf(ptr ")
                        .append(fmtPtr).append(", ptr ").append(strPtr).append(")\n");
            } else {
                String v = evalIntExpr(e);
                String fmtPtr = gepToCString(fmtIntName, 3);
                String call = fresh("printfi");
                body.append("  ").append(call).append(" = call i32 @printf(ptr ")
                        .append(fmtPtr).append(", i32 ").append(v).append(")\n");
            }
        }
    }

    /* ================= expressions ================= */

    private String evalIntExpr(Expr e) {
        if (e instanceof IntLit lit) return String.valueOf(lit.value);
        if (e instanceof Id id) {
            ensureLocalAlloca(id.name);
            String r = fresh("load");
            body.append("  ").append(r).append(" = load i32, ptr ").append(localAlloca.get(id.name)).append("\n");
            return r;
        }
        if (e instanceof Neg n) {
            String inner = evalIntExpr(n.inner);
            String t = fresh("sub");
            body.append("  ").append(t).append(" = sub i32 0, ").append(inner).append("\n");
            return t;
        }
        if (e instanceof Add a) {
            String l = evalIntExpr(a.left), r = evalIntExpr(a.right);
            String t = fresh("add");
            body.append("  ").append(t).append(" = add i32 ").append(l).append(", ").append(r).append("\n");
            return t;
        }
        throw new IllegalStateException("Non-int expression in int context.");
    }

    /* ================= helpers ================= */

    private void ensureLocalAlloca(String name) {
        if (!localAlloca.containsKey(name)) {
            String slot = fresh("slot_" + name + "_");
            body.append("  ").append(slot).append(" = alloca i32\n");
            localAlloca.put(name, slot);
        }
    }

    private String ensureGlobalCString(String content) {
        if (stringGlobals.containsKey(content)) return stringGlobals.get(content);
        String gname = "@.str." + (strId++);
        int n = llStringByteLen(content);
        String escaped = escapeForLLVMC(content) + "\\00";
        globals.append(gname).append(" = private constant [").append(n)
                .append(" x i8] c\"").append(escaped).append("\"\n");
        stringGlobals.put(content, gname);
        return gname;
    }

    private int llStringByteLen(String s) { return s.length() + 1; } // include NUL

    private String escapeForLLVMC(String s) {
        StringBuilder b = new StringBuilder();
        for (char ch : s.toCharArray()) {
            switch (ch) {
                case '\\' -> b.append("\\5C");
                case '"'  -> b.append("\\22");
                case '\n' -> b.append("\\0A");
                case '\r' -> b.append("\\0D");
                case '\t' -> b.append("\\09");
                default -> {
                    if (ch >= 32 && ch < 127) b.append(ch);
                    else b.append(String.format("\\%02X", (int) ch));
                }
            }
        }
        return b.toString();
    }

    private String gepToCString(String globalName, int arrayLen) {
        String t = fresh("gep");
        body.append("  ").append(t).append(" = getelementptr inbounds [")
                .append(arrayLen).append(" x i8], ptr ").append(globalName).append(", i32 0, i32 0\n");
        return t;
    }
}
// === END OF FILE ===
