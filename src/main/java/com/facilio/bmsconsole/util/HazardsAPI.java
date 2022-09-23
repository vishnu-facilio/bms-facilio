package com.facilio.bmsconsole.util;

import java.util.List;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.context.V3WorkorderHazardContext;
import com.facilio.bmsconsoleV3.context.safetyplans.V3HazardPrecautionContext;
import com.facilio.bmsconsoleV3.context.safetyplans.V3SafetyPlanHazardContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class HazardsAPI {

	public static List<V3SafetyPlanHazardContext> fetchAssociatedHazards(Long safetyPlanId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SAFETYPLAN_HAZARD);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		SelectRecordsBuilder<V3SafetyPlanHazardContext> builder = new SelectRecordsBuilder<V3SafetyPlanHazardContext>()
				.module(module)
				.beanClass(V3SafetyPlanHazardContext.class)
				.select(fields)
			    .andCondition(CriteriaAPI.getCondition("SAFETY_PLAN_ID", "safetyPlan", String.valueOf(safetyPlanId),NumberOperators.EQUALS));
				;

		List<V3SafetyPlanHazardContext> list = builder.get();
		return list;
			                 
	}

	public static List<SafetyPlanHazardContext> fetchAssociatedHazardsV2(Long safetyPlanId) throws Exception {
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

	public static List<V3HazardPrecautionContext> fetchAssociatedPrecautions(Long hazardId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.HAZARD_PRECAUTION);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		SelectRecordsBuilder<V3HazardPrecautionContext> builder = new SelectRecordsBuilder<V3HazardPrecautionContext>()
				.module(module)
				.beanClass(V3HazardPrecautionContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("HAZARD_ID", "hazard", String.valueOf(hazardId),NumberOperators.EQUALS));
		;

		List<V3HazardPrecautionContext> list = builder.get();
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

	public static List<V3WorkorderHazardContext> fetchV3WorkorderAssociatedHazards(Long workorderId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORKORDER_HAZARD);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		SelectRecordsBuilder<V3WorkorderHazardContext> builder = new SelectRecordsBuilder<V3WorkorderHazardContext>()
				.module(module)
				.beanClass(V3WorkorderHazardContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("WORKORDER_ID", "workorder", String.valueOf(workorderId),NumberOperators.EQUALS));
		;

		List<V3WorkorderHazardContext> list = builder.get();
		return list;

	}

	public static List<AssetHazardContext> fetchAssetAssociatedHazards(Long assetId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET_HAZARD);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		SelectRecordsBuilder<AssetHazardContext> builder = new SelectRecordsBuilder<AssetHazardContext>()
				.module(module)
				.beanClass(AssetHazardContext.class)
				.select(fields)
			    .andCondition(CriteriaAPI.getCondition("ASSET_ID", "asset", String.valueOf(assetId),NumberOperators.EQUALS));
				;

		List<AssetHazardContext> list = builder.get();
		return list;
			                 
	}
	
	public static SafetyPlanContext fetchSafetyPlan(Long safetyPlanId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.SAFETY_PLAN);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		SelectRecordsBuilder<SafetyPlanContext> builder = new SelectRecordsBuilder<SafetyPlanContext>()
				.module(module)
				.beanClass(SafetyPlanContext.class)
				.select(fields)
			    .andCondition(CriteriaAPI.getIdCondition(safetyPlanId, module))
				;

		SafetyPlanContext sp = builder.fetchFirst();
		return sp;
			                 
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
	
	public static void deleteAssetHazard(List<Long> hazardIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.ASSET_HAZARD);
		
		DeleteRecordBuilder<ModuleBaseWithCustomFields> builder = new DeleteRecordBuilder<ModuleBaseWithCustomFields>()
				.module(module)
				.andCondition(CriteriaAPI.getConditionFromList("HAZARD_ID", "hazard", hazardIds, NumberOperators.EQUALS));
		builder.delete();

	}
}
