package com.facilio.bmsconsole.util;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.bmsconsole.forms.FormActionType;
import com.facilio.bmsconsole.forms.FormRuleActionContext;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.forms.FormRuleContext.TriggerType;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class FormRuleAPI {
	
	
	public static final String FORM_RULE_RESULT_JSON = "formRuleResultJSON";
	public static final String FORM_RULE_CONTEXT = "formRuleContext";
	public static final String FORM_RULE_ACTION_CONTEXT = "formRuleActionContext";
	
	public static final String FORM_RULE_TRIGGER_TYPE = "formRuleTriggerType";
	
	public static final String FORM_DATA = "formRuleFormData";
	
	public static final String JSON_RESULT_FIELDID_STRING = "fieldId";
	public static final String JSON_RESULT_ACTION_STRING = "action";
	public static final String JSON_RESULT_ACTION_NAME_STRING = "actionName";
	public static final String JSON_RESULT_VALUE_STRING = "value";

	
	public static FormRuleContext getFormRuleContext(long formRuleId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormRuleFields())
				.table(ModuleFactory.getFormRuleModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(formRuleId, ModuleFactory.getFormRuleModule()));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			FormRuleContext formRuleContext = FieldUtil.getAsBeanFromMap(props.get(0), FormRuleContext.class);
			return formRuleContext;
		}
		return null;
	}
	
	public static FormRuleContext getFormRuleContext(Long formId,Long formFieldId,TriggerType triggerType) throws Exception {
		if(triggerType == TriggerType.FIELD_UPDATE && (formFieldId == null || formFieldId <=0)) {
			throw new IllegalArgumentException("Field Cannot Be Null for Action Type Field Update");
		}
		if(formId == null || formId <=0) {
			throw new IllegalArgumentException("Form Cannot Be Null");
		}
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormRuleFields());
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormRuleFields())
				.table(ModuleFactory.getFormRuleModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formId"), ""+formId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("triggerType"), ""+triggerType.getIntVal(), NumberOperators.EQUALS));
		
		if(formFieldId != null && formFieldId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), ""+formFieldId, NumberOperators.EQUALS));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			FormRuleContext formRuleContext = FieldUtil.getAsBeanFromMap(props.get(0), FormRuleContext.class);
			formRuleContext.setActions(getFormRuleActionContext(formRuleContext.getId()));
			return formRuleContext;
		}
		return null;
	}
	
	public static List<FormRuleActionContext> getFormRuleActionContext(long formRuleId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormRuleFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormRuleFields())
				.table(ModuleFactory.getFormRuleModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formRuleId"), ""+formRuleId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<FormRuleActionContext> formRuleActionContext = FieldUtil.getAsBeanListFromMapList(props, FormRuleActionContext.class);
			return formRuleActionContext;
		}
		return null;
	}
	public static JSONObject getActionJson(Long fieldId,FormActionType actionType,String value) {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put(JSON_RESULT_FIELDID_STRING,fieldId);
		JSONObject jsonObject1 = new JSONObject();
		jsonObject1.put(JSON_RESULT_ACTION_NAME_STRING, actionType.getName());
		jsonObject1.put(JSON_RESULT_VALUE_STRING, value);
		jsonObject.put(JSON_RESULT_ACTION_STRING, jsonObject1);
		
		return jsonObject;
	}
}
