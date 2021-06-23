package com.facilio.events.constants;

import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.util.EventRulesAPI;
import com.facilio.modules.FieldUtil;

public class AddNewEventRuleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		EventRuleContext eventRule = (EventRuleContext) context.get(EventConstants.EventContextNames.EVENT_RULE);
		if(eventRule != null) {
			long orgId = AccountUtil.getCurrentOrg().getOrgId();
			eventRule.setOrgId(orgId);
			eventRule.setActive(true);
			EventRulesAPI.updateChildIds(eventRule);
			
			Map<String, Object> prop = FieldUtil.getAsProperties(eventRule);
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table(EventConstants.EventModuleFactory.getEventRulesModule().getTableName())
															.fields(EventConstants.EventFieldFactory.getEventRulesFields())
															.addRecord(prop);
			
			insertBuilder.save();
			long id = (long) prop.get("id");
			eventRule.setId(id);
		}
		
		return false;
	}
}
