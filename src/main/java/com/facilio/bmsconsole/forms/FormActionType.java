package com.facilio.bmsconsole.forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
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
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.LookupField;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;

public enum FormActionType {

	SHOW_FIELD(1,"show") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {

			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
				
				JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.SHOW_FIELD, null, null);
				FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
			}
		}
	},
	HIDE_FIELD(2,"hide") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
				
				JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.HIDE_FIELD, null, null);
				
				FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
			}
		}
	},
	ENABLE_FIELD(3,"enable") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
				
				JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.ENABLE_FIELD, null, null);
				
				FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
			}
		}
	},
	DISABLE_FIELD(4,"disable") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			if(!CollectionUtils.isEmpty(formRuleActionContext.getFormRuleActionFieldsContext())) {
				
				JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.DISABLE_FIELD, null, null);
				
				FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
			}
		}
	},
	SET_FIELD_VALUE(5,"set") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			Map<String,Object> formData = (Map<String,Object>) facilioContext.get(FormRuleAPI.FORM_DATA);
			
			Map<String,Object> placeHolders = new HashMap<>();
			if (MapUtils.isNotEmpty(formData)) {
				FacilioForm formDetails = formRuleActionContext.getRuleContext().getFormContext();
				if (formDetails != null) {
					CommonCommandUtil.appendModuleNameInKey(formDetails.getModule().getName(), formDetails.getModule().getName(), formData, placeHolders);	
				}
			}
			
//			List<Long> valueFilledFields = (List<Long>) facilioContext.getOrDefault(FormRuleAPI.VALUE_FILLED_FIELD_IDS, new ArrayList<Long>());
//			
//			facilioContext.put(FormRuleAPI.VALUE_FILLED_FIELD_IDS,valueFilledFields);
//			
//			Map<String,Object> formDataToBeAddedforNextRound = (Map<String,Object>) facilioContext.getOrDefault(FormRuleAPI.FORM_DATA_FOR_NEXT_ROUND, new HashMap<String,Object>());
//			
//			facilioContext.put(FormRuleAPI.FORM_DATA_FOR_NEXT_ROUND,formDataToBeAddedforNextRound);
			
			FacilioForm form = (FacilioForm) facilioContext.get(ContextNames.FORM);
			
			if(!CollectionUtils.isEmpty(formRuleActionContext.getFormRuleActionFieldsContext())) {
				
				for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
					
					String meta = actionField.getActionMeta();
					JSONObject metaJson =  (JSONObject) new JSONParser().parse(meta);
					
					Object value = metaJson.get("setValue");
					if(value != null && value instanceof String) {
						String val = (String)value;
						if (FormRuleAPI.containsPlaceHolders(val)) {
							value = FormsAPI.resolveDefaultValPlaceholder(val);
							if (value == null) {
								value = FormRuleAPI.replacePlaceHoldersAndGetResult(placeHolders, val);
							}
						}
					}
					
					JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.SET_FIELD_VALUE, value, null);
					
					resultJson.add(json);
					
					//TODO get the field from the form object itself
					FormField field = FormsAPI.getFormFieldFromId(actionField.getFormFieldId());
					if (field.getField() != null && field.getField().getDataTypeEnum() == FieldType.LOOKUP && value != null && !value.equals("")) {
						value = Collections.singletonMap("id", FacilioUtil.parseLong(value));	
					}
					formDataToBeAddedforNextRound.put(field.getName(), value);
					
					valueFilledFields.add(actionField.getFormFieldId());
				}
				
				JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.SET_FIELD_VALUE, value, null);
				
				FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
				
				//TODO get the field from the form object itself
