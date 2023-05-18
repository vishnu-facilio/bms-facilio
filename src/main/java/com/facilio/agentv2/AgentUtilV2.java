package com.facilio.agentv2;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.alarms.AgentAlarmContext;
import com.facilio.agent.alarms.AgentEventContext;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agent.fw.constants.Status;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.metrics.MetricsApi;
import com.facilio.agentv2.point.PointEnum;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.BaseAlarmContext;
import com.facilio.bmsconsole.context.BaseEventContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.OrgInfoKeys;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.queue.source.KafkaMessageSource;
import com.facilio.queue.source.MessageSource;
import com.facilio.queue.source.MessageSourceUtil;
import com.facilio.services.messageQueue.MessageQueue;
import com.facilio.services.messageQueue.MessageQueueFactory;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.tasker.FacilioTimer;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.AckUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.wmsv2.endpoint.SessionManager;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.constants.Topics;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class AgentUtilV2
{
    private static final Logger LOGGER = LogManager.getLogger(AgentUtilV2.class.getName());
    private long orgId ;
    private String orgDomainName;

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
        AgentBean agentBean = getAgentBean();
        List<FacilioAgent> agentsFromDb = agentBean.getAgents(idsNotInMap);
        for (FacilioAgent agent :
                agentsFromDb) {
            idVsAgentMap.put(agent.getId(),agent);
            map.put(agent.getId(), agent);
        }
        return map;
    }

    public static JSONObject getOverview() throws Exception {
        JSONObject overiewData = new JSONObject();
        AgentBean agentBean = getAgentBean();
        JSONObject agentDetails = agentBean.getAgentCountDetails();
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
                        overiewData.put(AgentConstants.CONTROLLER,AgentConstants.getControllerBean().getControllerCountData(agentIds));
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
            AgentBean agentBean = getAgentBean();
            FacilioAgent agent = agentBean.getAgent(agentId);
            if(agent != null) {
                overiewData.putAll(FieldUtil.getAsJSON(agent));
                overiewData.put(AgentConstants.CONTROLLER, AgentConstants.getControllerBean().getControllerCountData(agentId));
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
                    case RESTART:
                        break;
                }
            }
            long timeStamp = (long) payload.get(AgentConstants.TIMESTAMP);
            if(payload.containsKey(AgentConstants.STATUS)){ // for LWT
                Status status = Status.valueOf(((Number) payload.get(AgentConstants.STATUS)).intValue());
                if(status == Status.CONNECTION_LOST || status == Status.DISCONNECTED){
                    LOGGER.info("Agent Connection Lost or Disconnected ");
                    agent.setConnected(false);
                    raiseAgentAlarm(agent);
                    createOrDeletePointsDataMissingAlarmJob(agent, false);
                } else if (status == Status.CONNECTED){
                    LOGGER.info("Agent Connected");
                    agent.setConnected(true);
                    dropAgentAlarm( agent);
                    createOrDeletePointsDataMissingAlarmJob(agent, true);
                } else {
                    LOGGER.info("Unknown status type - "+ status);
                }
                    //LogsApi.logAgentConnection(agent.getId(), status, connectionCount, timeStamp);
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

    private void createOrDeletePointsDataMissingAlarmJob(FacilioAgent agent, boolean createJob) throws Exception {
        if(createJob){
            if(FacilioTimer.getJob(agent.getId(), FacilioConstants.Job.POINTS_DATA_MISSING_ALARM_JOB_NAME) == null){
                LOGGER.info("Creating Points Data Missing Alarm Job for agent - "+ agent.getDisplayName());
                AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
                agentBean.schedulePointsDataMissingJob(agent);
            }
        } else {
            LOGGER.info("Deleting Points Data Missing Alarm Job for agent - "+ agent.getDisplayName());
            FacilioTimer.deleteJob(agent.getId(), FacilioConstants.Job.POINTS_DATA_MISSING_ALARM_JOB_NAME);
        }
    }

    public static boolean clearPointsDataMissingAlarm(FacilioAgent agent) {
        try {
            long pointsMissingCount = checkDataMissingIsFalse(agent);

            if (pointsMissingCount == 0) {
                LOGGER.info("Data arriving for all the commissioned points in agent " + agent.getDisplayName());
                long activeAlarms = getActiveAlarms(agent);
                if (activeAlarms > 0) {
                    AgentUtilV2.clearPointAlarm(agent);
                    return true;
                }
            }
            return true;
        } catch (Exception e) {
            LOGGER.error("Exception occurred while clearing PointsDataMissingAlarmJob " + agent.getDisplayName(), e);
        }
        return false;
    }

    private static long checkDataMissingIsFalse(FacilioAgent agent) throws Exception {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getPointFields());

        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getPointModule().getTableName())
                .select(new ArrayList<>())
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, fieldMap.get(AgentConstants.ID))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.DATA_MISSING), String.valueOf(1), StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_ID), String.valueOf(agent.getId()), StringOperators.IS));

        List<Map<String, Object>> rows = select.get();
        return (long) rows.get(0).get(AgentConstants.ID);
    }

    public static int getActiveAlarms(FacilioAgent agent) throws Exception {
        List<String> messageKeys = Collections.singletonList("AgentAlarm_" + AgentAlarmContext.AgentAlarmType.POINT.getIndex() + "_" + agent.getId());

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule baseAlarmModule = modBean.getModule(FacilioConstants.ContextNames.BASE_ALARM);

        Criteria severityCriteria = new Criteria();
        severityCriteria.addAndCondition(CriteriaAPI.getCondition("SEVERITY", "severity", FacilioConstants.Alarm.CLEAR_SEVERITY, StringOperators.ISN_T));

        LookupField severityField = new LookupField();
        severityField.setName("severity");
        severityField.setColumnName("SEVERITY_ID");
        severityField.setLookupModule(ModuleFactory.getAlarmSeverityModule());
        severityField.setModule(baseAlarmModule);
        severityField.setDataType(FieldType.LOOKUP);

        SelectRecordsBuilder<BaseAlarmContext> selectBuilder = new SelectRecordsBuilder<BaseAlarmContext>()
                .beanClass(BaseAlarmContext.class)
                .module(baseAlarmModule)
                .select(modBean.getAllFields(FacilioConstants.ContextNames.BASE_ALARM))
                .andCondition(CriteriaAPI.getCondition("ALARM_KEY", "key", StringUtils.join(messageKeys, ','), StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition(severityField, severityCriteria, LookupOperator.LOOKUP));

        List<BaseAlarmContext> activeAlarms = selectBuilder.get();

        return activeAlarms.size();

    }

    public static Collection<Long> getPointsDataMissing(FacilioAgent agent) throws Exception {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getPointFields());
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getPointModule().getTableName())
                .select(FieldFactory.getPointFields())
                .andCriteria(getPointsDataMissingCriteria(fieldMap, agent.getId(), agent));

        List<Map<String, Object>> rows = select.get();

        return rows.stream().map(row -> (Long) row.get(AgentConstants.ID)).collect(Collectors.toList());
    }

    private static Criteria getPointsDataMissingCriteria(Map<String, FacilioField> fieldMap, long jobId, FacilioAgent agent) {
        long currentTime = System.currentTimeMillis();

        String diffInMinutes = "(" + currentTime + "-" + fieldMap.get(AgentConstants.LAST_RECORDED_TIME).getCompleteColumnName() + ") / (" + 60 * 1000 + ")";
        FacilioField difference = FieldFactory.getField("difference", diffInMinutes, FieldType.NUMBER);

        String interval = "2 * COALESCE(" + fieldMap.get(AgentConstants.DATA_INTERVAL).getCompleteColumnName() + "," + agent.getInterval() + ")";

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_ID), String.valueOf(jobId), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.CONFIGURE_STATUS), String.valueOf(PointEnum.ConfigureStatus.CONFIGURED.getIndex()), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.FIELD_ID), CommonOperators.IS_NOT_EMPTY));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.RESOURCE_ID), CommonOperators.IS_NOT_EMPTY));
        criteria.addAndCondition(CriteriaAPI.getCondition(difference, interval, NumberOperators.GREATER_THAN));
        return criteria;
    }

    public static void togglePointsDataMissingAlarmJob(FacilioAgent agent) throws Exception {
        String jobName = FacilioConstants.Job.POINTS_DATA_MISSING_ALARM_JOB_NAME;
        boolean isActiveUpdateValue = agent.getConnected();
        long jobId = agent.getId();

        if(isActiveUpdateValue) {
            FacilioTimer.activateJob(jobId, jobName);
            LOGGER.info("Job Activated - Job Id : "+ jobId +", Job Name: " + jobName);
        }
        else {
            FacilioTimer.inActivateJob(jobId, jobName);
            LOGGER.info("Job InActivated - Job Id : "+ jobId +", Job Name: " + jobName);

        }
    }

    public static void dropAgentAlarm(FacilioAgent agent) throws Exception {
        long currentTime = System.currentTimeMillis();
        AgentEventContext event = getAgentEventContext(agent, currentTime, FacilioConstants.Alarm.CLEAR_SEVERITY);
        addEventToDB(event);
        LOGGER.info("Cleared Agent Alarm for Agent : " + agent.getDisplayName() + " ( ID :" + agent.getId()+ ")");

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
        LOGGER.info("Added Agent Alarm for Agent : " + agent.getDisplayName() + " ( ID :" + agent.getId() + ")");

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
        LOGGER.info("Cleared Controller Alarm for Agent : " + agent.getDisplayName() + " ( ID :" + agent.getId()+ ")");
    }

    public void raiseControllerAlarm(FacilioAgent agent,List<Controller> controllers) throws Exception {
        long currentTime = System.currentTimeMillis();
        AgentEventContext event = getControllerEventContext(agent, currentTime, FacilioConstants.Alarm.CRITICAL_SEVERITY,controllers);
        addEventToDB(event);
        LOGGER.info("Added controller Alarm for Agent : " + agent.getDisplayName() + " ( ID :" + agent.getId() + ")");

    }

    public static void raisePointAlarm(FacilioAgent agent, long points) throws Exception {
        long currentTime = System.currentTimeMillis();
        AgentEventContext event = getDataMissingEventContext(agent, currentTime, FacilioConstants.Alarm.CRITICAL_SEVERITY, points);
        addEventToDB(event);
        LOGGER.info("Added Point Alarm for Agent : " + agent.getDisplayName() + " ( ID :" + agent.getId() + ")");
    }

    public static void clearPointAlarm(FacilioAgent agent) throws Exception {
        long currentTime = System.currentTimeMillis();
        AgentEventContext event = getDataMissingEventContext(agent, currentTime, FacilioConstants.Alarm.CLEAR_SEVERITY, 0);
        addEventToDB(event);
        LOGGER.info("Cleared Point Alarm for Agent : " + agent.getDisplayName()+ " ( ID :" + agent.getId()+ ")");
    }

    private static AgentEventContext getDataMissingEventContext(FacilioAgent agent, long currentTime, String severity, long pointsDataMissingSize) {
        AgentEventContext event = new AgentEventContext();
        String description = null;
        String message = "Data missing in agent " + agent.getDisplayName();
        if (severity.equals(FacilioConstants.Alarm.CRITICAL_SEVERITY)) {
            description = "Data missing for "+ pointsDataMissingSize + " points in agent "+ agent.getDisplayName();
        } else if (severity.equals(FacilioConstants.Alarm.CLEAR_SEVERITY)) {
            description = "Data arriving for all points in agent "+ agent.getDisplayName();
        }
        event.setMessage(message);
        event.setDescription(description);
        event.setSeverityString(severity);
        event.setPointsDataMissingCount(pointsDataMissingSize);
        event.setCreatedTime(currentTime);
        event.setSiteId(AccountUtil.getCurrentSiteId());
        event.setAgent(agent);
        event.setAgentAlarmType(AgentAlarmContext.AgentAlarmType.POINT.getIndex());
        return event;
    }


    private AgentEventContext getControllerEventContext(FacilioAgent agent, long currentTime, String severity, List<Controller> controllers) {
        AgentEventContext event = new AgentEventContext();
        event.setMessage("Controllers are unresponsive in agent " + agent.getDisplayName());
        if (severity.equals(FacilioConstants.Alarm.CRITICAL_SEVERITY)){
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

    private static AgentEventContext getAgentEventContext(FacilioAgent agent, long currentTime, String severity) {
        AgentEventContext event = new AgentEventContext();
        String description = null;
        event.setMessage("Agent "+agent.getDisplayName() +" connection lost");
        if (severity.equals(FacilioConstants.Alarm.CRITICAL_SEVERITY)) {
            description = "Agent " + agent.getDisplayName()+ " has lost connection with the facilio cloud on " + DateTimeUtil.getFormattedTime(currentTime);
        } else if (severity.equals(FacilioConstants.Alarm.CLEAR_SEVERITY)) {
            description = "Agent " + agent.getDisplayName() + " has reestablished the connection with the facilio cloud on " + DateTimeUtil.getFormattedTime(currentTime);
        }
        event.setDescription(description);
        event.setSeverityString(severity);
        event.setCreatedTime(currentTime);
        event.setSiteId(AccountUtil.getCurrentSiteId());
        event.setAgent(agent);
        event.setAgentAlarmType(AgentAlarmContext.AgentAlarmType.AGENT.getIndex());
        return event;
    }

    public static void processCommandDelayAlarm(FacilioAgent agent, JSONObject data) throws Exception {
        long currentTime = System.currentTimeMillis();

        AgentEventContext event = new AgentEventContext();
        String description = null;
        String message = MessageFormat.format("Command sent to agent {0} is executing in delay", agent.getDisplayName()) ;
        String severity = (String) data.get("status");
        if (severity.equals(FacilioConstants.Alarm.CRITICAL_SEVERITY)) {
            description = MessageFormat.format("Command is executing in delay of {0} minutes", FacilioUtil.parseInt(data.get("delay").toString())) ;
        } else if (severity.equals(FacilioConstants.Alarm.CLEAR_SEVERITY)) {
            description = "Command execution is normal";
        }
        event.setMessage(message);
        event.setDescription(description);
        event.setSeverityString(severity);
        event.setCreatedTime(currentTime);
        event.setSiteId(AccountUtil.getCurrentSiteId());
        event.setAgent(agent);
        event.setAgentAlarmType(AgentAlarmContext.AgentAlarmType.COMMAND_DELAY.getIndex());

        addEventToDB(event);
        LOGGER.info("Command Delay Alarm for Agent : " + agent.getDisplayName() + " ( ID :" + agent.getId() + "), status - " + severity);

    }

    private boolean updateAgent(FacilioAgent agent, JSONObject jsonObject) throws Exception {
    	refreshAgent(agent);
        AgentBean agentBean = getAgentBean();
        return agentBean.editAgent(agent, jsonObject, true);
    }
    
    private void refreshAgent(FacilioAgent agent) throws Exception {
        AgentBean agentBean = getAgentBean();
        FacilioAgent agentFromDb = agentBean.getAgent(agent.getName());
        if (agentFromDb!=null) {
            agent.setProcessorVersion(agentFromDb.getProcessorVersion());
        }
}

    public FacilioAgent getFacilioAgent(String agentName) throws Exception {
        AgentBean agentBean = getAgentBean();
        FacilioAgent agent = agentBean.getAgent(agentName);
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

    public static int getAgentOfflineStatus( Map<String,Object> map) throws Exception {
        boolean connected = (boolean)map.get(AgentConstants.CONNECTED);
        return connected ? 0 : 1;
    }

    private static boolean isConfiguredPointExist(long agentId) throws Exception {
        AgentBean agentBean = getAgentBean();
        Set<Long> controllerIds = AgentConstants.getControllerBean().getControllerIds(Collections.singletonList(agentId));
        if(controllerIds.isEmpty()){
            return false;
        }
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(POINT_MODULE.getTableName())
                .select(POINT_FIELDS)
                .andCondition(agentBean.getDeletedTimeNullCondition(POINT_MODULE))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(POINT_MODULE), controllerIds, NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(POINT_MAP.get(AgentConstants.CONFIGURE_STATUS), CONFIGURED_STATUS, NumberOperators.EQUALS))
                .limit(1);
        Map<String,Object> map = builder.fetchFirst();
        return (map!= null && !map.isEmpty()) ;
    }
    
    public static MessageSource getMessageSource(long agentId) throws Exception {
    	FacilioAgent agent = null;
        AgentBean agentBean = getAgentBean();
        try {
    		agent = agentBean.getAgent(agentId);
		} catch (Exception e) {
			LOGGER.error("Error while fetching agent", e); 
		}
    	return getMessageSource(agent);
    }
    
    public static MessageSource getMessageSource(FacilioAgent agent){
    	String sourceName = null;
    	
    	if (agent != null && StringUtils.isNotEmpty(agent.getMessageSource())) {
    		sourceName = agent.getMessageSource();
    	}
    	else {
    		Map<String, String> orgInfo = null;
        	String orgInfoKey = OrgInfoKeys.MESSAGE_QUEUE_SOURCE; 
        	try { 
        		if(AccountUtil.getCurrentOrg() != null) { 
        			orgInfo = CommonCommandUtil.getOrgInfo(orgInfoKey); 
        			if (!MapUtils.isEmpty(orgInfo)) {
                		sourceName = orgInfo.get(orgInfoKey);
                	}
        		} 
        	} catch (Exception e) {
        		LOGGER.error("Error while fetching org info"); 
        	}
    	}
    	
    	if (sourceName != null) {
    		return MessageSourceUtil.getSource(sourceName);
    	}
    	
    	return MessageSourceUtil.getDefaultSource();
    }
    
   
    public static Object publishToQueue(String topic, String key, JSONObject payload, KafkaMessageSource source) {
		MessageQueue messageQueue = MessageQueueFactory.getMessageQueue(source);
    	try {
			return messageQueue.putRecord(topic, key, payload);
		} catch (Exception e) {
			LOGGER.info("Exception while put record ", e);
		}
    	return null;
    }

    public static AgentBean getAgentBean() throws Exception {
        AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
        return agentBean;
    }

    public void makeControllersActive(FacilioAgent agent) throws SQLException {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getControllersField());

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getNewControllerModule().getTableName())
                .fields(FieldFactory.getControllersField())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_ID), String.valueOf(agent.getId()), NumberOperators.EQUALS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.ACTIVE), String.valueOf(false), BooleanOperators.IS));
        Map<String, Object> toUpdate = new HashMap<>();
        toUpdate.put(AgentConstants.ACTIVE, true);
        updateBuilder.update(toUpdate);
    }

    public void makeControllersInActive(FacilioAgent agent, List<Long> controllerIds) throws SQLException {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getControllersField());

        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getNewControllerModule().getTableName())
                .fields(FieldFactory.getControllersField())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_ID), String.valueOf(agent.getId()), StringOperators.IS))
                .andCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.ID), controllerIds, StringOperators.IS));
        Map<String, Object> toUpdate = new HashMap<>();
        toUpdate.put(AgentConstants.ACTIVE, false);
        updateBuilder.update(toUpdate);
    }

    public void makeControllersActiveAndInactive(FacilioAgent agent, List<Long> controllerIds) throws SQLException {
        makeControllersActive(agent);
        makeControllersInActive(agent, controllerIds);
    }
    public static void scheduleMlBmsJob(long orgId) throws Exception{
        long interval = 120;

        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);

        long totalMinutesInADay = 60 * 24;
        LocalTime time = LocalTime.of(0, 0);
        for (long frequency = totalMinutesInADay / interval; frequency > 0; frequency--) {
            time = time.plusMinutes(interval);
            scheduleInfo.addTime(time);
        }
        FacilioTimer.scheduleCalendarJob(orgId, FacilioConstants.Job.ML_BMS_POINTS_TAGGING_JOB, System.currentTimeMillis(), scheduleInfo, "facilio");
    }

    public static boolean sendClearPointAlarm(FacilioAgent agent) {
        JSONObject content = new JSONObject();
        content.put(AgentConstants.AGENT, agent.getName());
        SessionManager.getInstance().sendMessage(new Message()
                .setTopic(Topics.Agent.agentPointAlarm)
                .setContent(content));
        return true;
    }

}
