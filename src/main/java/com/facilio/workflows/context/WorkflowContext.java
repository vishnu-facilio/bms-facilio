package com.facilio.workflows.context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringEscapeUtils;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.workflows.util.WorkflowUtil;
import com.udojava.evalex.Expression;

public class WorkflowContext {
	
	private static final Logger LOGGER = Logger.getLogger(WorkflowContext.class.getName());
	
	public static String VARIABLE_PLACE_HOLDER = "\\$\\{.+\\}";

	public Map<String,List<Map<String,Object>>> CACHED_DATA = new HashMap<String, List<Map<String,Object>>>();
	
	public Map<String, List<Map<String, Object>>> getCachedData() {
		return CACHED_DATA;
	}
	public void setCachedData(Map<String, List<Map<String, Object>>> cACHED_DATA) {
		CACHED_DATA = cACHED_DATA;
	}
	
	public void addCachedData(String name, List<Map<String, Object>> data) {
		CACHED_DATA.put(name, data);
	}

	Long id;
	Long orgId;
	String workflowString;
	List<ParameterContext> parameters;
	List<ExpressionContext> expressions;
	Map<String,Object> variableResultMap;
	String resultEvaluator;
	
	boolean isFromDerivation;
	
	public boolean isFromDerivation() {
		return isFromDerivation;
	}
	public void setFromDerivation(boolean isFromDerivation) {
		this.isFromDerivation = isFromDerivation;
	}

	private List<Long> dependentFieldIds;
	public List<Long> getDependentFieldIds() {
		return dependentFieldIds;
	}
	public void setDependentFieldIds(List<Long> dependentFieldIds) {
		this.dependentFieldIds = dependentFieldIds;
	}
	
	private List<FacilioField> dependentFields;
	public List<FacilioField> getDependentFields() {
		return dependentFields;
	}
	public void setDependentFields(List<FacilioField> dependentFields) {
		this.dependentFields = dependentFields;
	}
	
	public Map<String, Object> getVariableResultMap() {
		return variableResultMap;
	}

	public void setVariableResultMap(Map<String, Object> variableToExpresionMap) {
		this.variableResultMap = variableToExpresionMap;
	}
	public void addVariableResultMap(String key,Object value) {
		if(this.variableResultMap == null) {
			this.variableResultMap = new HashMap<>();
		}
		this.variableResultMap.put(key, value);
	}
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getWorkflowString() {
		return workflowString;
	}

	public void setWorkflowString(String workflowString) {
		this.workflowString = workflowString;
	}

	public List<ParameterContext> getParameters() {
		return parameters;
	}

	public void setParameters(List<ParameterContext> parameters) {
		this.parameters = parameters;
	}
	
	public void addParamater(ParameterContext parameter) {
		if(this.parameters == null) {
			this.parameters = new ArrayList<>();
		}
		this.parameters.add(parameter);
	}

	public List<ExpressionContext> getExpressions() {
		return expressions;
	}

	public void setExpressions(List<ExpressionContext> expressions) {
		this.expressions = expressions;
	}
	public void addExpression(ExpressionContext expression) {
		if(expressions == null) {
			expressions = new ArrayList<>();
		}
//		expression.setWorkflowContext(this);
		expressions.add(expression);
	}

	public String getResultEvaluator() {
		return resultEvaluator;
	}

	public void setResultEvaluator(String resultEvaluator) {
		this.resultEvaluator = resultEvaluator;
	}
	
	private Boolean showXml;
	public Boolean getShowXml() {
		return showXml;
	}
	public void setShowXml(Boolean showXml) {
		this.showXml = showXml;
	}
	public Boolean isShowXml() {
		if(showXml != null) {
			return showXml.booleanValue();
		}
		return false;
	}

