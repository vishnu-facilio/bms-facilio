package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.command.FacilioCommand;
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
		Long predictedTime = mlServiceContext.getExecuteTime();
		try {

			LOGGER.info("Start of InitMLModelCommand for usecase id "+mlServiceContext.getUseCaseId());

			List<List<Map<String, Object>>> models =  mlServiceContext.getModels();
			List<MLResponseContext> mlResponseContextList = new ArrayList<MLResponseContext>();

			JSONObject requestMeta = mlServiceContext.getRequestMeta();
			requestMeta.put("orgDetails", mlServiceContext.getOrgDetails());

			for(int index = 0;index < models.size();index++) {

				List<Map<String, Object>> singleModel = models.get(index);
				requestMeta.put("models", singleModel);  //updating with current asset model details

				JSONObject postObj = new JSONObject();
				postObj.put("requestMeta", requestMeta);
				postObj.put("date", String.valueOf(MLServiceAPI.getCurrentDate(false)));
				postObj.put("predictedtime", predictedTime);
				postObj.put("usecaseId", mlServiceContext.getUseCaseId());
				postObj.put("assetId" , mlServiceContext.getAssetList().get(index));
				LOGGER.info("ML api request without data :: "+postObj.toString());
				postObj.put("data", mlServiceContext.getDataObjectList().get(index));
				//			String postURL= FacilioProperties.getMlModelBuildingApi();
				String postURL= FacilioProperties.getAnomalyPredictAPIURL() + "/trainingModel";
				Map<String, String> headers = new HashMap<>();

				headers.put("Content-Type", "application/json");
				LOGGER.info(" Sending request to ML Server "+postURL+"::"+mlServiceContext.getUseCaseId());

				String result = AwsUtil.doHttpPost(postURL, headers, null, postObj.toString(),300);
				if(StringUtils.isEmpty(result) || result.contains("Internal Server Error")){
					if(StringUtils.isEmpty(result)) {
						result = "MLServer is not reachable";
					}
					String error = "Error_ML "+ postURL + " usecase ID : "+mlServiceContext.getUseCaseId()+" ERROR MESSAGE : "+"Response is not valid. RESULT : "+result;
					LOGGER.fatal(error);
					mlServiceContext.updateStatus(result);
					break;
				} else {
					//				LOGGER.info("\nmlresponse :: \n"+result);
					JSONParser parser = new JSONParser();
					JSONObject response = (JSONObject) parser.parse(result);
					MLResponseContext mlResponse = FieldUtil.getAsBeanFromJson(response, MLResponseContext.class);
					LOGGER.info("\nML Status has been updated...");
					//mlServiceContext.setMlResponse(mlResponse);
					if(mlResponse!=null) {
						mlServiceContext.updateStatus(mlResponse.getMessage());
						mlResponseContextList.add(mlResponse);
						if(!mlResponse.getStatus()) {
							break;
						}
					}
				}
			}
			mlServiceContext.setMlResponseList(mlResponseContextList);

		}
		catch(Exception e){
			String errMsg = "ML Api hit failed";
			String error = "Error_ML "+ FacilioProperties.getMlModelBuildingApi() + " usecase ID : "+mlServiceContext.getUseCaseId()+" FILE : InitMLServiceCommand "+" ERROR MESSAGE : "+errMsg;
			LOGGER.fatal(error);
			mlServiceContext.updateStatus(errMsg);
			return true;
		}
		LOGGER.info("End of InitMLModelCommand for usecase id "+mlServiceContext.getUseCaseId());
		return false;

	}
}
