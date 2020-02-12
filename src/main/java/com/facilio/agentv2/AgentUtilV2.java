package com.facilio.agentv2;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.point.PointsAPI;
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

    public static JSONObject getOverview() {
        JSONObject overiewData = new JSONObject();
        overiewData.put(AgentConstants.AGENT,AgentApiV2.getAgentCountDetails());
        try {
            overiewData.put(AgentConstants.CONTROLLER,ControllerApiV2.getControllerCountData(null));
        } catch (Exception e) {
            LOGGER.info("Exception while getting controllerCountdata",e);
        }
        try {
            overiewData.put(AgentConstants.POINTS,PointsAPI.getPointsCountData(-1L));
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting pointsCountData",e);
        }
        return overiewData;
    }

    public static JSONObject getAgentOverView(long agentId){
        JSONObject overiewData = new JSONObject();
        try {
            overiewData.put(AgentConstants.CONTROLLER,ControllerApiV2.getControllerCountData(agentId));
            overiewData.put(AgentConstants.POINTS,PointsAPI.getPointsCountData(agentId));
        } catch (Exception e) {
            LOGGER.info("Exception while getting controllerCountdata",e);
        }
        try {
            overiewData.put(AgentConstants.POINTS,PointsAPI.getPointsCountData(agentId));
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting pointsCountData",e);
        }
        return overiewData;
    }

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
     *
     * @param jsonObject {@link JSONObject} containing all the necessary data to build or update an agent
     * @param agent      {@link FacilioAgent} for comparing new changes
     * @return {@link Long} 0 if failure
     */
    public boolean processAgent(JSONObject jsonObject, FacilioAgent agent) throws Exception {
        if (agent != null) {
            return updateAgent(agent, jsonObject);
        } else {
            throw new Exception("Agent can't be null");
        }
    }

    private boolean updateAgent(FacilioAgent agent, JSONObject jsonObject) throws Exception {
        boolean isDone = AgentApiV2.editAgent(agent, jsonObject);
        if (isDone) {
            agentMap.replace(agent.getName(), agent);
        }
        return isDone;
    }

    public FacilioAgent getFacilioAgent(String agentName) throws Exception {
        FacilioAgent agent = agentMap.get(agentName);
        if (agent == null) {
            LOGGER.info(" creating new agent ");
            agent = AgentApiV2.getAgent(agentName);
            if (agent != null) {
                agentMap.put(agentName, agent);
            }
        }
        return agent;
    }

    public long addAgent(FacilioAgent agent) throws Exception {
        long agentId = AgentApiV2.addAgent(agent);
        if (agentId > 0) {
            agentMap.put(agent.getName(), agent);
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
