package com.facilio.bmsconsole.commands;

import com.facilio.agent.AgentType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.commands.AgentV2Command;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.point.Point;
import com.facilio.bmsconsole.commands.util.BmsPointsTaggingUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Log4j
public class ConfiguredPointsMlMigration extends AgentV2Command {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        try{
            List<Point> points = (List<Point>) context.get(AgentConstants.POINT);
            Map<Long, List<String>> controllerIdVsPointNamesMap = new HashMap<>();
            for (Point point : points) {
            if (!controllerIdVsPointNamesMap.containsKey(point.getControllerId())) {
                if (point.getResourceId() == null || point.getFieldId() == null) {
                    List<String> pointNames = new ArrayList<>();
                    pointNames.add(point.getName());
                    controllerIdVsPointNamesMap.put(point.getControllerId(), pointNames);
                }
            } else {
                if (point.getResourceId() == null || point.getFieldId() == null) {
                    controllerIdVsPointNamesMap.get(point.getControllerId()).add(point.getName());
                }
            }
        }

        List<HashMap<String, Object>> finalMapList = new ArrayList<>();
        for (Map.Entry<Long, List<String>> controllervsPointMap : controllerIdVsPointNamesMap.entrySet()) {
            HashMap<String, Object> pointMap = new HashMap<>();
            Controller controller = ControllerApiV2.getControllerFromDb(controllervsPointMap.getKey());
            pointMap.put("pointName", controllervsPointMap.getValue());
            pointMap.put("controller", controller.getName());
            pointMap.put("agentName", controller.getAgent().getName());
            pointMap.put("agentType", AgentType.valueOf(controller.getAgent().getAgentType()).toString());

            finalMapList.add(pointMap);
        }
        BmsPointsTaggingUtil.tagPointListV1(finalMapList);
        //each map contains list of point names belonging to one controller and list has points of all controllers
    }catch(Exception e){
        LOGGER.error("Error while tagging point in ML",e);
        return true;
    }
        return false;
    }
}
