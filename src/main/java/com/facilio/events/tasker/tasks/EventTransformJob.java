package com.facilio.events.tasker.tasks;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.AlarmTemplate;
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

public class EventTransformJob extends FacilioJob{

	private static Logger logger = Logger.getLogger(EventTransformJob.class.getName());
	
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
														.andCustomWhere("ORGID = ? AND STATE = ? AND INTERNAL_STATE = ? AND EVENT_RULE_ID IS NOT NULL", orgId, EventState.READY.getIntVal(), EventInternalState.FILTERED.getIntVal());
				List<Map<String, Object>> props = builder.get();
				for(Map<String, Object> prop : props)
				{
					EventContext event = FieldUtil.getAsBeanFromMap(prop, EventContext.class);
					EventRule rule = EventRulesAPI.getEventRule(orgId, event.getEventRuleId());
					
					if(rule.getTransformCriteriaId() != -1)
					{
						Criteria criteria = CriteriaAPI.getCriteria(orgId, rule.getTransformCriteriaId(), conn);
						boolean isMatched = criteria.computePredicate().evaluate(prop);
						if(isMatched)
						{
							AlarmTemplate template = (AlarmTemplate) TemplateAPI.getTemplate(orgId, rule.getTransformAlertTemplateId());
							JSONObject content = template.getTemplate(prop);
							prop.putAll(FieldUtil.getAsProperties(content));
							event = FieldUtil.getAsBeanFromMap(prop, EventContext.class);
						}
					}
					event.setInternalState(EventInternalState.TRANSFORMED);
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
				logger.log(Level.SEVERE, "Exception while executing EventTransformJob :::"+e.getMessage(), e);
			}
		}
	}
}
