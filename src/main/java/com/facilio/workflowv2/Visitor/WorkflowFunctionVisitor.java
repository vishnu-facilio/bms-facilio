package com.facilio.workflowv2.Visitor;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.facilio.scriptengine.systemfunctions.FacilioDBFunction;
import com.facilio.scriptengine.systemfunctions.FacilioDatabaseConnection;

import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BusinessHoursContext;
import com.facilio.bmsconsole.context.ConnectionContext;
import com.facilio.bmsconsole.util.ConnectionUtil;
import com.facilio.date.calenderandclock.CalenderAndClockContext;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.FieldOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.scriptengine.autogens.WorkflowV2Parser;
import com.facilio.scriptengine.autogens.WorkflowV2Parser.Recursive_expressionContext;
import com.facilio.scriptengine.context.DBParamContext;
import com.facilio.scriptengine.context.Value;
import com.facilio.scriptengine.context.WorkflowFunctionContext;
import com.facilio.scriptengine.context.WorkflowNamespaceContext;
import com.facilio.scriptengine.context.WorkflowReadingContext;
import com.facilio.scriptengine.systemfunctions.FacilioSystemFunctionNameSpace;
import com.facilio.scriptengine.util.ScriptUtil;
import com.facilio.scriptengine.visitor.FunctionVisitor;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.time.DateRange;
import com.facilio.v3.context.Constants;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.contexts.WorkflowCategoryReadingContext;
import com.facilio.workflowv2.contexts.WorkflowDataParent;
import com.facilio.workflowv2.contexts.WorkflowModuleDataContext;
import com.facilio.workflowv2.scope.Workflow_Scope;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.facilio.workflowv2.util.WorkflowV2Util;
import com.facilio.xml.builder.XMLBuilder;

import lombok.extern.log4j.Log4j;

@Log4j
public class WorkflowFunctionVisitor extends FunctionVisitor<Value> {

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

	
	public void setScope(Workflow_Scope scope) throws Exception {
		this.scope = scope;
	}
	
	public Workflow_Scope getScope() {
		return scope;
	}
	
