// Generated from MiniCpp.g4 by ANTLR 4.13.1
 package com.simplecpp.compiler.grammar; 
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class MiniCppParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		CIN=1, COUT=2, INT_KW=3, SHIFT_IN=4, SHIFT_OUT=5, PLUS=6, MINUS=7, ASSIGN=8, 
		SEMI=9, LPAREN=10, RPAREN=11, ID=12, INT_LIT=13, STRING_LIT=14, WS=15, 
		LINE_COMMENT=16, BLOCK_COMMENT=17;
	public static final int
		RULE_program = 0, RULE_stmt = 1, RULE_declStmt = 2, RULE_assignStmt = 3, 
		RULE_ioStmt = 4, RULE_cinStmt = 5, RULE_coutStmt = 6, RULE_expr = 7, RULE_addExpr = 8, 
		RULE_unary = 9, RULE_primary = 10;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "stmt", "declStmt", "assignStmt", "ioStmt", "cinStmt", "coutStmt", 
			"expr", "addExpr", "unary", "primary"
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

	@Override
	public String getGrammarFileName() { return "MiniCpp.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public MiniCppParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgramContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(MiniCppParser.EOF, 0); }
		public List<StmtContext> stmt() {
			return getRuleContexts(StmtContext.class);
		}
		public StmtContext stmt(int i) {
			return getRuleContext(StmtContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MiniCppVisitor ) return ((MiniCppVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(25);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 4110L) != 0)) {
				{
				{
				setState(22);
				stmt();
				}
				}
				setState(27);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(28);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StmtContext extends ParserRuleContext {
		public DeclStmtContext declStmt() {
			return getRuleContext(DeclStmtContext.class,0);
		}
		public AssignStmtContext assignStmt() {
			return getRuleContext(AssignStmtContext.class,0);
		}
		public IoStmtContext ioStmt() {
			return getRuleContext(IoStmtContext.class,0);
		}
		public StmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MiniCppVisitor ) return ((MiniCppVisitor<? extends T>)visitor).visitStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StmtContext stmt() throws RecognitionException {
		StmtContext _localctx = new StmtContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_stmt);
		try {
			setState(33);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT_KW:
				enterOuterAlt(_localctx, 1);
				{
				setState(30);
				declStmt();
				}
				break;
			case ID:
				enterOuterAlt(_localctx, 2);
				{
				setState(31);
				assignStmt();
				}
				break;
			case CIN:
			case COUT:
				enterOuterAlt(_localctx, 3);
				{
				setState(32);
				ioStmt();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DeclStmtContext extends ParserRuleContext {
		public TerminalNode INT_KW() { return getToken(MiniCppParser.INT_KW, 0); }
		public TerminalNode ID() { return getToken(MiniCppParser.ID, 0); }
		public TerminalNode SEMI() { return getToken(MiniCppParser.SEMI, 0); }
		public TerminalNode ASSIGN() { return getToken(MiniCppParser.ASSIGN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public DeclStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MiniCppVisitor ) return ((MiniCppVisitor<? extends T>)visitor).visitDeclStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DeclStmtContext declStmt() throws RecognitionException {
		DeclStmtContext _localctx = new DeclStmtContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_declStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(35);
			match(INT_KW);
			setState(36);
			match(ID);
			setState(39);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==ASSIGN) {
				{
				setState(37);
				match(ASSIGN);
				setState(38);
				expr();
				}
			}

			setState(41);
			match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AssignStmtContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(MiniCppParser.ID, 0); }
		public TerminalNode ASSIGN() { return getToken(MiniCppParser.ASSIGN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode SEMI() { return getToken(MiniCppParser.SEMI, 0); }
		public AssignStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MiniCppVisitor ) return ((MiniCppVisitor<? extends T>)visitor).visitAssignStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignStmtContext assignStmt() throws RecognitionException {
		AssignStmtContext _localctx = new AssignStmtContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_assignStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(43);
			match(ID);
			setState(44);
			match(ASSIGN);
			setState(45);
			expr();
			setState(46);
			match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IoStmtContext extends ParserRuleContext {
		public CinStmtContext cinStmt() {
			return getRuleContext(CinStmtContext.class,0);
		}
		public CoutStmtContext coutStmt() {
			return getRuleContext(CoutStmtContext.class,0);
		}
		public IoStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ioStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MiniCppVisitor ) return ((MiniCppVisitor<? extends T>)visitor).visitIoStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IoStmtContext ioStmt() throws RecognitionException {
		IoStmtContext _localctx = new IoStmtContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_ioStmt);
		try {
			setState(50);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case CIN:
				enterOuterAlt(_localctx, 1);
				{
				setState(48);
				cinStmt();
				}
				break;
			case COUT:
				enterOuterAlt(_localctx, 2);
				{
				setState(49);
				coutStmt();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CinStmtContext extends ParserRuleContext {
		public TerminalNode CIN() { return getToken(MiniCppParser.CIN, 0); }
		public TerminalNode SEMI() { return getToken(MiniCppParser.SEMI, 0); }
		public List<TerminalNode> SHIFT_IN() { return getTokens(MiniCppParser.SHIFT_IN); }
		public TerminalNode SHIFT_IN(int i) {
			return getToken(MiniCppParser.SHIFT_IN, i);
		}
		public List<TerminalNode> ID() { return getTokens(MiniCppParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(MiniCppParser.ID, i);
		}
		public CinStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cinStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MiniCppVisitor ) return ((MiniCppVisitor<? extends T>)visitor).visitCinStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CinStmtContext cinStmt() throws RecognitionException {
		CinStmtContext _localctx = new CinStmtContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_cinStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			match(CIN);
			setState(55); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(53);
				match(SHIFT_IN);
				setState(54);
				match(ID);
				}
				}
				setState(57); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==SHIFT_IN );
			setState(59);
			match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CoutStmtContext extends ParserRuleContext {
		public TerminalNode COUT() { return getToken(MiniCppParser.COUT, 0); }
		public TerminalNode SEMI() { return getToken(MiniCppParser.SEMI, 0); }
		public List<TerminalNode> SHIFT_OUT() { return getTokens(MiniCppParser.SHIFT_OUT); }
		public TerminalNode SHIFT_OUT(int i) {
			return getToken(MiniCppParser.SHIFT_OUT, i);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public CoutStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_coutStmt; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MiniCppVisitor ) return ((MiniCppVisitor<? extends T>)visitor).visitCoutStmt(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CoutStmtContext coutStmt() throws RecognitionException {
		CoutStmtContext _localctx = new CoutStmtContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_coutStmt);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(61);
			match(COUT);
			setState(64); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(62);
				match(SHIFT_OUT);
				setState(63);
				expr();
				}
				}
				setState(66); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==SHIFT_OUT );
			setState(68);
			match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExprContext extends ParserRuleContext {
		public AddExprContext addExpr() {
			return getRuleContext(AddExprContext.class,0);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MiniCppVisitor ) return ((MiniCppVisitor<? extends T>)visitor).visitExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		ExprContext _localctx = new ExprContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70);
			addExpr();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AddExprContext extends ParserRuleContext {
		public List<UnaryContext> unary() {
			return getRuleContexts(UnaryContext.class);
		}
		public UnaryContext unary(int i) {
			return getRuleContext(UnaryContext.class,i);
		}
		public List<TerminalNode> PLUS() { return getTokens(MiniCppParser.PLUS); }
		public TerminalNode PLUS(int i) {
			return getToken(MiniCppParser.PLUS, i);
		}
		public AddExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_addExpr; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MiniCppVisitor ) return ((MiniCppVisitor<? extends T>)visitor).visitAddExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AddExprContext addExpr() throws RecognitionException {
		AddExprContext _localctx = new AddExprContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_addExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			unary();
			setState(77);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PLUS) {
				{
				{
				setState(73);
				match(PLUS);
				setState(74);
				unary();
				}
				}
				setState(79);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class UnaryContext extends ParserRuleContext {
		public TerminalNode MINUS() { return getToken(MiniCppParser.MINUS, 0); }
		public UnaryContext unary() {
			return getRuleContext(UnaryContext.class,0);
		}
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public UnaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_unary; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MiniCppVisitor ) return ((MiniCppVisitor<? extends T>)visitor).visitUnary(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnaryContext unary() throws RecognitionException {
		UnaryContext _localctx = new UnaryContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_unary);
		try {
			setState(83);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case MINUS:
				enterOuterAlt(_localctx, 1);
				{
				setState(80);
				match(MINUS);
				setState(81);
				unary();
				}
				break;
			case LPAREN:
			case ID:
			case INT_LIT:
			case STRING_LIT:
				enterOuterAlt(_localctx, 2);
				{
				setState(82);
				primary();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PrimaryContext extends ParserRuleContext {
		public TerminalNode INT_LIT() { return getToken(MiniCppParser.INT_LIT, 0); }
		public TerminalNode STRING_LIT() { return getToken(MiniCppParser.STRING_LIT, 0); }
		public TerminalNode ID() { return getToken(MiniCppParser.ID, 0); }
		public TerminalNode LPAREN() { return getToken(MiniCppParser.LPAREN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(MiniCppParser.RPAREN, 0); }
		public PrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primary; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MiniCppVisitor ) return ((MiniCppVisitor<? extends T>)visitor).visitPrimary(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimaryContext primary() throws RecognitionException {
		PrimaryContext _localctx = new PrimaryContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_primary);
		try {
			setState(92);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case INT_LIT:
				enterOuterAlt(_localctx, 1);
				{
				setState(85);
				match(INT_LIT);
				}
				break;
			case STRING_LIT:
				enterOuterAlt(_localctx, 2);
				{
				setState(86);
				match(STRING_LIT);
				}
				break;
			case ID:
				enterOuterAlt(_localctx, 3);
				{
				setState(87);
				match(ID);
				}
				break;
			case LPAREN:
				enterOuterAlt(_localctx, 4);
				{
				setState(88);
				match(LPAREN);
				setState(89);
				expr();
				setState(90);
				match(RPAREN);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0011_\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0001\u0000\u0005\u0000\u0018"+
		"\b\u0000\n\u0000\f\u0000\u001b\t\u0000\u0001\u0000\u0001\u0000\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0003\u0001\"\b\u0001\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0003\u0002(\b\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004"+
		"\u0001\u0004\u0003\u00043\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0004\u00058\b\u0005\u000b\u0005\f\u00059\u0001\u0005\u0001\u0005\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0004\u0006A\b\u0006\u000b\u0006\f\u0006"+
		"B\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001"+
		"\b\u0005\bL\b\b\n\b\f\bO\t\b\u0001\t\u0001\t\u0001\t\u0003\tT\b\t\u0001"+
		"\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0003\n]\b\n\u0001"+
		"\n\u0000\u0000\u000b\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014"+
		"\u0000\u0000_\u0000\u0019\u0001\u0000\u0000\u0000\u0002!\u0001\u0000\u0000"+
		"\u0000\u0004#\u0001\u0000\u0000\u0000\u0006+\u0001\u0000\u0000\u0000\b"+
		"2\u0001\u0000\u0000\u0000\n4\u0001\u0000\u0000\u0000\f=\u0001\u0000\u0000"+
		"\u0000\u000eF\u0001\u0000\u0000\u0000\u0010H\u0001\u0000\u0000\u0000\u0012"+
		"S\u0001\u0000\u0000\u0000\u0014\\\u0001\u0000\u0000\u0000\u0016\u0018"+
		"\u0003\u0002\u0001\u0000\u0017\u0016\u0001\u0000\u0000\u0000\u0018\u001b"+
		"\u0001\u0000\u0000\u0000\u0019\u0017\u0001\u0000\u0000\u0000\u0019\u001a"+
		"\u0001\u0000\u0000\u0000\u001a\u001c\u0001\u0000\u0000\u0000\u001b\u0019"+
		"\u0001\u0000\u0000\u0000\u001c\u001d\u0005\u0000\u0000\u0001\u001d\u0001"+
		"\u0001\u0000\u0000\u0000\u001e\"\u0003\u0004\u0002\u0000\u001f\"\u0003"+
		"\u0006\u0003\u0000 \"\u0003\b\u0004\u0000!\u001e\u0001\u0000\u0000\u0000"+
		"!\u001f\u0001\u0000\u0000\u0000! \u0001\u0000\u0000\u0000\"\u0003\u0001"+
		"\u0000\u0000\u0000#$\u0005\u0003\u0000\u0000$\'\u0005\f\u0000\u0000%&"+
		"\u0005\b\u0000\u0000&(\u0003\u000e\u0007\u0000\'%\u0001\u0000\u0000\u0000"+
		"\'(\u0001\u0000\u0000\u0000()\u0001\u0000\u0000\u0000)*\u0005\t\u0000"+
		"\u0000*\u0005\u0001\u0000\u0000\u0000+,\u0005\f\u0000\u0000,-\u0005\b"+
		"\u0000\u0000-.\u0003\u000e\u0007\u0000./\u0005\t\u0000\u0000/\u0007\u0001"+
		"\u0000\u0000\u000003\u0003\n\u0005\u000013\u0003\f\u0006\u000020\u0001"+
		"\u0000\u0000\u000021\u0001\u0000\u0000\u00003\t\u0001\u0000\u0000\u0000"+
		"47\u0005\u0001\u0000\u000056\u0005\u0004\u0000\u000068\u0005\f\u0000\u0000"+
		"75\u0001\u0000\u0000\u000089\u0001\u0000\u0000\u000097\u0001\u0000\u0000"+
		"\u00009:\u0001\u0000\u0000\u0000:;\u0001\u0000\u0000\u0000;<\u0005\t\u0000"+
		"\u0000<\u000b\u0001\u0000\u0000\u0000=@\u0005\u0002\u0000\u0000>?\u0005"+
		"\u0005\u0000\u0000?A\u0003\u000e\u0007\u0000@>\u0001\u0000\u0000\u0000"+
		"AB\u0001\u0000\u0000\u0000B@\u0001\u0000\u0000\u0000BC\u0001\u0000\u0000"+
		"\u0000CD\u0001\u0000\u0000\u0000DE\u0005\t\u0000\u0000E\r\u0001\u0000"+
		"\u0000\u0000FG\u0003\u0010\b\u0000G\u000f\u0001\u0000\u0000\u0000HM\u0003"+
		"\u0012\t\u0000IJ\u0005\u0006\u0000\u0000JL\u0003\u0012\t\u0000KI\u0001"+
		"\u0000\u0000\u0000LO\u0001\u0000\u0000\u0000MK\u0001\u0000\u0000\u0000"+
		"MN\u0001\u0000\u0000\u0000N\u0011\u0001\u0000\u0000\u0000OM\u0001\u0000"+
		"\u0000\u0000PQ\u0005\u0007\u0000\u0000QT\u0003\u0012\t\u0000RT\u0003\u0014"+
		"\n\u0000SP\u0001\u0000\u0000\u0000SR\u0001\u0000\u0000\u0000T\u0013\u0001"+
		"\u0000\u0000\u0000U]\u0005\r\u0000\u0000V]\u0005\u000e\u0000\u0000W]\u0005"+
		"\f\u0000\u0000XY\u0005\n\u0000\u0000YZ\u0003\u000e\u0007\u0000Z[\u0005"+
		"\u000b\u0000\u0000[]\u0001\u0000\u0000\u0000\\U\u0001\u0000\u0000\u0000"+
		"\\V\u0001\u0000\u0000\u0000\\W\u0001\u0000\u0000\u0000\\X\u0001\u0000"+
		"\u0000\u0000]\u0015\u0001\u0000\u0000\u0000\t\u0019!\'29BMS\\";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}