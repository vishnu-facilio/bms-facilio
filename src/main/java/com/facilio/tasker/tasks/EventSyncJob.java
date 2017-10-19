package com.facilio.tasker.tasks;

import java.util.Iterator;
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
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.EventContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class EventSyncJob extends FacilioJob{

	private static Logger logger = Logger.getLogger(EventSyncJob.class.getName());
	
	@Override
	public void execute(JobContext jc) 
	{
		try
		{
			BasicAWSCredentials awsCreds = new BasicAWSCredentials(AwsUtil.getConfig(AwsUtil.AWS_ACCESS_KEY_ID), AwsUtil.getConfig(AwsUtil.AWS_SECRET_KEY_ID));
			AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
			
			DynamoDB dd = new DynamoDB(client);
			Table table = dd.getTable("ClientTest"); // Client Table
			RangeKeyCondition rkc = new RangeKeyCondition("TimeLog");
			rkc.gt(String.valueOf(System.currentTimeMillis() - ((1 * 60 * 1000) - 1))); // One Minute
			
			QuerySpec spec = new QuerySpec().withHashKey("ClientId", "123").withRangeKeyCondition(rkc);	// Device Mac ID
			ItemCollection<QueryOutcome> items = table.query(spec);
			Iterator<Item> iterator = items.iterator();
			while (iterator.hasNext()) 
			{
				Item item = iterator.next();
			    Long timestamp = item.getLong("TimeLog");
			    processPayload(timestamp, item.getMap("payload"));
			}
		}
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, "Exeception while executing EventSyncJob :::"+e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
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
	    
	    FacilioContext context = new FacilioContext();
	    context.put(FacilioConstants.ContextNames.EVENT, event);
	    Command addWorkOrder = FacilioChainFactory.getAddEventChain();
		addWorkOrder.execute(context);
	}
}
