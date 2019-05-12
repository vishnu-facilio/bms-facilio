package com.facilio.bmsconsole.commands;

import com.facilio.modules.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetValidationRulesContextCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<List<ReadingRuleContext>> readingRules = (List<List<ReadingRuleContext>>) context.get(FacilioConstants.ContextNames.VALIDATION_RULES);
		List<FacilioModule> modules =  (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);

		if (readingRules == null || readingRules.isEmpty()) {
			return false;
		}

		Map<Long, Long> modIdMap = new HashMap<>();
		List<FacilioField> fields = new ArrayList<>();
		for (FacilioModule module: modules) {
			List<FacilioField> fs = module.getFields();
			for (FacilioField f: fs) {
				fields.add(f);
				modIdMap.put(f.getFieldId(), module.getModuleId());
			}
		}

		List<List<List<ActionContext>>> actionsList = new ArrayList<>();
		List<List<ActionContext>> actionContextList = new ArrayList<>();

		for (int i = 0; i < readingRules.size(); ++i) {
			long fieldId = fields.get(i).getFieldId();
			readingRules.get(i).stream().forEach((r) -> {
				r.setReadingFieldId(fieldId);
				r.getEvent().setModuleId(modIdMap.get(fieldId));
				actionContextList.add(r.getActions());
			});
		}
		actionsList.add(actionContextList);

		context.put(FacilioConstants.ContextNames.READING_RULES_LIST, readingRules);
		context.put(FacilioConstants.ContextNames.ACTIONS_LIST, actionsList);
		return false;
	}
}
