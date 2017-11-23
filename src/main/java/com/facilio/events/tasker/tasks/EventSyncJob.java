package com.facilio.events.tasker.tasks;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.events.constants.EventConstants;
import com.facilio.events.context.EventContext;
import com.facilio.events.util.EventAPI;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class EventSyncJob extends FacilioJob{

	private static Logger logger = Logger.getLogger(EventSyncJob.class.getName());
	
	@Override
	public void execute(JobContext jc) 
	{
		if(AwsUtil.getConfig("enableeventjob").equals("true"))
		{
			try
			{
				BasicAWSCredentials awsCreds = new BasicAWSCredentials(AwsUtil.getConfig(AwsUtil.AWS_ACCESS_KEY_ID), AwsUtil.getConfig(AwsUtil.AWS_SECRET_KEY_ID));
				AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds)).withRegion("us-west-2").build();
				
				DynamoDB dd = new DynamoDB(client);
				Table table = dd.getTable("FacilioEvents"); // Client Table
				RangeKeyCondition rkc = new RangeKeyCondition("LogTime");
				rkc.gt(String.valueOf(System.currentTimeMillis() - ((1 * 60 * 1000) - 1))); // One Minute
				
				List<ControllerContext> controllers = DeviceAPI.getAllControllers();
				
				if(controllers != null && !controllers.isEmpty()) {
					for(ControllerContext controller : controllers) {
						QuerySpec spec = new QuerySpec().withHashKey("DeviceId", controller.getMacAddr()).withRangeKeyCondition(rkc);	// Device Mac ID
						ItemCollection<QueryOutcome> items = table.query(spec);
						if(items.iterator().hasNext())
						{
							processItems(items, OrgInfo.getCurrentOrgInfo().getOrgid());
						}
					}
				}
			}
			catch (Exception e) 
			{
				logger.log(Level.SEVERE, "Exception while executing EventSyncJob :::"+e.getMessage(), e);
			}
		}
	}
	
	private void processItems(ItemCollection<QueryOutcome> items, long orgId) throws Exception 
	{
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
												.table("Event")
												.fields(EventConstants.EventFieldFactory.getEventFields());
		Iterator<Item> iterator = items.iterator();
		JSONParser parser = new JSONParser();
		while (iterator.hasNext())
		{
			Item item = iterator.next();
		    Long timestamp = item.getLong("LogTime");
		    EventContext event = EventAPI.processPayload(timestamp, (JSONObject) parser.parse(item.getJSON("payload")), orgId);
			Map<String, Object> props = FieldUtil.getAsProperties(event);
			builder.addRecord(props);
		}
		builder.save();
	}
}
