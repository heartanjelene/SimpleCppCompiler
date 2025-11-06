package com.simplecpp.compiler;

import com.simplecpp.compiler.frontend.Frontend;
import com.simplecpp.compiler.ir.LLVMEmitter;
import com.simplecpp.compiler.optimize.ConstantFolder;
import com.simplecpp.compiler.sema.ReadUsageAnalyzer;
import com.simplecpp.compiler.sema.SemanticAnalyzer;
import com.simplecpp.compiler.util.Diagnostics;
import com.simplecpp.compiler.ast.AstProgram;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class CompilerMain {

    private static void usage() {
        System.out.println("Usage: java -jar SimpleCppCompiler.jar <input.cpp> [-o output.ll]");
    }

    public static void main(String[] args) {
        if (args.length < 1) { usage(); return; }

        String inputPath = args[0];
        String outputPath = null;
        for (int i = 1; i < args.length - 1; i++) {
            if ("-o".equals(args[i])) outputPath = args[i + 1];
        }
        if (outputPath == null) outputPath = deriveLlName(inputPath);

        Diagnostics diags = new Diagnostics();
        try {
            // 1) Parse to AST (fast-fail syntax in compile mode)
            AstProgram program = Frontend.parseToAst(Path.of(inputPath), diags);
            diags.throwIfErrors();

            // 2) Semantic analysis (types, usage)
            SemanticAnalyzer sema = new SemanticAnalyzer(diags);
            sema.analyze(program);
            diags.throwIfErrors();

            // 3) Basic optimization: constant folding
            ConstantFolder.fold(program);

            // 4) Small dataflow: track variables that are ever read
            Set<String> varsEverRead = new ReadUsageAnalyzer().collect(program);

            // 5) IR emission
            LLVMEmitter emitter = new LLVMEmitter(varsEverRead);
            String irModule = emitter.emitModule(program);

            // 6) Write IR to file
            Files.writeString(Path.of(outputPath), irModule);
            System.out.println("Wrote LLVM IR: " + outputPath);
            System.out.println("Next steps:");
            System.out.println("  llc " + outputPath + " -filetype=obj -o build/out.obj");
            System.out.println("  clang build/out.obj -o build/out.exe");
            System.out.println("  .\\build\\out.exe");
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
        } catch (RuntimeException e) {
            // Diagnostics already printed; add a friendly tail.
            System.err.println(e.getMessage());
        }
    }

    private static String deriveLlName(String in) {
        int dot = in.lastIndexOf('.');
        String base = (dot > 0) ? in.substring(0, dot) : in;
        return base + ".ll";
    }
}

