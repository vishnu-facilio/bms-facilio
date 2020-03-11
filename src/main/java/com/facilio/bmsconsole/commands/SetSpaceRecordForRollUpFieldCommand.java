package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.constants.FacilioConstants;

public class SetSpaceRecordForRollUpFieldCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		context.put(FacilioConstants.ContextNames.RECORD,(SpaceContext) context.get(FacilioConstants.ContextNames.SPACE));
		return false;
	}

}
