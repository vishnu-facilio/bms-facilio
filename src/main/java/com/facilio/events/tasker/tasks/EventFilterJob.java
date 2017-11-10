package com.facilio.events.tasker.tasks;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.transaction.FacilioConnectionPool;

public class EventFilterJob extends FacilioJob{

	private static Logger logger = Logger.getLogger(EventFilterJob.class.getName());
	
	@SuppressWarnings("deprecation")
	@Override
	public void execute(JobContext jc) 
	{
		if(AwsUtil.getConfig("enableeventjob").equals("true"))
		{
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) 
			{
				GenericSelectRecordBuilder rulebuilder = new GenericSelectRecordBuilder()
														.connection(conn)
														.select(EventConstants.getEventRuleFields())
														.table("Event_Rule")
														.andCustomWhere("ORGID = ?", jc.getOrgId())
														.orderBy("RULE_ORDER");
				List<Map<String, Object>> ruleprops = rulebuilder.get();
				
				GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
														.connection(conn)
														.select(EventConstants.getEventFields())
														.table("Event")
														.andCustomWhere("ORGID = ? AND STATE = ? AND INTERNAL_STATE = 1", jc.getOrgId(), "Ready");	//Org Id
				List<Map<String, Object>> props = builder.get();
				for(Map<String, Object> prop : props)
				{
					EventContext event = FieldUtil.getAsBeanFromMap(prop, EventContext.class);
					boolean isMatched = false;
					boolean ignoreEvent = false;
					Long matchedRuleId = null;
					for(Map<String, Object> ruleprop : ruleprops)
					{
						Criteria criteria = CriteriaAPI.getCriteria(jc.getOrgId(), (long) ruleprop.get("baseCriteriaId"), conn);
						isMatched = criteria.computePredicate().evaluate(event);
						if(isMatched)
						{
							matchedRuleId = (long) ruleprop.get("eventRuleId");
							ignoreEvent = (Boolean) ruleprop.get("ignoreEvent");
							break;
						}
					}
					if(ignoreEvent)
					{
						event.setState("Ignored");
					}
					event.setInternalState(2);
					if(isMatched)
					{
						event.setEventRuleId(matchedRuleId);
					}
					prop = FieldUtil.getAsProperties(event);
					
					GenericUpdateRecordBuilder updatebuilder = new GenericUpdateRecordBuilder()
															.connection(conn)
															.table("Event")
															.fields(EventConstants.getEventFields())
															.andCustomWhere("ORGID = ?", OrgInfo.getCurrentOrgInfo().getOrgid());
					updatebuilder.update(prop);
				}
			} 
			catch (Exception e) 
			{
				logger.log(Level.SEVERE, "Exception while executing EventFilterJob :::"+e.getMessage(), e);
			}
		}
	}
}
