package com.facilio.agentv2;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentKeys;
import com.facilio.agent.AgentType;
import com.facilio.agentv2.controller.Controller;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.*;

public class AgentApiV2 {

    private static final Logger LOGGER = LogManager.getLogger(AgentApiV2.class.getName());

    private static final long DEFAULT_TIME = 10L;

    private static StackTraceElement stackStrace() {
        return Thread.currentThread().getStackTrace()[2];
    }

    public static List<FacilioAgent> getAllAgents() throws Exception {
        return getAgents(null, null, true,null);
    }

    public static List<FacilioAgent> getAgents() throws Exception {
        return getAgents(null, null, false,null);
    }


    public static FacilioAgent getAgent(String agentName) throws Exception {
        List<FacilioAgent> agentList = getAgents(agentName, null, true,null);
        if (!agentList.isEmpty()) {
            return agentList.get(0);
        }
        return null;
    }

    public static List<FacilioAgent> getAgents(AgentType type) throws Exception {
        return getAgents(null, type, true,null);
    }

    public static List<FacilioAgent> listFacilioAgents(FacilioContext context) throws Exception {
        LOGGER.info(" getting agent ");
        return getAgents(null,null,false,context);
    }
    
    public static List<FacilioAgent> getAgents(Collection<Long> ids) throws Exception {
    		return getAgents(null, null, true, null, ids);
    }
    
    private static List<FacilioAgent> getAgents(String agentName, AgentType type, boolean getDeleted,FacilioContext context) throws Exception {
    		return getAgents(agentName, type, getDeleted, context, null);
    }

