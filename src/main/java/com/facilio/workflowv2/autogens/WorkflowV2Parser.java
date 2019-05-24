// Generated from /Users/krishna/git/bmsconsole/src/main/java/com/facilio/workflowv2/autogens/WorkflowV2.g4 by ANTLR 4.7.2
package com.facilio.workflowv2.autogens;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class WorkflowV2Parser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, VOID=13, DATA_TYPE_STRING=14, DATA_TYPE_NUMBER=15, 
		DATA_TYPE_DECIMAL=16, DATA_TYPE_BOOLEAN=17, RETURN=18, OR=19, DOT=20, 
		AND=21, EQ=22, NEQ=23, GT=24, LT=25, GTEQ=26, LTEQ=27, PLUS=28, MINUS=29, 
		MULT=30, DIV=31, MOD=32, POW=33, NOT=34, COMMA=35, SEMICOLON=36, COLON=37, 
		ASSIGN=38, OPEN_PARANTHESIS=39, CLOSE_PARANTHESIS=40, OPEN_BRACE=41, CLOSE_BRACE=42, 
		OPEN_BRACKET=43, CLOSE_BRACKET=44, TRUE=45, FALSE=46, NULL=47, IF=48, 
		ELSE=49, FOR_EACH=50, LOG=51, VAR=52, INT=53, FLOAT=54, STRING=55, COMMENT=56, 
		SPACE=57, OTHER=58;
	public static final int
		RULE_parse = 0, RULE_function_block = 1, RULE_function_name_declare = 2, 
		RULE_function_param = 3, RULE_data_type = 4, RULE_block = 5, RULE_statement = 6, 
		RULE_assignment = 7, RULE_if_statement = 8, RULE_condition_block = 9, 
		RULE_statement_block = 10, RULE_for_each_statement = 11, RULE_log = 12, 
		RULE_function_return = 13, RULE_expr = 14, RULE_stand_alone_expr = 15, 
		RULE_atom = 16, RULE_list_opperations = 17, RULE_map_opperations = 18, 
		RULE_db_param = 19, RULE_db_param_criteria = 20, RULE_db_param_field = 21, 
		RULE_db_param_aggr = 22, RULE_db_param_limit = 23, RULE_db_param_range = 24, 
		RULE_db_param_sort = 25, RULE_criteria = 26, RULE_condition = 27, RULE_condition_atom = 28;
	private static String[] makeRuleNames() {
		return new String[] {
			"parse", "function_block", "function_name_declare", "function_param", 
			"data_type", "block", "statement", "assignment", "if_statement", "condition_block", 
			"statement_block", "for_each_statement", "log", "function_return", "expr", 
			"stand_alone_expr", "atom", "list_opperations", "map_opperations", "db_param", 
			"db_param_criteria", "db_param_field", "db_param_aggr", "db_param_limit", 
			"db_param_range", "db_param_sort", "criteria", "condition", "condition_atom"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'in'", "'Module'", "'NameSpace'", "'criteria'", "'field'", "'aggregation'", 
			"'limit'", "'range'", "'to'", "'orderBy'", "'asc'", "'desc'", "'void'", 
			"'String'", "'Number'", "'Decimal'", "'Boolean'", "'return'", "'||'", 
			"'.'", "'&&'", "'=='", "'!='", "'>'", "'<'", "'>='", "'<='", "'+'", "'-'", 
			"'*'", "'/'", "'%'", "'^'", "'!'", "','", "';'", "':'", "'='", "'('", 
			"')'", "'{'", "'}'", "'['", "']'", "'true'", "'false'", "'null'", "'if'", 
			"'else'", "'for each'", "'log'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, "VOID", "DATA_TYPE_STRING", "DATA_TYPE_NUMBER", "DATA_TYPE_DECIMAL", 
			"DATA_TYPE_BOOLEAN", "RETURN", "OR", "DOT", "AND", "EQ", "NEQ", "GT", 
			"LT", "GTEQ", "LTEQ", "PLUS", "MINUS", "MULT", "DIV", "MOD", "POW", "NOT", 
			"COMMA", "SEMICOLON", "COLON", "ASSIGN", "OPEN_PARANTHESIS", "CLOSE_PARANTHESIS", 
			"OPEN_BRACE", "CLOSE_BRACE", "OPEN_BRACKET", "CLOSE_BRACKET", "TRUE", 
			"FALSE", "NULL", "IF", "ELSE", "FOR_EACH", "LOG", "VAR", "INT", "FLOAT", 
			"STRING", "COMMENT", "SPACE", "OTHER"
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
	public String getGrammarFileName() { return "WorkflowV2.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public WorkflowV2Parser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class ParseContext extends ParserRuleContext {
		public Function_blockContext function_block() {
			return getRuleContext(Function_blockContext.class,0);
		}
		public TerminalNode EOF() { return getToken(WorkflowV2Parser.EOF, 0); }
		public ParseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parse; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterParse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitParse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitParse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParseContext parse() throws RecognitionException {
		ParseContext _localctx = new ParseContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_parse);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			function_block();
			setState(59);
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

	public static class Function_blockContext extends ParserRuleContext {
		public Data_typeContext data_type() {
			return getRuleContext(Data_typeContext.class,0);
		}
		public Function_name_declareContext function_name_declare() {
			return getRuleContext(Function_name_declareContext.class,0);
		}
		public TerminalNode OPEN_PARANTHESIS() { return getToken(WorkflowV2Parser.OPEN_PARANTHESIS, 0); }
		public TerminalNode CLOSE_PARANTHESIS() { return getToken(WorkflowV2Parser.CLOSE_PARANTHESIS, 0); }
		public TerminalNode OPEN_BRACE() { return getToken(WorkflowV2Parser.OPEN_BRACE, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public TerminalNode CLOSE_BRACE() { return getToken(WorkflowV2Parser.CLOSE_BRACE, 0); }
		public List<Function_paramContext> function_param() {
			return getRuleContexts(Function_paramContext.class);
		}
		public Function_paramContext function_param(int i) {
			return getRuleContext(Function_paramContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(WorkflowV2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(WorkflowV2Parser.COMMA, i);
		}
		public Function_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterFunction_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitFunction_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitFunction_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Function_blockContext function_block() throws RecognitionException {
		Function_blockContext _localctx = new Function_blockContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_function_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(61);
			data_type();
			setState(62);
			function_name_declare();
			setState(63);
			match(OPEN_PARANTHESIS);
			setState(67);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VOID) | (1L << DATA_TYPE_STRING) | (1L << DATA_TYPE_NUMBER) | (1L << DATA_TYPE_DECIMAL) | (1L << DATA_TYPE_BOOLEAN))) != 0)) {
				{
				{
				setState(64);
				function_param();
				}
				}
				setState(69);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(74);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(70);
				match(COMMA);
				setState(71);
				function_param();
				}
				}
				setState(76);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(77);
			match(CLOSE_PARANTHESIS);
			setState(78);
			match(OPEN_BRACE);
			setState(79);
			block();
			setState(80);
			match(CLOSE_BRACE);
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

	public static class Function_name_declareContext extends ParserRuleContext {
		public TerminalNode VAR() { return getToken(WorkflowV2Parser.VAR, 0); }
		public Function_name_declareContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_name_declare; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterFunction_name_declare(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitFunction_name_declare(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitFunction_name_declare(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Function_name_declareContext function_name_declare() throws RecognitionException {
		Function_name_declareContext _localctx = new Function_name_declareContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_function_name_declare);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			match(VAR);
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

	public static class Function_paramContext extends ParserRuleContext {
		public Data_typeContext data_type() {
			return getRuleContext(Data_typeContext.class,0);
		}
		public TerminalNode VAR() { return getToken(WorkflowV2Parser.VAR, 0); }
		public Function_paramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_param; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterFunction_param(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitFunction_param(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitFunction_param(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Function_paramContext function_param() throws RecognitionException {
		Function_paramContext _localctx = new Function_paramContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_function_param);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84);
			data_type();
			setState(85);
			match(VAR);
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

	public static class Data_typeContext extends ParserRuleContext {
		public Token op;
		public TerminalNode VOID() { return getToken(WorkflowV2Parser.VOID, 0); }
		public TerminalNode DATA_TYPE_STRING() { return getToken(WorkflowV2Parser.DATA_TYPE_STRING, 0); }
		public TerminalNode DATA_TYPE_NUMBER() { return getToken(WorkflowV2Parser.DATA_TYPE_NUMBER, 0); }
		public TerminalNode DATA_TYPE_DECIMAL() { return getToken(WorkflowV2Parser.DATA_TYPE_DECIMAL, 0); }
		public TerminalNode DATA_TYPE_BOOLEAN() { return getToken(WorkflowV2Parser.DATA_TYPE_BOOLEAN, 0); }
		public Data_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_data_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterData_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitData_type(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitData_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Data_typeContext data_type() throws RecognitionException {
		Data_typeContext _localctx = new Data_typeContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_data_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(87);
			((Data_typeContext)_localctx).op = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VOID) | (1L << DATA_TYPE_STRING) | (1L << DATA_TYPE_NUMBER) | (1L << DATA_TYPE_DECIMAL) | (1L << DATA_TYPE_BOOLEAN))) != 0)) ) {
				((Data_typeContext)_localctx).op = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
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

	public static class BlockContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(92);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << T__2) | (1L << RETURN) | (1L << OPEN_PARANTHESIS) | (1L << OPEN_BRACE) | (1L << OPEN_BRACKET) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << IF) | (1L << FOR_EACH) | (1L << LOG) | (1L << VAR) | (1L << INT) | (1L << FLOAT) | (1L << STRING) | (1L << OTHER))) != 0)) {
				{
				{
				setState(89);
				statement();
				}
				}
				setState(94);
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

	public static class StatementContext extends ParserRuleContext {
		public Token OTHER;
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public If_statementContext if_statement() {
			return getRuleContext(If_statementContext.class,0);
		}
		public For_each_statementContext for_each_statement() {
			return getRuleContext(For_each_statementContext.class,0);
		}
		public LogContext log() {
			return getRuleContext(LogContext.class,0);
		}
		public Stand_alone_exprContext stand_alone_expr() {
			return getRuleContext(Stand_alone_exprContext.class,0);
		}
		public TerminalNode SEMICOLON() { return getToken(WorkflowV2Parser.SEMICOLON, 0); }
		public Function_returnContext function_return() {
			return getRuleContext(Function_returnContext.class,0);
		}
		public TerminalNode OTHER() { return getToken(WorkflowV2Parser.OTHER, 0); }
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_statement);
		try {
			setState(105);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(95);
				assignment();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(96);
				if_statement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(97);
				for_each_statement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(98);
				log();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(99);
				stand_alone_expr();
				setState(100);
				match(SEMICOLON);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(102);
				function_return();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(103);
				((StatementContext)_localctx).OTHER = match(OTHER);
				System.err.println("unknown char: " + (((StatementContext)_localctx).OTHER!=null?((StatementContext)_localctx).OTHER.getText():null));
				}
				break;
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

	public static class AssignmentContext extends ParserRuleContext {
		public TerminalNode VAR() { return getToken(WorkflowV2Parser.VAR, 0); }
		public TerminalNode ASSIGN() { return getToken(WorkflowV2Parser.ASSIGN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode SEMICOLON() { return getToken(WorkflowV2Parser.SEMICOLON, 0); }
		public List<TerminalNode> OPEN_BRACKET() { return getTokens(WorkflowV2Parser.OPEN_BRACKET); }
		public TerminalNode OPEN_BRACKET(int i) {
			return getToken(WorkflowV2Parser.OPEN_BRACKET, i);
		}
		public List<AtomContext> atom() {
			return getRuleContexts(AtomContext.class);
		}
		public AtomContext atom(int i) {
			return getRuleContext(AtomContext.class,i);
		}
		public List<TerminalNode> CLOSE_BRACKET() { return getTokens(WorkflowV2Parser.CLOSE_BRACKET); }
		public TerminalNode CLOSE_BRACKET(int i) {
			return getToken(WorkflowV2Parser.CLOSE_BRACKET, i);
		}
		public AssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterAssignment(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitAssignment(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitAssignment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_assignment);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(107);
			match(VAR);
			setState(114);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OPEN_BRACKET) {
				{
				{
				setState(108);
				match(OPEN_BRACKET);
				setState(109);
				atom();
				setState(110);
				match(CLOSE_BRACKET);
				}
				}
				setState(116);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(117);
			match(ASSIGN);
			setState(118);
			expr(0);
			setState(119);
			match(SEMICOLON);
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

	public static class If_statementContext extends ParserRuleContext {
		public List<TerminalNode> IF() { return getTokens(WorkflowV2Parser.IF); }
		public TerminalNode IF(int i) {
			return getToken(WorkflowV2Parser.IF, i);
		}
		public List<Condition_blockContext> condition_block() {
			return getRuleContexts(Condition_blockContext.class);
		}
		public Condition_blockContext condition_block(int i) {
			return getRuleContext(Condition_blockContext.class,i);
		}
		public List<TerminalNode> ELSE() { return getTokens(WorkflowV2Parser.ELSE); }
		public TerminalNode ELSE(int i) {
			return getToken(WorkflowV2Parser.ELSE, i);
		}
		public Statement_blockContext statement_block() {
			return getRuleContext(Statement_blockContext.class,0);
		}
		public If_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_if_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterIf_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitIf_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitIf_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final If_statementContext if_statement() throws RecognitionException {
		If_statementContext _localctx = new If_statementContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_if_statement);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(121);
			match(IF);
			setState(122);
			condition_block();
			setState(128);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(123);
					match(ELSE);
					setState(124);
					match(IF);
					setState(125);
					condition_block();
					}
					} 
				}
				setState(130);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			}
			setState(133);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				setState(131);
				match(ELSE);
				setState(132);
				statement_block();
				}
				break;
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

	public static class Condition_blockContext extends ParserRuleContext {
		public TerminalNode OPEN_PARANTHESIS() { return getToken(WorkflowV2Parser.OPEN_PARANTHESIS, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode CLOSE_PARANTHESIS() { return getToken(WorkflowV2Parser.CLOSE_PARANTHESIS, 0); }
		public Statement_blockContext statement_block() {
			return getRuleContext(Statement_blockContext.class,0);
		}
		public Condition_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterCondition_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitCondition_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitCondition_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Condition_blockContext condition_block() throws RecognitionException {
		Condition_blockContext _localctx = new Condition_blockContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_condition_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(135);
			match(OPEN_PARANTHESIS);
			setState(136);
			expr(0);
			setState(137);
			match(CLOSE_PARANTHESIS);
			setState(138);
			statement_block();
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

	public static class Statement_blockContext extends ParserRuleContext {
		public TerminalNode OPEN_BRACE() { return getToken(WorkflowV2Parser.OPEN_BRACE, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public TerminalNode CLOSE_BRACE() { return getToken(WorkflowV2Parser.CLOSE_BRACE, 0); }
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public Statement_blockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterStatement_block(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitStatement_block(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitStatement_block(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Statement_blockContext statement_block() throws RecognitionException {
		Statement_blockContext _localctx = new Statement_blockContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_statement_block);
		try {
			setState(145);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(140);
				match(OPEN_BRACE);
				setState(141);
				block();
				setState(142);
				match(CLOSE_BRACE);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(144);
				statement();
				}
				break;
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

	public static class For_each_statementContext extends ParserRuleContext {
		public TerminalNode FOR_EACH() { return getToken(WorkflowV2Parser.FOR_EACH, 0); }
		public List<TerminalNode> VAR() { return getTokens(WorkflowV2Parser.VAR); }
		public TerminalNode VAR(int i) {
			return getToken(WorkflowV2Parser.VAR, i);
		}
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public Statement_blockContext statement_block() {
			return getRuleContext(Statement_blockContext.class,0);
		}
		public List<TerminalNode> COMMA() { return getTokens(WorkflowV2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(WorkflowV2Parser.COMMA, i);
		}
		public For_each_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_for_each_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterFor_each_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitFor_each_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitFor_each_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final For_each_statementContext for_each_statement() throws RecognitionException {
		For_each_statementContext _localctx = new For_each_statementContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_for_each_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(147);
			match(FOR_EACH);
			setState(148);
			match(VAR);
			setState(153);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(149);
				match(COMMA);
				setState(150);
				match(VAR);
				}
				}
				setState(155);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(156);
			match(T__0);
			setState(157);
			expr(0);
			setState(158);
			statement_block();
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

	public static class LogContext extends ParserRuleContext {
		public TerminalNode LOG() { return getToken(WorkflowV2Parser.LOG, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode SEMICOLON() { return getToken(WorkflowV2Parser.SEMICOLON, 0); }
		public LogContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_log; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterLog(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitLog(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitLog(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LogContext log() throws RecognitionException {
		LogContext _localctx = new LogContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_log);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(160);
			match(LOG);
			setState(161);
			expr(0);
			setState(162);
			match(SEMICOLON);
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

	public static class Function_returnContext extends ParserRuleContext {
		public TerminalNode RETURN() { return getToken(WorkflowV2Parser.RETURN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode SEMICOLON() { return getToken(WorkflowV2Parser.SEMICOLON, 0); }
		public Function_returnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_return; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterFunction_return(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitFunction_return(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitFunction_return(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Function_returnContext function_return() throws RecognitionException {
		Function_returnContext _localctx = new Function_returnContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_function_return);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(164);
			match(RETURN);
			setState(165);
			expr(0);
			setState(166);
			match(SEMICOLON);
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

	public static class ExprContext extends ParserRuleContext {
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
	 
		public ExprContext() { }
		public void copyFrom(ExprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class DbParamInitializationContext extends ExprContext {
		public Db_paramContext db_param() {
			return getRuleContext(Db_paramContext.class,0);
		}
		public DbParamInitializationContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterDbParamInitialization(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitDbParamInitialization(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitDbParamInitialization(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NotExprContext extends ExprContext {
		public TerminalNode NOT() { return getToken(WorkflowV2Parser.NOT, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public NotExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterNotExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitNotExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitNotExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArithmeticFirstPrecedenceExprContext extends ExprContext {
		public Token op;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode MULT() { return getToken(WorkflowV2Parser.MULT, 0); }
		public TerminalNode DIV() { return getToken(WorkflowV2Parser.DIV, 0); }
		public TerminalNode MOD() { return getToken(WorkflowV2Parser.MOD, 0); }
		public ArithmeticFirstPrecedenceExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterArithmeticFirstPrecedenceExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitArithmeticFirstPrecedenceExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitArithmeticFirstPrecedenceExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class UnaryMinusExprContext extends ExprContext {
		public TerminalNode MINUS() { return getToken(WorkflowV2Parser.MINUS, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public UnaryMinusExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterUnaryMinusExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitUnaryMinusExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitUnaryMinusExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StandAloneStatementsContext extends ExprContext {
		public Stand_alone_exprContext stand_alone_expr() {
			return getRuleContext(Stand_alone_exprContext.class,0);
		}
		public StandAloneStatementsContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterStandAloneStatements(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitStandAloneStatements(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitStandAloneStatements(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ArithmeticSecondPrecedenceExprContext extends ExprContext {
		public Token op;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode PLUS() { return getToken(WorkflowV2Parser.PLUS, 0); }
		public TerminalNode MINUS() { return getToken(WorkflowV2Parser.MINUS, 0); }
		public ArithmeticSecondPrecedenceExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterArithmeticSecondPrecedenceExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitArithmeticSecondPrecedenceExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitArithmeticSecondPrecedenceExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BooleanExprContext extends ExprContext {
		public Token op;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode AND() { return getToken(WorkflowV2Parser.AND, 0); }
		public TerminalNode OR() { return getToken(WorkflowV2Parser.OR, 0); }
		public BooleanExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterBooleanExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitBooleanExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitBooleanExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AtomExprContext extends ExprContext {
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public AtomExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterAtomExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitAtomExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitAtomExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class CriteriaInitializationContext extends ExprContext {
		public CriteriaContext criteria() {
			return getRuleContext(CriteriaContext.class,0);
		}
		public CriteriaInitializationContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterCriteriaInitialization(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitCriteriaInitialization(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitCriteriaInitialization(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class RelationalExprContext extends ExprContext {
		public Token op;
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode LTEQ() { return getToken(WorkflowV2Parser.LTEQ, 0); }
		public TerminalNode GTEQ() { return getToken(WorkflowV2Parser.GTEQ, 0); }
		public TerminalNode LT() { return getToken(WorkflowV2Parser.LT, 0); }
		public TerminalNode GT() { return getToken(WorkflowV2Parser.GT, 0); }
		public TerminalNode EQ() { return getToken(WorkflowV2Parser.EQ, 0); }
		public TerminalNode NEQ() { return getToken(WorkflowV2Parser.NEQ, 0); }
		public RelationalExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterRelationalExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitRelationalExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitRelationalExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 28;
		enterRecursionRule(_localctx, 28, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(177);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				_localctx = new UnaryMinusExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(169);
				match(MINUS);
				setState(170);
				expr(10);
				}
				break;
			case 2:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(171);
				match(NOT);
				setState(172);
				expr(9);
				}
				break;
			case 3:
				{
				_localctx = new StandAloneStatementsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(173);
				stand_alone_expr();
				}
				break;
			case 4:
				{
				_localctx = new AtomExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(174);
				atom();
				}
				break;
			case 5:
				{
				_localctx = new DbParamInitializationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(175);
				db_param();
				}
				break;
			case 6:
				{
				_localctx = new CriteriaInitializationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(176);
				criteria();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(193);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(191);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
					case 1:
						{
						_localctx = new ArithmeticFirstPrecedenceExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(179);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(180);
						((ArithmeticFirstPrecedenceExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << MULT) | (1L << DIV) | (1L << MOD))) != 0)) ) {
							((ArithmeticFirstPrecedenceExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(181);
						expr(9);
						}
						break;
					case 2:
						{
						_localctx = new ArithmeticSecondPrecedenceExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(182);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(183);
						((ArithmeticSecondPrecedenceExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==PLUS || _la==MINUS) ) {
							((ArithmeticSecondPrecedenceExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(184);
						expr(8);
						}
						break;
					case 3:
						{
						_localctx = new RelationalExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(185);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(186);
						((RelationalExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EQ) | (1L << NEQ) | (1L << GT) | (1L << LT) | (1L << GTEQ) | (1L << LTEQ))) != 0)) ) {
							((RelationalExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(187);
						expr(7);
						}
						break;
					case 4:
						{
						_localctx = new BooleanExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(188);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(189);
						((BooleanExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==OR || _la==AND) ) {
							((BooleanExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(190);
						expr(6);
						}
						break;
					}
					} 
				}
				setState(195);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,11,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Stand_alone_exprContext extends ParserRuleContext {
		public Stand_alone_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stand_alone_expr; }
	 
		public Stand_alone_exprContext() { }
		public void copyFrom(Stand_alone_exprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class CustomModuleInitializationContext extends Stand_alone_exprContext {
		public TerminalNode OPEN_PARANTHESIS() { return getToken(WorkflowV2Parser.OPEN_PARANTHESIS, 0); }
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public TerminalNode CLOSE_PARANTHESIS() { return getToken(WorkflowV2Parser.CLOSE_PARANTHESIS, 0); }
		public CustomModuleInitializationContext(Stand_alone_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterCustomModuleInitialization(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitCustomModuleInitialization(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitCustomModuleInitialization(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NameSpaceInitializationContext extends Stand_alone_exprContext {
		public TerminalNode OPEN_PARANTHESIS() { return getToken(WorkflowV2Parser.OPEN_PARANTHESIS, 0); }
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public TerminalNode CLOSE_PARANTHESIS() { return getToken(WorkflowV2Parser.CLOSE_PARANTHESIS, 0); }
		public NameSpaceInitializationContext(Stand_alone_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterNameSpaceInitialization(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitNameSpaceInitialization(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitNameSpaceInitialization(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ListOppContext extends Stand_alone_exprContext {
		public List_opperationsContext list_opperations() {
			return getRuleContext(List_opperationsContext.class,0);
		}
		public ListOppContext(Stand_alone_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterListOpp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitListOpp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitListOpp(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MapOppsContext extends Stand_alone_exprContext {
		public Map_opperationsContext map_opperations() {
			return getRuleContext(Map_opperationsContext.class,0);
		}
		public MapOppsContext(Stand_alone_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterMapOpps(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitMapOpps(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitMapOpps(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ModuleInitializationContext extends Stand_alone_exprContext {
		public List<TerminalNode> VAR() { return getTokens(WorkflowV2Parser.VAR); }
		public TerminalNode VAR(int i) {
			return getToken(WorkflowV2Parser.VAR, i);
		}
		public List<TerminalNode> OPEN_PARANTHESIS() { return getTokens(WorkflowV2Parser.OPEN_PARANTHESIS); }
		public TerminalNode OPEN_PARANTHESIS(int i) {
			return getToken(WorkflowV2Parser.OPEN_PARANTHESIS, i);
		}
		public List<TerminalNode> CLOSE_PARANTHESIS() { return getTokens(WorkflowV2Parser.CLOSE_PARANTHESIS); }
		public TerminalNode CLOSE_PARANTHESIS(int i) {
			return getToken(WorkflowV2Parser.CLOSE_PARANTHESIS, i);
		}
		public ModuleInitializationContext(Stand_alone_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterModuleInitialization(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitModuleInitialization(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitModuleInitialization(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class DataTypeSpecificFunctionContext extends Stand_alone_exprContext {
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public TerminalNode DOT() { return getToken(WorkflowV2Parser.DOT, 0); }
		public TerminalNode VAR() { return getToken(WorkflowV2Parser.VAR, 0); }
		public TerminalNode OPEN_PARANTHESIS() { return getToken(WorkflowV2Parser.OPEN_PARANTHESIS, 0); }
		public TerminalNode CLOSE_PARANTHESIS() { return getToken(WorkflowV2Parser.CLOSE_PARANTHESIS, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(WorkflowV2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(WorkflowV2Parser.COMMA, i);
		}
		public DataTypeSpecificFunctionContext(Stand_alone_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterDataTypeSpecificFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitDataTypeSpecificFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitDataTypeSpecificFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Stand_alone_exprContext stand_alone_expr() throws RecognitionException {
		Stand_alone_exprContext _localctx = new Stand_alone_exprContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_stand_alone_expr);
		int _la;
		try {
			int _alt;
			setState(234);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				_localctx = new ModuleInitializationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(199); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(196);
						match(VAR);
						setState(197);
						match(OPEN_PARANTHESIS);
						setState(198);
						match(CLOSE_PARANTHESIS);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(201); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				_localctx = new CustomModuleInitializationContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(203);
				match(T__1);
				setState(204);
				match(OPEN_PARANTHESIS);
				setState(205);
				atom();
				setState(206);
				match(CLOSE_PARANTHESIS);
				}
				break;
			case 3:
				_localctx = new NameSpaceInitializationContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(208);
				match(T__2);
				setState(209);
				match(OPEN_PARANTHESIS);
				setState(210);
				atom();
				setState(211);
				match(CLOSE_PARANTHESIS);
				}
				break;
			case 4:
				_localctx = new ListOppContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(213);
				list_opperations();
				}
				break;
			case 5:
				_localctx = new MapOppsContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(214);
				map_opperations();
				}
				break;
			case 6:
				_localctx = new DataTypeSpecificFunctionContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(215);
				atom();
				setState(216);
				match(DOT);
				setState(217);
				match(VAR);
				setState(218);
				match(OPEN_PARANTHESIS);
				setState(222);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << T__2) | (1L << MINUS) | (1L << NOT) | (1L << OPEN_PARANTHESIS) | (1L << OPEN_BRACE) | (1L << OPEN_BRACKET) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << VAR) | (1L << INT) | (1L << FLOAT) | (1L << STRING))) != 0)) {
					{
					{
					setState(219);
					expr(0);
					}
					}
					setState(224);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(229);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(225);
					match(COMMA);
					setState(226);
					expr(0);
					}
					}
					setState(231);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(232);
				match(CLOSE_PARANTHESIS);
				}
				break;
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

	public static class AtomContext extends ParserRuleContext {
		public AtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atom; }
	 
		public AtomContext() { }
		public void copyFrom(AtomContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ParanthesisExprContext extends AtomContext {
		public TerminalNode OPEN_PARANTHESIS() { return getToken(WorkflowV2Parser.OPEN_PARANTHESIS, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode CLOSE_PARANTHESIS() { return getToken(WorkflowV2Parser.CLOSE_PARANTHESIS, 0); }
		public ParanthesisExprContext(AtomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterParanthesisExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitParanthesisExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitParanthesisExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BooleanAtomContext extends AtomContext {
		public TerminalNode TRUE() { return getToken(WorkflowV2Parser.TRUE, 0); }
		public TerminalNode FALSE() { return getToken(WorkflowV2Parser.FALSE, 0); }
		public BooleanAtomContext(AtomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterBooleanAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitBooleanAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitBooleanAtom(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ListSymbolOperationContext extends AtomContext {
		public TerminalNode VAR() { return getToken(WorkflowV2Parser.VAR, 0); }
		public TerminalNode OPEN_BRACKET() { return getToken(WorkflowV2Parser.OPEN_BRACKET, 0); }
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public TerminalNode CLOSE_BRACKET() { return getToken(WorkflowV2Parser.CLOSE_BRACKET, 0); }
		public ListSymbolOperationContext(AtomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterListSymbolOperation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitListSymbolOperation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitListSymbolOperation(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class VarAtomContext extends AtomContext {
		public TerminalNode VAR() { return getToken(WorkflowV2Parser.VAR, 0); }
		public VarAtomContext(AtomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterVarAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitVarAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitVarAtom(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class MapSymbolOperationContext extends AtomContext {
		public List<TerminalNode> VAR() { return getTokens(WorkflowV2Parser.VAR); }
		public TerminalNode VAR(int i) {
			return getToken(WorkflowV2Parser.VAR, i);
		}
		public List<TerminalNode> DOT() { return getTokens(WorkflowV2Parser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(WorkflowV2Parser.DOT, i);
		}
		public MapSymbolOperationContext(AtomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterMapSymbolOperation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitMapSymbolOperation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitMapSymbolOperation(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class StringAtomContext extends AtomContext {
		public TerminalNode STRING() { return getToken(WorkflowV2Parser.STRING, 0); }
		public StringAtomContext(AtomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterStringAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitStringAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitStringAtom(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NumberAtomContext extends AtomContext {
		public TerminalNode INT() { return getToken(WorkflowV2Parser.INT, 0); }
		public TerminalNode FLOAT() { return getToken(WorkflowV2Parser.FLOAT, 0); }
		public NumberAtomContext(AtomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterNumberAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitNumberAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitNumberAtom(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class NullAtomContext extends AtomContext {
		public TerminalNode NULL() { return getToken(WorkflowV2Parser.NULL, 0); }
		public NullAtomContext(AtomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterNullAtom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitNullAtom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitNullAtom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_atom);
		int _la;
		try {
			int _alt;
			setState(260);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				_localctx = new ParanthesisExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(236);
				match(OPEN_PARANTHESIS);
				setState(237);
				expr(0);
				setState(238);
				match(CLOSE_PARANTHESIS);
				}
				break;
			case 2:
				_localctx = new NumberAtomContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(240);
				_la = _input.LA(1);
				if ( !(_la==INT || _la==FLOAT) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case 3:
				_localctx = new BooleanAtomContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(241);
				_la = _input.LA(1);
				if ( !(_la==TRUE || _la==FALSE) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				}
				break;
			case 4:
				_localctx = new StringAtomContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(242);
				match(STRING);
				}
				break;
			case 5:
				_localctx = new NullAtomContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(243);
				match(NULL);
				}
				break;
			case 6:
				_localctx = new VarAtomContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(244);
				match(VAR);
				}
				break;
			case 7:
				_localctx = new ListSymbolOperationContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(245);
				match(VAR);
				setState(246);
				match(OPEN_BRACKET);
				setState(247);
				atom();
				setState(248);
				match(CLOSE_BRACKET);
				}
				break;
			case 8:
				_localctx = new MapSymbolOperationContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(250);
				match(VAR);
				setState(251);
				match(DOT);
				setState(252);
				match(VAR);
				setState(257);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
				while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1 ) {
						{
						{
						setState(253);
						match(DOT);
						setState(254);
						match(VAR);
						}
						} 
					}
					setState(259);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
				}
				}
				break;
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

	public static class List_opperationsContext extends ParserRuleContext {
		public List_opperationsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_list_opperations; }
	 
		public List_opperationsContext() { }
		public void copyFrom(List_opperationsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ListInitialisationContext extends List_opperationsContext {
		public List<TerminalNode> OPEN_BRACKET() { return getTokens(WorkflowV2Parser.OPEN_BRACKET); }
		public TerminalNode OPEN_BRACKET(int i) {
			return getToken(WorkflowV2Parser.OPEN_BRACKET, i);
		}
		public List<TerminalNode> CLOSE_BRACKET() { return getTokens(WorkflowV2Parser.CLOSE_BRACKET); }
		public TerminalNode CLOSE_BRACKET(int i) {
			return getToken(WorkflowV2Parser.CLOSE_BRACKET, i);
		}
		public ListInitialisationContext(List_opperationsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterListInitialisation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitListInitialisation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitListInitialisation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final List_opperationsContext list_opperations() throws RecognitionException {
		List_opperationsContext _localctx = new List_opperationsContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_list_opperations);
		try {
			int _alt;
			_localctx = new ListInitialisationContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(264); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(262);
					match(OPEN_BRACKET);
					setState(263);
					match(CLOSE_BRACKET);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(266); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	public static class Map_opperationsContext extends ParserRuleContext {
		public Map_opperationsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_map_opperations; }
	 
		public Map_opperationsContext() { }
		public void copyFrom(Map_opperationsContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class MapInitialisationContext extends Map_opperationsContext {
		public List<TerminalNode> OPEN_BRACE() { return getTokens(WorkflowV2Parser.OPEN_BRACE); }
		public TerminalNode OPEN_BRACE(int i) {
			return getToken(WorkflowV2Parser.OPEN_BRACE, i);
		}
		public List<TerminalNode> CLOSE_BRACE() { return getTokens(WorkflowV2Parser.CLOSE_BRACE); }
		public TerminalNode CLOSE_BRACE(int i) {
			return getToken(WorkflowV2Parser.CLOSE_BRACE, i);
		}
		public MapInitialisationContext(Map_opperationsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterMapInitialisation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitMapInitialisation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitMapInitialisation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Map_opperationsContext map_opperations() throws RecognitionException {
		Map_opperationsContext _localctx = new Map_opperationsContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_map_opperations);
		try {
			int _alt;
			_localctx = new MapInitialisationContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(270); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(268);
					match(OPEN_BRACE);
					setState(269);
					match(CLOSE_BRACE);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(272); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,19,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
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

	public static class Db_paramContext extends ParserRuleContext {
		public TerminalNode OPEN_BRACE() { return getToken(WorkflowV2Parser.OPEN_BRACE, 0); }
		public Db_param_criteriaContext db_param_criteria() {
			return getRuleContext(Db_param_criteriaContext.class,0);
		}
		public TerminalNode CLOSE_BRACE() { return getToken(WorkflowV2Parser.CLOSE_BRACE, 0); }
		public List<Db_param_fieldContext> db_param_field() {
			return getRuleContexts(Db_param_fieldContext.class);
		}
		public Db_param_fieldContext db_param_field(int i) {
			return getRuleContext(Db_param_fieldContext.class,i);
		}
		public List<Db_param_aggrContext> db_param_aggr() {
			return getRuleContexts(Db_param_aggrContext.class);
		}
		public Db_param_aggrContext db_param_aggr(int i) {
			return getRuleContext(Db_param_aggrContext.class,i);
		}
		public List<Db_param_limitContext> db_param_limit() {
			return getRuleContexts(Db_param_limitContext.class);
		}
		public Db_param_limitContext db_param_limit(int i) {
			return getRuleContext(Db_param_limitContext.class,i);
		}
		public List<Db_param_rangeContext> db_param_range() {
			return getRuleContexts(Db_param_rangeContext.class);
		}
		public Db_param_rangeContext db_param_range(int i) {
			return getRuleContext(Db_param_rangeContext.class,i);
		}
		public List<Db_param_sortContext> db_param_sort() {
			return getRuleContexts(Db_param_sortContext.class);
		}
		public Db_param_sortContext db_param_sort(int i) {
			return getRuleContext(Db_param_sortContext.class,i);
		}
		public Db_paramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_db_param; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterDb_param(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitDb_param(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitDb_param(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Db_paramContext db_param() throws RecognitionException {
		Db_paramContext _localctx = new Db_paramContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_db_param);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(274);
			match(OPEN_BRACE);
			setState(275);
			db_param_criteria();
			setState(279);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__4) {
				{
				{
				setState(276);
				db_param_field();
				}
				}
				setState(281);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(285);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__5) {
				{
				{
				setState(282);
				db_param_aggr();
				}
				}
				setState(287);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(291);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__6) {
				{
				{
				setState(288);
				db_param_limit();
				}
				}
				setState(293);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(297);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__7) {
				{
				{
				setState(294);
				db_param_range();
				}
				}
				setState(299);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(303);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__9) {
				{
				{
				setState(300);
				db_param_sort();
				}
				}
				setState(305);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(306);
			match(CLOSE_BRACE);
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

	public static class Db_param_criteriaContext extends ParserRuleContext {
		public TerminalNode COLON() { return getToken(WorkflowV2Parser.COLON, 0); }
		public CriteriaContext criteria() {
			return getRuleContext(CriteriaContext.class,0);
		}
		public List<TerminalNode> COMMA() { return getTokens(WorkflowV2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(WorkflowV2Parser.COMMA, i);
		}
		public Db_param_criteriaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_db_param_criteria; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterDb_param_criteria(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitDb_param_criteria(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitDb_param_criteria(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Db_param_criteriaContext db_param_criteria() throws RecognitionException {
		Db_param_criteriaContext _localctx = new Db_param_criteriaContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_db_param_criteria);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(308);
			match(T__3);
			setState(309);
			match(COLON);
			setState(310);
			criteria();
			setState(314);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(311);
				match(COMMA);
				}
				}
				setState(316);
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

	public static class Db_param_fieldContext extends ParserRuleContext {
		public TerminalNode COLON() { return getToken(WorkflowV2Parser.COLON, 0); }
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public List<TerminalNode> COMMA() { return getTokens(WorkflowV2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(WorkflowV2Parser.COMMA, i);
		}
		public Db_param_fieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_db_param_field; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterDb_param_field(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitDb_param_field(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitDb_param_field(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Db_param_fieldContext db_param_field() throws RecognitionException {
		Db_param_fieldContext _localctx = new Db_param_fieldContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_db_param_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(317);
			match(T__4);
			setState(318);
			match(COLON);
			setState(319);
			atom();
			setState(323);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(320);
				match(COMMA);
				}
				}
				setState(325);
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

	public static class Db_param_aggrContext extends ParserRuleContext {
		public TerminalNode COLON() { return getToken(WorkflowV2Parser.COLON, 0); }
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public List<TerminalNode> COMMA() { return getTokens(WorkflowV2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(WorkflowV2Parser.COMMA, i);
		}
		public Db_param_aggrContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_db_param_aggr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterDb_param_aggr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitDb_param_aggr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitDb_param_aggr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Db_param_aggrContext db_param_aggr() throws RecognitionException {
		Db_param_aggrContext _localctx = new Db_param_aggrContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_db_param_aggr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(326);
			match(T__5);
			setState(327);
			match(COLON);
			setState(328);
			atom();
			setState(332);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(329);
				match(COMMA);
				}
				}
				setState(334);
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

	public static class Db_param_limitContext extends ParserRuleContext {
		public TerminalNode COLON() { return getToken(WorkflowV2Parser.COLON, 0); }
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public List<TerminalNode> COMMA() { return getTokens(WorkflowV2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(WorkflowV2Parser.COMMA, i);
		}
		public Db_param_limitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_db_param_limit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterDb_param_limit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitDb_param_limit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitDb_param_limit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Db_param_limitContext db_param_limit() throws RecognitionException {
		Db_param_limitContext _localctx = new Db_param_limitContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_db_param_limit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(335);
			match(T__6);
			setState(336);
			match(COLON);
			setState(337);
			atom();
			setState(341);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(338);
				match(COMMA);
				}
				}
				setState(343);
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

	public static class Db_param_rangeContext extends ParserRuleContext {
		public TerminalNode COLON() { return getToken(WorkflowV2Parser.COLON, 0); }
		public List<AtomContext> atom() {
			return getRuleContexts(AtomContext.class);
		}
		public AtomContext atom(int i) {
			return getRuleContext(AtomContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(WorkflowV2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(WorkflowV2Parser.COMMA, i);
		}
		public Db_param_rangeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_db_param_range; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterDb_param_range(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitDb_param_range(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitDb_param_range(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Db_param_rangeContext db_param_range() throws RecognitionException {
		Db_param_rangeContext _localctx = new Db_param_rangeContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_db_param_range);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(344);
			match(T__7);
			setState(345);
			match(COLON);
			setState(346);
			atom();
			setState(347);
			match(T__8);
			setState(348);
			atom();
			setState(352);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(349);
				match(COMMA);
				}
				}
				setState(354);
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

	public static class Db_param_sortContext extends ParserRuleContext {
		public Token op;
		public TerminalNode COLON() { return getToken(WorkflowV2Parser.COLON, 0); }
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public List<TerminalNode> COMMA() { return getTokens(WorkflowV2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(WorkflowV2Parser.COMMA, i);
		}
		public Db_param_sortContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_db_param_sort; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterDb_param_sort(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitDb_param_sort(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitDb_param_sort(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Db_param_sortContext db_param_sort() throws RecognitionException {
		Db_param_sortContext _localctx = new Db_param_sortContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_db_param_sort);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(355);
			match(T__9);
			setState(356);
			match(COLON);
			setState(357);
			atom();
			setState(358);
			((Db_param_sortContext)_localctx).op = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==T__10 || _la==T__11) ) {
				((Db_param_sortContext)_localctx).op = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(362);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(359);
				match(COMMA);
				}
				}
				setState(364);
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

	public static class CriteriaContext extends ParserRuleContext {
		public TerminalNode OPEN_BRACKET() { return getToken(WorkflowV2Parser.OPEN_BRACKET, 0); }
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public TerminalNode CLOSE_BRACKET() { return getToken(WorkflowV2Parser.CLOSE_BRACKET, 0); }
		public CriteriaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_criteria; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterCriteria(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitCriteria(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitCriteria(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CriteriaContext criteria() throws RecognitionException {
		CriteriaContext _localctx = new CriteriaContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_criteria);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(365);
			match(OPEN_BRACKET);
			setState(366);
			condition(0);
			setState(367);
			match(CLOSE_BRACKET);
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

	public static class ConditionContext extends ParserRuleContext {
		public Token op;
		public Condition_atomContext condition_atom() {
			return getRuleContext(Condition_atomContext.class,0);
		}
		public TerminalNode OPEN_PARANTHESIS() { return getToken(WorkflowV2Parser.OPEN_PARANTHESIS, 0); }
		public List<ConditionContext> condition() {
			return getRuleContexts(ConditionContext.class);
		}
		public ConditionContext condition(int i) {
			return getRuleContext(ConditionContext.class,i);
		}
		public TerminalNode CLOSE_PARANTHESIS() { return getToken(WorkflowV2Parser.CLOSE_PARANTHESIS, 0); }
		public TerminalNode AND() { return getToken(WorkflowV2Parser.AND, 0); }
		public TerminalNode OR() { return getToken(WorkflowV2Parser.OR, 0); }
		public ConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionContext condition() throws RecognitionException {
		return condition(0);
	}

	private ConditionContext condition(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ConditionContext _localctx = new ConditionContext(_ctx, _parentState);
		ConditionContext _prevctx = _localctx;
		int _startState = 54;
		enterRecursionRule(_localctx, 54, RULE_condition, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(375);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VAR:
				{
				setState(370);
				condition_atom();
				}
				break;
			case OPEN_PARANTHESIS:
				{
				setState(371);
				match(OPEN_PARANTHESIS);
				setState(372);
				condition(0);
				setState(373);
				match(CLOSE_PARANTHESIS);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(382);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ConditionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_condition);
					setState(377);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(378);
					((ConditionContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==OR || _la==AND) ) {
						((ConditionContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(379);
					condition(3);
					}
					} 
				}
				setState(384);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Condition_atomContext extends ParserRuleContext {
		public Token op;
		public TerminalNode VAR() { return getToken(WorkflowV2Parser.VAR, 0); }
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public TerminalNode LTEQ() { return getToken(WorkflowV2Parser.LTEQ, 0); }
		public TerminalNode GTEQ() { return getToken(WorkflowV2Parser.GTEQ, 0); }
		public TerminalNode LT() { return getToken(WorkflowV2Parser.LT, 0); }
		public TerminalNode GT() { return getToken(WorkflowV2Parser.GT, 0); }
		public TerminalNode EQ() { return getToken(WorkflowV2Parser.EQ, 0); }
		public TerminalNode NEQ() { return getToken(WorkflowV2Parser.NEQ, 0); }
		public Condition_atomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition_atom; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterCondition_atom(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitCondition_atom(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitCondition_atom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Condition_atomContext condition_atom() throws RecognitionException {
		Condition_atomContext _localctx = new Condition_atomContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_condition_atom);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(385);
			match(VAR);
			setState(386);
			((Condition_atomContext)_localctx).op = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << EQ) | (1L << NEQ) | (1L << GT) | (1L << LT) | (1L << GTEQ) | (1L << LTEQ))) != 0)) ) {
				((Condition_atomContext)_localctx).op = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(387);
			atom();
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 14:
			return expr_sempred((ExprContext)_localctx, predIndex);
		case 27:
			return condition_sempred((ConditionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 8);
		case 1:
			return precpred(_ctx, 7);
		case 2:
			return precpred(_ctx, 6);
		case 3:
			return precpred(_ctx, 5);
		}
		return true;
	}
	private boolean condition_sempred(ConditionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 4:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3<\u0188\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\3\2\3\2\3\2\3\3\3\3"+
		"\3\3\3\3\7\3D\n\3\f\3\16\3G\13\3\3\3\3\3\7\3K\n\3\f\3\16\3N\13\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\7\7\7]\n\7\f\7\16\7`\13\7"+
		"\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\5\bl\n\b\3\t\3\t\3\t\3\t\3\t"+
		"\7\ts\n\t\f\t\16\tv\13\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\7\n\u0081"+
		"\n\n\f\n\16\n\u0084\13\n\3\n\3\n\5\n\u0088\n\n\3\13\3\13\3\13\3\13\3\13"+
		"\3\f\3\f\3\f\3\f\3\f\5\f\u0094\n\f\3\r\3\r\3\r\3\r\7\r\u009a\n\r\f\r\16"+
		"\r\u009d\13\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\5\20\u00b4\n\20\3\20\3\20"+
		"\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\7\20\u00c2\n\20\f\20"+
		"\16\20\u00c5\13\20\3\21\3\21\3\21\6\21\u00ca\n\21\r\21\16\21\u00cb\3\21"+
		"\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21\3\21"+
		"\3\21\3\21\7\21\u00df\n\21\f\21\16\21\u00e2\13\21\3\21\3\21\7\21\u00e6"+
		"\n\21\f\21\16\21\u00e9\13\21\3\21\3\21\5\21\u00ed\n\21\3\22\3\22\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22"+
		"\3\22\3\22\7\22\u0102\n\22\f\22\16\22\u0105\13\22\5\22\u0107\n\22\3\23"+
		"\3\23\6\23\u010b\n\23\r\23\16\23\u010c\3\24\3\24\6\24\u0111\n\24\r\24"+
		"\16\24\u0112\3\25\3\25\3\25\7\25\u0118\n\25\f\25\16\25\u011b\13\25\3\25"+
		"\7\25\u011e\n\25\f\25\16\25\u0121\13\25\3\25\7\25\u0124\n\25\f\25\16\25"+
		"\u0127\13\25\3\25\7\25\u012a\n\25\f\25\16\25\u012d\13\25\3\25\7\25\u0130"+
		"\n\25\f\25\16\25\u0133\13\25\3\25\3\25\3\26\3\26\3\26\3\26\7\26\u013b"+
		"\n\26\f\26\16\26\u013e\13\26\3\27\3\27\3\27\3\27\7\27\u0144\n\27\f\27"+
		"\16\27\u0147\13\27\3\30\3\30\3\30\3\30\7\30\u014d\n\30\f\30\16\30\u0150"+
		"\13\30\3\31\3\31\3\31\3\31\7\31\u0156\n\31\f\31\16\31\u0159\13\31\3\32"+
		"\3\32\3\32\3\32\3\32\3\32\7\32\u0161\n\32\f\32\16\32\u0164\13\32\3\33"+
		"\3\33\3\33\3\33\3\33\7\33\u016b\n\33\f\33\16\33\u016e\13\33\3\34\3\34"+
		"\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3\35\5\35\u017a\n\35\3\35\3\35\3\35"+
		"\7\35\u017f\n\35\f\35\16\35\u0182\13\35\3\36\3\36\3\36\3\36\3\36\2\4\36"+
		"8\37\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:\2\n"+
		"\3\2\17\23\3\2 \"\3\2\36\37\3\2\30\35\4\2\25\25\27\27\3\2\678\3\2/\60"+
		"\3\2\r\16\2\u01a0\2<\3\2\2\2\4?\3\2\2\2\6T\3\2\2\2\bV\3\2\2\2\nY\3\2\2"+
		"\2\f^\3\2\2\2\16k\3\2\2\2\20m\3\2\2\2\22{\3\2\2\2\24\u0089\3\2\2\2\26"+
		"\u0093\3\2\2\2\30\u0095\3\2\2\2\32\u00a2\3\2\2\2\34\u00a6\3\2\2\2\36\u00b3"+
		"\3\2\2\2 \u00ec\3\2\2\2\"\u0106\3\2\2\2$\u010a\3\2\2\2&\u0110\3\2\2\2"+
		"(\u0114\3\2\2\2*\u0136\3\2\2\2,\u013f\3\2\2\2.\u0148\3\2\2\2\60\u0151"+
		"\3\2\2\2\62\u015a\3\2\2\2\64\u0165\3\2\2\2\66\u016f\3\2\2\28\u0179\3\2"+
		"\2\2:\u0183\3\2\2\2<=\5\4\3\2=>\7\2\2\3>\3\3\2\2\2?@\5\n\6\2@A\5\6\4\2"+
		"AE\7)\2\2BD\5\b\5\2CB\3\2\2\2DG\3\2\2\2EC\3\2\2\2EF\3\2\2\2FL\3\2\2\2"+
		"GE\3\2\2\2HI\7%\2\2IK\5\b\5\2JH\3\2\2\2KN\3\2\2\2LJ\3\2\2\2LM\3\2\2\2"+
		"MO\3\2\2\2NL\3\2\2\2OP\7*\2\2PQ\7+\2\2QR\5\f\7\2RS\7,\2\2S\5\3\2\2\2T"+
		"U\7\66\2\2U\7\3\2\2\2VW\5\n\6\2WX\7\66\2\2X\t\3\2\2\2YZ\t\2\2\2Z\13\3"+
		"\2\2\2[]\5\16\b\2\\[\3\2\2\2]`\3\2\2\2^\\\3\2\2\2^_\3\2\2\2_\r\3\2\2\2"+
		"`^\3\2\2\2al\5\20\t\2bl\5\22\n\2cl\5\30\r\2dl\5\32\16\2ef\5 \21\2fg\7"+
		"&\2\2gl\3\2\2\2hl\5\34\17\2ij\7<\2\2jl\b\b\1\2ka\3\2\2\2kb\3\2\2\2kc\3"+
		"\2\2\2kd\3\2\2\2ke\3\2\2\2kh\3\2\2\2ki\3\2\2\2l\17\3\2\2\2mt\7\66\2\2"+
		"no\7-\2\2op\5\"\22\2pq\7.\2\2qs\3\2\2\2rn\3\2\2\2sv\3\2\2\2tr\3\2\2\2"+
		"tu\3\2\2\2uw\3\2\2\2vt\3\2\2\2wx\7(\2\2xy\5\36\20\2yz\7&\2\2z\21\3\2\2"+
		"\2{|\7\62\2\2|\u0082\5\24\13\2}~\7\63\2\2~\177\7\62\2\2\177\u0081\5\24"+
		"\13\2\u0080}\3\2\2\2\u0081\u0084\3\2\2\2\u0082\u0080\3\2\2\2\u0082\u0083"+
		"\3\2\2\2\u0083\u0087\3\2\2\2\u0084\u0082\3\2\2\2\u0085\u0086\7\63\2\2"+
		"\u0086\u0088\5\26\f\2\u0087\u0085\3\2\2\2\u0087\u0088\3\2\2\2\u0088\23"+
		"\3\2\2\2\u0089\u008a\7)\2\2\u008a\u008b\5\36\20\2\u008b\u008c\7*\2\2\u008c"+
		"\u008d\5\26\f\2\u008d\25\3\2\2\2\u008e\u008f\7+\2\2\u008f\u0090\5\f\7"+
		"\2\u0090\u0091\7,\2\2\u0091\u0094\3\2\2\2\u0092\u0094\5\16\b\2\u0093\u008e"+
		"\3\2\2\2\u0093\u0092\3\2\2\2\u0094\27\3\2\2\2\u0095\u0096\7\64\2\2\u0096"+
		"\u009b\7\66\2\2\u0097\u0098\7%\2\2\u0098\u009a\7\66\2\2\u0099\u0097\3"+
		"\2\2\2\u009a\u009d\3\2\2\2\u009b\u0099\3\2\2\2\u009b\u009c\3\2\2\2\u009c"+
		"\u009e\3\2\2\2\u009d\u009b\3\2\2\2\u009e\u009f\7\3\2\2\u009f\u00a0\5\36"+
		"\20\2\u00a0\u00a1\5\26\f\2\u00a1\31\3\2\2\2\u00a2\u00a3\7\65\2\2\u00a3"+
		"\u00a4\5\36\20\2\u00a4\u00a5\7&\2\2\u00a5\33\3\2\2\2\u00a6\u00a7\7\24"+
		"\2\2\u00a7\u00a8\5\36\20\2\u00a8\u00a9\7&\2\2\u00a9\35\3\2\2\2\u00aa\u00ab"+
		"\b\20\1\2\u00ab\u00ac\7\37\2\2\u00ac\u00b4\5\36\20\f\u00ad\u00ae\7$\2"+
		"\2\u00ae\u00b4\5\36\20\13\u00af\u00b4\5 \21\2\u00b0\u00b4\5\"\22\2\u00b1"+
		"\u00b4\5(\25\2\u00b2\u00b4\5\66\34\2\u00b3\u00aa\3\2\2\2\u00b3\u00ad\3"+
		"\2\2\2\u00b3\u00af\3\2\2\2\u00b3\u00b0\3\2\2\2\u00b3\u00b1\3\2\2\2\u00b3"+
		"\u00b2\3\2\2\2\u00b4\u00c3\3\2\2\2\u00b5\u00b6\f\n\2\2\u00b6\u00b7\t\3"+
		"\2\2\u00b7\u00c2\5\36\20\13\u00b8\u00b9\f\t\2\2\u00b9\u00ba\t\4\2\2\u00ba"+
		"\u00c2\5\36\20\n\u00bb\u00bc\f\b\2\2\u00bc\u00bd\t\5\2\2\u00bd\u00c2\5"+
		"\36\20\t\u00be\u00bf\f\7\2\2\u00bf\u00c0\t\6\2\2\u00c0\u00c2\5\36\20\b"+
		"\u00c1\u00b5\3\2\2\2\u00c1\u00b8\3\2\2\2\u00c1\u00bb\3\2\2\2\u00c1\u00be"+
		"\3\2\2\2\u00c2\u00c5\3\2\2\2\u00c3\u00c1\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4"+
		"\37\3\2\2\2\u00c5\u00c3\3\2\2\2\u00c6\u00c7\7\66\2\2\u00c7\u00c8\7)\2"+
		"\2\u00c8\u00ca\7*\2\2\u00c9\u00c6\3\2\2\2\u00ca\u00cb\3\2\2\2\u00cb\u00c9"+
		"\3\2\2\2\u00cb\u00cc\3\2\2\2\u00cc\u00ed\3\2\2\2\u00cd\u00ce\7\4\2\2\u00ce"+
		"\u00cf\7)\2\2\u00cf\u00d0\5\"\22\2\u00d0\u00d1\7*\2\2\u00d1\u00ed\3\2"+
		"\2\2\u00d2\u00d3\7\5\2\2\u00d3\u00d4\7)\2\2\u00d4\u00d5\5\"\22\2\u00d5"+
		"\u00d6\7*\2\2\u00d6\u00ed\3\2\2\2\u00d7\u00ed\5$\23\2\u00d8\u00ed\5&\24"+
		"\2\u00d9\u00da\5\"\22\2\u00da\u00db\7\26\2\2\u00db\u00dc\7\66\2\2\u00dc"+
		"\u00e0\7)\2\2\u00dd\u00df\5\36\20\2\u00de\u00dd\3\2\2\2\u00df\u00e2\3"+
		"\2\2\2\u00e0\u00de\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1\u00e7\3\2\2\2\u00e2"+
		"\u00e0\3\2\2\2\u00e3\u00e4\7%\2\2\u00e4\u00e6\5\36\20\2\u00e5\u00e3\3"+
		"\2\2\2\u00e6\u00e9\3\2\2\2\u00e7\u00e5\3\2\2\2\u00e7\u00e8\3\2\2\2\u00e8"+
		"\u00ea\3\2\2\2\u00e9\u00e7\3\2\2\2\u00ea\u00eb\7*\2\2\u00eb\u00ed\3\2"+
		"\2\2\u00ec\u00c9\3\2\2\2\u00ec\u00cd\3\2\2\2\u00ec\u00d2\3\2\2\2\u00ec"+
		"\u00d7\3\2\2\2\u00ec\u00d8\3\2\2\2\u00ec\u00d9\3\2\2\2\u00ed!\3\2\2\2"+
		"\u00ee\u00ef\7)\2\2\u00ef\u00f0\5\36\20\2\u00f0\u00f1\7*\2\2\u00f1\u0107"+
		"\3\2\2\2\u00f2\u0107\t\7\2\2\u00f3\u0107\t\b\2\2\u00f4\u0107\79\2\2\u00f5"+
		"\u0107\7\61\2\2\u00f6\u0107\7\66\2\2\u00f7\u00f8\7\66\2\2\u00f8\u00f9"+
		"\7-\2\2\u00f9\u00fa\5\"\22\2\u00fa\u00fb\7.\2\2\u00fb\u0107\3\2\2\2\u00fc"+
		"\u00fd\7\66\2\2\u00fd\u00fe\7\26\2\2\u00fe\u0103\7\66\2\2\u00ff\u0100"+
		"\7\26\2\2\u0100\u0102\7\66\2\2\u0101\u00ff\3\2\2\2\u0102\u0105\3\2\2\2"+
		"\u0103\u0101\3\2\2\2\u0103\u0104\3\2\2\2\u0104\u0107\3\2\2\2\u0105\u0103"+
		"\3\2\2\2\u0106\u00ee\3\2\2\2\u0106\u00f2\3\2\2\2\u0106\u00f3\3\2\2\2\u0106"+
		"\u00f4\3\2\2\2\u0106\u00f5\3\2\2\2\u0106\u00f6\3\2\2\2\u0106\u00f7\3\2"+
		"\2\2\u0106\u00fc\3\2\2\2\u0107#\3\2\2\2\u0108\u0109\7-\2\2\u0109\u010b"+
		"\7.\2\2\u010a\u0108\3\2\2\2\u010b\u010c\3\2\2\2\u010c\u010a\3\2\2\2\u010c"+
		"\u010d\3\2\2\2\u010d%\3\2\2\2\u010e\u010f\7+\2\2\u010f\u0111\7,\2\2\u0110"+
		"\u010e\3\2\2\2\u0111\u0112\3\2\2\2\u0112\u0110\3\2\2\2\u0112\u0113\3\2"+
		"\2\2\u0113\'\3\2\2\2\u0114\u0115\7+\2\2\u0115\u0119\5*\26\2\u0116\u0118"+
		"\5,\27\2\u0117\u0116\3\2\2\2\u0118\u011b\3\2\2\2\u0119\u0117\3\2\2\2\u0119"+
		"\u011a\3\2\2\2\u011a\u011f\3\2\2\2\u011b\u0119\3\2\2\2\u011c\u011e\5."+
		"\30\2\u011d\u011c\3\2\2\2\u011e\u0121\3\2\2\2\u011f\u011d\3\2\2\2\u011f"+
		"\u0120\3\2\2\2\u0120\u0125\3\2\2\2\u0121\u011f\3\2\2\2\u0122\u0124\5\60"+
		"\31\2\u0123\u0122\3\2\2\2\u0124\u0127\3\2\2\2\u0125\u0123\3\2\2\2\u0125"+
		"\u0126\3\2\2\2\u0126\u012b\3\2\2\2\u0127\u0125\3\2\2\2\u0128\u012a\5\62"+
		"\32\2\u0129\u0128\3\2\2\2\u012a\u012d\3\2\2\2\u012b\u0129\3\2\2\2\u012b"+
		"\u012c\3\2\2\2\u012c\u0131\3\2\2\2\u012d\u012b\3\2\2\2\u012e\u0130\5\64"+
		"\33\2\u012f\u012e\3\2\2\2\u0130\u0133\3\2\2\2\u0131\u012f\3\2\2\2\u0131"+
		"\u0132\3\2\2\2\u0132\u0134\3\2\2\2\u0133\u0131\3\2\2\2\u0134\u0135\7,"+
		"\2\2\u0135)\3\2\2\2\u0136\u0137\7\6\2\2\u0137\u0138\7\'\2\2\u0138\u013c"+
		"\5\66\34\2\u0139\u013b\7%\2\2\u013a\u0139\3\2\2\2\u013b\u013e\3\2\2\2"+
		"\u013c\u013a\3\2\2\2\u013c\u013d\3\2\2\2\u013d+\3\2\2\2\u013e\u013c\3"+
		"\2\2\2\u013f\u0140\7\7\2\2\u0140\u0141\7\'\2\2\u0141\u0145\5\"\22\2\u0142"+
		"\u0144\7%\2\2\u0143\u0142\3\2\2\2\u0144\u0147\3\2\2\2\u0145\u0143\3\2"+
		"\2\2\u0145\u0146\3\2\2\2\u0146-\3\2\2\2\u0147\u0145\3\2\2\2\u0148\u0149"+
		"\7\b\2\2\u0149\u014a\7\'\2\2\u014a\u014e\5\"\22\2\u014b\u014d\7%\2\2\u014c"+
		"\u014b\3\2\2\2\u014d\u0150\3\2\2\2\u014e\u014c\3\2\2\2\u014e\u014f\3\2"+
		"\2\2\u014f/\3\2\2\2\u0150\u014e\3\2\2\2\u0151\u0152\7\t\2\2\u0152\u0153"+
		"\7\'\2\2\u0153\u0157\5\"\22\2\u0154\u0156\7%\2\2\u0155\u0154\3\2\2\2\u0156"+
		"\u0159\3\2\2\2\u0157\u0155\3\2\2\2\u0157\u0158\3\2\2\2\u0158\61\3\2\2"+
		"\2\u0159\u0157\3\2\2\2\u015a\u015b\7\n\2\2\u015b\u015c\7\'\2\2\u015c\u015d"+
		"\5\"\22\2\u015d\u015e\7\13\2\2\u015e\u0162\5\"\22\2\u015f\u0161\7%\2\2"+
		"\u0160\u015f\3\2\2\2\u0161\u0164\3\2\2\2\u0162\u0160\3\2\2\2\u0162\u0163"+
		"\3\2\2\2\u0163\63\3\2\2\2\u0164\u0162\3\2\2\2\u0165\u0166\7\f\2\2\u0166"+
		"\u0167\7\'\2\2\u0167\u0168\5\"\22\2\u0168\u016c\t\t\2\2\u0169\u016b\7"+
		"%\2\2\u016a\u0169\3\2\2\2\u016b\u016e\3\2\2\2\u016c\u016a\3\2\2\2\u016c"+
		"\u016d\3\2\2\2\u016d\65\3\2\2\2\u016e\u016c\3\2\2\2\u016f\u0170\7-\2\2"+
		"\u0170\u0171\58\35\2\u0171\u0172\7.\2\2\u0172\67\3\2\2\2\u0173\u0174\b"+
		"\35\1\2\u0174\u017a\5:\36\2\u0175\u0176\7)\2\2\u0176\u0177\58\35\2\u0177"+
		"\u0178\7*\2\2\u0178\u017a\3\2\2\2\u0179\u0173\3\2\2\2\u0179\u0175\3\2"+
		"\2\2\u017a\u0180\3\2\2\2\u017b\u017c\f\4\2\2\u017c\u017d\t\6\2\2\u017d"+
		"\u017f\58\35\5\u017e\u017b\3\2\2\2\u017f\u0182\3\2\2\2\u0180\u017e\3\2"+
		"\2\2\u0180\u0181\3\2\2\2\u01819\3\2\2\2\u0182\u0180\3\2\2\2\u0183\u0184"+
		"\7\66\2\2\u0184\u0185\t\5\2\2\u0185\u0186\5\"\22\2\u0186;\3\2\2\2#EL^"+
		"kt\u0082\u0087\u0093\u009b\u00b3\u00c1\u00c3\u00cb\u00e0\u00e7\u00ec\u0103"+
		"\u0106\u010c\u0112\u0119\u011f\u0125\u012b\u0131\u013c\u0145\u014e\u0157"+
		"\u0162\u016c\u0179\u0180";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}