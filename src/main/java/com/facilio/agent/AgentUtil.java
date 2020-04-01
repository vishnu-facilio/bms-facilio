package com.facilio.agent;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentUtilV2;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.events.tasker.tasks.EventUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class writes agent's payload data to a table in DB.
 */
public class AgentUtil {
    private long orgId;
    private String orgDomainName;
    private static final long DEFAULT_TIME = 10L;

    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe


    private Map<String, FacilioAgent> agentMap = new HashMap<>();

    public Map<String, FacilioAgent> getAgentMap() {
        return agentMap;
    }

    public int getAgentCount() {
        return agentMap.size();
    }

    private int agentCount;

    private static final Logger LOGGER = LogManager.getLogger(AgentUtil.class.getName());

    public AgentUtil(long orgId, String orgDomainName) {
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
    }

    public void populateAgentContextMap(String agentName, AgentType type) {
        try {
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            List<Map<String, Object>> records = bean.getAgentDataMap(agentName, type);
            for (Map<String, Object> record : records) {
                JSONObject payload = new JSONObject();
                payload.putAll(record);
                FacilioAgent agent = getFacilioAgentFromJson(payload);
                agentMap.put(agent.getAgentName(), agent);
            }
        } catch (Exception e1) {
            LOGGER.info("exception ", e1);
        }
    }

    public FacilioAgent getFacilioAgent(String agentName, AgentType type) {
        FacilioAgent agent = agentMap.get(agentName);
        if (agent == null) {
            populateAgentContextMap(agentName, type);
            agent = agentMap.get(agentName);
        }
        return agent;
    }

    /**
     * This method writes data from the Agent to its table in database using {@link GenericInsertRecordBuilder}<br>
     * this also adds appropriate values to  each<br>
     *
     * @param payload is the JSONObject from Record and contains data to process
     */

    private FacilioAgent getFacilioAgentFromJson(JSONObject payload) {
        FacilioAgent agent = new FacilioAgent();
        if (payload.containsKey(AgentKeys.STATE)) {
            agent.setAgentState(Integer.parseInt(payload.get(AgentKeys.STATE).toString()));
        } else {
            agent.setAgentState(0);
        }
        if (payload.containsKey(AgentKeys.ID)) {
            agent.setId(Long.parseLong(payload.get(AgentKeys.ID).toString()));
        }
        if (payload.containsKey(AgentKeys.SITE_ID)) {
            agent.setSiteId(Long.parseLong(payload.get(AgentKeys.SITE_ID).toString()));
        }
        if (payload.containsKey(AgentKeys.VERSION)) {
            agent.setAgentDeviceDetails(payload.get(AgentKeys.VERSION).toString());

            if (payload.containsKey(AgentKeys.FACILIO_MQTT_VERSION)) {
                agent.setAgentVersion(payload.get(AgentKeys.FACILIO_MQTT_VERSION).toString());
            }
        }

        if (payload.containsKey(AgentKeys.CONNECTION_STATUS)) {
            agent.setAgentConnStatus(Boolean.valueOf(payload.get(AgentKeys.CONNECTION_STATUS).toString()));
        } else {
            agent.setAgentConnStatus(true);
        }

        if (payload.containsKey(AgentKeys.DATA_INTERVAL)) {
            agent.setInterval(Long.parseLong(payload.get(AgentKeys.DATA_INTERVAL).toString()));
        } else {
            agent.setInterval(DEFAULT_TIME);
        }

        if (payload.containsKey(AgentKeys.NUMBER_OF_CONTROLLERS)) {
            agent.setAgentNumberOfControllers(Integer.parseInt(payload.get(AgentKeys.NUMBER_OF_CONTROLLERS).toString()));
        }

        if (payload.containsKey(AgentKeys.NAME)) {
            agent.setAgentName(payload.get(AgentKeys.NAME).toString().trim());
        } else if (payload.containsKey(PublishType.AGENT.getValue())) {
            agent.setAgentName(payload.get(PublishType.AGENT.getValue()).toString().trim());
        } else {
            agent.setAgentName(orgDomainName);
        }

        if (payload.containsKey(AgentKeys.AGENT_TYPE)) {

            agent.setAgentType(payload.get(AgentKeys.AGENT_TYPE).toString());
        }
        if (payload.containsKey(AgentKeys.DELETED_TIME)) {
            agent.setDeletedTime(Long.parseLong(payload.get(AgentKeys.DELETED_TIME).toString()));
        }
        if (payload.containsKey(AgentKeys.WRITABLE)) {
            agent.setWritable(Boolean.parseBoolean(payload.get(AgentKeys.WRITABLE).toString()));
        } else {
            agent.setWritable(false);
        }
        return agent;

    }

