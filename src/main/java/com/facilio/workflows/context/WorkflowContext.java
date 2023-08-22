package com.facilio.workflows.context;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.workflowv2.contexts.WorkflowFieldsRelContext;
import com.facilio.workflowv2.contexts.WorkflowModuleRelContext;
import com.facilio.workflowv2.contexts.WorkflowNameSpaceRelContext;
import com.facilio.workflowv2.parser.ScriptValidator;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONArray;

import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.automation.util.GlobalVariableUtil;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.scriptengine.autogens.WorkflowV2Lexer;
import com.facilio.scriptengine.autogens.WorkflowV2Parser;
import com.facilio.scriptengine.context.ErrorListener;
import com.facilio.scriptengine.context.ParameterContext;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.util.WorkflowGlobalParamUtil;
import com.facilio.workflowlog.context.WorkflowLogContext;
import com.facilio.workflowlog.context.WorkflowLogContext.WorkflowLogStatus;
import com.facilio.workflowlog.context.WorkflowLogContext.WorkflowLogType;
import com.facilio.workflows.context.WorkflowExpression.WorkflowExpressionType;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.Visitor.WorkflowFunctionVisitor;
import com.facilio.workflowv2.parser.ScriptParser;
import com.facilio.workflowv2.parser.ScriptValidationException;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

@Getter
@Setter
public class WorkflowContext extends ScriptContext{
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	
	private static org.apache.log4j.Logger log = org.apache.log4j.LogManager.getLogger(WorkflowUtil.class.getName());

	private static final Logger LOGGER = Logger.getLogger(WorkflowContext.class.getName());
	
	public WorkflowContext() {
		this.setErrorListener(new ErrorListener());
	}
	public WorkflowContext(String workflowString) {
		this();
		this.workflowString = workflowString;
	}
	
	boolean throwExceptionForSyntaxError = false;

	public boolean isThrowExceptionForSyntaxError() {
		return throwExceptionForSyntaxError;
	}
	public void setThrowExceptionForSyntaxError(boolean throwExceptionForSyntaxError) {
		this.throwExceptionForSyntaxError = throwExceptionForSyntaxError;
	}
	
	private Map<String,List<Map<String,Object>>> cachedData = null;

	private Map<String,ReadingDataMeta> cachedRDM = null;

	public Map<String, ReadingDataMeta> getCachedRDM() {
		return cachedRDM;
	}
	public void setCachedRDM(Map<String, ReadingDataMeta> cachedRDM) {
		this.cachedRDM = cachedRDM;
		addGlobalParamater(WorkflowGlobalParamUtil.RDM_CACHE, this.cachedRDM);
	}
	Boolean isV2Script;
	
	Criteria criteria;					// apply this criteria to all select query
	
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	public Boolean getIsV2Script() {
		return isV2Script;
	}
	public void setIsV2Script(Boolean isV2Script) {
		this.isV2Script = isV2Script;
	}
	public boolean isV2Script() {
		if(this.isV2Script != null) {
			return isV2Script;
		}
		return false;
	}
	public Map<String, List<Map<String, Object>>> getCachedData() {
		return cachedData;
	}
	public void setCachedData(Map<String, List<Map<String, Object>>> cachedData) {
		this.cachedData = cachedData;
		addGlobalParamater(WorkflowGlobalParamUtil.DATA_CACHE, this.cachedData);
	}
	public void addCachedData(String name, List<Map<String, Object>> data) {
		if (cachedData == null) {
			cachedData = new HashMap<String, List<Map<String,Object>>>();
		}
		cachedData.put(name, data);
	}

	String workflowString;
	Map<String,Object> globalParameters;
	List<Object> params;
	
