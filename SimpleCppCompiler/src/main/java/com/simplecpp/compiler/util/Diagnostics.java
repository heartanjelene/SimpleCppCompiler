package com.simplecpp.compiler.util;

import java.util.ArrayList;
import java.util.List;

public class Diagnostics {

    public static class Message {
        public final int line, col;
        public final String text;
        public Message(int line, int col, String text) {
            this.line = line; this.col = col; this.text = text;
        }
        @Override public String toString() { return "(" + line + ":" + col + ") " + text; }
    }

    private final List<Message> errors = new ArrayList<>();

    public void error(int line, int col, String msg) { errors.add(new Message(line, col, msg)); }
    public boolean hasErrors() { return !errors.isEmpty(); }

    public List<Message> errors() { return List.copyOf(errors); }

    public void printErrors() { for (Message m : errors) System.err.println(m); }

    public void throwIfErrors() {
        if (hasErrors()) {
            printErrors();
            throw new RuntimeException("Compilation failed with errors.");
        }
    }
}