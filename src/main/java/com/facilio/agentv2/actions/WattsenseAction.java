package com.facilio.agentv2.actions;

import com.facilio.agent.integration.wattsense.WattsenseClient;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.device.Device;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.agentv2.misc.MiscController;
import com.facilio.agentv2.misc.MiscPoint;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.bmsconsole.jobs.DataFetcherJob;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class WattsenseAction extends FacilioAction {
    private static final Logger LOGGER = LogManager.getLogger(WattsenseAction.class.getName());
    private long deviceId;


    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
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

    public String getDevices() {
        try {
            LOGGER.info("getting devices");
            FacilioAgent agent = AgentApiV2.getAgent(getAgentId());
            WattsenseClient client = new WattsenseClient(agent);
            client.setApiKey(getApiKey());
            client.setSecretKey(getSecretKey());
            List<Device> devices = client.getDevices();
            for (Device device : devices) {
                long id = FieldDeviceApi.addFieldDevice(device);
                Controller controller = new MiscController();
                controller.setDeviceId(id);
                controller.setAgentId(device.getAgentId());
                controller.setName(device.getName());
                ControllerApiV2.addController(controller);
            }
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage());
            ex.printStackTrace();
        }
        return SUCCESS;
    }

    public String discoverPoints() {
        LOGGER.info("Discovering points");
        try {
            WattsenseClient client = new WattsenseClient(AgentApiV2.getAgent(getAgentId()));
            client.setApiKey(getApiKey());
            client.setSecretKey(getSecretKey());
            Device fieldDevice = FieldDeviceApi.getDevice(getDeviceId());
            if (fieldDevice != null) {
                List<MiscPoint> points = client.getPoints(fieldDevice);
                for (MiscPoint point : points) {
                    PointsAPI.addPoint(point);
                }
            }
        } catch (Exception ex) {
            return ERROR;
        }
        return SUCCESS;
    }
}
