package com.facilio.tasker.tasks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.aws.util.AwsUtil;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class SyncIotDataJob extends FacilioJob{

	private static Logger logger = Logger.getLogger(SyncIotDataJob.class.getName());
	
	static int counter = 31;
	@Override
	public void execute(JobContext jc) 
	{
		try
		{
			logger.log(Level.INFO, "Executing SyncIotDataJob ::::JobID:::" + jc.getJobId());
			
			String secretKey = AwsUtil.getConfig("secretKeyId");
	        String dateStamp = new SimpleDateFormat("yyyyMMdd").format(new Date()); 	//"20170525";
	        String regionName = AwsUtil.getConfig("region");		//"us-west-2";
	        String serviceName = AwsUtil.AWS_IOT_SERVICE_NAME;
	        
	        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
	        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	        String xAmzDate = dateFormat.format(new Date());
	        
	        String payload = "{\n\"state\":{\n\"desired\":" + AwsUtil.getDeviceData() + "\n},\n\"clientToken\":\"shiva2\",\n\"version\":" + counter + "\n}";
	        System.out.println("payload :::" +payload);
	        String signature = AwsUtil.getSignature(secretKey, dateStamp, regionName, serviceName, payload, xAmzDate);
	        String url = "https://" + AwsUtil.getConfig("host") + "/things/EM/shadow";
	        
	        Map<String, String> headers = new HashMap<String, String>();
	        headers.put("Content-Type", "application/json");
	        headers.put("Host", AwsUtil.getConfig("host"));
	        headers.put("X-Amz-Date", xAmzDate);
	        headers.put("Authorization", "AWS4-HMAC-SHA256 Credential=" + AwsUtil.getConfig("accessKeyId") + "/" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + AwsUtil.getConfig("region") + "/" + AwsUtil.AWS_IOT_SERVICE_NAME + "/aws4_request, SignedHeaders=content-type;host;x-amz-date, Signature=" + signature);
	        
	        AwsUtil.doHttpPost(url, headers, null, payload);
	        counter++;
		}
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, "Exeception while executing SyncIotDataJob :::"+e.getMessage(), e);
		}
	}
}
