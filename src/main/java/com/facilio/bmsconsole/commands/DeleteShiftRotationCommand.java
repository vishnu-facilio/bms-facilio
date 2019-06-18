package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteShiftRotationCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(recordIds != null && !recordIds.isEmpty()) {
			for(Long id: recordIds) {
				ShiftAPI.deleteShiftRotationScheduler(id);
				ShiftAPI.deleteShiftRotationApplicableFor(id);
				ShiftAPI.deleteShiftRotationDetails(id);
			}
		}
		return false;
	}

}
