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
		C_INPUT=13, C_PACKAGE=14, C_MAIN=15, C_LIST=16, C_SLICE=17, C_TYPE=18, 
		C_INT=19, C_UINT=20, C_BOOL=21, C_NUMBER=22, C_STRING=23, NAME=24, STRING=25, 
		COMMENT=26, WS=27;
	public static final String[] tokenNames = {
		"<INVALID>", "'.'", "'['", "']'", "'('", "')'", "'{'", "'}'", "'<'", "'>'", 
		"'='", "','", "';'", "'input'", "'package'", "'Main'", "'List'", "'Slice'", 
		"'type'", "'int'", "'uint'", "'Boolean'", "'Number'", "'String'", "NAME", 
		"STRING", "COMMENT", "WS"
	};
	public static final int
		RULE_xml2 = 0, RULE_inputDef = 1, RULE_packDef = 2, RULE_type = 3, RULE_typeMeta = 4, 
		RULE_field = 5, RULE_fieldMeta = 6, RULE_listMeta = 7, RULE_sliceMeta = 8, 
		RULE_packName = 9, RULE_typeName = 10;
	public static final String[] ruleNames = {
		"xml2", "inputDef", "packDef", "type", "typeMeta", "field", "fieldMeta", 
		"listMeta", "sliceMeta", "packName", "typeName"
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
		public InputDefContext input;
		public PackDefContext pack;
		public TypeContext type;
		public List<TypeContext> types = new ArrayList<TypeContext>();
		public InputDefContext inputDef() {
			return getRuleContext(InputDefContext.class,0);
		}
		public PackDefContext packDef() {
			return getRuleContext(PackDefContext.class,0);
		}
		public TerminalNode COMMENT(int i) {
			return getToken(Xml2AsParser.COMMENT, i);
		}
		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class,i);
		}
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public List<TerminalNode> COMMENT() { return getTokens(Xml2AsParser.COMMENT); }
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
			setState(30);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(22); ((Xml2Context)_localctx).input = inputDef();
				setState(23); ((Xml2Context)_localctx).pack = packDef();
				}
				break;

			case 2:
				{
				setState(25); ((Xml2Context)_localctx).pack = packDef();
				setState(26); ((Xml2Context)_localctx).input = inputDef();
				}
				break;

			case 3:
				{
				setState(28); ((Xml2Context)_localctx).pack = packDef();
				}
				break;

			case 4:
				{
				setState(29); ((Xml2Context)_localctx).input = inputDef();
				}
				break;
			}
			setState(36);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << C_BRACKET_L) | (1L << C_TYPE) | (1L << COMMENT))) != 0)) {
				{
				setState(34);
				switch (_input.LA(1)) {
				case C_BRACKET_L:
				case C_TYPE:
					{
					setState(32); ((Xml2Context)_localctx).type = type();
					((Xml2Context)_localctx).types.add(((Xml2Context)_localctx).type);
					}
					break;
				case COMMENT:
					{
					setState(33); match(COMMENT);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(38);
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

	public static class InputDefContext extends ParserRuleContext {
		public Token url;
		public TerminalNode C_SEMICOLON() { return getToken(Xml2AsParser.C_SEMICOLON, 0); }
		public TerminalNode COMMENT(int i) {
			return getToken(Xml2AsParser.COMMENT, i);
		}
		public TerminalNode C_INPUT() { return getToken(Xml2AsParser.C_INPUT, 0); }
		public List<TerminalNode> COMMENT() { return getTokens(Xml2AsParser.COMMENT); }
		public TerminalNode STRING() { return getToken(Xml2AsParser.STRING, 0); }
		public InputDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_inputDef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterInputDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitInputDef(this);
		}
	}

	public final InputDefContext inputDef() throws RecognitionException {
		InputDefContext _localctx = new InputDefContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_inputDef);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(42);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMENT) {
				{
				{
				setState(39); match(COMMENT);
				}
				}
				setState(44);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(45); match(C_INPUT);
			setState(46); ((InputDefContext)_localctx).url = match(STRING);
			setState(47); match(C_SEMICOLON);
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

	public static class PackDefContext extends ParserRuleContext {
		public PackNameContext pack;
		public TerminalNode C_SEMICOLON() { return getToken(Xml2AsParser.C_SEMICOLON, 0); }
		public TerminalNode COMMENT(int i) {
			return getToken(Xml2AsParser.COMMENT, i);
		}
		public TerminalNode C_PACKAGE() { return getToken(Xml2AsParser.C_PACKAGE, 0); }
		public List<TerminalNode> COMMENT() { return getTokens(Xml2AsParser.COMMENT); }
		public PackNameContext packName() {
			return getRuleContext(PackNameContext.class,0);
		}
		public PackDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_packDef; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterPackDef(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitPackDef(this);
		}
	}

	public final PackDefContext packDef() throws RecognitionException {
		PackDefContext _localctx = new PackDefContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_packDef);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(52);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMENT) {
				{
				{
				setState(49); match(COMMENT);
				}
				}
				setState(54);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(55); match(C_PACKAGE);
			setState(57);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(56); ((PackDefContext)_localctx).pack = packName();
				}
				break;
			}
			setState(60);
			_la = _input.LA(1);
			if (_la==C_SEMICOLON) {
				{
				setState(59); match(C_SEMICOLON);
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
		public TypeMetaContext typeMeta() {
			return getRuleContext(TypeMetaContext.class,0);
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
		enterRule(_localctx, 6, RULE_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(63);
			_la = _input.LA(1);
			if (_la==C_BRACKET_L) {
				{
				setState(62); typeMeta();
				}
			}

			setState(65); match(C_TYPE);
			setState(66); typeName();
			setState(67); match(C_BRACE_L);
			setState(72);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << C_BRACKET_L) | (1L << C_INPUT) | (1L << C_MAIN) | (1L << C_LIST) | (1L << C_TYPE) | (1L << C_INT) | (1L << C_UINT) | (1L << C_BOOL) | (1L << C_NUMBER) | (1L << C_STRING) | (1L << NAME) | (1L << COMMENT))) != 0)) {
				{
				setState(70);
				switch (_input.LA(1)) {
				case C_BRACKET_L:
				case C_INPUT:
				case C_MAIN:
				case C_LIST:
				case C_TYPE:
				case C_INT:
				case C_UINT:
				case C_BOOL:
				case C_NUMBER:
				case C_STRING:
				case NAME:
					{
					setState(68); field();
					}
					break;
				case COMMENT:
					{
					setState(69); match(COMMENT);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(74);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(75); match(C_BRACE_R);
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

	public static class TypeMetaContext extends ParserRuleContext {
		public Token xpath;
		public TerminalNode C_PAREN_R() { return getToken(Xml2AsParser.C_PAREN_R, 0); }
		public TerminalNode C_PAREN_L() { return getToken(Xml2AsParser.C_PAREN_L, 0); }
		public TerminalNode C_BRACKET_L() { return getToken(Xml2AsParser.C_BRACKET_L, 0); }
		public TerminalNode C_MAIN() { return getToken(Xml2AsParser.C_MAIN, 0); }
		public TerminalNode STRING() { return getToken(Xml2AsParser.STRING, 0); }
		public TerminalNode C_BRACKET_R() { return getToken(Xml2AsParser.C_BRACKET_R, 0); }
		public TypeMetaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeMeta; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterTypeMeta(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitTypeMeta(this);
		}
	}

	public final TypeMetaContext typeMeta() throws RecognitionException {
		TypeMetaContext _localctx = new TypeMetaContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_typeMeta);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(77); match(C_BRACKET_L);
			setState(78); match(C_MAIN);
			setState(79); match(C_PAREN_L);
			setState(80); ((TypeMetaContext)_localctx).xpath = match(STRING);
			setState(81); match(C_PAREN_R);
			setState(82); match(C_BRACKET_R);
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
		public TypeNameContext fieldType;
		public TypeNameContext fieldName;
		public Token fieldXPath;
		public TerminalNode C_EQUALS() { return getToken(Xml2AsParser.C_EQUALS, 0); }
		public TerminalNode C_SEMICOLON() { return getToken(Xml2AsParser.C_SEMICOLON, 0); }
		public List<TypeNameContext> typeName() {
			return getRuleContexts(TypeNameContext.class);
		}
		public FieldMetaContext fieldMeta() {
			return getRuleContext(FieldMetaContext.class,0);
		}
		public TypeNameContext typeName(int i) {
			return getRuleContext(TypeNameContext.class,i);
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
		enterRule(_localctx, 10, RULE_field);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84); fieldMeta();
			setState(85); ((FieldContext)_localctx).fieldType = typeName();
			setState(86); ((FieldContext)_localctx).fieldName = typeName();
			setState(87); match(C_EQUALS);
			setState(88); ((FieldContext)_localctx).fieldXPath = match(STRING);
			setState(90);
			_la = _input.LA(1);
			if (_la==C_SEMICOLON) {
				{
				setState(89); match(C_SEMICOLON);
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

	public static class FieldMetaContext extends ParserRuleContext {
		public List<ListMetaContext> listMeta() {
			return getRuleContexts(ListMetaContext.class);
		}
		public ListMetaContext listMeta(int i) {
			return getRuleContext(ListMetaContext.class,i);
		}
		public SliceMetaContext sliceMeta(int i) {
			return getRuleContext(SliceMetaContext.class,i);
		}
		public List<SliceMetaContext> sliceMeta() {
			return getRuleContexts(SliceMetaContext.class);
		}
		public FieldMetaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fieldMeta; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterFieldMeta(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitFieldMeta(this);
		}
	}

	public final FieldMetaContext fieldMeta() throws RecognitionException {
		FieldMetaContext _localctx = new FieldMetaContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_fieldMeta);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(96);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==C_BRACKET_L) {
				{
				setState(94);
				switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
				case 1:
					{
					setState(92); listMeta();
					}
					break;

				case 2:
					{
					setState(93); sliceMeta();
					}
					break;
				}
				}
				setState(98);
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

	public static class ListMetaContext extends ParserRuleContext {
		public TypeNameContext typeName;
		public List<TypeNameContext> key = new ArrayList<TypeNameContext>();
		public TerminalNode C_COMMA(int i) {
			return getToken(Xml2AsParser.C_COMMA, i);
		}
		public List<TerminalNode> C_COMMA() { return getTokens(Xml2AsParser.C_COMMA); }
		public TerminalNode C_PAREN_R() { return getToken(Xml2AsParser.C_PAREN_R, 0); }
		public TerminalNode C_PAREN_L() { return getToken(Xml2AsParser.C_PAREN_L, 0); }
		public List<TypeNameContext> typeName() {
			return getRuleContexts(TypeNameContext.class);
		}
		public TerminalNode C_BRACKET_L() { return getToken(Xml2AsParser.C_BRACKET_L, 0); }
		public TypeNameContext typeName(int i) {
			return getRuleContext(TypeNameContext.class,i);
		}
		public TerminalNode C_LIST() { return getToken(Xml2AsParser.C_LIST, 0); }
		public TerminalNode C_BRACKET_R() { return getToken(Xml2AsParser.C_BRACKET_R, 0); }
		public ListMetaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listMeta; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterListMeta(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitListMeta(this);
		}
	}

	public final ListMetaContext listMeta() throws RecognitionException {
		ListMetaContext _localctx = new ListMetaContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_listMeta);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99); match(C_BRACKET_L);
			setState(100); match(C_LIST);
			setState(113);
			_la = _input.LA(1);
			if (_la==C_PAREN_L) {
				{
				setState(101); match(C_PAREN_L);
				setState(110);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << C_INPUT) | (1L << C_MAIN) | (1L << C_LIST) | (1L << C_TYPE) | (1L << C_INT) | (1L << C_UINT) | (1L << C_BOOL) | (1L << C_NUMBER) | (1L << C_STRING) | (1L << NAME))) != 0)) {
					{
					setState(102); ((ListMetaContext)_localctx).typeName = typeName();
					((ListMetaContext)_localctx).key.add(((ListMetaContext)_localctx).typeName);
					setState(107);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==C_COMMA) {
						{
						{
						setState(103); match(C_COMMA);
						setState(104); ((ListMetaContext)_localctx).typeName = typeName();
						((ListMetaContext)_localctx).key.add(((ListMetaContext)_localctx).typeName);
						}
						}
						setState(109);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(112); match(C_PAREN_R);
				}
			}

			setState(115); match(C_BRACKET_R);
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

	public static class SliceMetaContext extends ParserRuleContext {
		public Token prefix;
		public Token sliceChar;
		public TerminalNode C_PAREN_R() { return getToken(Xml2AsParser.C_PAREN_R, 0); }
		public TerminalNode C_PAREN_L() { return getToken(Xml2AsParser.C_PAREN_L, 0); }
		public TerminalNode C_BRACKET_L() { return getToken(Xml2AsParser.C_BRACKET_L, 0); }
		public TerminalNode C_SLICE() { return getToken(Xml2AsParser.C_SLICE, 0); }
		public TerminalNode STRING() { return getToken(Xml2AsParser.STRING, 0); }
		public TerminalNode C_BRACKET_R() { return getToken(Xml2AsParser.C_BRACKET_R, 0); }
		public SliceMetaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_sliceMeta; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterSliceMeta(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitSliceMeta(this);
		}
	}

	public final SliceMetaContext sliceMeta() throws RecognitionException {
		SliceMetaContext _localctx = new SliceMetaContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_sliceMeta);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(117); match(C_BRACKET_L);
			setState(118); ((SliceMetaContext)_localctx).prefix = match(C_SLICE);
			setState(119); match(C_PAREN_L);
			setState(120); ((SliceMetaContext)_localctx).sliceChar = match(STRING);
			setState(121); match(C_PAREN_R);
			setState(122); match(C_BRACKET_R);
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
		enterRule(_localctx, 18, RULE_packName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(124); typeName();
			setState(129);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==1) {
				{
				{
				setState(125); match(1);
				setState(126); typeName();
				}
				}
				setState(131);
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
		public TerminalNode C_TYPE() { return getToken(Xml2AsParser.C_TYPE, 0); }
		public TerminalNode C_UINT() { return getToken(Xml2AsParser.C_UINT, 0); }
		public TerminalNode C_BOOL() { return getToken(Xml2AsParser.C_BOOL, 0); }
		public TerminalNode C_STRING() { return getToken(Xml2AsParser.C_STRING, 0); }
		public TerminalNode C_LIST() { return getToken(Xml2AsParser.C_LIST, 0); }
		public TerminalNode NAME() { return getToken(Xml2AsParser.NAME, 0); }
		public TerminalNode C_MAIN() { return getToken(Xml2AsParser.C_MAIN, 0); }
		public TerminalNode C_INPUT() { return getToken(Xml2AsParser.C_INPUT, 0); }
		public TerminalNode C_NUMBER() { return getToken(Xml2AsParser.C_NUMBER, 0); }
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
		enterRule(_localctx, 20, RULE_typeName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << C_INPUT) | (1L << C_MAIN) | (1L << C_LIST) | (1L << C_TYPE) | (1L << C_INT) | (1L << C_UINT) | (1L << C_BOOL) | (1L << C_NUMBER) | (1L << C_STRING) | (1L << NAME))) != 0)) ) {
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
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3\35\u0089\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2!\n\2\3\2\3\2\7\2%\n"+
		"\2\f\2\16\2(\13\2\3\3\7\3+\n\3\f\3\16\3.\13\3\3\3\3\3\3\3\3\3\3\4\7\4"+
		"\65\n\4\f\4\16\48\13\4\3\4\3\4\5\4<\n\4\3\4\5\4?\n\4\3\5\5\5B\n\5\3\5"+
		"\3\5\3\5\3\5\3\5\7\5I\n\5\f\5\16\5L\13\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\5\7]\n\7\3\b\3\b\7\ba\n\b\f\b\16\bd\13"+
		"\b\3\t\3\t\3\t\3\t\3\t\3\t\7\tl\n\t\f\t\16\to\13\t\5\tq\n\t\3\t\5\tt\n"+
		"\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\7\13\u0082\n\13"+
		"\f\13\16\13\u0085\13\13\3\f\3\f\3\f\2\r\2\4\6\b\n\f\16\20\22\24\26\2\3"+
		"\5\2\17\17\21\22\24\32\u0090\2 \3\2\2\2\4,\3\2\2\2\6\66\3\2\2\2\bA\3\2"+
		"\2\2\nO\3\2\2\2\fV\3\2\2\2\16b\3\2\2\2\20e\3\2\2\2\22w\3\2\2\2\24~\3\2"+
		"\2\2\26\u0086\3\2\2\2\30\31\5\4\3\2\31\32\5\6\4\2\32!\3\2\2\2\33\34\5"+
		"\6\4\2\34\35\5\4\3\2\35!\3\2\2\2\36!\5\6\4\2\37!\5\4\3\2 \30\3\2\2\2 "+
		"\33\3\2\2\2 \36\3\2\2\2 \37\3\2\2\2!&\3\2\2\2\"%\5\b\5\2#%\7\34\2\2$\""+
		"\3\2\2\2$#\3\2\2\2%(\3\2\2\2&$\3\2\2\2&\'\3\2\2\2\'\3\3\2\2\2(&\3\2\2"+
		"\2)+\7\34\2\2*)\3\2\2\2+.\3\2\2\2,*\3\2\2\2,-\3\2\2\2-/\3\2\2\2.,\3\2"+
		"\2\2/\60\7\17\2\2\60\61\7\33\2\2\61\62\7\16\2\2\62\5\3\2\2\2\63\65\7\34"+
		"\2\2\64\63\3\2\2\2\658\3\2\2\2\66\64\3\2\2\2\66\67\3\2\2\2\679\3\2\2\2"+
		"8\66\3\2\2\29;\7\20\2\2:<\5\24\13\2;:\3\2\2\2;<\3\2\2\2<>\3\2\2\2=?\7"+
		"\16\2\2>=\3\2\2\2>?\3\2\2\2?\7\3\2\2\2@B\5\n\6\2A@\3\2\2\2AB\3\2\2\2B"+
		"C\3\2\2\2CD\7\24\2\2DE\5\26\f\2EJ\7\b\2\2FI\5\f\7\2GI\7\34\2\2HF\3\2\2"+
		"\2HG\3\2\2\2IL\3\2\2\2JH\3\2\2\2JK\3\2\2\2KM\3\2\2\2LJ\3\2\2\2MN\7\t\2"+
		"\2N\t\3\2\2\2OP\7\4\2\2PQ\7\21\2\2QR\7\6\2\2RS\7\33\2\2ST\7\7\2\2TU\7"+
		"\5\2\2U\13\3\2\2\2VW\5\16\b\2WX\5\26\f\2XY\5\26\f\2YZ\7\f\2\2Z\\\7\33"+
		"\2\2[]\7\16\2\2\\[\3\2\2\2\\]\3\2\2\2]\r\3\2\2\2^a\5\20\t\2_a\5\22\n\2"+
		"`^\3\2\2\2`_\3\2\2\2ad\3\2\2\2b`\3\2\2\2bc\3\2\2\2c\17\3\2\2\2db\3\2\2"+
		"\2ef\7\4\2\2fs\7\22\2\2gp\7\6\2\2hm\5\26\f\2ij\7\r\2\2jl\5\26\f\2ki\3"+
		"\2\2\2lo\3\2\2\2mk\3\2\2\2mn\3\2\2\2nq\3\2\2\2om\3\2\2\2ph\3\2\2\2pq\3"+
		"\2\2\2qr\3\2\2\2rt\7\7\2\2sg\3\2\2\2st\3\2\2\2tu\3\2\2\2uv\7\5\2\2v\21"+
		"\3\2\2\2wx\7\4\2\2xy\7\23\2\2yz\7\6\2\2z{\7\33\2\2{|\7\7\2\2|}\7\5\2\2"+
		"}\23\3\2\2\2~\u0083\5\26\f\2\177\u0080\7\3\2\2\u0080\u0082\5\26\f\2\u0081"+
		"\177\3\2\2\2\u0082\u0085\3\2\2\2\u0083\u0081\3\2\2\2\u0083\u0084\3\2\2"+
		"\2\u0084\25\3\2\2\2\u0085\u0083\3\2\2\2\u0086\u0087\t\2\2\2\u0087\27\3"+
		"\2\2\2\23 $&,\66;>AHJ\\`bmps\u0083";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}