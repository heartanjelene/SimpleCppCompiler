package com.simplecpp.compiler.frontend;

import com.simplecpp.compiler.ast.AstBuilder;
import com.simplecpp.compiler.ast.AstProgram;
import com.simplecpp.compiler.grammar.MiniCppLexer;
import com.simplecpp.compiler.grammar.MiniCppParser;
import com.simplecpp.compiler.util.Diagnostics;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.ParseCancellationException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Frontend {

    public static boolean checkSyntax(Path path, Diagnostics diags) throws IOException {
        String input = Files.readString(path);
        CharStream chars = CharStreams.fromString(input);

        MiniCppLexer lexer = new MiniCppLexer(chars);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new BaseErrorListener() {
            @Override public void syntaxError(Recognizer<?, ?> r, Object o, int line, int col, String msg, RecognitionException e) {
                diags.error(line, col, "Lexical error: " + msg);
            }
        });

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MiniCppParser parser = new MiniCppParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new NiceSyntaxErrorListener(diags));
        parser.setErrorHandler(new DefaultErrorStrategy()); // recover & continue

        parser.program(); // walk only
        return !diags.hasErrors();
    }

    public static AstProgram parseToAst(Path path, Diagnostics diags) throws IOException {
        String input = Files.readString(path);
        CharStream chars = CharStreams.fromString(input);

        MiniCppLexer lexer = new MiniCppLexer(chars);
        lexer.removeErrorListeners();
        lexer.addErrorListener(new BaseErrorListener() {
            @Override public void syntaxError(Recognizer<?, ?> r, Object o, int line, int col, String msg, RecognitionException e) {
                diags.error(line, col, "Lexical error: " + msg);
            }
        });

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MiniCppParser parser = new MiniCppParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new NiceSyntaxErrorListener(diags));
        parser.setErrorHandler(new BailErrorStrategy()); // fast-fail; cleaner compile flow

        MiniCppParser.ProgramContext tree;
        try {
            tree = parser.program();
        } catch (ParseCancellationException ex) {
            diags.throwIfErrors(); // will throw if lexer/parser collected anything
            throw ex;
        }

        AstBuilder builder = new AstBuilder(tokens);
        return builder.visitProgram(tree);
    }
}

