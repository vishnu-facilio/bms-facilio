package com.facilio.tasker.tasks;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.document.BatchGetItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.RangeKeyCondition;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.facilio.aws.util.AwsUtil;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.wms.message.Message;
import com.facilio.wms.util.WmsApi;

public class DynamoDBMySQLSyncJob extends FacilioJob{

	private static Logger logger = Logger.getLogger(DynamoDBMySQLSyncJob.class.getName());
	
	@Override
	public void execute(JobContext jc) 
	{
		try
		{
			BasicAWSCredentials awsCreds = new BasicAWSCredentials(AwsUtil.getConfig("accessKeyId"), AwsUtil.getConfig("secretKeyId"));
			AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
			
			DynamoDB dd = new DynamoDB(client);
			Table table = dd.getTable("IotData");
			RangeKeyCondition rkc = new RangeKeyCondition("timestamp");
			rkc.gt(String.valueOf(System.currentTimeMillis() - 19999));
			QuerySpec spec = new QuerySpec().withHashKey("clientId", "1").withRangeKeyCondition(rkc);
			ItemCollection<QueryOutcome> items = table.query(spec);
			Iterator<Item> iterator = items.iterator();
			Item item = null;
			while (iterator.hasNext()) 
			{
			    item = iterator.next();
			    System.out.println(item.get("clientId"));
			    System.out.println(item.get("timestamp"));
			    System.out.println(item.get("payload"));
			    Message m = new Message();
			    m.setTo("3");
			    m.setContent(item.toJSON());
			    WmsApi.sendMessage(2, m);
			}
		}
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, "Exeception while executing SyncIotDataJob :::"+e.getMessage(), e);
		}
	}
}
