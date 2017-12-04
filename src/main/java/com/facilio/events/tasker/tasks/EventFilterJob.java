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

public class EventFilterJob extends FacilioJob{

	private static Logger logger = Logger.getLogger(EventFilterJob.class.getName());
	
	@Override
	public void execute(JobContext jc) 
	{
		if(AwsUtil.getConfig("enableeventjob").equals("true"))
		{
			try 
			{
				long orgId = AccountUtil.getCurrentOrg().getOrgId();
				List<EventRule> eventRules = EventRulesAPI.getEventRules(orgId);
				
				GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
														.select(EventConstants.EventFieldFactory.getEventFields())
														.table("Event")
														.andCustomWhere("ORGID = ? AND EVENT_STATE = ? AND INTERNAL_STATE = ?", orgId, EventState.READY.getIntVal(), EventInternalState.ADDED.getIntVal());	//Org Id
				List<Map<String, Object>> props = builder.get();
				for(Map<String, Object> prop : props)
				{
					EventContext event = FieldUtil.getAsBeanFromMap(prop, EventContext.class);
					boolean isMatched = false;
					boolean ignoreEvent = false;
					Long matchedRuleId = null;
					if(eventRules != null) {
						for(EventRule rule : eventRules)
						{
							Criteria criteria = CriteriaAPI.getCriteria(orgId, rule.getBaseCriteriaId());
							isMatched = criteria.computePredicate().evaluate(prop);
							if(isMatched)
							{
								matchedRuleId = rule.getEventRuleId();
								ignoreEvent = rule.isIgnoreEvent();
								break;
							}
						}
					}
					if(ignoreEvent)
					{
						event.setEventState(EventState.IGNORED);
					}
					event.setInternalState(EventInternalState.FILTERED);
					if(isMatched)
					{
						event.setEventRuleId(matchedRuleId);
					}
					event.getMessageKey();
					EventAPI.updateEvent(event, orgId);
				}
			} 
			catch (Exception e) 
			{
				logger.log(Level.SEVERE, "Exception while executing EventFilterJob :::"+e.getMessage(), e);
			}
		}
	}
}
