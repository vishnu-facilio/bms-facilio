package com.facilio.events.commands;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.events.context.EventRule;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class GetEventRulesCommand implements Command {

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<EventRule> eventRules = new ArrayList<>();
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
					.connection(conn)
					.select(EventConstants.getEventRuleFields())
					.table("Event_Rule")
					.andCustomWhere("Event_Rule.ORGID = ?", OrgInfo.getCurrentOrgInfo().getOrgid());
	
			List<Map<String, Object>> eventRulesList = selectBuider.get();
			for(Map<String, Object> eventRuleMap : eventRulesList)
			{
				EventRule eventRule = new EventRule();
				BeanUtils.populate(eventRule, eventRuleMap);
				eventRule.setBaseCriteria(CriteriaAPI.getCriteria(eventRule.getOrgId(), eventRule.getBaseCriteriaId() ,conn));
				if(eventRule.getHasCustomizeRule() != null && eventRule.getHasCustomizeRule())
				{
					eventRule.setCustomizeCriteria(CriteriaAPI.getCriteria(eventRule.getOrgId(), eventRule.getCustomizeCriteriaId() ,conn));
				}
				if(eventRule.getHasThresholdRule() != null && eventRule.getHasThresholdRule())
				{
					eventRule.setCreateAlarmCriteria(CriteriaAPI.getCriteria(eventRule.getOrgId(), eventRule.getCreateAlarmCriteriaId() ,conn));
				}
				eventRules.add(eventRule);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		context.put(EventConstants.EVENT_RULE_LIST, eventRules);
		return false;
	}
}
