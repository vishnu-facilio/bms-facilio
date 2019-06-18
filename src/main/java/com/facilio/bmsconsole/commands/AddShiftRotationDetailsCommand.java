package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ShiftRotationApplicableForContext;
import com.facilio.bmsconsole.context.ShiftRotationContext;
import com.facilio.bmsconsole.context.ShiftRotationDetailsContext;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;

public class AddShiftRotationDetailsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		ShiftRotationContext shiftRotation = (ShiftRotationContext) context.get(FacilioConstants.ContextNames.RECORD);
		if (shiftRotation != null) {
			ShiftAPI.addShiftRotationScheduler(shiftRotation);
			List<ShiftRotationApplicableForContext> applicableFor = (List<ShiftRotationApplicableForContext>) context
					.get(FacilioConstants.ContextNames.SHIFT_ROTATION_APPLICABLE_FOR);
			List<ShiftRotationDetailsContext> shiftRotationDetails = (List<ShiftRotationDetailsContext>) context
					.get(FacilioConstants.ContextNames.SHIFT_ROTATION_DETAILS);
			if (applicableFor != null && !applicableFor.isEmpty()) {
				ShiftAPI.addShiftRotationApplicableFor(applicableFor, shiftRotation.getId());
			}
			if (shiftRotationDetails != null && !shiftRotationDetails.isEmpty()) {
				ShiftAPI.addshiftRotationDetails(shiftRotationDetails, shiftRotation.getId());
			}
		}
		return false;
	}

}
