package com.facilio.bmsconsole.templates;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.facilio.bmsconsole.context.JobPlanContext;
import com.facilio.bmsconsole.context.TicketContext.SourceType;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.util.JobPlanApi;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.util.FacilioUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class FormTemplate extends Template {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long formId = -1;
	public long getFormId() {
		return formId;
	}
	public void setFormId(long formId) {
		this.formId = formId;
	}
	
	private FacilioForm form;
	public void setForm(FacilioForm form) {
		this.form = form;
	}
	public FacilioForm getForm() {
		return this.form;
	}
	
	private JSONObject mappingJson;
	public JSONObject getMappingJson() {
		return mappingJson;
	}
	public void setMappingJson(JSONObject mappingJson) {
		this.mappingJson = mappingJson;
	}
	
	@JSON(serialize = false)
	public String getMappingJsonStr() {
		if (mappingJson != null) {
			return mappingJson.toJSONString();
		}
		return null;
	}
	@JSON(serialize = false)
	public void setMappingJsonStr(String mappingJsonStr) throws ParseException {
		if (mappingJsonStr != null && mappingJson == null) {
			this.mappingJson = FacilioUtil.parseJson(mappingJsonStr);
		}
	}
	
	private SourceType sourceType;
	public int getSourceType() {
		if(sourceType != null) {
			return sourceType.getIndex();
		}
		else {
			return -1;
		}
	}
	public void setSourceType(int type) {
		this.sourceType = SourceType.getType(type);
	}
	public void setSourceType(SourceType type) {
		this.sourceType = type;
	}
	@JSON(serialize = false)
	public SourceType getSourceTypeEnum() {
		return sourceType;
	}
	
	private JSONObject jsonObj;
	
	@Override
	public JSONObject getOriginalTemplate() throws Exception {
		if (jsonObj == null) {
			jsonObj = new JSONObject();
			if (form != null) {
				List<FormField> formFields = form.getFields();
				for(FormField field: formFields) {
					if (field.getValue() != null) {
						boolean valueHandled = handleValue(jsonObj, field);
						if (!valueHandled) {
							jsonObj.put(field.getName(), field.getValue());
						}
					}
				}
				jsonObj.put("formId", form.getId());
			} else if (formId > 0) {
				jsonObj.put("formId", formId);
			}
			if (MapUtils.isNotEmpty(mappingJson)) {
				if (jsonObj == null) {
					jsonObj = new JSONObject();
				}
				jsonObj.putAll(mappingJson);
			}
		}
		return jsonObj;
	}
	
	@JsonIgnore
	@JSON(serialize=false)
	private ModuleBaseWithCustomFields getRecord() throws Exception {
		JSONObject obj = getOriginalTemplate();
		Class className = ContextNames.getClassFromModule(form.getModule());
		return (ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromJson(obj, className);
		
	}
	
	private boolean handleValue(JSONObject jsonObj, FormField field) throws Exception {
		Object value = field.getValue();
		if (field.getField() != null && field.getField().getDataTypeEnum() == FieldType.LOOKUP) {
			if(value != null && !value.equals("{}")) {
				jsonObj.put(field.getName(), getEmptyLookedUpProp(value));
			}
			return true;
		}
		
		switch(form.getModule().getName()) {
			case ContextNames.WORK_ORDER:{
				return handleWorkOrderValue(jsonObj, field);
			}
			case ContextNames.SERVICE_REQUEST:{
				return handleServiceRequestValue(jsonObj, field);
			}
		}
		
		return false;
	}
	
	private boolean handleServiceRequestValue(JSONObject jsonObj, FormField field) throws Exception {
		// TODO Auto-generated method stub
		
		if(handleAssignmentValue(jsonObj, field)) {
			return true;
		}
		return false;
	}
	private Map<String, Object> getEmptyLookedUpProp(Object id) {
		return Collections.singletonMap("id", id);
	}
	
	public boolean handleAssignmentValue(JSONObject jsonObj2, FormField field) throws Exception {
		if (field.getName().equals("assignment")) {
			JSONObject assignmentObj = FacilioUtil.parseJson(field.getValue().toString());
			JSONObject assignedTo = (JSONObject) assignmentObj.get("assignedTo");
			if (assignedTo != null && assignedTo.get("id") != null && !assignedTo.get("id").toString().isEmpty()) {
				jsonObj.put("assignedTo", assignedTo);
			}
			JSONObject assignmentGroup = (JSONObject) assignmentObj.get("assignmentGroup");
			if (assignmentGroup != null && assignmentGroup.get("id") != null && !assignmentGroup.get("id").toString().isEmpty()) {
				jsonObj.put("assignmentGroup", assignmentGroup);
			}
			return true;
		}
		return false;
	}
	
	private boolean handleWorkOrderValue(JSONObject jsonObj, FormField field) throws Exception {
		
		if(handleAssignmentValue(jsonObj, field)) {
			return true;
		}
		
		if (field.getName().equals(ContextNames.TASK_LIST)) {
			JobPlanContext jobPlan = JobPlanApi.getJobPlan(Long.parseLong(field.getValue().toString()));
			jsonObj.put("taskList", FieldUtil.getAsJSON(jobPlan.getTasks()));
			return true;
		}
		return false;
	}

}
