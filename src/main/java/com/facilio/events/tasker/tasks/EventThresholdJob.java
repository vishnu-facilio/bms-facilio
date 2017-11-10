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

public class EventThresholdJob extends FacilioJob{

	private static Logger logger = Logger.getLogger(EventThresholdJob.class.getName());
	
	@SuppressWarnings("deprecation")
	@Override
	public void execute(JobContext jc) 
	{
		if(AwsUtil.getConfig("enableeventjob").equals("true"))
		{
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) 
			{
				GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
														.connection(conn)
														.select(EventConstants.getEventFields())
														.table("Event")
														.andCustomWhere("ORGID = ? AND STATE = ? AND INTERNAL_STATE = 3", jc.getOrgId(), "Ready");
				List<Map<String, Object>> props = builder.get();
				for(Map<String, Object> prop : props)
				{
					EventContext event = FieldUtil.getAsBeanFromMap(prop, EventContext.class);
					GenericSelectRecordBuilder rulebuilder = new GenericSelectRecordBuilder()
												.connection(conn)
												.select(EventConstants.getEventRuleFields())
												.table("Event_Rule")
												.andCustomWhere("ORGID = ? AND EVENT_RULE_ID", jc.getOrgId(), (long) event.getEventRuleId());
					List<Map<String, Object>> ruleprops = rulebuilder.get();
					Map<String, Object> ruleprop = ruleprops.get(0);
					
					if((Boolean) ruleprop.get("hasThresholdRule"))
					{
						boolean ignoreEvent = true;
						Criteria criteria = CriteriaAPI.getCriteria(jc.getOrgId(), (long) ruleprop.get("createAlarmCriteriaId"), conn);
						boolean isMatched = criteria.computePredicate().evaluate(event);
						if(isMatched)
						{
							Integer createAlarmOccurs = (Integer) ruleprop.get("createAlarmOccurs");
							GenericSelectRecordBuilder filterbuilder = new GenericSelectRecordBuilder()
									.connection(conn)
									.select(EventConstants.getEventFields())
									.table("Event")
									.limit(createAlarmOccurs - 1)
									.andCustomWhere("ORGID = ? AND MESSAGE_KEY = ?", event.getOrgId(), event.getMessageKey())
									.orderBy("CREATED_TIME DESC");
							List<Map<String, Object>> filtereventslist = filterbuilder.get();
							if(createAlarmOccurs <= (filtereventslist.size() + 1))
							{
								Integer createAlarmOverseconds = (Integer) ruleprop.get("createAlarmOverseconds");
								long currentEventTime = event.getCreatedTime();
								for(Map<String, Object> filterevent : filtereventslist)
								{
									long prevEventTime = (long) filterevent.get("createdTime");
									ignoreEvent = (currentEventTime - prevEventTime) > createAlarmOverseconds;
								}
							}
						}
						if(ignoreEvent)
						{
							event.setState("Ignored");
						}
					}
					event.setInternalState(4);
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
				logger.log(Level.SEVERE, "Exception while executing EventThresholdJob :::"+e.getMessage(), e);
			}
		}
	}
}
