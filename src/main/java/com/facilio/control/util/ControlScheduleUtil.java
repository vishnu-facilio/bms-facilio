package com.facilio.control.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

import con.facilio.control.ControlGroupAssetCategory;
import con.facilio.control.ControlGroupAssetContext;
import con.facilio.control.ControlGroupContext;
import con.facilio.control.ControlGroupFieldContext;
import con.facilio.control.ControlGroupRoutineContext;
import con.facilio.control.ControlGroupRoutineSectionContext;
import con.facilio.control.ControlGroupSection;
import con.facilio.control.ControlScheduleContext;
import con.facilio.control.ControlScheduleExceptionContext;
import con.facilio.control.ControlScheduleGroupedSlot;
import con.facilio.control.ControlScheduleSlot;

public class ControlScheduleUtil {

	public static final String CONTROL_SCHEDULE_MODULE_NAME = "controlSchedule";
	public static final String CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME = "controlScheduleException";
	public static final String CONTROL_GROUP_MODULE_NAME = "controlGroupv2";
	public static final String CONTROL_GROUP_SECTION_MODULE_NAME = "controlGroupSection";
	public static final String CONTROL_GROUP_ASSET_CATEGORY_MODULE_NAME = "controlGroupAssetCategory";
	public static final String CONTROL_GROUP_ASSET_MODULE_NAME = "controlGroupAsset";
	public static final String CONTROL_GROUP_ASSET_FIELD_MODULE_NAME = "controlGroupField";
	public static final String CONTROL_GROUP_ROUTINE_MODULE_NAME = "controlGroupRoutine";
	public static final String CONTROL_GROUP_ROUTINE_SECTION_MODULE_NAME = "controlGroupRoutineSection";
	
	public static final String CONTROL_SCHEDULE_UNPLANNED_SLOTS_MODULE_NAME = "controlScheduleSlots";
	public static final String CONTROL_SCHEDULE_PLANNED_SLOTS_MODULE_NAME = "controlScheduleGroupedSlots";
	
	public static final String CONTROL_SCHEDULE_EXCEPTION_CONTEXT = "controlScheduleExceptionContext";
	public static final String CONTROL_SCHEDULE_CONTEXT = "controlScheduleContext";
	public static final String CONTROL_GROUP_CONTEXT = "controlGroupContext";
	public static final String CONTROL_GROUP_CONTEXT_OLD = "controlGroupContextOld";
	public static final String CONTROL_GROUP_ROUTINE_CONTEXT = "controlGroupRoutineContext";
	
	public static final String CONTROL_GROUP_UNPLANNED_SLOTS = "controlGroupUnplanedSlots";
	public static final String CONTROL_GROUP_PLANNED_SLOTS = "controlGroupplanedSlots";
	
	
	public static List<ControlActionCommandContext> planCommandsForRoutines(List<ControlScheduleSlot> slots) {
		
		List<ControlActionCommandContext> commands = new ArrayList<ControlActionCommandContext>();
		
		for(ControlScheduleSlot slot : slots) {
			if(slot.getRoutine() != null) {
				List<ControlGroupFieldContext> controlFields = new ArrayList<>();
				List<ControlGroupRoutineSectionContext> sections = slot.getRoutine().getSections();
				
				for(ControlGroupRoutineSectionContext section :sections) {
					controlFields.addAll(section.getFields());
				}
				
				for(ControlGroupFieldContext controlField : controlFields) {
					
					commands.add(new ControlActionCommandContext(controlField.getControlGroupAsset().getAsset(), controlField.getFieldId(), controlField.getTrueVal(),slot.getStartTime(),slot.getSchedule(),slot.getRoutine()));
				}
			}
		}
		return commands;
	}
	
	public static List<ControlActionCommandContext> planCommandsForSchedules(List<ControlScheduleGroupedSlot> slots,ControlGroupContext group) {
		
		List<ControlActionCommandContext> commands = new ArrayList<ControlActionCommandContext>();
		
		for(ControlScheduleGroupedSlot slot : slots) {
			
			group.getSections();
			
			for(ControlGroupSection section : group.getSections()) {
				
				List<ControlGroupAssetCategory> categories = section.getCategories();
				
				for(ControlGroupAssetCategory category : categories) {
					
					if(category.getControlAssets() != null) {
						
						for(ControlGroupAssetContext controlAsset  : category.getControlAssets()) {
							
							if(controlAsset.getControlFields() != null) {
								for(ControlGroupFieldContext controlField :  controlAsset.getControlFields()) {
									
									if(controlField.getTrueVal() != null) {
										commands.add(new ControlActionCommandContext(controlAsset.getAsset(), controlField.getFieldId(), controlField.getTrueVal(),slot.getStartTime(),slot.getSchedule()));
									}
									if(controlField.getFalseVal() != null) {
										commands.add(new ControlActionCommandContext(controlAsset.getAsset(), controlField.getFieldId(), controlField.getFalseVal(),slot.getEndTime(),slot.getSchedule()));
									}
								}
							}
						}
					}
				}
			}
		}
		
		return commands;
	}
	
