package com.facilio.workflowv2.Visitor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.bmsconsole.util.ConnectionUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.time.DateRange;
import com.facilio.workflows.context.ParameterContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowFieldType;
import com.facilio.workflows.context.WorkflowFunctionContext;
import com.facilio.workflows.context.WorkflowUserFunctionContext;
import com.facilio.workflows.functions.FacilioSystemFunctionNameSpace;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.autogens.WorkflowV2Parser;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.AtomContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Catch_statementContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Function_paramContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Recursive_expressionContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Try_catchContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Try_statementContext;
import com.facilio.workflowv2.contexts.DBParamContext;
import com.facilio.workflowv2.contexts.Value;
import com.facilio.workflowv2.contexts.WorkflowCategoryReadingContext;
import com.facilio.workflowv2.contexts.WorkflowDataParent;
import com.facilio.workflowv2.contexts.WorkflowModuleDataContext;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;
import com.facilio.workflowv2.contexts.WorkflowReadingContext;
import com.facilio.workflowv2.scope.Workflow_Scope;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.facilio.workflowv2.util.WorkflowV2TypeUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

public class WorkflowFunctionVisitor extends CommonParser<Value> {
	
	private static final Logger LOGGER = Logger.getLogger(WorkflowFunctionVisitor.class.getName());

    private Map<String, Value> varMemoryMap = new HashMap<String, Value>();
    
    private Map<String, Object> globalVarMemoryMap = new HashMap<String, Object>();		// keeping this as <String, Object> since we have to move this to all sub functions;
    
    Workflow_Scope scope = Workflow_Scope.DEFAULT;
    
    Workflow_Scope tempScope = null;
    
    public Workflow_Scope getTempScope() {
		return tempScope;
	}

	public void setTempScope(Workflow_Scope tempScope) {
		this.tempScope = tempScope;
	}
	
	public Workflow_Scope getCurrentScope() {
		if(getTempScope() != null) {
			Workflow_Scope temp = getTempScope();
			setTempScope(null);
			return temp;
		}
		return getScope();
	}

	private void putParamValue(String key,Value value) {
    	if(key != null) {
    		varMemoryMap.put(key, value);
    	}
    }
    
    private Value getParamValue(String key) {
    	if(varMemoryMap.containsKey(key)) {
    		return varMemoryMap.get(key);
    	}
    	else if (globalVarMemoryMap.containsKey(key)) {
    		return new Value(globalVarMemoryMap.get(key));
    	}
    	return null;
    }
    
    private Value removeParamValue(String key) {
    	if(varMemoryMap.containsKey(key)) {
    		return varMemoryMap.remove(key);
    	}
    	return null;
    }
    
    WorkflowContext workflowContext;
    
    public WorkflowContext getWorkflowContext() {
		return workflowContext;
	}

	public void setWorkflowContext(WorkflowContext workflowContext) {
		this.workflowContext = workflowContext;
	}
	
	public void setScope(Workflow_Scope scope) throws Exception {
		this.scope = scope;
	}
	
	public Workflow_Scope getScope() {
		return scope;
	}

	boolean breakCodeFlow;
    boolean isFunctionHeaderVisitor;
    public void setParams(List<Object> parmasObjects) throws Exception {
    	if(parmasObjects != null && parmasObjects.size() > 0) {
        	
        	for(int i = 0;i<parmasObjects.size(); i++) {
        		ParameterContext param = workflowContext.getParameters().get(i);
        		Object value = parmasObjects.get(i);
        		putParamValue(param.getName(), new Value(value));
        	}
    	}
    }
    
    public void setGlobalParams(Map<String, Object> globalParameters) throws Exception {
    	if(globalParameters != null) {
    		globalVarMemoryMap = globalParameters;
    	}
    }
    
