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
	
	static int counter = 34;
	@Override
	public void execute(JobContext jc) 
	{
		try
		{
			logger.log(Level.INFO, "Executing SyncIotDataJob ::::JobID:::" + jc.getJobId());
			
	        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
	        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	        String xAmzDate = dateFormat.format(new Date());
	        
	        String path = "/things/EM/shadow";
	        String url = "https://" + AwsUtil.getConfig("host") + path;
	        String payload = "{\n\"state\":{\n\"desired\":" + AwsUtil.getDeviceData() + "\n},\n\"clientToken\":\"shiva2\",\n\"version\":" + counter + "\n}";
	        String signature = AwsUtil.getSignature(payload, xAmzDate, path);
	        Map<String, String> headers = AwsUtil.getAuthHeaders(signature, xAmzDate);
	        AwsUtil.doHttpPost(url, headers, null, payload);
	        
	        counter++;
		}
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, "Exeception while executing SyncIotDataJob :::"+e.getMessage(), e);
		}
	}
}
