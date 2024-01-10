package com.facilio.workflowv2.util;

import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.collections4.CollectionUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.yaml.snakeyaml.Yaml;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.util.DBConf;
import com.facilio.modules.BmsAggregateOperators.NumberAggregateOperator;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.scriptengine.context.Value;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;

public class WorkflowV2Util {

	private static final String DEFAULT_WORKFLOW_FILE_NAME = FacilioUtil.normalizePath("conf/defaultWorkflows.json");
	
	private static final String DEFAULT_WORKFLOW_YML_FILE_NAME = FacilioUtil.normalizePath("conf/workflowscript/defaultWorkflows.yml");
	
	private static final String WORKFLOW_TEMPLATE_FILE_NAME = FacilioUtil.normalizePath("conf/workflowTemplates.json");
	
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

	public static final String IS_V2 = "IsV2";

	public static Function<List<?>, List<Integer>> anyListToIntList = (objList) -> {
		
		List<Integer> returnList = new ArrayList<Integer>();
		
		objList.forEach((objValue) -> returnList.add(Integer.parseInt(objValue.toString())));
		
		return returnList;
	};

	static {
		try {
			if (!"storm".equalsIgnoreCase(DBConf.getInstance().getService())) {
				initWorkflowResource();
			}
		} catch (Exception e) {
			CommonCommandUtil.emailException(WorkflowV2Util.class.getName(), "Workflow Resource init failed", e);
		}
	}

	private static void initWorkflowResource() throws Exception {
		
		ClassLoader classLoader = WorkflowV2Util.class.getClassLoader();
		
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
        try(InputStream inputStream = FacilioIntEnum.class.getClassLoader().getResourceAsStream(DEFAULT_WORKFLOW_YML_FILE_NAME);) {
        	
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
	
	public static int getWorkflowVersionHistoryMaxVersion(Long workflowId) throws Exception {
    	
		ModuleBean modBean = Constants.getModBean();
		
		FacilioModule module = modBean.getModule(FacilioConstants.Workflow.WORKFLOW_VERSION_HISTORY);
		
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Workflow.WORKFLOW_VERSION_HISTORY);
		
		Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
				.select(new HashSet<>())
				.table(module.getTableName())
				.aggregate(NumberAggregateOperator.MAX, fieldMap.get("version"))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("workflowId"), workflowId+"", NumberOperators.EQUALS))
				;
		
		List<Map<String, Object>> props = select.get();
		
		if(!CollectionUtils.isEmpty(props)) {
			
			return (int)props.get(0).get("version");
		}
		else {
			return 0;
		}
	}
}
