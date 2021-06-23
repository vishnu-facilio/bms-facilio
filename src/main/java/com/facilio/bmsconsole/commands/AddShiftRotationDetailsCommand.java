package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

public class AddShiftRotationDetailsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
//		ShiftRotationContext shiftRotation = (ShiftRotationContext) context.get(FacilioConstants.ContextNames.RECORD);
//		if (shiftRotation != null) {
//			ShiftAPI.addShiftRotationScheduler(shiftRotation);
//			List<ShiftRotationApplicableForContext> applicableFor = (List<ShiftRotationApplicableForContext>) context
//					.get(FacilioConstants.ContextNames.SHIFT_ROTATION_APPLICABLE_FOR);
//			List<ShiftRotationDetailsContext> shiftRotationDetails = (List<ShiftRotationDetailsContext>) context
//					.get(FacilioConstants.ContextNames.SHIFT_ROTATION_DETAILS);
//			if (applicableFor != null && !applicableFor.isEmpty()) {
//				ShiftAPI.addShiftRotationApplicableFor(applicableFor, shiftRotation.getId());
//			}
//			if (shiftRotationDetails != null && !shiftRotationDetails.isEmpty()) {
//				ShiftAPI.addshiftRotationDetails(shiftRotationDetails, shiftRotation.getId());
//			}
//		}
		return false;
	}

}
