package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.constants.FacilioConstants;

public class GetAllBaseLinesCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		context.put(FacilioConstants.ContextNames.BASE_LINE_LIST, BaseLineAPI.getAllBaseLines());
		return false;
	}

}
