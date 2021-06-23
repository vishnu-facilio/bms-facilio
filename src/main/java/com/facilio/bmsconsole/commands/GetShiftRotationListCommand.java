package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

public class GetShiftRotationListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
//		List<ShiftRotationContext> shiftRotationList = (List<ShiftRotationContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
//		if (CollectionUtils.isNotEmpty(shiftRotationList)) {
//			for(ShiftRotationContext shiftRotation : shiftRotationList) {
//				shiftRotation.setApplicableFor(ShiftAPI.getApplicableForShiftRotation(shiftRotation.getId()));
//				shiftRotation.setShiftRotations(ShiftAPI.getShiftRotationDetailsForShiftRotation(shiftRotation.getId()));
//			}
//			context.put(FacilioConstants.ContextNames.RECORD_LIST, shiftRotationList);
//		}
		return false;
	} 

}
