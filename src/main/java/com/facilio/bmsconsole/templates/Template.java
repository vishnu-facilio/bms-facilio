package com.facilio.bmsconsole.templates;

import com.facilio.bmsconsole.context.TemplateUrlContext;
import com.facilio.bmsconsole.util.FreeMarkerAPI;
import com.facilio.bmsconsole.util.TemplateAttachmentUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.Serializable;
import java.util.*;

import com.facilio.bmsconsole.context.TemplateUrlContext;
import com.facilio.bmsconsole.util.FreeMarkerAPI;
import com.facilio.bmsconsole.util.TemplateAttachmentUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

import lombok.Getter;


public abstract class Template implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(Template.class.getName());
	private static final String CUSTOM_SCRIPT_NAMESPACE = "cs";
		
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
	
	private long userWorkflowId = -1;
	
	public long getUserWorkflowId() {
		return userWorkflowId;
	}
	public void setUserWorkflowId(long userWorkflowId) {
		this.userWorkflowId = userWorkflowId;
	}

	private long workflowId = -1;
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}
	
	private WorkflowContext userWorkflow;
	
	public WorkflowContext getUserWorkflow() {
		return userWorkflow;
	}
	public void setUserWorkflow(WorkflowContext userWorkflow) {
		this.userWorkflow = userWorkflow;
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
		if (json != null) {

			Map<String, Object> params = new HashMap<>();
			JSONObject parsedJson = new JSONObject();
			executeWorkflow(params, parameters);
			executeUserWorkflow(params, parameters);
			if (MapUtils.isNotEmpty(params)) {
				if (isFtl()) {
					// StrSubstitutor.replace(jsonStr, params);
					for (Object key : json.keySet()) {
						Object value = json.get(key);
						if (value != null) {
							if (value instanceof JSONArray) {
								JSONArray newArray = new JSONArray();
								for (Object arrayVal : (JSONArray) value) {
									newArray.add(FreeMarkerAPI.processTemplate(arrayVal.toString(), params));
								}
								parsedJson.put(key, newArray);
							} else {
								parsedJson.put(key, FreeMarkerAPI.processTemplate(value.toString(), params));
							}
						}
					}
					parameters.put("mailType", "html");
				} else {
					String jsonStr = json.toJSONString();
					try {
						for (String key : params.keySet()) {
							Object value = params.get(key);
							if (value != null) {
								value = StringEscapeUtils.escapeJava(value.toString());
								params.put(key, value);
							}
						}
						jsonStr = StringSubstitutor.replace(jsonStr, params);// StrSubstitutor.replace(jsonStr, params);
					} catch (Exception e) {
						LOGGER.error(new StringBuilder("Error occurred during replacing of place holders \n")
								.append("JSON : ")
								.append(jsonStr)
								.append("\nParams : ")
								.append(params)
								.append("\nParameters : ")
								.append(parameters)
								.toString(), e);
						throw e;
					}
					JSONParser parser = new JSONParser();
					parsedJson = (JSONObject) parser.parse(jsonStr);
				}
			} else {
				parsedJson.putAll(json);
			}

			if (getIsAttachmentAdded()) {
				fetchAttachments();
				if (CollectionUtils.isNotEmpty(getAttachments())) {
					this.getAttachments().stream().filter(att -> att.getType() == TemplateAttachmentType.URL)
							.forEach(att -> {
								TemplateUrlContext urlContext = (TemplateUrlContext) att;
								urlContext.setUrlString(StringSubstitutor.replace(urlContext.getUrlString(), params));
							});
				}
			}
			return parsedJson;
		}
		return json;
	}

	protected void executeWorkflow(Map<String, Object> params, Map<String, Object> parameters) throws Exception {
		WorkflowContext workflow = getWorkflow();
		if (workflow != null) {
			if (workflow.isV2Script()) {
				params.putAll((Map<String, Object>) WorkflowUtil.getWorkflowExpressionResult(workflow, parameters));
			} else {
				params.putAll(WorkflowUtil.getExpressionResultMap(workflow, parameters));
			}
		}
	}

	protected void executeUserWorkflow(Map<String, Object> params, Map<String, Object> parameters) throws Exception {
		if (userWorkflow  != null) {
			Map<String, Object> userParams = (Map<String, Object>) WorkflowUtil.getWorkflowExpressionResult(userWorkflow, parameters);
			if (userParams != null && !userParams.isEmpty()) {
				// Replacing the old params via workflow
				for (String param : userParams.keySet()){
					params.put(CUSTOM_SCRIPT_NAMESPACE + "." +param, userParams.get(param));
				}

			}
		}
	}

	protected void fetchAttachments() throws Exception {
		attachments = TemplateAttachmentUtil.fetchAttachments(getId());
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
		WORKFLOW(18),
		CONTROL_ACTION(19),
		JOB_PLAN_TASK(20),
		JOB_PLAN_SECTION(21),
		PM_PRE_REQUEST(22),
		PM_PRE_REQUEST_SECTION(23),
		PM_PREREQUISITE_APPROVER(24),
		FORM(25),
		WHATSAPP(26),
		CALL(27),
		EMAIL_STRUCTURE(28),
		SATISFACTION_SURVEY_EXECUTION (29),
		SERVICEREQUEST_SATISFACTION_SURVEY_EXECUTION(30),
		PDF_TEMPLATE(31),
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
	
	private Boolean isAttachmentAdded;
	public Boolean getIsAttachmentAdded() {
		if (isAttachmentAdded == null) {
			return false;
		}
		return isAttachmentAdded;
	}
	public void setIsAttachmentAdded(Boolean isAttachmentAdded) {
		this.isAttachmentAdded = isAttachmentAdded;
	}
	
	
	@Getter
	private List<TemplateAttachment> attachments;
	public void addAttachment(TemplateAttachment attachment) {
		if (attachments == null) {
			attachments = new ArrayList<>();
		}
		attachments.add(attachment);
		
	}

}
