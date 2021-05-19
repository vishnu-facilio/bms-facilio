package com.facilio.bmsconsoleV3.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerIncludeExcludeResourceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class InspectionAPI {

	
	public static List<InspectionTriggerContext> getInspectionTriggerByScheduer(Long schedulerId,boolean fetchRelated) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_TRIGGER));
		
		Condition condition = CriteriaAPI.getCondition(fieldMap.get("scheduleId"), ""+schedulerId,NumberOperators.EQUALS);
		
		return getInspectionTrigger(condition, fetchRelated);
	}
	
	public static List<InspectionTriggerContext> getInspectionTrigger(Condition condition,boolean fetchRelated) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		SelectRecordsBuilder<InspectionTriggerContext> select = new SelectRecordsBuilder<InspectionTriggerContext>()
				.moduleName(FacilioConstants.Inspection.INSPECTION_TRIGGER)
				.beanClass(InspectionTriggerContext.class)
				.select(modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_TRIGGER))
				.andCondition(condition)
				;
		
		List<InspectionTriggerContext> triggers = select.get();
		
		if(triggers != null && !triggers.isEmpty()) {
			if(fetchRelated) {
				fetchInclExcl(triggers);
				fetchScheduleDetails(triggers);
			}
		}
		
		return triggers;
	}

	private static void fetchScheduleDetails(List<InspectionTriggerContext> triggers) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<Long> scheduleIds = triggers.stream().filter((trigger) -> {
				return trigger.getType() == InspectionTriggerContext.TriggerType.SCHEDULE.getVal() ? true : false; 
		}).map(InspectionTriggerContext::getScheduleId).collect(Collectors.toList());
		
		if(scheduleIds != null && !scheduleIds.isEmpty()) {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getBaseSchedulerFields())
					.table(ModuleFactory.getBaseSchedulerModule().getTableName())
					.andCondition(CriteriaAPI.getIdCondition(scheduleIds, ModuleFactory.getBaseSchedulerModule()));
				
			List<Map<String, Object>> props = selectBuilder.get();
			
			List<BaseScheduleContext> baseSchedules = FieldUtil.getAsBeanListFromMapList(props, BaseScheduleContext.class);
			
			Map<Long, List<BaseScheduleContext>> baseScheduleIDMap = baseSchedules.stream().collect(Collectors.groupingBy(BaseScheduleContext::getId));
			
			triggers.forEach((trigger) -> {trigger.setSchedule(baseScheduleIDMap.get(trigger.getScheduleId()).get(0));});
		}
	}

	private static void fetchInclExcl(List<InspectionTriggerContext> triggers) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<Long> ids = triggers.stream().map(InspectionTriggerContext::getId).collect(Collectors.toList());
		
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_TRIGGER_INCL_EXCL);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<InspectionTriggerIncludeExcludeResourceContext> select = new SelectRecordsBuilder<InspectionTriggerIncludeExcludeResourceContext>()
				.moduleName(FacilioConstants.Inspection.INSPECTION_TRIGGER_INCL_EXCL)
				.beanClass(InspectionTriggerIncludeExcludeResourceContext.class)
				.select(modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_TRIGGER_INCL_EXCL))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("inspectionTrigger"), ids, NumberOperators.EQUALS))
				;
		
		List<InspectionTriggerIncludeExcludeResourceContext> inclExcls = select.get();
		
		Map<Long, List<InspectionTriggerIncludeExcludeResourceContext>> groupedInclExcl = inclExcls.stream().collect(Collectors.groupingBy(InspectionTriggerIncludeExcludeResourceContext::getInspectionTriggerId));
		
		triggers.forEach((trigger) -> {trigger.setResInclExclList(groupedInclExcl.get(trigger.getId()));});
	}
}
