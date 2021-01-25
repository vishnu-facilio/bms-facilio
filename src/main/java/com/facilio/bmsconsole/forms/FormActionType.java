package com.facilio.bmsconsole.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

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
			
			Map<String,Object> placeHolders = new HashMap<>();
			if (MapUtils.isNotEmpty(formData)) {
				Map<String,Object> tempFormData = new HashMap<>(formData);	
				CommonCommandUtil.appendModuleNameInKey(formRuleActionContext.getRuleContext().getFormContext().getModule().getName(), formRuleActionContext.getRuleContext().getFormContext().getModule().getName(), tempFormData, placeHolders);
			}
			
			List<Long> valueFilledFields = (List<Long>) facilioContext.getOrDefault(FormRuleAPI.VALUE_FILLED_FIELD_IDS, new ArrayList<Long>());
			
			facilioContext.put(FormRuleAPI.VALUE_FILLED_FIELD_IDS,valueFilledFields);
			
			Map<String,Object> formDataToBeAddedforNextRound = (Map<String,Object>) facilioContext.getOrDefault(FormRuleAPI.FORM_DATA_FOR_NEXT_ROUND, new HashMap<String,Object>());
			
			facilioContext.put(FormRuleAPI.FORM_DATA_FOR_NEXT_ROUND,formDataToBeAddedforNextRound);
			
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
				
				FormField field = FormsAPI.getFormFieldFromId(actionField.getFormFieldId());
				formDataToBeAddedforNextRound.put(field.getField().getName(), value);
				
				valueFilledFields.add(actionField.getFormFieldId());
			}
			
		}
	},
	APPLY_FILTER(6,"filter") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			JSONArray resultJson = (JSONArray) facilioContext.get(FormRuleAPI.FORM_RULE_RESULT_JSON);
			
			Map<String,Object> formData = (Map<String,Object>) facilioContext.get(FormRuleAPI.FORM_DATA);
			
			Map<String,Object> placeHolders = new HashMap<>();
			if (MapUtils.isNotEmpty(formData)) {
				Map<String,Object> tempFormData = new HashMap<>(formData);
				CommonCommandUtil.appendModuleNameInKey(formRuleActionContext.getRuleContext().getFormContext().getModule().getName(), formRuleActionContext.getRuleContext().getFormContext().getModule().getName(), tempFormData, placeHolders);
			}
			
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
						condition.setComputedWhereClause(null);
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
	EXECUTE_SCRIPT(8,"executeScript") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			Map<String,Object> formData = (Map<String,Object>) facilioContext.get(FormRuleAPI.FORM_DATA);
			
			JSONArray resultJson = (JSONArray) facilioContext.get(FormRuleAPI.FORM_RULE_RESULT_JSON);
			
			WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext(formRuleActionContext.getWorkflowId());
			workflowContext.setLogNeeded(true);
			
			FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();
			
			FacilioContext context = chain.getContext();

			context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflowContext);
			context.put(WorkflowV2Util.WORKFLOW_PARAMS, Collections.singletonList(formData));

			chain.execute();
			
			List<Long> valueFilledFields = (List<Long>) facilioContext.getOrDefault(FormRuleAPI.VALUE_FILLED_FIELD_IDS, new ArrayList<Long>());
			
			facilioContext.put(FormRuleAPI.VALUE_FILLED_FIELD_IDS,valueFilledFields);
			
			Map<String,Object> formDataToBeAddedforNextRound = (Map<String,Object>) facilioContext.getOrDefault(FormRuleAPI.FORM_DATA_FOR_NEXT_ROUND, new HashMap<String,Object>());
			
			facilioContext.put(FormRuleAPI.FORM_DATA_FOR_NEXT_ROUND,formDataToBeAddedforNextRound);
			
			List returnList = (List) workflowContext.getReturnValue();
			
			JSONArray jsonArray = new JSONArray();
			
			if(returnList != null && !returnList.isEmpty()) {
				
				for(int i=0;i<returnList.size();i++) {
					Map<String,Object> fieldMap = (Map<String,Object>) returnList.get(i);
					
					JSONObject fieldJSON = mapToJson(fieldMap);
					
					Long fieldId = (Long) fieldJSON.get(FormRuleAPI.JSON_RESULT_FIELDID_STRING);
					
					JSONObject actionJSON = mapToJson((Map<String, Object>) fieldJSON.get(FormRuleAPI.JSON_RESULT_ACTION_STRING));
					
					String actionName = (String) actionJSON.get(FormRuleAPI.JSON_RESULT_ACTION_NAME_STRING);
					
					if(FormActionType.SET_FIELD_VALUE.getName().equals(actionName)) {
						
						valueFilledFields.add(fieldId);
						
						FormField field = FormsAPI.getFormFieldFromId(fieldId);
						formDataToBeAddedforNextRound.put(field.getField().getName(), actionJSON.get(FormRuleAPI.JSON_RESULT_VALUE_STRING));
					}
					
					jsonArray.add(fieldJSON);
				}
				
				resultJson.addAll(jsonArray);
			}
			
		}

		private JSONObject mapToJson(Map<String, Object> fieldMap) {
			JSONObject json = new JSONObject();
			json.putAll(fieldMap);
			return json;
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
