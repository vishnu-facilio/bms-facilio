package com.facilio.bmsconsoleV3.commands;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlScheduleContext;
import com.facilio.control.ControlScheduleExceptionContext;
import com.facilio.control.ControlScheduleSlot;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.builder.mssql.SelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
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
			
			validateScheduleExceptionWithBusinessHour(exception,schedule);
			validateScheduleExceptionWithOtherExceptions(exception,schedule);
			validateScheduleExceptionWithEditedSlots(exception,schedule);
		}
		
		return false;
	}

	private void validateScheduleExceptionWithEditedSlots(ControlScheduleExceptionContext currentException,ControlScheduleContext schedule) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> slotFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_SCHEDULE_UNPLANNED_SLOTS_MODULE_NAME);
		
		Map<String, FacilioField> slotFieldMap = FieldFactory.getAsMap(slotFields);
		
		SelectRecordsBuilder<ControlScheduleSlot> select = new  SelectRecordsBuilder<ControlScheduleSlot>()
				.moduleName(ControlScheduleUtil.CONTROL_SCHEDULE_UNPLANNED_SLOTS_MODULE_NAME)
				.select(slotFields)
				.beanClass(ControlScheduleSlot.class)
				.andCondition(CriteriaAPI.getCondition(slotFieldMap.get("schedule"), schedule.getId()+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(slotFieldMap.get("exception"), currentException.getId()+"", NumberOperators.NOT_EQUALS))
				.andCustomWhere("SYS_CREATED_TIME != SYS_MODIFIED_TIME");
				;
				
		List<ControlScheduleSlot> slots = select.get();
		
		for(ControlScheduleSlot slot : slots) {
			
			ControlScheduleExceptionContext exception = (ControlScheduleExceptionContext) ControlScheduleUtil.fetchRecord(ControlScheduleExceptionContext.class, ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME, null, CriteriaAPI.getIdCondition(slot.getException().getId(), modBean.getModule(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME))).get(0);
			
			long st = DateTimeUtil.getDayStartTimeOf(slot.getStartTime());
			long et = DateTimeUtil.getDayEndTimeOf(slot.getStartTime());
			
			List<DateRange> currentExceptionRanges = ControlScheduleUtil.getExceptionRanges(currentException, st, et);
			
			if(currentExceptionRanges != null && !currentExceptionRanges.isEmpty()) {
				
				for(DateRange currentExceptionRange : currentExceptionRanges) {
					
					if(currentExceptionRange.getStartTime() >= slot.getStartTime() && currentExceptionRange.getStartTime() < slot.getEndTime()) {
						throw new RESTException(ErrorCode.VALIDATION_ERROR,"Schedule is overlaping with a edited schedule - "+exception.getName() +" on " + DateTimeUtil.getFormattedTime(st,"dd-MMM-yyyy"));
					}
					if(currentExceptionRange.getEndTime() > slot.getStartTime() && currentExceptionRange.getEndTime() <= slot.getEndTime()) {
						throw new RESTException(ErrorCode.VALIDATION_ERROR,"Schedule is overlaping with a edited schedule - "+exception.getName() +" on " + DateTimeUtil.getFormattedTime(st,"dd-MMM-yyyy"));
					}
				}
			}
		}
		
	}

	private void validateScheduleExceptionWithOtherExceptions(ControlScheduleExceptionContext currentException,ControlScheduleContext schedule) throws Exception {

		List<ControlScheduleExceptionContext> exceptions = schedule.getExceptions();
		
		long weekStartTime = DateTimeUtil.getWeekStartTimeOf(DateTimeUtil.addDays(DateTimeUtil.getCurrenTime(), 7));
		long weekEndTime = DateTimeUtil.addDays(weekStartTime, 7);
		
		List<DateRange> currentExceptionRanges = ControlScheduleUtil.getExceptionRanges(currentException, weekStartTime, weekEndTime);
		
		for(ControlScheduleExceptionContext exception : exceptions) {
			
			if(currentException.getId() != exception.getId()) {
				List<DateRange> ranges = ControlScheduleUtil.getExceptionRanges(exception, weekStartTime, weekEndTime);
				
				for(DateRange range : ranges) {
					for(DateRange currentExceptionRange : currentExceptionRanges) {
						
						if(currentExceptionRange.getStartTime() >= range.getStartTime() && currentExceptionRange.getStartTime() < range.getEndTime()) {
							throw new RESTException(ErrorCode.VALIDATION_ERROR,"Schedule is overlaping with another schedule");
						}
						if(currentExceptionRange.getEndTime() > range.getStartTime() && currentExceptionRange.getEndTime() <= range.getEndTime()) {
							throw new RESTException(ErrorCode.VALIDATION_ERROR,"Schedule is overlaping with another schedule");
						}
					}
				}
			}
		}
	}

	private void validateScheduleExceptionWithBusinessHour(ControlScheduleExceptionContext exception,ControlScheduleContext schedule) throws Exception {
		
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
					if(stLocal.isBefore(rangeSt) && etLocal.isAfter(rangeEt)) {
						if(!exception.isOffSchedule()) {
							throw new RESTException(ErrorCode.VALIDATION_ERROR,"Extended hour schedule is within the scheduled hours");
						}
					}
				}
			}
			if(exception.isOffSchedule()) {
				if(isStartTimeWithinRange != true ||  isEndTimeWithinRange != true) {
					throw new RESTException(ErrorCode.VALIDATION_ERROR,"Reduced hour schedule is not within the scheduled hours");
				}
				
			}
			else {
				if(isStartTimeWithinRange == true || isEndTimeWithinRange == true) {
					throw new RESTException(ErrorCode.VALIDATION_ERROR,"Extended hour schedule is within the scheduled hours");
				}
			}
		}
		
	}

}
