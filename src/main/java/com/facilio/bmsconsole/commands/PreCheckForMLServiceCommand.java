package com.facilio.bmsconsole.commands;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.MLServiceContext;
import com.facilio.bmsconsole.util.MLServiceAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;

public class PreCheckForMLServiceCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(PreCheckForMLServiceCommand.class.getName());
	private MLServiceContext mlServiceContext;
	@Override
	public boolean executeCommand(Context context) throws Exception {

		mlServiceContext = (MLServiceContext) context.get(FacilioConstants.ContextNames.ML_MODEL_INFO);
		//		LOGGER.info("Start of CheckDuplicateMLServiceCommand for usecase id "+mlServiceContext.getUseCaseId());
		try {
			addMLService(); 

			if(mlServiceContext.getServiceType().equals("default")) {
				LOGGER.info("I am checking default ML Model");
				checkDuplicateOnDefaultModels();
			} else {
				LOGGER.info("I am checking custom ML Model");
				checkDuplicateOnCustomModels();
			}

			dateRangeCheck();

		}catch(Exception e) {
			e.printStackTrace();
			LOGGER.info("Failed in CheckDuplicateMLServiceCommand for usecase id "+mlServiceContext.getUseCaseId());
			return true;
		}
		LOGGER.info("End of CheckDuplicateMLServiceCommand for usecase id "+mlServiceContext.getUseCaseId());
		return false;

	}

	private void dateRangeCheck() throws Exception {
		long startTime = mlServiceContext.getStartTime();
		long endTime = mlServiceContext.getEndTime();

		// default start and endtime setting
		if(endTime == 0) { //for development purpose
			endTime = DateTimeUtil.getCurrenTime();
		} else {
			LOGGER.info("Initial MLService startTime = " + startTime+", endTime = "+endTime);
		}

		long currentTime = DateTimeUtil.getCurrenTime();
		if(mlServiceContext.getEndTime() > currentTime) {
			String errorMsg = "MLService data date range falls in future period";
			mlServiceContext.updateStatus(errorMsg);
			throw new Exception(errorMsg);
		}

		long gracePeriod = 5 * MLServiceAPI.DAYS_IN_MILLISECONDS; //5 days
		boolean isPastData = mlServiceContext.getEndTime() < (currentTime-gracePeriod);
		LOGGER.info("MLService data range falls in past/historic : "+isPastData);
		mlServiceContext.setPastData(isPastData);

		Date date = new Date();
		date.setTime(endTime);
		DateFormat formatterLocal = new SimpleDateFormat("yyyy-MM-dd");
		formatterLocal.setTimeZone(TimeZone.getTimeZone(AccountUtil.getCurrentOrg().getTimezone()));
		String dateStr = formatterLocal.format(date);
		Date date1 = formatterLocal.parse(dateStr);
		endTime = date1.getTime();

		if(startTime == 0) {
			startTime = endTime - MLServiceAPI.TRAINING_SAMPLING_PERIOD;
		}

		LOGGER.info("Final MLService startTime = " + startTime+", endTime = "+endTime);
		mlServiceContext.setEndTime(endTime);
		mlServiceContext.setStartTime(startTime);

	}

	private void addMLService() throws Exception {
		try {
			Map<String, Object> mlServiceRow = new HashMap<>();
			mlServiceRow.put("orgId", AccountUtil.getCurrentOrg().getOrgId());

			long workflowId = MLServiceAPI.getWorkFlowId(mlServiceContext.getWorkflowInfo());
			LOGGER.info("workflowId :: "+workflowId);
			if(workflowId!=0) { 
				mlServiceContext.setWorkflowId(workflowId);
				mlServiceRow.put("workflowId", String.valueOf(workflowId));
				LOGGER.info("setting workflowId :: "+workflowId);
			}
			mlServiceRow.put("modelName", mlServiceContext.getModelName());
			//		mlServiceRow.put("modelType", mlServiceContext.getModelType());
			mlServiceRow.put("scenario", mlServiceContext.getScenario());
			mlServiceRow.put("mlModelMeta", mlServiceContext.getRequestMeta().toString());
			long usecaseId = MLServiceAPI.addMLService(mlServiceRow);
			mlServiceContext.setUseCaseId(usecaseId);
			LOGGER.info("MLService initial entry has been added ::"+usecaseId);
		} catch(Exception e) {
			String errorMsg = "Invalid params error for MLService";
			mlServiceContext.updateStatus(errorMsg);
			throw new Exception(errorMsg);
		}
	}


	private boolean checkDuplicateOnDefaultModels() throws Exception {
		List<Map<String, Object>> scenarioData = MLServiceAPI.getSuccessfullScenarios(mlServiceContext.getScenario());
		LOGGER.info(scenarioData.size());
		if(scenarioData == null || scenarioData.isEmpty()) {
			return false;
		}

		String errorMsg = "Given prediction name is already exists.";
		mlServiceContext.updateStatus(errorMsg);
		throw new Exception(errorMsg);
	}

	private void checkDuplicateOnCustomModels() throws Exception {
		List<List<String>> readingVariables = MLServiceAPI.getModelReadings(mlServiceContext.getModels());
		List<String> uniqueReadingVaribales = getUniqueReadingVariables(readingVariables);
		mlServiceContext.setReadingVariables(uniqueReadingVaribales);
		checkDuplicateScenarioWithReadings(mlServiceContext);
	}

	private List<String> getUniqueReadingVariables(List<List<String>> modelsReading) throws Exception {
		List<String> initModelReading = modelsReading.get(0);
		for(int i = 1; i < modelsReading.size(); i++) {
			List<String> currentModelReading = modelsReading.get(i);
			boolean isHomogeneousModel = CollectionUtils.isEqualCollection(initModelReading, currentModelReading);
			if(!isHomogeneousModel) {
				String errorMsg = "Given prediction name ["+mlServiceContext.getScenario()+"] has different readings for different models. Models aren't homogeneous. ";
				errorMsg += "Please give same set of readings for all the models.";
				mlServiceContext.updateStatus(errorMsg);
				throw new Exception(errorMsg);
			}
		}
		return initModelReading;
	}

	private void checkDuplicateScenarioWithReadings(MLServiceContext mlServiceContext) throws Exception {
		List<String> scenarioFields = MLServiceAPI.getReadingListForScenario(mlServiceContext.getScenario());
		if(scenarioFields != null) {
			List<String> currentFields = mlServiceContext.getReadingVariables();
			if(currentFields.size()!=scenarioFields.size()) {
				String errorMsg = "Given prediction name ["+mlServiceContext.getScenario()+"] is already exist with different readingVariables "+scenarioFields;
				errorMsg = errorMsg+". Please give different scenario name";
				mlServiceContext.updateStatus(errorMsg);
				throw new Exception(errorMsg);
			}
			List<String> filterFields = scenarioFields.stream()
					.filter(fieldName -> !currentFields.contains(fieldName))
					.collect(Collectors.toList());
			if(filterFields.size()!=0) {
				String errorMsg = "Given prediction name ["+mlServiceContext.getScenario()+"] is already exist with different readingVariables "+scenarioFields;
				errorMsg = errorMsg+". Please give different scenario name";
				mlServiceContext.updateStatus(errorMsg);
				throw new Exception(errorMsg);
			}
		}		
	}

}
