package com.facilio.bmsconsoleV3.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseScheduleContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.induction.InductionResponseContext;
import com.facilio.bmsconsoleV3.context.induction.InductionTemplateContext;
import com.facilio.bmsconsoleV3.context.induction.InductionTriggerContext;
import com.facilio.bmsconsoleV3.context.induction.InductionTriggerIncludeExcludeResourceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.SupplementRecord;

public class InductionAPI {
	
	
	public static List<InductionResponseContext> getInductionResponse(InductionTemplateContext template,PeopleContext people) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<SiteContext> sites = null;
		if(template.getSiteApplyTo()) {
			sites = SpaceAPI.getAllSites();
		}
		else {
			if(template.getSites() != null) {
				sites = template.getSites();
			}
			else {
				InductionTemplateContext template1 = getInductionTemplate(CriteriaAPI.getIdCondition(template.getId(), modBean.getModule(FacilioConstants.Induction.INDUCTION_TEMPLATE)));
				sites = template1.getSites();
			}
		}
		
		List<InductionResponseContext> responses = new ArrayList<InductionResponseContext>();
		if(sites != null) {
			
			for(SiteContext site : sites) {
				InductionResponseContext response = template.constructResponse();
				
				response.setAssignedTo(people);
				response.setSiteId(site.getId());
				
				responses.add(response);
			}
		}
		
		return responses;
	}
	
	public static InductionTemplateContext getInductionTemplate(Condition condition) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.Induction.INDUCTION_TEMPLATE));
		
		SelectRecordsBuilder<InductionTemplateContext> select = new SelectRecordsBuilder<InductionTemplateContext>()
				.moduleName(FacilioConstants.Induction.INDUCTION_TEMPLATE)
				.beanClass(InductionTemplateContext.class)
				.select(modBean.getAllFields(FacilioConstants.Induction.INDUCTION_TEMPLATE))
				.andCondition(condition)
				.fetchSupplement((SupplementRecord)fieldMap.get("sites"))
				;
		
		InductionTemplateContext template = select.fetchFirst();
		
		return template;
	}
	
	public static void addInductionResponses(List<InductionResponseContext> responses) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		V3RecordAPI.addRecord(false, responses, modBean.getModule(FacilioConstants.Induction.INDUCTION_RESPONSE), modBean.getAllFields(FacilioConstants.Induction.INDUCTION_RESPONSE));
	}
	
	public static List<InductionTriggerContext> getInductionTriggerByScheduer(Long schedulerId,boolean fetchRelated) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.Induction.INDUCTION_TRIGGER));
		
		Condition condition = CriteriaAPI.getCondition(fieldMap.get("scheduleId"), ""+schedulerId,NumberOperators.EQUALS);
		
		return getInductionTrigger(condition, fetchRelated);
	}
	
	public static List<InductionTriggerContext> getInductionTrigger(Condition condition,boolean fetchRelated) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		SelectRecordsBuilder<InductionTriggerContext> select = new SelectRecordsBuilder<InductionTriggerContext>()
				.moduleName(FacilioConstants.Induction.INDUCTION_TRIGGER)
				.beanClass(InductionTriggerContext.class)
				.select(modBean.getAllFields(FacilioConstants.Induction.INDUCTION_TRIGGER))
				.andCondition(condition)
				;
		
		List<InductionTriggerContext> triggers = select.get();
		
		if(triggers != null && !triggers.isEmpty()) {
			if(fetchRelated) {
				fetchInclExcl(triggers);
				fetchScheduleDetails(triggers);
			}
		}
		
		return triggers;
	}

	private static void fetchScheduleDetails(List<InductionTriggerContext> triggers) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<Long> scheduleIds = triggers.stream().filter((trigger) -> {
				return trigger.getType() == InductionTriggerContext.TriggerType.SCHEDULE.getVal() ? true : false; 
		}).map(InductionTriggerContext::getScheduleId).collect(Collectors.toList());
		
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

	private static void fetchInclExcl(List<InductionTriggerContext> triggers) throws Exception {
		// TODO Auto-generated method stub
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<Long> ids = triggers.stream().map(InductionTriggerContext::getId).collect(Collectors.toList());
		
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Induction.INDUCTION_TRIGGER_INCL_EXCL);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<InductionTriggerIncludeExcludeResourceContext> select = new SelectRecordsBuilder<InductionTriggerIncludeExcludeResourceContext>()
				.moduleName(FacilioConstants.Induction.INDUCTION_TRIGGER_INCL_EXCL)
				.beanClass(InductionTriggerIncludeExcludeResourceContext.class)
				.select(modBean.getAllFields(FacilioConstants.Induction.INDUCTION_TRIGGER_INCL_EXCL))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("inductionTrigger"), ids, NumberOperators.EQUALS))
				;
		
		List<InductionTriggerIncludeExcludeResourceContext> inclExcls = select.get();
		
		Map<Long, List<InductionTriggerIncludeExcludeResourceContext>> groupedInclExcl = inclExcls.stream().collect(Collectors.groupingBy(InductionTriggerIncludeExcludeResourceContext::getInductionTriggerId));
		
		triggers.forEach((trigger) -> {trigger.setResInclExclList(groupedInclExcl.get(trigger.getId()));});
	}

}
