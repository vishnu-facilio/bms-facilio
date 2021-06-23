package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.constants.FacilioConstants;

public class SetFloorRecordForRollUpFieldCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		context.put(FacilioConstants.ContextNames.RECORD,(FloorContext) context.get(FacilioConstants.ContextNames.FLOOR));
		return false;
	}

}
