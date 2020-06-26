package com.facilio.bmsconsole.templates;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.struts2.json.annotations.JSON;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.JobPlanContext;
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
	
	private JSONObject jsonObj;
	
	@Override
	public JSONObject getOriginalTemplate() throws Exception {
		if (form != null && jsonObj == null) {
			List<FormField> formFields = form.getFields();
			jsonObj = new JSONObject();
			for(FormField field: formFields) {
				if (field.getValue() != null) {
					boolean valueHandled = handleValue(jsonObj, field);
					if (!valueHandled) {
						jsonObj.put(field.getName(), field.getValue());
					}
				}
			}
			jsonObj.put("formId", form.getId());
		}
		return jsonObj;
	}
	
	private FacilioForm form;
	public void setForm(FacilioForm form) {
		this.form = form;
	}
	public FacilioForm getForm() {
		return this.form;
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
			jsonObj.put(field.getName(), getEmptyLookedUpProp(value));
			return true;
		}
		
		switch(form.getModule().getName()) {
			case ContextNames.WORK_ORDER:{
				return handleWorkOrderValue(jsonObj, field);
			}
		}
		
		return false;
	}
	
	private Map<String, Object> getEmptyLookedUpProp(Object id) {
		return Collections.singletonMap("id", id);
	}
	
	private boolean handleWorkOrderValue(JSONObject jsonObj, FormField field) throws Exception {
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
		else if (field.getName().equals(ContextNames.TASK_LIST)) {
			JobPlanContext jobPlan = JobPlanApi.getJobPlan(Long.parseLong(field.getValue().toString()));
			jsonObj.put("taskList", FieldUtil.getAsJSON(jobPlan.getTasks()));
			return true;
		}
		return false;
	}

}
