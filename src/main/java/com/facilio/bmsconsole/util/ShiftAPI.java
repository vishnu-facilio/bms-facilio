package com.facilio.bmsconsole.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.BusinessHoursList;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.EnumField;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.ScheduleInfo.FrequencyType;
import com.facilio.tasker.job.JobContext;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

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
			for (Map<String, Object> p : props) {
				ShiftContext s = FieldUtil.getAsBeanFromMap(p, ShiftContext.class);
				j.add(String.valueOf(s.getBusinessHoursId()));
				shifts.add(s);
			}
		}
		
		if (shifts.isEmpty()) {
			return shifts;
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
	
	public static void deleteJobsForshifts(List<Long> ids) throws Exception {
		FacilioTimer.deleteJobs(ids, "StartShift");
		FacilioTimer.deleteJobs(ids, "EndShift");
	}

	public static void scheduleJobs(List<BusinessHourContext> days) throws Exception {
		for (BusinessHourContext d: days) {
			LocalTime startTime = d.getStartTimeAsLocalTime();
			LocalTime endTime = d.getEndTimeAsLocalTime();
			
			ScheduleInfo startShiftschedule = new ScheduleInfo();
			startShiftschedule.addValue(d.getDayOfWeek());
			startShiftschedule.addTime(d.getStartTime());
			startShiftschedule.setFrequencyType(FrequencyType.WEEKLY);
			FacilioTimer.scheduleCalendarJob(d.getId(), "StartShift", ShiftAPI.getShiftStartScheduleExecutionTime(d.getDayOfWeekEnum(), startTime, endTime), startShiftschedule, "facilio");
			
			ScheduleInfo endShiftschedule = new ScheduleInfo();
			endShiftschedule.addValue(d.getDayOfWeek());
			endShiftschedule.addTime(d.getEndTime());
			endShiftschedule.setFrequencyType(FrequencyType.WEEKLY);
			FacilioTimer.scheduleCalendarJob(d.getId(), "EndShift", getShiftEndScheduleExecutionTime(d.getDayOfWeekEnum(), startTime, endTime), endShiftschedule, "facilio");			
		}
	}
	
	public static void scheduleOneTimeJobs(List<JobContext> jcs, JSONObject obj) throws Exception {
		for (JobContext jc: jcs) {
			BmsJobUtil.scheduleOneTimeJobWithProps(jc.getJobId(), "EndShiftOTJ", jc.getExecutionTime(), "facilio", obj);
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
	
	private static long currentWeek(ZonedDateTime now, DayOfWeek day, LocalTime time) {
		ZonedDateTime adjusted = now.with(TemporalAdjusters.nextOrSame(day));
		return ZonedDateTime.of(adjusted.toLocalDate(), time, adjusted.getZone()).toInstant().toEpochMilli();
	}

	private static long nextWeek(ZonedDateTime now, DayOfWeek day, LocalTime time) {
		ZonedDateTime adjusted = now.with(TemporalAdjusters.next(day));
		return ZonedDateTime.of(adjusted.toLocalDate(), time, adjusted.getZone()).toInstant().toEpochMilli();
	}

	private static long getShiftEndScheduleExecutionTime(DayOfWeek day, LocalTime startTime, LocalTime endTime) {
		ZonedDateTime now = DateTimeUtil.getDateTime();
		int dCmp = now.getDayOfWeek().compareTo(day);
		if (endTime.compareTo(startTime) < 0) {
			day = day.plus(1);
		}
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
	
	public static List<ReadingContext> getUserWorkHoursReading(List<Long> userIds, String eventType, long executionTime) throws Exception {
		List<ReadingDataMeta> metaList = getUserWorkHoursRDM(userIds);
		
		if (metaList == null || metaList.isEmpty()) {
			return null;
		}
		
		Map<String, ReadingDataMeta> readingDataMeta = new HashMap<> ();
		for(ReadingDataMeta meta : metaList) {
			long resourceId = meta.getResourceId();
			String fieldName = meta.getField().getName();
			readingDataMeta.put(resourceId+"_"+fieldName, meta);
		}
		List<ReadingContext> rContexts = new ArrayList<>();
		for (long userId : userIds) {
			ReadingDataMeta meta = readingDataMeta.get(userId+"_"+"workHoursEntry");
			ReadingDataMeta woIdMeta = readingDataMeta.get(userId+"_"+"woId");
			if (meta == null || meta.getValue() == null) {
				continue;
			}
			switch (meta.getValue().toString()) {
			case "1":
				if (eventType == "shiftend") {
					ReadingContext r = new ReadingContext();
					r.addReading("workHoursEntry", "Shift-Pause");
					r.addReading("woId", woIdMeta.getValue());
					r.setParentId(userId);
					r.setTtime(executionTime);
					rContexts.add(r);
				}
				break;
			case "3":
				if (eventType == "shiftend") {
					ReadingContext r = new ReadingContext();
					r.addReading("workHoursEntry", "Shift-Pause");
					r.addReading("woId", woIdMeta.getValue());
					r.setParentId(userId);
					r.setTtime(executionTime);
					rContexts.add(r);
				}
				break;
			case "4":
				if (eventType == "shiftstart") {
					ReadingContext r = new ReadingContext();
					r.addReading("workHoursEntry", "Shift-Resume");
					r.addReading("woId", woIdMeta.getValue());
					r.setParentId(userId);
					r.setTtime(executionTime);
					rContexts.add(r);
				}
			case "5":
				if (eventType == "shiftend") {
					ReadingContext r = new ReadingContext();
					r.addReading("workHoursEntry", "Shift-Pause");
					r.addReading("woId", woIdMeta.getValue());
					r.setParentId(userId);
					r.setTtime(executionTime);
					rContexts.add(r);
				}
				break;
			}
		}
		return rContexts;
	}

	public static List<ReadingDataMeta> getUserWorkHoursRDM(List<Long> userIds)
			throws InstantiationException, IllegalAccessException, Exception {
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> allFields= bean.getAllFields("userworkhoursreading");
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
		List<Pair<Long, FacilioField>> rdmPairs = new ArrayList<>();
		
		for (long parentId: userIds) {
			for (String fieldName : Arrays.asList("workHoursEntry", "woId")) {
				FacilioField field = fieldMap.get(fieldName);
				if (field != null) {
					Pair<Long, FacilioField> pair = Pair.of(parentId, field);
					rdmPairs.add(pair);
				}
			}
		}
		
		List<ReadingDataMeta> metaList = ReadingsAPI.getReadingDataMetaList(rdmPairs);
		return metaList;
	} 

	public static List<ReadingContext> getUserShiftReading(List<Long> userIds, String entry, long executionTime) throws Exception {
		return userIds.stream().map(id -> {
			ReadingContext rContext = new ReadingContext();
			rContext.setParentId(id);
			rContext.addReading("shiftEntry", entry);
			rContext.setTtime(executionTime);
			return rContext;
		}).collect(Collectors.toList());
	}
	
	public static boolean isUserWorking(long userId) throws Exception {
		List<ReadingDataMeta> metaList = getUserWorkHoursRDM(Arrays.asList(userId));
		if (metaList == null || metaList.isEmpty()) {
			return false;
		}
	
		ReadingDataMeta meta = null;
		ReadingDataMeta woIdMeta = null;
		for(ReadingDataMeta m : metaList) {
			String fieldName = m.getField().getName();
			if (fieldName.equals("workHoursEntry")) {
				meta = m;
			} else if (fieldName.equals("woId")) {
				woIdMeta = m;
			}
		}
		
		if (meta == null || woIdMeta == null) {
			throw new IllegalStateException();
		}
		
		Integer value = (Integer) meta.getValue();
		if (value == null) {
			return false;
		}
		
		return value == 1 || value == 3 || value == 5;
	}
	
	public static void pauseWorkOrderForUser(long userId, EventType activityType, long currentWOId, long now) throws Exception {
		List<ReadingDataMeta> metaList = getUserWorkHoursRDM(Arrays.asList(userId));
		if (metaList == null || metaList.isEmpty()) {
			return;
		}
	
		ReadingDataMeta meta = null;
		ReadingDataMeta woIdMeta = null;
		for(ReadingDataMeta m : metaList) {
			String fieldName = m.getField().getName();
			if (fieldName.equals("workHoursEntry")) {
				meta = m;
			} else if (fieldName.equals("woId")) {
				woIdMeta = m;
			}
		}
		
		if (meta == null || woIdMeta == null) {
			throw new IllegalStateException();
		}
		
		Integer value = (Integer) meta.getValue();
		if (value == null) {
			return;
		}
		
		long woId = (long) woIdMeta.getValue();
		
		if ((value == 1 || value == 3 || value == 5) && woId != currentWOId) {
			addUserWorkHoursReading(userId, woId, activityType, "Pause", now);
		}
	}
	
	public static List<ReadingContext> addUserWorkHoursReading(long assignedToUserId, long workOrderId, EventType activityType, String reading, long time) throws Exception {
		return addUserWorkHoursReading(assignedToUserId, workOrderId, activityType, reading, time, false, false);
	}
	
	public static List<ReadingContext> addInvalidatedUserWorkHoursReading(long assignedToUserId, long workOrderId, EventType activityType, String reading, long time) throws Exception {
		return addUserWorkHoursReading(assignedToUserId, workOrderId, activityType, reading, time, false, true);
	}
	
	public static List<ReadingContext> addUserWorkHoursReading(long assignedToUserId, long workOrderId, EventType activityType, String reading, long time, boolean skipCheck) throws Exception {
		return addUserWorkHoursReading(assignedToUserId, workOrderId, activityType, reading, time, skipCheck, false);
	}
	
	public static List<ReadingContext> addUserWorkHoursReading(long assignedToUserId, long workOrderId, EventType activityType, String reading, long time, boolean skipCheck, boolean hasManualEntry) throws Exception {
		if (!skipCheck) {
			boolean addReading = allowAddReading(assignedToUserId, workOrderId, reading);
			if (!addReading) {
				return new ArrayList<>();
			}
		}
		
		ReadingContext rContext = new ReadingContext();
		rContext.setParentId(assignedToUserId);
		rContext.addReading("woId", workOrderId);
		rContext.addReading("workHoursEntry", reading);
		if (activityType != null) {
			rContext.addReading("sourceActivity", activityType.getValue());
		}
		rContext.addReading("hasManualEntry", hasManualEntry);
		rContext.setTtime(time);
		
		return Arrays.asList(rContext);
	}

	private static boolean allowAddReading(long assignedToUserId, long workOrderId, String currentReading)
			throws InstantiationException, IllegalAccessException, Exception {
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = bean.getModule("userworkhoursreading");
		List<FacilioField> fields = bean.getAllFields("userworkhoursreading");
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), module))
				.andCustomWhere("User_Workhour_Readings.WO_ID = ? AND User_Workhour_Readings.PARENT_ID = ?", workOrderId, assignedToUserId)
				.orderBy("User_Workhour_Readings.TTIME DESC")
				.limit(1);
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		Optional<FacilioField> whEntry = fields.stream().filter(e -> e.getName().equals("workHoursEntry")).findFirst();
		int currentValue = -1;
		if (whEntry.isPresent()) {
			currentValue = ((EnumField) whEntry.get()).getIndex(currentReading);
		} else {
			throw new IllegalStateException();
		}
		
		Set<Integer> starts = new HashSet<>(Arrays.asList(1, 3, 5));
		Set<Integer> halts = new HashSet<>(Arrays.asList(2, 4, 6));
		
		if (props == null || props.isEmpty()) {
			if (starts.contains(currentValue)) {
				return true;
			}
			return false;
		}
				
		Integer previousValue = (Integer) props.get(0).get("workHoursEntry");
		if (previousValue == null) {
			return true;
		}
		
		if ((starts.contains(currentValue) && halts.contains(previousValue)) 
				|| (starts.contains(previousValue) && halts.contains(currentValue))) {
			return true;
		}
		
		return false;
	}
	
	public static List<Long> getOuidFromShift(long shiftId) throws Exception {
		FacilioField ouid = new FacilioField();
		ouid.setName("ouid");
		ouid.setDataType(FieldType.NUMBER);
		ouid.setColumnName("ORG_USERID");
		ouid.setModule(AccountConstants.getOrgUserModule());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(Arrays.asList(ouid))
				.table("ORG_Users")
				.innerJoin("Shift_User_Rel")
				.on("ORG_Users.ORG_USERID = Shift_User_Rel.ORG_USERID")
				.andCondition(CriteriaAPI.getCondition("Shift_User_Rel.SHIFTID", "shiftId", String.valueOf(shiftId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getId()), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		return props.stream().map(x -> {return (long) x.get("ouid");}).collect(Collectors.toList());
	}
	
	public static List<Long> getOuids(long singleDayBusinessId) throws Exception {
		FacilioField ouid = new FacilioField();
		ouid.setName("ouid");
		ouid.setDataType(FieldType.NUMBER);
		ouid.setColumnName("ORG_USERID");
		ouid.setModule(AccountConstants.getOrgUserModule());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(Arrays.asList(ouid))
				.table("ORG_Users")
				.innerJoin("Shift_User_Rel")
				.on("ORG_Users.ORG_USERID = Shift_User_Rel.ORG_USERID")
				.innerJoin("Shift")
				.on("Shift_User_Rel.SHIFTID = Shift.ID")
				.innerJoin("BusinessHours")
				.on("Shift.BUSINESSHOURSID = BusinessHours.ID")
				.innerJoin("SingleDayBusinessHours")
				.on("SingleDayBusinessHours.PARENT_ID = BusinessHours.ID")
				.andCondition(CriteriaAPI.getCondition("SingleDayBusinessHours.ID", "childId", String.valueOf(singleDayBusinessId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("ORG_Users.ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getId()), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		return props.stream().map(x -> {return (long) x.get("ouid");}).collect(Collectors.toList());
	}
	
	public static List<String> getAssociatedUserNames(long shiftId) throws Exception {
		FacilioField name = new FacilioField();
		name.setName("name");
		name.setDataType(FieldType.STRING);
		name.setColumnName("NAME");
		name.setModule(AccountConstants.getUserModule());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(Arrays.asList(name))
				.table("Users")
				.innerJoin("ORG_Users")
				.on("Users.USERID = ORG_USERS.USERID")
				.innerJoin("Shift_User_Rel")
				.on("ORG_Users.ORG_USERID = Shift_User_Rel.ORG_USERID")
				.innerJoin("Shift")
				.on("Shift_User_Rel.SHIFTID = Shift.ID")
				.andCondition(CriteriaAPI.getCondition("Shift.ID", "shiftId", String.valueOf(shiftId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("Shift.ORGID", "orgId", String.valueOf(AccountUtil.getCurrentOrg().getId()), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		return props.stream().map(x -> {return StringEscapeUtils.escapeHtml4((String) x.get("name"));}).collect(Collectors.toList());
	}

	public static void deleteShift(long id) throws Exception {
		ShiftContext shift = null;
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getShiftField())
				.table(ModuleFactory.getShiftModule().getTableName())
				.andCustomWhere(ModuleFactory.getShiftModule().getTableName() + ".ORGID=? AND "+ ModuleFactory.getShiftModule().getTableName()+".ID=?", AccountUtil.getCurrentOrg().getOrgId(), id);
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			shift = FieldUtil.getAsBeanFromMap(props.get(0), ShiftContext.class);
		}
		
		if (shift == null) {
			return;
		}
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getShiftModule().getName())
				.andCustomWhere(ModuleFactory.getShiftModule().getTableName() + ".ORGID=? AND "+ ModuleFactory.getShiftModule().getTableName()+".ID=?", AccountUtil.getCurrentOrg().getOrgId(), id);
		deleteBuilder.delete();
		
		BusinessHoursList businessHoursList = BusinessHoursAPI.getBusinessHours(shift.getBusinessHoursId());
		
		scheduleOneTimeJob(shift, businessHoursList);
		
		deleteJobsForshifts(businessHoursList.stream().map(BusinessHourContext::getId).collect(Collectors.toList()));
		
		BusinessHoursAPI.deleteBusinessHours(shift.getBusinessHoursId());
	}

	public static void scheduleOneTimeJob(ShiftContext shift, BusinessHoursList businessHoursList)
			throws JsonParseException, JsonMappingException, SQLException, IOException, ParseException, Exception {
		List<Long> bisHrIds = businessHoursList.stream().map(e -> {
			LocalTime startTime = e.getStartTimeAsLocalTime();
			LocalTime endTime = e.getEndTimeAsLocalTime();
			
			DayOfWeek day = e.getDayOfWeekEnum();
			ZonedDateTime now = DateTimeUtil.getDateTime();
			if (day == now.getDayOfWeek()) {
				if (startTime.compareTo(endTime) <= 0) {
					if (now.toLocalTime().compareTo(startTime) > 0 
							&& now.toLocalTime().compareTo(endTime) <= 0) {
						return e.getId();
					} 
				} else {
					if (now.toLocalTime().compareTo(startTime) > 0) {
						return e.getId();
					} 
				}
			} else if (day.plus(1) == now.getDayOfWeek()) {
				if (startTime.compareTo(endTime) > 0 
						&& now.toLocalTime().compareTo(endTime) <= 0) {
					return e.getId();
				}
			}
			return -1l;
		}).filter(e -> e != null && e != -1l).collect(Collectors.toList());
		
		if (bisHrIds == null || bisHrIds.isEmpty()) {
			return;
		}
		List<JobContext> jcs = FacilioTimer.getJobs(bisHrIds, "EndShift");
		JSONObject obj = new JSONObject();
		obj.put("shiftId", shift.getId());
		scheduleOneTimeJobs(jcs, obj);
	}
	
	private static final BigDecimal SIXTY = new BigDecimal("60");
	private static final BigDecimal THOUSAND = new BigDecimal("1000");
	
	public static BigDecimal getUserShiftHours(long userId, long startTime, long endTime) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule shrModule = modBean.getModule(FacilioConstants.ContextNames.USER_SHIFT_READING);
		
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder();
		selectRecordBuilder.select(modBean.getAllFields(FacilioConstants.ContextNames.USER_SHIFT_READING));
		selectRecordBuilder.table(shrModule.getTableName());
		selectRecordBuilder.andCustomWhere("TTIME between ? and ?", startTime, endTime);
		selectRecordBuilder.andCustomWhere("ORGID = ? AND PARENT_ID = ?", AccountUtil.getCurrentOrg().getOrgId(), userId)
		.orderBy("TTIME ASC");
		
		List<Map<String, Object>> props = selectRecordBuilder.get();
		if (props == null || props.isEmpty()) {
			return BigDecimal.ZERO;
		}
		long result = 0;
		
		ArrayList<Long> readings = new ArrayList<>();
		
		if ((Integer) props.get(0).get("shiftEntry") == 2) {
			readings.add(startTime);
		}
		
		readings.addAll(props.stream().map(e -> (Long) e.get("ttime")).collect(Collectors.toList()));
		
		if ((Integer) props.get(props.size() - 1).get("shiftEntry") == 1) {
			readings.add(endTime);
		}
		
		for (int i = 0; i < readings.size(); i += 2) {
			result +=  (long) props.get(i+1).get("ttime") - (long) props.get(i).get("ttime");
		}

		BigDecimal res = (new BigDecimal(result)).divide(THOUSAND);
		res = res.divide(SIXTY.multiply(SIXTY), RoundingMode.HALF_UP);
		return res.setScale(2, RoundingMode.HALF_UP);
	}
	
	public static List<ReadingContext> handleWorkHoursReading(EventType activityType, long assignedToUserId, long workOrderId, TicketStatusContext oldTicketStatus, TicketStatusContext newTicketStatus) throws Exception {
		return handleWorkHoursReading(activityType, assignedToUserId, workOrderId, oldTicketStatus, newTicketStatus, false); 
	}

	public static List<ReadingContext> handleWorkHoursReading(EventType activityType, long assignedToUserId, long workOrderId, TicketStatusContext oldTicketStatus, TicketStatusContext newTicketStatus, boolean invalidate) throws Exception {
		List<ReadingContext> readings = new ArrayList<>();
		long now = System.currentTimeMillis();
		if(newTicketStatus != null && newTicketStatus.getId() != -1) {
			newTicketStatus = TicketAPI.getStatus(AccountUtil.getCurrentOrg().getOrgId(), newTicketStatus.getId());
		}
		else {
			newTicketStatus = TicketAPI.getStatus("Submitted");
		}
		if ("Work in Progress".equalsIgnoreCase(newTicketStatus.getStatus())) {
			if (oldTicketStatus.getId() == TicketAPI.getStatus("Assigned").getId() 
					|| oldTicketStatus.getId() == TicketAPI.getStatus("Closed").getId() 
					|| oldTicketStatus.getId() == TicketAPI.getStatus("Resolved").getId()) {
				//pauseWorkOrderForUser(assignedToUserId, activityType, workOrderId, now);
				readings.addAll(addUserWorkHoursReading(assignedToUserId, workOrderId, activityType, "Start", now));
			} else if (oldTicketStatus.getId() == TicketAPI.getStatus("On Hold").getId() || oldTicketStatus.getId() == TicketAPI.getStatus("Work in Progress").getId()) {
				//pauseWorkOrderForUser(assignedToUserId, activityType, workOrderId, now);
				readings.addAll(addUserWorkHoursReading(assignedToUserId, workOrderId, activityType, "Resume", now));
			}
		}  else if ("Resolved".equalsIgnoreCase(newTicketStatus.getStatus())) {
			if (invalidate) {
				readings.addAll(addInvalidatedUserWorkHoursReading(assignedToUserId, workOrderId, activityType, "Close", now));
			} else {
				readings.addAll(addUserWorkHoursReading(assignedToUserId, workOrderId, activityType, "Close", now));
			}
		} else if ("On Hold".equalsIgnoreCase(newTicketStatus.getStatus())) {
			readings.addAll(addUserWorkHoursReading(assignedToUserId, workOrderId, activityType, "Pause", now));
		} else if ("Closed".equalsIgnoreCase(newTicketStatus.getStatus())) {
			if (oldTicketStatus.getId() == TicketAPI.getStatus("On Hold").getId()) {
				readings.addAll(addUserWorkHoursReading(assignedToUserId, workOrderId, activityType, "Resume", now));
				readings.addAll(addUserWorkHoursReading(assignedToUserId, workOrderId, activityType, "Close", now));
			} else if (oldTicketStatus.getId() == TicketAPI.getStatus("Work in Progress").getId()) {
				readings.addAll(addUserWorkHoursReading(assignedToUserId, workOrderId, activityType, "Close", now));
			}
		}
		return readings;
	}
	
	public static void markAutoEntriesAsInvalid(long assignedToUserId, long workOrderId) throws Exception {
		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = bean.getModule("userworkhoursreading");
		List<FacilioField> fields = bean.getAllFields("userworkhoursreading");
		Optional<FacilioField> hasManualEntry = fields.stream().filter(e -> e.getName().equals("hasManualEntry")).findFirst();
		if (!hasManualEntry.isPresent()) {
			throw new IllegalStateException();
		}
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(Arrays.asList(hasManualEntry.get()))
				.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getOrgId(), module))
				.andCondition(CriteriaAPI.getCondition("PARENT_ID", "parentId", String.valueOf(assignedToUserId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("WO_ID", "woId", String.valueOf(workOrderId), NumberOperators.EQUALS));
		Map<String, Object> props = new HashMap<>();
		props.put("hasManualEntry", true);
		updateBuilder.update(props);
	}

	public static List<ReadingContext> addActualWorkHoursReading(long assignedToUserId, long workOrderId, EventType activityType, List<List<Long>> actualTimings) throws Exception {
		List<ReadingContext> whreadings = new ArrayList<>();
		if (actualTimings.size() == 1) {
			List<Long> readings = actualTimings.get(0);
			
			long startTime = readings.get(0); //1533025848442
			long endTime = readings.get(1); //  1533025848442
			whreadings.addAll(addUserWorkHoursReading(assignedToUserId, workOrderId, activityType, "Start", startTime, true));
			whreadings.addAll(addUserWorkHoursReading(assignedToUserId, workOrderId, activityType, "Close", endTime, true));
		} else {
			List<Long> readings = actualTimings.get(0);
			long startTime = readings.get(0);
			long endTime = readings.get(1);
			whreadings.addAll(addUserWorkHoursReading(assignedToUserId, workOrderId, activityType, "Start", startTime, true));
			whreadings.addAll(addUserWorkHoursReading(assignedToUserId, workOrderId, activityType, "Pause", endTime, true));
			for (int i = 1; i < actualTimings.size() - 1; i++) {
				List<Long> r = actualTimings.get(i);
				long s = r.get(0);
				long e = r.get(1);
				whreadings.addAll(addUserWorkHoursReading(assignedToUserId, workOrderId, activityType, "Resume", s, true));
				whreadings.addAll(addUserWorkHoursReading(assignedToUserId, workOrderId, activityType, "Pause", e, true));
			}
			readings = actualTimings.get(readings.size() - 1);
			startTime = readings.get(0);
			endTime = readings.get(1);
			whreadings.addAll(addUserWorkHoursReading(assignedToUserId, workOrderId, activityType, "Resume", startTime, true));
			whreadings.addAll(addUserWorkHoursReading(assignedToUserId, workOrderId, activityType, "Close", endTime, true));
		}
		return whreadings;
	}
}
