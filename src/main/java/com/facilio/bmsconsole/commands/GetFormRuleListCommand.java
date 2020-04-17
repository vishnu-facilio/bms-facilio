package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.forms.FormRuleContext;
import com.facilio.bmsconsole.util.FormRuleAPI;
import com.facilio.constants.FacilioConstants;

public class GetFormRuleListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		List<FormRuleContext> formRules = FormRuleAPI.getFormRuleContexts(moduleName);
		
		context.put(FormRuleAPI.FORM_RULE_CONTEXTS, formRules);
		return false;
	}

}
