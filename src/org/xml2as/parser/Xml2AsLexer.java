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
		C_INPUT=13, C_PACKAGE=14, C_MAIN=15, C_LIST=16, C_SLICE=17, C_DEFAULT=18, 
		C_TYPE=19, C_ENUM=20, C_INT=21, C_UINT=22, C_BOOL=23, C_NUMBER=24, C_STRING=25, 
		NAME=26, STRING=27, COMMENT=28, WS=29;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'.'", "'['", "']'", "'('", "')'", "'{'", "'}'", "'<'", "'>'", "'='", 
		"','", "';'", "'input'", "'package'", "'Main'", "'List'", "'Slice'", "'Default'", 
		"'type'", "'enum'", "'int'", "'uint'", "'Boolean'", "'Number'", "'String'", 
		"NAME", "STRING", "COMMENT", "WS"
	};
	public static final String[] ruleNames = {
		"T__0", "C_BRACKET_L", "C_BRACKET_R", "C_PAREN_L", "C_PAREN_R", "C_BRACE_L", 
		"C_BRACE_R", "C_ANGLE_L", "C_ANGLE_R", "C_EQUALS", "C_COMMA", "C_SEMICOLON", 
		"C_INPUT", "C_PACKAGE", "C_MAIN", "C_LIST", "C_SLICE", "C_DEFAULT", "C_TYPE", 
		"C_ENUM", "C_INT", "C_UINT", "C_BOOL", "C_NUMBER", "C_STRING", "NAME", 
		"STRING", "COMMENT", "WS"
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
		case 28: WS_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void WS_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0: skip();  break;
		}
	}

	public static final String _serializedATN =
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\2\37\u00d2\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\3\2\3\2\3\3\3"+
		"\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3"+
		"\f\3\f\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17"+
		"\3\17\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\21\3\21\3\21\3\22"+
		"\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\23\3\24"+
		"\3\24\3\24\3\24\3\24\3\25\3\25\3\25\3\25\3\25\3\26\3\26\3\26\3\26\3\27"+
		"\3\27\3\27\3\27\3\27\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\30\3\31\3\31"+
		"\3\31\3\31\3\31\3\31\3\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\33\3\33"+
		"\7\33\u00a7\n\33\f\33\16\33\u00aa\13\33\3\34\3\34\7\34\u00ae\n\34\f\34"+
		"\16\34\u00b1\13\34\3\34\3\34\3\35\3\35\3\35\3\35\7\35\u00b9\n\35\f\35"+
		"\16\35\u00bc\13\35\3\35\3\35\3\35\3\35\3\35\3\35\7\35\u00c4\n\35\f\35"+
		"\16\35\u00c7\13\35\3\35\5\35\u00ca\n\35\3\36\6\36\u00cd\n\36\r\36\16\36"+
		"\u00ce\3\36\3\36\5\u00af\u00ba\u00c5\37\3\3\1\5\4\1\7\5\1\t\6\1\13\7\1"+
		"\r\b\1\17\t\1\21\n\1\23\13\1\25\f\1\27\r\1\31\16\1\33\17\1\35\20\1\37"+
		"\21\1!\22\1#\23\1%\24\1\'\25\1)\26\1+\27\1-\30\1/\31\1\61\32\1\63\33\1"+
		"\65\34\1\67\35\19\36\1;\37\2\3\2\6\6\2&&C\\aac|\7\2&&\62;C\\aac|\4\2\f"+
		"\f\17\17\5\2\13\f\17\17\"\"\u00d7\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2"+
		"\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3"+
		"\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2"+
		"\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2"+
		"\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\2\65\3\2"+
		"\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\3=\3\2\2\2\5?\3\2\2\2\7A\3\2\2"+
		"\2\tC\3\2\2\2\13E\3\2\2\2\rG\3\2\2\2\17I\3\2\2\2\21K\3\2\2\2\23M\3\2\2"+
		"\2\25O\3\2\2\2\27Q\3\2\2\2\31S\3\2\2\2\33U\3\2\2\2\35[\3\2\2\2\37c\3\2"+
		"\2\2!h\3\2\2\2#m\3\2\2\2%s\3\2\2\2\'{\3\2\2\2)\u0080\3\2\2\2+\u0085\3"+
		"\2\2\2-\u0089\3\2\2\2/\u008e\3\2\2\2\61\u0096\3\2\2\2\63\u009d\3\2\2\2"+
		"\65\u00a4\3\2\2\2\67\u00ab\3\2\2\29\u00c9\3\2\2\2;\u00cc\3\2\2\2=>\7\60"+
		"\2\2>\4\3\2\2\2?@\7]\2\2@\6\3\2\2\2AB\7_\2\2B\b\3\2\2\2CD\7*\2\2D\n\3"+
		"\2\2\2EF\7+\2\2F\f\3\2\2\2GH\7}\2\2H\16\3\2\2\2IJ\7\177\2\2J\20\3\2\2"+
		"\2KL\7>\2\2L\22\3\2\2\2MN\7@\2\2N\24\3\2\2\2OP\7?\2\2P\26\3\2\2\2QR\7"+
		".\2\2R\30\3\2\2\2ST\7=\2\2T\32\3\2\2\2UV\7k\2\2VW\7p\2\2WX\7r\2\2XY\7"+
		"w\2\2YZ\7v\2\2Z\34\3\2\2\2[\\\7r\2\2\\]\7c\2\2]^\7e\2\2^_\7m\2\2_`\7c"+
		"\2\2`a\7i\2\2ab\7g\2\2b\36\3\2\2\2cd\7O\2\2de\7c\2\2ef\7k\2\2fg\7p\2\2"+
		"g \3\2\2\2hi\7N\2\2ij\7k\2\2jk\7u\2\2kl\7v\2\2l\"\3\2\2\2mn\7U\2\2no\7"+
		"n\2\2op\7k\2\2pq\7e\2\2qr\7g\2\2r$\3\2\2\2st\7F\2\2tu\7g\2\2uv\7h\2\2"+
		"vw\7c\2\2wx\7w\2\2xy\7n\2\2yz\7v\2\2z&\3\2\2\2{|\7v\2\2|}\7{\2\2}~\7r"+
		"\2\2~\177\7g\2\2\177(\3\2\2\2\u0080\u0081\7g\2\2\u0081\u0082\7p\2\2\u0082"+
		"\u0083\7w\2\2\u0083\u0084\7o\2\2\u0084*\3\2\2\2\u0085\u0086\7k\2\2\u0086"+
		"\u0087\7p\2\2\u0087\u0088\7v\2\2\u0088,\3\2\2\2\u0089\u008a\7w\2\2\u008a"+
		"\u008b\7k\2\2\u008b\u008c\7p\2\2\u008c\u008d\7v\2\2\u008d.\3\2\2\2\u008e"+
		"\u008f\7D\2\2\u008f\u0090\7q\2\2\u0090\u0091\7q\2\2\u0091\u0092\7n\2\2"+
		"\u0092\u0093\7g\2\2\u0093\u0094\7c\2\2\u0094\u0095\7p\2\2\u0095\60\3\2"+
		"\2\2\u0096\u0097\7P\2\2\u0097\u0098\7w\2\2\u0098\u0099\7o\2\2\u0099\u009a"+
		"\7d\2\2\u009a\u009b\7g\2\2\u009b\u009c\7t\2\2\u009c\62\3\2\2\2\u009d\u009e"+
		"\7U\2\2\u009e\u009f\7v\2\2\u009f\u00a0\7t\2\2\u00a0\u00a1\7k\2\2\u00a1"+
		"\u00a2\7p\2\2\u00a2\u00a3\7i\2\2\u00a3\64\3\2\2\2\u00a4\u00a8\t\2\2\2"+
		"\u00a5\u00a7\t\3\2\2\u00a6\u00a5\3\2\2\2\u00a7\u00aa\3\2\2\2\u00a8\u00a6"+
		"\3\2\2\2\u00a8\u00a9\3\2\2\2\u00a9\66\3\2\2\2\u00aa\u00a8\3\2\2\2\u00ab"+
		"\u00af\7$\2\2\u00ac\u00ae\13\2\2\2\u00ad\u00ac\3\2\2\2\u00ae\u00b1\3\2"+
		"\2\2\u00af\u00b0\3\2\2\2\u00af\u00ad\3\2\2\2\u00b0\u00b2\3\2\2\2\u00b1"+
		"\u00af\3\2\2\2\u00b2\u00b3\7$\2\2\u00b38\3\2\2\2\u00b4\u00b5\7\61\2\2"+
		"\u00b5\u00b6\7,\2\2\u00b6\u00ba\3\2\2\2\u00b7\u00b9\13\2\2\2\u00b8\u00b7"+
		"\3\2\2\2\u00b9\u00bc\3\2\2\2\u00ba\u00bb\3\2\2\2\u00ba\u00b8\3\2\2\2\u00bb"+
		"\u00bd\3\2\2\2\u00bc\u00ba\3\2\2\2\u00bd\u00be\7,\2\2\u00be\u00ca\7\61"+
		"\2\2\u00bf\u00c0\7\61\2\2\u00c0\u00c1\7\61\2\2\u00c1\u00c5\3\2\2\2\u00c2"+
		"\u00c4\13\2\2\2\u00c3\u00c2\3\2\2\2\u00c4\u00c7\3\2\2\2\u00c5\u00c6\3"+
		"\2\2\2\u00c5\u00c3\3\2\2\2\u00c6\u00c8\3\2\2\2\u00c7\u00c5\3\2\2\2\u00c8"+
		"\u00ca\t\4\2\2\u00c9\u00b4\3\2\2\2\u00c9\u00bf\3\2\2\2\u00ca:\3\2\2\2"+
		"\u00cb\u00cd\t\5\2\2\u00cc\u00cb\3\2\2\2\u00cd\u00ce\3\2\2\2\u00ce\u00cc"+
		"\3\2\2\2\u00ce\u00cf\3\2\2\2\u00cf\u00d0\3\2\2\2\u00d0\u00d1\b\36\2\2"+
		"\u00d1<\3\2\2\2\t\2\u00a8\u00af\u00ba\u00c5\u00c9\u00ce";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}