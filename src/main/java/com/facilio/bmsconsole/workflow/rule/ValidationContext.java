package com.facilio.bmsconsole.workflow.rule;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.db.criteria.Criteria;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class ValidationContext {
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long ruleId = -1;
	public long getRuleId() {
		return ruleId;
	}
	public void setRuleId(long ruleId) {
		this.ruleId = ruleId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String errorMessage;
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	private Criteria criteria;
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	
	private long criteriaId = -1;
	public long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	
	@JSON(serialize = false)
	public boolean isValid() {
		if (StringUtils.isEmpty(errorMessage)) {
			return false;
		}
		
		if (criteria == null || criteria.isEmpty()) {
			return false;
		}
		return true;
	}
	
	public boolean validate(ModuleBaseWithCustomFields moduleRecord) throws Exception {
		if (criteria != null && !criteria.isEmpty()) {
			Map<String, Object> prop = FieldUtil.getAsProperties(moduleRecord);
			Map<String, Object> placeHolders = new HashMap<>();
			CommonCommandUtil.appendModuleNameInKey(null, "workorder", prop, placeHolders);
			return criteria.computePredicate(placeHolders).evaluate(moduleRecord);
		}
		return true;
	}
}
