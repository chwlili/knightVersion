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
		C_INPUT=13, C_PACKAGE=14, C_MAIN=15, C_LIST=16, C_SLICE=17, C_DEFAULT=18, 
		C_TYPE=19, C_ENUM=20, C_INT=21, C_UINT=22, C_BOOL=23, C_NUMBER=24, C_STRING=25, 
		NAME=26, STRING=27, COMMENT=28, WS=29;
	public static final String[] tokenNames = {
		"<INVALID>", "'.'", "'['", "']'", "'('", "')'", "'{'", "'}'", "'<'", "'>'", 
		"'='", "','", "';'", "'input'", "'package'", "'Main'", "'List'", "'Slice'", 
		"'Default'", "'type'", "'enum'", "'int'", "'uint'", "'Boolean'", "'Number'", 
		"'String'", "NAME", "STRING", "COMMENT", "WS"
	};
	public static final int
		RULE_xml2 = 0, RULE_inputDef = 1, RULE_packDef = 2, RULE_type = 3, RULE_typeMeta = 4, 
		RULE_typeField = 5, RULE_fieldMeta = 6, RULE_listMeta = 7, RULE_sliceMeta = 8, 
		RULE_enumType = 9, RULE_enumField = 10, RULE_defaultMeta = 11, RULE_packName = 12, 
		RULE_typeName = 13;
	public static final String[] ruleNames = {
		"xml2", "inputDef", "packDef", "type", "typeMeta", "typeField", "fieldMeta", 
		"listMeta", "sliceMeta", "enumType", "enumField", "defaultMeta", "packName", 
		"typeName"
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
			setState(36);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(28); ((Xml2Context)_localctx).input = inputDef();
				setState(29); ((Xml2Context)_localctx).pack = packDef();
				}
				break;

			case 2:
				{
				setState(31); ((Xml2Context)_localctx).pack = packDef();
				setState(32); ((Xml2Context)_localctx).input = inputDef();
				}
				break;

			case 3:
				{
				setState(34); ((Xml2Context)_localctx).pack = packDef();
				}
				break;

			case 4:
				{
				setState(35); ((Xml2Context)_localctx).input = inputDef();
				}
				break;
			}
			setState(43);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << C_BRACKET_L) | (1L << C_TYPE) | (1L << C_ENUM) | (1L << COMMENT))) != 0)) {
				{
				setState(41);
				switch (_input.LA(1)) {
				case C_BRACKET_L:
				case C_TYPE:
					{
					setState(38); ((Xml2Context)_localctx).type = type();
					((Xml2Context)_localctx).types.add(((Xml2Context)_localctx).type);
					}
					break;
				case C_ENUM:
					{
					setState(39); ((Xml2Context)_localctx).enumType = enumType();
					((Xml2Context)_localctx).enums.add(((Xml2Context)_localctx).enumType);
					}
					break;
				case COMMENT:
					{
					setState(40); match(COMMENT);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(45);
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
			setState(49);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMENT) {
				{
				{
				setState(46); match(COMMENT);
				}
				}
				setState(51);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(52); match(C_INPUT);
			setState(53); ((InputDefContext)_localctx).url = match(STRING);
			setState(54); match(C_SEMICOLON);
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
			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMENT) {
				{
				{
				setState(56); match(COMMENT);
				}
				}
				setState(61);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(62); match(C_PACKAGE);
			setState(64);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(63); ((PackDefContext)_localctx).pack = packName();
				}
				break;
			}
			setState(67);
			_la = _input.LA(1);
			if (_la==C_SEMICOLON) {
				{
				setState(66); match(C_SEMICOLON);
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
			setState(70);
			_la = _input.LA(1);
			if (_la==C_BRACKET_L) {
				{
				setState(69); typeMeta();
				}
			}

			setState(72); match(C_TYPE);
			setState(73); typeName();
			setState(74); match(C_BRACE_L);
			setState(79);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << C_BRACKET_L) | (1L << C_INPUT) | (1L << C_MAIN) | (1L << C_LIST) | (1L << C_DEFAULT) | (1L << C_TYPE) | (1L << C_ENUM) | (1L << C_INT) | (1L << C_UINT) | (1L << C_BOOL) | (1L << C_NUMBER) | (1L << C_STRING) | (1L << NAME) | (1L << COMMENT))) != 0)) {
				{
				setState(77);
				switch (_input.LA(1)) {
				case C_BRACKET_L:
				case C_INPUT:
				case C_MAIN:
				case C_LIST:
				case C_DEFAULT:
				case C_TYPE:
				case C_ENUM:
				case C_INT:
				case C_UINT:
				case C_BOOL:
				case C_NUMBER:
				case C_STRING:
				case NAME:
					{
					setState(75); typeField();
					}
					break;
				case COMMENT:
					{
					setState(76); match(COMMENT);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(81);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(82); match(C_BRACE_R);
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
			setState(84); match(C_BRACKET_L);
			setState(85); match(C_MAIN);
			setState(86); match(C_PAREN_L);
			setState(87); ((TypeMetaContext)_localctx).xpath = match(STRING);
			setState(88); match(C_PAREN_R);
			setState(89); match(C_BRACKET_R);
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
		enterRule(_localctx, 10, RULE_typeField);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(91); fieldMeta();
			setState(92); ((TypeFieldContext)_localctx).fieldType = typeName();
			setState(93); ((TypeFieldContext)_localctx).fieldName = typeName();
			setState(94); match(C_EQUALS);
			setState(95); ((TypeFieldContext)_localctx).fieldXPath = match(STRING);
			setState(97);
			_la = _input.LA(1);
			if (_la==C_SEMICOLON) {
				{
				setState(96); match(C_SEMICOLON);
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
			setState(103);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==C_BRACKET_L) {
				{
				setState(101);
				switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
				case 1:
					{
					setState(99); listMeta();
					}
					break;

				case 2:
					{
					setState(100); sliceMeta();
					}
					break;
				}
				}
				setState(105);
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
			setState(106); match(C_BRACKET_L);
			setState(107); match(C_LIST);
			setState(120);
			_la = _input.LA(1);
			if (_la==C_PAREN_L) {
				{
				setState(108); match(C_PAREN_L);
				setState(117);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << C_INPUT) | (1L << C_MAIN) | (1L << C_LIST) | (1L << C_DEFAULT) | (1L << C_TYPE) | (1L << C_ENUM) | (1L << C_INT) | (1L << C_UINT) | (1L << C_BOOL) | (1L << C_NUMBER) | (1L << C_STRING) | (1L << NAME))) != 0)) {
					{
					setState(109); ((ListMetaContext)_localctx).typeName = typeName();
					((ListMetaContext)_localctx).key.add(((ListMetaContext)_localctx).typeName);
					setState(114);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==C_COMMA) {
						{
						{
						setState(110); match(C_COMMA);
						setState(111); ((ListMetaContext)_localctx).typeName = typeName();
						((ListMetaContext)_localctx).key.add(((ListMetaContext)_localctx).typeName);
						}
						}
						setState(116);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(119); match(C_PAREN_R);
				}
			}

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
			setState(124); match(C_BRACKET_L);
			setState(125); ((SliceMetaContext)_localctx).prefix = match(C_SLICE);
			setState(126); match(C_PAREN_L);
			setState(127); ((SliceMetaContext)_localctx).sliceChar = match(STRING);
			setState(128); match(C_PAREN_R);
			setState(129); match(C_BRACKET_R);
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
		public TerminalNode C_ENUM() { return getToken(Xml2AsParser.C_ENUM, 0); }
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public TerminalNode C_BRACE_R() { return getToken(Xml2AsParser.C_BRACE_R, 0); }
		public TerminalNode COMMENT(int i) {
			return getToken(Xml2AsParser.COMMENT, i);
		}
		public List<TerminalNode> COMMENT() { return getTokens(Xml2AsParser.COMMENT); }
		public List<EnumFieldContext> enumField() {
			return getRuleContexts(EnumFieldContext.class);
		}
		public EnumFieldContext enumField(int i) {
			return getRuleContext(EnumFieldContext.class,i);
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
		enterRule(_localctx, 18, RULE_enumType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(131); match(C_ENUM);
			setState(132); typeName();
			setState(133); match(C_BRACE_L);
			setState(138);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << C_BRACKET_L) | (1L << C_INPUT) | (1L << C_MAIN) | (1L << C_LIST) | (1L << C_DEFAULT) | (1L << C_TYPE) | (1L << C_ENUM) | (1L << C_INT) | (1L << C_UINT) | (1L << C_BOOL) | (1L << C_NUMBER) | (1L << C_STRING) | (1L << NAME) | (1L << COMMENT))) != 0)) {
				{
				setState(136);
				switch (_input.LA(1)) {
				case C_BRACKET_L:
				case C_INPUT:
				case C_MAIN:
				case C_LIST:
				case C_DEFAULT:
				case C_TYPE:
				case C_ENUM:
				case C_INT:
				case C_UINT:
				case C_BOOL:
				case C_NUMBER:
				case C_STRING:
				case NAME:
					{
					setState(134); enumField();
					}
					break;
				case COMMENT:
					{
					setState(135); match(COMMENT);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(140);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(141); match(C_BRACE_R);
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
		public DefaultMetaContext meta;
		public TypeNameContext fieldName;
		public Token fieldValue;
		public TerminalNode C_EQUALS() { return getToken(Xml2AsParser.C_EQUALS, 0); }
		public TerminalNode C_SEMICOLON() { return getToken(Xml2AsParser.C_SEMICOLON, 0); }
		public TypeNameContext typeName() {
			return getRuleContext(TypeNameContext.class,0);
		}
		public DefaultMetaContext defaultMeta() {
			return getRuleContext(DefaultMetaContext.class,0);
		}
		public TerminalNode STRING() { return getToken(Xml2AsParser.STRING, 0); }
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
		enterRule(_localctx, 20, RULE_enumField);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(144);
			_la = _input.LA(1);
			if (_la==C_BRACKET_L) {
				{
				setState(143); ((EnumFieldContext)_localctx).meta = defaultMeta();
				}
			}

			setState(146); ((EnumFieldContext)_localctx).fieldName = typeName();
			setState(147); match(C_EQUALS);
			setState(148); ((EnumFieldContext)_localctx).fieldValue = match(STRING);
			setState(150);
			_la = _input.LA(1);
			if (_la==C_SEMICOLON) {
				{
				setState(149); match(C_SEMICOLON);
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

	public static class DefaultMetaContext extends ParserRuleContext {
		public Token prefix;
		public TerminalNode C_PAREN_R() { return getToken(Xml2AsParser.C_PAREN_R, 0); }
		public TerminalNode C_PAREN_L() { return getToken(Xml2AsParser.C_PAREN_L, 0); }
		public TerminalNode C_BRACKET_L() { return getToken(Xml2AsParser.C_BRACKET_L, 0); }
		public TerminalNode C_DEFAULT() { return getToken(Xml2AsParser.C_DEFAULT, 0); }
		public TerminalNode C_BRACKET_R() { return getToken(Xml2AsParser.C_BRACKET_R, 0); }
		public DefaultMetaContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defaultMeta; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).enterDefaultMeta(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Xml2AsListener ) ((Xml2AsListener)listener).exitDefaultMeta(this);
		}
	}

	public final DefaultMetaContext defaultMeta() throws RecognitionException {
		DefaultMetaContext _localctx = new DefaultMetaContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_defaultMeta);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(152); match(C_BRACKET_L);
			setState(153); ((DefaultMetaContext)_localctx).prefix = match(C_DEFAULT);
			setState(156);
			_la = _input.LA(1);
			if (_la==C_PAREN_L) {
				{
				setState(154); match(C_PAREN_L);
				setState(155); match(C_PAREN_R);
				}
			}

			setState(158); match(C_BRACKET_R);
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
		enterRule(_localctx, 24, RULE_packName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(160); typeName();
			setState(165);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==1) {
				{
				{
				setState(161); match(1);
				setState(162); typeName();
				}
				}
				setState(167);
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
		public TerminalNode C_LIST() { return getToken(Xml2AsParser.C_LIST, 0); }
		public TerminalNode C_DEFAULT() { return getToken(Xml2AsParser.C_DEFAULT, 0); }
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
		enterRule(_localctx, 26, RULE_typeName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(168);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << C_INPUT) | (1L << C_MAIN) | (1L << C_LIST) | (1L << C_DEFAULT) | (1L << C_TYPE) | (1L << C_ENUM) | (1L << C_INT) | (1L << C_UINT) | (1L << C_BOOL) | (1L << C_NUMBER) | (1L << C_STRING) | (1L << NAME))) != 0)) ) {
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
		"\3\uacf5\uee8c\u4f5d\u8b0d\u4a45\u78bd\u1b2f\u3378\3\37\u00ad\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\5\2\'\n\2\3\2\3\2\3\2\7\2,\n\2\f\2\16\2/\13\2\3\3\7\3\62\n\3\f\3"+
		"\16\3\65\13\3\3\3\3\3\3\3\3\3\3\4\7\4<\n\4\f\4\16\4?\13\4\3\4\3\4\5\4"+
		"C\n\4\3\4\5\4F\n\4\3\5\5\5I\n\5\3\5\3\5\3\5\3\5\3\5\7\5P\n\5\f\5\16\5"+
		"S\13\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\5\7"+
		"d\n\7\3\b\3\b\7\bh\n\b\f\b\16\bk\13\b\3\t\3\t\3\t\3\t\3\t\3\t\7\ts\n\t"+
		"\f\t\16\tv\13\t\5\tx\n\t\3\t\5\t{\n\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\13\3\13\3\13\3\13\3\13\7\13\u008b\n\13\f\13\16\13\u008e\13\13\3"+
		"\13\3\13\3\f\5\f\u0093\n\f\3\f\3\f\3\f\3\f\5\f\u0099\n\f\3\r\3\r\3\r\3"+
		"\r\5\r\u009f\n\r\3\r\3\r\3\16\3\16\3\16\7\16\u00a6\n\16\f\16\16\16\u00a9"+
		"\13\16\3\17\3\17\3\17\2\20\2\4\6\b\n\f\16\20\22\24\26\30\32\34\2\3\5\2"+
		"\17\17\21\22\24\34\u00b7\2&\3\2\2\2\4\63\3\2\2\2\6=\3\2\2\2\bH\3\2\2\2"+
		"\nV\3\2\2\2\f]\3\2\2\2\16i\3\2\2\2\20l\3\2\2\2\22~\3\2\2\2\24\u0085\3"+
		"\2\2\2\26\u0092\3\2\2\2\30\u009a\3\2\2\2\32\u00a2\3\2\2\2\34\u00aa\3\2"+
		"\2\2\36\37\5\4\3\2\37 \5\6\4\2 \'\3\2\2\2!\"\5\6\4\2\"#\5\4\3\2#\'\3\2"+
		"\2\2$\'\5\6\4\2%\'\5\4\3\2&\36\3\2\2\2&!\3\2\2\2&$\3\2\2\2&%\3\2\2\2\'"+
		"-\3\2\2\2(,\5\b\5\2),\5\24\13\2*,\7\36\2\2+(\3\2\2\2+)\3\2\2\2+*\3\2\2"+
		"\2,/\3\2\2\2-+\3\2\2\2-.\3\2\2\2.\3\3\2\2\2/-\3\2\2\2\60\62\7\36\2\2\61"+
		"\60\3\2\2\2\62\65\3\2\2\2\63\61\3\2\2\2\63\64\3\2\2\2\64\66\3\2\2\2\65"+
		"\63\3\2\2\2\66\67\7\17\2\2\678\7\35\2\289\7\16\2\29\5\3\2\2\2:<\7\36\2"+
		"\2;:\3\2\2\2<?\3\2\2\2=;\3\2\2\2=>\3\2\2\2>@\3\2\2\2?=\3\2\2\2@B\7\20"+
		"\2\2AC\5\32\16\2BA\3\2\2\2BC\3\2\2\2CE\3\2\2\2DF\7\16\2\2ED\3\2\2\2EF"+
		"\3\2\2\2F\7\3\2\2\2GI\5\n\6\2HG\3\2\2\2HI\3\2\2\2IJ\3\2\2\2JK\7\25\2\2"+
		"KL\5\34\17\2LQ\7\b\2\2MP\5\f\7\2NP\7\36\2\2OM\3\2\2\2ON\3\2\2\2PS\3\2"+
		"\2\2QO\3\2\2\2QR\3\2\2\2RT\3\2\2\2SQ\3\2\2\2TU\7\t\2\2U\t\3\2\2\2VW\7"+
		"\4\2\2WX\7\21\2\2XY\7\6\2\2YZ\7\35\2\2Z[\7\7\2\2[\\\7\5\2\2\\\13\3\2\2"+
		"\2]^\5\16\b\2^_\5\34\17\2_`\5\34\17\2`a\7\f\2\2ac\7\35\2\2bd\7\16\2\2"+
		"cb\3\2\2\2cd\3\2\2\2d\r\3\2\2\2eh\5\20\t\2fh\5\22\n\2ge\3\2\2\2gf\3\2"+
		"\2\2hk\3\2\2\2ig\3\2\2\2ij\3\2\2\2j\17\3\2\2\2ki\3\2\2\2lm\7\4\2\2mz\7"+
		"\22\2\2nw\7\6\2\2ot\5\34\17\2pq\7\r\2\2qs\5\34\17\2rp\3\2\2\2sv\3\2\2"+
		"\2tr\3\2\2\2tu\3\2\2\2ux\3\2\2\2vt\3\2\2\2wo\3\2\2\2wx\3\2\2\2xy\3\2\2"+
		"\2y{\7\7\2\2zn\3\2\2\2z{\3\2\2\2{|\3\2\2\2|}\7\5\2\2}\21\3\2\2\2~\177"+
		"\7\4\2\2\177\u0080\7\23\2\2\u0080\u0081\7\6\2\2\u0081\u0082\7\35\2\2\u0082"+
		"\u0083\7\7\2\2\u0083\u0084\7\5\2\2\u0084\23\3\2\2\2\u0085\u0086\7\26\2"+
		"\2\u0086\u0087\5\34\17\2\u0087\u008c\7\b\2\2\u0088\u008b\5\26\f\2\u0089"+
		"\u008b\7\36\2\2\u008a\u0088\3\2\2\2\u008a\u0089\3\2\2\2\u008b\u008e\3"+
		"\2\2\2\u008c\u008a\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u008f\3\2\2\2\u008e"+
		"\u008c\3\2\2\2\u008f\u0090\7\t\2\2\u0090\25\3\2\2\2\u0091\u0093\5\30\r"+
		"\2\u0092\u0091\3\2\2\2\u0092\u0093\3\2\2\2\u0093\u0094\3\2\2\2\u0094\u0095"+
		"\5\34\17\2\u0095\u0096\7\f\2\2\u0096\u0098\7\35\2\2\u0097\u0099\7\16\2"+
		"\2\u0098\u0097\3\2\2\2\u0098\u0099\3\2\2\2\u0099\27\3\2\2\2\u009a\u009b"+
		"\7\4\2\2\u009b\u009e\7\24\2\2\u009c\u009d\7\6\2\2\u009d\u009f\7\7\2\2"+
		"\u009e\u009c\3\2\2\2\u009e\u009f\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a1"+
		"\7\5\2\2\u00a1\31\3\2\2\2\u00a2\u00a7\5\34\17\2\u00a3\u00a4\7\3\2\2\u00a4"+
		"\u00a6\5\34\17\2\u00a5\u00a3\3\2\2\2\u00a6\u00a9\3\2\2\2\u00a7\u00a5\3"+
		"\2\2\2\u00a7\u00a8\3\2\2\2\u00a8\33\3\2\2\2\u00a9\u00a7\3\2\2\2\u00aa"+
		"\u00ab\t\2\2\2\u00ab\35\3\2\2\2\30&+-\63=BEHOQcgitwz\u008a\u008c\u0092"+
		"\u0098\u009e\u00a7";
	public static final ATN _ATN =
		ATNSimulator.deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}