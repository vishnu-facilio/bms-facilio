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
        getIconMap(context); // for icon support in tree
        Long spaceId = (Long) context.get("siteId");
        Map<String,Object> iconMap = (Map<String, Object>) context.get("iconMap");
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

                Map<String,String> buildingIconMap = (Map<String, String>) iconMap.get("building");
                buildingProp.put("icongroup",buildingIconMap.get("icongroup"));
                buildingProp.put("iconname",buildingIconMap.get("iconname"));

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

                Map<String,String> spaceIconMap = (Map<String, String>) iconMap.get("space");
                buildingProp.put("icongroup",spaceIconMap.get("icongroup"));
                buildingProp.put("iconname",spaceIconMap.get("iconname"));

                siteChildrenProps.add(buildingProp);
            }
        }
        context.put("SITE_CHILDREN_PROPS",siteChildrenProps);
        return false;
    }

    private void getIconMap(Context context){
        Map<String,Object> iconModuleMap = new HashMap<>();
        Map<String,String> iconMap = new HashMap<>();

        iconMap.put("icongroup","portfolio");
        iconMap.put("iconname","building");
        iconModuleMap.put("building",iconMap);

        iconMap = new HashMap<>();
        iconMap.put("icongroup","default");
        iconMap.put("iconname","floorstack");
        iconModuleMap.put("floor",iconMap);

        iconMap = new HashMap<>();
        iconMap.put("icongroup","default");
        iconMap.put("iconname","workspace");
        iconModuleMap.put("space",iconMap);
        context.put("iconMap",iconModuleMap);
    }

}
