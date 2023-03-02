package com.facilio.bmsconsole.commands;

import com.facilio.agent.AgentType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.agentv2.commands.AgentV2Command;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.point.Point;
import com.facilio.bmsconsole.commands.util.BmsPointsTaggingUtil;
import com.facilio.fw.BeanFactory;
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
            AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
            Long agentId = (Long) context.get(AgentConstants.AGENT_ID);
            FacilioAgent agent = agentBean.getAgent(agentId);
            context.put(AgentConstants.AGENT,agent);
            Map<Long, List<String>> controllerIdVsPointNamesMap = new HashMap<>();
            for (Point point : points) {
            if (!controllerIdVsPointNamesMap.containsKey(point.getControllerId())) {

                    List<String> pointNames = new ArrayList<>();
                    if (agent.getAgentType()==AgentType.NIAGARA.getKey()){
                        pointNames.add(point.getDisplayName());
                    }else{
                        pointNames.add(point.getName());
                    }
                    controllerIdVsPointNamesMap.put(point.getControllerId(), pointNames);
            } else {
                if (agent.getAgentType()==AgentType.NIAGARA.getKey()){
                    controllerIdVsPointNamesMap.get(point.getControllerId()).add(point.getDisplayName());
                }else{
                    controllerIdVsPointNamesMap.get(point.getControllerId()).add(point.getName());
                }
            }
        }

        List<HashMap<String, Object>> finalMapList = new ArrayList<>();
        for (Map.Entry<Long, List<String>> controllervsPointMap : controllerIdVsPointNamesMap.entrySet()) {
            HashMap<String, Object> pointMap = new HashMap<>();
            Controller controller = AgentConstants.getControllerBean().getControllerFromDb(controllervsPointMap.getKey());
            pointMap.put("pointName", controllervsPointMap.getValue());
            pointMap.put("controller", controller.getName());
            pointMap.put("agentName", agent.getName());
            pointMap.put("agentType", AgentType.valueOf(agent.getAgentType()).toString());
            pointMap.put("agentId",agent.getId());
            pointMap.put("siteId",agent.getSiteId());
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
