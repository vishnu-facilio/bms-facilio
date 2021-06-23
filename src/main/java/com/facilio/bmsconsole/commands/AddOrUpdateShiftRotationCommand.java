package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ShiftRotationContext;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;

public class AddOrUpdateShiftRotationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		ShiftRotationContext shiftRotation = (ShiftRotationContext) context.get(FacilioConstants.ContextNames.SHIFT_ROTATION);
		if(shiftRotation!=null) {
			if (shiftRotation.getId() != -1) {
				ShiftAPI.updateShiftRotation(shiftRotation);
			}
			else {
				ShiftAPI.addShiftRotation(shiftRotation);
			}
		}
		return false;
	}

}
