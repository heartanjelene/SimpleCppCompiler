package com.simplecpp.compiler;

import com.simplecpp.compiler.pipeline.CompilerPipeline;
import com.simplecpp.compiler.pipeline.CompilerPipeline.CompilationResult;
import com.simplecpp.compiler.toolchain.NativeToolchain;
import com.simplecpp.compiler.toolchain.NativeToolchain.ToolchainResult;
import com.simplecpp.compiler.util.Diagnostics;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ExamplesIT {

    private static Optional<NativeToolchain> toolchain;

    @BeforeAll
    static void detectToolchain() {
        toolchain = NativeToolchain.find();
    }

    @Test
    void compilesAllExamplesToIr() throws IOException {
        List<String> examples = List.of("examples/PL1.cpp", "examples/PL2.cpp", "examples/PL3.cpp");
        for (String example : examples) {
            Diagnostics diags = new Diagnostics();
            CompilationResult result = CompilerPipeline.compile(Path.of(example), diags);
            assertFalse(diags.hasErrors(), () -> "Diagnostics should be empty for " + example);
            assertTrue(result.llvmIr().contains("define i32 @main()"));
        }
    }

    @Test
    void runsReferenceProgramsWhenToolchainAvailable() throws Exception {
        Assumptions.assumeTrue(toolchain.isPresent(), "LLVM toolchain not available");
        assertProgramOutput("examples/PL1.cpp", "5\n", "5");
        assertProgramOutput("examples/PL2.cpp", "", "7");
        assertProgramOutput("examples/PL3.cpp", "", "hello");
    }

    private void assertProgramOutput(String sourcePath, String stdin, String expectedFragment) throws Exception {
        Diagnostics diags = new Diagnostics();
        CompilationResult result = CompilerPipeline.compile(Path.of(sourcePath), diags);
        assertFalse(diags.hasErrors(), () -> "Compilation diagnostics were reported for " + sourcePath);

        Path tempDir = Files.createTempDirectory("mini-cpp-tests");
        Path irPath = tempDir.resolve("program.ll");
        Files.writeString(irPath, result.llvmIr());

        ToolchainResult toolResult = toolchain.get().compile(irPath, tempDir.resolve("program"));
        ProcessBuilder pb = new ProcessBuilder(toolResult.executable().toAbsolutePath().toString());
        pb.redirectErrorStream(true);
        Process process = pb.start();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8))) {
            writer.write(stdin);
        }
        String output;
        try (InputStream in = process.getInputStream()) {
            output = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        int exit = process.waitFor();
        assertEquals(0, exit, "Process should exit cleanly");
        assertTrue(output.contains(expectedFragment), () -> "Output did not contain '" + expectedFragment + "' but was:\n" + output);
    }
}