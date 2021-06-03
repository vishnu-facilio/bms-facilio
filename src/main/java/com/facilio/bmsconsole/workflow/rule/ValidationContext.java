package com.facilio.bmsconsole.workflow.rule;

import com.facilio.db.criteria.manager.NamedCriteria;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.workflows.context.WorkflowContext;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
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

	@JsonIgnore
	@JSON(serialize = false)
	public String getResolvedErrorMessage() throws Exception {
		if (errorMessagePlaceHolderScriptId > 0) {
			if (errorMessagePlaceHolderScript != null) {
				Object o = errorMessagePlaceHolderScript.executeWorkflow();
				if (o instanceof Map) {
					return StringSubstitutor.replace(errorMessage, (Map) o);
				}
			}
		}
		return errorMessage;
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

	private long errorMessagePlaceHolderScriptId = -1;
	public long getErrorMessagePlaceHolderScriptId() {
		return errorMessagePlaceHolderScriptId;
	}
	public void setErrorMessagePlaceHolderScriptId(long errorMessagePlaceHolderScriptId) {
		this.errorMessagePlaceHolderScriptId = errorMessagePlaceHolderScriptId;
	}

	private WorkflowContext errorMessagePlaceHolderScript;
	public WorkflowContext getErrorMessagePlaceHolderScript() {
		return errorMessagePlaceHolderScript;
	}
	public void setErrorMessagePlaceHolderScript(WorkflowContext errorMessagePlaceHolderScript) {
		this.errorMessagePlaceHolderScript = errorMessagePlaceHolderScript;
	}

	@JSON(serialize = false)
	@JsonIgnore
	public boolean isValid() throws Exception {
		if (StringUtils.isEmpty(errorMessage)) {
			return false;
		}
		
		if (namedCriteriaId < 0) {
			return false;
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
