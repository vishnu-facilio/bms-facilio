package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.facilio.db.criteria.Criteria;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormActionType;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.forms.FormRuleActionContext;
import com.facilio.bmsconsole.forms.FormRuleActionFieldsContext;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.forms.FormRuleContext.TriggerType;
import com.facilio.bmsconsole.forms.FormRuleContext.ExecuteType;
import com.facilio.bmsconsole.forms.FormRuleTriggerFieldContext;
import com.facilio.chain.FacilioContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

public class FormRuleAPI {
	
	
	public static final String FORM_RULE_RESULT_JSON = "formRuleResultJSON";
	public static final String SUB_FORM_RULE_RESULT_JSON = "subFormRuleResultJSON";
	public static final String FORM_RULE_CONTEXT = "formRuleContext";
	public static final String FORM_RULE_CONTEXTS = "formRuleContexts";
	public static final String FORM_RULE_ACTION_CONTEXT = "formRuleActionContext";
	
	public static final String FETCH_ONLY_SUB_FORM_RULES = "fetchOnlySubFormRules";
	
	public static final String IS_SUB_FORM_TRIGGER_FIELD = "isSubFormTriggerField";
	
	public static final String FORM_RULE_TRIGGER_TYPE = "formRuleTriggerType";
	public static final String FORM_RULE_EXECUTE_TYPE = "formRuleExecuteType";
	
	public static final String SUB_FORM_DATA_KEY = "relations";
	public static final String SUB_FORM_DATA_ACTIONS_KEY = "actions";
	
	public static final String FORM_DATA = "formRuleFormData";
	public static final String SUB_FORM_DATA = "formRuleSubFormData";
	public static final String SUB_FORM_DATA_INDEX = "formRuleSubFormDataIndex";
	
//	public static final String VALUE_FILLED_FIELD_IDS = "valueFiledFields";
	
//	public static final String FORM_DATA_FOR_NEXT_ROUND = "formDataForNextRound";
	
	public static final String JSON_RESULT_FIELDID_STRING = "fieldId";
	public static final String JSON_RESULT_SECTIONID_STRING = "sectionId";
	public static final String JSON_RESULT_ACTION_STRING = "action";
	public static final String JSON_RESULT_ACTION_NAME_STRING = "actionName";
	public static final String JSON_RESULT_VALUE_STRING = "value";
	
	
	private static final String FIELD_NAME_PLACE_HOLDER = "\\$\\{(.+?)\\}";
	
	
	public static void addFormRuleContext(FormRuleContext formRuleContext) throws Exception {

		if(formRuleContext.getExecuteTypeEnum()==null){
			formRuleContext.setExecuteType(ExecuteType.CREATE_AND_EDIT.getIntVal());
		}
		formRuleContext.setOrgId(AccountUtil.getCurrentOrg().getId());
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getFormRuleModule().getTableName())
				.fields(FieldFactory.getFormRuleFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(formRuleContext);
		props.put("status", true);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		
		formRuleContext.setId((Long) props.get("id"));
		
		if(formRuleContext.getTriggerTypeEnum() == TriggerType.FIELD_UPDATE) {
			if(formRuleContext.getTriggerFields() == null || formRuleContext.getTriggerFields().isEmpty()) {
				throw new Exception("In filed update trigger type trigger fields cannot be empty");
			}
			addFormRuleTriggerFieldsContext(formRuleContext,formRuleContext.getTriggerFields());
		}
	}
	
