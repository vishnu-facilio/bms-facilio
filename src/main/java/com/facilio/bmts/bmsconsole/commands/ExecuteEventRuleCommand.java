package com.facilio.bmts.bmsconsole.commands;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmts.bmsconsole.context.EventContext;
import com.facilio.bmts.constants.BmtsConstants;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class ExecuteEventRuleCommand implements Command {

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public boolean execute(Context context) throws Exception {
		
		Map<String, Object> props = (Map<String, Object>) context.get(BmtsConstants.EVENT_PROPERTY);
		if((Boolean) props.get("hasEventRule"))
		{
			EventContext event = (EventContext) context.get(BmtsConstants.EVENT);
			Map<String, Object> ruleprops = null;
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) 
			{
				GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
														.connection(conn)
														.select(BmtsConstants.getEventRuleFields())
														.table("Event_Rule")
														.andCustomWhere("ORGID = ?", event.getOrgId());	//Org Id
				
				List<Map<String, Object>> rulepropslist = builder.get();
				ruleprops = rulepropslist.get(0);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				throw e;
			}
			
			boolean ignoreEvent = false;
			if((Boolean) ruleprops.get("hasEventFilter"))
			{
				try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection())
				{
					Criteria criteria = CriteriaAPI.getCriteria(event.getOrgId(), (long) ruleprops.get("filterCriteriaId") ,conn);
					ignoreEvent = criteria.computePredicate().evaluate(event);
				}
				catch (Exception e) 
				{
					e.printStackTrace();
					throw e;
				}
			}
			if(!ignoreEvent)
			{
				if((Boolean) ruleprops.get("hasCustomizeRule"))
				{
					//TODO apply transform
				}
				if((Boolean) ruleprops.get("hasThresholdRule"))
				{
					Criteria criteria = new Criteria();
					ignoreEvent = criteria.computePredicate().evaluate(event);
				}
			}
			context.put(BmtsConstants.IGNORE_EVENT, ignoreEvent);
		}
		return false;
	}
}
