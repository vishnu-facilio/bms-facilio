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
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.events.context.EventRule;
import com.facilio.events.util.EventRulesAPI;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.transaction.FacilioConnectionPool;

public class EventThresholdJob extends FacilioJob{

	private static Logger logger = Logger.getLogger(EventThresholdJob.class.getName());
	
	@Override
	public void execute(JobContext jc) 
	{
		if(AwsUtil.getConfig("enableeventjob").equals("true"))
		{
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) 
			{
				long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
				GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
														.select(EventConstants.getEventFields())
														.table("Event")
														.andCustomWhere("ORGID = ? AND STATE = ? AND INTERNAL_STATE = ?", orgId, EventState.READY.getIntVal(), EventInternalState.TRANSFORMED.getIntVal());
				List<Map<String, Object>> props = builder.get();
				for(Map<String, Object> prop : props)
				{
					EventContext event = FieldUtil.getAsBeanFromMap(prop, EventContext.class);
					EventRule eventRule = EventRulesAPI.getEventRule(orgId, (long) event.getEventRuleId());
					
					if(eventRule.getThresholdCriteriaId() != -1)
					{
						Criteria criteria = CriteriaAPI.getCriteria(orgId, eventRule.getThresholdCriteriaId(), conn);
						boolean isMatched = criteria.computePredicate().evaluate(event);
						if(isMatched)
						{
							boolean ignoreEvent = true;
							int thresholdOccurs = eventRule.getThresholdOccurs();
							GenericSelectRecordBuilder filterbuilder = new GenericSelectRecordBuilder()
																			.select(EventConstants.getEventFields())
																			.table("Event")
																			.limit(thresholdOccurs - 1)
																			.andCustomWhere("ORGID = ? AND MESSAGE_KEY = ? AND CREATED_TIME < ?", event.getOrgId(), event.getMessageKey(), event.getCreatedTime())
																			.orderBy("CREATED_TIME DESC");
							List<Map<String, Object>> filtereventslist = filterbuilder.get();
							if(thresholdOccurs <= (filtereventslist.size() + 1))
							{
								int thresholdOverSeconds = eventRule.getThresholdOverSeconds();
								long currentEventTime = event.getCreatedTime();
								for(Map<String, Object> filterevent : filtereventslist)
								{
									long prevEventTime = (long) filterevent.get("createdTime");
									ignoreEvent = (currentEventTime - prevEventTime) > thresholdOverSeconds;
									if(ignoreEvent)
									{
										break;
									}
									currentEventTime = prevEventTime;
								}
							}
							if(ignoreEvent)
							{
								event.setState(EventState.IGNORED);
							}
						}
					}
					event.setInternalState(EventInternalState.THRESHOLD_DONE);
					event.getMessageKey();
					prop = FieldUtil.getAsProperties(event);
					
					GenericUpdateRecordBuilder updatebuilder = new GenericUpdateRecordBuilder()
																	.table("Event")
																	.fields(EventConstants.getEventFields())
																	.andCustomWhere("ORGID = ? AND ID = ?", orgId, event.getId());
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
