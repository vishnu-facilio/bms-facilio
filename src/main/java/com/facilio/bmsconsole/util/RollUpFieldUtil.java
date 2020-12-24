package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.instant.jobs.ParallelWorkflowRuleExecutionJob;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.bmsconsole.commands.ExecuteRollUpFieldCommand;
import com.facilio.bmsconsole.context.HistoricalLoggerContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.RollUpField;
import com.facilio.bmsconsole.context.WorkflowRuleLoggerContext;
import com.facilio.bmsconsole.context.WorkflowRuleResourceLoggerContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
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
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.tasker.FacilioTimer;

public class RollUpFieldUtil {
	
	private static final Logger LOGGER = Logger.getLogger(RollUpFieldUtil.class.getName());
	
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
	
	public static void updateRollUpFieldParentDataFromRDM(List<ReadingDataMeta> rollUpFieldData) throws Exception {
		
		for(ReadingDataMeta rollUpData:rollUpFieldData) 
		{			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField rollUpFieldToBeUpdated = modBean.getFieldFromDB(rollUpData.getFieldId());
			
			FacilioField parentRollUpField = rollUpData.getField();
			Map<String,Object> prop = new HashMap<String,Object>();
			prop.put(rollUpFieldToBeUpdated.getName(), rollUpData.getValue());
			
			UpdateRecordBuilder<ModuleBaseWithCustomFields> updateBuilder = new UpdateRecordBuilder<ModuleBaseWithCustomFields>()
					.moduleName(parentRollUpField.getModule().getName()) 
					.fields(Collections.singletonList(rollUpFieldToBeUpdated))
					.andCondition(CriteriaAPI.getCondition(parentRollUpField.getModule().getTableName()+".ID", "id", ""+rollUpData.getReadingDataId(), NumberOperators.EQUALS));

			updateBuilder.updateViaMap(prop);
		}
	}
	
	public static List<RollUpField> getRollUpFieldsByIds(List<Long> ids, boolean isFetchSubProps) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getRollUpFieldFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
			.select(FieldFactory.getRollUpFieldFields())
			.table(ModuleFactory.getRollUpFieldsModule().getTableName())
			.andCondition(CriteriaAPI.getCondition(fieldMap.get("id"), ids, NumberOperators.EQUALS));
							
