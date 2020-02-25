package com.facilio.agentv2.controller;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.device.Device;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.custom.CustomController;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerUtilV2 {

    private static final Logger LOGGER = LogManager.getLogger(ControllerUtilV2.class.getName());
    private long orgId;
    private long agentId;
    List<Map<String, Controller>> controllerMapList = new ArrayList<>();


    public ControllerUtilV2(long agentId, long orgId) {
        this.orgId = orgId;
        this.agentId = agentId;
        loadControllerListMap();
    }

    public static boolean discoverPoints(Long controllerId) {
        return ControllerMessenger.discoverPoints(controllerId);
    }

    /**
     * This method iterates {@link FacilioControllerType} values and loads controllers of respective types to the controllerMapList
     */
    private void loadControllerListMap() {
        for (FacilioControllerType value : FacilioControllerType.values()) {
            controllerMapList.add(new HashMap<>());
        }
    }

    public static Map<Long, Controller> fieldDeviceToController(long agentId, Device device) throws Exception {
        Controller controller;
        Map<Long, Controller> deviceIdControllerMap = new HashMap<>();
        LOGGER.info("device are " + device);
        JSONObject controllerProps = device.getControllerProps();
        controllerProps.putAll((JSONObject)controllerProps.get(AgentConstants.CONTROLLER));
        LOGGER.info(" controller props JSON " + controllerProps);
        if ((controllerProps != null) && (!controllerProps.isEmpty())) {
            controllerProps.put(AgentConstants.DEVICE_ID, device.getId());
            controllerProps.putAll((JSONObject)device.getControllerProps().get(AgentConstants.CONTROLLER));
            controllerProps.put(AgentConstants.CONTROLLER_TYPE,controllerProps.get(AgentConstants.TYPE));
            controllerProps.remove(AgentConstants.TYPE);
            controller = getControllerFromJSON( controllerProps);
            if (controller != null) {
                controller.setAgentId(agentId);
                Controller controllerFromDb = ControllerApiV2.getControllerFromDb(controller.getChildJSON(), agentId, FacilioControllerType.valueOf(controller.getControllerType()));
                if (controllerFromDb != null) {
                    LOGGER.info(" controller present ");
                    deviceIdControllerMap.put(device.getId(), controllerFromDb);

                } else {
                    LOGGER.info(" making new controller ");
                    controller.setActive(true);
                    controller.setDataInterval(900000);
                    controller.setAvailablePoints(0);
                    long controllerId = ControllerApiV2.addController(controller);
                    if (controllerId > 0) {
                        controller.setId(controllerId);
                        deviceIdControllerMap.put(device.getId(), controller);
                        //deviceId.add(device.getId());
                    }
                }
            } else {
                throw new Exception("Controller Cont be null ");
            }
        } else {
            throw new Exception("controllerProps can't be null or empty -> " + controllerProps);
        }

        LOGGER.info(" device id controller map is ->" + deviceIdControllerMap);
        return deviceIdControllerMap;
    }

    public static Controller getControllerFromJSON(Map<String, Object> controllerJSON) {
        Controller controller = null;
        try {
            if (controllerJSON != null && (!controllerJSON.isEmpty())) {
                if (containsValueCheck(AgentConstants.TYPE, controllerJSON)) {
                    FacilioControllerType controllerType = FacilioControllerType.valueOf(Math.toIntExact((Long) controllerJSON.get(AgentConstants.TYPE)));
                    controllerJSON.put(AgentConstants.CONTROLLER_TYPE,controllerType.asInt());
                    controllerJSON.remove(AgentConstants.TYPE);
                    controller = ControllerApiV2.getControllerFromMap(controllerJSON,controllerType);
                } else {
                    LOGGER.info(" Controller Type missing ");
                }
            } else {
                LOGGER.info("Exception occurred, controllerJSON can't be null or empty " + controllerJSON);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
        return controller;
    }

    // pakka


/*    public static boolean processController(long agentId, List<Long> ids) {
        LOGGER.info(" processing devices ");
        FacilioChain chain = TransactionChainFactory.getConfigurePointAndProcessControllerV2Chain();
        FacilioContext context = chain.getContext();
        context.put(AgentConstants.AGENT_ID,agentId);
        context.put(AgentConstants.ID,ids);
        try {
            chain.execute();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }*/

    private static boolean containsValueCheck(String key, Map<String, Object> jsonObject) {
        return jsonObject.containsKey(key) && (jsonObject.get(key) != null);
    }

    public static Controller makeCustomController(long orgId, long agentId, JSONObject controllerJson) {
        CustomController controller = null;
        try {
            controller = (CustomController) ControllerApiV2.getControllerFromDb(controllerJson, agentId, FacilioControllerType.CUSTOM); // check if a custom controller is present already
        } catch (Exception e) {
            LOGGER.info(" Exception while fetching controller ",e);
        }
        if (controller == null) {
            controller = new CustomController();
            controller.setOrgId(orgId);
            controller.setAgentId(agentId);
            controller.setActive(true);
            controller.setName((String)controllerJson.get(AgentConstants.NAME));
            if (ControllerApiV2.addController(controller) < 1) {
                controller = null;
            }
        }
        return controller;
    }

    public static boolean deleteController(long controllerId) {
        try {
            FacilioChain chain = TransactionChainFactory.getDeleteControllerChain();
            FacilioContext context = chain.getContext();
            context.put(AgentConstants.CONTROLLER_ID, controllerId);
            return chain.execute();
        } catch (Exception e) {
            LOGGER.info("Exception Occurred ", e);
        }
        return false;
    }

    /**
     * @param controllerType
     * @return
     */
    public Controller getController(JSONObject controllerJson, FacilioControllerType controllerType) {
        LOGGER.info(" getting controllers from db");
        // avoids null pointer --  loads the controller map
        if ((controllerMapList.get(controllerType.asInt()) == null)) {
            controllerMapList.get(controllerType.asInt()).putAll(new HashMap<>());
        }
        // map empty
        if (controllerType == null) { // controller type is null
            return null;
            //return ControllerApiV2.getAllControllersFromDb(agentId).get(controllerIdentifier);
        } else if ((controllerMapList.get(controllerType.asInt()).isEmpty())) { // map for the controllerType is empty
            LOGGER.info(" controller not found in map , getting from db");
            Map<String, Controller> controllers = ControllerApiV2.getControllersFromDb(agentId, controllerType,null); // get all controller fpr that controllerType
            if (controllers.isEmpty()) {
                return null;
            }
            for (String key : controllers.keySet()) {
                controllerMapList.get(controllers.get(key).getControllerType()).put(key, controllers.get(key));
            }
            return controllerMapList.get(controllerType.asInt()).get(controllerJson.toString());
        } else {
            LOGGER.info(" controller found in map ");
            if (controllerMapList.get(controllerType.asInt()).containsKey(controllerJson.toString())) {
                return controllerMapList.get(controllerType.asInt()).get(controllerJson.toString());
            } else {
                Controller controller = null;
                try {
                    controller = ControllerApiV2.getControllerFromDb(controllerJson, agentId, controllerType);
                } catch (Exception e) {
                    LOGGER.info(" Exception while fetching controller ",e);
                }
                if (controller != null) {
                    try {
                        controllerMapList.get(controllerType.asInt()).put(controller.getChildJSON().toString(), controller);
                        return controller;
                    } catch (Exception e) {
                        LOGGER.info("Exception occured, cant generate identifier");
                    }
                }
                LOGGER.info("Exception Occurred, No such controller for agent " + agentId + ", with identifier " + controllerJson);
                return null;
            }
        }
    }

    /**
     * {
     * "controller": "X_#_X",
     * "type": 1,
     * ...,
     * ...,
     * ...
     * }
     * "controller" - identifier actully, MANDATORY- if not present returns null.
     * "type" - mandatory for Java-Agent, but if not present will make it custom controllers.
     * <p>
     * <p>
     * Gets respective controller or makes them from the payload.
     *
     * @param payload - must contain key "controller"
     * @return - null if controller is not found or if the key "controller" is missing.
     */
    public Controller getControllerFromAgentPayload(JSONObject payload) {
        if (!payload.containsKey(AgentConstants.CONTROLLER)) {
            return null;
        }

        JSONObject controllerJson = (JSONObject) payload.get(AgentConstants.CONTROLLER);

        if (payload.containsKey(AgentConstants.TYPE)) {
            FacilioControllerType controllerType = FacilioControllerType.valueOf(((Number) payload.get(AgentConstants.TYPE)).intValue());

            if (controllerType != null) {
                LOGGER.info(" getControllerFromPayload " + controllerMapList.get(controllerType.asInt()));
                return getController(controllerJson, controllerType);
            }
            // if controllerType is null from the information - then make it custom
            else {
                LOGGER.info(" getControllerFromPayload " + controllerMapList.get(controllerType.asInt()));
                Controller customController = ControllerUtilV2.makeCustomController(orgId, agentId, controllerJson);
                controllerMapList.get(FacilioControllerType.CUSTOM.asInt()).put(customController.getName(), customController);
                return customController;
            }
        }
        // if payload isn't having controllerType information- make it custom.
        else if (payload.containsKey(AgentConstants.CONTROLLER)) {
            LOGGER.info(" getControllerFromPayload " + controllerMapList.get(FacilioControllerType.CUSTOM.asInt()));
            Controller customController = ControllerUtilV2.makeCustomController(orgId, agentId, controllerJson);
            controllerMapList.get(FacilioControllerType.CUSTOM.asInt()).put(customController.getName(), customController);
            return customController;
        } else {
            LOGGER.info(" EXveption occurred, Controller detail missing from payload -> " + payload);
        }
        return null;
    }

    public JSONArray getAllControllerList() {
        JSONArray controllerArray = new JSONArray();
        Map<String, Controller> controllers = new HashMap<>();
        for (FacilioControllerType type : FacilioControllerType.values()) {
            controllers.putAll(ControllerApiV2.getControllersFromDb(agentId, type,null));
        }
        for (String controllerIdentifier : controllers.keySet()) {
            controllerArray.add(controllers.get(controllerIdentifier).toJSON());
        }
        return controllerArray;
    }

}
