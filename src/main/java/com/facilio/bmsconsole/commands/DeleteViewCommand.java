package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteViewCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long viewId =  (Long) context.get(FacilioConstants.ContextNames.VIEWID);
		ViewAPI.deleteView(viewId);
		return false;
	}

}
