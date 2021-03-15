package com.facilio.bmsconsoleV3.commands;

import java.util.HashMap;
import java.util.List;

import com.facilio.bmsconsoleV3.context.V3FloorContext;
import com.facilio.bmsconsoleV3.context.V3SpaceContext;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext.SpaceType;
import com.facilio.bmsconsoleV3.context.V3LockersContext;
import com.facilio.bmsconsole.util.SpaceAPI;

public class AddLockersSpaceCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		HashMap record = (HashMap)context.get("recordMap");
		
		List<V3LockersContext> spaces = (List<V3LockersContext>)record.get("lockers");
		
		if (spaces != null && !spaces.isEmpty()) {
			for (V3LockersContext space : spaces) {
				space.setSpaceType(SpaceType.SPACE);
				updateSiteAndBuildingId(space);
			}
		}
		return false;
		
	}
	
	public static void updateSiteAndBuildingId(V3LockersContext space) throws Exception {
		if(space.getBuilding() != null && space.getBuilding().getId() > 0) {
			long buildingId = space.getBuilding().getId();
			BuildingContext building = SpaceAPI.getBuildingSpace(buildingId);
			space.setSiteId(building.getSiteId());
		}
		if(space.getFloor() != null && space.getFloor().getId() > 0) {
			long floorId = space.getFloor().getId();
			V3FloorContext floor = SpaceAPI.getV3FloorSpace(floorId);
			space.setSiteId(floor.getSiteId());
			space.setBuilding(floor.getBuilding());
		}
		if (space.getParentSpace() != null && space.getParentSpace().getId() > 0) {
			long spaceId = space.getParentSpace().getId();
			V3SpaceContext spaces = SpaceAPI.getV3Space(spaceId);
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
