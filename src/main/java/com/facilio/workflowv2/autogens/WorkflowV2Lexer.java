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
		DATA_TYPE_CRITERIA=22, RETURN=23, TRY=24, CATCH=25, OR=26, SINGLE_OR=27, 
		DOT=28, AND=29, SINGLE_AND=30, EQ=31, NEQ=32, GT=33, LT=34, GTEQ=35, LTEQ=36, 
		PLUS=37, MINUS=38, MULT=39, DIV=40, MOD=41, POW=42, NOT=43, COMMA=44, 
		SEMICOLON=45, COLON=46, ASSIGN=47, OPEN_PARANTHESIS=48, CLOSE_PARANTHESIS=49, 
		OPEN_BRACE=50, CLOSE_BRACE=51, OPEN_BRACKET=52, CLOSE_BRACKET=53, TRUE=54, 
		FALSE=55, NULL=56, IF=57, ELSE=58, FOR_EACH=59, LOG=60, VAR=61, INT=62, 
		FLOAT=63, STRING=64, COMMENT=65, SPACE=66, OTHER=67;
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
			"DATA_TYPE_CRITERIA", "RETURN", "TRY", "CATCH", "OR", "SINGLE_OR", "DOT", 
			"AND", "SINGLE_AND", "EQ", "NEQ", "GT", "LT", "GTEQ", "LTEQ", "PLUS", 
			"MINUS", "MULT", "DIV", "MOD", "POW", "NOT", "COMMA", "SEMICOLON", "COLON", 
			"ASSIGN", "OPEN_PARANTHESIS", "CLOSE_PARANTHESIS", "OPEN_BRACE", "CLOSE_BRACE", 
			"OPEN_BRACKET", "CLOSE_BRACKET", "TRUE", "FALSE", "NULL", "IF", "ELSE", 
			"FOR_EACH", "LOG", "VAR", "INT", "FLOAT", "ESCAPED_QUOTE", "STRING", 
			"COMMENT", "SPACE", "OTHER"
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2E\u01d4\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t+\4"+
		",\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64\t"+
		"\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t<\4=\t="+
		"\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\3\2\3\2\3\2\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3"+
		"\7\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13"+
		"\3\13\3\13\3\13\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16\3\16"+
		"\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\20\3\20\3\20\3\20"+
		"\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\22\3\22\3\23"+
		"\3\23\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24"+
		"\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27\3\27\3\27"+
		"\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31"+
		"\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3\33\3\33\3\33\3\34\3\34\3\35\3\35"+
		"\3\36\3\36\3\36\3\37\3\37\3 \3 \3 \3!\3!\3!\3\"\3\"\3#\3#\3$\3$\3$\3%"+
		"\3%\3%\3&\3&\3\'\3\'\3(\3(\3)\3)\3*\3*\3+\3+\3,\3,\3-\3-\3.\3.\3/\3/\3"+
		"\60\3\60\3\61\3\61\3\62\3\62\3\63\3\63\3\64\3\64\3\65\3\65\3\66\3\66\3"+
		"\67\3\67\3\67\3\67\3\67\38\38\38\38\38\38\39\39\39\39\39\3:\3:\3:\3;\3"+
		";\3;\3;\3;\3<\3<\3<\3<\3<\3<\3<\3<\3<\3=\3=\3=\3=\3>\3>\7>\u0199\n>\f"+
		">\16>\u019c\13>\3?\6?\u019f\n?\r?\16?\u01a0\3@\6@\u01a4\n@\r@\16@\u01a5"+
		"\3@\3@\7@\u01aa\n@\f@\16@\u01ad\13@\3@\3@\6@\u01b1\n@\r@\16@\u01b2\5@"+
		"\u01b5\n@\3A\3A\3A\3B\3B\3B\7B\u01bd\nB\fB\16B\u01c0\13B\3B\3B\3C\3C\3"+
		"C\3C\7C\u01c8\nC\fC\16C\u01cb\13C\3C\3C\3D\3D\3D\3D\3E\3E\3\u01be\2F\3"+
		"\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37"+
		"\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37="+
		" ?!A\"C#E$G%I&K\'M(O)Q*S+U,W-Y.[/]\60_\61a\62c\63e\64g\65i\66k\67m8o9"+
		"q:s;u<w=y>{?}@\177A\u0081\2\u0083B\u0085C\u0087D\u0089E\3\2\7\5\2C\\a"+
		"ac|\6\2\62;C\\aac|\3\2\62;\4\2\f\f\17\17\5\2\13\f\17\17\"\"\2\u01db\2"+
		"\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2"+
		"\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2"+
		"\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2"+
		"\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2"+
		"\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2"+
		"\2\2\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2"+
		"\2I\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U"+
		"\3\2\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2"+
		"\2\2\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2"+
		"\2o\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{"+
		"\3\2\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0083\3\2\2\2\2\u0085\3\2\2\2\2\u0087"+
		"\3\2\2\2\2\u0089\3\2\2\2\3\u008b\3\2\2\2\5\u008e\3\2\2\2\7\u0098\3\2\2"+
		"\2\t\u009f\3\2\2\2\13\u00aa\3\2\2\2\r\u00b2\3\2\2\2\17\u00bb\3\2\2\2\21"+
		"\u00c1\3\2\2\2\23\u00cd\3\2\2\2\25\u00d3\3\2\2\2\27\u00d9\3\2\2\2\31\u00dc"+
		"\3\2\2\2\33\u00e4\3\2\2\2\35\u00ec\3\2\2\2\37\u00f0\3\2\2\2!\u00f5\3\2"+
		"\2\2#\u00fa\3\2\2\2%\u0101\3\2\2\2\'\u0108\3\2\2\2)\u0110\3\2\2\2+\u0114"+
		"\3\2\2\2-\u0119\3\2\2\2/\u0122\3\2\2\2\61\u0129\3\2\2\2\63\u012d\3\2\2"+
		"\2\65\u0133\3\2\2\2\67\u0136\3\2\2\29\u0138\3\2\2\2;\u013a\3\2\2\2=\u013d"+
		"\3\2\2\2?\u013f\3\2\2\2A\u0142\3\2\2\2C\u0145\3\2\2\2E\u0147\3\2\2\2G"+
		"\u0149\3\2\2\2I\u014c\3\2\2\2K\u014f\3\2\2\2M\u0151\3\2\2\2O\u0153\3\2"+
		"\2\2Q\u0155\3\2\2\2S\u0157\3\2\2\2U\u0159\3\2\2\2W\u015b\3\2\2\2Y\u015d"+
		"\3\2\2\2[\u015f\3\2\2\2]\u0161\3\2\2\2_\u0163\3\2\2\2a\u0165\3\2\2\2c"+
		"\u0167\3\2\2\2e\u0169\3\2\2\2g\u016b\3\2\2\2i\u016d\3\2\2\2k\u016f\3\2"+
		"\2\2m\u0171\3\2\2\2o\u0176\3\2\2\2q\u017c\3\2\2\2s\u0181\3\2\2\2u\u0184"+
		"\3\2\2\2w\u0189\3\2\2\2y\u0192\3\2\2\2{\u0196\3\2\2\2}\u019e\3\2\2\2\177"+
		"\u01b4\3\2\2\2\u0081\u01b6\3\2\2\2\u0083\u01b9\3\2\2\2\u0085\u01c3\3\2"+
		"\2\2\u0087\u01ce\3\2\2\2\u0089\u01d2\3\2\2\2\u008b\u008c\7k\2\2\u008c"+
		"\u008d\7p\2\2\u008d\4\3\2\2\2\u008e\u008f\7P\2\2\u008f\u0090\7c\2\2\u0090"+
		"\u0091\7o\2\2\u0091\u0092\7g\2\2\u0092\u0093\7U\2\2\u0093\u0094\7r\2\2"+
		"\u0094\u0095\7c\2\2\u0095\u0096\7e\2\2\u0096\u0097\7g\2\2\u0097\6\3\2"+
		"\2\2\u0098\u0099\7O\2\2\u0099\u009a\7q\2\2\u009a\u009b\7f\2\2\u009b\u009c"+
		"\7w\2\2\u009c\u009d\7n\2\2\u009d\u009e\7g\2\2\u009e\b\3\2\2\2\u009f\u00a0"+
		"\7E\2\2\u00a0\u00a1\7q\2\2\u00a1\u00a2\7p\2\2\u00a2\u00a3\7p\2\2\u00a3"+
		"\u00a4\7g\2\2\u00a4\u00a5\7e\2\2\u00a5\u00a6\7v\2\2\u00a6\u00a7\7k\2\2"+
		"\u00a7\u00a8\7q\2\2\u00a8\u00a9\7p\2\2\u00a9\n\3\2\2\2\u00aa\u00ab\7T"+
		"\2\2\u00ab\u00ac\7g\2\2\u00ac\u00ad\7c\2\2\u00ad\u00ae\7f\2\2\u00ae\u00af"+
		"\7k\2\2\u00af\u00b0\7p\2\2\u00b0\u00b1\7i\2\2\u00b1\f\3\2\2\2\u00b2\u00b3"+
		"\7e\2\2\u00b3\u00b4\7t\2\2\u00b4\u00b5\7k\2\2\u00b5\u00b6\7v\2\2\u00b6"+
		"\u00b7\7g\2\2\u00b7\u00b8\7t\2\2\u00b8\u00b9\7k\2\2\u00b9\u00ba\7c\2\2"+
		"\u00ba\16\3\2\2\2\u00bb\u00bc\7h\2\2\u00bc\u00bd\7k\2\2\u00bd\u00be\7"+
		"g\2\2\u00be\u00bf\7n\2\2\u00bf\u00c0\7f\2\2\u00c0\20\3\2\2\2\u00c1\u00c2"+
		"\7c\2\2\u00c2\u00c3\7i\2\2\u00c3\u00c4\7i\2\2\u00c4\u00c5\7t\2\2\u00c5"+
		"\u00c6\7g\2\2\u00c6\u00c7\7i\2\2\u00c7\u00c8\7c\2\2\u00c8\u00c9\7v\2\2"+
		"\u00c9\u00ca\7k\2\2\u00ca\u00cb\7q\2\2\u00cb\u00cc\7p\2\2\u00cc\22\3\2"+
		"\2\2\u00cd\u00ce\7n\2\2\u00ce\u00cf\7k\2\2\u00cf\u00d0\7o\2\2\u00d0\u00d1"+
		"\7k\2\2\u00d1\u00d2\7v\2\2\u00d2\24\3\2\2\2\u00d3\u00d4\7t\2\2\u00d4\u00d5"+
		"\7c\2\2\u00d5\u00d6\7p\2\2\u00d6\u00d7\7i\2\2\u00d7\u00d8\7g\2\2\u00d8"+
		"\26\3\2\2\2\u00d9\u00da\7v\2\2\u00da\u00db\7q\2\2\u00db\30\3\2\2\2\u00dc"+
		"\u00dd\7i\2\2\u00dd\u00de\7t\2\2\u00de\u00df\7q\2\2\u00df\u00e0\7w\2\2"+
		"\u00e0\u00e1\7r\2\2\u00e1\u00e2\7D\2\2\u00e2\u00e3\7{\2\2\u00e3\32\3\2"+
		"\2\2\u00e4\u00e5\7q\2\2\u00e5\u00e6\7t\2\2\u00e6\u00e7\7f\2\2\u00e7\u00e8"+
		"\7g\2\2\u00e8\u00e9\7t\2\2\u00e9\u00ea\7D\2\2\u00ea\u00eb\7{\2\2\u00eb"+
		"\34\3\2\2\2\u00ec\u00ed\7c\2\2\u00ed\u00ee\7u\2\2\u00ee\u00ef\7e\2\2\u00ef"+
		"\36\3\2\2\2\u00f0\u00f1\7f\2\2\u00f1\u00f2\7g\2\2\u00f2\u00f3\7u\2\2\u00f3"+
		"\u00f4\7e\2\2\u00f4 \3\2\2\2\u00f5\u00f6\7x\2\2\u00f6\u00f7\7q\2\2\u00f7"+
		"\u00f8\7k\2\2\u00f8\u00f9\7f\2\2\u00f9\"\3\2\2\2\u00fa\u00fb\7U\2\2\u00fb"+
		"\u00fc\7v\2\2\u00fc\u00fd\7t\2\2\u00fd\u00fe\7k\2\2\u00fe\u00ff\7p\2\2"+
		"\u00ff\u0100\7i\2\2\u0100$\3\2\2\2\u0101\u0102\7P\2\2\u0102\u0103\7w\2"+
		"\2\u0103\u0104\7o\2\2\u0104\u0105\7d\2\2\u0105\u0106\7g\2\2\u0106\u0107"+
		"\7t\2\2\u0107&\3\2\2\2\u0108\u0109\7D\2\2\u0109\u010a\7q\2\2\u010a\u010b"+
		"\7q\2\2\u010b\u010c\7n\2\2\u010c\u010d\7g\2\2\u010d\u010e\7c\2\2\u010e"+
		"\u010f\7p\2\2\u010f(\3\2\2\2\u0110\u0111\7O\2\2\u0111\u0112\7c\2\2\u0112"+
		"\u0113\7r\2\2\u0113*\3\2\2\2\u0114\u0115\7N\2\2\u0115\u0116\7k\2\2\u0116"+
		"\u0117\7u\2\2\u0117\u0118\7v\2\2\u0118,\3\2\2\2\u0119\u011a\7E\2\2\u011a"+
		"\u011b\7t\2\2\u011b\u011c\7k\2\2\u011c\u011d\7v\2\2\u011d\u011e\7g\2\2"+
		"\u011e\u011f\7t\2\2\u011f\u0120\7k\2\2\u0120\u0121\7c\2\2\u0121.\3\2\2"+
		"\2\u0122\u0123\7t\2\2\u0123\u0124\7g\2\2\u0124\u0125\7v\2\2\u0125\u0126"+
		"\7w\2\2\u0126\u0127\7t\2\2\u0127\u0128\7p\2\2\u0128\60\3\2\2\2\u0129\u012a"+
		"\7v\2\2\u012a\u012b\7t\2\2\u012b\u012c\7{\2\2\u012c\62\3\2\2\2\u012d\u012e"+
		"\7e\2\2\u012e\u012f\7c\2\2\u012f\u0130\7v\2\2\u0130\u0131\7e\2\2\u0131"+
		"\u0132\7j\2\2\u0132\64\3\2\2\2\u0133\u0134\7~\2\2\u0134\u0135\7~\2\2\u0135"+
		"\66\3\2\2\2\u0136\u0137\7~\2\2\u01378\3\2\2\2\u0138\u0139\7\60\2\2\u0139"+
		":\3\2\2\2\u013a\u013b\7(\2\2\u013b\u013c\7(\2\2\u013c<\3\2\2\2\u013d\u013e"+
		"\7(\2\2\u013e>\3\2\2\2\u013f\u0140\7?\2\2\u0140\u0141\7?\2\2\u0141@\3"+
		"\2\2\2\u0142\u0143\7#\2\2\u0143\u0144\7?\2\2\u0144B\3\2\2\2\u0145\u0146"+
		"\7@\2\2\u0146D\3\2\2\2\u0147\u0148\7>\2\2\u0148F\3\2\2\2\u0149\u014a\7"+
		"@\2\2\u014a\u014b\7?\2\2\u014bH\3\2\2\2\u014c\u014d\7>\2\2\u014d\u014e"+
		"\7?\2\2\u014eJ\3\2\2\2\u014f\u0150\7-\2\2\u0150L\3\2\2\2\u0151\u0152\7"+
		"/\2\2\u0152N\3\2\2\2\u0153\u0154\7,\2\2\u0154P\3\2\2\2\u0155\u0156\7\61"+
		"\2\2\u0156R\3\2\2\2\u0157\u0158\7\'\2\2\u0158T\3\2\2\2\u0159\u015a\7`"+
		"\2\2\u015aV\3\2\2\2\u015b\u015c\7#\2\2\u015cX\3\2\2\2\u015d\u015e\7.\2"+
		"\2\u015eZ\3\2\2\2\u015f\u0160\7=\2\2\u0160\\\3\2\2\2\u0161\u0162\7<\2"+
		"\2\u0162^\3\2\2\2\u0163\u0164\7?\2\2\u0164`\3\2\2\2\u0165\u0166\7*\2\2"+
		"\u0166b\3\2\2\2\u0167\u0168\7+\2\2\u0168d\3\2\2\2\u0169\u016a\7}\2\2\u016a"+
		"f\3\2\2\2\u016b\u016c\7\177\2\2\u016ch\3\2\2\2\u016d\u016e\7]\2\2\u016e"+
		"j\3\2\2\2\u016f\u0170\7_\2\2\u0170l\3\2\2\2\u0171\u0172\7v\2\2\u0172\u0173"+
		"\7t\2\2\u0173\u0174\7w\2\2\u0174\u0175\7g\2\2\u0175n\3\2\2\2\u0176\u0177"+
		"\7h\2\2\u0177\u0178\7c\2\2\u0178\u0179\7n\2\2\u0179\u017a\7u\2\2\u017a"+
		"\u017b\7g\2\2\u017bp\3\2\2\2\u017c\u017d\7p\2\2\u017d\u017e\7w\2\2\u017e"+
		"\u017f\7n\2\2\u017f\u0180\7n\2\2\u0180r\3\2\2\2\u0181\u0182\7k\2\2\u0182"+
		"\u0183\7h\2\2\u0183t\3\2\2\2\u0184\u0185\7g\2\2\u0185\u0186\7n\2\2\u0186"+
		"\u0187\7u\2\2\u0187\u0188\7g\2\2\u0188v\3\2\2\2\u0189\u018a\7h\2\2\u018a"+
		"\u018b\7q\2\2\u018b\u018c\7t\2\2\u018c\u018d\7\"\2\2\u018d\u018e\7g\2"+
		"\2\u018e\u018f\7c\2\2\u018f\u0190\7e\2\2\u0190\u0191\7j\2\2\u0191x\3\2"+
		"\2\2\u0192\u0193\7n\2\2\u0193\u0194\7q\2\2\u0194\u0195\7i\2\2\u0195z\3"+
		"\2\2\2\u0196\u019a\t\2\2\2\u0197\u0199\t\3\2\2\u0198\u0197\3\2\2\2\u0199"+
		"\u019c\3\2\2\2\u019a\u0198\3\2\2\2\u019a\u019b\3\2\2\2\u019b|\3\2\2\2"+
		"\u019c\u019a\3\2\2\2\u019d\u019f\t\4\2\2\u019e\u019d\3\2\2\2\u019f\u01a0"+
		"\3\2\2\2\u01a0\u019e\3\2\2\2\u01a0\u01a1\3\2\2\2\u01a1~\3\2\2\2\u01a2"+
		"\u01a4\t\4\2\2\u01a3\u01a2\3\2\2\2\u01a4\u01a5\3\2\2\2\u01a5\u01a3\3\2"+
		"\2\2\u01a5\u01a6\3\2\2\2\u01a6\u01a7\3\2\2\2\u01a7\u01ab\7\60\2\2\u01a8"+
		"\u01aa\t\4\2\2\u01a9\u01a8\3\2\2\2\u01aa\u01ad\3\2\2\2\u01ab\u01a9\3\2"+
		"\2\2\u01ab\u01ac\3\2\2\2\u01ac\u01b5\3\2\2\2\u01ad\u01ab\3\2\2\2\u01ae"+
		"\u01b0\7\60\2\2\u01af\u01b1\t\4\2\2\u01b0\u01af\3\2\2\2\u01b1\u01b2\3"+
		"\2\2\2\u01b2\u01b0\3\2\2\2\u01b2\u01b3\3\2\2\2\u01b3\u01b5\3\2\2\2\u01b4"+
		"\u01a3\3\2\2\2\u01b4\u01ae\3\2\2\2\u01b5\u0080\3\2\2\2\u01b6\u01b7\7^"+
		"\2\2\u01b7\u01b8\7$\2\2\u01b8\u0082\3\2\2\2\u01b9\u01be\7$\2\2\u01ba\u01bd"+
		"\5\u0081A\2\u01bb\u01bd\n\5\2\2\u01bc\u01ba\3\2\2\2\u01bc\u01bb\3\2\2"+
		"\2\u01bd\u01c0\3\2\2\2\u01be\u01bf\3\2\2\2\u01be\u01bc\3\2\2\2\u01bf\u01c1"+
		"\3\2\2\2\u01c0\u01be\3\2\2\2\u01c1\u01c2\7$\2\2\u01c2\u0084\3\2\2\2\u01c3"+
		"\u01c4\7\61\2\2\u01c4\u01c5\7\61\2\2\u01c5\u01c9\3\2\2\2\u01c6\u01c8\n"+
		"\5\2\2\u01c7\u01c6\3\2\2\2\u01c8\u01cb\3\2\2\2\u01c9\u01c7\3\2\2\2\u01c9"+
		"\u01ca\3\2\2\2\u01ca\u01cc\3\2\2\2\u01cb\u01c9\3\2\2\2\u01cc\u01cd\bC"+
		"\2\2\u01cd\u0086\3\2\2\2\u01ce\u01cf\t\6\2\2\u01cf\u01d0\3\2\2\2\u01d0"+
		"\u01d1\bD\2\2\u01d1\u0088\3\2\2\2\u01d2\u01d3\13\2\2\2\u01d3\u008a\3\2"+
		"\2\2\f\2\u019a\u01a0\u01a5\u01ab\u01b2\u01b4\u01bc\u01be\u01c9\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}