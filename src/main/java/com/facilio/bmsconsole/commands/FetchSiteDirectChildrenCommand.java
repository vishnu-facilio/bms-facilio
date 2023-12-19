package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchSiteDirectChildrenCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Long spaceId = (Long) context.get("siteId");
        List<BuildingContext> siteChildren = SpaceAPI.getSiteBuildings(spaceId);
        List<SpaceContext> independantSpaces = SpaceAPI.getIndependentSpaces(spaceId,context);
        List<Map<String, Object>> siteChildrenProps = new ArrayList<>();
        if(siteChildren != null && !siteChildren.isEmpty()) {
            for (BuildingContext building : siteChildren) {
                Map<String, Object> buildingProp = new HashMap<>();
                buildingProp.put("title", building.getName());
                buildingProp.put("id", building.getId());
                buildingProp.put("isOpen",false);
                buildingProp.put("isEnd",false);
                buildingProp.put("moduleName", building.getSpaceTypeEnum().getStringVal());
                siteChildrenProps.add(buildingProp);
            }
        }
        if(independantSpaces != null && !independantSpaces.isEmpty()) {
            for (SpaceContext space : independantSpaces) {
                Map<String, Object> buildingProp = new HashMap<>();
                buildingProp.put("title", space.getName());
                buildingProp.put("id", space.getId());
                buildingProp.put("moduleName", space.getSpaceTypeEnum().getStringVal());
                buildingProp.put("isOpen",false);
                buildingProp.put("isEnd", false);
                siteChildrenProps.add(buildingProp);
            }
        }
        context.put("SITE_CHILDREN_PROPS",siteChildrenProps);
        return false;
    }

}
