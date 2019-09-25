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
		RETURN=22, OR=23, DOT=24, AND=25, EQ=26, NEQ=27, GT=28, LT=29, GTEQ=30, 
		LTEQ=31, PLUS=32, MINUS=33, MULT=34, DIV=35, MOD=36, POW=37, NOT=38, COMMA=39, 
		SEMICOLON=40, COLON=41, ASSIGN=42, OPEN_PARANTHESIS=43, CLOSE_PARANTHESIS=44, 
		OPEN_BRACE=45, CLOSE_BRACE=46, OPEN_BRACKET=47, CLOSE_BRACKET=48, TRUE=49, 
		FALSE=50, NULL=51, IF=52, ELSE=53, FOR_EACH=54, LOG=55, VAR=56, INT=57, 
		FLOAT=58, STRING=59, COMMENT=60, SPACE=61, OTHER=62;
	public static final int
		RULE_parse = 0, RULE_function_block = 1, RULE_function_name_declare = 2, 
		RULE_function_param = 3, RULE_data_type = 4, RULE_block = 5, RULE_statement = 6, 
		RULE_assignment = 7, RULE_if_statement = 8, RULE_condition_block = 9, 
		RULE_statement_block = 10, RULE_for_each_statement = 11, RULE_log = 12, 
		RULE_function_return = 13, RULE_boolean_expr = 14, RULE_boolean_expr_atom = 15, 
		RULE_expr = 16, RULE_stand_alone_expr = 17, RULE_recursive_expression = 18, 
		RULE_atom = 19, RULE_list_opperations = 20, RULE_map_opperations = 21, 
		RULE_db_param = 22, RULE_db_param_group = 23, RULE_db_param_criteria = 24, 
		RULE_db_param_field = 25, RULE_db_param_aggr = 26, RULE_db_param_limit = 27, 
		RULE_db_param_range = 28, RULE_db_param_group_by = 29, RULE_db_param_sort = 30, 
		RULE_criteria = 31, RULE_condition = 32, RULE_condition_atom = 33;
	private static String[] makeRuleNames() {
		return new String[] {
			"parse", "function_block", "function_name_declare", "function_param", 
			"data_type", "block", "statement", "assignment", "if_statement", "condition_block", 
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
			"'Boolean'", "'Map'", "'List'", "'return'", "'||'", "'.'", "'&&'", "'=='", 
			"'!='", "'>'", "'<'", "'>='", "'<='", "'+'", "'-'", "'*'", "'/'", "'%'", 
			"'^'", "'!'", "','", "';'", "':'", "'='", "'('", "')'", "'{'", "'}'", 
			"'['", "']'", "'true'", "'false'", "'null'", "'if'", "'else'", "'for each'", 
			"'log'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, "VOID", "DATA_TYPE_STRING", "DATA_TYPE_NUMBER", 
			"DATA_TYPE_BOOLEAN", "DATA_TYPE_MAP", "DATA_TYPE_LIST", "RETURN", "OR", 
			"DOT", "AND", "EQ", "NEQ", "GT", "LT", "GTEQ", "LTEQ", "PLUS", "MINUS", 
			"MULT", "DIV", "MOD", "POW", "NOT", "COMMA", "SEMICOLON", "COLON", "ASSIGN", 
			"OPEN_PARANTHESIS", "CLOSE_PARANTHESIS", "OPEN_BRACE", "CLOSE_BRACE", 
			"OPEN_BRACKET", "CLOSE_BRACKET", "TRUE", "FALSE", "NULL", "IF", "ELSE", 
			"FOR_EACH", "LOG", "VAR", "INT", "FLOAT", "STRING", "COMMENT", "SPACE", 
			"OTHER"
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
			setState(68);
			function_block();
			setState(69);
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
			setState(71);
			data_type();
			setState(72);
			function_name_declare();
			setState(73);
			match(OPEN_PARANTHESIS);
			setState(77);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VOID) | (1L << DATA_TYPE_STRING) | (1L << DATA_TYPE_NUMBER) | (1L << DATA_TYPE_BOOLEAN) | (1L << DATA_TYPE_MAP) | (1L << DATA_TYPE_LIST))) != 0)) {
				{
				{
				setState(74);
				function_param();
				}
				}
				setState(79);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(84);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(80);
				match(COMMA);
				setState(81);
				function_param();
				}
				}
				setState(86);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(87);
			match(CLOSE_PARANTHESIS);
			setState(88);
			match(OPEN_BRACE);
			setState(89);
			block();
			setState(90);
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
			setState(92);
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
			setState(94);
			data_type();
			setState(95);
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
			setState(97);
			((Data_typeContext)_localctx).op = _input.LT(1);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << VOID) | (1L << DATA_TYPE_STRING) | (1L << DATA_TYPE_NUMBER) | (1L << DATA_TYPE_BOOLEAN) | (1L << DATA_TYPE_MAP) | (1L << DATA_TYPE_LIST))) != 0)) ) {
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
			setState(102);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << RETURN) | (1L << OPEN_PARANTHESIS) | (1L << OPEN_BRACE) | (1L << OPEN_BRACKET) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << IF) | (1L << FOR_EACH) | (1L << LOG) | (1L << VAR) | (1L << INT) | (1L << FLOAT) | (1L << STRING) | (1L << OTHER))) != 0)) {
				{
				{
				setState(99);
				statement();
				}
				}
				setState(104);
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
			setState(115);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(105);
				assignment();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(106);
				if_statement();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(107);
				for_each_statement();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(108);
				log();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(109);
				stand_alone_expr();
				setState(110);
				match(SEMICOLON);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(112);
				function_return();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(113);
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
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode SEMICOLON() { return getToken(WorkflowV2Parser.SEMICOLON, 0); }
		public List<TerminalNode> OPEN_BRACKET() { return getTokens(WorkflowV2Parser.OPEN_BRACKET); }
		public TerminalNode OPEN_BRACKET(int i) {
			return getToken(WorkflowV2Parser.OPEN_BRACKET, i);
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
			setState(117);
			match(VAR);
			setState(124);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OPEN_BRACKET) {
				{
				{
				setState(118);
				match(OPEN_BRACKET);
				setState(119);
				expr(0);
				setState(120);
				match(CLOSE_BRACKET);
				}
				}
				setState(126);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(127);
			match(ASSIGN);
			setState(128);
			expr(0);
			setState(129);
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
			setState(131);
			match(IF);
			setState(132);
			condition_block();
			setState(138);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(133);
					match(ELSE);
					setState(134);
					match(IF);
					setState(135);
					condition_block();
					}
					} 
				}
				setState(140);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			}
			setState(143);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,6,_ctx) ) {
			case 1:
				{
				setState(141);
				match(ELSE);
				setState(142);
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
		enterRule(_localctx, 18, RULE_condition_block);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(145);
			boolean_expr();
			setState(146);
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
			setState(153);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(148);
				match(OPEN_BRACE);
				setState(149);
				block();
				setState(150);
				match(CLOSE_BRACE);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(152);
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
			setState(155);
			match(FOR_EACH);
			setState(156);
			match(VAR);
			setState(161);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(157);
				match(COMMA);
				setState(158);
				match(VAR);
				}
				}
				setState(163);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(164);
			match(T__0);
			setState(165);
			expr(0);
			setState(166);
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
			setState(168);
			match(LOG);
			setState(169);
			expr(0);
			setState(170);
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
			setState(172);
			match(RETURN);
			setState(173);
			expr(0);
			setState(174);
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
		enterRule(_localctx, 28, RULE_boolean_expr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(176);
			match(OPEN_PARANTHESIS);
			setState(177);
			boolean_expr_atom(0);
			setState(178);
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
		int _startState = 30;
		enterRecursionRule(_localctx, 30, RULE_boolean_expr_atom, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(186);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				_localctx = new ExprForBooleanContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(181);
				expr(0);
				}
				break;
			case 2:
				{
				_localctx = new BoolExprParanthesisContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(182);
				match(OPEN_PARANTHESIS);
				setState(183);
				boolean_expr_atom(0);
				setState(184);
				match(CLOSE_PARANTHESIS);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(193);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new BooleanExprCalculationContext(new Boolean_expr_atomContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_boolean_expr_atom);
					setState(188);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(189);
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
					setState(190);
					boolean_expr_atom(3);
					}
					} 
				}
				setState(195);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
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
		int _startState = 32;
		enterRecursionRule(_localctx, 32, RULE_expr, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(205);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				{
				_localctx = new UnaryMinusExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(197);
				match(MINUS);
				setState(198);
				expr(9);
				}
				break;
			case 2:
				{
				_localctx = new NotExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(199);
				match(NOT);
				setState(200);
				expr(8);
				}
				break;
			case 3:
				{
				_localctx = new AtomExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(201);
				atom();
				}
				break;
			case 4:
				{
				_localctx = new StandAloneStatementsContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(202);
				stand_alone_expr();
				}
				break;
			case 5:
				{
				_localctx = new DbParamInitializationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(203);
				db_param();
				}
				break;
			case 6:
				{
				_localctx = new CriteriaInitializationContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(204);
				criteria();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(218);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,13,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(216);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
					case 1:
						{
						_localctx = new ArithmeticFirstPrecedenceExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(207);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(208);
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
						setState(209);
						expr(8);
						}
						break;
					case 2:
						{
						_localctx = new ArithmeticSecondPrecedenceExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(210);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(211);
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
						setState(212);
						expr(7);
						}
						break;
					case 3:
						{
						_localctx = new RelationalExprContext(new ExprContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expr);
						setState(213);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(214);
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
						setState(215);
						expr(6);
						}
						break;
					}
					} 
				}
				setState(220);
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
		enterRule(_localctx, 34, RULE_stand_alone_expr);
		try {
			int _alt;
			_localctx = new Recursive_exprContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(221);
			atom();
			setState(223); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(222);
					recursive_expression();
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(225); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,14,_ctx);
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
		enterRule(_localctx, 36, RULE_recursive_expression);
		int _la;
		try {
			setState(250);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(227);
				match(DOT);
				setState(228);
				match(VAR);
				setState(229);
				match(OPEN_PARANTHESIS);
				setState(233);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__1) | (1L << T__2) | (1L << T__3) | (1L << T__4) | (1L << MINUS) | (1L << NOT) | (1L << OPEN_PARANTHESIS) | (1L << OPEN_BRACE) | (1L << OPEN_BRACKET) | (1L << TRUE) | (1L << FALSE) | (1L << NULL) | (1L << VAR) | (1L << INT) | (1L << FLOAT) | (1L << STRING))) != 0)) {
					{
					{
					setState(230);
					expr(0);
					}
					}
					setState(235);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(240);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(236);
					match(COMMA);
					setState(237);
					expr(0);
					}
					}
					setState(242);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(243);
				match(CLOSE_PARANTHESIS);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(244);
				match(DOT);
				setState(245);
				match(VAR);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(246);
				match(OPEN_BRACKET);
				setState(247);
				atom();
				setState(248);
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
		enterRule(_localctx, 38, RULE_atom);
		int _la;
		try {
			int _alt;
			setState(292);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				_localctx = new ParanthesisExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(252);
				match(OPEN_PARANTHESIS);
				setState(253);
				expr(0);
				setState(254);
				match(CLOSE_PARANTHESIS);
				}
				break;
			case 2:
				_localctx = new NumberAtomContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(256);
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
				setState(257);
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
				setState(258);
				match(STRING);
				}
				break;
			case 5:
				_localctx = new NullAtomContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(259);
				match(NULL);
				}
				break;
			case 6:
				_localctx = new VarAtomContext(_localctx);
				enterOuterAlt(_localctx, 6);
				{
				setState(260);
				match(VAR);
				}
				break;
			case 7:
				_localctx = new NameSpaceInitializationContext(_localctx);
				enterOuterAlt(_localctx, 7);
				{
				setState(261);
				match(T__1);
				setState(262);
				match(OPEN_PARANTHESIS);
				setState(263);
				expr(0);
				setState(264);
				match(CLOSE_PARANTHESIS);
				}
				break;
			case 8:
				_localctx = new CustomModuleInitializationContext(_localctx);
				enterOuterAlt(_localctx, 8);
				{
				setState(266);
				match(T__2);
				setState(267);
				match(OPEN_PARANTHESIS);
				setState(268);
				expr(0);
				setState(269);
				match(CLOSE_PARANTHESIS);
				}
				break;
			case 9:
				_localctx = new ConnectionInitializationContext(_localctx);
				enterOuterAlt(_localctx, 9);
				{
				setState(271);
				match(T__3);
				setState(272);
				match(OPEN_PARANTHESIS);
				setState(273);
				expr(0);
				setState(274);
				match(CLOSE_PARANTHESIS);
				}
				break;
			case 10:
				_localctx = new ReadingInitializationContext(_localctx);
				enterOuterAlt(_localctx, 10);
				{
				setState(276);
				match(T__4);
				setState(277);
				match(OPEN_PARANTHESIS);
				setState(278);
				expr(0);
				setState(279);
				match(COMMA);
				setState(280);
				expr(0);
				setState(281);
				match(CLOSE_PARANTHESIS);
				}
				break;
			case 11:
				_localctx = new ModuleAndSystemNameSpaceInitializationContext(_localctx);
				enterOuterAlt(_localctx, 11);
				{
				setState(286); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(283);
						match(VAR);
						setState(284);
						match(OPEN_PARANTHESIS);
						setState(285);
						match(CLOSE_PARANTHESIS);
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(288); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,18,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 12:
				_localctx = new ListOppContext(_localctx);
				enterOuterAlt(_localctx, 12);
				{
				setState(290);
				list_opperations();
				}
				break;
			case 13:
				_localctx = new MapOppsContext(_localctx);
				enterOuterAlt(_localctx, 13);
				{
				setState(291);
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
		enterRule(_localctx, 40, RULE_list_opperations);
		try {
			int _alt;
			_localctx = new ListInitialisationContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(296); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(294);
					match(OPEN_BRACKET);
					setState(295);
					match(CLOSE_BRACKET);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(298); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
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
		enterRule(_localctx, 42, RULE_map_opperations);
		try {
			int _alt;
			_localctx = new MapInitialisationContext(_localctx);
			enterOuterAlt(_localctx, 1);
			{
			setState(302); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(300);
					match(OPEN_BRACE);
					setState(301);
					match(CLOSE_BRACE);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(304); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,21,_ctx);
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
		enterRule(_localctx, 44, RULE_db_param);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(306);
			match(OPEN_BRACE);
			setState(307);
			db_param_criteria();
			setState(311);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__6) | (1L << T__7) | (1L << T__8) | (1L << T__9) | (1L << T__11) | (1L << T__12))) != 0)) {
				{
				{
				setState(308);
				db_param_group();
				}
				}
				setState(313);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(314);
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
		enterRule(_localctx, 46, RULE_db_param_group);
		try {
			setState(322);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__6:
				enterOuterAlt(_localctx, 1);
				{
				setState(316);
				db_param_field();
				}
				break;
			case T__7:
				enterOuterAlt(_localctx, 2);
				{
				setState(317);
				db_param_aggr();
				}
				break;
			case T__8:
				enterOuterAlt(_localctx, 3);
				{
				setState(318);
				db_param_limit();
				}
				break;
			case T__9:
				enterOuterAlt(_localctx, 4);
				{
				setState(319);
				db_param_range();
				}
				break;
			case T__11:
				enterOuterAlt(_localctx, 5);
				{
				setState(320);
				db_param_group_by();
				}
				break;
			case T__12:
				enterOuterAlt(_localctx, 6);
				{
				setState(321);
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
		enterRule(_localctx, 48, RULE_db_param_criteria);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(324);
			match(T__5);
			setState(325);
			match(COLON);
			setState(326);
			criteria();
			setState(330);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(327);
				match(COMMA);
				}
				}
				setState(332);
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
		enterRule(_localctx, 50, RULE_db_param_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(333);
			match(T__6);
			setState(334);
			match(COLON);
			setState(335);
			expr(0);
			setState(339);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(336);
				match(COMMA);
				}
				}
				setState(341);
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
		enterRule(_localctx, 52, RULE_db_param_aggr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(342);
			match(T__7);
			setState(343);
			match(COLON);
			setState(344);
			expr(0);
			setState(348);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(345);
				match(COMMA);
				}
				}
				setState(350);
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
		enterRule(_localctx, 54, RULE_db_param_limit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(351);
			match(T__8);
			setState(352);
			match(COLON);
			setState(353);
			expr(0);
			setState(357);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(354);
				match(COMMA);
				}
				}
				setState(359);
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
		enterRule(_localctx, 56, RULE_db_param_range);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(360);
			match(T__9);
			setState(361);
			match(COLON);
			setState(362);
			expr(0);
			setState(363);
			match(T__10);
			setState(364);
			expr(0);
			setState(368);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(365);
				match(COMMA);
				}
				}
				setState(370);
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
		enterRule(_localctx, 58, RULE_db_param_group_by);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(371);
			match(T__11);
			setState(372);
			match(COLON);
			setState(373);
			expr(0);
			setState(377);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(374);
				match(COMMA);
				}
				}
				setState(379);
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
		enterRule(_localctx, 60, RULE_db_param_sort);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(380);
			match(T__12);
			setState(381);
			match(COLON);
			setState(382);
			expr(0);
			setState(383);
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
			setState(387);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(384);
				match(COMMA);
				}
				}
				setState(389);
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
		enterRule(_localctx, 62, RULE_criteria);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(390);
			match(OPEN_BRACKET);
			setState(391);
			condition(0);
			setState(392);
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
		int _startState = 64;
		enterRecursionRule(_localctx, 64, RULE_condition, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(400);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VAR:
				{
				setState(395);
				condition_atom();
				}
				break;
			case OPEN_PARANTHESIS:
				{
				setState(396);
				match(OPEN_PARANTHESIS);
				setState(397);
				condition(0);
				setState(398);
				match(CLOSE_PARANTHESIS);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(407);
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
					setState(402);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(403);
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
					setState(404);
					condition(3);
					}
					} 
				}
				setState(409);
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
		enterRule(_localctx, 66, RULE_condition_atom);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(410);
			match(VAR);
			setState(411);
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
			setState(412);
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
		case 15:
			return boolean_expr_atom_sempred((Boolean_expr_atomContext)_localctx, predIndex);
		case 16:
			return expr_sempred((ExprContext)_localctx, predIndex);
		case 32:
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3@\u01a1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\3\2\3\2\3\2\3\3\3\3\3\3\3\3\7\3N\n\3\f\3\16\3Q\13\3"+
		"\3\3\3\3\7\3U\n\3\f\3\16\3X\13\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\5\3\5\3"+
		"\5\3\6\3\6\3\7\7\7g\n\7\f\7\16\7j\13\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\b\5\bv\n\b\3\t\3\t\3\t\3\t\3\t\7\t}\n\t\f\t\16\t\u0080\13\t\3\t"+
		"\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\7\n\u008b\n\n\f\n\16\n\u008e\13\n\3\n"+
		"\3\n\5\n\u0092\n\n\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\5\f\u009c\n\f\3"+
		"\r\3\r\3\r\3\r\7\r\u00a2\n\r\f\r\16\r\u00a5\13\r\3\r\3\r\3\r\3\r\3\16"+
		"\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\21\3\21\3\21"+
		"\3\21\3\21\3\21\5\21\u00bd\n\21\3\21\3\21\3\21\7\21\u00c2\n\21\f\21\16"+
		"\21\u00c5\13\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\5\22\u00d0"+
		"\n\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\22\7\22\u00db\n\22\f\22"+
		"\16\22\u00de\13\22\3\23\3\23\6\23\u00e2\n\23\r\23\16\23\u00e3\3\24\3\24"+
		"\3\24\3\24\7\24\u00ea\n\24\f\24\16\24\u00ed\13\24\3\24\3\24\7\24\u00f1"+
		"\n\24\f\24\16\24\u00f4\13\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\5\24\u00fd"+
		"\n\24\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\3\25\3\25\3\25\6\25\u0121\n\25\r\25\16\25\u0122\3"+
		"\25\3\25\5\25\u0127\n\25\3\26\3\26\6\26\u012b\n\26\r\26\16\26\u012c\3"+
		"\27\3\27\6\27\u0131\n\27\r\27\16\27\u0132\3\30\3\30\3\30\7\30\u0138\n"+
		"\30\f\30\16\30\u013b\13\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\5\31"+
		"\u0145\n\31\3\32\3\32\3\32\3\32\7\32\u014b\n\32\f\32\16\32\u014e\13\32"+
		"\3\33\3\33\3\33\3\33\7\33\u0154\n\33\f\33\16\33\u0157\13\33\3\34\3\34"+
		"\3\34\3\34\7\34\u015d\n\34\f\34\16\34\u0160\13\34\3\35\3\35\3\35\3\35"+
		"\7\35\u0166\n\35\f\35\16\35\u0169\13\35\3\36\3\36\3\36\3\36\3\36\3\36"+
		"\7\36\u0171\n\36\f\36\16\36\u0174\13\36\3\37\3\37\3\37\3\37\7\37\u017a"+
		"\n\37\f\37\16\37\u017d\13\37\3 \3 \3 \3 \3 \7 \u0184\n \f \16 \u0187\13"+
		" \3!\3!\3!\3!\3\"\3\"\3\"\3\"\3\"\3\"\5\"\u0193\n\"\3\"\3\"\3\"\7\"\u0198"+
		"\n\"\f\"\16\"\u019b\13\"\3#\3#\3#\3#\3#\2\5 \"B$\2\4\6\b\n\f\16\20\22"+
		"\24\26\30\32\34\36 \"$&(*,.\60\62\64\668:<>@BD\2\n\3\2\22\27\4\2\31\31"+
		"\33\33\3\2$&\3\2\"#\3\2\34!\3\2;<\3\2\63\64\3\2\20\21\2\u01b9\2F\3\2\2"+
		"\2\4I\3\2\2\2\6^\3\2\2\2\b`\3\2\2\2\nc\3\2\2\2\fh\3\2\2\2\16u\3\2\2\2"+
		"\20w\3\2\2\2\22\u0085\3\2\2\2\24\u0093\3\2\2\2\26\u009b\3\2\2\2\30\u009d"+
		"\3\2\2\2\32\u00aa\3\2\2\2\34\u00ae\3\2\2\2\36\u00b2\3\2\2\2 \u00bc\3\2"+
		"\2\2\"\u00cf\3\2\2\2$\u00df\3\2\2\2&\u00fc\3\2\2\2(\u0126\3\2\2\2*\u012a"+
		"\3\2\2\2,\u0130\3\2\2\2.\u0134\3\2\2\2\60\u0144\3\2\2\2\62\u0146\3\2\2"+
		"\2\64\u014f\3\2\2\2\66\u0158\3\2\2\28\u0161\3\2\2\2:\u016a\3\2\2\2<\u0175"+
		"\3\2\2\2>\u017e\3\2\2\2@\u0188\3\2\2\2B\u0192\3\2\2\2D\u019c\3\2\2\2F"+
		"G\5\4\3\2GH\7\2\2\3H\3\3\2\2\2IJ\5\n\6\2JK\5\6\4\2KO\7-\2\2LN\5\b\5\2"+
		"ML\3\2\2\2NQ\3\2\2\2OM\3\2\2\2OP\3\2\2\2PV\3\2\2\2QO\3\2\2\2RS\7)\2\2"+
		"SU\5\b\5\2TR\3\2\2\2UX\3\2\2\2VT\3\2\2\2VW\3\2\2\2WY\3\2\2\2XV\3\2\2\2"+
		"YZ\7.\2\2Z[\7/\2\2[\\\5\f\7\2\\]\7\60\2\2]\5\3\2\2\2^_\7:\2\2_\7\3\2\2"+
		"\2`a\5\n\6\2ab\7:\2\2b\t\3\2\2\2cd\t\2\2\2d\13\3\2\2\2eg\5\16\b\2fe\3"+
		"\2\2\2gj\3\2\2\2hf\3\2\2\2hi\3\2\2\2i\r\3\2\2\2jh\3\2\2\2kv\5\20\t\2l"+
		"v\5\22\n\2mv\5\30\r\2nv\5\32\16\2op\5$\23\2pq\7*\2\2qv\3\2\2\2rv\5\34"+
		"\17\2st\7@\2\2tv\b\b\1\2uk\3\2\2\2ul\3\2\2\2um\3\2\2\2un\3\2\2\2uo\3\2"+
		"\2\2ur\3\2\2\2us\3\2\2\2v\17\3\2\2\2w~\7:\2\2xy\7\61\2\2yz\5\"\22\2z{"+
		"\7\62\2\2{}\3\2\2\2|x\3\2\2\2}\u0080\3\2\2\2~|\3\2\2\2~\177\3\2\2\2\177"+
		"\u0081\3\2\2\2\u0080~\3\2\2\2\u0081\u0082\7,\2\2\u0082\u0083\5\"\22\2"+
		"\u0083\u0084\7*\2\2\u0084\21\3\2\2\2\u0085\u0086\7\66\2\2\u0086\u008c"+
		"\5\24\13\2\u0087\u0088\7\67\2\2\u0088\u0089\7\66\2\2\u0089\u008b\5\24"+
		"\13\2\u008a\u0087\3\2\2\2\u008b\u008e\3\2\2\2\u008c\u008a\3\2\2\2\u008c"+
		"\u008d\3\2\2\2\u008d\u0091\3\2\2\2\u008e\u008c\3\2\2\2\u008f\u0090\7\67"+
		"\2\2\u0090\u0092\5\26\f\2\u0091\u008f\3\2\2\2\u0091\u0092\3\2\2\2\u0092"+
		"\23\3\2\2\2\u0093\u0094\5\36\20\2\u0094\u0095\5\26\f\2\u0095\25\3\2\2"+
		"\2\u0096\u0097\7/\2\2\u0097\u0098\5\f\7\2\u0098\u0099\7\60\2\2\u0099\u009c"+
		"\3\2\2\2\u009a\u009c\5\16\b\2\u009b\u0096\3\2\2\2\u009b\u009a\3\2\2\2"+
		"\u009c\27\3\2\2\2\u009d\u009e\78\2\2\u009e\u00a3\7:\2\2\u009f\u00a0\7"+
		")\2\2\u00a0\u00a2\7:\2\2\u00a1\u009f\3\2\2\2\u00a2\u00a5\3\2\2\2\u00a3"+
		"\u00a1\3\2\2\2\u00a3\u00a4\3\2\2\2\u00a4\u00a6\3\2\2\2\u00a5\u00a3\3\2"+
		"\2\2\u00a6\u00a7\7\3\2\2\u00a7\u00a8\5\"\22\2\u00a8\u00a9\5\26\f\2\u00a9"+
		"\31\3\2\2\2\u00aa\u00ab\79\2\2\u00ab\u00ac\5\"\22\2\u00ac\u00ad\7*\2\2"+
		"\u00ad\33\3\2\2\2\u00ae\u00af\7\30\2\2\u00af\u00b0\5\"\22\2\u00b0\u00b1"+
		"\7*\2\2\u00b1\35\3\2\2\2\u00b2\u00b3\7-\2\2\u00b3\u00b4\5 \21\2\u00b4"+
		"\u00b5\7.\2\2\u00b5\37\3\2\2\2\u00b6\u00b7\b\21\1\2\u00b7\u00bd\5\"\22"+
		"\2\u00b8\u00b9\7-\2\2\u00b9\u00ba\5 \21\2\u00ba\u00bb\7.\2\2\u00bb\u00bd"+
		"\3\2\2\2\u00bc\u00b6\3\2\2\2\u00bc\u00b8\3\2\2\2\u00bd\u00c3\3\2\2\2\u00be"+
		"\u00bf\f\4\2\2\u00bf\u00c0\t\3\2\2\u00c0\u00c2\5 \21\5\u00c1\u00be\3\2"+
		"\2\2\u00c2\u00c5\3\2\2\2\u00c3\u00c1\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4"+
		"!\3\2\2\2\u00c5\u00c3\3\2\2\2\u00c6\u00c7\b\22\1\2\u00c7\u00c8\7#\2\2"+
		"\u00c8\u00d0\5\"\22\13\u00c9\u00ca\7(\2\2\u00ca\u00d0\5\"\22\n\u00cb\u00d0"+
		"\5(\25\2\u00cc\u00d0\5$\23\2\u00cd\u00d0\5.\30\2\u00ce\u00d0\5@!\2\u00cf"+
		"\u00c6\3\2\2\2\u00cf\u00c9\3\2\2\2\u00cf\u00cb\3\2\2\2\u00cf\u00cc\3\2"+
		"\2\2\u00cf\u00cd\3\2\2\2\u00cf\u00ce\3\2\2\2\u00d0\u00dc\3\2\2\2\u00d1"+
		"\u00d2\f\t\2\2\u00d2\u00d3\t\4\2\2\u00d3\u00db\5\"\22\n\u00d4\u00d5\f"+
		"\b\2\2\u00d5\u00d6\t\5\2\2\u00d6\u00db\5\"\22\t\u00d7\u00d8\f\7\2\2\u00d8"+
		"\u00d9\t\6\2\2\u00d9\u00db\5\"\22\b\u00da\u00d1\3\2\2\2\u00da\u00d4\3"+
		"\2\2\2\u00da\u00d7\3\2\2\2\u00db\u00de\3\2\2\2\u00dc\u00da\3\2\2\2\u00dc"+
		"\u00dd\3\2\2\2\u00dd#\3\2\2\2\u00de\u00dc\3\2\2\2\u00df\u00e1\5(\25\2"+
		"\u00e0\u00e2\5&\24\2\u00e1\u00e0\3\2\2\2\u00e2\u00e3\3\2\2\2\u00e3\u00e1"+
		"\3\2\2\2\u00e3\u00e4\3\2\2\2\u00e4%\3\2\2\2\u00e5\u00e6\7\32\2\2\u00e6"+
		"\u00e7\7:\2\2\u00e7\u00eb\7-\2\2\u00e8\u00ea\5\"\22\2\u00e9\u00e8\3\2"+
		"\2\2\u00ea\u00ed\3\2\2\2\u00eb\u00e9\3\2\2\2\u00eb\u00ec\3\2\2\2\u00ec"+
		"\u00f2\3\2\2\2\u00ed\u00eb\3\2\2\2\u00ee\u00ef\7)\2\2\u00ef\u00f1\5\""+
		"\22\2\u00f0\u00ee\3\2\2\2\u00f1\u00f4\3\2\2\2\u00f2\u00f0\3\2\2\2\u00f2"+
		"\u00f3\3\2\2\2\u00f3\u00f5\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f5\u00fd\7."+
		"\2\2\u00f6\u00f7\7\32\2\2\u00f7\u00fd\7:\2\2\u00f8\u00f9\7\61\2\2\u00f9"+
		"\u00fa\5(\25\2\u00fa\u00fb\7\62\2\2\u00fb\u00fd\3\2\2\2\u00fc\u00e5\3"+
		"\2\2\2\u00fc\u00f6\3\2\2\2\u00fc\u00f8\3\2\2\2\u00fd\'\3\2\2\2\u00fe\u00ff"+
		"\7-\2\2\u00ff\u0100\5\"\22\2\u0100\u0101\7.\2\2\u0101\u0127\3\2\2\2\u0102"+
		"\u0127\t\7\2\2\u0103\u0127\t\b\2\2\u0104\u0127\7=\2\2\u0105\u0127\7\65"+
		"\2\2\u0106\u0127\7:\2\2\u0107\u0108\7\4\2\2\u0108\u0109\7-\2\2\u0109\u010a"+
		"\5\"\22\2\u010a\u010b\7.\2\2\u010b\u0127\3\2\2\2\u010c\u010d\7\5\2\2\u010d"+
		"\u010e\7-\2\2\u010e\u010f\5\"\22\2\u010f\u0110\7.\2\2\u0110\u0127\3\2"+
		"\2\2\u0111\u0112\7\6\2\2\u0112\u0113\7-\2\2\u0113\u0114\5\"\22\2\u0114"+
		"\u0115\7.\2\2\u0115\u0127\3\2\2\2\u0116\u0117\7\7\2\2\u0117\u0118\7-\2"+
		"\2\u0118\u0119\5\"\22\2\u0119\u011a\7)\2\2\u011a\u011b\5\"\22\2\u011b"+
		"\u011c\7.\2\2\u011c\u0127\3\2\2\2\u011d\u011e\7:\2\2\u011e\u011f\7-\2"+
		"\2\u011f\u0121\7.\2\2\u0120\u011d\3\2\2\2\u0121\u0122\3\2\2\2\u0122\u0120"+
		"\3\2\2\2\u0122\u0123\3\2\2\2\u0123\u0127\3\2\2\2\u0124\u0127\5*\26\2\u0125"+
		"\u0127\5,\27\2\u0126\u00fe\3\2\2\2\u0126\u0102\3\2\2\2\u0126\u0103\3\2"+
		"\2\2\u0126\u0104\3\2\2\2\u0126\u0105\3\2\2\2\u0126\u0106\3\2\2\2\u0126"+
		"\u0107\3\2\2\2\u0126\u010c\3\2\2\2\u0126\u0111\3\2\2\2\u0126\u0116\3\2"+
		"\2\2\u0126\u0120\3\2\2\2\u0126\u0124\3\2\2\2\u0126\u0125\3\2\2\2\u0127"+
		")\3\2\2\2\u0128\u0129\7\61\2\2\u0129\u012b\7\62\2\2\u012a\u0128\3\2\2"+
		"\2\u012b\u012c\3\2\2\2\u012c\u012a\3\2\2\2\u012c\u012d\3\2\2\2\u012d+"+
		"\3\2\2\2\u012e\u012f\7/\2\2\u012f\u0131\7\60\2\2\u0130\u012e\3\2\2\2\u0131"+
		"\u0132\3\2\2\2\u0132\u0130\3\2\2\2\u0132\u0133\3\2\2\2\u0133-\3\2\2\2"+
		"\u0134\u0135\7/\2\2\u0135\u0139\5\62\32\2\u0136\u0138\5\60\31\2\u0137"+
		"\u0136\3\2\2\2\u0138\u013b\3\2\2\2\u0139\u0137\3\2\2\2\u0139\u013a\3\2"+
		"\2\2\u013a\u013c\3\2\2\2\u013b\u0139\3\2\2\2\u013c\u013d\7\60\2\2\u013d"+
		"/\3\2\2\2\u013e\u0145\5\64\33\2\u013f\u0145\5\66\34\2\u0140\u0145\58\35"+
		"\2\u0141\u0145\5:\36\2\u0142\u0145\5<\37\2\u0143\u0145\5> \2\u0144\u013e"+
		"\3\2\2\2\u0144\u013f\3\2\2\2\u0144\u0140\3\2\2\2\u0144\u0141\3\2\2\2\u0144"+
		"\u0142\3\2\2\2\u0144\u0143\3\2\2\2\u0145\61\3\2\2\2\u0146\u0147\7\b\2"+
		"\2\u0147\u0148\7+\2\2\u0148\u014c\5@!\2\u0149\u014b\7)\2\2\u014a\u0149"+
		"\3\2\2\2\u014b\u014e\3\2\2\2\u014c\u014a\3\2\2\2\u014c\u014d\3\2\2\2\u014d"+
		"\63\3\2\2\2\u014e\u014c\3\2\2\2\u014f\u0150\7\t\2\2\u0150\u0151\7+\2\2"+
		"\u0151\u0155\5\"\22\2\u0152\u0154\7)\2\2\u0153\u0152\3\2\2\2\u0154\u0157"+
		"\3\2\2\2\u0155\u0153\3\2\2\2\u0155\u0156\3\2\2\2\u0156\65\3\2\2\2\u0157"+
		"\u0155\3\2\2\2\u0158\u0159\7\n\2\2\u0159\u015a\7+\2\2\u015a\u015e\5\""+
		"\22\2\u015b\u015d\7)\2\2\u015c\u015b\3\2\2\2\u015d\u0160\3\2\2\2\u015e"+
		"\u015c\3\2\2\2\u015e\u015f\3\2\2\2\u015f\67\3\2\2\2\u0160\u015e\3\2\2"+
		"\2\u0161\u0162\7\13\2\2\u0162\u0163\7+\2\2\u0163\u0167\5\"\22\2\u0164"+
		"\u0166\7)\2\2\u0165\u0164\3\2\2\2\u0166\u0169\3\2\2\2\u0167\u0165\3\2"+
		"\2\2\u0167\u0168\3\2\2\2\u01689\3\2\2\2\u0169\u0167\3\2\2\2\u016a\u016b"+
		"\7\f\2\2\u016b\u016c\7+\2\2\u016c\u016d\5\"\22\2\u016d\u016e\7\r\2\2\u016e"+
		"\u0172\5\"\22\2\u016f\u0171\7)\2\2\u0170\u016f\3\2\2\2\u0171\u0174\3\2"+
		"\2\2\u0172\u0170\3\2\2\2\u0172\u0173\3\2\2\2\u0173;\3\2\2\2\u0174\u0172"+
		"\3\2\2\2\u0175\u0176\7\16\2\2\u0176\u0177\7+\2\2\u0177\u017b\5\"\22\2"+
		"\u0178\u017a\7)\2\2\u0179\u0178\3\2\2\2\u017a\u017d\3\2\2\2\u017b\u0179"+
		"\3\2\2\2\u017b\u017c\3\2\2\2\u017c=\3\2\2\2\u017d\u017b\3\2\2\2\u017e"+
		"\u017f\7\17\2\2\u017f\u0180\7+\2\2\u0180\u0181\5\"\22\2\u0181\u0185\t"+
		"\t\2\2\u0182\u0184\7)\2\2\u0183\u0182\3\2\2\2\u0184\u0187\3\2\2\2\u0185"+
		"\u0183\3\2\2\2\u0185\u0186\3\2\2\2\u0186?\3\2\2\2\u0187\u0185\3\2\2\2"+
		"\u0188\u0189\7\61\2\2\u0189\u018a\5B\"\2\u018a\u018b\7\62\2\2\u018bA\3"+
		"\2\2\2\u018c\u018d\b\"\1\2\u018d\u0193\5D#\2\u018e\u018f\7-\2\2\u018f"+
		"\u0190\5B\"\2\u0190\u0191\7.\2\2\u0191\u0193\3\2\2\2\u0192\u018c\3\2\2"+
		"\2\u0192\u018e\3\2\2\2\u0193\u0199\3\2\2\2\u0194\u0195\f\4\2\2\u0195\u0196"+
		"\t\3\2\2\u0196\u0198\5B\"\5\u0197\u0194\3\2\2\2\u0198\u019b\3\2\2\2\u0199"+
		"\u0197\3\2\2\2\u0199\u019a\3\2\2\2\u019aC\3\2\2\2\u019b\u0199\3\2\2\2"+
		"\u019c\u019d\7:\2\2\u019d\u019e\t\6\2\2\u019e\u019f\5\"\22\2\u019fE\3"+
		"\2\2\2#OVhu~\u008c\u0091\u009b\u00a3\u00bc\u00c3\u00cf\u00da\u00dc\u00e3"+
		"\u00eb\u00f2\u00fc\u0122\u0126\u012c\u0132\u0139\u0144\u014c\u0155\u015e"+
		"\u0167\u0172\u017b\u0185\u0192\u0199";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}