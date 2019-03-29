package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class GetBaseLinesForSpaceCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long spaceId = (long) context.get(FacilioConstants.ContextNames.SPACE_ID);
		context.put(FacilioConstants.ContextNames.BASE_LINE_LIST, BaseLineAPI.getBaseLinesOfSpace(spaceId));
		return false;
	}

}
