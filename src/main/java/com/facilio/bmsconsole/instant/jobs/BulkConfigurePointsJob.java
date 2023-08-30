package com.facilio.bmsconsole.instant.jobs;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.Point;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.taskengine.job.InstantJob;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BulkConfigurePointsJob extends InstantJob {
    @Override
    public void execute(FacilioContext facilioContext) throws Exception {
        List<Long> pointIds = (List<Long>) facilioContext.get(AgentConstants.POINT_IDS);
        FacilioControllerType controllerType = (FacilioControllerType) facilioContext.get(AgentConstants.CONTROLLER_TYPE);
        int interval = (int) facilioContext.get(AgentConstants.DATA_INTERVAL);
        boolean logical = (boolean) facilioContext.get(AgentConstants.LOGICAL);

        List<Point> points = new GetPointRequest().ofType(controllerType).fromIds(pointIds).getPoints();
        if(!points.isEmpty()){
            Map<Long, List<Point>> controllerIdVsPointsMap = getControllerIdVsPointsMap(points);
            long agentId = points.get(0).getAgentId();
            for(Map.Entry<Long, List<Point>> controllerVsPoints : controllerIdVsPointsMap.entrySet()){
                long controllerId = controllerVsPoints.getKey();
                List<Point> pointsToConfigure = controllerVsPoints.getValue();
                FacilioChain chain = TransactionChainFactory.getConfigurePointAndProcessControllerV2Chain();
                FacilioContext context = chain.getContext();
                context.put(AgentConstants.CONTROLLER_ID, controllerId);
                context.put(AgentConstants.AGENT_ID, agentId);
                context.put(AgentConstants.POINTS, pointsToConfigure);
                context.put(AgentConstants.CONTROLLER_TYPE, controllerType);
                context.put(AgentConstants.DATA_INTERVAL, interval);
                context.put(AgentConstants.LOGICAL, logical);
                chain.execute();
            }
        } else {
            throw new Exception("No points found in DB for ids->" + pointIds);
        }
    }

    private static Map<Long, List<Point>> getControllerIdVsPointsMap(List<Point> points) {
        Map<Long, List<Point>> controllerIdVsPoints = new HashMap<>();
        for (Point point : points) {
            long controllerId = point.getControllerId();
            if(controllerIdVsPoints.containsKey(controllerId)){
                controllerIdVsPoints.get(controllerId).add(point);
            } else {
                List<Point> pointsList = new ArrayList<>();
                pointsList.add(point);
                controllerIdVsPoints.put(controllerId, pointsList);
            }
        }
        return controllerIdVsPoints;
    }
}
