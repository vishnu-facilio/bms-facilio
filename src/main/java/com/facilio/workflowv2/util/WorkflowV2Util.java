package com.facilio.workflowv2.util;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.jobs.SingleResourceHistoricalFormulaCalculatorJob;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.Visitor.FacilioWorkflowFunctionVisitor;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.DataTypeSpecificFunctionContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.ExprContext;
import com.facilio.workflowv2.contexts.DBParamContext;
import com.facilio.workflowv2.contexts.Value;
import com.facilio.workflowv2.contexts.WorkflowNamespaceContext;

public class WorkflowV2Util {

	public static Map<String, String> MODULE_CLASS_MAPPER = new HashMap<>();
	
	public static Map<String, Object> MODULE_OBJECT_CACHE = new HashMap<>();
	
	public static Map<String, String> MODULE_DISPLAY_NAME_MAP = new HashMap<>();

	public static final String MODULE_CLASS_MAPPER_FILE_NAME = "conf/workflowModuleClassMapper.xml";
	
	public static final String DEFAULT_WORKFLOW_FILE_NAME = "conf/defaultWorkflows.json";
	
	public static final String MODULE_CRUD_CLASS_NAME = "com.facilio.workflowv2.modulefunctions.FacilioModuleFunctionImpl";
	
	public static final String CRUD_MODULE_KEY = "default_module";
	
	public static JSONObject defaultWorkflows = new JSONObject();

	static {
		try {
			initWorkflowResource();
		} catch (Exception e) {
			CommonCommandUtil.emailException(WorkflowV2Util.class.getName(), "Workflow Resource init failed", e);
		}
	}
	
	public static Object getInstanceOf(FacilioModule module) throws Exception {
		
		if(MODULE_OBJECT_CACHE.containsKey(module.getName())) {
			return MODULE_OBJECT_CACHE.get(module.getName());
		}
		return MODULE_OBJECT_CACHE.get(CRUD_MODULE_KEY);
	}

	private static void initWorkflowResource() throws Exception {
		
		ClassLoader classLoader = WorkflowV2Util.class.getClassLoader();
		File file = new File(classLoader.getResource(MODULE_CLASS_MAPPER_FILE_NAME).getFile());
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);
				
		doc.getDocumentElement().normalize();
		
		NodeList moduleNodes = doc.getElementsByTagName("module");
		if(moduleNodes.getLength() > 0) {
			for(int i=0;i<moduleNodes.getLength();i++) {
				Node moduleNode = moduleNodes.item(i);
				if (moduleNode.getNodeType() == Node.ELEMENT_NODE) {
	        		Element module  = (Element) moduleNode;
	        		String moduleName = module.getAttribute("name");
	        		String displayName = module.getAttribute("displayname");
	        		String moduleClassName = module.getAttribute("classname");
	        		MODULE_CLASS_MAPPER.put(moduleName, moduleClassName);
	        		MODULE_DISPLAY_NAME_MAP.put(displayName, moduleName);
	        	}
			}
        }
		
		for(String moduleName :MODULE_CLASS_MAPPER.keySet()) {
			String className = MODULE_CLASS_MAPPER.get(moduleName);
			
			Class<?> moduleFunctionClass = classLoader.loadClass(className);
	        Object moduleFunctionObject = moduleFunctionClass.newInstance();
	        MODULE_OBJECT_CACHE.put(moduleName, moduleFunctionObject);
		}
		
		Class<?> moduleFunctionClass = classLoader.loadClass(MODULE_CRUD_CLASS_NAME);
        Object moduleFunctionObject = moduleFunctionClass.newInstance();
        MODULE_OBJECT_CACHE.put(CRUD_MODULE_KEY, moduleFunctionObject);
        
        
        // reading defaultWorkflow.json file       
        JSONParser jsonParser = new JSONParser();
        
        FileReader reader = new FileReader(classLoader.getResource(DEFAULT_WORKFLOW_FILE_NAME).getFile());
        
