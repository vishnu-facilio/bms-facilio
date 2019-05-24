// Generated from /Users/krishna/git/bmsconsole/src/main/java/com/facilio/workflowv2/autogens/WorkflowV2.g4 by ANTLR 4.7.2
package com.facilio.workflowv2.autogens;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link WorkflowV2Parser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface WorkflowV2Visitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#parse}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParse(WorkflowV2Parser.ParseContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#function_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_block(WorkflowV2Parser.Function_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#function_name_declare}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_name_declare(WorkflowV2Parser.Function_name_declareContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#function_param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_param(WorkflowV2Parser.Function_paramContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#data_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitData_type(WorkflowV2Parser.Data_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(WorkflowV2Parser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(WorkflowV2Parser.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(WorkflowV2Parser.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#if_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_statement(WorkflowV2Parser.If_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#condition_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition_block(WorkflowV2Parser.Condition_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#statement_block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement_block(WorkflowV2Parser.Statement_blockContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#for_each_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFor_each_statement(WorkflowV2Parser.For_each_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#log}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLog(WorkflowV2Parser.LogContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#function_return}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_return(WorkflowV2Parser.Function_returnContext ctx);
	/**
	 * Visit a parse tree produced by the {@code dbParamInitialization}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDbParamInitialization(WorkflowV2Parser.DbParamInitializationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code notExpr}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNotExpr(WorkflowV2Parser.NotExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arithmeticFirstPrecedenceExpr}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmeticFirstPrecedenceExpr(WorkflowV2Parser.ArithmeticFirstPrecedenceExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code unaryMinusExpr}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryMinusExpr(WorkflowV2Parser.UnaryMinusExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code standAloneStatements}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStandAloneStatements(WorkflowV2Parser.StandAloneStatementsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code arithmeticSecondPrecedenceExpr}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArithmeticSecondPrecedenceExpr(WorkflowV2Parser.ArithmeticSecondPrecedenceExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code booleanExpr}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanExpr(WorkflowV2Parser.BooleanExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code atomExpr}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtomExpr(WorkflowV2Parser.AtomExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code criteriaInitialization}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCriteriaInitialization(WorkflowV2Parser.CriteriaInitializationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code relationalExpr}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationalExpr(WorkflowV2Parser.RelationalExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code moduleInitialization}
	 * labeled alternative in {@link WorkflowV2Parser#stand_alone_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModuleInitialization(WorkflowV2Parser.ModuleInitializationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code customModuleInitialization}
	 * labeled alternative in {@link WorkflowV2Parser#stand_alone_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCustomModuleInitialization(WorkflowV2Parser.CustomModuleInitializationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nameSpaceInitialization}
	 * labeled alternative in {@link WorkflowV2Parser#stand_alone_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNameSpaceInitialization(WorkflowV2Parser.NameSpaceInitializationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code listOpp}
	 * labeled alternative in {@link WorkflowV2Parser#stand_alone_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListOpp(WorkflowV2Parser.ListOppContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mapOpps}
	 * labeled alternative in {@link WorkflowV2Parser#stand_alone_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMapOpps(WorkflowV2Parser.MapOppsContext ctx);
	/**
	 * Visit a parse tree produced by the {@code dataTypeSpecificFunction}
	 * labeled alternative in {@link WorkflowV2Parser#stand_alone_expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDataTypeSpecificFunction(WorkflowV2Parser.DataTypeSpecificFunctionContext ctx);
	/**
	 * Visit a parse tree produced by the {@code paranthesisExpr}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParanthesisExpr(WorkflowV2Parser.ParanthesisExprContext ctx);
	/**
	 * Visit a parse tree produced by the {@code numberAtom}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumberAtom(WorkflowV2Parser.NumberAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code booleanAtom}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBooleanAtom(WorkflowV2Parser.BooleanAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code stringAtom}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStringAtom(WorkflowV2Parser.StringAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code nullAtom}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNullAtom(WorkflowV2Parser.NullAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code varAtom}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVarAtom(WorkflowV2Parser.VarAtomContext ctx);
	/**
	 * Visit a parse tree produced by the {@code listAndMapSymbolOperation}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListAndMapSymbolOperation(WorkflowV2Parser.ListAndMapSymbolOperationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code listInitialisation}
	 * labeled alternative in {@link WorkflowV2Parser#list_opperations}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListInitialisation(WorkflowV2Parser.ListInitialisationContext ctx);
	/**
	 * Visit a parse tree produced by the {@code mapInitialisation}
	 * labeled alternative in {@link WorkflowV2Parser#map_opperations}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMapInitialisation(WorkflowV2Parser.MapInitialisationContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#db_param}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDb_param(WorkflowV2Parser.Db_paramContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#db_param_criteria}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDb_param_criteria(WorkflowV2Parser.Db_param_criteriaContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#db_param_field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDb_param_field(WorkflowV2Parser.Db_param_fieldContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#db_param_aggr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDb_param_aggr(WorkflowV2Parser.Db_param_aggrContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#db_param_limit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDb_param_limit(WorkflowV2Parser.Db_param_limitContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#db_param_range}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDb_param_range(WorkflowV2Parser.Db_param_rangeContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#db_param_sort}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDb_param_sort(WorkflowV2Parser.Db_param_sortContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#criteria}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCriteria(WorkflowV2Parser.CriteriaContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition(WorkflowV2Parser.ConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link WorkflowV2Parser#condition_atom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition_atom(WorkflowV2Parser.Condition_atomContext ctx);
}