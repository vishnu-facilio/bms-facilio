package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.commands.AgentV2Command;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateLastRecordedValueAndFilterPointsCommand extends AgentV2Command {
    private static final Logger LOGGER = LogManager.getLogger(UpdateLastRecordedValueAndFilterPointsCommand.class.getName());
    private static final long TOLERANCE = 30000; //30secs tolerance

    @Override
    public boolean executeCommand(Context context) throws Exception {
        FacilioAgent agent = (FacilioAgent) context.get(AgentConstants.AGENT);
        long timeStamp = Long.parseLong(context.get(FacilioConstants.ContextNames.TIMESTAMP).toString());
        Map<String, Object> snapshot = (Map<String, Object>) context.get(FacilioConstants.ContextNames.DataProcessor.DATA_SNAPSHOT);
        Map<String, Point> pointRecords = (Map<String, Point>) context.get(FacilioConstants.ContextNames.DataProcessor.POINT_RECORDS);

        List<String> pointsToRemove = new ArrayList<>();
        List<Point> points = new ArrayList<>();
        for (Map.Entry<String, Object> stringObjectEntry : snapshot.entrySet()) {
            String pointName = stringObjectEntry.getKey();
            Object value = stringObjectEntry.getValue();
            Point point = pointRecords.get(pointName);
            if (value == null || isPointWithinAgentInterval(agent, timeStamp, point)) {
                pointsToRemove.add(pointName);
            } else {
                //update point last recorded time and value
                if (point != null) {
                    point.setLastRecordedTime(timeStamp);
                    point.setLastRecordedValue(value.toString());
                    points.add(point);
                } else {
                    LOGGER.info("Point name not found for " + pointName);
                }
            }
        }
        PointsAPI.updatePointsValue(points);

        for (String pointName : pointsToRemove) {
            snapshot.remove(pointName);
        }
        if (!pointsToRemove.isEmpty()) {
            LOGGER.info("Filtered points : " + pointsToRemove);
        }
        return false;
    }

    private boolean isPointWithinAgentInterval(FacilioAgent agent, long timeStamp, Point point) throws Exception {
        return AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.POINT_FILTER) && timeStamp - point.getLastRecordedTime() + TOLERANCE < agent.getInterval() * 60 * 1000;
    }
}
