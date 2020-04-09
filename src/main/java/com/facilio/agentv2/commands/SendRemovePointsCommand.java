package com.facilio.agentv2.commands;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.Point;
import org.apache.commons.chain.Context;

import java.util.List;

public class SendRemovePointsCommand extends AgentV2Command {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(containsCheck(AgentConstants.POINT_IDS,context) && (containsCheck(AgentConstants.CONTROLLER_TYPE,context))){
            List<Long> pointIds = (List<Long>) context.get(AgentConstants.POINT_IDS);
            FacilioControllerType controllerType = (FacilioControllerType) context.get(AgentConstants.CONTROLLER_TYPE);
            if((pointIds != null)&&( !pointIds.isEmpty())){
                GetPointRequest getPointRequest = new GetPointRequest().fromIds(pointIds)
                        .ofType(controllerType);
                List<Point> points = getPointRequest.getPoints();
                ControllerMessenger.deletePoints(points);
            }else {
                throw new Exception(" pointIds null or empty->"+pointIds);
            }
        }else {
            throw new Exception(" point ids and controller type are mandatory ");
        }
        return false;
    }
}