	public static boolean isFieldExistInRuleAction(List<Long> fieldIds) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormRuleActionFieldsFields());
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormRuleActionFieldsFields())
				.table(ModuleFactory.getFormRuleActionFieldModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formFieldId"), fieldIds, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return true;
		}
		return false;
	}
	
	public static boolean isFieldExistAsRuleTriggerField(List<Long> fieldIds) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormRuleTriggerFieldFields());
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormRuleTriggerFieldFields())
				.table(ModuleFactory.getFormRuleTriggerFieldModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), fieldIds, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return true;
		}
		return false;
	}
	
	public static void addFormRuleTriggerFieldsContext(FormRuleContext formRuleContext, List<FormRuleTriggerFieldContext> triggerFields) throws Exception {
		
		for(FormRuleTriggerFieldContext triggerField : triggerFields) {
			triggerField.setRuleId(formRuleContext.getId());
			triggerField.setOrgId(AccountUtil.getCurrentOrg().getId());
		}
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getFormRuleTriggerFieldModule().getTableName())
				.fields(FieldFactory.getFormRuleTriggerFieldFields());
		
		List<Map<String, Object>> props = FieldUtil.getAsMapList(triggerFields, FormRuleTriggerFieldContext.class);
		insertBuilder.addRecords(props);
		insertBuilder.save();
	}
	
	public static void deleteFormRuleTriggerFieldsContext(long formRuleId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormRuleTriggerFieldFields());
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getFormRuleTriggerFieldModule().getTableName())
		.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleId"), formRuleId+"", NumberOperators.EQUALS));
		
		deleteRecordBuilder.delete();
		
	}
	
	public static Map<Long,FormRuleContext> getFormTypeRulesMap(long formId) throws Exception {
		
		List<FacilioField> fields = FieldFactory.getFormRuleFields();
		fields.addAll(FieldFactory.getFormRuleActionFields());
		fields.addAll(FieldFactory.getFormRuleActionFieldsFields());
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getFormRuleModule().getTableName())
				.innerJoin(ModuleFactory.getFormRuleActionModule().getTableName())
				.on("Form_Rule_Action.FORM_RULE_ID = Form_Rule.ID")
				.innerJoin(ModuleFactory.getFormRuleActionFieldModule().getTableName())
				.on("Form_Rule_Action.id = Form_Rule_Action_Field.FORM_RULE_ACTION_ID")
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formId"), formId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("type"), FormRuleContext.FormRuleType.FROM_FORM.getIntVal()+"", NumberOperators.EQUALS))
				;
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		Map<Long,FormRuleContext> returnMap = new HashMap<Long, FormRuleContext>();
		
		if (props != null && !props.isEmpty()) {
			
			for(Map<String, Object> prop :props) {
				FormRuleContext formRuleContext = FieldUtil.getAsBeanFromMap(prop, FormRuleContext.class);
				formRuleContext.setActions(getFormRuleActionContext(formRuleContext.getId()));
				FormRuleActionFieldsContext formRuleActionFieldContext = formRuleContext.getActions().get(0).getFormRuleActionFieldsContext().get(0);
				
				if (formRuleActionFieldContext.getCriteriaId() > 0) {
					formRuleActionFieldContext.setCriteria(CriteriaAPI.getCriteria(formRuleActionFieldContext.getCriteriaId()));
				}
				
				if(formRuleContext.getCriteriaId() > 0) {
					formRuleContext.setCriteria(CriteriaAPI.getCriteria(formRuleContext.getCriteriaId()));
				}
				if(formRuleContext.getSubFormCriteriaId() > 0) {
					formRuleContext.setSubFormCriteria(CriteriaAPI.getCriteria(formRuleContext.getSubFormCriteriaId()));
				}
				returnMap.put(formRuleActionFieldContext.getFormFieldId(), formRuleContext);

			}
		}
		return returnMap;
	}
	
	public static void addFormRuleActionContext(FormRuleActionContext formRuleActionContext) throws Exception {

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getFormRuleActionModule().getTableName())
				.fields(FieldFactory.getFormRuleActionFields());

		formRuleActionContext.setOrgId(AccountUtil.getCurrentOrg().getId());
		Map<String, Object> props = FieldUtil.getAsProperties(formRuleActionContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		formRuleActionContext.setId((Long) props.get("id"));
	}
	
	public static void addFormRuleActionFieldsContext(FormRuleActionContext formRuleActionContext) throws Exception {

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getFormRuleActionFieldModule().getTableName())
				.fields(FieldFactory.getFormRuleActionFieldsFields());

		for(FormRuleActionFieldsContext formRuleActionFields : formRuleActionContext.getFormRuleActionFieldsContext()) {
			
			formRuleActionFields.setFormRuleActionId(formRuleActionContext.getId());
			formRuleActionFields.setOrgId(AccountUtil.getCurrentOrg().getId());
			
			if(formRuleActionFields.getCriteria() != null) {

				long formFieldId = formRuleActionFields.getFormFieldId();
				FormField formField = null;
				FacilioForm facilioForm = null;
				if(formFieldId>0) {
					formField = FormsAPI.getFormFieldFromId(formFieldId);
				}
				if(formField!=null && formField.getFormId()>0) {
					facilioForm = FormsAPI.getFormFromDB(formField.getFormId());
				}
				if(facilioForm != null && facilioForm.getModule().getName()!=null) {
					CriteriaAPI.updateConditionField(facilioForm.getModule().getName(), formRuleActionFields.getCriteria());
				}
				long id = CriteriaAPI.addCriteria(formRuleActionFields.getCriteria(), AccountUtil.getCurrentOrg().getId());
				formRuleActionFields.setCriteriaId(id);
			}
			
			Map<String, Object> props = FieldUtil.getAsProperties(formRuleActionFields);
			insertBuilder.addRecord(props);
			
		}
		
		insertBuilder.save();

	}
	
	public static void updateFormRuleContext(FormRuleContext formRuleContext) throws Exception {
		
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getFormRuleModule().getTableName())
				.fields(FieldFactory.getFormRuleFields())
				.andCondition(CriteriaAPI.getIdCondition(formRuleContext.getId(), ModuleFactory.getFormRuleModule()));

		Map<String, Object> props = FieldUtil.getAsProperties(formRuleContext);
		updateBuilder.update(props);
		
	}
	
	public static void deleteFormRuleContext(FormRuleContext formRuleContext) throws Exception {
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getFormRuleModule().getTableName())
		.andCondition(CriteriaAPI.getIdCondition(formRuleContext.getId(), ModuleFactory.getFormRuleModule()));
		
		deleteRecordBuilder.delete();
	}
	
	public static void deleteFormRuleActionContext(FormRuleActionContext formRuleActionContext) throws Exception {
		
		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		
		deleteRecordBuilder.table(ModuleFactory.getFormRuleActionModule().getTableName())
		.andCondition(CriteriaAPI.getIdCondition(formRuleActionContext.getId(), ModuleFactory.getFormRuleActionModule()));
		
		deleteRecordBuilder.delete();
	}
	
	public static FormRuleContext getFormRuleContext(long formRuleId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormRuleFields())
				.table(ModuleFactory.getFormRuleModule().getTableName())
				.andCondition(CriteriaAPI.getIdCondition(formRuleId, ModuleFactory.getFormRuleModule()));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			FormRuleContext formRuleContext = FieldUtil.getAsBeanFromMap(props.get(0), FormRuleContext.class);
			formRuleContext.setActions(getFormRuleActionContext(formRuleContext.getId()));
			formRuleContext.setTriggerFields(getFormRuleTriggerFields(formRuleContext));
			
			if (formRuleContext.getCriteriaId() > 0) {
				
				formRuleContext.setCriteria(CriteriaAPI.getCriteria(formRuleContext.getCriteriaId()));
				
			}
			if(formRuleContext.getSubFormCriteriaId() > 0) {
				
				formRuleContext.setSubFormCriteria(CriteriaAPI.getCriteria(formRuleContext.getSubFormCriteriaId()));
			}
			return formRuleContext;
		}
		return null;
	}
	
