package com.facilio.agentv2;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.facilio.chain.FacilioChain;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.chain.Chain;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.agent.AgentKeys;
import com.facilio.agent.AgentType;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.Job;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.tasker.ScheduleInfo;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

public class AgentApiV2 {

    private static final Logger LOGGER = LogManager.getLogger(AgentApiV2.class.getName());

    public static List<FacilioAgent> getAgents(Collection<Long> ids) throws Exception {
        FacilioModule newAgentModule = ModuleFactory.getNewAgentModule();
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getIdCondition(ids,newAgentModule));
        criteria.addAndCondition(getDeletedTimeNullCondition(newAgentModule));
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.CRITERIA,criteria);
        return getAgents(context);
    }

    public static FacilioAgent getAgent(Long agentId) throws Exception {
        if ((agentId != null) && (agentId > 0)) {
            FacilioModule newAgentModule = ModuleFactory.getNewAgentModule();
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getIdCondition(agentId, newAgentModule));
            criteria.addAndCondition(getDeletedTimeNullCondition(newAgentModule));
            FacilioContext context = new FacilioContext();
            context.put(FacilioConstants.ContextNames.CRITERIA, criteria);
            List<FacilioAgent> agents = getAgents(context);
            if (!agents.isEmpty()) {
                return agents.get(0);
            }else {
                return null;
            }
        } else {
            throw new Exception("agentId cane be null or less than 1 "+agentId);
        }
    }

    public static long addAgent(FacilioAgent agent) throws Exception {
        Chain chain = TransactionChainFactory.addNewAgent();
        FacilioContext context = new FacilioContext();
        agent.setCreatedTime(System.currentTimeMillis());
        agent.setLastDataReceivedTime(-1);
        if (agent.getAgentType() == AgentType.CLOUD.getKey()) {
            agent.setConnected(true);
        }
        agent.setLastModifiedTime(agent.getCreatedTime());
        context.put(AgentConstants.AGENT, agent);
        chain.execute(context);
        if(context.containsKey(AgentConstants.AGENT_ID)){

            Long agentId = (Long) context.get(AgentConstants.AGENT_ID);
            if ((agentId != null) && (agentId > 0)) {
                agent.setId(agentId);
                return agentId;
            } else {
                LOGGER.info("Failed adding agent " + agent.getName());
                throw new Exception(" Agent added but agentId can't be obtained -> "+agentId);
            }
        }else {
            throw new Exception(" Agent added but context is missing agentId ");
        }
    }


    public static boolean editAgent(FacilioAgent agent, JSONObject jsonObject, boolean updateLastDataReceivedTime) throws Exception {
        Long currTime = System.currentTimeMillis() ;
        if(jsonObject.containsKey(AgentConstants.TIMESTAMP)){
            currTime = (Long) jsonObject.get(AgentConstants.TIMESTAMP);
        }
        if (updateLastDataReceivedTime) {
            agent.setLastDataReceivedTime(currTime);
        }
        if (containsValueCheck(AgentConstants.DISPLAY_NAME, jsonObject)) {
            if (!jsonObject.get(AgentConstants.DISPLAY_NAME).toString().equals(agent.getDisplayName())) {
                agent.setDisplayName(jsonObject.get(AgentConstants.DISPLAY_NAME).toString());
                agent.setLastModifiedTime(currTime);
            }
        }

        if (containsValueCheck(AgentConstants.DATA_INTERVAL, jsonObject)) {
            long currDataInterval = Long.parseLong(jsonObject.get(AgentConstants.DATA_INTERVAL).toString());
            if (agent.getInterval() != currDataInterval) {
                agent.setInterval(currDataInterval);
                agent.setLastModifiedTime(currTime);
                if(agent.getAgentType() == AgentType.CLOUD.getKey()) {
                		FacilioTimer.deleteJob(agent.getId(), Job.CLOUD_AGENT_JOB_NAME);
                		scheduleRestJob(agent);
                }
            }
        }

        if (containsValueCheck(AgentConstants.WRITABLE, jsonObject)) {
            boolean currWriteble = Boolean.parseBoolean(jsonObject.get(AgentConstants.WRITABLE).toString());
            if (agent.getWritable() != currWriteble) {
                agent.setWritable(currWriteble);
                agent.setLastModifiedTime(currTime);
               List<Long> controllerIds =  ControllerApiV2.getControllersUsingAgentId(agent.getId());
               controllerIds.forEach(controllerId -> {
            	   try {
					ControllerApiV2.editController(controllerId, jsonObject);
				} catch (Exception e) {
					LOGGER.error("Exception occurred while Controller writable updating....");
				}
               });
            }
        }
        
        long oldWorkflowId = -1;
        if (containsValueCheck(AgentConstants.WORKFLOW, jsonObject)) {
            WorkflowContext workflow = FieldUtil.getAsBeanFromMap((Map<String, Object>)jsonObject.get(AgentConstants.WORKFLOW), WorkflowContext.class);
            oldWorkflowId = agent.getWorkflowId();
            LOGGER.info("workflow : " + workflow.getWorkflowV2String());
            LOGGER.info("old workflow Id:" + oldWorkflowId);
            if(workflow.validateWorkflow()) {
                LOGGER.info("Workflow validation succeeded");
                long workflowId = WorkflowUtil.addWorkflow(workflow);
                LOGGER.info("new Workflow ID :" + workflowId);
                agent.setWorkflowId(workflowId);
            }
			else {
                LOGGER.info("Workflow validation failed");
				throw new IllegalArgumentException(agent.getWorkflow().getErrorListener().getErrorsAsString());
			}
            
        }
        boolean status = updateAgent(agent);
        LOGGER.info("Update status : " + status);
        if (oldWorkflowId != -1) {
        		WorkflowUtil.deleteWorkflow(oldWorkflowId);
        }
        
        return status;
    }


    public static boolean updateAgent(FacilioAgent agent) throws Exception {
        FacilioModule MODULE = ModuleFactory.getNewAgentModule();
        long currTime = System.currentTimeMillis();
        agent.setLastUpdatedTime(currTime);
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(MODULE.getTableName())
                .fields(FieldFactory.getNewAgentFields())
                .andCondition(CriteriaAPI.getIdCondition(agent.getId(), MODULE));
        updateRecordBuilder.update(FieldUtil.getAsJSON(agent));
        return true;
    }

    public static boolean updateAgentLastDataRevievedTime(FacilioAgent agent){
        FacilioModule agentDataModule = ModuleFactory.getNewAgentModule();
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(agentDataModule.getTableName())
                .fields(FieldFactory.getNewAgentFields())
                .andCondition(CriteriaAPI.getIdCondition(agent.getId(), agentDataModule));
        Map<String, Object> toUpdate = new HashMap<>();
        toUpdate.put(AgentConstants.LAST_DATA_RECEIVED_TIME,agent.getLastDataReceivedTime());
        try {
            if (updateRecordBuilder.update(toUpdate) > 0) {
                return true;
            } else {
                LOGGER.info("Exception occurred while updating agent, updating failed");
            }
        } catch (SQLException e) {
            LOGGER.info("Eception occurred while updating agent last data received time");
        }
        return false;
    }



    public static Condition getDeletedTimeNullCondition(FacilioModule module){
        return CriteriaAPI.getCondition(FieldFactory.getDeletedTimeField(module), "NULL", CommonOperators.IS_EMPTY);
    }

    static JSONObject getAgentCountDetails() {
        Map<String, FacilioField> fieldsmap = FieldFactory.getAsMap(FieldFactory.getNewAgentFields());
        FacilioModule agentDataModule = ModuleFactory.getNewAgentModule();
        try {
            List<FacilioField> fields = new ArrayList<>();
            fields.add(fieldsmap.get(AgentConstants.CONNECTED));
            fields.add(FieldFactory.getIdField(agentDataModule));
            fields.add(FieldFactory.getSiteIdField(agentDataModule));
            fields.add(FieldFactory.getAgentDataIntervalField(agentDataModule));
            fields.add(FieldFactory.getAgentLastDataReveivedField(agentDataModule));
            List<Long> ids = new ArrayList<>();
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .table(agentDataModule.getTableName())
                    .select(fields)
                    .andCondition(getDeletedTimeNullCondition(agentDataModule));
            List<Map<String, Object>> data = selectRecordBuilder.get();
            Set<Long> siteSet = new HashSet<>();
            int offlineCount = 0;
            if (!data.isEmpty()) {
                for (Map<String, Object> datum : data) {
                    ids.add((Long) datum.get(AgentConstants.ID));
                   offlineCount+= AgentUtilV2.getAgentOfflineStatus(datum);
                    if((datum.get(AgentConstants.SITE_ID) != null) && (((Number)datum.get(AgentConstants.SITE_ID)).longValue()>0) ){
                        siteSet.add((Long) datum.get(AgentConstants.SITE_ID));
                    }
                }
            }else {
            	return new JSONObject();
            }
            JSONObject countData = new JSONObject();
            countData.put(AgentConstants.RECORD_IDS,ids);
            countData.put(AgentConstants.SITE_COUNT,siteSet.size());
            countData.put(AgentConstants.TOTAL_COUNT,data.size());
            countData.put(AgentConstants.ACTIVE_COUNT,(data.size()-offlineCount));
            return countData;
        } catch (Exception e) {
            LOGGER.info("Exception while getting agent count data ",e);
        }
        return new JSONObject();
    }

    static long getAgentCount() {
        Map<String, FacilioField> fieldsmap = FieldFactory.getAsMap(FieldFactory.getNewAgentFields());
        FacilioModule agentDataModule = ModuleFactory.getNewAgentModule();
        try {
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .select(new HashSet<>())
                    .table(agentDataModule.getTableName())
                    .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, fieldsmap.get(AgentConstants.ID))
                    .andCondition(getDeletedTimeNullCondition(agentDataModule));
            List<Map<String, Object>> result = builder.get();
            return (long) result.get(0).get(AgentConstants.ID);
        } catch (Exception e) {
            LOGGER.info("Exception while getting agent count ",e);
        }
        return 0;
    }

    public static boolean deleteAgent(Long agentId) {
        if(agentId > 0){
            deleteAgent(Collections.singletonList(agentId));
        }else {
            LOGGER.info("Exception while deleting agent, AgentId can't be less than 1");
        }
        return false;
    }

    public static boolean deleteAgent(List<Long> ids) {
        try {
            FacilioChain facilioChain = TransactionChainFactory.deleteAgentV2();
            FacilioContext context = new FacilioContext();
            context.put(AgentConstants.RECORD_IDS, ids);
            facilioChain.setContext(context);
            facilioChain.execute();
        }catch (Exception e){
            LOGGER.info("Exception while deleting agent ->"+ids+"--",e);
        }
        return false;
    }

    public static List<Map<String, Object>> getAgentListData(boolean fetchDeleted) throws Exception {
        FacilioModule agentDataModule = ModuleFactory.getNewAgentModule();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getNewAgentFields());
        FacilioModule controllerModule = ModuleFactory.getNewControllerModule();
        if( ! fieldMap.isEmpty()){
            if(fieldMap.containsKey(AgentConstants.DELETED_TIME)){
                fieldMap.remove(AgentConstants.DELETED_TIME);
            }
            if(fieldMap.containsKey(AgentConstants.DEVICE_DETAILS)){
                fieldMap.remove(AgentConstants.DEVICE_DETAILS);
            }
            if(fieldMap.containsKey(AgentConstants.PROCESSOR_VERSION)){
                fieldMap.remove(AgentConstants.PROCESSOR_VERSION);
            }
            if(fieldMap.containsKey(AgentKeys.TRANSFORM_WORKFLOW_ID)){
                fieldMap.remove(AgentKeys.TRANSFORM_WORKFLOW_ID);
            }
        }
        fieldMap.put(AgentConstants.CONTROLLERS,FieldFactory.getCountOfDistinctField(FieldFactory.getIdField(controllerModule),AgentConstants.CONTROLLERS));
        GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
                .table(agentDataModule.getTableName())
                .select(fieldMap.values())
                .leftJoin(controllerModule.getTableName()).on(agentDataModule.getTableName() + ".ID = " + controllerModule.getTableName() + ".AGENT_ID")
                .groupBy(agentDataModule.getTableName() + ".ID");
        if(fieldMap.containsKey(AgentConstants.CONNECTED)){
                genericSelectRecordBuilder.orderBy(fieldMap.get(AgentConstants.CONNECTED).getColumnName());

        }
        if (!fetchDeleted) {
            genericSelectRecordBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getDeletedTimeField(agentDataModule), "NULL", CommonOperators.IS_EMPTY));
        }
        List<Map<String, Object>> maps = genericSelectRecordBuilder.get();
        if (CollectionUtils.isNotEmpty(maps)) {
        		List<Long> workflowIds = new ArrayList<>();
	        	for (Map<String, Object> agent : maps) {
	        		if (agent.get("workflowId") != null) {
	        			workflowIds.add((long) agent.get("workflowId"));
	        		}
	        	}
	        	if (!workflowIds.isEmpty()) {
	        		Map<Long, WorkflowContext> workflowMap = WorkflowUtil.getWorkflowsAsMap(workflowIds);
	        		for (Map<String, Object> agent : maps) {
		        		if (agent.get("workflowId") != null) {
		        			WorkflowContext workflow = workflowMap.get(agent.get("workflowId"));
		        			agent.put("workflow", workflow);
		        		}
		        	}
	        	}
        }
        return maps;
    }

    public static List<Map<String,Object>> getAgentFilter() throws Exception {
        FacilioModule agentDataModule = ModuleFactory.getNewAgentModule();
        List<FacilioField> filterFields = new ArrayList<>();
        filterFields.add(FieldFactory.getIdField(agentDataModule));
        filterFields.add(FieldFactory.getNameField(agentDataModule));
        filterFields.add(FieldFactory.getNewAgentTypeField(agentDataModule));
        filterFields.add(FieldFactory.getAgentConnectedField(agentDataModule));
        filterFields.add(FieldFactory.getAgentTypeField(agentDataModule));
        filterFields.add(FieldFactory.getAgentLastDataReveivedField(agentDataModule));
        filterFields.add(FieldFactory.getAgentDataIntervalField(agentDataModule));
        filterFields.add(FieldFactory.getField(AgentConstants.DISPLAY_NAME, "DISPLAY_NAME", agentDataModule, FieldType.STRING));
        
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(agentDataModule.getTableName())
                .select(filterFields)
                .andCondition(getDeletedTimeNullCondition(agentDataModule));
        List<Map<String,Object>> list =  selectRecordBuilder.get();
        if(list == null || list.isEmpty()){
            return Collections.emptyList();
        }
        for(Map<String,Object> itr : list){
            AgentUtilV2.getAgentOfflineStatus(itr);
        }
        return list;
    }

    public static Map<String, FacilioAgent> getWattsenseAgentsMap() throws Exception {
        Map<String, FacilioAgent> wattsenseAgentsMap = new HashMap<>();
        FacilioModule newAgentModule = ModuleFactory.getNewAgentModule();
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getAgentTypeField(newAgentModule),String.valueOf(AgentType.WATTSENSE.getKey()),NumberOperators.EQUALS));
        criteria.addAndCondition(getDeletedTimeNullCondition(newAgentModule));
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.CRITERIA,criteria);
        List<FacilioAgent> agents = getAgents(context);
        if(agents != null){
            for (FacilioAgent agent : agents) {
                wattsenseAgentsMap.put(agent.getName(),agent);
            }
        }
        return wattsenseAgentsMap;
    }

    public static FacilioAgent getWattsenseAgent() throws Exception {
        FacilioModule newAgentModule = ModuleFactory.getNewAgentModule();
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getAgentTypeField(newAgentModule),String.valueOf(AgentType.WATTSENSE.getKey()),NumberOperators.EQUALS));
        criteria.addAndCondition(getDeletedTimeNullCondition(newAgentModule));
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.CRITERIA,criteria);
        List<FacilioAgent> agents = getAgents(context);
        if(agents != null){
            return agents.get(0);
        }
        return null;
    }

    public static long getWattsenseAgentCount() {
        FacilioModule agentDataModule = ModuleFactory.getNewAgentModule();
        try {
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .select(new HashSet<>())
                    .table(agentDataModule.getTableName())
                    .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(agentDataModule))
                    .andCondition(getDeletedTimeNullCondition(agentDataModule))
                    .andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentTypeField(agentDataModule),String.valueOf(AgentType.WATTSENSE.getKey()),NumberOperators.EQUALS));
            List<Map<String, Object>> result = builder.get();
            return (long) result.get(0).get(AgentConstants.ID);
        } catch (Exception e) {
            LOGGER.info("Exception while getting agent count ",e);
        }
        return 0;
    }

    private long getAgentSites(Long agentId) throws Exception {
        FacilioModule newAgentDataModule = ModuleFactory.getNewAgentModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(newAgentDataModule.getTableName())
                .select(Arrays.asList(FieldFactory.getCountOfDistinctField(FieldFactory.getSiteIdField(newAgentDataModule),AgentConstants.TOTAL_COUNT)));
        if(agentId != null){
            builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(newAgentDataModule), String.valueOf(agentId),NumberOperators.EQUALS));
        }
        List<Map<String, Object>> result = builder.get();
        if( ! result.isEmpty()){
            return (long) result.get(1).get(AgentConstants.TOTAL_COUNT);
        }
        return 0;
    }

    private static boolean containsValueCheck(String key,JSONObject jsonObject){
        if(notNull(key)&& notNull(jsonObject) &&jsonObject.containsKey(key) && ( jsonObject.get(key) != null) ){
            return true;
        }
        return false;
    }
    private static boolean notNull(Object object) {
        return object != null;
    }
    public static FacilioAgent getAgent(String agentName) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(ModuleFactory.getNewAgentModule()),agentName,StringOperators.IS));
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.CRITERIA,criteria);
        List<FacilioAgent> agents = getAgents(context);
        if( ! agents.isEmpty() ){
            return agents.get(0);
        }
        return null;
    }

    private static List<FacilioAgent> getAgents(FacilioContext context) throws Exception {
        FacilioModule newAgentModule = ModuleFactory.getNewAgentModule();
        List<FacilioField> newAgentFields = FieldFactory.getNewAgentFields();

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(newAgentModule.getTableName())
                .select(newAgentFields);

        if (context.containsKey(FacilioConstants.ContextNames.CRITERIA)) {
            Criteria criteria;
            criteria = (Criteria) context.get(FacilioConstants.ContextNames.CRITERIA);
            selectRecordBuilder.andCriteria(criteria);
        }
        List<Map<String, Object>> maps = selectRecordBuilder.get();
        if(FacilioProperties.isDevelopment()){
            LOGGER.info("get agent query "+selectRecordBuilder.toString());
        }
        return FieldUtil.getAsBeanListFromMapList(maps,FacilioAgent.class);
    }

    public static void scheduleRestJob(FacilioAgent agent) throws Exception {
    		long interval = agent.getInterval();
    		ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);
        long totalMinutesInADay = 60 * 24;
        LocalTime time = LocalTime.of(0, 0);
        for (long frequency = totalMinutesInADay / interval; frequency > 0; frequency--) {
            time = time.plusMinutes(interval);
            scheduleInfo.addTime(time);
        }
    		FacilioTimer.scheduleCalendarJob(agent.getId(), Job.CLOUD_AGENT_JOB_NAME, System.currentTimeMillis(), scheduleInfo, "facilio");
    }


    /*public static long getWattsenseAgentCount(){
        getAgentCount();
    }*/
}
