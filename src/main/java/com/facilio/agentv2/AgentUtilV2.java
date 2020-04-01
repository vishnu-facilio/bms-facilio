package com.facilio.agentv2;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.alarms.AgentEventContext;
import com.facilio.agent.alarms.ControllerEventContext;
import com.facilio.agent.fw.constants.Status;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.metrics.MetricsApi;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.AckUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;

public class AgentUtilV2
{
    private static final Logger LOGGER = LogManager.getLogger(AgentUtilV2.class.getName());
    private long orgId ;
    private String orgDomainName;

    private Map<String, FacilioAgent> agentMap = new HashMap<>();
    private Map<Long, FacilioAgent> idVsAgentMap = new HashMap<>();


    public Map<Long, FacilioAgent> getAgentsFromIds(List<Long> agentIds) throws Exception {
        Map<Long, FacilioAgent> map = new HashMap<>();
        List<Long> idsNotInMap = new ArrayList<>();
        for (Long id:agentIds){
            if (idVsAgentMap.containsKey(id)){
                map.put(id,idVsAgentMap.get(id));
            }else{
                idsNotInMap.add(id);
            }
        }
        List<FacilioAgent> agentsFromDb = AgentApiV2.getAgents(idsNotInMap);
        for (FacilioAgent agent :
                agentsFromDb) {
            idVsAgentMap.put(agent.getId(),agent);
            map.put(agent.getId(), agent);
        }
        return map;
    }

    public static JSONObject getOverview() {
        JSONObject overiewData = new JSONObject();
        overiewData.put(AgentConstants.AGENT,AgentApiV2.getAgentCountDetails());
        try {
            LOGGER.info(" controller data ");
            overiewData.put(AgentConstants.CONTROLLER,ControllerApiV2.getControllerCountData(null));
        } catch (Exception e) {
            LOGGER.info("Exception while getting controllerCountdata",e);
        }
        try {
            LOGGER.info(" point count data ");
            overiewData.put(AgentConstants.POINTS,PointsAPI.getPointsCountData(-1L));
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting pointsCountData",e);
        }
        overiewData.put("chartParams",MetricsApi.getMetricsGraphData(null));
        overiewData.put(AgentConstants.INTEGRATIONS,0);
        return overiewData;
    }

    public static JSONObject getAgentOverView(long agentId){
        JSONObject overiewData = new JSONObject();
        try {
            FacilioAgent agent = AgentApiV2.getAgent(agentId);
            if(agent != null) {
                overiewData.putAll(FieldUtil.getAsJSON(agent));
                overiewData.put(AgentConstants.CONTROLLER, ControllerApiV2.getControllerCountData(agentId));
                overiewData.put(AgentConstants.POINTS, PointsAPI.getPointsCountData(agentId));
            }
        } catch (Exception e) {
            LOGGER.info("Exception while getting controllerCountdata",e);
        }
        try {
            overiewData.put(AgentConstants.POINTS,PointsAPI.getPointsCountData(agentId));
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting pointsCountData",e);
        }
        overiewData.put("chartParams",MetricsApi.getMetricsGraphData(agentId));
        return overiewData;
    }

    public Map<String, FacilioAgent> getAgentMap() { return agentMap; }

    public int getAgentCount() {
        return agentMap.size();
    }
    private int agentCount;


    public AgentUtilV2(long orgId, String orgDomainName)  {
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        //populateAgentContextMap();
    }

    /**
     * This method processes the {@link JSONObject} and creates or updates the agent in the database
     *
     * @param payload {@link JSONObject} containing all the necessary data to build or update an agent
     * @param agent      {@link FacilioAgent} for comparing new changes
     * @return {@link Long} 0 if failure
     */
    public boolean processAgent(JSONObject payload, FacilioAgent agent) throws Exception {
        if (agent != null) {
            long timeStamp = (long) payload.get(AgentConstants.TIMESTAMP);
            if(payload.containsKey(AgentConstants.STATUS)){ // for LWT
                Status status = Status.valueOf(((Number) payload.get(AgentConstants.STATUS)).intValue());
                long connectionCount = 0;
                if(status == Status.CONNECTION_LOST || status == Status.DISCONNECTED){
                    LOGGER.info(" LWT -- "+payload);
                    agent.setConnected(false);
                    raiseAgentAlarm(agent);
                    raiseControllerAlarm(agent,"test-string");
                }else if (containsValueCheck(AgentConstants.COUNT,payload)){
                    LOGGER.info(" CONNECTED ");
                    agent.setConnected(true);
                    dropAgentAlarm( agent);
                    connectionCount = (long) payload.get(AgentConstants.COUNT);
                }else {
                    LOGGER.info("SUBSCRIBED");
                    agent.setConnected(true);
                    dropAgentAlarm( agent);
                }
                   // LogsApi.logAgentConnection(agent.getId(), status, connectionCount, timeStamp);
            }
            if(( ! payload.containsKey(AgentConstants.STATUS)) && (payload.containsKey(AgentConstants.MESSAGE_ID)) && (payload.containsKey(AgentConstants.COMMAND)) ){ // for PING
                AckUtil.ackPing(agent.getId(),orgId,payload);
                int status = Integer.parseInt(payload.get(AgentConstants.STATUS).toString());

            }
            agent.setConnected(true);
            return updateAgent(agent, payload);
        } else {
            throw new Exception("Agent can't be null");
        }
    }

