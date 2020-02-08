package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SafetyPlanHazardContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkorderHazardContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class HazardsAPI {

	public static List<SafetyPlanHazardContext> fetchAssociatedHazards(Long safetyPlanId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SAFETYPLAN_HAZARD);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		SelectRecordsBuilder<SafetyPlanHazardContext> builder = new SelectRecordsBuilder<SafetyPlanHazardContext>()
				.module(module)
				.beanClass(SafetyPlanHazardContext.class)
				.select(fields)
			    .andCondition(CriteriaAPI.getCondition("SAFETY_PLAN_ID", "safetyPlan", String.valueOf(safetyPlanId),NumberOperators.EQUALS));
				;

		List<SafetyPlanHazardContext> list = builder.get();
		return list;
			                 
	}
	
	public static List<WorkorderHazardContext> fetchWorkorderAssociatedHazards(Long workorderId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_HAZARD);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		SelectRecordsBuilder<WorkorderHazardContext> builder = new SelectRecordsBuilder<WorkorderHazardContext>()
				.module(module)
				.beanClass(WorkorderHazardContext.class)
				.select(fields)
			    .andCondition(CriteriaAPI.getCondition("WORKORDER_ID", "workorder", String.valueOf(workorderId),NumberOperators.EQUALS));
				;

		List<WorkorderHazardContext> list = builder.get();
		return list;
			                 
	}
	
	public static void deleteHazardPrecautions(List<Long> precautionIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.HAZARD_PRECAUTION);
		
		DeleteRecordBuilder<ModuleBaseWithCustomFields> builder = new DeleteRecordBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.andCondition(CriteriaAPI.getConditionFromList("PRECAUTION_ID", "precaution", precautionIds, NumberOperators.EQUALS));
		builder.delete();

	}
	

	public static void deleteSafetyPlanHazard(List<Long> hazardIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SAFETYPLAN_HAZARD);
		
		DeleteRecordBuilder<ModuleBaseWithCustomFields> builder = new DeleteRecordBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.andCondition(CriteriaAPI.getConditionFromList("HAZARD_ID", "hazard", hazardIds, NumberOperators.EQUALS));
		builder.delete();

	}
	
	public static void deleteWorkOrderHazard(List<Long> hazardIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_HAZARD);
		
		DeleteRecordBuilder<ModuleBaseWithCustomFields> builder = new DeleteRecordBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.andCondition(CriteriaAPI.getConditionFromList("HAZARD_ID", "hazard", hazardIds, NumberOperators.EQUALS));
		builder.delete();

	}
}
