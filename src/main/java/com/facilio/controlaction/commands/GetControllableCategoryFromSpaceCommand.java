package com.facilio.controlaction.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.controlaction.context.ControlActionCommandContext;
import com.facilio.controlaction.context.ControlPointContext;
import com.facilio.controlaction.context.ControllableAssetCategoryContext;
import com.facilio.controlaction.context.ControllableResourceContext;
import com.facilio.controlaction.context.ControllableAssetCategoryContext.ControllableCategory;
import com.facilio.controlaction.context.ControllablePointContext;
import com.facilio.controlaction.context.ControllablePointContext.ControllablePoints;
import com.facilio.controlaction.util.ControlActionUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.mssql.SelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.mv.context.MVProjectContext;

public class GetControllableCategoryFromSpaceCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		long spaceId = (long)context.get(FacilioConstants.ContextNames.SPACE_ID);
		
		ModuleBean modbean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule assetModule = modbean.getModule(FacilioConstants.ContextNames.ASSET);
		
		List<FacilioField> fields = new ArrayList<FacilioField>();
		
		fields.addAll(modbean.getAllFields(assetModule.getName()));
		
		fields.addAll(FieldFactory.getReadingDataMetaFields());
		fields.addAll(FieldFactory.getControlPointFields());
		fields.addAll(FieldFactory.getControllablePointFields());
		
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
		
		List<Integer> categoryIncludeIds = (List<Integer>) context.get(ControlActionUtil.CATEGORY_INCLUDE_LIST);
		List<Integer> categoryExcludeIds = (List<Integer>) context.get(ControlActionUtil.CATEGORY_EXCLUDE_LIST);
				
		SelectRecordsBuilder<ModuleBaseWithCustomFields> select = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
			.module(assetModule)
			.beanClass(ModuleBaseWithCustomFields.class)
				.select(fields)
				
				.innerJoin(ModuleFactory.getReadingDataMetaModule().getTableName())
				.on(ModuleFactory.getReadingDataMetaModule().getTableName()+".RESOURCE_ID = "+assetModule.getTableName()+".ID")
				.leftJoin(ModuleFactory.getControlPointModule().getTableName())
				.on(ModuleFactory.getReadingDataMetaModule().getTableName()+".ID = "+ModuleFactory.getControlPointModule().getTableName()+".ID")
				.innerJoin(ModuleFactory.getControllablePointModule().getTableName())
				.on(ModuleFactory.getReadingDataMetaModule().getTableName()+".FIELD_ID = "+ModuleFactory.getControllablePointModule().getTableName()+".FIELD_ID")
				
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("space"), spaceId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("isControllable"), Boolean.TRUE.toString(), BooleanOperators.IS))
				;
		
		if(categoryIncludeIds != null && !categoryIncludeIds.isEmpty()) {
			
			List<Integer> controllablePoints = new ArrayList<>(); 
			for(Integer categoryIncludeId : categoryIncludeIds) {
				
				ControllableCategory category = ControllableCategory.getControllableCategoryMap().get(categoryIncludeId);
				
				for(ControllablePoints point : category.getPoints()) {
					controllablePoints.add(point.getPointId());
				}
			}
			select.andCondition(CriteriaAPI.getCondition(fieldsMap.get("controllablePoint"), StringUtils.join(controllablePoints, ","), NumberOperators.EQUALS));
		}
		if(categoryExcludeIds != null  && !categoryExcludeIds.isEmpty()) {
			
			List<Integer> controllablePoints = new ArrayList<>(); 
			for(Integer categoryExcludeId : categoryExcludeIds) {
				
				ControllableCategory category = ControllableCategory.getControllableCategoryMap().get(categoryExcludeId);
				
				for(ControllablePoints point : category.getPoints()) {
					controllablePoints.add(point.getPointId());
				}
			}
			
			select.andCondition(CriteriaAPI.getCondition(fieldsMap.get("controllablePoint"), StringUtils.join(controllablePoints, ","), NumberOperators.NOT_EQUALS));
		}
		
		List<Map<String, Object>> props = select.getAsProps();
		
		
		if(props != null && !props.isEmpty()) {
			
			Map<Long,ControllableResourceContext> controllableResourceMap = new HashMap<Long, ControllableResourceContext>();// temp
			
			Map<Long,ControllableAssetCategoryContext> controllableAssetCategoryMap = new HashMap<Long, ControllableAssetCategoryContext>();
			
			for(Map<String, Object> prop : props) {
				
				long resourceId = (long) prop.get("resourceId");
				
				long categoryId = (long) ((Map)prop.get("category")).get("id");
				
				ControllableResourceContext controllableResourceContext = controllableResourceMap.get(resourceId);
				
				ControllableAssetCategoryContext controllableAssetCategory = controllableAssetCategoryMap.get(categoryId);
				if(controllableResourceContext == null) {
					
					AssetContext asset = FieldUtil.getAsBeanFromMap(prop, AssetContext.class);
					asset.setId(resourceId);

					if(controllableAssetCategory == null) {
						controllableAssetCategory = ControlActionUtil.getControllableAssetCategory(asset.getCategory().getId());
						controllableAssetCategoryMap.put(asset.getCategory().getId(), controllableAssetCategory);
					}
					
					asset.setCategory(controllableAssetCategory);
					
					controllableResourceContext = new ControllableResourceContext(asset);
					
					controllableResourceMap.put(resourceId, controllableResourceContext);
					
					controllableAssetCategory.addControllableResourceContexts(controllableResourceContext);
					
				}
				
				ControlPointContext controlPointcontext = FieldUtil.getAsBeanFromMap(prop, ControlPointContext.class);
				
				ReadingsAPI.fillExtendedPropertiesForRDM(controlPointcontext, null);
				ReadingsAPI.convertUnitForRdmData(controlPointcontext);
				
				if(controlPointcontext.getChildRDMId() > 0) {
					ReadingDataMeta childRDM = ReadingsAPI.getReadingDataMeta(controlPointcontext.getChildRDMId());
					ReadingsAPI.convertUnitForRdmData(childRDM);
					controlPointcontext.setChildRDM(childRDM);
				}
				
				ControlActionCommandContext lastExecutedCommand = ControlActionUtil.getLastExecutedCommandGreaterThanSpecificTime(controlPointcontext.getResourceId(), controlPointcontext.getFieldId(), controlPointcontext.getTtime());
				
				if(lastExecutedCommand != null) {
					controlPointcontext.setValue(lastExecutedCommand.getValue());
					controlPointcontext.setTtime(lastExecutedCommand.getExecutedTime());
					controlPointcontext.setValueFromCommand(true);
				}
				
				ControllablePointContext controllablePoint = FieldUtil.getAsBeanFromMap(prop, ControllablePointContext.class);
				
				controllableResourceContext.addControllablePointMap(controllablePoint.getControllablePoint(), controlPointcontext);
				
				controllableResourceContext.addControllablePoints(controllablePoint.getControllableEnum());
				
				controllableAssetCategory.addAvailablePoints(controllablePoint.getControllableEnum());
			}
			
			context.put(ControlActionUtil.CONTROLLABLE_CATEGORIES, controllableAssetCategoryMap);
		}
		return false;
	}

}
