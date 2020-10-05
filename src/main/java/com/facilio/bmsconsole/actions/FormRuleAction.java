package com.facilio.bmsconsole.actions;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.chain.FacilioChain;
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
	 String moduleName;
	 
	 public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

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
		
		FacilioChain c = TransactionChainFactory.getAddFormRuleChain();
		Context context = c.getContext();
		
		context.put(FormRuleAPI.FORM_RULE_CONTEXT,formRuleContext);
		
		c.execute();
		
		setResult(FormRuleAPI.FORM_RULE_RESULT_JSON, formRuleContext);
		return SUCCESS;
	}
	
	public String updateRule() throws Exception {
		
		FacilioChain c = TransactionChainFactory.getUpdateFormRuleChain();
		Context context = c.getContext();
		
		context.put(FormRuleAPI.FORM_RULE_CONTEXT,formRuleContext);
		
		c.execute();
		
		setResult(FormRuleAPI.FORM_RULE_RESULT_JSON, formRuleContext);
		return SUCCESS;
	}
	
	public String deleteRule() throws Exception {
		FacilioChain c = TransactionChainFactory.getDeleteFormRuleChain();
		
		Context context = c.getContext();
		
		context.put(FormRuleAPI.FORM_RULE_CONTEXT,formRuleContext);
		
		c.execute();
		
		setResult(FormRuleAPI.FORM_RULE_RESULT_JSON, formRuleContext);
		
		return SUCCESS;
	}
	
	public String getRule() throws Exception {
		
		FacilioChain c = TransactionChainFactory.fetchFormRuleDetailsChain();
		
		Context context = c.getContext();
		
		context.put(FormRuleAPI.FORM_RULE_CONTEXT,formRuleContext);
		
		c.execute();
				
		setResult(FormRuleAPI.FORM_RULE_RESULT_JSON, context.get(FormRuleAPI.FORM_RULE_RESULT_JSON));
		
		return SUCCESS;
	}
	
	public String getFormRulesMap() throws Exception {
		
		FacilioChain c = ReadOnlyChainFactory.getFormRulesMapList();
		
		Context context = c.getContext();
		
		context.put(FormRuleAPI.FORM_RULE_CONTEXT,formRuleContext);
		
		c.execute();
						
		setResult("formRuleResultJSON", context.get(FormRuleAPI.FORM_RULE_RESULT_JSON));
				
		return SUCCESS;
	}
	
	
	public String getRuleList() throws Exception {
		
		FacilioChain chain = ReadOnlyChainFactory.getFormRuleList();
		
		Context context = chain.getContext();
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, this.getModuleName());
		
		chain.execute();
		
		setResult(FormRuleAPI.FORM_RULE_CONTEXTS, context.get(FormRuleAPI.FORM_RULE_CONTEXTS));
		return SUCCESS;
	}

	public String executeFormActionRules() throws Exception {
		
		FacilioChain c = TransactionChainFactory.getExecuteFormActionRecursivelyRules();
		
		Context context = c.getContext();
		
		context.put(FacilioConstants.ContextNames.FORM_ID, this.getFormId());
		context.put(FacilioConstants.ContextNames.FORM_FIELD_ID, this.getFormFieldId());
		context.put(FormRuleAPI.FORM_RULE_TRIGGER_TYPE,FormRuleContext.TriggerType.getAllTriggerType().get(triggerType));
		context.put(FormRuleAPI.FORM_DATA,formData);
		
		c.execute();
		
		setResult(FormRuleAPI.FORM_RULE_RESULT_JSON, context.get(FormRuleAPI.FORM_RULE_RESULT_JSON));
		return SUCCESS;
	}

}
