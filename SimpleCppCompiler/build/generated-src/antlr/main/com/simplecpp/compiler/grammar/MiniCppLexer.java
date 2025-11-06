// Generated from MiniCpp.g4 by ANTLR 4.13.1
 package com.simplecpp.compiler.grammar; 
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class MiniCppLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		CIN=1, COUT=2, INT_KW=3, SHIFT_IN=4, SHIFT_OUT=5, PLUS=6, MINUS=7, ASSIGN=8, 
		SEMI=9, LPAREN=10, RPAREN=11, ID=12, INT_LIT=13, STRING_LIT=14, WS=15, 
		LINE_COMMENT=16, BLOCK_COMMENT=17;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"CIN", "COUT", "INT_KW", "SHIFT_IN", "SHIFT_OUT", "PLUS", "MINUS", "ASSIGN", 
			"SEMI", "LPAREN", "RPAREN", "ID", "INT_LIT", "STRING_LIT", "WS", "LINE_COMMENT", 
			"BLOCK_COMMENT"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'cin'", "'cout'", "'int'", "'>>'", "'<<'", "'+'", "'-'", "'='", 
			"';'", "'('", "')'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "CIN", "COUT", "INT_KW", "SHIFT_IN", "SHIFT_OUT", "PLUS", "MINUS", 
			"ASSIGN", "SEMI", "LPAREN", "RPAREN", "ID", "INT_LIT", "STRING_LIT", 
			"WS", "LINE_COMMENT", "BLOCK_COMMENT"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public MiniCppLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "MiniCpp.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\u0011y\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002\u0001"+
		"\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004"+
		"\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007"+
		"\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b"+
		"\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002"+
		"\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0001\u0000\u0001\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001"+
		"\u0003\u0001\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001"+
		"\u0005\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\b\u0001\b"+
		"\u0001\t\u0001\t\u0001\n\u0001\n\u0001\u000b\u0001\u000b\u0005\u000bE"+
		"\b\u000b\n\u000b\f\u000bH\t\u000b\u0001\f\u0004\fK\b\f\u000b\f\f\fL\u0001"+
		"\r\u0001\r\u0001\r\u0001\r\u0005\rS\b\r\n\r\f\rV\t\r\u0001\r\u0001\r\u0001"+
		"\u000e\u0004\u000e[\b\u000e\u000b\u000e\f\u000e\\\u0001\u000e\u0001\u000e"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0005\u000fe\b\u000f"+
		"\n\u000f\f\u000fh\t\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0001\u0010\u0005\u0010p\b\u0010\n\u0010\f\u0010s\t\u0010"+
		"\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001q\u0000"+
		"\u0011\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004\t\u0005\u000b\u0006"+
		"\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017\f\u0019\r\u001b\u000e"+
		"\u001d\u000f\u001f\u0010!\u0011\u0001\u0000\u0006\u0003\u0000AZ__az\u0004"+
		"\u000009AZ__az\u0001\u000009\u0002\u0000\"\"\\\\\u0003\u0000\t\n\r\r "+
		" \u0002\u0000\n\n\r\r\u007f\u0000\u0001\u0001\u0000\u0000\u0000\u0000"+
		"\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001\u0000\u0000\u0000\u0000"+
		"\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000\u0000\u0000\u0000\u000b"+
		"\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000\u0000\u0000\u000f\u0001"+
		"\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000\u0000\u0000\u0013\u0001"+
		"\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000\u0000\u0000\u0017\u0001"+
		"\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000\u0000\u0000\u001b\u0001"+
		"\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000\u0000\u0000\u001f\u0001"+
		"\u0000\u0000\u0000\u0000!\u0001\u0000\u0000\u0000\u0001#\u0001\u0000\u0000"+
		"\u0000\u0003\'\u0001\u0000\u0000\u0000\u0005,\u0001\u0000\u0000\u0000"+
		"\u00070\u0001\u0000\u0000\u0000\t3\u0001\u0000\u0000\u0000\u000b6\u0001"+
		"\u0000\u0000\u0000\r8\u0001\u0000\u0000\u0000\u000f:\u0001\u0000\u0000"+
		"\u0000\u0011<\u0001\u0000\u0000\u0000\u0013>\u0001\u0000\u0000\u0000\u0015"+
		"@\u0001\u0000\u0000\u0000\u0017B\u0001\u0000\u0000\u0000\u0019J\u0001"+
		"\u0000\u0000\u0000\u001bN\u0001\u0000\u0000\u0000\u001dZ\u0001\u0000\u0000"+
		"\u0000\u001f`\u0001\u0000\u0000\u0000!k\u0001\u0000\u0000\u0000#$\u0005"+
		"c\u0000\u0000$%\u0005i\u0000\u0000%&\u0005n\u0000\u0000&\u0002\u0001\u0000"+
		"\u0000\u0000\'(\u0005c\u0000\u0000()\u0005o\u0000\u0000)*\u0005u\u0000"+
		"\u0000*+\u0005t\u0000\u0000+\u0004\u0001\u0000\u0000\u0000,-\u0005i\u0000"+
		"\u0000-.\u0005n\u0000\u0000./\u0005t\u0000\u0000/\u0006\u0001\u0000\u0000"+
		"\u000001\u0005>\u0000\u000012\u0005>\u0000\u00002\b\u0001\u0000\u0000"+
		"\u000034\u0005<\u0000\u000045\u0005<\u0000\u00005\n\u0001\u0000\u0000"+
		"\u000067\u0005+\u0000\u00007\f\u0001\u0000\u0000\u000089\u0005-\u0000"+
		"\u00009\u000e\u0001\u0000\u0000\u0000:;\u0005=\u0000\u0000;\u0010\u0001"+
		"\u0000\u0000\u0000<=\u0005;\u0000\u0000=\u0012\u0001\u0000\u0000\u0000"+
		">?\u0005(\u0000\u0000?\u0014\u0001\u0000\u0000\u0000@A\u0005)\u0000\u0000"+
		"A\u0016\u0001\u0000\u0000\u0000BF\u0007\u0000\u0000\u0000CE\u0007\u0001"+
		"\u0000\u0000DC\u0001\u0000\u0000\u0000EH\u0001\u0000\u0000\u0000FD\u0001"+
		"\u0000\u0000\u0000FG\u0001\u0000\u0000\u0000G\u0018\u0001\u0000\u0000"+
		"\u0000HF\u0001\u0000\u0000\u0000IK\u0007\u0002\u0000\u0000JI\u0001\u0000"+
		"\u0000\u0000KL\u0001\u0000\u0000\u0000LJ\u0001\u0000\u0000\u0000LM\u0001"+
		"\u0000\u0000\u0000M\u001a\u0001\u0000\u0000\u0000NT\u0005\"\u0000\u0000"+
		"OP\u0005\\\u0000\u0000PS\t\u0000\u0000\u0000QS\b\u0003\u0000\u0000RO\u0001"+
		"\u0000\u0000\u0000RQ\u0001\u0000\u0000\u0000SV\u0001\u0000\u0000\u0000"+
		"TR\u0001\u0000\u0000\u0000TU\u0001\u0000\u0000\u0000UW\u0001\u0000\u0000"+
		"\u0000VT\u0001\u0000\u0000\u0000WX\u0005\"\u0000\u0000X\u001c\u0001\u0000"+
		"\u0000\u0000Y[\u0007\u0004\u0000\u0000ZY\u0001\u0000\u0000\u0000[\\\u0001"+
		"\u0000\u0000\u0000\\Z\u0001\u0000\u0000\u0000\\]\u0001\u0000\u0000\u0000"+
		"]^\u0001\u0000\u0000\u0000^_\u0006\u000e\u0000\u0000_\u001e\u0001\u0000"+
		"\u0000\u0000`a\u0005/\u0000\u0000ab\u0005/\u0000\u0000bf\u0001\u0000\u0000"+
		"\u0000ce\b\u0005\u0000\u0000dc\u0001\u0000\u0000\u0000eh\u0001\u0000\u0000"+
		"\u0000fd\u0001\u0000\u0000\u0000fg\u0001\u0000\u0000\u0000gi\u0001\u0000"+
		"\u0000\u0000hf\u0001\u0000\u0000\u0000ij\u0006\u000f\u0000\u0000j \u0001"+
		"\u0000\u0000\u0000kl\u0005/\u0000\u0000lm\u0005*\u0000\u0000mq\u0001\u0000"+
		"\u0000\u0000np\t\u0000\u0000\u0000on\u0001\u0000\u0000\u0000ps\u0001\u0000"+
		"\u0000\u0000qr\u0001\u0000\u0000\u0000qo\u0001\u0000\u0000\u0000rt\u0001"+
		"\u0000\u0000\u0000sq\u0001\u0000\u0000\u0000tu\u0005*\u0000\u0000uv\u0005"+
		"/\u0000\u0000vw\u0001\u0000\u0000\u0000wx\u0006\u0010\u0000\u0000x\"\u0001"+
		"\u0000\u0000\u0000\b\u0000FLRT\\fq\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}