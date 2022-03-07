package com.facilio.agentv2.actions;

import com.facilio.agent.integration.wattsense.WattsenseClient;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.misc.MiscPoint;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.bmsconsole.actions.FacilioAction;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class WattsenseAction extends FacilioAction {
    private static final Logger LOGGER = LogManager.getLogger(WattsenseAction.class.getName());
    private long controllerId;

    public long getControllerId() {
        return controllerId;
    }

    public void setControllerId(long controllerId) {
        this.controllerId = controllerId;
    }

    private long agentId;
    private String apiKey;
    private String secretKey;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getAgentId() {
        return agentId;
    }

    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    public String discoverPoints() {
        LOGGER.info("Discovering points");
        try {
            WattsenseClient client = new WattsenseClient(AgentApiV2.getAgent(getAgentId()));
            client.setApiKey(getApiKey());
            client.setSecretKey(getSecretKey());
            Controller controller = ControllerApiV2.getControllerFromDb(getControllerId());
            List<MiscPoint> points = client.getPoints(controller);
                for (MiscPoint point : points) {
                    PointsAPI.addPoint(point);
                }

        } catch (Exception ex) {
            return ERROR;
        }
        return SUCCESS;
    }
}
