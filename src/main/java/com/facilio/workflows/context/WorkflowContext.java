package com.facilio.workflows.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.json.simple.JSONArray;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.workflows.context.WorkflowExpression.WorkflowExpressionType;
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
	List<WorkflowExpression> expressions;
	
	public List<WorkflowExpression> getExpressions() {
		return expressions;
	}
	
	// only from client
	public void setExpressions(JSONArray workflowExpressions) throws Exception {
		if(workflowExpressions != null) {
			
			for(int i=0 ;i<workflowExpressions.size();i++) {
				
				WorkflowExpression workflowExpression = null;
				
				Map workflowExp = (Map)workflowExpressions.get(i);
				Integer workflowExpressionType = (Integer) workflowExp.get("workflowExpressionType");
				if(workflowExpressionType == null) {
					workflowExpressionType = 0;
				}
				if(workflowExpressionType <= 0 || workflowExpressionType == WorkflowExpressionType.EXPRESSION.getValue()) {
					workflowExpression = new ExpressionContext();
					workflowExpression = FieldUtil.getAsBeanFromMap(workflowExp, ExpressionContext.class);
				}
				else if(workflowExpressionType == WorkflowExpressionType.ITERATION.getValue()) {
					workflowExpression = new IteratorContext();
					workflowExpression = FieldUtil.getAsBeanFromMap(workflowExp, IteratorContext.class);
				}
				addWorkflowExpression(workflowExpression);
			}
		}
	}
	
	public void setWorkflowExpressions(List<WorkflowExpression> workflowExpressions) throws Exception {
		
		this.expressions = workflowExpressions;
	}
	
	public void addWorkflowExpression(WorkflowExpression expression) {
		expressions = expressions == null ? new ArrayList<>() : expressions;
		expressions.add(expression);
	}
	
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
			
			executeExpression(expressions,this);
			
			if(getResultEvaluator() == null && isSingleExpression()) {
				ExpressionContext exp = (ExpressionContext) expressions.get(0);
				return variableResultMap.get(exp.getName());
			}
		}
		if(isTerminateExecution()) {
			LOGGER.finer("workflow --- "+this.getId()+" has been terminated");
			return 0;
		}
		
		if (AccountUtil.getCurrentOrg().getId() == 135) {
			LOGGER.finer("variableToExpresionMap --- "+variableResultMap+" \n\n"+"expString --- "+getResultEvaluator());
		}
		
		result =  WorkflowUtil.evaluateExpression(getResultEvaluator(),variableResultMap, ignoreNullValues);
		if (AccountUtil.getCurrentOrg().getId() == 135) {
			LOGGER.finer("result --- "+result);
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
		if(expressions != null && expressions.size() == 1) {
			return true;
		}
		return false;
	}
	
	public boolean isMapReturnWorkflow() {
		if(expressions != null) {
			ExpressionContext exp = (ExpressionContext) expressions.get(0);
			if(expressions.size() == 1 && exp != null && exp.getFieldName() == null && exp.getCriteria() != null && exp.getModuleName() != null) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isListReturnWorkflow() {
		if(expressions != null) {
			ExpressionContext exp = (ExpressionContext) expressions.get(0);
			if(expressions.size() == 1 && exp != null && exp.getFieldName() != null && exp.getCriteria() != null && exp.getModuleName() != null && exp.getAggregateString() == null) {
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
			if(expressions != null) {
				ExpressionContext exp = (ExpressionContext) expressions.get(0);
				if(expressions.size() == 1 && exp.getFieldName() != null && exp.getCriteria() != null && exp.getModuleName() != null && exp.getAggregateString() != null) {
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
