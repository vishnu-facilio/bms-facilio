package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.controller.ControllerUtilV2;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointsAPI;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

public class ControllerActions extends AgentActionV2 {

    private static final Logger LOGGER = LogManager.getLogger(ControllerActions.class.getName());

    @NotNull
    @Min(value = 0, message = "Controller Id can't be less than 1")
    private Long controllerId;

    public Long getControllerId() {
        return controllerId;
    }

    public void setControllerId(Long controllerId) {
        this.controllerId = controllerId;
    }

    public String getControllerUsingId() {
        try {
            Controller controller = ControllerApiV2.getControllerFromDb(getControllerId());
            if (controller != null) {
                setResult(AgentConstants.RESULT, SUCCESS);
                setResult(AgentConstants.DATA, controller.toJSON());
                return SUCCESS;
            } else {
                setResult(AgentConstants.RESULT, NONE);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting controller using orgId", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
        }
        setResult(AgentConstants.DATA, new JSONObject());
        return SUCCESS;
    }

    /**
     * get all points for a controller
     *
     * @return
     */
    public String listPoints() {
        JSONArray pointData = new JSONArray();
        try {
            List<Point> points = PointsAPI.getAllPoints(null, getControllerId());
            LOGGER.info(" in device action " + points);
            if (!points.isEmpty()) {
                for (Point point : points) {
                    pointData.add(point.toJSON());
                }
            }
            setResult(AgentConstants.DATA, points);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting points", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
        }
        return SUCCESS;
    }


    public String discoverPoints() {
        try {
            if (ControllerUtilV2.discoverPoints(getControllerId())) {
                setResult(AgentConstants.RESULT, SUCCESS);
            } else {
                setResult(AgentConstants.RESULT, ERROR);
            }
            return SUCCESS;
        } catch (Exception e) {
            LOGGER.info("Exception while discoverPoints", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
        }
        return SUCCESS;
    }

    public String listControllerPointsCount(){
        try{
                long count = PointsAPI.getAgentPointsCount(-1, getControllerId());
                setResult(AgentConstants.DATA,count);
                setResult(AgentConstants.RESULT,SUCCESS);
        }catch (Exception e){
            LOGGER.info("Exception occurred while getting all point for agent->"+controllerId+" -",e);
            setResult(AgentConstants.EXCEPTION,e.getMessage());
            setResult(AgentConstants.RESULT,ERROR);
        }
        return SUCCESS;
    }
}
