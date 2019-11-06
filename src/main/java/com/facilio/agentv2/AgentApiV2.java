package com.facilio.agentv2;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentType;
import com.facilio.agentv2.controller.Controller;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AgentApiV2 {

    private static final Logger LOGGER = LogManager.getLogger(AgentApiV2.class.getName());

    private static final long DEFAULT_TIME = 10L;

    private static StackTraceElement stackStrace(){
        return Thread.currentThread().getStackTrace()[2];
    }

    public static List<FacilioAgent> getAgents(){
        return getAgents(null,null);
    }

    public static FacilioAgent getAgent(String agentName){
        List<FacilioAgent> agentList = getAgents(agentName, null);
        if( ! agentList.isEmpty()){
            return agentList.get(0);
        }
        return null;
    }

    public static List<FacilioAgent> getAgents(AgentType type){
        return getAgents(null,type);
    }

    private static List<FacilioAgent> getAgents(String agentName,AgentType type){
        List<FacilioAgent> agentList = new ArrayList<>();
        try {
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentOrg().getOrgId());
            FacilioContext context = new FacilioContext();
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getNewAgentDataFields());
            context.put(FacilioConstants.ContextNames.TABLE_NAME,AgentConstants.AGENT_TABLE);
            context.put(FacilioConstants.ContextNames.FIELDS,fieldMap.values());
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.DELETED_TIME), "NULL", CommonOperators.IS_EMPTY));
            if((agentName != null) && ( ! agentName.isEmpty())){
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.NAME),agentName, StringOperators.IS));
            }
            if(type != null){
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_TYPE), String.valueOf(type.getKey()),NumberOperators.EQUALS));
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
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
        if(jsonObject.containsKey(key) && ( jsonObject.get(key) != null) ){
            return true;
        }
        return false;
    }
}
