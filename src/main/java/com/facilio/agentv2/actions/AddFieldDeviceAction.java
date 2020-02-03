package com.facilio.agentv2.actions;

import com.facilio.agentv2.device.Device;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.bmsconsole.actions.FacilioAction;
import jdk.nashorn.api.scripting.JSObject;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddFieldDeviceAction extends FacilioAction {
    private static final Logger LOGGER = LogManager.getLogger(AddFieldDeviceAction.class.getName());

    @NotNull JSONObject device;

    public JSONObject getDevice() {
        return device;
    }

    public void setDevice(JSONObject device) {
        this.device = device;
    }

    public String addDevice() throws Exception {
        try {
            JSONObject d = getDevice();
            if (d != null) {
                if (d.containsKey("name") &&
                        d.containsKey("orgId") &&
                        d.containsKey("siteId") &&
                        d.containsKey("agentId") &&
                        d.containsKey("type") &&
                        d.containsKey("controllerProps")) {

                    String name = (String) d.get("name");
                    Long orgId = Long.parseLong(d.get("orgId").toString());
                    Long siteId = Long.parseLong(d.get("siteId").toString());
                    Long agentId = Long.parseLong(d.get("agentId").toString());
                    int type = Integer.parseInt(d.get("type").toString());
                    JSONObject controllerProps = new JSONObject();
                    try {
                        Map<String, String> controllerPropsMap = (Map<String, String>) d.get("controllerProps");

                        for (String key :
                                controllerPropsMap.keySet()) {
                            controllerProps.put(key, controllerPropsMap.get(key));
                        }
                    } catch (Exception ex) {
                        LOGGER.info("Error while converting controllerProps");
                    }

                    Device fieldDevice = new Device();

                    fieldDevice.setName(name);
                    fieldDevice.setOrgId(orgId);
                    fieldDevice.setSiteId(siteId);
                    fieldDevice.setAgentId(agentId);
                    fieldDevice.setType(type);
                    fieldDevice.setControllerProps(controllerProps);
                    fieldDevice.setCreatedTime(System.currentTimeMillis());

                    List<Device> deviceList = new ArrayList<>();
                    deviceList.add(fieldDevice);

                    FieldDeviceApi.addFieldDevices(deviceList);

                    return SUCCESS;
                } else {
                    return ERROR;
                }
            } else {
                return ERROR;
            }
        } catch (Exception ex) {
            LOGGER.info("Exception while adding Field Device :" + ex.getMessage());
            return ERROR;
        }
    }
}
