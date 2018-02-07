package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;

public class AddSpaceCommand implements Command {
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		SpaceContext space = (SpaceContext) context.get(FacilioConstants.ContextNames.SPACE);
		if(space != null) 
		{
			space.setSpaceType(SpaceType.SPACE);
			updateSiteAndBuildingId(space);
			
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			String dataTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME);
			List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.EXISTING_FIELD_LIST);
			
			InsertRecordBuilder<SpaceContext> builder = new InsertRecordBuilder<SpaceContext>()
															.moduleName(moduleName)
															.table(dataTableName)
															.fields(fields);
															
			long id = builder.insert(space);
			space.setId(id);
			SpaceAPI.updateHelperFields(space);
			context.put(FacilioConstants.ContextNames.RECORD_ID, id);
		}
		else 
		{
			throw new IllegalArgumentException("Space Object cannot be null");
		}
		return false;
	}
	
	private void updateSiteAndBuildingId(SpaceContext space) throws Exception {
		if(space.getBuilding() != null) {
			long buildingId = space.getBuilding().getId();
			BuildingContext building = SpaceAPI.getBuildingSpace(buildingId);
			space.setSiteId(building.getSiteId());
		}
		
		if(space.getFloor() != null) {
			long floorId = space.getFloor().getId();
			FloorContext floor = SpaceAPI.getFloorSpace(floorId);
			space.setSiteId(floor.getSiteId());
			space.setBuilding(floor.getBuilding());
		}
	}
}
