package com.facilio.agentv2.point;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.bmsconsole.commands.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigurePointCommand extends FacilioCommand {

    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(ConfigurePointCommand.class.getName());


    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (containsAndNotNull(context, AgentConstants.POINTS) && containsAndNotNull(context, AgentConstants.CONTROLLER)) {
            Controller controller = (Controller) context.get(AgentConstants.CONTROLLER);
            if (containsAndNotNull(context, AgentConstants.POINTS)) {
                List<Point> points = (List<Point>) context.get(AgentConstants.POINTS);
                Objects.requireNonNull(points,"points can't be null");
                Objects.requireNonNull(controller,"controller can't be null");
                LOGGER.info("------configuring "+points.size()+" points for "+controller.getId());
                List<Point> pointsToConfigure = new ArrayList<>();
                for (Point point : points) {
                    if ((point.getControllerId() < 1) || (point.getControllerId() == controller.getId())) {
                        point.setControllerId(controller.getId());
                        pointsToConfigure.add(point);
                    } else {
                        LOGGER.info(" point already configured "+point.getId());
                    }
                }
                boolean isLogical = (boolean) context.getOrDefault(AgentConstants.LOGICAL, false);
                PointsAPI.configurePoints(pointsToConfigure, controller,isLogical);
            }
        } else {
            throw new Exception(AgentConstants.RECORD_IDS + ", " + AgentConstants.CONTROLLER_TYPE + " missing from context->" + context);
        }
        return false;
    }

    private static boolean containsAndNotNull(Context context, String key) {
        return ((context != null) && (key != null) && context.containsKey(key) && (context.get(key) != null));
    }
}
