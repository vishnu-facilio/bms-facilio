package com.facilio.agentv2;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.alarms.AgentAlarmContext;
import com.facilio.agent.alarms.AgentEventContext;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agent.fw.constants.Status;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.logs.LogsApi;
import com.facilio.agentv2.metrics.MetricsApi;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.AckUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class AgentUtilV2
{
    private static final Logger LOGGER = LogManager.getLogger(AgentUtilV2.class.getName());
    private long orgId ;
    private String orgDomainName;

    private Map<String, FacilioAgent> agentMap = new HashMap<>();
    private Map<Long, FacilioAgent> idVsAgentMap = new HashMap<>();
    private static final String CONFIGURED_STATUS = "3";
    private static final List<FacilioField> POINT_FIELDS = FieldFactory.getPointFields();
    private static final FacilioModule POINT_MODULE = ModuleFactory.getPointModule();
    private static final  Map<String, FacilioField> POINT_MAP = FieldFactory.getAsMap(POINT_FIELDS);
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
        JSONObject agentDetails = AgentApiV2.getAgentCountDetails();
        if(agentDetails == null ||agentDetails.isEmpty()) {
        	return new JSONObject();
        }
        overiewData.put(AgentConstants.AGENT,agentDetails);
        if (overiewData.containsKey(AgentConstants.AGENT)) {
            JSONObject agentCount = (JSONObject) overiewData.get(AgentConstants.AGENT);
            if(agentCount.containsKey(AgentConstants.RECORD_IDS)){
                List<Long> agentIds = (List<Long>) agentCount.get(AgentConstants.RECORD_IDS);
                LOGGER.info("agentIds "+agentIds);
                if( ! agentIds.isEmpty()){
                    try {
                        LOGGER.info(" controller data ");
                        overiewData.put(AgentConstants.CONTROLLER,ControllerApiV2.getControllerCountData(agentIds));
                    } catch (Exception e) {
                        LOGGER.info("Exception while getting controllerCountdata",e);
                    }
                    try {
                        LOGGER.info(" point count data ");
                        overiewData.put(AgentConstants.POINTS,PointsAPI.getPointsCountData(agentIds));
                    } catch (Exception e) {
                        LOGGER.info("Exception occurred while getting pointsCountData",e);
                    }
                }else {
                    LOGGER.info("zero agents");
                }
            }
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
            if (containsValueCheck(AgentConstants.COMMAND,payload)) {
                FacilioCommand command = FacilioCommand.valueOf(((Number)payload.get(AgentConstants.COMMAND)).intValue());
                switch (command){
                    case STATS:
                       // AgentStatsApi.processStats(agent,payload);
                        break;
                    case THREAD_DUMP:
                        AgentThreadDumpAPI.processThreadDump(payload,agent);
                        break;
                    case PING:
                        break;
                    case SHUTDOWN:
                        break;
                }
            }
            long timeStamp = (long) payload.get(AgentConstants.TIMESTAMP);
            if(payload.containsKey(AgentConstants.STATUS)){ // for LWT
                Status status = Status.valueOf(((Number) payload.get(AgentConstants.STATUS)).intValue());
                long connectionCount = 0;
                if(status == Status.CONNECTION_LOST || status == Status.DISCONNECTED){
                    LOGGER.info(" LWT -- "+payload);
                    agent.setConnected(false);
                    raiseAgentAlarm(agent);
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
                    LogsApi.logAgentConnection(agent.getId(), status, connectionCount, timeStamp);
            }
            if(( ! payload.containsKey(AgentConstants.STATUS)) && (payload.containsKey(AgentConstants.MESSAGE_ID)) && (payload.containsKey(AgentConstants.COMMAND)) ){ // for PING
                AckUtil.ackPing(agent.getId(),orgId,payload);
                agent.setConnected(true);
            }
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

    public void clearControllerAlarm(FacilioAgent agent) throws Exception {
        long currentTime = System.currentTimeMillis();
        AgentEventContext event = getControllerEventContext(agent, currentTime, FacilioConstants.Alarm.CLEAR_SEVERITY,null);
        addEventToDB(event);
        LOGGER.info("Cleared Controller Alarm for Agent : " + agent.getName() + " ( ID :" + agent.getId()+ ")");
    }

    public void raiseControllerAlarm(FacilioAgent agent,List<Controller> controllers) throws Exception {
        long currentTime = System.currentTimeMillis();
        AgentEventContext event = getControllerEventContext(agent, currentTime, FacilioConstants.Alarm.CRITICAL_SEVERITY,controllers);
        addEventToDB(event);
        LOGGER.info("Added controller Alarm for Agent : " + agent.getName() + " ( ID :" + agent.getId() + ")");

    }

    private AgentEventContext getControllerEventContext(FacilioAgent agent, long currentTime, String severity, List<Controller> controllers) {
        AgentEventContext event = new AgentEventContext();
        if (severity.equals(FacilioConstants.Alarm.CRITICAL_SEVERITY)){
            event.setMessage("Data missing for "+controllers.size()+" controllers in agent "+agent.getName());
            StringBuilder descBuilder = new StringBuilder();
            for (Controller c :
                    controllers) {
                descBuilder.append(c.getName()).append(",");
            }
            event.setDescription(descBuilder.toString());
            event.setComment("Disconnected time : " + DateTimeUtil.getFormattedTime(currentTime));
        }else{
            event.setMessage("Data arriving for all controllers in agent "+agent.getName());
        }
        event.setSeverityString(severity);
        event.setCreatedTime(currentTime);
        event.setSiteId(AccountUtil.getCurrentSiteId());
        event.setAgent(agent);
        event.setControllersList(controllers);
        event.setAgentAlarmType(AgentAlarmContext.AgentAlarmType.CONTROLLER.getIndex());
        return event;
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
        event.setAgentAlarmType(AgentAlarmContext.AgentAlarmType.AGENT.getIndex());
        return event;
    }


    private boolean updateAgent(FacilioAgent agent, JSONObject jsonObject) throws Exception {
    	refreshAgent(agent);
        boolean isDone = AgentApiV2.editAgent(agent, jsonObject, true);
        if (isDone) {
            agentMap.replace(agent.getName(), agent);
        }
        return isDone;
    }
    
    private void refreshAgent(FacilioAgent agent) throws Exception {

        FacilioAgent agentFromDb = AgentApiV2.getAgent(agent.getName());
        if (agentFromDb!=null) {
            agent.setProcessorVersion(agentFromDb.getProcessorVersion());
        }
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
    public static String generateRandomAPIKey(){
       return UUID.randomUUID().toString().replace("-","");
    }

    static int getAgentOfflineStatus( Map<String,Object> map) throws Exception {
        int offLineAgents =0;
        Long lastReceivedTime = (Long)map.get(AgentConstants.LAST_DATA_RECEIVED_TIME);
        boolean connected = (boolean)map.get(AgentConstants.CONNECTED);
        long agentId = (long)map.get("id");
        if(connected && lastReceivedTime == null){
            return offLineAgents;
        }
        if(!connected){
           return ++offLineAgents;
        }
        long diffInMins =  (long)Math.floor((System.currentTimeMillis() - lastReceivedTime)/1000/60 << 0);
        long interval = (long)map.getOrDefault(AgentConstants.DATA_INTERVAL,0L);
        if(diffInMins > (interval * 2) ){
            Integer agentType = (Integer)map.getOrDefault("agentType",null);
            if(agentType !=null &&  !isConfiguredPointExist(agentId)){
                return offLineAgents;
            }
            ++offLineAgents;
            map.put(AgentConstants.CONNECTED,false);
        }
        return offLineAgents;
    }

    private static boolean isConfiguredPointExist(long agentId) throws Exception {
        Set<Long> controllerIds = ControllerApiV2.getControllerIds(Collections.singletonList(agentId));
        if(controllerIds.isEmpty()){
            return false;
        }
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(POINT_MODULE.getTableName())
                .select(POINT_FIELDS)
                .andCondition(AgentApiV2.getDeletedTimeNullCondition(POINT_MODULE))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(POINT_MODULE), controllerIds, NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(POINT_MAP.get(AgentConstants.CONFIGURE_STATUS), CONFIGURED_STATUS, NumberOperators.EQUALS))
                .limit(1);
        Map<String,Object> map = builder.fetchFirst();
        return (map!= null && !map.isEmpty()) ;
    }
}
