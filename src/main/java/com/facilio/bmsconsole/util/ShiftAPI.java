package com.facilio.bmsconsole.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BreakContext;
import com.facilio.bmsconsole.context.BusinessHourContext;
import com.facilio.bmsconsole.context.BusinessHoursList;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ShiftContext;
import com.facilio.bmsconsole.context.ShiftRotationApplicableForContext;
import com.facilio.bmsconsole.context.ShiftRotationContext;
import com.facilio.bmsconsole.context.ShiftRotationDetailsContext;
import com.facilio.bmsconsole.context.ShiftUserRelContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BaseLineContext;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.ScheduleInfo.FrequencyType;
import com.facilio.tasker.job.JobContext;
import com.facilio.time.DateTimeUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class ShiftAPI {
	
	public static final long UNLIMITED_PERIOD = -2;
	
	public static List<ShiftContext> getAllShifts() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SHIFT);
		if (module == null) {
			return new ArrayList<>();
		}
		SelectRecordsBuilder<ShiftContext> builder = new SelectRecordsBuilder<ShiftContext>()
				.beanClass(ShiftContext.class)
				.module(modBean.getModule(FacilioConstants.ContextNames.SHIFT))
				.select(modBean.getAllFields(FacilioConstants.ContextNames.SHIFT));
		List<ShiftContext> list = builder.get();
		return list;
		
//		List<ShiftContext> shifts = new ArrayList<>();
//		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
//				.select(FieldFactory.getShiftField())
//				.table(ModuleFactory.getShiftModule().getTableName())
//				.andCustomWhere(ModuleFactory.getShiftModule().getTableName() + ".ORGID=?", AccountUtil.getCurrentOrg().getOrgId())
//				.orderBy("name");
//		List<Map<String, Object>> props = selectBuilder.get();
//		StringJoiner j = new StringJoiner(",");
////		if (props != null && !props.isEmpty()) {
////			for (Map<String, Object> p : props) {
////				ShiftContext s = FieldUtil.getAsBeanFromMap(p, ShiftContext.class);
////				j.add(String.valueOf(s.getBusinessHoursId()));
////				shifts.add(s);
////			}
////		}
//		
//		if (shifts.isEmpty()) {
//			return shifts;
//		}
//		
//		String businessHoursTable = ModuleFactory.getBusinessHoursModule().getTableName();
//		String singleDayTable = ModuleFactory.getSingleDayBusinessHourModule().getTableName();
//		selectBuilder = new GenericSelectRecordBuilder()
//				.select(FieldFactory.getSingleDayBusinessHoursFields())
//				.table(businessHoursTable)
//				.innerJoin(singleDayTable)
//				.on(businessHoursTable+".ID = "+singleDayTable+".PARENT_ID")
//				.andCustomWhere(businessHoursTable+".ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
//				.andCondition(CriteriaAPI.getCondition("PARENT_ID","parentId", j.toString(), NumberOperators.EQUALS))
//				.orderBy("dayOfWeek");
//		
//		props = selectBuilder.get();
//		
//		Map<Long, List<BusinessHourContext>> parentIdVsContext = new HashMap<>();
//		if (props != null && !props.isEmpty()) {
//			for (Map<String, Object> prop: props) {
//				BusinessHourContext b = FieldUtil.getAsBeanFromMap(prop, BusinessHourContext.class);
//				long id = b.getParentId();
//				if (!parentIdVsContext.containsKey(id)) {
//					parentIdVsContext.put(id, new ArrayList<>());
//				}
//				parentIdVsContext.get(id).add(b);
//			}
//		}
//		
////		shifts.forEach(s -> {
////			List<BusinessHourContext> b = parentIdVsContext.get(s.getBusinessHoursId());
////			s.setDays(b);
////		});
//		return shifts;
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
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getShiftUserRelModule().getTableName())
				.select(FieldFactory.getShiftUserRelModuleFields())
				.andCondition(CriteriaAPI.getCondition("SHIFTID", "shiftId", String.valueOf(id), NumberOperators.EQUALS))	
				.orderBy("START_TIME");
		List<Map<String, Object>> list = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(list)) {
			throw new IllegalArgumentException("Shift is associated with employees. Remove before delete");
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SHIFT);
		DeleteRecordBuilder<ShiftContext> builder = new DeleteRecordBuilder<ShiftContext>()
				.module(module)
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		builder.delete();
		
		deleteSchedulers(id);
		
