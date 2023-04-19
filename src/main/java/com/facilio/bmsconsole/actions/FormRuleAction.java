package com.facilio.bmsconsole.actions;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class FormRuleAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	 private long formId;
	 private long subFormId = -1;
	 private long formFieldId;
	 private int triggerType;

	public int getExecuteType() {
		return executeType;
	}

	public void setExecuteType(int executeType) {
		this.executeType = executeType;
	}

	private int executeType = -1;
	 private boolean fetchOnlySubformRules;
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
		
		context.put(FacilioConstants.ContextNames.FORM_ID, this.getFormId());
		context.put(FacilioConstants.ContextNames.MODULE_NAME, this.getModuleName());
		context.put(FormRuleAPI.FETCH_ONLY_SUB_FORM_RULES, isFetchOnlySubformRules());
		
		chain.execute();
		
		setResult(FormRuleAPI.FORM_RULE_CONTEXTS, context.get(FormRuleAPI.FORM_RULE_CONTEXTS));
		return SUCCESS;
	}

	public String executeFormActionRules() throws Exception {

		 if(executeType<0){
			 executeType = FormRuleContext.ExecuteType.CREATE_AND_EDIT.getIntVal();
		 }
		FacilioChain c = TransactionChainFactory.getExecuteFormActionRules();
		
		Context context = c.getContext();
		
		context.put(FacilioConstants.ContextNames.FORM_ID, this.getFormId());
		context.put(FacilioConstants.ContextNames.SUB_FORM_ID, this.getSubFormId());
		context.put(FacilioConstants.ContextNames.FORM_FIELD_ID, this.getFormFieldId());
		context.put(FormRuleAPI.FORM_RULE_TRIGGER_TYPE,FormRuleContext.TriggerType.getAllTriggerType().get(triggerType));
		context.put(FormRuleAPI.FORM_RULE_EXECUTE_TYPE,FormRuleContext.ExecuteType.getAllExecuteType().get(getExecuteType()));
		context.put(FormRuleAPI.FORM_DATA,formData);
		
		c.execute();
		
		setResult(FormRuleAPI.FORM_RULE_RESULT_JSON, context.get(FormRuleAPI.FORM_RULE_RESULT_JSON));
		setResult(FormRuleAPI.SUB_FORM_RULE_RESULT_JSON, context.get(FormRuleAPI.SUB_FORM_RULE_RESULT_JSON));
		return SUCCESS;
	}
	
	public String changeStatus() throws Exception {
		
		FacilioChain c = TransactionChainFactory.getChangeStatusForFormRuleChain();
		Context context = c.getContext();
		
		context.put(FormRuleAPI.FORM_RULE_CONTEXT,formRuleContext);
		
		c.execute();
		
		setResult(FormRuleAPI.FORM_RULE_RESULT_JSON, formRuleContext);
		return SUCCESS;
		
	}

	public long getSubFormId() {
		return subFormId;
	}

	public void setSubFormId(long subFormId) {
		this.subFormId = subFormId;
	}

	public boolean isFetchOnlySubformRules() {
		return fetchOnlySubformRules;
	}

	public void setFetchOnlySubformRules(boolean fetchOnlySubformRules) {
		this.fetchOnlySubformRules = fetchOnlySubformRules;
	}

}
