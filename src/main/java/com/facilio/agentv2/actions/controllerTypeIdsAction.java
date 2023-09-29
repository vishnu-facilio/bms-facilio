package com.facilio.agentv2.actions;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.point.PointsAPI;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class controllerTypeIdsAction extends IdsAction {

    private static final Logger LOGGER = LogManager.getLogger(controllerTypeIdsAction.class.getName());

    private List<Map<String,Object>> instances = null;
    
    @NotNull
    @Min(value = 0, message = "controllerType can't be less than 1")
    @Max(99)
    private Integer controllerType;

    private Long agentId = -1L;
    private Boolean logical=false;
    List<Long> controllerIds;
    private int interval = -1;

	public String configurePoints() {
        try {
            List<Long> pointIds = getRecordIds();
            JSONObject filterJSON = getFilterJSON();
            if (!pointIds.isEmpty() || !filterJSON.isEmpty()) {
                PointsAPI.configurePoints(agentId, pointIds, getControllerIds(), FacilioControllerType.valueOf(getControllerType()), getLogical(),
                        getInterval(), filterJSON);
                setResponseCode(HttpURLConnection.HTTP_OK);
                setResult(AgentConstants.RESULT, SUCCESS);
                ok();
            } else {
                throw new Exception("Point ids or filter criteria can't be empty");
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while configuring controller", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    public String configureAllPoints() {
        try {
            PointsAPI.configureAllPoints(getAgentId(), getRecordIds(), FacilioControllerType.valueOf(getControllerType()), getInterval());
            setResponseCode(HttpURLConnection.HTTP_OK);
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while configuring controller", e);
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            internalError();
        }
        return SUCCESS;
    }

    private JSONObject getFilterJSON() throws ParseException {
        if(getFilters()!=null){
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(getFilters());
        }
        return new JSONObject();
    }

    public String removePoints() {
        try {
            List<Long> pointIds = getRecordIds();
            if (PointsAPI.deletePointsChain(pointIds, FacilioControllerType.valueOf(getControllerType()))) {
                ok();
                setResult(AgentConstants.RESULT, SUCCESS);
                return SUCCESS;
            } else {
                setResponseCode(HttpURLConnection.HTTP_NOT_MODIFIED);
            }
        } catch (Exception e) {
            internalError();
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            LOGGER.info("Exception while deleting point", e);
            setResult(AgentConstants.RESULT, ERROR);
            return SUCCESS;
        }
        setResult(AgentConstants.RESULT, ERROR);
        return SUCCESS;
    }

    public String unConfigurePoints() {
        try {
            setResult(AgentConstants.RESULT, PointsAPI.unConfigurePointsChain(getRecordIds(), FacilioControllerType.valueOf(getControllerType())));
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while unconfiguring point -> " + getRecordIds() + " -,", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            internalError();
        }
        return SUCCESS;
    }

    public String subscribePoints() {
        try {
            setResult(AgentConstants.RESULT, PointsAPI.subscribeUnsubscribePoints(getInstances(), FacilioControllerType.valueOf(getControllerType()), FacilioCommand.SUBSCRIBE));
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while unconfiguring point -> " + getRecordIds() + " -,", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            internalError();
        }
        return SUCCESS;
    }

    public String unsubscribePoints() {
        try {
            setResult(AgentConstants.RESULT, PointsAPI.subscribeUnsubscribePoints(getInstances(), FacilioControllerType.valueOf(getControllerType()), FacilioCommand.UNSUBSCRIBE));
            setResult(AgentConstants.RESULT, SUCCESS);
            ok();
        } catch (Exception e) {
            LOGGER.info("Exception occurred while unconfiguring point -> " + getRecordIds() + " -,", e);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResult(AgentConstants.RESULT, ERROR);
            internalError();
        }
        return SUCCESS;
    }
}
