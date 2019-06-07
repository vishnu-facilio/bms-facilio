package com.facilio.events.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventRule;
import com.facilio.events.util.EventRulesAPI;
import com.facilio.modules.FieldUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.Map;

public class AddEventRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		EventRule eventRule = (EventRule) context.get(EventConstants.EventContextNames.EVENT_RULE);
		if(eventRule != null) {
			long orgId = AccountUtil.getCurrentOrg().getOrgId();
			eventRule.setOrgId(orgId);
			EventRulesAPI.updateEventRuleChildIds(eventRule, orgId);
			
			Map<String, Object> prop = FieldUtil.getAsProperties(eventRule);
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table(EventConstants.EventModuleFactory.getEventRuleModule().getTableName())
															.fields(EventConstants.EventFieldFactory.getEventRuleFields())
															.addRecord(prop);
			
			insertBuilder.save();
			long id = (long) prop.get("id");
			eventRule.setEventRuleId(id);
		}
		
		return false;
	}
}
