package com.facilio.agentv2.actions;

import com.facilio.bmsconsole.actions.FacilioAction;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import javax.validation.constraints.NotNull;

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
        throw new Exception("Field Devices are not supported anymore");
        /*try {
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
                    fieldDevice.setControllerType(type);
                    fieldDevice.setControllerProps(controllerProps);
                    fieldDevice.setCreatedTime(System.currentTimeMillis());

                    List<Device> deviceList = new ArrayList<>();
                    deviceList.add(fieldDevice);

                    FieldDeviceApi.addFieldDevices(deviceList);
                    setResponseCode(HttpURLConnection.HTTP_OK);
                    return SUCCESS;
                } else {
                    setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
                    return ERROR;
                }
            } else {
                setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
                return ERROR;
            }
        } catch (Exception ex) {
            LOGGER.info("Exception while adding Field Device :" + ex.getMessage());
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            return ERROR;
        }*/
    }
}