    public long processAgent(JSONObject jsonObject, String agentName) {
       /* String agentName = agentName;
        if (StringUtils.isEmpty(agentName)) { //Temp fix to avoid NPE
        	agentName = orgDomainName;
            LOGGER.info(" in process agent agentName="+agentName);
        }*/
        FacilioAgent agent = getFacilioAgent(agentName, null);
        if (jsonObject.containsKey(AgentKeys.DATA_INTERVAL)) {
            Long currDataInterval = Long.parseLong(jsonObject.get(AgentKeys.DATA_INTERVAL).toString());
            if (currDataInterval.longValue() > 120L) {
                currDataInterval = 15L;
                jsonObject.replace(AgentKeys.DATA_INTERVAL, currDataInterval);
            }
        }
        if (agent == null) {
            agent = getFacilioAgentFromJson(jsonObject);
            return addAgent(agent);
        } else {
            Condition agentNameCondition = new Condition();
            agentNameCondition.setField(FieldFactory.getAgentNameField(ModuleFactory.getAgentDataModule()));
            agentNameCondition.setOperator(StringOperators.IS);
            agentNameCondition.setValue(agentName);
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.CRITERIA, agentNameCondition);

            FacilioChain updateAgentChain = TransactionChainFactory.updateAgent();

            try {

                Map<String, Object> toUpdate = new HashMap<>();
                if (jsonObject.containsKey(AgentKeys.CONNECTION_STATUS)) {
                    Boolean connectionStatus = Boolean.valueOf((jsonObject.get(AgentKeys.CONNECTION_STATUS).toString()));
                    if (agent.getAgentConnStatus() != connectionStatus) {
                        toUpdate.put(AgentKeys.CONNECTION_STATUS, connectionStatus);
                        agent.setAgentConnStatus(connectionStatus);
                    }
                } else {
                    toUpdate.put(AgentKeys.CONNECTION_STATUS, true);
                    agent.setAgentConnStatus(true);
                }

                if (jsonObject.containsKey(AgentKeys.DISPLAY_NAME)) {
                    if (!jsonObject.get(AgentKeys.DISPLAY_NAME).toString().equals(agent.getDisplayName())) {
                        toUpdate.put(AgentKeys.DISPLAY_NAME, jsonObject.get(AgentKeys.DISPLAY_NAME));
                        agent.setDisplayName(jsonObject.get(AgentKeys.DISPLAY_NAME).toString());
                    }
                }

                if (jsonObject.containsKey(AgentKeys.STATE)) {
                    Integer currStatus = Integer.parseInt(jsonObject.get(AgentKeys.STATE).toString());
                    if (!agent.getAgentState().equals(currStatus)) {
                        toUpdate.put(AgentKeys.STATE, currStatus);
                        agent.setAgentState(currStatus);

                    }
                } else {
                    toUpdate.put(AgentKeys.STATE, 0);
                    agent.setAgentState(0);
                }

                if (jsonObject.containsKey(AgentKeys.DATA_INTERVAL)) {
                    Long currDataInterval = Long.parseLong(jsonObject.get(AgentKeys.DATA_INTERVAL).toString());
                    if ((agent.getInterval() != null) && agent.getInterval() != currDataInterval.longValue()) {
                        toUpdate.put(AgentKeys.DATA_INTERVAL, currDataInterval);
                        agent.setInterval(currDataInterval);
                    }
                }
                if (jsonObject.containsKey(AgentKeys.AGENT_TYPE)) {
                    agent.setAgentType(jsonObject.get(AgentKeys.AGENT_TYPE).toString().trim());
                    toUpdate.put(AgentKeys.AGENT_TYPE, agent.getAgentType());
                }
                if (jsonObject.containsKey(AgentKeys.NUMBER_OF_CONTROLLERS)) {
                    Integer currNumberOfControllers = Integer.parseInt(jsonObject.get(AgentKeys.NUMBER_OF_CONTROLLERS).toString());
                    if ((agent.getAgentNumberOfControllers().intValue() != currNumberOfControllers.intValue())) {
                        toUpdate.put(AgentKeys.NUMBER_OF_CONTROLLERS, currNumberOfControllers);
                        agent.setAgentNumberOfControllers(currNumberOfControllers);
                    }
                } else {
                    toUpdate.put(AgentKeys.NUMBER_OF_CONTROLLERS, 0);
                    agent.setAgentNumberOfControllers(0);
                }
                if (jsonObject.containsKey(AgentKeys.STATUS)) {
                    int status = Integer.parseInt(jsonObject.get(AgentKeys.STATUS).toString());
                    if (status == 0) {
                        AgentUtilV2.raiseAgentAlarm(AgentApiV2.getAgent(agent.getId()));
                    }
                    if (status == 1) {
                        AgentUtilV2.dropAgentAlarm(AgentApiV2.getAgent(agent.getId()));
                    }
                }

                if (jsonObject.containsKey(AgentKeys.VERSION)) {
                    Object currDeviceDetails = jsonObject.get(AgentKeys.VERSION);
                    String currDeviceDetailsString = currDeviceDetails.toString();
                    String currVersion = getVersion(currDeviceDetails);
                    if ((agent.getAgentDeviceDetails() == null) || (agent.getAgentDeviceDetails() != null && !(agent.getAgentDeviceDetails().equalsIgnoreCase(currDeviceDetailsString)))) {
                        toUpdate.put(AgentKeys.DEVICE_DETAILS, currDeviceDetailsString);
                        toUpdate.put(AgentKeys.VERSION, currVersion);
                        agent.setAgentDeviceDetails(currDeviceDetailsString);
                        agent.setAgentVersion(currVersion);
                    }
                }
                if (jsonObject.containsKey(AgentKeys.WRITABLE)) {
                    Boolean currWriteble = Boolean.parseBoolean(jsonObject.get(AgentKeys.WRITABLE).toString());
                    if (agent.getWritable() != currWriteble) {
                        toUpdate.put(AgentKeys.WRITABLE, currWriteble);
                        agent.setWritable(currWriteble);
                    }
                }
                if (jsonObject.containsKey(AgentKeys.DELETED_TIME)) {
                    Long currDeletedTime = Long.parseLong(jsonObject.get(AgentKeys.DELETED_TIME).toString());
                    if (agent.getDeletedTime().longValue() != currDeletedTime.longValue()) {
                        agent.setDeletedTime(currDeletedTime);
                        toUpdate.put(AgentKeys.DELETED_TIME, currDeletedTime);
                    }
                }
                if (agent.getAgentType() == null && (jsonObject.containsKey(AgentKeys.AGENT_TYPE))) {
                    toUpdate.put(AgentKeys.AGENT_TYPE, (AgentType.valueOf(jsonObject.get(AgentKeys.AGENT_TYPE).toString())).getKey());
                }
                if (!toUpdate.isEmpty()) {
                    toUpdate.put(AgentKeys.LAST_MODIFIED_TIME, System.currentTimeMillis());
                }
                if (!toUpdate.isEmpty()) {
                    toUpdate.put(AgentKeys.LAST_DATA_RECEIVED_TIME, System.currentTimeMillis());
                    context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP, toUpdate);
                    if (updateAgentChain.execute(context)) {
                        return 1;
                    }
                }
                return 0;
            } catch (Exception e) {
                LOGGER.info("Exception occurred ", e);
            }
        }

        return 0;
    }

    /*public static void dropAgentAlarm(FacilioAgent agent) throws Exception {
        long currentTime = System.currentTimeMillis();
        AgentEventContext event = getAgentEventContext(agent, currentTime, FacilioConstants.Alarm.CLEAR_SEVERITY);
        addEventToDB(event);
        LOGGER.info("Cleared Agent Alarm for Agent : " + agent.getAgentName() + " ( ID :" + agent.getId() + ")");

    }

    private static void addEventToDB(AgentEventContext event) throws Exception {
        List<BaseEventContext> eventList = new ArrayList<BaseEventContext>();
        eventList.add(event);
        FacilioContext context = new FacilioContext();
        context.put(EventConstants.EventContextNames.EVENT_LIST, eventList);
        FacilioChain chain = TransactionChainFactory.getV2AddEventChain(false);
        chain.execute(context);
    }

    public static void raiseAgentAlarm(FacilioAgent agent) throws Exception {
        long currentTime = System.currentTimeMillis();
        AgentEventContext event = getAgentEventContext(agent, currentTime, FacilioConstants.Alarm.CRITICAL_SEVERITY);

        addEventToDB(event);
        LOGGER.info("Added Agent Alarm for Agent : " + agent.getAgentName() + " ( ID :" + agent.getId() + ")");

    }

    private static AgentEventContext getAgentEventContext(FacilioAgent agent, long currentTime, String severity) {
        AgentEventContext event = new AgentEventContext();
        String description = null;
        String message = null;
        if (severity.equals(FacilioConstants.Alarm.CRITICAL_SEVERITY)) {
            description = "Agent " + agent.getAgentName() + " has lost connection with the facilio cloud @" + DateTimeUtil.getFormattedTime(currentTime);
            message = "agent " + agent.getAgentName() + " connection lost ";
        } else if (severity.equals(FacilioConstants.Alarm.CLEAR_SEVERITY)) {
            description = "Agent " + agent.getAgentName() + " has lost connection with the facilio cloud @" + DateTimeUtil.getFormattedTime(currentTime);
            message = "agent " + agent.getAgentName() + " connection reestablished";
        }
        event.setMessage(message);
        event.setDescription(description);
        //event.setComment("Disconnected time : " + DateTimeUtil.getFormattedTime(currentTime));
        event.setSeverityString(severity);
        event.setCreatedTime(currentTime);
        event.setSiteId(AccountUtil.getCurrentSiteId());
        event.setAgent(agent);
        return event;
    }
*/

    public long addAgent(FacilioAgent agent) {
        JSONObject payload = new JSONObject();
        long currTime = System.currentTimeMillis();
        payload.put(AgentKeys.NAME, agent.getAgentName());
        payload.put(AgentKeys.DISPLAY_NAME, agent.getDisplayName());
        payload.put(AgentKeys.STATE, agent.getAgentState());
        payload.put(AgentKeys.CONNECTION_STATUS, agent.getAgentConnStatus());
        payload.put(AgentKeys.SITE_ID, agent.getSiteId());
        payload.put(AgentKeys.AGENT_TYPE, agent.getAgentType());
        payload.put(AgentKeys.DATA_INTERVAL, agent.getInterval());
        payload.put(AgentKeys.WRITABLE, agent.getWritable());
        payload.put(AgentKeys.DEVICE_DETAILS, agent.getAgentDeviceDetails());
        payload.put(AgentKeys.VERSION, agent.getAgentVersion());
        payload.put(AgentKeys.CREATED_TIME, currTime);
        payload.put(AgentKeys.LAST_MODIFIED_TIME, currTime);
        payload.put(AgentKeys.LAST_DATA_RECEIVED_TIME, currTime);
        FacilioChain chain = TransactionChainFactory.getAddAgentChain();
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.PAY_LOAD, payload);
        try {
            chain.execute(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FacilioModule agentDataModule = ModuleFactory.getAgentDataModule();
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder().table(AgentKeys.AGENT_TABLE).select(FieldFactory.getAgentDataFields())
//                .andCondition(CriteriaAPI.getCurrentOrgIdCondition(agentDataModule))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentNameField(agentDataModule), agent.getAgentName(), StringOperators.IS));
        long id = 0L;
        try {
            List<Map<String, Object>> row = selectRecordBuilder.get();
            if (row.size() == 1 && row.get(0).containsKey(AgentKeys.ID)) {
                id = Long.parseLong(row.get(0).get(AgentKeys.ID).toString());
            } else {
                LOGGER.info("Exception multiple entries with same AgentId ");
            }
        } catch (Exception e) {
            LOGGER.info("Exception while fetching agent detail for agentId ", e);
        }

        try {
            agent.setId(id);
            if (id > 0) {
                agentMap.put(agent.getAgentName(), agent);
            }
            return id;

        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
        return 0L;
    }


    private String getVersion(Object payload) {
        JSONObject jsonObject2 = (JSONObject) payload;
        return jsonObject2.get(AgentKeys.FACILIO_MQTT_VERSION).toString();
    }


    //This method fetches Details of all the agent in the current Org and which aren't deleted.
    public List<Map<String, Object>> agentDetails() {
        FacilioModule agentDataModule = ModuleFactory.getAgentDataModule();
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getControllerIdCount(ModuleFactory.getControllerModule()));
        fields.addAll(FieldFactory.getAgentDataFields());
        GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder().table(AgentKeys.AGENT_TABLE)
                .select(fields).leftJoin(AgentKeys.CONTROLLER_TABLE)
                .on(AgentKeys.AGENT_TABLE + ".ID=Controller.Agent_Id")
                .groupBy(AgentKeys.AGENT_TABLE + ".ID").andCondition(CriteriaAPI.getOrgIdCondition(orgId, agentDataModule))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getDeletedTimeField(agentDataModule), "NULL", CommonOperators.IS_EMPTY));

        try {
            return genericSelectRecordBuilder.get();
        } catch (Exception e) {
            LOGGER.info("Exception occured ", e);
        }
        return new ArrayList<>();
    }

    /**
     * This method inserts system's current time to the specified agent's Deleted_Time column so that it will be considered deleted.
     *
     * @return true if the deletion process happens and false if account is null or deletion doesn't happen.
     */
    public static boolean agentDelete(String agentName) {
        if (AccountUtil.getCurrentOrg() != null) {
            List<FacilioField> fields = new ArrayList<>();
            fields.add(FieldFactory.getDeletedTimeField(ModuleFactory.getAgentDataModule()));
            HashMap<String, Object> toUpdate = new HashMap<>();
            toUpdate.put(AgentKeys.DELETED_TIME, System.currentTimeMillis());
            GenericUpdateRecordBuilder genericUpdateRecordBuilder = new GenericUpdateRecordBuilder()
                    .table(AgentKeys.AGENT_TABLE)
                    .fields(fields);
//                    .andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getAgentDataModule()));
            if (agentName != null) {
                int deletedRows = 0;
                try {
                    deletedRows = genericUpdateRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentNameField(ModuleFactory.getAgentDataModule()), agentName, StringOperators.IS))
                            .update(toUpdate);
                } catch (SQLException e) {
                    LOGGER.info("Exception occurred");
                }
                return (deletedRows > 0);
            } else {
                return false;
            }
        }
        return false;
    }

    static boolean agentEdit(JSONObject payload) throws SQLException {
        boolean status = false;
        if (AccountUtil.getCurrentOrg() != null && payload.containsKey(AgentKeys.ID)) {
            FacilioChain agentEditChain = TransactionChainFactory.getAgentEditChain();
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.PAY_LOAD, payload);
            try {
                status = agentEditChain.execute(context);
            } catch (Exception e) {
                LOGGER.info("Exception occurred ", e);
            }
        }
        return status;
    }

    /**
     * This method inserts agent-log data into the log table, each time a message is sent to or received from an agent.
     *
     * @param payLoad Contains data needed for the log-table.
     * @param orgId   Tells which org's lod data to be fetched.
     * @param agentId If passed, fetches only that particular agent's log data.NOT MANDATORY.
     * @param sent
     */
    public static void putLog(JSONObject payLoad, Long orgId, Long agentId, boolean sent) {
        FacilioChain addLogChain = TransactionChainFactory.addLogChain();
        FacilioContext context = new FacilioContext();
        context.put(AgentKeys.ORG_ID, orgId);
        Map<String, Object> toUpdate = new HashMap<>();
        if (sent) {
            payLoad.put(AgentKeys.COMMAND_STATUS, CommandStatus.SENT.getKey());
            toUpdate.put(AgentKeys.COMMAND_STATUS, CommandStatus.SENT.getKey());
            LOGGER.info(" Sent message logged ");

        } else {
            if (payLoad.containsKey(AgentKeys.COMMAND_STATUS)) {
                toUpdate.put(AgentKeys.COMMAND_STATUS, Integer.parseInt(payLoad.get(AgentKeys.COMMAND_STATUS).toString()));
            }
        }
        if (payLoad.containsKey(AgentKeys.COMMAND)) {
            toUpdate.put(AgentKeys.COMMAND, ControllerCommand.valueOf(payLoad.get(AgentKeys.COMMAND).toString()).getValue());
        }
        toUpdate.put(AgentKeys.AGENT_ID, agentId);
        if (payLoad.containsKey(AgentKeys.TIMESTAMP)) {
            toUpdate.put(AgentKeys.TIMESTAMP, payLoad.get(AgentKeys.TIMESTAMP));
        } else {
            toUpdate.put(AgentKeys.TIMESTAMP, System.currentTimeMillis());
        }
        if (payLoad.containsKey(AgentKeys.DEVICE_ID)) {
            toUpdate.put(AgentKeys.DEVICE_ID, payLoad.get(AgentKeys.DEVICE_ID).toString());
        }
        if (payLoad.containsKey(AgentKeys.MESSAGE_ID)) {
            toUpdate.put(AgentKeys.MESSAGE_ID, payLoad.get(AgentKeys.MESSAGE_ID).toString());
        }
        if (payLoad.containsKey(AgentKeys.CONTENT)) {
            toUpdate.put(AgentKeys.CONTENT, payLoad.get(AgentKeys.CONTENT));
        }
        try {
            toUpdate.put(AgentKeys.CREATED_TIME, System.currentTimeMillis());
            context.put(FacilioConstants.ContextNames.TO_INSERT_MAP, toUpdate);
            addLogChain.execute(context);
               /* ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
                bean.addLog(toUpdate);*/
        } catch (Exception e) {
            LOGGER.info("Exception occured ", e);
        }
    }


    /**
     * This method fetches agent-log data of the entire organization or a particular agent.
     *
     * @param agentId If passed, only those ogs which belongs this particular agent is returned, NOT MANDATORY.
     * @return agent-log data from database
     * @throws Exception
     */
    public static List<Map<String, Object>> getAgentLog(Long agentId) throws Exception {
        GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder().table(AgentKeys.AGENT_LOG_TABLE)
                .select(FieldFactory.getAgentLogFields());
//                .andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getAgentLogModule()));
        if (agentId != null) {
            genericSelectRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(ModuleFactory.getAgentLogModule()), agentId.toString(), NumberOperators.EQUALS));
        }
        return genericSelectRecordBuilder.get();
    }


    // not used

    /**
     * This method fetches metrics data of a particular agent and for a particular Publish-type.
     * If agentId and publishType is absent results with just ORGID condition is returned.
     *
     * @param agentId     This parameter if passed fetches only those data which belongs to an agent,NOT MANDATORY.
     * @param publishType This parameter if passed fetches only those data which belongs to this publish-type,NOT MANDATORY.
     * @throws Exception
     */
    public static List<Map<String, Object>> getAgentMetrics(Long agentId, Integer publishType) throws Exception {
        FacilioModule agentMetriceModule = ModuleFactory.getAgentMetricsModule();
        GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder().table(AgentKeys.METRICS_TABLE)
                .select(FieldFactory.getAgentMetricsFields());
//                .andCondition(CriteriaAPI.getCurrentOrgIdCondition(ModuleFactory.getAgentMetricsModule()));
        if (agentId != null) {
            genericSelectRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(agentMetriceModule), agentId.toString(), NumberOperators.EQUALS));
        }
        if (publishType != null) {
            genericSelectRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getPublishTypeField(agentMetriceModule), publishType.toString(), NumberOperators.EQUALS));
        }
        return genericSelectRecordBuilder.get();
    }


    /**
     * This method adds metrics to the database, which holds data explaining size and number of messages that were sent by the agent per publishtype, per day.
     * If message already there for a particular Publishtype tha day, updation takes place else insertion.
     *
     * @param messageSize Size of an individual message.
     * @param agentId     If of the agent that sent this message.
     * @param publishType Publish-Type of the message.
     */
    public void addAgentMetrics(Integer messageSize, Long agentId, int publishType) {
        Long createdTime = DateTimeUtil.getDayStartTimeOf(System.currentTimeMillis());
        Long lastUpdatedTime = System.currentTimeMillis();
        ModuleCRUDBean bean;
        Map<String, Object> metrics = new HashMap<>();
        Map<String, Object> record;
        FacilioChain addAgentMetricsChain = TransactionChainFactory.getAddAgentMetricsChain();
        FacilioChain updateAgentMetricsChain = TransactionChainFactory.getUpdateAgentMetricsChain();
        FacilioContext context = new FacilioContext();
        context.put(AgentKeys.ORG_ID, orgId);
        try {
            bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            List<Map<String, Object>> records = bean.getMetrics(agentId, publishType, createdTime);
            if (!records.isEmpty()) {
                record = records.get(0);
                //LOGGER.info(" record selected is -> "+record);
                if ((!record.isEmpty()) && createdTime == Long.parseLong(record.get(AgentKeys.CREATED_TIME).toString())) {
                    // HashMap<String, Object> criteriax = new HashMap<>();
                    Criteria criteria = new Criteria();
                    Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getAgentMetricsFields());
                    criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentKeys.CREATED_TIME), String.valueOf(createdTime), NumberOperators.EQUALS));
                    criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentKeys.ID), String.valueOf(record.get(AgentKeys.ID)), NumberOperators.EQUALS));
                    criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(EventUtil.DATA_TYPE), String.valueOf(publishType), NumberOperators.EQUALS));
                    criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentKeys.AGENT_ID), String.valueOf(agentId), NumberOperators.EQUALS));

                   /* criteriax.put(AgentKeys.AGENT_ID, agentId);

                        criteriax.put(EventUtil.DATA_TYPE, publishType);
                        criteriax.put(AgentKeys.CREATED_TIME, createdTime);
                        criteriax.put(AgentKeys.ID, record.get(AgentKeys.ID));*/
                    //LOGGER.info(" updating id ->"+record.get(AgentKeys.ID));
                    metrics.put(AgentKeys.SIZE, Integer.parseInt(record.get(AgentKeys.SIZE).toString()) + messageSize);
                    metrics.put(AgentKeys.NO_OF_MESSAGES, Integer.parseInt(record.get(AgentKeys.NO_OF_MESSAGES).toString()) + 1);
                    metrics.put(AgentKeys.LAST_UPDATED_TIME, lastUpdatedTime);
                    context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP, metrics);
                    context.put(FacilioConstants.ContextNames.CRITERIA, criteria);
                    //LOGGER.info(" updating Agent Metrics");
                    updateAgentMetricsChain.execute(context);
                    //bean.updateAgentMetrics(metrics, criteria);
                }
            } else {
                metrics.put(AgentKeys.AGENT_ID, agentId);
                metrics.put(EventUtil.DATA_TYPE, publishType);
                metrics.put(AgentKeys.NO_OF_MESSAGES, 1);
                metrics.put(AgentKeys.SIZE, messageSize);
                metrics.put(AgentKeys.CREATED_TIME, createdTime);
                metrics.put(AgentKeys.LAST_UPDATED_TIME, lastUpdatedTime);
                context.put(FacilioConstants.ContextNames.TO_INSERT_MAP, metrics);
                addAgentMetricsChain.execute(context);
                //bean.insertAgentMetrics(metrics);
            }


        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
    }


    public static FacilioAgent getAgentDetails(long agentId) throws Exception {
        List<FacilioAgent> agents = getAgents(Collections.singletonList(agentId));
        if (CollectionUtils.isNotEmpty(agents)) {
            return agents.get(0);
        }
        return null;
    }

    public static List<FacilioAgent> getAgents(Collection<Long> agentIds) throws Exception {
        FacilioModule module = ModuleFactory.getAgentDataModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .select(FieldFactory.getAgentDataFields())
                .andCondition(CriteriaAPI.getIdCondition(agentIds, module));
        List<Map<String, Object>> props = builder.get();
        if (CollectionUtils.isNotEmpty(props)) {
            return FieldUtil.getAsBeanListFromMapList(props, FacilioAgent.class);
        }
        return null;
    }

    public static Map<Long, FacilioAgent> getAgentsMap(Collection<Long> agentIds) throws Exception {
        List<FacilioAgent> agents = getAgents(agentIds);
        if (CollectionUtils.isNotEmpty(agents)) {
            return agents.stream().collect(Collectors.toMap(FacilioAgent::getId, Function.identity()));
        }
        return null;
    }

    public String addVersionLog(long newVersionId, long agentId) throws Exception {
        try {
            Map<String, Object> row = new HashMap<>();
            row.put("orgId", this.orgId);
            row.put("versionId", newVersionId);
            row.put("agentId", agentId);
            String token = null;
            token = generateNewToken();
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getAgentVersionLogModule().getTableName())
                    .andCondition(CriteriaAPI.getCondition(FieldFactory.getAuthKeyField(ModuleFactory.getAgentVersionLogModule()), token, StringOperators.IS));
            List<Map<String, Object>> maps = selectRecordBuilder.get();
            if (maps.size() == 0) {
                row.put("authKey", token);
                GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
                        .table(ModuleFactory.getAgentVersionLogModule().getTableName());
                insertRecordBuilder.insert(row);

            }

            return token;
        } catch (Exception ex) {
            LOGGER.error("Exception while creating token");
            return null;
        }
    }

    public static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

}




