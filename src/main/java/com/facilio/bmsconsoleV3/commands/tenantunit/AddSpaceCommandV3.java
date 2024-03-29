package com.facilio.bmsconsoleV3.commands.tenantunit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsoleV3.context.V3FloorContext;
import com.facilio.bmsconsoleV3.context.V3SpaceContext;
import com.facilio.bmsconsoleV3.context.inventory.V3TransferRequestContext;
import com.facilio.chain.FacilioContext;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsoleV3.context.V3BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.V3TenantUnitSpaceContext;
import org.apache.commons.collections4.CollectionUtils;

public class AddSpaceCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		String moduleName = Constants.getModuleName(context);
		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
		List<V3TenantUnitSpaceContext> spaces = recordMap.get(moduleName);
		if (CollectionUtils.isNotEmpty(spaces)) {
			for (V3TenantUnitSpaceContext unitspace : spaces) {
				unitspace.setSpaceType(SpaceType.SPACE);
				updateSiteAndBuildingId(unitspace);
			}
		}
		return false;
		
	}
	
	public static void updateSiteAndBuildingId(V3TenantUnitSpaceContext space) throws Exception {
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
