package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.MLServiceAPI;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import java.util.List;
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
		if(!mlServiceContext.isPastData() || mlServiceContext.getMlID() == 0) {
			return false;
		}
		try {

			switch(mlServiceContext.getModelName()){
				case "energyprediction":
				case "loadprediction": {
					triggerSingleHistoricJob(mlServiceContext);
					break;
				}
				case "energyanomaly": {
					triggerParentChildHistoricJob(mlServiceContext);
					break;
				}
				default :{
					String errMsg = "Given modelname is not available";
					LOGGER.fatal(errMsg);
					mlServiceContext.updateStatus(errMsg);
					return true;
				}
			}
			mlServiceContext.updateStatus("Successfully activated mlservice for historic data");
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
	private void triggerParentChildHistoricJob(MLServiceContext mlServiceContext) throws Exception {
		List<Long> parentMlIdList  = mlServiceContext.getParentMlIdList();
		Long jobId = parentMlIdList.get(0);
		String jobName = "testMLHistoricalJob";
		Long exectionTime = 60*60*1000L;


		String parentMlIdString = parentMlIdList.toString().replace("[", "").replace("]", "").replace(" ", "");
		String childMlIdString = mlServiceContext.getChildMlIdList().toString().replace("[", "").replace("]", "").replace(" ", "");

		JSONObject jobProps = new JSONObject();
		JSONObject childJson = new JSONObject();
		childJson.put("childMlid",childMlIdString);
		childJson.put("executionTime", exectionTime);
		jobProps.put("startTime", mlServiceContext.getEndTime());
		jobProps.put("endTime", mlServiceContext.getEndTime()+(MLServiceAPI.DAYS_IN_MILLISECONDS));
		jobProps.put("executionTime", MLServiceAPI.DAYS_IN_MILLISECONDS);
		jobProps.put("parentMlid", ""+parentMlIdString);
		jobProps.put("child",childJson);

		try {
			FacilioTimer.scheduleOneTimeJobWithDelay(jobId, jobName, 30, "ml");
			BmsJobUtil.addJobProps(jobId, jobName, jobProps);
		} catch (Exception e) {
			e.printStackTrace();
			String errorMsg = "Failed while triggering Parent Child  historic job";
			LOGGER.info(errorMsg);
			mlServiceContext.updateStatus(errorMsg);
			throw new Exception(errorMsg);
		}

	}
}
