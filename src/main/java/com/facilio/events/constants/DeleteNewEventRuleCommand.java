package com.facilio.events.constants;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.util.EventRulesAPI;
import com.facilio.modules.FacilioModule;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class DeleteNewEventRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long id = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if (id != -1) {
			EventRuleContext oldRule = EventRulesAPI.getEventRule(id, false);
			if (oldRule != null) {
				context.put(EventConstants.EventContextNames.EVENT_RULE, oldRule);
				
				FacilioModule module = EventConstants.EventModuleFactory.getEventRulesModule();
				GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
																.table(module.getTableName())
																.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
																.andCondition(CriteriaAPI.getIdCondition(id, module))
																;
				deleteBuilder.delete();
				EventRulesAPI.deleteChildIds(oldRule, null);
			}
		}
		return false;
	}
}
