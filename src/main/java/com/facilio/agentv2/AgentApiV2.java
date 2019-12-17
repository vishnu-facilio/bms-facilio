package com.facilio.agentv2;

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

import java.util.*;

public class AgentApiV2 {

    private static final Logger LOGGER = LogManager.getLogger(AgentApiV2.class.getName());

    private static final long DEFAULT_TIME = 10L;

    private static StackTraceElement stackStrace(){
        return Thread.currentThread().getStackTrace()[2];
    }

    public static List<FacilioAgent> getAllAgents(){
        return getAgents(null,null,true);
    }
    public static List<FacilioAgent> getAgents(){
        return getAgents(null,null,false);
    }


    public static FacilioAgent getAgent(String agentName){
        List<FacilioAgent> agentList = getAgents(agentName, null,true);
        if( ! agentList.isEmpty()){
            return agentList.get(0);
        }
        return null;
    }

    private static final Map<String, FacilioField> FIELDSMAP = FieldFactory.getAsMap(FieldFactory.getNewAgentDataFields());
    private static final FacilioModule MODULE = ModuleFactory.getNewAgentDataModule();

    public static List<FacilioAgent> getAgents(AgentType type){
        return getAgents(null,type,true);
    }

    private static List<FacilioAgent> getAgents(String agentName,AgentType type,boolean getDeleted){
        List<FacilioAgent> agentList = new ArrayList<>();
        try {
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentOrg().getOrgId());
            FacilioContext context = new FacilioContext();
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getNewAgentDataFields());
            context.put(FacilioConstants.ContextNames.TABLE_NAME,AgentConstants.AGENT_TABLE);
            context.put(FacilioConstants.ContextNames.FIELDS,fieldMap.values());
            Criteria criteria = new Criteria();
            if((agentName != null) && ( ! agentName.isEmpty())){
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.NAME),agentName, StringOperators.IS));
            }
            if(type != null){
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_TYPE), String.valueOf(type.getKey()),NumberOperators.EQUALS));
            }
            if( ! getDeleted ){
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.DELETED_TIME), "NULL", CommonOperators.IS_EMPTY));
            }
            context.put(FacilioConstants.ContextNames.CRITERIA,criteria);

            List<Map<String, Object>> records = bean.getRows(context);
            LOGGER.info(" rows selected are "+records);
            return getAgentsFromRows(records);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return agentList;
    }

    public static FacilioAgent getAgent(Long agentId){
        LOGGER.info(" agentId for getAgent is "+agentId);
        if( (agentId != null) && (agentId > 0)) {
            try {
                ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentOrg().getOrgId());
                FacilioContext context = new FacilioContext();
                context.put(FacilioConstants.ContextNames.TABLE_NAME, AgentConstants.AGENT_TABLE);
                Criteria criteria = new Criteria();
                Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getNewAgentDataFields());
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.ID), String.valueOf(agentId), NumberOperators.EQUALS));
                context.put(FacilioConstants.ContextNames.CRITERIA,criteria);
                context.put(FacilioConstants.ContextNames.LIMIT_VALUE, 1);
                context.put(FacilioConstants.ContextNames.FIELDS, fieldsMap.values());
                List<Map<String, Object>> records = bean.getRows(context);
                LOGGER.info(" rows selected are "+records);
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
                LOGGER.info(" making agent "+agent.toJSON());
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

    public static long addAgent(FacilioAgent agent) {
        LOGGER.info(" adding new agent " + stackStrace());
        Chain chain = TransactionChainFactory.addNewAgent();
        FacilioContext context = new FacilioContext();
        agent.setCreatedTime(System.currentTimeMillis());
        agent.setLastDataReceivedTime(agent.getCreatedTime());
        agent.setLastModifiedTime(agent.getCreatedTime());
        context.put(AgentConstants.AGENT,agent);
        try {
            chain.execute(context);
            Long agentId = (Long) context.get(AgentConstants.AGENT_ID);
            if( (agentId != null) && (agentId > 0)){
                agent.setId(agentId);
                return agentId;
            }else {
                LOGGER.info("Failed adding agent "+agent.getName());
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred while adding agent->"+agent.getName()+" ",e);
        }
        return -1;
    }


    public static long addAgent(JSONObject jsonObject) {
        if( (jsonObject != null) && ( ! jsonObject.isEmpty() ) ){
            if( containsValueCheck(AgentConstants.NAME,jsonObject) && containsValueCheck(AgentConstants.TYPE,jsonObject) && containsValueCheck(AgentConstants.SITE_ID,jsonObject)){
                FacilioAgent agent = new FacilioAgent();
                agent.setName((String)jsonObject.get(AgentConstants.NAME));
                //agent.setType(AgentType.valueOf((int)jsonObject.get(AgentConstants.TYPE)));
                agent.setSiteId((long)jsonObject.get(AgentConstants.SITE_ID));
                return addAgent(agent);
            }
        }
        return -1;
    }

    public static boolean editAgent(FacilioAgent agent,JSONObject jsonObject) {
        if (containsValueCheck(AgentConstants.DISPLAY_NAME,jsonObject)) {
            if (!jsonObject.get(AgentConstants.DISPLAY_NAME).toString().equals(agent.getDisplayName())) {
                agent.setDisplayName(jsonObject.get(AgentConstants.DISPLAY_NAME).toString());
            }
        }

        if (containsValueCheck(AgentConstants.DATA_INTERVAL,jsonObject)) {
            long currDataInterval = Long.parseLong(jsonObject.get(AgentConstants.DATA_INTERVAL).toString());
            if (agent.getInterval() != currDataInterval) {
                agent.setInterval(currDataInterval);
            }
        }

        if (containsValueCheck(AgentConstants.WRITABLE,jsonObject)) {
            boolean currWriteble = Boolean.parseBoolean(jsonObject.get(AgentConstants.WRITABLE).toString());
            if (agent.getWritable() != currWriteble) {
                agent.setWritable(currWriteble);
            }
        }
        return updateAgent(agent);
    }

    public static void main(String[] args) {
        System.out.println((List<FacilioField>) FIELDSMAP.values());
    }
    public static boolean updateAgent(FacilioAgent agent) {
        long currTime = System.currentTimeMillis();
        agent.setLastModifiedTime(currTime);
        agent.setLastUpdatedTime(currTime);
        agent.setLastDataReceivedTime(currTime);
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(MODULE.getTableName())
                .fields(FieldFactory.getNewAgentDataFields())
                .andCondition(CriteriaAPI.getIdCondition(agent.getId(),MODULE));
        try {
                if(updateRecordBuilder.update(FieldUtil.getAsJSON(agent)) > 0){
                    return true;
                }else {
                    LOGGER.info("Exception occurred while updating agent, updating failed");
                    return false;
                }
        }catch (Exception e){
            LOGGER.info("Exception occurred while updating agent ",e);
        }
        return false;
    }

    public static long getAgentCount() {
        long orgId = AccountUtil.getCurrentAccount().getOrg().getOrgId();
        if( orgId > 0){
            return getAgentCount(orgId);
        }
        return 0;
    }

    private static long getAgentCount(long orgId) {
        try {
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .select(new HashSet<>())
                    .table(MODULE.getTableName())
                    .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FIELDSMAP.get(AgentConstants.ID))
                    .andCondition(CriteriaAPI.getOrgIdCondition(orgId, MODULE));
            List<Map<String, Object>> result = builder.get();
            LOGGER.info(" count is "+result.get(0));
            return (long) result.get(0).get(AgentConstants.ID);
        }catch (Exception e){
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
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(MODULE.getTableName())
                    .fields(FieldFactory.getNewAgentDataFields())
                    .andCondition(CriteriaAPI.getIdCondition(ids, MODULE));
            if(builder.update(Collections.singletonMap(AgentKeys.DELETED_TIME, System.currentTimeMillis())) > 0 ){
                return true;
            }

        }catch (Exception e){
            LOGGER.info("Exception while deleting agent ->"+ids+"--",e);
        }
        return false;
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