	public static List<ControlScheduleGroupedSlot> planGroupedSlots(List<ControlScheduleSlot> controlSlots) {
		
		List<ControlScheduleSlot> onSlots = new ArrayList<ControlScheduleSlot>();
		List<ControlScheduleSlot> offSlots = new ArrayList<ControlScheduleSlot>();
		
		for(ControlScheduleSlot controlSlot : controlSlots) {
			if(controlSlot.getOffSchedule() != null && controlSlot.getOffSchedule()) {
				offSlots.add(controlSlot);
			}
			else {
				onSlots.add(controlSlot);
			}
		}
		
		Collections.sort(onSlots);
		Collections.sort(offSlots);
		
		List<ControlScheduleSlot> mergedOnSlots = mergeSameTypeSlots(onSlots);
		List<ControlScheduleSlot> mergedOffSlots = mergeSameTypeSlots(offSlots);
		
		List<ControlScheduleSlot> allSlots = new ArrayList<ControlScheduleSlot>();
		
		allSlots.addAll(mergedOnSlots);
		allSlots.addAll(mergedOffSlots);
		
		Collections.sort(allSlots);
		
		return mergeONAndOFFSchedules(allSlots);
	}
	
	
	public static List<ControlScheduleSlot> planScheduleSlots(ControlGroupContext group, long startTime, long endTime) throws Exception {
		
		if(startTime <= 0) {
			startTime = DateTimeUtil.getCurrenTime();
		}
		
		List<ControlScheduleSlot> slots = new ArrayList<ControlScheduleSlot>();
		
		ControlScheduleContext schedule = group.getControlSchedule();
		List<ControlScheduleExceptionContext> exceptions = schedule.getExceptions();
		List<ControlGroupRoutineContext> routines = group.getRoutines();
		
		List<DateRange> businessHourRanges = BusinessHoursAPI.getBusinessHoursList(schedule.getBusinessHoursContext(), startTime, endTime);
		
		if(exceptions != null) {
			
			Map<Long,DateRange> startTimeVsBusinessHoursRangeMap = getAsDateVsDateRangeMap(businessHourRanges);
			
			List<DateRange> businessHoursToBeRemoved = new ArrayList<DateRange>();
			
			for(ControlScheduleExceptionContext exception :exceptions) {
				
				List<DateRange> dateRanges = getExceptionRanges(exception, startTime, endTime);
				
				for(DateRange dateRange :dateRanges) {
					
					if(exception.getExcludeSchedule()) {
						long exceptionDate = DateTimeUtil.getDayStartTimeOf(dateRange.getStartTime());
						DateRange businessHour = startTimeVsBusinessHoursRangeMap.get(exceptionDate);
						if(!businessHoursToBeRemoved.contains(businessHour)) {
							businessHoursToBeRemoved.add(businessHour);
						}
					}
					
					slots.add(new ControlScheduleSlot(schedule,exception, dateRange.getStartTime(), dateRange.getEndTime()));
				}
				
			}
			businessHourRanges.removeAll(businessHoursToBeRemoved);
		}
		
		for(DateRange businessHourRange :businessHourRanges) {
			slots.add(new ControlScheduleSlot(schedule, businessHourRange.getStartTime(), businessHourRange.getEndTime()));
		}
		
		if(routines != null) {
			for(ControlGroupRoutineContext routine : routines) {
				List<Long> routineTimes = routine.scheduleAsObj().nextExecutionTimes(startTime, endTime);
				for(Long routineTime :routineTimes) {
					slots.add(new ControlScheduleSlot(routine, routineTime));
				}
			}
		}
		
		return slots;
	}
	
	private static List<ControlScheduleGroupedSlot> mergeONAndOFFSchedules(List<ControlScheduleSlot> controlSlots) {					//controlSlots should sorted by startTime
		
		List<ControlScheduleGroupedSlot> groupedSlots = new ArrayList<ControlScheduleGroupedSlot>(); 
		
		ControlScheduleSlot temp = null;
		
		for(ControlScheduleSlot controlSlot : controlSlots) {
			
			if(temp == null) {
				temp = controlSlot;
			}
			else {
				if(temp.isOffSchedule() == controlSlot.isOffSchedule()) {
					groupedSlots.add(new ControlScheduleGroupedSlot(temp));
					temp = controlSlot;
				}
				else {
					temp.mergeONAndOff(controlSlot);
				}
			}
		}
		groupedSlots.add(new ControlScheduleGroupedSlot(temp));
		
		return groupedSlots;
	}
	
