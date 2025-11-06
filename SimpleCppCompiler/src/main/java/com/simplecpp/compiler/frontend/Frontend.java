package com.simplecpp.compiler.frontend;

import com.simplecpp.compiler.ast.AstBuilder;
import com.simplecpp.compiler.ast.AstProgram;
import com.simplecpp.compiler.grammar.MiniCppLexer;
import com.simplecpp.compiler.grammar.MiniCppParser;
import com.simplecpp.compiler.util.Diagnostics;
import org.antlr.v4.runtime.*;␊
import org.antlr.v4.runtime.misc.ParseCancellationException;␊

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Frontend {

    public static boolean checkSyntax(Path path, Diagnostics diags) throws IOException {
        String input = Files.readString(path);
        ParserArtifacts artifacts = buildParser(CharStreams.fromString(input), diags, false);
        artifacts.parser().program();
        return !diags.hasErrors();
    }

    public static AstProgram parseToAst(Path path, Diagnostics diags) throws IOException {
        String input = Files.readString(path);
        return parseToAst(CharStreams.fromString(input), diags);
    }

    public static AstProgram parseSource(String source, Diagnostics diags) throws IOException {
        return parseToAst(CharStreams.fromString(source), diags);
    }

    private static AstProgram parseToAst(CharStream chars, Diagnostics diags) throws IOException {
        ParserArtifacts artifacts = buildParser(chars, diags, true);

        MiniCppParser.ProgramContext tree;
        try {
            tree = artifacts.parser().program();
        } catch (ParseCancellationException ex) {
            diags.throwIfErrors();
            throw ex;
        }

        AstBuilder builder = new AstBuilder(artifacts.tokens());
        return builder.visitProgram(tree);
    }

    private static ParserArtifacts buildParser(CharStream chars, Diagnostics diags, boolean bail) {
        MiniCppLexer lexer = createLexer(chars, diags);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MiniCppParser parser = new MiniCppParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new NiceSyntaxErrorListener(diags));
        parser.setErrorHandler(bail ? new BailErrorStrategy() : new DefaultErrorStrategy());
        return new ParserArtifacts(parser, tokens);
    }

    private static MiniCppLexer createLexer(CharStream chars, Diagnostics diags) {
        MiniCppLexer lexer = new MiniCppLexer(chars);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new BaseErrorListener() {
            @Override public void syntaxError(Recognizer<?, ?> r, Object o, int line, int col, String msg, RecognitionException e) {
                diags.error(line, col, "Lexical error: " + msg);
            }
        });
        return lexer;
    }

    private record ParserArtifacts(MiniCppParser parser, CommonTokenStream tokens) {}
}

SimpleCppCompiler/src/main/java/com/simplecpp/compiler/gui/CompilerGuiMain.java
New
+211
-0

package com.simplecpp.compiler.gui;

