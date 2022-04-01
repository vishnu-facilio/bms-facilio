package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.MLContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.MLServiceUtil;
import com.facilio.bmsconsoleV3.context.V3MLServiceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.taskengine.job.JobContext;
import com.facilio.tasker.FacilioTimer;
import com.facilio.v3.exception.ErrorCode;
import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.List;

public class TriggerMLServiceJobCommand extends FacilioCommand implements Serializable {

	private static final Logger LOGGER = Logger.getLogger(TriggerMLServiceJobCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		V3MLServiceContext mlServiceContext = (V3MLServiceContext) context.get(MLServiceUtil.MLSERVICE_CONTEXT);
		if(!mlServiceContext.isHistoric() || mlServiceContext.getMlID() == null) {
			return false;
		}
		try {
			LOGGER.info("MLService historic data trigger started..");
			switch(mlServiceContext.getModelName()){
				case "energyprediction":
				case "ahuoptimization":
				case "loadprediction": {
					triggerSingleHistoricJob(mlServiceContext);
					break;
				}
				case "energyanomaly": {
					triggerParentChildHistoricJob(mlServiceContext);
					break;
				}
				default :{
					throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.VALIDATION_ERROR, "Given modelname is not available");
				}
			}
			MLServiceUtil.updateMLStatus(mlServiceContext, "Sucessfully triggered historic job for mlservice");
		} catch (Exception e) {
			throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.UNHANDLED_EXCEPTION, "Error while triggering historic mlservice job", e);
		}
		return false;
	}

	private void triggerSingleHistoricJob(V3MLServiceContext mlServiceContext) throws Exception {
		long jobId = mlServiceContext.getMlID();
		String jobName = "testMLHistoricalJob";

		JSONObject jobProps = new JSONObject();
		jobProps.put("startTime", mlServiceContext.getEndTime());
		jobProps.put("endTime", mlServiceContext.getEndTime()+1);
		jobProps.put("executionTime", mlServiceContext.getTrainingSamplingPeriod());
		jobProps.put("parentMlid", ""+jobId);

		try {
			scheduleHistoricJob(jobId, jobName, jobProps);
		} catch (Exception e) {
			String errMsg = "Error while triggering historic mlservice job";
			LOGGER.error(errMsg);
			e.printStackTrace();
			throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.UNHANDLED_EXCEPTION, errMsg);
		}


	}
	private void triggerParentChildHistoricJob(V3MLServiceContext mlServiceContext) throws Exception {
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
		jobProps.put("endTime", mlServiceContext.getEndTime()+(MLServiceUtil.DAYS_IN_MILLISECONDS));
		jobProps.put("executionTime", MLServiceUtil.DAYS_IN_MILLISECONDS);
		jobProps.put("parentMlid", ""+parentMlIdString);
		jobProps.put("child",childJson);

		try {
			scheduleHistoricJob(jobId, jobName, jobProps);
		} catch (Exception e) {
			String errMsg = "Failed while triggering Parent Child historic job";
			LOGGER.error(errMsg);
			e.printStackTrace();
			throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.UNHANDLED_EXCEPTION, errMsg);
		}

	}


	private void scheduleHistoricJob(long jobId, String jobName, JSONObject jobProps) throws Exception {
		JobContext jobContext = FacilioTimer.getJob(jobId, jobName);
		if(jobContext!=null) {
			BmsJobUtil.deleteJobWithProps(jobId, jobName);
		}
		FacilioTimer.scheduleOneTimeJobWithDelay(jobId, jobName, 30, "ml");
		BmsJobUtil.addJobProps(jobId, jobName, jobProps);
	}

}
