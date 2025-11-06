package com.simplecpp.compiler.ir;

import com.simplecpp.compiler.ast.AstNodes.*;
import com.simplecpp.compiler.ast.AstProgram;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Emits LLVM IR for the enriched MiniCpp subset:
 * - main(): allocas for local ints and bools
 * - scanf/printf for input/output
 * - arithmetic, comparisons, logical operations
 * - structured control flow (if/else, while, blocks)
 *
 * Assumes LLVM toolchain (llc, clang) is on PATH for native code emission.
 */
public class LLVMEmitter {

    private static class VarInfo {
        final String alloca;
        final ValueType type;
        VarInfo(String alloca, ValueType type) { this.alloca = alloca; this.type = type; }
    }

    private final StringBuilder globals = new StringBuilder();
    private final StringBuilder body = new StringBuilder();
    private int tmp = 0, strId = 0, label = 0;

    private final Deque<Map<String, VarInfo>> scopes = new ArrayDeque<>();
    private final Map<String, String> stringGlobals = new LinkedHashMap<>();

    private String fmtIntName, fmtStrName, boolTrueName, boolFalseName;

    public LLVMEmitter(Set<String> varsEverRead) {
        // varsEverRead kept for backwards compatibility (currently unused but retained for API stability)
    }

    private String fresh(String hint) { return "%" + hint + (++tmp); }
    private String freshLabel(String hint) { return hint + (++label); }

    public String emitModule(AstProgram p) {
        StringBuilder out = new StringBuilder();
        out.append("declare i32 @printf(ptr, ...)\n");
        out.append("declare i32 @scanf(ptr, ...)\n\n");

        fmtIntName = ensureGlobalCString("%d");
        fmtStrName = ensureGlobalCString("%s");
        boolTrueName = ensureGlobalCString("true");
        boolFalseName = ensureGlobalCString("false");

        body.append("define i32 @main() {\n");
        pushScope();
        for (Stmt s : p.root.statements) emitStmt(s);
        popScope();
        body.append("  ret i32 0\n}\n");

        out.append(globals).append('\n').append(body);
        return out.toString();
    }

    /* ================= Scopes ================= */

    private void pushScope() { scopes.push(new LinkedHashMap<>()); }
    private void popScope() { scopes.pop(); }

    private void declareVar(String name, ValueType type, String slot) {
        scopes.peek().put(name, new VarInfo(slot, type));
    }

    private VarInfo lookupVar(String name) {
        for (Map<String, VarInfo> scope : scopes) {
            VarInfo info = scope.get(name);
            if (info != null) return info;
        }
        return null;
    }

    private VarInfo ensureImplicitVar(String name, ValueType type) {
        VarInfo existing = lookupVar(name);
        if (existing != null) return existing;
        String slot = fresh("slot_" + name + "_");
        body.append("  ").append(slot).append(" = alloca ").append(llvmType(type)).append("\n");
        VarInfo info = new VarInfo(slot, type);
        scopes.peek().put(name, info);
        return info;
    }

    private VarInfo resolveVar(String name) {
        VarInfo info = lookupVar(name);
        if (info == null) throw new IllegalStateException("Unknown variable '" + name + "' in IR emission");
        return info;
    }

    /* ================= Statement emission ================= */

    private void emitStmt(Stmt s) {
        if (s instanceof Block b) emitBlock(b);
        else if (s instanceof Decl d) emitDecl(d);
        else if (s instanceof Assign a) emitAssign(a);
        else if (s instanceof Cin c) emitCin(c);
        else if (s instanceof Cout c) emitCout(c);
        else if (s instanceof If i) emitIf(i);
        else if (s instanceof While w) emitWhile(w);
        else throw new IllegalStateException("Unhandled stmt: " + s.getClass());
    }

    private void emitBlock(Block b) {
        pushScope();
        for (Stmt s : b.statements) emitStmt(s);
        popScope();
    }

    private void emitDecl(Decl d) {
        String slot = fresh("slot_" + d.name + "_");
        body.append("  ").append(slot).append(" = alloca ").append(llvmType(d.type)).append("\n");
        declareVar(d.name, d.type, slot);
        if (d.initOrNull != null) {
            Value init = evalExpr(d.initOrNull);
            storeValue(init, resolveVar(d.name));
        }
    }

    private void emitAssign(Assign a) {
        Value value = evalExpr(a.value);
        VarInfo info = lookupVar(a.name);
        if (info == null) {
            info = ensureImplicitVar(a.name, value.type);
        }
        storeValue(value, info);
    }

