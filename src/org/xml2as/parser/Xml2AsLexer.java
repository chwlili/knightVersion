// Generated from Xml2As.g4 by ANTLR 4.1
package org.xml2as.parser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class Xml2AsLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, C_BRACKET_L=2, C_BRACKET_R=3, C_PAREN_L=4, C_PAREN_R=5, C_BRACE_L=6, 
		C_BRACE_R=7, C_ANGLE_L=8, C_ANGLE_R=9, C_EQUALS=10, C_COMMA=11, C_SEMICOLON=12, 
		C_INPUT=13, C_FILE=14, C_NODE=15, C_TYPE=16, C_INT=17, C_UINT=18, C_BOOL=19, 
		C_NUMBER=20, C_STRING=21, C_LIST=22, C_HASH=23, C_PACKAGE=24, NAME=25, 
		STRING=26, COMMENT=27, WS=28;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'.'", "'['", "']'", "'('", "')'", "'{'", "'}'", "'<'", "'>'", "'='", 
		"','", "';'", "'input'", "'file'", "'node'", "'type'", "'int'", "'uint'", 
		"'Boolean'", "'Number'", "'String'", "'List'", "'Hash'", "'package'", 
		"NAME", "STRING", "COMMENT", "WS"
	};
	public static final String[] ruleNames = {
		"T__0", "C_BRACKET_L", "C_BRACKET_R", "C_PAREN_L", "C_PAREN_R", "C_BRACE_L", 
		"C_BRACE_R", "C_ANGLE_L", "C_ANGLE_R", "C_EQUALS", "C_COMMA", "C_SEMICOLON", 
		"C_INPUT", "C_FILE", "C_NODE", "C_TYPE", "C_INT", "C_UINT", "C_BOOL", 
		"C_NUMBER", "C_STRING", "C_LIST", "C_HASH", "C_PACKAGE", "NAME", "STRING", 
		"COMMENT", "WS"
	};


	public Xml2AsLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Xml2As.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 27: WS_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: skip();  break;
		}
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2\36\u00c7\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\3\2\3\2\3\3\3\3\3\4\3\4"+
		"\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r"+
		"\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\20\3\20"+
		"\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\23\3\23"+
		"\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\24\3\25\3\25\3\25"+
		"\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\3\27\3\27\3\27"+
		"\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\31\3\31\3\31\3\31\3\31\3\31\3\31"+
		"\3\31\3\32\3\32\7\32\u009c\n\32\f\32\16\32\u009f\13\32\3\33\3\33\7\33"+
		"\u00a3\n\33\f\33\16\33\u00a6\13\33\3\33\3\33\3\34\3\34\3\34\3\34\7\34"+
		"\u00ae\n\34\f\34\16\34\u00b1\13\34\3\34\3\34\3\34\3\34\3\34\3\34\7\34"+
		"\u00b9\n\34\f\34\16\34\u00bc\13\34\3\34\5\34\u00bf\n\34\3\35\6\35\u00c2"+
		"\n\35\r\35\16\35\u00c3\3\35\3\35\5\u00a4\u00af\u00ba\36\3\3\1\5\4\1\7"+
		"\5\1\t\6\1\13\7\1\r\b\1\17\t\1\21\n\1\23\13\1\25\f\1\27\r\1\31\16\1\33"+
		"\17\1\35\20\1\37\21\1!\22\1#\23\1%\24\1\'\25\1)\26\1+\27\1-\30\1/\31\1"+
		"\61\32\1\63\33\1\65\34\1\67\35\19\36\2\3\2\6\4\2C\\c|\6\2\62;C\\aac|\4"+
		"\2\f\f\17\17\5\2\13\f\17\17\"\"\u00cc\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2"+
		"\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2"+
		"\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3"+
		"\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3"+
		"\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65"+
		"\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\3;\3\2\2\2\5=\3\2\2\2\7?\3\2\2\2\tA\3"+
		"\2\2\2\13C\3\2\2\2\rE\3\2\2\2\17G\3\2\2\2\21I\3\2\2\2\23K\3\2\2\2\25M"+
		"\3\2\2\2\27O\3\2\2\2\31Q\3\2\2\2\33S\3\2\2\2\35Y\3\2\2\2\37^\3\2\2\2!"+
		"c\3\2\2\2#h\3\2\2\2%l\3\2\2\2\'q\3\2\2\2)y\3\2\2\2+\u0080\3\2\2\2-\u0087"+
		"\3\2\2\2/\u008c\3\2\2\2\61\u0091\3\2\2\2\63\u0099\3\2\2\2\65\u00a0\3\2"+
		"\2\2\67\u00be\3\2\2\29\u00c1\3\2\2\2;<\7\60\2\2<\4\3\2\2\2=>\7]\2\2>\6"+
		"\3\2\2\2?@\7_\2\2@\b\3\2\2\2AB\7*\2\2B\n\3\2\2\2CD\7+\2\2D\f\3\2\2\2E"+
		"F\7}\2\2F\16\3\2\2\2GH\7\177\2\2H\20\3\2\2\2IJ\7>\2\2J\22\3\2\2\2KL\7"+
		"@\2\2L\24\3\2\2\2MN\7?\2\2N\26\3\2\2\2OP\7.\2\2P\30\3\2\2\2QR\7=\2\2R"+
		"\32\3\2\2\2ST\7k\2\2TU\7p\2\2UV\7r\2\2VW\7w\2\2WX\7v\2\2X\34\3\2\2\2Y"+
		"Z\7h\2\2Z[\7k\2\2[\\\7n\2\2\\]\7g\2\2]\36\3\2\2\2^_\7p\2\2_`\7q\2\2`a"+
		"\7f\2\2ab\7g\2\2b \3\2\2\2cd\7v\2\2de\7{\2\2ef\7r\2\2fg\7g\2\2g\"\3\2"+
		"\2\2hi\7k\2\2ij\7p\2\2jk\7v\2\2k$\3\2\2\2lm\7w\2\2mn\7k\2\2no\7p\2\2o"+
		"p\7v\2\2p&\3\2\2\2qr\7D\2\2rs\7q\2\2st\7q\2\2tu\7n\2\2uv\7g\2\2vw\7c\2"+
		"\2wx\7p\2\2x(\3\2\2\2yz\7P\2\2z{\7w\2\2{|\7o\2\2|}\7d\2\2}~\7g\2\2~\177"+
		"\7t\2\2\177*\3\2\2\2\u0080\u0081\7U\2\2\u0081\u0082\7v\2\2\u0082\u0083"+
		"\7t\2\2\u0083\u0084\7k\2\2\u0084\u0085\7p\2\2\u0085\u0086\7i\2\2\u0086"+
		",\3\2\2\2\u0087\u0088\7N\2\2\u0088\u0089\7k\2\2\u0089\u008a\7u\2\2\u008a"+
		"\u008b\7v\2\2\u008b.\3\2\2\2\u008c\u008d\7J\2\2\u008d\u008e\7c\2\2\u008e"+
		"\u008f\7u\2\2\u008f\u0090\7j\2\2\u0090\60\3\2\2\2\u0091\u0092\7r\2\2\u0092"+
		"\u0093\7c\2\2\u0093\u0094\7e\2\2\u0094\u0095\7m\2\2\u0095\u0096\7c\2\2"+
		"\u0096\u0097\7i\2\2\u0097\u0098\7g\2\2\u0098\62\3\2\2\2\u0099\u009d\t"+
		"\2\2\2\u009a\u009c\t\3\2\2\u009b\u009a\3\2\2\2\u009c\u009f\3\2\2\2\u009d"+
		"\u009b\3\2\2\2\u009d\u009e\3\2\2\2\u009e\64\3\2\2\2\u009f\u009d\3\2\2"+
		"\2\u00a0\u00a4\7$\2\2\u00a1\u00a3\13\2\2\2\u00a2\u00a1\3\2\2\2\u00a3\u00a6"+
		"\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a4\u00a2\3\2\2\2\u00a5\u00a7\3\2\2\2\u00a6"+
		"\u00a4\3\2\2\2\u00a7\u00a8\7$\2\2\u00a8\66\3\2\2\2\u00a9\u00aa\7\61\2"+
		"\2\u00aa\u00ab\7,\2\2\u00ab\u00af\3\2\2\2\u00ac\u00ae\13\2\2\2\u00ad\u00ac"+
		"\3\2\2\2\u00ae\u00b1\3\2\2\2\u00af\u00b0\3\2\2\2\u00af\u00ad\3\2\2\2\u00b0"+
		"\u00b2\3\2\2\2\u00b1\u00af\3\2\2\2\u00b2\u00b3\7,\2\2\u00b3\u00bf\7\61"+
		"\2\2\u00b4\u00b5\7\61\2\2\u00b5\u00b6\7\61\2\2\u00b6\u00ba\3\2\2\2\u00b7"+
		"\u00b9\13\2\2\2\u00b8\u00b7\3\2\2\2\u00b9\u00bc\3\2\2\2\u00ba\u00bb\3"+
		"\2\2\2\u00ba\u00b8\3\2\2\2\u00bb\u00bd\3\2\2\2\u00bc\u00ba\3\2\2\2\u00bd"+
		"\u00bf\t\4\2\2\u00be\u00a9\3\2\2\2\u00be\u00b4\3\2\2\2\u00bf8\3\2\2\2"+
		"\u00c0\u00c2\t\5\2\2\u00c1\u00c0\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c3\u00c1"+
		"\3\2\2\2\u00c3\u00c4\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5\u00c6\b\35\2\2"+
		"\u00c6:\3\2\2\2\t\2\u009d\u00a4\u00af\u00ba\u00be\u00c3";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}