public static List<FormRuleTriggerFieldContext> getFormRuleTriggerFields(FormRuleContext formRuleContext) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormRuleTriggerFieldFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormRuleTriggerFieldFields())
				.table(ModuleFactory.getFormRuleTriggerFieldModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleId"), ""+formRuleContext.getId(), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<FormRuleTriggerFieldContext> formRuleTriggerFieldContext = FieldUtil.getAsBeanListFromMapList(props, FormRuleTriggerFieldContext.class);
			return formRuleTriggerFieldContext;
		}
		return null;
	}
	
	public static List<FormRuleContext> getFormRuleContext(Long formId,TriggerType triggerType, Map<String, Object> formData,ExecuteType executeType) throws Exception {
		if(formId == null || formId <=0) {
			throw new IllegalArgumentException("Form Cannot Be Null");
		}
		
		List<Long> triggerValues = new ArrayList<>();
		triggerValues.add((long) triggerType.getIntVal());

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormRuleFields());

		Criteria executeTypeCriteria = getExecuteTypeCriteria(fieldMap,executeType);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormRuleFields())
				.table(ModuleFactory.getFormRuleModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formId"), ""+formId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("STATUS", "status", "true", BooleanOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("triggerType"), StringUtils.join(triggerValues, ","), NumberOperators.EQUALS))
				.andCriteria(executeTypeCriteria);
		
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<FormRuleContext> formRuleContexts = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				FormRuleContext formRuleContext = FieldUtil.getAsBeanFromMap(prop, FormRuleContext.class);
				formRuleContext.setActions(getFormRuleActionContext(formRuleContext.getId()));
				formRuleContexts.add(formRuleContext);
			}
		}
		return formRuleContexts;
	}

	private static Criteria getExecuteTypeCriteria(Map<String, FacilioField> fieldMap,ExecuteType executeType){

		Criteria executeTypeCriteria = new Criteria();
		executeTypeCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("executeType"), ""+executeType.getIntVal(), NumberOperators.EQUALS));
		executeTypeCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("executeType"), String.valueOf(ExecuteType.CREATE_AND_EDIT.getIntVal()), NumberOperators.EQUALS));

		return executeTypeCriteria;
	}
	public static List<FormRuleContext> getSubFormRuleContext(Long formId,Long subformId,TriggerType triggerType) throws Exception {
		if(formId == null || formId <=0) {
			throw new IllegalArgumentException("Form Cannot Be Null");
		}
		
		List<Long> triggerValues = new ArrayList<>();
		triggerValues.add((long) triggerType.getIntVal());

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormRuleFields());
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormRuleFields())
				.table(ModuleFactory.getFormRuleModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formId"), ""+formId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("subFormId"), ""+subformId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("triggerType"), StringUtils.join(triggerValues, ","), NumberOperators.EQUALS));
		
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<FormRuleContext> formRuleContexts = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				FormRuleContext formRuleContext = FieldUtil.getAsBeanFromMap(prop, FormRuleContext.class);
				formRuleContext.setActions(getFormRuleActionContext(formRuleContext.getId()));
				formRuleContext.setSubFormContext(FormsAPI.getFormFromDB(formRuleContext.getSubFormId()));
				formRuleContexts.add(formRuleContext);
			}
		}
		return formRuleContexts;
	}
	
	public static List<FormRuleContext> getFormRuleContext(Long formId,List<Long> formFieldId,TriggerType triggerType,ExecuteType executeType) throws Exception {
		if(triggerType == TriggerType.FIELD_UPDATE && (formFieldId == null || formFieldId.isEmpty())) {
			throw new IllegalArgumentException("Field Cannot Be Null for Action Type Field Update");
		}
		if(formId == null || formId <=0) {
			throw new IllegalArgumentException("Form Cannot Be Null");
		}
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormRuleTriggerFieldFields());
		fieldMap.putAll(FieldFactory.getAsMap(FieldFactory.getFormRuleFields()));

		Criteria executeTypeCriteria = getExecuteTypeCriteria(fieldMap, executeType);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormRuleFields())
				.table(ModuleFactory.getFormRuleModule().getTableName())
				.innerJoin(ModuleFactory.getFormRuleTriggerFieldModule().getTableName())
				.on(ModuleFactory.getFormRuleModule().getTableName()+".ID = "+ModuleFactory.getFormRuleTriggerFieldModule().getTableName()+".RULE_ID")
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formId"), ""+formId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), ""+StringUtils.join(formFieldId, ","), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("triggerType"), ""+triggerType.getIntVal(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("STATUS", "status", "true", BooleanOperators.IS))
				.andCriteria(executeTypeCriteria)
				.orderBy("RULE_EXECUTION_ORDER");
		
		
		List<Map<String, Object>> props = selectBuilder.get();
		List<FormRuleContext> formRuleContexts = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				FormRuleContext formRuleContext = FieldUtil.getAsBeanFromMap(prop, FormRuleContext.class);
				formRuleContext.setFormContext(FormsAPI.getFormFromDB(formRuleContext.getFormId()));
				if(formRuleContext.getSubFormId() > 0 ) {
					formRuleContext.setSubFormContext(FormsAPI.getFormFromDB(formRuleContext.getSubFormId()));
				}
				formRuleContext.setActions(getFormRuleActionContext(formRuleContext.getId()));
				formRuleContexts.add(formRuleContext);
			}
		}
		return formRuleContexts;
	}
	
	public static List<FormRuleContext> getFormRuleContexts(String moduleName, Long formId, boolean fetchOnlySubformRules) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modBean.getModule(moduleName);
		
		Map<String, FacilioField> formRuleFieldMap = FieldFactory.getAsMap(FieldFactory.getFormRuleFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormRuleFields())
				.table(ModuleFactory.getFormRuleModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(formRuleFieldMap.get("formId"), ""+formId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(formRuleFieldMap.get("type"), ""+FormRuleContext.FormRuleType.FROM_RULE.getIntVal(), NumberOperators.EQUALS))
				;
		
		if(fetchOnlySubformRules) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(formRuleFieldMap.get("subFormId"), "", CommonOperators.IS_NOT_EMPTY));
		}
		else {
			selectBuilder.andCondition(CriteriaAPI.getCondition(formRuleFieldMap.get("subFormId"), "", CommonOperators.IS_EMPTY));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			List<FormRuleContext> formRuleContexts = new ArrayList<>();
			for(Map<String, Object> prop :props) {
				FormRuleContext formRuleContext = FieldUtil.getAsBeanFromMap(prop, FormRuleContext.class);
				formRuleContext.setTriggerFields(getFormRuleTriggerFields(formRuleContext));
				formRuleContexts.add(formRuleContext);
			}
			return formRuleContexts;
		}
		return null;
	}
	
	public static List<FormRuleActionContext> getFormRuleActionContext(long formRuleId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormRuleActionFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormRuleActionFields())
				.table(ModuleFactory.getFormRuleActionModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formRuleId"), ""+formRuleId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<FormRuleActionContext> formRuleActionContext = FieldUtil.getAsBeanListFromMapList(props, FormRuleActionContext.class);
			
			List<Long> workflowIds = new ArrayList<>();
			for(FormRuleActionContext formRuleAction : formRuleActionContext) {
				if (formRuleAction.getActionTypeEnum() == FormActionType.EXECUTE_SCRIPT) {
					workflowIds.add(formRuleAction.getWorkflowId());
				}
				else {
					formRuleAction.setFormRuleActionFieldsContext(getFormRuleActionFieldContext(formRuleAction.getId()));
				}
			}
			if (!workflowIds.isEmpty()) {
				Map<Long, WorkflowContext> workflows = WorkflowUtil.getWorkflowsAsMap(workflowIds);
				for(FormRuleActionContext formRuleAction : formRuleActionContext) {
					if (formRuleAction.getWorkflowId() != -1) {
						formRuleAction.setWorkflow(workflows.get(formRuleAction.getWorkflowId()));
					}
				}
			}
			return formRuleActionContext;
		}
		return null;
	}
	
	public static List<FormRuleActionFieldsContext> getFormRuleActionFieldContext(long formRuleActionId) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormRuleActionFieldsFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormRuleActionFieldsFields())
				.table(ModuleFactory.getFormRuleActionFieldModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formRuleActionId"), ""+formRuleActionId, NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<FormRuleActionFieldsContext> formRuleActionContext = FieldUtil.getAsBeanListFromMapList(props, FormRuleActionFieldsContext.class);
			return formRuleActionContext;
		}
		return null;
	}
	
	public static JSONObject getActionJson(Long fieldId,FormActionType actionType,Object value, String lable) {
		JSONObject jsonObject = new JSONObject();
		
		if (lable != null && !lable.isEmpty()) {
			jsonObject.put(lable,fieldId);
		}else {
			jsonObject.put(JSON_RESULT_FIELDID_STRING,fieldId);
		}
		JSONObject jsonObject1 = new JSONObject();
		jsonObject1.put(JSON_RESULT_ACTION_NAME_STRING, actionType.getName());
		jsonObject1.put(JSON_RESULT_VALUE_STRING, value);
		jsonObject.put(JSON_RESULT_ACTION_STRING, jsonObject1);
		
		return jsonObject;
	}
	
	public static String replacePlaceHoldersAndGetResult(Map<String,Object> fromData,String value) {
		
		List<String> allMatch = new ArrayList<String>();
		
		Pattern pattern = Pattern.compile(FIELD_NAME_PLACE_HOLDER);
		Matcher matcher = pattern.matcher(value);

		while (matcher.find())
		{
			allMatch.add(matcher.group());
		}
		for(String match :allMatch) {
			String field = match.substring(2, match.length()-1);
			Object val = fromData.get(field);
			
			String replaceValue = "";
			if(val != null) {
				replaceValue = val.toString();
			}
			
			value = value.replaceFirst(Pattern.quote(match), replaceValue);
		}
		return value;
	}

	public static boolean containsPlaceHolders(String value) {
		
		Pattern pattern = Pattern.compile(FIELD_NAME_PLACE_HOLDER);
		Matcher matcher = pattern.matcher(value);
		return matcher.find();
	}
	
