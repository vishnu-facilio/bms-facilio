package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ValidationRulesAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;

import java.util.Collections;

public class DeleteFormValidationRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long validaionRuleId = (long) context.get(FacilioConstants.ContextNames.ID);
		FacilioUtil.throwIllegalArgumentException((validaionRuleId < 1), "Invalid Form Id");
		int count = ValidationRulesAPI.deleteValidations(Collections.singletonList(validaionRuleId), ModuleFactory.getFormValidationRuleModule());
		context.put(FacilioConstants.ContextNames.COUNT, count);
		return false;
	}

}
