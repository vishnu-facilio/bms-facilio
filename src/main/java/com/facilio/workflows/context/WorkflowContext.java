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
	List<WorkflowExpression> workflowExpressions;
	
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

	
	public List<WorkflowExpression> getWorkflowExpressions() {
		return workflowExpressions;
	}
	public void setWorkflowExpressions(List<WorkflowExpression> workflowExpressions) {
		this.workflowExpressions = workflowExpressions;
	}
	public void addWorkflowExpression(WorkflowExpression expression) {
		workflowExpressions = workflowExpressions == null ? new ArrayList<>() : workflowExpressions;
		workflowExpressions.add(expression);
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
		if (workflowExpressions != null) {
			
			executeExpression(workflowExpressions,this);
			
			if(getResultEvaluator() == null && isSingleExpression()) {
				ExpressionContext exp = (ExpressionContext) workflowExpressions.get(0);
				return variableResultMap.get(exp.getName());
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
	
	public static void executeExpression(List<WorkflowExpression> expressions,WorkflowContext workflowContext) throws Exception {
		
		Map<String, Object> variableResultMap1 = workflowContext.getVariableResultMap();
		for(int i=0; i<expressions.size(); i++) {
			
			WorkflowExpression wokflowExpresion = expressions.get(i);
			
			if(wokflowExpresion instanceof ExpressionContext) {
				
				ExpressionContext expressionContext = (ExpressionContext) wokflowExpresion;
				expressionContext = WorkflowUtil.fillParamterAndParseExpressionContext(expressionContext,variableResultMap1);
				expressionContext.setVariableToExpresionMap(variableResultMap1);
				expressionContext.setWorkflowContext(workflowContext);
				
				Object res = expressionContext.execute();
				variableResultMap1.put(expressionContext.getName(), res);
			}
			else if(wokflowExpresion instanceof IteratorContext) {
		
				IteratorContext iteratorContext = (IteratorContext) wokflowExpresion;
				iteratorContext.setVariableToExpresionMap(variableResultMap1);
				iteratorContext.setWorkflowContext(workflowContext);
				iteratorContext.execute();
			}
		}
	}
	
	public boolean isSingleExpression() {
		if(workflowExpressions != null && workflowExpressions.size() == 1) {
			return true;
		}
		return false;
	}
	
	public boolean isMapReturnWorkflow() {
		if(workflowExpressions != null) {
			ExpressionContext exp = (ExpressionContext) workflowExpressions.get(0);
			if(workflowExpressions.size() == 1 && exp != null && exp.getFieldName() == null && exp.getCriteria() != null && exp.getModuleName() != null) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isListReturnWorkflow() {
		if(workflowExpressions != null) {
			ExpressionContext exp = (ExpressionContext) workflowExpressions.get(0);
			if(workflowExpressions.size() == 1 && exp != null && exp.getFieldName() != null && exp.getCriteria() != null && exp.getModuleName() != null && exp.getAggregateString() == null) {
				return true;
			}
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
			if(workflowExpressions != null) {
				ExpressionContext exp = (ExpressionContext) workflowExpressions.get(0);
				if(workflowExpressions.size() == 1 && exp.getFieldName() != null && exp.getCriteria() != null && exp.getModuleName() != null && exp.getAggregateString() != null) {
					return true;
				}
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
