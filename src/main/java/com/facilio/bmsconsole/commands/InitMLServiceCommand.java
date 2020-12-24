package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.MLResponseContext;
import com.facilio.bmsconsole.context.MLServiceContext;
import com.facilio.bmsconsole.util.MLServiceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;

public class InitMLServiceCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(InitMLServiceCommand.class.getName());


	@Override
	public boolean executeCommand(Context context) throws Exception {

		MLServiceContext mlServiceContext = (MLServiceContext) context.get(FacilioConstants.ContextNames.ML_MODEL_INFO);
		Long predictedTime = (Long) context.get(FacilioConstants.ContextNames.END_TTIME);
		try {
			LOGGER.info("Start of InitMLModelCommand for usecase id "+mlServiceContext.getUseCaseId());

			JSONObject modelVariables = mlServiceContext.getReqJson();
			modelVariables.put("orgDetails", mlServiceContext.getOrgDetails());
			JSONObject postObj = new JSONObject();
			postObj.put("modelvariables", modelVariables);
			postObj.put("date", String.valueOf(MLServiceAPI.getCurrentDate(false)));
			postObj.put("predictedtime", predictedTime);
			postObj.put("usecaseId", mlServiceContext.getUseCaseId());

			LOGGER.info("ML api request without data :: "+postObj.toString());
			postObj.put("data", mlServiceContext.getDataObject());
			//			String postURL= FacilioProperties.getMlModelBuildingApi();
			String postURL= FacilioProperties.getAnomalyPredictAPIURL() + "/trainingModel";
			Map<String, String> headers = new HashMap<>();

			headers.put("Content-Type", "application/json");
			LOGGER.info(" Sending request to ML Server "+postURL+"::"+mlServiceContext.getUseCaseId());

			String result = AwsUtil.doHttpPost(postURL, headers, null, postObj.toString(),300);
			if(StringUtils.isEmpty(result) || result.contains("Internal Server Error")){
				if(StringUtils.isEmpty(result)) {
					result = "Server is not reachable";
				}
				String error = "Error_ML "+ postURL + " usecase ID : "+mlServiceContext.getUseCaseId()+" ERROR MESSAGE : "+"Response is not valid. RESULT : "+result;
				LOGGER.fatal(error);
				mlServiceContext.setStatus(error);
				MLServiceAPI.updateMLServiceStatus(mlServiceContext.getUseCaseId(), result);
			} else {
				//				LOGGER.info("\nmlresponse :: \n"+result);

				JSONParser parser = new JSONParser();
				JSONObject response = (JSONObject) parser.parse(result);
				//				JSONObject response = new JSONObject(result);
				//				Map<String,Object> response = new ObjectMapper().readValue(result, HashMap.class);
				MLResponseContext mlResponse = FieldUtil.getAsBeanFromJson(response, MLResponseContext.class);
				MLServiceAPI.updateMLServiceStatus(mlResponse.getUsecaseId(), mlResponse.getMessage());
				LOGGER.info("\nML Status has been updated...");
//				LOGGER.info(result);
				mlServiceContext.setMlResponse(mlResponse);
				if(mlResponse!=null) {
					mlServiceContext.setStatus(mlResponse.getMessage() +" for usecase id : "+ mlServiceContext.getUseCaseId());
				}
			}
		}catch(Exception e){
			String errMsg = "ML Api hit failed";
			String error = "Error_ML "+ FacilioProperties.getMlModelBuildingApi() + " usecase ID : "+mlServiceContext.getUseCaseId()+" FILE : InitMLServiceCommand "+" ERROR MESSAGE : "+errMsg;
			LOGGER.fatal(error);
			mlServiceContext.setStatus(error);
			MLServiceAPI.updateMLServiceStatus(mlServiceContext.getUseCaseId(), errMsg);
		}
		LOGGER.info("End of InitMLModelCommand for usecase id "+mlServiceContext.getUseCaseId());
		return false;

	}
}
