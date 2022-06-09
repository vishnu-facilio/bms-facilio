package com.facilio.qa.command;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.qa.displaylogic.util.DisplayLogicUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;

public class PrepareDataForDisplayLogicExecutionCommand extends FacilioCommand {
    
	@Override
    public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		
		JSONObject answerData = new JSONObject();
		
		Long responseId = getResponseId((FacilioContext) context);
		
		if(responseId == null) {
			return true;
		}
		
		answerData.put("response", responseId);
		
		context.put(FacilioConstants.QAndA.Command.ANSWER_DATA, answerData);
		context.put(DisplayLogicUtil.IS_DISPLAY_LOGIC_EXECUTION_ON_PAGE_LOAD, Boolean.TRUE);
		
		return false;
	}

	 private Long getResponseId (FacilioContext context) {
	        Object responseId = Constants.getQueryParam(context, "response");
	        return responseId == null ? null : FacilioUtil.parseLong(responseId);
	    }
}
