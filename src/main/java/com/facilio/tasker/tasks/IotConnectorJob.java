package com.facilio.tasker.tasks;

import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

import java.util.logging.Logger;

public class IotConnectorJob extends FacilioJob{

	private static Logger logger = Logger.getLogger(IotConnectorJob.class.getName());
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobContext jc) 
	{
//		try
//		{
//			Map<String, Object> jobDetails = AdminAPI.getSystemJob("IotConnector");
//			Long jobId = (Long) jobDetails.get("jobId");
//			Long lastExecutionTime = (Long) jobDetails.get("lastexecutiontime");
//			
//			BasicAWSCredentials awsCreds = new BasicAWSCredentials(AwsUtil.getConfig(AwsUtil.AWS_ACCESS_KEY_ID), AwsUtil.getConfig(AwsUtil.AWS_SECRET_KEY_ID));
//			AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
//			
//			DynamoDB dd = new DynamoDB(client);
//			Table table = dd.getTable(AwsUtil.AWS_IOT_DYNAMODB_TABLE_NAME);
//			RangeKeyCondition rkc = new RangeKeyCondition("TimeLog");
//			//rkc.gt(String.valueOf(System.currentTimeMillis() - ((20 * 1000) - 1)));
//			rkc.gt(String.valueOf(lastExecutionTime));
//			
//			List<String> clients = getClients(dd);
//			System.out.println("IotConnectorJob lastExecutionTime::::" + lastExecutionTime);
//			System.out.println("IotConnectorJob Cliens::::" + clients);
//			System.out.println("IotConnectorJob jobId::::" + jobId);
//			for(String clientId : clients)
//			{
//				QuerySpec spec = new QuerySpec().withHashKey("ClientId", clientId).withRangeKeyCondition(rkc);
//				ItemCollection<QueryOutcome> items = table.query(spec);
//				Iterator<Item> iterator = items.iterator();
//				Item item = null;
//				
//				while (iterator.hasNext()) 
//				{
//				    item = iterator.next();
//				    Long timestamp = item.getLong("TimeLog");
//				    lastExecutionTime = timestamp + 1 > lastExecutionTime?timestamp + 1 : lastExecutionTime;
//				    processPayload(timestamp, item.getMap("payload"));
//				}
//				//WmsApi.sendMessage(2, 3, dataList);
//			}
//			deleteClients(dd, clients);
//			AdminAPI.updateSystemJob(jobId, lastExecutionTime);
//		}
//		catch (Exception e) 
//		{
//			logger.log(Level.SEVERE, "Exeception while executing IotConnectorJob :::"+e.getMessage(), e);
//		}
	}

//	private void processPayload(Long timestamp, Map<String, Map<String, Object>> payload) throws Exception 
//	{
//	    System.out.println("IotConnectorJob Payload:::" + payload);
//	    Long controllerId = Long.parseLong(payload.get("metainfo").get("controllerId").toString());
//	    Map<String, Map<String, Object>> controllerInstances = DeviceAPI.getControllerInstances(controllerId);
//	    Iterator<String> dataIterator = payload.keySet().iterator();
//	    
//	    List<Map<String, Object>> unmodelledInstances = new ArrayList<Map<String, Object>>();
//	    Map<String, Map<String, Double>> deviceDataMap = new HashMap<>();
//	    while(dataIterator.hasNext())
//	    {
//	    	String key = dataIterator.next();
//	    	if(!"metainfo".equals(key))
//	    	{
//	    		if(!controllerInstances.containsKey(key) || controllerInstances.get(key).get("deviceId") == null || controllerInstances.get(key).get("columnName") == null)
//	    		{
//	    			unmodelledInstances.add(payload.get(key));
//	    			continue;
//	    		}
//	    		Long deviceId = (Long) controllerInstances.get(key).get("deviceId");
//	    		Map<String, Double> dataMap;
//	    		if(deviceDataMap.containsKey(deviceId.toString()))
//	    		{
//	    			dataMap = deviceDataMap.get(deviceId.toString());
//	    		}
//	    		else
//	    		{
//	    			dataMap = new HashMap<String, Double>();
//	    		}
//	    		String columnName = (String) controllerInstances.get(key).get("columnName");
//	    		Double value = Double.parseDouble(payload.get(key).get("currentvalue").toString());
//	    		dataMap.put(columnName, value);
//	    		deviceDataMap.put(deviceId.toString(), dataMap);
//	    	}
//	    }
//	    if(!deviceDataMap.isEmpty())
//	    {
//	    	DeviceAPI.addDeviceData(timestamp, deviceDataMap);
//	    }
//	    if(!unmodelledInstances.isEmpty())
//		{
//	    	DeviceAPI.addUnmodelledData(controllerId, timestamp, unmodelledInstances);
//		}		
//	}
//
//	private List<String> getClients(DynamoDB dd) throws Exception 
//	{
//		List<String> clients = new ArrayList<>();
//		Table table = dd.getTable("IotClient");
//		QuerySpec spec = new QuerySpec().withHashKey("IotClient", "IotClient");
//		ItemCollection<QueryOutcome> items = table.query(spec);
//		Iterator<Item> iterator = items.iterator();
//		Item item = null;
//		
//		while (iterator.hasNext()) 
//		{
//		    item = iterator.next();
//		    clients.add(item.getString("ClientId"));
//		    Map<String, Object> payload = item.getMap("payload");
//		    if(((String)payload.get("status")).equals("connected"))
//		    {
//		    	Long controllerId = Long.parseLong(item.getString("ClientId").substring(item.getString("ClientId").lastIndexOf("-") + 1));
//		    	if((Integer)DeviceAPI.getControllerInfo(controllerId).get("status") == 1)
//		    	{
//		    		DeviceAPI.updateControllerStatus(controllerId, 2);
//		    	}
//		    }
//		}
//		return clients;
//	}
//	
//	private void deleteClients(DynamoDB dd, List<String> clients) 
//	{
//		Table table = dd.getTable("IotClient");
//		for(String client : clients)
//		{
//			table.deleteItem("IotClient", "IotClient", "ClientId", client);
//		}
//	}
}
