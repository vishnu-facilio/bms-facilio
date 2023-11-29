package com.facilio.agentv2.controller;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.alarms.AgentAlarmContext;
import com.facilio.agent.alarms.AgentEventContext;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.AgentUtilV2;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.agentv2.modbusrtu.ModbusRtuControllerContext;
import com.facilio.agentv2.modbusrtu.RtuNetworkContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.custom.CustomController;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.remotemonitoring.RemoteMonitorConstants;
import com.facilio.remotemonitoring.compute.RawAlarmUtil;
import com.facilio.remotemonitoring.context.AlarmApproach;
import com.facilio.remotemonitoring.context.AlarmTypeContext;
import com.facilio.remotemonitoring.context.IncomingRawAlarmContext;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.sql.SQLException;
import java.util.*;

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
                    .withControllerProperties(controllerJson, FacilioControllerType.CUSTOM);
            try {
                controller = (CustomController) getControllerRequest.getController();
            } catch (Exception e) {
                LOGGER.info("Exception occurred while making custom controller ", e);
            }
        } catch (Exception e) {
            LOGGER.info(" Exception while fetching controller ", e);
        }
        if (controller == null) {
            controller = new CustomController();
            controller.setOrgId(orgId);
            controller.setAgentId(agentId);
            controller.setActive(true);
            controller.setName((String) controllerJson.get(AgentConstants.NAME));
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

        if ((controllerJson != null) && (controllerType != null)) {
            Controller mockController = AgentConstants.getControllerBean().makeControllerFromMap(controllerJson, controllerType);
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
                } catch (Exception e) {
                    LOGGER.info("Exception while getting controller og type " + controllerType.asString() + " for agent " + agentId);
                }
                if (controllers.isEmpty()) {
                    return null;
                }
                for (String key : controllers.keySet()) {
                    controllerMapList.get(controllers.get(key).getControllerType()).put(key, controllers.get(key));
                }
                return controllerMapList.get(controllerType.asInt()).get(mockController.getIdentifier());
            } else {
                if (controllerMapList.get(controllerType.asInt()).containsKey(mockController.getIdentifier())) {
                    return controllerMapList.get(controllerType.asInt()).get(mockController.getIdentifier());
                } else {
                    Controller controller = null;
                    try {
                        GetControllerRequest getControllerRequest = new GetControllerRequest()
                                .withAgentId(agentId)
                                .withControllerProperties(controllerJson, controllerType);
                        controller = getControllerRequest.getController();
                        if (AccountUtil.getCurrentOrg().getOrgId() == 152 && controller != null) {
                            LOGGER.info("Controller from DB for :" + controllerType.asInt() + " : " + controller.toJSON());
                        }
                        //controller = AgentConstants.getControllerBean().getControllerFromDb(controllerJson, agentId, controllerType);
                    } catch (Exception e) {
                        LOGGER.info(" Exception while fetching controller ", e);
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


        } else {
            throw new Exception(" controllerJSON " + controllerJson + " controllerType" + controllerType.asString() + " cant be null");
        }


        // map empty

    }

    public static <T extends Controller> T getControllerFromJSON(JSONObject deviceJSON, FacilioControllerType controllerType) throws Exception {
        Controller controller = null;
        JSONObject controllerPropJSON;
        Object object = deviceJSON.get(AgentConstants.CONTROLLER);
        if (object != null) {
            if (object instanceof String) {
                controllerPropJSON = (JSONObject) new JSONParser().parse(String.valueOf(object));
                deviceJSON.putAll(controllerPropJSON);
            } else {
                deviceJSON.putAll((JSONObject) object);
            }
        }
        controller = AgentConstants.getControllerBean().makeControllerFromMap(deviceJSON, controllerType);
        return (T) controller;
    }

    /*    */

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
            if (controllersToAdd != null && !controllersToAdd.isEmpty()) {
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
                    } else {
                        throw new Exception("Controller cant be null ");
                    }
                }
            }
        }
        return true;
    }

    private static void changeControllerActiveStatus(Long agentId, List<Long> controllerIds, boolean status) throws SQLException {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getControllersField());

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getNewControllerModule().getTableName())
                .fields(FieldFactory.getControllersField())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_ID), String.valueOf(agentId), StringOperators.IS));

        if (status) {
            updateBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.ACTIVE), String.valueOf(false), BooleanOperators.IS));
        }
        if (controllerIds != null && !controllerIds.isEmpty()) {
            updateBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.ID), controllerIds, StringOperators.IS));
        }

        Map<String, Object> toUpdate = new HashMap<>();
        toUpdate.put(AgentConstants.ACTIVE, status);
        updateBuilder.update(toUpdate);
    }

    public static void makeControllersInActive(FacilioAgent agent, List<Long> controllerIds) throws SQLException {
        changeControllerActiveStatus(agent.getId(), controllerIds, false);
    }

    public static void makeControllersActive(FacilioAgent agent, List<Long> controllerIds) throws SQLException {
        changeControllerActiveStatus(agent.getId(), controllerIds, true);
    }

    public static void makeControllersActiveAndInactive(FacilioAgent agent, List<Long> controllerIds) throws SQLException {
        makeControllersActive(agent, null); // Making all the controllers active and in-active using controller ids.
        makeControllersInActive(agent, controllerIds);
    }

    public static void processControllerRawAlarm(List<Controller> offlineControllers, String severity) throws Exception {
        Long timestamp = System.currentTimeMillis();

        for (Controller controller : offlineControllers) {
            IncomingRawAlarmContext alarmContext = new IncomingRawAlarmContext();
            alarmContext.setMessage("Controller offline");
            alarmContext.setController(controller);
            alarmContext.setSourceType(IncomingRawAlarmContext.RawAlarmSourceType.CONTROLLER);
            alarmContext.setAlarmApproach(AlarmApproach.RETURN_TO_NORMAL.getIndex());
            AlarmTypeContext alarmType = new AlarmTypeContext();
            alarmType.setLinkName(RemoteMonitorConstants.SystemAlarmTypes.CONTROLLER_OFFLINE);
            alarmContext.setAlarmType(alarmType);
            if (severity.equals(FacilioConstants.Alarm.CRITICAL_SEVERITY)) {
                alarmContext.setOccurredTime(timestamp);
            } else {
                alarmContext.setClearedTime(timestamp);
            }

            RawAlarmUtil.pushToStormRawAlarmQueue(alarmContext);
        }
    }

    public static void clearRawControllerAlarm(FacilioAgent agent, Controller controller) throws Exception {
        if (!controller.isActive()) {
            LOGGER.info("Making Controller active and Clearing Controller Raw Alarm for Controller : " + controller.getName());
            makeControllersActive(agent, Collections.singletonList(controller.getId()));
            processControllerRawAlarm(Collections.singletonList(controller), FacilioConstants.Alarm.CLEAR_SEVERITY);
        }
    }

    public static void processUpdateLastDataReceivedTimeAndClearControllerAlarm(FacilioAgent agent, Controller controller) throws Exception {
        try {
            AgentConstants.getControllerBean().updateLastDataReceivedTime(agent.getId(), controller.getId(), System.currentTimeMillis());
            clearRawControllerAlarm(agent, controller);
        } catch (Exception e) {
            LOGGER.error("Exception while updating last data received time/Clear raw controller alarm", e);
        }
    }

    public static void clearControllerAlarm(FacilioAgent agent) throws Exception {
        long currentTime = System.currentTimeMillis();
        AgentEventContext event = getControllerEventContext(agent, currentTime, FacilioConstants.Alarm.CLEAR_SEVERITY, null);
        AgentUtilV2.addEventToDB(event);
        LOGGER.info("Cleared Controller Alarm for Agent : " + agent.getDisplayName() + " ( ID :" + agent.getId() + ")");
    }

    public static void raiseControllerAlarm(FacilioAgent agent, List<Controller> controllers) throws Exception {
        long currentTime = System.currentTimeMillis();
        AgentEventContext event = getControllerEventContext(agent, currentTime, FacilioConstants.Alarm.CRITICAL_SEVERITY, controllers);
        AgentUtilV2.addEventToDB(event);
        LOGGER.info("Added controller Alarm for Agent : " + agent.getDisplayName() + " ( ID :" + agent.getId() + ")");
    }

    private static AgentEventContext getControllerEventContext(FacilioAgent agent, long currentTime, String severity, List<Controller> controllers) {
        AgentEventContext event = new AgentEventContext();
        event.setMessage("Controllers are unresponsive in agent " + agent.getDisplayName());
        if (severity.equals(FacilioConstants.Alarm.CRITICAL_SEVERITY)) {
            StringBuilder descBuilder = new StringBuilder();
            descBuilder.append("Count : " + controllers.size());
            descBuilder.append(", Controllers Name: ");
            for (Controller c : controllers) {
                descBuilder.append(c.getName()).append(", ");
            }
            event.setDescription(descBuilder.toString());
            event.setComment("Disconnected time : " + DateTimeUtil.getFormattedTime(currentTime));
        }
        event.setSeverityString(severity);
        event.setCreatedTime(currentTime);
        event.setSiteId(AccountUtil.getCurrentSiteId());
        event.setAgent(agent);
        event.setControllersList(controllers);
        event.setAgentAlarmType(AgentAlarmContext.AgentAlarmType.CONTROLLER.getIndex());
        return event;
    }

    public static void createOrDeleteControllerOfflineAlarmJob(FacilioAgent agent, boolean createJob) throws Exception {
        if (createJob) {
            if (FacilioTimer.getJob(agent.getId(), FacilioConstants.Job.CONTROLLER_OFFLINE_ALARM_JOB_NAME) == null &&
                    agent.getControllerAlarmIntervalInMins() != null && agent.getControllerAlarmIntervalInMins() > 0) {
                LOGGER.info("Creating Controller Offline Alarm Job for agent - " + agent.getDisplayName());
                AgentConstants.getAgentBean().scheduleJob(agent, FacilioConstants.Job.CONTROLLER_OFFLINE_ALARM_JOB_NAME);
            }
        } else {
            LOGGER.info("Deleting Controller Offline Alarm Job for agent - " + agent.getDisplayName());
            FacilioTimer.deleteJob(agent.getId(), FacilioConstants.Job.CONTROLLER_OFFLINE_ALARM_JOB_NAME);
        }
    }
}
