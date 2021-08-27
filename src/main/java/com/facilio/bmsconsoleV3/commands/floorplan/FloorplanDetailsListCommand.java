package com.facilio.bmsconsoleV3.commands.floorplan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.facilio.modules.ModuleBaseWithCustomFields;
import org.apache.commons.collections4.CollectionUtils;



import org.apache.commons.chain.Context;

import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;

import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsoleV3.context.V3EmployeeContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanContext;
import com.facilio.bmsconsoleV3.context.floorplan.V3IndoorFloorPlanContext.FloorPlanType;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.beans.ModuleBean;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.db.criteria.operators.LookupOperator;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.LookupField;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.SupplementRecord;
import com.facilio.v3.V3Builder.V3Config;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import com.facilio.v3.context.Constants;
import com.facilio.v3.util.ChainUtil;
import com.facilio.db.criteria.operators.StringOperators;



public class FloorplanDetailsListCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE);
		String search = (String) context.get(FacilioConstants.ContextNames.SEARCH);
		long floorId =  (long) context.get(FacilioConstants.ContextNames.FLOOR);
		long floorplanId =  (long) context.get(FacilioConstants.ContextNames.FLOOR_PLAN_ID);
		Criteria filterCriteria = (Criteria) context.get(Constants.FILTER_CRITERIA);
		List<JSONObject> moduleDataList = new ArrayList<>();
		List<String> modules = new ArrayList<>();
		ModuleBean modBean = (ModuleBean)BeanFactory.lookup("ModuleBean");
		if(moduleName != null && StringUtils.isNotEmpty((String) moduleName)) {
			modules.add(moduleName);
		} else {
			modules.add(FacilioConstants.ContextNames.EMPLOYEE);
			modules.add(FacilioConstants.ContextNames.Floorplan.DESKS);
			modules.add(FacilioConstants.ContextNames.SPACE);
			modules.add(FacilioConstants.ContextNames.LOCKERS);
			modules.add(FacilioConstants.ContextNames.PARKING_STALL);
		}
		
		for(String module : modules) {
			FacilioModule moduleObj = modBean.getModule(module);
			List<FacilioField> fieldsList = new ArrayList<>();
			fieldsList.addAll(modBean.getAllFields(module));
			FacilioField idField = FieldFactory.getIdField(moduleObj);

			Map<String, FacilioField> fields = FieldFactory.getAsMap(fieldsList);
			Criteria searchCriteria = constructMainFieldSearchCriteria(module, (String) search);
			Criteria criteria = new Criteria();
			List<SupplementRecord> supplementFields = new ArrayList<>();
			
			if(floorId > 0) {
				criteria.addAndCondition(CriteriaAPI.getCondition(fields.get("floor"), String.valueOf(floorId) , NumberOperators.EQUALS));
			}
			
			if(floorplanId > 0 && floorId < 0) {
				V3IndoorFloorPlanContext floorplan = (V3IndoorFloorPlanContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.Floorplan.INDOOR_FLOORPLAN, floorplanId, V3IndoorFloorPlanContext.class);
				if(floorplan.getFloor() != null) {
					floorId = floorplan.getFloor().getId();
				}
			}
			
			if (module.equalsIgnoreCase(FacilioConstants.ContextNames.EMPLOYEE)) {
				supplementFields.add((SupplementRecord) fields.get("department"));
				supplementFields.add((SupplementRecord) fields.get("space"));
			}
			else if(floorId > 0) {
				
				FacilioModule spaceCategorymodule  = modBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY);
				List<FacilioField> spaceCategoryFields = modBean.getAllFields(FacilioConstants.ContextNames.SPACE_CATEGORY);
				

				SelectRecordsBuilder<SpaceCategoryContext> builder = new SelectRecordsBuilder<SpaceCategoryContext>()
						.module(spaceCategorymodule)
						.beanClass(SpaceCategoryContext.class)
						.select(spaceCategoryFields);
				if(module.equalsIgnoreCase(FacilioConstants.ContextNames.SPACE)) {
					
					builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(spaceCategorymodule), FacilioConstants.ContextNames.SpaceCategory.DESK, StringOperators.ISN_T))
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(spaceCategorymodule), FacilioConstants.ContextNames.SpaceCategory.LOCKERS, StringOperators.ISN_T))
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(spaceCategorymodule), FacilioConstants.ContextNames.SpaceCategory.PARKING_STALL, StringOperators.ISN_T));
					
				} else if(module.equalsIgnoreCase(FacilioConstants.ContextNames.Floorplan.DESKS)) {
					
					builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(spaceCategorymodule), FacilioConstants.ContextNames.SpaceCategory.DESK, StringOperators.IS));
					
				} else if(module.equalsIgnoreCase(FacilioConstants.ContextNames.LOCKERS)) {
					
					builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(spaceCategorymodule), FacilioConstants.ContextNames.SpaceCategory.LOCKERS, StringOperators.IS));
					
				} else if(module.equalsIgnoreCase(FacilioConstants.ContextNames.PARKING_STALL)) {
					
					builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(spaceCategorymodule), FacilioConstants.ContextNames.SpaceCategory.PARKING_STALL, StringOperators.IS));
					
				}
				List<SpaceCategoryContext> spaceCategories = builder.get();
					List<Long> categoryIds = new ArrayList<>();
					spaceCategories.forEach(i -> categoryIds.add(i.getId()));
					
					criteria.addAndCondition(CriteriaAPI.getCondition(fields.get("spaceCategory"), StringUtils.join(categoryIds,",") , NumberOperators.EQUALS));
			}
			
			V3Config v3Config = ChainUtil.getV3Config(module);
            Class beanClass = ChainUtil.getBeanClass(v3Config, moduleObj);
			
			SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordsBuilder = new SelectRecordsBuilder<>()
	                .module(moduleObj)
	                .beanClass(beanClass);
			
			if(!module.equalsIgnoreCase(FacilioConstants.ContextNames.EMPLOYEE) && floorplanId > 0) {
				
				Map<String, FacilioField> objectfields = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN_OBJECTS));
				FacilioField floorplanField = objectfields.get("indoorfloorplan");
				FacilioModule floorplanObjModule = modBean.getModule(FacilioConstants.ContextNames.INDOOR_FLOOR_PLAN_OBJECTS);
				FacilioField floorplanIdField = FieldFactory.getIdField(floorplanObjModule);
				
				// Assuming only desks as markers - should be changed 
				if(module.equalsIgnoreCase(FacilioConstants.ContextNames.Floorplan.DESKS)){
					Map<String, FacilioField> markerfields = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.Floorplan.MARKER));
					FacilioField markermoduleField = markerfields.get("markerModuleId");
					FacilioModule markerModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.MARKER);
					FacilioField markerfloorPlanIdField = markerfields.get("indoorfloorplan");
					FacilioField markerRecordIdField = markerfields.get("recordId");
					FacilioField markerIdField = FieldFactory.getIdField(markerModule);
					
					selectRecordsBuilder
					.innerJoin(markerModule.getTableName())
					.on(markerRecordIdField.getCompleteColumnName()+" = "+ idField.getCompleteColumnName())
					.innerJoin(floorplanObjModule.getTableName())
					.on( floorplanIdField.getCompleteColumnName() +" = "+ markerIdField.getCompleteColumnName())
					.andCondition(CriteriaAPI.getCondition(markermoduleField, String.valueOf(moduleObj.getModuleId()), NumberOperators.EQUALS))
					.andCondition(CriteriaAPI.getCondition(markerfloorPlanIdField, String.valueOf(floorplanId), NumberOperators.EQUALS));
				} else {
					Map<String, FacilioField> zonefields = FieldFactory.getAsMap(modBean.getAllFields(FacilioConstants.ContextNames.Floorplan.MARKED_ZONES));
					FacilioModule markedzoneModule = modBean.getModule(FacilioConstants.ContextNames.Floorplan.MARKED_ZONES);
					FacilioField markedzoneplanIdField = zonefields.get("indoorfloorplan");
					FacilioField markedzonespaceIdField = zonefields.get("space");
					FacilioField markedzoneIdField = FieldFactory.getIdField(markedzoneModule);
					
					selectRecordsBuilder
					.innerJoin(markedzoneModule.getTableName())
					.on(markedzonespaceIdField.getCompleteColumnName()+" = "+ idField.getCompleteColumnName())
					.innerJoin(floorplanObjModule.getTableName())
					.on( floorplanIdField.getCompleteColumnName() +" = "+ markedzoneIdField.getCompleteColumnName())
					.andCondition(CriteriaAPI.getCondition(markedzoneplanIdField, String.valueOf(floorplanId), NumberOperators.EQUALS));
				}
			}
			
			if (searchCriteria != null && !searchCriteria.isEmpty()) {
	            selectRecordsBuilder.andCriteria(searchCriteria);
	        }

	        if (criteria != null && !criteria.isEmpty()) {
	            selectRecordsBuilder.andCriteria(criteria);
	        }
	        
	        if (filterCriteria != null && !filterCriteria.isEmpty()) {
	            selectRecordsBuilder.andCriteria(filterCriteria);
	        }
	        
	        if (supplementFields != null && CollectionUtils.isNotEmpty(supplementFields)) {
	            selectRecordsBuilder.fetchSupplements(supplementFields);
	        }

	        selectRecordsBuilder.select(modBean.getAllFields(module));
	        List<? extends ModuleBaseWithCustomFields> records = selectRecordsBuilder.get();

	        JSONObject moduleDataObj = new JSONObject();
	        moduleDataObj.put("name", moduleObj.getName());
	        moduleDataObj.put("displayName", moduleObj.getDisplayName());
	        moduleDataObj.put(FacilioConstants.ContextNames.DATA, records);
	        moduleDataList.add(moduleDataObj);
	        
		}
		
		context.put(FacilioConstants.ContextNames.DATA, moduleDataList);

		return false;
	}
	
	private Criteria constructMainFieldSearchCriteria(String moduleName, String search) throws Exception {
		Criteria criteria = null;	
		if (moduleName != null && search != null && search instanceof String && StringUtils.isNotEmpty((String) search)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField primaryField = modBean.getPrimaryField(moduleName);
					
			criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition(primaryField, (String) search, StringOperators.CONTAINS));
			
		}
		return criteria;
	}

	
}