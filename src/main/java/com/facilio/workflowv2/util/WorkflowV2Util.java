package com.facilio.workflowv2.util;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import org.yaml.snakeyaml.Yaml;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioEnum;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflowv2.Visitor.WorkflowFunctionVisitor;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.ExprContext;
import com.facilio.workflowv2.autogens.WorkflowV2Parser.Recursive_expressionContext;
import com.facilio.workflowv2.contexts.DBParamContext;
import com.facilio.workflowv2.contexts.Value;
import com.facilio.workflowv2.contexts.WorkflowDataParent;

public class WorkflowV2Util {

	public static Map<String, String> MODULE_CLASS_MAPPER = new HashMap<>();
	
	public static Map<String, Object> MODULE_OBJECT_CACHE = new HashMap<>();
	
	public static Map<String, String> MODULE_DISPLAY_NAME_MAP = new HashMap<>();

	private static final String MODULE_CLASS_MAPPER_FILE_NAME = "conf/workflowModuleClassMapper.xml";
	
	private static final String DEFAULT_WORKFLOW_FILE_NAME = "conf/defaultWorkflows.json";
	
	private static final String DEFAULT_WORKFLOW_YML_FILE_NAME = "conf/workflowscript/defaultWorkflows.yml";
	
	private static final String WORKFLOW_TEMPLATE_FILE_NAME = "conf/workflowTemplates.json";
	
	public static final String MODULE_CRUD_CLASS_NAME = "com.facilio.workflowv2.modulefunctions.FacilioModuleFunctionImpl";
	
	public static final String CRUD_MODULE_KEY = "default_module";
	
	public static final Integer SELECT_DEFAULT_LIMIT = 5000;
	
	private static JSONObject defaultWorkflows = new JSONObject();
	
	private static JSONObject workflowTemplates = new JSONObject();
	
	public static final String WORKFLOW_SORT_BY_AGGR_STRING = "aggr";
	
	public static final String WORKFLOW_CONTEXT = "workflow";
	public static final String WORKFLOW_RESPONSE = "workflowResponse";
	public static final String WORKFLOW_LOG_STRING = "workflowLogString";
	public static final String WORKFLOW_USER_FUNCTION_CONTEXT = "workflowUserFunction";
	public static final String WORKFLOW_USER_FUNCTION_CONTEXTS = "workflowUserFunctions";
	public static final String WORKFLOW_NAMESPACE_CONTEXT = "workflowNameSpace";
	public static final String WORKFLOW_NAMESPACE_CONTEXT_LIST = "workflowNameSpaceList";
	public static final String DEFAULT_WORKFLOW_ID = "defaultWorkflowId";
	public static final String WORKFLOW_PARAMS = "workflowParams";
	public static final String SCHEDULED_WORKFLOW_CONTEXT = "scheduledWorkflowContext";
	public static final String SCHEDULED_WORKFLOW_CONTEXT_LIST = "scheduledWorkflowContextList";
	public static final String WORKFLOW_SYNTAX_ERROR = "workflowSyntaxError";
	
	public static final String WORKFLOW_WHERE_STRING = "where";
	
	public static final String SCHEDULED_WORKFLOW_JOB_NAME = "ScheduledWorkflow";
	
	public static final String NEW_NAMESPACE_INITIALIZATION = "NameSpace";
	public static final String NEW_CONNECTION_INITIALIZATION = "Connection";
	

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
        
        try(FileReader reader = new FileReader(classLoader.getResource(DEFAULT_WORKFLOW_FILE_NAME).getFile());) {
        	
        	 defaultWorkflows = (JSONObject)jsonParser.parse(reader);
             
//    		  $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$     CODE TO VALIDATE DEFAULT WORKFLOWS         &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&
           
//           for(Object key :defaultWorkflows.keySet()) {
//           	JSONObject wf = (JSONObject)defaultWorkflows.get(key);
//           	String wfString = (String)wf.get("workflow");
//           	
//           	WorkflowContext workflowContext = new WorkflowContext();
//           	workflowContext.setWorkflowV2String(wfString);
//           	workflowContext.setIsV2Script(true);
//           	
//           	if(workflowContext.validateWorkflow()) {
//           		System.out.println("NO ERROR IN ---- "+key);
//           	}
//           	else {
//           		System.out.println("ERROR IN ---- "+key);
//           	}
//           }
           try (FileReader reader1 = new FileReader(classLoader.getResource(WORKFLOW_TEMPLATE_FILE_NAME).getFile());) {
        	   workflowTemplates = (JSONObject)jsonParser.parse(reader1);
           }
        }
        try(InputStream inputStream = FacilioEnum.class.getClassLoader().getResourceAsStream(DEFAULT_WORKFLOW_YML_FILE_NAME);) {
        	
        	Yaml yaml = new Yaml();
        	Map<String, Object> defaultWorkflowFromYaml = (Map<String, Object>) yaml.load(inputStream);
        	
        	for(String key : defaultWorkflowFromYaml.keySet()) {
        		
        		Map jsonMap = (Map) defaultWorkflowFromYaml.get(key);
        		
        		JSONObject json = FieldUtil.getAsJSON(jsonMap);
        		
        		defaultWorkflowFromYaml.put(key, json);
        	}
        	
        	defaultWorkflows.putAll(defaultWorkflowFromYaml);
        	
        }
        catch (Exception e) {
			e.printStackTrace();
		}
        
	}

	public static String getModuleClassNameFromModuleName(String moduleName) {
		return MODULE_CLASS_MAPPER.get(moduleName);
	}
	
	public static String getModuleName(String moduleDisplayName) {
		return MODULE_DISPLAY_NAME_MAP.get(moduleDisplayName);
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
				
			fillCriteriaField(criteria, module.getName());
		}
	}
	
	public static void fillCriteriaField(Criteria criteria,String moduleName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		for (String key : criteria.getConditions().keySet()) {
			Condition condition = criteria.getConditions().get(key);
			FacilioField field = modBean.getField(condition.getFieldName(), moduleName);
			condition.setField(field);
		}
	}

	public static List<Object> getParamList(Recursive_expressionContext ctx, boolean isDataTypeSpecificFunction,WorkflowFunctionVisitor facilioWorkflowFunctionVisitor, Value value) throws Exception {
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
	
	public static void checkForNullAndThrowException(Value value,String name) {
		if(value == null || value.asObject() == null) {
			throw new RuntimeException("Variable "+name+"'s value is null");
		}
	}

	public static String adjustCriteriaPattern(String criteriaPattern) {
		
    	criteriaPattern = criteriaPattern.replace("||", " or ");
    	criteriaPattern = criteriaPattern.replace("&&", " and ");
    	criteriaPattern = criteriaPattern.substring(1, criteriaPattern.length()-1);
    	return criteriaPattern;
	}
	
	public static Object getDefaultWorkflow(int id) {
		return defaultWorkflows.get(""+id);
	}
	
	public static Object getWorkflowTemplate(int id) {
		return workflowTemplates.get(""+id);
	}
	
	public static JSONObject getAsJSONObject(Map<String, Object> object) {
    	JSONObject json = new JSONObject();

    	if(object == null || object.isEmpty()) {
    		return null;
    	}
	    Iterator<String> keysItr = object.keySet().iterator();
	    while(keysItr.hasNext()) {
	        String key = keysItr.next();
	        Object value = object.get(key);

	        json.put(key, value);
	    }
	    return json;
	}
}