//				FormField field = FormsAPI.getFormFieldFromId(actionField.getFormFieldId());
//				if(formRuleActionContext.getRuleContext().getSubFormId() > 0) {
//					
//					FormRuleAPI.setDataForNextRoundInSubFormData(facilioContext, field, value);
//				}
//				else {
//					
//					formDataToBeAddedforNextRound.put(field.getName(), value);
//				}
//				
//				valueFilledFields.add(actionField.getFormFieldId());
			}
			
		}
	},
	APPLY_FILTER(6,"filter") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			Map<String,Object> formData = (Map<String,Object>) facilioContext.get(FormRuleAPI.FORM_DATA);
			
			Map<String,Object> placeHolders = new HashMap<>();
			if (MapUtils.isNotEmpty(formData)) {
				FacilioForm formDetails = formRuleActionContext.getRuleContext().getFormContext();
				if (formDetails != null) {
					CommonCommandUtil.appendModuleNameInKey(formDetails.getModule().getName(), formDetails.getModule().getName(), formData, placeHolders);	
				}
			}
			
			if(!CollectionUtils.isEmpty(formRuleActionContext.getFormRuleActionFieldsContext())) {
				
				for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
					
					//TODO get the field from the form object itself
					FormField formField = FormsAPI.getFormFieldFromId(actionField.getFormFieldId());
					
					String meta = actionField.getActionMeta();
					JSONObject metaJson =  (JSONObject) new JSONParser().parse(meta);
					JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.APPLY_FILTER, metaJson, null);
					if (json.get("action") != null) {
						JSONObject actionJson = (JSONObject) json.get("action");
						actionJson.put("isEnum", true);
					}
					FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
				}
				else {
					if(actionField.getCriteriaId() < 0 && actionField.getCriteria() == null) {
						throw new IllegalArgumentException("No Filter Found");
					}
					Criteria criteria = actionField.getCriteria();
					if(criteria == null) {
						criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getId(), actionField.getCriteriaId());
					}
					
					for(String key : criteria.getConditions().keySet()) {
						
						if(actionField.getActionMeta() == null) {
							throw new IllegalArgumentException("No Filter Found");
						}
						
						String meta = actionField.getActionMeta();
						JSONObject metaJson =  (JSONObject) new JSONParser().parse(meta);
						JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.APPLY_FILTER, metaJson, null);
						if (json.get("action") != null) {
							JSONObject actionJson = (JSONObject) json.get("action");
							actionJson.put("isEnum", true);
						}
						resultJson.add(json);
					}
					else {
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
						
						JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.APPLY_FILTER, criteria, null);
						
						resultJson.add(json);
					}
					
					JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.APPLY_FILTER, criteria, null);
					
					FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
				}
			}
		}
	},
	ALLOW_CREATE(7,"allowCreate") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			if(!CollectionUtils.isEmpty(formRuleActionContext.getFormRuleActionFieldsContext())) {
				
				JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), this, null, null);
				JSONObject metaJson =  FacilioUtil.parseJson(actionField.getActionMeta());
				json.put("formId", metaJson.get("formId"));
				FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
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
			
//			List<Long> valueFilledFields = (List<Long>) facilioContext.getOrDefault(FormRuleAPI.VALUE_FILLED_FIELD_IDS, new ArrayList<Long>());
//			
//			facilioContext.put(FormRuleAPI.VALUE_FILLED_FIELD_IDS,valueFilledFields);
//			
//			Map<String,Object> formDataToBeAddedforNextRound = (Map<String,Object>) facilioContext.getOrDefault(FormRuleAPI.FORM_DATA_FOR_NEXT_ROUND, new HashMap<String,Object>());
//			
//			facilioContext.put(FormRuleAPI.FORM_DATA_FOR_NEXT_ROUND,formDataToBeAddedforNextRound);
			
			List returnList = (List) workflowContext.getReturnValue();
			
			JSONArray jsonArray = new JSONArray();
			
			if(returnList != null && !returnList.isEmpty()) {
				
				for(int i=0;i<returnList.size();i++) {
					Map<String,Object> fieldMap = (Map<String,Object>) returnList.get(i);
					
					JSONObject fieldJSON = mapToJson(fieldMap);
					
//					Long fieldId = (Long) fieldJSON.get(FormRuleAPI.JSON_RESULT_FIELDID_STRING);
					
//					JSONObject actionJSON = mapToJson((Map<String, Object>) fieldJSON.get(FormRuleAPI.JSON_RESULT_ACTION_STRING));
					
//					String actionName = (String) actionJSON.get(FormRuleAPI.JSON_RESULT_ACTION_NAME_STRING);
					
//					if(FormActionType.SET_FIELD_VALUE.getName().equals(actionName)) {
//						
//						FormField field = FormsAPI.getFormFieldFromId(fieldId);
//						
//						if(formRuleActionContext.getRuleContext().getSubFormId() > 0) {
//							
//							FormRuleAPI.setDataForNextRoundInSubFormData(facilioContext, field, actionJSON.get(FormRuleAPI.JSON_RESULT_VALUE_STRING));
//						}
//						else {
//							valueFilledFields.add(fieldId);
//							formDataToBeAddedforNextRound.put(field.getName(), actionJSON.get(FormRuleAPI.JSON_RESULT_VALUE_STRING));
//						}
//					}
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
	SHOW_SECTION(9,"showSection") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {

			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
				
				JSONObject json = FormRuleAPI.getActionJson(actionField.getFormSectionId(), FormActionType.SHOW_SECTION, null, FormRuleAPI.JSON_RESULT_SECTIONID_STRING);
			
				FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
			}
		}
	},
	HIDE_SECTION(10,"hideSection") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
				
				JSONObject json = FormRuleAPI.getActionJson(actionField.getFormSectionId(), FormActionType.HIDE_SECTION, null, FormRuleAPI.JSON_RESULT_SECTIONID_STRING);
				
				FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
			}
		}
	},
	SET_MANDATORY(11,"setMandatory") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {

			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
				
				JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.SET_MANDATORY, null, null);
				
				FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
			}
		}
	},
	REMOVE_MANDATORY(12,"removeMandatory") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
				
				JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.REMOVE_MANDATORY, null, null);
				
				FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
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
}
