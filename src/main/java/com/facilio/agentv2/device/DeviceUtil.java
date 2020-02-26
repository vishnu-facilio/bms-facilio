package com.facilio.agentv2.device;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
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

    public static boolean deleteFieldDevice(long id) {
        return deleteFieldDevices(Collections.singletonList(id));
    }

    private static boolean containsValueCheck(String key, Map<String, Object> jsonObject) {
        if (jsonObject.containsKey(key) && (jsonObject.get(key) != null)) {
            return true;
        }
        return false;
    }

    public static boolean processDevices(FacilioAgent agent, JSONObject payload) throws Exception {
        LOGGER.info(" processing devices ");
        List<Device> devices = new ArrayList<>();
        if (containsValueCheck(AgentConstants.DATA, payload)) {
            JSONArray devicesArray = (JSONArray) payload.get(AgentConstants.DATA);

            if (devicesArray.isEmpty()) {
                throw new Exception("Exception Occurred, Device data is empty");
            }

            for (Object deviceObject : devicesArray) {
                JSONObject deviceJSON = (JSONObject) deviceObject;

                deviceJSON.put(AgentConstants.AGENT_ID, agent.getId());
                Device device = new Device(AccountUtil.getCurrentOrg().getOrgId(), agent.getId());
                if(containsValueCheck(AgentConstants.CONTROLLER_TYPE,deviceJSON)){
                    device.setControllerType(((Number) deviceJSON.get(AgentConstants.CONTROLLER_TYPE)).intValue());
                }else {
                    throw new Exception(" controllerType missing from deviceJson "+deviceJSON);
                }
                if (agent.getSiteId() < 1) {
                    LOGGER.info(" Exception occurred. Agent is missing its siteId,skipping device processing.");
                    continue;
                }
                device.setSiteId(agent.getSiteId());
                if (deviceJSON.containsKey(AgentConstants.CONTROLLER)) {
                    device.setIdentifier( getControllerIdentifier(device.getControllerType(),(JSONObject) deviceJSON.get(AgentConstants.CONTROLLER)));
                } else {
                    LOGGER.info("Exception occurred, controller params found in device json -> " + payload);
                    device.setName(deviceJSON.toString());
                }
                if(deviceJSON.containsKey(AgentConstants.NAME)){
                    device.setName((String)deviceJSON.get(AgentConstants.NAME));
                }else{
                    device.setName(device.getIdentifier().toString());
                }
                device.setCreatedTime(System.currentTimeMillis());
                if (!deviceJSON.containsKey(AgentConstants.CREATED_TIME)) {
                    deviceJSON.put(AgentConstants.CREATED_TIME, device.getCreatedTime());
                }
                device.setControllerProps(deviceJSON);
                devices.add(device);
            }
            return addDevices(devices);
        } else {
            throw new Exception("Exception occurred while processing device, data is missing from payload->" + payload);
        }
    }

    public static String getControllerIdentifier(int type, JSONObject jsonObject) {
        try {
            Controller controller = ControllerApiV2.makeControllerFromMap(jsonObject,FacilioControllerType.valueOf(type));
            return controller.getIdentifier();
        }catch (Exception e){
            LOGGER.info("Exception while making identifier "+FacilioControllerType.valueOf(type));
        }
        return null;
    }

    public static boolean addDevices(List<Device> devices) throws Exception {
        if (devices != null && !devices.isEmpty()) {
            /*FacilioChain chain = TransactionChainFactory.getAddDevicesChain();
            FacilioContext context = chain.getContext();
            context.put(AgentConstants.DATA, devices);
            return chain.execute();*/
            FieldDeviceApi.addFieldDevices(devices);
            return true;
        }
        throw new Exception("Devices to add can't be empty");
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
