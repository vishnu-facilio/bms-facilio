package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.MLServiceContext;
import com.facilio.bmsconsole.util.MLServiceAPI;
import com.facilio.constants.FacilioConstants;

public class CheckDuplicateMLServiceCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(InitMLServiceCommand.class.getName());


	@Override
	public boolean executeCommand(Context context) throws Exception {

		MLServiceContext mlServiceContext = (MLServiceContext) context.get(FacilioConstants.ContextNames.ML_MODEL_INFO);
//		LOGGER.info("Start of CheckDuplicateMLServiceCommand for usecase id "+mlServiceContext.getUseCaseId());
		try {
			checkDuplicateScenario(mlServiceContext);
		}catch(Exception e){
			LOGGER.info("Failed in CheckDuplicateMLServiceCommand for usecase id "+mlServiceContext.getUseCaseId());
			return true;
		}
//		LOGGER.info("End of CheckDuplicateMLServiceCommand for usecase id "+mlServiceContext.getUseCaseId());
		return false;

	}

//	private void checkReadingData(MLServiceContext mlServiceContext) throws Exception {
//		Long assetId = (Long) mlServiceContext.getAssetDetails().get(FacilioConstants.ContextNames.ASSET_ID);
//		List<String> readingVariable = mlServiceContext.getReadingList();
//		LOGGER.info("ML Service assetId :: "+assetId + " and readingVariables : "+readingVariable);
//		List<Map<String, Object>> readingFieldsDetails = MLServiceAPI.getReadingFields(assetId, readingVariable);
//		LOGGER.info(readingFieldsDetails.size());
//		throw new Exception("Stopping explicitily..");
//	}

	private void checkDuplicateScenario(MLServiceContext mlServiceContext) throws Exception {
		List<String> scenarioFields = MLServiceAPI.getReadingListForScenario(mlServiceContext.getScenario());
		if(scenarioFields != null) {
			List<String> currentFields = mlServiceContext.getReadingList();
			if(currentFields.size()!=scenarioFields.size()) {
				String errorMsg = "Given scenario ["+mlServiceContext.getScenario()+"] is already exist with different readingVariables "+scenarioFields;
				errorMsg = errorMsg+". Please give different scenario name";
				mlServiceContext.setStatus(errorMsg);
				throw new Exception(errorMsg);
			}
			List<String> filterFields = scenarioFields.stream()
					.filter(fieldName -> !currentFields.contains(fieldName))
					.collect(Collectors.toList());
			if(filterFields.size()!=0) {
				String errorMsg = "Given scenario ["+mlServiceContext.getScenario()+"] is already exist with different readingVariables "+scenarioFields;
				errorMsg = errorMsg+". Please give different scenario name";
				mlServiceContext.setStatus(errorMsg);
				throw new Exception(errorMsg);
			}
		}		
	}
}
