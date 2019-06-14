package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.ShiftRotationContext;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;

public class GetShiftRotationListCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		List<ShiftRotationContext> shiftRotationList = (List<ShiftRotationContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if (CollectionUtils.isNotEmpty(shiftRotationList)) {
			for(ShiftRotationContext shiftRotation : shiftRotationList) {
				shiftRotation.setApplicableFor(ShiftAPI.getApplicableForShiftRotation(shiftRotation.getId()));
				shiftRotation.setShiftRotations(ShiftAPI.getShiftRotationDetailsForShiftRotation(shiftRotation.getId()));
			}
			context.put(FacilioConstants.ContextNames.RECORD_LIST, shiftRotationList);
		}
		return false;
	} 

}
