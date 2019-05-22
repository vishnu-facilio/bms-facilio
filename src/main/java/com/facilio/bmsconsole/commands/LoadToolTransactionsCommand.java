package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;

public class LoadToolTransactionsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		  context.put(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.TOOL_TRANSACTIONS)); 
	      return false; 
	 }

}