        defaultWorkflows = (JSONObject)jsonParser.parse(reader);
	}

	public static String getModuleClassNameFromModuleName(String moduleName) {
		return MODULE_CLASS_MAPPER.get(moduleName);
	}
	
	public static String getModuleName(String moduleDisplayName) {
		return MODULE_DISPLAY_NAME_MAP.get(moduleDisplayName);
	}
	
	public static Object getDefaultWorkflowResult(int defaultWorkflowId,List<Object> params) throws Exception {
		JSONObject workflowJson = (JSONObject)defaultWorkflows.get(""+defaultWorkflowId);
		String workflowString = (String) workflowJson.get("workflow");
		WorkflowContext workflow = new WorkflowContext();
		workflow.setWorkflowV2String(workflowString);
		return WorkflowV2API.executeWorkflow(workflow, params, null, true, true, false);
	}
	
	public static void fillExtraInfo(Value paramValue, FacilioModule module) throws Exception {

		if (paramValue.asObject() instanceof DBParamContext || paramValue.asObject() instanceof Criteria) {
			
			Criteria criteria = null;
			if (paramValue.asObject() instanceof DBParamContext) {
				criteria = paramValue.asDbParams().getCriteria();
			}
			else if (paramValue.asObject() instanceof Criteria) {
				criteria = paramValue.asCriteria();
			}
				
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			for (String key : criteria.getConditions().keySet()) {
				Condition condition = criteria.getConditions().get(key);
				FacilioField field = modBean.getField(condition.getFieldName(), module.getName());
				condition.setField(field);
			}
		}
	}

	public static List<Object> getParamList(DataTypeSpecificFunctionContext ctx, boolean isDataTypeSpecificFunction,FacilioWorkflowFunctionVisitor facilioWorkflowFunctionVisitor, Value value) throws Exception {
		List<Object> paramValues = new ArrayList<>();
		if (isDataTypeSpecificFunction) {
			paramValues.add(value.asObject());
		}
		for (ExprContext expr : ctx.expr()) {
			Value paramValue = facilioWorkflowFunctionVisitor.visit(expr);
			if(value != null && value.asObject() instanceof FacilioModule) {
				WorkflowV2Util.fillExtraInfo(paramValue, value.asModule());
			}
			paramValues.add(paramValue.asObject());
		}
		return paramValues;
	}

	public static WorkflowNamespaceContext getNameSpace(String name) throws Exception {

		FacilioModule module = ModuleFactory.getWorkflowNamespaceModule();
		List<FacilioField> fields = FieldFactory.getWorkflowNamespaceFields();

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields)
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), name, StringOperators.IS));

		List<Map<String, Object>> props = selectBuilder.get();

		WorkflowNamespaceContext workflowNamespaceContext = null;
		if (props != null && !props.isEmpty()) {
			workflowNamespaceContext = FieldUtil.getAsBeanFromMap(props.get(0), WorkflowNamespaceContext.class);
		}
		return workflowNamespaceContext;
	}
	
	public static String adjustCriteriaPattern(String criteriaPattern) {
		
    	criteriaPattern = criteriaPattern.replace("||", " or ");
    	criteriaPattern = criteriaPattern.replace("&&", " and ");
    	criteriaPattern = criteriaPattern.substring(1, criteriaPattern.length()-1);
    	return criteriaPattern;
	}

	public static WorkflowContext getWorkflowFunction(String nameSpace, String functionName) throws Exception {

		WorkflowNamespaceContext workflowNamespaceContext = getNameSpace(nameSpace);
		return getWorkflowFunction(workflowNamespaceContext.getId(), functionName);
	}

	public static WorkflowContext getWorkflowFunction(Long nameSpaceId, String functionName) throws Exception {

		FacilioModule module = ModuleFactory.getWorkflowModule();
		List<FacilioField> fields = FieldFactory.getWorkflowFields();

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields)
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("name"), functionName, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("nameSpaceId"), nameSpaceId + "",
						NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();

		WorkflowContext workflowContext = null;
		if (props != null && !props.isEmpty()) {
			workflowContext = FieldUtil.getAsBeanFromMap(props.get(0), WorkflowContext.class);
		}
		return workflowContext;
	}

}