    private void emitCin(Cin c) {
        for (String name : c.names) {
            VarInfo info = lookupVar(name);
            if (info == null) {
                info = ensureImplicitVar(name, ValueType.INT);
            }
            if (info.type == ValueType.INT) {
                callScanf(fmtIntName, info.alloca);
            } else if (info.type == ValueType.BOOL) {
                String tempAlloca = fresh("bool_scan_tmp");
                body.append("  ").append(tempAlloca).append(" = alloca i32\n");
                callScanf(fmtIntName, tempAlloca);
                String loaded = fresh("bool_scan_val");
                body.append("  ").append(loaded).append(" = load i32, ptr ").append(tempAlloca).append("\n");
                String cmp = fresh("bool_from_int");
                body.append("  ").append(cmp).append(" = icmp ne i32 ").append(loaded).append(", 0\n");
                body.append("  store i1 ").append(cmp).append(", ptr ").append(info.alloca).append("\n");
            } else {
                throw new IllegalStateException("cin does not support type " + info.type);
            }
        }
    }

    private void callScanf(String fmtGlobal, String targetAlloca) {
        String fmtPtr = gepToCString(fmtGlobal, 3);
        String call = fresh("scanf");
        body.append("  ").append(call).append(" = call i32 @scanf(ptr ")
                .append(fmtPtr).append(", ptr ").append(targetAlloca).append(")\n");
    }

    private void emitCout(Cout c) {
        for (Expr e : c.items) {
            if (e instanceof StringLit s) {
                emitPrintString(s.value);
            } else {
                Value v = evalExpr(e);
                if (v.type == ValueType.INT) {
                    emitPrintInt(v.llvmValue());
                } else if (v.type == ValueType.BOOL) {
                    emitPrintBool(v.llvmValue());
                } else {
                    throw new IllegalStateException("cout does not support value type " + v.type);
                }
            }
        }
    }

    private void emitPrintInt(String value) {
        String fmtPtr = gepToCString(fmtIntName, 3);
        String call = fresh("printfi");
        body.append("  ").append(call).append(" = call i32 @printf(ptr ")
                .append(fmtPtr).append(", i32 ").append(value).append(")\n");
    }

    private void emitPrintBool(String boolValue) {
        String truePtr = gepToCString(boolTrueName, llStringByteLen("true"));
        String falsePtr = gepToCString(boolFalseName, llStringByteLen("false"));
        String select = fresh("boolstr");
        body.append("  ").append(select).append(" = select i1 ").append(boolValue)
                .append(", ptr ").append(truePtr).append(", ptr ").append(falsePtr).append("\n");
        String fmtPtr = gepToCString(fmtStrName, 3);
        String call = fresh("printfb");
        body.append("  ").append(call).append(" = call i32 @printf(ptr ")
                .append(fmtPtr).append(", ptr ").append(select).append(")\n");
    }

    private void emitPrintString(String content) {
        String g = ensureGlobalCString(content);
        String fmtPtr = gepToCString(fmtStrName, 3);
        String strPtr = gepToCString(g, llStringByteLen(content));
        String call = fresh("printfs");
        body.append("  ").append(call).append(" = call i32 @printf(ptr ")
                .append(fmtPtr).append(", ptr ").append(strPtr).append(")\n");
    }

    private void emitIf(If i) {
        Value cond = evalExpr(i.condition);
        if (cond.type != ValueType.BOOL) throw new IllegalStateException("if condition must be bool");
        String thenLabel = freshLabel("then");
        String elseLabel = (i.elseBranch != null) ? freshLabel("else") : null;
        String endLabel = freshLabel("endif");

        body.append("  br i1 ").append(cond.llvmValue()).append(", label %").append(thenLabel)
                .append(", label %").append(elseLabel != null ? elseLabel : endLabel).append("\n");

        body.append(thenLabel).append(":\n");
        pushScope();
        emitStmt(i.thenBranch);
        popScope();
        body.append("  br label %").append(endLabel).append("\n");

        if (i.elseBranch != null) {
            body.append(elseLabel).append(":\n");
            pushScope();
            emitStmt(i.elseBranch);
            popScope();
            body.append("  br label %").append(endLabel).append("\n");
        }

        body.append(endLabel).append(":\n");
    }

    private void emitWhile(While w) {
        String condLabel = freshLabel("while.cond");
        String bodyLabel = freshLabel("while.body");
        String endLabel = freshLabel("while.end");

        body.append("  br label %").append(condLabel).append("\n");
        body.append(condLabel).append(":\n");
        Value cond = evalExpr(w.condition);
        if (cond.type != ValueType.BOOL) throw new IllegalStateException("while condition must be bool");
        body.append("  br i1 ").append(cond.llvmValue()).append(", label %").append(bodyLabel)
                .append(", label %").append(endLabel).append("\n");

        body.append(bodyLabel).append(":\n");
        pushScope();
        emitStmt(w.body);
        popScope();
        body.append("  br label %").append(condLabel).append("\n");

        body.append(endLabel).append(":\n");
    }

    /* ================= Expression evaluation ================= */

    private record Value(ValueType type, String llvmValue) {}

