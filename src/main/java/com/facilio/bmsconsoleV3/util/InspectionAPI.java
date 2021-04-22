package com.facilio.bmsconsoleV3.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerContext;
import com.facilio.bmsconsoleV3.context.inspection.InspectionTriggerIncludeExcludeResourceContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class InspectionAPI {

	
	public static List<InspectionTriggerContext> getInspectionTriggerByScheduer(Long schedulerId,boolean fetchInclExcl) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_TRIGGER));
		
		Condition condition = CriteriaAPI.getCondition(fieldMap.get("scheduleId"), ""+schedulerId,NumberOperators.EQUALS);
		
		return getInspectionTrigger(condition, fetchInclExcl);
	}
	
	public static List<InspectionTriggerContext> getInspectionTrigger(Condition condition,boolean fetchInclExcl) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		SelectRecordsBuilder<InspectionTriggerContext> select = new SelectRecordsBuilder<InspectionTriggerContext>()
				.moduleName(FacilioConstants.Inspection.INSPECTION_TRIGGER)
				.beanClass(InspectionTriggerContext.class)
				.select(modBean.getAllFields(FacilioConstants.Inspection.INSPECTION_TRIGGER))
				.andCondition(condition)
				;
		
		List<InspectionTriggerContext> triggers = select.get();
		
		if(fetchInclExcl) {
			fetchInclExcl(triggers);
		}
		
		return triggers;
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
