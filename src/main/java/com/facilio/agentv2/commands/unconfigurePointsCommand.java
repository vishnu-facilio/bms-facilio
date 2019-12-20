package com.facilio.agentv2.commands;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointsAPI;
import org.apache.commons.chain.Context;

import java.util.List;

public class unconfigurePointsCommand extends AgentV2Command {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(containsCheck(AgentConstants.POINT_IDS,context) && containsCheck(AgentConstants.TYPE,context)){
            List<Long> pointIds = (List<Long>) context.get(AgentConstants.POINT_IDS);
            PointsAPI.unConfigurePoints(pointIds);
            List<Point> points = PointsAPI.getpoints(pointIds, (FacilioControllerType) context.get(AgentConstants.TYPE));
            ControllerMessenger.unConfigurePoints(points);
        }
        return false;
    }
}
