package com.facilio.bmsconsole.commands;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.facilio.bmsconsoleV3.context.shift.Shift;
import com.facilio.bmsconsoleV3.util.ShiftAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.ShiftUserRelContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.time.DateTimeUtil;

public class GetUsersForShiftCommand extends FacilioCommand {

	private boolean isSameDayShift(Shift shift) {
		LocalTime start = LocalTime.ofSecondOfDay(shift.getStartTime());
		LocalTime end = LocalTime.ofSecondOfDay(shift.getEndTime());
		return start.isBefore(end);
	}

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long shiftId = (long) context.get(FacilioConstants.ContextNames.SHIFT_ID);
		Shift shift = ShiftAPI.getShift(shiftId);

		long currentDay = isSameDayShift(shift) ?
				DateTimeUtil.getDayStartTime(false) :
				DateTimeUtil.getDayStartTime(-1, false);

		long startTime = currentDay;
		long endTime = currentDay;
		long userID = -1;
		long shiftID = shift.getId();
		boolean alignDate = false;

//		List<ShiftUserRelContext> mapping = ShiftAPI.getShiftUserMapping(startTime, endTime, userID, shiftID, alignDate);

//		List<Long> users = new ArrayList<>();
//		if (CollectionUtils.isNotEmpty(mapping)) {
//			for (ShiftUserRelContext shiftUserRelContext : mapping) {
//				users.add(shiftUserRelContext.getOuid());
//			}
//		}

//		context.put(FacilioConstants.ContextNames.USERS, users);
		context.put(FacilioConstants.ContextNames.SHIFT, shift);
		context.put(FacilioConstants.ContextNames.DATE, currentDay);
		return false;
	}

}
