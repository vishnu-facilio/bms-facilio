package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.constants.FacilioConstants;

public class SetSpaceRecordForRollUpFieldCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		context.put(FacilioConstants.ContextNames.RECORD,(SpaceContext) context.get(FacilioConstants.ContextNames.SPACE));
		return false;
	}

}
