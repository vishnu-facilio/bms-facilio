package com.facilio.control.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ControlActionMode;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingType;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.tenant.TenantSpaceContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.BusinessHoursAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlGroupAssetCategory;
import com.facilio.control.ControlGroupAssetContext;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlGroupFieldContext;
import com.facilio.control.ControlGroupRoutineContext;
import com.facilio.control.ControlGroupRoutineSectionContext;
import com.facilio.control.ControlGroupSection;
import com.facilio.control.ControlGroupTenentContext;
import com.facilio.control.ControlScheduleContext;
import com.facilio.control.ControlScheduleExceptionContext;
import com.facilio.control.ControlScheduleExceptionTenantContext;
import com.facilio.control.ControlScheduleGroupedSlot;
import com.facilio.control.ControlScheduleSlot;
import com.facilio.control.ControlScheduleTenantContext;
import com.facilio.control.ControlScheduleSlot.ControlScheduleSlotWrapperForDisplay;
import com.facilio.control.ControlScheduleSlot.ControlScheduleWrapper;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BuildingOperator;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class ControlScheduleUtil {
	
	private static final Logger LOGGER = LogManager.getLogger(ControlScheduleUtil.class.getName());

	public static final String CONTROL_SCHEDULE_MODULE_NAME = "controlSchedule";
	public static final String CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME = "controlScheduleException";
	public static final String CONTROL_GROUP_MODULE_NAME = "controlGroupv2";
	public static final String CONTROL_GROUP_SECTION_MODULE_NAME = "controlGroupSection";
	public static final String CONTROL_GROUP_ASSET_CATEGORY_MODULE_NAME = "controlGroupAssetCategory";
	public static final String CONTROL_GROUP_ASSET_MODULE_NAME = "controlGroupAsset";
	public static final String CONTROL_GROUP_ASSET_FIELD_MODULE_NAME = "controlGroupField";
	public static final String CONTROL_GROUP_ROUTINE_MODULE_NAME = "controlGroupRoutine";
	public static final String CONTROL_GROUP_ROUTINE_SECTION_MODULE_NAME = "controlGroupRoutineSection";
	public static final String CONTROL_GROUP_TENANT_SHARING_MODULE_NAME = "controlGroupv2TenantSharing";
	public static final String CONTROL_SCHEDULE_TENANT_SHARING_MODULE_NAME = "controlScheduleTenant";
	public static final String CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME = "controlScheduleExceptionTenant";
	
	public static final String CONTROL_SCHEDULE_UNPLANNED_SLOTS_MODULE_NAME = "controlScheduleSlots";
	public static final String CONTROL_SCHEDULE_PLANNED_SLOTS_MODULE_NAME = "controlScheduleGroupedSlots";
	
	public static final String CONTROL_SCHEDULE_EXCEPTION_CONTEXT = "controlScheduleExceptionContext";
	public static final String CONTROL_SCHEDULE_CONTEXT = "controlScheduleContext";
	public static final String CONTROL_GROUP_CONTEXT = "controlGroupContext";
	public static final String CONTROL_GROUP_ID = "controlGroupId";
	public static final String CONTROL_GROUP_CONTEXT_OLD = "controlGroupContextOld";
	public static final String CONTROL_GROUP_ROUTINE_CONTEXT = "controlGroupRoutineContext";
	
	public static final String CONTROL_GROUP_UNPLANNED_SLOTS = "controlGroupUnplanedSlots";
	public static final String CONTROL_GROUP_PLANNED_SLOTS = "controlGroupplanedSlots";
	
	
	public static void planAssetsForTenant(ControlGroupTenentContext groupTenent) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TENANT_SPACES);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<TenantSpaceContext> select1 = new SelectRecordsBuilder<TenantSpaceContext>()
				.select(fields)
				.moduleName(FacilioConstants.ContextNames.TENANT_SPACES)
				.beanClass(TenantSpaceContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("tenant"), groupTenent.getTenant().getId()+"", NumberOperators.EQUALS));
		
		List<TenantSpaceContext> tenantSpaces = select1.get();
		
		List<Long> tenantSpaceList = new ArrayList<Long>();
		
		
		for(TenantSpaceContext tenantSpace :tenantSpaces) {
			tenantSpaceList.add(tenantSpace.getSpace().getId());
		}
		
		fields = modBean.getAllFields(FacilioConstants.ContextNames.ASSET);
		
		fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<AssetContext> select = new SelectRecordsBuilder<AssetContext>()
				.select(fields)
				.beanClass(AssetContext.class)
				.moduleName(FacilioConstants.ContextNames.ASSET)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("space"), StringUtils.join(tenantSpaceList, ","), BuildingOperator.BUILDING_IS));
		
		List<AssetContext> assets = select.get();
		
		if(assets == null || assets.isEmpty()) {
			throw new Exception("This tenant has no assets associated");
		}
		List<Long> accesableAssetIds = new ArrayList<Long>(); 
		for(AssetContext asset :assets) {
			accesableAssetIds.add(asset.getId());
		}
		
		List<ControlGroupSection> newSectionList = new ArrayList<ControlGroupSection>();
		
		List<ControlGroupAssetContext> assetListToBeMarked = new ArrayList<ControlGroupAssetContext>();
		
		for(ControlGroupSection section : groupTenent.getSections()) {
			
			List<ControlGroupAssetCategory> newAssetCategoryList = new ArrayList<ControlGroupAssetCategory>();
			
			for(ControlGroupAssetCategory category : section.getCategories()) {
				
				List<ControlGroupAssetContext> newAssetList = new ArrayList<ControlGroupAssetContext>();
				for(ControlGroupAssetContext asset : category.getControlAssets()) {
				
					if(accesableAssetIds.contains(asset.getAsset().getId())) {
						
						assetListToBeMarked.add(asset);
						
						newAssetList.add(asset);
					}
				}
				
				if(!newAssetList.isEmpty()) {
					category.setControlAssets(newAssetList);
					newAssetCategoryList.add(category);
				}
			}
			
			if(!newAssetCategoryList.isEmpty()) {
				section.setCategories(newAssetCategoryList);
				
				newSectionList.add(section);
			}
		}
		
		if(newSectionList.isEmpty()) {
			throw new Exception("No asset matched with current group");
		}
		
		groupTenent.setSections(newSectionList);
		
		if(!assetListToBeMarked.isEmpty()) {
			
			Map<String,Object> updateMap = new HashMap<String, Object>();
			
			updateMap.put("status", ControlGroupAssetContext.Status.CONTROL_PASSED_TO_CHILD.getIntVal());
			
			for(ControlGroupAssetContext asset : assetListToBeMarked) {
				
				UpdateRecordBuilder<ControlGroupAssetContext> update = new UpdateRecordBuilder<ControlGroupAssetContext>()
						.fields(modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_ASSET_MODULE_NAME))
						.moduleName(ControlScheduleUtil.CONTROL_GROUP_ASSET_MODULE_NAME)
						.andCondition(CriteriaAPI.getIdCondition(asset.getId(), modBean.getModule(ControlScheduleUtil.CONTROL_GROUP_ASSET_MODULE_NAME)));
						
				update.updateViaMap(updateMap);
				
			}
		}
		
	}
	
	public static ControlScheduleTenantContext controlScheduleToControlScheduleTenantShared(ControlScheduleContext schedule,TenantContext tenant,ControlGroupContext group) throws Exception {
		
		ControlScheduleTenantContext scheduleTenent = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(schedule), ControlScheduleTenantContext.class);
		
		scheduleTenent.setTenant(tenant);
		scheduleTenent.setParentGroup(group);
		scheduleTenent.setParentSchedule(schedule);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		InsertRecordBuilder<ControlScheduleTenantContext> insert = new InsertRecordBuilder<ControlScheduleTenantContext>()
				.moduleName(ControlScheduleUtil.CONTROL_SCHEDULE_TENANT_SHARING_MODULE_NAME)
				.fields(modBean.getAllFields(ControlScheduleUtil.CONTROL_SCHEDULE_TENANT_SHARING_MODULE_NAME))
				.addRecord(scheduleTenent);
		
		insert.save();
		
		InsertRecordBuilder<ControlScheduleExceptionTenantContext> insert1 = new InsertRecordBuilder<ControlScheduleExceptionTenantContext>()
				.moduleName(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME)
				.fields(modBean.getAllFields(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME));
		
		List<ControlScheduleExceptionTenantContext> exceptionTenantList = new ArrayList<ControlScheduleExceptionTenantContext>();
		for(ControlScheduleExceptionContext exception : schedule.getExceptions()) {
			
			ControlScheduleExceptionTenantContext exceptionTenant = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(exception), ControlScheduleExceptionTenantContext.class);
			
			exceptionTenant.setParentException(exception);
			exceptionTenant.setTenant(tenant);
			exceptionTenant.setParentGroup(group);
			
			insert1.addRecord(exceptionTenant);
			exceptionTenantList.add(exceptionTenant);
		}
		
		insert1.save();
		
		scheduleTenent.setExceptions(null);
		for(ControlScheduleExceptionTenantContext exceptionTenant :exceptionTenantList) {
			scheduleTenent.addException(exceptionTenant);
		}
		
		FacilioChain chain = TransactionChainFactoryV3.getDeleteAndAddControlScheduleExceptionChain();
		
		FacilioContext newcontext = chain.getContext();
		
		Map<String, List<ControlScheduleContext>> map = Collections.singletonMap(ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME, Collections.singletonList(scheduleTenent));
		
		newcontext.put(FacilioConstants.ContextNames.RECORD_MAP, map);
		
		chain.execute();
		
		scheduleTenent.setExceptions(null);					
		scheduleTenent.setParentGroup(null);
		
		return scheduleTenent;
	}
	
	public static void deleteSlotsAndGroupedSlots(ControlGroupContext controlGroup, long startTime, long endTime) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> slotFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_SCHEDULE_UNPLANNED_SLOTS_MODULE_NAME);
		
		Map<String, FacilioField> slotFieldMap = FieldFactory.getAsMap(slotFields);
		
		DeleteRecordBuilder<ControlScheduleSlot> delete = new  DeleteRecordBuilder<ControlScheduleSlot>()
				.moduleName(ControlScheduleUtil.CONTROL_SCHEDULE_UNPLANNED_SLOTS_MODULE_NAME)
				.andCondition(CriteriaAPI.getCondition(slotFieldMap.get("group"), controlGroup.getId()+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(slotFieldMap.get("startTime"), startTime+"", DateOperators.IS_AFTER))
				.andCondition(CriteriaAPI.getCondition(slotFieldMap.get("endTime"), endTime+"", DateOperators.IS_BEFORE))
				.andCustomWhere("SYS_CREATED_TIME = SYS_MODIFIED_TIME");
				;
		
		delete.delete();
		
		deleteDeleteGroupedSlotAndCommands(controlGroup, startTime, endTime);
	}
	
	public static void deleteDeleteGroupedSlotAndCommands(ControlGroupContext controlGroup, long startTime, long endTime) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> groupedSlotFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_SCHEDULE_PLANNED_SLOTS_MODULE_NAME);
		
		Map<String, FacilioField> groupedSlotFieldMap = FieldFactory.getAsMap(groupedSlotFields);
				
		DeleteRecordBuilder<ControlScheduleGroupedSlot> delete1 = new  DeleteRecordBuilder<ControlScheduleGroupedSlot>()
				.moduleName(ControlScheduleUtil.CONTROL_SCHEDULE_PLANNED_SLOTS_MODULE_NAME)
				.andCondition(CriteriaAPI.getCondition(groupedSlotFieldMap.get("group"), controlGroup.getId()+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(groupedSlotFieldMap.get("startTime"), startTime+"", DateOperators.IS_AFTER))
				.andCondition(CriteriaAPI.getCondition(groupedSlotFieldMap.get("endTime"), endTime+"", DateOperators.IS_BEFORE))
				;
		
		delete1.delete();
		
		List<FacilioField> controlCommandFields = modBean.getAllFields(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE);
		
		Map<String, FacilioField> controlCommandFieldMap = FieldFactory.getAsMap(controlCommandFields);
		
		DeleteRecordBuilder<ControlActionCommandContext> delete2 = new  DeleteRecordBuilder<ControlActionCommandContext>()
				.moduleName(FacilioConstants.ContextNames.CONTROL_ACTION_COMMAND_MODULE)
				.andCondition(CriteriaAPI.getCondition(controlCommandFieldMap.get("group"), controlGroup.getId()+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(controlCommandFieldMap.get("executedTime"), startTime+"", DateOperators.IS_AFTER))
				.andCondition(CriteriaAPI.getCondition(controlCommandFieldMap.get("executedTime"), endTime+"", DateOperators.IS_BEFORE))
				;
		
		delete2.delete();
	}
	
	public static List<ControlActionCommandContext> planCommandsForRoutines(List<ControlScheduleSlot> slots) throws Exception {
		
		List<ControlActionCommandContext> commands = new ArrayList<ControlActionCommandContext>();
		
		for(ControlScheduleSlot slot : slots) {
			if(slot.getRoutine() != null) {
				List<ControlGroupFieldContext> controlFields = new ArrayList<>();
				List<ControlGroupRoutineSectionContext> sections = slot.getRoutine().getSections();
				
				for(ControlGroupRoutineSectionContext section :sections) {
					controlFields.addAll(section.getFields());
				}
				
				for(ControlGroupFieldContext controlField : controlFields) {
					ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(controlField.getControlGroupAsset().getAsset().getId(), controlField.getField());
					boolean isControllableField = false;
					if(rdm != null && rdm.isControllable() && rdm.getReadingTypeEnum() == ReadingType.WRITE) {
						isControllableField = true;
					}
					ControlActionCommandContext.Status status = isControllableField == true ? ControlActionCommandContext.Status.SCHEDULED : ControlActionCommandContext.Status.SCHEDULED_WITH_NO_PERMISSION;
					
					commands.add(new ControlActionCommandContext(controlField.getControlGroupAsset().getAsset(), controlField.getFieldId(), controlField.getTrueVal(),slot.getStartTime(),slot.getGroup(),slot.getRoutine(),status,rdm));
				}
			}
		}
		return commands;
	}
	
	public static List<ControlActionCommandContext> planCommandsForSchedules(List<ControlScheduleGroupedSlot> slots,ControlGroupContext group) throws Exception {
		
		List<ControlActionCommandContext> commands = new ArrayList<ControlActionCommandContext>();
		
		for(ControlScheduleGroupedSlot slot : slots) {
			
			group.getSections();
			
			for(ControlGroupSection section : group.getSections()) {
				
				List<ControlGroupAssetCategory> categories = section.getCategories();
				
				for(ControlGroupAssetCategory category : categories) {
					
					if(category.getControlAssets() != null) {
						
						for(ControlGroupAssetContext controlAsset  : category.getControlAssets()) {
							
							if(controlAsset.getStatusEnum() != ControlGroupAssetContext.Status.CONTROL_PASSED_TO_CHILD && controlAsset.getControlFields() != null) {
								for(ControlGroupFieldContext controlField :  controlAsset.getControlFields()) {
									
									ReadingDataMeta rdm = ReadingsAPI.getReadingDataMeta(controlAsset.getAsset().getId(), controlField.getField());
									boolean isControllableField = false;
									if(rdm != null && rdm.isControllable() && rdm.getReadingTypeEnum() == ReadingType.WRITE) {
										isControllableField = true;
									}
									ControlActionCommandContext.Status status = isControllableField == true ? ControlActionCommandContext.Status.SCHEDULED : ControlActionCommandContext.Status.SCHEDULED_WITH_NO_PERMISSION;  
									if(slot.getStartTime() > 0 && controlField.getTrueVal() != null) {
										commands.add(new ControlActionCommandContext(controlAsset.getAsset(), controlField.getFieldId(), controlField.getTrueVal(),slot.getStartTime(),group,status,rdm));
									}
									if(slot.getEndTime() > 0 && controlField.getFalseVal() != null) {
										commands.add(new ControlActionCommandContext(controlAsset.getAsset(), controlField.getFieldId(), controlField.getFalseVal(),slot.getEndTime(),group,status,rdm));
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
	
	public static List<ControlScheduleSlot> slotsForDisplay(List<ControlScheduleSlot> controlSlots) {
		
		List<ControlScheduleSlot> slots = new ArrayList<ControlScheduleSlot>(); 
		
		ControlScheduleSlot temp = null;
		
		for(ControlScheduleSlot controlSlot : controlSlots) {
			
			if(temp == null) {
				temp = controlSlot;
			}
			else {
				if(temp.isTouching(controlSlot)) {
					ControlScheduleSlotWrapperForDisplay slotForDisplay = temp.mergeForDisplay(controlSlot);
					slots.addAll(slotForDisplay.getAsList());
					ControlScheduleSlot lastBlock = slotForDisplay.getLast();
					if(lastBlock.getException() == null) {
						temp = lastBlock;
						slots.remove(slots.size()-1);
					}
					else {
						temp = null;
					}
				}
				else {
					slots.add(temp);
					temp = controlSlot;
				}
			}
		}
		
		if(temp != null) {
			slots.add(temp);
		}
		
		return slots;
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
				
				Map<Long, ControlScheduleSlot> timeVsSlotMap = fetchAlreadyEditedSlot(group,exception);
				
				for(DateRange dateRange :dateRanges) {
					
					if(timeVsSlotMap.get(DateTimeUtil.getDayStartTimeOf(dateRange.getStartTime())) == null) {
						
						if(exception.getExcludeSchedule()) {
							long exceptionDate = DateTimeUtil.getDayStartTimeOf(dateRange.getStartTime());
							DateRange businessHour = startTimeVsBusinessHoursRangeMap.get(exceptionDate);
							if(!businessHoursToBeRemoved.contains(businessHour)) {
								businessHoursToBeRemoved.add(businessHour);
							}
						}
						
						slots.add(new ControlScheduleSlot(group,schedule,exception, dateRange.getStartTime(), dateRange.getEndTime()));
					}
					
				}
				
				slots.addAll(timeVsSlotMap.values());
				
			}
			businessHourRanges.removeAll(businessHoursToBeRemoved);
		}
		
		for(DateRange businessHourRange :businessHourRanges) {
			slots.add(new ControlScheduleSlot(group,schedule, businessHourRange.getStartTime(), businessHourRange.getEndTime()));
		}
		
		if(routines != null) {
			for(ControlGroupRoutineContext routine : routines) {
				List<Long> routineTimes = routine.scheduleAsObj().nextExecutionTimes(startTime, endTime);
				for(Long routineTime :routineTimes) {
					slots.add(new ControlScheduleSlot(group,routine, routineTime));
				}
			}
		}
		
		
		
		return slots;
	}
	
	private static Map<Long,ControlScheduleSlot> fetchAlreadyEditedSlot(ControlGroupContext group, ControlScheduleExceptionContext exception) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> fields = modBean.getAllFields(ControlScheduleUtil.CONTROL_SCHEDULE_UNPLANNED_SLOTS_MODULE_NAME);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		Criteria criteria = new Criteria();
		
		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("group"), group.getId()+"", NumberOperators.EQUALS));
		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("exception"), exception.getId()+"", NumberOperators.EQUALS));
		
		SelectRecordsBuilder<ControlScheduleSlot> builder = new SelectRecordsBuilder<ControlScheduleSlot>()
				.module(modBean.getModule(ControlScheduleUtil.CONTROL_SCHEDULE_UNPLANNED_SLOTS_MODULE_NAME))
				.select(fields)
				.beanClass(ControlScheduleSlot.class)
				.andCriteria(criteria)
				.andCustomWhere("SYS_CREATED_TIME != SYS_MODIFIED_TIME");
				;
		
		
		List<ControlScheduleSlot> res = builder.get();
		
		Map<Long,ControlScheduleSlot> timeVsSlotMap = new HashMap<Long, ControlScheduleSlot>();
		
		for(ControlScheduleSlot slot :res) {
			
			timeVsSlotMap.put(DateTimeUtil.getDayStartTimeOf(slot.getStartTime()), slot);
		}
		
		return timeVsSlotMap;
		
	}

	public static List<ControlScheduleGroupedSlot> mergeSlotsForIgnoreNormalScheduleMode(List<ControlScheduleSlot> controlSlots) {					//controlSlots should sorted by startTime
		
		List<ControlScheduleGroupedSlot> groupedSlots = new ArrayList<ControlScheduleGroupedSlot>();
		
		ControlScheduleSlot temp = null;
		
		for(ControlScheduleSlot controlSlot :controlSlots) {
			
			if(temp == null) {
				temp = controlSlot;
			}
			else {
				
				if(temp.isTouching(controlSlot)) {
					temp = mergeForIgnoreNormalScheduleMode(temp, controlSlot);
					if(temp != null) {
						groupedSlots.add(new ControlScheduleGroupedSlot(temp));
					}
					if(controlSlot.getException() != null) {
						temp = null;
					}
					else {
						temp = controlSlot;
					}
				}
				else {
					if(temp.getException() != null) {
						groupedSlots.add(new ControlScheduleGroupedSlot(temp));
					}
					temp = controlSlot;
				}
			}
		}
		
		return groupedSlots;
//		
	}
	
	private static ControlScheduleSlot mergeForIgnoreNormalScheduleMode(ControlScheduleSlot first,ControlScheduleSlot second) {
		if(first.getException() != null) {
			if(first.getOffSchedule()) {
				if(first.getEndTime() > second.getStartTime() && first.getEndTime() < second.getEndTime()) {
					
					first.setStartTime(first.getEndTime());
					first.setEndTime(second.getStartTime());
					
					return first;
				}
				else {
					first.setEndTime(second.getStartTime());
					first.setStartTime(null);
					return first;
				}
			}
			else {
				if(first.getEndTime() > second.getStartTime() && first.getEndTime() < second.getEndTime()) {
					
					first.setEndTime(null);
					return first;
				}
				else {
					return null; 	//excluding this case
				}
			}
		}
		else {
			if(second.getOffSchedule()) {
				if(second.getEndTime() >= first.getEndTime()) {
					second.setEndTime(second.getStartTime());
					second.setStartTime(null);
					return second;
				}
				else {
					Long temp = second.getStartTime();
					second.setStartTime(second.getEndTime());
					second.setEndTime(temp);
					return second;
				}
			}
			else {
				if(second.getEndTime() > first.getEndTime()) {
					second.setStartTime(first.getEndTime());
					return second;
				}
				else {
					return null;
				}
			}
		}
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
					ControlScheduleWrapper wrapper = temp.mergeONAndOff(controlSlot);
					if(wrapper != null) {
						if(wrapper.getTrimed() != null && wrapper.getPrevious() != null) {
							groupedSlots.add(new ControlScheduleGroupedSlot(wrapper.getPrevious()));
						}
						temp = wrapper.getTrimed();
					}
					
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
		
		return getControlSchedule(scheduleId, CONTROL_SCHEDULE_MODULE_NAME);
	}
	
	public static ControlScheduleContext getControlSchedule(long scheduleId,String moduleName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		ControlScheduleContext schedule = (ControlScheduleContext) ControlScheduleUtil.fetchRecord(ControlScheduleContext.class, moduleName, null,CriteriaAPI.getIdCondition(scheduleId, modBean.getModule(moduleName))).get(0);
		
		schedule.setBusinessHoursContext(BusinessHoursAPI.getBusinessHours(Collections.singletonList(schedule.getBusinessHour())).get(0));
		
		String exceptionModuleName = null;
		if(moduleName.equals(CONTROL_SCHEDULE_MODULE_NAME)) {
			exceptionModuleName = CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME;
		}
		else if (moduleName.equals(CONTROL_SCHEDULE_TENANT_SHARING_MODULE_NAME)) {
			exceptionModuleName = CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME;
		}
		
		FacilioModule exceptionModule = modBean.getModule(exceptionModuleName);
		
		SelectRecordsBuilder<ControlScheduleExceptionContext> select = new SelectRecordsBuilder<ControlScheduleExceptionContext>()
				.moduleName(exceptionModuleName)
				.beanClass(ControlScheduleExceptionContext.class)
				.select(modBean.getAllFields(exceptionModuleName))
				.innerJoin(ModuleFactory.getControlScheduleVsExceptionModule().getTableName())
				.on(ModuleFactory.getControlScheduleVsExceptionModule().getTableName()+".EXCEPTION_ID = "+exceptionModule.getTableName()+".ID")
				.andCustomWhere(ModuleFactory.getControlScheduleVsExceptionModule().getTableName()+".SCHEDULE_ID = ?", scheduleId);
		
		List<ControlScheduleExceptionContext> exceptions = select.get();
		
		schedule.setExceptions(exceptions);

		return schedule;
	}
	
	public static ControlGroupContext getControlGroup(long groupId) throws Exception {
		
		return getControlGroup(groupId,CONTROL_GROUP_MODULE_NAME);
	}
	
	public static ControlGroupContext getControlGroup(long groupId,String moduleName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		String controlScheduleModuleName = null;
		ControlGroupContext group = null;
		if(moduleName.equals(CONTROL_GROUP_MODULE_NAME)) {
			controlScheduleModuleName = CONTROL_SCHEDULE_MODULE_NAME;
			
			List<ControlGroupContext> groups = ControlScheduleUtil.fetchRecord(ControlGroupContext.class, moduleName, null,CriteriaAPI.getIdCondition(groupId, modBean.getModule(moduleName)));
			
			group = groups.get(0);
		}
		else if (moduleName.equals(CONTROL_GROUP_TENANT_SHARING_MODULE_NAME)) {
			controlScheduleModuleName = CONTROL_SCHEDULE_TENANT_SHARING_MODULE_NAME;
			
			List<ControlGroupTenentContext> groups = ControlScheduleUtil.fetchRecord(ControlGroupTenentContext.class, moduleName, null,CriteriaAPI.getIdCondition(groupId, modBean.getModule(moduleName)));
			
			group = groups.get(0);
		}
		
		group.setControlSchedule(getControlSchedule(group.getControlSchedule().getId(),controlScheduleModuleName));
		
		List<ControlGroupSection> sections =  ControlScheduleUtil.fetchRecord(ControlGroupSection.class, CONTROL_GROUP_SECTION_MODULE_NAME, null,CriteriaAPI.getCondition("CONTROL_GROUP", "controlGroup", ""+group.getId(), NumberOperators.EQUALS));
		
		group.setSections(sections);
		
		Map<Long,ControlGroupSection> sectionMap = new HashMap<Long, ControlGroupSection>();
		
		for(ControlGroupSection section : sections) {
			sectionMap.put(section.getId(), section);
		}
		
		List<ControlGroupAssetCategory> categories =  ControlScheduleUtil.fetchRecord(ControlGroupAssetCategory.class, CONTROL_GROUP_ASSET_CATEGORY_MODULE_NAME, null,CriteriaAPI.getCondition("CONTROL_GROUP", "controlGroup", ""+group.getId(), NumberOperators.EQUALS));
		
		Map<Long,ControlGroupAssetCategory> categoryMap = new HashMap<Long, ControlGroupAssetCategory>();
		
		for(ControlGroupAssetCategory category : categories) {
			
			category.setAssetCategory(AssetsAPI.getCategoryForAsset(category.getAssetCategory().getId()));
			categoryMap.put(category.getId(), category);
			
			ControlGroupSection section = sectionMap.get(category.getControlGroupSection().getId());
			section.addCategory(category);
		}
		
		List<ControlGroupAssetContext> assets =  ControlScheduleUtil.fetchRecord(ControlGroupAssetContext.class, CONTROL_GROUP_ASSET_MODULE_NAME, null,CriteriaAPI.getCondition("CONTROL_GROUP_ASSET_CATEGORY", "controlGroupAssetCategory", StringUtils.join(categoryMap.keySet(), ","), NumberOperators.EQUALS));
		
		Map<Long,ControlGroupAssetContext> assetMap = new HashMap<Long, ControlGroupAssetContext>();
		for(ControlGroupAssetContext asset :assets) {
			
			asset.setAsset(ResourceAPI.getResource(asset.getAsset().getId()));
			ControlGroupAssetCategory category = categoryMap.get(asset.getControlGroupAssetCategory().getId());
			category.addControlAsset(asset);
			assetMap.put(asset.getId(), asset);
		}
		
		Criteria criteria = new Criteria();
		
		criteria.addAndCondition(CriteriaAPI.getCondition("CONTROL_GROUP_ASSET", "controlGroupAsset", StringUtils.join(assetMap.keySet(), ","), NumberOperators.EQUALS));
		criteria.addAndCondition(CriteriaAPI.getCondition("ROUTINE_ID", "routine", null, CommonOperators.IS_EMPTY));
		
		List<ControlGroupFieldContext> fields =  ControlScheduleUtil.fetchRecord(ControlGroupFieldContext.class, CONTROL_GROUP_ASSET_FIELD_MODULE_NAME, criteria,null);
		
		for(ControlGroupFieldContext field :fields) {
			
			field.setField(modBean.getField(field.getFieldId()));
			ControlGroupAssetContext asset = assetMap.get(field.getControlGroupAsset().getId());
			
			asset.addField(field);
		}
		
		List<ControlGroupRoutineContext> routines =  ControlScheduleUtil.fetchRecord(ControlGroupRoutineContext.class, CONTROL_GROUP_ROUTINE_MODULE_NAME, null,CriteriaAPI.getCondition("CONTROL_GROUP", "controlGroup", ""+group.getId(), NumberOperators.EQUALS));
		
		group.setRoutines(routines);
		
		Map<Long,ControlGroupRoutineContext> routineMap = new HashMap<Long, ControlGroupRoutineContext>();
		for(ControlGroupRoutineContext routine :routines) {
			routineMap.put(routine.getId(), routine);
		}
		
		if(!routineMap.isEmpty()) {
			List<ControlGroupRoutineSectionContext> routineSections =  ControlScheduleUtil.fetchRecord(ControlGroupRoutineSectionContext.class, CONTROL_GROUP_ROUTINE_SECTION_MODULE_NAME, null,CriteriaAPI.getCondition("CONTROL_ROUTINE", "routine", StringUtils.join(routineMap.keySet(), ","), NumberOperators.EQUALS));
			
			Map<Long,ControlGroupRoutineSectionContext> routineSectionMap = new HashMap<Long, ControlGroupRoutineSectionContext>();
			for(ControlGroupRoutineSectionContext routineSection : routineSections) {
				
				ControlGroupRoutineContext routine = routineMap.get(routineSection.getRoutine().getId());
				routine.addSection(routineSection);
				routineSectionMap.put(routineSection.getId(), routineSection);
			}
			
			List<ControlGroupFieldContext> routineSectionsFields =  ControlScheduleUtil.fetchRecord(ControlGroupFieldContext.class, CONTROL_GROUP_ASSET_FIELD_MODULE_NAME, null,CriteriaAPI.getCondition("ROUTINE_ID", "routine", StringUtils.join(routineMap.keySet(), ","), NumberOperators.EQUALS));
			
			for(ControlGroupFieldContext routineSectionsField : routineSectionsFields) {
				ControlGroupRoutineSectionContext section = routineSectionMap.get(routineSectionsField.getRoutineSection().getId());
				section.addField(routineSectionsField);
			}
		}
		
		return group;
	}
	
	public static Object getObjectFromRecordMap(Context context,String moduleName) {
		List<Object> objectlist = (List<Object>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get(moduleName));
		return objectlist.get(0);
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
		
		List res = builder.get();
		
		return res;
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