	private static List<ControlScheduleSlot> mergeSameTypeSlots(List<ControlScheduleSlot> controlSlots) {	//controlSlots should sorted by startTime
		
		List<ControlScheduleSlot> groupedSlots = new ArrayList<ControlScheduleSlot>();
		
		ControlScheduleSlot temp = null;
		
		for(ControlScheduleSlot controlSlot :controlSlots) {
			
			if(temp == null) {
				temp = controlSlot;
			}
			else {
				
				if(temp.isTouching(controlSlot)) {
					temp.merge(controlSlot);
				}
				else {
					groupedSlots.add(temp);
					temp = controlSlot;
				}
			}
		}
		if(temp != null) {
			groupedSlots.add(temp);
		}
		
		return groupedSlots;
	}
	
	private static Map<Long, DateRange> getAsDateVsDateRangeMap(List<DateRange> businessHourRanges) {
		
		Map<Long,DateRange> startTimeVsRangeMap = new HashMap<Long, DateRange>();
		
		for(DateRange businessHourRange : businessHourRanges) {
			
			startTimeVsRangeMap.put(DateTimeUtil.getDayStartTimeOf(businessHourRange.getStartTime()), businessHourRange);
		}
		
		return startTimeVsRangeMap;
	}

	private static List<DateRange> getExceptionRanges(ControlScheduleExceptionContext exception,long startTime, long endTime) throws Exception {
		
		List<DateRange> dateRanges = new ArrayList<DateRange>();
		
		if(exception.getTypeEnum() == ControlScheduleExceptionContext.Type.ONETIME) {
			if(exception.getStartTime() < endTime) {
				dateRanges.add(new DateRange(exception.getStartTime(),exception.getEndTime()));
			}
		}
		else if (exception.getTypeEnum() == ControlScheduleExceptionContext.Type.RECURING) {
			
			while(true) {
				
				long tempStartTime = exception.startScheduleAsObj().nextExecutionTime(startTime/1000) * 1000;
				long tempEndTime = exception.endScheduleAsObj().nextExecutionTime(tempStartTime/1000) * 1000;
				
				if(tempStartTime < endTime && tempEndTime <= endTime) {
					dateRanges.add(new DateRange(tempStartTime, tempEndTime));
					startTime = tempEndTime;
				}
				if(tempEndTime >= endTime) {
					break;
				}
			}
		}
		return dateRanges;
	}
	
	public static ControlScheduleContext getControlSchedule(long scheduleId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		ControlScheduleContext schedule = (ControlScheduleContext) ControlScheduleUtil.fetchRecord(ControlScheduleContext.class, CONTROL_SCHEDULE_MODULE_NAME, null,CriteriaAPI.getIdCondition(scheduleId, modBean.getModule(CONTROL_SCHEDULE_MODULE_NAME))).get(0);
		
		schedule.setBusinessHoursContext(BusinessHoursAPI.getBusinessHours(Collections.singletonList(schedule.getBusinessHour())).get(0));
		
		List<ControlScheduleExceptionContext> exceptions =  ControlScheduleUtil.fetchRecord(ControlScheduleExceptionContext.class, CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME, null,CriteriaAPI.getCondition("CONTROL_SCHEDULE", "controlSchedule", ""+scheduleId, NumberOperators.EQUALS));
		
		schedule.setExceptions(exceptions);
		
		return schedule;
	}
	
