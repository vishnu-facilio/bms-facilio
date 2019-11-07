package com.facilio.bmsconsole.jobs;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class AddMLJob extends FacilioJob {

	private static final Logger LOGGER = Logger.getLogger(AddMLJob.class.getName());

	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobContext jc) throws Exception {
		// TODO Auto-generated method stub
		JSONObject props=BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
		
		Context context = new FacilioContext();
		LOGGER.info("ML JOB name : "+props.get("name").toString().toUpperCase());
		switch(props.get("name").toString().toUpperCase()){
			case "ANOMALY":{
				FacilioChain c = FacilioChainFactory.enableAnomalyDetectionChain();
				context.put("TreeHierarchy", props.get("TreeHierarchy"));
				context.put("meterInterval", props.get("meterInterval"));
				if(props.containsKey("ratioHierarchy"))
				{
					context.put("ratioHierarchy", props.get("ratioHierarchy"));
				}
				c.execute(context);
				break;
			}
			case "ENERGY":{
				FacilioChain c = FacilioChainFactory.addEnergyPredictionchain();
				context.put("energyMeterID",props.get("energyMeterId"));
				context.put("weekEnd", props.get("weekEnd"));
				context.put("meterInterval", props.get("meterInterval"));
				context.put("modelName", props.get("modelName"));
				context.put("modelPath", props.get("modelPath"));
				c.execute(context);		
				break;
			}
			case "LOAD":{
				FacilioChain c = FacilioChainFactory.addLoadPredictionchain();
				context.put("energyMeterID",props.get("energyMeterId"));
				context.put("weekEnd", props.get("weekEnd"));
				context.put("meterInterval", props.get("meterInterval"));
				context.put("modelName", props.get("modelName"));
				context.put("modelPath", props.get("modelPath"));
				c.execute(context);
				break;
			}
		}
		
	}

}
