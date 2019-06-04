package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.context.ShiftUserRelContext;
import com.facilio.bmsconsole.util.ShiftAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;

public class GetUsersForShiftCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		long shiftId = (long) context.get(FacilioConstants.ContextNames.SHIFT_ID);
		ShiftContext shift = ShiftAPI.getShift(shiftId);
		
		boolean sameDayShift = shift.getStartTimeAsLocalTime().isBefore(shift.getEndTimeAsLocalTime());
		long currentDay;
		if (sameDayShift) {
			currentDay = DateTimeUtil.getDayStartTime(true);
		} else {
			currentDay = DateTimeUtil.getDayStartTime(-1, true);
		}
		
		List<ShiftUserRelContext> shiftUserMapping = ShiftAPI.getShiftUserMapping(currentDay, currentDay, shift.getId());
		
		List<Long> users = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(shiftUserMapping)) {
			for (ShiftUserRelContext shiftUserRelContext : shiftUserMapping) {
				users.add(shiftUserRelContext.getOuid());
			}
		}
		
		context.put(FacilioConstants.ContextNames.USERS, users);
		context.put(FacilioConstants.ContextNames.SHIFT, shift);
		context.put(FacilioConstants.ContextNames.DATE, currentDay);
		return false;
	}

}
