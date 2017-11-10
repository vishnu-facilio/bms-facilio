package com.facilio.events.tasker.tasks;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.RangeKeyCondition;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.AlarmTemplate;
import com.facilio.events.context.EventContext;
import com.facilio.fw.OrgInfo;
import com.facilio.events.constants.EventConstants;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.transaction.FacilioConnectionPool;

public class EventTransformJob extends FacilioJob{

	private static Logger logger = Logger.getLogger(EventTransformJob.class.getName());
	
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
														.andCustomWhere("ORGID = ? AND STATE = ? AND INTERNAL_STATE = 2 AND EVENT_RULE_ID IS NOT NULL", jc.getOrgId(), "Ready");
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
					
					if((Boolean) ruleprop.get("hasTransformRule"))
					{
						Criteria criteria = CriteriaAPI.getCriteria(jc.getOrgId(), (long) ruleprop.get("customizeCriteriaId"), conn);
						boolean isMatched = criteria.computePredicate().evaluate(event);
						if(isMatched)
						{
							AlarmTemplate template = (AlarmTemplate) TemplateAPI.getTemplate(event.getOrgId(), (Long) ruleprop.get("alarmTemplateId"));
							JSONParser parser = new JSONParser();
							
							prop = FieldUtil.getAsProperties(event);
							JSONObject content = (JSONObject) parser.parse((String) template.getTemplate(prop).get("content"));
							
							event = FieldUtil.getAsBeanFromJson(content, EventContext.class);
						}
					}
					event.setInternalState(3);
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
				logger.log(Level.SEVERE, "Exception while executing EventTransformJob :::"+e.getMessage(), e);
			}
		}
	}
}
