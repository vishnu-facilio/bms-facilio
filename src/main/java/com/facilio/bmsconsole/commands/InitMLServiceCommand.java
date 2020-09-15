package com.facilio.bmsconsole.commands;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.chain.Context;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.actions.MLResponseParser;
import com.facilio.bmsconsole.context.MLResponseContext;
import com.facilio.bmsconsole.context.MLServiceContext;
import com.facilio.bmsconsole.util.MLAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;

public class InitMLServiceCommand extends FacilioCommand {

	private static final Logger LOGGER = Logger.getLogger(InitMLServiceCommand.class.getName());


	@Override
	public boolean executeCommand(Context context) throws Exception {

		MLServiceContext mlJobContext = (MLServiceContext) context.get(FacilioConstants.ContextNames.ML_MODEL_INFO);
		try {
			LOGGER.info("Start of InitMLModelCommand");

			JSONObject postObj = mlJobContext.getReqJson();
			postObj.put("date", getCurrentDate(true));
			postObj.put("predictedtime", 1591972500000L);
			postObj.put("orgDetails", mlJobContext.getOrgDetails());
			postObj.put("data", mlJobContext.getDataObject());
			postObj.put("usecaseId", mlJobContext.getUseCaseId());

			String postURL= FacilioProperties.getMlModelBuildingApi();
			Map<String, String> headers = new HashMap<>();
			LOGGER.info("whole data ::\n"+postObj.toString());

			headers.put("Content-Type", "application/json");
			LOGGER.info(" Sending request to ML Server "+postURL+"::"+mlJobContext.getUseCaseId());
			String result = AwsUtil.doHttpPost(postURL, headers, null, postObj.toString(),300);
			if(StringUtils.isEmpty(result) || result.contains("Internal Server Error")){
				LOGGER.fatal("Error_ML "+ postURL + " usecase ID : "+mlJobContext.getUseCaseId()+" ERROR MESSAGE : "+"Response is not valid. RESULT : "+result);
				context.put("ML_ERROR", true);
			} else {
				LOGGER.info("\n\n\n\nmlresponse :: \n"+result);
				LOGGER.info("I am sleeping for next 20 sec");
				Thread.sleep(1000 * 20);
				JSONObject response = new JSONObject(result);
				MLResponseContext  mlResponse = FieldUtil.getAsBeanFromMap(response.toMap(), MLResponseContext.class);
				Map<String, Object> row = new HashMap<>();
				row.put("status", mlResponse.getMessage());
				MLAPI.updateMLServiceInfo(mlResponse.getUsecaseId(), row);
				LOGGER.info("\nML Status has been updated :: \n"+result);
				mlJobContext.setMlResponse(mlResponse);
			}
		}catch(Exception e){
			if(!e.getMessage().contains("ML error")){
				LOGGER.fatal("Error_JAVA "+ FacilioProperties.getMlModelBuildingApi() + " ML ID : "+mlJobContext.getUseCaseId()+" FILE : GenerateMLModelCommand "+" ERROR MESSAGE : "+e);
				throw e;
			}
		}
		LOGGER.info("End of InitMLModelCommand");
		return false;

	}

	private Object getCurrentDate(boolean dummyHit) {
		if(dummyHit) {
			return "2020-06-12";
		}
		else {
			return LocalDate.now(TimeZone.getTimeZone(AccountUtil.getCurrentAccount().getTimeZone()).toZoneId());
		}
	}
}
