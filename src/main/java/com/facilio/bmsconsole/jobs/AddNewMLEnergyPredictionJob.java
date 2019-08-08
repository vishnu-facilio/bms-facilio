package com.facilio.bmsconsole.jobs;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.chain.FacilioContext;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

public class AddNewMLEnergyPredictionJob extends FacilioJob {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobContext jc) throws Exception {
		// TODO Auto-generated method stub
		JSONObject props=BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
		
		Context context = new FacilioContext();
		
		if(props.get("isAnomaly").toString().equals("1"))
		{
			Chain c = FacilioChainFactory.enableAnomalyDetectionChain();
			context.put("TreeHierarchy", props.get("TreeHierarchy"));
			context.put("meterInterval", props.get("meterInterval"));
			c.execute(context);
		}
		else
		{
			Chain c = FacilioChainFactory.addEnergyPredictionchain();
			context.put("energyMeterID",props.get("energyMeterId"));
			context.put("weekEnd", props.get("weekEnd"));
			context.put("meterInterval", props.get("meterInterval"));
			c.execute(context);
		}
	}

}
