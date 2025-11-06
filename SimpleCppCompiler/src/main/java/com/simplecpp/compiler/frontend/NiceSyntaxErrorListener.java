package com.simplecpp.compiler.frontend;

import com.simplecpp.compiler.util.Diagnostics;
import org.antlr.v4.runtime.*;
// IntervalSet is not in the core runtime package; import it explicitly:
import org.antlr.v4.runtime.misc.IntervalSet;

import java.util.ArrayList;
import java.util.List;

public class NiceSyntaxErrorListener extends BaseErrorListener {
    private final Diagnostics diags;

    public NiceSyntaxErrorListener(Diagnostics diags) { this.diags = diags; }

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                            int line, int charPositionInLine, String msg, RecognitionException e) {
        List<String> expected = new ArrayList<>();
        if (recognizer instanceof Parser p) {
            IntervalSet set = p.getExpectedTokens();
            Vocabulary v = p.getVocabulary();
            int count = 0;
            for (int t : set.toArray()) {
                expected.add(v.getDisplayName(t));
                if (++count >= 6) break; // cap hint list so it stays readable
            }
        }
        String hint = expected.isEmpty() ? "" : " Expected: " + String.join(", ", expected);
        diags.error(line, charPositionInLine, "Syntax error: " + msg + "." + hint);
    }
}