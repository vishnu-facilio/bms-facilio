package com.facilio.bmsconsole.instant.jobs;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.point.Point;
import com.facilio.bacnet.BACNetUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.taskengine.job.InstantJob;
import com.facilio.v3.util.FilterUtil;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BulkConfigurePointsJob extends InstantJob {

    Integer interval;
    boolean logical;
    FacilioControllerType controllerType;
    int limit = 5000;
    long agentId = -1L;

    @Override
    public void execute(FacilioContext context) throws Exception {
        List<Long> pointIds = (List<Long>) context.get(AgentConstants.POINT_IDS);
        List<Long> controllerIds = (List<Long>) context.get(AgentConstants.CONTROLLERIDS);
        controllerType = (FacilioControllerType) context.get(AgentConstants.CONTROLLER_TYPE);
        interval = (int) context.get(AgentConstants.DATA_INTERVAL);
        agentId = (long) context.get(AgentConstants.AGENT_ID);
        logical = (boolean) context.get(AgentConstants.LOGICAL);
        JSONObject filters = (JSONObject) context.get(FacilioConstants.ContextNames.FILTERS);

        GetPointRequest pointRequest = new GetPointRequest().ofType(controllerType);
        setCriteria(agentId, context, pointIds, controllerIds, filters, pointRequest);

        List<Point> points = pointRequest.getPoints();
        if (!points.isEmpty()) {
            Map<Long, List<Point>> controllerIdVsPointsMap = getControllerIdVsPointsMap(points);
            long agentId = points.get(0).getAgentId();
            for (Map.Entry<Long, List<Point>> controllerVsPoints : controllerIdVsPointsMap.entrySet()) {
                configurePoints(agentId, controllerVsPoints);
            }
        } else {
            throw new Exception("No points found in DB for ids->" + pointIds);
        }
    }

    private void setCriteria(long agentId, FacilioContext context, List<Long> pointIds, List<Long> controllerIds, JSONObject filters, GetPointRequest pointRequest) throws Exception {
        if (agentId != -1L) {
            pointRequest.withAgentId(agentId);
        }
        if (pointIds != null && !pointIds.isEmpty()) {
            pointRequest.fromIds(pointIds);
        } else if (filters != null && !filters.isEmpty()) {
            Criteria filterCriteria = FilterUtil.getCriteriaFromFilters(filters, FacilioConstants.ContextNames.POINTS, context);
            pointRequest.withCriteria(filterCriteria).limit(limit);
        } else {
            throw new Exception("Either Point ids or criteria can't be empty");
        }
        if (controllerIds != null && !controllerIds.isEmpty()) {
            pointRequest.withControllerIds(controllerIds);
        }
        if (controllerType == FacilioControllerType.BACNET_IP) {
            pointRequest.withCriteria(BACNetUtil.getBacnetInstanceTypeCriteria());
        }
    }

    private void configurePoints(long agentId, Map.Entry<Long, List<Point>> controllerVsPoints) throws Exception {
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

    private static Map<Long, List<Point>> getControllerIdVsPointsMap(List<Point> points) {
        Map<Long, List<Point>> controllerIdVsPoints = new HashMap<>();
        for (Point point : points) {
            long controllerId = point.getControllerId();
            if (controllerIdVsPoints.containsKey(controllerId)) {
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