		List<Map<String, Object>> props = selectBuilder.get();
		return getRollUpFieldFromProps(props, isFetchSubProps);
	}
	
	public static int updateRollUpField(RollUpField rollUpFieldContext) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getRollUpFieldFields());
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
			.fields(FieldFactory.getRollUpFieldFields())
			.table(ModuleFactory.getRollUpFieldsModule().getTableName())
			.andCondition(CriteriaAPI.getIdCondition(rollUpFieldContext.getId(), ModuleFactory.getRollUpFieldsModule()));
			
		Map<String, Object> props = FieldUtil.getAsProperties(rollUpFieldContext);
		int rowsUpdated = updateBuilder.update(props);
		return rowsUpdated;
	}

	public static List<RollUpField> getRollUpFieldsByChildModuleId(FacilioModule childModule, boolean isFetchSubProps) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getRollUpFieldFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
			.select(FieldFactory.getRollUpFieldFields())
			.table(ModuleFactory.getRollUpFieldsModule().getTableName())
			.andCondition(CriteriaAPI.getCondition(fieldMap.get("childModuleId"), childModule.getExtendedModuleIds(), NumberOperators.EQUALS));
							
		List<Map<String, Object>> props = selectBuilder.get();
		return getRollUpFieldFromProps(props, isFetchSubProps);
	}
	
	public static List<RollUpField> getRollUpFieldsByParentModuleId(FacilioModule parentModule, boolean isFetchSubProps) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getRollUpFieldFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
			.select(FieldFactory.getRollUpFieldFields())
			.table(ModuleFactory.getRollUpFieldsModule().getTableName())
			.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentModuleId"), parentModule.getExtendedModuleIds(), NumberOperators.EQUALS));
							
		List<Map<String, Object>> props = selectBuilder.get();
		return getRollUpFieldFromProps(props, isFetchSubProps);
	}
	
	public static List<RollUpField> getRollUpFieldsByParentRollUpFieldId(Long parentRollUpFieldId, boolean isFetchSubProps) throws Exception {
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getRollUpFieldFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
			.select(FieldFactory.getRollUpFieldFields())
			.table(ModuleFactory.getRollUpFieldsModule().getTableName())
			.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentRollUpFieldId"), ""+parentRollUpFieldId, NumberOperators.EQUALS));
							
		List<Map<String, Object>> props = selectBuilder.get();
		return getRollUpFieldFromProps(props, isFetchSubProps);
	}
	
	public static List<RollUpField> getRollUpFieldFromProps(List<Map<String, Object>> props, boolean isFetchSubProps) throws Exception {
		
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
						rollUpField.setAggregateFieldId(rollUpField.getAggregateField().getFieldId());		
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
	
	public static List<Long> getDistinctChildModuleRecordIds(RollUpField triggeringChildField, Criteria criteria, int offsetCount) throws Exception {
		
		FacilioField selectDistinctField = new FacilioField();
		selectDistinctField.setName(triggeringChildField.getChildField().getName()+"distinct");
		selectDistinctField.setDisplayName(triggeringChildField.getChildField().getDisplayName());
		selectDistinctField.setColumnName("DISTINCT("+triggeringChildField.getChildField().getCompleteColumnName()+")");
		
		List<FacilioField> selectFields = new ArrayList<>();
		selectFields.add(selectDistinctField);
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
				.table(triggeringChildField.getChildModule().getTableName())
				.beanClass(ModuleBaseWithCustomFields.class)
				.module(triggeringChildField.getChildModule())
				.select(selectFields)
				.setAggregation()
				.orderBy(triggeringChildField.getChildModule().getTableName()+ "." +triggeringChildField.getChildField().getColumnName())
				.limit(5000)
				.offset(offsetCount);
		
		if(triggeringChildField.getChildCriteria() != null) {
			selectBuilder.andCriteria(triggeringChildField.getChildCriteria());
		}
		if(criteria != null) {
			selectBuilder.andCriteria(criteria);
		}
		
		List<Long> childModuleRecordIds = new ArrayList<Long>();
		List<Map<String, Object>> propsList = selectBuilder.getAsProps();
		
		if(propsList != null && !propsList.isEmpty())
		{
			for(Map<String, Object> prop :propsList)
			{
				Long parentLookUpId = null;	
				if(prop.get(selectDistinctField.getName()) instanceof Map && selectDistinctField instanceof LookupField) {
					Map<String, Object> lookUpObject = (Map<String, Object>) prop.get(selectDistinctField.getName());
					if(lookUpObject.get("id") != null) {
						parentLookUpId = (Long) lookUpObject.get("id");
					}
				}
				else {
					parentLookUpId = (Long) prop.get(selectDistinctField.getName());
				}
				
				if(parentLookUpId != null) {
					childModuleRecordIds.add(parentLookUpId);	
				}
			}
		}
		return childModuleRecordIds;
	}
	
	public static List<List<Long>> getDistinctChildModuleRecordIdsInBatch(RollUpField triggeringChildField, Criteria criteria, int offsetCount) throws Exception {
		
		FacilioField selectDistinctField = new FacilioField();
		selectDistinctField.setName(triggeringChildField.getChildField().getName()+"distinct");
		selectDistinctField.setDisplayName(triggeringChildField.getChildField().getDisplayName());
		selectDistinctField.setColumnName("DISTINCT("+triggeringChildField.getChildField().getCompleteColumnName()+")");
		selectDistinctField.setDisplayType(triggeringChildField.getChildField().getDisplayTypeInt());
		selectDistinctField.setDisplayType(triggeringChildField.getChildField().getDisplayType());
		selectDistinctField.setDataType(FieldType.NUMBER.getTypeAsInt());
		selectDistinctField.setDataType(FieldType.NUMBER);
		
		List<FacilioField> selectFields = new ArrayList<>();
		selectFields.add(selectDistinctField);
		
		SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
				.table(triggeringChildField.getChildModule().getTableName())
				.beanClass(ModuleBaseWithCustomFields.class)
				.module(triggeringChildField.getChildModule())
				.select(selectFields)
				.setAggregation();
		
		if(triggeringChildField.getChildCriteria() != null) {
			selectBuilder.andCriteria(triggeringChildField.getChildCriteria());
		}
		if(criteria != null) {
			selectBuilder.andCriteria(criteria);
		}
		
		SelectRecordsBuilder.BatchResult<Map<String, Object>> bs = selectBuilder.getAsPropsInBatches(triggeringChildField.getChildModule().getTableName()+ "." +triggeringChildField.getChildField().getColumnName(), offsetCount);
		List<List<Long>> childModuleRecordIdsList = new ArrayList<List<Long>>();
		while (bs.hasNext()) 
		{
		   List<Map<String, Object>> propsList = bs.get();
		   List<Long> childModuleRecordIds = new ArrayList<Long>();

		   if(propsList != null && !propsList.isEmpty())
		   {
				for(Map<String, Object> prop :propsList)
				{
					Long parentLookUpId = null;	
					if(prop.get(selectDistinctField.getName()) instanceof Map && selectDistinctField instanceof LookupField) {
						Map<String, Object> lookUpObject = (Map<String, Object>) prop.get(selectDistinctField.getName());
						if(lookUpObject.get("id") != null) {
							parentLookUpId = (Long) lookUpObject.get("id");
						}
					}
					else {
						parentLookUpId = (Long) prop.get(selectDistinctField.getName());
					}
					
					if(parentLookUpId != null) {
						childModuleRecordIds.add(parentLookUpId);	
					}
				}
			}
		   childModuleRecordIdsList.add(childModuleRecordIds);
		}

		return childModuleRecordIdsList;
	}
	
	public static void aggregateFieldAndAddRollUpFieldData(RollUpField triggeringChildField, List<Long> triggerChildGroupedIds, List<ReadingDataMeta> rollUpFieldData) throws Exception {
		
		List<FacilioField> selectFields = new ArrayList<FacilioField>();
		FacilioField childFieldCloned = triggeringChildField.getChildField().clone();
		childFieldCloned.setName(childFieldCloned.getName()+"parentLookUpId");
		List<Long> aggregatedRollUpFieldDataIds = new ArrayList<Long>();

		selectFields.add(childFieldCloned);
		SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
				.table(triggeringChildField.getChildModule().getTableName())
				.module(triggeringChildField.getChildModule())
				.select(selectFields)
				.aggregate(AggregateOperator.getAggregateOperator(triggeringChildField.getAggregateFunctionId()), triggeringChildField.getAggregateField())
				.andCondition(CriteriaAPI.getCondition(triggeringChildField.getChildField(), triggerChildGroupedIds, NumberOperators.EQUALS))
				.groupBy(triggeringChildField.getChildModule().getTableName()+ "." +triggeringChildField.getChildField().getColumnName());
			
		if(triggeringChildField.getChildCriteria() != null) {
			selectBuilder.andCriteria(triggeringChildField.getChildCriteria());
		}
		
		List<Map<String, Object>> propsList = selectBuilder.getAsProps();
		if(propsList != null && !propsList.isEmpty())
		{
			for(Map<String, Object> prop :propsList)
			{
				long parentLookUpId = -1;
				Object aggregatedRollUpValue = null;
				
				if(prop.get(triggeringChildField.getAggregateField().getName()) instanceof Map && triggeringChildField.getAggregateField() instanceof LookupField) {
					Map<String, Object> lookUpAggregatedObject = (Map<String, Object>) prop.get(triggeringChildField.getAggregateField().getName());
					if(lookUpAggregatedObject.get("id") != null) {
						aggregatedRollUpValue = lookUpAggregatedObject.get("id");
					}
				}
				else {
					aggregatedRollUpValue = prop.get(triggeringChildField.getAggregateField().getName());
				}
				
				if(prop.get(childFieldCloned.getName()) instanceof Map && childFieldCloned instanceof LookupField) {
					Map<String, Object> lookUpObject = (Map<String, Object>) prop.get(childFieldCloned.getName());
					if(lookUpObject.get("id") != null) {
						parentLookUpId = (long)lookUpObject.get("id");
					}
				}
				else {
					parentLookUpId = (long)prop.get(childFieldCloned.getName());
				}
			
				if(parentLookUpId != -1 && aggregatedRollUpValue != null) {
					aggregatedRollUpFieldDataIds.add(parentLookUpId);
					constructAndAddDynamicRdm(parentLookUpId, aggregatedRollUpValue, triggeringChildField.getParentRollUpField(), rollUpFieldData);
				}
				LOGGER.info("Aggregated Rollup Result : "+ aggregatedRollUpValue + " ParentLookUpPrimaryId : " + parentLookUpId +" RollUpFieldContext -- " +triggeringChildField);
			}
		}
		
		for(Long triggerChildGroupedId:triggerChildGroupedIds) {		
			if(!aggregatedRollUpFieldDataIds.contains(triggerChildGroupedId)) {
				Object aggregatedRollUpValue = null;
				if(triggeringChildField.getAggregateFunctionId() == BmsAggregateOperators.CommonAggregateOperator.COUNT.getValue()) {
					aggregatedRollUpValue = 0l;
				}
				constructAndAddDynamicRdm(triggerChildGroupedId, aggregatedRollUpValue, triggeringChildField.getParentRollUpField(), rollUpFieldData);
			}	
		}
	}
	
	private static void constructAndAddDynamicRdm(long parentLookUpId, Object aggregatedRollUpResult, FacilioField parentRollUpField, List<ReadingDataMeta> rollUpFieldData) {
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
		FacilioModule spaceModule = modBean.getModule("space");
		
		Map<String, FacilioField> siteModuleFields = FieldFactory.getAsMap(modBean.getAllFields(siteModule.getName()));
		FacilioField noOfBuildingsField = siteModuleFields.get("noOfBuildings");
		
		//add alter table for NO_OF_INDEPENDENT_SPACES for all three tables
		FacilioField spaceField = new FacilioField();
		spaceField.setName("noOfSubSpaces");
		spaceField.setColumnName("NO_OF_SUB_SPACES");
		spaceField.setDisplayName("No. Of Sub Spaces");
		spaceField.setOrgId(noOfBuildingsField.getOrgId());
		spaceField.setDisplayType(FacilioField.FieldDisplayType.ROLL_UP_FIELD.getIntValForDB());
		spaceField.setDataType(noOfBuildingsField.getDataType());
		spaceField.setDefault(noOfBuildingsField.getDefault());
		spaceField.setDisabled(noOfBuildingsField.getDisabled());
		spaceField.setRequired(noOfBuildingsField.getRequired());
		spaceField.setModule(spaceModule);
		modBean.addField(spaceField);
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
		criteria1.addAndCondition(spaceNull);
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
		criteria2.addAndCondition(spaceNull);
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
		criteria3.addAndCondition(spaceNull);
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
		criteria4.addAndCondition(spaceNull);
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
		criteria5.addAndCondition(spaceNull);
		criteria5.addAndCondition(spaceTypeSpace);
		criteria5.addAndCondition(floorNull);
		criteria5.addAndCondition(buildingNull);
		criteria5.addAndCondition(siteNotNull);
		childCriteriaId5 = CriteriaAPI.addCriteria(criteria5);
		
		RollUpField siteRollUpFieldContextIS = new RollUpField(siteRollUpFieldContext);
		siteRollUpFieldContextIS.setParentRollUpFieldId(siteModuleFields.get("noOfIndependentSpaces").getFieldId());
		siteRollUpFieldContextIS.setChildCriteriaId(childCriteriaId5);
		rollUpFields.add(siteRollUpFieldContextIS);
		
		for(RollUpField rollUpField:rollUpFields) {
			rollUpField.setIsSystemRollUpField(true);
		}
		addRollUpField(rollUpFields);
	}
	
	public static void addRollUpForSubSpaceFields() throws Exception{
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule baseSpaceModule = modBean.getModule("basespace");
		FacilioModule tagetSpaceModule = modBean.getModule("space");
		
		Map<String, FacilioField> baseSpaceModuleFields = FieldFactory.getAsMap(modBean.getAllFields(baseSpaceModule.getName()));
		Map<String, FacilioField> targetSpaceModuleFields = FieldFactory.getAsMap(modBean.getAllFields(tagetSpaceModule.getName()));

		List<RollUpField> rollUpFields = new ArrayList<RollUpField>();
		long childCriteriaId1 = -1, childCriteriaId2 = -1, childCriteriaId3 = -1, childCriteriaId4 = -1;
		
		Condition space1NotNull = CriteriaAPI.getCondition(baseSpaceModuleFields.get("space1"), "", CommonOperators.IS_NOT_EMPTY);
		Condition space1Null = CriteriaAPI.getCondition(baseSpaceModuleFields.get("space1"), "", CommonOperators.IS_EMPTY);
		Condition space2NotNull = CriteriaAPI.getCondition(baseSpaceModuleFields.get("space2"), "", CommonOperators.IS_NOT_EMPTY);
		Condition space2Null = CriteriaAPI.getCondition(baseSpaceModuleFields.get("space2"), "", CommonOperators.IS_EMPTY);
		Condition space3NotNull = CriteriaAPI.getCondition(baseSpaceModuleFields.get("space3"), "", CommonOperators.IS_NOT_EMPTY);
		Condition space3Null = CriteriaAPI.getCondition(baseSpaceModuleFields.get("space3"), "", CommonOperators.IS_EMPTY);
		Condition space4NotNull = CriteriaAPI.getCondition(baseSpaceModuleFields.get("space4"), "", CommonOperators.IS_NOT_EMPTY);
		Condition space4Null = CriteriaAPI.getCondition(baseSpaceModuleFields.get("space4"), "", CommonOperators.IS_EMPTY);
		Condition siteNotNull = CriteriaAPI.getCondition(baseSpaceModuleFields.get("site"), "", CommonOperators.IS_NOT_EMPTY);
		Condition spaceTypeSpace = CriteriaAPI.getCondition(baseSpaceModuleFields.get("spaceType"), ""+SpaceType.SPACE.getIntVal(), NumberOperators.EQUALS);
		
		//Make sure no of independent/spaces fields is added prior to this and SetIds here directly
		//space1

		RollUpField rollUpFieldContext = new RollUpField();
		rollUpFieldContext.setChildModuleId(baseSpaceModule.getModuleId());
		rollUpFieldContext.setAggregateFunctionId(BmsAggregateOperators.CommonAggregateOperator.COUNT.getValue());
		rollUpFieldContext.setParentModuleId(tagetSpaceModule.getModuleId());
		rollUpFieldContext.setParentRollUpFieldId(targetSpaceModuleFields.get("noOfSubSpaces").getFieldId());
		
		RollUpField space1RollUpFieldContext = new RollUpField(rollUpFieldContext);
		space1RollUpFieldContext.setChildFieldId(baseSpaceModuleFields.get("space1").getFieldId());
		
		Criteria criteria1 = new Criteria();
		criteria1.addAndCondition(spaceTypeSpace);
		criteria1.addAndCondition(space1NotNull);
		criteria1.addAndCondition(space2Null);
		criteria1.addAndCondition(space3Null);
		criteria1.addAndCondition(space4Null);
		criteria1.addAndCondition(siteNotNull);
		childCriteriaId1 = CriteriaAPI.addCriteria(criteria1);
		space1RollUpFieldContext.setChildCriteriaId(childCriteriaId1);
		rollUpFields.add(space1RollUpFieldContext);
	
		RollUpField space2RollUpFieldContext = new RollUpField(rollUpFieldContext);
		space2RollUpFieldContext.setChildFieldId(baseSpaceModuleFields.get("space2").getFieldId());
		
		Criteria criteria2 = new Criteria();
		criteria2.addAndCondition(spaceTypeSpace);
		criteria2.addAndCondition(space1NotNull);
		criteria2.addAndCondition(space2NotNull);
		criteria2.addAndCondition(space3Null);
		criteria2.addAndCondition(space4Null);
		criteria2.addAndCondition(siteNotNull);
		childCriteriaId2 = CriteriaAPI.addCriteria(criteria2);
		space2RollUpFieldContext.setChildCriteriaId(childCriteriaId2);
		rollUpFields.add(space2RollUpFieldContext);
		
		RollUpField space3RollUpFieldContext = new RollUpField(rollUpFieldContext);
		space3RollUpFieldContext.setChildFieldId(baseSpaceModuleFields.get("space3").getFieldId());
		
		Criteria criteria3 = new Criteria();
		criteria3.addAndCondition(spaceTypeSpace);
		criteria3.addAndCondition(space1NotNull);
		criteria3.addAndCondition(space2NotNull);
		criteria3.addAndCondition(space3NotNull);
		criteria3.addAndCondition(space4Null);
		criteria3.addAndCondition(siteNotNull);
		childCriteriaId3 = CriteriaAPI.addCriteria(criteria3);
		space3RollUpFieldContext.setChildCriteriaId(childCriteriaId3);
		rollUpFields.add(space3RollUpFieldContext);
		
		RollUpField space4RollUpFieldContext = new RollUpField(rollUpFieldContext);
		space4RollUpFieldContext.setChildFieldId(baseSpaceModuleFields.get("space4").getFieldId());
		
		Criteria criteria4 = new Criteria();
		criteria4.addAndCondition(spaceTypeSpace);
		criteria4.addAndCondition(space1NotNull);
		criteria4.addAndCondition(space2NotNull);
		criteria4.addAndCondition(space3NotNull);
		criteria4.addAndCondition(space4NotNull);
		criteria4.addAndCondition(siteNotNull);
		childCriteriaId4 = CriteriaAPI.addCriteria(criteria4);
		space4RollUpFieldContext.setChildCriteriaId(childCriteriaId4);
		rollUpFields.add(space4RollUpFieldContext);
		
		for(RollUpField rollUpField:rollUpFields) {
			rollUpField.setIsSystemRollUpField(true);
		}
		addRollUpField(rollUpFields);
	}
	
	public static void runRollUpFieldForModule(String moduleName) throws Exception{

		FacilioContext context = new FacilioContext();		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		
//		Map<String, Criteria> moduleCriteriaMap = new HashMap<String, Criteria>();
//		moduleCriteriaMap.put(moduleName, null);
		
		FacilioTimer.scheduleInstantJob("ExecuteBulkRollUpFieldJob", context);
	}
	
	public static void runInternalBulkRollUpFieldRules(List<Long> rollUpFieldRuleIds) throws Exception{	
		if(rollUpFieldRuleIds != null && !rollUpFieldRuleIds.isEmpty()) 
		{	
			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.ROLL_UP_FIELD_IDS,rollUpFieldRuleIds);
			FacilioTimer.scheduleInstantJob("ExecuteBulkRollUpFieldJob", context);	
		}
	}
}
