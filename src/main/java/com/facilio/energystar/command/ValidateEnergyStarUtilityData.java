package com.facilio.energystar.command;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;

public class ValidateEnergyStarUtilityData extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		// time should not overlap
		// atleast one valid point must be present
		
		return false;
	}

}
