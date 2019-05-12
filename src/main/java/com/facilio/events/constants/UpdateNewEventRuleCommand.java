package com.facilio.events.constants;

import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.util.EventRulesAPI;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class UpdateNewEventRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EventRuleContext eventRule = (EventRuleContext) context.get(EventConstants.EventContextNames.EVENT_RULE);
		if(eventRule != null && eventRule.getId() != -1) {
			EventRuleContext oldRule = EventRulesAPI.getEventRule(eventRule.getId(), false);
			EventRulesAPI.updateChildIds(eventRule);
			
			FacilioModule module = EventConstants.EventModuleFactory.getEventRulesModule();
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
															.table(module.getTableName())
															.fields(EventConstants.EventFieldFactory.getEventRulesFields())
															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
															.andCondition(CriteriaAPI.getIdCondition(eventRule.getId(), module))
															;
			
			updateBuilder.update(FieldUtil.getAsProperties(eventRule));
			EventRulesAPI.deleteChildIds(oldRule, eventRule);
		}
		return false;
	}
}
