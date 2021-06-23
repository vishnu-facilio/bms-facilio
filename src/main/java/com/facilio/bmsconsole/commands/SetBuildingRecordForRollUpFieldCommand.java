package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.constants.FacilioConstants;

public class SetBuildingRecordForRollUpFieldCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		context.put(FacilioConstants.ContextNames.RECORD,(BuildingContext) context.get(FacilioConstants.ContextNames.BUILDING));	
		return false;
	}

}
