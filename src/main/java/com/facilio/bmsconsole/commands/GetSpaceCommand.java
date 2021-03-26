package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.util.LocationAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldType;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetSpaceCommand extends FacilioCommand {

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long spaceId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(spaceId > 0) 
		{
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		//	Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			List<LookupField> fetchLookupsList = new ArrayList<LookupField>();
			List<FacilioField> customLookupFields = fields.stream().filter(field -> (field.getDefault() != null && !field.getDefault()) && (field.getDataType() == FieldType.LOOKUP.getTypeAsInt())).collect(Collectors.toList());
			if (CollectionUtils.isNotEmpty(customLookupFields)) {
				customLookupFields.forEach(field -> fetchLookupsList.add((LookupField) field));
			}
			
			SelectRecordsBuilder<SpaceContext> builder = new SelectRecordsBuilder<SpaceContext>()
					.table(module.getTableName())
					.moduleName(moduleName)
					.beanClass(SpaceContext.class)
					.select(fields)
					.andCustomWhere(module.getTableName()+".ID = ?", spaceId)
					.orderBy("ID").fetchSupplements(fetchLookupsList);

			List<SpaceContext> spaces = builder.get();	
			if(spaces.size() > 0) {
				SpaceContext space = spaces.get(0);
				BuildingContext building = space.getBuilding();
				if(building != null)
				{
					space.setBuilding(getBuildingContext(building.getId()));
				}
				FloorContext floor = space.getFloor();
				if(floor != null)
				{
					space.setFloor(getFloorContext(floor.getId()));
				}
				SpaceCategoryContext spaceCategory = space.getSpaceCategory();
				if(spaceCategory != null)
				{
					space.setSpaceCategory(getSpaceCategoryContext(spaceCategory.getId()));
				}
				long spaceId1 = space.getSpaceId1();
				if(spaceId1 != -1)
				{
					SpaceContext childSpaces1 = SpaceAPI.getSpace(spaceId1);
					space.setSpace1(childSpaces1);
				}
				long spaceId2 = space.getSpaceId2();
				if(spaceId2 != -1)
				{
					SpaceContext childSpaces2 = SpaceAPI.getSpace(spaceId2);
					space.setSpace2(childSpaces2);
				}
				long spaceId3 = space.getSpaceId3();
				if(spaceId3 != -1)
				{
					SpaceContext childSpaces1 = SpaceAPI.getSpace(spaceId3);
					space.setSpace3(childSpaces1);
				}
				SpaceContext tempSpace4 = space.getSpace4();
				if(tempSpace4 != null)
				{
					SpaceContext childSpaces1 = SpaceAPI.getSpace(tempSpace4.getId());
					space.setSpace4(childSpaces1);
				}
				LocationContext location = space.getLocation();
				if(location != null) {
					 Map<Long, LocationContext> locationMap = LocationAPI.getLocationMap(Collections.singletonList(location.getId()));
					if(locationMap != null && !locationMap.isEmpty()) {
						space.setLocation(locationMap.get(location.getId()));
					}
				}
				context.put(FacilioConstants.ContextNames.SPACE, space);
			}
			
		}
		else {
			throw new IllegalArgumentException("Invalid Space ID : "+spaceId);
		}
		
		return false;
	}
	
//	public SpaceContext getSpaceContext(long Id) throws Exception
//	{
//		SpaceContext spaceContext = null;
//		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//		FacilioModule module  = modBean.getModule(FacilioConstants.ContextNames.SPACE);
//		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SPACE);
//		SelectRecordsBuilder<SpaceContext> builder = new SelectRecordsBuilder<SpaceContext>()
//				.module(module)
//				.beanClass(SpaceContext.class)
//				.select(fields)
//				.andCondition(CriteriaAPI.getIdCondition(Id, module));
//		List<SpaceContext> spaceContexts = builder.get();
//		if(spaceContexts.size() > 0)
//		{
//			spaceContext = spaceContexts.get(0);
//		}
//		return spaceContext;
//	}
//	
	public SpaceCategoryContext getSpaceCategoryContext(long Id) throws Exception
	{
		SpaceCategoryContext spaceCategory = null;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module  = modBean.getModule(FacilioConstants.ContextNames.SPACE_CATEGORY);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.SPACE_CATEGORY);
		SelectRecordsBuilder<SpaceCategoryContext> builder = new SelectRecordsBuilder<SpaceCategoryContext>()
				.module(module)
				.beanClass(SpaceCategoryContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getIdCondition(Id, module));
		List<SpaceCategoryContext> spaceCategories = builder.get();
		if(spaceCategories.size() > 0)
		{
			spaceCategory = spaceCategories.get(0);
		}
		return spaceCategory;
	}
	
	public FloorContext getFloorContext(long Id) throws Exception
	{
		FloorContext floor = null;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module  = modBean.getModule(FacilioConstants.ContextNames.FLOOR);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.FLOOR);
		SelectRecordsBuilder<FloorContext> builder = new SelectRecordsBuilder<FloorContext>()
				.module(module)
				.beanClass(FloorContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getIdCondition(Id, module));
		List<FloorContext> floors = builder.get();
		if(floors.size() > 0)
		{
			floor = floors.get(0);
		}
		return floor;
	}
	
	public BuildingContext getBuildingContext(long Id) throws Exception
	{
		BuildingContext building = null;
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module  = modBean.getModule(FacilioConstants.ContextNames.BUILDING);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BUILDING);
		SelectRecordsBuilder<BuildingContext> builder = new SelectRecordsBuilder<BuildingContext>()
				.module(module)
				.beanClass(BuildingContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getIdCondition(Id, module));
		List<BuildingContext> buildings = builder.get();
		if(buildings.size() > 0)
		{
			building = buildings.get(0);
		}
		return building;
	}

}
