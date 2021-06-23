package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.constants.FacilioConstants;

public class FetchControllerSettingCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ControllerContext controller = (ControllerContext) context.get(FacilioConstants.ContextNames.CONTROLLER_SETTINGS);
		context.put(FacilioConstants.ContextNames.CONTROLLER, ControllerAPI.getController(controller.getId()));
		return false;
	}
	
}