    private static List<FacilioAgent> getAgents(String agentName, AgentType type, boolean getDeleted,FacilioContext context, Collection<Long> ids) throws Exception {
        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentOrg().getOrgId());
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getNewAgentFields());
        if(context == null){
            context = new FacilioContext();
        }
        context.put(FacilioConstants.ContextNames.TABLE_NAME, AgentConstants.AGENT_TABLE);
        context.put(FacilioConstants.ContextNames.FIELDS, fieldMap.values());
        Criteria criteria = new Criteria();
        if ((agentName != null) && (!agentName.isEmpty())) {
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.NAME), agentName, StringOperators.IS));
        }
        if (type != null) {
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_TYPE), String.valueOf(type.getKey()), NumberOperators.EQUALS));
        }
        if (!getDeleted) {
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.DELETED_TIME), "NULL", CommonOperators.IS_EMPTY));
        }
        if (ids != null) {
        		criteria.addAndCondition(CriteriaAPI.getIdCondition(ids, null));
        }
        context.put(FacilioConstants.ContextNames.SORT_FIELDS,fieldMap.get(AgentConstants.CONNECTED).getColumnName()+" DESC");
        context.put(FacilioConstants.ContextNames.CRITERIA, criteria);

        List<Map<String, Object>> records = bean.getRows(context);
        return getAgentsFromRows(records);
    }

    public static FacilioAgent getAgent(Long agentId){
        LOGGER.info(" agentId for getAgent is "+agentId);
        if( (agentId != null) && (agentId > 0)) {
            try {
                Organization currentOrg = AccountUtil.getCurrentOrg();
                Objects.requireNonNull(currentOrg, " current org null ");
                ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", currentOrg.getOrgId());
                FacilioContext context = new FacilioContext();
                context.put(FacilioConstants.ContextNames.TABLE_NAME, AgentConstants.AGENT_TABLE);
                Criteria criteria = new Criteria();
                Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getNewAgentFields());
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.ID), String.valueOf(agentId), NumberOperators.EQUALS));
                context.put(FacilioConstants.ContextNames.CRITERIA, criteria);
                context.put(FacilioConstants.ContextNames.LIMIT_VALUE, 1);
                context.put(FacilioConstants.ContextNames.FIELDS, fieldsMap.values());
                List<Map<String, Object>> records = bean.getRows(context);
                List<FacilioAgent> agentList = getAgentsFromRows(records);
                if (!agentList.isEmpty()) {
                    return agentList.get(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            LOGGER.info("Exception occurred, AgentId null or less than 1 -> "+agentId);
        }
        return null;
    }

    private static List<FacilioAgent> getAgentsFromRows(List<Map<String, Object>> records) {
        List<FacilioAgent> agentList = new ArrayList<>();
        for (Map<String, Object> record : records) {
            JSONObject payload = new JSONObject();
            payload.putAll(record);
            try {
                FacilioAgent agent = getFacilioAgentFromJson(payload);
                agentList.add(agent);
            }catch (Exception e)
            {
                LOGGER.info("Exception occurred ",e);
            }
        }
        return agentList;
    }


   public static FacilioAgent getFacilioAgentFromJson(JSONObject payload) throws Exception {
            return FieldUtil.getAsBeanFromJson(payload,FacilioAgent.class);
    }

    public static long addAgent(FacilioAgent agent) throws Exception {
        LOGGER.info(" adding new agent ");
        Chain chain = TransactionChainFactory.addNewAgent();
        FacilioContext context = new FacilioContext();
        agent.setCreatedTime(System.currentTimeMillis());
        agent.setLastDataReceivedTime(agent.getCreatedTime());
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


    public static long addAgent(JSONObject jsonObject) {
        if( (jsonObject != null) && ( ! jsonObject.isEmpty() ) ){
            if (containsValueCheck(AgentConstants.NAME, jsonObject) && containsValueCheck(AgentConstants.AGENT_TYPE, jsonObject) && containsValueCheck(AgentConstants.SITE_ID, jsonObject)) {
                FacilioAgent agent = new FacilioAgent();
                agent.setName((String) jsonObject.get(AgentConstants.NAME));
                //agent.setType(AgentType.valueOf((int)jsonObject.get(AgentConstants.TYPE)));
                agent.setSiteId((long) jsonObject.get(AgentConstants.SITE_ID));
                try {
                    return addAgent(agent);
                } catch (Exception e) {
                    LOGGER.info("Exception while padding agent");
                }
            }
        }
        return -1;
    }

    public static boolean editAgent(FacilioAgent agent, JSONObject jsonObject) throws Exception {
        Long currTime = System.currentTimeMillis() ;
        if(jsonObject.containsKey(AgentConstants.TIMESTAMP)){
            currTime = (Long) jsonObject.get(AgentConstants.TIMESTAMP);
        }
        agent.setLastDataReceivedTime(currTime);
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
            }
        }

        if (containsValueCheck(AgentConstants.WRITABLE, jsonObject)) {
            boolean currWriteble = Boolean.parseBoolean(jsonObject.get(AgentConstants.WRITABLE).toString());
            if (agent.getWritable() != currWriteble) {
                agent.setWritable(currWriteble);
                agent.setLastModifiedTime(currTime);
            }
        }
        return updateAgent(agent);
    }

    public static boolean editAgent(FacilioAgent agent, Long dataInterval, Boolean writable, String displayName) throws Exception {
        if (agent != null) {
            if ((dataInterval != null) && (dataInterval > 0L)) {
                agent.setInterval(dataInterval);
            }
            if ((writable != null)) {
                agent.setWritable(writable);
            }
            if ((displayName != null) && (!displayName.isEmpty())) {
                agent.setDisplayName(displayName);
            }

            return updateAgent(agent);
        } else {
            LOGGER.info("Exception occurred, agent can't be null to update ");
        }
        return false;
    }

    public static boolean updateAgent(FacilioAgent agent) throws Exception {
        FacilioModule MODULE = ModuleFactory.getNewAgentDataModule();
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
        FacilioModule agentDataModule = ModuleFactory.getNewAgentDataModule();
        LOGGER.info(" updating agent last data recieved time "+agent.getCreatedTime());
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
        FacilioModule agentDataModule = ModuleFactory.getNewAgentDataModule();
        try {
            List<FacilioField> fields = new ArrayList<>();
            fields.add(fieldsmap.get(AgentConstants.CONNECTED));
            fields.add(FieldFactory.getIdField(agentDataModule));
            fields.add(FieldFactory.getSiteIdField(agentDataModule));
            GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                    .table(agentDataModule.getTableName())
                    .select(fields)
                    .andCondition(getDeletedTimeNullCondition(agentDataModule));
            List<Map<String, Object>> data = selectRecordBuilder.get();
            Set<Long> siteSet = new HashSet<>();
            int offlineCount = 0;
            if (!data.isEmpty()) {
                for (Map<String, Object> datum : data) {
                    if ((datum.get(AgentConstants.CONNECTED) == null) || (!(boolean) datum.get(AgentConstants.CONNECTED))) {
                        offlineCount++;
                    }
                    if((datum.get(AgentConstants.SITE_ID) != null) && (((Number)datum.get(AgentConstants.SITE_ID)).longValue()>0) ){
                        siteSet.add((Long) datum.get(AgentConstants.SITE_ID));
                    }
                }
            }
            JSONObject countData = new JSONObject();
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
        FacilioModule agentDataModule = ModuleFactory.getNewAgentDataModule();
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
        FacilioModule agentDataModule = ModuleFactory.getNewAgentDataModule();
        LOGGER.info(" in delete agent");
        try {
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(agentDataModule.getTableName())
                    .fields(FieldFactory.getNewAgentFields())
                    .andCondition(CriteriaAPI.getIdCondition(ids, agentDataModule));
            Map<String, Object> toUpdateMap = new HashMap<>();
            toUpdateMap.put(AgentConstants.DELETED_TIME,System.currentTimeMillis());
            int rowsAffected = builder.update(toUpdateMap);
            LOGGER.info(" rows affected ->"+rowsAffected);
            LOGGER.info(" update query "+builder.toString());
            if( rowsAffected > 0 ){
                return true;
            }

        }catch (Exception e){
            LOGGER.info("Exception while deleting agent ->"+ids+"--",e);
        }
        return false;
    }

    public static List<Map<String, Object>> getAgentListData(boolean fetchDeleted) throws Exception {
        FacilioModule agentDataModule = ModuleFactory.getNewAgentDataModule();
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
        LOGGER.info(" query "+genericSelectRecordBuilder.toString());
        return maps;
    }

    public static List<Map<String,Object>> getAgentFilter() throws Exception {
        FacilioModule agentDataModule = ModuleFactory.getNewAgentDataModule();
        List<FacilioField> filterFields = new ArrayList<>();
        filterFields.add(FieldFactory.getIdField(agentDataModule));
        filterFields.add(FieldFactory.getNameField(agentDataModule));
        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(agentDataModule.getTableName())
                .select(filterFields)
                .andCondition(getDeletedTimeNullCondition(agentDataModule));
        return selectRecordBuilder.get();
    }

    private long getAgentSites(Long agentId) throws Exception {
        FacilioModule newAgentDataModule = ModuleFactory.getNewAgentDataModule();
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

    private FacilioAgent getAgent(Controller controller) throws Exception {
        if ((controller != null)) {
            if ((controller.getAgentId() > 0)) {
                FacilioAgent agent = AgentApiV2.getAgent(controller.getAgentId());
                if (agent != null) {
                    return agent;
                } else {
                    throw new Exception("Exception Occurred, Agent null ");
                }
            } else {
                throw new Exception("Exception Occurred, controller.getAgentId cant be less than 1 ->" + controller.getAgentId());
            }
        } else {
            throw new Exception("Controller can't be null -> " + controller);
        }
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
}
