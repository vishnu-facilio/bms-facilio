package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsoleV3.commands.safetyplan.V3AssetHazardContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderHazardContext;
import com.facilio.bmsconsoleV3.context.safetyplans.*;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
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

	public static List<Long> fetchAssociatedSafetyPlanHazardIds(String safetyPlanId) throws Exception {
		List<Long> hazardIds = new ArrayList<>();
		Criteria criteria = new Criteria();
		Condition condition = CriteriaAPI.getCondition("SAFETY_PLAN_ID", "safetyPlan",safetyPlanId, NumberOperators.EQUALS);
		criteria.addAndCondition(condition);
		Map<Long, V3SafetyPlanHazardContext> props = V3RecordAPI.getRecordsMap(FacilioConstants.ContextNames.SAFETYPLAN_HAZARD, null, V3SafetyPlanHazardContext.class, criteria, null);
		if(props != null){
			for (V3SafetyPlanHazardContext safetyPlanHazard : props.values()){
				V3HazardContext hazard = safetyPlanHazard.getHazard();
				hazardIds.add(hazard.getId());
			}
		}
		return hazardIds;
	}

	public static List<Long> fetchAssociatedAssetHazardIds(String assetId) throws Exception {
		List<Long> hazardIds = new ArrayList<>();
		Criteria criteria = new Criteria();
		Condition condition = CriteriaAPI.getCondition("ASSET_ID", "asset",assetId, NumberOperators.EQUALS);
		criteria.addAndCondition(condition);
		Map<Long, V3AssetHazardContext> props = V3RecordAPI.getRecordsMap(FacilioConstants.ContextNames.ASSET_HAZARD, null, V3AssetHazardContext.class, criteria, null);
		if(props != null){
			for (V3AssetHazardContext assetHazard : props.values()){
				V3HazardContext hazard = assetHazard.getHazard();
				hazardIds.add(hazard.getId());
			}
		}
		return hazardIds;
	}

	public static List<Long> fetchAssociatedSpaceHazardIds(String spaceId) throws Exception {
		List<Long> hazardIds = new ArrayList<>();
		Criteria criteria = new Criteria();
		Condition condition = CriteriaAPI.getCondition("BASESPACE_ID", "space",spaceId, NumberOperators.EQUALS);
		criteria.addAndCondition(condition);
		Map<Long, V3BaseSpaceHazardContext> props = V3RecordAPI.getRecordsMap(FacilioConstants.ContextNames.BASESPACE_HAZARD, null, V3BaseSpaceHazardContext.class, criteria, null);
		if(props != null){
			for (V3BaseSpaceHazardContext spaceHazard : props.values()){
				V3HazardContext hazard = spaceHazard.getHazard();
				hazardIds.add(hazard.getId());
			}
		}
		return hazardIds;
	}

	public static List<Long> fetchAssociatedWorkOrderHazardIds(String workorderId) throws Exception {
		List<Long> hazardIds = new ArrayList<>();
		Criteria criteria = new Criteria();
		Condition condition = CriteriaAPI.getCondition("WORKORDER_ID", "workorder",workorderId, NumberOperators.EQUALS);
		criteria.addAndCondition(condition);
		Map<Long, V3WorkorderHazardContext> props = V3RecordAPI.getRecordsMap(FacilioConstants.ContextNames.WORKORDER_HAZARD, null, V3WorkorderHazardContext.class, criteria, null);
		if(props != null){
			for (V3WorkorderHazardContext workorderHazard : props.values()){
				V3HazardContext hazard = workorderHazard.getHazard();
				hazardIds.add(hazard.getId());
			}
		}
		return hazardIds;
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

	public static List<Long> fetchAssociatedHazardPrecautions(String hazardId) throws Exception {
		List<Long> precautionIds = new ArrayList<>();
		Criteria criteria = new Criteria();
		Condition condition = CriteriaAPI.getCondition("HAZARD_ID", "hazard",hazardId, NumberOperators.EQUALS);
		criteria.addAndCondition(condition);
		Map<Long, V3HazardPrecautionContext> props = V3RecordAPI.getRecordsMap(FacilioConstants.ContextNames.HAZARD_PRECAUTION, null, V3HazardPrecautionContext.class, criteria, null);
		if(props != null){
			for (V3HazardPrecautionContext hazardPrecaution : props.values()){
				V3PrecautionContext precaution = hazardPrecaution.getPrecaution();
				precautionIds.add(precaution.getId());
			}
		}
		return precautionIds;

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