//	public static void setDataForNextRoundInSubFormData(FacilioContext facilioContext,FormField field,Object value) {
//		
//		int index = (int) facilioContext.get(FormRuleAPI.SUB_FORM_DATA_INDEX);
//		
//		FormRuleActionContext formRuleActionContext = (FormRuleActionContext) facilioContext.get(FormRuleAPI.FORM_RULE_ACTION_CONTEXT);
//		
//		FacilioForm subForm = formRuleActionContext.getRuleContext().getSubFormContext();
//		
//		Map<String, Object> formDataToBeAddedforNextRound = (Map<String, Object>) facilioContext.get(FormRuleAPI.FORM_DATA_FOR_NEXT_ROUND);
//		
//		Map<String,Object> allSubformData = (Map<String, Object>) formDataToBeAddedforNextRound.getOrDefault(FormRuleAPI.SUB_FORM_DATA_KEY, new HashMap<String,Object>());
//		
//		List<Map<String,Object>> subFromDataList = (List<Map<String, Object>>) allSubformData.getOrDefault(subForm.getName(), new ArrayList<HashMap<String,Object>>());
//		
//		
//		Map<String, Object> subFromData = null;
//		if(subFromDataList.size()-1 >= index) {
//			subFromDataList.get(index);
//		}
//		
//		if(subFromData == null) {
//			subFromData = new HashMap<String,Object>();
//			subFromDataList.add(index, subFromData);
//		}
//			
//		subFromData.put(field.getName(), value);
//		
//		subFromDataList.set(index, subFromData);
//		allSubformData.put(subForm.getName(), subFromDataList);
//		formDataToBeAddedforNextRound.put(FormRuleAPI.SUB_FORM_DATA_KEY, allSubformData);
//		
//	}

	public static void AddResultJSONToRespectiveResultSet(FacilioContext facilioContext, JSONObject json) {
		// TODO Auto-generated method stub
		
		FormRuleContext formRuleContext = (FormRuleContext) facilioContext.get(FormRuleAPI.FORM_RULE_CONTEXT);
		
		if(formRuleContext.getSubFormId() > 0) {
			
			JSONObject subFormRuleResultJSON = (JSONObject) facilioContext.get(FormRuleAPI.SUB_FORM_RULE_RESULT_JSON);
			
			if(!subFormRuleResultJSON.containsKey(formRuleContext.getSubFormContext().getName())) {
				subFormRuleResultJSON.put(formRuleContext.getSubFormContext().getName(), new JSONArray());
			}
			
			JSONArray subFromRecordObjectList = (JSONArray) subFormRuleResultJSON.get(formRuleContext.getSubFormContext().getName());
			
			int index = (int) facilioContext.get(FormRuleAPI.SUB_FORM_DATA_INDEX);
			
			if(subFromRecordObjectList.size()-1 < index) {
				subFromRecordObjectList.add(new JSONObject());
			}
			
			JSONObject subFormRecordObject = (JSONObject) subFromRecordObjectList.get(index);
			
			JSONArray actions = (JSONArray) subFormRecordObject.getOrDefault(SUB_FORM_DATA_ACTIONS_KEY, new JSONArray());
			
			if(json != null) {
				actions.add(json);
			}
			
			subFormRecordObject.put(SUB_FORM_DATA_ACTIONS_KEY, actions);
		}
		else {
			JSONArray resultJson = (JSONArray) facilioContext.get(FormRuleAPI.FORM_RULE_RESULT_JSON);
			resultJson.add(json);
		}
		
	}
}
