package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.forms.FormActionType;
import com.facilio.bmsconsole.forms.FormRuleActionContext;
import com.facilio.bmsconsole.forms.FormRuleActionFieldsContext;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.forms.FormRuleContext.TriggerType;
import com.facilio.bmsconsole.forms.FormRuleTriggerFieldContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class FormRuleAPI {
	
	
	public static final String FORM_RULE_RESULT_JSON = "formRuleResultJSON";
	public static final String FORM_RULE_CONTEXT = "formRuleContext";
	public static final String FORM_RULE_CONTEXTS = "formRuleContexts";
	public static final String FORM_RULE_ACTION_CONTEXT = "formRuleActionContext";
	
	public static final String FORM_RULE_TRIGGER_TYPE = "formRuleTriggerType";
	
	public static final String FORM_DATA = "formRuleFormData";
	
	public static final String JSON_RESULT_FIELDID_STRING = "fieldId";
	public static final String JSON_RESULT_ACTION_STRING = "action";
	public static final String JSON_RESULT_ACTION_NAME_STRING = "actionName";
	public static final String JSON_RESULT_VALUE_STRING = "value";
	
	
	private static final String FIELD_NAME_PLACE_HOLDER = "\\$\\{(.+?)\\}";
	
	
	public static void addFormRuleContext(FormRuleContext formRuleContext) throws Exception {
		
		formRuleContext.setOrgId(AccountUtil.getCurrentOrg().getId());
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getFormRuleModule().getTableName())
				.fields(FieldFactory.getFormRuleFields());
		
		Map<String, Object> props = FieldUtil.getAsProperties(formRuleContext);
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
				FormRuleActionContext formRuleActionContext = FieldUtil.getAsBeanFromMap(prop, FormRuleActionContext.class);
				
				FormRuleActionFieldsContext formRuleActionFieldContext = FieldUtil.getAsBeanFromMap(prop, FormRuleActionFieldsContext.class);
				
				if(formRuleContext.getCriteriaId() > 0) {
					formRuleContext.setCriteria(CriteriaAPI.getCriteria(formRuleContext.getCriteriaId()));
				}
				
				if(formRuleActionFieldContext.getCriteriaId() > 0 ) {
					formRuleActionFieldContext.setCriteria(CriteriaAPI.getCriteria(formRuleActionFieldContext.getCriteriaId()));
				}
				
				
				formRuleActionContext.setFormRuleActionFieldsContext(Collections.singletonList(formRuleActionFieldContext));
				
				formRuleContext.setActions(Collections.singletonList(formRuleActionContext));
				
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
	
	public static List<FormRuleContext> getFormRuleContext(Long formId,TriggerType triggerType) throws Exception {
		if(formId == null || formId <=0) {
			throw new IllegalArgumentException("Form Cannot Be Null");
		}
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormRuleFields());
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormRuleFields())
				.table(ModuleFactory.getFormRuleModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formId"), ""+formId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("triggerType"), ""+triggerType.getIntVal(), NumberOperators.EQUALS));
		
		
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
	
	public static List<FormRuleContext> getFormRuleContext(Long formId,Long formFieldId,TriggerType triggerType) throws Exception {
		if(triggerType == TriggerType.FIELD_UPDATE && (formFieldId == null || formFieldId <=0)) {
			throw new IllegalArgumentException("Field Cannot Be Null for Action Type Field Update");
		}
		if(formId == null || formId <=0) {
			throw new IllegalArgumentException("Form Cannot Be Null");
		}
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getFormRuleTriggerFieldFields());
		fieldMap.putAll(FieldFactory.getAsMap(FieldFactory.getFormRuleFields()));
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormRuleFields())
				.table(ModuleFactory.getFormRuleModule().getTableName())
				.innerJoin(ModuleFactory.getFormRuleTriggerFieldModule().getTableName())
				.on(ModuleFactory.getFormRuleModule().getTableName()+".ID = "+ModuleFactory.getFormRuleTriggerFieldModule().getTableName()+".RULE_ID")
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("formId"), ""+formId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), ""+formFieldId, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("triggerType"), ""+triggerType.getIntVal(), NumberOperators.EQUALS))
				.orderBy("RULE_EXECUTION_ORDER");
		
		
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
	
	public static List<FormRuleContext> getFormRuleContexts(String moduleName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule module = modBean.getModule(moduleName);
		
		Map<String, FacilioField> formFieldMap = FieldFactory.getAsMap(FieldFactory.getFormFields());
		
		Map<String, FacilioField> formRuleFieldMap = FieldFactory.getAsMap(FieldFactory.getFormRuleFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFormRuleFields())
				.table(ModuleFactory.getFormRuleModule().getTableName())
				.innerJoin(ModuleFactory.getFormModule().getTableName())
				.on("Forms.ID = Form_Rule.FORM_ID")
				.andCondition(CriteriaAPI.getCondition(formFieldMap.get("moduleId"), ""+module.getModuleId(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(formRuleFieldMap.get("type"), ""+FormRuleContext.FormRuleType.FROM_RULE.getIntVal(), NumberOperators.EQUALS))
				;
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			List<FormRuleContext> formRuleContexts = new ArrayList<>();
			for(Map<String, Object> prop :props) {
				FormRuleContext formRuleContext = FieldUtil.getAsBeanFromMap(prop, FormRuleContext.class);
				formRuleContext.setActions(getFormRuleActionContext(formRuleContext.getId()));
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
			
			for(FormRuleActionContext formRuleAction : formRuleActionContext) {
				
				formRuleAction.setFormRuleActionFieldsContext(getFormRuleActionFieldContext(formRuleAction.getId()));
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
	
	public static JSONObject getActionJson(Long fieldId,FormActionType actionType,Object value) {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put(JSON_RESULT_FIELDID_STRING,fieldId);
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
}
