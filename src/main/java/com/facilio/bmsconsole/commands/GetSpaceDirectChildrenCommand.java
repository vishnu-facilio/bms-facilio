package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.FloorContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class GetSpaceDirectChildrenCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Long baseSpaceId = (Long) context.get(FacilioConstants.ContextNames.BASE_SPACE_ID);
        ArrayList childrenList = new ArrayList();
        if (baseSpaceId != null && baseSpaceId > 0) {
            BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(baseSpaceId);
            if (baseSpace != null) {
                if (baseSpace.getSpaceType() == BaseSpaceContext.SpaceType.SITE.getIntVal()) {
                    List<SpaceContext> spaceList = SpaceAPI.getIndependentSpaces(baseSpaceId, context);
                    List<BuildingContext> buildingsList = SpaceAPI.getSiteBuildings(baseSpaceId);

                    if (CollectionUtils.isNotEmpty(buildingsList)) {
                        childrenList.addAll(buildingsList);
                    }
                    if (CollectionUtils.isNotEmpty(spaceList)) {
                        childrenList.addAll(spaceList);
                    }
                } else if (baseSpace.getSpaceType() == BaseSpaceContext.SpaceType.BUILDING.getIntVal()) {
                    List<FloorContext> floorsList = SpaceAPI.getBuildingsFloorsContext(baseSpaceId,context);
                    List<SpaceContext> spaceList = SpaceAPI.getIndependentSpaces(baseSpaceId, context);
                    if (CollectionUtils.isNotEmpty(floorsList)) {
                        childrenList.addAll(floorsList);
                    }
                    if (CollectionUtils.isNotEmpty(spaceList)) {
                        childrenList.addAll(spaceList);
                    }
                }
            }
        }

        context.put(FacilioConstants.ContextNames.BASE_SPACE_LIST, childrenList);

        return false;
    }
}
