package com.facilio.workflows.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.util.ScriptUtil;
import com.facilio.scriptengine.systemfunctions.*;
import com.facilio.workflows.functions.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FormulaFieldContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BaseLineContext;
import com.facilio.modules.BaseLineContext.AdjustType;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.queue.source.MessageSourceUtil;
import com.facilio.report.util.DemoHelperUtil;
import com.facilio.scriptengine.context.ParameterContext;
import com.facilio.scriptengine.context.WorkflowFieldType;
import com.facilio.scriptengine.context.WorkflowFunctionContext;
import com.facilio.scriptengine.util.WorkflowGlobalParamUtil;
import com.facilio.services.messageQueue.MessageQueue;
import com.facilio.services.messageQueue.MessageQueueFactory;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.util.FacilioUtil;
import com.facilio.wmsv2.constants.Topics;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.facilio.wmsv2.handler.AuditLogHandler;
import com.facilio.wmsv2.handler.ScriptLogHander;
import com.facilio.wmsv2.message.Message;
import com.facilio.workflowlog.context.WorkflowLogContext;
import com.facilio.workflowlog.context.WorkflowLogContext.WorkflowLogStatus;
import com.facilio.workflowlog.context.WorkflowLogContext.WorkflowLogType;
import com.facilio.workflows.conditions.context.ElseContext;
import com.facilio.workflows.conditions.context.ElseIfContext;
import com.facilio.workflows.conditions.context.IfContext;
import com.facilio.workflows.context.ConditionContext;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.IteratorContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowExpression;
import com.facilio.workflows.context.WorkflowFieldContext;
import com.facilio.workflowv2.util.UserFunctionAPI;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.udojava.evalex.Expression;

;

public class WorkflowUtil {
	
	public static Map<Double, Double> CHILLER_TEMP_VS_PRESSURE = null;
	private static org.apache.log4j.Logger log = LogManager.getLogger(WorkflowUtil.class.getName());

	private static final Logger LOGGER = Logger.getLogger(WorkflowUtil.class.getName());

	static final List<String> COMPARISION_OPPERATORS = new ArrayList<>();
	static final List<String> ARITHMETIC_OPPERATORS = new ArrayList<>();
	
//	private static final String CONDITION_FORMATTER = "((.*?)`(baseLine\\{(\\d+)\\}\\s*)?([^`]*)`(.*))";
	private static final String CONDITION_FORMATTER = "((.*?)`(baseLine\\{(\\d+)(\\,*)(\\d*)\\}\\s*)?([^`]*)`(.*))";
	private static final String CUSTOM_FUNCTION_RESULT_EVALUATOR = "(.*?)(\\.)(.*?)(\\()(.*?)(\\))";
	private static final String ITERATOR_VARIABLE = "(.*?)(\\,)(.*?)(\\:)(.*+)";
	private static final String VARIABLE_PLACE_HOLDER = "\\$\\{(.+?)\\}";
	private static final String ALPHA_NUMERIC_WITH_UNDERSCORE = "^[a-zA-Z0-9_]*$";
	
	
	static {
		COMPARISION_OPPERATORS.add("==");
		COMPARISION_OPPERATORS.add("=");
		COMPARISION_OPPERATORS.add("!=");
		COMPARISION_OPPERATORS.add("<>");
		COMPARISION_OPPERATORS.add("<");
		COMPARISION_OPPERATORS.add(">");
		COMPARISION_OPPERATORS.add(">=");
		COMPARISION_OPPERATORS.add("<=");
		COMPARISION_OPPERATORS.add("&&");
		COMPARISION_OPPERATORS.add("||");
		
		ARITHMETIC_OPPERATORS.add("+");
		ARITHMETIC_OPPERATORS.add("-");
		ARITHMETIC_OPPERATORS.add("*");
		ARITHMETIC_OPPERATORS.add("/");
		ARITHMETIC_OPPERATORS.add("%");
		ARITHMETIC_OPPERATORS.add("^");
	}
	
	public static List<Object> getFieldAsListFromProperties(List<Map<String,Object>> props, String fieldName) {
		
		List<Object> values = null;
		for(Map<String, Object> prop :props) {
			if(values == null) {
				values = new ArrayList<>();
			}
			Object value = prop.get(fieldName);
			values.add(value);
		}
		return values;
	}
	public static Multimap<String, FacilioField> getAllParentAndfieldIdfromWorkflow(String workflowString) throws Exception {
		
		WorkflowContext workflowContext = new WorkflowContext();
		workflowContext.setWorkflowString(workflowString);
		return getAllParentAndfieldIdfromWorkflow(workflowContext);
	}
	public static Multimap<String, FacilioField> getAllParentAndfieldIdfromWorkflow(WorkflowContext workflow) throws Exception {
		
		WorkflowContext workflowContext = getWorkflowContextFromString(workflow.getWorkflowString());
		parseExpression(workflowContext);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		Multimap<String, FacilioField> resultMap = ArrayListMultimap.create();
		
		for(WorkflowExpression workflowExpression : workflowContext.getExpressions()) {
			
			if(workflowExpression instanceof ExpressionContext) {
				
				ExpressionContext exp = (ExpressionContext) workflowExpression;
				String parentId = WorkflowUtil.getParentIdFromCriteria(exp.getCriteria());
				FacilioField field = modBean.getField(exp.getFieldName(), exp.getModuleName());
				resultMap.put(parentId, field);
			}
		}
		return resultMap;
	}

	/**
	 * {
	 *     A: [4, 595],
	 *     B: [-1, 596]
	 *     C: [4, 599]
	 * }
	 */
	public static SortedMap<String, Object[]> getAllVariableAndParentAndfieldIdfromWorkflow(WorkflowContext workflow) throws Exception {

		SortedMap<String, Object[]> varMap = new TreeMap();
		WorkflowContext workflowContext = getWorkflowContextFromString(workflow.getWorkflowString());
		parseExpression(workflowContext);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		for(WorkflowExpression workflowExpression : workflowContext.getExpressions()) {

			if(workflowExpression instanceof ExpressionContext) {

				ExpressionContext exp = (ExpressionContext) workflowExpression;
				if(exp.getCriteria() == null) {
					continue;
				}
				String parentIdStr = WorkflowUtil.getParentIdFromCriteria(exp.getCriteria());

				Long parentId = parentIdStr != null ? NumberUtils.toLong(parentIdStr, -1) : -1;
				FacilioField field = modBean.getField(exp.getFieldName(), exp.getModuleName());
				varMap.put(exp.getName(), new Object[]{parentId, field});
			}
		}
		return varMap;
	}

	public static String getParentIdFromCriteria (Criteria criteria) {
		
		Map<String, Condition> conditions = criteria.getConditions();
		if(conditions != null) {
			for(String key:conditions.keySet()) {
				Condition condition = conditions.get(key);
				if(condition.getFieldName().contains("parentId")) {
					return condition.getValue();
				}
			}
		}
		return null;
	}
	
	public static synchronized Map<Double, Double> getChillerTempVsPressureMap() {
		
		if(CHILLER_TEMP_VS_PRESSURE == null) {

			WorkflowUtil w = new WorkflowUtil();
			w.getClass().getClassLoader().getResource(FacilioUtil.normalizePath("conf/chillerdata.csv"));
			File chillerCSVFile = new File(w.getClass().getClassLoader().getResource(FacilioUtil.normalizePath("conf/chillerdata.csv")).getFile());
	        String line = "";
	        String cvsSplitBy = ",";

	        Map<Double,Double> temp = new HashMap<>();
	        try (BufferedReader br = new BufferedReader(new FileReader(chillerCSVFile))) {
	            while ((line = br.readLine()) != null) {

	                String[] values = line.split(cvsSplitBy);

	                Double presure = Double.parseDouble(values[0]);
	                Double temprature = Double.parseDouble(values[1]);
	                
	                temp.put(presure, temprature);
	            }
	        } 
	        catch (IOException e) {
	            log.info("Exception occurred ", e);
	        }
	        CHILLER_TEMP_VS_PRESSURE = temp;
		}
		
		return CHILLER_TEMP_VS_PRESSURE;
	}
	
	public static List<String> getComparisionOpperator() {
		return COMPARISION_OPPERATORS;
	}
	public static List<String> getArithmeticOpperator() {
		return ARITHMETIC_OPPERATORS;
	}
	static final String WORKFLOW_STRING =  "workflow";
	static final String PARAMETER_STRING =  "parameter";
	static final String TYPE_STRING =  "type";
	static final String EXPRESSION_STRING =  "expression";
	static final String ITERATOR_STRING =  "iterator";
	static final String NAME_STRING =  "name";
	static final String VAR_STRING =  "var";
	static final String CONSTANT_STRING =  "constant";
	static final String FUNCTION_STRING =  "function";
	static final String EXPR_STRING =  "expr";
	static final String PRINT_STRING =  "print";
	static final String MODULE_STRING =  "module";
	static final String FIELD_STRING =  "field";
	static final String AGGREGATE_STRING =  "aggregate";
	static final String CRITERIA_STRING =  "criteria";
	static final String ORDER_BY_STRING =  "orderBy";
	static final String SORT_STRING =  "sort";
	static final String LIMIT_STRING =  "limit";
	static final String GROUP_BY_STRING =  "groupBy";
	static final String CONDITION_STRING =  "condition";
	static final String CONDITIONS_STRING =  "conditions";
	static final String CONDITION_IF_STRING =  "if";
	static final String CONDITION_IF_ELSE_STRING =  "elseif";
	static final String CONDITION_ELSE_STRING =  "else";
	static final String PATTERN_STRING =  "pattern";
	static final String SEQUENCE_STRING =  "sequence";
	static final String RESULT_STRING =  "result";
	static final String IS_NULL_STRING =  "IS NULL";
	static final String IS_NOT_NULL_STRING =  "IS NOT NULL";
	
	public static String getCacheKey (String moduleName, long resourceId) {
		return getCacheKey(moduleName, String.valueOf(resourceId));
	}
	
	public static String getCacheKey (String moduleName, String resourceId) {
		return moduleName+"-"+resourceId;
	}
	
	// workflow Result Fetching block starts
	
	private static boolean evalWorkflowResultForBoolean (Object result) {
		if (result == null) {
		    return false;
		}
		if (result instanceof Boolean) {
		    return (boolean) result;
		}
		else {
		    double resultDouble = FacilioUtil.parseDouble(result);
		    return resultDouble == 1;
		}
	}
	
	public static Object getResult(Long workflowId,Map<String,Object> paramMap)  throws Exception  {
		return getResult(workflowId, paramMap, false);
	}

	public static Object getResult(Long workflowId,Map<String,Object> paramMap, boolean ignoreNullExpressions)  throws Exception  {
		WorkflowContext workflowContext = getWorkflowContext(workflowId);
		return getWorkflowResult(workflowContext,paramMap, null, ignoreNullExpressions, false,false);
	}
	public static Map<String, Object> getExpressionResultMap(Long workflowId,Map<String,Object> paramMap)  throws Exception  {
		WorkflowContext workflowContext = getWorkflowContext(workflowId);
		return getExpressionResultMap(workflowContext,paramMap);
	}
	
	public static Object getWorkflowExpressionResult(String workflowString,Map<String,Object> paramMap,boolean isV2Script) throws Exception {
		if(isV2Script) {
			WorkflowContext workflow = new WorkflowContext();
			workflow.setIsV2Script(isV2Script);
			workflow.setWorkflowV2String(workflowString);
			return getWorkflowResult(workflow,paramMap, null, false, false,false);
		}
		return getWorkflowResult(new WorkflowContext(workflowString),paramMap, null, false, false,false);
	}
	public static Object getWorkflowExpressionResult(String workflowString,Map<String,Object> paramMap,Criteria criteria) throws Exception {
		
		WorkflowContext workflow = new WorkflowContext(workflowString);
		workflow.setCriteria(criteria);
		return getWorkflowResult(workflow,paramMap, null, false, false,false);
	}
	public static Object getWorkflowExpressionResult(WorkflowContext workflowContext,Map<String,Object> paramMap) throws Exception {
		return getWorkflowResult(workflowContext,paramMap, null, false, false,false);
	}
	
	public static Map<String, Object> getExpressionResultMap(String workflowContext,Map<String,Object> paramMap,Criteria criteria) throws Exception {
		WorkflowContext workflow = new WorkflowContext(workflowContext);
		workflow.setCriteria(criteria);
		return (Map<String, Object>) getWorkflowResult(workflow,paramMap, null, false, false,true);
	}
	public static Map<String, Object> getExpressionResultMap(WorkflowContext workflowContext,Map<String,Object> paramMap) throws Exception {
		return (Map<String, Object>) getWorkflowResult(workflowContext,paramMap, null, false, false,true);
	}
	
	public static boolean getWorkflowExpressionResultAsBoolean(WorkflowContext workflowContext,Map<String,Object> paramMap) throws Exception {
		Object result = getWorkflowResult(workflowContext,paramMap, null, false, false,false);
		return evalWorkflowResultForBoolean(result);
	}
	
