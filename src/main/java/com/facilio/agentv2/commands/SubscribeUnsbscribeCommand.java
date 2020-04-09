package com.facilio.agentv2.commands;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointsAPI;
import org.apache.commons.chain.Context;

import java.util.List;

public class SubscribeUnsbscribeCommand extends AgentV2Command {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(containsCheck(AgentConstants.POINT_IDS,context) && containsCheck(AgentConstants.CONTROLLER_TYPE,context) && containsCheck(AgentConstants.COMMAND,context)){
            List<Long> pointIds = (List<Long>) context.get(AgentConstants.POINT_IDS);
            if((pointIds == null) || (  pointIds.isEmpty())){
                throw new Exception("pointIds cant be empty ");
            }
            FacilioCommand command = (FacilioCommand) context.get(AgentConstants.COMMAND);
            PointsAPI.updatePointsSubscribedOrUnsubscribed(pointIds, command);
            GetPointRequest getPointRequest = new GetPointRequest().fromIds(pointIds)
                    .ofType((FacilioControllerType) context.get(AgentConstants.CONTROLLER_TYPE));
            List<Point> points = getPointRequest.getPoints();
            if(FacilioCommand.UNSUBSCRIBE == command){
                ControllerMessenger.subscribeUnscbscribePoints(points,command);
            }
            else if(FacilioCommand.SUBSCRIBE == command){
                ControllerMessenger.subscribeUnscbscribePoints(points,command);
            }
            else {
                throw new Exception(" command cant be anything other than sub  and unsub");
            }
        }
        return false;
    }
}
