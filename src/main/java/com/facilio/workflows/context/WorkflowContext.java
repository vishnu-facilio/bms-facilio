package com.facilio.workflows.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringEscapeUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.workflows.util.WorkflowUtil;

public class WorkflowContext implements Serializable {
	
	private static final Logger LOGGER = Logger.getLogger(WorkflowContext.class.getName());
	
	public static String VARIABLE_PLACE_HOLDER = "\\$\\{.+\\}";

	private Map<String,List<Map<String,Object>>> cachedData = null;

	private Map<String,ReadingDataMeta> cachedRDM = null;

	public Map<String, ReadingDataMeta> getCachedRDM() {
		return cachedRDM;
	}
	public void setCachedRDM(Map<String, ReadingDataMeta> cachedRDM) {
		this.cachedRDM = cachedRDM;
	}
	
	public Map<String, List<Map<String, Object>>> getCachedData() {
		return cachedData;
	}
	public void setCachedData(Map<String, List<Map<String, Object>>> cachedData) {
		this.cachedData = cachedData;
	}
	public void addCachedData(String name, List<Map<String, Object>> data) {
		if (cachedData == null) {
			cachedData = new HashMap<String, List<Map<String,Object>>>();
		}
		cachedData.put(name, data);
	}

	Long id;
	Long orgId;
	String workflowString;
	List<ParameterContext> parameters;
	List<ExpressionContext> expressions;
	Map<String,Object> variableResultMap;
	String resultEvaluator;
	
	boolean isIgnoreNullParams;
	
	public boolean isIgnoreNullParams() {
		return isIgnoreNullParams;
	}
	public void setIgnoreNullParams(boolean isIgnoreNullParams) {
		this.isIgnoreNullParams = isIgnoreNullParams;
	}

	boolean ignoreMarkedReadings;
	
	public boolean isIgnoreMarkedReadings() {
		return ignoreMarkedReadings;
	}
	public void setIgnoreMarkedReadings(boolean ignoreMarkedReadings) {
		this.ignoreMarkedReadings = ignoreMarkedReadings;
	}

	boolean terminateExecution;
	
	public boolean isTerminateExecution() {
		return terminateExecution;
	}
	public void setTerminateExecution(boolean terminateExecution) {
		this.terminateExecution = terminateExecution;
	}

	boolean getDataFromCache;
	public boolean isGetDataFromCache() {
		return getDataFromCache;
	}
	public void setGetDataFromCache(boolean getDataFromCache) {
		this.getDataFromCache = getDataFromCache;
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
	
	private WorkflowUIMode workflowUIMode;
	public WorkflowUIMode getFormulaFieldTypeEnum() {
		return workflowUIMode;
	}
	public void setWorkflowUIMode(WorkflowUIMode workflowUIMode) {
		this.workflowUIMode = workflowUIMode;
	}
	public int getWorkflowUIMode() {
		if (workflowUIMode != null) {
			return workflowUIMode.getValue();
		}
		return -1;
	}
	public void setWorkflowUIMode(int workflowUIMode) {
		this.workflowUIMode = WorkflowUIMode.valueOf(workflowUIMode);
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
				expressionContext.setWorkflowContext(this);
				
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
		if(isTerminateExecution()) {
			LOGGER.severe("workflow --- "+this.getId()+" has been terminated");
			return 0;
		}
		
		if (AccountUtil.getCurrentOrg().getId() == 88 && "(b == false) && (c >= 3) && (d >= 3)".equals(getResultEvaluator())) {
			LOGGER.info("variableToExpresionMap --- "+variableResultMap+" \n\n"+"expString --- "+getResultEvaluator());
		}
		
		result =  WorkflowUtil.evaluateExpression(getResultEvaluator(),variableResultMap, ignoreNullValues);
		if (AccountUtil.getCurrentOrg().getId() == 88 && "(b == false) && (c >= 3) && (d >= 3)".equals(getResultEvaluator())) {
			LOGGER.info("result --- "+result);
		}
		return result;
	}
	
	private ExpressionContext fillParamterAndParseExpressionContext(ExpressionContext expressionContext) throws Exception {
		
		String expressionString = expressionContext.getExpressionString();
		LOGGER.fine("BEFORE STRING --- "+expressionString);
		
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
		LOGGER.fine("AFTER STRING --- "+expressionString);
		expressionContext = WorkflowUtil.getExpressionContextFromExpressionString(expressionString);
		
		return expressionContext;
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
	
	public enum WorkflowUIMode {
		GUI,
		XML,
		COMPLEX
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static WorkflowUIMode valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}
