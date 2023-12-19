package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import java.util.*;

public class FetchSpaceDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long firstParentId = -1;
        long siteId = -1;
        long buildingId = -1;
        do {
            long spaceId = (long) context.getOrDefault("parentId", context.get("spaceId"));
            BaseSpaceContext spaceContext = SpaceAPI.getBaseSpace(spaceId);
            String spaceType = spaceContext.getSpaceTypeEnum().getStringVal();
            switch (spaceType) {
                case "Space":
                    firstParentId = spaceContext.getSpaceId5() != -1 ? spaceContext.getSpaceId5() : (
                            spaceContext.getSpaceId4() != -1 ? spaceContext.getSpaceId4() : (
                                    spaceContext.getSpaceId3() != -1 ? spaceContext.getSpaceId3() : (
                                            spaceContext.getSpaceId2() != -1 ? spaceContext.getSpaceId2() : (
                                                    spaceContext.getSpaceId1() != -1 ? spaceContext.getSpaceId1() : (
                                                            spaceContext.getFloorId() != -1 ? spaceContext.getFloorId() : (
                                                                    spaceContext.getBuildingId() != -1 ? spaceContext.getBuildingId() : (
                                                                            spaceContext.getSiteId()
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                    );
                    if(firstParentId == spaceContext.getSiteId()){
                        siteId = firstParentId;
                    }
                    if(firstParentId == spaceContext.getBuildingId()){
                        buildingId = firstParentId;
                    }
                    break;
                case "Floor":
                    firstParentId = spaceContext.getBuildingId();
                    buildingId = firstParentId;
                    break;
                case "Building":
                    firstParentId = spaceContext.getSiteId();
                    siteId = firstParentId;
                    break;
                default:
                    firstParentId = -1;
            }
            if(firstParentId != -1) {
                if(siteId != firstParentId) {
                    List<Long> expandedKeys = (List<Long>) context.getOrDefault("expandedKeys", new ArrayList<Long>());
                    expandedKeys.add(firstParentId);
                    context.put("expandedKeys", expandedKeys);
                }
                context.put("parentId", firstParentId);
                context.put("buildingId", buildingId);
                context.put("siteId", siteId);
            }
        }while (firstParentId != -1);
        return false;
    }
}
