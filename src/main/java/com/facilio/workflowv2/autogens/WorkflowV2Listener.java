// Generated from /Users/krishna/git/bmsconsole/src/main/java/com/facilio/workflowv2/autogens/WorkflowV2.g4 by ANTLR 4.7.2
package com.facilio.workflowv2.autogens;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link WorkflowV2Parser}.
 */
public interface WorkflowV2Listener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#parse}.
	 * @param ctx the parse tree
	 */
	void enterParse(WorkflowV2Parser.ParseContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#parse}.
	 * @param ctx the parse tree
	 */
	void exitParse(WorkflowV2Parser.ParseContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#function_block}.
	 * @param ctx the parse tree
	 */
	void enterFunction_block(WorkflowV2Parser.Function_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#function_block}.
	 * @param ctx the parse tree
	 */
	void exitFunction_block(WorkflowV2Parser.Function_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#function_name_declare}.
	 * @param ctx the parse tree
	 */
	void enterFunction_name_declare(WorkflowV2Parser.Function_name_declareContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#function_name_declare}.
	 * @param ctx the parse tree
	 */
	void exitFunction_name_declare(WorkflowV2Parser.Function_name_declareContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#function_param}.
	 * @param ctx the parse tree
	 */
	void enterFunction_param(WorkflowV2Parser.Function_paramContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#function_param}.
	 * @param ctx the parse tree
	 */
	void exitFunction_param(WorkflowV2Parser.Function_paramContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#data_type}.
	 * @param ctx the parse tree
	 */
	void enterData_type(WorkflowV2Parser.Data_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#data_type}.
	 * @param ctx the parse tree
	 */
	void exitData_type(WorkflowV2Parser.Data_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#block}.
	 * @param ctx the parse tree
	 */
	void enterBlock(WorkflowV2Parser.BlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#block}.
	 * @param ctx the parse tree
	 */
	void exitBlock(WorkflowV2Parser.BlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(WorkflowV2Parser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(WorkflowV2Parser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(WorkflowV2Parser.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(WorkflowV2Parser.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#if_statement}.
	 * @param ctx the parse tree
	 */
	void enterIf_statement(WorkflowV2Parser.If_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#if_statement}.
	 * @param ctx the parse tree
	 */
	void exitIf_statement(WorkflowV2Parser.If_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#condition_block}.
	 * @param ctx the parse tree
	 */
	void enterCondition_block(WorkflowV2Parser.Condition_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#condition_block}.
	 * @param ctx the parse tree
	 */
	void exitCondition_block(WorkflowV2Parser.Condition_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#statement_block}.
	 * @param ctx the parse tree
	 */
	void enterStatement_block(WorkflowV2Parser.Statement_blockContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#statement_block}.
	 * @param ctx the parse tree
	 */
	void exitStatement_block(WorkflowV2Parser.Statement_blockContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#for_each_statement}.
	 * @param ctx the parse tree
	 */
	void enterFor_each_statement(WorkflowV2Parser.For_each_statementContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#for_each_statement}.
	 * @param ctx the parse tree
	 */
	void exitFor_each_statement(WorkflowV2Parser.For_each_statementContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#log}.
	 * @param ctx the parse tree
	 */
	void enterLog(WorkflowV2Parser.LogContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#log}.
	 * @param ctx the parse tree
	 */
	void exitLog(WorkflowV2Parser.LogContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#function_return}.
	 * @param ctx the parse tree
	 */
	void enterFunction_return(WorkflowV2Parser.Function_returnContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#function_return}.
	 * @param ctx the parse tree
	 */
	void exitFunction_return(WorkflowV2Parser.Function_returnContext ctx);
	/**
	 * Enter a parse tree produced by the {@code dbParamInitialization}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 */
	void enterDbParamInitialization(WorkflowV2Parser.DbParamInitializationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code dbParamInitialization}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 */
	void exitDbParamInitialization(WorkflowV2Parser.DbParamInitializationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code notExpr}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNotExpr(WorkflowV2Parser.NotExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code notExpr}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNotExpr(WorkflowV2Parser.NotExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arithmeticFirstPrecedenceExpr}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 */
	void enterArithmeticFirstPrecedenceExpr(WorkflowV2Parser.ArithmeticFirstPrecedenceExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arithmeticFirstPrecedenceExpr}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 */
	void exitArithmeticFirstPrecedenceExpr(WorkflowV2Parser.ArithmeticFirstPrecedenceExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code unaryMinusExpr}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 */
	void enterUnaryMinusExpr(WorkflowV2Parser.UnaryMinusExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code unaryMinusExpr}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 */
	void exitUnaryMinusExpr(WorkflowV2Parser.UnaryMinusExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code standAloneStatements}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 */
	void enterStandAloneStatements(WorkflowV2Parser.StandAloneStatementsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code standAloneStatements}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 */
	void exitStandAloneStatements(WorkflowV2Parser.StandAloneStatementsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code arithmeticSecondPrecedenceExpr}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 */
	void enterArithmeticSecondPrecedenceExpr(WorkflowV2Parser.ArithmeticSecondPrecedenceExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code arithmeticSecondPrecedenceExpr}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 */
	void exitArithmeticSecondPrecedenceExpr(WorkflowV2Parser.ArithmeticSecondPrecedenceExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code booleanExpr}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBooleanExpr(WorkflowV2Parser.BooleanExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code booleanExpr}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBooleanExpr(WorkflowV2Parser.BooleanExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code atomExpr}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAtomExpr(WorkflowV2Parser.AtomExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code atomExpr}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAtomExpr(WorkflowV2Parser.AtomExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code criteriaInitialization}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 */
	void enterCriteriaInitialization(WorkflowV2Parser.CriteriaInitializationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code criteriaInitialization}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 */
	void exitCriteriaInitialization(WorkflowV2Parser.CriteriaInitializationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code relationalExpr}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 */
	void enterRelationalExpr(WorkflowV2Parser.RelationalExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code relationalExpr}
	 * labeled alternative in {@link WorkflowV2Parser#expr}.
	 * @param ctx the parse tree
	 */
	void exitRelationalExpr(WorkflowV2Parser.RelationalExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code recursive_expr}
	 * labeled alternative in {@link WorkflowV2Parser#stand_alone_expr}.
	 * @param ctx the parse tree
	 */
	void enterRecursive_expr(WorkflowV2Parser.Recursive_exprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code recursive_expr}
	 * labeled alternative in {@link WorkflowV2Parser#stand_alone_expr}.
	 * @param ctx the parse tree
	 */
	void exitRecursive_expr(WorkflowV2Parser.Recursive_exprContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#recursive_expression}.
	 * @param ctx the parse tree
	 */
	void enterRecursive_expression(WorkflowV2Parser.Recursive_expressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#recursive_expression}.
	 * @param ctx the parse tree
	 */
	void exitRecursive_expression(WorkflowV2Parser.Recursive_expressionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code paranthesisExpr}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void enterParanthesisExpr(WorkflowV2Parser.ParanthesisExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code paranthesisExpr}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void exitParanthesisExpr(WorkflowV2Parser.ParanthesisExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code numberAtom}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void enterNumberAtom(WorkflowV2Parser.NumberAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code numberAtom}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void exitNumberAtom(WorkflowV2Parser.NumberAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code booleanAtom}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void enterBooleanAtom(WorkflowV2Parser.BooleanAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code booleanAtom}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void exitBooleanAtom(WorkflowV2Parser.BooleanAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code stringAtom}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void enterStringAtom(WorkflowV2Parser.StringAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code stringAtom}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void exitStringAtom(WorkflowV2Parser.StringAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nullAtom}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void enterNullAtom(WorkflowV2Parser.NullAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nullAtom}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void exitNullAtom(WorkflowV2Parser.NullAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code varAtom}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void enterVarAtom(WorkflowV2Parser.VarAtomContext ctx);
	/**
	 * Exit a parse tree produced by the {@code varAtom}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void exitVarAtom(WorkflowV2Parser.VarAtomContext ctx);
	/**
	 * Enter a parse tree produced by the {@code nameSpaceInitialization}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void enterNameSpaceInitialization(WorkflowV2Parser.NameSpaceInitializationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code nameSpaceInitialization}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void exitNameSpaceInitialization(WorkflowV2Parser.NameSpaceInitializationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code customModuleInitialization}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void enterCustomModuleInitialization(WorkflowV2Parser.CustomModuleInitializationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code customModuleInitialization}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void exitCustomModuleInitialization(WorkflowV2Parser.CustomModuleInitializationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code moduleAndSystemNameSpaceInitialization}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void enterModuleAndSystemNameSpaceInitialization(WorkflowV2Parser.ModuleAndSystemNameSpaceInitializationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code moduleAndSystemNameSpaceInitialization}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void exitModuleAndSystemNameSpaceInitialization(WorkflowV2Parser.ModuleAndSystemNameSpaceInitializationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code listOpp}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void enterListOpp(WorkflowV2Parser.ListOppContext ctx);
	/**
	 * Exit a parse tree produced by the {@code listOpp}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void exitListOpp(WorkflowV2Parser.ListOppContext ctx);
	/**
	 * Enter a parse tree produced by the {@code mapOpps}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void enterMapOpps(WorkflowV2Parser.MapOppsContext ctx);
	/**
	 * Exit a parse tree produced by the {@code mapOpps}
	 * labeled alternative in {@link WorkflowV2Parser#atom}.
	 * @param ctx the parse tree
	 */
	void exitMapOpps(WorkflowV2Parser.MapOppsContext ctx);
	/**
	 * Enter a parse tree produced by the {@code listInitialisation}
	 * labeled alternative in {@link WorkflowV2Parser#list_opperations}.
	 * @param ctx the parse tree
	 */
	void enterListInitialisation(WorkflowV2Parser.ListInitialisationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code listInitialisation}
	 * labeled alternative in {@link WorkflowV2Parser#list_opperations}.
	 * @param ctx the parse tree
	 */
	void exitListInitialisation(WorkflowV2Parser.ListInitialisationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code mapInitialisation}
	 * labeled alternative in {@link WorkflowV2Parser#map_opperations}.
	 * @param ctx the parse tree
	 */
	void enterMapInitialisation(WorkflowV2Parser.MapInitialisationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code mapInitialisation}
	 * labeled alternative in {@link WorkflowV2Parser#map_opperations}.
	 * @param ctx the parse tree
	 */
	void exitMapInitialisation(WorkflowV2Parser.MapInitialisationContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#db_param}.
	 * @param ctx the parse tree
	 */
	void enterDb_param(WorkflowV2Parser.Db_paramContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#db_param}.
	 * @param ctx the parse tree
	 */
	void exitDb_param(WorkflowV2Parser.Db_paramContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#db_param_criteria}.
	 * @param ctx the parse tree
	 */
	void enterDb_param_criteria(WorkflowV2Parser.Db_param_criteriaContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#db_param_criteria}.
	 * @param ctx the parse tree
	 */
	void exitDb_param_criteria(WorkflowV2Parser.Db_param_criteriaContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#db_param_field}.
	 * @param ctx the parse tree
	 */
	void enterDb_param_field(WorkflowV2Parser.Db_param_fieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#db_param_field}.
	 * @param ctx the parse tree
	 */
	void exitDb_param_field(WorkflowV2Parser.Db_param_fieldContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#db_param_aggr}.
	 * @param ctx the parse tree
	 */
	void enterDb_param_aggr(WorkflowV2Parser.Db_param_aggrContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#db_param_aggr}.
	 * @param ctx the parse tree
	 */
	void exitDb_param_aggr(WorkflowV2Parser.Db_param_aggrContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#db_param_limit}.
	 * @param ctx the parse tree
	 */
	void enterDb_param_limit(WorkflowV2Parser.Db_param_limitContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#db_param_limit}.
	 * @param ctx the parse tree
	 */
	void exitDb_param_limit(WorkflowV2Parser.Db_param_limitContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#db_param_range}.
	 * @param ctx the parse tree
	 */
	void enterDb_param_range(WorkflowV2Parser.Db_param_rangeContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#db_param_range}.
	 * @param ctx the parse tree
	 */
	void exitDb_param_range(WorkflowV2Parser.Db_param_rangeContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#db_param_group_by}.
	 * @param ctx the parse tree
	 */
	void enterDb_param_group_by(WorkflowV2Parser.Db_param_group_byContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#db_param_group_by}.
	 * @param ctx the parse tree
	 */
	void exitDb_param_group_by(WorkflowV2Parser.Db_param_group_byContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#db_param_sort}.
	 * @param ctx the parse tree
	 */
	void enterDb_param_sort(WorkflowV2Parser.Db_param_sortContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#db_param_sort}.
	 * @param ctx the parse tree
	 */
	void exitDb_param_sort(WorkflowV2Parser.Db_param_sortContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#criteria}.
	 * @param ctx the parse tree
	 */
	void enterCriteria(WorkflowV2Parser.CriteriaContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#criteria}.
	 * @param ctx the parse tree
	 */
	void exitCriteria(WorkflowV2Parser.CriteriaContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#condition}.
	 * @param ctx the parse tree
	 */
	void enterCondition(WorkflowV2Parser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#condition}.
	 * @param ctx the parse tree
	 */
	void exitCondition(WorkflowV2Parser.ConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link WorkflowV2Parser#condition_atom}.
	 * @param ctx the parse tree
	 */
	void enterCondition_atom(WorkflowV2Parser.Condition_atomContext ctx);
	/**
	 * Exit a parse tree produced by {@link WorkflowV2Parser#condition_atom}.
	 * @param ctx the parse tree
	 */
	void exitCondition_atom(WorkflowV2Parser.Condition_atomContext ctx);
}