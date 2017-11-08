package com.facilio.events.commands;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventProperty;
import com.facilio.events.context.EventRule;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class GetEventRuleCommand implements Command {

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		Map<Long, Criteria> criteriaMap = new HashMap<>();
		EventRule eventRule = new EventRule();
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
					.connection(conn)
					.select(EventConstants.getEventRuleFields())
					.table("Event_Rule")
					.andCustomWhere("Event_Rule.ORGID = ?", OrgInfo.getCurrentOrgInfo().getOrgid());
	
			List<Map<String, Object>> eventRules = selectBuider.get();
			BeanUtils.populate(eventRule, eventRules.get(0));
			
			if(eventRule.getHasEventFilter() != null && eventRule.getHasEventFilter())
			{
				criteriaMap.put(eventRule.getFilterCriteriaId(), CriteriaAPI.getCriteria(eventRule.getOrgId(), eventRule.getFilterCriteriaId() ,conn));
			}
			if(eventRule.getHasCustomizeRule() != null && eventRule.getHasCustomizeRule())
			{
				criteriaMap.put(eventRule.getCustomizeCriteriaId(), CriteriaAPI.getCriteria(eventRule.getOrgId(), eventRule.getCustomizeCriteriaId() ,conn));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		context.put(EventConstants.EVENT_RULE, eventRule);
		context.put(EventConstants.EVENT_CRITERIA_MAP, criteriaMap);
		return false;
	}
}