//		ShiftContext shift = null;
//		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
//				.select(FieldFactory.getShiftField())
//				.table(ModuleFactory.getShiftModule().getTableName())
//				.andCustomWhere(ModuleFactory.getShiftModule().getTableName() + ".ORGID=? AND "+ ModuleFactory.getShiftModule().getTableName()+".ID=?", AccountUtil.getCurrentOrg().getOrgId(), id);
//		List<Map<String, Object>> props = selectBuilder.get();
//		if (props != null && !props.isEmpty()) {
//			shift = FieldUtil.getAsBeanFromMap(props.get(0), ShiftContext.class);
//		}
//		
//		if (shift == null) {
//			return;
//		}
//		
//		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
//				.table(ModuleFactory.getShiftModule().getName())
//				.andCustomWhere(ModuleFactory.getShiftModule().getTableName() + ".ORGID=? AND "+ ModuleFactory.getShiftModule().getTableName()+".ID=?", AccountUtil.getCurrentOrg().getOrgId(), id);
//		deleteBuilder.delete();
		
//		BusinessHoursList businessHoursList = BusinessHoursAPI.getBusinessHours(shift.getBusinessHoursId());
		
//		scheduleOneTimeJob(shift, businessHoursList);
		
//		deleteJobsForshifts(businessHoursList.stream().map(BusinessHourContext::getId).collect(Collectors.toList()));
		
