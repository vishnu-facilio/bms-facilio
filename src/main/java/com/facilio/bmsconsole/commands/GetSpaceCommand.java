package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.SkillContext;
import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetSpaceCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long spaceId = (long) context.get(FacilioConstants.ContextNames.ID);
		
		if(spaceId > 0) 
		{
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(moduleName);
			
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
		//	Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			
			SelectRecordsBuilder<SpaceContext> builder = new SelectRecordsBuilder<SpaceContext>()
					.table(module.getTableName())
					.moduleName(moduleName)
					.beanClass(SpaceContext.class)
					.select(fields)
					.andCustomWhere(module.getTableName()+".ID = ?", spaceId)
					.orderBy("ID");

			List<SpaceContext> spaces = builder.get();	
			if(spaces.size() > 0) {
				SpaceContext space = spaces.get(0);
				long buildingId = space.getBuilding().getId();
				long floorId = space.getFloor().getId();
				long spaceCategoryId = space.getSpaceCategory().getId();
				BuildingContext building = getBuildingContext(buildingId);
				if(building != null)
				{
					space.setBuilding(building);
				}
				FloorContext floor = getFloorContext(floorId);
				if(floor != null)
				{
					space.setFloor(floor);
				}
				SpaceCategoryContext spaceCategory = getSpaceCategoryContext(spaceCategoryId);
				if(spaceCategory != null)
				{
					space.setSpaceCategory(spaceCategory);
				}
				context.put(FacilioConstants.ContextNames.SPACE, space);
			}
			
		}
		else {
			throw new IllegalArgumentException("Invalid Space ID : "+spaceId);
		}
		
		return false;
	}
	
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
