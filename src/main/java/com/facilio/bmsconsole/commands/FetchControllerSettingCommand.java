package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.constants.FacilioConstants;

public class FetchControllerSettingCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		ControllerContext controller = (ControllerContext) context.get(FacilioConstants.ContextNames.CONTROLLER_SETTINGS);
		context.put(FacilioConstants.ContextNames.CONTROLLER, ControllerAPI.getController(controller.getId()));
		return false;
	}
	
}
