package com.facilio.bmsconsole.commands;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.point.PointsUtil;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddPointsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Controller controller = (Controller) context.get(AgentConstants.CONTROLLER);
        List<Map<String,Object>> points = (List<Map<String, Object>>) context.get(AgentConstants.POINTS);
        PointsUtil.addPoints(controller,points);

        FacilioAgent agent = (FacilioAgent) context.get(AgentConstants.AGENT);
        context.put(AgentConstants.POINT_NAMES,mlPointsToTag(points,agent));
        return false;
    }
    public List<String> mlPointsToTag(List<Map<String,Object>>points, FacilioAgent agent){
        List<String>pointNames = new ArrayList<>();
        for (Map<String,Object>point: points){
            if((int) point.get(AgentConstants.CONFIGURE_STATUS)==3){
                pointNames.add((String) point.get("name"));
            }
        }


        return pointNames;
    }
}
