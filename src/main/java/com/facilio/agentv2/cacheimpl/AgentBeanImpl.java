package com.facilio.agentv2.cacheimpl;

import com.facilio.agent.AgentType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.AgentUtilV2;
import com.facilio.agentv2.CloudAgentUtil;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.ControllerUtilV2;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.FacilioException;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.taskengine.ScheduleInfo;
import com.facilio.tasker.FacilioTimer;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.chain.Chain;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


public class AgentBeanImpl implements AgentBean {

    private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(AgentBeanImpl.class.getName());

    @Override
    public List<FacilioAgent> getAgents(Collection<Long> ids) throws Exception {
        return getAgents(ids, false);
    }

    @Override
    public List<FacilioAgent> getAgents(Collection<Long> ids, boolean fetchOnlyName) throws Exception {
        FacilioModule newAgentModule = ModuleFactory.getNewAgentModule();
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getIdCondition(ids, newAgentModule));
        criteria.addAndCondition(getDeletedTimeNullCondition(newAgentModule));
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.CRITERIA, criteria);
        List<String> fields = null;
        if (fetchOnlyName) {
            fields = Arrays.asList("id", AgentConstants.NAME, AgentConstants.DISPLAY_NAME);
        }
        return getAgents(context, fields);
    }

    @Override
    public Map<Long, FacilioAgent> getAgentMap(Collection<Long> ids) throws Exception {
        return getAgentMap(ids, false);
    }

    @Override
    public Map<Long, FacilioAgent> getAgentMap(Collection<Long> ids, boolean fetchOnlyName) throws Exception {
        List<FacilioAgent> agents = getAgents(ids, fetchOnlyName);
        return agents.stream().collect(Collectors.toMap(FacilioAgent::getId, Function.identity()));
    }

    @Override
    public FacilioAgent getAgent(Long agentId) throws Exception {
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
            } else {
                return null;
            }
        } else {
            throw new Exception("agentId can't be null or less than 1 " + agentId);
        }
    }

    @Override
    public long addAgent(FacilioAgent agent) throws Exception {
        Chain chain = TransactionChainFactory.addNewAgent();
        FacilioContext context = new FacilioContext();
        agent.setCreatedTime(System.currentTimeMillis());
        agent.setLastDataReceivedTime(-1);
        if (agent.getAgentType() == AgentType.CLOUD.getKey() || agent.getAgentTypeEnum().isAgentService()) {
            agent.setConnected(true);
        }
        agent.setLastModifiedTime(agent.getCreatedTime());
        context.put(AgentConstants.AGENT, agent);
        chain.execute(context);
        if (context.containsKey(AgentConstants.AGENT_ID)) {

            Long agentId = (Long) context.get(AgentConstants.AGENT_ID);
            if ((agentId != null) && (agentId > 0)) {
                agent.setId(agentId);
                return agentId;
            } else {
                LOGGER.info("Failed adding agent " + agent.getName());
                throw new Exception(" Agent added but agentId can't be obtained -> " + agentId);
            }
        } else {
            throw new Exception(" Agent added but context is missing agentId ");
        }
    }

    @Override
    public boolean editAgent(FacilioAgent agent, JSONObject jsonObject, boolean updateLastDataReceivedTime) throws Exception {
        AgentType agentType = AgentType.valueOf(agent.getAgentType());
        Long currTime = System.currentTimeMillis();
        if (updateLastDataReceivedTime) {
            agent.setLastDataReceivedTime(currTime);
        }
        updateAgentLastDataReceivedTime(agent);
        if (containsValueCheck(AgentConstants.DISPLAY_NAME, jsonObject)) {
            if (!jsonObject.get(AgentConstants.DISPLAY_NAME).toString().equals(agent.getDisplayName())) {
                agent.setDisplayName(jsonObject.get(AgentConstants.DISPLAY_NAME).toString());
                agent.setLastModifiedTime(currTime);
            }
        }
        if (containsValueCheck(AgentConstants.SITE_ID, jsonObject)) {
            long newSiteId = Long.parseLong(jsonObject.get(AgentConstants.SITE_ID).toString());
            if (agent.getSiteId() == 0) {
                agent.setSiteId(newSiteId);
                agent.setLastModifiedTime(currTime);
            }
        }
        if (containsValueCheck(AgentConstants.CONTROLLER_ALARM_INTERVAL_IN_MINS, jsonObject)) {
            String intervalStr = jsonObject.get(AgentConstants.CONTROLLER_ALARM_INTERVAL_IN_MINS).toString();
            Integer newInterval = Integer.parseInt(intervalStr);
            if (agent.getControllerAlarmIntervalInMins() != newInterval) {
                agent.setControllerAlarmIntervalInMins(newInterval);
                agent.setLastModifiedTime(currTime);
                if(newInterval == -99){
                    ControllerUtilV2.createOrDeleteControllerOfflineAlarmJob(agent, false);
                } else {
                    ControllerUtilV2.createOrDeleteControllerOfflineAlarmJob(agent, true);
                }
            }
        }
        if (containsValueCheck(AgentConstants.DATA_INTERVAL, jsonObject)) {
            long currDataInterval = Long.parseLong(jsonObject.get(AgentConstants.DATA_INTERVAL).toString());
            if (agent.getInterval() != currDataInterval) {
                agent.setInterval(currDataInterval);
                agent.setLastModifiedTime(currTime);
                if (agentType == AgentType.NIAGARA || agentType == AgentType.FACILIO) {
                    AgentMessenger.setProperty(agent.getId(), AgentConstants.DATA_INTERVAL, currDataInterval);
                }
                if (agent.getAgentType() == AgentType.CLOUD.getKey()) {
                    FacilioTimer.deleteJob(agent.getId(), FacilioConstants.Job.CLOUD_AGENT_JOB_NAME);
                    scheduleRestJob(agent);
                }
            }
        }

        if (containsValueCheck(AgentConstants.WRITABLE, jsonObject)) {
            boolean currWriteble = Boolean.parseBoolean(jsonObject.get(AgentConstants.WRITABLE).toString());
            if (agent.getWritable() != currWriteble) {
                agent.setWritable(currWriteble);
                agent.setLastModifiedTime(currTime);
            }
        }

        if (containsValueCheck(AgentConstants.AUTO_MAPPING_PARENT_FIELD_ID, jsonObject)) {
            String var = jsonObject.get(AgentConstants.AUTO_MAPPING_PARENT_FIELD_ID).toString();
            long newAutoMappingParentFieldId = Long.parseLong(var);
            if (agent.getAutoMappingParentFieldId() != newAutoMappingParentFieldId) {
                agent.setAutoMappingParentFieldId(newAutoMappingParentFieldId);
                agent.setLastModifiedTime(currTime);
            }
        }

        if (containsValueCheck(AgentConstants.ALLOW_AUTO_MAPPING, jsonObject)) {
            boolean currentValue = Boolean.parseBoolean(jsonObject.get(AgentConstants.ALLOW_AUTO_MAPPING).toString());
            if (agent.isAllowAutoMapping() != currentValue) {
                agent.setAllowAutoMapping(currentValue);
                agent.setLastModifiedTime(currTime);
            }
        }


        List<Long> oldWorkflowIds = new ArrayList<>();
        addWorkflows(AgentConstants.TRANSFORM_WORKFLOW, agent, jsonObject, agent.getTransformWorkflowId(), oldWorkflowIds, agent::setTransformWorkflowId);
        addWorkflows(AgentConstants.COMMAND_WORKFLOW, agent, jsonObject, agent.getCommandWorkflowId(), oldWorkflowIds, agent::setCommandWorkflowId);
        if (agentType.isAgentService()) {
            WorkflowContext workflow = FieldUtil.getAsBeanFromMap((Map<String, Object>) jsonObject.get(AgentConstants.WORKFLOW), WorkflowContext.class);
            agent.setWorkflow(workflow);
            CloudAgentUtil.editServiceAgent(agent);
        } else {
            addWorkflows(AgentConstants.WORKFLOW, agent, jsonObject, agent.getWorkflowId(), oldWorkflowIds, agent::setWorkflowId);
        }

        boolean status = updateAgent(agent);
        LOGGER.debug("Update status : " + status);
        if (!oldWorkflowIds.isEmpty()) {
            WorkflowUtil.deleteWorkflows(oldWorkflowIds);
        }

        return status;

    }

    private boolean validateWorkflows(FacilioAgent agent, String name) {
        AgentType agentType = agent.getAgentTypeEnum();
        if (!name.equals(AgentConstants.TRANSFORM_WORKFLOW) && (agentType == AgentType.FACILIO || agentType == AgentType.NIAGARA)) {
            return false;
        }
        if (name.equals(AgentConstants.WORKFLOW) && agentType != AgentType.CLOUD && agentType != AgentType.CLOUD_ON_SERVICE) {
            return false;
        }
        return true;
    }

    private void addWorkflows(String property, FacilioAgent agent, JSONObject jsonObject, long oldWorkflowId, List<Long> oldWorkflowIds, Consumer<Long> setWorkflow) throws Exception {
        if (containsValueCheck(property, jsonObject)) {
            if (!validateWorkflows(agent, property)) {
                throw new FacilioException("Workflow cannot be added for this agent type");
            }
            WorkflowContext workflow = FieldUtil.getAsBeanFromMap((Map<String, Object>) jsonObject.get(property), WorkflowContext.class);
            if (workflow.validateWorkflow()) {
                long workflowId = WorkflowUtil.addWorkflow(workflow);
                setWorkflow.accept(workflowId);
            } else {
                throw new IllegalArgumentException(workflow.getErrorListener().getErrorsAsString());
            }
            if (oldWorkflowId > 0) {
                oldWorkflowIds.add(oldWorkflowId);
            }
        }
    }

    public boolean updateAgent(FacilioAgent agent) throws Exception {
        FacilioModule module = ModuleFactory.getNewAgentModule();
        long currTime = System.currentTimeMillis();
        agent.setLastUpdatedTime(currTime);
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(FieldFactory.getNewAgentFields())
                .andCondition(CriteriaAPI.getIdCondition(agent.getId(), module));
        updateRecordBuilder.update(FieldUtil.getAsJSON(agent));
        return true;
    }

    @Override
    public void updateAgentLastDataReceivedTime(FacilioAgent agent) {
        FacilioModule agentDataModule = ModuleFactory.getNewAgentModule();
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(agentDataModule.getTableName())
                .fields(FieldFactory.getNewAgentFields())
                .andCondition(CriteriaAPI.getIdCondition(agent.getId(), agentDataModule));
        Map<String, Object> toUpdate = new HashMap<>();
        toUpdate.put(AgentConstants.LAST_DATA_RECEIVED_TIME, agent.getLastDataReceivedTime());
        try {
            if (updateRecordBuilder.update(toUpdate) > 0) {
            } else {
                LOGGER.info("Exception occurred while updating agent, updating failed");
            }
        } catch (SQLException e) {
            LOGGER.info("Exception occurred while updating agent last data received time");
        }
    }

    public Condition getDeletedTimeNullCondition(FacilioModule module) {
        return CriteriaAPI.getCondition(FieldFactory.getDeletedTimeField(module), "NULL", CommonOperators.IS_EMPTY);
    }

    public JSONObject getAgentCountDetails() {
        Map<String, FacilioField> fieldsmap = FieldFactory.getAsMap(FieldFactory.getNewAgentFields());
        FacilioModule agentDataModule = ModuleFactory.getNewAgentModule();
        try {
            List<FacilioField> fields = new ArrayList<>();
            fields.add(fieldsmap.get(AgentConstants.CONNECTED));
            fields.add(FieldFactory.getIdField(agentDataModule));
            fields.add(FieldFactory.getSiteIdField(agentDataModule));
            fields.add(FieldFactory.getAgentDataIntervalField(agentDataModule));
            fields.add(FieldFactory.getAgentTypeField(agentDataModule));
            fields.add(FieldFactory.getLastDataReceivedField(agentDataModule));
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
                    offlineCount += AgentUtilV2.getAgentOfflineStatus(datum);
                    if ((datum.get(AgentConstants.SITE_ID) != null) && (((Number) datum.get(AgentConstants.SITE_ID)).longValue() > 0)) {
                        siteSet.add((Long) datum.get(AgentConstants.SITE_ID));
                    }
                }
            } else {
                return new JSONObject();
            }
            JSONObject countData = new JSONObject();
            countData.put(AgentConstants.RECORD_IDS, ids);
            countData.put(AgentConstants.SITE_COUNT, siteSet.size());
            countData.put(AgentConstants.TOTAL_COUNT, data.size());
            countData.put(AgentConstants.ACTIVE_COUNT, (data.size() - offlineCount));
            return countData;
        } catch (Exception e) {
            LOGGER.info("Exception while getting agent count data ", e);
        }
        return new JSONObject();
    }

    public long getAgentCount(String querySearch, Criteria filterCriteria) {
        Map<String, FacilioField> fieldsmap = FieldFactory.getAsMap(FieldFactory.getNewAgentFields());
        FacilioModule agentDataModule = ModuleFactory.getNewAgentModule();
        try {
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .select(new HashSet<>())
                    .table(agentDataModule.getTableName())
                    .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, fieldsmap.get(AgentConstants.ID))
                    .andCondition(getDeletedTimeNullCondition(agentDataModule));
            if (querySearch != null) {
                builder.andCondition(CriteriaAPI.getCondition(fieldsmap.get("displayName"), querySearch, StringOperators.CONTAINS));
            }
            if (filterCriteria != null) {
                builder.andCriteria(filterCriteria);
            }
            List<Map<String, Object>> result = builder.get();
            return (long) result.get(0).get(AgentConstants.ID);
        } catch (Exception e) {
            LOGGER.info("Exception while getting agent count ", e);
        }
        return 0;
    }

    @Override
    public boolean deleteAgent(List<Long> ids) throws Exception {
        try {
            FacilioChain facilioChain = TransactionChainFactory.deleteAgentV2();
            FacilioContext context = new FacilioContext();
            context.put(AgentConstants.RECORD_IDS, ids);
            facilioChain.setContext(context);
            return !facilioChain.execute();
        } catch (Exception e) {
            LOGGER.info("Exception while deleting agent ->" + ids + "--", e);
            return false;
        }
    }

    @Override
    public List<Map<String, Object>> getAgentListData(boolean fetchDeleted, String querySearch, JSONObject pagination, List<Long> defaultIds, Criteria filterCriteria) throws Exception {
        FacilioModule agentDataModule = ModuleFactory.getNewAgentModule();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getNewAgentFields());
        FacilioModule controllerModule = ModuleFactory.getNewControllerModule();
        if (!fieldMap.isEmpty()) {
            if (fieldMap.containsKey(AgentConstants.DELETED_TIME)) {
                fieldMap.remove(AgentConstants.DELETED_TIME);
            }
            if (fieldMap.containsKey(AgentConstants.DEVICE_DETAILS)) {
                fieldMap.remove(AgentConstants.DEVICE_DETAILS);
            }
            if (fieldMap.containsKey(AgentConstants.PROCESSOR_VERSION)) {
                fieldMap.remove(AgentConstants.PROCESSOR_VERSION);
            }
        }
        fieldMap.put(AgentConstants.CONTROLLERS, FieldFactory.getCountOfDistinctField(FieldFactory.getIdField(controllerModule), AgentConstants.CONTROLLERS));
        GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
                .table(agentDataModule.getTableName())
                .select(fieldMap.values())
                .leftJoin(controllerModule.getTableName()).on(agentDataModule.getTableName() + ".ID = " + controllerModule.getTableName() + ".AGENT_ID")
                .groupBy(agentDataModule.getTableName() + ".ID");
        if (querySearch != null) {
            genericSelectRecordBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("displayName"), querySearch, StringOperators.CONTAINS));
        }
        if (filterCriteria != null) {
            genericSelectRecordBuilder.andCriteria(filterCriteria);
        }
        String orderBy = "";
        if (fieldMap.containsKey(AgentConstants.CONNECTED)) {
            String orderByConnected = fieldMap.get(AgentConstants.CONNECTED).getCompleteColumnName();
            String orderById = fieldMap.get(AgentConstants.ID).getCompleteColumnName();
            orderBy = orderByConnected + "," + orderById;
        }
        if (CollectionUtils.isNotEmpty(defaultIds)) {
            orderBy = RecordAPI.getDefaultIdOrderBy(agentDataModule, defaultIds, orderBy);
        }
        if (orderBy != null && !orderBy.isEmpty()) {
            genericSelectRecordBuilder.orderBy(orderBy);
        }
        if (pagination != null) {
            int page = (int) pagination.get("page");
            int perPage = (int) pagination.get("perPage");

            if (perPage != -1) {
                int offset = ((page - 1) * perPage);
                if (offset < 0) {
                    offset = 0;
                }

                genericSelectRecordBuilder.offset(offset);
                genericSelectRecordBuilder.limit(perPage);
            }
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
                if (agent.get("transformWorkflowId") != null) {
                    workflowIds.add((long) agent.get("transformWorkflowId"));
                }
                if (agent.get("commandWorkflowId") != null) {
                    workflowIds.add((long) agent.get("commandWorkflowId"));
                }
            }
            if (!workflowIds.isEmpty()) {
                Map<Long, WorkflowContext> workflowMap = WorkflowUtil.getWorkflowsAsMap(workflowIds);
                for (Map<String, Object> agent : maps) {
                    if (agent.get("workflowId") != null) {
                        WorkflowContext workflow = workflowMap.get(agent.get("workflowId"));
                        agent.put("workflow", workflow);
                    }
                    if (agent.get("transformWorkflowId") != null) {
                        WorkflowContext workflow = workflowMap.get(agent.get("transformWorkflowId"));
                        agent.put(AgentConstants.TRANSFORM_WORKFLOW, workflow);
                    }
                    if (agent.get("commandWorkflowId") != null) {
                        WorkflowContext workflow = workflowMap.get(agent.get("commandWorkflowId"));
                        agent.put(AgentConstants.COMMAND_WORKFLOW, workflow);
                    }
                }
            }
        }

        return maps;
    }

    @Override
    public List<Map<String, Object>> getAgentFilter() throws Exception {
        FacilioModule agentDataModule = ModuleFactory.getNewAgentModule();
        List<FacilioField> filterFields = new ArrayList<>();
        filterFields.add(FieldFactory.getIdField(agentDataModule));
        filterFields.add(FieldFactory.getNameField(agentDataModule));
        filterFields.add(FieldFactory.getNewAgentTypeField(agentDataModule));
        filterFields.add(FieldFactory.getAgentConnectedField(agentDataModule));
        filterFields.add(FieldFactory.getAgentTypeField(agentDataModule));
        filterFields.add(FieldFactory.getLastDataReceivedField(agentDataModule));
        filterFields.add(FieldFactory.getAgentDataIntervalField(agentDataModule));
        filterFields.add(FieldFactory.getField(AgentConstants.DISPLAY_NAME, "DISPLAY_NAME", agentDataModule, FieldType.STRING));

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(agentDataModule.getTableName())
                .select(filterFields)
                .andCondition(getDeletedTimeNullCondition(agentDataModule));
        List<Map<String, Object>> list = selectRecordBuilder.get();
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        for (Map<String, Object> itr : list) {
            AgentUtilV2.getAgentOfflineStatus(itr);
        }
        return list;
    }

    private static boolean containsValueCheck(String key, JSONObject jsonObject) {
        if (notNull(key) && notNull(jsonObject) && jsonObject.containsKey(key) && (jsonObject.get(key) != null)) {
            return true;
        }
        return false;
    }

    private static boolean notNull(Object object) {
        return object != null;
    }

    @Override
    public FacilioAgent getAgent(String agentName) throws Exception {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(ModuleFactory.getNewAgentModule()), agentName, StringOperators.IS));
        List<FacilioAgent> agents = getAgents(criteria);
        if (!agents.isEmpty()) {
            return agents.get(0);
        }
        return null;
    }

    @Override
    public List<FacilioAgent> getAgents(Criteria criteria) throws Exception {
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.CRITERIA, criteria);
        List<FacilioAgent> agents = getAgents(context);
        return agents;
    }

    private List<FacilioAgent> getAgents(FacilioContext context) throws Exception {
        return getAgents(context, null);
    }

    private List<FacilioAgent> getAgents(FacilioContext context, List<String> fields) throws Exception {
        FacilioModule newAgentModule = ModuleFactory.getNewAgentModule();
        List<FacilioField> newAgentfields = FieldFactory.getNewAgentFields();
        if (fields != null) {
            newAgentfields = newAgentfields.stream().filter(field -> fields.contains(field.getName()))
                    .collect(Collectors.toList());
        }

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(newAgentModule.getTableName())
                .select(newAgentfields)
                .andCondition(getDeletedTimeNullCondition(newAgentModule));

        if (context.containsKey(FacilioConstants.ContextNames.CRITERIA)) {
            Criteria criteria;
            criteria = (Criteria) context.get(FacilioConstants.ContextNames.CRITERIA);
            selectRecordBuilder.andCriteria(criteria);
        }
        List<Map<String, Object>> maps = selectRecordBuilder.get();
        return FieldUtil.getAsBeanListFromMapList(maps, FacilioAgent.class);
    }

    @Override
    public void scheduleRestJob(FacilioAgent agent) throws Exception {
        long interval = agent.getInterval();

        if (interval >= 3) {
            ScheduleInfo scheduleInfo = new ScheduleInfo();
            scheduleInfo.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);

            long totalMinutesInADay = 60 * 24;
            LocalTime time = LocalTime.of(0, 0);
            for (long frequency = totalMinutesInADay / interval; frequency > 0; frequency--) {
                time = time.plusMinutes(interval);
                scheduleInfo.addTime(time);
            }
            FacilioTimer.scheduleCalendarJob(agent.getId(), FacilioConstants.Job.CLOUD_AGENT_JOB_NAME, System.currentTimeMillis(), scheduleInfo, "priority");
        } else {
            interval *= 60;
            FacilioTimer.schedulePeriodicJob(agent.getId(), FacilioConstants.Job.CLOUD_AGENT_JOB_NAME, interval, (int) interval, "priority");
        }
    }

    public void scheduleJob(FacilioAgent agent, String jobName) throws Exception {
        long interval = 30;

        ScheduleInfo scheduleInfo = new ScheduleInfo();
        scheduleInfo.setFrequencyType(ScheduleInfo.FrequencyType.DAILY);

        long totalMinutesInADay = 60 * 24;
        LocalTime time = LocalTime.of(0, 0);
        for (long frequency = totalMinutesInADay / interval; frequency > 0; frequency--) {
            time = time.plusMinutes(interval);
            scheduleInfo.addTime(time);
        }
        FacilioTimer.scheduleCalendarJob(agent.getId(), jobName, System.currentTimeMillis(), scheduleInfo, "facilio");
    }

}

