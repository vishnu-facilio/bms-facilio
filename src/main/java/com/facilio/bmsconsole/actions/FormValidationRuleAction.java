package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.ValidationContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.chain.Context;

@Setter
@Getter
public class FormValidationRuleAction extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	 private long formId;
	 private long id;
	 private ValidationContext validationRule;

	public String addOrUpdateRule() throws Exception {
		FacilioChain c = TransactionChainFactory.getAddOrUpdateFormValidationRuleChain();
		Context context = c.getContext();
		context.put(FacilioConstants.ContextNames.FormValidationRuleConstants.RULE, validationRule);
		c.execute();
		
		setResult(FacilioConstants.ContextNames.FormValidationRuleConstants.RESULT, validationRule);
		return SUCCESS;
	}
	
	public String deleteRule() throws Exception {
		FacilioChain c = TransactionChainFactory.getDeleteFormValidationRuleChain();
		Context context = c.getContext();
		context.put(FacilioConstants.ContextNames.ID, id);
		c.execute();
		
		setResult(FacilioConstants.ContextNames.FormValidationRuleConstants.RESULT, context.get(FacilioConstants.ContextNames.COUNT));
		return SUCCESS;
	}
	
	public String getRule() throws Exception {
		
		FacilioChain c = ReadOnlyChainFactory.getFormValidationRule();
		Context context = c.getContext();
		context.put(FacilioConstants.ContextNames.ID, id);
		c.execute();
				
		setResult(FacilioConstants.ContextNames.FormValidationRuleConstants.RULE, context.get(FacilioConstants.ContextNames.FormValidationRuleConstants.RULE));
		return SUCCESS;
	}
	
	public String getRuleList() throws Exception {
		FacilioChain chain = ReadOnlyChainFactory.getFormValidationRuleList();
		Context context = chain.getContext();
		context.put(FacilioConstants.ContextNames.FORM_ID, formId);
		context.put(FacilioConstants.ContextNames.PAGINATION, getPagination());
		context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
		chain.execute();
		
		setResult(FacilioConstants.ContextNames.FormValidationRuleConstants.RULES, context.get(FacilioConstants.ContextNames.FormValidationRuleConstants.RULES));
		return SUCCESS;
	}

}
