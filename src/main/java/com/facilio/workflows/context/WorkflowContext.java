package com.facilio.workflows.context;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.json.simple.JSONArray;

import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.workflows.context.WorkflowExpression.WorkflowExpressionType;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.Visitor.FacilioWorkflowFunctionVisitor;
import com.facilio.workflowv2.autogens.WorkflowV2Lexer;
import com.facilio.workflowv2.autogens.WorkflowV2Parser;

public class WorkflowContext implements Serializable {
	
	/**
	 * 
	 */
	
	public WorkflowContext() {
		
	}
	public WorkflowContext(String workflowString) {
		this.workflowString = workflowString;
	}
	private static final long serialVersionUID = 1L;

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

	long id = -1l;
	Long orgId;
	Long nameSpaceId;
	String name;
	String workflowString;
	List<ParameterContext> parameters;
	List<Object> params;							// for v2 workflow
	
	List<WorkflowExpression> expressions;
	
	public List<WorkflowExpression> getExpressions() {
		return expressions;
	}
	
	boolean isLogNeeded;
	
	public List<Object> getParams() {
		return params;
	}
	public void setParams(List<Object> params) {
		this.params = params;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isLogNeeded() {
		return isLogNeeded;
	}
	public boolean getIsLogNeeded() {
		return isLogNeeded;
	}
	public Long getNameSpaceId() {
		return nameSpaceId;
	}
	public void setNameSpaceId(Long nameSpaceId) {
		this.nameSpaceId = nameSpaceId;
	}
	public void setLogNeeded(boolean isLogNeeded) {
		this.isLogNeeded = isLogNeeded;
	}
	public void setIsLogNeeded(boolean isLogNeeded) {
		this.isLogNeeded = isLogNeeded;
	}
	// only from client
	public void setExpressions(JSONArray workflowExpressions) throws Exception {
		if(workflowExpressions != null) {
			
			for(int i=0 ;i<workflowExpressions.size();i++) {
				
				WorkflowExpression workflowExpression = null;
				
				Map workflowExp = (Map)workflowExpressions.get(i);
				Integer workflowExpressionType = 0;
				if (workflowExp.containsKey("workflowExpressionType")) {
					workflowExpressionType = Integer.parseInt(workflowExp.get("workflowExpressionType").toString());
				}
				if(workflowExpressionType <= 0 || workflowExpressionType == WorkflowExpressionType.EXPRESSION.getValue()) {
					workflowExpression = null;
					workflowExpression = FieldUtil.getAsBeanFromMap(workflowExp, ExpressionContext.class);
				}
				else if(workflowExpressionType == WorkflowExpressionType.ITERATION.getValue()) {
					workflowExpression = null;
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
	public long getId() {
		return id;
	}

	public void setId(long id) {
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
	boolean isDebugMode;
	public boolean isDebugMode() {
		return isDebugMode;
	}
	public void setDebugMode(boolean isDebugMode) {
		this.isDebugMode = isDebugMode;
	}
	public Object executeWorkflow() throws Exception {
		
		Object result = null;
		
		if(workflowUIMode == WorkflowUIMode.NEW_WORKFLOW) {
			
			FacilioWorkflowFunctionVisitor visitor = null;
			try {
				InputStream stream = new ByteArrayInputStream(workflowString.getBytes(StandardCharsets.UTF_8));
				
				WorkflowV2Lexer lexer = new WorkflowV2Lexer(CharStreams.fromStream(stream, StandardCharsets.UTF_8));
		        
				WorkflowV2Parser parser = new WorkflowV2Parser(new CommonTokenStream(lexer));
		        ParseTree tree = parser.parse();
		        
		        visitor = new FacilioWorkflowFunctionVisitor();
		        visitor.visitFunctionHeader(tree);
		        visitor.setParams(params);
		        visitor.visit(tree);
		        
		        if(isDebugMode) {
		        	return visitor.getResultString().toString();
		        }
		        
		        return visitor.getReturnValue();
			}
			catch(Exception e) {
				visitor.getResultString().append(e.toString()+"\n");
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
				if(isDebugMode) {
		        	return visitor.getResultString().toString();
		        }
				throw e;
			}
		}
		
		variableResultMap = new HashMap<String,Object>();
		for(ParameterContext parameter:parameters) {
			variableResultMap.put(parameter.getName(), parameter.getValue());
		}
		if (expressions != null) {
			
			executeExpression(expressions,this);
			
			if(getResultEvaluator() == null && isSingleExpression() && expressions.get(0) instanceof ExpressionContext) {
				ExpressionContext exp = (ExpressionContext) expressions.get(0);
				return variableResultMap.get(exp.getName());
			}
		}
		if(isTerminateExecution()) {
			LOGGER.info("workflow --- "+this.getId()+" has been terminated");
			return 0;
		}
		
		
		result =  WorkflowUtil.evaluateExpression(getResultEvaluator(),variableResultMap, isIgnoreNullParams);
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
				
				Object res = expressionContext.execute(workflowContext);
				if(expressionContext.getName() != null && !expressionContext.getName().isEmpty()) {
					variableResultMap1.put(expressionContext.getName(), res);
				}
			}
			else if(wokflowExpresion instanceof IteratorContext) {
		
				IteratorContext iteratorContext = (IteratorContext) wokflowExpresion;
				iteratorContext.execute(workflowContext);
			}
			else if(wokflowExpresion instanceof ConditionContext) {
				
				ConditionContext conditionContext = (ConditionContext) wokflowExpresion;
				conditionContext.execute(workflowContext);
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
		if(expressions != null && !expressions.isEmpty() && expressions.get(0) instanceof ExpressionContext) {
			ExpressionContext exp = (ExpressionContext) expressions.get(0);
			if(expressions.size() == 1 && exp != null && exp.getFieldName() == null && exp.getCriteria() != null && exp.getModuleName() != null) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isListReturnWorkflow() {
		if(expressions != null && !expressions.isEmpty() && expressions.get(0) instanceof ExpressionContext) {
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
			if(expressions != null && !expressions.isEmpty() && expressions.get(0) instanceof ExpressionContext) {
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
		COMPLEX,
		NEW_WORKFLOW
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
