package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ValidationRulesAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

public class GetFormValidationRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {

		long validaionRuleId = (long) context.get(FacilioConstants.ContextNames.ID);
		FacilioUtil.throwIllegalArgumentException((validaionRuleId < 1), "Invalid Form Id");
		context.put(FacilioConstants.ContextNames.FormValidationRuleConstants.RULE, ValidationRulesAPI.getValidationById(validaionRuleId, ModuleFactory.getFormValidationRuleModule()));

		return false;
	}

}
