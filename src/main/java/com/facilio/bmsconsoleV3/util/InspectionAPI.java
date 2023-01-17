package com.facilio.bmsconsoleV3.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.v3.context.Constants;
import org.apache.log4j.Priority;
import org.apache.poi.util.StringUtil;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsoleV3.context.inspection.InspectionResponseContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerIncludeExcludeResourceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.EnumOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.mysql.jdbc.StringUtils;

import lombok.extern.log4j.Log4j;

@Log4j
public class InspectionAPI {

	
	public static List<InspectionTriggerContext> getInspectionTriggerByScheduer(Long schedulerId,boolean fetchRelated) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_TRIGGER));
		
		Condition condition = CriteriaAPI.getCondition(fieldMap.get("scheduleId"), ""+schedulerId,NumberOperators.EQUALS);
		
		return getInspectionTrigger(condition, fetchRelated);
	}
	
	public static void scheduleResponseCreationJob(List<InspectionTriggerContext> triggers) {
		
		JSONObject props = new JSONObject();
		props.put("saveAsV3PreCreate", true);
		triggers.stream().forEach((trigger) -> {
			
			if(trigger.getParent().getStatus() && trigger.getType() == InspectionTriggerContext.TriggerType.SCHEDULE.getVal()) {
				BaseScheduleContext baseSchedule = trigger.getSchedule();
				
				try {
					BmsJobUtil.deleteJobWithProps(baseSchedule.getId(), "BaseSchedulerSingleInstanceJob");
					
					FacilioTimer.scheduleOneTimeJobWithDelay(baseSchedule.getId(), "BaseSchedulerSingleInstanceJob", 10, "priority");
					BmsJobUtil.addJobProps(baseSchedule.getId(), "BaseSchedulerSingleInstanceJob", props);
					
				} catch (Exception e) {
					LOGGER.log(Priority.ERROR, e.getMessage(), e);
				}
			}
		});
	}
	
	public static void deleteScheduledPreOpenInspections(List<Long> inspectionIds) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_RESPONSE));

		SelectRecordsBuilder<InspectionResponseContext> selectBuilder = new SelectRecordsBuilder<InspectionResponseContext>()
				.module(modBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE))
				.select(modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_RESPONSE))
				.beanClass(InspectionResponseContext.class)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), InspectionResponseContext.Status.PRE_OPEN.getIndex()+"", EnumOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parent"), inspectionIds, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), DateTimeUtil.getCurrenTime()+"", DateOperators.IS_AFTER))
				.skipModuleCriteria();

		SelectRecordsBuilder.BatchResult<InspectionResponseContext> batches = selectBuilder.getInBatches("Inspection_Responses.ID", 5000);

		int i=0;
		while (batches.hasNext()) {
			LOGGER.info("Fetching pre open inspection Batch wise, Batch ID == "+i++);
			List<InspectionResponseContext> props = batches.get();
			List<Long> ids = props.stream().map(InspectionResponseContext::getId).collect(Collectors.toList());
			LOGGER.info("Pre open inspection Id's to be deleted === "+ids);

			DeleteRecordBuilder<InspectionResponseContext> deleteBuilder1 = new DeleteRecordBuilder<InspectionResponseContext>()
					.module(modBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE))
					.andCondition(CriteriaAPI.getIdCondition(ids, modBean.getModule(FacilioConstants.Inspection.INSPECTION_RESPONSE)))
					.skipModuleCriteria();

			int countOfDeletedRecords= deleteBuilder1.delete();

			LOGGER.info("COUNT OF DELETED PRE OPEN INSPECTIONS ===  "+countOfDeletedRecords + " for template ID === "+inspectionIds);
		}

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
			
			triggers.stream().filter((trigger) -> {
				return trigger.getType() == InspectionTriggerContext.TriggerType.SCHEDULE.getVal() ? true : false; 
			}).forEach((trigger) -> {trigger.setSchedule(baseScheduleIDMap.get(trigger.getScheduleId()).get(0));});
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
		
		List<Long> resourceIds = inclExcls.stream().map(InspectionTriggerIncludeExcludeResourceContext::getResourceId).collect(Collectors.toList());
		
		Map<Long, ResourceContext> resourceMap = ResourceAPI.getResourceAsMapFromIds(resourceIds);
		
		for(InspectionTriggerIncludeExcludeResourceContext inclExcl : inclExcls) {
			inclExcl.setResource(resourceMap.get(inclExcl.getResourceId()));
		}
		
		Map<Long, List<InspectionTriggerIncludeExcludeResourceContext>> groupedInclExcl = inclExcls.stream().collect(Collectors.groupingBy(InspectionTriggerIncludeExcludeResourceContext::getInspectionTriggerId));
		
		triggers.forEach((trigger) -> {trigger.setResInclExclList(groupedInclExcl.get(trigger.getId()));});
	}
}