    public Map<String, Object> getGlobalParam() {
    	return globalVarMemoryMap;
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
    public Value visitListInitialisationWithElements(WorkflowV2Parser.ListInitialisationWithElementsContext ctx) {
    	
    	List<Object> objects = new ArrayList<>();
    	
    	for (AtomContext atom : ctx.atom()) {
    		
    		Value value = this.visit(atom);
    		
    		objects.add(value.asObject());
    	}
    	
    	return new Value(objects);
    }
    
    @Override 
    public Value visitRecursive_expr(WorkflowV2Parser.Recursive_exprContext ctx) {
    	try {
    		
    		Value value = this.visit(ctx.atom());
    		
    		boolean ifScopeSetted = false;
    		if(value.asObject() == null) {													// on error command this if
    			String scopeString = ctx.atom().getText();
    	    	
    	    	Workflow_Scope tempScope = Workflow_Scope.getNameMap().get(scopeString);
    	    	
    	    	if(tempScope != null) {
    	    		setTempScope(tempScope);
    	    		ifScopeSetted = true;
    	    	}
    		}
    		if(!ifScopeSetted) {
    			WorkflowV2Util.checkForNullAndThrowException(value, ctx.atom().getText());
    		}
    		
    		for(int i=0;i<ctx.recursive_expression().size();i++) {
    			
    			Recursive_expressionContext functionCall = ctx.recursive_expression(i);
    			
    			if(ifScopeSetted) {
    				
    				String moduleName = functionCall.VAR().getText();
    				
    				Object scopedObject = getCurrentScope().getObject(moduleName, this, functionCall.expr());
    	    		if(scopedObject != null) {
    	    			value = new Value(scopedObject); 
    	    		}
    	    		else {
    	    			throw new Exception("no scoped object found - "+moduleName);
    	    		}
    				ifScopeSetted = false;
    				continue;
    			}
    			
    			if(value.asObject() instanceof WorkflowDataParent) {
    				
    				WorkflowDataParent workflowData = (WorkflowDataParent) value.asObject();
    				
    				boolean fetch = false;
    				boolean continueOnFetch = false;
    				
    				if(functionCall.OPEN_PARANTHESIS() == null && functionCall.CLOSE_PARANTHESIS() == null && functionCall.OPEN_BRACKET() == null && functionCall.CLOSE_BRACKET() ==  null) {
    					String varName = functionCall.VAR().getText();
    					
    					DBParamContext dbParam = workflowData.getDbParam();
    					
    					if(i == ctx.recursive_expression().size()-1) {
        					fetch = true;
        					continueOnFetch = true;
        				}
    					
    					if(dbParam.getFieldName() == null) {
    						dbParam.setFieldName(varName);
    						if(!fetch) {
    							continue;
    						}
    					}
    					else if (dbParam.getAggregateString() == null) {
    						dbParam.setAggregateString(varName);
    						if(!fetch) {
    							continue;
    						}
    					}
    					else {
    						fetch = true;
    					}
    				}
    				else if(functionCall.OPEN_PARANTHESIS() != null && functionCall.CLOSE_PARANTHESIS() != null) {
    					
    					String varName = functionCall.VAR().getText();
    					
    					if(varName.equals(WorkflowV2Util.WORKFLOW_WHERE_STRING)) {
    						
    						Value criteriaValue = this.visit(functionCall.expr(0));
    						
    						workflowData.getDbParam().addAndCriteria(criteriaValue.asCriteria());
    						
    						fetch =true;
    						continueOnFetch =true;
    					}
    					else {
    						if(dbParamContext == null || dbParamContext.getFieldName() == null) {
    							fetch = false;
    						}
    						else {
    							fetch = true;
    						}
    					}
    				}
    				
    				if(fetch) {
    					Object result = workflowData.fetchResult(this);
    	    			
    	    			value = new Value(result);
    					
    					if(continueOnFetch) {
    						continue;
    					}
    				}
    			}
				if(functionCall.OPEN_PARANTHESIS() != null && functionCall.CLOSE_PARANTHESIS() != null) {
    				
    				String functionName = functionCall.VAR().getText();
    				if (value.asObject() instanceof FacilioModule) {									// module Functions
        				FacilioModule module = (FacilioModule) value.asObject();
        				
        				Object moduleFunctionObject = WorkflowV2Util.getInstanceOf(module);
            			Method method = moduleFunctionObject.getClass().getMethod(functionName, Map.class,List.class);
            			
            			List<Object> params = WorkflowV2Util.getParamList(functionCall,true,this,value);
            			
            			Object result = method.invoke(moduleFunctionObject, getGlobalParam(),params);
            			value =  new Value(result);
                	}
    				else if(value.asObject() instanceof WorkflowModuleDataContext) {
    					
    					WorkflowModuleDataContext moduleDataContext = (WorkflowModuleDataContext) value.asObject();
    					DBParamContext dbParam = moduleDataContext.getDbParam();
    					
    					List<Object> params = WorkflowV2Util.getParamList(functionCall,true,this,new Value(moduleDataContext.getModule()));
    					if(dbParam != null) {
    						params.add(1, dbParam);
    					}
    					
    					FacilioModule module = moduleDataContext.getModule();
        				
        				Object moduleFunctionObject = WorkflowV2Util.getInstanceOf(module);
            			Method method = moduleFunctionObject.getClass().getMethod(functionName, Map.class,List.class);
            			
            			Object result = method.invoke(moduleFunctionObject, getGlobalParam(),params);
            			
            			if(moduleDataContext.isSingleParent() && result instanceof List) {
            				List<Object> resultList = (List<Object>) result;
            				result = resultList.get(0);
            			}
            			value =  new Value(result);
    					
    				}
            		else if (value.asObject() instanceof WorkflowNamespaceContext) {					// user defined functions
            			
            			WorkflowNamespaceContext namespaceContext = (WorkflowNamespaceContext) value.asObject();
            			List<Object> paramValues = WorkflowV2Util.getParamList(functionCall,false,this,null);
            			
            			WorkflowContext wfContext = UserFunctionAPI.getWorkflowFunction(namespaceContext.getId(), functionCall.VAR().getText());
            			wfContext.setParams(paramValues);
            			wfContext.setGlobalParameters(getGlobalParam());
            			
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
                    	else if(value.asObject() instanceof WorkflowCategoryReadingContext) {
                    		wfFunctionContext.setNameSpace(FacilioSystemFunctionNameSpace.WORKFLOW_READINGS.getName());
                    		isDataTypeSpecificFunction = true;
                    	}
                    	else if(value.asObject() instanceof FacilioField) {
                    		wfFunctionContext.setNameSpace(FacilioSystemFunctionNameSpace.FIELD.getName());
                    		isDataTypeSpecificFunction = true;
                    	}
                    	else if(value.asObject() instanceof ConnectionContext) {
                    		wfFunctionContext.setNameSpace(FacilioSystemFunctionNameSpace.CONNECTION.getName());
                    		isDataTypeSpecificFunction = true;
                    	}
                    	else if(value.asObject() instanceof Criteria) {
                    		wfFunctionContext.setNameSpace(FacilioSystemFunctionNameSpace.CRITERIA.getName());
                    		isDataTypeSpecificFunction = true;
                    	}
                    	else if(value.asObject() instanceof Number) {
                    		wfFunctionContext.setNameSpace(FacilioSystemFunctionNameSpace.NUMBER.getName());
                    		isDataTypeSpecificFunction = true;
                    	}
                    	else if(value.asObject() instanceof ScheduleInfo) {
                    		wfFunctionContext.setNameSpace(FacilioSystemFunctionNameSpace.SCHEDULE.getName());
                    		isDataTypeSpecificFunction = true;
                    	}
                    	else if (value.asObject() instanceof FacilioSystemFunctionNameSpace) {
                    		wfFunctionContext.setNameSpace(((FacilioSystemFunctionNameSpace)value.asObject()).getName());
                    	}
                    	
                    	List<Object> paramValues = WorkflowV2Util.getParamList(functionCall,isDataTypeSpecificFunction,this,value);
                    	
                    	Object result = WorkflowUtil.evalSystemFunctions(getGlobalParam(),wfFunctionContext, paramValues);
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
    		    		Map<String, Object> map = value.asMap();
    		    		
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
    public Value visitAssignSingleVar(WorkflowV2Parser.AssignSingleVarContext ctx) { 
    	
    	String varName = ctx.VAR().getText();
    	
    	Value value = assignmentValue;
    	
    	putParamValue(varName, value);
    	
    	assignmentValue = null;
    	
    	return Value.VOID; 
    }
    
    @Override 
    public Value visitAssignSingleBracketVar(WorkflowV2Parser.AssignSingleBracketVarContext ctx) { 
    	
    	String varName = ctx.VAR().getText();
    	
    	Value value = assignmentValue;
    	
    	Value parentValue = getParamValue(varName);
    	
    	if(parentValue.asObject() instanceof List) {
    		
    		Value index = this.visit(ctx.expr());
    		
    		WorkflowV2Util.checkForNullAndThrowException(index, ctx.expr().getText());
    		
    		parentValue.asList().add(index.asInt(), value.asObject());
    	}
    	else if (parentValue.asObject() instanceof Map) {
    		Value key = this.visit(ctx.expr());
    		
    		WorkflowV2Util.checkForNullAndThrowException(key, ctx.expr().getText());
    		
    		parentValue.asMap().put(key.asString(), value.asObject());
    	}
    	assignmentValue = null;
    	return Value.VOID; 
    }
    
    @Override 
    public Value visitAssignMultiDotVar(WorkflowV2Parser.AssignMultiDotVarContext ctx) {
    	
    	boolean isFirst = true;
    	
    	Value value = assignmentValue;
    	
    	Map parentMap = null;
    	int varSize = ctx.VAR().size();					// min value is 2
    	int i = 0;
    	for(TerminalNode var : ctx.VAR()) {
    		
    		i++;
    		
    		String varName = var.getText();
    		
    		if(isFirst) {
    			Value parentValue = getParamValue(varName);
    			if (parentValue == null) {
    				parentValue = new Value(new HashMap<>());
    				putParamValue(varName, parentValue);
    			}
    			
    			if(parentValue.asObject() instanceof Map) {
    				parentMap = parentValue.asMap();
    			}
    			isFirst = false;
    			continue;
    		}
    		
    		if(i == varSize) {
    			parentMap.put(varName, value.asObject());
    		}
    		else {
    			Object currentObject = parentMap.get(varName);
    			
    			Map currentMap = null;
    			if(currentObject instanceof Map) {
    				currentMap = (Map) currentObject;
    			}
    			else {
    				currentMap = new HashMap<>();
    				parentMap.put(varName, currentMap);
    			}
    			parentMap = currentMap;
    		}
    	}
    	
    	assignmentValue = null;
    	
    	return Value.VOID; 
    }
    
    @Override
    public Value visitAssignment(WorkflowV2Parser.AssignmentContext ctx) {
    	
    	
    	Value value = this.visit(ctx.expr());
    	
    	this.assignmentValue = value;
    	this.visit(ctx.assignment_var());
    	
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
    public Value visitTry_catch(Try_catchContext ctx) {
    	
    	List<Try_statementContext> tryStatements = ctx.try_statement();
    	List<Catch_statementContext> catchStatements = ctx.catch_statement();
    	
    	try {
    		if(tryStatements != null) {
    			for(Try_statementContext tryStatement :tryStatements) {
    				visit(tryStatement);
        		}
    		}
    	}
    	catch (Exception e) {
    		if(catchStatements != null) {
    			for(Catch_statementContext catchStatement :catchStatements) {
    				visit(catchStatement);
        		}
    		}
		}
    	return Value.VOID;
    }
    @Override
    public Value visitVarAtom(WorkflowV2Parser.VarAtomContext ctx) {
        String varName = ctx.getText();
        Value value = getParamValue(varName);
        if(value == null) {
            return Value.VOID;
        }
        return value;
    }

    @Override
    public Value visitStringAtom(WorkflowV2Parser.StringAtomContext ctx) {
        String str = ctx.getText();
        // strip quotes
        str = str.substring(1, str.length() - 1).replace("\"\"", "\"");
        str = str.replace("\\\"","\"");
        str = str.replace("\\n","\n");
        str = str.replace("\\r","\r");
        return new Value(str);
    }

    @Override
    public Value visitNumberAtom(WorkflowV2Parser.NumberAtomContext ctx) {
    	if(ctx.getText().contains(".")) {
    		return new Value(Double.valueOf(ctx.getText()));
    	}
        return new Value(Long.valueOf(ctx.getText()));
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
    		String moduleDisplayName = ctx.VAR().getText();

    		Object scopedObject = getCurrentScope().getObject(moduleDisplayName, this, ctx.expr());
    		if(scopedObject != null) {
    			return new Value(scopedObject); 
    		}
    		String nameSpaceString = ctx.VAR().getText();
        	FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.getFacilioDefaultFunction(nameSpaceString);
        	if(nameSpaceEnum == null) {
        		throw new RuntimeException("No Module Or System NameSpace With this Name -> "+moduleDisplayName);
        	}
        	return new Value(nameSpaceEnum); 
    	}
    	catch(Exception e) {
    		throw new RuntimeException(e.getMessage());
    	}
    }
    
    // on error command the above mthod and open the bellow one
    
//    public Value visitModuleAndSystemNameSpaceInitialization(WorkflowV2Parser.ModuleAndSystemNameSpaceInitializationContext ctx) {
//    	try {
//    		String moduleDisplayName = ctx.VAR().getText();
//        	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//        	FacilioModule module = modBean.getModule(WorkflowV2Util.getModuleName(moduleDisplayName));
//        	if(module == null) {
//        		String nameSpaceString = ctx.VAR().getText();
//            	FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.getFacilioDefaultFunction(nameSpaceString);
//            	if(nameSpaceEnum == null) {
//            		throw new RuntimeException("No Module Or System NameSpace With this Name -> "+moduleDisplayName);
//            	}
//            	return new Value(nameSpaceEnum); 
//        	}
//        	else {
//        		return new Value(module);
//        	}
//    	}
//    	catch(Exception e) {
//    		throw new RuntimeException(e.getMessage());
//    	}
//    }
    
//    @Override 
//    public Value visitConnectionInitialization(WorkflowV2Parser.ConnectionInitializationContext ctx) { 
//    	
//    	
//    	try {
//    		Value connectionNameValue = this.visit(ctx.expr());
//    		
//    		WorkflowV2Util.checkForNullAndThrowException(connectionNameValue, ctx.expr().getText());
//    		
//    		String connectionName = connectionNameValue.asString();
//        	ConnectionContext connection = ConnectionUtil.getConnection(connectionName);
//        	if(connection == null) {
//        		throw new RuntimeException("Connection "+connection+ " Does not exist");
//        	}
//        	return new Value(connection); 
//    	}
//    	catch(Exception e) {
//    		throw new RuntimeException(e.getMessage());
//    	}
//    
//    }
    
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
    
//    @Override 
//    public Value visitNameSpaceInitialization(WorkflowV2Parser.NameSpaceInitializationContext ctx) {
//    	try {
//    		Value nameSpaceValue = this.visit(ctx.expr());
//    		WorkflowV2Util.checkForNullAndThrowException(nameSpaceValue, ctx.expr().getText());
//        	FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.getFacilioDefaultFunction(nameSpaceValue.asString());
//        	if(nameSpaceEnum == null) {
//        		WorkflowNamespaceContext namespace = UserFunctionAPI.getNameSpace(nameSpaceValue.asString());
//        		if(namespace == null) {
//        			throw new RuntimeException("No such namespace - "+nameSpaceValue.asString());
//        		}
//        		return new Value(namespace);
//        	}
//        	return new Value(nameSpaceEnum); 
//    	}
//    	catch(Exception e) {
//    		throw new RuntimeException(e);
//    	}
//    }
    
    @Override
    public Value visitNewKeywordIntitialization(WorkflowV2Parser.NewKeywordIntitializationContext ctx) {
    	try {
    		String newObject = ctx.VAR().toString();
        	
        	switch(newObject) {
        		case WorkflowV2Util.NEW_NAMESPACE_INITIALIZATION :
        			Value nameSpaceName = this.visit(ctx.expr(0));
        			
        			WorkflowV2Util.checkForNullAndThrowException(nameSpaceName, ctx.expr(0).getText());
        			
        			FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.getFacilioDefaultFunction(nameSpaceName.asString());
                	if(nameSpaceEnum == null) {
                		WorkflowNamespaceContext namespace = UserFunctionAPI.getNameSpace(nameSpaceName.asString());
                		if(namespace == null) {
                			throw new RuntimeException("No such namespace - "+nameSpaceName.asString());
                		}
                		return new Value(namespace);
                	}
                	return new Value(nameSpaceEnum); 
        		case WorkflowV2Util.NEW_CONNECTION_INITIALIZATION :
					Value connectionNameValue = this.visit(ctx.expr(0));
		    		
		    		WorkflowV2Util.checkForNullAndThrowException(connectionNameValue, ctx.expr(0).getText());
		    		
		    		String connectionName = connectionNameValue.asString();
		        	ConnectionContext connection = ConnectionUtil.getConnection(connectionName);
		        	if(connection == null) {
		        		throw new RuntimeException("Connection "+connectionNameValue+ " Does not exist");
		        	}
		        	return new Value(connection); 
        		default:
        			throw new RuntimeException("invalid use of new keyword");
        	}
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
        
        if(left.asObject() == null || right.asObject() == null) {
    		return Value.VOID;
    	}

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
        if(left.asObject() instanceof String || right.asObject() instanceof String) {
        	return new Value(left.asString() + right.asString());
        }
        if(left.asObject() == null || right.asObject() == null) {
    		return Value.VOID;
    	}
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

        Double numericalLeftValue = 0d, numericalRightValue = 0d;
        
        if((ctx.op.getType() == WorkflowV2Parser.LT || ctx.op.getType() == WorkflowV2Parser.LTEQ || ctx.op.getType() == WorkflowV2Parser.GT || ctx.op.getType() == WorkflowV2Parser.GTEQ)) 
        {
        	if(left.asObject() != null && right.asObject() != null && left.asObject() instanceof String && right.asObject() instanceof String) {
				int result = left.asString().compareTo(right.asString());
				if(result > 0) {
					numericalLeftValue = 1d;
				} else if (result < 0) {
					numericalRightValue = 1d;
				}
        	}
        	else {
        		numericalLeftValue = WorkflowV2TypeUtil.assignNumericValuesForComparison(left);
        		numericalRightValue = WorkflowV2TypeUtil.assignNumericValuesForComparison(right);	
        	}
        }
        
        boolean comparisonResult, equalityResult;
        
        switch (ctx.op.getType()) {
            case WorkflowV2Parser.LT:
            	return new Value((numericalLeftValue != null && numericalRightValue != null) ? (numericalLeftValue < numericalRightValue) : false);
                
            case WorkflowV2Parser.LTEQ:	            	
            	comparisonResult = (numericalLeftValue != null && numericalRightValue != null) ? (numericalLeftValue < numericalRightValue) : false;
            	equalityResult = (numericalLeftValue != null && numericalRightValue != null) ? (numericalLeftValue.equals(numericalRightValue)) : WorkflowV2TypeUtil.evaluateEquality(left, right);
            	return new Value(comparisonResult || equalityResult);
            	
            case WorkflowV2Parser.GT:	
            	return new Value((numericalLeftValue != null && numericalRightValue != null) ? (numericalLeftValue > numericalRightValue) : false);
            	
            case WorkflowV2Parser.GTEQ:    	
            	comparisonResult = (numericalLeftValue != null && numericalRightValue != null) ? (numericalLeftValue > numericalRightValue) : false;
            	equalityResult = (numericalLeftValue != null && numericalRightValue != null) ? (numericalLeftValue.equals(numericalRightValue)) : WorkflowV2TypeUtil.evaluateEquality(left, right);
            	return new Value(comparisonResult || equalityResult);
            	
            case WorkflowV2Parser.EQ:           	
            	return new Value(WorkflowV2TypeUtil.evaluateEquality(left, right));
            case WorkflowV2Parser.NEQ:
            	return new Value(!(WorkflowV2TypeUtil.evaluateEquality(left, right)));
            	
            default:
                throw new RuntimeException("Unknown relational operator: " + WorkflowV2Parser.tokenNames[ctx.op.getType()]);
        }
    }
    
    @Override 
    public Value visitBoolean_expr(WorkflowV2Parser.Boolean_exprContext ctx) 
    { 
    	return this.visit(ctx.boolean_expr_atom()); 
    }
    
    @Override 
    public Value visitExprForBoolean(WorkflowV2Parser.ExprForBooleanContext ctx) { 
    	return this.visit(ctx.expr()); 
    }
    
    @Override 
    public Value visitBoolExprParanthesis(WorkflowV2Parser.BoolExprParanthesisContext ctx) 
    { 
    	return this.visit(ctx.boolean_expr_atom()); 
    }
    
	@Override
	public Value visitBooleanExprCalculation(WorkflowV2Parser.BooleanExprCalculationContext ctx) {
		Value left = this.visit(ctx.boolean_expr_atom(0));
		
		switch (ctx.op.getType()) {
		case WorkflowV2Parser.AND:
			if(left.asBoolean()) {
				Value right = this.visit(ctx.boolean_expr_atom(1));
				return new Value(left.asBoolean() && right.asBoolean());
			}
			return new Value(left.asBoolean());
		case WorkflowV2Parser.OR:
			if(!left.asBoolean()) {
				Value right = this.visit(ctx.boolean_expr_atom(1));
				return new Value(left.asBoolean() || right.asBoolean());
			}
			return new Value(left.asBoolean());
		default:
			throw new RuntimeException("unknown operator: " + WorkflowV2Parser.tokenNames[ctx.op.getType()]);
		}
		
	}
	
	
	@Override
	public Value visitBooleanExpr(WorkflowV2Parser.BooleanExprContext ctx) {
		Value left = this.visit(ctx.expr(0));
		Value right = this.visit(ctx.expr(1));
		
		switch (ctx.op.getType()) {
		case WorkflowV2Parser.SINGLE_AND:
				return new Value(left.asBoolean() && right.asBoolean());
		case WorkflowV2Parser.SINGLE_OR:
				return new Value(left.asBoolean() || right.asBoolean());
		default:
			throw new RuntimeException("unknown operator: " + WorkflowV2Parser.tokenNames[ctx.op.getType()]);
		}
	}
    
    @Override
    public Value visitLog(WorkflowV2Parser.LogContext ctx) {
        Value value = this.visit(ctx.expr());
        workflowContext.getLogStringBuilder().append(value.asString()+"\n");
        LOGGER.log(Level.INFO, workflowContext.getId()+" - "+value.asString());
        
        System.out.println(workflowContext.getId()+" - "+value.asString());
        return value;
    }
    
    Value assignmentValue = null;
    
	@Override
    public Value visitIf_statement(WorkflowV2Parser.If_statementContext ctx) {

        List<WorkflowV2Parser.Condition_blockContext> conditions =  ctx.condition_block();

        boolean evaluatedBlock = false;

        for(WorkflowV2Parser.Condition_blockContext condition : conditions) {

            Value evaluated = this.visit(condition.boolean_expr());

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
				putParamValue(loopVariableIndexName, new Value(i));
				putParamValue(loopVariableValueName, new Value(iterateList.get(i)));
				
				this.visit(ctx.statement_block());
			}
			removeParamValue(loopVariableIndexName);
			removeParamValue(loopVariableValueName);
			
		}
		else if(exprValue.asObject() instanceof Map) {
			Map iterateMap = (Map) exprValue.asObject();
			for(Object key :iterateMap.keySet() ) {
				putParamValue(loopVariableIndexName, new Value(key));						// index acts as key for Map Iteration
				putParamValue(loopVariableValueName, new Value(iterateMap.get(key)));
				
				this.visit(ctx.statement_block());
			}
			removeParamValue(loopVariableIndexName);
			removeParamValue(loopVariableValueName);
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