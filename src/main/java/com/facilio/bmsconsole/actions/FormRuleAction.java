package com.facilio.bmsconsole.actions;

import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class FormRuleAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	 private long formId;
	 private long formFieldId;
	 private int triggerType;
	 Map<String,Object> formData;
	 
	 FormRuleContext formRuleContext;

	 public FormRuleContext getFormRuleContext() {
		return formRuleContext;
	}

	public void setFormRuleContext(FormRuleContext formRuleContext) {
		this.formRuleContext = formRuleContext;
	}

	public Map<String, Object> getFormData() {
		return formData;
	}

	public void setFormData(Map<String, Object> formData) {
		this.formData = formData;
	}
	public long getFormId() {
		return formId;
	}

	public void setFormId(long formId) {
		this.formId = formId;
	}

	public long getFormFieldId() {
		return formFieldId;
	}

	public void setFormFieldId(long formFieldId) {
		this.formFieldId = formFieldId;
	}

	public int getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(int triggerType) {
		this.triggerType = triggerType;
	}
	
	public String addRule() throws Exception {
		
		
		return null;
	}
	
	public String updateRule() throws Exception {
		
		
		return null;
	}
	
	public String deleteRule() throws Exception {
		
		
		return null;
	}
	
	public String getRule() throws Exception {
		
		
		return null;
	}

	public String executeFormActionRules() throws Exception {
		Context context = new FacilioContext();
		
		context.put(FacilioConstants.ContextNames.FORM_ID, this.getFormId());
		context.put(FacilioConstants.ContextNames.FORM_FIELD_ID, this.getFormFieldId());
		context.put(FormRuleAPI.FORM_RULE_TRIGGER_TYPE,FormRuleContext.TriggerType.getAllTriggerType().get(triggerType));
		context.put(FormRuleAPI.FORM_DATA,formData);
		
		Chain c = TransactionChainFactory.getExecuteFormActionRules();
		c.execute(context);
		
		setResult(FormRuleAPI.FORM_RULE_RESULT_JSON, context.get(FormRuleAPI.FORM_RULE_RESULT_JSON));
		return SUCCESS;
	}

}