	public Object executeWorkflow(boolean ignoreNullValues) throws Exception {
		
		Object result = null;
		
		variableResultMap = new HashMap<String,Object>();
		for(ParameterContext parameter:parameters) {
			variableResultMap.put(parameter.getName(), parameter.getValue());
		}
		if (expressions != null) {
			for(int i=0; i<expressions.size(); i++) {
				
				ExpressionContext expressionContext = expressions.get(i);
				
				expressionContext = fillParamterAndParseExpressionContext(expressionContext);
				expressionContext.setVariableToExpresionMap(variableResultMap);
				
				Object res = expressionContext.executeExpression();
				variableResultMap.put(expressionContext.getName(), res);
				
				ParameterContext parameterContext = new ParameterContext();
				parameterContext.setName(expressionContext.getName());
				parameterContext.setValue(variableResultMap.get(expressionContext.getName()));
				this.addParamater(parameterContext);
				
				if(i ==0 && getResultEvaluator() == null && isSingleExpression()) {
					return res;
				}
			}
		}
		LOGGER.severe("variableToExpresionMap --- "+variableResultMap+" \n\n"+"expString --- "+getResultEvaluator());
		
		result =  evaluateExpression(getResultEvaluator(),variableResultMap, ignoreNullValues);
		LOGGER.severe("result --- "+result);
		return result;
	}
	
	private ExpressionContext fillParamterAndParseExpressionContext(ExpressionContext expressionContext) throws Exception {
		
		String expressionString = expressionContext.getExpressionString();
		LOGGER.severe("BEFORE STRING --- "+expressionString);
		
		if(expressionContext.getExpressionString().split(VARIABLE_PLACE_HOLDER).length > 1) {
			for(ParameterContext parameter :parameters) {
				String val = null;
				if (parameter.getValue() != null) {
					val = parameter.getValue().toString();
				}
				else {
					val = "";
				}
				
				String var = "${"+parameter.getName()+"}";
				String varRegex = "\\$\\{"+parameter.getName()+"\\}";
				if(expressionString.contains(var)) {
					expressionString = expressionString.replaceAll(varRegex, StringEscapeUtils.escapeXml10(val));
				}
			}
		}
		LOGGER.severe("AFTER STRING --- "+expressionString);
		expressionContext = WorkflowUtil.getExpressionContextFromExpressionString(expressionString);
		
		return expressionContext;
	}
	
	private Object evaluateExpression(String exp,Map<String,Object> variablesMap, boolean ignoreNullValues) {

		LOGGER.severe("EXPRESSION STRING IS -- "+exp+" variablesMap -- "+variablesMap);
		if(exp == null) {
			return null;
		}
		Expression expression = new Expression(exp);
		for(String key : variablesMap.keySet()) {
			String value = "0";
			if(variablesMap.get(key) != null) {
				value = variablesMap.get(key).toString();
			}
			expression.with(key, value);
		}
		BigDecimal result = expression.eval();
		return result.doubleValue();
	}
	
	public boolean isSingleExpression() {
		if(expressions != null && expressions.size() == 1) {
			return true;
		}
		return false;
	}
	
	public boolean isMapReturnWorkflow() {
		if(expressions != null && expressions.size() == 1 && expressions.get(0).getFieldName() == null && expressions.get(0).getCriteria() != null && expressions.get(0).getModuleName() != null) {
			return true;
		}
		return false;
	}
	
	public boolean isListReturnWorkflow() {
		if(expressions != null && expressions.size() == 1 && expressions.get(0).getFieldName() != null && expressions.get(0).getCriteria() != null && expressions.get(0).getModuleName() != null && expressions.get(0).getAggregateString() == null) {
			return true;
		}
		return false;
	}
	
	public boolean isBooleanReturnWorkflow() {
		if(getResultEvaluator() != null) {
			for(String opperator: WorkflowUtil.getComparisionOpperator()) {
				if(getResultEvaluator().contains(opperator)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean isSingleValueReturnWorkflow() {
		if(getResultEvaluator() != null) {
			if(isBooleanReturnWorkflow()) {
				return false;
			}
			return true;
		}
		else {
			if(expressions != null && expressions.size() == 1 && expressions.get(0).getFieldName() != null && expressions.get(0).getCriteria() != null && expressions.get(0).getModuleName() != null && expressions.get(0).getAggregateString() != null) {
				return true;
			}
		}
		return false;
	}
}
