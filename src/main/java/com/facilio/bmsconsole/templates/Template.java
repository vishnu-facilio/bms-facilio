package com.facilio.bmsconsole.templates;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.FreeMarkerAPI;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

public abstract class Template implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(Template.class.getName());
	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private long workflowId = -1;
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}
	
	private WorkflowContext workflow;
	public WorkflowContext getWorkflow() {
		return workflow;
	}
	public void setWorkflow(WorkflowContext workflow) {
		this.workflow = workflow;
	}

	private JSONArray placeholder;
	public JSONArray getPlaceholder() {
		return placeholder;
	}
	public void setPlaceholder(JSONArray placeholder) {
		this.placeholder = placeholder;
	}
	
	public String getPlaceholderStr() {
		if(placeholder != null) {
			return placeholder.toJSONString();
		}
		return null;
	}
	public void setPlaceholderStr(String placeholderStr) throws ParseException {
		if(placeholderStr != null) {
			JSONParser parser = new JSONParser();
			placeholder = (JSONArray) parser.parse(placeholderStr);
		}
	}

	private Type type;
	public int getType() {
		if(type != null) {
			return type.getIntVal();
		}
		return -1;
	}
	public Type getTypeEnum() {
		return this.type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public void setType(int type) {
		this.type = Type.TYPE_MAP.get(type);
	}
	
	private Boolean ftl;
	public Boolean getFtl() {
		return ftl;
	}
	public void setFtl(Boolean ftl) {
		this.ftl = ftl;
	}
	public boolean isFtl() {
		if (ftl != null) {
			return ftl.booleanValue();
		}
		return false;
	}
	
	public final JSONObject getTemplate(Map<String, Object> parameters) throws Exception {
		JSONObject json = getOriginalTemplate();
		if (json != null && workflow != null) {
			Map<String, Object> params = WorkflowUtil.getExpressionResultMap(workflow.getWorkflowString(), parameters);
			
			JSONObject parsedJson = null;
			if (isFtl()) {
				// StrSubstitutor.replace(jsonStr, params);
				parsedJson = new JSONObject();
				for (Object key : json.keySet()) {
					Object value = json.get(key);
					if (value instanceof JSONArray) {
						JSONArray newArray = new JSONArray();
						for(Object arrayVal: (JSONArray)value) {
							newArray.add(FreeMarkerAPI.processTemplate(arrayVal.toString(), params));
						}
						parsedJson.put(key, newArray);
					}
					else {
						parsedJson.put(key, FreeMarkerAPI.processTemplate(json.get(key).toString(), params));
					}
				}
				parameters.put("mailType", "html");
			}
			else {
				String jsonStr = json.toJSONString();
				if (AccountUtil.getCurrentOrg().getId() == 155) {
					LOGGER.info("JSON : "+jsonStr);
					LOGGER.info("Params : "+params);
				}
				jsonStr = StringSubstitutor.replace(jsonStr, params);// StrSubstitutor.replace(jsonStr, params);
				JSONParser parser = new JSONParser();
				parsedJson = (JSONObject) parser.parse(jsonStr);
			}
			
			return parsedJson;
		}
		return json;
	}
	
	public abstract JSONObject getOriginalTemplate() throws Exception;
	
	public static enum Type {
		DEFAULT(0),
		EMAIL(1),
		SMS(2),
		JSON(3),
		EXCEL(4),
		WORKORDER(5),
		ALARM(6),
		TASK_GROUP(7),
		PUSH_NOTIFICATION(8),
		WEB_NOTIFICATION(9),
		ASSIGNMENT(10),
		SLA(11),
		TASK_GROUP_TASK(12),
		PM_WORKORDER(13),
		PM_TASK(14),
		PM_TASK_SECTION(15),
		WO_TASK(16),
		WO_TASK_SECTION(17),
		WORKFLOW(18)
		;

		
		private int intVal;
		
		private Type(int intVal) {
			this.intVal = intVal;
		}
		
		public int getIntVal() {
			return intVal;
		}
		
		private static final Map<Integer, Type> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, Type> initTypeMap() {
			Map<Integer, Type> typeMap = new HashMap<>();
			
			for(Type type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}
		public static Map<Integer, Type> getAllTypes() {
			return TYPE_MAP;
		}
		
		public static Type getType(int val) {
			if(TYPE_MAP.containsKey(val)) {
				return TYPE_MAP.get(val);
			}
			else {
				throw new IllegalArgumentException("Invalid Template Type val");
			}
		}
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		try {
			return getOriginalTemplate() == null ? "null" : getOriginalTemplate().toJSONString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
