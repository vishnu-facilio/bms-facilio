package com.facilio.agentv2;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AgentUtilV2
{
    private long orgId ;
    private String orgDomainName;

    private Map<String, FacilioAgent> agentMap = new HashMap<>();
    public Map<String, FacilioAgent> getAgentMap() { return agentMap; }

    public int getAgentCount() {
        return agentMap.size();
    }

    private int agentCount;

    private static final Logger LOGGER = LogManager.getLogger(AgentUtilV2.class.getName());

    public AgentUtilV2(long orgId, String orgDomainName)  {
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        //populateAgentContextMap();
    }


    /**
     * This method populates the agents from database and maps them to their name
     */
   /* public void populateAgentContextMap() {
            List<FacilioAgent> agentList = AgentApiV2.getAgents();
            LOGGER.info(" getting all agents "+agentList);
            agentList.forEach(agent -> agentMap.put(agent.getName(),agent));
            LOGGER.info("\n-\n-\n- after populating new agents" + agentMap + "\n-\n-\n-");
    }*/

    /**
     * This method processes the {@link JSONObject} and creates or updates the agent in the database
     * @param jsonObject {@link JSONObject} containing all the necessary data to build or update an agent
     * @param agentName in case of updation, agent name.
     * @return {@link Long} 0 if failure
     */
    public long processAgent(JSONObject jsonObject,String agentName) {
        FacilioAgent agent = getFacilioAgent(agentName);
        if(agent==null) {
            try {
                agent = AgentApiV2.getFacilioAgentFromJson(jsonObject);
                LOGGER.info("adding agent " + agent.getName());
                return addAgent(agent);
            }catch (Exception e){
                LOGGER.info(" Exception occurred ");
            }
        }
        else  {
            if (updateAgent(agent, jsonObject)) {
                return 1;
            }
            return 0;
        }
        return 0;
    }

    private boolean updateAgent(FacilioAgent agent, JSONObject jsonObject){
        try {

                boolean isDone = AgentApiV2.editAgent(agent,jsonObject);
                if(isDone){
                    agentMap.replace(agent.getName(),agent);
                }
                return isDone;
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
        return false;
    }

    public FacilioAgent getFacilioAgent(String agentName) {
        FacilioAgent agent = agentMap.get(agentName);
        if(agent == null){
            agent = AgentApiV2.getAgent(agentName);
            if( agent != null ){
                agentMap.put(agentName,agent);
            }
        }
        return agent;
    }

    public long addAgent(FacilioAgent agent) {
        long agentId = AgentApiV2.addAgent(agent);
        if( agentId > 0 ){
            agentMap.put(agent.getName(),agent);
        }
        return agentId;
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

    public static com.facilio.agentv2.FacilioAgent makeNewFacilioAgent(String agentName) {
        LOGGER.info(" making new FacilioAgent for name "+agentName);
        com.facilio.agentv2.FacilioAgent agent = new com.facilio.agentv2.FacilioAgent();
        agent.setName(agentName);
        agent.setConnectionStatus(Boolean.TRUE);
        agent.setState(1);
        agent.setWritable(false);
        agent.setInterval(15L);
        agent.setWritable(false);
        return agent;
    }
}
