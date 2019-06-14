package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ShiftRotationContext;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;

public class GetShiftRotationDetailCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		ShiftRotationContext shiftRotation = (ShiftRotationContext) context.get(FacilioConstants.ContextNames.RECORD);
		if(shiftRotation!=null) {
			shiftRotation.setApplicableFor(ShiftAPI.getApplicableForShiftRotation(shiftRotation.getId()));
			shiftRotation.setShiftRotations(ShiftAPI.getShiftRotationDetailsForShiftRotation(shiftRotation.getId()));
			context.put(FacilioConstants.ContextNames.SHIFT_ROTATION, shiftRotation);
		}
		return false;
	}

}
