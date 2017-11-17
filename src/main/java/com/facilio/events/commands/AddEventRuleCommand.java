package com.facilio.events.commands;

import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventRule;
import com.facilio.events.util.EventRulesAPI;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddEventRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		EventRule eventRule = (EventRule) context.get(EventConstants.EventContextNames.EVENT_RULE);
		if(eventRule != null) {
			long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
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
