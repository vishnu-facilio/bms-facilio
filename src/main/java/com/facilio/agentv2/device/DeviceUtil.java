package com.facilio.agentv2.device;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DeviceUtil {

    private static final Logger LOGGER = LogManager.getLogger(DeviceUtil.class.getName());

    public void processDevices(FacilioAgent agent, JSONObject payload) {
        LOGGER.info(" processing devices ");
        List<Device> devices = new ArrayList<>();
        if (containsValueCheck(AgentConstants.DATA, payload)) {
            JSONArray devicesArray = (JSONArray) payload.get(AgentConstants.DATA);
            if (devicesArray.isEmpty()) {
                LOGGER.info("Exception Occurred, Device data is empty");
                return;
            }
            for (Object deviceObject : devicesArray) {
                JSONObject deviceJSON = (JSONObject) deviceObject;
                deviceJSON.put(AgentConstants.AGENT_ID, agent.getId());
                Device device = new Device(AccountUtil.getCurrentOrg().getOrgId(), agent.getId());
                device.setSiteId(agent.getSiteId());
                if (deviceJSON.containsKey(AgentConstants.IDENTIFIER)) {
                    device.setName(String.valueOf(deviceJSON.get(AgentConstants.IDENTIFIER)));
                } else {
                    LOGGER.info("Exception occurred, no identifier found in device json -> " + payload);
                    return;
                }
                device.setCreatedTime(System.currentTimeMillis());
                if (!deviceJSON.containsKey(AgentConstants.CREATED_TIME)) {
                    deviceJSON.put(AgentConstants.CREATED_TIME, device.getCreatedTime());
                }
                device.setControllerProps(deviceJSON);
                devices.add(device);
            }
            addDevices(devices);
        }
    }

    public boolean addDevices(List<Device> devices) {
        if (devices != null && !devices.isEmpty()) {
            FacilioChain chain = TransactionChainFactory.getAddDevicesChain();
            FacilioContext context = chain.getContext();
            context.put(AgentConstants.DATA, devices);
            try {
                return chain.execute();
            } catch (Exception e) {
                LOGGER.info("Exception occurred ", e);
            }
        }
        return false;
    }

    private static boolean containsValueCheck(String key, Map<String, Object> jsonObject) {
        if (jsonObject.containsKey(key) && (jsonObject.get(key) != null)) {
            return true;
        }
        return false;
    }


    public static boolean deleteFieldDevice(long id){
        return deleteFieldDevices(Collections.singletonList(id));
    }

    public static boolean deleteFieldDevices(List<Long> ids) {
        FacilioChain chain = TransactionChainFactory.getDeleteFieldDeviceChain();
        FacilioContext context = chain.getContext();
        List<Long> deleteList = new ArrayList<>();
        for (Long id : ids) {
            if (id > 0) {
                deleteList.add(id);
            }
        }
        context.put(AgentConstants.ID, deleteList);
        try {
            return chain.execute();
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
        return false;
    }
}
