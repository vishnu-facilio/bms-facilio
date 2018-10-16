package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;

public class SetValidationRulesContextCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<List<ReadingRuleContext>> readingRules = (List<List<ReadingRuleContext>>) context.get(FacilioConstants.ContextNames.READING_RULES_LIST);
		context.get(FacilioConstants.ContextNames.ACTIONS_LIST);
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		
		for (int i = 0; i < readingRules.size(); ++i) {
			long fieldId = fields.get(i).getFieldId();
			readingRules.get(i).stream().forEach((r) -> {
				r.setReadingFieldId(fieldId);
				r.getEvent().setModuleId(module.getModuleId());
			});
		}
		return false;
	}

}
