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
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, VOID=16, DATA_TYPE_STRING=17, 
		DATA_TYPE_NUMBER=18, DATA_TYPE_BOOLEAN=19, DATA_TYPE_MAP=20, DATA_TYPE_LIST=21, 
		DATA_TYPE_CRITERIA=22, RETURN=23, TRY=24, CATCH=25, OR=26, SINGLE_OR=27, 
		DOT=28, AND=29, SINGLE_AND=30, EQ=31, NEQ=32, GT=33, LT=34, GTEQ=35, LTEQ=36, 
		PLUS=37, MINUS=38, MULT=39, DIV=40, MOD=41, POW=42, NOT=43, COMMA=44, 
		SEMICOLON=45, COLON=46, ASSIGN=47, OPEN_PARANTHESIS=48, CLOSE_PARANTHESIS=49, 
		OPEN_BRACE=50, CLOSE_BRACE=51, OPEN_BRACKET=52, CLOSE_BRACKET=53, TRUE=54, 
		FALSE=55, NULL=56, IF=57, ELSE=58, FOR_EACH=59, LOG=60, VAR=61, INT=62, 
		FLOAT=63, STRING=64, COMMENT=65, SPACE=66, OTHER=67;
	public static final int
		RULE_parse = 0, RULE_function_block = 1, RULE_function_name_declare = 2, 
		RULE_function_param = 3, RULE_data_type = 4, RULE_block = 5, RULE_try_catch = 6, 
		RULE_try_statement = 7, RULE_catch_statement = 8, RULE_statement = 9, 
		RULE_assignment = 10, RULE_assignment_var = 11, RULE_if_statement = 12, 
		RULE_condition_block = 13, RULE_statement_block = 14, RULE_for_each_statement = 15, 
		RULE_log = 16, RULE_function_return = 17, RULE_boolean_expr = 18, RULE_boolean_expr_atom = 19, 
		RULE_expr = 20, RULE_stand_alone_expr = 21, RULE_recursive_expression = 22, 
		RULE_atom = 23, RULE_list_opperations = 24, RULE_map_opperations = 25, 
		RULE_db_param = 26, RULE_db_param_group = 27, RULE_db_param_criteria = 28, 
		RULE_db_param_field = 29, RULE_db_param_aggr = 30, RULE_db_param_limit = 31, 
		RULE_db_param_range = 32, RULE_db_param_group_by = 33, RULE_db_param_sort = 34, 
		RULE_criteria = 35, RULE_condition = 36, RULE_condition_atom = 37;
	private static String[] makeRuleNames() {
		return new String[] {
			"parse", "function_block", "function_name_declare", "function_param", 
			"data_type", "block", "try_catch", "try_statement", "catch_statement", 
			"statement", "assignment", "assignment_var", "if_statement", "condition_block", 
			"statement_block", "for_each_statement", "log", "function_return", "boolean_expr", 
			"boolean_expr_atom", "expr", "stand_alone_expr", "recursive_expression", 
			"atom", "list_opperations", "map_opperations", "db_param", "db_param_group", 
			"db_param_criteria", "db_param_field", "db_param_aggr", "db_param_limit", 
			"db_param_range", "db_param_group_by", "db_param_sort", "criteria", "condition", 
			"condition_atom"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'in'", "'NameSpace'", "'Module'", "'Connection'", "'Reading'", 
			"'criteria'", "'field'", "'aggregation'", "'limit'", "'range'", "'to'", 
			"'groupBy'", "'orderBy'", "'asc'", "'desc'", "'void'", "'String'", "'Number'", 
			"'Boolean'", "'Map'", "'List'", "'Criteria'", "'return'", "'try'", "'catch'", 
			"'||'", "'|'", "'.'", "'&&'", "'&'", "'=='", "'!='", "'>'", "'<'", "'>='", 
			"'<='", "'+'", "'-'", "'*'", "'/'", "'%'", "'^'", "'!'", "','", "';'", 
			"':'", "'='", "'('", "')'", "'{'", "'}'", "'['", "']'", "'true'", "'false'", 
			"'null'", "'if'", "'else'", "'for each'", "'log'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, "VOID", "DATA_TYPE_STRING", "DATA_TYPE_NUMBER", 
			"DATA_TYPE_BOOLEAN", "DATA_TYPE_MAP", "DATA_TYPE_LIST", "DATA_TYPE_CRITERIA", 
			"RETURN", "TRY", "CATCH", "OR", "SINGLE_OR", "DOT", "AND", "SINGLE_AND", 
			"EQ", "NEQ", "GT", "LT", "GTEQ", "LTEQ", "PLUS", "MINUS", "MULT", "DIV", 
			"MOD", "POW", "NOT", "COMMA", "SEMICOLON", "COLON", "ASSIGN", "OPEN_PARANTHESIS", 
			"CLOSE_PARANTHESIS", "OPEN_BRACE", "CLOSE_BRACE", "OPEN_BRACKET", "CLOSE_BRACKET", 
			"TRUE", "FALSE", "NULL", "IF", "ELSE", "FOR_EACH", "LOG", "VAR", "INT", 
			"FLOAT", "STRING", "COMMENT", "SPACE", "OTHER"
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
			setState(76);
			function_block();
			setState(77);
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
			setState(79);
			data_type();
			setState(80);
			function_name_declare();
			setState(81);
			match(OPEN_PARANTHESIS);
			setState(85);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VOID) | (1L << DATA_TYPE_STRING) | (1L << DATA_TYPE_NUMBER) | (1L << DATA_TYPE_BOOLEAN) | (1L << DATA_TYPE_MAP) | (1L << DATA_TYPE_LIST) | (1L << DATA_TYPE_CRITERIA))) != 0)) {
				{
				{
				setState(82);
				function_param();
				}
				}
				setState(87);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(92);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(88);
				match(COMMA);
				setState(89);
				function_param();
				}
				}
				setState(94);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(95);
			match(CLOSE_PARANTHESIS);
			setState(96);
			match(OPEN_BRACE);
			setState(97);
			block();
			setState(98);
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
			setState(100);
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
			setState(102);
			data_type();
			setState(103);
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
		public TerminalNode DATA_TYPE_BOOLEAN() { return getToken(WorkflowV2Parser.DATA_TYPE_BOOLEAN, 0); }
		public TerminalNode DATA_TYPE_MAP() { return getToken(WorkflowV2Parser.DATA_TYPE_MAP, 0); }
		public TerminalNode DATA_TYPE_LIST() { return getToken(WorkflowV2Parser.DATA_TYPE_LIST, 0); }
		public TerminalNode DATA_TYPE_CRITERIA() { return getToken(WorkflowV2Parser.DATA_TYPE_CRITERIA, 0); }
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
			setState(105);
			((Data_typeContext)_localctx).op = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VOID) | (1L << DATA_TYPE_STRING) | (1L << DATA_TYPE_NUMBER) | (1L << DATA_TYPE_BOOLEAN) | (1L << DATA_TYPE_MAP) | (1L << DATA_TYPE_LIST) | (1L << DATA_TYPE_CRITERIA))) != 0)) ) {
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
			setState(110);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << RETURN) | (1L << TRY) | (1L << OPEN_PARANTHESIS) | (1L << OPEN_BRACE) | (1L << OPEN_BRACKET) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << IF) | (1L << FOR_EACH) | (1L << LOG) | (1L << VAR) | (1L << INT) | (1L << FLOAT))) != 0) || _la==STRING || _la==OTHER) {
				{
				{
				setState(107);
				statement();
				}
				}
				setState(112);
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

	public static class Try_catchContext extends ParserRuleContext {
		public TerminalNode TRY() { return getToken(WorkflowV2Parser.TRY, 0); }
		public List<TerminalNode> OPEN_BRACE() { return getTokens(WorkflowV2Parser.OPEN_BRACE); }
		public TerminalNode OPEN_BRACE(int i) {
			return getToken(WorkflowV2Parser.OPEN_BRACE, i);
		}
		public List<TerminalNode> CLOSE_BRACE() { return getTokens(WorkflowV2Parser.CLOSE_BRACE); }
		public TerminalNode CLOSE_BRACE(int i) {
			return getToken(WorkflowV2Parser.CLOSE_BRACE, i);
		}
		public TerminalNode CATCH() { return getToken(WorkflowV2Parser.CATCH, 0); }
		public List<Try_statementContext> try_statement() {
			return getRuleContexts(Try_statementContext.class);
		}
		public Try_statementContext try_statement(int i) {
			return getRuleContext(Try_statementContext.class,i);
		}
		public List<Catch_statementContext> catch_statement() {
			return getRuleContexts(Catch_statementContext.class);
		}
		public Catch_statementContext catch_statement(int i) {
			return getRuleContext(Catch_statementContext.class,i);
		}
		public Try_catchContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_try_catch; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterTry_catch(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitTry_catch(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitTry_catch(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Try_catchContext try_catch() throws RecognitionException {
		Try_catchContext _localctx = new Try_catchContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_try_catch);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(113);
			match(TRY);
			setState(114);
			match(OPEN_BRACE);
			setState(118);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << RETURN) | (1L << TRY) | (1L << OPEN_PARANTHESIS) | (1L << OPEN_BRACE) | (1L << OPEN_BRACKET) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << IF) | (1L << FOR_EACH) | (1L << LOG) | (1L << VAR) | (1L << INT) | (1L << FLOAT))) != 0) || _la==STRING || _la==OTHER) {
				{
				{
				setState(115);
				try_statement();
				}
				}
				setState(120);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(121);
			match(CLOSE_BRACE);
			setState(122);
			match(CATCH);
			setState(123);
			match(OPEN_BRACE);
			setState(127);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << RETURN) | (1L << TRY) | (1L << OPEN_PARANTHESIS) | (1L << OPEN_BRACE) | (1L << OPEN_BRACKET) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << IF) | (1L << FOR_EACH) | (1L << LOG) | (1L << VAR) | (1L << INT) | (1L << FLOAT))) != 0) || _la==STRING || _la==OTHER) {
				{
				{
				setState(124);
				catch_statement();
				}
				}
				setState(129);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(130);
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

	public static class Try_statementContext extends ParserRuleContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public Try_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_try_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterTry_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitTry_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitTry_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Try_statementContext try_statement() throws RecognitionException {
		Try_statementContext _localctx = new Try_statementContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_try_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			statement();
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

	public static class Catch_statementContext extends ParserRuleContext {
		public StatementContext statement() {
			return getRuleContext(StatementContext.class,0);
		}
		public Catch_statementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_catch_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterCatch_statement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitCatch_statement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitCatch_statement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Catch_statementContext catch_statement() throws RecognitionException {
		Catch_statementContext _localctx = new Catch_statementContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_catch_statement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(134);
			statement();
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
		public Try_catchContext try_catch() {
			return getRuleContext(Try_catchContext.class,0);
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
		enterRule(_localctx, 18, RULE_statement);
		try {
			setState(147);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(136);
				assignment();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(137);
				if_statement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(138);
				for_each_statement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(139);
				log();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(140);
				stand_alone_expr();
				setState(141);
				match(SEMICOLON);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(143);
				function_return();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(144);
				try_catch();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(145);
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
		public Assignment_varContext assignment_var() {
			return getRuleContext(Assignment_varContext.class,0);
		}
		public TerminalNode ASSIGN() { return getToken(WorkflowV2Parser.ASSIGN, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode SEMICOLON() { return getToken(WorkflowV2Parser.SEMICOLON, 0); }
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
		enterRule(_localctx, 20, RULE_assignment);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(149);
			assignment_var();
			setState(150);
			match(ASSIGN);
			setState(151);
			expr(0);
			setState(152);
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

	public static class Assignment_varContext extends ParserRuleContext {
		public Assignment_varContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment_var; }
	 
		public Assignment_varContext() { }
		public void copyFrom(Assignment_varContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class AssignSingleBracketVarContext extends Assignment_varContext {
		public TerminalNode VAR() { return getToken(WorkflowV2Parser.VAR, 0); }
		public TerminalNode OPEN_BRACKET() { return getToken(WorkflowV2Parser.OPEN_BRACKET, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode CLOSE_BRACKET() { return getToken(WorkflowV2Parser.CLOSE_BRACKET, 0); }
		public AssignSingleBracketVarContext(Assignment_varContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterAssignSingleBracketVar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitAssignSingleBracketVar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitAssignSingleBracketVar(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AssignMultiDotVarContext extends Assignment_varContext {
		public List<TerminalNode> VAR() { return getTokens(WorkflowV2Parser.VAR); }
		public TerminalNode VAR(int i) {
			return getToken(WorkflowV2Parser.VAR, i);
		}
		public List<TerminalNode> DOT() { return getTokens(WorkflowV2Parser.DOT); }
		public TerminalNode DOT(int i) {
			return getToken(WorkflowV2Parser.DOT, i);
		}
		public AssignMultiDotVarContext(Assignment_varContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterAssignMultiDotVar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitAssignMultiDotVar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitAssignMultiDotVar(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class AssignSingleVarContext extends Assignment_varContext {
		public TerminalNode VAR() { return getToken(WorkflowV2Parser.VAR, 0); }
		public AssignSingleVarContext(Assignment_varContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterAssignSingleVar(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitAssignSingleVar(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitAssignSingleVar(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Assignment_varContext assignment_var() throws RecognitionException {
		Assignment_varContext _localctx = new Assignment_varContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_assignment_var);
		int _la;
		try {
			setState(167);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				_localctx = new AssignSingleVarContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(154);
				match(VAR);
				}
				break;
			case 2:
				_localctx = new AssignSingleBracketVarContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(155);
				match(VAR);
				{
				setState(156);
				match(OPEN_BRACKET);
				setState(157);
				expr(0);
				setState(158);
				match(CLOSE_BRACKET);
				}
				}
				break;
			case 3:
				_localctx = new AssignMultiDotVarContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(160);
				match(VAR);
				setState(163); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(161);
					match(DOT);
					setState(162);
					match(VAR);
					}
					}
					setState(165); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==DOT );
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
		enterRule(_localctx, 24, RULE_if_statement);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(169);
			match(IF);
			setState(170);
			condition_block();
			setState(176);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(171);
					match(ELSE);
					setState(172);
					match(IF);
					setState(173);
					condition_block();
					}
					} 
				}
				setState(178);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			}
			setState(181);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(179);
				match(ELSE);
				setState(180);
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
		public Boolean_exprContext boolean_expr() {
			return getRuleContext(Boolean_exprContext.class,0);
		}
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
		enterRule(_localctx, 26, RULE_condition_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(183);
			boolean_expr();
			setState(184);
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
		enterRule(_localctx, 28, RULE_statement_block);
		try {
			setState(191);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(186);
				match(OPEN_BRACE);
				setState(187);
				block();
				setState(188);
				match(CLOSE_BRACE);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(190);
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
		enterRule(_localctx, 30, RULE_for_each_statement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(193);
			match(FOR_EACH);
			setState(194);
			match(VAR);
			setState(199);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(195);
				match(COMMA);
				setState(196);
				match(VAR);
				}
				}
				setState(201);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(202);
			match(T__0);
			setState(203);
			expr(0);
			setState(204);
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
		enterRule(_localctx, 32, RULE_log);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(206);
			match(LOG);
			setState(207);
			expr(0);
			setState(208);
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
		enterRule(_localctx, 34, RULE_function_return);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(210);
			match(RETURN);
			setState(211);
			expr(0);
			setState(212);
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

	public static class Boolean_exprContext extends ParserRuleContext {
		public TerminalNode OPEN_PARANTHESIS() { return getToken(WorkflowV2Parser.OPEN_PARANTHESIS, 0); }
		public Boolean_expr_atomContext boolean_expr_atom() {
			return getRuleContext(Boolean_expr_atomContext.class,0);
		}
		public TerminalNode CLOSE_PARANTHESIS() { return getToken(WorkflowV2Parser.CLOSE_PARANTHESIS, 0); }
		public Boolean_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolean_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterBoolean_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitBoolean_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitBoolean_expr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Boolean_exprContext boolean_expr() throws RecognitionException {
		Boolean_exprContext _localctx = new Boolean_exprContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_boolean_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(214);
			match(OPEN_PARANTHESIS);
			setState(215);
			boolean_expr_atom(0);
			setState(216);
			match(CLOSE_PARANTHESIS);
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

	public static class Boolean_expr_atomContext extends ParserRuleContext {
		public Boolean_expr_atomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolean_expr_atom; }
	 
		public Boolean_expr_atomContext() { }
		public void copyFrom(Boolean_expr_atomContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ExprForBooleanContext extends Boolean_expr_atomContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public ExprForBooleanContext(Boolean_expr_atomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterExprForBoolean(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitExprForBoolean(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitExprForBoolean(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BoolExprParanthesisContext extends Boolean_expr_atomContext {
		public TerminalNode OPEN_PARANTHESIS() { return getToken(WorkflowV2Parser.OPEN_PARANTHESIS, 0); }
		public Boolean_expr_atomContext boolean_expr_atom() {
			return getRuleContext(Boolean_expr_atomContext.class,0);
		}
		public TerminalNode CLOSE_PARANTHESIS() { return getToken(WorkflowV2Parser.CLOSE_PARANTHESIS, 0); }
		public BoolExprParanthesisContext(Boolean_expr_atomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterBoolExprParanthesis(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitBoolExprParanthesis(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitBoolExprParanthesis(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class BooleanExprCalculationContext extends Boolean_expr_atomContext {
		public Token op;
		public List<Boolean_expr_atomContext> boolean_expr_atom() {
			return getRuleContexts(Boolean_expr_atomContext.class);
		}
		public Boolean_expr_atomContext boolean_expr_atom(int i) {
			return getRuleContext(Boolean_expr_atomContext.class,i);
		}
		public TerminalNode AND() { return getToken(WorkflowV2Parser.AND, 0); }
		public TerminalNode OR() { return getToken(WorkflowV2Parser.OR, 0); }
		public BooleanExprCalculationContext(Boolean_expr_atomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterBooleanExprCalculation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitBooleanExprCalculation(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitBooleanExprCalculation(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Boolean_expr_atomContext boolean_expr_atom() throws RecognitionException {
		return boolean_expr_atom(0);
	}

	private Boolean_expr_atomContext boolean_expr_atom(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Boolean_expr_atomContext _localctx = new Boolean_expr_atomContext(_ctx, _parentState);
		Boolean_expr_atomContext _prevctx = _localctx;
		int _startState = 38;
		enterRecursionRule(_localctx, 38, RULE_boolean_expr_atom, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(224);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				_localctx = new ExprForBooleanContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(219);
				expr(0);
				}
				break;
			case 2:
				{
				_localctx = new BoolExprParanthesisContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(220);
				match(OPEN_PARANTHESIS);
				setState(221);
				boolean_expr_atom(0);
				setState(222);
				match(CLOSE_PARANTHESIS);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(231);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new BooleanExprCalculationContext(new Boolean_expr_atomContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_boolean_expr_atom);
					setState(226);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(227);
					((BooleanExprCalculationContext)_localctx).op = _input.LT(1);
					_la = _input.LA(1);
					if ( !(_la==OR || _la==AND) ) {
						((BooleanExprCalculationContext)_localctx).op = (Token)_errHandler.recoverInline(this);
					}
					else {
						if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
						_errHandler.reportMatch(this);
						consume();
					}
					setState(228);
					boolean_expr_atom(3);
					}
					} 
				}
				setState(233);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
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
		public TerminalNode SINGLE_AND() { return getToken(WorkflowV2Parser.SINGLE_AND, 0); }
		public TerminalNode SINGLE_OR() { return getToken(WorkflowV2Parser.SINGLE_OR, 0); }
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
		int _startState = 40;
		enterRecursionRule(_localctx, 40, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(243);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				_localctx = new UnaryMinusExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(235);
				match(MINUS);
				setState(236);
				expr(10);
				}
				break;
			case 2:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(237);
				match(NOT);
				setState(238);
				expr(9);
				}
				break;
			case 3:
				{
				_localctx = new AtomExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(239);
				atom();
				}
				break;
			case 4:
				{
				_localctx = new StandAloneStatementsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(240);
				stand_alone_expr();
				}
				break;
			case 5:
				{
				_localctx = new DbParamInitializationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(241);
				db_param();
				}
				break;
			case 6:
				{
				_localctx = new CriteriaInitializationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(242);
				criteria();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(259);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(257);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
					case 1:
						{
						_localctx = new ArithmeticFirstPrecedenceExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(245);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(246);
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
						setState(247);
						expr(9);
						}
						break;
					case 2:
						{
						_localctx = new ArithmeticSecondPrecedenceExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(248);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(249);
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
						setState(250);
						expr(8);
						}
						break;
					case 3:
						{
						_localctx = new RelationalExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(251);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(252);
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
						setState(253);
						expr(7);
						}
						break;
					case 4:
						{
						_localctx = new BooleanExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(254);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(255);
						((BooleanExprContext)_localctx).op = _input.LT(1);
						_la = _input.LA(1);
						if ( !(_la==SINGLE_OR || _la==SINGLE_AND) ) {
							((BooleanExprContext)_localctx).op = (Token)_errHandler.recoverInline(this);
						}
						else {
							if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
							_errHandler.reportMatch(this);
							consume();
						}
						setState(256);
						expr(6);
						}
						break;
					}
					} 
				}
				setState(261);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
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
	public static class Recursive_exprContext extends Stand_alone_exprContext {
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public List<Recursive_expressionContext> recursive_expression() {
			return getRuleContexts(Recursive_expressionContext.class);
		}
		public Recursive_expressionContext recursive_expression(int i) {
			return getRuleContext(Recursive_expressionContext.class,i);
		}
		public Recursive_exprContext(Stand_alone_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterRecursive_expr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitRecursive_expr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitRecursive_expr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Stand_alone_exprContext stand_alone_expr() throws RecognitionException {
		Stand_alone_exprContext _localctx = new Stand_alone_exprContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_stand_alone_expr);
		try {
			int _alt;
			_localctx = new Recursive_exprContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(262);
			atom();
			setState(264); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(263);
					recursive_expression();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(266); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
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

	public static class Recursive_expressionContext extends ParserRuleContext {
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
		public TerminalNode OPEN_BRACKET() { return getToken(WorkflowV2Parser.OPEN_BRACKET, 0); }
		public AtomContext atom() {
			return getRuleContext(AtomContext.class,0);
		}
		public TerminalNode CLOSE_BRACKET() { return getToken(WorkflowV2Parser.CLOSE_BRACKET, 0); }
		public Recursive_expressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_recursive_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterRecursive_expression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitRecursive_expression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitRecursive_expression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Recursive_expressionContext recursive_expression() throws RecognitionException {
		Recursive_expressionContext _localctx = new Recursive_expressionContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_recursive_expression);
		int _la;
		try {
			setState(291);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(268);
				match(DOT);
				setState(269);
				match(VAR);
				setState(270);
				match(OPEN_PARANTHESIS);
				setState(274);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (((((_la - 2)) & ~0x3f) == 0 && ((1L << (_la - 2)) & ((1L << (T__1 - 2)) | (1L << (T__2 - 2)) | (1L << (T__3 - 2)) | (1L << (T__4 - 2)) | (1L << (MINUS - 2)) | (1L << (NOT - 2)) | (1L << (OPEN_PARANTHESIS - 2)) | (1L << (OPEN_BRACE - 2)) | (1L << (OPEN_BRACKET - 2)) | (1L << (TRUE - 2)) | (1L << (FALSE - 2)) | (1L << (NULL - 2)) | (1L << (VAR - 2)) | (1L << (INT - 2)) | (1L << (FLOAT - 2)) | (1L << (STRING - 2)))) != 0)) {
					{
					{
					setState(271);
					expr(0);
					}
					}
					setState(276);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(281);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(277);
					match(COMMA);
					setState(278);
					expr(0);
					}
					}
					setState(283);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(284);
				match(CLOSE_PARANTHESIS);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(285);
				match(DOT);
				setState(286);
				match(VAR);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(287);
				match(OPEN_BRACKET);
				setState(288);
				atom();
				setState(289);
				match(CLOSE_BRACKET);
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
	public static class NameSpaceInitializationContext extends AtomContext {
		public TerminalNode OPEN_PARANTHESIS() { return getToken(WorkflowV2Parser.OPEN_PARANTHESIS, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode CLOSE_PARANTHESIS() { return getToken(WorkflowV2Parser.CLOSE_PARANTHESIS, 0); }
		public NameSpaceInitializationContext(AtomContext ctx) { copyFrom(ctx); }
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
	public static class ConnectionInitializationContext extends AtomContext {
		public TerminalNode OPEN_PARANTHESIS() { return getToken(WorkflowV2Parser.OPEN_PARANTHESIS, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode CLOSE_PARANTHESIS() { return getToken(WorkflowV2Parser.CLOSE_PARANTHESIS, 0); }
		public ConnectionInitializationContext(AtomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterConnectionInitialization(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitConnectionInitialization(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitConnectionInitialization(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ListOppContext extends AtomContext {
		public List_opperationsContext list_opperations() {
			return getRuleContext(List_opperationsContext.class,0);
		}
		public ListOppContext(AtomContext ctx) { copyFrom(ctx); }
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
	public static class ModuleAndSystemNameSpaceInitializationContext extends AtomContext {
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
		public ModuleAndSystemNameSpaceInitializationContext(AtomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterModuleAndSystemNameSpaceInitialization(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitModuleAndSystemNameSpaceInitialization(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitModuleAndSystemNameSpaceInitialization(this);
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
	public static class ReadingInitializationContext extends AtomContext {
		public TerminalNode OPEN_PARANTHESIS() { return getToken(WorkflowV2Parser.OPEN_PARANTHESIS, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode COMMA() { return getToken(WorkflowV2Parser.COMMA, 0); }
		public TerminalNode CLOSE_PARANTHESIS() { return getToken(WorkflowV2Parser.CLOSE_PARANTHESIS, 0); }
		public ReadingInitializationContext(AtomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterReadingInitialization(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitReadingInitialization(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitReadingInitialization(this);
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
	public static class CustomModuleInitializationContext extends AtomContext {
		public TerminalNode OPEN_PARANTHESIS() { return getToken(WorkflowV2Parser.OPEN_PARANTHESIS, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode CLOSE_PARANTHESIS() { return getToken(WorkflowV2Parser.CLOSE_PARANTHESIS, 0); }
		public CustomModuleInitializationContext(AtomContext ctx) { copyFrom(ctx); }
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
	public static class MapOppsContext extends AtomContext {
		public Map_opperationsContext map_opperations() {
			return getRuleContext(Map_opperationsContext.class,0);
		}
		public MapOppsContext(AtomContext ctx) { copyFrom(ctx); }
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

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_atom);
		int _la;
		try {
			int _alt;
			setState(333);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				_localctx = new ParanthesisExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(293);
				match(OPEN_PARANTHESIS);
				setState(294);
				expr(0);
				setState(295);
				match(CLOSE_PARANTHESIS);
				}
				break;
			case 2:
				_localctx = new NumberAtomContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(297);
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
				setState(298);
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
				setState(299);
				match(STRING);
				}
				break;
			case 5:
				_localctx = new NullAtomContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(300);
				match(NULL);
				}
				break;
			case 6:
				_localctx = new VarAtomContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(301);
				match(VAR);
				}
				break;
			case 7:
				_localctx = new NameSpaceInitializationContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(302);
				match(T__1);
				setState(303);
				match(OPEN_PARANTHESIS);
				setState(304);
				expr(0);
				setState(305);
				match(CLOSE_PARANTHESIS);
				}
				break;
			case 8:
				_localctx = new CustomModuleInitializationContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(307);
				match(T__2);
				setState(308);
				match(OPEN_PARANTHESIS);
				setState(309);
				expr(0);
				setState(310);
				match(CLOSE_PARANTHESIS);
				}
				break;
			case 9:
				_localctx = new ConnectionInitializationContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(312);
				match(T__3);
				setState(313);
				match(OPEN_PARANTHESIS);
				setState(314);
				expr(0);
				setState(315);
				match(CLOSE_PARANTHESIS);
				}
				break;
			case 10:
				_localctx = new ReadingInitializationContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(317);
				match(T__4);
				setState(318);
				match(OPEN_PARANTHESIS);
				setState(319);
				expr(0);
				setState(320);
				match(COMMA);
				setState(321);
				expr(0);
				setState(322);
				match(CLOSE_PARANTHESIS);
				}
				break;
			case 11:
				_localctx = new ModuleAndSystemNameSpaceInitializationContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(327); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(324);
						match(VAR);
						setState(325);
						match(OPEN_PARANTHESIS);
						setState(326);
						match(CLOSE_PARANTHESIS);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(329); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 12:
				_localctx = new ListOppContext(_localctx);
				enterOuterAlt(_localctx, 12);
				{
				setState(331);
				list_opperations();
				}
				break;
			case 13:
				_localctx = new MapOppsContext(_localctx);
				enterOuterAlt(_localctx, 13);
				{
				setState(332);
				map_opperations();
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
		enterRule(_localctx, 48, RULE_list_opperations);
		try {
			int _alt;
			_localctx = new ListInitialisationContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(337); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(335);
					match(OPEN_BRACKET);
					setState(336);
					match(CLOSE_BRACKET);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(339); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
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
		enterRule(_localctx, 50, RULE_map_opperations);
		try {
			int _alt;
			_localctx = new MapInitialisationContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(343); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(341);
					match(OPEN_BRACE);
					setState(342);
					match(CLOSE_BRACE);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(345); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,24,_ctx);
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
		public List<Db_param_groupContext> db_param_group() {
			return getRuleContexts(Db_param_groupContext.class);
		}
		public Db_param_groupContext db_param_group(int i) {
			return getRuleContext(Db_param_groupContext.class,i);
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
		enterRule(_localctx, 52, RULE_db_param);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(347);
			match(OPEN_BRACE);
			setState(348);
			db_param_criteria();
			setState(352);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__11) | (1L << T__12))) != 0)) {
				{
				{
				setState(349);
				db_param_group();
				}
				}
				setState(354);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(355);
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

	public static class Db_param_groupContext extends ParserRuleContext {
		public Db_param_fieldContext db_param_field() {
			return getRuleContext(Db_param_fieldContext.class,0);
		}
		public Db_param_aggrContext db_param_aggr() {
			return getRuleContext(Db_param_aggrContext.class,0);
		}
		public Db_param_limitContext db_param_limit() {
			return getRuleContext(Db_param_limitContext.class,0);
		}
		public Db_param_rangeContext db_param_range() {
			return getRuleContext(Db_param_rangeContext.class,0);
		}
		public Db_param_group_byContext db_param_group_by() {
			return getRuleContext(Db_param_group_byContext.class,0);
		}
		public Db_param_sortContext db_param_sort() {
			return getRuleContext(Db_param_sortContext.class,0);
		}
		public Db_param_groupContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_db_param_group; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterDb_param_group(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitDb_param_group(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitDb_param_group(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Db_param_groupContext db_param_group() throws RecognitionException {
		Db_param_groupContext _localctx = new Db_param_groupContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_db_param_group);
		try {
			setState(363);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__6:
				enterOuterAlt(_localctx, 1);
				{
				setState(357);
				db_param_field();
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 2);
				{
				setState(358);
				db_param_aggr();
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 3);
				{
				setState(359);
				db_param_limit();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 4);
				{
				setState(360);
				db_param_range();
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 5);
				{
				setState(361);
				db_param_group_by();
				}
				break;
			case T__12:
				enterOuterAlt(_localctx, 6);
				{
				setState(362);
				db_param_sort();
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
		enterRule(_localctx, 56, RULE_db_param_criteria);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(365);
			match(T__5);
			setState(366);
			match(COLON);
			setState(367);
			criteria();
			setState(371);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(368);
				match(COMMA);
				}
				}
				setState(373);
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
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
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
		enterRule(_localctx, 58, RULE_db_param_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(374);
			match(T__6);
			setState(375);
			match(COLON);
			setState(376);
			expr(0);
			setState(380);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(377);
				match(COMMA);
				}
				}
				setState(382);
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
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
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
		enterRule(_localctx, 60, RULE_db_param_aggr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(383);
			match(T__7);
			setState(384);
			match(COLON);
			setState(385);
			expr(0);
			setState(389);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(386);
				match(COMMA);
				}
				}
				setState(391);
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
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
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
		enterRule(_localctx, 62, RULE_db_param_limit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(392);
			match(T__8);
			setState(393);
			match(COLON);
			setState(394);
			expr(0);
			setState(398);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(395);
				match(COMMA);
				}
				}
				setState(400);
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
		enterRule(_localctx, 64, RULE_db_param_range);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(401);
			match(T__9);
			setState(402);
			match(COLON);
			setState(403);
			expr(0);
			setState(404);
			match(T__10);
			setState(405);
			expr(0);
			setState(409);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(406);
				match(COMMA);
				}
				}
				setState(411);
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

	public static class Db_param_group_byContext extends ParserRuleContext {
		public TerminalNode COLON() { return getToken(WorkflowV2Parser.COLON, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public List<TerminalNode> COMMA() { return getTokens(WorkflowV2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(WorkflowV2Parser.COMMA, i);
		}
		public Db_param_group_byContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_db_param_group_by; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterDb_param_group_by(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitDb_param_group_by(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitDb_param_group_by(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Db_param_group_byContext db_param_group_by() throws RecognitionException {
		Db_param_group_byContext _localctx = new Db_param_group_byContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_db_param_group_by);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(412);
			match(T__11);
			setState(413);
			match(COLON);
			setState(414);
			expr(0);
			setState(418);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(415);
				match(COMMA);
				}
				}
				setState(420);
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
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
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
		enterRule(_localctx, 68, RULE_db_param_sort);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(421);
			match(T__12);
			setState(422);
			match(COLON);
			setState(423);
			expr(0);
			setState(424);
			((Db_param_sortContext)_localctx).op = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==T__13 || _la==T__14) ) {
				((Db_param_sortContext)_localctx).op = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(428);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(425);
				match(COMMA);
				}
				}
				setState(430);
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
		enterRule(_localctx, 70, RULE_criteria);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(431);
			match(OPEN_BRACKET);
			setState(432);
			condition(0);
			setState(433);
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
		int _startState = 72;
		enterRecursionRule(_localctx, 72, RULE_condition, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(441);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VAR:
				{
				setState(436);
				condition_atom();
				}
				break;
			case OPEN_PARANTHESIS:
				{
				setState(437);
				match(OPEN_PARANTHESIS);
				setState(438);
				condition(0);
				setState(439);
				match(CLOSE_PARANTHESIS);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(448);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ConditionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_condition);
					setState(443);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(444);
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
					setState(445);
					condition(3);
					}
					} 
				}
				setState(450);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,35,_ctx);
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
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
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
		enterRule(_localctx, 74, RULE_condition_atom);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(451);
			match(VAR);
			setState(452);
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
			setState(453);
			expr(0);
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
		case 19:
			return boolean_expr_atom_sempred((Boolean_expr_atomContext)_localctx, predIndex);
		case 20:
			return expr_sempred((ExprContext)_localctx, predIndex);
		case 36:
			return condition_sempred((ConditionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean boolean_expr_atom_sempred(Boolean_expr_atomContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 2);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 8);
		case 2:
			return precpred(_ctx, 7);
		case 3:
			return precpred(_ctx, 6);
		case 4:
			return precpred(_ctx, 5);
		}
		return true;
	}
	private boolean condition_sempred(ConditionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 5:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3E\u01ca\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\3\2\3\2\3\2\3\3\3\3\3\3\3"+
		"\3\7\3V\n\3\f\3\16\3Y\13\3\3\3\3\3\7\3]\n\3\f\3\16\3`\13\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\7\7\7o\n\7\f\7\16\7r\13\7\3\b\3"+
		"\b\3\b\7\bw\n\b\f\b\16\bz\13\b\3\b\3\b\3\b\3\b\7\b\u0080\n\b\f\b\16\b"+
		"\u0083\13\b\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\13\5\13\u0096\n\13\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3"+
		"\r\3\r\3\r\3\r\3\r\3\r\6\r\u00a6\n\r\r\r\16\r\u00a7\5\r\u00aa\n\r\3\16"+
		"\3\16\3\16\3\16\3\16\7\16\u00b1\n\16\f\16\16\16\u00b4\13\16\3\16\3\16"+
		"\5\16\u00b8\n\16\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\5\20\u00c2\n"+
		"\20\3\21\3\21\3\21\3\21\7\21\u00c8\n\21\f\21\16\21\u00cb\13\21\3\21\3"+
		"\21\3\21\3\21\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3"+
		"\24\3\25\3\25\3\25\3\25\3\25\3\25\5\25\u00e3\n\25\3\25\3\25\3\25\7\25"+
		"\u00e8\n\25\f\25\16\25\u00eb\13\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\3\26\3\26\5\26\u00f6\n\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\3\26\3\26\3\26\7\26\u0104\n\26\f\26\16\26\u0107\13\26\3\27\3\27\6\27"+
		"\u010b\n\27\r\27\16\27\u010c\3\30\3\30\3\30\3\30\7\30\u0113\n\30\f\30"+
		"\16\30\u0116\13\30\3\30\3\30\7\30\u011a\n\30\f\30\16\30\u011d\13\30\3"+
		"\30\3\30\3\30\3\30\3\30\3\30\3\30\5\30\u0126\n\30\3\31\3\31\3\31\3\31"+
		"\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31"+
		"\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31"+
		"\3\31\3\31\6\31\u014a\n\31\r\31\16\31\u014b\3\31\3\31\5\31\u0150\n\31"+
		"\3\32\3\32\6\32\u0154\n\32\r\32\16\32\u0155\3\33\3\33\6\33\u015a\n\33"+
		"\r\33\16\33\u015b\3\34\3\34\3\34\7\34\u0161\n\34\f\34\16\34\u0164\13\34"+
		"\3\34\3\34\3\35\3\35\3\35\3\35\3\35\3\35\5\35\u016e\n\35\3\36\3\36\3\36"+
		"\3\36\7\36\u0174\n\36\f\36\16\36\u0177\13\36\3\37\3\37\3\37\3\37\7\37"+
		"\u017d\n\37\f\37\16\37\u0180\13\37\3 \3 \3 \3 \7 \u0186\n \f \16 \u0189"+
		"\13 \3!\3!\3!\3!\7!\u018f\n!\f!\16!\u0192\13!\3\"\3\"\3\"\3\"\3\"\3\""+
		"\7\"\u019a\n\"\f\"\16\"\u019d\13\"\3#\3#\3#\3#\7#\u01a3\n#\f#\16#\u01a6"+
		"\13#\3$\3$\3$\3$\3$\7$\u01ad\n$\f$\16$\u01b0\13$\3%\3%\3%\3%\3&\3&\3&"+
		"\3&\3&\3&\5&\u01bc\n&\3&\3&\3&\7&\u01c1\n&\f&\16&\u01c4\13&\3\'\3\'\3"+
		"\'\3\'\3\'\2\5(*J(\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60"+
		"\62\64\668:<>@BDFHJL\2\13\3\2\22\30\4\2\34\34\37\37\3\2)+\3\2\'(\3\2!"+
		"&\4\2\35\35  \3\2@A\3\289\3\2\20\21\2\u01e4\2N\3\2\2\2\4Q\3\2\2\2\6f\3"+
		"\2\2\2\bh\3\2\2\2\nk\3\2\2\2\fp\3\2\2\2\16s\3\2\2\2\20\u0086\3\2\2\2\22"+
		"\u0088\3\2\2\2\24\u0095\3\2\2\2\26\u0097\3\2\2\2\30\u00a9\3\2\2\2\32\u00ab"+
		"\3\2\2\2\34\u00b9\3\2\2\2\36\u00c1\3\2\2\2 \u00c3\3\2\2\2\"\u00d0\3\2"+
		"\2\2$\u00d4\3\2\2\2&\u00d8\3\2\2\2(\u00e2\3\2\2\2*\u00f5\3\2\2\2,\u0108"+
		"\3\2\2\2.\u0125\3\2\2\2\60\u014f\3\2\2\2\62\u0153\3\2\2\2\64\u0159\3\2"+
		"\2\2\66\u015d\3\2\2\28\u016d\3\2\2\2:\u016f\3\2\2\2<\u0178\3\2\2\2>\u0181"+
		"\3\2\2\2@\u018a\3\2\2\2B\u0193\3\2\2\2D\u019e\3\2\2\2F\u01a7\3\2\2\2H"+
		"\u01b1\3\2\2\2J\u01bb\3\2\2\2L\u01c5\3\2\2\2NO\5\4\3\2OP\7\2\2\3P\3\3"+
		"\2\2\2QR\5\n\6\2RS\5\6\4\2SW\7\62\2\2TV\5\b\5\2UT\3\2\2\2VY\3\2\2\2WU"+
		"\3\2\2\2WX\3\2\2\2X^\3\2\2\2YW\3\2\2\2Z[\7.\2\2[]\5\b\5\2\\Z\3\2\2\2]"+
		"`\3\2\2\2^\\\3\2\2\2^_\3\2\2\2_a\3\2\2\2`^\3\2\2\2ab\7\63\2\2bc\7\64\2"+
		"\2cd\5\f\7\2de\7\65\2\2e\5\3\2\2\2fg\7?\2\2g\7\3\2\2\2hi\5\n\6\2ij\7?"+
		"\2\2j\t\3\2\2\2kl\t\2\2\2l\13\3\2\2\2mo\5\24\13\2nm\3\2\2\2or\3\2\2\2"+
		"pn\3\2\2\2pq\3\2\2\2q\r\3\2\2\2rp\3\2\2\2st\7\32\2\2tx\7\64\2\2uw\5\20"+
		"\t\2vu\3\2\2\2wz\3\2\2\2xv\3\2\2\2xy\3\2\2\2y{\3\2\2\2zx\3\2\2\2{|\7\65"+
		"\2\2|}\7\33\2\2}\u0081\7\64\2\2~\u0080\5\22\n\2\177~\3\2\2\2\u0080\u0083"+
		"\3\2\2\2\u0081\177\3\2\2\2\u0081\u0082\3\2\2\2\u0082\u0084\3\2\2\2\u0083"+
		"\u0081\3\2\2\2\u0084\u0085\7\65\2\2\u0085\17\3\2\2\2\u0086\u0087\5\24"+
		"\13\2\u0087\21\3\2\2\2\u0088\u0089\5\24\13\2\u0089\23\3\2\2\2\u008a\u0096"+
		"\5\26\f\2\u008b\u0096\5\32\16\2\u008c\u0096\5 \21\2\u008d\u0096\5\"\22"+
		"\2\u008e\u008f\5,\27\2\u008f\u0090\7/\2\2\u0090\u0096\3\2\2\2\u0091\u0096"+
		"\5$\23\2\u0092\u0096\5\16\b\2\u0093\u0094\7E\2\2\u0094\u0096\b\13\1\2"+
		"\u0095\u008a\3\2\2\2\u0095\u008b\3\2\2\2\u0095\u008c\3\2\2\2\u0095\u008d"+
		"\3\2\2\2\u0095\u008e\3\2\2\2\u0095\u0091\3\2\2\2\u0095\u0092\3\2\2\2\u0095"+
		"\u0093\3\2\2\2\u0096\25\3\2\2\2\u0097\u0098\5\30\r\2\u0098\u0099\7\61"+
		"\2\2\u0099\u009a\5*\26\2\u009a\u009b\7/\2\2\u009b\27\3\2\2\2\u009c\u00aa"+
		"\7?\2\2\u009d\u009e\7?\2\2\u009e\u009f\7\66\2\2\u009f\u00a0\5*\26\2\u00a0"+
		"\u00a1\7\67\2\2\u00a1\u00aa\3\2\2\2\u00a2\u00a5\7?\2\2\u00a3\u00a4\7\36"+
		"\2\2\u00a4\u00a6\7?\2\2\u00a5\u00a3\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7"+
		"\u00a5\3\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00aa\3\2\2\2\u00a9\u009c\3\2"+
		"\2\2\u00a9\u009d\3\2\2\2\u00a9\u00a2\3\2\2\2\u00aa\31\3\2\2\2\u00ab\u00ac"+
		"\7;\2\2\u00ac\u00b2\5\34\17\2\u00ad\u00ae\7<\2\2\u00ae\u00af\7;\2\2\u00af"+
		"\u00b1\5\34\17\2\u00b0\u00ad\3\2\2\2\u00b1\u00b4\3\2\2\2\u00b2\u00b0\3"+
		"\2\2\2\u00b2\u00b3\3\2\2\2\u00b3\u00b7\3\2\2\2\u00b4\u00b2\3\2\2\2\u00b5"+
		"\u00b6\7<\2\2\u00b6\u00b8\5\36\20\2\u00b7\u00b5\3\2\2\2\u00b7\u00b8\3"+
		"\2\2\2\u00b8\33\3\2\2\2\u00b9\u00ba\5&\24\2\u00ba\u00bb\5\36\20\2\u00bb"+
		"\35\3\2\2\2\u00bc\u00bd\7\64\2\2\u00bd\u00be\5\f\7\2\u00be\u00bf\7\65"+
		"\2\2\u00bf\u00c2\3\2\2\2\u00c0\u00c2\5\24\13\2\u00c1\u00bc\3\2\2\2\u00c1"+
		"\u00c0\3\2\2\2\u00c2\37\3\2\2\2\u00c3\u00c4\7=\2\2\u00c4\u00c9\7?\2\2"+
		"\u00c5\u00c6\7.\2\2\u00c6\u00c8\7?\2\2\u00c7\u00c5\3\2\2\2\u00c8\u00cb"+
		"\3\2\2\2\u00c9\u00c7\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca\u00cc\3\2\2\2\u00cb"+
		"\u00c9\3\2\2\2\u00cc\u00cd\7\3\2\2\u00cd\u00ce\5*\26\2\u00ce\u00cf\5\36"+
		"\20\2\u00cf!\3\2\2\2\u00d0\u00d1\7>\2\2\u00d1\u00d2\5*\26\2\u00d2\u00d3"+
		"\7/\2\2\u00d3#\3\2\2\2\u00d4\u00d5\7\31\2\2\u00d5\u00d6\5*\26\2\u00d6"+
		"\u00d7\7/\2\2\u00d7%\3\2\2\2\u00d8\u00d9\7\62\2\2\u00d9\u00da\5(\25\2"+
		"\u00da\u00db\7\63\2\2\u00db\'\3\2\2\2\u00dc\u00dd\b\25\1\2\u00dd\u00e3"+
		"\5*\26\2\u00de\u00df\7\62\2\2\u00df\u00e0\5(\25\2\u00e0\u00e1\7\63\2\2"+
		"\u00e1\u00e3\3\2\2\2\u00e2\u00dc\3\2\2\2\u00e2\u00de\3\2\2\2\u00e3\u00e9"+
		"\3\2\2\2\u00e4\u00e5\f\4\2\2\u00e5\u00e6\t\3\2\2\u00e6\u00e8\5(\25\5\u00e7"+
		"\u00e4\3\2\2\2\u00e8\u00eb\3\2\2\2\u00e9\u00e7\3\2\2\2\u00e9\u00ea\3\2"+
		"\2\2\u00ea)\3\2\2\2\u00eb\u00e9\3\2\2\2\u00ec\u00ed\b\26\1\2\u00ed\u00ee"+
		"\7(\2\2\u00ee\u00f6\5*\26\f\u00ef\u00f0\7-\2\2\u00f0\u00f6\5*\26\13\u00f1"+
		"\u00f6\5\60\31\2\u00f2\u00f6\5,\27\2\u00f3\u00f6\5\66\34\2\u00f4\u00f6"+
		"\5H%\2\u00f5\u00ec\3\2\2\2\u00f5\u00ef\3\2\2\2\u00f5\u00f1\3\2\2\2\u00f5"+
		"\u00f2\3\2\2\2\u00f5\u00f3\3\2\2\2\u00f5\u00f4\3\2\2\2\u00f6\u0105\3\2"+
		"\2\2\u00f7\u00f8\f\n\2\2\u00f8\u00f9\t\4\2\2\u00f9\u0104\5*\26\13\u00fa"+
		"\u00fb\f\t\2\2\u00fb\u00fc\t\5\2\2\u00fc\u0104\5*\26\n\u00fd\u00fe\f\b"+
		"\2\2\u00fe\u00ff\t\6\2\2\u00ff\u0104\5*\26\t\u0100\u0101\f\7\2\2\u0101"+
		"\u0102\t\7\2\2\u0102\u0104\5*\26\b\u0103\u00f7\3\2\2\2\u0103\u00fa\3\2"+
		"\2\2\u0103\u00fd\3\2\2\2\u0103\u0100\3\2\2\2\u0104\u0107\3\2\2\2\u0105"+
		"\u0103\3\2\2\2\u0105\u0106\3\2\2\2\u0106+\3\2\2\2\u0107\u0105\3\2\2\2"+
		"\u0108\u010a\5\60\31\2\u0109\u010b\5.\30\2\u010a\u0109\3\2\2\2\u010b\u010c"+
		"\3\2\2\2\u010c\u010a\3\2\2\2\u010c\u010d\3\2\2\2\u010d-\3\2\2\2\u010e"+
		"\u010f\7\36\2\2\u010f\u0110\7?\2\2\u0110\u0114\7\62\2\2\u0111\u0113\5"+
		"*\26\2\u0112\u0111\3\2\2\2\u0113\u0116\3\2\2\2\u0114\u0112\3\2\2\2\u0114"+
		"\u0115\3\2\2\2\u0115\u011b\3\2\2\2\u0116\u0114\3\2\2\2\u0117\u0118\7."+
		"\2\2\u0118\u011a\5*\26\2\u0119\u0117\3\2\2\2\u011a\u011d\3\2\2\2\u011b"+
		"\u0119\3\2\2\2\u011b\u011c\3\2\2\2\u011c\u011e\3\2\2\2\u011d\u011b\3\2"+
		"\2\2\u011e\u0126\7\63\2\2\u011f\u0120\7\36\2\2\u0120\u0126\7?\2\2\u0121"+
		"\u0122\7\66\2\2\u0122\u0123\5\60\31\2\u0123\u0124\7\67\2\2\u0124\u0126"+
		"\3\2\2\2\u0125\u010e\3\2\2\2\u0125\u011f\3\2\2\2\u0125\u0121\3\2\2\2\u0126"+
		"/\3\2\2\2\u0127\u0128\7\62\2\2\u0128\u0129\5*\26\2\u0129\u012a\7\63\2"+
		"\2\u012a\u0150\3\2\2\2\u012b\u0150\t\b\2\2\u012c\u0150\t\t\2\2\u012d\u0150"+
		"\7B\2\2\u012e\u0150\7:\2\2\u012f\u0150\7?\2\2\u0130\u0131\7\4\2\2\u0131"+
		"\u0132\7\62\2\2\u0132\u0133\5*\26\2\u0133\u0134\7\63\2\2\u0134\u0150\3"+
		"\2\2\2\u0135\u0136\7\5\2\2\u0136\u0137\7\62\2\2\u0137\u0138\5*\26\2\u0138"+
		"\u0139\7\63\2\2\u0139\u0150\3\2\2\2\u013a\u013b\7\6\2\2\u013b\u013c\7"+
		"\62\2\2\u013c\u013d\5*\26\2\u013d\u013e\7\63\2\2\u013e\u0150\3\2\2\2\u013f"+
		"\u0140\7\7\2\2\u0140\u0141\7\62\2\2\u0141\u0142\5*\26\2\u0142\u0143\7"+
		".\2\2\u0143\u0144\5*\26\2\u0144\u0145\7\63\2\2\u0145\u0150\3\2\2\2\u0146"+
		"\u0147\7?\2\2\u0147\u0148\7\62\2\2\u0148\u014a\7\63\2\2\u0149\u0146\3"+
		"\2\2\2\u014a\u014b\3\2\2\2\u014b\u0149\3\2\2\2\u014b\u014c\3\2\2\2\u014c"+
		"\u0150\3\2\2\2\u014d\u0150\5\62\32\2\u014e\u0150\5\64\33\2\u014f\u0127"+
		"\3\2\2\2\u014f\u012b\3\2\2\2\u014f\u012c\3\2\2\2\u014f\u012d\3\2\2\2\u014f"+
		"\u012e\3\2\2\2\u014f\u012f\3\2\2\2\u014f\u0130\3\2\2\2\u014f\u0135\3\2"+
		"\2\2\u014f\u013a\3\2\2\2\u014f\u013f\3\2\2\2\u014f\u0149\3\2\2\2\u014f"+
		"\u014d\3\2\2\2\u014f\u014e\3\2\2\2\u0150\61\3\2\2\2\u0151\u0152\7\66\2"+
		"\2\u0152\u0154\7\67\2\2\u0153\u0151\3\2\2\2\u0154\u0155\3\2\2\2\u0155"+
		"\u0153\3\2\2\2\u0155\u0156\3\2\2\2\u0156\63\3\2\2\2\u0157\u0158\7\64\2"+
		"\2\u0158\u015a\7\65\2\2\u0159\u0157\3\2\2\2\u015a\u015b\3\2\2\2\u015b"+
		"\u0159\3\2\2\2\u015b\u015c\3\2\2\2\u015c\65\3\2\2\2\u015d\u015e\7\64\2"+
		"\2\u015e\u0162\5:\36\2\u015f\u0161\58\35\2\u0160\u015f\3\2\2\2\u0161\u0164"+
		"\3\2\2\2\u0162\u0160\3\2\2\2\u0162\u0163\3\2\2\2\u0163\u0165\3\2\2\2\u0164"+
		"\u0162\3\2\2\2\u0165\u0166\7\65\2\2\u0166\67\3\2\2\2\u0167\u016e\5<\37"+
		"\2\u0168\u016e\5> \2\u0169\u016e\5@!\2\u016a\u016e\5B\"\2\u016b\u016e"+
		"\5D#\2\u016c\u016e\5F$\2\u016d\u0167\3\2\2\2\u016d\u0168\3\2\2\2\u016d"+
		"\u0169\3\2\2\2\u016d\u016a\3\2\2\2\u016d\u016b\3\2\2\2\u016d\u016c\3\2"+
		"\2\2\u016e9\3\2\2\2\u016f\u0170\7\b\2\2\u0170\u0171\7\60\2\2\u0171\u0175"+
		"\5H%\2\u0172\u0174\7.\2\2\u0173\u0172\3\2\2\2\u0174\u0177\3\2\2\2\u0175"+
		"\u0173\3\2\2\2\u0175\u0176\3\2\2\2\u0176;\3\2\2\2\u0177\u0175\3\2\2\2"+
		"\u0178\u0179\7\t\2\2\u0179\u017a\7\60\2\2\u017a\u017e\5*\26\2\u017b\u017d"+
		"\7.\2\2\u017c\u017b\3\2\2\2\u017d\u0180\3\2\2\2\u017e\u017c\3\2\2\2\u017e"+
		"\u017f\3\2\2\2\u017f=\3\2\2\2\u0180\u017e\3\2\2\2\u0181\u0182\7\n\2\2"+
		"\u0182\u0183\7\60\2\2\u0183\u0187\5*\26\2\u0184\u0186\7.\2\2\u0185\u0184"+
		"\3\2\2\2\u0186\u0189\3\2\2\2\u0187\u0185\3\2\2\2\u0187\u0188\3\2\2\2\u0188"+
		"?\3\2\2\2\u0189\u0187\3\2\2\2\u018a\u018b\7\13\2\2\u018b\u018c\7\60\2"+
		"\2\u018c\u0190\5*\26\2\u018d\u018f\7.\2\2\u018e\u018d\3\2\2\2\u018f\u0192"+
		"\3\2\2\2\u0190\u018e\3\2\2\2\u0190\u0191\3\2\2\2\u0191A\3\2\2\2\u0192"+
		"\u0190\3\2\2\2\u0193\u0194\7\f\2\2\u0194\u0195\7\60\2\2\u0195\u0196\5"+
		"*\26\2\u0196\u0197\7\r\2\2\u0197\u019b\5*\26\2\u0198\u019a\7.\2\2\u0199"+
		"\u0198\3\2\2\2\u019a\u019d\3\2\2\2\u019b\u0199\3\2\2\2\u019b\u019c\3\2"+
		"\2\2\u019cC\3\2\2\2\u019d\u019b\3\2\2\2\u019e\u019f\7\16\2\2\u019f\u01a0"+
		"\7\60\2\2\u01a0\u01a4\5*\26\2\u01a1\u01a3\7.\2\2\u01a2\u01a1\3\2\2\2\u01a3"+
		"\u01a6\3\2\2\2\u01a4\u01a2\3\2\2\2\u01a4\u01a5\3\2\2\2\u01a5E\3\2\2\2"+
		"\u01a6\u01a4\3\2\2\2\u01a7\u01a8\7\17\2\2\u01a8\u01a9\7\60\2\2\u01a9\u01aa"+
		"\5*\26\2\u01aa\u01ae\t\n\2\2\u01ab\u01ad\7.\2\2\u01ac\u01ab\3\2\2\2\u01ad"+
		"\u01b0\3\2\2\2\u01ae\u01ac\3\2\2\2\u01ae\u01af\3\2\2\2\u01afG\3\2\2\2"+
		"\u01b0\u01ae\3\2\2\2\u01b1\u01b2\7\66\2\2\u01b2\u01b3\5J&\2\u01b3\u01b4"+
		"\7\67\2\2\u01b4I\3\2\2\2\u01b5\u01b6\b&\1\2\u01b6\u01bc\5L\'\2\u01b7\u01b8"+
		"\7\62\2\2\u01b8\u01b9\5J&\2\u01b9\u01ba\7\63\2\2\u01ba\u01bc\3\2\2\2\u01bb"+
		"\u01b5\3\2\2\2\u01bb\u01b7\3\2\2\2\u01bc\u01c2\3\2\2\2\u01bd\u01be\f\4"+
		"\2\2\u01be\u01bf\t\3\2\2\u01bf\u01c1\5J&\5\u01c0\u01bd\3\2\2\2\u01c1\u01c4"+
		"\3\2\2\2\u01c2\u01c0\3\2\2\2\u01c2\u01c3\3\2\2\2\u01c3K\3\2\2\2\u01c4"+
		"\u01c2\3\2\2\2\u01c5\u01c6\7?\2\2\u01c6\u01c7\t\6\2\2\u01c7\u01c8\5*\26"+
		"\2\u01c8M\3\2\2\2&W^px\u0081\u0095\u00a7\u00a9\u00b2\u00b7\u00c1\u00c9"+
		"\u00e2\u00e9\u00f5\u0103\u0105\u010c\u0114\u011b\u0125\u014b\u014f\u0155"+
		"\u015b\u0162\u016d\u0175\u017e\u0187\u0190\u019b\u01a4\u01ae\u01bb\u01c2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}