import com.simplecpp.compiler.pipeline.CompilerPipeline;
import com.simplecpp.compiler.pipeline.CompilerPipeline.CompilationResult;
import com.simplecpp.compiler.toolchain.NativeToolchain;
import com.simplecpp.compiler.toolchain.NativeToolchain.ToolchainResult;
import com.simplecpp.compiler.util.Diagnostics;
import com.simplecpp.compiler.util.Diagnostics.Message;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public final class CompilerGuiMain {

    private final JFrame frame;
    private final JTextArea codeArea;
    private final JTextArea inputArea;
    private final JTextArea outputArea;
    private final JLabel statusLabel;

    private CompilerGuiMain() {
        frame = new JFrame("Mini C++ Compiler");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JEditorPane header = new JEditorPane();
        header.setEditable(false);
        header.setContentType("text/html");
        header.setText(loadHeaderHtml());
        header.setBorder(new EmptyBorder(8, 12, 8, 12));
        frame.add(header, BorderLayout.NORTH);

        codeArea = new JTextArea();
        codeArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        codeArea.setText(sampleProgram());
        JScrollPane codeScroll = new JScrollPane(codeArea);
        codeScroll.setBorder(BorderFactory.createTitledBorder("Source code"));

        inputArea = new JTextArea();
        inputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(BorderFactory.createTitledBorder("Program input (stdin)"));
        inputScroll.setPreferredSize(new Dimension(200, 80));

        outputArea = new JTextArea();
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        outputArea.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Compiler log"));

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 8, 8));
        centerPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
        centerPanel.add(codeScroll);

        JPanel rightColumn = new JPanel(new BorderLayout(8, 8));
        rightColumn.add(inputScroll, BorderLayout.NORTH);
        rightColumn.add(outputScroll, BorderLayout.CENTER);
        centerPanel.add(rightColumn);

        frame.add(centerPanel, BorderLayout.CENTER);

        JButton compileButton = new JButton("Compile");
        compileButton.addActionListener(e -> compile(false));
        JButton runButton = new JButton("Compile & Run");
        runButton.addActionListener(e -> compile(true));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(compileButton);
        buttonPanel.add(runButton);

        statusLabel = new JLabel("Ready");
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBorder(new EmptyBorder(8, 12, 8, 12));
        southPanel.add(buttonPanel, BorderLayout.WEST);
        southPanel.add(statusLabel, BorderLayout.EAST);

        frame.add(southPanel, BorderLayout.SOUTH);
        frame.setSize(1100, 700);
        frame.setLocationRelativeTo(null);
    }

    private void compile(boolean run) {
        statusLabel.setText(run ? "Compiling and running..." : "Compiling...");
        outputArea.setText("");
        String source = codeArea.getText();
        String stdin = inputArea.getText();

        SwingWorker<GuiResult, Void> worker = new SwingWorker<>() {
            @Override protected GuiResult doInBackground() {
                Diagnostics diags = new Diagnostics();
                try {
                    CompilationResult result = CompilerPipeline.compileSource(source, diags);
                    if (diags.hasErrors()) {
                        return GuiResult.error(formatDiagnostics(diags.errors()));
                    }
                    Path tempDir = Files.createTempDirectory("mini-cpp-gui");
                    Path irPath = tempDir.resolve("program.ll");
                    Files.writeString(irPath, result.llvmIr());
                    StringBuilder log = new StringBuilder();
                    log.append("IR written to ").append(irPath).append(System.lineSeparator());

                    Optional<NativeToolchain> toolchain = NativeToolchain.find();
                    if (toolchain.isPresent()) {
                        Path exePath = tempDir.resolve(System.getProperty("os.name").toLowerCase().contains("win") ? "program.exe" : "program");
                        ToolchainResult toolResult = toolchain.get().compile(irPath, exePath);
                        if (!toolResult.log().isBlank()) {
                            log.append(toolResult.log()).append(System.lineSeparator());
                        }
                        log.append("Executable: ").append(toolResult.executable()).append(System.lineSeparator());
                        if (run) {
                            log.append(runExecutable(toolResult.executable(), stdin));
                        }
                    } else {
                        log.append("LLVM toolchain not found; native build skipped.\n");
                        log.append("You can build manually using:\n");
                        log.append(NativeToolchain.manualInstructions(irPath, tempDir.resolve("program")));
                    }
                    return GuiResult.success(log.toString());
                } catch (IOException ex) {
                    return GuiResult.error("I/O error: " + ex.getMessage());
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return GuiResult.error("Native toolchain invocation was interrupted.");
                } catch (RuntimeException ex) {
                    return GuiResult.error(ex.getMessage());
                }
            }

            @Override protected void done() {
                try {
                    GuiResult result = get();
                    outputArea.setText(result.message());
                    statusLabel.setText(result.success() ? "Done" : "Failed");
                } catch (Exception ex) {
                    outputArea.setText("Unexpected error: " + ex.getMessage());
                    statusLabel.setText("Failed");
                }
            }
        };
        worker.execute();
    }

    private String runExecutable(Path executable, String stdin) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(executable.toAbsolutePath().toString());
        pb.redirectErrorStream(true);
        Process process = pb.start();
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8))) {
            if (!stdin.isBlank()) {
                writer.write(stdin);
            }
        }
        String output;
        try (InputStream in = process.getInputStream()) {
            output = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        int exit = process.waitFor();
        return "Program output (exit " + exit + "):\n" + output + System.lineSeparator();
    }

    private static String formatDiagnostics(List<Message> messages) {
        StringBuilder sb = new StringBuilder();
        sb.append("Compilation failed:\n");
        for (Message m : messages) {
            sb.append(m).append(System.lineSeparator());
        }
        return sb.toString();
    }

    private String loadHeaderHtml() {
        try (InputStream in = getClass().getResourceAsStream("/gui/header.html")) {
            if (in == null) return "<html><body><h1>Mini C++ Compiler</h1></body></html>";
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "<html><body><h1>Mini C++ Compiler</h1></body></html>";
        }
    }

    private String sampleProgram() {
        return "int x = 0;\n" +
               "bool ready = true;\n" +
               "while (x < 3) {\n" +
               "    cout << \"Iteration \" << x << \"\\n\";\n" +
               "    x = x + 1;\n" +
               "}\n" +
               "if (ready && x == 3) {\n" +
               "    cout << \"Done\\n\";\n" +
               "}\n";
    }

    private record GuiResult(boolean success, String message) {
        static GuiResult success(String msg) { return new GuiResult(true, msg); }
        static GuiResult error(String msg) { return new GuiResult(false, msg); }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CompilerGuiMain gui = new CompilerGuiMain();
            gui.frame.setVisible(true);
        });
    }
}