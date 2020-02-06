package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.SafetyPlanHazardContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
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
}
