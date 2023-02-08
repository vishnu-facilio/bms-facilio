package com.facilio.bmsconsole.instant.jobs;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.taskengine.job.InstantJob;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BulkUnConfigurePointsJob extends InstantJob {

    private static final Logger LOGGER = LogManager.getLogger(BulkUnConfigurePointsJob.class.getName());

    @Override
    public void execute(FacilioContext facilioContext) throws Exception {
        List<Long> pointIds = (List<Long>) facilioContext.get(AgentConstants.POINT_IDS);
        FacilioControllerType controllerType = (FacilioControllerType) facilioContext.get(AgentConstants.CONTROLLER_TYPE);

        List<Point> points = new GetPointRequest().ofType(controllerType).fromIds(pointIds).getPoints();
        if(!points.isEmpty()) {
            Map<Long, List<Long>> controllerIdVsPointsMap = getControllerIdVsPointsMap(points);
            for(Map.Entry<Long, List<Long>> controllerVsPoints : controllerIdVsPointsMap.entrySet()) {
                long controllerId = controllerVsPoints.getKey();
                FacilioChain chain = TransactionChainFactory.unconfigurePointsChain();
                FacilioContext context = chain.getContext();
                context.put(AgentConstants.CONTROLLER_ID, controllerId);
                context.put(AgentConstants.POINT_IDS, controllerVsPoints.getValue());
                context.put(AgentConstants.CONTROLLER_TYPE, controllerType);
                chain.execute();
            }
        } else {
            LOGGER.info ("No points found in DB for ids->" + pointIds);
        }
    }

    private static Map<Long, List<Long>> getControllerIdVsPointsMap(List<Point> points) {
        Map<Long, List<Long>> controllerIdVsPoints = new HashMap<>();
        for (Point point : points) {
            long controllerId = point.getControllerId();
            if(controllerIdVsPoints.containsKey(controllerId)){
                controllerIdVsPoints.get(controllerId).add(point.getId());
            } else {
                List<Long> pointsList = new ArrayList<>();
                pointsList.add(point.getId());
                controllerIdVsPoints.put(controllerId, pointsList);
            }
        }
        return controllerIdVsPoints;
    }
}
