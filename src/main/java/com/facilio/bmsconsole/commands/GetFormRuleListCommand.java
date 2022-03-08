package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.constants.FacilioConstants;

public class GetFormRuleListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		Long formId = (Long) context.getOrDefault(FacilioConstants.ContextNames.FORM_ID, -1L);
		
		boolean fetchOnlySubformRules = (boolean) context.get(FormRuleAPI.FETCH_ONLY_SUB_FORM_RULES);
		
		List<FormRuleContext> formRules = FormRuleAPI.getFormRuleContexts(moduleName, formId, fetchOnlySubformRules);
		
		context.put(FormRuleAPI.FORM_RULE_CONTEXTS, formRules);
		return false;
	}

}
