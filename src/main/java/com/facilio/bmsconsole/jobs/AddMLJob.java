package com.facilio.bmsconsole.jobs;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;

public class AddMLJob extends FacilioJob {

	private static final Logger LOGGER = Logger.getLogger(AddMLJob.class.getName());

	@Override
	public void execute(JobContext jc) throws Exception {
		// TODO Auto-generated method stub
		JSONObject props=BmsJobUtil.getJobProps(jc.getJobId(), jc.getJobName());
		
		LOGGER.info("ML JOB name : "+props.get("name").toString().toUpperCase());
		switch(props.get("name").toString().toUpperCase()){
			case "ANOMALY":{
				if((!props.containsKey("parentHierarchy")) || (props.containsKey("parentHierarchy") && props.get("parentHierarchy").toString().equalsIgnoreCase("true"))){
					FacilioChain chain = FacilioChainFactory.enableAnomalyDetectionChain();
					FacilioContext context = chain.getContext();
					context.put("TreeHierarchy", props.get("TreeHierarchy"));
					context.put("energyDeltaField", props.get("energyDeltaField"));
					if(props.containsKey("markedField"))
					{
						context.put("markedField", props.get("markedField"));
					}
					context.put("parentIdField", props.get("parentIdField"));
					
					context.put("mlModelVariables", props.get("mlModelVariables"));
					if(props.containsKey("mlVariables"))
					{
						context.put("mlVariables", props.get("mlVariables"));
					}
					if(props.containsKey("ratioHierarchy"))
					{
						context.put("ratioHierarchy", props.get("ratioHierarchy"));
					}
					context.put("parentHierarchy", "true");
					chain.execute();
				}else if(props.get("parentHierarchy").toString().equalsIgnoreCase("false")){
					 String[] ids = props.get("assetIds").toString().split(",");
					for (int i = 0; i < ids.length; i++) {
						FacilioChain chain = FacilioChainFactory.enableAnomalyDetectionChain();
						FacilioContext context = chain.getContext();
						context.put("TreeHierarchy", ids[i]);
						context.put("energyDeltaField", props.get("energyDeltaField"));
						if(props.containsKey("markedField"))
						{
							context.put("markedField", props.get("markedField"));
						}
						context.put("parentIdField", props.get("parentIdField"));
						if(props.containsKey("mlVariables"))
						{
							context.put("mlVariables", props.get("mlVariables"));
						}
						context.put("mlModelVariables", props.get("mlModelVariables"));
						context.put("parentHierarchy", props.get("parentHierarchy"));
						chain.execute();
					}
				}
				break;
			}
			case "ENERGY":{
				FacilioChain chain = FacilioChainFactory.addEnergyPredictionchain();
				FacilioContext context = chain.getContext();
				context.put("energyMeterID",props.get("energyMeterId"));
				context.put("mlModelVariables", props.get("mlModelVariables"));
				context.put("mlVariables", props.get("mlVariables"));
				context.put("modelPath", props.get("modelPath"));
				chain.execute();
				break;
			}
			case "LOAD":{
				FacilioChain chain = FacilioChainFactory.addLoadPredictionchain();
				FacilioContext context = chain.getContext();
				context.put("energyMeterID",props.get("energyMeterId"));
				context.put("mlModelVariables", props.get("mlModelVariables"));
				context.put("mlVariables", props.get("mlVariables"));
				context.put("modelPath", props.get("modelPath"));
				chain.execute();
				break;
			}
			default :{
				LOGGER.fatal("Name not matched with any Job");
			}
		}
		
	}

}
