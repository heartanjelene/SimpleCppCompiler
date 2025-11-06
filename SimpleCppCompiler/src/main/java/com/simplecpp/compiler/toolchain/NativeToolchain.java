package com.simplecpp.compiler.toolchain;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class NativeToolchain {

    public record ToolchainResult(Path executable, String log) {}

    private final String llcCommand;
    private final String clangCommand;
    private final boolean windows;

    private NativeToolchain(String llcCommand, String clangCommand, boolean windows) {
        this.llcCommand = llcCommand;
        this.clangCommand = clangCommand;
        this.windows = windows;
    }

    public static Optional<NativeToolchain> find() {
        boolean windows = System.getProperty("os.name").toLowerCase().contains("win");
        String llc = locate("llc");
        String clang = windows ? locateAny("clang", "clang.exe", "clang-cl") : locate("clang");
        if (llc == null || clang == null) {
            return Optional.empty();
        }
        return Optional.of(new NativeToolchain(llc, clang, windows));
    }

    private static String locate(String command) {
        return commandExists(command) ? command : null;
    }

    private static String locateAny(String... commands) {
        for (String cmd : commands) {
            if (commandExists(cmd)) return cmd;
        }
        return null;
    }

    private static boolean commandExists(String command) {
        ProcessBuilder pb = new ProcessBuilder(command, "--version");
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            try (InputStream in = p.getInputStream()) {
                in.readAllBytes();
            }
            int code = p.waitFor();
            return code == 0;
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public ToolchainResult compile(Path irFile, Path executable) throws IOException, InterruptedException {
        Path exeAbs = executable.toAbsolutePath();
        Path finalExe = exeAbs;
        if (windows && !exeAbs.toString().endsWith(".exe")) {
            finalExe = exeAbs.resolveSibling(exeAbs.getFileName() + ".exe");
        }
        if (finalExe.getParent() != null) Files.createDirectories(finalExe.getParent());
        Path objFile = Files.createTempFile("mini-cpp", windows ? ".obj" : ".o");
        try {
            StringBuilder log = new StringBuilder();
            runCommand(List.of(llcCommand, "-filetype=obj", "-o", objFile.toString(), irFile.toString()), log);
            List<String> clangCmd = new ArrayList<>();
            clangCmd.add(clangCommand);
            clangCmd.add(objFile.toString());
            clangCmd.add("-o");
            clangCmd.add(finalExe.toString());
            runCommand(clangCmd, log);
            return new ToolchainResult(finalExe, log.toString());
        } finally {
            Files.deleteIfExists(objFile);
        }
    }

    private void runCommand(List<String> command, StringBuilder log) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        String output;
        try (InputStream in = p.getInputStream()) {
            output = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        int code = p.waitFor();
        log.append(String.join(" ", command)).append(System.lineSeparator()).append(output);
        if (code != 0) {
            throw new IOException("Command failed (exit " + code + "): " + String.join(" ", command) + "\n" + output);
        }
    }

    public static String manualInstructions(Path irFile, Path exeFile) {
        boolean windows = System.getProperty("os.name").toLowerCase().contains("win");
        String objName = windows ? "build\\out.obj" : "build/out.o";
        String exeName = exeFile.toString();
        StringBuilder sb = new StringBuilder();
        sb.append("  llc ").append(irFile).append(" -filetype=obj -o ").append(objName).append(System.lineSeparator());
        sb.append("  clang ").append(objName).append(" -o ").append(exeName).append(System.lineSeparator());
        if (windows) sb.append("  ").append(exeName).append(System.lineSeparator());
        else sb.append("  ./").append(exeName).append(System.lineSeparator());
        return sb.toString();
    }
}