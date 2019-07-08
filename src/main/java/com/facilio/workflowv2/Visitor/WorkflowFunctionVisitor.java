package com.facilio.workflowv2.Visitor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.bmsconsole.util.ConnectionUtil;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import com.facilio.workflows.context.ParameterContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowFieldType;
import com.facilio.workflows.context.WorkflowFunctionContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflows.functions.FacilioSystemFunctionNameSpace;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.autogens.WorkflowV2BaseVisitor;
import com.facilio.workflowv2.autogens.WorkflowV2Parser;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Function_paramContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Recursive_expressionContext;
import com.facilio.workflowv2.contexts.DBParamContext;
import com.facilio.workflowv2.contexts.Value;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;
import com.facilio.workflowv2.contexts.WorkflowReadingContext;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class WorkflowFunctionVisitor extends WorkflowV2BaseVisitor<Value> {
	
	private static final Logger LOGGER = Logger.getLogger(WorkflowFunctionVisitor.class.getName());

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
    	if(parmasObjects != null && parmasObjects.size() > 0) {
//    		if(parmasObjects.size() < workflowContext.getParameters().size()) {
//        		throw new RuntimeException("param count mismatched");
//        	}
        	
        	for(int i = 0;i<parmasObjects.size(); i++) {
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
    
//    @Override 
//    public Value visitListSymbolOperation(WorkflowV2Parser.ListSymbolOperationContext ctx)
//	{
//    	String varName = ctx.VAR().getText();
//    	Value value = this.varMemoryMap.get(varName);
//    	if(value.asObject() instanceof List ) {
//    		Value listValue = this.visit(ctx.atom());
//    		Integer index = listValue.asInt();
//    		return new Value(value.asList().get(index));
//    	}
//    	else {
//    		throw new RuntimeException("var "+varName+" is not of list type");
//    	}
//	}
    
//    @Override 
//    public Value visitMapSymbolOperation(WorkflowV2Parser.MapSymbolOperationContext ctx)
//	{
//    	String mapVar = ctx.VAR(0).getText();
//    	Value mapValue = this.varMemoryMap.get(mapVar);
//    	if(mapValue.asObject() instanceof Map ) {
//    		Map<Object, Object> map = mapValue.asMap();
//    		
//    		int length = ctx.VAR().size();
//    		
//    		for(int i=1;i<length;i++) {
//    			String key = ctx.VAR(i).getText();
//    			Object currentValue = map.get(key);
//    			
//    			if(i == length-1) {
//    				return new Value(currentValue);
//    			}
//    			map = (Map<Object, Object>) currentValue;
//    		}
//    	}
//    	else {
//    		throw new RuntimeException("not a map " + mapVar);
//    	}
//    	return Value.VOID;
//	}
    
    @Override 
    public Value visitRecursive_expr(WorkflowV2Parser.Recursive_exprContext ctx) {
    	try {
    		
    		Value value = this.visit(ctx.atom());
    		
    		WorkflowV2Util.checkForNullAndThrowException(value, ctx.atom().getText());
    		
    		for(Recursive_expressionContext functionCall :ctx.recursive_expression()) {
    			if(functionCall.OPEN_PARANTHESIS() != null && functionCall.CLOSE_PARANTHESIS() != null) {
    				if (value.asObject() instanceof FacilioModule) {									// module Functions
        				FacilioModule module = (FacilioModule) value.asObject();
        				
        				String functionName = functionCall.VAR().getText();
            			
        				Object moduleFunctionObject = WorkflowV2Util.getInstanceOf(module);
            			Method method = moduleFunctionObject.getClass().getMethod(functionName, List.class);
            			
            			List<Object> params = WorkflowV2Util.getParamList(functionCall,true,this,value);

            			Object result = method.invoke(moduleFunctionObject, params);
            			value =  new Value(result);
                	}
            		else if (value.asObject() instanceof WorkflowNamespaceContext) {					// user defined functions
            			
            			WorkflowNamespaceContext namespaceContext = (WorkflowNamespaceContext) value.asObject();
            			List<Object> paramValues = WorkflowV2Util.getParamList(functionCall,false,this,null);
            			
            			WorkflowContext wfContext = UserFunctionAPI.getWorkflowFunction(namespaceContext.getId(), functionCall.VAR().getText());
            			wfContext.setParams(paramValues);
            			
            			Object res = wfContext.executeWorkflow();
            			
            			value = new Value(res);
            		}
            		else {																				// system functions	
            			WorkflowFunctionContext wfFunctionContext = new WorkflowFunctionContext();
                    	wfFunctionContext.setFunctionName(functionCall.VAR().getText());
                    	
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
                    	else if(value.asObject() instanceof DateRange) {
                    		wfFunctionContext.setNameSpace(FacilioSystemFunctionNameSpace.DATE_RANGE.getName());
                    		isDataTypeSpecificFunction = true;
                    	}
                    	else if(value.asObject() instanceof WorkflowReadingContext) {
                    		wfFunctionContext.setNameSpace(FacilioSystemFunctionNameSpace.READINGS.getName());
                    		isDataTypeSpecificFunction = true;
                    	}
                    	else if(value.asObject() instanceof FacilioField) {
                    		wfFunctionContext.setNameSpace(FacilioSystemFunctionNameSpace.FIELD.getName());
                    		isDataTypeSpecificFunction = true;
                    	}
                    	else if(value.asObject() instanceof ConnectionContext) {
                    		wfFunctionContext.setNameSpace(FacilioSystemFunctionNameSpace.FIELD.getName());
                    		isDataTypeSpecificFunction = true;
                    	}
                    	else if (value.asObject() instanceof FacilioSystemFunctionNameSpace) {
                    		wfFunctionContext.setNameSpace(((FacilioSystemFunctionNameSpace)value.asObject()).getName());
                    	}
                    	
                    	List<Object> paramValues = WorkflowV2Util.getParamList(functionCall,isDataTypeSpecificFunction,this,value);
                    	
                    	Object result = WorkflowUtil.evalSystemFunctions(wfFunctionContext, paramValues);
                    	value = new Value(result); 
            		}
    			}
    			else if (functionCall.OPEN_BRACKET() != null && functionCall.CLOSE_BRACKET() != null) {				// list here

    				if(value.asObject() instanceof List ) {
    		    		Value listValue = this.visit(functionCall.atom());
    		    		WorkflowV2Util.checkForNullAndThrowException(listValue, functionCall.atom().getText());
    		    		Integer index = listValue.asInt();
    		    		return new Value(value.asList().get(index));
    		    	}
    		    	else {
    		    		throw new RuntimeException("var "+ctx.atom().getText()+" is not of list type");
    		    	}
    			}
    			else {
    		    	if(value.asObject() instanceof Map ) {
    		    		Map<Object, Object> map = value.asMap();
    		    		
    		    		Object currentValue = map.get(functionCall.VAR().getText());
    		    		value = new Value(currentValue);
    		    	}
    		    	else {
    		    		throw new RuntimeException("not a map " + ctx.atom().getText());
    		    	}
    			}
    		}
    		return value;
    	}
    	catch(Exception e) {
    		LOGGER.log(Level.SEVERE, e.getMessage(), e);
    		workflowContext.getLogStringBuilder().append("ERROR ::: "+e.getMessage()+"\n");
    		if(e.getCause() != null) {
    			workflowContext.getLogStringBuilder().append("ERROR ::: "+e.getCause()+"\n");
    		}
    		throw new RuntimeException(e.getCause());
    	}
    }
    
    @Override
    public Value visitAssignment(WorkflowV2Parser.AssignmentContext ctx) {
    	
    	String varName = ctx.VAR().getText();
    	
    	if (ctx.expr(1) != null) {
        	
        	Value parentValue = varMemoryMap.get(varName);
        	
        	if(parentValue.asObject() instanceof List) {
        		
        		Value index = this.visit(ctx.expr(0));
        		
        		WorkflowV2Util.checkForNullAndThrowException(index, ctx.expr(0).getText());
        		
        		Value value = this.visit(ctx.expr(1));
        		
        		parentValue.asList().add(index.asInt(), value.asObject());
        	}
        	else if (parentValue.asObject() instanceof Map) {
        		Value key = this.visit(ctx.expr(0));
        		WorkflowV2Util.checkForNullAndThrowException(key, ctx.expr(0).getText());
        		Value value = this.visit(ctx.expr(1));
        		parentValue.asMap().put(key.asObject(), value.asObject());
        	}
        }
        else {
        	Value value = this.visit(ctx.expr(0));
        	return varMemoryMap.put(varName, value);
        }
        return Value.VOID;
    }
    
    @Override 
    public Value visitFunction_block(WorkflowV2Parser.Function_blockContext ctx) {
    	
    	String functionName = ctx.function_name_declare().getText();
    	
    	WorkflowFieldType returnType = WorkflowFieldType.getStringvaluemap().get(ctx.data_type().op.getText());
    	
    	workflowContext.setReturnType(returnType.getIntValue());
    	
    	if(workflowContext instanceof WorkflowUserFunctionContext) {
    		((WorkflowUserFunctionContext)workflowContext).setName(functionName);
    	}
    	
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
    public Value visitModuleAndSystemNameSpaceInitialization(WorkflowV2Parser.ModuleAndSystemNameSpaceInitializationContext ctx) {
    	try {
    		String moduleDisplayName = ctx.VAR(0).getText();
        	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        	FacilioModule module = modBean.getModule(WorkflowV2Util.getModuleName(moduleDisplayName));
        	if(module == null) {
        		String nameSpaceString = ctx.VAR(0).getText();
            	FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.getFacilioDefaultFunction(nameSpaceString);
            	if(nameSpaceEnum == null) {
            		throw new RuntimeException("No Module Or System NameSpace With this Name -> "+moduleDisplayName);
            	}
            	return new Value(nameSpaceEnum); 
        	}
        	else {
        		return new Value(module);
        	}
    	}
    	catch(Exception e) {
    		throw new RuntimeException(e.getMessage());
    	}
    }
    
    @Override 
    public Value visitConnectionInitialization(WorkflowV2Parser.ConnectionInitializationContext ctx) { 
    	
    	
    	try {
    		Value connectionNameValue = this.visit(ctx.expr());
    		
    		WorkflowV2Util.checkForNullAndThrowException(connectionNameValue, ctx.expr().getText());
    		
    		String connectionName = connectionNameValue.asString();
        	ConnectionContext connection = ConnectionUtil.getConnection(connectionName);
        	if(connection == null) {
        		throw new RuntimeException("Connection "+connection+ " Does not exist");
        	}
        	return new Value(connection); 
    	}
    	catch(Exception e) {
    		throw new RuntimeException(e.getMessage());
    	}
    
    }
    
    @Override 
    public Value visitReadingInitialization(WorkflowV2Parser.ReadingInitializationContext ctx) 
    {
    	Value fieldValue = this.visit(ctx.expr(0));
    	WorkflowV2Util.checkForNullAndThrowException(fieldValue, ctx.expr(0).getText());
    	Value parentValue = this.visit(ctx.expr(1));
    	WorkflowV2Util.checkForNullAndThrowException(parentValue, ctx.expr(1).getText());
    	return new Value(new WorkflowReadingContext(fieldValue.asLong(),parentValue.asLong())); 
    }
    
    @Override 
    public Value visitCustomModuleInitialization(WorkflowV2Parser.CustomModuleInitializationContext ctx) {
    	try {
    		Value moduleNameValue = this.visit(ctx.expr());
    		
    		WorkflowV2Util.checkForNullAndThrowException(moduleNameValue, ctx.expr().getText());
    		
    		String moduleName = moduleNameValue.asString();
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
    public Value visitNameSpaceInitialization(WorkflowV2Parser.NameSpaceInitializationContext ctx) {
    	try {
    		Value nameSpaceValue = this.visit(ctx.expr());
    		WorkflowV2Util.checkForNullAndThrowException(nameSpaceValue, ctx.expr().getText());
        	FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.getFacilioDefaultFunction(nameSpaceValue.asString());
        	if(nameSpaceEnum == null) {
        		WorkflowNamespaceContext namespace = UserFunctionAPI.getNameSpace(nameSpaceValue.asString());
        		if(namespace == null) {
        			throw new RuntimeException("No such namespace - "+nameSpaceValue.asString());
        		}
        		return new Value(namespace);
        	}
        	return new Value(nameSpaceEnum); 
    	}
    	catch(Exception e) {
    		throw new RuntimeException(e);
    	}
    	
    }
    

    @Override
    public Value visitUnaryMinusExpr(WorkflowV2Parser.UnaryMinusExprContext ctx) {
        Value value = this.visit(ctx.expr());
        return new Value(-value.asDouble());
    }
    
    @Override
    public Value visitNotExpr(WorkflowV2Parser.NotExprContext ctx) {
        Value value = this.visit(ctx.expr());
        return new Value(!value.asBoolean());
    }
    
    @Override
    public Value visitArithmeticFirstPrecedenceExpr(WorkflowV2Parser.ArithmeticFirstPrecedenceExprContext ctx) {

        Value left = this.visit(ctx.expr(0));
        Value right = this.visit(ctx.expr(1));

        switch (ctx.op.getType()) {
            case WorkflowV2Parser.MULT:
                return new Value(left.asDouble() * right.asDouble());
            case WorkflowV2Parser.DIV:
                return new Value(left.asDouble() / right.asDouble());
            case WorkflowV2Parser.MOD:
                return new Value(left.asDouble() % right.asDouble());
            default:
                throw new RuntimeException("unknown operator: " + WorkflowV2Parser.tokenNames[ctx.op.getType()]);
        }
    }
    
    @Override
    public Value visitArithmeticSecondPrecedenceExpr(WorkflowV2Parser.ArithmeticSecondPrecedenceExprContext ctx) {

        Value left = this.visit(ctx.expr(0));
        Value right = this.visit(ctx.expr(1));

        switch (ctx.op.getType()) {
            case WorkflowV2Parser.PLUS:
                return left.isNumber() && right.isNumber() ?
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
        workflowContext.getLogStringBuilder().append(value.asString()+"\n");
        return value;
    }
    
    Criteria criteria = null;
    
    @Override 
    public Value visitCondition_atom(WorkflowV2Parser.Condition_atomContext ctx) {
    	
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
    		else if(operatorValue.asObject() instanceof DateRange) {
    			operator = DateOperators.BETWEEN;
    		}
    		else {
    			operator = NumberOperators.EQUALS;
    		}
    		break;
    	case "!=" :
    		if(operatorValue.asObject() instanceof String) {
    			operator = StringOperators.ISN_T;
    		}
    		else {
    			operator = NumberOperators.NOT_EQUALS;
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
    	
    	criteria.setPattern(criteria.getPattern().replaceFirst(Pattern.quote(ctx.getText()), String.valueOf(seq)));
    	
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
			Value fieldValue = this.visit(ctx.db_param_field(0).expr());
			WorkflowV2Util.checkForNullAndThrowException(fieldValue, ctx.db_param_field(0).expr().getText());
			dbParamContext.setFieldName(fieldValue.asString());
		}
		if(ctx.db_param_aggr(0) != null) {
			Value aggrValue = this.visit(ctx.db_param_aggr(0).expr());
			WorkflowV2Util.checkForNullAndThrowException(aggrValue, ctx.db_param_aggr(0).expr().getText());
			dbParamContext.setAggregateString(aggrValue.asString());
		}
		if(ctx.db_param_limit(0) != null) {
			Value limitValue = this.visit(ctx.db_param_limit(0).expr());
			WorkflowV2Util.checkForNullAndThrowException(limitValue, ctx.db_param_limit(0).expr().getText());
			dbParamContext.setLimit(limitValue.asInt());
		}
		if(ctx.db_param_range(0) != null) {
			Value fromValue = this.visit(ctx.db_param_range(0).expr(0));
			WorkflowV2Util.checkForNullAndThrowException(fromValue, ctx.db_param_range(0).expr(0).getText());
			Value toValue = this.visit(ctx.db_param_range(0).expr(1));
			WorkflowV2Util.checkForNullAndThrowException(toValue, ctx.db_param_range(0).expr(1).getText());
			dbParamContext.setRange(Pair.of(fromValue.asInt(), toValue.asInt()));
		}
		if(ctx.db_param_group_by(0) != null) {
			Value fieldValue = this.visit(ctx.db_param_group_by(0).expr());
			WorkflowV2Util.checkForNullAndThrowException(fieldValue, ctx.db_param_group_by(0).expr().getText());
			dbParamContext.setGroupBy(fieldValue.asString());
		}
		if(ctx.db_param_sort(0) != null) {
			Value sortByField = this.visit(ctx.db_param_sort(0).expr());
			WorkflowV2Util.checkForNullAndThrowException(sortByField, ctx.db_param_sort(0).expr().getText());
			dbParamContext.setSortByFieldName(sortByField.asString());
			dbParamContext.setSortOrder(ctx.db_param_sort(0).op.getText());
		}
		
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
    	WorkflowV2Util.checkForNullAndThrowException(exprValue, ctx.expr().getText());
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
    	Value returnValue = Value.VOID;
    	if(workflowContext.getReturnTypeEnum() != null) {
    		switch(workflowContext.getReturnTypeEnum()) {
    		case VOID:
    			throw new RuntimeException("Method Return Type is Void But has a Return Statement");
    		
    		default:
    			returnValue = this.visit(ctx.expr());
    			
    			boolean correctDataTypeSpecified = false;
    			if(returnValue == null || returnValue.asObject() == null) {
    				correctDataTypeSpecified = true;
    			}
    			else {
    				Class[] ObjectClass = workflowContext.getReturnTypeEnum().getObjectClass();
    				for(int i=0;i<ObjectClass.length;i++) {
        				if(returnValue.asObject().getClass().equals(ObjectClass[i])) {
        					correctDataTypeSpecified = true;
        					break;
        				}
        			}
    			}
    			
    			if(!correctDataTypeSpecified) {
    				throw new RuntimeException("Method Return Type is "+workflowContext.getReturnTypeEnum().getStringValue()+" But has a Return Value of "+returnValue.asObject().getClass());
    			}
    		}
    	}
    	
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
