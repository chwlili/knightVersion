// Generated from Xml2As.g4 by ANTLR 4.1
package org.xml2as.parser;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link Xml2AsParser}.
 */
public interface Xml2AsListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link Xml2AsParser#typeName}.
	 * @param ctx the parse tree
	 */
	void enterTypeName(@NotNull Xml2AsParser.TypeNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Xml2AsParser#typeName}.
	 * @param ctx the parse tree
	 */
	void exitTypeName(@NotNull Xml2AsParser.TypeNameContext ctx);

	/**
	 * Enter a parse tree produced by {@link Xml2AsParser#metaParam}.
	 * @param ctx the parse tree
	 */
	void enterMetaParam(@NotNull Xml2AsParser.MetaParamContext ctx);
	/**
	 * Exit a parse tree produced by {@link Xml2AsParser#metaParam}.
	 * @param ctx the parse tree
	 */
	void exitMetaParam(@NotNull Xml2AsParser.MetaParamContext ctx);

	/**
	 * Enter a parse tree produced by {@link Xml2AsParser#typeField}.
	 * @param ctx the parse tree
	 */
	void enterTypeField(@NotNull Xml2AsParser.TypeFieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link Xml2AsParser#typeField}.
	 * @param ctx the parse tree
	 */
	void exitTypeField(@NotNull Xml2AsParser.TypeFieldContext ctx);

	/**
	 * Enter a parse tree produced by {@link Xml2AsParser#enumType}.
	 * @param ctx the parse tree
	 */
	void enterEnumType(@NotNull Xml2AsParser.EnumTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Xml2AsParser#enumType}.
	 * @param ctx the parse tree
	 */
	void exitEnumType(@NotNull Xml2AsParser.EnumTypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link Xml2AsParser#inputDef}.
	 * @param ctx the parse tree
	 */
	void enterInputDef(@NotNull Xml2AsParser.InputDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link Xml2AsParser#inputDef}.
	 * @param ctx the parse tree
	 */
	void exitInputDef(@NotNull Xml2AsParser.InputDefContext ctx);

	/**
	 * Enter a parse tree produced by {@link Xml2AsParser#paramValue}.
	 * @param ctx the parse tree
	 */
	void enterParamValue(@NotNull Xml2AsParser.ParamValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link Xml2AsParser#paramValue}.
	 * @param ctx the parse tree
	 */
	void exitParamValue(@NotNull Xml2AsParser.ParamValueContext ctx);

	/**
	 * Enter a parse tree produced by {@link Xml2AsParser#packName}.
	 * @param ctx the parse tree
	 */
	void enterPackName(@NotNull Xml2AsParser.PackNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Xml2AsParser#packName}.
	 * @param ctx the parse tree
	 */
	void exitPackName(@NotNull Xml2AsParser.PackNameContext ctx);

	/**
	 * Enter a parse tree produced by {@link Xml2AsParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(@NotNull Xml2AsParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Xml2AsParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(@NotNull Xml2AsParser.TypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link Xml2AsParser#packDef}.
	 * @param ctx the parse tree
	 */
	void enterPackDef(@NotNull Xml2AsParser.PackDefContext ctx);
	/**
	 * Exit a parse tree produced by {@link Xml2AsParser#packDef}.
	 * @param ctx the parse tree
	 */
	void exitPackDef(@NotNull Xml2AsParser.PackDefContext ctx);

	/**
	 * Enter a parse tree produced by {@link Xml2AsParser#enumField}.
	 * @param ctx the parse tree
	 */
	void enterEnumField(@NotNull Xml2AsParser.EnumFieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link Xml2AsParser#enumField}.
	 * @param ctx the parse tree
	 */
	void exitEnumField(@NotNull Xml2AsParser.EnumFieldContext ctx);

	/**
	 * Enter a parse tree produced by {@link Xml2AsParser#meta}.
	 * @param ctx the parse tree
	 */
	void enterMeta(@NotNull Xml2AsParser.MetaContext ctx);
	/**
	 * Exit a parse tree produced by {@link Xml2AsParser#meta}.
	 * @param ctx the parse tree
	 */
	void exitMeta(@NotNull Xml2AsParser.MetaContext ctx);

	/**
	 * Enter a parse tree produced by {@link Xml2AsParser#xml2}.
	 * @param ctx the parse tree
	 */
	void enterXml2(@NotNull Xml2AsParser.Xml2Context ctx);
	/**
	 * Exit a parse tree produced by {@link Xml2AsParser#xml2}.
	 * @param ctx the parse tree
	 */
	void exitXml2(@NotNull Xml2AsParser.Xml2Context ctx);
}