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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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

    public static boolean discoverPoints(Long controllerId) throws Exception {
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


    public static Map<Long, Controller> fieldDeviceToController(Device device) throws Exception {
        Controller controller;
        Map<Long, Controller> deviceIdControllerMap = new HashMap<>();
        JSONObject controllerProps = device.getControllerProps();
        if ((controllerProps != null) && (!controllerProps.isEmpty())) {
            controllerProps.put(AgentConstants.DEVICE_ID, device.getId());
            if (controllerProps.containsKey(AgentConstants.CONTROLLER)) {
                controllerProps.putAll((JSONObject)device.getControllerProps().get(AgentConstants.CONTROLLER));
            }
            if(controllerProps.containsKey(AgentConstants.AGENT_TYPE)){
                controllerProps.remove(AgentConstants.AGENT_TYPE);
            }
            controller = makeControllerFromFieldDevice(device);
            if (controller != null) {
                controller.setAgentId(device.getAgentId());
                Controller controllerFromDb = null;//ControllerApiV2.getControllerFromDb(controller.getChildJSON(), agentId, FacilioControllerType.valueOf(controller.getControllerType()));
                GetControllerRequest getControllerRequest = new GetControllerRequest()
                        .withAgentId(device.getAgentId())
                        .withControllerProperties(controller.getChildJSON(),FacilioControllerType.valueOf(controller.getControllerType()));
                controllerFromDb = getControllerRequest.getController();
                if (controllerFromDb != null) {
                    LOGGER.info(" controller present ");
                    deviceIdControllerMap.put(device.getId(), controllerFromDb);

                } else {
                    LOGGER.info(" making new controller ");
                    controller.setActive(true);
                    controller.setDataInterval(900000);
                    controller.setAvailablePoints(0);
                    controller.setDeviceId(device.getId());
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

    public static Controller makeControllerFromFieldDevice(Device fieldDevice) {
        Controller controller = null;
        try {
            if(fieldDevice != null){
                FacilioControllerType controllerType = FacilioControllerType.valueOf(fieldDevice.getControllerType());

                JSONObject controllerPropJSON;
                JSONObject controllerJSON = fieldDevice.getControllerProps();
                Object object = controllerJSON.get(AgentConstants.CONTROLLER);
                if(object instanceof String){
                    controllerPropJSON = (JSONObject) new JSONParser().parse(String.valueOf(object));
                    controllerJSON.putAll(controllerPropJSON);
                }else {
                    controllerJSON.putAll((JSONObject)object);
                }
                controller = ControllerApiV2.makeControllerFromMap(controllerJSON,controllerType);
            }else {
                LOGGER.info("fieldDevice cant be null");
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

            GetControllerRequest getControllerRequest = new GetControllerRequest()
                    .withAgentId(agentId)
                    .withControllerProperties(controllerJson,FacilioControllerType.CUSTOM);
            try {
                controller = (CustomController) getControllerRequest.getController();
            }catch (Exception e){
                LOGGER.info("Exception occurred while making custom controller ",e);
            }
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



    public Controller getCachedController(JSONObject controllerJson, FacilioControllerType controllerType) throws Exception {
        LOGGER.info(" getting controllers from db");

        if((controllerJson != null)&&(controllerType != null)){
            Controller mockController = ControllerApiV2.makeControllerFromMap(controllerJson,controllerType);
            LOGGER.info(" looking controller "+mockController.getIdentifier());
            if ((controllerMapList.get(controllerType.asInt()) == null)) {// avoids null pointer --  loads the controller map
                LOGGER.info(" controllermap is null for controllerType ->"+controllerType);
                controllerMapList.get(controllerType.asInt()).putAll(new HashMap<>());
            }
            if ((controllerMapList.get(controllerType.asInt()).isEmpty())) { // map for the controllerType is empty
                LOGGER.info(" controller map for type "+controllerType.asString()+" , getting from db");
                Map<String, Controller> controllers = new HashMap<>(); // get all controller fpr that controllerType
                try {
                    GetControllerRequest getControllerRequest = new GetControllerRequest()
                            .withAgentId(agentId)
                            .ofType(controllerType);
                    controllers = getControllerRequest.getControllersMap();
                }catch (Exception e){
                    LOGGER.info("Exception while getting controller og type "+controllerType.asString()+" for agent "+agentId);
                }
                if (controllers.isEmpty()) {
                    return null;
                }
                for (String key : controllers.keySet()) {
                    controllerMapList.get(controllers.get(key).getControllerType()).put(key, controllers.get(key));
                }
                LOGGER.info(" got controllers from db and loading it to map "+controllers.size());
                return controllerMapList.get(controllerType.asInt()).get(mockController.getIdentifier());
            }
            else {
                LOGGER.info(" controllers present for type "+controllerType.asString());
                if (controllerMapList.get(controllerType.asInt()).containsKey(mockController.getIdentifier())) {
                    LOGGER.info(" controller present in cache ");
                    return controllerMapList.get(controllerType.asInt()).get(mockController.getIdentifier());
                } else {
                    Controller controller = null;
                    try {
                        GetControllerRequest getControllerRequest = new GetControllerRequest()
                                .withAgentId(agentId)
                                .withControllerProperties(controllerJson,controllerType);
                        controller = getControllerRequest.getController();
                        //controller = ControllerApiV2.getControllerFromDb(controllerJson, agentId, controllerType);
                    } catch (Exception e) {
                        LOGGER.info(" Exception while fetching controller ",e);
                    }
                    if (controller != null) {
                        try {
                            controllerMapList.get(controllerType.asInt()).put(controller.getIdentifier(), controller);
                            LOGGER.info(" caching controller "+controller.getIdentifier());
                            return controller;
                        } catch (Exception e) {
                            LOGGER.info("Exception occured, cant generate identifier");
                        }
                    }
                    LOGGER.info("Exception Occurred, No such controller for agent " + agentId + ", with identifier " + controllerJson);
                    return null;
                }
            }


        }else {
         throw new Exception(" controllerJSON "+controllerJson+" controllerType"+controllerType.asString()+" cant be null");
        }


        // map empty

    }

/*    *//**
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
     *//*
    public Controller getControllerFromAgentPayload(JSONObject payload) throws Exception {
        if (payload.containsKey(AgentConstants.CONTROLLER)) {

            JSONObject controllerJson = (JSONObject) payload.get(AgentConstants.CONTROLLER);
            if (payload.containsKey(AgentConstants.CONTROLLER_TYPE)) {
                FacilioControllerType controllerType = FacilioControllerType.valueOf(((Number) payload.get(AgentConstants.CONTROLLER_TYPE)).intValue());
                if (controllerType != null) {
                    LOGGER.info(" getControllerFromPayload " + controllerMapList.get(controllerType.asInt()));
                    GetControllerRequest getControllerRequest = new GetControllerRequest()
                            .withControllerProperties(controllerJson,controllerType);
                    Controller controller = getControllerRequest.getController();
                    return controller;
                }
                // if controllerType is null from the information - then make it custom

            }
        }  else {
            throw new Exception("payload is missing key "+AgentConstants.CONTROLLER);
        }
    }*/

}