	public static boolean getWorkflowExpressionResultAsBoolean(WorkflowContext workflowContext,Map<String,Object> paramMap,long parentId, long recordId ,WorkflowLogType logtype) throws Exception {
		Object result = getWorkflowResult(workflowContext,paramMap, null, false, false,false,parentId, recordId ,logtype);
		return evalWorkflowResultForBoolean(result);
	}
	
	public static boolean getWorkflowExpressionResultAsBoolean(WorkflowContext workflowContext,Map<String,Object> paramMap, Map<String, ReadingDataMeta> rdmCache, boolean ignoreNullExpressions, boolean ignoreMarked) throws Exception {
		Object result = getWorkflowResult(workflowContext, paramMap, rdmCache, ignoreNullExpressions, ignoreMarked, false);
		return evalWorkflowResultForBoolean(result);
	}
	
	public static Object getWorkflowExpressionResult(WorkflowContext workflowContext,Map<String,Object> paramMap, Map<String, ReadingDataMeta> rdmCache, boolean ignoreNullExpressions, boolean ignoreMarked) throws Exception {
		return getWorkflowResult(workflowContext, paramMap, rdmCache, ignoreNullExpressions, ignoreMarked, false);
	}
	
	public static Object getWorkflowExpressionResult(WorkflowContext workflowContext,Map<String,Object> paramMap, Map<String, ReadingDataMeta> rdmCache, boolean ignoreNullExpressions, boolean ignoreMarked, long parentId, long resourceId ,WorkflowLogType logtype) throws Exception {
		return getWorkflowResult(workflowContext, paramMap, rdmCache, ignoreNullExpressions, ignoreMarked, false,parentId,resourceId,logtype);
	}
	
	private static Object getWorkflowResult(WorkflowContext workflowContext,Map<String,Object> paramMap, Map<String, ReadingDataMeta> rdmCache, boolean ignoreNullExpressions, boolean ignoreMarked, boolean isVariableMapNeeded) throws Exception {
		return getWorkflowResult(workflowContext, paramMap, rdmCache, ignoreNullExpressions, ignoreMarked, isVariableMapNeeded, -1l, -1l, null);
	}
	
	
	private static Object getWorkflowResult(WorkflowContext workflowContext,Map<String,Object> paramMap, Map<String, ReadingDataMeta> rdmCache, boolean ignoreNullExpressions, boolean ignoreMarked, boolean isVariableMapNeeded, long parentId, long recordId,WorkflowLogType logtype) throws Exception {

		if(!workflowContext.isV2Script()) {
			workflowContext = getWorkflowContextFromString(workflowContext.getWorkflowString(),workflowContext);
			List<ParameterContext> parameterContexts = validateAndGetParameters(workflowContext,paramMap);
			workflowContext.setParameters(parameterContexts);
		}
		else {
			workflowContext.fillFunctionHeaderFromScript();
			List<Object> params = new ArrayList<>();
			for(ParameterContext parameterContext : workflowContext.getParameters()) {
				Object objectValue = paramMap.get(parameterContext.getName());
				if(objectValue instanceof ModuleBaseWithCustomFields) {
					objectValue = FieldUtil.getAsProperties(objectValue);
				}
				params.add(objectValue);
			}
			workflowContext.setParams(params);
		}
		Map<String, Object> globalParameters = validateAndGetGlobalParameters(workflowContext,paramMap);
		workflowContext.setGlobalParameters(globalParameters);
		workflowContext.setCachedRDM(rdmCache);
		workflowContext.setIgnoreMarkedReadings(ignoreMarked);
		workflowContext.setRecordId(recordId);
		workflowContext.setParentId(parentId);
		workflowContext.setLogType(logtype);
		
		paramMap = workflowContext.getVariableResultMap();
		
		workflowContext.setIgnoreNullParams(ignoreNullExpressions);
		Object result = workflowContext.executeWorkflow();
		if (AccountUtil.getCurrentOrg().getId() == 1) {
			LOGGER.info("Result of Formula from WorkflowUtil:446: " + result );
		}
		if(isVariableMapNeeded) {
			return workflowContext.getVariableResultMap();
		}
		else {
			return result;
		}
	}
	
	// workflow Result Fetching block ends
	
	public static void deleteWorkflow(long id) throws Exception {
		deleteWorkflows(Collections.singletonList(id));
	}
	
	public static void deleteWorkflows(Collection<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowModule();
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(ids, module));
		
