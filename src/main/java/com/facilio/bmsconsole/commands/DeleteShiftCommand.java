package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteShiftCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long id = (long) context.get(FacilioConstants.ContextNames.ID);
		boolean doValidation = (boolean) context.get(FacilioConstants.ContextNames.DO_VALIDTION);
		
		if (doValidation) {
			List<String> names = ShiftAPI.getAssociatedUserNames(id);
			if (names != null && !names.isEmpty()) {
				context.put(FacilioConstants.ContextNames.USERS, names);
				return true;
			}
		}
		
		ShiftAPI.deleteShift(id);
		
		return false;
	}

}
