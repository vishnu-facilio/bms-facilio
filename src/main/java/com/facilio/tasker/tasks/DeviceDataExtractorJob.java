package com.facilio.tasker.tasks;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.device.types.DistechControls;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class DeviceDataExtractorJob extends FacilioJob{

	private static Logger logger = Logger.getLogger(DeviceDataExtractorJob.class.getName());
	
	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobContext jc) 
	{
		try
		{
			Long controllerId = DeviceAPI.getControllerId(jc.getJobId());
			Long clientId = AssetsAPI.getOrgId(controllerId);
			String topic = AwsUtil.AWS_IOT_SERVICE_NAME;
			
			String userName = "admin";
	    	String password = "Admin@1234";
			String ipAddress = "192.168.0.148";
			JSONObject payload = new DistechControls(userName, password, ipAddress).getPresentValues();
			JSONObject metainfo = new JSONObject();
			metainfo.put("controllerId", controllerId.toString());
			payload.put("metainfo", metainfo);
            AwsUtil.getAwsIotMqttClient(clientId.toString()).publish(topic, payload.toString());
		}
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, "Exception while executing DeviceDataExtractorJob :::"+e.getMessage(), e);
		}
	}
}
