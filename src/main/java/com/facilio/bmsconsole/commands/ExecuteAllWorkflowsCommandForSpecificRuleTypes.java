package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.chain.FacilioChain;
import com.facilio.events.constants.EventConstants;

public class ExecuteAllWorkflowsCommandForSpecificRuleTypes extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Boolean isHistorical = (Boolean) context.get(EventConstants.EventContextNames.IS_HISTORICAL_EVENT);
		isHistorical = isHistorical != null ? isHistorical : Boolean.FALSE;
		
		if(!isHistorical) { // Execute notification ruletypes only if its not historical
			FacilioChain getExecuteAllWorkflowsForSpecificRuleTypesChain = TransactionChainFactory.getExecuteAllWorkflowsForSpecificRuleTypes();
			getExecuteAllWorkflowsForSpecificRuleTypesChain.execute(context);	
		}
		return false;
	}
}
