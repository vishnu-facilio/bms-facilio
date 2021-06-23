package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ShiftRotationContext;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;

public class GetShiftRotationDetailCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long recordId = (long) context.get(FacilioConstants.ContextNames.ID);
		ShiftRotationContext shiftRotation = ShiftAPI.getShiftRotation(recordId);
		context.put(FacilioConstants.ContextNames.SHIFT_ROTATION, shiftRotation);
//		ShiftRotationContext shiftRotation = (ShiftRotationContext) context.get(FacilioConstants.ContextNames.RECORD);
//		if(shiftRotation!=null) {
//			shiftRotation.setApplicableFor(ShiftAPI.getApplicableForShiftRotation(shiftRotation.getId()));
//			shiftRotation.setShiftRotations(ShiftAPI.getShiftRotationDetailsForShiftRotation(shiftRotation.getId()));
//			context.put(FacilioConstants.ContextNames.SHIFT_ROTATION, shiftRotation);
//		}
		return false;
	}

}
