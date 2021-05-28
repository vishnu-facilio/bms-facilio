package com.facilio.bmsconsole.workflow.rule;

import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.manager.NamedCriteria;
import com.facilio.db.criteria.manager.NamedCriteriaAPI;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.json.annotations.JSON;

import java.util.Map;

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
	@Deprecated
	public Criteria getCriteria() {
		return criteria;
	}
	@Deprecated
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	private long criteriaId = -1;
	@Deprecated
	public long getCriteriaId() {
		return criteriaId;
	}
	@Deprecated
	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}

	private NamedCriteria namedCriteria;
	public NamedCriteria getNamedCriteria() {
		return namedCriteria;
	}
	public void setNamedCriteria(NamedCriteria namedCriteria) {
		this.namedCriteria = namedCriteria;
	}

	private long namedCriteriaId = -1;
	public long getNamedCriteriaId() {
		return namedCriteriaId;
	}
	public void setNamedCriteriaId(long namedCriteriaId) {
		this.namedCriteriaId = namedCriteriaId;
	}

	@JSON(serialize = false)
	public boolean isValid() throws Exception {
		if (StringUtils.isEmpty(errorMessage)) {
			return false;
		}
		
		if (namedCriteriaId < 0) {
			if (criteria == null || criteria.isEmpty()) {
				return false;
			}

			WorkflowRuleContext workflowRule = WorkflowRuleAPI.getWorkflowRule(ruleId);

			String conditionName = StringUtils.isNotEmpty(name) ? name : errorMessage;

			NamedCriteria namedCriteria = NamedCriteriaAPI.convertCriteriaToNamedCriteria(conditionName, workflowRule.getModuleId(), criteria);
			this.namedCriteriaId = namedCriteria.getId();
		}
		return true;
	}
	
	public boolean validate(ModuleBaseWithCustomFields moduleRecord, Context context, Map<String, Object> placeHolders) throws Exception {
		if (namedCriteria != null) {
			return namedCriteria.evaluate(moduleRecord, context, placeHolders);
		}
		return true;
	}
}
