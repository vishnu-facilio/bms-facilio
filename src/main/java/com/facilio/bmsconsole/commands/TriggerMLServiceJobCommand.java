package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.MLServiceContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.tasker.FacilioTimer;

public class TriggerMLServiceJobCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(TriggerMLServiceJobCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		MLServiceContext mlServiceContext = (MLServiceContext) context.get(FacilioConstants.ContextNames.ML_MODEL_INFO);
		if(!mlServiceContext.isPastData()) {
			return false;
		}
		try {

			switch(mlServiceContext.getModelName()){
			case "energyprediction":
			case "loadprediction": {
				triggerSingleHistoricJob(mlServiceContext);
				break;
			}
			default :{
				String errMsg = "Given modelname is not available";
				LOGGER.fatal(errMsg);
				mlServiceContext.updateStatus(errMsg);
				return true;
			}
			}
			mlServiceContext.updateStatus("Successfully activated mlservice ");
			LOGGER.info("Sucessfully triggered historic job for mlservice");
			return false;

		} catch (Exception e) {
			LOGGER.error("Error while triggering mlservice job");
			throw e;
		}
	}

	private void triggerSingleHistoricJob(MLServiceContext mlServiceContext) throws Exception {
		long jobId = mlServiceContext.getMlID();
		String jobName = "testMLHistoricalJob";
		
		JSONObject jobProps = new JSONObject();
		jobProps.put("startTime", mlServiceContext.getEndTime());
		jobProps.put("endTime", mlServiceContext.getEndTime()+1);
		jobProps.put("executionTime", mlServiceContext.getTrainingSamplingPeriod());
		jobProps.put("parentMlid", ""+jobId);
		
		try {
			FacilioTimer.scheduleOneTimeJobWithDelay(jobId, jobName, 30, "ml");
			BmsJobUtil.addJobProps(jobId, jobName, jobProps);
		} catch (Exception e) {
			e.printStackTrace();
			String errorMsg = "Failed while triggering historic job";
			LOGGER.info(errorMsg);
			mlServiceContext.updateStatus(errorMsg);
			throw new Exception(errorMsg);
		}
		
		
	}

}
