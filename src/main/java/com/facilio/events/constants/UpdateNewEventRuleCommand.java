package com.facilio.events.constants;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.util.EventRulesAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;

public class UpdateNewEventRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		EventRuleContext eventRule = (EventRuleContext) context.get(EventConstants.EventContextNames.EVENT_RULE);
		if(eventRule != null && eventRule.getId() != -1) {
			EventRuleContext oldRule = EventRulesAPI.getEventRule(eventRule.getId(), false);
			EventRulesAPI.updateChildIds(eventRule);
			
			FacilioModule module = EventConstants.EventModuleFactory.getEventRulesModule();
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
															.table(module.getTableName())
															.fields(EventConstants.EventFieldFactory.getEventRulesFields())
//															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
															.andCondition(CriteriaAPI.getIdCondition(eventRule.getId(), module))
															;
			
			updateBuilder.update(FieldUtil.getAsProperties(eventRule));
			EventRulesAPI.deleteChildIds(oldRule, eventRule);
		}
		return false;
	}
}
