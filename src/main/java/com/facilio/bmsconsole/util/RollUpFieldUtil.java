package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.RollUpField;
import com.facilio.bmsconsole.context.WorkflowRuleLoggerContext;
import com.facilio.bmsconsole.context.WorkflowRuleResourceLoggerContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.AggregateOperator;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class RollUpFieldUtil {
	
	public static void addRollUpField(List<RollUpField> rollUpFields) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
			.table(ModuleFactory.getRollUpFieldsModule().getTableName())
			.fields(FieldFactory.getRollUpFieldFields());
		
		for(RollUpField rollUpField:rollUpFields) {
			Map<String, Object> props = FieldUtil.getAsProperties(rollUpField);
			insertBuilder.addRecord(props);
		}
		insertBuilder.save();
	}

	public static List<RollUpField> getRollUpFieldsByChildModuleId(FacilioModule childModule, boolean isFetchSubProps) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getRollUpFieldFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
			.select(FieldFactory.getRollUpFieldFields())
			.table(ModuleFactory.getRollUpFieldsModule().getTableName())
			.andCondition(CriteriaAPI.getCondition(fieldMap.get("childModuleId"), childModule.getExtendedModuleIds(), NumberOperators.EQUALS));
							
		List<Map<String, Object>> props = selectBuilder.get();
		return getRollUpFieldFromProps(props, childModule, isFetchSubProps);
	}
	
	public static List<RollUpField> getRollUpFieldFromProps(List<Map<String, Object>> props, FacilioModule childModule, boolean isFetchSubProps) throws Exception {
		
		if (props != null && !props.isEmpty()) {
			
			List<RollUpField> rollUpFields = FieldUtil.getAsBeanListFromMapList(props, RollUpField.class);
			
			if(isFetchSubProps) 
			{
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				List<Long> fieldIds = new ArrayList<Long>();
				List<Long> criteriaIds = new ArrayList<Long>();

				for(RollUpField rollUpField:rollUpFields) 
				{		
					fieldIds.add(rollUpField.getChildFieldId());
					if(rollUpField.getAggregateFieldId() != -1) {
						fieldIds.add(rollUpField.getAggregateFieldId());
					}
					if(rollUpField.getChildCriteriaId() != -1) {
						criteriaIds.add(rollUpField.getChildCriteriaId());
					}
					fieldIds.add(rollUpField.getParentRollUpFieldId());
				}
				
				List<FacilioField> fields = modBean.getFields(fieldIds);
				Map<Long, FacilioField> fieldMap = FieldFactory.getAsIdMap(fields);
				Map<Long, Criteria> criteriaMap = !criteriaIds.isEmpty() ? CriteriaAPI.getCriteriaAsMap(criteriaIds) : null;
				
				for(RollUpField rollUpField:rollUpFields) 
				{		
					rollUpField.setChildField(fieldMap.get(rollUpField.getChildFieldId()));
					rollUpField.setChildModule(modBean.getModule(rollUpField.getChildModuleId()));
					rollUpField.getChildField().setModule(rollUpField.getChildModule());
					if(rollUpField.getAggregateFieldId() == -1) {
						rollUpField.setAggregateField(rollUpField.getChildField());		
					}else {
						rollUpField.setAggregateField(fieldMap.get(rollUpField.getAggregateFieldId()));
					}	
					rollUpField.setAggregateFunctionOperator(AggregateOperator.getAggregateOperator(rollUpField.getAggregateFunctionId()));
					
					if(criteriaMap != null && !criteriaMap.isEmpty()) 
					{		
						if(rollUpField.getChildCriteriaId() != -1 && criteriaMap.containsKey(rollUpField.getChildCriteriaId())) 
						{
							Criteria childCriteria = criteriaMap.get(rollUpField.getChildCriteriaId());
							if(childCriteria != null) {
								rollUpField.setChildCriteria(childCriteria);	
							}
						}
					}
					
					rollUpField.setParentRollUpField(fieldMap.get(rollUpField.getParentRollUpFieldId()));
					rollUpField.setParentModule(modBean.getModule(rollUpField.getParentModuleId()));
					rollUpField.getParentRollUpField().setModule(rollUpField.getParentModule());
				}
			}
			return rollUpFields;
		}
		return null;		
	}
	
	public static void aggregateFieldAndAddRollUpFieldData(RollUpField triggeringChildField, List<Long> triggerChildGroupedIds, List<ReadingDataMeta> rollUpFieldData) throws Exception {
		
		FacilioField aggregationField = AggregateOperator.getAggregateOperator(triggeringChildField.getAggregateFunctionId()).getSelectField(triggeringChildField.getAggregateField());
		aggregationField.setName("aggregationField");
		
		List<FacilioField> selectFields = new ArrayList<FacilioField>();
		selectFields.add(aggregationField);
		selectFields.add(triggeringChildField.getChildField());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(selectFields)
				.table(triggeringChildField.getChildModule().getTableName())
				.innerJoin("Resources")
				.on("Resources.ID=" +triggeringChildField.getChildModule().getTableName()+ ".ID")
				.andCondition(CriteriaAPI.getCondition("Resources.SYS_DELETED","", "", CommonOperators.IS_EMPTY))
				.andCondition(CriteriaAPI.getCondition(triggeringChildField.getChildField(), triggerChildGroupedIds, NumberOperators.EQUALS))
				.groupBy(triggeringChildField.getChildModule().getTableName()+ "." +triggeringChildField.getChildField().getColumnName());
		
		if(triggeringChildField.getChildCriteria() != null) {
			selectBuilder.andCriteria(triggeringChildField.getChildCriteria());
		}
		
		List<Map<String, Object>> propsList = selectBuilder.get();
		System.out.println(" query -- "+selectBuilder.get());
		if(propsList != null && !propsList.isEmpty())
		{
			for(Map<String, Object> prop :propsList)
			{
				long parentLookUpId = (long)prop.get(triggeringChildField.getChildField().getName());
				long aggregatedRollUpValue = (prop.get("aggregationField") != null) ? (long)prop.get("aggregationField") : -99;
				constructAndAddDynamicRdm(parentLookUpId, aggregatedRollUpValue, triggeringChildField.getParentRollUpField(), rollUpFieldData);
			}
		}	
	}
	
	private static void constructAndAddDynamicRdm(long parentLookUpId, long aggregatedRollUpResult, FacilioField parentRollUpField, List<ReadingDataMeta> rollUpFieldData) {
		ReadingDataMeta rdm = new ReadingDataMeta();
		rdm.setFieldId(parentRollUpField.getFieldId());
		rdm.setField(parentRollUpField);	
		rdm.setValue(aggregatedRollUpResult);
		rdm.setReadingDataId(parentLookUpId);
		rdm.setOrgId(AccountUtil.getCurrentOrg().getId());
		rollUpFieldData.add(rdm);
	}
	
	public static void addRollUpMigFields() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule siteModule = modBean.getModule("site");
		FacilioModule buildingModule = modBean.getModule("building");
		FacilioModule floorModule = modBean.getModule("floor");
		
		Map<String, FacilioField> siteModuleFields = FieldFactory.getAsMap(modBean.getAllFields(siteModule.getName()));
		FacilioField noOfBuildingsField = siteModuleFields.get("noOfBuildings");
		
		//add alter table for NO_OF_INDEPENDENT_SPACES for all three tables
		FacilioField independentSpaceFieldF = new FacilioField();
		independentSpaceFieldF.setName("noOfIndependentSpaces");
		independentSpaceFieldF.setColumnName("NO_OF_INDEPENDENT_SPACES");
		independentSpaceFieldF.setDisplayName("No. Of Independent Spaces");
		independentSpaceFieldF.setOrgId(noOfBuildingsField.getOrgId());
		independentSpaceFieldF.setDisplayType(noOfBuildingsField.getDisplayTypeInt());
		independentSpaceFieldF.setDataType(noOfBuildingsField.getDataType());
		independentSpaceFieldF.setDefault(noOfBuildingsField.getDefault());
		independentSpaceFieldF.setDisabled(noOfBuildingsField.getDisabled());
		independentSpaceFieldF.setRequired(noOfBuildingsField.getRequired());
		independentSpaceFieldF.setModule(floorModule);
		modBean.addField(independentSpaceFieldF);
		
		FacilioField independentSpaceFieldB = new FacilioField();
		independentSpaceFieldB.setName("noOfIndependentSpaces");
		independentSpaceFieldB.setColumnName("NO_OF_INDEPENDENT_SPACES");
		independentSpaceFieldB.setDisplayName("No. Of Independent Spaces");
		independentSpaceFieldB.setOrgId(noOfBuildingsField.getOrgId());
		independentSpaceFieldB.setDisplayType(noOfBuildingsField.getDisplayTypeInt());
		independentSpaceFieldB.setDataType(noOfBuildingsField.getDataType());
		independentSpaceFieldB.setDefault(noOfBuildingsField.getDefault());
		independentSpaceFieldB.setDisabled(noOfBuildingsField.getDisabled());
		independentSpaceFieldB.setRequired(noOfBuildingsField.getRequired());
		independentSpaceFieldB.setModule(buildingModule);
		modBean.addField(independentSpaceFieldB);

		FacilioField independentSpaceFieldS = new FacilioField();
		independentSpaceFieldS.setName("noOfIndependentSpaces");
		independentSpaceFieldS.setColumnName("NO_OF_INDEPENDENT_SPACES");
		independentSpaceFieldS.setDisplayName("No. Of Independent Spaces");
		independentSpaceFieldS.setOrgId(noOfBuildingsField.getOrgId());
		independentSpaceFieldS.setDisplayType(noOfBuildingsField.getDisplayTypeInt());
		independentSpaceFieldS.setDataType(noOfBuildingsField.getDataType());
		independentSpaceFieldS.setDefault(noOfBuildingsField.getDefault());
		independentSpaceFieldS.setDisabled(noOfBuildingsField.getDisabled());
		independentSpaceFieldS.setRequired(noOfBuildingsField.getRequired());
		independentSpaceFieldS.setModule(siteModule);
		modBean.addField(independentSpaceFieldS);
	}
	
	public static void addRollUpForBaseSpaceFields() throws Exception{
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule baseSpaceModule = modBean.getModule("basespace");
		FacilioModule siteModule = modBean.getModule("site");
		FacilioModule buildingModule = modBean.getModule("building");
		FacilioModule floorModule = modBean.getModule("floor");
		
		Map<String, FacilioField> baseSpaceModuleFields = FieldFactory.getAsMap(modBean.getAllFields(baseSpaceModule.getName()));
		Map<String, FacilioField> siteModuleFields = FieldFactory.getAsMap(modBean.getAllFields(siteModule.getName()));
		Map<String, FacilioField> buildingModuleFields = FieldFactory.getAsMap(modBean.getAllFields(buildingModule.getName()));
		Map<String, FacilioField> floorModuleFields = FieldFactory.getAsMap(modBean.getAllFields(floorModule.getName()));

		List<RollUpField> rollUpFields = new ArrayList<RollUpField>();
		long childCriteriaId1 = -1, childCriteriaId2 = -1, childCriteriaId3 = -1, childCriteriaId4 = -1, childCriteriaId5 = -1;
		RollUpField rollUpFieldContext = new RollUpField();
		rollUpFieldContext.setChildModuleId(baseSpaceModule.getModuleId());
		rollUpFieldContext.setAggregateFunctionId(BmsAggregateOperators.CommonAggregateOperator.COUNT.getValue());
		
		Condition spaceNotNull = CriteriaAPI.getCondition(baseSpaceModuleFields.get("space1"), "", CommonOperators.IS_NOT_EMPTY);
		Condition spaceNull = CriteriaAPI.getCondition(baseSpaceModuleFields.get("space1"), "", CommonOperators.IS_EMPTY);
		Condition floorNotNull = CriteriaAPI.getCondition(baseSpaceModuleFields.get("floor"), "", CommonOperators.IS_NOT_EMPTY);
		Condition floorNull = CriteriaAPI.getCondition(baseSpaceModuleFields.get("floor"), "", CommonOperators.IS_EMPTY);
		Condition buildingNotNull = CriteriaAPI.getCondition(baseSpaceModuleFields.get("building"), "", CommonOperators.IS_NOT_EMPTY);
		Condition buildingNull = CriteriaAPI.getCondition(baseSpaceModuleFields.get("building"), "", CommonOperators.IS_EMPTY);
		Condition siteNotNull = CriteriaAPI.getCondition(baseSpaceModuleFields.get("site"), "", CommonOperators.IS_NOT_EMPTY);
		Condition spaceTypeSpace = CriteriaAPI.getCondition(baseSpaceModuleFields.get("spaceType"), ""+SpaceType.SPACE.getIntVal(), NumberOperators.EQUALS);
		Condition spaceTypeNotSpace = CriteriaAPI.getCondition(baseSpaceModuleFields.get("spaceType"), ""+SpaceType.SPACE.getIntVal(), NumberOperators.NOT_EQUALS);
		
		//Make sure no of independent spaces fields is added prior to this and SetIds here directly
		//floor
		
		RollUpField floorRollUpFieldContextIS = new RollUpField(rollUpFieldContext);
		floorRollUpFieldContextIS.setChildFieldId(baseSpaceModuleFields.get("floor").getFieldId());
		floorRollUpFieldContextIS.setParentModuleId(floorModule.getModuleId());
		floorRollUpFieldContextIS.setParentRollUpFieldId(floorModuleFields.get("noOfIndependentSpaces").getFieldId());
	
		Criteria criteria1 = new Criteria();
		//criteria1.addAndCondition(spaceNotNull);
		criteria1.addAndCondition(spaceTypeSpace);
		criteria1.addAndCondition(floorNotNull);
		criteria1.addAndCondition(buildingNotNull);
		criteria1.addAndCondition(siteNotNull);
		childCriteriaId1 = CriteriaAPI.addCriteria(criteria1);
		floorRollUpFieldContextIS.setChildCriteriaId(childCriteriaId1);
		rollUpFields.add(floorRollUpFieldContextIS);

		//building
		RollUpField buildingRollUpFieldContext = new RollUpField(rollUpFieldContext);
		buildingRollUpFieldContext.setChildFieldId(baseSpaceModuleFields.get("building").getFieldId());
		buildingRollUpFieldContext.setParentModuleId(buildingModule.getModuleId());

		Criteria criteria2 = new Criteria();
		//criteria2.addAndCondition(spaceNull);
		criteria2.addAndCondition(spaceTypeNotSpace);
		criteria2.addAndCondition(floorNotNull);
		criteria2.addAndCondition(buildingNotNull);
		criteria2.addAndCondition(siteNotNull);
		childCriteriaId2 = CriteriaAPI.addCriteria(criteria2);
		
		RollUpField buildingRollUpFieldContextF = new RollUpField(buildingRollUpFieldContext);
		buildingRollUpFieldContextF.setParentRollUpFieldId(buildingModuleFields.get("noOfFloors").getFieldId());
		buildingRollUpFieldContextF.setChildCriteriaId(childCriteriaId2);
		rollUpFields.add(buildingRollUpFieldContextF);
		
		Criteria criteria3 = new Criteria();
		//criteria3.addAndCondition(spaceNotNull);
		criteria3.addAndCondition(spaceTypeSpace);
		criteria3.addAndCondition(floorNull);
		criteria3.addAndCondition(buildingNotNull);
		criteria3.addAndCondition(siteNotNull);
		childCriteriaId3 = CriteriaAPI.addCriteria(criteria3);

		RollUpField buildingRollUpFieldContextIS = new RollUpField(buildingRollUpFieldContext);
		buildingRollUpFieldContextIS.setParentRollUpFieldId(buildingModuleFields.get("noOfIndependentSpaces").getFieldId());
		buildingRollUpFieldContextIS.setChildCriteriaId(childCriteriaId3);
		rollUpFields.add(buildingRollUpFieldContextIS);
	
		//site
		RollUpField siteRollUpFieldContext = new RollUpField(rollUpFieldContext);
		siteRollUpFieldContext.setChildFieldId(baseSpaceModuleFields.get("site").getFieldId());
		siteRollUpFieldContext.setParentModuleId(siteModule.getModuleId());
		
		Criteria criteria4 = new Criteria();
		//criteria4.addAndCondition(spaceNull);
		criteria4.addAndCondition(spaceTypeNotSpace);
		criteria4.addAndCondition(floorNull);
		criteria4.addAndCondition(buildingNotNull);
		criteria4.addAndCondition(siteNotNull);
		childCriteriaId4 = CriteriaAPI.addCriteria(criteria4);
		
		RollUpField siteRollUpFieldContextB = new RollUpField(siteRollUpFieldContext);
		siteRollUpFieldContextB.setParentRollUpFieldId(siteModuleFields.get("noOfBuildings").getFieldId());
		siteRollUpFieldContextB.setChildCriteriaId(childCriteriaId4);
		rollUpFields.add(siteRollUpFieldContextB);
		
		Criteria criteria5 = new Criteria();
		//criteria5.addAndCondition(spaceNotNull);
		criteria5.addAndCondition(spaceTypeSpace);
		criteria5.addAndCondition(floorNull);
		criteria5.addAndCondition(buildingNull);
		criteria5.addAndCondition(siteNotNull);
		childCriteriaId5 = CriteriaAPI.addCriteria(criteria5);
		
		RollUpField siteRollUpFieldContextIS = new RollUpField(siteRollUpFieldContext);
		siteRollUpFieldContextIS.setParentRollUpFieldId(siteModuleFields.get("noOfIndependentSpaces").getFieldId());
		siteRollUpFieldContextIS.setChildCriteriaId(childCriteriaId5);
		rollUpFields.add(siteRollUpFieldContextIS);
		
		addRollUpField(rollUpFields);
	}
}
