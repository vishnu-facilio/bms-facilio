package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class OperationAlarmAction extends FacilioAction{
	private static final long serialVersionUID = 1L;

	public String getResource() throws Exception
	{
		FacilioContext context = new FacilioContext();
		long hrsToCheckinMillis= 3600000;
		long endTime = System.currentTimeMillis();
		long startTime = System.currentTimeMillis() - hrsToCheckinMillis ;
		context.put(FacilioConstants.ContextNames.START_TIME, startTime);
		context.put(FacilioConstants.ContextNames.END_TIME, endTime);
		FacilioChain newAsset = TransactionChainFactory.getExecuteOperationAlarm();
		newAsset.execute(context);
		return SUCCESS;
	}

}
