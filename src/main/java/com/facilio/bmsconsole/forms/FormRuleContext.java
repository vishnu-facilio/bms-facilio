package com.facilio.bmsconsole.forms;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FieldUtil;

public class FormRuleContext {
	long id = -1;
	long orgId = -1;
	long formId = -1;
	long subFormId = -1;
	FacilioForm formContext;
	FacilioForm subFormContext;
	long criteriaId = -1;
	long subFormCriteriaId = -1;
	long siteId = -1;
	List<FormRuleTriggerFieldContext> triggerFields;
	
	String name;
	String description;
	Criteria subFormCriteria;
	Criteria criteria;
	FormRuleType type;
	TriggerType triggerType;
	ExecuteType executeType;

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	String moduleName;
	Form_On_Load_Rule_Type formOnLoadRuleType;
	RuleType ruleType;
	
	Boolean status = true;
	
	public Boolean getStatus() {
		if (status == null) {
			return true;
		}
		return status;
	}
	
	public void setStatus(Boolean status) {
		this.status = status;
	}
	
	public long getSiteId() {
		return siteId;
	}
	
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	List<FormRuleActionContext> actions;
	
	public List<FormRuleActionContext> getActions() {
		return actions;
	}
	
	public List<FormRuleTriggerFieldContext> getTriggerFields() {
		return triggerFields;
	}

	public void setTriggerFields(List<FormRuleTriggerFieldContext> triggerFields) {
		this.triggerFields = triggerFields;
	}

	public void setActions(List<FormRuleActionContext> actions) {
		this.actions = actions;
	}
	
	public int getType() {
		if(type != null) {
			return type.getIntVal();
		}
		return -1;
	}
	public FormRuleType getTypeEnum() {
		return type;
	}

	public void setType(int type) {
		this.type = FormRuleType.getAllRuleType().get(type);
	}

	private Boolean isDefault = false;
	public Boolean getIsDefault() {
		return isDefault;
	}
	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	private List<String> appLinkNamesForRule;
	public List<String> getAppLinkNamesForRule() {
		return appLinkNamesForRule;
	}
	public void setAppLinkNamesForRule(List<String> appLinkNamesForRule) {
		this.appLinkNamesForRule = appLinkNamesForRule;
	}

