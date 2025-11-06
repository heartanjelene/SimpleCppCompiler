package com.simplecpp.compiler;

import com.simplecpp.compiler.pipeline.CompilerPipeline;
import com.simplecpp.compiler.pipeline.CompilerPipeline.CompilationResult;
import com.simplecpp.compiler.toolchain.NativeToolchain;
import com.simplecpp.compiler.toolchain.NativeToolchain.ToolchainResult;
import com.simplecpp.compiler.util.Diagnostics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

public class CompilerMain {

    private static void usage() {
        System.out.println("Usage: java -jar SimpleCppCompiler.jar <input.cpp> [options]");
        System.out.println("Options:");
        System.out.println("  --emit-llvm <path>   Write LLVM IR to the given file (default: <input>.ll)");
        System.out.println("  -o <path>            Write native executable to the given path (default: <input>[.exe])");
        System.out.println("  --ir-only            Skip native code generation");
    }

    public static void main(String[] args) {
        if (args.length < 1) { usage(); return; }

        Path inputPath = Path.of(args[0]);
        Path irOut = null;
        Path exeOut = null;
        boolean irOnly = false;

        for (int i = 1; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "--emit-llvm" -> {
                    if (i + 1 >= args.length) { System.err.println("Missing path after --emit-llvm"); return; }
                    irOut = Path.of(args[++i]);
                }
                case "-o" -> {
                    if (i + 1 >= args.length) { System.err.println("Missing path after -o"); return; }
                    exeOut = Path.of(args[++i]);
                }
                case "--ir-only" -> irOnly = true;
                default -> {
                    System.err.println("Unknown option: " + arg);
                    usage();
                    return;
                }
            }
        }

        if (irOut == null) irOut = Path.of(deriveLlName(inputPath.toString()));
        if (exeOut == null) exeOut = Path.of(deriveExeName(inputPath.toString()));

        Diagnostics diags = new Diagnostics();
        try {
            CompilationResult result = CompilerPipeline.compile(inputPath, diags);
            String irModule = result.llvmIr();

            if (irOut.getParent() != null) Files.createDirectories(irOut.getParent());
            Files.writeString(irOut, irModule);
            System.out.println("Wrote LLVM IR: " + irOut);

            if (!irOnly) {
                Optional<NativeToolchain> toolchain = NativeToolchain.find();
                if (toolchain.isPresent()) {
                    ToolchainResult nativeResult = toolchain.get().compile(irOut, exeOut);
                    if (!nativeResult.log().isBlank()) {
                        System.out.println(nativeResult.log().trim());
                    }
                    System.out.println("Wrote executable: " + nativeResult.executable());
                    System.out.println("Run it with: " + runnableCommand(nativeResult.executable()));
                } else {
                    System.out.println("LLVM toolchain (llc/clang) not found on PATH; skipping native build.");
                    System.out.println("You can build manually using:");
                    System.out.println(NativeToolchain.manualInstructions(irOut, exeOut));
                }
            }
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Native toolchain invocation was interrupted.");
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

    private static String deriveExeName(String in) {
        int dot = in.lastIndexOf('.');
        String base = (dot > 0) ? in.substring(0, dot) : in;
        boolean windows = System.getProperty("os.name").toLowerCase().contains("win");
        return windows ? base + ".exe" : base;
    }

    private static String runnableCommand(Path executable) {
        return executable.toAbsolutePath().toString();
    }
}