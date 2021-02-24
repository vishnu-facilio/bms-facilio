package com.facilio.bmsconsoleV3.commands;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlScheduleContext;
import com.facilio.control.ControlScheduleExceptionContext;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;

public class ControlScheduleExceptionValidateCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = (String) context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME,ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME);
		
		ControlScheduleExceptionContext exception = (ControlScheduleExceptionContext) ControlScheduleUtil.getObjectFromRecordMap(context, moduleName);
		
		if(exception.getSchedule() != null) {
			
			String scheduleModuleName = ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME;
			if(moduleName.equals(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME)) {
				scheduleModuleName = ControlScheduleUtil.CONTROL_SCHEDULE_TENANT_SHARING_MODULE_NAME;
			}
			
			ControlScheduleContext schedule = ControlScheduleUtil.getControlSchedule(exception.getSchedule().getId(),scheduleModuleName);
			
			validateScheduleException(exception,schedule);
		}
		
		return false;
	}

	private void validateScheduleException(ControlScheduleExceptionContext exception,ControlScheduleContext schedule) throws Exception {
		
		long weekStartTime = DateTimeUtil.getWeekStartTimeOf(DateTimeUtil.addDays(DateTimeUtil.getCurrenTime(), 7));
		long weekEndTime = DateTimeUtil.addDays(weekStartTime, 7);
		
		List<DateRange> ranges = ControlScheduleUtil.getExceptionRanges(exception, weekStartTime, weekEndTime);
		
		for(DateRange range :ranges) {
			
			boolean isStartTimeWithinRange = false;
			boolean isEndTimeWithinRange = false;
			
			Long startTime = range.getStartTime();
			Long endTime = range.getEndTime();
			
			ZonedDateTime startDateTime = DateTimeUtil.getZonedDateTime(startTime);
			ZonedDateTime endDateTime = DateTimeUtil.getZonedDateTime(endTime);
			
			LocalTime stLocal = startDateTime.toLocalTime();
			LocalTime etLocal = endDateTime.toLocalTime();
			
			DayOfWeek dayofWeek = startDateTime.getDayOfWeek();
			
			Map<DayOfWeek, List<Pair<LocalTime, LocalTime>>> map = schedule.getBusinessHoursContext().getAsMapBusinessHours();
			
			List<Pair<LocalTime, LocalTime>> availableTimes = map.get(dayofWeek);
			
			if(availableTimes != null) {
				for(Pair<LocalTime, LocalTime> availableTime : availableTimes) {
					
					LocalTime rangeSt = availableTime.getKey();
					LocalTime rangeEt = availableTime.getValue();
					if(stLocal.equals(rangeSt) || ( stLocal.isAfter(rangeSt) && stLocal.isBefore(rangeEt))) {
						isStartTimeWithinRange = true;
					}
					if(etLocal.equals(rangeEt) || ( etLocal.isAfter(rangeSt) && etLocal.isBefore(rangeEt))) {
						isEndTimeWithinRange = true;
					}
				}
			}
			if(exception.isOffSchedule()) {
				if(isStartTimeWithinRange != true ||  isEndTimeWithinRange != true) {
					throw new RESTException(ErrorCode.VALIDATION_ERROR,"Schedule exception is not within range");
				}
				
			}
			else {
				if(isStartTimeWithinRange == true || isEndTimeWithinRange == true) {
					throw new RESTException(ErrorCode.VALIDATION_ERROR,"Schedule exception is not within range");
				}
			}
		}
		
	}

}
