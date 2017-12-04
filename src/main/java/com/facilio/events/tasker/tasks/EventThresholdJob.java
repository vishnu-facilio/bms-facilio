package com.facilio.events.tasker.tasks;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.context.EventContext.EventInternalState;
import com.facilio.events.context.EventContext.EventState;
import com.facilio.events.context.EventRule;
import com.facilio.events.util.EventAPI;
import com.facilio.events.util.EventRulesAPI;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class EventThresholdJob extends FacilioJob{

	private static Logger logger = Logger.getLogger(EventThresholdJob.class.getName());
	
	@Override
	public void execute(JobContext jc) 
	{
		if(AwsUtil.getConfig("enableeventjob").equals("true"))
		{
			try 
			{
				long orgId = AccountUtil.getCurrentOrg().getOrgId();
				GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
														.select(EventConstants.EventFieldFactory.getEventFields())
														.table("Event")
														.andCustomWhere("ORGID = ? AND EVENT_STATE = ? AND INTERNAL_STATE = ?", orgId, EventState.READY.getIntVal(), EventInternalState.TRANSFORMED.getIntVal());
				List<Map<String, Object>> props = builder.get();
				for(Map<String, Object> prop : props)
				{
					EventContext event = FieldUtil.getAsBeanFromMap(prop, EventContext.class);
					EventRule eventRule = EventRulesAPI.getEventRule(orgId, (long) event.getEventRuleId());
					
					if(eventRule.getThresholdCriteriaId() != -1)
					{
						Criteria criteria = CriteriaAPI.getCriteria(orgId, eventRule.getThresholdCriteriaId());
						boolean isMatched = criteria.computePredicate().evaluate(event);
						if(isMatched)
						{
							boolean ignoreEvent = true;
							int thresholdOccurs = eventRule.getThresholdOccurs();
							GenericSelectRecordBuilder filterbuilder = new GenericSelectRecordBuilder()
																			.select(EventConstants.EventFieldFactory.getEventFields())
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
								event.setEventState(EventState.IGNORED);
							}
						}
					}
					event.setInternalState(EventInternalState.THRESHOLD_DONE);
					event.getMessageKey();
					EventAPI.updateEvent(event, orgId);
				}
			} 
			catch (Exception e) 
			{
				logger.log(Level.SEVERE, "Exception while executing EventThresholdJob :::"+e.getMessage(), e);
			}
		}
	}
}
