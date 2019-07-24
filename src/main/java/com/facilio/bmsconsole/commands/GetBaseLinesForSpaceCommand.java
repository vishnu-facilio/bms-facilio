package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.constants.FacilioConstants;

public class GetBaseLinesForSpaceCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		long spaceId = (long) context.get(FacilioConstants.ContextNames.SPACE_ID);
		context.put(FacilioConstants.ContextNames.BASE_LINE_LIST, BaseLineAPI.getBaseLinesOfSpace(spaceId));
		return false;
	}

}
