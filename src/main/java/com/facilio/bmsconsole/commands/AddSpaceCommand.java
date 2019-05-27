package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

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
		if(space.getFloor().getId() != -1 && space.getFloor().getId() != 0) {
			long floorId = space.getFloor().getId();
			FloorContext floor = SpaceAPI.getFloorSpace(floorId);
			space.setSiteId(floor.getSiteId());
			space.setBuilding(floor.getBuilding());
		}
		if (space.getParentSpace() != null && space.getParentSpace().getId() > -1) {
			long spaceId = space.getParentSpace().getId();
			SpaceContext spaces = SpaceAPI.getSpace(spaceId);
			space.setSiteId(spaces.getSiteId());
			space.setBuilding(spaces.getBuilding());
			space.setFloorId(spaces.getFloorId());
			if (spaces.getSpaceId3() > 0) {
				space.setSpaceId4(spaceId);
				space.setSpaceId3(spaces.getSpaceId3());
				space.setSpaceId2(spaces.getSpaceId2());
				space.setSpaceId1(spaces.getSpaceId1());
			}
			else if (spaces.getSpaceId2() > 0) {
				space.setSpaceId3(spaceId);
				space.setSpaceId2(spaces.getSpaceId2());
				space.setSpaceId1(spaces.getSpaceId1());
			}
			else if (spaces.getSpaceId1() > 0) {
				space.setSpaceId2(spaceId);
				space.setSpaceId1(spaces.getSpaceId1());
			}
			else {
				space.setSpaceId1(spaceId);
			}
		}
	}
}