	public boolean evaluateCriteria (Map<String, Object> record,FacilioContext context) throws Exception {
		boolean criteriaFlag = true;
		criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getId(), criteriaId);
		record= FieldUtil.getAsProperties(CriteriaAPI.setLookupFieldsData(criteria,record));
		if(criteria != null && record != null) {
			criteriaFlag = criteria.computePredicate(record).evaluate(record);
		}
		return criteriaFlag;
	}
	
	public boolean evaluateSubFormCriteria (Map<String, Object> subFormRecord,FacilioContext context) throws Exception {
		boolean criteriaFlag = true;
		subFormCriteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getId(), subFormCriteriaId);
		subFormRecord=FieldUtil.getAsProperties(CriteriaAPI.setLookupFieldsData(subFormCriteria,subFormRecord));
		if(subFormCriteria != null && subFormRecord != null) {
			criteriaFlag = subFormCriteria.computePredicate(subFormRecord).evaluate(subFormRecord);
		}
		return criteriaFlag;
	}
	
	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	public void executeAction(FacilioContext context) throws Exception {

		if(actions == null) {
			actions = FormRuleAPI.getFormRuleActionContext(id);
		}
		if(actions != null) {
			for(FormRuleActionContext formRuleAction :actions) {
				formRuleAction.setRuleContext(this);
				formRuleAction.executeAction(context);
			}
		}
	}
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public long getFormId() {
		return formId;
	}

	public void setFormId(long formId) {
		this.formId = formId;
	}

	public long getCriteriaId() {
		return criteriaId;
	}

	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	
	public FacilioForm getFormContext() {
		return formContext;
	}

	public void setFormContext(FacilioForm formContext) {
		this.formContext = formContext;
	}

	public int getFormOnLoadRuleType() {
		if(formOnLoadRuleType != null) {
			return formOnLoadRuleType.getIntVal();
		}
		return -1;
	}

	public void setFormOnLoadRuleType(int formOnLoadRuleType) {
		this.formOnLoadRuleType = Form_On_Load_Rule_Type.getAllTriggerType().get(formOnLoadRuleType);
	}
	
	public int getTriggerType() {
		if(triggerType != null) {
			return triggerType.getIntVal();
		}
		return -1;
	}
	
	public TriggerType getTriggerTypeEnum() {
		return triggerType;
	}

	public void setTriggerType(int triggerTypeInt) {
		this.triggerType = TriggerType.getAllTriggerType().get(triggerTypeInt);
	}

	public int getRuleType() {
		if(ruleType != null) {
			return ruleType.getIntVal();
		}
		return -1;
	}
	
	public RuleType getRuleTypeEnum() {
		return ruleType;
	}

	public void setRuleType(int ruleType) {
		this.ruleType = RuleType.getAllRuleType().get(ruleType);
	}

	public enum TriggerType {
		
		FORM_ON_LOAD(1,"Form On Load"),
		FIELD_UPDATE(2,"Field Update"),
		FORM_SUBMIT(3, "Form Submit"),
		SUB_FORM_ADD_OR_DELETE(4,"Sub Form Add or Delete")
		;

		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public void setIntVal(int intVal) {
			this.intVal = intVal;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		private TriggerType(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}

		private static final Map<Integer, TriggerType> triggerTypeMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, TriggerType> initTypeMap() {
			Map<Integer, TriggerType> typeMap = new HashMap<>();

			for (TriggerType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, TriggerType> getAllTriggerType() {
			return triggerTypeMap;
		}
	}
	
	public enum Form_On_Load_Rule_Type {
		
		DIRECT(1,"Direct"),
		WITH_FIELD(2,"With Field"),
		;

		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public void setIntVal(int intVal) {
			this.intVal = intVal;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		private Form_On_Load_Rule_Type(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}

		private static final Map<Integer, Form_On_Load_Rule_Type> triggerTypeMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, Form_On_Load_Rule_Type> initTypeMap() {
			Map<Integer, Form_On_Load_Rule_Type> typeMap = new HashMap<>();

			for (Form_On_Load_Rule_Type type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, Form_On_Load_Rule_Type> getAllTriggerType() {
			return triggerTypeMap;
		}
	}
	
	public enum RuleType {
		
		ACTION(1,"Action"),
		VALIDATION(2,"Validation"),
		;

		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public void setIntVal(int intVal) {
			this.intVal = intVal;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		private RuleType(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}

		private static final Map<Integer, RuleType> ruleTypeMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, RuleType> initTypeMap() {
			Map<Integer, RuleType> typeMap = new HashMap<>();

			for (RuleType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, RuleType> getAllRuleType() {
			return ruleTypeMap;
		}
	}
	
	public enum FormRuleType {
		
		FROM_RULE(1,"From Rule"),
		FROM_FORM(2,"From Form"),
		;

		int intVal;
		String name;

		public int getIntVal() {
			return intVal;
		}

		public void setIntVal(int intVal) {
			this.intVal = intVal;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		private FormRuleType(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}

		private static final Map<Integer, FormRuleType> ruleTypeMap = Collections.unmodifiableMap(initTypeMap());

		private static Map<Integer, FormRuleType> initTypeMap() {
			Map<Integer, FormRuleType> typeMap = new HashMap<>();

			for (FormRuleType type : values()) {
				typeMap.put(type.getIntVal(), type);
			}
			return typeMap;
		}

		public static Map<Integer, FormRuleType> getAllRuleType() {
			return ruleTypeMap;
		}
	}

	public long getSubFormId() {
		return subFormId;
	}

	public void setSubFormId(long subFormId) {
		this.subFormId = subFormId;
	}

	public FacilioForm getSubFormContext() {
		return subFormContext;
	}

	public void setSubFormContext(FacilioForm subFormContext) {
		this.subFormContext = subFormContext;
	}

	public Criteria getSubFormCriteria() {
		return subFormCriteria;
	}

	public void setSubFormCriteria(Criteria subFormCriteria) {
		this.subFormCriteria = subFormCriteria;
	}

	public long getSubFormCriteriaId() {
		return subFormCriteriaId;
	}

	public void setSubFormCriteriaId(long subFormCriteriaId) {
		this.subFormCriteriaId = subFormCriteriaId;
	}

	public int getExecuteType() {
		if (executeType != null) {
			return executeType.getIntVal();
		}
		return -1;
	}

	public ExecuteType getExecuteTypeEnum() {
		return executeType;
	}
	public void setExecuteType(int executeTypeInt) {
		this.executeType = ExecuteType.getAllExecuteType().get(executeTypeInt);
	}

	public enum ExecuteType{
		CREATE(1,"Create"),
		EDIT(2,"Edit"),
		CREATE_AND_EDIT(3,"Create And Edit");
		int intVal;
		String name;
		public int getIntVal() {
			return intVal;
		}

		public void setIntVal(int intVal) {
			this.intVal = intVal;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		private ExecuteType(int intVal, String name) {
			this.intVal = intVal;
			this.name = name;
		}

		private static final Map<Integer, ExecuteType> executeType = Collections.unmodifiableMap(initExecuteTypeMap());


		private static Map<Integer, ExecuteType> initExecuteTypeMap() {
			Map<Integer, ExecuteType> executeTypeMap = new HashMap<>();
			for (ExecuteType executeType: values()) {
				executeTypeMap.put(executeType.getIntVal(), executeType);
			}
			return executeTypeMap;
		}

		public static Map<Integer, ExecuteType> getAllExecuteType() {
			return executeType;
		}
	}

}
