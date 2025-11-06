package com.simplecpp.compiler.tools;

import com.simplecpp.compiler.frontend.Frontend;
import com.simplecpp.compiler.util.Diagnostics;

import java.nio.file.Path;

public class SyntaxCheckMain {

    private static void usage() {
        System.out.println("Usage: java SyntaxCheckMain <input.cpp>");
    }

    public static void main(String[] args) {
        if (args.length != 1) { usage(); return; }

        Diagnostics diags = new Diagnostics();
        try {
            boolean ok = Frontend.checkSyntax(Path.of(args[0]), diags);
            if (ok) System.out.println("No lexical/syntax errors found.");
            else { diags.printErrors(); System.exit(1); }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(2);
        }
    }
}

