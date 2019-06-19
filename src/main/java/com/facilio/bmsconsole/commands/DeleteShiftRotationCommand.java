package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteShiftRotationCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		long id = (long) context.get(FacilioConstants.ContextNames.ID);
		ShiftAPI.deleteShiftRotation(id);
		return false;
	}

}
