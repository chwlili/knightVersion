// Generated from Xml2As.g4 by ANTLR 4.1
package org.xml2as.parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class Xml2AsParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, C_BRACKET_L=2, C_BRACKET_R=3, C_PAREN_L=4, C_PAREN_R=5, C_BRACE_L=6, 
		C_BRACE_R=7, C_ANGLE_L=8, C_ANGLE_R=9, C_EQUALS=10, C_COMMA=11, C_SEMICOLON=12, 
		C_INPUT=13, C_FILE=14, C_NODE=15, C_TYPE=16, C_INT=17, C_UINT=18, C_BOOL=19, 
		C_NUMBER=20, C_STRING=21, C_LIST=22, C_HASH=23, C_PACKAGE=24, NAME=25, 
		STRING=26, COMMENT=27, WS=28;
	public static final String[] tokenNames = {
		"<INVALID>", "'.'", "'['", "']'", "'('", "')'", "'{'", "'}'", "'<'", "'>'", 
		"'='", "','", "';'", "'input'", "'file'", "'node'", "'type'", "'int'", 
		"'uint'", "'Boolean'", "'Number'", "'String'", "'List'", "'Hash'", "'package'", 
		"NAME", "STRING", "COMMENT", "WS"
	};
	public static final int
		RULE_xml2 = 0, RULE_type = 1, RULE_input = 2, RULE_field = 3, RULE_nativeType = 4, 
		RULE_listType = 5, RULE_hashType = 6, RULE_packName = 7, RULE_typeName = 8;
	public static final String[] ruleNames = {
		"xml2", "type", "input", "field", "nativeType", "listType", "hashType", 
		"packName", "typeName"
	};

	@Override
	public String getGrammarFileName() { return "Xml2As.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public Xml2AsParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class Xml2Context extends ParserRuleContext {
		public TerminalNode C_SEMICOLON() { return getToken(Xml2AsParser.C_SEMICOLON, 0); }
		public TerminalNode COMMENT(int i) {
			return getToken(Xml2AsParser.COMMENT, i);
		}
		public TerminalNode C_PACKAGE() { return getToken(Xml2AsParser.C_PACKAGE, 0); }
		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class,i);
		}
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public List<TerminalNode> COMMENT() { return getTokens(Xml2AsParser.COMMENT); }
		public PackNameContext packName() {
			return getRuleContext(PackNameContext.class,0);
		}
		public Xml2Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_xml2; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterXml2(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitXml2(this);
		}
	}

	public final Xml2Context xml2() throws RecognitionException {
		Xml2Context _localctx = new Xml2Context(_ctx, getState());
		enterRule(_localctx, 0, RULE_xml2);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(21);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMENT) {
				{
				{
				setState(18); match(COMMENT);
				}
				}
				setState(23);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(24); match(C_PACKAGE);
			setState(26);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(25); packName();
				}
				break;
			}
			setState(29);
			_la = _input.LA(1);
			if (_la==C_SEMICOLON) {
				{
				setState(28); match(C_SEMICOLON);
				}
			}

			setState(35);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << C_BRACKET_L) | (1L << C_TYPE) | (1L << COMMENT))) != 0)) {
				{
				setState(33);
				switch (_input.LA(1)) {
				case C_BRACKET_L:
				case C_TYPE:
					{
					setState(31); type();
					}
					break;
				case COMMENT:
					{
					setState(32); match(COMMENT);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(37);
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

	public static class TypeContext extends ParserRuleContext {
		public TerminalNode C_TYPE() { return getToken(Xml2AsParser.C_TYPE, 0); }
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TerminalNode C_BRACE_R() { return getToken(Xml2AsParser.C_BRACE_R, 0); }
		public FieldContext field(int i) {
			return getRuleContext(FieldContext.class,i);
		}
		public TerminalNode COMMENT(int i) {
			return getToken(Xml2AsParser.COMMENT, i);
		}
		public List<FieldContext> field() {
			return getRuleContexts(FieldContext.class);
		}
		public List<TerminalNode> COMMENT() { return getTokens(Xml2AsParser.COMMENT); }
		public InputContext input() {
			return getRuleContext(InputContext.class,0);
		}
		public TerminalNode C_BRACE_L() { return getToken(Xml2AsParser.C_BRACE_L, 0); }
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitType(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		TypeContext _localctx = new TypeContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(39);
			_la = _input.LA(1);
			if (_la==C_BRACKET_L) {
				{
				setState(38); input();
				}
			}

			setState(41); match(C_TYPE);
			setState(42); typeName();
			setState(43); match(C_BRACE_L);
			setState(48);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << C_INPUT) | (1L << C_FILE) | (1L << C_NODE) | (1L << C_TYPE) | (1L << C_INT) | (1L << C_UINT) | (1L << C_BOOL) | (1L << C_NUMBER) | (1L << C_STRING) | (1L << C_LIST) | (1L << C_HASH) | (1L << NAME) | (1L << COMMENT))) != 0)) {
				{
				setState(46);
				switch (_input.LA(1)) {
				case C_INPUT:
				case C_FILE:
				case C_NODE:
				case C_TYPE:
				case C_INT:
				case C_UINT:
				case C_BOOL:
				case C_NUMBER:
				case C_STRING:
				case C_LIST:
				case C_HASH:
				case NAME:
					{
					setState(44); field();
					}
					break;
				case COMMENT:
					{
					setState(45); match(COMMENT);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(50);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(51); match(C_BRACE_R);
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

	public static class InputContext extends ParserRuleContext {
		public Token filePath;
		public Token nodePath;
		public TerminalNode C_COMMA() { return getToken(Xml2AsParser.C_COMMA, 0); }
		public TerminalNode C_PAREN_R() { return getToken(Xml2AsParser.C_PAREN_R, 0); }
		public TerminalNode C_PAREN_L() { return getToken(Xml2AsParser.C_PAREN_L, 0); }
		public TerminalNode STRING(int i) {
			return getToken(Xml2AsParser.STRING, i);
		}
		public List<TerminalNode> C_EQUALS() { return getTokens(Xml2AsParser.C_EQUALS); }
		public TerminalNode C_NODE() { return getToken(Xml2AsParser.C_NODE, 0); }
		public TerminalNode C_BRACKET_L() { return getToken(Xml2AsParser.C_BRACKET_L, 0); }
		public TerminalNode C_INPUT() { return getToken(Xml2AsParser.C_INPUT, 0); }
		public List<TerminalNode> STRING() { return getTokens(Xml2AsParser.STRING); }
		public TerminalNode C_FILE() { return getToken(Xml2AsParser.C_FILE, 0); }
		public TerminalNode C_BRACKET_R() { return getToken(Xml2AsParser.C_BRACKET_R, 0); }
		public TerminalNode C_EQUALS(int i) {
			return getToken(Xml2AsParser.C_EQUALS, i);
		}
		public InputContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_input; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterInput(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitInput(this);
		}
	}

	public final InputContext input() throws RecognitionException {
		InputContext _localctx = new InputContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_input);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(53); match(C_BRACKET_L);
			setState(54); match(C_INPUT);
			setState(55); match(C_PAREN_L);
			setState(56); match(C_FILE);
			setState(57); match(C_EQUALS);
			setState(58); ((InputContext)_localctx).filePath = match(STRING);
			setState(63);
			_la = _input.LA(1);
			if (_la==C_COMMA) {
				{
				setState(59); match(C_COMMA);
				setState(60); match(C_NODE);
				setState(61); match(C_EQUALS);
				setState(62); ((InputContext)_localctx).nodePath = match(STRING);
				}
			}

			setState(65); match(C_PAREN_R);
			setState(66); match(C_BRACKET_R);
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

	public static class FieldContext extends ParserRuleContext {
		public Token nodePath;
		public TerminalNode C_EQUALS() { return getToken(Xml2AsParser.C_EQUALS, 0); }
		public TerminalNode C_SEMICOLON() { return getToken(Xml2AsParser.C_SEMICOLON, 0); }
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public HashTypeContext hashType() {
			return getRuleContext(HashTypeContext.class,0);
		}
		public ListTypeContext listType() {
			return getRuleContext(ListTypeContext.class,0);
		}
		public NativeTypeContext nativeType() {
			return getRuleContext(NativeTypeContext.class,0);
		}
		public TerminalNode STRING() { return getToken(Xml2AsParser.STRING, 0); }
		public FieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_field; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitField(this);
		}
	}

	public final FieldContext field() throws RecognitionException {
		FieldContext _localctx = new FieldContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(71);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				{
				setState(68); nativeType();
				}
				break;

			case 2:
				{
				setState(69); listType();
				}
				break;

			case 3:
				{
				setState(70); hashType();
				}
				break;
			}
			setState(73); typeName();
			setState(74); match(C_EQUALS);
			setState(75); ((FieldContext)_localctx).nodePath = match(STRING);
			setState(77);
			_la = _input.LA(1);
			if (_la==C_SEMICOLON) {
				{
				setState(76); match(C_SEMICOLON);
				}
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

	public static class NativeTypeContext extends ParserRuleContext {
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public NativeTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nativeType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterNativeType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitNativeType(this);
		}
	}

	public final NativeTypeContext nativeType() throws RecognitionException {
		NativeTypeContext _localctx = new NativeTypeContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_nativeType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79); typeName();
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

	public static class ListTypeContext extends ParserRuleContext {
		public TerminalNode C_ANGLE_R() { return getToken(Xml2AsParser.C_ANGLE_R, 0); }
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TerminalNode C_ANGLE_L() { return getToken(Xml2AsParser.C_ANGLE_L, 0); }
		public TerminalNode C_LIST() { return getToken(Xml2AsParser.C_LIST, 0); }
		public ListTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterListType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitListType(this);
		}
	}

	public final ListTypeContext listType() throws RecognitionException {
		ListTypeContext _localctx = new ListTypeContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_listType);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(81); match(C_LIST);
			setState(82); match(C_ANGLE_L);
			setState(83); typeName();
			setState(84); match(C_ANGLE_R);
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

	public static class HashTypeContext extends ParserRuleContext {
		public Token NAME;
		public List<Token> params = new ArrayList<Token>();
		public TerminalNode C_COMMA(int i) {
			return getToken(Xml2AsParser.C_COMMA, i);
		}
		public List<TerminalNode> C_COMMA() { return getTokens(Xml2AsParser.C_COMMA); }
		public TerminalNode C_PAREN_R() { return getToken(Xml2AsParser.C_PAREN_R, 0); }
		public TerminalNode C_PAREN_L() { return getToken(Xml2AsParser.C_PAREN_L, 0); }
		public TerminalNode C_HASH() { return getToken(Xml2AsParser.C_HASH, 0); }
		public TerminalNode C_ANGLE_R() { return getToken(Xml2AsParser.C_ANGLE_R, 0); }
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TerminalNode NAME(int i) {
			return getToken(Xml2AsParser.NAME, i);
		}
		public TerminalNode C_ANGLE_L() { return getToken(Xml2AsParser.C_ANGLE_L, 0); }
		public List<TerminalNode> NAME() { return getTokens(Xml2AsParser.NAME); }
		public HashTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hashType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterHashType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitHashType(this);
		}
	}

	public final HashTypeContext hashType() throws RecognitionException {
		HashTypeContext _localctx = new HashTypeContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_hashType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(86); match(C_HASH);
			setState(87); match(C_ANGLE_L);
			setState(88); typeName();
			setState(89); match(C_ANGLE_R);
			setState(100);
			_la = _input.LA(1);
			if (_la==C_PAREN_L) {
				{
				setState(90); match(C_PAREN_L);
				setState(91); ((HashTypeContext)_localctx).NAME = match(NAME);
				((HashTypeContext)_localctx).params.add(((HashTypeContext)_localctx).NAME);
				setState(96);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==C_COMMA) {
					{
					{
					setState(92); match(C_COMMA);
					setState(93); ((HashTypeContext)_localctx).NAME = match(NAME);
					((HashTypeContext)_localctx).params.add(((HashTypeContext)_localctx).NAME);
					}
					}
					setState(98);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(99); match(C_PAREN_R);
				}
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

	public static class PackNameContext extends ParserRuleContext {
		public List<TypeNameContext> typeName() {
			return getRuleContexts(TypeNameContext.class);
		}
		public TypeNameContext typeName(int i) {
			return getRuleContext(TypeNameContext.class,i);
		}
		public PackNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_packName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterPackName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitPackName(this);
		}
	}

	public final PackNameContext packName() throws RecognitionException {
		PackNameContext _localctx = new PackNameContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_packName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(102); typeName();
			setState(107);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==1) {
				{
				{
				setState(103); match(1);
				setState(104); typeName();
				}
				}
				setState(109);
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

	public static class TypeNameContext extends ParserRuleContext {
		public TerminalNode C_HASH() { return getToken(Xml2AsParser.C_HASH, 0); }
		public TerminalNode C_TYPE() { return getToken(Xml2AsParser.C_TYPE, 0); }
		public TerminalNode C_UINT() { return getToken(Xml2AsParser.C_UINT, 0); }
		public TerminalNode C_BOOL() { return getToken(Xml2AsParser.C_BOOL, 0); }
		public TerminalNode C_NODE() { return getToken(Xml2AsParser.C_NODE, 0); }
		public TerminalNode C_STRING() { return getToken(Xml2AsParser.C_STRING, 0); }
		public TerminalNode C_LIST() { return getToken(Xml2AsParser.C_LIST, 0); }
		public TerminalNode NAME() { return getToken(Xml2AsParser.NAME, 0); }
		public TerminalNode C_INPUT() { return getToken(Xml2AsParser.C_INPUT, 0); }
		public TerminalNode C_NUMBER() { return getToken(Xml2AsParser.C_NUMBER, 0); }
		public TerminalNode C_FILE() { return getToken(Xml2AsParser.C_FILE, 0); }
		public TerminalNode C_INT() { return getToken(Xml2AsParser.C_INT, 0); }
		public TypeNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterTypeName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitTypeName(this);
		}
	}

	public final TypeNameContext typeName() throws RecognitionException {
		TypeNameContext _localctx = new TypeNameContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_typeName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(110);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << C_INPUT) | (1L << C_FILE) | (1L << C_NODE) | (1L << C_TYPE) | (1L << C_INT) | (1L << C_UINT) | (1L << C_BOOL) | (1L << C_NUMBER) | (1L << C_STRING) | (1L << C_LIST) | (1L << C_HASH) | (1L << NAME))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			consume();
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
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3\36s\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\7\2\26"+
		"\n\2\f\2\16\2\31\13\2\3\2\3\2\5\2\35\n\2\3\2\5\2 \n\2\3\2\3\2\7\2$\n\2"+
		"\f\2\16\2\'\13\2\3\3\5\3*\n\3\3\3\3\3\3\3\3\3\3\3\7\3\61\n\3\f\3\16\3"+
		"\64\13\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4B\n\4\3\4"+
		"\3\4\3\4\3\5\3\5\3\5\5\5J\n\5\3\5\3\5\3\5\3\5\5\5P\n\5\3\6\3\6\3\7\3\7"+
		"\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\7\ba\n\b\f\b\16\bd\13\b\3"+
		"\b\5\bg\n\b\3\t\3\t\3\t\7\tl\n\t\f\t\16\to\13\t\3\n\3\n\3\n\2\13\2\4\6"+
		"\b\n\f\16\20\22\2\3\4\2\17\31\33\33x\2\27\3\2\2\2\4)\3\2\2\2\6\67\3\2"+
		"\2\2\bI\3\2\2\2\nQ\3\2\2\2\fS\3\2\2\2\16X\3\2\2\2\20h\3\2\2\2\22p\3\2"+
		"\2\2\24\26\7\35\2\2\25\24\3\2\2\2\26\31\3\2\2\2\27\25\3\2\2\2\27\30\3"+
		"\2\2\2\30\32\3\2\2\2\31\27\3\2\2\2\32\34\7\32\2\2\33\35\5\20\t\2\34\33"+
		"\3\2\2\2\34\35\3\2\2\2\35\37\3\2\2\2\36 \7\16\2\2\37\36\3\2\2\2\37 \3"+
		"\2\2\2 %\3\2\2\2!$\5\4\3\2\"$\7\35\2\2#!\3\2\2\2#\"\3\2\2\2$\'\3\2\2\2"+
		"%#\3\2\2\2%&\3\2\2\2&\3\3\2\2\2\'%\3\2\2\2(*\5\6\4\2)(\3\2\2\2)*\3\2\2"+
		"\2*+\3\2\2\2+,\7\22\2\2,-\5\22\n\2-\62\7\b\2\2.\61\5\b\5\2/\61\7\35\2"+
		"\2\60.\3\2\2\2\60/\3\2\2\2\61\64\3\2\2\2\62\60\3\2\2\2\62\63\3\2\2\2\63"+
		"\65\3\2\2\2\64\62\3\2\2\2\65\66\7\t\2\2\66\5\3\2\2\2\678\7\4\2\289\7\17"+
		"\2\29:\7\6\2\2:;\7\20\2\2;<\7\f\2\2<A\7\34\2\2=>\7\r\2\2>?\7\21\2\2?@"+
		"\7\f\2\2@B\7\34\2\2A=\3\2\2\2AB\3\2\2\2BC\3\2\2\2CD\7\7\2\2DE\7\5\2\2"+
		"E\7\3\2\2\2FJ\5\n\6\2GJ\5\f\7\2HJ\5\16\b\2IF\3\2\2\2IG\3\2\2\2IH\3\2\2"+
		"\2JK\3\2\2\2KL\5\22\n\2LM\7\f\2\2MO\7\34\2\2NP\7\16\2\2ON\3\2\2\2OP\3"+
		"\2\2\2P\t\3\2\2\2QR\5\22\n\2R\13\3\2\2\2ST\7\30\2\2TU\7\n\2\2UV\5\22\n"+
		"\2VW\7\13\2\2W\r\3\2\2\2XY\7\31\2\2YZ\7\n\2\2Z[\5\22\n\2[f\7\13\2\2\\"+
		"]\7\6\2\2]b\7\33\2\2^_\7\r\2\2_a\7\33\2\2`^\3\2\2\2ad\3\2\2\2b`\3\2\2"+
		"\2bc\3\2\2\2ce\3\2\2\2db\3\2\2\2eg\7\7\2\2f\\\3\2\2\2fg\3\2\2\2g\17\3"+
		"\2\2\2hm\5\22\n\2ij\7\3\2\2jl\5\22\n\2ki\3\2\2\2lo\3\2\2\2mk\3\2\2\2m"+
		"n\3\2\2\2n\21\3\2\2\2om\3\2\2\2pq\t\2\2\2q\23\3\2\2\2\20\27\34\37#%)\60"+
		"\62AIObfm";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}