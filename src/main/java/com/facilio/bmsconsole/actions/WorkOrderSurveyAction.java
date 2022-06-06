package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkOrderSurveyAction extends FacilioAction{

	private Long workOrderId;

	public String fetchWorkOrderSurvey() throws Exception{

		FacilioChain chain = ReadOnlyChainFactory.fetchWorkOrderSurveyChain();
		FacilioContext context = chain.getContext();
		context.put(FacilioConstants.ContextNames.WORKORDER_ID,getWorkOrderId());
		chain.execute();

		setResult("isSurveyAvailable",context.get("isSurveyAvailable"));
		setResult("isSuperAdmin",context.get("isSuperAdmin"));
		setResult("isViewAllSurvey",context.get("isViewAllSurvey"));
		setResult("response",context.get("response"));

		return SUCCESS;
	}

}
