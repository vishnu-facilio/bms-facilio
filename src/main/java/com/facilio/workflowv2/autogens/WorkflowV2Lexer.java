// Generated from /Users/krishna/git/bmsconsole/src/main/java/com/facilio/workflowv2/autogens/WorkflowV2.g4 by ANTLR 4.7.2
package com.facilio.workflowv2.autogens;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class WorkflowV2Lexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, VOID=16, DATA_TYPE_STRING=17, 
		DATA_TYPE_NUMBER=18, DATA_TYPE_BOOLEAN=19, DATA_TYPE_MAP=20, DATA_TYPE_LIST=21, 
		RETURN=22, OR=23, SINGLE_OR=24, DOT=25, AND=26, SINGLE_AND=27, EQ=28, 
		NEQ=29, GT=30, LT=31, GTEQ=32, LTEQ=33, PLUS=34, MINUS=35, MULT=36, DIV=37, 
		MOD=38, POW=39, NOT=40, COMMA=41, SEMICOLON=42, COLON=43, ASSIGN=44, OPEN_PARANTHESIS=45, 
		CLOSE_PARANTHESIS=46, OPEN_BRACE=47, CLOSE_BRACE=48, OPEN_BRACKET=49, 
		CLOSE_BRACKET=50, TRUE=51, FALSE=52, NULL=53, IF=54, ELSE=55, FOR_EACH=56, 
		LOG=57, VAR=58, INT=59, FLOAT=60, STRING=61, COMMENT=62, SPACE=63, OTHER=64;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
			"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "VOID", "DATA_TYPE_STRING", 
			"DATA_TYPE_NUMBER", "DATA_TYPE_BOOLEAN", "DATA_TYPE_MAP", "DATA_TYPE_LIST", 
			"RETURN", "OR", "SINGLE_OR", "DOT", "AND", "SINGLE_AND", "EQ", "NEQ", 
			"GT", "LT", "GTEQ", "LTEQ", "PLUS", "MINUS", "MULT", "DIV", "MOD", "POW", 
			"NOT", "COMMA", "SEMICOLON", "COLON", "ASSIGN", "OPEN_PARANTHESIS", "CLOSE_PARANTHESIS", 
			"OPEN_BRACE", "CLOSE_BRACE", "OPEN_BRACKET", "CLOSE_BRACKET", "TRUE", 
			"FALSE", "NULL", "IF", "ELSE", "FOR_EACH", "LOG", "VAR", "INT", "FLOAT", 
			"ESCAPED_QUOTE", "STRING", "COMMENT", "SPACE", "OTHER"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'in'", "'NameSpace'", "'Module'", "'Connection'", "'Reading'", 
			"'criteria'", "'field'", "'aggregation'", "'limit'", "'range'", "'to'", 
			"'groupBy'", "'orderBy'", "'asc'", "'desc'", "'void'", "'String'", "'Number'", 
			"'Boolean'", "'Map'", "'List'", "'return'", "'||'", "'|'", "'.'", "'&&'", 
			"'&'", "'=='", "'!='", "'>'", "'<'", "'>='", "'<='", "'+'", "'-'", "'*'", 
			"'/'", "'%'", "'^'", "'!'", "','", "';'", "':'", "'='", "'('", "')'", 
			"'{'", "'}'", "'['", "']'", "'true'", "'false'", "'null'", "'if'", "'else'", 
			"'for each'", "'log'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, "VOID", "DATA_TYPE_STRING", "DATA_TYPE_NUMBER", 
			"DATA_TYPE_BOOLEAN", "DATA_TYPE_MAP", "DATA_TYPE_LIST", "RETURN", "OR", 
			"SINGLE_OR", "DOT", "AND", "SINGLE_AND", "EQ", "NEQ", "GT", "LT", "GTEQ", 
			"LTEQ", "PLUS", "MINUS", "MULT", "DIV", "MOD", "POW", "NOT", "COMMA", 
			"SEMICOLON", "COLON", "ASSIGN", "OPEN_PARANTHESIS", "CLOSE_PARANTHESIS", 
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


	public WorkflowV2Lexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "WorkflowV2.g4"; }

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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2B\u01bb\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7"+
		"\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3\13\3"+
		"\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3"+
		"\16\3\16\3\16\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3"+
		"\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3"+
		"\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3"+
		"\25\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27\3\27\3\30\3"+
		"\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\33\3\34\3\34\3\35\3\35\3\35\3"+
		"\36\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3!\3\"\3\"\3\"\3#\3#\3$\3$\3%\3%\3"+
		"&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3\60\3\60"+
		"\3\61\3\61\3\62\3\62\3\63\3\63\3\64\3\64\3\64\3\64\3\64\3\65\3\65\3\65"+
		"\3\65\3\65\3\65\3\66\3\66\3\66\3\66\3\66\3\67\3\67\3\67\38\38\38\38\3"+
		"8\39\39\39\39\39\39\39\39\39\3:\3:\3:\3:\3;\3;\7;\u0180\n;\f;\16;\u0183"+
		"\13;\3<\6<\u0186\n<\r<\16<\u0187\3=\6=\u018b\n=\r=\16=\u018c\3=\3=\7="+
		"\u0191\n=\f=\16=\u0194\13=\3=\3=\6=\u0198\n=\r=\16=\u0199\5=\u019c\n="+
		"\3>\3>\3>\3?\3?\3?\7?\u01a4\n?\f?\16?\u01a7\13?\3?\3?\3@\3@\3@\3@\7@\u01af"+
		"\n@\f@\16@\u01b2\13@\3@\3@\3A\3A\3A\3A\3B\3B\3\u01a5\2C\3\3\5\4\7\5\t"+
		"\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23"+
		"%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G"+
		"%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67m8o9q:s;u<w=y>{"+
		"\2}?\177@\u0081A\u0083B\3\2\7\5\2C\\aac|\6\2\62;C\\aac|\3\2\62;\4\2\f"+
		"\f\17\17\5\2\13\f\17\17\"\"\2\u01c2\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2"+
		"\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23"+
		"\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2"+
		"\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2"+
		"\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3"+
		"\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2"+
		"\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2"+
		"\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2["+
		"\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2"+
		"\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2"+
		"\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2"+
		"\2\2\2\u0083\3\2\2\2\3\u0085\3\2\2\2\5\u0088\3\2\2\2\7\u0092\3\2\2\2\t"+
		"\u0099\3\2\2\2\13\u00a4\3\2\2\2\r\u00ac\3\2\2\2\17\u00b5\3\2\2\2\21\u00bb"+
		"\3\2\2\2\23\u00c7\3\2\2\2\25\u00cd\3\2\2\2\27\u00d3\3\2\2\2\31\u00d6\3"+
		"\2\2\2\33\u00de\3\2\2\2\35\u00e6\3\2\2\2\37\u00ea\3\2\2\2!\u00ef\3\2\2"+
		"\2#\u00f4\3\2\2\2%\u00fb\3\2\2\2\'\u0102\3\2\2\2)\u010a\3\2\2\2+\u010e"+
		"\3\2\2\2-\u0113\3\2\2\2/\u011a\3\2\2\2\61\u011d\3\2\2\2\63\u011f\3\2\2"+
		"\2\65\u0121\3\2\2\2\67\u0124\3\2\2\29\u0126\3\2\2\2;\u0129\3\2\2\2=\u012c"+
		"\3\2\2\2?\u012e\3\2\2\2A\u0130\3\2\2\2C\u0133\3\2\2\2E\u0136\3\2\2\2G"+
		"\u0138\3\2\2\2I\u013a\3\2\2\2K\u013c\3\2\2\2M\u013e\3\2\2\2O\u0140\3\2"+
		"\2\2Q\u0142\3\2\2\2S\u0144\3\2\2\2U\u0146\3\2\2\2W\u0148\3\2\2\2Y\u014a"+
		"\3\2\2\2[\u014c\3\2\2\2]\u014e\3\2\2\2_\u0150\3\2\2\2a\u0152\3\2\2\2c"+
		"\u0154\3\2\2\2e\u0156\3\2\2\2g\u0158\3\2\2\2i\u015d\3\2\2\2k\u0163\3\2"+
		"\2\2m\u0168\3\2\2\2o\u016b\3\2\2\2q\u0170\3\2\2\2s\u0179\3\2\2\2u\u017d"+
		"\3\2\2\2w\u0185\3\2\2\2y\u019b\3\2\2\2{\u019d\3\2\2\2}\u01a0\3\2\2\2\177"+
		"\u01aa\3\2\2\2\u0081\u01b5\3\2\2\2\u0083\u01b9\3\2\2\2\u0085\u0086\7k"+
		"\2\2\u0086\u0087\7p\2\2\u0087\4\3\2\2\2\u0088\u0089\7P\2\2\u0089\u008a"+
		"\7c\2\2\u008a\u008b\7o\2\2\u008b\u008c\7g\2\2\u008c\u008d\7U\2\2\u008d"+
		"\u008e\7r\2\2\u008e\u008f\7c\2\2\u008f\u0090\7e\2\2\u0090\u0091\7g\2\2"+
		"\u0091\6\3\2\2\2\u0092\u0093\7O\2\2\u0093\u0094\7q\2\2\u0094\u0095\7f"+
		"\2\2\u0095\u0096\7w\2\2\u0096\u0097\7n\2\2\u0097\u0098\7g\2\2\u0098\b"+
		"\3\2\2\2\u0099\u009a\7E\2\2\u009a\u009b\7q\2\2\u009b\u009c\7p\2\2\u009c"+
		"\u009d\7p\2\2\u009d\u009e\7g\2\2\u009e\u009f\7e\2\2\u009f\u00a0\7v\2\2"+
		"\u00a0\u00a1\7k\2\2\u00a1\u00a2\7q\2\2\u00a2\u00a3\7p\2\2\u00a3\n\3\2"+
		"\2\2\u00a4\u00a5\7T\2\2\u00a5\u00a6\7g\2\2\u00a6\u00a7\7c\2\2\u00a7\u00a8"+
		"\7f\2\2\u00a8\u00a9\7k\2\2\u00a9\u00aa\7p\2\2\u00aa\u00ab\7i\2\2\u00ab"+
		"\f\3\2\2\2\u00ac\u00ad\7e\2\2\u00ad\u00ae\7t\2\2\u00ae\u00af\7k\2\2\u00af"+
		"\u00b0\7v\2\2\u00b0\u00b1\7g\2\2\u00b1\u00b2\7t\2\2\u00b2\u00b3\7k\2\2"+
		"\u00b3\u00b4\7c\2\2\u00b4\16\3\2\2\2\u00b5\u00b6\7h\2\2\u00b6\u00b7\7"+
		"k\2\2\u00b7\u00b8\7g\2\2\u00b8\u00b9\7n\2\2\u00b9\u00ba\7f\2\2\u00ba\20"+
		"\3\2\2\2\u00bb\u00bc\7c\2\2\u00bc\u00bd\7i\2\2\u00bd\u00be\7i\2\2\u00be"+
		"\u00bf\7t\2\2\u00bf\u00c0\7g\2\2\u00c0\u00c1\7i\2\2\u00c1\u00c2\7c\2\2"+
		"\u00c2\u00c3\7v\2\2\u00c3\u00c4\7k\2\2\u00c4\u00c5\7q\2\2\u00c5\u00c6"+
		"\7p\2\2\u00c6\22\3\2\2\2\u00c7\u00c8\7n\2\2\u00c8\u00c9\7k\2\2\u00c9\u00ca"+
		"\7o\2\2\u00ca\u00cb\7k\2\2\u00cb\u00cc\7v\2\2\u00cc\24\3\2\2\2\u00cd\u00ce"+
		"\7t\2\2\u00ce\u00cf\7c\2\2\u00cf\u00d0\7p\2\2\u00d0\u00d1\7i\2\2\u00d1"+
		"\u00d2\7g\2\2\u00d2\26\3\2\2\2\u00d3\u00d4\7v\2\2\u00d4\u00d5\7q\2\2\u00d5"+
		"\30\3\2\2\2\u00d6\u00d7\7i\2\2\u00d7\u00d8\7t\2\2\u00d8\u00d9\7q\2\2\u00d9"+
		"\u00da\7w\2\2\u00da\u00db\7r\2\2\u00db\u00dc\7D\2\2\u00dc\u00dd\7{\2\2"+
		"\u00dd\32\3\2\2\2\u00de\u00df\7q\2\2\u00df\u00e0\7t\2\2\u00e0\u00e1\7"+
		"f\2\2\u00e1\u00e2\7g\2\2\u00e2\u00e3\7t\2\2\u00e3\u00e4\7D\2\2\u00e4\u00e5"+
		"\7{\2\2\u00e5\34\3\2\2\2\u00e6\u00e7\7c\2\2\u00e7\u00e8\7u\2\2\u00e8\u00e9"+
		"\7e\2\2\u00e9\36\3\2\2\2\u00ea\u00eb\7f\2\2\u00eb\u00ec\7g\2\2\u00ec\u00ed"+
		"\7u\2\2\u00ed\u00ee\7e\2\2\u00ee \3\2\2\2\u00ef\u00f0\7x\2\2\u00f0\u00f1"+
		"\7q\2\2\u00f1\u00f2\7k\2\2\u00f2\u00f3\7f\2\2\u00f3\"\3\2\2\2\u00f4\u00f5"+
		"\7U\2\2\u00f5\u00f6\7v\2\2\u00f6\u00f7\7t\2\2\u00f7\u00f8\7k\2\2\u00f8"+
		"\u00f9\7p\2\2\u00f9\u00fa\7i\2\2\u00fa$\3\2\2\2\u00fb\u00fc\7P\2\2\u00fc"+
		"\u00fd\7w\2\2\u00fd\u00fe\7o\2\2\u00fe\u00ff\7d\2\2\u00ff\u0100\7g\2\2"+
		"\u0100\u0101\7t\2\2\u0101&\3\2\2\2\u0102\u0103\7D\2\2\u0103\u0104\7q\2"+
		"\2\u0104\u0105\7q\2\2\u0105\u0106\7n\2\2\u0106\u0107\7g\2\2\u0107\u0108"+
		"\7c\2\2\u0108\u0109\7p\2\2\u0109(\3\2\2\2\u010a\u010b\7O\2\2\u010b\u010c"+
		"\7c\2\2\u010c\u010d\7r\2\2\u010d*\3\2\2\2\u010e\u010f\7N\2\2\u010f\u0110"+
		"\7k\2\2\u0110\u0111\7u\2\2\u0111\u0112\7v\2\2\u0112,\3\2\2\2\u0113\u0114"+
		"\7t\2\2\u0114\u0115\7g\2\2\u0115\u0116\7v\2\2\u0116\u0117\7w\2\2\u0117"+
		"\u0118\7t\2\2\u0118\u0119\7p\2\2\u0119.\3\2\2\2\u011a\u011b\7~\2\2\u011b"+
		"\u011c\7~\2\2\u011c\60\3\2\2\2\u011d\u011e\7~\2\2\u011e\62\3\2\2\2\u011f"+
		"\u0120\7\60\2\2\u0120\64\3\2\2\2\u0121\u0122\7(\2\2\u0122\u0123\7(\2\2"+
		"\u0123\66\3\2\2\2\u0124\u0125\7(\2\2\u01258\3\2\2\2\u0126\u0127\7?\2\2"+
		"\u0127\u0128\7?\2\2\u0128:\3\2\2\2\u0129\u012a\7#\2\2\u012a\u012b\7?\2"+
		"\2\u012b<\3\2\2\2\u012c\u012d\7@\2\2\u012d>\3\2\2\2\u012e\u012f\7>\2\2"+
		"\u012f@\3\2\2\2\u0130\u0131\7@\2\2\u0131\u0132\7?\2\2\u0132B\3\2\2\2\u0133"+
		"\u0134\7>\2\2\u0134\u0135\7?\2\2\u0135D\3\2\2\2\u0136\u0137\7-\2\2\u0137"+
		"F\3\2\2\2\u0138\u0139\7/\2\2\u0139H\3\2\2\2\u013a\u013b\7,\2\2\u013bJ"+
		"\3\2\2\2\u013c\u013d\7\61\2\2\u013dL\3\2\2\2\u013e\u013f\7\'\2\2\u013f"+
		"N\3\2\2\2\u0140\u0141\7`\2\2\u0141P\3\2\2\2\u0142\u0143\7#\2\2\u0143R"+
		"\3\2\2\2\u0144\u0145\7.\2\2\u0145T\3\2\2\2\u0146\u0147\7=\2\2\u0147V\3"+
		"\2\2\2\u0148\u0149\7<\2\2\u0149X\3\2\2\2\u014a\u014b\7?\2\2\u014bZ\3\2"+
		"\2\2\u014c\u014d\7*\2\2\u014d\\\3\2\2\2\u014e\u014f\7+\2\2\u014f^\3\2"+
		"\2\2\u0150\u0151\7}\2\2\u0151`\3\2\2\2\u0152\u0153\7\177\2\2\u0153b\3"+
		"\2\2\2\u0154\u0155\7]\2\2\u0155d\3\2\2\2\u0156\u0157\7_\2\2\u0157f\3\2"+
		"\2\2\u0158\u0159\7v\2\2\u0159\u015a\7t\2\2\u015a\u015b\7w\2\2\u015b\u015c"+
		"\7g\2\2\u015ch\3\2\2\2\u015d\u015e\7h\2\2\u015e\u015f\7c\2\2\u015f\u0160"+
		"\7n\2\2\u0160\u0161\7u\2\2\u0161\u0162\7g\2\2\u0162j\3\2\2\2\u0163\u0164"+
		"\7p\2\2\u0164\u0165\7w\2\2\u0165\u0166\7n\2\2\u0166\u0167\7n\2\2\u0167"+
		"l\3\2\2\2\u0168\u0169\7k\2\2\u0169\u016a\7h\2\2\u016an\3\2\2\2\u016b\u016c"+
		"\7g\2\2\u016c\u016d\7n\2\2\u016d\u016e\7u\2\2\u016e\u016f\7g\2\2\u016f"+
		"p\3\2\2\2\u0170\u0171\7h\2\2\u0171\u0172\7q\2\2\u0172\u0173\7t\2\2\u0173"+
		"\u0174\7\"\2\2\u0174\u0175\7g\2\2\u0175\u0176\7c\2\2\u0176\u0177\7e\2"+
		"\2\u0177\u0178\7j\2\2\u0178r\3\2\2\2\u0179\u017a\7n\2\2\u017a\u017b\7"+
		"q\2\2\u017b\u017c\7i\2\2\u017ct\3\2\2\2\u017d\u0181\t\2\2\2\u017e\u0180"+
		"\t\3\2\2\u017f\u017e\3\2\2\2\u0180\u0183\3\2\2\2\u0181\u017f\3\2\2\2\u0181"+
		"\u0182\3\2\2\2\u0182v\3\2\2\2\u0183\u0181\3\2\2\2\u0184\u0186\t\4\2\2"+
		"\u0185\u0184\3\2\2\2\u0186\u0187\3\2\2\2\u0187\u0185\3\2\2\2\u0187\u0188"+
		"\3\2\2\2\u0188x\3\2\2\2\u0189\u018b\t\4\2\2\u018a\u0189\3\2\2\2\u018b"+
		"\u018c\3\2\2\2\u018c\u018a\3\2\2\2\u018c\u018d\3\2\2\2\u018d\u018e\3\2"+
		"\2\2\u018e\u0192\7\60\2\2\u018f\u0191\t\4\2\2\u0190\u018f\3\2\2\2\u0191"+
		"\u0194\3\2\2\2\u0192\u0190\3\2\2\2\u0192\u0193\3\2\2\2\u0193\u019c\3\2"+
		"\2\2\u0194\u0192\3\2\2\2\u0195\u0197\7\60\2\2\u0196\u0198\t\4\2\2\u0197"+
		"\u0196\3\2\2\2\u0198\u0199\3\2\2\2\u0199\u0197\3\2\2\2\u0199\u019a\3\2"+
		"\2\2\u019a\u019c\3\2\2\2\u019b\u018a\3\2\2\2\u019b\u0195\3\2\2\2\u019c"+
		"z\3\2\2\2\u019d\u019e\7^\2\2\u019e\u019f\7$\2\2\u019f|\3\2\2\2\u01a0\u01a5"+
		"\7$\2\2\u01a1\u01a4\5{>\2\u01a2\u01a4\n\5\2\2\u01a3\u01a1\3\2\2\2\u01a3"+
		"\u01a2\3\2\2\2\u01a4\u01a7\3\2\2\2\u01a5\u01a6\3\2\2\2\u01a5\u01a3\3\2"+
		"\2\2\u01a6\u01a8\3\2\2\2\u01a7\u01a5\3\2\2\2\u01a8\u01a9\7$\2\2\u01a9"+
		"~\3\2\2\2\u01aa\u01ab\7\61\2\2\u01ab\u01ac\7\61\2\2\u01ac\u01b0\3\2\2"+
		"\2\u01ad\u01af\n\5\2\2\u01ae\u01ad\3\2\2\2\u01af\u01b2\3\2\2\2\u01b0\u01ae"+
		"\3\2\2\2\u01b0\u01b1\3\2\2\2\u01b1\u01b3\3\2\2\2\u01b2\u01b0\3\2\2\2\u01b3"+
		"\u01b4\b@\2\2\u01b4\u0080\3\2\2\2\u01b5\u01b6\t\6\2\2\u01b6\u01b7\3\2"+
		"\2\2\u01b7\u01b8\bA\2\2\u01b8\u0082\3\2\2\2\u01b9\u01ba\13\2\2\2\u01ba"+
		"\u0084\3\2\2\2\f\2\u0181\u0187\u018c\u0192\u0199\u019b\u01a3\u01a5\u01b0"+
		"\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}