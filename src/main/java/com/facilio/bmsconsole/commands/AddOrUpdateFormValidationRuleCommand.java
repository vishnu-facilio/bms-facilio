package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ValidationRulesAPI;
import com.facilio.bmsconsole.workflow.rule.ValidationContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Context;

public class AddOrUpdateFormValidationRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		ValidationContext validationRule = (ValidationContext) context.get(FacilioConstants.ContextNames.FormValidationRuleConstants.RULE);

		if(validationRule == null || !validationRule.isValid()) {
			throw new IllegalArgumentException("Invalid validation rule");
		}
		ValidationRulesAPI.addOrUpdateFormValidations(validationRule, ModuleFactory.getFormValidationRuleModule());
		return false;
	}

}
