package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;

public class LoadToolTransactionsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		  context.put(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.TOOL_TRANSACTIONS)); 
	      return false; 
	 }

}