    public static void dropAgentAlarm(FacilioAgent agent) throws Exception {
        long currentTime = System.currentTimeMillis();
        AgentEventContext event = getAgentEventContext(agent, currentTime, FacilioConstants.Alarm.CLEAR_SEVERITY);
        addEventToDB(event);
        LOGGER.info("Cleared Agent Alarm for Agent : " + agent.getName() + " ( ID :" + agent.getId()+ ")");

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
        LOGGER.info("Added Agent Alarm for Agent : " + agent.getName() + " ( ID :" + agent.getId() + ")");

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

    public void dropControllerAlarm(FacilioAgent agent) throws Exception {
        long currentTime = System.currentTimeMillis();
        ControllerEventContext event = getControllerEventContext(agent, currentTime, FacilioConstants.Alarm.CRITICAL_SEVERITY,null);
        addControllerEventToDB(event);
        LOGGER.info("Added controller Alarm for Agent : " + agent.getName() + " ( ID :" + agent.getId() + ")");

    }

    public void raiseControllerAlarm(FacilioAgent agent,String controllerList) throws Exception {
        long currentTime = System.currentTimeMillis();
        ControllerEventContext event = getControllerEventContext(agent, currentTime, FacilioConstants.Alarm.CRITICAL_SEVERITY,controllerList);
        addControllerEventToDB(event);
        LOGGER.info("Added controller Alarm for Agent : " + agent.getName() + " ( ID :" + agent.getId() + ")");

    }

    private ControllerEventContext getControllerEventContext(FacilioAgent agent, long currentTime, String severity, String controllerIdList) {
        ControllerEventContext event = new ControllerEventContext();
        event.setMessage("message");
        event.setDescription("description");
        //event.setComment("Disconnected time : " + DateTimeUtil.getFormattedTime(currentTime));
        event.setSeverityString(severity);
        event.setCreatedTime(currentTime);
        event.setSiteId(AccountUtil.getCurrentSiteId());
        event.setAgent(agent);
        event.setControllerList(controllerIdList);
        return event;
    }

    private void addControllerEventToDB(ControllerEventContext event) throws Exception {
        List<ControllerEventContext> eventList = new ArrayList<ControllerEventContext>();
        eventList.add(event);
        FacilioContext context = new FacilioContext();
        context.put(EventConstants.EventContextNames.EVENT_LIST, eventList);
        FacilioChain chain = TransactionChainFactory.getV2AddEventChain(false);
        chain.execute(context);
    }

    private static AgentEventContext getAgentEventContext(FacilioAgent agent, long currentTime, String severity) {
        AgentEventContext event = new AgentEventContext();
        String description = null;
        String message = null;
        if (severity.equals(FacilioConstants.Alarm.CRITICAL_SEVERITY)) {
            description = "Agent " + agent.getName() + " has lost connection with the facilio cloud @"+ DateTimeUtil.getFormattedTime(currentTime);
            message = "agent "+agent.getName() +" connection lost ";
        } else if (severity.equals(FacilioConstants.Alarm.CLEAR_SEVERITY)) {
            description = "Agent " + agent.getName() + " has lost connection with the facilio cloud @"+ DateTimeUtil.getFormattedTime(currentTime);
            message = "agent "+agent.getName() +" connection reestablished";
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

    private static boolean containsValueCheck(String key,JSONObject jsonObject){
        if(jsonObject.containsKey(key) && ( jsonObject.get(key) != null) ){
            return true;
        }
        return false;
    }

}
