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
	 * Enter a parse tree produced by {@link Xml2AsParser#field}.
	 * @param ctx the parse tree
	 */
	void enterField(@NotNull Xml2AsParser.FieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link Xml2AsParser#field}.
	 * @param ctx the parse tree
	 */
	void exitField(@NotNull Xml2AsParser.FieldContext ctx);

	/**
	 * Enter a parse tree produced by {@link Xml2AsParser#input}.
	 * @param ctx the parse tree
	 */
	void enterInput(@NotNull Xml2AsParser.InputContext ctx);
	/**
	 * Exit a parse tree produced by {@link Xml2AsParser#input}.
	 * @param ctx the parse tree
	 */
	void exitInput(@NotNull Xml2AsParser.InputContext ctx);

	/**
	 * Enter a parse tree produced by {@link Xml2AsParser#hashType}.
	 * @param ctx the parse tree
	 */
	void enterHashType(@NotNull Xml2AsParser.HashTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Xml2AsParser#hashType}.
	 * @param ctx the parse tree
	 */
	void exitHashType(@NotNull Xml2AsParser.HashTypeContext ctx);

	/**
	 * Enter a parse tree produced by {@link Xml2AsParser#listType}.
	 * @param ctx the parse tree
	 */
	void enterListType(@NotNull Xml2AsParser.ListTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Xml2AsParser#listType}.
	 * @param ctx the parse tree
	 */
	void exitListType(@NotNull Xml2AsParser.ListTypeContext ctx);

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
	 * Enter a parse tree produced by {@link Xml2AsParser#xml2}.
	 * @param ctx the parse tree
	 */
	void enterXml2(@NotNull Xml2AsParser.Xml2Context ctx);
	/**
	 * Exit a parse tree produced by {@link Xml2AsParser#xml2}.
	 * @param ctx the parse tree
	 */
	void exitXml2(@NotNull Xml2AsParser.Xml2Context ctx);

	/**
	 * Enter a parse tree produced by {@link Xml2AsParser#nativeType}.
	 * @param ctx the parse tree
	 */
	void enterNativeType(@NotNull Xml2AsParser.NativeTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link Xml2AsParser#nativeType}.
	 * @param ctx the parse tree
	 */
	void exitNativeType(@NotNull Xml2AsParser.NativeTypeContext ctx);
}