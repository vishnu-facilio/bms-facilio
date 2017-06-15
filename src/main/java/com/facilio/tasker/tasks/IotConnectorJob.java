package com.facilio.tasker.tasks;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
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
import com.facilio.bmsconsole.device.types.DistechControls;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.fw.OrgInfo;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.wms.util.WmsApi;

public class IotConnectorJob extends FacilioJob{

	private static Logger logger = Logger.getLogger(IotConnectorJob.class.getName());
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobContext jc) 
	{
		try
		{
			JSONObject dataList = new JSONObject();
			JSONArray timeArray = new JSONArray();
			JSONArray valueArray = new JSONArray();
			timeArray.add("x");
			valueArray.add("kW");
			
			BasicAWSCredentials awsCreds = new BasicAWSCredentials(AwsUtil.getConfig(AwsUtil.AWS_ACCESS_KEY_ID), AwsUtil.getConfig(AwsUtil.AWS_SECRET_KEY_ID));
			AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
			
			DynamoDB dd = new DynamoDB(client);
			Table table = dd.getTable(AwsUtil.AWS_IOT_DYNAMODB_TABLE_NAME);
			RangeKeyCondition rkc = new RangeKeyCondition("timestamp");
			rkc.gt(String.valueOf(System.currentTimeMillis() - 19999));
			QuerySpec spec = new QuerySpec().withHashKey("clientId", "6").withRangeKeyCondition(rkc);
			ItemCollection<QueryOutcome> items = table.query(spec);
			Iterator<Item> iterator = items.iterator();
			Item item = null;
			
			while (iterator.hasNext()) 
			{
			    item = iterator.next();
			    Long timestamp = item.getLong("timestamp");
			    Map<String, Map<String, String>> payload = item.getMap("payload");
			    System.out.println(payload);
			    Long controllerId = Long.parseLong(payload.get("metainfo").get("controllerId"));
			    Map<String, Map<String, Object>> deviceInstances = DeviceAPI.getActiveDeviceInstances(controllerId);
			    Iterator<String> dataIterator = payload.keySet().iterator();
			    
			    boolean needDeviceConfiguration = false;
			    Map<String, Map<String, Double>> deviceDataMap = new HashMap<>();
			    while(dataIterator.hasNext())
			    {
			    	String key = dataIterator.next();
			    	if(!"metainfo".equals(key))
			    	{
			    		if(!deviceInstances.containsKey(key))
			    		{
			    			needDeviceConfiguration = true;
			    			continue;
			    		}
			    		Long deviceId = (Long) deviceInstances.get(key).get("deviceId");
			    		Map<String, Double> dataMap;
			    		if(deviceDataMap.containsKey(deviceId.toString()))
			    		{
			    			dataMap = deviceDataMap.get(deviceId.toString());
			    		}
			    		else
			    		{
			    			dataMap = new HashMap<String, Double>();
			    		}
			    		String columnName = (String) deviceInstances.get(key).get("columnName");
			    		Double value = Double.parseDouble(payload.get(key).get("value"));
			    		dataMap.put(columnName, value);
			    		deviceDataMap.put(deviceId.toString(), dataMap);
					    
			    		timeArray.add(timestamp);
						valueArray.add(value);
			    	}
			    }
			    DeviceAPI.addDeviceData(timestamp, deviceDataMap);
			    if(needDeviceConfiguration)
				{
					DeviceAPI.discoverDevices(controllerId, 6L);
				}
			}
			dataList.put("x", timeArray);
			dataList.put("y", valueArray);
			WmsApi.sendMessage(2, 3, dataList);
		}
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, "Exeception while executing IotConnectorJob :::"+e.getMessage(), e);
		}
	}
}
