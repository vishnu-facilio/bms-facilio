package com.facilio.events.tasker.tasks;

import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.chain.Command;
import org.json.simple.JSONObject;

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
import com.facilio.events.context.EventContext;
import com.facilio.events.constants.EventConstants;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.transaction.FacilioConnectionPool;

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
				AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
				
				DynamoDB dd = new DynamoDB(client);
				Table table = dd.getTable("FacilioEvents"); // Client Table
				RangeKeyCondition rkc = new RangeKeyCondition("LogTime");
				rkc.gt(String.valueOf(System.currentTimeMillis() - ((1 * 60 * 1000) - 1))); // One Minute
				
				QuerySpec spec = new QuerySpec().withHashKey("DeviceId", "123").withRangeKeyCondition(rkc);	// Device Mac ID
				ItemCollection<QueryOutcome> items = table.query(spec);
				Iterator<Item> iterator = items.iterator();
				while (iterator.hasNext())
				{
					Item item = iterator.next();
				    Long timestamp = item.getLong("LogTime");
				    processPayload(timestamp, item.getMap("payload"));
				}
			}
			catch (Exception e) 
			{
				logger.log(Level.SEVERE, "Exception while executing EventSyncJob :::"+e.getMessage(), e);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	private void processPayload(Long timestamp, Map<String, String> payload) throws Exception 
	{
	    System.out.println("EventSyncJob Payload:::" + payload);
	    EventContext event = new EventContext();
	    JSONObject additionalInfo = new JSONObject();
	    Iterator<String> iterator = payload.keySet().iterator();
	    while(iterator.hasNext())
	    {
	    	String key = iterator.next();
	    	String value = payload.get(key);
	    	if(key.equals("source"))
	    	{
	    		event.setSource(value);
	    	}
	    	else if(key.equals("node"))
	    	{
	    		event.setNode(value);
	    	}
	    	else if(key.equals("type"))
	    	{
	    		event.setEventType(value);
	    	}
	    	else if(key.equals("severity"))
	    	{
	    		event.setSeverity(value);
	    	}
	    	else if(key.equals("description"))
	    	{
	    		event.setDescription(value);
	    	}
	    	else
	    	{
	    		additionalInfo.put(key, value);
	    	}
	    }
	    if(!additionalInfo.isEmpty())
	    {
	    	event.setAdditionInfo(additionalInfo.toString());
	    }
	    event.setOrgId(1);
	    event.setCreatedTime(timestamp);
	    event.setState("Processed");
	    
	    FacilioContext context = new FacilioContext();
	    context.put(EventConstants.EVENT, event);
	    
	    try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) 
		{
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
													.connection(conn)
													.select(EventConstants.getEventPropertyFields())
													.table("Event_Property")
													.andCustomWhere("ORGID = ?", 1);	//Org Id
			
			List<Map<String, Object>> props = builder.get();
			context.put(EventConstants.EVENT_PROPERTY, props.get(0));
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			throw e;
		}
	    Command addEvent = EventConstants.getAddEventChain();
	    addEvent.execute(context);
	}
}