    @Override 
    public Value visitRecursive_expr(WorkflowV2Parser.Recursive_exprContext ctx) {
    	try {
    		
    		Value value = this.visit(ctx.atom());
    		
    		boolean ifScopeSetted = false;
    		if(value.asObject() == null) {
    			String scopeString = ctx.atom().getText();
    	    	
    	    	Workflow_Scope tempScope = Workflow_Scope.getNameMap().get(scopeString);
    	    	
    	    	if(tempScope != null) {
    	    		setTempScope(tempScope);
    	    		ifScopeSetted = true;
    	    	}
    		}
    		if(!ifScopeSetted) {
    			ScriptUtil.checkForNullAndThrowException(value, ctx.atom().getText());
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
        				
        				Object moduleFunctionObject = ScriptUtil.getModuleInstanceOf(module);
            			Method method = moduleFunctionObject.getClass().getMethod(functionName, Map.class,List.class);
            			
            			List<Object> params = ScriptUtil.getParamList(functionCall,true,this,value);
            			
            			Object result = method.invoke(moduleFunctionObject, getGlobalParam(),params);
            			value =  new Value(result);
                	}
    				else if(value.asObject() instanceof WorkflowModuleDataContext) {
    					
    					WorkflowModuleDataContext moduleDataContext = (WorkflowModuleDataContext) value.asObject();
    					DBParamContext dbParam = moduleDataContext.getDbParam();
    					
    					List<Object> params = ScriptUtil.getParamList(functionCall,true,this,new Value(moduleDataContext.getModule()));
    					if(dbParam != null) {
    						params.add(1, dbParam);
    					}
    					
    					FacilioModule module = moduleDataContext.getModule();
        				
        				Object moduleFunctionObject = ScriptUtil.getModuleInstanceOf(module);
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
            			List<Object> paramValues = ScriptUtil.getParamList(functionCall,false,this,null);
            			WorkflowContext wfContext = null;
            			if(AccountUtil.isFeatureEnabled(FeatureLicense.FETCH_SCRIPT_FROM_CACHE)) {
            				wfContext = Constants.getScriptBean().getFunction(namespaceContext.getName(), functionCall.VAR().getText());
            			}
            			else {
            				wfContext = UserFunctionAPI.getWorkflowFunction(namespaceContext.getId(), functionCall.VAR().getText());
            			}
            			if(wfContext == null) {
                			throw new RuntimeException("No such function - "+functionCall.VAR().getText());
                		}
            			wfContext.setParams(paramValues);
            			wfContext.setGlobalParameters(getGlobalParam());
            			wfContext.setLogStringBuilder(this.getScriptContext().getLogStringBuilder());
            			
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
                    	else if(value.asObject() instanceof BusinessHoursContext) {
                    		wfFunctionContext.setNameSpace(FacilioSystemFunctionNameSpace.BUSINESS_HOUR.getName());
                    		isDataTypeSpecificFunction = true;
                    	}
                    	else if(value.asObject() instanceof XMLBuilder) {
                    		wfFunctionContext.setNameSpace(FacilioSystemFunctionNameSpace.XML_BUILDER.getName());
                    		isDataTypeSpecificFunction = true;
                    	}
                    	else if(value.asObject() instanceof Pattern) {
                    		wfFunctionContext.setNameSpace(FacilioSystemFunctionNameSpace.REGEX.getName());
                    		isDataTypeSpecificFunction = true;
                    	}
                    	else if (value.asObject() instanceof FacilioSystemFunctionNameSpace) {
                    		wfFunctionContext.setNameSpace(((FacilioSystemFunctionNameSpace)value.asObject()).getName());
                    	}
						else if(value.asObject() instanceof FacilioDatabaseConnection){
							wfFunctionContext.setNameSpace(((FacilioSystemFunctionNameSpace.DATABASE_CONNECTION.getName())));
							isDataTypeSpecificFunction = true;
						}
                    	
                    	List<Object> paramValues = ScriptUtil.getParamList(functionCall,isDataTypeSpecificFunction,this,value);
                    	
                    	Object result = WorkflowUtil.evalSystemFunctions(getGlobalParam(),wfFunctionContext, paramValues);
                    	value = new Value(result); 
            		}
    			}
    			else if (functionCall.OPEN_BRACKET() != null && functionCall.CLOSE_BRACKET() != null) {				// list here

    				if(value.asObject() instanceof List ) {
    		    		Value listValue = this.visit(functionCall.atom());
    		    		ScriptUtil.checkForNullAndThrowException(listValue, functionCall.atom().getText());
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
//    		LOGGER.log(Level.SEVERE, e.getMessage(), e);
//    		workflowContext.getLogStringBuilder().append("ERROR ::: "+e.getMessage()+"\n");
//    		if(e.getCause() != null) {
//    			workflowContext.getLogStringBuilder().append("ERROR ::: "+e.getCause()+"\n");
//    		}
			if (e.getCause()!= null){
				throw new RuntimeException(e.getCause());
			}else {
				throw new RuntimeException(e);
			}
    	}
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

    @Override
    public Value visitNewKeywordIntitialization(WorkflowV2Parser.NewKeywordIntitializationContext ctx) {
    	try {
    		String newObject = ctx.VAR().toString();
        	
        	switch(newObject) {
        		case WorkflowV2Util.NEW_NAMESPACE_INITIALIZATION :
        			Value nameSpaceName = this.visit(ctx.expr(0));
        			
        			ScriptUtil.checkForNullAndThrowException(nameSpaceName, ctx.expr(0).getText());
        			
        			FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.getFacilioDefaultFunction(nameSpaceName.asString());
                	if(nameSpaceEnum == null) {
                		WorkflowNamespaceContext namespace = null;
                		if(AccountUtil.isFeatureEnabled(FeatureLicense.FETCH_SCRIPT_FROM_CACHE)) {
                			namespace = Constants.getScriptBean().getNameSpace(nameSpaceName.asString());
                		}
                		else {
                			namespace = UserFunctionAPI.getNameSpace(nameSpaceName.asString());
                		}
                		if(namespace == null) {
                			throw new RuntimeException("No such namespace - "+nameSpaceName.asString());
                		}
                		return new Value(namespace);
                	}
                	return new Value(nameSpaceEnum); 
        		case WorkflowV2Util.NEW_CONNECTION_INITIALIZATION :
					Value connectionNameValue = this.visit(ctx.expr(0));
		    		
		    		ScriptUtil.checkForNullAndThrowException(connectionNameValue, ctx.expr(0).getText());
		    		
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
    public Value visitCondition_atom(WorkflowV2Parser.Condition_atomContext ctx) {
    	
		Criteria currentCriteria = criteria != null ? criteria : fieldCriteria;
		
    	Condition condition = new Condition();
    	condition.setFieldName(ctx.VAR().getText());
    	
    	Operator operator = null;
    	
    	Value operatorValue = this.visit(ctx.expr());
    	switch(ctx.op.getText()) {
    	case "==" :
    		if(operatorValue.asObject() == null) {
    			operator = CommonOperators.IS_EMPTY;
    		}
    		else if(operatorValue.asObject() instanceof String) {
    			operator = StringOperators.IS;
    		}
    		else if(operatorValue.asObject() instanceof Boolean) {
    			operator = BooleanOperators.IS;
    		}
    		else if(operatorValue.asObject() instanceof DateRange) {
    			operator = DateOperators.BETWEEN;
    		}
    		else if(operatorValue.asObject() instanceof CalenderAndClockContext) {
    			operator = DateOperators.CALENDER_AND_CLOCK;
    		}
    		else if (operatorValue.asObject() instanceof List) {
    			operator = StringOperators.IS;
    		}
    		else if (operatorValue.asObject() instanceof FacilioField) {
    			operator = FieldOperator.EQUAL;
    		}
    		else if (operatorValue.asObject() instanceof BaseSpaceContext) {
    			operator = BuildingOperator.BUILDING_IS;
    		}
    		else {
    			operator = NumberOperators.EQUALS;
    		}
    		break;
    	case "!=" :
    		if(operatorValue.asObject() == null) {
    			operator = CommonOperators.IS_NOT_EMPTY;
    		}
    		else if(operatorValue.asObject() instanceof String) {
    			operator = StringOperators.ISN_T;
    		}
    		else if (operatorValue.asObject() instanceof List) {
    			operator = StringOperators.ISN_T;
    		}
    		else if (operatorValue.asObject() instanceof FacilioField) {
    			operator = FieldOperator.NOT_EQUAL;
    		}
    		else {
    			operator = NumberOperators.NOT_EQUALS;
    		}
    		break;
    	default:
    		if (operatorValue.asObject() instanceof FacilioField) {
    			operator = FieldOperator.getAllOperators().get(ctx.op.getText());
    		}
    		else {
    			operator = NumberOperators.getAllOperators().get(ctx.op.getText());
    		}
    		break;
    	}
    	condition.setOperator(operator);
    	
    	String value = operatorValue.asString();
    	
    	if (operatorValue.asObject() instanceof List) {
    		value = StringUtils.join(operatorValue.asList(), ",");
		}
    	else if (operatorValue.asObject() instanceof FacilioField) {
    		FacilioField field = operatorValue.asField();
    		value = field.getModule().getName()+"."+field.getName();
		}
    	else if (operatorValue.asObject() instanceof BaseSpaceContext) {
    		BaseSpaceContext baseSpace = (BaseSpaceContext) operatorValue.asObject() ;
    		value = ""+baseSpace.getId();
		}
    	else if (operatorValue.asObject() instanceof CalenderAndClockContext) {
    		CalenderAndClockContext calenderAndClock = (CalenderAndClockContext) operatorValue.asObject();
    		value = calenderAndClock.getName()+"."+calenderAndClock.getValue()+"."+getCurrentExecutionTime();
    		//value = calenderAndClock.getFullName()+"."+getCurrentExecutionTime();
		}
    	else if (operatorValue.asObject() instanceof String) {
    		value = operatorValue.asString();
    		value = value.replace(",", StringOperators.DELIMITED_COMMA);
		}
    	
    	condition.setValue(value);
    	
    	int seq = currentCriteria.addConditionMap(condition);
    	
    	currentCriteria.setPattern(currentCriteria.getPattern().replaceFirst(Pattern.quote(ctx.getText()), String.valueOf(seq)));
    	
    	return visitChildren(ctx); 
    }
    

}