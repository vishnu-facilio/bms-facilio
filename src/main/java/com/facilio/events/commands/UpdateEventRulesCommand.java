package com.facilio.events.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventRule;
import com.facilio.events.util.EventRulesAPI;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class UpdateEventRulesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EventRule eventRule = (EventRule) context.get(EventConstants.EventContextNames.EVENT_RULE);
		if(eventRule != null && eventRule.getEventRuleId() != -1) {
			long orgId =  AccountUtil.getCurrentOrg().getOrgId();
			EventRulesAPI.updateEventRuleChildIds(eventRule, orgId);
			
			FacilioModule module = EventConstants.EventModuleFactory.getEventRuleModule();
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
															.table(module.getTableName())
															.fields(EventConstants.EventFieldFactory.getEventRuleFields())
															.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
															.andCustomWhere("EVENT_RULE_ID = ?", eventRule.getEventRuleId());
			
			updateBuilder.update(FieldUtil.getAsProperties(eventRule));
		}
		return false;
	}

}
