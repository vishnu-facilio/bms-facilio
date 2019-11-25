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
				if(!props.containsKey("parentHierarchy") || (props.containsKey("parentHierarchy") && props.get("parentHierarchy").toString().equalsIgnoreCase("true"))){
					FacilioChain c = FacilioChainFactory.enableAnomalyDetectionChain();
					context.put("TreeHierarchy", props.get("TreeHierarchy"));
					context.put("energyDeltaField", props.get("energyDeltaField"));
					context.put("markedField", props.get("markedField"));
					context.put("parentIdField", props.get("parentIdField"));
					context.put("mlVariables", props.get("mlVariables"));
					context.put("mlModelVariables", props.get("mlModelVariables"));
					if(props.containsKey("ratioHierarchy"))
					{
						context.put("ratioHierarchy", props.get("ratioHierarchy"));
					}
					c.execute(context);
				}else if(props.get("parentHierarchy").toString().equalsIgnoreCase("false")){
					 String[] ids = props.get("assetIds").toString().split(",");
					for (int i = 0; i < ids.length; i++) {
						FacilioChain c = FacilioChainFactory.enableAnomalyDetectionChain();
						context.put("TreeHierarchy", ids[i]);
						context.put("energyDeltaField", props.get("energyDeltaField"));
						context.put("markedField", props.get("markedField"));
						context.put("parentIdField", props.get("parentIdField"));
						context.put("mlVariables", props.get("mlVariables"));
						context.put("mlModelVariables", props.get("mlModelVariables"));
						c.execute(context);
					}
				}
				break;
			}
			case "ENERGY":{
				FacilioChain c = FacilioChainFactory.addEnergyPredictionchain();
				context.put("energyMeterID",props.get("energyMeterId"));
				context.put("mlModelVariables", props.get("mlModelVariables"));
				context.put("mlVariables", props.get("mlVariables"));
				context.put("modelPath", props.get("modelPath"));
				c.execute(context);		
				break;
			}
			case "LOAD":{
				FacilioChain c = FacilioChainFactory.addLoadPredictionchain();
				context.put("energyMeterID",props.get("energyMeterId"));
				context.put("mlModelVariables", props.get("mlModelVariables"));
				context.put("mlVariables", props.get("mlVariables"));
				context.put("modelPath", props.get("modelPath"));
				c.execute(context);
				break;
			}
			default :{
				LOGGER.fatal("Name not matched with any Job");
			}
		}
		
	}

}