//		BusinessHoursAPI.deleteBusinessHours(shift.getBusinessHoursId());
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
	
	public static List<ReadingContext> handleWorkHoursReading(EventType activityType, long assignedToUserId, long workOrderId, FacilioStatus oldTicketStatus, FacilioStatus newTicketStatus) throws Exception {
		return handleWorkHoursReading(activityType, assignedToUserId, workOrderId, oldTicketStatus, newTicketStatus, false); 
	}

	public static List<ReadingContext> handleWorkHoursReading(EventType activityType, long assignedToUserId, long workOrderId, FacilioStatus oldTicketStatus, FacilioStatus newTicketStatus, boolean invalidate) throws Exception {
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

	public static void addShift(ShiftContext shift) throws Exception {
		checkValidation(shift);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		InsertRecordBuilder<ShiftContext> builder = new InsertRecordBuilder<ShiftContext>()
				.module(modBean.getModule(FacilioConstants.ContextNames.SHIFT))
				.fields(modBean.getAllFields(FacilioConstants.ContextNames.SHIFT))
				.addRecord(shift);
		builder.save();
		
		addSchedulers(shift);
	}

	private static void addSchedulers(ShiftContext shift) throws Exception {
		ScheduleInfo schedule = new ScheduleInfo();
		schedule.setFrequencyType(FrequencyType.DAILY);
		schedule.addTime(shift.getEndTimeAsLocalTime());
		FacilioTimer.scheduleCalendarJob(shift.getId(), "AttendanceAbsentSchedulerJob", System.currentTimeMillis(), schedule, "facilio");
	}
	
	private static void deleteSchedulers(long id) throws Exception {
		FacilioTimer.deleteJob(id, "AttendanceAbsentSchedulerJob");
	}

	private static void checkValidation(ShiftContext shift) throws Exception {
		if (StringUtils.isEmpty(shift.getName())) {
			throw new IllegalArgumentException("Name is mandatory");
		}
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SHIFT);
		SelectRecordsBuilder<ShiftContext> builder = new SelectRecordsBuilder<ShiftContext>()
				.beanClass(ShiftContext.class)
				.module(module)
				.select(Collections.singletonList(FieldFactory.getIdField(module)))
				.andCondition(CriteriaAPI.getCondition("NAME", "name", shift.getName(), StringOperators.IS));
		if (shift.getId() > 0) {
			builder.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(shift.getId()), NumberOperators.NOT_EQUALS));
		}
		List<ShiftContext> list = builder.get();
		if (CollectionUtils.isNotEmpty(list) && list.size() > 0) {
			throw new IllegalArgumentException("Shift with name already found");
		}
	}

	public static void updateShift(ShiftContext shift) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SHIFT);
		UpdateRecordBuilder<ShiftContext> builder = new UpdateRecordBuilder<ShiftContext>()
				.module(module)
				.fields(modBean.getAllFields(module.getName()))
				.andCondition(CriteriaAPI.getIdCondition(shift.getId(), module));
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		builder.update(shift);
		
		deleteSchedulers(shift.getId());
		addSchedulers(shift);
	}

	public static ShiftContext getShift(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SHIFT);
		SelectRecordsBuilder<ShiftContext> builder = new SelectRecordsBuilder<ShiftContext>()
				.beanClass(ShiftContext.class)
				.module(module)
				.select(modBean.getAllFields(module.getName()))
				.andCondition(CriteriaAPI.getIdCondition(id, module));
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		return builder.fetchFirst();
	}

	public static List<ShiftUserRelContext> getShiftUserMapping(long startTime, long endTime) throws Exception {
		return getShiftUserMapping(startTime, endTime, -1, -1);
	}
	
	public static List<ShiftUserRelContext> getShiftUserMapping(long startTime, long endTime, long shiftId) throws Exception {
		return getShiftUserMapping(startTime, endTime, -1, shiftId);
	}
	
	public static List<ShiftUserRelContext> getShiftUserMapping(long startTime, long endTime, long orgUserId, long shiftId) throws Exception {
		startTime = DateTimeUtil.getDayStartTimeOf(startTime);
        endTime = DateTimeUtil.getDayEndTimeOf(endTime);
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getShiftUserRelModule().getTableName())
				.select(FieldFactory.getShiftUserRelModuleFields())
				.orderBy("START_TIME");
		
		Criteria c = new Criteria();
		c.addAndCondition(CriteriaAPI.getCondition("END_TIME", "endTime", String.valueOf(startTime), NumberOperators.GREATER_THAN_EQUAL));
		c.addOrCondition(CriteriaAPI.getCondition("END_TIME", "endTime", String.valueOf(UNLIMITED_PERIOD), NumberOperators.EQUALS));
		builder.andCriteria(c);
		
		c = new Criteria();
		c.addAndCondition(CriteriaAPI.getCondition("START_TIME", "startTime", String.valueOf(endTime), NumberOperators.LESS_THAN_EQUAL));
		c.addOrCondition(CriteriaAPI.getCondition("START_TIME", "startTime", String.valueOf(UNLIMITED_PERIOD), NumberOperators.EQUALS));
		builder.andCriteria(c);
		
		if (orgUserId > 0) {
			builder.andCondition(CriteriaAPI.getCondition("ORG_USERID", "orgUserid", String.valueOf(orgUserId), NumberOperators.EQUALS));
		}
		if (shiftId > 0) {
			builder.andCondition(CriteriaAPI.getCondition("SHIFTID", "shiftId", String.valueOf(shiftId), NumberOperators.EQUALS));	
		}
		
		List<Map<String, Object>> list = builder.get();
		List<ShiftUserRelContext> shiftUserMapping = FieldUtil.getAsBeanListFromMapList(list, ShiftUserRelContext.class);
		System.out.println(list);
		return shiftUserMapping;		
	}
	
	private static void insertShiftUserMapping(ShiftUserRelContext shift) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getShiftUserRelModule().getTableName())
				.fields(FieldFactory.getShiftUserRelModuleFields());
		insertBuilder.addRecord(FieldUtil.getAsProperties(shift));
		insertBuilder.save();
	}

	public static void addShiftUserMapping(ShiftContext shift, long orgUserId, long startTime, long endTime) throws Exception {
		startTime = DateTimeUtil.getDayStartTimeOf(startTime);
        endTime = DateTimeUtil.getDayEndTimeOf(endTime);
        
		List<ShiftUserRelContext> shiftUserMapping = getShiftUserMapping(startTime, endTime, orgUserId, -1);
		
		int count = 0;
		Map<String, Object> prop = new HashMap<>();
		while (count < shiftUserMapping.size()) {
			ShiftUserRelContext shiftUserRel = shiftUserMapping.get(count);
			
			count++;
			boolean containsTime = shiftUserRel.containsTime(startTime);
			if (containsTime) {
				List<FacilioField> fields = new ArrayList<>();
				fields.add(FieldFactory.getField("endTime", "END_TIME", FieldType.NUMBER));
				fields.add(FieldFactory.getField("shiftId", "SHIFTID", FieldType.NUMBER));
				
				GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
						.table(ModuleFactory.getShiftUserRelModule().getTableName())
						.fields(fields)
						.andCondition(CriteriaAPI.getIdCondition(shiftUserRel.getId(), ModuleFactory.getShiftUserRelModule()));
				
				boolean shouldRearrangeShifts = endTime != shiftUserRel.getEndTime();
				if (shouldRearrangeShifts) {
					prop.put("endTime", startTime - 1);
				} else {
					prop.put("shiftId", shift.getId());
				}
				if (shouldRearrangeShifts && shiftUserRel.getStartTime() > (startTime - 1)) {
					// If start time is greater than end time, delete it
					GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
							.table(ModuleFactory.getShiftBreakRelModule().getTableName())
							.andCondition(CriteriaAPI.getIdCondition(shiftUserRel.getId(), ModuleFactory.getShiftUserRelModule()));
					deleteBuilder.delete();
				} else {
					builder.update(prop);
				}
				prop.clear();
				
				if (shouldRearrangeShifts) {
					ShiftUserRelContext add = new ShiftUserRelContext();
					add.setStartTime(startTime);
					add.setEndTime(endTime);
					add.setShiftId(shift.getId());
					add.setOuid(orgUserId);
					insertShiftUserMapping(add);
				}
				
				if (shouldRearrangeShifts && shiftUserRel.containsTime(endTime)) {
					// When changing last row, need to handle special case
					ShiftUserRelContext add = new ShiftUserRelContext();
					add.setStartTime(endTime + 1);
					add.setEndTime(shiftUserRel.getEndTime());
					add.setShiftId(shiftUserRel.getShiftId());
					add.setOuid(orgUserId);
					insertShiftUserMapping(add);
				}
				break;
			}
		}
		
		List<Long> deleteIds = new ArrayList<>();
		while (count < shiftUserMapping.size()) {
			ShiftUserRelContext shiftUserRel = shiftUserMapping.get(count);
			count++;
			if (endTime == UNLIMITED_PERIOD || !shiftUserRel.containsTime(endTime + 1)) {
				deleteIds.add(shiftUserRel.getId());
			}
			else {
				if ((endTime + 1) != shiftUserRel.getStartTime()) {
					GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
							.table(ModuleFactory.getShiftUserRelModule().getTableName())
							.fields(Collections.singletonList(FieldFactory.getField("startTime", "START_TIME", FieldType.NUMBER)))
							.andCondition(CriteriaAPI.getIdCondition(shiftUserRel.getId(), ModuleFactory.getShiftUserRelModule()));
					prop.put("startTime", endTime + 1);
					builder.update(prop);
					prop.clear();
				}
				
				break;
			}
		}
		
		if (CollectionUtils.isNotEmpty(deleteIds)) {
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table(ModuleFactory.getShiftUserRelModule().getTableName())
					.andCondition(CriteriaAPI.getIdCondition(deleteIds, ModuleFactory.getShiftUserRelModule()));
			builder.delete();
		}
	}

	public static void addOrUpdateBreak(BreakContext breakContext) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BREAK);
		
		if (breakContext.getId() > 0) {
			UpdateRecordBuilder<BreakContext> builder = new UpdateRecordBuilder<BreakContext>()
					.module(module)
					.fields(modBean.getAllFields(module.getName()))
					.andCondition(CriteriaAPI.getIdCondition(breakContext.getId(), module));
			builder.update(breakContext);
			
			deleteBreakShiftRel(breakContext.getId());
		}
		else {
			InsertRecordBuilder<BreakContext> builder = new InsertRecordBuilder<BreakContext>()
					.module(module)
					.fields(modBean.getAllFields(module.getName()));
			builder.addRecord(breakContext);
			builder.save();
		}
		
		addBreakShiftRel(breakContext);
	}
	
	public static List<BreakContext> getBreakList() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BREAK);
		SelectRecordsBuilder<BreakContext> builder = new SelectRecordsBuilder<BreakContext>()
				.module(module)
				.select(modBean.getAllFields(module.getName()))
				.beanClass(BreakContext.class);
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		List<BreakContext> list = builder.get();
		return list;
	}
	
	private static void deleteBreakShiftRel(long id) throws Exception {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getShiftBreakRelModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("BREAK_ID", "breakId", String.valueOf(id), NumberOperators.EQUALS));
		builder.delete();
	}

	private static void addBreakShiftRel(BreakContext breakContext) throws Exception {
		if (CollectionUtils.isNotEmpty(breakContext.getShifts())) {
			List<Map<String, Object>> props = new ArrayList<>();
			List<ShiftContext> shiftFromDB = new ArrayList<>();
			for (ShiftContext shiftContext : breakContext.getShifts()) {
				ShiftContext shift = getShift(shiftContext.getId());
				if (shift == null) {
					throw new IllegalArgumentException("Invalid shift");
				}
				shiftFromDB.add(shift);
				
				Map<String, Object> prop = new HashMap<>();
				props.add(prop);
				
				prop.put("shiftId", shift.getId());
				prop.put("breakId", breakContext.getId());
			}
			breakContext.setShifts(shiftFromDB);
			
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(ModuleFactory.getShiftBreakRelModule().getTableName())
					.fields(FieldFactory.getShiftBreakRelModuleFields());
			
			insertBuilder.addRecords(props);
			insertBuilder.save();
		}		
	}
	
	public static List<ShiftContext> getShiftsAttachedToBreak(long breakId) throws Exception {
		List<ShiftContext> list = new ArrayList<>();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getShiftBreakRelModule().getTableName())
				.select(FieldFactory.getShiftBreakRelModuleFields())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getShiftBreakRelModule()))
				.andCondition(CriteriaAPI.getCondition("BREAK_ID", "breakId", String.valueOf(breakId), NumberOperators.EQUALS));
		List<Map<String, Object>> shiftBreakList = builder.get();
		if (CollectionUtils.isNotEmpty(shiftBreakList)) {
			for (Map<String, Object> map : shiftBreakList) {
				list.add(getShift((long) map.get("shiftId")));
			}
		}
		return list;
	}

	public static BreakContext getBreak(long breakId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BREAK);
		SelectRecordsBuilder<BreakContext> builder = new SelectRecordsBuilder<BreakContext>()
				.module(module)
				.select(modBean.getAllFields(module.getName()))
				.beanClass(BreakContext.class)
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(breakId, module));
		BreakContext breakContext = builder.fetchFirst();
		breakContext.setShifts(getShiftsAttachedToBreak(breakId));
		return breakContext;
	}

	public static void deleteBreak(Long breakId) throws Exception {
		deleteBreakShiftRel(breakId);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BREAK);
		DeleteRecordBuilder<BreakContext> builder = new DeleteRecordBuilder<BreakContext>()
				.module(module)
				.andCondition(CriteriaAPI.getIdCondition(breakId, module))
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		;
		builder.delete();
	}
	
	public static List<BreakContext> getBreakssAttachedToShift(long breakId) throws Exception {
		List<BreakContext> list = new ArrayList<>();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getShiftBreakRelModule().getTableName())
				.select(FieldFactory.getShiftBreakRelModuleFields())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getShiftBreakRelModule()))
				.andCondition(CriteriaAPI.getCondition("SHIFTID", "shiftId", String.valueOf(breakId), NumberOperators.EQUALS));
		List<Map<String, Object>> shiftBreakList = builder.get();
		if (CollectionUtils.isNotEmpty(shiftBreakList)) {
			for (Map<String, Object> map : shiftBreakList) {
				list.add(getBreak((long) map.get("breakId")));
			}
		}
		return list;
	}
	
	public static void addShiftRotation(ShiftRotationContext shiftRotation) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		InsertRecordBuilder<ShiftRotationContext> builder = new InsertRecordBuilder<ShiftRotationContext>()
				.module(modBean.getModule(FacilioConstants.ContextNames.SHIFT_ROTATION))
				.fields(modBean.getAllFields(FacilioConstants.ContextNames.SHIFT_ROTATION))
				.addRecord(shiftRotation);
		builder.save();
		
	}
	
	public static void addShiftRotationApplicableFor(List<ShiftRotationApplicableForContext> applicableForList, long shiftRotationId) throws Exception {
		List<Map<String, Object>> props = new ArrayList<>();
		for(ShiftRotationApplicableForContext applicableFor: applicableForList) {
			applicableFor.setShiftRotationId(shiftRotationId);
			props.add(FieldUtil.getAsProperties(applicableFor));
		}
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getShiftRotationApplicableForModule().getTableName())
				.fields(FieldFactory.getShiftRotationApplicableForModuleFields());
		insertBuilder.addRecords(props);
		insertBuilder.save();
	
	}
	
	public static void addshiftRotationDetails(List<ShiftRotationDetailsContext> shiftRotationDetails, long shiftRotationId) throws Exception {
		List<Map<String, Object>> props = new ArrayList<>();
		for(ShiftRotationDetailsContext detail: shiftRotationDetails) {
			detail.setShiftRotationId(shiftRotationId);
			props.add(FieldUtil.getAsProperties(detail));
		}
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getShiftRotationDetailsModule().getTableName())
				.fields(FieldFactory.getShiftRotationDetailsModuleFields());
		insertBuilder.addRecords(props);
		insertBuilder.save();
	
	}
	
	public static List<ShiftRotationApplicableForContext> getApplicableForShiftRotation(long id) throws Exception {
		List<ShiftRotationApplicableForContext> list = new ArrayList<>();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getShiftRotationApplicableForModule().getTableName())
				.select(FieldFactory.getShiftRotationApplicableForModuleFields())
				.andCondition(CriteriaAPI.getCondition("SHIFT_ROTATION_ID", "shiftRotationId", String.valueOf(id), NumberOperators.EQUALS));
		List<Map<String, Object>> props = builder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			for (Map<String, Object> prop : props) {
				list.add(FieldUtil.getAsBeanFromMap(prop, ShiftRotationApplicableForContext.class));
			}
		}
		return list;
	}
	
	public static List<ShiftRotationDetailsContext> getShiftRotationDetailsForShiftRotation(long id) throws Exception {
		List<ShiftRotationDetailsContext> list = new ArrayList<>();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getShiftRotationDetailsModule().getTableName())
				.select(FieldFactory.getShiftRotationDetailsModuleFields())
				.andCondition(CriteriaAPI.getCondition("SHIFT_ROTATION_ID", "shiftRotationId", String.valueOf(id), NumberOperators.EQUALS));
		List<Map<String, Object>> props = builder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			for (Map<String, Object> prop : props) {
				list.add(FieldUtil.getAsBeanFromMap(prop, ShiftRotationDetailsContext.class));
			}
		}
		return list;
	}
	
	public static void updateShiftUserMapping(List<ShiftUserRelContext> shiftUserRels) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getField("endTime", "END_TIME", FieldType.NUMBER));
		fields.add(FieldFactory.getField("shiftId", "SHIFTID", FieldType.NUMBER));
		fields.add(FieldFactory.getField("startTime", "START_TIME", FieldType.NUMBER));
		for (ShiftUserRelContext shiftUserRel : shiftUserRels) {
			Map<String, Object> prop = new HashMap<>();
			GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
					.table(ModuleFactory.getShiftUserRelModule().getTableName())
					.fields(FieldFactory.getShiftUserRelModuleFields())
					// .andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getShiftUserRelModule()))
					.andCondition(
							CriteriaAPI.getIdCondition(shiftUserRel.getId(), ModuleFactory.getShiftUserRelModule()));

			prop.put("endTime", shiftUserRel.getEndTime());
			prop.put("startTime", shiftUserRel.getStartTime());
			prop.put("shiftId", shiftUserRel.getShiftId());
			builder.update(prop);
			prop.clear();
		}

	}
}