	long recordId;
	public long getRecordId() {
		return recordId;
	}
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}

	long parentId;
	WorkflowLogType logType;
	@Getter@Setter
	private Long recordModuleId;

	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	public WorkflowLogType getLogType() {
		return logType;
	}
	public void setLogType(WorkflowLogType logType) {
		this.logType = logType;
	}

	boolean isParsedV2Script;
	
	public boolean isParsedV2Script() {
		return isParsedV2Script;
	}
	public void setParsedV2Script(boolean isParsedV2Script) {
		this.isParsedV2Script = isParsedV2Script;
	}

	List<WorkflowExpression> expressions;
	
	public List<WorkflowExpression> getExpressions() {
		return expressions;
	}
	
	public List<Object> getParams() {
		return params;
	}
	public void setParams(List<Object> params) {
		this.params = params;
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
	
	boolean fetchMarkedReadings = false;

	public boolean isFetchMarkedReadings() {
		return fetchMarkedReadings;
	}
	public void setFetchMarkedReadings(boolean fetchMarkedReadings) {
		this.fetchMarkedReadings = fetchMarkedReadings;
	}

	boolean terminateExecution;
	
	public boolean isTerminateExecution() {
		return terminateExecution;
	}
	public void setTerminateExecution(boolean terminateExecution) {
		this.terminateExecution = terminateExecution;
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

	private Set<WorkflowModuleRelContext> moduleRels=new TreeSet<>();
	private Set<WorkflowNameSpaceRelContext> nameSpaceRels= new TreeSet<>();
	private Set<WorkflowFieldsRelContext> fieldRels= new TreeSet<>();
	public void setVariableResultMap(Map<String, Object> variableToExpresionMap) {
		this.variableResultMap = variableToExpresionMap;
	}
	public void addVariableResultMap(String key,Object value) {
		if(this.variableResultMap == null) {
			this.variableResultMap = new HashMap<>();
		}
		this.variableResultMap.put(key, value);
	}

	public String getWorkflowString() {
		return workflowString;
	}

	public void setWorkflowString(String workflowString) {
		this.workflowString = workflowString;
	}

	public void addParamater(ParameterContext parameter) {
		if(getParameters() == null) {
			setParameters(new ArrayList<>());
		}
		getParameters().add(parameter);
	}
	
	public Map<String,Object> getGlobalParameters() {
		return globalParameters;
	}

	public void setGlobalParameters(Map<String,Object> globalParameters) {
		this.globalParameters = globalParameters;
	}
	
	public void addGlobalParamater(String key,Object value) {
		if(key == null && !WorkflowGlobalParamUtil.getApprovedGlobalParamNames().contains(key)) {
			return;
		}
		if(this.globalParameters == null) {
			this.globalParameters = new HashMap<>();
		}
		this.globalParameters.put(key, value);
	}

	public String getResultEvaluator() {
		return resultEvaluator;
	}

	public void setResultEvaluator(String resultEvaluator) {
		this.resultEvaluator = resultEvaluator;
	}
	
	public WorkflowUIMode getFormulaFieldTypeEnum() {
		return WorkflowUIMode.valueOf(getWorkflowUIMode());
	}

	public void fillFunctionHeaderFromScript() throws Exception {
		
		try(InputStream stream = new ByteArrayInputStream(getWorkflowV2String().getBytes(StandardCharsets.UTF_8));) {
			WorkflowV2Lexer lexer = new WorkflowV2Lexer(CharStreams.fromStream(stream, StandardCharsets.UTF_8));
	        
			WorkflowV2Parser parser = new WorkflowV2Parser(new CommonTokenStream(lexer));
	        ParseTree tree = parser.parse();
	        
	        WorkflowFunctionVisitor visitor = new WorkflowFunctionVisitor();
	        visitor.setScriptContext(this);
	        visitor.visitFunctionHeader(tree);
		}
	}
	
	/**
	 * @return True if there is no validation error
	 * @throws Exception
	 */
	public boolean validateWorkflow() throws Exception {
		if(isV2Script()) {
			WorkflowV2Parser parser = getParser(this.getWorkflowV2String());
			parser.parse();
			return !this.getErrorListener().hasErrors();
		}
		return true;
	}
	
	public void parseScript() throws Exception {
		
		if(isV2Script()) {
			
			try {
				WorkflowV2Parser parser = getParser(this.getWorkflowV2String());
		        ParseTree tree = parser.parse();
		        
		        if(!getErrorListener().hasErrors()) {
		        	ScriptParser scriptParser = new ScriptParser();
		        	scriptParser.setWorkflowContext(this);
		        	scriptParser.visit(tree);
		        }
		        else {
		        	if(isThrowExceptionForSyntaxError()) {
		        		throw new Exception(getErrorListener().getErrorsAsString());
		        	}
		        	else {
		        		LOGGER.log(Level.SEVERE, "Workflow - "+getId()+" has syntax errors - "+getErrorListener().getErrorsAsString());
		        	}
		        }
		        this.setParsedV2Script(true);
			}
			catch(Exception e) {
				this.setWorkflowExpressions(null);
				this.setParsedV2Script(false);
				this.setWorkflowUIMode(WorkflowUIMode.XML);
			}
		}
	}

	public void validateScript() throws Exception {

		if(isV2Script()) {

			try {
				WorkflowV2Parser parser = getParser(this.getWorkflowV2String());
				ParseTree tree = parser.parse();

				if(!getErrorListener().hasErrors()) {
					ScriptValidator scriptValidator = new ScriptValidator();
					scriptValidator.setWorkflowContext(this);
					scriptValidator.visit(tree);
				}
			}
			catch(ScriptValidationException e) {
	            log.error(e.getMessage(), e);
	            throw e;
	        }
			catch(Exception e) {
				log.error(e.getMessage(), e);
			}
		}
	}
	
	public Object executeWorkflow() throws Exception {
		
		if(isRunAsAdmin()) {
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", true,AccountUtil.getCurrentOrg().getId());
			return bean.executeWorkflow(this);
		}
		else {
			return executeWorkflowScoped();
		}
	}
	
	public Object executeWorkflowScoped() throws Exception {
		
		long currentMillis = System.currentTimeMillis();
		int selectCount = AccountUtil.getCurrentSelectQuery(), pSelectCount = AccountUtil.getCurrentPublicSelectQuery();
		
		Object result = null;
		
		try {
			if(isV2Script()) {
				
				try {
					WorkflowV2Parser parser = getParser(this.getWorkflowV2String());
			        ParseTree tree = parser.parse();

			        if(!getErrorListener().hasErrors()) {
			        	WorkflowFunctionVisitor visitor = new WorkflowFunctionVisitor();
			        	visitor.setScriptContext(this);
				        visitor.visitFunctionHeader(tree);
				        visitor.setParams(params);
				        visitor.setGlobalParams(fillDefaultGlobalVariables(globalParameters));
				        visitor.visit(tree);
				        WorkflowUtil.sendScriptLogs(this,this.getLogString(),WorkflowLogStatus.SUCCESS,null);
			        }
			        else {
			        	if(isThrowExceptionForSyntaxError()) {
			        		throw new Exception(getErrorListener().getErrorsAsString());
			        	}
			        	else {
			        		LOGGER.log(Level.SEVERE, "Workflow - "+getId()+" has syntax errors - "+getErrorListener().getErrorsAsString());
			        	}

			        	WorkflowUtil.sendScriptLogs(this,null,WorkflowLogStatus.SYNTAX_ERROR,getErrorListener().getErrorsAsString());
			        }
			        
			        return this.getReturnValue();
				}
				catch(Exception e) {
					String errorMeg = "Exception occured in script : "+getId();
					if(this instanceof WorkflowUserFunctionContext) {
						String name = ((WorkflowUserFunctionContext)this).getName();
						errorMeg = errorMeg+" name - "+ name;
					}
					errorMeg = errorMeg+" message - "+e.getMessage();						
					this.getLogStringBuilder().append("ERROR ::: "+errorMeg+"\n");
					LOGGER.log(Level.SEVERE, errorMeg, e);
					
					WorkflowUtil.sendScriptLogs(this,this.getLogString(),WorkflowLogStatus.FAILURE,errorMeg);
					
					throw e;
				}
			}
			
			variableResultMap = new HashMap<String,Object>();
			for(ParameterContext parameter:getParameters()) {
				variableResultMap.put(parameter.getName(), parameter.getValue());
			}
			
			if(globalParameters != null && !globalParameters.isEmpty()) {
				
				variableResultMap.putAll(globalParameters);
			}
			
			if (expressions != null) {
				
				WorkflowUtil.executeExpression(expressions,this);
				
				if(getResultEvaluator() == null && isSingleExpression() && expressions.get(0) instanceof ExpressionContext) {
					ExpressionContext exp = (ExpressionContext) expressions.get(0);
					return variableResultMap.get(exp.getName());
				}
			}
			if(isTerminateExecution()) {
				LOGGER.info("workflow --- "+this.getId()+" has been terminated");
				result = 0;
			}
			else {
				result =  WorkflowUtil.evaluateExpression(getResultEvaluator(),variableResultMap, isIgnoreNullParams);
			}
		}
		finally {
			long executionTime = System.currentTimeMillis() - currentMillis;
			int totalSelect = AccountUtil.getCurrentSelectQuery() - selectCount;
			int totalPublicSelect = AccountUtil.getCurrentPublicSelectQuery() - pSelectCount;
			String msg = MessageFormat.format("### time taken for workflow ({0}) is {1}, select : {2}, pSelect : {3}", this.getId(), executionTime, totalSelect, totalPublicSelect);
			if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 339l) {
//				log.info(msg);			
			}
			else {
				log.debug(msg);
			}
		}
		
		return result;
	}
	
	private Map<String, Object> fillDefaultGlobalVariables(Map<String, Object> globalParameters) throws Exception {
		globalParameters = globalParameters == null ? new HashMap<>() : globalParameters;
		
		globalParameters.put("currentOrg", FieldUtil.getAsJSON(AccountUtil.getCurrentOrg()));
		globalParameters.put("currentUser", FieldUtil.getAsJSON(AccountUtil.getCurrentUser()));

		Map<String, Map<String, Object>> liveVariables = GlobalVariableUtil.getLiveVariables();
		if (MapUtils.isNotEmpty(liveVariables)) {
			globalParameters.put("cv", liveVariables);		
		}
		return globalParameters;
	}
	//	old workflow methods starts
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
	@Deprecated
	public boolean isSingleExpression() {
		if(expressions != null && expressions.size() == 1) {
			return true;
		}
		return false;
	}
	@Deprecated
	public boolean isMapReturnWorkflow() {
		if(expressions != null && !expressions.isEmpty() && expressions.get(0) instanceof ExpressionContext) {
			ExpressionContext exp = (ExpressionContext) expressions.get(0);
			if(expressions.size() == 1 && exp != null && exp.getFieldName() == null && exp.getCriteria() != null && exp.getModuleName() != null) {
				return true;
			}
		}
		return false;
	}
	@Deprecated
	public boolean isListReturnWorkflow() {
		if(expressions != null && !expressions.isEmpty() && expressions.get(0) instanceof ExpressionContext) {
			ExpressionContext exp = (ExpressionContext) expressions.get(0);
			if(expressions.size() == 1 && exp != null && exp.getFieldName() != null && exp.getCriteria() != null && exp.getModuleName() != null && exp.getAggregateString() == null) {
				return true;
			}
		}
		return false;
	}
	@Deprecated
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
	@Deprecated
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
	
	private WorkflowV2Parser getParser(String script) throws Exception {
		
		try(InputStream stream = new ByteArrayInputStream(script.getBytes(StandardCharsets.UTF_8));) {
			WorkflowV2Lexer lexer = new WorkflowV2Lexer(CharStreams.fromStream(stream, StandardCharsets.UTF_8));
	        
			WorkflowV2Parser parser = new WorkflowV2Parser(new CommonTokenStream(lexer));
			
			if(this.getErrorListener() == null) {
				this.setErrorListener(new ErrorListener());
			}
			parser.addErrorListener(this.getErrorListener());
			
			return parser;
		}
	}
	

	private IAMUser sysCreatedByObj;
	private IAMUser sysModifiedByObj;

	public IAMUser getSysCreatedByObj() {
		return sysCreatedByObj;
	}
	public void setSysCreatedByObj(IAMUser sysCreatedByObj) {
		this.sysCreatedByObj = sysCreatedByObj;
	}
	public IAMUser getSysModifiedByObj() {
		return sysModifiedByObj;
	}
	public void setSysModifiedByObj(IAMUser sysModifiedByObj) {
		this.sysModifiedByObj = sysModifiedByObj;
	}
}
