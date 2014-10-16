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
		C_INPUT=13, C_PACKAGE=14, C_TYPE=15, C_ENUM=16, C_INT=17, C_UINT=18, C_BOOL=19, 
		C_NUMBER=20, C_STRING=21, NAME=22, STRING=23, COMMENT=24, WS=25;
	public static final String[] tokenNames = {
		"<INVALID>", "'.'", "'['", "']'", "'('", "')'", "'{'", "'}'", "'<'", "'>'", 
		"'='", "','", "';'", "'input'", "'package'", "'type'", "'enum'", "'int'", 
		"'uint'", "'Boolean'", "'Number'", "'String'", "NAME", "STRING", "COMMENT", 
		"WS"
	};
	public static final int
		RULE_xml2 = 0, RULE_inputDef = 1, RULE_packDef = 2, RULE_type = 3, RULE_typeField = 4, 
		RULE_enumType = 5, RULE_enumField = 6, RULE_meta = 7, RULE_metaParam = 8, 
		RULE_packName = 9, RULE_typeName = 10, RULE_paramValue = 11;
	public static final String[] ruleNames = {
		"xml2", "inputDef", "packDef", "type", "typeField", "enumType", "enumField", 
		"meta", "metaParam", "packName", "typeName", "paramValue"
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
		public EnumTypeContext enumType;
		public List<EnumTypeContext> enums = new ArrayList<EnumTypeContext>();
		public InputDefContext inputDef() {
			return getRuleContext(InputDefContext.class,0);
		}
		public List<EnumTypeContext> enumType() {
			return getRuleContexts(EnumTypeContext.class);
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
		public EnumTypeContext enumType(int i) {
			return getRuleContext(EnumTypeContext.class,i);
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
			setState(32);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(24); ((Xml2Context)_localctx).input = inputDef();
				setState(25); ((Xml2Context)_localctx).pack = packDef();
				}
				break;

			case 2:
				{
				setState(27); ((Xml2Context)_localctx).pack = packDef();
				setState(28); ((Xml2Context)_localctx).input = inputDef();
				}
				break;

			case 3:
				{
				setState(30); ((Xml2Context)_localctx).pack = packDef();
				}
				break;

			case 4:
				{
				setState(31); ((Xml2Context)_localctx).input = inputDef();
				}
				break;
			}
			setState(39);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << C_BRACKET_L) | (1L << C_TYPE) | (1L << C_ENUM) | (1L << COMMENT))) != 0)) {
				{
				setState(37);
				switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
				case 1:
					{
					setState(34); ((Xml2Context)_localctx).type = type();
					((Xml2Context)_localctx).types.add(((Xml2Context)_localctx).type);
					}
					break;

				case 2:
					{
					setState(35); ((Xml2Context)_localctx).enumType = enumType();
					((Xml2Context)_localctx).enums.add(((Xml2Context)_localctx).enumType);
					}
					break;

				case 3:
					{
					setState(36); match(COMMENT);
					}
					break;
				}
				}
				setState(41);
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
			setState(45);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMENT) {
				{
				{
				setState(42); match(COMMENT);
				}
				}
				setState(47);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(48); match(C_INPUT);
			setState(49); ((InputDefContext)_localctx).url = match(STRING);
			setState(50); match(C_SEMICOLON);
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
			setState(55);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMENT) {
				{
				{
				setState(52); match(COMMENT);
				}
				}
				setState(57);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(58); match(C_PACKAGE);
			setState(60);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(59); ((PackDefContext)_localctx).pack = packName();
				}
				break;
			}
			setState(63);
			_la = _input.LA(1);
			if (_la==C_SEMICOLON) {
				{
				setState(62); match(C_SEMICOLON);
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
		public MetaContext metas;
		public TerminalNode C_TYPE() { return getToken(Xml2AsParser.C_TYPE, 0); }
		public List<TypeFieldContext> typeField() {
			return getRuleContexts(TypeFieldContext.class);
		}
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TerminalNode C_BRACE_R() { return getToken(Xml2AsParser.C_BRACE_R, 0); }
		public TerminalNode COMMENT(int i) {
			return getToken(Xml2AsParser.COMMENT, i);
		}
		public TypeFieldContext typeField(int i) {
			return getRuleContext(TypeFieldContext.class,i);
		}
		public List<MetaContext> meta() {
			return getRuleContexts(MetaContext.class);
		}
		public List<TerminalNode> COMMENT() { return getTokens(Xml2AsParser.COMMENT); }
		public MetaContext meta(int i) {
			return getRuleContext(MetaContext.class,i);
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
			setState(68);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==C_BRACKET_L) {
				{
				{
				setState(65); ((TypeContext)_localctx).metas = meta();
				}
				}
				setState(70);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(71); match(C_TYPE);
			setState(72); typeName();
			setState(73); match(C_BRACE_L);
			setState(78);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << C_BRACKET_L) | (1L << C_INPUT) | (1L << C_TYPE) | (1L << C_ENUM) | (1L << C_INT) | (1L << C_UINT) | (1L << C_BOOL) | (1L << C_NUMBER) | (1L << C_STRING) | (1L << NAME) | (1L << COMMENT))) != 0)) {
				{
				setState(76);
				switch (_input.LA(1)) {
				case C_BRACKET_L:
				case C_INPUT:
				case C_TYPE:
				case C_ENUM:
				case C_INT:
				case C_UINT:
				case C_BOOL:
				case C_NUMBER:
				case C_STRING:
				case NAME:
					{
					setState(74); typeField();
					}
					break;
				case COMMENT:
					{
					setState(75); match(COMMENT);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(80);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(81); match(C_BRACE_R);
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

	public static class TypeFieldContext extends ParserRuleContext {
		public MetaContext metas;
		public TypeNameContext fieldType;
		public TypeNameContext fieldName;
		public Token fieldXPath;
		public TerminalNode C_EQUALS() { return getToken(Xml2AsParser.C_EQUALS, 0); }
		public TerminalNode C_SEMICOLON() { return getToken(Xml2AsParser.C_SEMICOLON, 0); }
		public List<TypeNameContext> typeName() {
			return getRuleContexts(TypeNameContext.class);
		}
		public TypeNameContext typeName(int i) {
			return getRuleContext(TypeNameContext.class,i);
		}
		public List<MetaContext> meta() {
			return getRuleContexts(MetaContext.class);
		}
		public TerminalNode STRING() { return getToken(Xml2AsParser.STRING, 0); }
		public MetaContext meta(int i) {
			return getRuleContext(MetaContext.class,i);
		}
		public TypeFieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeField; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterTypeField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitTypeField(this);
		}
	}

	public final TypeFieldContext typeField() throws RecognitionException {
		TypeFieldContext _localctx = new TypeFieldContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_typeField);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(86);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==C_BRACKET_L) {
				{
				{
				setState(83); ((TypeFieldContext)_localctx).metas = meta();
				}
				}
				setState(88);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(89); ((TypeFieldContext)_localctx).fieldType = typeName();
			setState(90); ((TypeFieldContext)_localctx).fieldName = typeName();
			setState(91); match(C_EQUALS);
			setState(92); ((TypeFieldContext)_localctx).fieldXPath = match(STRING);
			setState(94);
			_la = _input.LA(1);
			if (_la==C_SEMICOLON) {
				{
				setState(93); match(C_SEMICOLON);
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

	public static class EnumTypeContext extends ParserRuleContext {
		public MetaContext metas;
		public TerminalNode C_ENUM() { return getToken(Xml2AsParser.C_ENUM, 0); }
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TerminalNode C_BRACE_R() { return getToken(Xml2AsParser.C_BRACE_R, 0); }
		public TerminalNode COMMENT(int i) {
			return getToken(Xml2AsParser.COMMENT, i);
		}
		public List<MetaContext> meta() {
			return getRuleContexts(MetaContext.class);
		}
		public List<TerminalNode> COMMENT() { return getTokens(Xml2AsParser.COMMENT); }
		public List<EnumFieldContext> enumField() {
			return getRuleContexts(EnumFieldContext.class);
		}
		public EnumFieldContext enumField(int i) {
			return getRuleContext(EnumFieldContext.class,i);
		}
		public MetaContext meta(int i) {
			return getRuleContext(MetaContext.class,i);
		}
		public TerminalNode C_BRACE_L() { return getToken(Xml2AsParser.C_BRACE_L, 0); }
		public EnumTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterEnumType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitEnumType(this);
		}
	}

	public final EnumTypeContext enumType() throws RecognitionException {
		EnumTypeContext _localctx = new EnumTypeContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_enumType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==C_BRACKET_L) {
				{
				{
				setState(96); ((EnumTypeContext)_localctx).metas = meta();
				}
				}
				setState(101);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(102); match(C_ENUM);
			setState(103); typeName();
			setState(104); match(C_BRACE_L);
			setState(109);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << C_BRACKET_L) | (1L << C_INPUT) | (1L << C_TYPE) | (1L << C_ENUM) | (1L << C_INT) | (1L << C_UINT) | (1L << C_BOOL) | (1L << C_NUMBER) | (1L << C_STRING) | (1L << NAME) | (1L << COMMENT))) != 0)) {
				{
				setState(107);
				switch (_input.LA(1)) {
				case C_BRACKET_L:
				case C_INPUT:
				case C_TYPE:
				case C_ENUM:
				case C_INT:
				case C_UINT:
				case C_BOOL:
				case C_NUMBER:
				case C_STRING:
				case NAME:
					{
					setState(105); enumField();
					}
					break;
				case COMMENT:
					{
					setState(106); match(COMMENT);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(111);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(112); match(C_BRACE_R);
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

	public static class EnumFieldContext extends ParserRuleContext {
		public MetaContext metas;
		public TypeNameContext fieldName;
		public Token fieldValue;
		public TerminalNode C_EQUALS() { return getToken(Xml2AsParser.C_EQUALS, 0); }
		public TerminalNode C_SEMICOLON() { return getToken(Xml2AsParser.C_SEMICOLON, 0); }
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public List<MetaContext> meta() {
			return getRuleContexts(MetaContext.class);
		}
		public TerminalNode STRING() { return getToken(Xml2AsParser.STRING, 0); }
		public MetaContext meta(int i) {
			return getRuleContext(MetaContext.class,i);
		}
		public EnumFieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enumField; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterEnumField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitEnumField(this);
		}
	}

	public final EnumFieldContext enumField() throws RecognitionException {
		EnumFieldContext _localctx = new EnumFieldContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_enumField);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(117);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==C_BRACKET_L) {
				{
				{
				setState(114); ((EnumFieldContext)_localctx).metas = meta();
				}
				}
				setState(119);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(120); ((EnumFieldContext)_localctx).fieldName = typeName();
			setState(121); match(C_EQUALS);
			setState(122); ((EnumFieldContext)_localctx).fieldValue = match(STRING);
			setState(124);
			_la = _input.LA(1);
			if (_la==C_SEMICOLON) {
				{
				setState(123); match(C_SEMICOLON);
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

	public static class MetaContext extends ParserRuleContext {
		public TypeNameContext prefix;
		public MetaParamContext metaParam;
		public List<MetaParamContext> params = new ArrayList<MetaParamContext>();
		public TerminalNode C_COMMA(int i) {
			return getToken(Xml2AsParser.C_COMMA, i);
		}
		public List<TerminalNode> C_COMMA() { return getTokens(Xml2AsParser.C_COMMA); }
		public TerminalNode C_PAREN_R() { return getToken(Xml2AsParser.C_PAREN_R, 0); }
		public TerminalNode C_PAREN_L() { return getToken(Xml2AsParser.C_PAREN_L, 0); }
		public MetaParamContext metaParam(int i) {
			return getRuleContext(MetaParamContext.class,i);
		}
		public List<MetaParamContext> metaParam() {
			return getRuleContexts(MetaParamContext.class);
		}
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TerminalNode C_BRACKET_L() { return getToken(Xml2AsParser.C_BRACKET_L, 0); }
		public TerminalNode C_BRACKET_R() { return getToken(Xml2AsParser.C_BRACKET_R, 0); }
		public MetaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_meta; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterMeta(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitMeta(this);
		}
	}

	public final MetaContext meta() throws RecognitionException {
		MetaContext _localctx = new MetaContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_meta);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(126); match(C_BRACKET_L);
			setState(127); ((MetaContext)_localctx).prefix = typeName();
			setState(140);
			_la = _input.LA(1);
			if (_la==C_PAREN_L) {
				{
				setState(128); match(C_PAREN_L);
				setState(137);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << C_INPUT) | (1L << C_TYPE) | (1L << C_ENUM) | (1L << C_INT) | (1L << C_UINT) | (1L << C_BOOL) | (1L << C_NUMBER) | (1L << C_STRING) | (1L << NAME) | (1L << STRING))) != 0)) {
					{
					setState(129); ((MetaContext)_localctx).metaParam = metaParam();
					((MetaContext)_localctx).params.add(((MetaContext)_localctx).metaParam);
					setState(134);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==C_COMMA) {
						{
						{
						setState(130); match(C_COMMA);
						setState(131); ((MetaContext)_localctx).metaParam = metaParam();
						((MetaContext)_localctx).params.add(((MetaContext)_localctx).metaParam);
						}
						}
						setState(136);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(139); match(C_PAREN_R);
				}
			}

			setState(142); match(C_BRACKET_R);
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

	public static class MetaParamContext extends ParserRuleContext {
		public ParamValueContext value;
		public ParamValueContext paramValue() {
			return getRuleContext(ParamValueContext.class,0);
		}
		public MetaParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_metaParam; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterMetaParam(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitMetaParam(this);
		}
	}

	public final MetaParamContext metaParam() throws RecognitionException {
		MetaParamContext _localctx = new MetaParamContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_metaParam);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(144); ((MetaParamContext)_localctx).value = paramValue();
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
			setState(146); typeName();
			setState(151);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==1) {
				{
				{
				setState(147); match(1);
				setState(148); typeName();
				}
				}
				setState(153);
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
		public TerminalNode C_ENUM() { return getToken(Xml2AsParser.C_ENUM, 0); }
		public TerminalNode C_TYPE() { return getToken(Xml2AsParser.C_TYPE, 0); }
		public TerminalNode C_UINT() { return getToken(Xml2AsParser.C_UINT, 0); }
		public TerminalNode C_BOOL() { return getToken(Xml2AsParser.C_BOOL, 0); }
		public TerminalNode C_STRING() { return getToken(Xml2AsParser.C_STRING, 0); }
		public TerminalNode NAME() { return getToken(Xml2AsParser.NAME, 0); }
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
			setState(154);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << C_INPUT) | (1L << C_TYPE) | (1L << C_ENUM) | (1L << C_INT) | (1L << C_UINT) | (1L << C_BOOL) | (1L << C_NUMBER) | (1L << C_STRING) | (1L << NAME))) != 0)) ) {
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

	public static class ParamValueContext extends ParserRuleContext {
		public TerminalNode C_ENUM() { return getToken(Xml2AsParser.C_ENUM, 0); }
		public TerminalNode C_TYPE() { return getToken(Xml2AsParser.C_TYPE, 0); }
		public TerminalNode C_UINT() { return getToken(Xml2AsParser.C_UINT, 0); }
		public TerminalNode C_BOOL() { return getToken(Xml2AsParser.C_BOOL, 0); }
		public TerminalNode C_STRING() { return getToken(Xml2AsParser.C_STRING, 0); }
		public TerminalNode NAME() { return getToken(Xml2AsParser.NAME, 0); }
		public TerminalNode C_INPUT() { return getToken(Xml2AsParser.C_INPUT, 0); }
		public TerminalNode C_NUMBER() { return getToken(Xml2AsParser.C_NUMBER, 0); }
		public TerminalNode STRING() { return getToken(Xml2AsParser.STRING, 0); }
		public TerminalNode C_INT() { return getToken(Xml2AsParser.C_INT, 0); }
		public ParamValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_paramValue; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterParamValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitParamValue(this);
		}
	}

	public final ParamValueContext paramValue() throws RecognitionException {
		ParamValueContext _localctx = new ParamValueContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_paramValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(156);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << C_INPUT) | (1L << C_TYPE) | (1L << C_ENUM) | (1L << C_INT) | (1L << C_UINT) | (1L << C_BOOL) | (1L << C_NUMBER) | (1L << C_STRING) | (1L << NAME) | (1L << STRING))) != 0)) ) {
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
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3\33\u00a1\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2#\n\2\3\2\3\2"+
		"\3\2\7\2(\n\2\f\2\16\2+\13\2\3\3\7\3.\n\3\f\3\16\3\61\13\3\3\3\3\3\3\3"+
		"\3\3\3\4\7\48\n\4\f\4\16\4;\13\4\3\4\3\4\5\4?\n\4\3\4\5\4B\n\4\3\5\7\5"+
		"E\n\5\f\5\16\5H\13\5\3\5\3\5\3\5\3\5\3\5\7\5O\n\5\f\5\16\5R\13\5\3\5\3"+
		"\5\3\6\7\6W\n\6\f\6\16\6Z\13\6\3\6\3\6\3\6\3\6\3\6\5\6a\n\6\3\7\7\7d\n"+
		"\7\f\7\16\7g\13\7\3\7\3\7\3\7\3\7\3\7\7\7n\n\7\f\7\16\7q\13\7\3\7\3\7"+
		"\3\b\7\bv\n\b\f\b\16\by\13\b\3\b\3\b\3\b\3\b\5\b\177\n\b\3\t\3\t\3\t\3"+
		"\t\3\t\3\t\7\t\u0087\n\t\f\t\16\t\u008a\13\t\5\t\u008c\n\t\3\t\5\t\u008f"+
		"\n\t\3\t\3\t\3\n\3\n\3\13\3\13\3\13\7\13\u0098\n\13\f\13\16\13\u009b\13"+
		"\13\3\f\3\f\3\r\3\r\3\r\2\16\2\4\6\b\n\f\16\20\22\24\26\30\2\4\4\2\17"+
		"\17\21\30\4\2\17\17\21\31\u00ac\2\"\3\2\2\2\4/\3\2\2\2\69\3\2\2\2\bF\3"+
		"\2\2\2\nX\3\2\2\2\fe\3\2\2\2\16w\3\2\2\2\20\u0080\3\2\2\2\22\u0092\3\2"+
		"\2\2\24\u0094\3\2\2\2\26\u009c\3\2\2\2\30\u009e\3\2\2\2\32\33\5\4\3\2"+
		"\33\34\5\6\4\2\34#\3\2\2\2\35\36\5\6\4\2\36\37\5\4\3\2\37#\3\2\2\2 #\5"+
		"\6\4\2!#\5\4\3\2\"\32\3\2\2\2\"\35\3\2\2\2\" \3\2\2\2\"!\3\2\2\2#)\3\2"+
		"\2\2$(\5\b\5\2%(\5\f\7\2&(\7\32\2\2\'$\3\2\2\2\'%\3\2\2\2\'&\3\2\2\2("+
		"+\3\2\2\2)\'\3\2\2\2)*\3\2\2\2*\3\3\2\2\2+)\3\2\2\2,.\7\32\2\2-,\3\2\2"+
		"\2.\61\3\2\2\2/-\3\2\2\2/\60\3\2\2\2\60\62\3\2\2\2\61/\3\2\2\2\62\63\7"+
		"\17\2\2\63\64\7\31\2\2\64\65\7\16\2\2\65\5\3\2\2\2\668\7\32\2\2\67\66"+
		"\3\2\2\28;\3\2\2\29\67\3\2\2\29:\3\2\2\2:<\3\2\2\2;9\3\2\2\2<>\7\20\2"+
		"\2=?\5\24\13\2>=\3\2\2\2>?\3\2\2\2?A\3\2\2\2@B\7\16\2\2A@\3\2\2\2AB\3"+
		"\2\2\2B\7\3\2\2\2CE\5\20\t\2DC\3\2\2\2EH\3\2\2\2FD\3\2\2\2FG\3\2\2\2G"+
		"I\3\2\2\2HF\3\2\2\2IJ\7\21\2\2JK\5\26\f\2KP\7\b\2\2LO\5\n\6\2MO\7\32\2"+
		"\2NL\3\2\2\2NM\3\2\2\2OR\3\2\2\2PN\3\2\2\2PQ\3\2\2\2QS\3\2\2\2RP\3\2\2"+
		"\2ST\7\t\2\2T\t\3\2\2\2UW\5\20\t\2VU\3\2\2\2WZ\3\2\2\2XV\3\2\2\2XY\3\2"+
		"\2\2Y[\3\2\2\2ZX\3\2\2\2[\\\5\26\f\2\\]\5\26\f\2]^\7\f\2\2^`\7\31\2\2"+
		"_a\7\16\2\2`_\3\2\2\2`a\3\2\2\2a\13\3\2\2\2bd\5\20\t\2cb\3\2\2\2dg\3\2"+
		"\2\2ec\3\2\2\2ef\3\2\2\2fh\3\2\2\2ge\3\2\2\2hi\7\22\2\2ij\5\26\f\2jo\7"+
		"\b\2\2kn\5\16\b\2ln\7\32\2\2mk\3\2\2\2ml\3\2\2\2nq\3\2\2\2om\3\2\2\2o"+
		"p\3\2\2\2pr\3\2\2\2qo\3\2\2\2rs\7\t\2\2s\r\3\2\2\2tv\5\20\t\2ut\3\2\2"+
		"\2vy\3\2\2\2wu\3\2\2\2wx\3\2\2\2xz\3\2\2\2yw\3\2\2\2z{\5\26\f\2{|\7\f"+
		"\2\2|~\7\31\2\2}\177\7\16\2\2~}\3\2\2\2~\177\3\2\2\2\177\17\3\2\2\2\u0080"+
		"\u0081\7\4\2\2\u0081\u008e\5\26\f\2\u0082\u008b\7\6\2\2\u0083\u0088\5"+
		"\22\n\2\u0084\u0085\7\r\2\2\u0085\u0087\5\22\n\2\u0086\u0084\3\2\2\2\u0087"+
		"\u008a\3\2\2\2\u0088\u0086\3\2\2\2\u0088\u0089\3\2\2\2\u0089\u008c\3\2"+
		"\2\2\u008a\u0088\3\2\2\2\u008b\u0083\3\2\2\2\u008b\u008c\3\2\2\2\u008c"+
		"\u008d\3\2\2\2\u008d\u008f\7\7\2\2\u008e\u0082\3\2\2\2\u008e\u008f\3\2"+
		"\2\2\u008f\u0090\3\2\2\2\u0090\u0091\7\5\2\2\u0091\21\3\2\2\2\u0092\u0093"+
		"\5\30\r\2\u0093\23\3\2\2\2\u0094\u0099\5\26\f\2\u0095\u0096\7\3\2\2\u0096"+
		"\u0098\5\26\f\2\u0097\u0095\3\2\2\2\u0098\u009b\3\2\2\2\u0099\u0097\3"+
		"\2\2\2\u0099\u009a\3\2\2\2\u009a\25\3\2\2\2\u009b\u0099\3\2\2\2\u009c"+
		"\u009d\t\2\2\2\u009d\27\3\2\2\2\u009e\u009f\t\3\2\2\u009f\31\3\2\2\2\27"+
		"\"\')/9>AFNPX`emow~\u0088\u008b\u008e\u0099";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}