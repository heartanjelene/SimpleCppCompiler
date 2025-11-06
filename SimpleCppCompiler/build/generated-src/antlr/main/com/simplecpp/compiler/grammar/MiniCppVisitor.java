// Generated from MiniCpp.g4 by ANTLR 4.13.1
 package com.simplecpp.compiler.grammar; 
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MiniCppParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MiniCppVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MiniCppParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(MiniCppParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniCppParser#stmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStmt(MiniCppParser.StmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniCppParser#declStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDeclStmt(MiniCppParser.DeclStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniCppParser#assignStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignStmt(MiniCppParser.AssignStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniCppParser#ioStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIoStmt(MiniCppParser.IoStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniCppParser#cinStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCinStmt(MiniCppParser.CinStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniCppParser#coutStmt}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCoutStmt(MiniCppParser.CoutStmtContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniCppParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(MiniCppParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniCppParser#addExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAddExpr(MiniCppParser.AddExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniCppParser#unary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnary(MiniCppParser.UnaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link MiniCppParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(MiniCppParser.PrimaryContext ctx);
}