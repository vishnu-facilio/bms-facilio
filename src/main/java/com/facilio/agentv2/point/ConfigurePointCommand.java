package com.facilio.agentv2.point;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.rdm.RdmControllerContext;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigurePointCommand extends FacilioCommand {

    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(ConfigurePointCommand.class.getName());


    @Override
    public boolean executeCommand(Context context) throws Exception {
        if(context.containsKey(AgentConstants.CONTROLLER_ID)){
            Long controllerId = (Long) context.get(AgentConstants.CONTROLLER_ID);
            Controller controller = AgentConstants.getControllerBean().getControllerFromDb(controllerId);
//            List<Controller> controllers = FieldUtil.getAsBeanListFromMapList(AgentConstants.getControllerBean().getControllers(Collections.singletonList(controllerId)), Controller.class);

            if(controller== null){
                throw new Exception(" no controllers found ");
            }
            context.put(AgentConstants.CONTROLLER,controller);
        }else {
            LOGGER.info(" Exception Occurred,ids missing from context to get Controller");
            throw new Exception("id missing from context to get Controller");
        }


        if (containsAndNotNull(context, AgentConstants.POINTS) && containsAndNotNull(context, AgentConstants.CONTROLLER)) {
            Controller controller = (Controller) context.get(AgentConstants.CONTROLLER);

            if(controller.getControllerType()==13){
                boolean isTdb = ((RdmControllerContext) controller).getIsTdb();
                if(!isTdb){
                  throw new Exception("Cannot configure points for non-tdb controller. "+controller.getId());
              }
            }

            int interval = -1;
            if (context.containsKey(AgentConstants.DATA_INTERVAL)) {
                interval = Integer.parseInt((context.get(AgentConstants.DATA_INTERVAL).toString()));
                if (interval <= 0) {
                	interval = -99;
                }
            }
            if (containsAndNotNull(context, AgentConstants.POINTS)) {
                List<Point> points = (List<Point>) context.get(AgentConstants.POINTS);
                Objects.requireNonNull(points,"points can't be null");
                Objects.requireNonNull(controller,"controller can't be null");
                LOGGER.info("Configuring "+points.size() + " points for " + controller.getName());
                List<Point> pointsToConfigure = new ArrayList<>();
                for (Point point : points) {
                    if ((point.getControllerId() < 1) || (point.getControllerId() == controller.getId())) {
                        point.setControllerId(controller.getId());
                        pointsToConfigure.add(point);
                    } else {
                        LOGGER.info("Point already configured "+point.getName());
                    }
                }
                boolean isLogical = (boolean) context.getOrDefault(AgentConstants.LOGICAL, false);
                PointsAPI.configurePoints(pointsToConfigure, controller, isLogical, interval);
            }
        } else {
            throw new Exception(AgentConstants.POINTS + ", " + AgentConstants.CONTROLLER_TYPE + " missing from context->" + context);
        }
        return false;
    }

    private static boolean containsAndNotNull(Context context, String key) {
        return ((context != null) && (key != null) && context.containsKey(key) && (context.get(key) != null));
    }
}
