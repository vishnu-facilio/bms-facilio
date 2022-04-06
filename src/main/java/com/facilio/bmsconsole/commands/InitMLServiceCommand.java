package com.facilio.bmsconsole.commands;

import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.MLResponseContext;
import com.facilio.bmsconsole.util.MLServiceUtil;
import com.facilio.bmsconsoleV3.context.V3MLServiceContext;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.FieldUtil;
import com.facilio.v3.exception.ErrorCode;
import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InitMLServiceCommand extends FacilioCommand implements Serializable {

	private static final Logger LOGGER = Logger.getLogger(InitMLServiceCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		V3MLServiceContext mlServiceContext = (V3MLServiceContext) context.get(MLServiceUtil.MLSERVICE_CONTEXT);
		boolean updateApi = context.get(MLServiceUtil.IS_UPDATE) != null;
		Long predictedTime = mlServiceContext.getEndTime();
		String postURL= FacilioProperties.getAnomalyPredictAPIURL() + "/trainingModel";
		int dataDuration = MLServiceUtil.getDataDuration(mlServiceContext);
		try {

			LOGGER.info("Start of InitMLModelCommand for usecase id "+mlServiceContext.getId());

			List<List<Map<String, Object>>> models =  mlServiceContext.getModelReadings();
			List<MLResponseContext> mlResponseContextList = new ArrayList<>();

			JSONObject requestMeta = mlServiceContext.getMlModelMetaJson();
			requestMeta.put("orgDetails", MLServiceUtil.getOrgInfo());

			List<Long> assetIds = MLServiceUtil.getAllAssetIds(mlServiceContext);

			for(int index = 0;index < models.size();index++) {

				List<Map<String, Object>> singleModel = models.get(index);
				requestMeta.put("models", singleModel);  //updating with current asset model details

				JSONObject postObj = new JSONObject();
				postObj.put("requestMeta", requestMeta);
				postObj.put("date", String.valueOf(MLServiceUtil.getCurrentDate(false)));
				postObj.put("predictedtime", predictedTime);
				postObj.put("usecaseId", mlServiceContext.getId());
				postObj.put("assetId" , assetIds.get(index));
				postObj.put("duration" , dataDuration);
				LOGGER.info("ML api request without data :: "+postObj.toString());
				if(updateApi) {
					postObj.put("data", new JSONArray());
				} else {
					postObj.put("data", mlServiceContext.getDataObjectList().get(index));
				}
				//			String postURL= FacilioProperties.getMlModelBuildingApi();
				Map<String, String> headers = new HashMap<>();

				headers.put("Content-Type", "application/json");
				LOGGER.info(" Sending request to ML Server "+postURL+"::"+mlServiceContext.getId());

				String result = AwsUtil.doHttpPost(postURL, headers, null, postObj.toString(),300);
				LOGGER.info("\nmlresponse :: \n"+result);
				if(StringUtils.isEmpty(result) || result.contains("Internal Server Error")){
					LOGGER.fatal("Error_ML "+ postURL + " usecase ID : "+mlServiceContext.getId()+" ERROR MESSAGE : "+"Response is not valid. RESULT : "+result);
					throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.UNHANDLED_EXCEPTION, "MLServer is not reachable");
				} else {
					JSONParser parser = new JSONParser();
					JSONObject response = (JSONObject) parser.parse(result);
					MLResponseContext mlResponse = FieldUtil.getAsBeanFromJson(response, MLResponseContext.class);
					LOGGER.info("\nML Status has been updated...");
					//mlServiceContext.setMlResponse(mlResponse);
					if(mlResponse!=null) {
						mlServiceContext.setStatus(mlResponse.getMessage());
						mlResponseContextList.add(mlResponse);
						if(!mlResponse.getStatus()) {
							String errMsg = "error in ml core";
							if(mlResponse.getMessage()!=null) {
								errMsg += " : "+mlResponse.getMessage();
							}
							throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.UNHANDLED_EXCEPTION, errMsg);
						}
					}
				}
			}
			mlServiceContext.setMlResponseList(mlResponseContextList);
			MLServiceUtil.updateMLStatus(mlServiceContext, "ML Server api hit success");
		}
		catch(Exception e){
			e.printStackTrace();
			String errMsg = "ML Server api failed";
			LOGGER.error("Error_ML "+ postURL + " usecase ID : "+mlServiceContext.getId()+" FILE : InitMLServiceCommand "+" ERROR MESSAGE : "+errMsg);
			throw MLServiceUtil.throwError(mlServiceContext, ErrorCode.UNHANDLED_EXCEPTION, errMsg);
		}
		LOGGER.info("End of InitMLModelCommand for usecase id "+mlServiceContext.getId());
		return false;

	}
}
