package com.facilio.bmsconsole.forms;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;

public enum FormActionType {

	SHOW_FIELD(1,"show") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {

			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			JSONArray resultJson = (JSONArray) facilioContext.get(FormRuleAPI.FORM_RULE_RESULT_JSON);
			
			for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
				
				JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.SHOW_FIELD, null);
				resultJson.add(json);
			}
		}
	},
	HIDE_FIELD(2,"hide") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			JSONArray resultJson = (JSONArray) facilioContext.get(FormRuleAPI.FORM_RULE_RESULT_JSON);
			
			for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
				
				JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.HIDE_FIELD, null);
				resultJson.add(json);
			}
			
		}
	},
	ENABLE_FIELD(3,"enable") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			JSONArray resultJson = (JSONArray) facilioContext.get(FormRuleAPI.FORM_RULE_RESULT_JSON);
			
			for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
				
				JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.ENABLE_FIELD, null);
				resultJson.add(json);
			}
		}
	},
	DISABLE_FIELD(4,"disable") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			JSONArray resultJson = (JSONArray) facilioContext.get(FormRuleAPI.FORM_RULE_RESULT_JSON);
			
			for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
				
				JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.DISABLE_FIELD, null);
				resultJson.add(json);
			}
		}
	},
	SET_FIELD_VALUE(5,"set") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			Map<String,Object> formData = (Map<String,Object>) facilioContext.get(FormRuleAPI.FORM_DATA);
			
			JSONArray resultJson = (JSONArray) facilioContext.get(FormRuleAPI.FORM_RULE_RESULT_JSON);
			
			Map<String,Object> tempFormData = new HashMap<>(formData);	
			
			Map<String,Object> placeHolders = new HashMap<>();
			
			CommonCommandUtil.appendModuleNameInKey(formRuleActionContext.getRuleContext().getFormContext().getModule().getName(), formRuleActionContext.getRuleContext().getFormContext().getModule().getName(), tempFormData, placeHolders);
			
			FacilioForm form = (FacilioForm) facilioContext.get(ContextNames.FORM);
			
			for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
				
				String meta = actionField.getActionMeta();
				JSONObject metaJson =  (JSONObject) new JSONParser().parse(meta);
				
				Object value = metaJson.get("setValue");
				if(value != null && value instanceof String && FormRuleAPI.containsPlaceHolders((String)value)) {
					value = FormRuleAPI.replacePlaceHoldersAndGetResult(placeHolders, (String)value);
				}
				
				// Date Field behaviour... TODO check if field is date type and then do the following
				
				value = getDateFieldSetValue(metaJson, resultJson, formData, value, form);
				
				JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.SET_FIELD_VALUE, value);
				resultJson.add(json);
			}
			
		}
	},
	APPLY_FILTER(6,"filter") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			JSONArray resultJson = (JSONArray) facilioContext.get(FormRuleAPI.FORM_RULE_RESULT_JSON);
			
			Map<String,Object> formData = (Map<String,Object>) facilioContext.get(FormRuleAPI.FORM_DATA);
			
			Map<String,Object> tempFormData = new HashMap<>(formData);
			
			Map<String,Object> placeHolders = new HashMap<>();
			
			CommonCommandUtil.appendModuleNameInKey(formRuleActionContext.getRuleContext().getFormContext().getModule().getName(), formRuleActionContext.getRuleContext().getFormContext().getModule().getName(), tempFormData, placeHolders);
			
			for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
				
				if(actionField.getCriteriaId() < 0 && actionField.getCriteria() == null) {
					throw new IllegalArgumentException("No Filter Found");
				}
				Criteria criteria = actionField.getCriteria();
				if(criteria == null) {
					criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getId(), actionField.getCriteriaId());
				}
				
				for(String key : criteria.getConditions().keySet()) {
					
					Condition condition = criteria.getConditions().get(key);
					
					if(condition.getValue() instanceof String && FormRuleAPI.containsPlaceHolders(condition.getValue())) {
						String value = FormRuleAPI.replacePlaceHoldersAndGetResult(placeHolders, condition.getValue());
						condition.setValue(value);
					}
				}
				
				JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.APPLY_FILTER, criteria);
				
				resultJson.add(json);
			}
		}
	},
	ALLOW_CREATE(7,"allowCreate") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			JSONArray resultJson = (JSONArray) facilioContext.get(FormRuleAPI.FORM_RULE_RESULT_JSON);
			
			for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
				
				JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), this, null);
				JSONObject metaJson =  FacilioUtil.parseJson(actionField.getActionMeta());
				json.put("formId", metaJson.get("formId"));
				resultJson.add(json);
			}
		}
	},
	;
	
	private int val;
	private String name;
	
	public int getVal() {
		return val;
	}
	
	public String getName() {
		return name;
	}

	private FormActionType(int val,String name) {
		this.val = val;
		this.name = name;
	}
	
	abstract public void performAction(FacilioContext facilioContext) throws Exception;
	
	
	private static final Map<Integer, FormActionType> TYPE_MAP = Collections.unmodifiableMap(initTypeMap());


	private static Map<Integer, FormActionType> initTypeMap() {
		Map<Integer, FormActionType> typeMap = new HashMap<>();
		for (FormActionType type : values()) {
			typeMap.put(type.getVal(), type);
		}
		return typeMap;
	}
	
	public static FormActionType getActionType(int actionTypeVal) {
		return TYPE_MAP.get(actionTypeVal);
	}
	
	private static Object getDateFieldSetValue(JSONObject metaJson, JSONArray resultJson, Map<String,Object> formData, Object value,FacilioForm form ) throws Exception {
		Boolean setToday = (Boolean) metaJson.get("setToday");
		if (setToday != null && setToday) {
			value = DateTimeUtil.getDayStartTime();
		}
		else if (metaJson.containsKey("dayCount")) {
			Integer dayCount = Integer.parseInt(metaJson.get("dayCount").toString()); // Number of days to be added or subtract
			long time = -1;
			if (metaJson.containsKey("timeFieldId")) {	// Based on the date of this form field, the days will be calculated
				long fieldId = Long.parseLong(metaJson.get("timeFieldId").toString());
				@SuppressWarnings("unchecked")
				JSONObject resultObj =  (JSONObject)resultJson.stream().filter(result -> {
					return (long)((JSONObject)result).get("fieldId") == fieldId;
				}).findFirst().orElse(null);
				if (resultObj != null) {	 // The dependent date is set in the previous action 
					Object timeValue = ((JSONObject)resultObj.get("action")).get("value");
					if (timeValue != null) {
						time = Long.parseLong(timeValue.toString());
					}
				}
				else if(formData != null && form != null) { // The dependent date is got from form data
					FormField field = form.getFields().stream()
							.filter(formField -> formField.getId() == fieldId).findFirst().orElse(null);
					if (formData.get(field.getName()) != null) {
						time = (long) formData.get(field.getName());
					}
				}
			}
			else {
				time = DateTimeUtil.getDayStartTime();
			}
			if (time != -1) {
				Boolean minus = (Boolean) metaJson.get("isMinus");
				if (minus != null && minus) {
					value = DateTimeUtil.minusDays(time, dayCount);
				}
				else {
					value = DateTimeUtil.addDays(time, dayCount);
				}
			}
		}
		return value;
	}
}
