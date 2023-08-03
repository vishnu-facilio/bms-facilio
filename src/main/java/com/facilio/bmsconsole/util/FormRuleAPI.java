package com.facilio.bmsconsole.util;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.facilio.db.criteria.Criteria;
import com.facilio.modules.*;
import com.facilio.modules.fields.BaseLookupField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.MultiLookupField;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
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
		props.putIfAbsent("status", true);
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

				String moduleName = getModuleNameFromFormField(formFieldId,formRuleActionContext);
				
				if(StringUtils.isNotEmpty(moduleName) && formRuleActionFields.getCriteria()!=null) {
					CriteriaAPI.updateConditionField(moduleName, formRuleActionFields.getCriteria());
				}

				long id = CriteriaAPI.addCriteria(formRuleActionFields.getCriteria(), AccountUtil.getCurrentOrg().getId());
				formRuleActionFields.setCriteriaId(id);
			}
			
			Map<String, Object> props = FieldUtil.getAsProperties(formRuleActionFields);
			insertBuilder.addRecord(props);
			
		}
		
		insertBuilder.save();

	}

	public static String getModuleNameFromFormField(long formFieldId, FormRuleActionContext formRuleActionContext) throws Exception{

		FormField formField = null;
		String moduleName = null;
		FacilioField field = null;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		if(formFieldId>0) {
			formField = FormsAPI.getFormFieldFromId(formFieldId);
		}

		FacilioUtil.throwIllegalArgumentException(formField == null, "Invalid Form Field Configured.");

		if(formField.getField()==null && formField.getFieldId()>0){
			field = modBean.getField(formField.getFieldId());
		}else{
			field=formField.getField();
		}

		if(field!=null && formRuleActionContext.getActionTypeEnum() == FormActionType.APPLY_FILTER && (field instanceof LookupField || field instanceof MultiLookupField)){
			moduleName = ((BaseLookupField) field).getLookupModule().getName();
		}
		if(field!=null && formRuleActionContext.getActionTypeEnum() != FormActionType.APPLY_FILTER){
			moduleName = field.getModule().getName();
		}

		return moduleName;

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

	public static long getFormRuleCriteriaId(long formRuleId,boolean fetchSubFormCriteriaId) throws Exception{

		FacilioModule formRuleModule = ModuleFactory.getFormRuleModule();
		FacilioField criteriaIdField = null;
		Long oldCriteriaId = -1l;

		if(fetchSubFormCriteriaId){
			criteriaIdField = FieldFactory.getField("subFormCriteriaId", "SUB_FORM_CRITERIA_ID", formRuleModule, FieldType.LOOKUP);
		}else {
			criteriaIdField = FieldFactory.getField("criteriaId", "CRITERIA_ID", formRuleModule, FieldType.LOOKUP);
		}

		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.select(Collections.singletonList(criteriaIdField))
				.table(formRuleModule.getTableName())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(formRuleModule),Collections.singletonList(formRuleId), NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectRecordBuilder.get();

		if(CollectionUtils.isEmpty(props)){
			return -1;
		}

		Map<String,Object> prop = props.get(0);
		if(fetchSubFormCriteriaId){
			oldCriteriaId = (Long) prop.get("subFormCriteriaId");
		}else {
			oldCriteriaId = (Long) prop.get("criteriaId");
		}
		return oldCriteriaId;
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

	public static List<FormRuleContext> getFormRules(Criteria formRuleCriteria) throws Exception {

		FacilioModule formRuleModule = ModuleFactory.getFormRuleModule();
		List<FacilioField> formRuleFields = FieldFactory.getFormRuleFields();

		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.table(formRuleModule.getTableName())
				.select(formRuleFields);

		if (formRuleCriteria != null && !formRuleCriteria.isEmpty()) {
			selectRecordBuilder.andCriteria(formRuleCriteria);
		}
		List<Map<String, Object>> propsList = selectRecordBuilder.get();

		List<FormRuleContext> formRules = FieldUtil.getAsBeanListFromMapList(propsList, FormRuleContext.class);

		return formRules;
	}

	public static void setFormRuleTriggerFields(List<FormRuleContext> formRuleContexts) throws Exception {

		if (CollectionUtils.isEmpty(formRuleContexts)) {
			return;
		}

		List<FacilioField> formRuleTriggerFields = FieldFactory.getFormRuleTriggerFieldFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(formRuleTriggerFields);

		Map<Long, FormRuleContext> ruleIdVsRule = formRuleContexts.stream().collect(Collectors.toMap(FormRuleContext::getId, Function.identity()));
		Set<Long> formRuleIds = ruleIdVsRule.keySet();

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(formRuleTriggerFields)
				.table(ModuleFactory.getFormRuleTriggerFieldModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("ruleId"), formRuleIds, NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();

		if (CollectionUtils.isEmpty(props)) {
			return;
		}

		List<FormRuleTriggerFieldContext> formRuleTriggerFieldContext = FieldUtil.getAsBeanListFromMapList(props, FormRuleTriggerFieldContext.class);

		Map<Long, List<FormRuleTriggerFieldContext>> ruleIdVsTriggerFields = new HashMap<>();

		for (FormRuleTriggerFieldContext formRuleTriggerField : formRuleTriggerFieldContext) {
			long ruleId = formRuleTriggerField.getRuleId();
			if (CollectionUtils.isNotEmpty(ruleIdVsTriggerFields.get(ruleId))) {
				List<FormRuleTriggerFieldContext> triggerFieldContexts = ruleIdVsTriggerFields.get(ruleId);
				triggerFieldContexts.add(formRuleTriggerField);
				ruleIdVsTriggerFields.put(ruleId, triggerFieldContexts);
			} else {
				List<FormRuleTriggerFieldContext> triggerFieldContexts = new ArrayList<>();
				triggerFieldContexts.add(formRuleTriggerField);
				ruleIdVsTriggerFields.put(ruleId, triggerFieldContexts);
			}
		}

		for (FormRuleContext formRuleContext : formRuleContexts) {
			formRuleContext.setTriggerFields(ruleIdVsTriggerFields.get(formRuleContext.getId()));
		}

	}

	public static void setFormRuleActionAndTriggerFieldContext(List<FormRuleContext> formRuleContexts) throws Exception {

		if (CollectionUtils.isEmpty(formRuleContexts)) {
			return;
		}

		FormRuleAPI.setFormRuleTriggerFields(formRuleContexts);

		List<FacilioField> formRuleActionFields = FieldFactory.getFormRuleActionFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(formRuleActionFields);

		Map<Long, FormRuleContext> ruleIdVsRule = formRuleContexts.stream().collect(Collectors.toMap(FormRuleContext::getId, Function.identity()));
		Set<Long> formRuleIds = ruleIdVsRule.keySet();

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(formRuleActionFields)
				.table(ModuleFactory.getFormRuleActionModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formRuleId"), formRuleIds, NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();

		if (CollectionUtils.isEmpty(props)) {
			return;
		}

		List<FormRuleActionContext> formRuleActionContexts = FieldUtil.getAsBeanListFromMapList(props, FormRuleActionContext.class);

		List<Long> workflowIds = new ArrayList<>();
		List<Long> actionIds = new ArrayList<>();

		for (FormRuleActionContext formRuleAction : formRuleActionContexts) {
			if (formRuleAction.getActionTypeEnum() == FormActionType.EXECUTE_SCRIPT) {
				workflowIds.add(formRuleAction.getWorkflowId());
			} else {
				actionIds.add(formRuleAction.getId());
			}
		}
		if (CollectionUtils.isNotEmpty(workflowIds)) {
			Map<Long, WorkflowContext> workflows = WorkflowUtil.getWorkflowsAsMap(workflowIds);
			for (FormRuleActionContext formRuleAction : formRuleActionContexts) {
				if (formRuleAction.getWorkflowId() != -1) {
					formRuleAction.setWorkflow(workflows.get(formRuleAction.getWorkflowId()));
				}
			}
		}
		if (CollectionUtils.isNotEmpty(actionIds)) {
			Map<Long, List<FormRuleActionFieldsContext>> actionIdsVsActionFields = getFormRuleActionFieldContext(actionIds);
			for (FormRuleActionContext formRuleAction : formRuleActionContexts) {
				formRuleAction.setFormRuleActionFieldsContext(actionIdsVsActionFields.get(formRuleAction.getId()));
			}
		}

		Map<Long, List<FormRuleActionContext>> ruleIdVsAction = new HashMap<>();
		for (FormRuleActionContext formRuleActionContext : formRuleActionContexts) {
			long ruleId = formRuleActionContext.getFormRuleId();
			if (CollectionUtils.isNotEmpty(ruleIdVsAction.get(ruleId))) {
				List<FormRuleActionContext> actionContexts = ruleIdVsAction.get(ruleId);
				actionContexts.add(formRuleActionContext);
				ruleIdVsAction.put(ruleId, actionContexts);
			} else {
				List<FormRuleActionContext> actionContexts = new ArrayList<>();
				actionContexts.add(formRuleActionContext);
				ruleIdVsAction.put(ruleId, actionContexts);
			}
		}

		for (FormRuleContext formRuleContext : formRuleContexts) {
			formRuleContext.setActions(ruleIdVsAction.get(formRuleContext.getId()));
		}

	}


	private static Map<Long, List<FormRuleActionFieldsContext>> getFormRuleActionFieldContext(List<Long> actionIds) throws Exception {

		Map<Long, List<FormRuleActionFieldsContext>> actionIdsVsActionFields = new HashMap<>();
		List<FacilioField> formRuleActionFields = FieldFactory.getFormRuleActionFieldsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(formRuleActionFields);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(formRuleActionFields)
				.table(ModuleFactory.getFormRuleActionFieldModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formRuleActionId"), actionIds, NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();

		if (CollectionUtils.isEmpty(props)) {
			return actionIdsVsActionFields;
		}

		List<FormRuleActionFieldsContext> formRuleActionFieldsContexts = FieldUtil.getAsBeanListFromMapList(props, FormRuleActionFieldsContext.class);

		for (FormRuleActionFieldsContext formRuleActionFieldsContext : formRuleActionFieldsContexts) {
			long actionId = formRuleActionFieldsContext.getFormRuleActionId();
			if (CollectionUtils.isNotEmpty(actionIdsVsActionFields.get(actionId))) {
				List<FormRuleActionFieldsContext> actionFieldsContexts = actionIdsVsActionFields.get(actionId);
				actionFieldsContexts.add(formRuleActionFieldsContext);
				actionIdsVsActionFields.put(actionId, actionFieldsContexts);
			} else {
				List<FormRuleActionFieldsContext> actionFieldsContexts = new ArrayList<>();
				actionFieldsContexts.add(formRuleActionFieldsContext);
				actionIdsVsActionFields.put(actionId, actionFieldsContexts);
			}
		}

		return actionIdsVsActionFields;
	}

	public static void deleteFormRule(List<Long> componentIds) throws Exception {

		FacilioModule formRuleModule = ModuleFactory.getFormRuleModule();

		Criteria ruleIdsCriteria = new Criteria();
		ruleIdsCriteria.addAndCondition(CriteriaAPI.getIdCondition(componentIds, formRuleModule));

		List<FormRuleContext> formRuleContexts = FormRuleAPI.getFormRules(ruleIdsCriteria);

		for (FormRuleContext formRuleContext : formRuleContexts) {

			List<FormRuleActionContext> oldactions = FormRuleAPI.getFormRuleActionContext(formRuleContext.getId());

			for (FormRuleActionContext action : oldactions) {
				if (action.getActionTypeEnum() == FormActionType.EXECUTE_SCRIPT) {
					long oldWorkflowId = action.getWorkflowId();
					WorkflowUtil.deleteWorkflow(oldWorkflowId);
				} else {
					for (FormRuleActionFieldsContext actionField : action.getFormRuleActionFieldsContext()) {

						if (actionField.getCriteriaId() > 0) {
							CriteriaAPI.deleteCriteria(actionField.getCriteriaId());
						}
					}
				}

				FormRuleAPI.deleteFormRuleActionContext(action);
			}

			FormRuleAPI.deleteFormRuleContext(formRuleContext);
		}
	}


	public static void setFormRuleCriteria(List<FormRuleContext> formRuleContexts) throws Exception {

		List<Long> criteriaIds = new ArrayList<>();
		List<Long> subFormCriteriaIds = new ArrayList<>();
		for (FormRuleContext formRuleContext : formRuleContexts) {
			long criteriaId = formRuleContext.getCriteriaId();
			long subFormCriteriaId = formRuleContext.getSubFormCriteriaId();
			if (criteriaId > 0) {
				criteriaIds.add(criteriaId);
			}
			if (subFormCriteriaId > 0) {
				subFormCriteriaIds.add(subFormCriteriaId);
			}
		}

		Map<Long, Criteria> criteriaIdVsCriteria = new HashMap<>();
		if (CollectionUtils.isNotEmpty(criteriaIds)) {
			criteriaIdVsCriteria = CriteriaAPI.getCriteriaAsMap(criteriaIds);
		}

		Map<Long, Criteria> criteriaIdVsSubFormCriteria = new HashMap<>();
		if (CollectionUtils.isNotEmpty(subFormCriteriaIds)) {
			criteriaIdVsSubFormCriteria = CriteriaAPI.getCriteriaAsMap(subFormCriteriaIds);
			;
		}

		for (FormRuleContext formRuleContext : formRuleContexts) {
			if (MapUtils.isNotEmpty(criteriaIdVsCriteria)) {
				long criteriaId = formRuleContext.getCriteriaId();
				if (criteriaId > 0) {
					formRuleContext.setCriteria(criteriaIdVsCriteria.get(criteriaId));
				}
			}
			if (MapUtils.isNotEmpty(criteriaIdVsSubFormCriteria)) {
				long subFormCriteriaId = formRuleContext.getSubFormCriteriaId();
				if (subFormCriteriaId > 0) {
					formRuleContext.setSubFormCriteria(criteriaIdVsSubFormCriteria.get(subFormCriteriaId));
				}
			}
		}

	}

}
