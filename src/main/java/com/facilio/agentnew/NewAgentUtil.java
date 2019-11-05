package com.facilio.agentnew;
import com.facilio.agent.AgentType;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewAgentUtil
{
    private long orgId ;
    private String orgDomainName;

    private Map<String, FacilioAgent> agentMap = new HashMap<>();
    public Map<String, FacilioAgent> getAgentMap() { return agentMap; }

    public int getAgentCount() {
        return agentMap.size();
    }

    private int agentCount;

    private static final Logger LOGGER = LogManager.getLogger(NewAgentUtil.class.getName());

    public NewAgentUtil(long orgId, String orgDomainName)  {
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        populateAgentContextMap();
    }

    private static StackTraceElement[] stackStrace(){
        return Thread.currentThread().getStackTrace();
    }

    /**
     * This method populates the agents from database and maps them to their name
     */
    public void populateAgentContextMap() {
            List<FacilioAgent> agentList = NewAgentAPI.getAgents();
            LOGGER.info(" getting all agents "+agentList);
            agentList.forEach(agent -> agentMap.put(agent.getName(),agent));
            LOGGER.info("\n-\n-\n- after populating new agents" + agentMap + "\n-\n-\n-");
    }

    /**
     * This method processes the {@link JSONObject} and creates or updates the agent in the database
     * @param jsonObject {@link JSONObject} containing all the necessary data to build or update an agent
     * @param agentName in case of updation, agent name.
     * @return {@link Long} 0 if failure
     */
    public long processAgent(JSONObject jsonObject,String agentName) {
        LOGGER.info(" processing agent " + agentName + " - - " + stackStrace());
        FacilioAgent agent = getFacilioAgent(agentName);
        if(agent==null) {
            try {
                agent = NewAgentAPI.getFacilioAgentFromJson(jsonObject);
                LOGGER.info("adding agent " + agent.getName());
                return addAgent(agent);
            }catch (Exception e){
                LOGGER.info(" Exception occurred ");
            }
        }
        else  {
            LOGGER.info(" updating new agent " + stackStrace());
            if (updateAgent(agent, jsonObject)) {
                LOGGER.info(" update success " + stackStrace());
                return 1;
            }
            return 0;
        }
        return 0;
    }

    private boolean updateAgent(FacilioAgent agent, JSONObject jsonObject){
        Condition agentNameCondition = new Condition();
        agentNameCondition.setField(FieldFactory.getAgentNameField(ModuleFactory.getAgentDataModule()));
        agentNameCondition.setOperator(StringOperators.IS);
        agentNameCondition.setValue(agent.getName());

        FacilioChain updateAgentChain = TransactionChainFactory.updateAgent();
        FacilioContext context = updateAgentChain.getContext();
        context.put(FacilioConstants.ContextNames.CRITERIA,agentNameCondition);

        try {
            Map<String, Object> toUpdate = new HashMap<>();
            if (containsValueCheck(AgentConstants.CONNECTION_STATUS,jsonObject)) {
                Boolean connectionStatus = Boolean.valueOf((jsonObject.get(AgentConstants.CONNECTION_STATUS).toString()));
                if (agent.getConnectionStatus() != connectionStatus) {
                    toUpdate.put(AgentConstants.CONNECTION_STATUS, connectionStatus);
                    agent.setConnectionStatus(connectionStatus);
                }
            } else {
                agent.setConnectionStatus(true);
                toUpdate.put(AgentConstants.CONNECTION_STATUS, true);
            }

            if (containsValueCheck(AgentConstants.DISPLAY_NAME,jsonObject)) {
                if (!jsonObject.get(AgentConstants.DISPLAY_NAME).toString().equals(agent.getDisplayName())) {
                    toUpdate.put(AgentConstants.DISPLAY_NAME, jsonObject.get(AgentConstants.DISPLAY_NAME));
                    agent.setDisplayName(jsonObject.get(AgentConstants.DISPLAY_NAME).toString());
                }
            }

            if (containsValueCheck(AgentConstants.STATE,jsonObject)) {
                Integer currStatus = Integer.parseInt(jsonObject.get(AgentConstants.STATE).toString());
                if (!agent.getState().equals(currStatus)) {
                    toUpdate.put(AgentConstants.STATE, currStatus);
                    agent.setState(currStatus);

                }
            } else {
                toUpdate.put(AgentConstants.STATE, 0);
                agent.setState(0);
            }

            if (containsValueCheck(AgentConstants.DATA_INTERVAL,jsonObject)) {
                Long currDataInterval = Long.parseLong(jsonObject.get(AgentConstants.DATA_INTERVAL).toString());
                if (agent.getInterval().longValue() != currDataInterval.longValue()) {
                    toUpdate.put(AgentConstants.DATA_INTERVAL, currDataInterval);
                    agent.setInterval(currDataInterval);
                }
            }
            if(containsValueCheck(AgentConstants.AGENT_TYPE,jsonObject)){
                agent.setType(jsonObject.get(AgentConstants.AGENT_TYPE).toString().trim());
                toUpdate.put(AgentConstants.AGENT_TYPE,agent.getType());
            }
         /*   if (containsValueCheck(AgentConstants.NUMBER_OF_CONTROLLERS,jsonObject)) {
                Integer currNumberOfControllers = Integer.parseInt(jsonObject.get(AgentConstants.NUMBER_OF_CONTROLLERS).toString());
                if ((agent.getNumberOfControllers().intValue() != currNumberOfControllers.intValue())) {
                    toUpdate.put(AgentConstants.NUMBER_OF_CONTROLLERS, currNumberOfControllers);
                    agent.setNumberOfControllers(currNumberOfControllers);
                }
            } else {
                toUpdate.put(AgentConstants.NUMBER_OF_CONTROLLERS, 0);
                agent.setNumberOfControllers(0);
            }*/

            /*if (containsValueCheck(AgentConstants.VERSION,jsonObject)) {
                Object currDeviceDetails = jsonObject.get(AgentConstants.VERSION);
                String currDeviceDetailsString = currDeviceDetails.toString();
                String currVersion = getVersion(currDeviceDetails);
                if ( (agent.getDeviceDetails() == null) || (agent.getDeviceDetails() != null && !(agent.getDeviceDetails().equalsIgnoreCase(currDeviceDetailsString))) ) {
                    toUpdate.put(AgentConstants.DEVICE_DETAILS, currDeviceDetailsString);
                    toUpdate.put(AgentConstants.VERSION, currVersion);
                    agent.setDeviceDetails(currDeviceDetailsString);
                    agent.setVersion(currVersion);
                }
            }*/
            if (containsValueCheck(AgentConstants.WRITABLE,jsonObject)) {
                Boolean currWriteble = Boolean.parseBoolean(jsonObject.get(AgentConstants.WRITABLE).toString());
                if (agent.getWritable() != currWriteble) {
                    toUpdate.put(AgentConstants.WRITABLE, currWriteble);
                    agent.setWritable(currWriteble);
                }
            }
            if (containsValueCheck(AgentConstants.DELETED_TIME,jsonObject)) {
                Long currDeletedTime = Long.parseLong(jsonObject.get(AgentConstants.DELETED_TIME).toString());
                if (agent.getDeletedTime().longValue() != currDeletedTime.longValue()) {
                    agent.setDeletedTime(currDeletedTime);
                    toUpdate.put(AgentConstants.DELETED_TIME, currDeletedTime);
                }
            }
            if(agent.getType() == null && containsValueCheck(AgentConstants.AGENT_TYPE,jsonObject)){
                toUpdate.put(AgentConstants.AGENT_TYPE, (AgentType.valueOf(jsonObject.get(AgentConstants.AGENT_TYPE).toString())).getKey());
            }
            if (!toUpdate.isEmpty()) {
                LOGGER.info("updating new agent and the toupdate map is " + toUpdate);
                toUpdate.put(AgentConstants.LAST_MODIFIED_TIME, System.currentTimeMillis());
                toUpdate.put(AgentConstants.LAST_DATA_RECEIVED_TIME, System.currentTimeMillis());
                context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP,toUpdate);
                return updateAgentChain.execute();
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
        return false;
    }

    public FacilioAgent getFacilioAgent(String agentName) {
        LOGGER.info("getting facilioAgent from new agentutil "+stackStrace());
        FacilioAgent agent = agentMap.get(agentName);
        if(agent == null){
            LOGGER.info("agent is null when getting  new agent " + stackStrace());
            agent = NewAgentAPI.getAgent(agentName);
            if( agent != null ){
                agentMap.put(agentName,agent);
            }
        }
        LOGGER.info(" final new agent return - - " + agent);
        return agent;
    }

    public long addAgent(FacilioAgent agent) {
        LOGGER.info(" adding new agent " + stackStrace());
        Chain chain = TransactionChainFactory.addNewAgent();
        FacilioContext context = new FacilioContext();
        context.put(AgentConstants.AGENT,agent);
        try {
            chain.execute(context);
            Long agentId = (Long) context.get(FacilioConstants.ContextNames.ID);
            if( (agentId != null) && (agentId > 0)){
                agent.setId(agentId);
                return agentId;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private String getVersion(Object payload) {
        JSONObject jsonObject2 = (JSONObject)payload;
        return jsonObject2.get(AgentConstants.AGENT_VERSION).toString();
    }

    private static boolean containsValueCheck(String key,JSONObject jsonObject){
        if(jsonObject.containsKey(key) && ( jsonObject.get(key) != null) ){
            return true;
        }
        return false;
    }
}
