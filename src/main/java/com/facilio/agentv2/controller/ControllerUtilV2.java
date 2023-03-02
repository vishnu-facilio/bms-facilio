package com.facilio.agentv2.controller;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.agentv2.modbusrtu.ModbusRtuControllerContext;
import com.facilio.agentv2.modbusrtu.RtuNetworkContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.custom.CustomController;
import com.facilio.util.FacilioUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facilio.agent.controller.FacilioControllerType.MODBUS_RTU;

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


    public static Controller makeCustomController(long orgId, long agentId, JSONObject controllerJson) throws Exception {
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
            if (AgentConstants.getControllerBean().addController(controller) < 1) {
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

        if((controllerJson != null)&&(controllerType != null)){
            Controller mockController = AgentConstants.getControllerBean().makeControllerFromMap(controllerJson,controllerType);
            if ((controllerMapList.get(controllerType.asInt()) == null)) {// avoids null pointer --  loads the controller map
                controllerMapList.get(controllerType.asInt()).putAll(new HashMap<>());
            }
            if ((controllerMapList.get(controllerType.asInt()).isEmpty())) { // map for the controllerType is empty
                Map<String, Controller> controllers = new HashMap<>(); // get all controller fpr that controllerType
                try {
                    GetControllerRequest getControllerRequest = new GetControllerRequest()
                            .withAgentId(agentId)
                            .ofType(controllerType);
                    if (controllerType == MODBUS_RTU) {
                        getControllerRequest.withControllerProperties(controllerJson, controllerType);
                    }
                    controllers = getControllerRequest.getControllersMap();
                    if (AccountUtil.getCurrentOrg().getOrgId() == 152) {
                        LOGGER.info("Controllers from DB for :" + controllerType.asInt() + " : " + controllers);
                    }
                }catch (Exception e){
                    LOGGER.info("Exception while getting controller og type "+controllerType.asString()+" for agent "+agentId);
                }
                if (controllers.isEmpty()) {
                    return null;
                }
                for (String key : controllers.keySet()) {
                    controllerMapList.get(controllers.get(key).getControllerType()).put(key, controllers.get(key));
                }
                return controllerMapList.get(controllerType.asInt()).get(mockController.getIdentifier());
            }
            else {
                if (controllerMapList.get(controllerType.asInt()).containsKey(mockController.getIdentifier())) {
                    return controllerMapList.get(controllerType.asInt()).get(mockController.getIdentifier());
                } else {
                    Controller controller = null;
                    try {
                        GetControllerRequest getControllerRequest = new GetControllerRequest()
                                .withAgentId(agentId)
                                .withControllerProperties(controllerJson,controllerType);
                        controller = getControllerRequest.getController();
                        if (AccountUtil.getCurrentOrg().getOrgId() == 152 && controller != null) {
                            LOGGER.info("Controller from DB for :" + controllerType.asInt() + " : " + controller.toJSON());
                        }
                        //controller = AgentConstants.getControllerBean().getControllerFromDb(controllerJson, agentId, controllerType);
                    } catch (Exception e) {
                        LOGGER.info(" Exception while fetching controller ",e);
                    }
                    if (controller != null) {
                        try {
                            controllerMapList.get(controllerType.asInt()).put(controller.getIdentifier(), controller);
                            return controller;
                        } catch (Exception e) {
                            LOGGER.info("Exception occurred, cant generate identifier");
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

    public static <T extends Controller> T getControllerFromJSON(JSONObject deviceJSON,FacilioControllerType controllerType) throws Exception {
        Controller controller = null;
        JSONObject controllerPropJSON;
        Object object = deviceJSON.get(AgentConstants.CONTROLLER);
        if(object != null){
            if(object instanceof String){
                controllerPropJSON = (JSONObject) new JSONParser().parse(String.valueOf(object));
                deviceJSON.putAll(controllerPropJSON);
            }else {
                deviceJSON.putAll((JSONObject)object);
            }
        }
        controller = AgentConstants.getControllerBean().makeControllerFromMap(deviceJSON,controllerType);
        return (T) controller;
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
     * @param jsonObject - must contain key "controller"
     * @return - null if controller is not found or if the key "controller" is missing.
     */

    private static boolean containsValueCheck(String key, Map<String, Object> jsonObject) {
        if (jsonObject.containsKey(key) && (jsonObject.get(key) != null)) {
            return true;
        }
        return false;
    }

    public static boolean processControllers(FacilioAgent agent, JSONObject payload) throws Exception {

        if (containsValueCheck(AgentConstants.DATA, payload)) {
            JSONArray controllersArray = (JSONArray) payload.get(AgentConstants.DATA);

            FacilioUtil.throwIllegalArgumentException((controllersArray == null || controllersArray.isEmpty()), "Controller Array should not be empty");

            Long ct = (Long) ((JSONObject) controllersArray.get(0)).get(AgentConstants.CONTROLLER_TYPE);
            FacilioControllerType controllerType = FacilioControllerType.valueOf(Math.toIntExact(ct));

            List<? extends Controller> controllerList = new ArrayList<>();
            for (Object controllerObject : controllersArray) {
                JSONObject deviceJSON = (JSONObject) controllerObject;
                deviceJSON.put(AgentConstants.AGENT_ID, agent.getId());
                controllerList.add(getControllerFromJSON(deviceJSON, controllerType));
            }

            List<? extends Controller> controllersToAdd = AgentConstants.getControllerBean().getControllersToAdd(agent.getId(), controllerType, controllerList);
            if(controllersToAdd !=null && !controllersToAdd.isEmpty()){
                for (Object controllerObject : controllersToAdd) {
                    Controller controller = (Controller) controllerObject;
                    if (controller != null) {
                        controller.setAgentId(agent.getId());
                        controller.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
                        if (agent.getSiteId() < 1) {
                            LOGGER.info(" Exception occurred. Agent is missing its siteId,skipping device processing.");
                            continue;
                        }
                        controller.setSiteId(agent.getSiteId());
                        controller.setActive(true);
                        controller.setDataInterval(900000);
                        controller.setControllerType(controllerType.asInt());
                        long controllerId = -1;
                        if (controller.getControllerType() == MODBUS_RTU.asInt()) {

                            ModbusRtuControllerContext rtuControllerContext = (ModbusRtuControllerContext) controller;
                            RtuNetworkContext rtuNetworkContextFromJson = rtuControllerContext.getNetwork();
                            RtuNetworkContext rtuNetworkContext = RtuNetworkContext.getRtuNetworkContext(agent.getId(), rtuNetworkContextFromJson.getComPort());
                            if (rtuNetworkContext == null) {
                                rtuControllerContext.setNetwork(rtuNetworkContextFromJson);
                            } else {
                                rtuControllerContext.setNetwork(rtuNetworkContext);
                            }
                            controllerId = AgentConstants.getControllerBean().addController(rtuControllerContext);
                        } else {
                            controllerId = AgentConstants.getControllerBean().addController(controller);
                        }
                        if (controllerId > 0) {
                            controller.setId(controllerId);
                        }
                    }else {
                        throw new Exception("Controller cant be null ");
                    }
                }
            }
        }
        return true;
    }
}
