package com.facilio.workflowv2.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.ParameterContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowFieldType;
import com.facilio.workflows.context.WorkflowFunctionContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflows.exceptions.ScriptParseException;
import com.facilio.workflows.functions.FacilioSystemFunctionNameSpace;
import com.facilio.workflowv2.Visitor.CommonParser;
import com.facilio.workflowv2.autogens.WorkflowV2Parser;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.AssignmentContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Db_param_aggrContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Db_param_fieldContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Db_param_group_byContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Db_param_limitContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Db_param_rangeContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Db_param_sortContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.ExprContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.For_each_statementContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Function_paramContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Recursive_expressionContext;
import com.facilio.workflowv2.contexts.DBParamContext;
import com.facilio.workflowv2.contexts.Value;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class ScriptParser extends CommonParser<Value> {

	private static final Logger LOGGER = Logger.getLogger(ScriptParser.class.getName());

	WorkflowContext workflowContext;

	public WorkflowContext getWorkflowContext() {
		return workflowContext;
	}

	public void setWorkflowContext(WorkflowContext workflowContext) {
		this.workflowContext = workflowContext;
	}
	
	@Override 
    public Value visitCustomModuleInitialization(WorkflowV2Parser.CustomModuleInitializationContext ctx) {
    	try {
    		String moduleName = ctx.expr().getText();
    		
    		moduleName = moduleName.substring(1, moduleName.length()-1);

    		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        	FacilioModule module = modBean.getModule(moduleName);
        	if(module == null) {
        		throw new RuntimeException("Module "+moduleName+ " Does not exist");
        	}
        	return new Value(module); 
    	}
    	catch(Exception e) {
    		throw new RuntimeException(e.getMessage());
    	}
    }
	
	@Override
    public Value visitDb_param_field(Db_param_fieldContext ctx) {
    	if(dbParamContext.getFieldName() == null) {
    		String fieldValue = ctx.expr().getText();
    		fieldValue = fieldValue.substring(1, fieldValue.length()-1);
    		dbParamContext.setFieldName(fieldValue);
    	}
		return Value.VOID;
    }
    
    @Override
    public Value visitDb_param_aggr(Db_param_aggrContext ctx) {
    	
    	if(dbParamContext.getAggregateString() == null) {
    		
    		String aggRes = ctx.expr().getText();
    		
    		aggRes = aggRes.substring(1,aggRes.length()-1);
    		
    		if(aggRes.contains(",")) {
    			String[] aggrValues = aggRes.split(",");
    			String aggrString = aggrValues[0].trim();
    			String aggrFieldName = aggrValues[1].trim();
    			
    			dbParamContext.setAggregateString(aggrString);
    			dbParamContext.setAggregateFieldName(aggrFieldName);
    		}
    		else {
    			dbParamContext.setAggregateString(aggRes);
    		}
    	}
    	
    	return Value.VOID;
    }
    
    @Override
    public Value visitDb_param_limit(Db_param_limitContext ctx) {
    	
    	if(dbParamContext.getLimit() <= 0) {
    		
    		String limit = ctx.expr().getText();
    		dbParamContext.setLimit(Integer.valueOf(limit));
    	}
    	
		return Value.VOID;
    }
    
    @Override
    public Value visitDb_param_range(Db_param_rangeContext ctx) {
    	
    	return Value.VOID;
    }
    
    @Override
    public Value visitDb_param_group_by(Db_param_group_byContext ctx) {
    	
    	if(dbParamContext.getGroupBy() == null) {
    		
    		String fieldValue = ctx.expr().getText();
    		dbParamContext.setGroupBy(fieldValue.substring(1, fieldValue.length()-1));
    	}
    	
    	return Value.VOID;
    }
    
    @Override
    public Value visitDb_param_sort(Db_param_sortContext ctx) {
    	
    	if(dbParamContext.getSortOrder() == null) {
    		
    		String fieldValue = ctx.expr().getText();
    		dbParamContext.setSortByFieldName(fieldValue.substring(1, fieldValue.length()-1));
    		if(ctx.op != null) {
    			dbParamContext.setSortOrder(ctx.op.getText());
    		}
    	}
    	
    	return Value.VOID;
    }
	
//	@Override
//	public Value visitNameSpaceInitialization(NameSpaceInitializationContext ctx) {
//		try {
//    		String nameSpaceValue = ctx.expr().getText();
//    		nameSpaceValue = nameSpaceValue.substring(1, nameSpaceValue.trim().length()-1);
//        	FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.getFacilioDefaultFunction(nameSpaceValue);
//        	if(nameSpaceEnum == null) {
//        		WorkflowNamespaceContext namespace = UserFunctionAPI.getNameSpace(nameSpaceValue);
//        		if(namespace == null) {
//        			throw new RuntimeException("No such namespace - "+nameSpaceValue);
//        		}
//        		return new Value(namespace);
//        	}
//        	return new Value(nameSpaceEnum); 
//    	}
//    	catch(Exception e) {
//    		throw new RuntimeException(e);
//    	}
//    	
//	}
	
	@Override
	public Value visitAssignment(AssignmentContext ctx) {
		String var = ctx.assignment_var().getText();
		String exprString = ctx.expr().getText();
		
		ExpressionContext expressionContext = new ExpressionContext();
		expressionContext.setName(var);
		if(exprString.contains("Module") || exprString.contains("new NameSpace")) {
			Value value = visit(ctx.expr());
			if(value.asObject() instanceof WorkflowFunctionContext) {
				expressionContext.setDefaultFunctionContext((WorkflowFunctionContext)value.asObject());
			}
			if(value.asObject() instanceof DBParamContext) {
				DBParamContext dbParam = value.asDbParams();
				expressionContext.setModuleName(dbParam.getModuleName());
				expressionContext.setCriteria(dbParam.getCriteria());
				expressionContext.setFieldName(dbParam.getFieldName());
				expressionContext.setAggregateString(dbParam.getAggregateString());
				if(dbParam.getLimit() > 0) {
					expressionContext.setLimit(dbParam.getLimit()+"");
				}
				expressionContext.setGroupBy(dbParam.getGroupBy());
				expressionContext.setOrderByFieldName(dbParam.getSortByFieldName());
				expressionContext.setSortBy(dbParam.getSortOrder());
				if(dbParam.getFieldCriteria() != null) {
					for(Condition condition : dbParam.getFieldCriteria().getConditions().values()) {
						expressionContext.addAggregateCondition(condition);
					}
				}
			}
		}
		else {
			if(FacilioUtil.isNumeric(exprString)) {
				expressionContext.setConstant(exprString);
			}
			else {
				expressionContext.setExpr(exprString);
			}
		}
		this.workflowContext.addWorkflowExpression(expressionContext);
		
		return visitChildren(ctx);
	}
	
	@Override
    public Value visitIf_statement(WorkflowV2Parser.If_statementContext ctx) {
		
		throw new ScriptParseException("Workflow With 'If'");
	}
	
	@Override
	public Value visitFor_each_statement(For_each_statementContext ctx) {
		throw new ScriptParseException("Workflow With 'If'");
	}
	
	@Override
	public Value visitRecursive_expr(WorkflowV2Parser.Recursive_exprContext ctx) {

		Value value = this.visit(ctx.atom());

		WorkflowV2Util.checkForNullAndThrowException(value, ctx.atom().getText());

		for (Recursive_expressionContext functionCall : ctx.recursive_expression()) {
			if (functionCall.OPEN_PARANTHESIS() != null && functionCall.CLOSE_PARANTHESIS() != null) {
				
				if (value.asObject() instanceof FacilioModule) { // module Functions
					
					FacilioModule module = (FacilioModule) value.asObject();
    				
					String s = functionCall.expr(0).getText();
        			Value dbParamValue = visit(functionCall.expr(0));
        			
        			DBParamContext dbParams = dbParamValue.asDbParams();
        			dbParams.setModuleName(module.getName());
        			
        			return dbParamValue;
				} 
				else if (value.asObject() instanceof WorkflowNamespaceContext) { // user defined functions
					
					throw new ScriptParseException("");
				} 
				else { // system functions
					
					if (value.asObject() instanceof FacilioSystemFunctionNameSpace) {
						
						WorkflowFunctionContext wfFunctionContext = new WorkflowFunctionContext();
						wfFunctionContext.setFunctionName(functionCall.VAR().getText());
						wfFunctionContext.setNameSpace(((FacilioSystemFunctionNameSpace) value.asObject()).getName());
						String params = null;
						for(ExprContext expr : functionCall.expr()) {
							params = params == null ? "" : params;
							String text = expr.getText();
//							text = "${"+text+"}";
							params = params + text+",";
						}
						if(params != null) {
							params = params.substring(0,params.length() - 1);
						    wfFunctionContext.setParams(params);
						}
						return new Value(wfFunctionContext);
					}
				}
			}
		}
		return visitChildren(ctx);
	}

	@Override
	public Value visitFunction_block(WorkflowV2Parser.Function_blockContext ctx) {

		String functionName = ctx.function_name_declare().getText();

		WorkflowFieldType returnType = WorkflowFieldType.getStringvaluemap().get(ctx.data_type().op.getText());

		workflowContext.setReturnType(returnType.getIntValue());

		if (workflowContext instanceof WorkflowUserFunctionContext) {
			((WorkflowUserFunctionContext) workflowContext).setName(functionName);
		}

		List<ParameterContext> params = new ArrayList<>();
		for (Function_paramContext param : ctx.function_param()) {
			ParameterContext parameterContext = new ParameterContext();
			parameterContext.setTypeString(param.data_type().op.getText());
			parameterContext.setName(param.VAR().getText());
			params.add(parameterContext);
		}

		workflowContext.setParameters(params);
		return visitChildren(ctx);
	}
	
	@Override 
    public Value visitCondition_atom(WorkflowV2Parser.Condition_atomContext ctx) {
		
		Criteria currentCriteria = criteria != null ? criteria : fieldCriteria;
    	
    	Condition condition = new Condition();
    	condition.setFieldName(ctx.VAR().getText());
    	
    	String value = ctx.expr().getText();
    	
    	Operator operator = null;
    	
    	if(value.contains("new NameSpace(\"date\").getDateRange")) {
    		String nameSpaceParseRegex = "new NameSpace\\(\\\"date\\\"\\)\\.getDateRange\\((.*)\\)";
    		
    		Pattern condtionStringpattern = Pattern.compile(nameSpaceParseRegex);
    		
    		Matcher matcher = condtionStringpattern.matcher(value);
    		
    		while(matcher.find()) {
    			String operatorString = matcher.group(1);
    			if(operatorString.contains(",")) {
    				String[] operators = operatorString.split(",");
    				String dateOperatorName = operators[0].trim().substring(1, operators[0].trim().length()-1);
    				operator = DateOperators.getAllOperators().get(dateOperatorName);
    				value = operators[1].trim();
    			}
    			else {
    				operator = DateOperators.getAllOperators().get(operatorString.trim().substring(1,operatorString.trim().length()-1));
    			}
    		}
    	}
    	else {
    		
        	switch(ctx.op.getText()) {
        	case "==" :
        		operator = NumberOperators.EQUALS;
        		break;
        	case "!=" :
        		operator = NumberOperators.NOT_EQUALS;
        		break;
        	default:
        		operator = NumberOperators.getAllOperators().get(ctx.op.getText());
        		break;
        	}
    		
    	}
    	condition.setOperator(operator);
    	
    	condition.setValue(value);
    	
    	int seq = currentCriteria.addConditionMap(condition);
    	
    	currentCriteria.setPattern(currentCriteria.getPattern().replaceFirst(Pattern.quote(ctx.getText()), String.valueOf(seq)));
    	
    	return Value.VOID; 
    }

	@Override
	public Value visitFunction_return(WorkflowV2Parser.Function_returnContext ctx) {
		this.workflowContext.setResultEvaluator(ctx.expr().getText());
		return visitChildren(ctx);
	}

}