    private Value evalExpr(Expr e) {
        if (e instanceof IntLit lit) return new Value(ValueType.INT, String.valueOf(lit.value));
        if (e instanceof BoolLit lit) return new Value(ValueType.BOOL, lit.value ? "1" : "0");
        if (e instanceof StringLit) throw new IllegalStateException("String literal used outside of cout");
        if (e instanceof Id id) {
            VarInfo info = lookupVar(id.name);
            if (info == null) {
                // Fallback for identifiers that were only implicitly declared via assignment/cin.
                info = ensureImplicitVar(id.name, ValueType.INT);
            }
            String load = fresh("load_" + id.name + "_");
            body.append("  ").append(load).append(" = load ").append(llvmType(info.type)).append(", ptr ")
                    .append(info.alloca).append("\n");
            return new Value(info.type, load);
        }
        if (e instanceof Unary u) {
            Value inner = evalExpr(u.inner);
            return switch (u.op) {
                case NEG -> {
                    if (inner.type != ValueType.INT)
                        throw new IllegalStateException("Unary '-' requires int");
                    String res = fresh("neg");
                    body.append("  ").append(res).append(" = sub i32 0, ").append(inner.llvmValue()).append("\n");
                    yield new Value(ValueType.INT, res);
                }
                case NOT -> {
                    if (inner.type != ValueType.BOOL)
                        throw new IllegalStateException("Logical '!' requires bool");
                    String res = fresh("not");
                    body.append("  ").append(res).append(" = xor i1 ").append(inner.llvmValue()).append(", true\n");
                    yield new Value(ValueType.BOOL, res);
                }
            };
        }
        if (e instanceof Binary b) {
            Value left = evalExpr(b.left);
            Value right = evalExpr(b.right);
            return switch (b.op) {
                case ADD -> arithmetic("add", left, right);
                case SUB -> arithmetic("sub", left, right);
                case MUL -> arithmetic("mul", left, right);
                case DIV -> arithmetic("sdiv", left, right);
                case MOD -> arithmetic("srem", left, right);
                case LT, LTE, GT, GTE -> compare(b.op, left, right);
                case EQ, NEQ -> equality(b.op, left, right);
                case AND -> logical("and", left, right);
                case OR -> logical("or", left, right);
            };
        }
        throw new IllegalStateException("Unsupported expression: " + e.getClass());
    }

    private Value arithmetic(String op, Value left, Value right) {
        if (left.type != ValueType.INT || right.type != ValueType.INT)
            throw new IllegalStateException("Arithmetic ops require ints");
        String res = fresh(op);
        body.append("  ").append(res).append(" = ").append(op).append(" i32 ")
                .append(left.llvmValue()).append(", ").append(right.llvmValue()).append("\n");
        return new Value(ValueType.INT, res);
    }

    private Value compare(Binary.Op op, Value left, Value right) {
        if (left.type != ValueType.INT || right.type != ValueType.INT)
            throw new IllegalStateException("Comparison requires ints");
        String res = fresh("cmp");
        String predicate = switch (op) {
            case LT -> "slt";
            case LTE -> "sle";
            case GT -> "sgt";
            case GTE -> "sge";
            default -> throw new IllegalStateException();
        };
        body.append("  ").append(res).append(" = icmp ").append(predicate).append(" i32 ")
                .append(left.llvmValue()).append(", ").append(right.llvmValue()).append("\n");
        return new Value(ValueType.BOOL, res);
    }

    private Value equality(Binary.Op op, Value left, Value right) {
        if (left.type != right.type)
            throw new IllegalStateException("Equality operands must have same type");
        String res = fresh("eq");
        String pred = (op == Binary.Op.EQ) ? "eq" : "ne";
        String ty = llvmType(left.type);
        body.append("  ").append(res).append(" = icmp ").append(pred).append(' ').append(ty).append(' ')
                .append(left.llvmValue()).append(", ").append(right.llvmValue()).append("\n");
        return new Value(ValueType.BOOL, res);
    }

    private Value logical(String op, Value left, Value right) {
        if (left.type != ValueType.BOOL || right.type != ValueType.BOOL)
            throw new IllegalStateException("Logical ops require bools");
        String res = fresh(op);
        body.append("  ").append(res).append(" = ").append(op).append(" i1 ")
                .append(left.llvmValue()).append(", ").append(right.llvmValue()).append("\n");
        return new Value(ValueType.BOOL, res);
    }

    private void storeValue(Value value, VarInfo target) {
        if (value.type != target.type)
            throw new IllegalStateException("Type mismatch storing to " + target.type);
        body.append("  store ").append(llvmType(target.type)).append(' ').append(value.llvmValue())
                .append(", ptr ").append(target.alloca).append("\n");
    }

    /* ================= helpers ================= */

    private String llvmType(ValueType type) {
        return switch (type) {
            case INT -> "i32";
            case BOOL -> "i1";
            case STRING -> "ptr"; // string literals lowered separately
        };
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

    private int llStringByteLen(String s) {
        return s.getBytes(StandardCharsets.UTF_8).length + 1; // include NUL
    }

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