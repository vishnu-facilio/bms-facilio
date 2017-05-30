package com.facilio.tasker.tasks;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.aws.util.AwsUtil;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class SyncIotDataJob extends FacilioJob{

	private static Logger logger = Logger.getLogger(SyncIotDataJob.class.getName());
	
	@Override
	public void execute(JobContext jc) 
	{
		try
		{
			String topic = "iotdata";
			String clientId = "100"; //TODO
            AwsUtil.getAwsIotMqttClient(clientId).publish(topic, AwsUtil.getDeviceData());
		}
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, "Exeception while executing SyncIotDataJob :::"+e.getMessage(), e);
		}
	}
}
