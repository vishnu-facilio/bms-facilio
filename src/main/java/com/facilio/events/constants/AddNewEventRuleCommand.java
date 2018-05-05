package com.facilio.events.constants;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.context.EventRuleContext;
import com.facilio.events.util.EventRulesAPI;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddNewEventRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EventRuleContext eventRule = (EventRuleContext) context.get(EventConstants.EventContextNames.EVENT_RULE);
		if(eventRule != null) {
			long orgId = AccountUtil.getCurrentOrg().getOrgId();
			eventRule.setOrgId(orgId);
			eventRule.setActive(true);
			EventRulesAPI.updateChildIds(eventRule, null);
			
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