		deleteBuilder.delete();
	}
	
	public static void validateWorkflow(WorkflowContext workflow) throws Exception {
		
		checkParamsDeclaration(workflow);
		checkDuplicateParams(workflow);
	}
	
	public static Long addWorkflow(String workflowString) throws Exception {
		WorkflowContext workflowContext = new WorkflowContext();
		workflowContext.setWorkflowString(workflowString);
		return addWorkflow(workflowContext);
	}
	
	private static void checkDuplicateParams(WorkflowContext workflow) throws Exception {
		
		List<String> params = new ArrayList<String>();
		
		for(ParameterContext parameter :workflow.getParameters()) {
			
			if(params.contains(parameter.getName())) {
				throw new IllegalArgumentException("param - "+parameter.getName()+" is declared more than once");
			}
			else {
				params.add(parameter.getName());
			}
		}
	}
	private static void checkParamsDeclaration(WorkflowContext workflow) throws Exception {
		
		List<String> params = new ArrayList<String>();
		
		for(ParameterContext parameter :workflow.getParameters()) {
			params.add(parameter.getName());
		}
		for(WorkflowExpression workflowExpression : workflow.getExpressions()) {
			
			if(workflowExpression instanceof ExpressionContext) {
				ExpressionContext expressionContext = (ExpressionContext)  workflowExpression;
				
				String expString = expressionContext.getExpressionString();
				
				int nameStartIndex = expString.indexOf("<expression name=\"")+"<expression name=\"".length();
				String name = expString.substring(nameStartIndex, expString.indexOf('"', nameStartIndex));
				
				params.add(name.trim());
				
				if(expressionContext.getExpressionString().contains("${")) {
					
					List<String> allMatch = new ArrayList<String>();
					
					Pattern pattern = Pattern.compile(VARIABLE_PLACE_HOLDER);
					Matcher matcher = pattern.matcher(expressionContext.getExpressionString());

					while (matcher.find())
					{
						allMatch.add(matcher.group());
					}
					for(String match :allMatch) {
						String variable = match.substring(2, match.length()-1);
						if(!params.contains(variable)) {
							throw new IllegalArgumentException("Variable - "+variable+" is not declared in params");
						}
					}
				}
			}
		}
	}
	
	public static Long addWorkflow(WorkflowContext workflowContext) throws Exception {	// change this method

		if(workflowContext.isV2Script() && workflowContext.getWorkflowV2String() == null) {
			getV2ScriptFromWorkflowContext(workflowContext);
		}
		else {
			if(!workflowContext.isV2Script()&&workflowContext.getWorkflowString() == null) {
				workflowContext.setWorkflowString(getXmlStringFromWorkflow(workflowContext));
			}

			if (!workflowContext.isV2Script()) {
				getWorkflowContextFromString(workflowContext.getWorkflowString(), workflowContext);

				validateWorkflow(workflowContext);
			}
		}

		throwExceptionIfScriptValidationFailed(workflowContext);

		
		workflowContext.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getWorkflowModule().getTableName())
				.fields(FieldFactory.getWorkflowFields());

		Map<String, Object> props = FieldUtil.getAsProperties(workflowContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		workflowContext.setId((Long) props.get("id"));
		
		if(!workflowContext.isV2Script()) {
			
			insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getWorkflowFieldModule().getTableName())
					.fields(FieldFactory.getWorkflowFieldsFields());
			
			workflowContext = WorkflowUtil.getWorkflowContext(workflowContext.getId(), true);
			
			List<WorkflowFieldContext> workflowFields = getWorkflowField(workflowContext);
			
			if (workflowFields != null && !workflowFields.isEmpty()) {
				for(WorkflowFieldContext workflowField :workflowFields) {
					props = FieldUtil.getAsProperties(workflowField);
					insertBuilder.addRecord(props);
				}
			}

			insertBuilder.save();
		}

		return workflowContext.getId();
	}
	
	public static List<WorkflowFieldContext>  getWorkflowField(WorkflowContext workflowContext) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<WorkflowFieldContext> workflowFieldList = null;
		if(!workflowContext.isV2Script()) {
			for(WorkflowExpression workflowExpression :workflowContext.getExpressions()) {
				
				if(workflowExpression instanceof ExpressionContext) {
					ExpressionContext expression = (ExpressionContext)  workflowExpression;
					
					String fieldName = expression.getFieldName();
					String moduleName = expression.getModuleName();
					
					if(moduleName != null && fieldName != null && !moduleName.startsWith("$")) {
						FacilioModule module = modBean.getModule(moduleName);
						FacilioField field = modBean.getField(fieldName, moduleName);
						if(field != null) {
							
							if(workflowFieldList == null) {
								workflowFieldList = new ArrayList<>();
							}
							
							WorkflowFieldContext workflowFieldContext = new WorkflowFieldContext();
							
							workflowFieldContext.setOrgId(module.getOrgId());
							workflowFieldContext.setModuleId(module.getModuleId());
							workflowFieldContext.setFieldId(field.getId());
							workflowFieldContext.setField(field);
							if (workflowContext.getId() > 0) {
								workflowFieldContext.setWorkflowId(workflowContext.getId());
							}
							Long parentId = null;
							if(expression.getCriteria() != null) {
								Map<String, Condition> conditions = expression.getCriteria().getConditions();
								for(Condition condition :conditions.values()) {
									if(condition.getFieldName().equals("parentId") && !condition.getValue().equals("${resourceId}")) {
										if(condition.getValue() != null && !condition.getValue().contains("${")) {
											parentId = Long.parseLong(condition.getValue());
										}
									}
								}
							}
							if(parentId != null) {
								workflowFieldContext.setResourceId(parentId);
							}
							if(expression.getAggregateOpperator() != null) {
								workflowFieldContext.setAggregation(expression.getAggregateOpperator());
							}
							
							if(!workflowFieldList.contains(workflowFieldContext)) {
								workflowFieldList.add(workflowFieldContext);
							}
						}
					}
				}
			}
		}
		return workflowFieldList;
	}
	
	public static List<WorkflowFieldContext> getWorkflowFields(long workflowId) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowFieldModule();
		List<FacilioField> fields = FieldFactory.getWorkflowFieldsFields();
		FacilioField workflowIdField = FieldFactory.getAsMap(fields).get("workflowId");
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(fields)
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(workflowIdField, String.valueOf(workflowId), PickListOperators.IS))
														;
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<WorkflowFieldContext> workflowFields = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				workflowFields.add(FieldUtil.getAsBeanFromMap(prop, WorkflowFieldContext.class));
			}
			return workflowFields;
		}
		return null;
	}
	
	public static Map<Long, List<Long>> getDependentFieldsIdsAsMap(Collection<Long> workflowIds) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowFieldModule();
		List<FacilioField> fields = FieldFactory.getWorkflowFieldsFields();
		FacilioField workflowIdField = FieldFactory.getAsMap(fields).get("workflowId");
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.select(fields)
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(workflowIdField, StringUtils.join(workflowIds, ","), PickListOperators.IS))
														;
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			Map<Long, List<Long>> dependentFields = new HashMap<>();
			for (Map<String, Object> prop : props) {
				Long fieldId = (long) prop.get("fieldId");
				Long workflowId = (long) prop.get("workflowId");
				
				List<Long> dependents = dependentFields.get(workflowId);
				if (dependents == null) {
					dependents = new ArrayList<>();
					dependentFields.put(workflowId, dependents);
				}
				dependents.add(fieldId);
			}
			return dependentFields;
		}
		return null;
	}
	
	private static final Pattern REG_EX = Pattern.compile("([1-9]\\d*)");
	
	
	private static String getV2ScriptFromCondition(Condition condition,ExpressionContext exp) throws Exception {
		
		String conditionFieldName = condition.getFieldName();
		Operator opp = condition.getOperator();
		String value = condition.getValue();
		
		String operatorStringValue = opp.getOperator().trim();
		
		String conditionString = null;
		List<String> dateOperatorKeys = new ArrayList(DateOperators.getAllOperators().keySet());
		dateOperatorKeys.removeAll(CommonOperators.getAllOperators().keySet());
		dateOperatorKeys.remove(DateOperators.ISN_T.getOperator());
		dateOperatorKeys.remove(DateOperators.IS.getOperator());
		
		
		if(operatorStringValue.equals("between")) {
			String[] values = getMultipleValueStringFromValue(value);
			conditionString = conditionFieldName +" >= "+ values[0] +" && "+conditionFieldName +" <= "+ values[1];
		}
		else if (operatorStringValue.equals("is empty")) {
			conditionString = conditionFieldName +" == null";
		}
		else if (operatorStringValue.equals("is not empty")) {
			conditionString = conditionFieldName +" != null";
		}
		else if (dateOperatorKeys.contains(operatorStringValue)) {
			
			String dateOperatorValue = null;
			if(value != null) {
				if(value.contains(",")) {
					String[] values = value .split(",");
					
					String value1 = getValueStringFromValue(values[0]);
					String value2 = getValueStringFromValue(values[1]);
					if(value2 != null) {
						dateOperatorValue = value1 +","+value2;
					}
					else {
						dateOperatorValue = value1;
					}
				}
				else {
					dateOperatorValue = value;
				}
			}
			if(exp != null && exp.getConditionSeqVsBaselineId() != null && !exp.getConditionSeqVsBaselineId().isEmpty() && exp.getConditionSeqVsBaselineId().containsKey(condition.getSequence())) {
				Long baselineID = exp.getConditionSeqVsBaselineId().get(condition.getSequence());
				BaseLineContext baseline = BaseLineAPI.getBaseLine(baselineID);
				dateOperatorValue = dateOperatorValue + ",\""+baseline.getName()+"\"";
			}
			String dateOperatorVal = null;
			operatorStringValue = "\""+operatorStringValue+"\"";
			if(dateOperatorValue != null && !dateOperatorValue.equals("null") && !dateOperatorValue.isEmpty()) {
				dateOperatorVal =  "new NameSpace(\"date\").getDateRange("+operatorStringValue+","+dateOperatorValue+")";
			}
			else {
				dateOperatorVal =  "new NameSpace(\"date\").getDateRange("+operatorStringValue+")";
			}
			
			conditionString = conditionFieldName +" == "+ dateOperatorVal;
		}
		else {
			value = getValueStringFromValue(value);
			if(operatorStringValue.equals("=")) {
				operatorStringValue = "==";
			}
			if(operatorStringValue.equals("is")) {
				operatorStringValue = "==";
			}
			if(operatorStringValue.equals("isn't")) {
				operatorStringValue = "!=";
			}
			if(operatorStringValue.equals("lookup")) {
				operatorStringValue = "==";
				if(value.contains("ID =")) {
					value = value.substring(value.indexOf('=')+1, value.length()).trim();
				}
			}
			
			if(value.contains(",")) {
				String[] values = value.split(",");
				conditionString = "(";
				int j = 0;
				for(String value1 : values) {
					j++;
					conditionString = conditionString + conditionFieldName +" "+ operatorStringValue +" "+ value1;
					if(j != values.length) {
						if(operatorStringValue.equals("==")) {
							conditionString = conditionString + " || ";
						}
						else if (operatorStringValue.equals("!=")) {
							conditionString = conditionString + " && ";
						}
					}
				}
				conditionString = conditionString + ")";
			}
			else {
				conditionString = conditionFieldName +" "+ operatorStringValue +" "+ value;
			}
		}
		return conditionString;
	}
	
	private static String getV2ScriptFromExpressionContext(ExpressionContext exp,String code) throws Exception {
		
		String name = exp.getName();
		
		if(name.contains(".")) {
			throw new Exception("Workflow Contains '.'");
		}
		
		if(exp.getDefaultFunctionContext() != null) {
			String paramString = exp.getDefaultFunctionContext().getParams().replace("'", "\"");
			code = code + name +" = new NameSpace(\""+exp.getDefaultFunctionContext().getNameSpace()+"\")."+exp.getDefaultFunctionContext().getFunctionName()+"("+paramString+");\n";
		}
		else if(exp.getConstant() != null) {
			code = code + name +" = "+getValueStringFromValue(exp.getConstant().toString())+";\n";
		}
		else if(exp.getExpr() != null) {
			String exprString = exp.getExpr();
			exprString = exprString.replaceAll("\\&\\&", "&");
			exprString = exprString.replaceAll("\\|\\|", "|");
			code = code + name +" = "+exprString+";\n";
		}
		else if(exp.getPrintStatement() != null) {
			String printStatement = exp.getPrintStatement().replace("'", "\"");
			code = code + "log "+printStatement+";\n";
		}
		else {
			String moduleName = exp.getModuleName().trim();
			Criteria criteria = exp.getCriteria();
			String field = exp.getFieldName();
			String aggregate = exp.getAggregateString();
			String orderBy = exp.getOrderByFieldName();
			String sortBy = exp.getSortBy();
			String pattern = criteria.getPattern();
			String limit = exp.getLimit();
			
			List<Condition> fieldConditions = exp.getAggregateCondition();
			
			pattern = pattern.replace("or", " || ");
			pattern = pattern.replace("and", " && ");
			if(pattern.charAt(0) == '(' && pattern.charAt(pattern.length()-1) == ')') {
				pattern = pattern.substring(1, pattern.length()-1);
			}
			Matcher matcher = REG_EX.matcher(pattern);
			int i = 0;
			StringBuilder patternBuilder = new StringBuilder();
			while (matcher.find()) {
				
				String key = matcher.group(1);
				Condition condition = criteria.getConditions().get(key);
				
				condition.setSequence(Integer.parseInt(key));
				
				String conditionString = getV2ScriptFromCondition(condition,exp);
				patternBuilder.append(pattern.substring(i, matcher.start()));
				patternBuilder.append(conditionString);
				i = matcher.end();
			}
			patternBuilder.append(pattern.substring(i, pattern.length()));
			String db = "criteria : ["+patternBuilder.toString() +"],";
			if(field != null) {
				db = db + "field : \""+field+"\",";
				if(aggregate != null) {
					db = db + "aggregation : \""+aggregate+"\",";
				}
			}
			if(fieldConditions != null && !fieldConditions.isEmpty()) {
				
				String conditionString = "";
				for(int j=0;j<fieldConditions.size();j++) {
					
					Condition condition =  fieldConditions.get(j);
					conditionString = conditionString + getV2ScriptFromCondition(condition,null);
					
					if(j != fieldConditions.size()-1) {
						conditionString = conditionString + " && ";
					}
				}
				
				conditionString = "fieldCriteria : ["+conditionString +"],";
				
				db = db + conditionString;
			}
			if(orderBy != null) {
				String sort = "asc";
				if(sortBy != null) {
					sort = sortBy.toLowerCase();
				}
				db = db + "orderBy : \""+orderBy+"\" "+sort+",";
			}
			if(limit != null) {
				db = db + "limit : "+limit+",";
			}
			db = db.substring(0, db.length()-1);
			code = code + name + " = Module(\""+moduleName+"\").fetch({"+db+"});\n";
		}
		
		return code;
	}
	
	static String getValueStringFromValue(String value) {
		
		value = value.trim();
		if(value.contains("${") && value.contains("}")) {
			value = value.substring(2, value.length() - 1);
		}
		return value;
	}
	static String[] getMultipleValueStringFromValue(String value) {
		
		String[] values = value.split(",");
		for(int i=0;i<values.length;i++) {
			String value1 = values[i].trim();
			if(value1.contains("${") && value1.contains("}")) {
				value1 = value1.substring(2, value1.length() - 1);
			}
			values[i] = value1;
		}
		
		return values;
	}
	
	public static boolean isWorkflowFromRule(long wfId) throws Exception {
		List<FacilioField> fields = FieldFactory.getWorkflowRuleFields();
		FacilioModule module = ModuleFactory.getWorkflowRuleModule();
		
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
													.table(module.getTableName())
													.select(fields)
													.andCondition(CriteriaAPI.getCondition("WORKFLOW_ID", "workflowId", wfId+"", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = ruleBuilder.get();
		if(props.isEmpty()) {
			return  false;
		}
		return  true;
	}
	
	public static String isWorkflowFromFormulaField(long wfId) throws Exception {
		List<FacilioField> fields = FieldFactory.getFormulaFieldFields();
		FacilioModule module = ModuleFactory.getFormulaFieldModule();
		
		GenericSelectRecordBuilder ruleBuilder = new GenericSelectRecordBuilder()
													.table(module.getTableName())
													.select(fields)
													.andCondition(CriteriaAPI.getCondition("WORKFLOW_ID", "workflowId", wfId+"", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = ruleBuilder.get();
		if(props.isEmpty()) {
			return  null;
		}
		FormulaFieldContext ff  =  FieldUtil.getAsBeanFromMap(props.get(0), FormulaFieldContext.class);
		if(ff.getReadingFieldId() > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField field = modBean.getField(ff.getReadingFieldId());
			if(field.getDataTypeEnum() == FieldType.NUMBER || field.getDataTypeEnum() == FieldType.DECIMAL) {
				return "Number";
			}
			else if (field.getDataTypeEnum() == FieldType.BOOLEAN) {
				return "Boolean";
			}
		}
		return null;
	}
	
	public static WorkflowContext getV2ScriptFromWorkflowContext(long wfId) throws Exception {
		
		WorkflowContext workflow = WorkflowUtil.getWorkflowContext(wfId, true);
		
		return getV2ScriptFromWorkflowContext(workflow);
	}
	
	private static void oldWorkflowCorrection(WorkflowContext workflow) {
		
		// adding return statement for 
		if(workflow.getResultEvaluator() == null && workflow.isSingleExpression() && workflow.getExpressions().get(0) instanceof ExpressionContext) {
			ExpressionContext exp = (ExpressionContext) workflow.getExpressions().get(0);
			workflow.setResultEvaluator(exp.getName());
		}
	}
	
	public static WorkflowContext getV2ScriptFromWorkflowContext(WorkflowContext workflow) throws Exception {
		// TODO Auto-generated method stub
		
		oldWorkflowCorrection(workflow);
		
		List<ParameterContext> params = workflow.getParameters();
		String paramString = "";
		
		for(ParameterContext param :params) {
			if(param.getName().contains(".")) {
				throw new Exception("Workflow Contains '.'");
			}
			paramString = paramString + param.getTypeString() +" "+param.getName()+",";
		}
		if(paramString .length() > 0) {
			paramString = paramString.substring(0, paramString.length()-1);
		}
		
		String returnType = "void";
		if(workflow.getReturnType() < 0) {
			
			boolean isFromRule = WorkflowUtil.isWorkflowFromRule(workflow.getId());
			if(isFromRule) {
				returnType = "Boolean";
			}
			else {
				String returnType1 = isWorkflowFromFormulaField(workflow.getId());
				if(returnType1 != null) {
					returnType = returnType1;
				}
				else if(workflow.getResultEvaluator() != null) {
					returnType = "Number"; 
				}
			}
		}
		else {
			returnType = workflow.getReturnTypeEnum().getStringValue();
		}
		
		String code = returnType+" test("+paramString+") {\n";
		
		for(WorkflowExpression expression : workflow.getExpressions()) {
			if(expression instanceof ExpressionContext) {
				ExpressionContext exp = (ExpressionContext) expression;
				
				code = getV2ScriptFromExpressionContext(exp, code);
				
			 }
			 else if(expression instanceof IteratorContext) {						// remove this after all migration
				 
				 IteratorContext iteratorContext = (IteratorContext)  expression;
				 
				 code = code + "for each "+iteratorContext.getLoopVariableIndexName()+","+iteratorContext.getLoopVariableValueName()+" in "+iteratorContext.getIteratableVariable()+"{ \n";
				
				 for(WorkflowExpression itrWorkflowExpression : iteratorContext.getExpressions()) {
					 
					 ExpressionContext exp = (ExpressionContext) itrWorkflowExpression;
					 
					 code = getV2ScriptFromExpressionContext(exp, code);
				 }
				 code = code +"}\n";
			 }
			 
			 else if(expression instanceof ConditionContext) {						// remove this after all migration
				 
				 ConditionContext conditionContext = (ConditionContext) expression;
				 
				 IfContext IfContext = conditionContext.getIfContext();
				 
				 String criteriaString = IfContext.getCriteria();
				 
				 criteriaString = criteriaString.replaceAll("IS NULL", "== null");
				 criteriaString = criteriaString.replaceAll("IS NOT NULL", "!= null");
				 
				 code = code + "if( "+criteriaString+") { \n";
				 
				 if(IfContext.getExpressions() != null) {
					 for(WorkflowExpression itrWorkflowExpression : IfContext.getExpressions()) {
						 
						 ExpressionContext exp = (ExpressionContext) itrWorkflowExpression;
						 
						 code = getV2ScriptFromExpressionContext(exp, code);
					 }
				 }
				 code = code +"}\n";
				
				 if(conditionContext.getElseIfContexts() != null) {
					 for(ElseIfContext elseIfContext : conditionContext.getElseIfContexts()) {
						 
						 String elseCriteriaString = elseIfContext.getCriteria();
						 elseCriteriaString = elseCriteriaString.replaceAll("IS NULL", "== null");
						 elseCriteriaString = elseCriteriaString.replaceAll("IS NOT NULL", "!= null");
						 
						 code = code + "else if( "+elseCriteriaString+") { \n";
						 if(elseIfContext.getExpressions() != null) {
							 for(WorkflowExpression itrWorkflowExpression : elseIfContext.getExpressions()) {
								 
								 ExpressionContext exp = (ExpressionContext) itrWorkflowExpression;
								 
								 code = getV2ScriptFromExpressionContext(exp, code);
							 }
						 }
						 code = code +"}\n";
					 }
				 }
				 if(conditionContext.getElseContext() != null) {
					 code = code + "else { \n";
					 if(conditionContext.getElseContext().getExpressions() != null) {
						 for(WorkflowExpression itrWorkflowExpression : conditionContext.getElseContext().getExpressions()) {
							 
							 ExpressionContext exp = (ExpressionContext) itrWorkflowExpression;
							 
							 code = getV2ScriptFromExpressionContext(exp, code);
						 }
					 }
					 code = code +"}\n";
				 }
			 }
		}
		
		if(workflow.getResultEvaluator() != null && !workflow.getResultEvaluator().isEmpty()) {
			String resEval = workflow.getResultEvaluator();
			
			resEval = resEval.replaceAll("\\&\\&", "&");
			resEval = resEval.replaceAll("\\|\\|", "|");
			
			if(containsIgnoreCase(resEval, "if")) {
				String[] ress = resEval.split(",");
				
				String condn = ress[0];
				String trueRes = ress[1];
				String falseRes = ress[2];
				
				condn = condn.substring(condn.indexOf('(')+1, condn.length());
				falseRes = falseRes.substring(0, falseRes.length()-1);
				
				code = code + "if("+condn+") { \n";
				code = code + "return "+trueRes+";\n";
				code = code + "}";
				code = code + "else { \n";
				code = code + "return "+falseRes+";\n";
				code = code + "}\n";
			}
			else {
				code = code + "return "+resEval+";\n";
			}
		}
		
		code = code + "}";
		
		workflow.setWorkflowV2String(code);
		workflow.setIsV2Script(true);
		
		boolean res = workflow.validateWorkflow();
		
		if(!res) {
			LOGGER.severe(workflow.getId()+"  errors ----- "+ workflow.getErrorListener().getErrors() +"   "+ code);
		}
		return workflow;
	}
	
	public static boolean containsIgnoreCase(String str, String subString) {
        return str.toLowerCase().contains(subString.toLowerCase());
    }
	
	public static WorkflowContext getWorkflowContext(Long workflowId) throws Exception  {
		return getWorkflowContext(workflowId,false);
	}
	public static WorkflowContext getWorkflowContext(Long workflowId,boolean isWithExpParsed) throws Exception  {
		
		FacilioModule module = ModuleFactory.getWorkflowModule(); 
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowFields())
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS))
				.andCondition(CriteriaAPI.getIdCondition(workflowId, module));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		WorkflowContext workflowContext = null;
		if (props != null && !props.isEmpty()) {
			workflowContext = getWorkflowFromProp(props.get(0), isWithExpParsed);
		}
		return workflowContext;
	}
	public static List<WorkflowContext> getWorkflowContext(Criteria criteria) throws Exception  {
		
		FacilioModule module = ModuleFactory.getWorkflowModule(); 
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowFields())
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS))
				.andCriteria(criteria);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanListFromMapList(props, WorkflowContext.class);
		}
		return null;
	}
	
	public static Map<Long, WorkflowContext> getWorkflowsAsMap(Collection<Long> ids) throws Exception {
		return getWorkflowsAsMap(ids, false);
	}
	
	public static Map<Long, WorkflowContext> getWorkflowsAsMap(Collection<Long> ids, boolean isWithExpParsed) throws Exception {
		FacilioModule module = ModuleFactory.getWorkflowModule(); 
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowFields())
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition("SYS_DELETED", "deleted", Boolean.FALSE.toString(), BooleanOperators.IS))
				.andCondition(CriteriaAPI.getIdCondition(ids, module));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			Map<Long, WorkflowContext> workflows = new HashMap<>();
			for (Map<String, Object> prop : props) {
				WorkflowContext workflow = getWorkflowFromProp(prop, isWithExpParsed);
				workflows.put(workflow.getId(), workflow);
			}
			return workflows;
		}
		return null;
	}
	
	private static WorkflowContext getWorkflowFromProp(Map<String, Object> prop, boolean isWithExpParsed) throws Exception {
		WorkflowContext workflow = FieldUtil.getAsBeanFromMap(prop, WorkflowContext.class);
		if(workflow.isV2Script()) {
			if(workflow.getWorkflowUIMode() == WorkflowContext.WorkflowUIMode.GUI.getValue()) {
				workflow.parseScript();
			}
		}
		else {
			workflow = getWorkflowContextFromString(workflow.getWorkflowString(),workflow);
			if(isWithExpParsed) {
				parseExpression(workflow);
			}
		}
		return workflow;
	}
	
	public static void parseExpression(WorkflowContext workflowContext) throws Exception {
		new ArrayList<>();
		if(workflowContext != null && workflowContext.getExpressions() != null) {
			getExpressionParsedFromString(workflowContext.getExpressions());
		}
	}
	
	public static void getExpressionParsedFromString(List<WorkflowExpression> expressions) throws Exception { 
		
		if(expressions != null && !expressions.isEmpty()) {
			for(int i = 0; i < expressions.size(); i++) {
				WorkflowExpression workflowExpression = expressions.get(i);
				if(workflowExpression instanceof ExpressionContext) {
					ExpressionContext expressionContext = (ExpressionContext)  workflowExpression;
					expressionContext = getExpressionContextFromExpressionString(expressionContext.getExpressionString(),expressionContext);
					expressions.set(i, expressionContext);
				}
				else if(workflowExpression instanceof IteratorContext) {
					IteratorContext iteratorContext = (IteratorContext) workflowExpression;
					getExpressionParsedFromString(iteratorContext.getExpressions());
				}
				else if(workflowExpression instanceof ConditionContext) {
					ConditionContext conditionContext = (ConditionContext) workflowExpression;
					
					if(conditionContext.getIfContext() != null) {
						IfContext ifContext = conditionContext.getIfContext();
						getExpressionParsedFromString(ifContext.getExpressions());
					}
					if(conditionContext.getElseIfContexts() != null) {
						for(ElseIfContext elseIfContext : conditionContext.getElseIfContexts()) {
							getExpressionParsedFromString(elseIfContext.getExpressions());
						}
					}
					if(conditionContext.getElseContext() != null) {
						ElseContext elseContext = conditionContext.getElseContext();
						getExpressionParsedFromString(elseContext.getExpressions());
					}
				}
			}
		}
	}
	
	public static List<ParameterContext> validateAndGetParameters(WorkflowContext workflowContext,Map<String,Object> paramMap) throws Exception {
		
		List<ParameterContext> paramterContexts = workflowContext.getParameters();
		if(paramterContexts != null && !paramterContexts.isEmpty()) {
			
			if(!workflowContext.isIgnoreNullParams()) {
				if(paramMap == null || paramMap.isEmpty()) {
					throw new IllegalArgumentException("No paramters match found");
				}
				if(paramterContexts.size() > paramMap.size()) {
					throw new IllegalArgumentException("No. of arguments mismatched");
				}
			}
			for(ParameterContext parameterContext:paramterContexts) {
				Object value = paramMap.get(parameterContext.getName());
				
//				checkType(parameterContext,value);
				
				parameterContext.setValue(value);
				workflowContext.addVariableResultMap(parameterContext.getName(), value);
			}
		}
		return paramterContexts;
		
	}
	
	public static Map<String,Object> validateAndGetGlobalParameters(WorkflowContext workflowContext,Map<String,Object> paramMap) throws Exception {
		
		
		Map<String,Object> globalParams = new HashMap<>();
		List<String> aprovedGlobalParams = WorkflowGlobalParamUtil.getApprovedGlobalParamNames();
		if(aprovedGlobalParams != null && !aprovedGlobalParams.isEmpty() && paramMap != null) {
			
			for(String aprovedGlobalParam:aprovedGlobalParams) {
				Object value = paramMap.get(aprovedGlobalParam);
				
				if(value != null) {
					globalParams.put(aprovedGlobalParam, value);
					
					workflowContext.addVariableResultMap(aprovedGlobalParam, value);
				}
			}
		}
		return globalParams;
		
	}
	
	public static boolean checkType(ParameterContext parameterContext,Object value) throws Exception {
		
		 WorkflowFieldType type = parameterContext.getWorkflowFieldType();
		
		if (value == null) {
			return true;
		}
		
		switch(type) {
			case STRING : 
				if(value instanceof String) {
					return true;
				}
				throw new IllegalArgumentException(parameterContext.getName()+" type mismatched "+value);
			case BOOLEAN:
				if(value instanceof Boolean) {
					return true;
				}
				throw new IllegalArgumentException(parameterContext.getName()+" type mismatched "+value);
			case NUMBER:
				if(value instanceof Integer || value instanceof Long) {
					return true;
				}
				throw new IllegalArgumentException(parameterContext.getName()+" type mismatched "+value);
		}
		return false;
	}
	
	// context to String Methods starts
	
	public static String getXmlStringFromWorkflow(WorkflowContext workflowContext) throws Exception {
		 DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		 DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		 Document doc = dBuilder.newDocument();
		 
		 Element workflowElement = doc.createElement(WORKFLOW_STRING);
		 
		 if(workflowContext.getParameters() != null) {
			 
			 for(ParameterContext parameterContext : workflowContext.getParameters()) {
				 Element parameterElement = doc.createElement(PARAMETER_STRING);
				 parameterElement.setAttribute(NAME_STRING, parameterContext.getName());
				 parameterElement.setAttribute(TYPE_STRING, parameterContext.getTypeString());
				 workflowElement.appendChild(parameterElement);
			 }
		 }

		 if(workflowContext.getExpressions() != null) {
			 
			 for(WorkflowExpression workflowExpression :workflowContext.getExpressions()) {
				 
				 Element exp = getWorkflowExpressionContext(workflowExpression,doc);
				 workflowElement.appendChild(exp);
			 }
		 }
		 if(workflowContext.getResultEvaluator() != null) {
			 Element resultElement = doc.createElement(RESULT_STRING);
			 resultElement.setTextContent(workflowContext.getResultEvaluator());
			 workflowElement.appendChild(resultElement);
		 }
		 
		 doc.appendChild(workflowElement);
		 
		 DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
		 LSSerializer lsSerializer = domImplementation.createLSSerializer();
		 
		 String result = lsSerializer.writeToString(doc);
		 return result;
	}
	
	public static Element getWorkflowExpressionContext(WorkflowExpression workflowExpression,Document doc) {
		if(workflowExpression instanceof ExpressionContext) {
			 Element expressionElement = getExpressionXMLFromExpresionContext(workflowExpression,doc);
			 return expressionElement;
		 }
		 else if(workflowExpression instanceof IteratorContext) {
			 
			 IteratorContext iteratorContext = (IteratorContext)  workflowExpression;
			 
			 Element iteratorElement = doc.createElement(ITERATOR_STRING);
			 iteratorElement.setAttribute(VAR_STRING, iteratorContext.getLoopVariableIndexName()+","+iteratorContext.getLoopVariableValueName()+":"+iteratorContext.getIteratableVariable());
			 
			 for(WorkflowExpression itrWorkflowExpression : iteratorContext.getExpressions()) {
				 
				 Element expressionElement = getWorkflowExpressionContext(itrWorkflowExpression,doc);
				 iteratorElement.appendChild(expressionElement);
			 }
			 return iteratorElement;
		 }
		 
		 else if(workflowExpression instanceof ConditionContext) {
			 
			 ConditionContext conditionContext = (ConditionContext) workflowExpression;
			 Element conditionElement = doc.createElement(CONDITIONS_STRING);
			 
			 IfContext IfContext = conditionContext.getIfContext();
			 
			 Element ifConditionElement = doc.createElement(CONDITION_IF_STRING);
			 ifConditionElement.setAttribute(CRITERIA_STRING, IfContext.getCriteria());
			 
			 if(IfContext.getExpressions() != null) {
				 for(WorkflowExpression ifWorkflowExpression : IfContext.getExpressions()) {
					 Element expressionElement = getWorkflowExpressionContext(ifWorkflowExpression,doc);
					 ifConditionElement.appendChild(expressionElement);
				 }
			 }
			 conditionElement.appendChild(ifConditionElement);
			
			 if(conditionContext.getElseIfContexts() != null) {
				 for(ElseIfContext elseIfContext : conditionContext.getElseIfContexts()) {
					 
					 Element ifElseConditionElement = doc.createElement(CONDITION_IF_ELSE_STRING);
					 ifElseConditionElement.setAttribute(CRITERIA_STRING, elseIfContext.getCriteria());
					 if(elseIfContext.getExpressions() != null) {
						 for(WorkflowExpression elseIfWorkflowExpression : elseIfContext.getExpressions()) {
							 Element expressionElement = getWorkflowExpressionContext(elseIfWorkflowExpression,doc);
							 ifElseConditionElement.appendChild(expressionElement);
						 }
					 }
					 conditionElement.appendChild(ifElseConditionElement);
				 }
			 }
			 if(conditionContext.getElseContext() != null) {
				 
				 ElseContext elseContext = conditionContext.getElseContext();
				 
				 Element elseConditionElement = doc.createElement(CONDITION_ELSE_STRING);
				 
				 if(elseContext.getExpressions() != null) {
					 for(WorkflowExpression elseWorkflowExpression : elseContext.getExpressions()) {
						 Element expressionElement = getWorkflowExpressionContext(elseWorkflowExpression,doc);
						 elseConditionElement.appendChild(expressionElement);
					 }
				 }
				 conditionElement.appendChild(elseConditionElement);
			 }
			 return conditionElement;
		 }
		return null;
	}
	
	private static Element getExpressionXMLFromExpresionContext(WorkflowExpression workflowExpression,Document doc) {

		ExpressionContext expressionContext = (ExpressionContext)  workflowExpression;
		
		Element expressionElement = doc.createElement(EXPRESSION_STRING);
		 expressionElement.setAttribute(NAME_STRING, expressionContext.getName());
		 
		 if(expressionContext.getConstant() != null) {
			 Element valueElement = doc.createElement(CONSTANT_STRING);
			 valueElement.setTextContent(expressionContext.getConstant().toString());
			 expressionElement.appendChild(valueElement);
		 }
		 else if(expressionContext.getDefaultFunctionContext() != null) {
			 WorkflowFunctionContext function = expressionContext.getDefaultFunctionContext();
			 Element valueElement = doc.createElement(FUNCTION_STRING);
			 if(function.getParams() != null && !function.getParams().equals("")) {
				 valueElement.setTextContent(function.getNameSpace()+"."+function.getFunctionName()+"("+function.getParams()+")");
			 }
			 else {
				 valueElement.setTextContent(function.getNameSpace()+"."+function.getFunctionName()+"()");
			 }
			 expressionElement.appendChild(valueElement);
		 }
		 else if(expressionContext.getExpr() != null) {
			 Element exprElement = doc.createElement(EXPR_STRING);
			 exprElement.setTextContent(expressionContext.getExpr());
			 expressionElement.appendChild(exprElement);
		 }
		 else if(expressionContext.getPrintStatement() != null) {
			 Element printElement = doc.createElement(PRINT_STRING);
			 printElement.setTextContent(expressionContext.getPrintStatement());
			 expressionElement.appendChild(printElement);
		 }
		 else {
			 Element moduleElement = doc.createElement(MODULE_STRING);
			 moduleElement.setAttribute(NAME_STRING, expressionContext.getModuleName());
			 expressionElement.appendChild(moduleElement);
			 
			 
			 Element criteriaElement = doc.createElement(CRITERIA_STRING);
			 criteriaElement.setAttribute(PATTERN_STRING, expressionContext.getCriteria().getPattern());
			 
			 Map<String, Condition> conditionMap =  expressionContext.getCriteria().getConditions();
			 for(Map.Entry<String, Condition> conditionEntry : conditionMap.entrySet()) {
				 
				 Condition condition = conditionEntry.getValue();
				 Object key = conditionEntry.getKey();
				 Element conditionElement = doc.createElement(CONDITION_STRING);
				 conditionElement.setAttribute(SEQUENCE_STRING, key.toString());
				 if (condition.getOperator() instanceof DateOperators && expressionContext.getConditionSeqVsBaselineId() != null &&  expressionContext.getConditionSeqVsBaselineId().containsKey(Integer.parseInt(key.toString()))) {
					 conditionElement.setTextContent(condition.getFieldName()+"`baseLine{" + expressionContext.getConditionSeqVsBaselineId().get(Integer.parseInt(key.toString())) + "}"+condition.getOperator().getOperator()+"`"+condition.getValue());
				 }
				 else {
					 conditionElement.setTextContent(condition.getFieldName()+"`"+condition.getOperator().getOperator()+"`"+condition.getValue());
				 }
				 criteriaElement.appendChild(conditionElement);
			 }
			 expressionElement.appendChild(criteriaElement);
			 
			 if(expressionContext.getFieldName() != null) {
				 Element fieldElement = doc.createElement(FIELD_STRING);
				 fieldElement.setAttribute(NAME_STRING, expressionContext.getFieldName());
				 if(expressionContext.getAggregateString() != null) {
					 fieldElement.setAttribute(AGGREGATE_STRING, expressionContext.getAggregateString());
				 }
				 
				 if(expressionContext.getAggregateCondition() != null) {
					 for(Condition condition : expressionContext.getAggregateCondition()) {
						 
						 Element conditionElement = doc.createElement(CONDITION_STRING);
						 conditionElement.setTextContent(condition.getFieldName()+"`"+condition.getOperator().getOperator()+"`"+condition.getValue());
						 fieldElement.appendChild(conditionElement);
					 }
				 }
				 
				 expressionElement.appendChild(fieldElement);
			 }
			 if(expressionContext.getOrderByFieldName() != null) {
				 Element orderElement = doc.createElement(ORDER_BY_STRING);
				 orderElement.setAttribute(NAME_STRING, expressionContext.getOrderByFieldName());
				 if(expressionContext.getSortBy() != null) {
					 orderElement.setAttribute(SORT_STRING, expressionContext.getSortBy());
				 }
				 expressionElement.appendChild(orderElement);
			 }
			 if(expressionContext.getLimit() != null) {
				 Element limitElement = doc.createElement(LIMIT_STRING);
				 limitElement.setTextContent(expressionContext.getLimit());
				 expressionElement.appendChild(limitElement);
			 }
			 if(expressionContext.getGroupBy() != null) {
				 Element groupByElement = doc.createElement(GROUP_BY_STRING);
				 groupByElement.setTextContent(expressionContext.getGroupBy());
				 expressionElement.appendChild(groupByElement);
			 }
		 }
		 return expressionElement;
	}
	
	// context to String Methods ends
	
	// string to Context methods
	
	public static WorkflowContext getWorkflowContextFromString(String workflow) throws Exception {
		return getWorkflowContextFromString(workflow,null);
	}
	public static WorkflowContext getWorkflowContextFromString(String workflow,WorkflowContext workflowContext) throws Exception {
    	if(workflowContext == null) {
    		workflowContext = new WorkflowContext();
    	}
		workflowContext.setWorkflowString(workflow);
		
		try(InputStream stream = new ByteArrayInputStream(workflow.getBytes("UTF-16"));) {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	Document doc = dBuilder.parse(stream);
	        doc.getDocumentElement().normalize();
	        
	        workflowContext.setParameters(getParameterListFromWorkflowString(workflow));

	        List<WorkflowExpression> workflowExpressionList = getWorkflowExpressions(workflow);

	        workflowContext.setWorkflowExpressions(workflowExpressionList);

	        NodeList resultNodes = doc.getElementsByTagName(RESULT_STRING);
	        if(resultNodes.getLength() > 0) {
	        	Node resultNode = resultNodes.item(0);
	        	if (resultNode.getNodeType() == Node.ELEMENT_NODE) {
	        		Element result  = (Element) resultNode;
	        		String resultString = result.getTextContent();
	        		workflowContext.setResultEvaluator(resultString);
	        	}
	        }
	        return workflowContext;
		}
	}

	public static WorkflowContext getWorkflowContextFromMap(Map<String,Object>workflowMap) throws Exception{
		WorkflowContext workflowContext = new WorkflowContext();
		if(workflowMap.containsKey("workflowV2String")){
			workflowContext.setWorkflowV2String(String.valueOf(workflowMap.get("workflowV2String")));
		}
		if(workflowMap.containsKey("isV2Script")){
			workflowContext.setIsV2Script((Boolean) workflowMap.get("isV2Script"));
		}
		return workflowContext;
	}
	
	private static List<ParameterContext> getParameterListFromWorkflowString(String workflow) throws Exception {
		
		try(InputStream stream = new ByteArrayInputStream(workflow.getBytes("UTF-16"));) {
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	Document doc = dBuilder.parse(stream);
	        doc.getDocumentElement().normalize();
	        
	        List<ParameterContext> paramterContexts = new ArrayList<>();
	        NodeList parameterNodes = doc.getElementsByTagName(PARAMETER_STRING);
	        for(int i=0;i<parameterNodes.getLength();i++) {
	        	
	        	Node parameterNode = parameterNodes.item(i);
	        	if (parameterNode.getNodeType() == Node.ELEMENT_NODE) {
	        		ParameterContext parameterContext = new ParameterContext();
	        		Element parameter = (Element) parameterNode;
	        		
	        		String name = parameter.getAttribute(NAME_STRING);
	        		String type = parameter.getAttribute(TYPE_STRING);
	        		parameterContext.setName(name);
	        		parameterContext.setTypeString(type);
	        		
	        		paramterContexts.add(parameterContext);
	        	}
	        }
	        return paramterContexts;
		}
	}
	
	private static List<WorkflowExpression> getWorkflowExpressions(String workflow) throws Exception {
		
//		LOGGER.log(Level.SEVERE, "workflow -- "+workflow);
		List<WorkflowExpression> workflowExpressions = new ArrayList<>();
    	
		try(InputStream stream = new ByteArrayInputStream(workflow.getBytes("UTF-16"));) {
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	Document doc = dBuilder.parse(stream);
	        doc.getDocumentElement().normalize();
	        
			NodeList childNodes = doc.getDocumentElement().getChildNodes();
	        
	        for (int i = 0; i < childNodes.getLength(); i++) {
	        	
	        	Node expressionNode = childNodes.item(i);
	        	 
	        	Document document = expressionNode.getOwnerDocument();
	        	DOMImplementationLS domImplLS = (DOMImplementationLS) document.getImplementation();
	            LSSerializer serializer = domImplLS.createLSSerializer();
	            serializer.getDomConfig().setParameter("xml-declaration", Boolean.FALSE);
	        	
	        	if(expressionNode.getNodeName().equals(EXPRESSION_STRING)) {
	        		
	            	String str = serializer.writeToString(expressionNode);
	        		
	        		ExpressionContext expressionContext = new ExpressionContext();
	            	expressionContext.setExpressionString(str);
	                 
	                workflowExpressions.add(expressionContext);
	        	}
	        	else if(expressionNode.getNodeName().equals(ITERATOR_STRING)) {
	        		
	        		IteratorContext iteratorContext = new IteratorContext();
	        		
	            	if(expressionNode.getNodeType() == Node.ELEMENT_NODE) {
	            		Element value = (Element) expressionNode;
	            		
	            		String valueString = value.getAttribute(VAR_STRING);
	            		Pattern condtionStringpattern = Pattern.compile(ITERATOR_VARIABLE);
	             		Matcher matcher = condtionStringpattern.matcher(valueString);
	             		while (matcher.find()) {
	             			if(matcher.group(1) != null) {
	             				iteratorContext.setLoopVariableIndexName(matcher.group(1));
	             			}
	             			if(matcher.group(3) != null) {
	             				iteratorContext.setLoopVariableValueName(matcher.group(3));
	             			}
	             			if(matcher.group(5) != null) {
	             				iteratorContext.setIteratableVariable(matcher.group(5));
	             			}
	             		}
	             		
	             		String str = serializer.writeToString(expressionNode);
	             		
	             		List<WorkflowExpression> workflowExpressionList = getWorkflowExpressions(str);
	             		
	             		iteratorContext.setWorkflowExpressions(workflowExpressionList);
	            	}
	            	workflowExpressions.add(iteratorContext);
	        	}
	        	else if(expressionNode.getNodeName().equals(CONDITIONS_STRING)) {
	        		
	        		ConditionContext conditionContext= new ConditionContext();
	        		
	            	if(expressionNode.getNodeType() == Node.ELEMENT_NODE) {
	            		Element value = (Element) expressionNode;
	            		NodeList conditionChildNodes = value.getChildNodes();
	            		
	            		for(int j=0;j<conditionChildNodes.getLength();j++) {
	            			
	            			Node conditionChildNode = conditionChildNodes.item(j);
	            			
	            			if(conditionChildNode.getNodeName().equals(CONDITION_IF_STRING)) {
	            				if(conditionChildNode.getNodeType() == Node.ELEMENT_NODE) {
	            					IfContext ifContext = new IfContext();
	            					Element conditionChildElement = (Element) conditionChildNode;
	            					
	            					ifContext.setCriteria(conditionChildElement.getAttribute(CRITERIA_STRING));
	            					String str = serializer.writeToString(conditionChildElement);
	            					
	            					List<WorkflowExpression> workflowExpressionList = getWorkflowExpressions(str);
	            					
	            					ifContext.setWorkflowExpressions(workflowExpressionList);
	            					
	            					conditionContext.setIfContext(ifContext);
	            				}
	            			}
	            			else if(conditionChildNode.getNodeName().equals(CONDITION_IF_ELSE_STRING)) {
	            				if(conditionChildNode.getNodeType() == Node.ELEMENT_NODE) {
	            					Element conditionChildElement = (Element) conditionChildNode;
	            					
	            					ElseIfContext elseIfContext = new ElseIfContext();
	            					
	            					elseIfContext.setCriteria(conditionChildElement.getAttribute(CRITERIA_STRING));
	            					String str = serializer.writeToString(conditionChildElement);
	            					
	            					List<WorkflowExpression> workflowExpressionList = getWorkflowExpressions(str);
	            					
	            					elseIfContext.setWorkflowExpressions(workflowExpressionList);
	            					
	            					conditionContext.addElseIfContext(elseIfContext);
	            				}
	            			}
	            			else if(conditionChildNode.getNodeName().equals(CONDITION_ELSE_STRING)) {
	            				if(conditionChildNode.getNodeType() == Node.ELEMENT_NODE) {
	            					Element conditionChildElement = (Element) conditionChildNode;
	            					
	            					ElseContext elseContext = new ElseContext();
	            					
	            					String str = serializer.writeToString(conditionChildElement);
	            					
	            					List<WorkflowExpression> workflowExpressionList = getWorkflowExpressions(str);
	            					
	            					elseContext.setWorkflowExpressions(workflowExpressionList);
	            					
	            					conditionContext.setElseContext(elseContext);
	            				}
	            			}
	            		}
	            	}
	            	workflowExpressions.add(conditionContext);
	        	}
	        }
	        return workflowExpressions;
		}
	}
	
	private static Condition getConditionObjectFromConditionString(ExpressionContext expressionContext,String conditionString,String moduleName,Integer sequence) throws Exception {
		Pattern condtionStringpattern = Pattern.compile(CONDITION_FORMATTER);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Matcher matcher = condtionStringpattern.matcher(conditionString);
		Condition condition = null;
		while (matcher.find()) {
			String fieldName = matcher.group(2);
			FacilioField field = null;
			String operatorString = matcher.group(7);		// 7
			Operator operator = null;
			
			// For current enpi, module will be param
			if (moduleName.startsWith("$")) {
				if (operatorString.equals("=")) {
					operator = NumberOperators.EQUALS;
				}
				else if (operatorString.equals("between")) {
					operator = DateOperators.BETWEEN;
				}
			}
			else {
				field = modBean.getField(fieldName, moduleName);
				if(FacilioUtil.isNumeric(operatorString)) {
					operator = Operator.getOperator(Integer.parseInt(operatorString));
				}
				else {
					operator = field.getDataTypeEnum().getOperator(operatorString);
				}
			}
			
			String conditionValue = matcher.group(8); // 8
			
			condition = new Condition();
			
			boolean isWithParamCondition = false;
			String testString = conditionString+" test";
			if(testString.split(VARIABLE_PLACE_HOLDER).length > 1) {
				isWithParamCondition = true;
			}
			if (matcher.group(3) != null && !isWithParamCondition) {
				if(operator instanceof DateOperators && ((DateOperators)operator).isBaseLineSupported()) {
					BaseLineContext baseLine = BaseLineAPI.getBaseLine(Long.parseLong(matcher.group(4)));
					if(matcher.group(6) != null && !matcher.group(6).equals("")) {
						Integer isAdjust = Integer.parseInt(matcher.group(6));
						baseLine.setAdjustType(isAdjust);
					}
					else {
						baseLine.setAdjustType(AdjustType.WEEK);
					}
					if (field != null) {	// For current Enpi
						condition = baseLine.getBaseLineCondition(field, ((DateOperators)operator).getRange(conditionValue));
					}
					if(sequence != null) {
						expressionContext.addConditionSeqVsBaselineId(sequence, baseLine.getId());
					}
				}
				else {
					throw new IllegalArgumentException("BaseLine is not supported for this operator");
				}
			}
			else {
				condition = new Condition();
				if (field != null) {
					condition.setField(field);
				}
				else {
					condition.setFieldName(fieldName);
				}
				if(operator == null) {
					LOGGER.severe("conditionString --- "+conditionString+",  operatorString --- "+operatorString);
				}
				condition.setOperator(operator);
				condition.setValue(conditionValue);
				if(operator.equals(LookupOperator.LOOKUP)) {
					Criteria criteria = new Criteria();
					Condition condition2 = new Condition();
					condition2.setComputedWhereClause(conditionValue);
					condition2.setOperator(CommonOperators.IS_EMPTY);
					criteria.addAndCondition(condition2);
					
					condition.setCriteriaValue(criteria);
				}
				if(matcher.group(3) != null && sequence != null) {
					
					if(operator instanceof DateOperators && ((DateOperators)operator).isBaseLineSupported()) {
						if(matcher.group(4) != null) {
							expressionContext.addConditionSeqVsBaselineId(sequence,Long.parseLong(matcher.group(4)));
						}
					}
				}
			}
		}
		return condition;
	}
	// string to Context ends
	
	private static ExpressionContext getExpressionContextFromExpressionString(String expressionString,ExpressionContext expressionContext) throws Exception {
		
		
		try(InputStream stream = new ByteArrayInputStream(expressionString.getBytes("UTF-16"));) {
			
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    	Document doc = dBuilder.parse(stream);
	        doc.getDocumentElement().normalize();
	        
	        NodeList expressionNodes = doc.getElementsByTagName(EXPRESSION_STRING);
	        Node expressionNode = expressionNodes.item(0);
			
			if (expressionNode.getNodeType() == Node.ELEMENT_NODE) {
				
				expressionContext = expressionContext == null ? new ExpressionContext() : expressionContext;
				expressionContext.setExpressionString(expressionString);
	       	 
	            Element expression = (Element) expressionNode;
	            String expressionName = expression.getAttribute(NAME_STRING);
	            
	            expressionContext.setName(expressionName);
	            
	            NodeList valueNodes = expression.getElementsByTagName(CONSTANT_STRING);
	            NodeList functionNodes = expression.getElementsByTagName(FUNCTION_STRING);
	            NodeList exprNodes = expression.getElementsByTagName(EXPR_STRING);
	            NodeList printNodes = expression.getElementsByTagName(PRINT_STRING);
	           
	            if(valueNodes.getLength() > 0) {
	            	Node valueNode =  valueNodes.item(0);
	            	if (valueNode.getNodeType() == Node.ELEMENT_NODE) {
	            		Element value = (Element) valueNode;
	            		String valueString = value.getTextContent();
	            		expressionContext.setConstant(valueString);
	            	}
	            }
	            else if(exprNodes.getLength() > 0 ) {
	            	Node valueNode =  exprNodes.item(0);
	            	if (valueNode.getNodeType() == Node.ELEMENT_NODE) {
	            		Element value = (Element) valueNode;
	            		String valueString = value.getTextContent();
	            		expressionContext.setExpr(valueString);
	            	}
	            }
	            else if(printNodes.getLength() > 0 ) {
	            	Node valueNode =  printNodes.item(0);
	            	if (valueNode.getNodeType() == Node.ELEMENT_NODE) {
	            		Element value = (Element) valueNode;
	            		String valueString = value.getTextContent();
	            		expressionContext.setPrintStatement(valueString);
	            	}
	            }
	            else if (functionNodes.getLength() > 0) {
	            	Node valueNode =  functionNodes.item(0);
	            	if(valueNode.getNodeType() == Node.ELEMENT_NODE) {
	            		Element value = (Element) valueNode;
	            		String valueString = value.getTextContent();
	            		 Pattern condtionStringpattern = Pattern.compile(CUSTOM_FUNCTION_RESULT_EVALUATOR);
	             		Matcher matcher = condtionStringpattern.matcher(valueString);
	             		while (matcher.find()) {
	             			expressionContext.setIsCustomFunctionResultEvaluator(true);
	             			WorkflowFunctionContext defaultFunctionContext = new WorkflowFunctionContext();
	             			defaultFunctionContext.setNameSpace(matcher.group(1));
	             			if(matcher.group(3).contains(".")) {
	             				String[] splits = matcher.group(3).split("\\.");
	             				
	             				String extraNameSpace = splits[0];
	             				String functionName = splits[1];
	             				
	             				defaultFunctionContext.setNameSpace(defaultFunctionContext.getNameSpace()+"."+extraNameSpace);
	             				defaultFunctionContext.setFunctionName(functionName);
	             			}
	             			else {
	             				defaultFunctionContext.setFunctionName(matcher.group(3));
	             			}
	             			if(matcher.group(5) != null) {
	             				defaultFunctionContext.setParams(matcher.group(5));
	             			}
	             			expressionContext.setDefaultFunctionContext(defaultFunctionContext);
	             		}
	            	}
	            }
	            else {
	            	NodeList moduleNodes = expression.getElementsByTagName(MODULE_STRING);
	                Node moduleNode = moduleNodes.item(0);
	                if (moduleNode.getNodeType() == Node.ELEMENT_NODE) {
	                	 Element module = (Element) moduleNode;
	                	 String moduleName = module.getAttribute(NAME_STRING);
	                	 expressionContext.setModuleName(moduleName);
	                }
	                
	                NodeList fieldNodes = expression.getElementsByTagName(FIELD_STRING);
	                if(fieldNodes.getLength() > 0) {
	                    Node fieldNode = fieldNodes.item(0);
	                    if (fieldNode.getNodeType() == Node.ELEMENT_NODE) {
	                    	 
	                    	 Element field = (Element) fieldNode;
	    	            	 String fieldName = field.getAttribute(NAME_STRING);
	    	            	 expressionContext.setFieldName(fieldName);
	    	            	 String aggregate = field.getAttribute(AGGREGATE_STRING);
	    	            	 expressionContext.setAggregateString(aggregate);
	    	            	 
	    	            	 NodeList aggreagteConditionNodes = field.getElementsByTagName(CONDITION_STRING);
	    	            	 for(int j=0;j<aggreagteConditionNodes.getLength();j++) {
	    	            		 
	    	            		 Node aggreagteConditionNode = aggreagteConditionNodes.item(j); 
	    	            		 if (aggreagteConditionNode.getNodeType() == Node.ELEMENT_NODE) {
	    	            			 Element condition = (Element) aggreagteConditionNode;
	    	            			 Condition condition1 = getConditionObjectFromConditionString(expressionContext,condition.getTextContent(),expressionContext.getModuleName(),null);
	    	            			 expressionContext.addAggregateCondition(condition1);
	    	            		 }
	    	            	 }
	                    }
	                }
	                
	                NodeList criteriaNodes = expression.getElementsByTagName(CRITERIA_STRING);
	                Node criteriaNode = criteriaNodes.item(0);
	                if (criteriaNode.getNodeType() == Node.ELEMENT_NODE) {
	                	Element criteria  = (Element) criteriaNode;
	                	String pattern = criteria.getAttribute(PATTERN_STRING);
	                	Criteria criteria1 = new Criteria();
	                	criteria1.setPattern(pattern);
	                	Map<String, Condition> conditions = new HashMap<>();
	                	NodeList conditionNodes = criteria.getElementsByTagName(CONDITION_STRING);
	                	for(int j=0;j<conditionNodes.getLength();j++) {
	                		Condition condition1 = null;
	                		Node conditionNode = conditionNodes.item(j);
	                		if(conditionNode.getNodeType() == Node.ELEMENT_NODE) {
	                			Element condition  = (Element) conditionNode;
	                			String sequence = condition.getAttribute(SEQUENCE_STRING);
	                			condition1 = getConditionObjectFromConditionString(expressionContext,condition.getTextContent(),expressionContext.getModuleName(),Integer.parseInt(sequence));
	                			conditions.put(sequence, condition1);
	                		}
	                	}
	                	criteria1.setConditions(conditions);
	                	expressionContext.setCriteria(criteria1);
	                }
	                
	                NodeList orderByNodes = expression.getElementsByTagName(ORDER_BY_STRING);
	                Node orderByNode = orderByNodes.item(0);
	               
	                if(orderByNode != null && orderByNode.getNodeType() == Node.ELEMENT_NODE) {
	                	Element orderBy  = (Element) orderByNode;
	                	String orderByFieldName = orderBy.getAttribute(NAME_STRING);
	                	String sortString = orderBy.getAttribute(SORT_STRING);
	                	expressionContext.setOrderByFieldName(orderByFieldName);
	                	expressionContext.setSortBy(sortString);
	                }
	                NodeList limitNodes = expression.getElementsByTagName(LIMIT_STRING);
	                Node limitNode = limitNodes.item(0);
	                
	                if(limitNode != null && limitNode.getNodeType() == Node.ELEMENT_NODE) {
	                	
	                	Element limit  = (Element) limitNode;
	                	expressionContext.setLimit(limit.getTextContent());
	                }
	                
	                NodeList groupByNodes = expression.getElementsByTagName(GROUP_BY_STRING);
	                Node groupByNode = groupByNodes.item(0);
	                if(groupByNode != null && groupByNode.getNodeType() == Node.ELEMENT_NODE) {
		                	Element groupBy  = (Element) groupByNode;
		                	expressionContext.setGroupBy(groupBy.getTextContent());
	                }
	            }
	            return expressionContext;
	        }
			return null;
		}
		
	}
	
	// for v2
	public static Object evalSystemFunctions(Map<String, Object> globalParams, WorkflowFunctionContext workflowFunctionContext,List<Object> objects) throws Exception {
		
		FacilioWorkflowFunctionInterface defaultFunctions = getFacilioFunction(workflowFunctionContext.getNameSpace(),workflowFunctionContext.getFunctionName());
		
		Object[] objs = new Object[objects.size()];
		
		for(int i=0;i<objects.size();i++) {
			objs[i] = objects.get(i);
		}
		return defaultFunctions.execute(globalParams, objs);
	}
	
	// for v1
	public static Object evalSystemFunctions(Map<String, Object> globalParams,WorkflowFunctionContext workflowFunctionContext,Map<String,Object> variableToExpresionMap) throws Exception {		
		
		FacilioWorkflowFunctionInterface defaultFunctions = getFacilioFunction(workflowFunctionContext.getNameSpace(),workflowFunctionContext.getFunctionName());
		
		String[] paramList = workflowFunctionContext.getParamList();
		Object[] objects = null;

		int objectIndex = 0;
		if(paramList != null && paramList.length > 0) {
			
			objects = new Object[paramList.length];
			
			for(String param:paramList) {
				
				if(param != null && (param.startsWith("'") &&  param.endsWith("'")) ) {
					// param is a constant
					objects[objectIndex++] = param.substring(1, param.length()-1);
				}
				else {
					Object obj = variableToExpresionMap.get(param);
					objects[objectIndex++] = obj;
				}
			}
		}
		if(defaultFunctions != null) {
			if(objects == null) {
				LOGGER.fine("function params--- IS NULL");
			}
			else {
				LOGGER.fine("function params---"+Arrays.toString(objects));
			}
			return defaultFunctions.execute(globalParams, objects);
		}
		else {
			
			List<Object> paramValues = new ArrayList<>();
			
			if(objects != null) {
				for(int i=0;i<objects.length;i++) {
					paramValues.add(objects[i]);
				}
			}
			
			WorkflowContext wfContext = UserFunctionAPI.getWorkflowFunction(workflowFunctionContext.getNameSpace(), workflowFunctionContext.getFunctionName());
			wfContext.setParams(paramValues);
			wfContext.setGlobalParameters(globalParams);
			
			Object res = wfContext.executeWorkflow();
			
			return res;
		}
	}
	
	public static FacilioWorkflowFunctionInterface getFacilioFunction(String nameSpace,String functionName) {
		
		FacilioWorkflowFunctionInterface facilioWorkflowFunction = null;
		FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.getFacilioDefaultFunction(nameSpace);
		if (nameSpaceEnum != null) {
			switch(nameSpaceEnum) {
				case DEFAULT:
					facilioWorkflowFunction = FacilioDefaultFunction.getFacilioDefaultFunction(functionName);
					break;
				case MATH :
					facilioWorkflowFunction = FacilioMathFunction.getFacilioMathFunction(functionName);
					break;
				case DATE :
					facilioWorkflowFunction = FacilioDateFunction.getFacilioDateFunction(functionName);
					break;
				case THERMOPHYSICALR134A :
					facilioWorkflowFunction = ThermoPhysicalR134aFunctions.getThermoPhysicalR134aFunction(functionName);
					break;
				case COST :
					facilioWorkflowFunction = FacilioCostFunctions.getFacilioCostFunction(functionName);
					break;
				case MAP :
					facilioWorkflowFunction = FacilioMapFunction.getFacilioMapFunction(functionName);
					break;
				case LIST :
					facilioWorkflowFunction = FacilioListFunction.getFacilioListFunction(functionName);
					break;
				case STRING :
					facilioWorkflowFunction = FacilioStringFunction.getFacilioStringFunction(functionName);
					break;
				case READINGS :
					facilioWorkflowFunction = FacilioReadingFunctions.getFacilioReadingFunctions(functionName);
					break;
				case PSYCHROMETRICS :
					facilioWorkflowFunction = FacilioPsychrometricsFunction.getFacilioMathFunction(functionName);
					break;
				case ENERGYMETER :
					facilioWorkflowFunction = FacilioEnergyMeterFunction.getFacilioEnergyMeterFunction(functionName);
					break;
				case MODULE :
					facilioWorkflowFunction = FacilioModuleFunctions.getFacilioModuleFunctions(functionName);
					break;
				case RESOURCE :
					facilioWorkflowFunction = FacilioResourceFunction.getFacilioResourceFunction(functionName);
					break;
				case SYSTEM:
					facilioWorkflowFunction = FacilioSystemFunctions.getFacilioSystemFunction(functionName);
					break;
				case ASSET:
					facilioWorkflowFunction = FacilioAssetFunctions.getFacilioAssetFunction(functionName);
					break;
				case WORKORDER:
					facilioWorkflowFunction = FacilioWorkOrderFunctions.getFacilioWorkOrderFunction(functionName);
					break;
				case CONSUMPTION:
					facilioWorkflowFunction = FacilioConsumptionFunctions.getFacilioConsumptionFunction(functionName);
					break;
				case ML:
					facilioWorkflowFunction = MLFunctions.getFacilioMLFunction(functionName);
					break;
				case NOTIFICATION:
					facilioWorkflowFunction = FacilioNotificationFunctions.getFacilioNotificationFunctions(functionName);
					break;
				case DATE_RANGE:
					facilioWorkflowFunction = FacilioDateRangeFunctions.getFacilioDateFunction(functionName);
					break;
				case FIELD:
					facilioWorkflowFunction = FacilioFieldFunctions.getFacilioFieldFunctions(functionName);
					break;
				case CONNECTION:
					facilioWorkflowFunction = FacilioConnectionFunctions.getFacilioConnectionFunctions(functionName);
					break;
				case CRITERIA:
					facilioWorkflowFunction = FacilioCriteriaFunctions.getFacilioCriteriaFunction(functionName);
					break;
				case ANALYTICS:
					facilioWorkflowFunction = FacilioAnalyticsFunctions.getFacilioAnalyticsFunction(functionName);
					break;
				case NUMBER:
					facilioWorkflowFunction = FacilioNumberFunctions.getFacilioNumberFunction(functionName);
					break;
				case CHAT_BOT:
					facilioWorkflowFunction = FacilioChatBotFunctions.getFacilioChatBotFunctions(functionName);
					break;
				case HTTP:
					facilioWorkflowFunction = FacilioHTTPFunctions.getFacilioHTTPFunctions(functionName);
					break;
				case SCHEDULE:
					facilioWorkflowFunction = FacilioScheduleFunctions.getFacilioScheduleFunctions(functionName);
					break;
				case CONNECTED_APP:
					facilioWorkflowFunction = FacilioConnectedAppFunctions.getFacilioConnectedAppFunction(functionName);
					break;
				case ML_NAMESPACE:
					facilioWorkflowFunction = FacilioMLNameSpaceFunctions.getFacilioMLNameSpaceFunctions(functionName);
					break;
				case ORG_SPECIFIC:
					facilioWorkflowFunction = FacilioOrgSpecificFunctions.getFacilioOrgSpecificFunctions(functionName);
					break;
				case CONTROLS:
					facilioWorkflowFunction = FacilioControlFunctions.getFacilioControlFunctions(functionName);
					break;
				case BUSINESS_HOUR:
					facilioWorkflowFunction = FacilioBusinessHourFunctions.getFacilioBusinessHourFunctions(functionName);
					break;
				case XML_BUILDER:
					facilioWorkflowFunction = FacilioXMLBuilderFunctions.getFacilioXMLBuilderFunctions(functionName);
					break;
				case FILE:
					facilioWorkflowFunction = FacilioFileFunction.getFacilioFileFunction(functionName);
					break;
				case WMS:
					facilioWorkflowFunction = FacilioWMSFunctions.getFacilioWMSFunction(functionName);
					break;
				case CSV_BUILDER:
					facilioWorkflowFunction = FacilioCSVFunctions.getFacilioCSVFunctions(functionName);
					break;
				case EVENT:
					facilioWorkflowFunction = FacilioEventFunctions.getFacilioEventFunction(functionName);
					break;
				case REGEX:
					facilioWorkflowFunction = FacilioRegexFunctions.getFacilioRegexFunction(functionName);
					break;
				case DATABASE_CONNECTION:
					facilioWorkflowFunction = FacilioDBFunction.getFacilioDbcFunction(functionName);
					break;
				case RELATIONSHIP:
					facilioWorkflowFunction = FacilioRelationshipFunctions.getFacilioRelationshipFunction(functionName);
					break;
				case COMMENTS:
					facilioWorkflowFunction = FacilioCommentsFunction.getFacilioCommentsFunctions(functionName);
					break;
				case AUTOMATION:
					facilioWorkflowFunction = FacilioAutomationFunctions.getFacilioAutomationFunction(functionName);
					break;
				case IAM:
					facilioWorkflowFunction = FacilioIAMFunction.getFacilioIAMFunction(functionName);
			}
		}
		return facilioWorkflowFunction;
	}
	
	public static List<FacilioWorkflowFunctionInterface> getFacilioFunctions(String nameSpace) {
		
		List<FacilioWorkflowFunctionInterface> facilioWorkflowFunction = null;
		FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.getFacilioDefaultFunction(nameSpace);
		if (nameSpaceEnum != null) {
			switch(nameSpaceEnum) {
				case DEFAULT:
					facilioWorkflowFunction = new ArrayList<>( FacilioDefaultFunction.getAllFunctions().values());
					break;
				case MATH :
					facilioWorkflowFunction = new ArrayList<>( FacilioMathFunction.getAllFunctions().values());
					break;
				case DATE :
					facilioWorkflowFunction = new ArrayList<>( FacilioDateFunction.getAllFunctions().values());
					break;
				case THERMOPHYSICALR134A :
					facilioWorkflowFunction = new ArrayList<>( ThermoPhysicalR134aFunctions.getAllFunctions().values());
					break;
				case COST :
					facilioWorkflowFunction = new ArrayList<>( FacilioCostFunctions.getAllFunctions().values());
					break;
				case MAP :
					facilioWorkflowFunction = new ArrayList<>( FacilioMapFunction.getAllFunctions().values());
					break;
				case LIST :
					facilioWorkflowFunction = new ArrayList<>( FacilioListFunction.getAllFunctions().values());
					break;
				case STRING :
					facilioWorkflowFunction = new ArrayList<>( FacilioStringFunction.getAllFunctions().values());
					break;
				case READINGS :
					facilioWorkflowFunction = new ArrayList<>( FacilioReadingFunctions.getAllFunctions().values());
					break;
				case PSYCHROMETRICS :
					facilioWorkflowFunction = new ArrayList<>( FacilioPsychrometricsFunction.getAllFunctions().values());
					break;
				case ENERGYMETER :
					facilioWorkflowFunction = new ArrayList<>( FacilioEnergyMeterFunction.getAllFunctions().values());
					break;
				case MODULE :
					facilioWorkflowFunction = new ArrayList<>( FacilioModuleFunctions.getAllFunctions().values());
					break;
				case RESOURCE :
					facilioWorkflowFunction = new ArrayList<>( FacilioResourceFunction.getAllFunctions().values());
					break;
				case SYSTEM:
					facilioWorkflowFunction = new ArrayList<>( FacilioSystemFunctions.getAllFunctions().values());
					break;
				case ASSET:
					facilioWorkflowFunction = new ArrayList<>( FacilioAssetFunctions.getAllFunctions().values());
					break;
				case WORKORDER:
					facilioWorkflowFunction = new ArrayList<>( FacilioWorkOrderFunctions.getAllFunctions().values());
					break;
				case CONSUMPTION:
					facilioWorkflowFunction = new ArrayList<>( FacilioConsumptionFunctions.getAllFunctions().values());
					break;
				case ML:
					facilioWorkflowFunction = new ArrayList<>( MLFunctions.getAllFunctions().values());
					break;
				case NOTIFICATION:
					facilioWorkflowFunction = new ArrayList<>( FacilioNotificationFunctions.getAllFunctions().values());
					break;
				case DATE_RANGE:
					facilioWorkflowFunction = new ArrayList<>( FacilioDateRangeFunctions.getAllFunctions().values());
					break;
				case FIELD:
					facilioWorkflowFunction = new ArrayList<>( FacilioFieldFunctions.getAllFunctions().values());
					break;
				case CONNECTION:
					facilioWorkflowFunction = new ArrayList<>( FacilioConnectionFunctions.getAllFunctions().values());
					break;
				case CRITERIA:
					facilioWorkflowFunction = new ArrayList<>( FacilioCriteriaFunctions.getAllFunctions().values());
					break;
				case ANALYTICS:
					facilioWorkflowFunction = new ArrayList<>( FacilioAnalyticsFunctions.getAllFunctions().values());
					break;
				case NUMBER:
					facilioWorkflowFunction = new ArrayList<>( FacilioNumberFunctions.getAllFunctions().values());
					break;
				case CHAT_BOT:
					facilioWorkflowFunction = new ArrayList<>( FacilioChatBotFunctions.getAllFunctions().values());
					break;
				case HTTP:
					facilioWorkflowFunction = new ArrayList<>( FacilioHTTPFunctions.getAllFunctions().values());
					break;
				case SCHEDULE:
					facilioWorkflowFunction = new ArrayList<>( FacilioScheduleFunctions.getAllFunctions().values());
					break;
				case CONNECTED_APP:
					facilioWorkflowFunction = new ArrayList<>( FacilioConnectedAppFunctions.getAllFunctions().values());
					break;
				case ML_NAMESPACE:
					facilioWorkflowFunction = new ArrayList<>( FacilioMLNameSpaceFunctions.getAllFunctions().values());
					break;
				case ORG_SPECIFIC:
					facilioWorkflowFunction = new ArrayList<>( FacilioOrgSpecificFunctions.getAllFunctions().values());
					break;
				case CONTROLS:
					facilioWorkflowFunction = new ArrayList<>( FacilioControlFunctions.getAllFunctions().values());
					break;
				case BUSINESS_HOUR:
					facilioWorkflowFunction = new ArrayList<>( FacilioBusinessHourFunctions.getAllFunctions().values()); 
					break;
				case XML_BUILDER:
					facilioWorkflowFunction = new ArrayList<>( FacilioXMLBuilderFunctions.getAllFunctions().values());
					break;
				case FILE:
					facilioWorkflowFunction = new ArrayList<>( FacilioFileFunction.getAllFunctions().values());
					break;
				case WMS:
					facilioWorkflowFunction = new ArrayList<>( FacilioWMSFunctions.getAllFunctions().values());
					break;
				case CSV_BUILDER:
					facilioWorkflowFunction = new ArrayList<>( FacilioCSVFunctions.getAllFunctions().values());
					break;
				case EVENT:
					facilioWorkflowFunction = new ArrayList<>( FacilioEventFunctions.getAllFunctions().values());
					break;
				case REGEX:
					facilioWorkflowFunction = new ArrayList<>( FacilioRegexFunctions.getAllFunctions().values());
					break;
				case DATABASE_CONNECTION:
					facilioWorkflowFunction = new ArrayList<>(FacilioDBFunction.getAllFunctions().values());
					break;
				case RELATIONSHIP:
					facilioWorkflowFunction = new ArrayList<>(FacilioRelationshipFunctions.getAllFunctions().values());
					break;
				case COMMENTS:
					facilioWorkflowFunction = new ArrayList<>(FacilioCommentsFunction.getAllFunctions().values());
					break;
				case AUTOMATION:
					facilioWorkflowFunction = new ArrayList<>(FacilioAutomationFunctions.getAllFunctions().values());
					break;
				case IAM:
					facilioWorkflowFunction = new ArrayList<>(FacilioIAMFunction.getAllFunctions().values());
			}
		}
		
		return facilioWorkflowFunction;
	}
	
	public static FacilioFunctionsParamType getFacilioFunctionParam(int value,String fieldName) {
		FacilioFunctionsParamType param = FacilioFunctionsParamType.getFacilioDefaultFunction(value);
		param.setFieldName(fieldName);
		return param;
	}
	
	private static final DecimalFormat getDefaultDecimalFormat() {
		DecimalFormat df = new DecimalFormat("#.#");
		df.setMaximumFractionDigits(20);
		
		return df;
	}
	private static final DecimalFormat DEFAULT_DECIMAL_FORMAT = getDefaultDecimalFormat();
	
	public static Object evaluateExpression(String exp,Map<String,Object> variablesMap, boolean ignoreNullValues) throws Exception {
		if(exp == null) {
			return null;
		}
		exp = exp.trim();
		if(exp.matches(ALPHA_NUMERIC_WITH_UNDERSCORE)) {
			return variablesMap.get(exp);
		}
		Boolean customExRes = evalCustomEx(exp,variablesMap);
		if(customExRes != null) {
			return customExRes;
		}
		Expression expression = new Expression(exp);
		List<String> keys = expression.getUsedVariables();
		if(variablesMap.containsKey("e")) {
			keys.add("e");
		}
		for(String key : keys) {
			String value = "0";
			if(variablesMap.get(key) != null && !StringUtils.isEmpty(variablesMap.get(key).toString())) {
				value = variablesMap.get(key).toString();
				if (NumberUtils.isCreatable(value)) {
					value = DEFAULT_DECIMAL_FORMAT.format(Double.parseDouble(value));
				}
			}
			else if (!ignoreNullValues) {
				return null;
			}
			expression.with(key, value);
		}
		try {
			BigDecimal result = expression.eval();
			return result.doubleValue();
		}
		catch(ArithmeticException e) {
			return null;
		}
		catch (NumberFormatException e) {
			if ("Infinite or NaN".equals(e.getMessage())) {
				return null;
			}
			throw e;
		}
	}
	
	private static Boolean evalCustomEx(String exp,Map<String,Object> variablesMap) {
		
		if(exp.contains(IS_NULL_STRING) || exp.contains(IS_NOT_NULL_STRING)) {
			int index = exp.indexOf(IS_NULL_STRING);
			if(index < 0) {
				index = exp.indexOf(IS_NOT_NULL_STRING);
			}
			String var = exp.substring(0, index).trim();
			Object value = variablesMap.get(var);
			if(exp.contains(IS_NULL_STRING)) {
				if(value == null) {
					return Boolean.TRUE;
				}
				else {
					return Boolean.FALSE;
				}
			}
			else if(exp.contains(IS_NOT_NULL_STRING)) {
				if(value != null) {
					return Boolean.TRUE;
				}
				else {
					return Boolean.FALSE;
				}
			}
 		}
		return null;
	}
	public static ExpressionContext fillParamterAndParseExpressionContext(ExpressionContext expressionContext,Map<String,Object> variableResultMap) throws Exception {
		
		String expressionString = expressionContext.getExpressionString();
		LOGGER.fine("BEFORE STRING --- "+expressionString);
		
		if(expressionContext.getExpressionString().split(VARIABLE_PLACE_HOLDER).length > 1) {
			for(String key : variableResultMap.keySet()) {
				String val = null;
				if (variableResultMap.get(key) != null) {
					val = variableResultMap.get(key).toString();
				}
				else {
					val = "";
				}
				
				String var = "${"+key+"}";
				String varRegex = "\\$\\{"+key+"\\}";
				if(expressionString.contains(var)) {
					String value = val;
					if(value.contains("$")) {
						value = value.replaceAll(Pattern.quote("$"),  Matcher.quoteReplacement("\\$"));
					}
					expressionString = expressionString.replaceAll(varRegex, StringEscapeUtils.escapeXml10(value));
				}
			}
		}
		LOGGER.fine("AFTER STRING --- "+expressionString);
		expressionContext = WorkflowUtil.getExpressionContextFromExpressionString(expressionString,expressionContext);
		
		return expressionContext;
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
	
	public static Criteria parseCriteriaString(String moduleName,String criteriaString) throws Exception {
		
		String CONDITION_SPACE_SEPERATOR = "##";
		
		Pattern condtionStringpattern = Pattern.compile(CONDITION_FORMATTER);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(moduleName);
		
		Map<String, FacilioField> fieldMap = new HashMap<>();
		for (FacilioField field : fields) {
			fieldMap.put(field.getName(), field);
		}
		Map<String, Condition> conditions = new HashMap<>();
		
		String[] values = criteriaString.split(CONDITION_SPACE_SEPERATOR);
		int sequence = 0;
		
		StringBuilder sb =  new StringBuilder();
		for(String value : values) {
			value = value.trim();
			if(value.equals("and")) {
				sb.append("AND ");
			}
			else if(value.equals("or")) {
				sb.append("OR ");
			}
			else if(value.equals("(")) {
				sb.append("( ");
			}
			else if(value.equals(")")) {
				sb.append(") ");
			}
			else{
				Matcher matcher = condtionStringpattern.matcher(value);
				while (matcher.find()) {
					String fieldName = matcher.group(2);
					FacilioField field = modBean.getField(fieldName, moduleName);
					Operator operator = field.getDataTypeEnum().getOperator(matcher.group(7));
					String conditionValue = matcher.group(8);
					
					Condition condition = null;
					if (matcher.group(3) != null) {
						if(operator instanceof DateOperators && ((DateOperators)operator).isBaseLineSupported()) {
							BaseLineContext baseLine = BaseLineAPI.getBaseLine(Long.parseLong(matcher.group(4)));
							
							if(matcher.group(6) != null && !matcher.group(6).equals("")) {
								Integer isAdjust = Integer.parseInt(matcher.group(6));
								baseLine.setAdjustType(isAdjust);
							}
							else {
								baseLine.setAdjustType(AdjustType.WEEK);
							}
							
							condition = baseLine.getBaseLineCondition(field, ((DateOperators)operator).getRange(conditionValue));
						}
						else {
							throw new IllegalArgumentException("BaseLine is not supported for this operator");
						}
					}
					else {
						condition = new Condition();
						condition.setField(field);
						condition.setOperator(operator);
						condition.setValue(conditionValue);
					}
					sequence++;
					sb.append(sequence + " ");
					condition.setSequence(sequence);
					conditions.put(String.valueOf(sequence), condition);
				}
			}
		}
		Criteria criteria = new Criteria();
		criteria.setConditions(conditions);
		criteria.setPattern(sb.toString());
		
		return criteria;
	}
	
	public static String getStringValueFromDouble (Double value) {
		if(value != null)
		{
			DecimalFormat decimalFormat = new DecimalFormat("#.###");
			decimalFormat.setMaximumFractionDigits(340);
		    String convertedString = decimalFormat.format(value);
		    return convertedString;	
		}
		return null;
	}
	
	
	public static Long demoCheckGetEndTime(FacilioModule module,Criteria criteria) {
		if(criteria != null && criteria.getConditions() != null) {
			
			boolean isWithTtime= false;
			Map<String, Condition> conditions = criteria.getConditions();
			for(String key:conditions.keySet()) {
				Condition condition = conditions.get(key);
				if(condition.getFieldName().equals("ttime")) {
					isWithTtime = true;
					break;
				}
			}
			
			if(isWithTtime) {
				Long endTime = DemoHelperUtil.getEndTime(module);
				return endTime;
			}
		}
		return -1l;
	}
	
	public static void sendScriptLogs(WorkflowContext workflowContext, String logs,WorkflowLogStatus statusId,String exception) throws Exception {
        if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.WORKFLOW_LOG) && workflowContext!= null && (workflowContext.getLogType() == WorkflowLogType.WORKFLOW_RULE_EVALUATION || workflowContext.getLogType() == WorkflowLogType.FORMULA)){
            return;
        }
		long orgId = AccountUtil.getCurrentOrg() != null ? AccountUtil.getCurrentOrg().getOrgId() : -1;
		
        if (orgId > 0L && workflowContext.getId() > 0 && workflowContext.getParentId() > 0 && workflowContext.getLogType() != null) {
        	
        	WorkflowLogContext workflowlogcontext = new WorkflowLogContext();
        	workflowlogcontext.setOrgId(orgId);
        	workflowlogcontext.setRecordId(workflowContext.getRecordId());
        	workflowlogcontext.setParentId(workflowContext.getParentId());
        	workflowlogcontext.setWorkflowId(workflowContext.getId());
        	workflowlogcontext.setException(exception);
			workflowlogcontext.setRecordModuleId(workflowContext.getRecordModuleId());
        	workflowlogcontext.setLogType(workflowContext.getLogType());
        	workflowlogcontext.setLogValue(logs);
        	workflowlogcontext.setStatus(statusId.getStatusId());
        	workflowlogcontext.setCreatedTime(System.currentTimeMillis());
        		
        	JSONObject json= FieldUtil.getAsJSON(workflowlogcontext);
        	
        	 SessionManager.getInstance().sendMessage(new Message()
                     .setTopic(ScriptLogHander.TOPIC+"/"+orgId+"/"+workflowContext.getId())
                     .setOrgId(orgId)
                     .setContent(json)
             );
        }
    }

	public static Map<Long, String> getWorkFlowNameFromId(List<Long> workflowIds) throws Exception{
		FacilioModule workflowModule = ModuleFactory.getWorkflowRuleModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getWorkflowRuleFields())
				.table(ModuleFactory.getWorkflowRuleModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(workflowModule), workflowIds, NumberOperators.EQUALS));
		List<Map<String, Object>> props = selectBuilder.get();
		Map<Long, String> idVsName = new HashMap<>();
		if(!CollectionUtils.isEmpty(props)) {
			for(Map<String, Object> prop : props) {
				idVsName.put((Long)prop.get("id"), (String)prop.get("name"));
			}
		}
		return idVsName;
	}

	public static Map<Long, Long> getStateFlowIdFromTransitionId(List<Long> transitionIds) throws Exception{
		FacilioModule transitionModule = ModuleFactory.getStateRuleTransitionModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getStateRuleTransitionFields())
				.table(transitionModule.getTableName())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(transitionModule), transitionIds, NumberOperators.EQUALS));
		List<Map<String, Object>> props = selectBuilder.get();
		Map<Long, Long> transitionIdVsStateFlowId = new HashMap<>();
		if(!CollectionUtils.isEmpty(props)) {
			for(Map<String, Object> prop : props) {
				transitionIdVsStateFlowId.put((Long)prop.get("id"), (Long)prop.get("stateFlowId"));
			}
		}
		return transitionIdVsStateFlowId;
	}

	public  static void throwExceptionIfScriptValidationFailed(WorkflowContext workflowContext) throws Exception{
		if(workflowContext.isV2Script()) {
			ScriptContext scriptContext = ScriptUtil.validateScript(workflowContext.getWorkflowV2String());
			if (scriptContext.getErrorListener().hasErrors()) {
				throw new IllegalArgumentException(scriptContext.getErrorListener().getErrorsAsString());
			}
		}
	}

}
