package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ValidationRulesAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

public class GetAllFormValidationRulesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long formId = (long) context.get(FacilioConstants.ContextNames.FORM_ID);
		JSONObject pagination = (JSONObject) context.get(FacilioConstants.ContextNames.PAGINATION);
		String searchString = (String) context.get(FacilioConstants.ContextNames.SEARCH);
		FacilioUtil.throwIllegalArgumentException((formId < 1), "Invalid Form Id");
		context.put(FacilioConstants.ContextNames.FormValidationRuleConstants.RULES, ValidationRulesAPI.getValidationsByParentId(formId, ModuleFactory.getFormValidationRuleModule(), pagination, searchString));

		return false;
	}

}
