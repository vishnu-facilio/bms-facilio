package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.commands.AgentV2Command;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.FacilioException;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateLastRecordedValueAndFilterPointsCommand extends AgentV2Command {
    private static final Logger LOGGER = LogManager.getLogger(UpdateLastRecordedValueAndFilterPointsCommand.class.getName());
    private static final long TOLERANCE_MS = 0; //0secs tolerance

    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean isCov = false;
        boolean updatePointDataMissing = false;
        if (context.containsKey(FacilioConstants.ContextNames.ADJUST_READING_TTIME)) {
            isCov = !(Boolean) context.get(FacilioConstants.ContextNames.ADJUST_READING_TTIME);
        }


        FacilioAgent agent = (FacilioAgent) context.get(AgentConstants.AGENT);
        long timeStamp = Long.parseLong(context.get(FacilioConstants.ContextNames.TIMESTAMP).toString());
        Map<String, Map<String, Object>> snapshot = (Map<String, Map<String, Object>>) context.get(FacilioConstants.ContextNames.DataProcessor.DATA_SNAPSHOT);
        Map<String, Point> pointRecords = (Map<String, Point>) context.get(FacilioConstants.ContextNames.DataProcessor.POINT_RECORDS);
        Map<String,String> errorPoints = new HashMap<>();
        boolean filterEnabled = AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.POINT_FILTER);

        List<String> pointsToRemove = new ArrayList<>();
        List<Point> points = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> stringObjectEntry : snapshot.entrySet()) {
            String pointName = stringObjectEntry.getKey();
            Map<String, Object> pointData = stringObjectEntry.getValue();
            Object value = pointData.get(AgentConstants.VALUE);
            Point point = pointRecords.get(pointName);
            if (point != null) {
                if (!isCov && filterEnabled && intervalFilterConditions(agent, timeStamp, point)) {
                    pointsToRemove.add(pointName);
                    point.setDataFiltered(true);
                    errorPoints.put(pointName, "Point is filtered out as the data has already come in this interval period");
                } else {
                    // set data missing as false
                    if (point.getDataMissing()) {
                        point.setDataMissing(false);
                        updatePointDataMissing = true;
                    }
                    //update point last recorded time and value
                    point.setLastRecordedTime(timeStamp);
                    point.setLastRecordedValue(value.toString());
                    points.add(point);
                }
            } else {
                LOGGER.debug("Point name not found for " + pointName);
            }
        }

        context.put(AgentConstants.ERROR_POINTS, errorPoints);
        if (!points.isEmpty()) {
            PointsAPI.updatePointsValue(points, updatePointDataMissing);
        }

        for (String pointName : pointsToRemove) {
            snapshot.remove(pointName);
        }
        if (!pointsToRemove.isEmpty()) {
            LOGGER.debug("Filtered points : " + pointsToRemove);
        }

        if (snapshot.isEmpty()) {
            throw new FacilioException("Data for all the points came for the interval period already");
        }
        return false;

    }

    private boolean intervalFilterConditions(FacilioAgent agent, long timeStamp, Point point) throws Exception {
        boolean isFiltered;
        long timeFromLastRecorded = timeStamp - point.getLastRecordedTime() + TOLERANCE_MS;
        if (point.getInterval() > 0) {
            isFiltered = timeFromLastRecorded < (long) point.getInterval() * 60 * 1000;
        } else {
            isFiltered = timeFromLastRecorded < agent.getInterval() * 60 * 1000;
        }
        return isFiltered;
    }
}