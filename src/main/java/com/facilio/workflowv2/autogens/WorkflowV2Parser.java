// Generated from /home/facilio/git/bmsconsole/src/main/java/com/facilio/workflowv2/autogens/WorkflowV2.g4 by ANTLR 4.7.2
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
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		VOID=18, DATA_TYPE_STRING=19, DATA_TYPE_NUMBER=20, DATA_TYPE_BOOLEAN=21, 
		DATA_TYPE_MAP=22, DATA_TYPE_LIST=23, DATA_TYPE_CRITERIA=24, DATA_TYPE_CHAT_BOT_ACTION=25, 
		RETURN=26, TRY=27, CATCH=28, OR=29, SINGLE_OR=30, DOT=31, AND=32, SINGLE_AND=33, 
		EQ=34, NEQ=35, GT=36, LT=37, GTEQ=38, LTEQ=39, PLUS=40, MINUS=41, MULT=42, 
		DIV=43, MOD=44, POW=45, NOT=46, COMMA=47, SEMICOLON=48, COLON=49, ASSIGN=50, 
		OPEN_PARANTHESIS=51, CLOSE_PARANTHESIS=52, OPEN_BRACE=53, CLOSE_BRACE=54, 
		OPEN_BRACKET=55, CLOSE_BRACKET=56, TRUE=57, FALSE=58, NULL=59, IF=60, 
		ELSE=61, FOR_EACH=62, LOG=63, VAR=64, INT=65, FLOAT=66, STRING=67, COMMENT=68, 
		BLOCKCOMMENT=69, SPACE=70, OTHER=71;
	public static final int
		RULE_parse = 0, RULE_function_block = 1, RULE_function_name_declare = 2, 
		RULE_function_param = 3, RULE_data_type = 4, RULE_block = 5, RULE_try_catch = 6, 
		RULE_try_statement = 7, RULE_catch_statement = 8, RULE_statement = 9, 
		RULE_assignment = 10, RULE_assignment_var = 11, RULE_if_statement = 12, 
		RULE_condition_block = 13, RULE_statement_block = 14, RULE_for_each_statement = 15, 
		RULE_log = 16, RULE_function_return = 17, RULE_boolean_expr = 18, RULE_boolean_expr_atom = 19, 
		RULE_expr = 20, RULE_stand_alone_expr = 21, RULE_calender_clock_expr = 22, 
		RULE_calender_var = 23, RULE_clock_var = 24, RULE_recursive_expression = 25, 
		RULE_atom = 26, RULE_list_opperations = 27, RULE_map_opperations = 28, 
		RULE_db_param = 29, RULE_db_param_group = 30, RULE_db_param_criteria = 31, 
		RULE_db_param_field_criteria = 32, RULE_db_param_field = 33, RULE_db_param_aggr = 34, 
		RULE_db_param_limit = 35, RULE_db_param_range = 36, RULE_db_param_group_by = 37, 
		RULE_db_param_sort = 38, RULE_criteria = 39, RULE_condition = 40, RULE_condition_atom = 41;
	private static String[] makeRuleNames() {
		return new String[] {
			"parse", "function_block", "function_name_declare", "function_param", 
			"data_type", "block", "try_catch", "try_statement", "catch_statement", 
			"statement", "assignment", "assignment_var", "if_statement", "condition_block", 
			"statement_block", "for_each_statement", "log", "function_return", "boolean_expr", 
			"boolean_expr_atom", "expr", "stand_alone_expr", "calender_clock_expr", 
			"calender_var", "clock_var", "recursive_expression", "atom", "list_opperations", 
			"map_opperations", "db_param", "db_param_group", "db_param_criteria", 
			"db_param_field_criteria", "db_param_field", "db_param_aggr", "db_param_limit", 
			"db_param_range", "db_param_group_by", "db_param_sort", "criteria", "condition", 
			"condition_atom"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'in'", "'Calender.'", "'Clock.'", "'new '", "'Module'", "'Reading'", 
			"'criteria'", "'fieldCriteria'", "'field'", "'aggregation'", "'limit'", 
			"'range'", "'to'", "'groupBy'", "'orderBy'", "'asc'", "'desc'", "'void'", 
			"'String'", "'Number'", "'Boolean'", "'Map'", "'List'", "'Criteria'", 
			"'ChatBotAction'", "'return'", "'try'", "'catch'", "'||'", "'|'", "'.'", 
			"'&&'", "'&'", "'=='", "'!='", "'>'", "'<'", "'>='", "'<='", "'+'", "'-'", 
			"'*'", "'/'", "'%'", "'^'", "'!'", "','", "';'", "':'", "'='", "'('", 
			"')'", "'{'", "'}'", "'['", "']'", "'true'", "'false'", "'null'", "'if'", 
			"'else'", "'for each'", "'log'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, "VOID", "DATA_TYPE_STRING", "DATA_TYPE_NUMBER", 
			"DATA_TYPE_BOOLEAN", "DATA_TYPE_MAP", "DATA_TYPE_LIST", "DATA_TYPE_CRITERIA", 
			"DATA_TYPE_CHAT_BOT_ACTION", "RETURN", "TRY", "CATCH", "OR", "SINGLE_OR", 
			"DOT", "AND", "SINGLE_AND", "EQ", "NEQ", "GT", "LT", "GTEQ", "LTEQ", 
			"PLUS", "MINUS", "MULT", "DIV", "MOD", "POW", "NOT", "COMMA", "SEMICOLON", 
			"COLON", "ASSIGN", "OPEN_PARANTHESIS", "CLOSE_PARANTHESIS", "OPEN_BRACE", 
			"CLOSE_BRACE", "OPEN_BRACKET", "CLOSE_BRACKET", "TRUE", "FALSE", "NULL", 
			"IF", "ELSE", "FOR_EACH", "LOG", "VAR", "INT", "FLOAT", "STRING", "COMMENT", 
			"BLOCKCOMMENT", "SPACE", "OTHER"
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
			setState(84);
			function_block();
			setState(85);
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
			setState(87);
			data_type();
			setState(88);
			function_name_declare();
			setState(89);
			match(OPEN_PARANTHESIS);
			setState(93);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VOID) | (1L << DATA_TYPE_STRING) | (1L << DATA_TYPE_NUMBER) | (1L << DATA_TYPE_BOOLEAN) | (1L << DATA_TYPE_MAP) | (1L << DATA_TYPE_LIST) | (1L << DATA_TYPE_CRITERIA) | (1L << DATA_TYPE_CHAT_BOT_ACTION))) != 0)) {
				{
				{
				setState(90);
				function_param();
				}
				}
				setState(95);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(100);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(96);
				match(COMMA);
				setState(97);
				function_param();
				}
				}
				setState(102);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(103);
			match(CLOSE_PARANTHESIS);
			setState(104);
			match(OPEN_BRACE);
			setState(105);
			block();
			setState(106);
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
			setState(108);
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
			setState(110);
			data_type();
			setState(111);
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
		public TerminalNode DATA_TYPE_CHAT_BOT_ACTION() { return getToken(WorkflowV2Parser.DATA_TYPE_CHAT_BOT_ACTION, 0); }
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
			setState(113);
			((Data_typeContext)_localctx).op = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VOID) | (1L << DATA_TYPE_STRING) | (1L << DATA_TYPE_NUMBER) | (1L << DATA_TYPE_BOOLEAN) | (1L << DATA_TYPE_MAP) | (1L << DATA_TYPE_LIST) | (1L << DATA_TYPE_CRITERIA) | (1L << DATA_TYPE_CHAT_BOT_ACTION))) != 0)) ) {
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
			setState(118);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << RETURN) | (1L << TRY) | (1L << OPEN_PARANTHESIS) | (1L << OPEN_BRACE) | (1L << OPEN_BRACKET) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << IF) | (1L << FOR_EACH) | (1L << LOG))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (VAR - 64)) | (1L << (INT - 64)) | (1L << (FLOAT - 64)) | (1L << (STRING - 64)) | (1L << (OTHER - 64)))) != 0)) {
				{
				{
				setState(115);
				statement();
				}
				}
				setState(120);
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
			setState(121);
			match(TRY);
			setState(122);
			match(OPEN_BRACE);
			setState(126);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << RETURN) | (1L << TRY) | (1L << OPEN_PARANTHESIS) | (1L << OPEN_BRACE) | (1L << OPEN_BRACKET) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << IF) | (1L << FOR_EACH) | (1L << LOG))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (VAR - 64)) | (1L << (INT - 64)) | (1L << (FLOAT - 64)) | (1L << (STRING - 64)) | (1L << (OTHER - 64)))) != 0)) {
				{
				{
				setState(123);
				try_statement();
				}
				}
				setState(128);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(129);
			match(CLOSE_BRACE);
			setState(130);
			match(CATCH);
			setState(131);
			match(OPEN_BRACE);
			setState(135);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << RETURN) | (1L << TRY) | (1L << OPEN_PARANTHESIS) | (1L << OPEN_BRACE) | (1L << OPEN_BRACKET) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << IF) | (1L << FOR_EACH) | (1L << LOG))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (VAR - 64)) | (1L << (INT - 64)) | (1L << (FLOAT - 64)) | (1L << (STRING - 64)) | (1L << (OTHER - 64)))) != 0)) {
				{
				{
				setState(132);
				catch_statement();
				}
				}
				setState(137);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(138);
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
			setState(140);
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
			setState(142);
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
			setState(155);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(144);
				assignment();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(145);
				if_statement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(146);
				for_each_statement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(147);
				log();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(148);
				stand_alone_expr();
				setState(149);
				match(SEMICOLON);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(151);
				function_return();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(152);
				try_catch();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(153);
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
			setState(157);
			assignment_var();
			setState(158);
			match(ASSIGN);
			setState(159);
			expr(0);
			setState(160);
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
			setState(175);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				_localctx = new AssignSingleVarContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(162);
				match(VAR);
				}
				break;
			case 2:
				_localctx = new AssignSingleBracketVarContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(163);
				match(VAR);
				{
				setState(164);
				match(OPEN_BRACKET);
				setState(165);
				expr(0);
				setState(166);
				match(CLOSE_BRACKET);
				}
				}
				break;
			case 3:
				_localctx = new AssignMultiDotVarContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(168);
				match(VAR);
				setState(171); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(169);
					match(DOT);
					setState(170);
					match(VAR);
					}
					}
					setState(173); 
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
			setState(177);
			match(IF);
			setState(178);
			condition_block();
			setState(184);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(179);
					match(ELSE);
					setState(180);
					match(IF);
					setState(181);
					condition_block();
					}
					} 
				}
				setState(186);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
			}
			setState(189);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(187);
				match(ELSE);
				setState(188);
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
			setState(191);
			boolean_expr();
			setState(192);
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
			setState(199);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(194);
				match(OPEN_BRACE);
				setState(195);
				block();
				setState(196);
				match(CLOSE_BRACE);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(198);
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
			setState(201);
			match(FOR_EACH);
			setState(202);
			match(VAR);
			setState(207);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(203);
				match(COMMA);
				setState(204);
				match(VAR);
				}
				}
				setState(209);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(210);
			match(T__0);
			setState(211);
			expr(0);
			setState(212);
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
			setState(214);
			match(LOG);
			setState(215);
			expr(0);
			setState(216);
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
			setState(218);
			match(RETURN);
			setState(219);
			expr(0);
			setState(220);
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
			setState(222);
			match(OPEN_PARANTHESIS);
			setState(223);
			boolean_expr_atom(0);
			setState(224);
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
			setState(232);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				_localctx = new ExprForBooleanContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(227);
				expr(0);
				}
				break;
			case 2:
				{
				_localctx = new BoolExprParanthesisContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(228);
				match(OPEN_PARANTHESIS);
				setState(229);
				boolean_expr_atom(0);
				setState(230);
				match(CLOSE_PARANTHESIS);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(239);
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
					setState(234);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(235);
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
					setState(236);
					boolean_expr_atom(3);
					}
					} 
				}
				setState(241);
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
	public static class CalClockExprContext extends ExprContext {
		public Calender_clock_exprContext calender_clock_expr() {
			return getRuleContext(Calender_clock_exprContext.class,0);
		}
		public CalClockExprContext(ExprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterCalClockExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitCalClockExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitCalClockExpr(this);
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
			setState(252);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				{
				_localctx = new UnaryMinusExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(243);
				match(MINUS);
				setState(244);
				expr(11);
				}
				break;
			case 2:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(245);
				match(NOT);
				setState(246);
				expr(10);
				}
				break;
			case 3:
				{
				_localctx = new AtomExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(247);
				atom();
				}
				break;
			case 4:
				{
				_localctx = new CalClockExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(248);
				calender_clock_expr();
				}
				break;
			case 5:
				{
				_localctx = new StandAloneStatementsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(249);
				stand_alone_expr();
				}
				break;
			case 6:
				{
				_localctx = new DbParamInitializationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(250);
				db_param();
				}
				break;
			case 7:
				{
				_localctx = new CriteriaInitializationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(251);
				criteria();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(268);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,16,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(266);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
					case 1:
						{
						_localctx = new ArithmeticFirstPrecedenceExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(254);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(255);
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
						setState(256);
						expr(10);
						}
						break;
					case 2:
						{
						_localctx = new ArithmeticSecondPrecedenceExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(257);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(258);
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
						setState(259);
						expr(9);
						}
						break;
					case 3:
						{
						_localctx = new RelationalExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(260);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(261);
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
						setState(262);
						expr(8);
						}
						break;
					case 4:
						{
						_localctx = new BooleanExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(263);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(264);
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
						setState(265);
						expr(7);
						}
						break;
					}
					} 
				}
				setState(270);
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
			setState(271);
			atom();
			setState(273); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(272);
					recursive_expression();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(275); 
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

	public static class Calender_clock_exprContext extends ParserRuleContext {
		public Calender_clock_exprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_calender_clock_expr; }
	 
		public Calender_clock_exprContext() { }
		public void copyFrom(Calender_clock_exprContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class CalenderExprContext extends Calender_clock_exprContext {
		public Calender_varContext calender_var() {
			return getRuleContext(Calender_varContext.class,0);
		}
		public CalenderExprContext(Calender_clock_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterCalenderExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitCalenderExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitCalenderExpr(this);
			else return visitor.visitChildren(this);
		}
	}
	public static class ClockExprContext extends Calender_clock_exprContext {
		public Clock_varContext clock_var() {
			return getRuleContext(Clock_varContext.class,0);
		}
		public ClockExprContext(Calender_clock_exprContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterClockExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitClockExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitClockExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Calender_clock_exprContext calender_clock_expr() throws RecognitionException {
		Calender_clock_exprContext _localctx = new Calender_clock_exprContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_calender_clock_expr);
		try {
			setState(281);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
				_localctx = new CalenderExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(277);
				match(T__1);
				setState(278);
				calender_var();
				}
				break;
			case T__2:
				_localctx = new ClockExprContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(279);
				match(T__2);
				setState(280);
				clock_var();
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

	public static class Calender_varContext extends ParserRuleContext {
		public TerminalNode VAR() { return getToken(WorkflowV2Parser.VAR, 0); }
		public TerminalNode INT() { return getToken(WorkflowV2Parser.INT, 0); }
		public Calender_varContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_calender_var; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterCalender_var(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitCalender_var(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitCalender_var(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Calender_varContext calender_var() throws RecognitionException {
		Calender_varContext _localctx = new Calender_varContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_calender_var);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(283);
			_la = _input.LA(1);
			if ( !(_la==VAR || _la==INT) ) {
			_errHandler.recoverInline(this);
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

	public static class Clock_varContext extends ParserRuleContext {
		public TerminalNode VAR() { return getToken(WorkflowV2Parser.VAR, 0); }
		public List<TerminalNode> INT() { return getTokens(WorkflowV2Parser.INT); }
		public TerminalNode INT(int i) {
			return getToken(WorkflowV2Parser.INT, i);
		}
		public TerminalNode COLON() { return getToken(WorkflowV2Parser.COLON, 0); }
		public Clock_varContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_clock_var; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterClock_var(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitClock_var(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitClock_var(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Clock_varContext clock_var() throws RecognitionException {
		Clock_varContext _localctx = new Clock_varContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_clock_var);
		try {
			setState(290);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(285);
				match(VAR);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(286);
				match(INT);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(287);
				match(INT);
				setState(288);
				match(COLON);
				setState(289);
				match(INT);
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
		enterRule(_localctx, 50, RULE_recursive_expression);
		int _la;
		try {
			setState(315);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(292);
				match(DOT);
				setState(293);
				match(VAR);
				setState(294);
				match(OPEN_PARANTHESIS);
				setState(298);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << MINUS) | (1L << NOT) | (1L << OPEN_PARANTHESIS) | (1L << OPEN_BRACE) | (1L << OPEN_BRACKET) | (1L << TRUE) | (1L << FALSE) | (1L << NULL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (VAR - 64)) | (1L << (INT - 64)) | (1L << (FLOAT - 64)) | (1L << (STRING - 64)))) != 0)) {
					{
					{
					setState(295);
					expr(0);
					}
					}
					setState(300);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(305);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(301);
					match(COMMA);
					setState(302);
					expr(0);
					}
					}
					setState(307);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(308);
				match(CLOSE_PARANTHESIS);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(309);
				match(DOT);
				setState(310);
				match(VAR);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(311);
				match(OPEN_BRACKET);
				setState(312);
				atom();
				setState(313);
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
	public static class NewKeywordIntitializationContext extends AtomContext {
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
		public NewKeywordIntitializationContext(AtomContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterNewKeywordIntitialization(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitNewKeywordIntitialization(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitNewKeywordIntitialization(this);
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

	public final AtomContext atom() throws RecognitionException {
		AtomContext _localctx = new AtomContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_atom);
		int _la;
		try {
			setState(373);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,27,_ctx) ) {
			case 1:
				_localctx = new ParanthesisExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(317);
				match(OPEN_PARANTHESIS);
				setState(318);
				expr(0);
				setState(319);
				match(CLOSE_PARANTHESIS);
				}
				break;
			case 2:
				_localctx = new NumberAtomContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(321);
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
				setState(322);
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
				setState(323);
				match(STRING);
				}
				break;
			case 5:
				_localctx = new NullAtomContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(324);
				match(NULL);
				}
				break;
			case 6:
				_localctx = new VarAtomContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(325);
				match(VAR);
				}
				break;
			case 7:
				_localctx = new NewKeywordIntitializationContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(326);
				match(T__3);
				setState(327);
				match(VAR);
				setState(328);
				match(OPEN_PARANTHESIS);
				setState(332);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << MINUS) | (1L << NOT) | (1L << OPEN_PARANTHESIS) | (1L << OPEN_BRACE) | (1L << OPEN_BRACKET) | (1L << TRUE) | (1L << FALSE) | (1L << NULL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (VAR - 64)) | (1L << (INT - 64)) | (1L << (FLOAT - 64)) | (1L << (STRING - 64)))) != 0)) {
					{
					{
					setState(329);
					expr(0);
					}
					}
					setState(334);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(339);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(335);
					match(COMMA);
					setState(336);
					expr(0);
					}
					}
					setState(341);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(342);
				match(CLOSE_PARANTHESIS);
				}
				break;
			case 8:
				_localctx = new CustomModuleInitializationContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(343);
				match(T__4);
				setState(344);
				match(OPEN_PARANTHESIS);
				setState(345);
				expr(0);
				setState(346);
				match(CLOSE_PARANTHESIS);
				}
				break;
			case 9:
				_localctx = new ReadingInitializationContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(348);
				match(T__5);
				setState(349);
				match(OPEN_PARANTHESIS);
				setState(350);
				expr(0);
				setState(351);
				match(COMMA);
				setState(352);
				expr(0);
				setState(353);
				match(CLOSE_PARANTHESIS);
				}
				break;
			case 10:
				_localctx = new ModuleAndSystemNameSpaceInitializationContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(355);
				match(VAR);
				setState(356);
				match(OPEN_PARANTHESIS);
				setState(360);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << T__5) | (1L << MINUS) | (1L << NOT) | (1L << OPEN_PARANTHESIS) | (1L << OPEN_BRACE) | (1L << OPEN_BRACKET) | (1L << TRUE) | (1L << FALSE) | (1L << NULL))) != 0) || ((((_la - 64)) & ~0x3f) == 0 && ((1L << (_la - 64)) & ((1L << (VAR - 64)) | (1L << (INT - 64)) | (1L << (FLOAT - 64)) | (1L << (STRING - 64)))) != 0)) {
					{
					{
					setState(357);
					expr(0);
					}
					}
					setState(362);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(367);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(363);
					match(COMMA);
					setState(364);
					expr(0);
					}
					}
					setState(369);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(370);
				match(CLOSE_PARANTHESIS);
				}
				break;
			case 11:
				_localctx = new ListOppContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(371);
				list_opperations();
				}
				break;
			case 12:
				_localctx = new MapOppsContext(_localctx);
				enterOuterAlt(_localctx, 12);
				{
				setState(372);
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
	public static class ListInitialisationWithElementsContext extends List_opperationsContext {
		public TerminalNode OPEN_BRACKET() { return getToken(WorkflowV2Parser.OPEN_BRACKET, 0); }
		public TerminalNode CLOSE_BRACKET() { return getToken(WorkflowV2Parser.CLOSE_BRACKET, 0); }
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
		public ListInitialisationWithElementsContext(List_opperationsContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterListInitialisationWithElements(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitListInitialisationWithElements(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitListInitialisationWithElements(this);
			else return visitor.visitChildren(this);
		}
	}

	public final List_opperationsContext list_opperations() throws RecognitionException {
		List_opperationsContext _localctx = new List_opperationsContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_list_opperations);
		int _la;
		try {
			int _alt;
			setState(396);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,31,_ctx) ) {
			case 1:
				_localctx = new ListInitialisationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(377); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(375);
						match(OPEN_BRACKET);
						setState(376);
						match(CLOSE_BRACKET);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(379); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,28,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				_localctx = new ListInitialisationWithElementsContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(381);
				match(OPEN_BRACKET);
				setState(383); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(382);
					atom();
					}
					}
					setState(385); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( ((((_la - 4)) & ~0x3f) == 0 && ((1L << (_la - 4)) & ((1L << (T__3 - 4)) | (1L << (T__4 - 4)) | (1L << (T__5 - 4)) | (1L << (OPEN_PARANTHESIS - 4)) | (1L << (OPEN_BRACE - 4)) | (1L << (OPEN_BRACKET - 4)) | (1L << (TRUE - 4)) | (1L << (FALSE - 4)) | (1L << (NULL - 4)) | (1L << (VAR - 4)) | (1L << (INT - 4)) | (1L << (FLOAT - 4)) | (1L << (STRING - 4)))) != 0) );
				setState(391);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(387);
					match(COMMA);
					setState(388);
					atom();
					}
					}
					setState(393);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(394);
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
		enterRule(_localctx, 56, RULE_map_opperations);
		try {
			int _alt;
			_localctx = new MapInitialisationContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(400); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(398);
					match(OPEN_BRACE);
					setState(399);
					match(CLOSE_BRACE);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(402); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,32,_ctx);
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
		enterRule(_localctx, 58, RULE_db_param);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(404);
			match(OPEN_BRACE);
			setState(405);
			db_param_criteria();
			setState(409);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__10) | (1L << T__11) | (1L << T__13) | (1L << T__14))) != 0)) {
				{
				{
				setState(406);
				db_param_group();
				}
				}
				setState(411);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(412);
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
		public Db_param_field_criteriaContext db_param_field_criteria() {
			return getRuleContext(Db_param_field_criteriaContext.class,0);
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
		enterRule(_localctx, 60, RULE_db_param_group);
		try {
			setState(421);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__8:
				enterOuterAlt(_localctx, 1);
				{
				setState(414);
				db_param_field();
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 2);
				{
				setState(415);
				db_param_field_criteria();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 3);
				{
				setState(416);
				db_param_aggr();
				}
				break;
			case T__10:
				enterOuterAlt(_localctx, 4);
				{
				setState(417);
				db_param_limit();
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 5);
				{
				setState(418);
				db_param_range();
				}
				break;
			case T__13:
				enterOuterAlt(_localctx, 6);
				{
				setState(419);
				db_param_group_by();
				}
				break;
			case T__14:
				enterOuterAlt(_localctx, 7);
				{
				setState(420);
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
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
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
		enterRule(_localctx, 62, RULE_db_param_criteria);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(423);
			match(T__6);
			setState(424);
			match(COLON);
			setState(425);
			expr(0);
			setState(429);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(426);
				match(COMMA);
				}
				}
				setState(431);
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

	public static class Db_param_field_criteriaContext extends ParserRuleContext {
		public TerminalNode COLON() { return getToken(WorkflowV2Parser.COLON, 0); }
		public CriteriaContext criteria() {
			return getRuleContext(CriteriaContext.class,0);
		}
		public List<TerminalNode> COMMA() { return getTokens(WorkflowV2Parser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(WorkflowV2Parser.COMMA, i);
		}
		public Db_param_field_criteriaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_db_param_field_criteria; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).enterDb_param_field_criteria(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof WorkflowV2Listener ) ((WorkflowV2Listener)listener).exitDb_param_field_criteria(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof WorkflowV2Visitor ) return ((WorkflowV2Visitor<? extends T>)visitor).visitDb_param_field_criteria(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Db_param_field_criteriaContext db_param_field_criteria() throws RecognitionException {
		Db_param_field_criteriaContext _localctx = new Db_param_field_criteriaContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_db_param_field_criteria);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(432);
			match(T__7);
			setState(433);
			match(COLON);
			setState(434);
			criteria();
			setState(438);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(435);
				match(COMMA);
				}
				}
				setState(440);
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
		enterRule(_localctx, 66, RULE_db_param_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(441);
			match(T__8);
			setState(442);
			match(COLON);
			setState(443);
			expr(0);
			setState(447);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(444);
				match(COMMA);
				}
				}
				setState(449);
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
		enterRule(_localctx, 68, RULE_db_param_aggr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(450);
			match(T__9);
			setState(451);
			match(COLON);
			setState(452);
			expr(0);
			setState(456);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(453);
				match(COMMA);
				}
				}
				setState(458);
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
		enterRule(_localctx, 70, RULE_db_param_limit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(459);
			match(T__10);
			setState(460);
			match(COLON);
			setState(461);
			expr(0);
			setState(465);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(462);
				match(COMMA);
				}
				}
				setState(467);
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
		enterRule(_localctx, 72, RULE_db_param_range);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(468);
			match(T__11);
			setState(469);
			match(COLON);
			setState(470);
			expr(0);
			setState(471);
			match(T__12);
			setState(472);
			expr(0);
			setState(476);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(473);
				match(COMMA);
				}
				}
				setState(478);
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
		enterRule(_localctx, 74, RULE_db_param_group_by);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(479);
			match(T__13);
			setState(480);
			match(COLON);
			setState(481);
			expr(0);
			setState(485);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(482);
				match(COMMA);
				}
				}
				setState(487);
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
		enterRule(_localctx, 76, RULE_db_param_sort);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(488);
			match(T__14);
			setState(489);
			match(COLON);
			setState(490);
			expr(0);
			setState(491);
			((Db_param_sortContext)_localctx).op = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==T__15 || _la==T__16) ) {
				((Db_param_sortContext)_localctx).op = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(495);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(492);
				match(COMMA);
				}
				}
				setState(497);
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
		enterRule(_localctx, 78, RULE_criteria);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(498);
			match(OPEN_BRACKET);
			setState(499);
			condition(0);
			setState(500);
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
		int _startState = 80;
		enterRecursionRule(_localctx, 80, RULE_condition, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(508);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VAR:
				{
				setState(503);
				condition_atom();
				}
				break;
			case OPEN_PARANTHESIS:
				{
				setState(504);
				match(OPEN_PARANTHESIS);
				setState(505);
				condition(0);
				setState(506);
				match(CLOSE_PARANTHESIS);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(515);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,44,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ConditionContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_condition);
					setState(510);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(511);
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
					setState(512);
					condition(3);
					}
					} 
				}
				setState(517);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,44,_ctx);
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
		enterRule(_localctx, 82, RULE_condition_atom);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(518);
			match(VAR);
			setState(519);
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
			setState(520);
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
		case 40:
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
			return precpred(_ctx, 9);
		case 2:
			return precpred(_ctx, 8);
		case 3:
			return precpred(_ctx, 7);
		case 4:
			return precpred(_ctx, 6);
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3I\u020d\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\3"+
		"\2\3\2\3\2\3\3\3\3\3\3\3\3\7\3^\n\3\f\3\16\3a\13\3\3\3\3\3\7\3e\n\3\f"+
		"\3\16\3h\13\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\7\7\7"+
		"w\n\7\f\7\16\7z\13\7\3\b\3\b\3\b\7\b\177\n\b\f\b\16\b\u0082\13\b\3\b\3"+
		"\b\3\b\3\b\7\b\u0088\n\b\f\b\16\b\u008b\13\b\3\b\3\b\3\t\3\t\3\n\3\n\3"+
		"\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\5\13\u009e\n\13"+
		"\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\6\r\u00ae\n\r"+
		"\r\r\16\r\u00af\5\r\u00b2\n\r\3\16\3\16\3\16\3\16\3\16\7\16\u00b9\n\16"+
		"\f\16\16\16\u00bc\13\16\3\16\3\16\5\16\u00c0\n\16\3\17\3\17\3\17\3\20"+
		"\3\20\3\20\3\20\3\20\5\20\u00ca\n\20\3\21\3\21\3\21\3\21\7\21\u00d0\n"+
		"\21\f\21\16\21\u00d3\13\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\23"+
		"\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\25\5\25"+
		"\u00eb\n\25\3\25\3\25\3\25\7\25\u00f0\n\25\f\25\16\25\u00f3\13\25\3\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\5\26\u00ff\n\26\3\26\3\26"+
		"\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\26\7\26\u010d\n\26\f\26"+
		"\16\26\u0110\13\26\3\27\3\27\6\27\u0114\n\27\r\27\16\27\u0115\3\30\3\30"+
		"\3\30\3\30\5\30\u011c\n\30\3\31\3\31\3\32\3\32\3\32\3\32\3\32\5\32\u0125"+
		"\n\32\3\33\3\33\3\33\3\33\7\33\u012b\n\33\f\33\16\33\u012e\13\33\3\33"+
		"\3\33\7\33\u0132\n\33\f\33\16\33\u0135\13\33\3\33\3\33\3\33\3\33\3\33"+
		"\3\33\3\33\5\33\u013e\n\33\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34"+
		"\3\34\3\34\3\34\3\34\7\34\u014d\n\34\f\34\16\34\u0150\13\34\3\34\3\34"+
		"\7\34\u0154\n\34\f\34\16\34\u0157\13\34\3\34\3\34\3\34\3\34\3\34\3\34"+
		"\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\7\34\u0169\n\34\f\34"+
		"\16\34\u016c\13\34\3\34\3\34\7\34\u0170\n\34\f\34\16\34\u0173\13\34\3"+
		"\34\3\34\3\34\5\34\u0178\n\34\3\35\3\35\6\35\u017c\n\35\r\35\16\35\u017d"+
		"\3\35\3\35\6\35\u0182\n\35\r\35\16\35\u0183\3\35\3\35\7\35\u0188\n\35"+
		"\f\35\16\35\u018b\13\35\3\35\3\35\5\35\u018f\n\35\3\36\3\36\6\36\u0193"+
		"\n\36\r\36\16\36\u0194\3\37\3\37\3\37\7\37\u019a\n\37\f\37\16\37\u019d"+
		"\13\37\3\37\3\37\3 \3 \3 \3 \3 \3 \3 \5 \u01a8\n \3!\3!\3!\3!\7!\u01ae"+
		"\n!\f!\16!\u01b1\13!\3\"\3\"\3\"\3\"\7\"\u01b7\n\"\f\"\16\"\u01ba\13\""+
		"\3#\3#\3#\3#\7#\u01c0\n#\f#\16#\u01c3\13#\3$\3$\3$\3$\7$\u01c9\n$\f$\16"+
		"$\u01cc\13$\3%\3%\3%\3%\7%\u01d2\n%\f%\16%\u01d5\13%\3&\3&\3&\3&\3&\3"+
		"&\7&\u01dd\n&\f&\16&\u01e0\13&\3\'\3\'\3\'\3\'\7\'\u01e6\n\'\f\'\16\'"+
		"\u01e9\13\'\3(\3(\3(\3(\3(\7(\u01f0\n(\f(\16(\u01f3\13(\3)\3)\3)\3)\3"+
		"*\3*\3*\3*\3*\3*\5*\u01ff\n*\3*\3*\3*\7*\u0204\n*\f*\16*\u0207\13*\3+"+
		"\3+\3+\3+\3+\2\5(*R,\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\60"+
		"\62\64\668:<>@BDFHJLNPRT\2\f\3\2\24\33\4\2\37\37\"\"\3\2,.\3\2*+\3\2$"+
		")\4\2  ##\3\2BC\3\2CD\3\2;<\3\2\22\23\2\u022e\2V\3\2\2\2\4Y\3\2\2\2\6"+
		"n\3\2\2\2\bp\3\2\2\2\ns\3\2\2\2\fx\3\2\2\2\16{\3\2\2\2\20\u008e\3\2\2"+
		"\2\22\u0090\3\2\2\2\24\u009d\3\2\2\2\26\u009f\3\2\2\2\30\u00b1\3\2\2\2"+
		"\32\u00b3\3\2\2\2\34\u00c1\3\2\2\2\36\u00c9\3\2\2\2 \u00cb\3\2\2\2\"\u00d8"+
		"\3\2\2\2$\u00dc\3\2\2\2&\u00e0\3\2\2\2(\u00ea\3\2\2\2*\u00fe\3\2\2\2,"+
		"\u0111\3\2\2\2.\u011b\3\2\2\2\60\u011d\3\2\2\2\62\u0124\3\2\2\2\64\u013d"+
		"\3\2\2\2\66\u0177\3\2\2\28\u018e\3\2\2\2:\u0192\3\2\2\2<\u0196\3\2\2\2"+
		">\u01a7\3\2\2\2@\u01a9\3\2\2\2B\u01b2\3\2\2\2D\u01bb\3\2\2\2F\u01c4\3"+
		"\2\2\2H\u01cd\3\2\2\2J\u01d6\3\2\2\2L\u01e1\3\2\2\2N\u01ea\3\2\2\2P\u01f4"+
		"\3\2\2\2R\u01fe\3\2\2\2T\u0208\3\2\2\2VW\5\4\3\2WX\7\2\2\3X\3\3\2\2\2"+
		"YZ\5\n\6\2Z[\5\6\4\2[_\7\65\2\2\\^\5\b\5\2]\\\3\2\2\2^a\3\2\2\2_]\3\2"+
		"\2\2_`\3\2\2\2`f\3\2\2\2a_\3\2\2\2bc\7\61\2\2ce\5\b\5\2db\3\2\2\2eh\3"+
		"\2\2\2fd\3\2\2\2fg\3\2\2\2gi\3\2\2\2hf\3\2\2\2ij\7\66\2\2jk\7\67\2\2k"+
		"l\5\f\7\2lm\78\2\2m\5\3\2\2\2no\7B\2\2o\7\3\2\2\2pq\5\n\6\2qr\7B\2\2r"+
		"\t\3\2\2\2st\t\2\2\2t\13\3\2\2\2uw\5\24\13\2vu\3\2\2\2wz\3\2\2\2xv\3\2"+
		"\2\2xy\3\2\2\2y\r\3\2\2\2zx\3\2\2\2{|\7\35\2\2|\u0080\7\67\2\2}\177\5"+
		"\20\t\2~}\3\2\2\2\177\u0082\3\2\2\2\u0080~\3\2\2\2\u0080\u0081\3\2\2\2"+
		"\u0081\u0083\3\2\2\2\u0082\u0080\3\2\2\2\u0083\u0084\78\2\2\u0084\u0085"+
		"\7\36\2\2\u0085\u0089\7\67\2\2\u0086\u0088\5\22\n\2\u0087\u0086\3\2\2"+
		"\2\u0088\u008b\3\2\2\2\u0089\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a\u008c"+
		"\3\2\2\2\u008b\u0089\3\2\2\2\u008c\u008d\78\2\2\u008d\17\3\2\2\2\u008e"+
		"\u008f\5\24\13\2\u008f\21\3\2\2\2\u0090\u0091\5\24\13\2\u0091\23\3\2\2"+
		"\2\u0092\u009e\5\26\f\2\u0093\u009e\5\32\16\2\u0094\u009e\5 \21\2\u0095"+
		"\u009e\5\"\22\2\u0096\u0097\5,\27\2\u0097\u0098\7\62\2\2\u0098\u009e\3"+
		"\2\2\2\u0099\u009e\5$\23\2\u009a\u009e\5\16\b\2\u009b\u009c\7I\2\2\u009c"+
		"\u009e\b\13\1\2\u009d\u0092\3\2\2\2\u009d\u0093\3\2\2\2\u009d\u0094\3"+
		"\2\2\2\u009d\u0095\3\2\2\2\u009d\u0096\3\2\2\2\u009d\u0099\3\2\2\2\u009d"+
		"\u009a\3\2\2\2\u009d\u009b\3\2\2\2\u009e\25\3\2\2\2\u009f\u00a0\5\30\r"+
		"\2\u00a0\u00a1\7\64\2\2\u00a1\u00a2\5*\26\2\u00a2\u00a3\7\62\2\2\u00a3"+
		"\27\3\2\2\2\u00a4\u00b2\7B\2\2\u00a5\u00a6\7B\2\2\u00a6\u00a7\79\2\2\u00a7"+
		"\u00a8\5*\26\2\u00a8\u00a9\7:\2\2\u00a9\u00b2\3\2\2\2\u00aa\u00ad\7B\2"+
		"\2\u00ab\u00ac\7!\2\2\u00ac\u00ae\7B\2\2\u00ad\u00ab\3\2\2\2\u00ae\u00af"+
		"\3\2\2\2\u00af\u00ad\3\2\2\2\u00af\u00b0\3\2\2\2\u00b0\u00b2\3\2\2\2\u00b1"+
		"\u00a4\3\2\2\2\u00b1\u00a5\3\2\2\2\u00b1\u00aa\3\2\2\2\u00b2\31\3\2\2"+
		"\2\u00b3\u00b4\7>\2\2\u00b4\u00ba\5\34\17\2\u00b5\u00b6\7?\2\2\u00b6\u00b7"+
		"\7>\2\2\u00b7\u00b9\5\34\17\2\u00b8\u00b5\3\2\2\2\u00b9\u00bc\3\2\2\2"+
		"\u00ba\u00b8\3\2\2\2\u00ba\u00bb\3\2\2\2\u00bb\u00bf\3\2\2\2\u00bc\u00ba"+
		"\3\2\2\2\u00bd\u00be\7?\2\2\u00be\u00c0\5\36\20\2\u00bf\u00bd\3\2\2\2"+
		"\u00bf\u00c0\3\2\2\2\u00c0\33\3\2\2\2\u00c1\u00c2\5&\24\2\u00c2\u00c3"+
		"\5\36\20\2\u00c3\35\3\2\2\2\u00c4\u00c5\7\67\2\2\u00c5\u00c6\5\f\7\2\u00c6"+
		"\u00c7\78\2\2\u00c7\u00ca\3\2\2\2\u00c8\u00ca\5\24\13\2\u00c9\u00c4\3"+
		"\2\2\2\u00c9\u00c8\3\2\2\2\u00ca\37\3\2\2\2\u00cb\u00cc\7@\2\2\u00cc\u00d1"+
		"\7B\2\2\u00cd\u00ce\7\61\2\2\u00ce\u00d0\7B\2\2\u00cf\u00cd\3\2\2\2\u00d0"+
		"\u00d3\3\2\2\2\u00d1\u00cf\3\2\2\2\u00d1\u00d2\3\2\2\2\u00d2\u00d4\3\2"+
		"\2\2\u00d3\u00d1\3\2\2\2\u00d4\u00d5\7\3\2\2\u00d5\u00d6\5*\26\2\u00d6"+
		"\u00d7\5\36\20\2\u00d7!\3\2\2\2\u00d8\u00d9\7A\2\2\u00d9\u00da\5*\26\2"+
		"\u00da\u00db\7\62\2\2\u00db#\3\2\2\2\u00dc\u00dd\7\34\2\2\u00dd\u00de"+
		"\5*\26\2\u00de\u00df\7\62\2\2\u00df%\3\2\2\2\u00e0\u00e1\7\65\2\2\u00e1"+
		"\u00e2\5(\25\2\u00e2\u00e3\7\66\2\2\u00e3\'\3\2\2\2\u00e4\u00e5\b\25\1"+
		"\2\u00e5\u00eb\5*\26\2\u00e6\u00e7\7\65\2\2\u00e7\u00e8\5(\25\2\u00e8"+
		"\u00e9\7\66\2\2\u00e9\u00eb\3\2\2\2\u00ea\u00e4\3\2\2\2\u00ea\u00e6\3"+
		"\2\2\2\u00eb\u00f1\3\2\2\2\u00ec\u00ed\f\4\2\2\u00ed\u00ee\t\3\2\2\u00ee"+
		"\u00f0\5(\25\5\u00ef\u00ec\3\2\2\2\u00f0\u00f3\3\2\2\2\u00f1\u00ef\3\2"+
		"\2\2\u00f1\u00f2\3\2\2\2\u00f2)\3\2\2\2\u00f3\u00f1\3\2\2\2\u00f4\u00f5"+
		"\b\26\1\2\u00f5\u00f6\7+\2\2\u00f6\u00ff\5*\26\r\u00f7\u00f8\7\60\2\2"+
		"\u00f8\u00ff\5*\26\f\u00f9\u00ff\5\66\34\2\u00fa\u00ff\5.\30\2\u00fb\u00ff"+
		"\5,\27\2\u00fc\u00ff\5<\37\2\u00fd\u00ff\5P)\2\u00fe\u00f4\3\2\2\2\u00fe"+
		"\u00f7\3\2\2\2\u00fe\u00f9\3\2\2\2\u00fe\u00fa\3\2\2\2\u00fe\u00fb\3\2"+
		"\2\2\u00fe\u00fc\3\2\2\2\u00fe\u00fd\3\2\2\2\u00ff\u010e\3\2\2\2\u0100"+
		"\u0101\f\13\2\2\u0101\u0102\t\4\2\2\u0102\u010d\5*\26\f\u0103\u0104\f"+
		"\n\2\2\u0104\u0105\t\5\2\2\u0105\u010d\5*\26\13\u0106\u0107\f\t\2\2\u0107"+
		"\u0108\t\6\2\2\u0108\u010d\5*\26\n\u0109\u010a\f\b\2\2\u010a\u010b\t\7"+
		"\2\2\u010b\u010d\5*\26\t\u010c\u0100\3\2\2\2\u010c\u0103\3\2\2\2\u010c"+
		"\u0106\3\2\2\2\u010c\u0109\3\2\2\2\u010d\u0110\3\2\2\2\u010e\u010c\3\2"+
		"\2\2\u010e\u010f\3\2\2\2\u010f+\3\2\2\2\u0110\u010e\3\2\2\2\u0111\u0113"+
		"\5\66\34\2\u0112\u0114\5\64\33\2\u0113\u0112\3\2\2\2\u0114\u0115\3\2\2"+
		"\2\u0115\u0113\3\2\2\2\u0115\u0116\3\2\2\2\u0116-\3\2\2\2\u0117\u0118"+
		"\7\4\2\2\u0118\u011c\5\60\31\2\u0119\u011a\7\5\2\2\u011a\u011c\5\62\32"+
		"\2\u011b\u0117\3\2\2\2\u011b\u0119\3\2\2\2\u011c/\3\2\2\2\u011d\u011e"+
		"\t\b\2\2\u011e\61\3\2\2\2\u011f\u0125\7B\2\2\u0120\u0125\7C\2\2\u0121"+
		"\u0122\7C\2\2\u0122\u0123\7\63\2\2\u0123\u0125\7C\2\2\u0124\u011f\3\2"+
		"\2\2\u0124\u0120\3\2\2\2\u0124\u0121\3\2\2\2\u0125\63\3\2\2\2\u0126\u0127"+
		"\7!\2\2\u0127\u0128\7B\2\2\u0128\u012c\7\65\2\2\u0129\u012b\5*\26\2\u012a"+
		"\u0129\3\2\2\2\u012b\u012e\3\2\2\2\u012c\u012a\3\2\2\2\u012c\u012d\3\2"+
		"\2\2\u012d\u0133\3\2\2\2\u012e\u012c\3\2\2\2\u012f\u0130\7\61\2\2\u0130"+
		"\u0132\5*\26\2\u0131\u012f\3\2\2\2\u0132\u0135\3\2\2\2\u0133\u0131\3\2"+
		"\2\2\u0133\u0134\3\2\2\2\u0134\u0136\3\2\2\2\u0135\u0133\3\2\2\2\u0136"+
		"\u013e\7\66\2\2\u0137\u0138\7!\2\2\u0138\u013e\7B\2\2\u0139\u013a\79\2"+
		"\2\u013a\u013b\5\66\34\2\u013b\u013c\7:\2\2\u013c\u013e\3\2\2\2\u013d"+
		"\u0126\3\2\2\2\u013d\u0137\3\2\2\2\u013d\u0139\3\2\2\2\u013e\65\3\2\2"+
		"\2\u013f\u0140\7\65\2\2\u0140\u0141\5*\26\2\u0141\u0142\7\66\2\2\u0142"+
		"\u0178\3\2\2\2\u0143\u0178\t\t\2\2\u0144\u0178\t\n\2\2\u0145\u0178\7E"+
		"\2\2\u0146\u0178\7=\2\2\u0147\u0178\7B\2\2\u0148\u0149\7\6\2\2\u0149\u014a"+
		"\7B\2\2\u014a\u014e\7\65\2\2\u014b\u014d\5*\26\2\u014c\u014b\3\2\2\2\u014d"+
		"\u0150\3\2\2\2\u014e\u014c\3\2\2\2\u014e\u014f\3\2\2\2\u014f\u0155\3\2"+
		"\2\2\u0150\u014e\3\2\2\2\u0151\u0152\7\61\2\2\u0152\u0154\5*\26\2\u0153"+
		"\u0151\3\2\2\2\u0154\u0157\3\2\2\2\u0155\u0153\3\2\2\2\u0155\u0156\3\2"+
		"\2\2\u0156\u0158\3\2\2\2\u0157\u0155\3\2\2\2\u0158\u0178\7\66\2\2\u0159"+
		"\u015a\7\7\2\2\u015a\u015b\7\65\2\2\u015b\u015c\5*\26\2\u015c\u015d\7"+
		"\66\2\2\u015d\u0178\3\2\2\2\u015e\u015f\7\b\2\2\u015f\u0160\7\65\2\2\u0160"+
		"\u0161\5*\26\2\u0161\u0162\7\61\2\2\u0162\u0163\5*\26\2\u0163\u0164\7"+
		"\66\2\2\u0164\u0178\3\2\2\2\u0165\u0166\7B\2\2\u0166\u016a\7\65\2\2\u0167"+
		"\u0169\5*\26\2\u0168\u0167\3\2\2\2\u0169\u016c\3\2\2\2\u016a\u0168\3\2"+
		"\2\2\u016a\u016b\3\2\2\2\u016b\u0171\3\2\2\2\u016c\u016a\3\2\2\2\u016d"+
		"\u016e\7\61\2\2\u016e\u0170\5*\26\2\u016f\u016d\3\2\2\2\u0170\u0173\3"+
		"\2\2\2\u0171\u016f\3\2\2\2\u0171\u0172\3\2\2\2\u0172\u0174\3\2\2\2\u0173"+
		"\u0171\3\2\2\2\u0174\u0178\7\66\2\2\u0175\u0178\58\35\2\u0176\u0178\5"+
		":\36\2\u0177\u013f\3\2\2\2\u0177\u0143\3\2\2\2\u0177\u0144\3\2\2\2\u0177"+
		"\u0145\3\2\2\2\u0177\u0146\3\2\2\2\u0177\u0147\3\2\2\2\u0177\u0148\3\2"+
		"\2\2\u0177\u0159\3\2\2\2\u0177\u015e\3\2\2\2\u0177\u0165\3\2\2\2\u0177"+
		"\u0175\3\2\2\2\u0177\u0176\3\2\2\2\u0178\67\3\2\2\2\u0179\u017a\79\2\2"+
		"\u017a\u017c\7:\2\2\u017b\u0179\3\2\2\2\u017c\u017d\3\2\2\2\u017d\u017b"+
		"\3\2\2\2\u017d\u017e\3\2\2\2\u017e\u018f\3\2\2\2\u017f\u0181\79\2\2\u0180"+
		"\u0182\5\66\34\2\u0181\u0180\3\2\2\2\u0182\u0183\3\2\2\2\u0183\u0181\3"+
		"\2\2\2\u0183\u0184\3\2\2\2\u0184\u0189\3\2\2\2\u0185\u0186\7\61\2\2\u0186"+
		"\u0188\5\66\34\2\u0187\u0185\3\2\2\2\u0188\u018b\3\2\2\2\u0189\u0187\3"+
		"\2\2\2\u0189\u018a\3\2\2\2\u018a\u018c\3\2\2\2\u018b\u0189\3\2\2\2\u018c"+
		"\u018d\7:\2\2\u018d\u018f\3\2\2\2\u018e\u017b\3\2\2\2\u018e\u017f\3\2"+
		"\2\2\u018f9\3\2\2\2\u0190\u0191\7\67\2\2\u0191\u0193\78\2\2\u0192\u0190"+
		"\3\2\2\2\u0193\u0194\3\2\2\2\u0194\u0192\3\2\2\2\u0194\u0195\3\2\2\2\u0195"+
		";\3\2\2\2\u0196\u0197\7\67\2\2\u0197\u019b\5@!\2\u0198\u019a\5> \2\u0199"+
		"\u0198\3\2\2\2\u019a\u019d\3\2\2\2\u019b\u0199\3\2\2\2\u019b\u019c\3\2"+
		"\2\2\u019c\u019e\3\2\2\2\u019d\u019b\3\2\2\2\u019e\u019f\78\2\2\u019f"+
		"=\3\2\2\2\u01a0\u01a8\5D#\2\u01a1\u01a8\5B\"\2\u01a2\u01a8\5F$\2\u01a3"+
		"\u01a8\5H%\2\u01a4\u01a8\5J&\2\u01a5\u01a8\5L\'\2\u01a6\u01a8\5N(\2\u01a7"+
		"\u01a0\3\2\2\2\u01a7\u01a1\3\2\2\2\u01a7\u01a2\3\2\2\2\u01a7\u01a3\3\2"+
		"\2\2\u01a7\u01a4\3\2\2\2\u01a7\u01a5\3\2\2\2\u01a7\u01a6\3\2\2\2\u01a8"+
		"?\3\2\2\2\u01a9\u01aa\7\t\2\2\u01aa\u01ab\7\63\2\2\u01ab\u01af\5*\26\2"+
		"\u01ac\u01ae\7\61\2\2\u01ad\u01ac\3\2\2\2\u01ae\u01b1\3\2\2\2\u01af\u01ad"+
		"\3\2\2\2\u01af\u01b0\3\2\2\2\u01b0A\3\2\2\2\u01b1\u01af\3\2\2\2\u01b2"+
		"\u01b3\7\n\2\2\u01b3\u01b4\7\63\2\2\u01b4\u01b8\5P)\2\u01b5\u01b7\7\61"+
		"\2\2\u01b6\u01b5\3\2\2\2\u01b7\u01ba\3\2\2\2\u01b8\u01b6\3\2\2\2\u01b8"+
		"\u01b9\3\2\2\2\u01b9C\3\2\2\2\u01ba\u01b8\3\2\2\2\u01bb\u01bc\7\13\2\2"+
		"\u01bc\u01bd\7\63\2\2\u01bd\u01c1\5*\26\2\u01be\u01c0\7\61\2\2\u01bf\u01be"+
		"\3\2\2\2\u01c0\u01c3\3\2\2\2\u01c1\u01bf\3\2\2\2\u01c1\u01c2\3\2\2\2\u01c2"+
		"E\3\2\2\2\u01c3\u01c1\3\2\2\2\u01c4\u01c5\7\f\2\2\u01c5\u01c6\7\63\2\2"+
		"\u01c6\u01ca\5*\26\2\u01c7\u01c9\7\61\2\2\u01c8\u01c7\3\2\2\2\u01c9\u01cc"+
		"\3\2\2\2\u01ca\u01c8\3\2\2\2\u01ca\u01cb\3\2\2\2\u01cbG\3\2\2\2\u01cc"+
		"\u01ca\3\2\2\2\u01cd\u01ce\7\r\2\2\u01ce\u01cf\7\63\2\2\u01cf\u01d3\5"+
		"*\26\2\u01d0\u01d2\7\61\2\2\u01d1\u01d0\3\2\2\2\u01d2\u01d5\3\2\2\2\u01d3"+
		"\u01d1\3\2\2\2\u01d3\u01d4\3\2\2\2\u01d4I\3\2\2\2\u01d5\u01d3\3\2\2\2"+
		"\u01d6\u01d7\7\16\2\2\u01d7\u01d8\7\63\2\2\u01d8\u01d9\5*\26\2\u01d9\u01da"+
		"\7\17\2\2\u01da\u01de\5*\26\2\u01db\u01dd\7\61\2\2\u01dc\u01db\3\2\2\2"+
		"\u01dd\u01e0\3\2\2\2\u01de\u01dc\3\2\2\2\u01de\u01df\3\2\2\2\u01dfK\3"+
		"\2\2\2\u01e0\u01de\3\2\2\2\u01e1\u01e2\7\20\2\2\u01e2\u01e3\7\63\2\2\u01e3"+
		"\u01e7\5*\26\2\u01e4\u01e6\7\61\2\2\u01e5\u01e4\3\2\2\2\u01e6\u01e9\3"+
		"\2\2\2\u01e7\u01e5\3\2\2\2\u01e7\u01e8\3\2\2\2\u01e8M\3\2\2\2\u01e9\u01e7"+
		"\3\2\2\2\u01ea\u01eb\7\21\2\2\u01eb\u01ec\7\63\2\2\u01ec\u01ed\5*\26\2"+
		"\u01ed\u01f1\t\13\2\2\u01ee\u01f0\7\61\2\2\u01ef\u01ee\3\2\2\2\u01f0\u01f3"+
		"\3\2\2\2\u01f1\u01ef\3\2\2\2\u01f1\u01f2\3\2\2\2\u01f2O\3\2\2\2\u01f3"+
		"\u01f1\3\2\2\2\u01f4\u01f5\79\2\2\u01f5\u01f6\5R*\2\u01f6\u01f7\7:\2\2"+
		"\u01f7Q\3\2\2\2\u01f8\u01f9\b*\1\2\u01f9\u01ff\5T+\2\u01fa\u01fb\7\65"+
		"\2\2\u01fb\u01fc\5R*\2\u01fc\u01fd\7\66\2\2\u01fd\u01ff\3\2\2\2\u01fe"+
		"\u01f8\3\2\2\2\u01fe\u01fa\3\2\2\2\u01ff\u0205\3\2\2\2\u0200\u0201\f\4"+
		"\2\2\u0201\u0202\t\3\2\2\u0202\u0204\5R*\5\u0203\u0200\3\2\2\2\u0204\u0207"+
		"\3\2\2\2\u0205\u0203\3\2\2\2\u0205\u0206\3\2\2\2\u0206S\3\2\2\2\u0207"+
		"\u0205\3\2\2\2\u0208\u0209\7B\2\2\u0209\u020a\t\6\2\2\u020a\u020b\5*\26"+
		"\2\u020bU\3\2\2\2/_fx\u0080\u0089\u009d\u00af\u00b1\u00ba\u00bf\u00c9"+
		"\u00d1\u00ea\u00f1\u00fe\u010c\u010e\u0115\u011b\u0124\u012c\u0133\u013d"+
		"\u014e\u0155\u016a\u0171\u0177\u017d\u0183\u0189\u018e\u0194\u019b\u01a7"+
		"\u01af\u01b8\u01c1\u01ca\u01d3\u01de\u01e7\u01f1\u01fe\u0205";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}