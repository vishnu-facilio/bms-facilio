package com.facilio.bmsconsole.forms;

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
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import com.facilio.workflowv2.util.WorkflowV2Util;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum FormActionType {

	SHOW_FIELD(1,"show") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {

			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);

			if(!CollectionUtils.isEmpty(formRuleActionContext.getFormRuleActionFieldsContext())) {
				for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {

					JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.SHOW_FIELD, null, null);
					FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
				}
			}
		}
	},
	HIDE_FIELD(2,"hide") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {

			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);

			if(!CollectionUtils.isEmpty(formRuleActionContext.getFormRuleActionFieldsContext())) {
				for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {

					JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.HIDE_FIELD, null, null);

					FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
				}
			}
		}
	},
	ENABLE_FIELD(3,"enable") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {

			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);

			if(!CollectionUtils.isEmpty(formRuleActionContext.getFormRuleActionFieldsContext())) {
				for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {

					JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.ENABLE_FIELD, null, null);

					FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
				}
			}
		}
	},
	DISABLE_FIELD(4,"disable") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {

			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);

			if(!CollectionUtils.isEmpty(formRuleActionContext.getFormRuleActionFieldsContext())) {

				for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {

					JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.DISABLE_FIELD, null, null);

					FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
				}
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

				if(formRuleActionContext.getRuleContext().getSubFormId() > 0) {
					Map<String, Object> subFormData = (Map<String, Object>) facilioContext.get(FormRuleAPI.SUB_FORM_DATA);
					if(MapUtils.isNotEmpty(subFormData)) {
						FacilioForm subForm = formRuleActionContext.getRuleContext().getSubFormContext();
						if (formDetails != null) {
							CommonCommandUtil.appendModuleNameInKey(subForm.getModule().getName(), formDetails.getModule().getName()+"."+subForm.getName(), subFormData, placeHolders);
						}
					}
				}
			}


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

					FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
					
				}
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
				if(formRuleActionContext.getRuleContext().getSubFormId() > 0) {
					Map<String, Object> subFormData = (Map<String, Object>) facilioContext.get(FormRuleAPI.SUB_FORM_DATA);
					if(MapUtils.isNotEmpty(subFormData)) {
						FacilioForm subForm = formRuleActionContext.getRuleContext().getSubFormContext();
						if (formDetails != null) {
							CommonCommandUtil.appendModuleNameInKey(formDetails.getModule().getName()+"."+subForm.getName(), formDetails.getModule().getName()+"."+subForm.getName(), subFormData, placeHolders);	
						}
					}
				}
			}
			
			if(!CollectionUtils.isEmpty(formRuleActionContext.getFormRuleActionFieldsContext())) {
				
				for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
					
					//TODO get the field from the form object itself
					FormField formField = FormsAPI.getFormFieldFromId(actionField.getFormFieldId());
					
					if(formField.getField() != null && formField.getField().getDataTypeEnum() == FieldType.ENUM || formField.getField().getDataTypeEnum() == FieldType.MULTI_ENUM || formField.getField().getDataTypeEnum() == FieldType.SYSTEM_ENUM) {
						
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
							
							Condition condition = criteria.getConditions().get(key);
							
							if(condition.getValue() instanceof String && FormRuleAPI.containsPlaceHolders(condition.getValue())) {
								String value = FormRuleAPI.replacePlaceHoldersAndGetResult(placeHolders, condition.getValue());
								condition.setValue(value);
								condition.setComputedWhereClause(null);
							}
						}
						
						JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.APPLY_FILTER, criteria, null);
						
						FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
					}
				}
			}
		}
	},
	ALLOW_CREATE(7,"allowCreate") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			if(!CollectionUtils.isEmpty(formRuleActionContext.getFormRuleActionFieldsContext())) {
				
				for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
					
					JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), this, null, null);
					JSONObject metaJson =  FacilioUtil.parseJson(actionField.getActionMeta());
					json.put("formId", metaJson.get("formId"));
					FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
				}
				
			}
		}
	},
	EXECUTE_SCRIPT(8,"executeScript") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			Map<String,Object> formData = (Map<String,Object>) facilioContext.get(FormRuleAPI.FORM_DATA);

			if(formRuleActionContext.getRuleContext().getSubFormContext()!=null){
				formData.put(ContextNames.FORM_RULE_FORM_OBJECT,formRuleActionContext.getRuleContext().getSubFormContext());
			}else{
				formData.put(ContextNames.FORM_RULE_FORM_OBJECT,formRuleActionContext.getRuleContext().getFormContext());
			}

			JSONArray resultJson = (JSONArray) facilioContext.get(FormRuleAPI.FORM_RULE_RESULT_JSON);
			
			WorkflowContext workflowContext = WorkflowUtil.getWorkflowContext(formRuleActionContext.getWorkflowId());
			workflowContext.setLogNeeded(true);

			FacilioChain chain = TransactionChainFactory.getExecuteWorkflowChain();
			
			FacilioContext context = chain.getContext();

			context.put(WorkflowV2Util.WORKFLOW_CONTEXT, workflowContext);
			context.put(WorkflowV2Util.WORKFLOW_PARAMS, Collections.singletonList(formData));

			chain.execute();
			
			List returnList = (List) workflowContext.getReturnValue();
			
			JSONArray jsonArray = new JSONArray();
			
			if(returnList != null && !returnList.isEmpty()) {
				
				for(int i=0;i<returnList.size();i++) {
					Map<String,Object> fieldMap = (Map<String,Object>) returnList.get(i);
					
					JSONObject fieldJSON = mapToJson(fieldMap);

					if(formRuleActionContext.getRuleContext().getSubFormId()>0){
						FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,fieldJSON);
					}else{
						jsonArray.add(fieldJSON);
					}
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
			
			if(!CollectionUtils.isEmpty(formRuleActionContext.getFormRuleActionFieldsContext())) {
				
				for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
					
					JSONObject json = FormRuleAPI.getActionJson(actionField.getFormSectionId(), FormActionType.SHOW_SECTION, null, FormRuleAPI.JSON_RESULT_SECTIONID_STRING);
				
					FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
				}
			}
		}
	},
	HIDE_SECTION(10,"hideSection") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			if(!CollectionUtils.isEmpty(formRuleActionContext.getFormRuleActionFieldsContext())) {
				
				for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
					
					JSONObject json = FormRuleAPI.getActionJson(actionField.getFormSectionId(), FormActionType.HIDE_SECTION, null, FormRuleAPI.JSON_RESULT_SECTIONID_STRING);
					
					FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
				}
			}
		}
	},
	SET_MANDATORY(11,"setMandatory") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {

			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			if(!CollectionUtils.isEmpty(formRuleActionContext.getFormRuleActionFieldsContext())) {
				
				for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
					
					JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.SET_MANDATORY, null, null);
					
					FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
				}
				
			}
		}
	},
	REMOVE_MANDATORY(12,"removeMandatory") {
		@Override
		public void performAction(FacilioContext facilioContext) throws Exception {
			
			FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
			
			if(!CollectionUtils.isEmpty(formRuleActionContext.getFormRuleActionFieldsContext())) {
				
				for(FormRuleActionFieldsContext actionField : formRuleActionContext.getFormRuleActionFieldsContext()) {
					
					JSONObject json = FormRuleAPI.getActionJson(actionField.getFormFieldId(), FormActionType.REMOVE_MANDATORY, null, null);
					
					FormRuleAPI.AddResultJSONToRespectiveResultSet(facilioContext,json);
				}
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
