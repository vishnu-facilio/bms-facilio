package com.facilio.devicepoints;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.fw.BeanFactory;
import com.facilio.timeseries.TimeSeriesAPI;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;

/**
 * AgentProcessor is a dedicated Processor for processing payloads with PUBLISH_TYPE set to 'DevicePoints'.
 */
public  class DevicePointsUtil {
    private static final Logger LOGGER = LogManager.getLogger(DevicePointsUtil.class.getName());

    public  void processDevicePoints(JSONObject payLoad, long orgId, HashMap deviceMap) throws Exception {
        LOGGER.info("in DevicePointsUtil.ProcessDevicePoints");
        long instanceNumber = (Long)payLoad.get(DevicePointsKeys.INSTANCE_NUMBER);
        String destinationAddress = "";
        if(payLoad.containsKey(DevicePointsKeys.MAC_ADDRESS)) {
            destinationAddress = (String) payLoad.get(DevicePointsKeys.MAC_ADDRESS);
        }
        long subnetPrefix = (Long)payLoad.get(DevicePointsKeys.SUBNET_PREFIX);
        long networkNumber = -1;
        if(payLoad.containsKey(DevicePointsKeys.NETWORK_NUMBER)) {
            networkNumber = (Long) payLoad.get(DevicePointsKeys.NETWORK_NUMBER);
        }
        String broadcastAddress = (String) payLoad.get(DevicePointsKeys.BROADCAST_ADDRESS);
        String deviceName = (String) payLoad.get(DevicePointsKeys.DEVICE_NAME);

        String deviceId = instanceNumber+"_"+destinationAddress+"_"+networkNumber;
        if( ! deviceMap.containsKey(deviceId)) {
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            ControllerContext controller = bean.getController(deviceName, deviceId);
            if(controller == null) {
                controller = new ControllerContext();
                controller.setName(deviceName);
                controller.setBroadcastIp(broadcastAddress);
                controller.setDestinationId(destinationAddress);
                controller.setInstanceNumber(instanceNumber);
                controller.setNetworkNumber(networkNumber);
                controller.setSubnetPrefix(Math.toIntExact(subnetPrefix));
                controller.setMacAddr(deviceId);
                controller = bean.addController(controller);
            }
            long controllerSettingsId = controller.getId();
            if(controllerSettingsId > -1) {
                JSONArray points = (JSONArray)payLoad.get(DevicePointsKeys.POINTS);
                LOGGER.info("Device Points : "+points);
                TimeSeriesAPI.addUnmodeledInstances(points, controllerSettingsId);
            }
        }
    }
}

