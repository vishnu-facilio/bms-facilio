package com.facilio.devicepoints;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.bmsconsole.util.IoTMessageAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.timeseries.TimeSeriesAPI;
import com.facilio.util.FacilioUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * AgentProcessor is a dedicated Processor for processing payloads with PUBLISH_TYPE set to 'DevicePoints'.
 */
public  class DevicePointsUtil {
    private static final Logger LOGGER = LogManager.getLogger(DevicePointsUtil.class.getName());

    public  void processDevicePoints(JSONObject payLoad, long orgId, Long agentId) throws Exception {
        LOGGER.info("in DevicePointsUtil.ProcessDevicePoints");
        long instanceNumber = (Long)payLoad.get(DevicePointsKeys.INSTANCE_NUMBER);
        String destinationAddress = "";
        if(payLoad.containsKey(DevicePointsKeys.MAC_ADDRESS)) {
            destinationAddress = (String) payLoad.get(DevicePointsKeys.MAC_ADDRESS);
        }
        long subnetPrefix = FacilioUtil.parseLong(payLoad.get(DevicePointsKeys.SUBNET_PREFIX));
        long networkNumber = -1;
        if(payLoad.containsKey(DevicePointsKeys.NETWORK_NUMBER)) {
            networkNumber = (Long) payLoad.get(DevicePointsKeys.NETWORK_NUMBER);
        }
        String broadcastAddress = (String) payLoad.get(DevicePointsKeys.BROADCAST_ADDRESS);
        String deviceName = (String) payLoad.get(DevicePointsKeys.DEVICE_NAME);
        Integer controllerType = 0;
        if(payLoad.containsKey(DevicePointsKeys.CONTROLLER_TYPE)){
             controllerType = Integer.parseInt(payLoad.get(DevicePointsKeys.CONTROLLER_TYPE).toString());
        }
        String deviceId = instanceNumber+"_"+destinationAddress+"_"+networkNumber;
        // if( ! deviceMap.containsKey(deviceId)) {
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            ControllerContext controller = bean.getController(deviceName, deviceId);
            if(controller == null) {
                controller = new ControllerContext();
                controller.setName(deviceName);
                controller.setAgentId(agentId);
                controller.setBroadcastIp(broadcastAddress);
                controller.setDestinationId(destinationAddress);
                controller.setInstanceNumber(instanceNumber);
                controller.setNetworkNumber(networkNumber);
                controller.setSubnetPrefix(Math.toIntExact(subnetPrefix));
                controller.setMacAddr(deviceId);
                controller.setControllerType(controllerType);
                controller = bean.addController(controller);
            } else {
                if (controller.getAgentId() < 1 && controller.getId() > -1) {
                    controller.setAgentId(agentId);
                    Map<String, Object> prop = new HashMap<>();
                    prop.put("agentId", agentId);
                    prop.put("lastDataReceivedTime", System.currentTimeMillis());
                    ControllerAPI.updateController(prop, Collections.singletonList(controller.getId()));

                }
            }
            long controllerSettingsId = controller.getId();
            if(controllerSettingsId > -1) {
                JSONArray points = (JSONArray)payLoad.get(DevicePointsKeys.POINTS);
                LOGGER.info("Device Points : "+points);
                
                int count;
                 TimeSeriesAPI.addPointsInstances(points, controllerSettingsId);
                count = TimeSeriesAPI.addUnmodeledInstances(points, controllerSettingsId);
                
                JSONObject info = new JSONObject();
                info.put("controllerId", controllerSettingsId);
                info.put("publishType", "devicePoints");
                info.put("count", count);
                IoTMessageAPI.sendPublishNotification(null, info);
            }
        // }
    }
}

