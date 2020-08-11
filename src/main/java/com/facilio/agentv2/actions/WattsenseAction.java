package com.facilio.agentv2.actions;

import com.facilio.agent.integration.wattsense.WattsenseClient;
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

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    private long deviceId;


    public String getDevices() {
        try {
            if (getDeviceId() == null) throw new Exception("Device ID is null");
            LOGGER.info("getting devices");
            WattsenseClient client = new WattsenseClient();
            client.setApiKey("XdwqbEv0aOgrVXBQZPD7Lxp5A89GP68wR064gW3eq4lz6JkKMwdYRmbyo2N1JR4Y");
            client.setSecretKey("YPreE3g20A8wX9dBVNYpL1QzvMW5QzM18QKZtoWleDbR6OKJa4yoxm7qr5PZkmRM");
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
        WattsenseClient client = new WattsenseClient();
        client.setApiKey("XdwqbEv0aOgrVXBQZPD7Lxp5A89GP68wR064gW3eq4lz6JkKMwdYRmbyo2N1JR4Y");
        client.setSecretKey("YPreE3g20A8wX9dBVNYpL1QzvMW5QzM18QKZtoWleDbR6OKJa4yoxm7qr5PZkmRM");
        try {
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
