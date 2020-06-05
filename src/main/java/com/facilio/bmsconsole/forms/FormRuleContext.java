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

public class FormRuleContext {
	long id = -1;
	long orgId = -1;
	long formId = -1;
	long criteriaId = -1;
	long siteId = -1;
	List<FormRuleTriggerFieldContext> triggerFields;
	
	String name;
	String description;
	Criteria criteria;
	FormRuleType type;
	TriggerType triggerType;
	RuleType ruleType;
	
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

	public void setType(int type) {
		this.type = FormRuleType.getAllRuleType().get(type);
	}

	public boolean evaluateCriteria (Map<String, Object> record,FacilioContext context) throws Exception {
		boolean criteriaFlag = true;
		criteria = CriteriaAPI.getCriteria(AccountUtil.getCurrentOrg().getId(), criteriaId);
		if(criteria != null && record != null) {
			criteriaFlag = criteria.computePredicate(record).evaluate(record);
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
}
