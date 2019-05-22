package com.facilio.workflowv2.Visitor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.Operator;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.fw.BeanFactory;
import com.facilio.workflows.context.ParameterContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowFunctionContext;
import com.facilio.workflows.functions.FacilioSystemFunctionNameSpace;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.autogens.WorkflowV2BaseVisitor;
import com.facilio.workflowv2.autogens.WorkflowV2Parser;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.ExprContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Function_paramContext;
import com.facilio.workflowv2.contexts.DBParamContext;
import com.facilio.workflowv2.contexts.Value;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class FacilioWorkflowFunctionVisitor extends WorkflowV2BaseVisitor<Value> {

    private Map<String, Value> varMemoryMap = new HashMap<String, Value>();
    
    WorkflowContext workflowContext;
    
    public WorkflowContext getWorkflowContext() {
		return workflowContext;
	}

	public void setWorkflowContext(WorkflowContext workflowContext) {
		this.workflowContext = workflowContext;
	}

	boolean breakCodeFlow;
    boolean isFunctionHeaderVisitor;
    public void setParams(List<Object> parmasObjects) throws Exception {
    	if(workflowContext.getParameters().size() > 0) {
    		if(parmasObjects.size() < workflowContext.getParameters().size()) {
        		throw new Exception("param count mismatched");
        	}
        	
        	for(int i = 0;i<workflowContext.getParameters().size(); i++) {
        		ParameterContext param = workflowContext.getParameters().get(i);
        		Object value = parmasObjects.get(i);
        		varMemoryMap.put(param.getName(), new Value(value));
        	}
    	}
    }
    
    public void visitFunctionHeader(ParseTree tree) {
    	isFunctionHeaderVisitor = true;
    	this.visit(tree);
    	isFunctionHeaderVisitor = false;
    	breakCodeFlow = false;
    }
    @Override 
    public Value visitMapInitialisation(WorkflowV2Parser.MapInitialisationContext ctx) { 
    	return new Value(new HashMap<>()); 
    }
    
    @Override 
    public Value visitListInitialisation(WorkflowV2Parser.ListInitialisationContext ctx) 
    { 
    	return new Value(new ArrayList<>()); 
    }
    
    @Override 
    public Value visitListFetch(WorkflowV2Parser.ListFetchContext ctx) 
	{
    	Value listValue = this.visit(ctx.atom().get(0));
    	List list = listValue.asList();
    	int index = this.visit(ctx.atom().get(1)).asInt();
		return new Value(list.get(index));
	}
    @Override 
    public Value visitDataTypeSpecificFunction(WorkflowV2Parser.DataTypeSpecificFunctionContext ctx) {
    	try {
    		
    		Value value = this.visit(ctx.atom());
    		
    		if (value.asObject() instanceof FacilioModule) {									// module Functions
				FacilioModule module = (FacilioModule) value.asObject();
				
				String functionName = ctx.VAR().getText();
    			
				Object moduleFunctionObject = WorkflowV2Util.getInstanceOf(module);
    			Method method = moduleFunctionObject.getClass().getMethod(functionName, List.class);
    			
    			List<Object> params = WorkflowV2Util.getParamList(ctx,true,this,value);

    			Object result = method.invoke(moduleFunctionObject, params);
    			return new Value(result);
        	}
    		else if (value.asObject() instanceof WorkflowNamespaceContext) {					// user defined functions
    			
    			WorkflowNamespaceContext namespaceContext = (WorkflowNamespaceContext) value.asObject();
    			List<Object> paramValues = WorkflowV2Util.getParamList(ctx,false,this,null);
    			
    			WorkflowContext wfContext = WorkflowV2Util.getWorkflowFunction(namespaceContext.getId(), ctx.VAR().getText());
    			wfContext.setParams(paramValues);
    			
    			Object res = wfContext.executeWorkflow();
    			
    			return new Value(res);
    		}
    		else {																				// system functions	
    			WorkflowFunctionContext wfFunctionContext = new WorkflowFunctionContext();
            	wfFunctionContext.setFunctionName(ctx.VAR().getText());
            	
            	boolean isDataTypeSpecificFunction = false;
            	
            	if(value.asObject() instanceof List) {
            		wfFunctionContext.setNameSpace(FacilioSystemFunctionNameSpace.LIST.getName());
            		isDataTypeSpecificFunction = true;
            	}
            	else if(value.asObject() instanceof Map) {
            		wfFunctionContext.setNameSpace(FacilioSystemFunctionNameSpace.MAP.getName());
            		isDataTypeSpecificFunction = true;
            	}
            	else if(value.asObject() instanceof String) {
            		wfFunctionContext.setNameSpace(FacilioSystemFunctionNameSpace.STRING.getName());
            		isDataTypeSpecificFunction = true;
            	}
            	else if (value.asObject() instanceof FacilioSystemFunctionNameSpace) {
            		wfFunctionContext.setNameSpace(((FacilioSystemFunctionNameSpace)value.asObject()).getName());
            	}
            	
            	List<Object> paramValues = WorkflowV2Util.getParamList(ctx,isDataTypeSpecificFunction,this,value);
            	
            	Object result = WorkflowUtil.evalSystemFunctions(wfFunctionContext, paramValues);
            	return new Value(result); 
    		}
    	}
    	catch(Exception e) {
    		throw new ParseCancellationException(e); 
    	}
    }
    
    @Override
    public Value visitAssignment(WorkflowV2Parser.AssignmentContext ctx) {
        String varName = ctx.VAR().getText();
        Value value = this.visit(ctx.expr());
        return varMemoryMap.put(varName, value);
    }
    
    @Override 
    public Value visitFunction_block(WorkflowV2Parser.Function_blockContext ctx) {
    	
    	String functionName = ctx.function_name_declare().getText();
    	
    	workflowContext.setName(functionName);
    	
    	List<ParameterContext> params = new ArrayList<>();
		for(Function_paramContext param :ctx.function_param()) {
			ParameterContext parameterContext = new ParameterContext();
			parameterContext.setTypeString(param.data_type().op.getText());
			parameterContext.setName(param.VAR().getText());
			params.add(parameterContext);
    	}
		
		workflowContext.setParameters(params);
		if(isFunctionHeaderVisitor) {
			breakCodeFlow = true;
		}
    	return visitChildren(ctx); 
    }
    

    @Override
    public Value visitVarAtom(WorkflowV2Parser.VarAtomContext ctx) {
        String varName = ctx.getText();
        Value value = varMemoryMap.get(varName);
        if(value == null) {
            throw new RuntimeException("no such variable: " + varName);
        }
        return value;
    }

    @Override
    public Value visitStringAtom(WorkflowV2Parser.StringAtomContext ctx) {
        String str = ctx.getText();
        // strip quotes
        str = str.substring(1, str.length() - 1).replace("\"\"", "\"");
        return new Value(str);
    }

    @Override
    public Value visitNumberAtom(WorkflowV2Parser.NumberAtomContext ctx) {
        return new Value(Double.valueOf(ctx.getText()));
    }

    @Override
    public Value visitBooleanAtom(WorkflowV2Parser.BooleanAtomContext ctx) {
        return new Value(Boolean.valueOf(ctx.getText()));
    }

    @Override
    public Value visitNullAtom(WorkflowV2Parser.NullAtomContext ctx) {
        return new Value(null);
    }

    @Override
    public Value visitParanthesisExpr(WorkflowV2Parser.ParanthesisExprContext ctx) {
        return this.visit(ctx.expr());
    }
    
    @Override 
    public Value visitModuleInitialization(WorkflowV2Parser.ModuleInitializationContext ctx) {
    	try {
    		String moduleDisplayName = ctx.VAR(0).getText();
        	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        	FacilioModule module = modBean.getModule(WorkflowV2Util.getModuleName(moduleDisplayName));
        	return new Value(module); 
    	}
    	catch(Exception e) {
    		throw new ParseCancellationException(e);
    	}
    }
    
    @Override 
    public Value visitCustomModuleInitialization(WorkflowV2Parser.CustomModuleInitializationContext ctx) {
    	try {
    		String moduleName = this.visit(ctx.atom()).asString();
        	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        	FacilioModule module = modBean.getModule(moduleName);
        	return new Value(module); 
    	}
    	catch(Exception e) {
    		throw new ParseCancellationException(e);
    	}
    }
    
    @Override 
    public Value visitNameSpaceInitialization(WorkflowV2Parser.NameSpaceInitializationContext ctx) {
    	try {
    		String nameSpaceString = this.visit(ctx.atom()).asString();
        	FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.getFacilioDefaultFunction(nameSpaceString);
        	if(nameSpaceEnum == null) {
        		WorkflowNamespaceContext namespace = WorkflowV2Util.getNameSpace(nameSpaceString);
        		if(namespace == null) {
        			throw new ParseCancellationException("No such namespace - "+nameSpaceString);
        		}
        		return new Value(namespace);
        	}
        	return new Value(nameSpaceEnum); 
    	}
    	catch(Exception e) {
    		throw new ParseCancellationException(e);
    	}
    	
    }
    
//    @Override
//    public Value visitFunctionCall(WorkflowV2Parser.FunctionCallContext ctx) {
//    	try {
//    		String functionNameText = ctx.FUNCTION_NAME().getText();
//        	String[] splits = functionNameText.split(".");
//        	if(splits[0].equals("app")) {
//        		WorkflowFunctionContext wfFunctionContext = new WorkflowFunctionContext(); 
//        		String nameSpace = splits[1];
//        		String functionName = splits[2];
//        		
//        		wfFunctionContext.setNameSpace(nameSpace);
//        		wfFunctionContext.setFunctionName(functionName);
//        		
//        		Object[] paramValues = new Object[ctx.expr().size()];
//        		int i = 0;
//    			for(ExprContext expr :ctx.expr()) {
//            		Value paramValue = this.visit(expr);
//            		paramValues [i++] = paramValue.asObject();
//            	}
//            	Object result = WorkflowUtil.evalSystemFunctions(wfFunctionContext, paramValues);
//            	return new Value(result);
//        	}
//    	}
//    	catch(Exception e) {
//    	}
//    	return Value.VOID;
//    }
    
    
    
//   @Override 
//   public Value visitFetchRecord(WorkflowV2Parser.FetchRecordContext ctx) {
//	   
//	   String moduleName = ctx.MODULE_NAME().getText();
//	   System.out.println("moduleName -- "+moduleName);
//	   System.out.println("conditions -- "+ctx.criteria().condition().getText());
//	   this.visit(ctx.criteria());
//	   System.out.println("conditionMap -- "+conditionMap);
//	   
//	   String criteria = ctx.criteria().condition().getText();
//	   
//	   String s = criteria;
//	   for(int key:conditionMap.keySet()) {
//		   String sk = conditionMap.get(key);
//		   s = s.replaceFirst(sk, key+"");
//	   }
//	   
//	   System.out.println("final  criteria -- "+s);
//	   
////	   for(ConditionContext condition :ctx.condition().condition()) {
////		   System.out.println("condition -- "+condition.getText());
////		   System.out.println("fieldName -- "+condition.VAR().getText());
////		   System.out.println("opp -- "+condition.op.getText());
////		   System.out.println("value -- "+this.visit(condition.atom()));
////	   }
//	   if(ctx.VAR(0) != null) {
//		   if(ctx.VAR(1) != null) {
//			   System.out.println("aggregation -- "+ctx.VAR(0));
//			   System.out.println("fieldName --   "+ctx.VAR(1));
//		   }
//		   else {
//			   System.out.println("fieldName -- "+ctx.VAR(0));
//		   }
//	   }
//	   
//	   return new Value(10);
//   }

    @Override
    public Value visitUnaryMinusExpr(WorkflowV2Parser.UnaryMinusExprContext ctx) {
        Value value = this.visit(ctx.expr());
        return new Value(-value.asDouble());
    }
    
//    @Override 
//    public Value visitApiCall(WorkflowV2Parser.ApiCallContext ctx) {
//    	
//    	System.out.println("moduleName -- "+ctx.API().getText());
//    	
//    	for(ExprContext expr :ctx.expr()) {
//    		Value value = this.visit(expr);
//    		System.out.println("val -- "+value);
//    	}
//    	return visitChildren(ctx); 
//    }

    @Override
    public Value visitNotExpr(WorkflowV2Parser.NotExprContext ctx) {
        Value value = this.visit(ctx.expr());
        return new Value(!value.asBoolean());
    }
    
    @Override
    public Value visitArithmeticExpr(WorkflowV2Parser.ArithmeticExprContext ctx) {

        Value left = this.visit(ctx.expr(0));
        Value right = this.visit(ctx.expr(1));

        switch (ctx.op.getType()) {
            case WorkflowV2Parser.MULT:
                return new Value(left.asDouble() * right.asDouble());
            case WorkflowV2Parser.DIV:
                return new Value(left.asDouble() / right.asDouble());
            case WorkflowV2Parser.MOD:
                return new Value(left.asDouble() % right.asDouble());
            case WorkflowV2Parser.PLUS:
                return left.isDouble() && right.isDouble() ?
                        new Value(left.asDouble() + right.asDouble()) :
                        new Value(left.asString() + right.asString());
            case WorkflowV2Parser.MINUS:
                return new Value(left.asDouble() - right.asDouble());
            default:
                throw new RuntimeException("unknown operator: " + WorkflowV2Parser.tokenNames[ctx.op.getType()]);
        }
    }

    @Override
    public Value visitRelationalExpr(WorkflowV2Parser.RelationalExprContext ctx) {

        Value left = this.visit(ctx.expr(0));
        Value right = this.visit(ctx.expr(1));

        switch (ctx.op.getType()) {
            case WorkflowV2Parser.LT:
                return new Value(left.asDouble() < right.asDouble());
            case WorkflowV2Parser.LTEQ:
                return new Value(left.asDouble() <= right.asDouble());
            case WorkflowV2Parser.GT:
                return new Value(left.asDouble() > right.asDouble());
            case WorkflowV2Parser.GTEQ:
                return new Value(left.asDouble() >= right.asDouble());
            case WorkflowV2Parser.EQ:
                return  new Value(left.equals(right));
            case WorkflowV2Parser.NEQ:
                return new Value(!left.equals(right));
            default:
                throw new RuntimeException("unknown operator: " + WorkflowV2Parser.tokenNames[ctx.op.getType()]);
        }
    }
    
    public Value visitBooleanExpr(WorkflowV2Parser.BooleanExprContext ctx) {
    	 Value left = this.visit(ctx.expr(0));
         Value right = this.visit(ctx.expr(1));

         switch (ctx.op.getType()) {
             case WorkflowV2Parser.AND:
            	 return new Value(left.asBoolean() && right.asBoolean());
             case WorkflowV2Parser.OR:
            	 return new Value(left.asBoolean() || right.asBoolean());
             default:
                 throw new RuntimeException("unknown operator: " + WorkflowV2Parser.tokenNames[ctx.op.getType()]);
         } 
    }

    @Override
    public Value visitLog(WorkflowV2Parser.LogContext ctx) {
        Value value = this.visit(ctx.expr());
        workflowContext.getLogString().append(value.asString()+"\n");
        System.out.println(value);
        return value;
    }
    
    Criteria criteria = null;
    
    @Override 
    public Value visitCondition_atom(WorkflowV2Parser.Condition_atomContext ctx) {
    	
    	System.out.println("condition subs --- "+ctx.getText());
    	Condition condition = new Condition();
    	condition.setFieldName(ctx.VAR().getText());
    	
    	Operator operator = null;
    	
    	Value operatorValue = this.visit(ctx.atom());
    	switch(ctx.op.getText()) {
    	case "==" :
    		if(operatorValue.asObject() instanceof String) {
    			operator = StringOperators.IS;
    		}
    		else if(operatorValue.asObject() instanceof Boolean) {
    			operator = BooleanOperators.IS;
    		}
    		else {
    			operator = NumberOperators.EQUALS;
    		}
    		break;
    	default:
    		operator = NumberOperators.getAllOperators().get(ctx.op.getText());
    		break;
    	}
    	condition.setOperator(operator);
    	
    	String value = operatorValue.asString();
    	
    	condition.setValue(value);
    	
    	int seq = criteria.addConditionMap(condition);
    	
    	criteria.setPattern(criteria.getPattern().replaceFirst(ctx.getText(), String.valueOf(seq)));
    	
    	return visitChildren(ctx); 
    }
    
    @Override 
    public Value visitCriteria(WorkflowV2Parser.CriteriaContext ctx) {
    	
    	criteria = new Criteria(); 
    	
    	String criteriaSting = ctx.getText();
    	
    	criteria.setPattern(criteriaSting);
    	
    	this.visit(ctx.condition());
    	
    	criteria.setPattern(WorkflowV2Util.adjustCriteriaPattern(criteria.getPattern()));
    	
    	Value criteriaVal = new Value(criteria);
    	
    	criteria = null;
    	return criteriaVal;
    }
    
	@Override
	public Value visitDb_param(WorkflowV2Parser.Db_paramContext ctx) {
		
		DBParamContext dbParamContext = new DBParamContext();
		
		Value criteriaValue = this.visit(ctx.db_param_criteria().criteria());
		
		dbParamContext.setCriteria(criteriaValue.asCriteria());

		if(ctx.db_param_field(0) != null) {
			Value fieldValue = this.visit(ctx.db_param_field(0).atom());
			dbParamContext.setFieldName(fieldValue.asString());
		}
		if(ctx.db_param_aggr(0) != null) {
			Value aggrValue = this.visit(ctx.db_param_aggr(0).atom());
			dbParamContext.setAggregateString(aggrValue.asString());
		}
		if(ctx.db_param_limit(0) != null) {
			Value limitValue = this.visit(ctx.db_param_limit(0).atom());
			dbParamContext.setLimit(limitValue.asInt());
		}
		if(ctx.db_param_range(0) != null) {
			Value fromValue = this.visit(ctx.db_param_range(0).atom(0));
			Value toValue = this.visit(ctx.db_param_range(0).atom(1));
			dbParamContext.setRange(Pair.of(fromValue.asInt(), toValue.asInt()));
		}
		if(ctx.db_param_sort(0) != null) {
			Value sortByField = this.visit(ctx.db_param_sort(0).atom());
			dbParamContext.setSortByFieldName(sortByField.asString());
			dbParamContext.setSortOrder(ctx.db_param_sort(0).op.getText());
		}
		
		System.out.println("ffinal -- "+dbParamContext);
		
		return new Value(dbParamContext);
	}

	@Override
    public Value visitIf_statement(WorkflowV2Parser.If_statementContext ctx) {

        List<WorkflowV2Parser.Condition_blockContext> conditions =  ctx.condition_block();

        boolean evaluatedBlock = false;

        for(WorkflowV2Parser.Condition_blockContext condition : conditions) {

            Value evaluated = this.visit(condition.expr());

            if(evaluated.asBoolean()) {
                evaluatedBlock = true;
                this.visit(condition.statement_block());
                break;
            }
        }

        if(!evaluatedBlock && ctx.statement_block() != null) {
            // evaluate the else-stat_block (if present == not null)
            this.visit(ctx.statement_block());
        }

        return Value.VOID;
    }

    public Value visitFor_each_statement(WorkflowV2Parser.For_each_statementContext ctx) {
    	
    	Value exprValue = this.visit(ctx.expr());
    	System.out.println("iterateVae - "+exprValue);
    	String loopVariableIndexName = ctx.VAR(0).getText();
    	String loopVariableValueName = ctx.VAR(1).getText();
    	
    	if(exprValue.asObject() instanceof Collection) {
			
			List iterateList = new ArrayList((Collection)exprValue.asObject());
			
			for(int i=0 ; i<iterateList.size() ;i++) {
				varMemoryMap.put(loopVariableIndexName, new Value(i));
				varMemoryMap.put(loopVariableValueName, new Value(iterateList.get(i)));
				
				this.visit(ctx.statement_block());
			}
			varMemoryMap.remove(loopVariableIndexName);
			varMemoryMap.remove(loopVariableValueName);
			
		}
		else if(exprValue.asObject() instanceof Map) {
			Map iterateMap = (Map) exprValue.asObject();
			for(Object key :iterateMap.keySet() ) {
				varMemoryMap.put(loopVariableIndexName, new Value(key));						// index acts as key for Map Iteration 
				varMemoryMap.put(loopVariableValueName, new Value(iterateMap.get(key)));
				
				this.visit(ctx.statement_block());
			}
			varMemoryMap.remove(loopVariableIndexName);
			varMemoryMap.remove(loopVariableValueName);
		}
    	
    	return Value.VOID; 
    }
    
    @Override 
    public Value visitFunction_return(WorkflowV2Parser.Function_returnContext ctx)
    {
    	Value returnValue = this.visit(ctx.expr());
    	workflowContext.setReturnValue(returnValue.asObject());
    	this.breakCodeFlow = true;
    	return Value.VOID; 
    }
    
    @Override
    protected boolean shouldVisitNextChild(RuleNode node, Value currentResult) {
    	if(breakCodeFlow) {
    		return false;
    	}
    	return super.shouldVisitNextChild(node, currentResult);
    }

}
