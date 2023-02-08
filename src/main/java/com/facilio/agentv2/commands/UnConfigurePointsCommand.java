package com.facilio.agentv2.commands;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointsAPI;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class UnConfigurePointsCommand extends AgentV2Command {
    private static final Logger LOGGER = LogManager.getLogger(UnConfigurePointsCommand.class.getName());


    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(containsCheck(AgentConstants.POINT_IDS,context) && containsCheck(AgentConstants.CONTROLLER_TYPE,context)){
            Long controllerId = (Long) context.get(AgentConstants.CONTROLLER_ID);
            List<Long> pointIds = (List<Long>) context.get(AgentConstants.POINT_IDS);
            FacilioControllerType controllerType = (FacilioControllerType) context.get(AgentConstants.CONTROLLER_TYPE);

            GetPointRequest getPointRequest = new GetPointRequest()
                    .ofType(controllerType)
                    .fromIds(pointIds);
            if(controllerId != null){
                getPointRequest.withControllerId(controllerId);
            }
            List<Point> points = getPointRequest.getPoints();
            ControllerMessenger.unConfigurePoints(points);
            PointsAPI.UpdatePointsUnConfigured(pointIds);

        }
        return false;
    }
}
