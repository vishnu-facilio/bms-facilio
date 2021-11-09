package com.facilio.bmsconsoleV3.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Priority;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsoleV3.context.survey.SurveyResponseContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyTriggerContext;
import com.facilio.bmsconsoleV3.context.survey.SurveyTriggerIncludeExcludeResourceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
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

import lombok.extern.log4j.Log4j;

@Log4j
public class SurveyAPI {

public static List<SurveyTriggerContext> getSurveyTriggerByScheduer(Long schedulerId,boolean fetchRelated) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.Survey.SURVEY_TRIGGER));
		
		Condition condition = CriteriaAPI.getCondition(fieldMap.get("scheduleId"), ""+schedulerId,NumberOperators.EQUALS);
		
		return getSurveyTrigger(condition, fetchRelated);
	}
	
	public static void scheduleResponseCreationJob(List<SurveyTriggerContext> triggers) {
		
		JSONObject props = new JSONObject();
		props.put("saveAsV3", true);
		triggers.stream().forEach((trigger) -> {
			
			if(trigger.getParent().getStatus() && trigger.getType() == SurveyTriggerContext.TriggerType.SCHEDULE.getVal()) {
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
	
	public static void deleteScheduledPreOpenSurveys(List<Long> surveyIds) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		GenericDeleteRecordBuilder delete = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getBaseSchedulerModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", modBean.getModule(FacilioConstants.Survey.SURVEY_TEMPLATE).getModuleId()+"", NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition("RECORD_ID", "recordId", org.apache.commons.lang3.StringUtils.join(surveyIds, ","), NumberOperators.EQUALS));
		
		
		delete.delete();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.Survey.SURVEY_RESPONSE));
		
		DeleteRecordBuilder<SurveyResponseContext> deleteBuilder1 = new DeleteRecordBuilder<SurveyResponseContext>()
				.module(modBean.getModule(FacilioConstants.Survey.SURVEY_RESPONSE))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("status"), SurveyResponseContext.Status.PRE_OPEN.getIndex()+"", EnumOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parent"), surveyIds, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("createdTime"), DateTimeUtil.getCurrenTime()+"", DateOperators.IS_AFTER));
		
		deleteBuilder1.delete();
		
		System.out.println(deleteBuilder1);
	}
	
	public static List<SurveyTriggerContext> getSurveyTrigger(Condition condition,boolean fetchRelated) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		SelectRecordsBuilder<SurveyTriggerContext> select = new SelectRecordsBuilder<SurveyTriggerContext>()
				.moduleName(FacilioConstants.Survey.SURVEY_TRIGGER)
				.beanClass(SurveyTriggerContext.class)
				.select(modBean.getAllFields(FacilioConstants.Survey.SURVEY_TRIGGER))
				.andCondition(condition)
				;
		
		List<SurveyTriggerContext> triggers = select.get();
		
		if(triggers != null && !triggers.isEmpty()) {
			if(fetchRelated) {
				fetchInclExcl(triggers);
				fetchScheduleDetails(triggers);
			}
		}
		
		return triggers;
	}

	private static void fetchScheduleDetails(List<SurveyTriggerContext> triggers) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<Long> scheduleIds = triggers.stream().filter((trigger) -> {
				return trigger.getType() == SurveyTriggerContext.TriggerType.SCHEDULE.getVal() ? true : false; 
		}).map(SurveyTriggerContext::getScheduleId).collect(Collectors.toList());
		
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

	private static void fetchInclExcl(List<SurveyTriggerContext> triggers) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<Long> ids = triggers.stream().map(SurveyTriggerContext::getId).collect(Collectors.toList());
		
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Survey.SURVEY_TRIGGER_INCL_EXCL);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<SurveyTriggerIncludeExcludeResourceContext> select = new SelectRecordsBuilder<SurveyTriggerIncludeExcludeResourceContext>()
				.moduleName(FacilioConstants.Survey.SURVEY_TRIGGER_INCL_EXCL)
				.beanClass(SurveyTriggerIncludeExcludeResourceContext.class)
				.select(modBean.getAllFields(FacilioConstants.Survey.SURVEY_TRIGGER_INCL_EXCL))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("surveyTrigger"), ids, NumberOperators.EQUALS))
				;
		
		List<SurveyTriggerIncludeExcludeResourceContext> inclExcls = select.get();
		
		List<Long> resourceIds = inclExcls.stream().map(SurveyTriggerIncludeExcludeResourceContext::getResourceId).collect(Collectors.toList());
		
		Map<Long, ResourceContext> resourceMap = ResourceAPI.getResourceAsMapFromIds(resourceIds);
		
		for(SurveyTriggerIncludeExcludeResourceContext inclExcl : inclExcls) {
			inclExcl.setResource(resourceMap.get(inclExcl.getResourceId()));
		}
		
		Map<Long, List<SurveyTriggerIncludeExcludeResourceContext>> groupedInclExcl = inclExcls.stream().collect(Collectors.groupingBy(SurveyTriggerIncludeExcludeResourceContext::getSurveyTriggerId));
		
		triggers.forEach((trigger) -> {trigger.setResInclExclList(groupedInclExcl.get(trigger.getId()));});
	}
}
