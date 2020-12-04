package com.facilio.bmsconsole.util;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.accounts.dto.Organization;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.ScheduleInfo.FrequencyType;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.google.type.DayOfWeek;

import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;

public class BusinessHoursAPI {
	
	public static final String DEFAULT_DAY_START_TIME = "00:00";
	public static final String DEFAULT_DAY_END_TIME = "23:59";

	public static long addBusinessHours(BusinessHoursContext businessHours) throws Exception {

		Map<String, Object> props = new HashMap<>();
		props.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
		props.put("name", businessHours.getName());
		GenericInsertRecordBuilder businessHoursBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getBusinessHoursModule().getTableName())
				.fields(FieldFactory.getBusinessHoursFields()).addRecord(props);

		businessHoursBuilder.save();
		long parentId = (long) props.get("id");
		businessHours.setId(parentId);
		addSingleBusinessHours(businessHours);
		return parentId;
	}

	public static long addBusinessHours(List<BusinessHourContext> businessHours) throws SQLException, RuntimeException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Object> props = new HashMap<>();
		props.put("orgId", AccountUtil.getCurrentOrg().getOrgId());
		GenericInsertRecordBuilder businessHoursBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getBusinessHoursModule().getTableName())
				.fields(FieldFactory.getBusinessHoursFields()).addRecord(props);
		businessHoursBuilder.save();
		long parentId = (long) props.get("id");

		GenericInsertRecordBuilder singleDayBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getSingleDayBusinessHourModule().getTableName())
				.fields(FieldFactory.getSingleDayBusinessHoursFields());

		List<Map<String, Object>> singleDayProps = new ArrayList<>();
		for (BusinessHourContext singleDay : businessHours) {
			singleDay.setParentId(parentId);
			singleDayProps.add(FieldUtil.getAsProperties(singleDay));
		}
		singleDayBuilder.addRecords(singleDayProps);
		singleDayBuilder.save();

		int len = businessHours.size();
		for (int i = 0; i < len; ++i) {
			businessHours.get(i).setId((long) singleDayProps.get(i).get("id"));
		}

		return parentId;
	}

	public static BusinessHoursList getCorrespondingBusinessHours(long siteId) throws Exception {
		BusinessHoursList businessHoursList = null;
		if (siteId > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SITE);
			SelectRecordsBuilder<SiteContext> builder = new SelectRecordsBuilder<SiteContext>()
					.module(module)
					.beanClass(SiteContext.class)
					.select(modBean.getAllFields(module.getName()))
					.andCondition(CriteriaAPI.getIdCondition(siteId, module));
			SiteContext siteContext = builder.fetchFirst();
			if (siteContext != null && siteContext.getOperatingHour() > 0) {
				long id = siteContext.getOperatingHour();
				businessHoursList = getBusinessHours(id);
			}
		}

		if (businessHoursList == null) {
			Organization organization = AccountUtil.getCurrentOrg();
			if (organization.getBusinessHour() > 0) {
				businessHoursList = getBusinessHours(organization.getBusinessHour());
			}
		}

		if (businessHoursList == null) {
			businessHoursList = new BusinessHoursList();
			for (int i = 1; i <= 7; i++) {
				BusinessHourContext businessHourContext = new BusinessHourContext();
				businessHourContext.setDayOfWeek(i);
				businessHoursList.add(businessHourContext);
			}
		}
		return businessHoursList;
	}

	public static BusinessHoursList getBusinessHours(long id) throws Exception {
		String businessHoursTable = ModuleFactory.getBusinessHoursModule().getTableName();
		String singleDayTable = ModuleFactory.getSingleDayBusinessHourModule().getTableName();
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getSingleDayBusinessHoursFields()).table(businessHoursTable)
				.innerJoin(singleDayTable).on(businessHoursTable + ".ID = " + singleDayTable + ".PARENT_ID")
				.andCustomWhere(businessHoursTable + ".ORGID = ? AND " + businessHoursTable + ".ID = ?", orgId, id)
				.orderBy("dayOfWeek");

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			BusinessHoursList businessHours = new BusinessHoursList();
			for (Map<String, Object> prop : props) {
				businessHours.add(FieldUtil.getAsBeanFromMap(prop, BusinessHourContext.class));
			}
			return businessHours;
		}
		return null;
	}
	
	public static BusinessHoursList getBusinessHours(Criteria criteria) throws Exception {
		
		if(criteria == null) {
			throw new IllegalArgumentException("criteria cannot be null during fetch");
		}
		String businessHoursTable = ModuleFactory.getBusinessHoursModule().getTableName();
		String singleDayTable = ModuleFactory.getSingleDayBusinessHourModule().getTableName();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getSingleDayBusinessHoursFields()).table(businessHoursTable)
				.innerJoin(singleDayTable).on(businessHoursTable + ".ID = " + singleDayTable + ".PARENT_ID")
				.andCriteria(criteria)
				.orderBy("dayOfWeek");

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			BusinessHoursList businessHours = new BusinessHoursList();
			for (Map<String, Object> prop : props) {
				businessHours.add(FieldUtil.getAsBeanFromMap(prop, BusinessHourContext.class));
			}
			return businessHours;
		}
		return null;
	}

	public static List<BusinessHoursContext> getBusinessHours(List<Long> ids) throws Exception {
		List<BusinessHoursContext> businessHours = new ArrayList<>();
		if (CollectionUtils.isEmpty(ids)) {
			return businessHours;
		}

		FacilioModule module = ModuleFactory.getBusinessHoursModule();
		String businessHoursTable = ModuleFactory.getBusinessHoursModule().getTableName();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getBusinessHoursFields()).table(businessHoursTable)
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(ids, module)).orderBy("Id");
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<BusinessHourContext> singleDayBusinessHourList = getSingleDayBusinessHours(ids);
			for (Map<String, Object> prop : props) {
				BusinessHoursContext businessHour = FieldUtil.getAsBeanFromMap(prop, BusinessHoursContext.class);
				businessHour.setSingleDaybusinessHoursList(singleDayBusinessHourList.stream()
						.filter(bh -> bh.getParentId() == (businessHour.getId())).collect(Collectors.toList()));
				businessHours.add(businessHour);
			}
		}
		return businessHours;
	}
	
	
	public static List<DateRange> getBusinessHoursForNextNDays(BusinessHoursContext businessHour, int days) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		
		List<BusinessHourContext> businessHours = businessHour.getSingleDaybusinessHoursList();
		
		if(businessHours == null || businessHours.isEmpty()) {
			businessHours = fetchDefault24_7Days();
		}
		
		List<DateRange> dateRanges = new ArrayList<DateRange>();
		
		for(BusinessHourContext singleDayBusinessHour :businessHours) {
			
			if(singleDayBusinessHour.getStartTime() == null) {
				singleDayBusinessHour.setStartTime(DEFAULT_DAY_START_TIME);
			}
			if(singleDayBusinessHour.getEndTime() == null) {
				singleDayBusinessHour.setEndTime(DEFAULT_DAY_END_TIME);
			}
			
			ScheduleInfo startTimeScheduleInfo = new ScheduleInfo();
			
			startTimeScheduleInfo.setFrequencyType(FrequencyType.WEEKLY);
			startTimeScheduleInfo.setValues(Collections.singletonList(singleDayBusinessHour.getDayOfWeek()));
			startTimeScheduleInfo.setTimes(Collections.singletonList(singleDayBusinessHour.getStartTime()));
			
			ScheduleInfo endTimeScheduleInfo = new ScheduleInfo();
			
			endTimeScheduleInfo.setFrequencyType(FrequencyType.WEEKLY);
			endTimeScheduleInfo.setValues(Collections.singletonList(singleDayBusinessHour.getDayOfWeek()));
			endTimeScheduleInfo.setTimes(Collections.singletonList(singleDayBusinessHour.getEndTime()));
			
			long startTime = DateTimeUtil.getDayStartTime();
			
			long endTime = DateTimeUtil.addDays(startTime, days);
			
			while(true) {
				
				long tempStartTime = startTimeScheduleInfo.nextExecutionTime(startTime/1000) * 1000;
				long tempEndTime = endTimeScheduleInfo.nextExecutionTime(tempStartTime/1000) * 1000;
				
				if(tempStartTime < endTime && tempEndTime <= endTime) {
					dateRanges.add(new DateRange(tempStartTime, tempEndTime));
					startTime = tempEndTime;
				}
				else {
					break;
				}
			}
 		}
		
		Collections.sort(dateRanges);
		
		return dateRanges;
	}

	private static List<BusinessHourContext> fetchDefault24_7Days() {
		
		List<BusinessHourContext> businessHours = new ArrayList<BusinessHourContext>();
		
		BusinessHourContext businessHour = new BusinessHourContext();
		businessHour.setDayOfWeek(DayOfWeek.SUNDAY_VALUE);
		businessHours.add(businessHour);
		
		businessHour = new BusinessHourContext();
		businessHour.setDayOfWeek(DayOfWeek.MONDAY_VALUE);
		businessHours.add(businessHour);
		
		businessHour = new BusinessHourContext();
		businessHour.setDayOfWeek(DayOfWeek.TUESDAY_VALUE);
		businessHours.add(businessHour);
		
		businessHour = new BusinessHourContext();
		businessHour.setDayOfWeek(DayOfWeek.WEDNESDAY_VALUE);
		businessHours.add(businessHour);
		
		businessHour = new BusinessHourContext();
		businessHour.setDayOfWeek(DayOfWeek.THURSDAY_VALUE);
		businessHours.add(businessHour);
		
		businessHour = new BusinessHourContext();
		businessHour.setDayOfWeek(DayOfWeek.FRIDAY_VALUE);
		businessHours.add(businessHour);
		
		businessHour = new BusinessHourContext();
		businessHour.setDayOfWeek(DayOfWeek.SATURDAY_VALUE);
		businessHours.add(businessHour);
		
		return businessHours;
	}

	public static List<BusinessHourContext> getSingleDayBusinessHours(List<Long> ids) throws Exception {// need
		FacilioModule module = ModuleFactory.getBusinessHoursModule();
		String businessHoursTable = ModuleFactory.getBusinessHoursModule().getTableName();
		String singleDayTable = ModuleFactory.getSingleDayBusinessHourModule().getTableName();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getSingleDayBusinessHoursFields()).table(businessHoursTable)
				.innerJoin(singleDayTable).on(businessHoursTable + ".ID = " + singleDayTable + ".PARENT_ID")
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getIdCondition(ids, module)).orderBy("dayOfWeek");
		List<Map<String, Object>> props = selectBuilder.get();
		List<BusinessHourContext> businessHours = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				businessHours.add(FieldUtil.getAsBeanFromMap(prop, BusinessHourContext.class));
			}
		}
		return businessHours;
	}

	public static void deleteBusinessHours(long id) throws Exception {
		FacilioModule businessHoursTable = ModuleFactory.getBusinessHoursModule();
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder().table(businessHoursTable.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(id, businessHoursTable)).andCondition(
						CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getOrgId(), businessHoursTable));
		builder.delete();
	}
	
	public static int updateBusinessHours(BusinessHoursContext businessHours) throws Exception {
		GenericUpdateRecordBuilder businessHoursBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getBusinessHoursModule().getTableName())
				.fields(FieldFactory.getBusinessHoursFields()).andCustomWhere("id = ?", businessHours.getId());
		Map<String, Object> props = FieldUtil.getAsProperties(businessHours);
		return businessHoursBuilder.update(props);
	}
	
	public static void deleteSingleBusinessHour(long id) throws Exception {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getSingleDayBusinessHourModule().getTableName())
				.andCustomWhere("PARENT_ID = ?", id);
		builder.delete();
	}
	
	public static void addSingleBusinessHours(BusinessHoursContext businessHours) throws Exception {
		GenericInsertRecordBuilder singleDayBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getSingleDayBusinessHourModule().getTableName())
				.fields(FieldFactory.getSingleDayBusinessHoursFields());

		List<Map<String, Object>> singleDayProps = new ArrayList<>();
		List<BusinessHourContext> singleDayList = businessHours.getSingleDaybusinessHoursList();
		for (BusinessHourContext singleDay : singleDayList) {
			singleDay.setParentId(businessHours.getId());
			singleDayProps.add(FieldUtil.getAsProperties(singleDay));
		}
		singleDayBuilder.addRecords(singleDayProps);
		singleDayBuilder.save();

		int len = singleDayList.size();
		for (int i = 0; i < len; ++i) {
			singleDayList.get(i).setId((long) singleDayProps.get(i).get("id"));
		}

	}

}
