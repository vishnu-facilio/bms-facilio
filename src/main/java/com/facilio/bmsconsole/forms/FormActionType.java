package com.facilio.bmsconsole.forms;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;

public enum FormActionType {

	SHOW_FIELD(1,"show") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {

			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			JSONArray resultJson = (JSONArray) facilioContext.get(FormRuleAPI.FORM_RULE_RESULT_JSON);
			
			JSONObject json = FormRuleAPI.getActionJson(formRuleActionContext.getFormFieldId(), FormActionType.SHOW_FIELD, null);
			
			resultJson.add(json);
			
		}
	},
	HIDE_FIELD(2,"hide") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			JSONArray resultJson = (JSONArray) facilioContext.get(FormRuleAPI.FORM_RULE_RESULT_JSON);
			
			JSONObject json = FormRuleAPI.getActionJson(formRuleActionContext.getFormFieldId(), FormActionType.HIDE_FIELD, null);
			
			resultJson.add(json);
			
		}
	},
	ENABLE_FIELD(3,"enable") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			JSONArray resultJson = (JSONArray) facilioContext.get(FormRuleAPI.FORM_RULE_RESULT_JSON);
			
			JSONObject json = FormRuleAPI.getActionJson(formRuleActionContext.getFormFieldId(), FormActionType.ENABLE_FIELD, null);
			
			resultJson.add(json);
		}
	},
	DISABLE_FIELD(4,"disable") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			JSONArray resultJson = (JSONArray) facilioContext.get(FormRuleAPI.FORM_RULE_RESULT_JSON);
			
			JSONObject json = FormRuleAPI.getActionJson(formRuleActionContext.getFormFieldId(), FormActionType.DISABLE_FIELD, null);
			
			resultJson.add(json);
		}
	},
	SET_FIELD_VALUE(5,"set") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			Map<String,Object> fromData = (Map<String,Object>) facilioContext.get(FormRuleAPI.FORM_DATA);
			
			JSONArray resultJson = (JSONArray) facilioContext.get(FormRuleAPI.FORM_RULE_RESULT_JSON);
			String meta = formRuleActionContext.getActionMeta();
			JSONObject metaJson =  (JSONObject) new JSONParser().parse(meta);
			
			Object value = metaJson.get("setValue");
			if(value instanceof String && FormRuleAPI.containsPlaceHolders((String)value)) {
				value = FormRuleAPI.replacePlaceHoldersAndGetResult(fromData, (String)value);
			}
			JSONObject json = FormRuleAPI.getActionJson(formRuleActionContext.getFormFieldId(), FormActionType.SET_FIELD_VALUE, value);
			resultJson.add(json);
		}
	},
	APPLY_FILTER(6,"filter") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			if(formRuleActionContext.getCriteriaId() < 0 && formRuleActionContext.getCriteria() == null) {
				throw new IllegalArgumentException("No Filter Found");
			}
			Criteria criteria = formRuleActionContext.getCriteria();
			if(criteria == null) {
				criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getId(), formRuleActionContext.getCriteriaId());
			}
			
			Map<String,Object> fromData = (Map<String,Object>) facilioContext.get(FormRuleAPI.FORM_DATA);
			
			for(String key : criteria.getConditions().keySet()) {
				
				Condition condition = criteria.getConditions().get(key);
				
				if(condition.getValue() instanceof String && FormRuleAPI.containsPlaceHolders(condition.getValue())) {
					String value = FormRuleAPI.replacePlaceHoldersAndGetResult(fromData, condition.getValue());
					condition.setValue(value);
				}
			}
			
			JSONObject json = FormRuleAPI.getActionJson(formRuleActionContext.getFormFieldId(), FormActionType.APPLY_FILTER, criteria);
			
			JSONArray resultJson = (JSONArray) facilioContext.get(FormRuleAPI.FORM_RULE_RESULT_JSON);
			resultJson.add(json);
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
}
