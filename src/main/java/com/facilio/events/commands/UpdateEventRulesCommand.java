package com.facilio.events.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.AlarmTemplate;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventRule;
import com.facilio.events.util.EventRulesAPI;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class UpdateEventRulesCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		EventRule eventRule = (EventRule) context.get(EventConstants.EventContextNames.EVENT_RULE);
		if(eventRule != null && eventRule.getEventRuleId() != -1) {
			long orgId =  OrgInfo.getCurrentOrgInfo().getOrgid();
			EventRulesAPI.updateEventRuleChildIds(eventRule, orgId);
			
			
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
															.table(EventConstants.EventModuleFactory.getEventRuleModule().getTableName())
															.fields(EventConstants.EventFieldFactory.getEventRuleFields())
															.andCustomWhere("EVENT_RULE_ID = ?", eventRule.getEventRuleId());
			
			updateBuilder.update(FieldUtil.getAsProperties(eventRule));
		}
		return false;
	}

}
