package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;

public class SetTicketModuleTableNames implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "ticket");
		context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, "Tickets");
		
		return false;
	}

}