	public static ControlGroupContext getControlGroup(long groupId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<ControlGroupContext> groups = ControlScheduleUtil.fetchRecord(ControlGroupContext.class, CONTROL_GROUP_MODULE_NAME, null,CriteriaAPI.getIdCondition(groupId, modBean.getModule(CONTROL_GROUP_MODULE_NAME)));
				
		ControlGroupContext group = groups.get(0);
		
		group.setControlSchedule(getControlSchedule(group.getControlSchedule().getId()));
		
		List<ControlGroupSection> sections =  ControlScheduleUtil.fetchRecord(ControlGroupSection.class, CONTROL_GROUP_SECTION_MODULE_NAME, null,CriteriaAPI.getCondition("CONTROL_GROUP", "controlGroup", ""+group.getId(), NumberOperators.EQUALS));
		
		group.setSections(sections);
		
		Map<Long,ControlGroupSection> sectionMap = new HashMap<Long, ControlGroupSection>();
		
		for(ControlGroupSection section : sections) {
			sectionMap.put(section.getId(), section);
		}
		
		List<ControlGroupAssetCategory> categories =  ControlScheduleUtil.fetchRecord(ControlGroupAssetCategory.class, CONTROL_GROUP_ASSET_CATEGORY_MODULE_NAME, null,CriteriaAPI.getCondition("CONTROL_GROUP", "controlGroup", ""+group.getId(), NumberOperators.EQUALS));
		
		Map<Long,ControlGroupAssetCategory> categoryMap = new HashMap<Long, ControlGroupAssetCategory>();
		
		for(ControlGroupAssetCategory category : categories) {
			categoryMap.put(category.getId(), category);
			
			ControlGroupSection section = sectionMap.get(category.getControlGroupSection().getId());
			section.addCategory(category);
		}
		
		List<ControlGroupAssetContext> assets =  ControlScheduleUtil.fetchRecord(ControlGroupAssetContext.class, CONTROL_GROUP_ASSET_MODULE_NAME, null,CriteriaAPI.getCondition("CONTROL_GROUP_ASSET_CATEGORY", "controlGroupAssetCategory", StringUtils.join(categoryMap.keySet(), ","), NumberOperators.EQUALS));
		
		Map<Long,ControlGroupAssetContext> assetMap = new HashMap<Long, ControlGroupAssetContext>();
		for(ControlGroupAssetContext asset :assets) {
			ControlGroupAssetCategory category = categoryMap.get(asset.getControlGroupAssetCategory().getId());
			category.addControlAsset(asset);
			assetMap.put(asset.getId(), asset);
		}
		
		Criteria criteria = new Criteria();
		
		criteria.addAndCondition(CriteriaAPI.getCondition("CONTROL_GROUP_ASSET", "controlGroupAsset", StringUtils.join(assetMap.keySet(), ","), NumberOperators.EQUALS));
		criteria.addAndCondition(CriteriaAPI.getCondition("ROUTINE_ID", "routine", null, CommonOperators.IS_EMPTY));
		
		List<ControlGroupFieldContext> fields =  ControlScheduleUtil.fetchRecord(ControlGroupFieldContext.class, CONTROL_GROUP_ASSET_FIELD_MODULE_NAME, criteria,null);
		
		for(ControlGroupFieldContext field :fields) {
			
			ControlGroupAssetContext asset = assetMap.get(field.getControlGroupAsset().getId());
			
			asset.addField(field);
		}
		
		List<ControlGroupRoutineContext> routines =  ControlScheduleUtil.fetchRecord(ControlGroupRoutineContext.class, CONTROL_GROUP_ROUTINE_MODULE_NAME, null,CriteriaAPI.getCondition("CONTROL_GROUP", "controlGroup", ""+group.getId(), NumberOperators.EQUALS));
		
		group.setRoutines(routines);
		
		Map<Long,ControlGroupRoutineContext> routineMap = new HashMap<Long, ControlGroupRoutineContext>();
		for(ControlGroupRoutineContext routine :routines) {
			routineMap.put(routine.getId(), routine);
		}
		
//		if(!routineMap.isEmpty()) {
//			List<ControlGroupFieldContext> routinefields =  ControlScheduleUtil.fetchRecord(ControlGroupFieldContext.class, CONTROL_GROUP_ASSET_FIELD_MODULE_NAME, null,CriteriaAPI.getCondition("ROUTINE_ID", "routine", StringUtils.join(routineMap.keySet(), ","), NumberOperators.EQUALS));
//			
//			for(ControlGroupFieldContext routinefield :routinefields) {
//				
//				ControlGroupRoutineContext routine = routineMap.get(routinefield.getRoutine().getId());
//				routine.addField(routinefield);
//			}
//		}
		
		return group;
	}
	
	
	public static <E> List<E> fetchRecord(Class beanclass,String moduleName,Criteria criteria,Condition condition) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		SelectRecordsBuilder builder = new SelectRecordsBuilder()
				.module(modBean.getModule(moduleName))
				.select(modBean.getAllFields(moduleName))
				.beanClass(beanclass)
				;
		
		if(criteria != null) {
			builder.andCriteria(criteria);
		}
		if(condition != null) {
			builder.andCondition(condition);
		}
		
		return builder.get();
	}
	
	public static <E> void addRecord(String moduleName,List<E> records) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
		InsertRecordBuilder builder = new InsertRecordBuilder()
				.moduleName(moduleName)
				.table(modBean.getModule(moduleName).getTableName())
				.fields(modBean.getAllFields(moduleName));

		builder.addRecords(records);
		
		builder.save();
				
	}
}
