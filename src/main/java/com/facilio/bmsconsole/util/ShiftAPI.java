package com.facilio.bmsconsole.util;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringJoiner;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.AddShiftCommand;
import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.ScheduleInfo.FrequencyType;

public class ShiftAPI {
	public static List<ShiftContext> getAllShifts() throws Exception {
		List<ShiftContext> shifts = new ArrayList<>();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getShiftField())
				.table(ModuleFactory.getShiftModule().getTableName())
				.andCustomWhere(ModuleFactory.getShiftModule().getTableName() + ".ORGID=?", AccountUtil.getCurrentOrg().getOrgId())
				.orderBy("name");
		List<Map<String, Object>> props = selectBuilder.get();
		StringJoiner j = new StringJoiner(",");
		if (props != null && !props.isEmpty()) {
			ShiftContext s = FieldUtil.getAsBeanFromMap(props.get(0), ShiftContext.class);
			j.add(String.valueOf(s.getBusinessHoursId()));
			shifts.add(s);
		}
		
		String businessHoursTable = ModuleFactory.getBusinessHoursModule().getTableName();
		String singleDayTable = ModuleFactory.getSingleDayBusinessHourModule().getTableName();
		selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getSingleDayBusinessHoursFields())
				.table(businessHoursTable)
				.innerJoin(singleDayTable)
				.on(businessHoursTable+".ID = "+singleDayTable+".PARENT_ID")
				.andCustomWhere(businessHoursTable+".ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCondition(CriteriaAPI.getCondition("PARENT_ID","parentId", j.toString(), NumberOperators.EQUALS))
				.orderBy("dayOfWeek");
		
		props = selectBuilder.get();
		
		List<BusinessHourContext> days = new ArrayList<>();
		Map<Long, List<BusinessHourContext>> parentIdVsContext = new HashMap<>();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop: props) {
				BusinessHourContext b = FieldUtil.getAsBeanFromMap(prop, BusinessHourContext.class);
				long id = b.getParentId();
				if (!parentIdVsContext.containsKey(id)) {
					parentIdVsContext.put(id, new ArrayList<>());
				}
				parentIdVsContext.get(id).add(b);
			}
		}
		
		shifts.forEach(s -> {
			List<BusinessHourContext> b = parentIdVsContext.get(s.getBusinessHoursId());
			s.setDays(b);
		});
		return shifts;
	}
	
	public static void deleteJobsForshift(long shiftId) throws Exception {
		FacilioTimer.deleteJob(shiftId);
	}

	public static void scheduleJobs(long shiftId, List<BusinessHourContext> days) throws Exception {
		deleteJobsForshift(shiftId);
		for (BusinessHourContext d: days) {
			LocalTime startTime = d.getStartTimeAsLocalTime();
			LocalTime endTime = d.getEndTimeAsLocalTime();
			
			ScheduleInfo startShiftschedule = new ScheduleInfo();
			startShiftschedule.addValue(d.getDayOfWeek());
			startShiftschedule.addTime(d.getStartTime());
			startShiftschedule.setFrequencyType(FrequencyType.WEEKLY);
			FacilioTimer.scheduleCalendarJob(shiftId, getJobName(d.getDayOfWeekEnum(), true), ShiftAPI.getShiftStartScheduleExecutionTime(d.getDayOfWeekEnum(), startTime, endTime), startShiftschedule, "priority");
			
			ScheduleInfo endShiftschedule = new ScheduleInfo();
			endShiftschedule.addValue(d.getDayOfWeek());
			endShiftschedule.addTime(d.getStartTime());
			endShiftschedule.setFrequencyType(FrequencyType.WEEKLY);
			FacilioTimer.scheduleCalendarJob(shiftId, getJobName(d.getDayOfWeekEnum(), false), getShiftEndScheduleExecutionTime(d.getDayOfWeekEnum(), startTime, endTime), endShiftschedule, "priority");			
		}
	}

	public static long getShiftStartScheduleExecutionTime(DayOfWeek day, LocalTime startTime, LocalTime endTime) {
		ZonedDateTime now = DateTimeUtil.getDateTime();
		int dCmp = now.getDayOfWeek().compareTo(day);
		if (dCmp == 0) {
			int cmp = now.toLocalTime().compareTo(startTime);
			if (cmp > 0) {
				return nextWeek(now, day, startTime);
			} else {
				return currentWeek(now, day, startTime);
			}
		} else if (dCmp < 0) {
			return currentWeek(now, day, startTime);
		} 
		return nextWeek(now, day, startTime); 
	}
	
	private static long currentWeek(ZonedDateTime now, DayOfWeek day, LocalTime startTime) {
		ZonedDateTime adjusted = now.with(TemporalAdjusters.nextOrSame(day));
		return ZonedDateTime.of(adjusted.toLocalDate(), startTime, adjusted.getZone()).toInstant().toEpochMilli();
	}

	private static long nextWeek(ZonedDateTime now, DayOfWeek day, LocalTime startTime) {
		ZonedDateTime adjusted = now.with(TemporalAdjusters.next(day));
		return ZonedDateTime.of(adjusted.toLocalDate(), startTime, adjusted.getZone()).toInstant().toEpochMilli();
	}

	private static long getShiftEndScheduleExecutionTime(DayOfWeek day, LocalTime startTime, LocalTime endTime) {
		ZonedDateTime now = DateTimeUtil.getDateTime();
		int dCmp = now.getDayOfWeek().compareTo(day);
		if (dCmp == 0) {
			int cmp = now.toLocalTime().compareTo(startTime);
			if (cmp > 0) {
				return nextWeek(now, day, endTime);
			} else {
				return currentWeek(now, day, endTime);
			}
		} else if (dCmp < 0) {
			return currentWeek(now, day, endTime);
		} 
		return nextWeek(now, day, endTime);
	}
	
	private static String getJobName(DayOfWeek day, boolean isStartTime) {
		return day.getDisplayName(TextStyle.FULL, Locale.US) + (isStartTime ? "_SHIFT_START" : "_SHIFT_END");		
	}
}
