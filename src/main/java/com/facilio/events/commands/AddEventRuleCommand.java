package com.facilio.events.commands;

import java.sql.Connection;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventRule;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class AddEventRuleCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		EventRule eventRule = (EventRule) context.get(EventConstants.EVENT_RULE);
		eventRule.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
		
		Map<String, Object> eventRulesProp = FieldUtil.getAsProperties(eventRule);
		System.out.println(eventRulesProp);
		
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) 
		{
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table("Event_Rule")
					.fields(EventConstants.getEventRuleFields())
					.addRecord(eventRulesProp);

			insertBuilder.save();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			throw e;
		}
		
		return false;
	}
}
