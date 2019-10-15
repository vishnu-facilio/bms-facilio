package com.facilio.agentnew;
import com.facilio.agent.AgentKeys;
import com.facilio.agent.AgentType;
import com.facilio.agent.PublishType;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgentUtil
{
    private long orgId ;
    private String orgDomainName;
    private static final long DEFAULT_TIME = 10L;
    private Map<String, FacilioAgent> agentMap = new HashMap<>();
    public Map<String, FacilioAgent> getAgentMap() { return agentMap; }

    public int getAgentCount() {
        return agentMap.size();
    }

    private int agentCount;

    private static final Logger LOGGER = LogManager.getLogger(com.facilio.agent.AgentUtil.class.getName());

    public AgentUtil(long orgId, String orgDomainName)  {
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
    }

    private static StackTraceElement stackStrace(){
        return Thread.currentThread().getStackTrace()[2];
    }

    /**
     * This method populates the agents from database and maps them to their name
     * @param agentName String agent's name to load the particular agent and null to load all agents
     */
    public void populateAgentContextMap(String agentName) {
        LOGGER.info(" populating agent context before try " + agentName + " " + stackStrace());
        try {
            LOGGER.info(" populating agent context " + agentName + " " + stackStrace());
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            List<Map<String, Object>> records = bean.getAgentDataMap(agentName,null);
            for (Map<String,Object> record :records) {
                JSONObject payload = new JSONObject();
                payload.putAll(record);
                FacilioAgent agent = getFacilioAgentFromJson(payload);
                agentMap.put(agent.getName(), agent);
            }
            LOGGER.info("\n-\n-\n- after populating " + agentMap + "\n-\n-\n-");
        } catch (Exception e1) {
            LOGGER.info("exception ",e1);
        }
    }

    /**
     * This method builds a new FacilioAgent from the {@link JSONObject}-payload
     * @param payload {@link JSONObject} payload containing all data to build the agent
     * @return
     */
    private FacilioAgent getFacilioAgentFromJson(JSONObject payload){
        LOGGER.info(" getting facilio agent from JSON \n" + payload + "\n" + stackStrace());
        FacilioAgent agent = new FacilioAgent();
        if(containsValueCheck(AgentKeys.STATE,payload)) {
            agent.setState(Integer.parseInt(payload.get(AgentKeys.STATE).toString()));
        } else{
            agent.setState(0);
        }
        if(containsValueCheck(AgentKeys.ID,payload)){
            agent.setId(Long.parseLong(payload.get(AgentKeys.ID).toString()));
        }
        if(containsValueCheck(AgentKeys.SITE_ID,payload)){
            agent.setSiteId(Long.parseLong(payload.get(AgentKeys.SITE_ID).toString()));
        }
        if(containsValueCheck(AgentKeys.VERSION,payload)) {
            agent.setDeviceDetails(payload.get(AgentKeys.VERSION).toString());

            if(containsValueCheck(AgentKeys.FACILIO_MQTT_VERSION,payload)) {
                agent.setVersion(payload.get(AgentKeys.FACILIO_MQTT_VERSION).toString());
            }
        }

        if(containsValueCheck(AgentKeys.CONNECTION_STATUS,payload)) {
            agent.setConnectionStatus(Boolean.valueOf(payload.get(AgentKeys.CONNECTION_STATUS).toString()));
        } else {
            agent.setConnectionStatus(true);
        }

        if(containsValueCheck(AgentKeys.DATA_INTERVAL,payload)) {
            agent.setInterval(Long.parseLong( payload.get(AgentKeys.DATA_INTERVAL).toString()));
        } else {
            agent.setInterval(DEFAULT_TIME);
        }

        if(containsValueCheck(AgentKeys.NUMBER_OF_CONTROLLERS,payload)){
            agent.setNumberOfControllers(Integer.parseInt(payload.get(AgentKeys.NUMBER_OF_CONTROLLERS).toString()));
        }

        if(containsValueCheck(AgentKeys.NAME,payload)){
            agent.setName(payload.get(AgentKeys.NAME).toString().trim());
        }
        else if(containsValueCheck(PublishType.agent.getValue(),payload)){
            agent.setName(payload.get(PublishType.agent.getValue()).toString().trim());
        }
        else{
            agent.setName(orgDomainName);
        }
        LOGGER.info("agent name form key agent " + agent.getName() + " - - - - - - - " + payload);
        if(containsValueCheck(AgentKeys.AGENT_TYPE,payload)){
            agent.setType(payload.get(AgentKeys.AGENT_TYPE).toString());
        }
        if(containsValueCheck(AgentKeys.DELETED_TIME,payload)){
            agent.setDeletedTime(Long.parseLong(payload.get(AgentKeys.DELETED_TIME).toString()));
        }
        if(containsValueCheck(AgentKeys.WRITABLE,payload)){
            agent.setWritable(Boolean.parseBoolean(payload.get(AgentKeys.WRITABLE).toString()));
        }else {
            agent.setWritable(false);
        }
        LOGGER.info(" agent from getfagentfromJSON \n" + agent.toJSON() + "\n" + stackStrace());
    return agent;

    }

    /**
     * This method processes the {@link JSONObject} and creates or updates the agent in the database
     * @param jsonObject {@link JSONObject} containing all the necessary data to build or update an agent
     * @param agentName in case of updation, agent name.
     * @return {@link Long} 0 if failure
     */
    public long processAgent(JSONObject jsonObject,String agentName) {
        //AgentType type = AgentType.valueOf(Math.toIntExact((Long) jsonObject.get(AgentKeys.AGENT_TYPE)));
        LOGGER.info(" processing agent " + agentName + " - - " + stackStrace());
        FacilioAgent agent = getFacilioAgent(agentName);
        if(agent==null) {
            agent = getFacilioAgentFromJson(jsonObject);
            return addAgent(agent);
        }
        else  {
            LOGGER.info(" updating agent " + stackStrace());
            if (updateAgent(agent, jsonObject)) {
                LOGGER.info(" update success " + stackStrace());
                return 1;
            }
            return 0;
        }
    }

    private boolean updateAgent(FacilioAgent agent, JSONObject jsonObject){
        Condition agentNameCondition = new Condition();
        agentNameCondition.setField(FieldFactory.getAgentNameField(ModuleFactory.getAgentDataModule()));
        agentNameCondition.setOperator(StringOperators.IS);
        agentNameCondition.setValue(agent.getName());
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.CRITERIA,agentNameCondition);

        Chain updateAgentChain = TransactionChainFactory.updateAgent();

        try {
            Map<String, Object> toUpdate = new HashMap<>();
            if (containsValueCheck(AgentKeys.COMMAND_STATUS,jsonObject)) {
                Boolean connectionStatus = Boolean.valueOf((jsonObject.get(AgentKeys.CONNECTION_STATUS).toString()));
                if (agent.getConnectionStatus() != connectionStatus) {
                    toUpdate.put(AgentKeys.CONNECTION_STATUS, connectionStatus);
                    agent.setConnectionStatus(connectionStatus);
                }
            } else {
                toUpdate.put(AgentKeys.CONNECTION_STATUS, true);
                agent.setConnectionStatus(true);
            }

            if (containsValueCheck(AgentKeys.DISPLAY_NAME,jsonObject)) {
                if (!jsonObject.get(AgentKeys.DISPLAY_NAME).toString().equals(agent.getDisplayName())) {
                    toUpdate.put(AgentKeys.DISPLAY_NAME, jsonObject.get(AgentKeys.DISPLAY_NAME));
                    agent.setDisplayName(jsonObject.get(AgentKeys.DISPLAY_NAME).toString());
                }
            }

            if (containsValueCheck(AgentKeys.STATE,jsonObject)) {
                Integer currStatus = Integer.parseInt(jsonObject.get(AgentKeys.STATE).toString());
                if (!agent.getState().equals(currStatus)) {
                    toUpdate.put(AgentKeys.STATE, currStatus);
                    agent.setState(currStatus);

                }
            } else {
                toUpdate.put(AgentKeys.STATE, 0);
                agent.setState(0);
            }

            if (containsValueCheck(AgentKeys.DATA_INTERVAL,jsonObject)) {
                Long currDataInterval = Long.parseLong(jsonObject.get(AgentKeys.DATA_INTERVAL).toString());
                if (agent.getInterval().longValue() != currDataInterval.longValue()) {
                    toUpdate.put(AgentKeys.DATA_INTERVAL, currDataInterval);
                    agent.setInterval(currDataInterval);
                }
            }
            if(containsValueCheck(AgentKeys.AGENT_TYPE,jsonObject)){
                agent.setType(jsonObject.get(AgentKeys.AGENT_TYPE).toString().trim());
                toUpdate.put(AgentKeys.AGENT_TYPE,agent.getType());
            }
            if (containsValueCheck(AgentKeys.NUMBER_OF_CONTROLLERS,jsonObject)) {
                Integer currNumberOfControllers = Integer.parseInt(jsonObject.get(AgentKeys.NUMBER_OF_CONTROLLERS).toString());
                if ((agent.getNumberOfControllers().intValue() != currNumberOfControllers.intValue())) {
                    toUpdate.put(AgentKeys.NUMBER_OF_CONTROLLERS, currNumberOfControllers);
                    agent.setNumberOfControllers(currNumberOfControllers);
                }
            } else {
                toUpdate.put(AgentKeys.NUMBER_OF_CONTROLLERS, 0);
                agent.setNumberOfControllers(0);
            }

            if (containsValueCheck(AgentKeys.VERSION,jsonObject)) {
                Object currDeviceDetails = jsonObject.get(AgentKeys.VERSION);
                String currDeviceDetailsString = currDeviceDetails.toString();
                String currVersion = getVersion(currDeviceDetails);
                if ( (agent.getDeviceDetails() == null) || (agent.getDeviceDetails() != null && !(agent.getDeviceDetails().equalsIgnoreCase(currDeviceDetailsString))) ) {
                    toUpdate.put(AgentKeys.DEVICE_DETAILS, currDeviceDetailsString);
                    toUpdate.put(AgentKeys.VERSION, currVersion);
                    agent.setDeviceDetails(currDeviceDetailsString);
                    agent.setVersion(currVersion);
                }
            }
            if (containsValueCheck(AgentKeys.WRITABLE,jsonObject)) {
                Boolean currWriteble = Boolean.parseBoolean(jsonObject.get(AgentKeys.WRITABLE).toString());
                if (agent.getWritable() != currWriteble) {
                    toUpdate.put(AgentKeys.WRITABLE, currWriteble);
                    agent.setWritable(currWriteble);
                }
            }
            if (containsValueCheck(AgentKeys.DELETED_TIME,jsonObject)) {
                Long currDeletedTime = Long.parseLong(jsonObject.get(AgentKeys.DELETED_TIME).toString());
                if (agent.getDeletedTime().longValue() != currDeletedTime.longValue()) {
                    agent.setDeletedTime(currDeletedTime);
                    toUpdate.put(AgentKeys.DELETED_TIME, currDeletedTime);
                }
            }
            if(agent.getType() == null && containsValueCheck(AgentKeys.AGENT_TYPE,jsonObject)){
                toUpdate.put(AgentKeys.AGENT_TYPE, (AgentType.valueOf(jsonObject.get(AgentKeys.AGENT_TYPE).toString())).getKey());
            }
            if (!toUpdate.isEmpty()) {
                LOGGER.info("updating agent and the toupdate map is " + toUpdate);
                toUpdate.put(AgentKeys.LAST_MODIFIED_TIME, System.currentTimeMillis());
                toUpdate.put(AgentKeys.LAST_DATA_RECEIVED_TIME, System.currentTimeMillis());
                context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP,toUpdate);
                return updateAgentChain.execute(context);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
        return false;
    }

    public FacilioAgent getFacilioAgent(String agentName) {
        LOGGER.info("getting facilioAgent from agentutil "+stackStrace());
        FacilioAgent agent = agentMap.get(agentName);
        if(agent == null){
            LOGGER.info("agent is null when gettingfacilioagent " + stackStrace());
            populateAgentContextMap(agentName);
            agent =agentMap.get(agentName);
        }
        LOGGER.info(" final agent return - - " + agent);
        return agent;
    }

    private long addAgent(FacilioAgent agent) {
        LOGGER.info(" adding agent " + stackStrace());
        Chain chain = TransactionChainFactory.getAddAgentChain();
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.PAY_LOAD,agent.toJSON());
        try {
            chain.execute(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FacilioModule agentDataModule = ModuleFactory.getAgentDataModule();
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder().table(AgentKeys.AGENT_TABLE).select(FieldFactory.getAgentDataFields())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentNameField(agentDataModule),agent.getName(),StringOperators.IS));
        long id = 0L;
        try {
            List<Map<String,Object>> row = selectRecordBuilder.get();
            if(row.size() == 1 && row.get(0).containsKey(AgentKeys.ID)){
                id = Long.parseLong(row.get(0).get(AgentKeys.ID).toString());
            }else {
                LOGGER.info("Exception multiple entries with same AgentId ");
            }
        } catch (Exception e) {
            LOGGER.info("Exception while fetching agent detail for agentId ",e);
        }

        try {
            agent.setId(id);
            if(id > 0)  {
                agentMap.put(agent.getName(), agent);
            }
            LOGGER.info(" agent id after ading and returning "+id);
            return id;

        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
        return 0L;
    }

    private String getVersion(Object payload) {
        JSONObject jsonObject2 = (JSONObject)payload;
        return jsonObject2.get(AgentKeys.FACILIO_MQTT_VERSION).toString();
    }

    private boolean containsValueCheck(String key,JSONObject jsonObject){
        if(jsonObject.containsKey(key) && ( jsonObject.get(key) != null) ){
            return true;
        }
        return false;
    }
}
