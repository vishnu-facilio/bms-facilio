package com.facilio.events.tasker.tasks;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.JSONTemplate;
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

public class EventTransformJob extends FacilioJob{

	private static Logger logger = Logger.getLogger(EventTransformJob.class.getName());
	
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
														.andCustomWhere("ORGID = ? AND EVENT_STATE = ? AND INTERNAL_STATE = ? AND EVENT_RULE_ID IS NOT NULL", orgId, EventState.READY.getIntVal(), EventInternalState.FILTERED.getIntVal());
				List<Map<String, Object>> props = builder.get();
				for(Map<String, Object> prop : props)
				{
					EventContext event = FieldUtil.getAsBeanFromMap(prop, EventContext.class);
					logger.log(Level.SEVERE, event.getEventRuleId()+"");
					logger.log(Level.INFO, event.getEventRuleId()+" INfo");
					EventRule rule = EventRulesAPI.getEventRule(orgId, event.getEventRuleId());
					logger.log(Level.INFO, "Transform criteria : "+rule.getTransformCriteriaId());
					if(rule.getTransformCriteriaId() != -1)
					{
						Criteria criteria = CriteriaAPI.getCriteria(orgId, rule.getTransformCriteriaId());
						logger.log(Level.INFO, prop.toString());
						boolean isMatched = criteria.computePredicate().evaluate(prop);
						if(isMatched)
						{
							logger.log(Level.INFO, event.getMessageKey()+" matched transform critiera of "+event.getEventRuleId());
							JSONTemplate template = (JSONTemplate) TemplateAPI.getTemplate(orgId, rule.getTransformAlertTemplateId());
							JSONObject content = template.getTemplate(prop);
							logger.log(Level.INFO, "template val : "+content);
							prop.putAll(FieldUtil.getAsProperties(content));
							logger.log(Level.INFO, "Transformed prop : "+prop);
							event = FieldUtil.getAsBeanFromMap(prop, EventContext.class);
						}
					}
					event.setInternalState(EventInternalState.TRANSFORMED);
					event.getMessageKey();
					EventAPI.updateEvent(event, orgId);
				}
			} 
			catch (Exception e) 
			{
				logger.log(Level.SEVERE, "Exception while executing EventTransformJob :::"+e.getMessage(), e);
			}
		}
	}
}
