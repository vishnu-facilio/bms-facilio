package com.facilio.agentv2.controller;

import com.facilio.agent.FacilioAgent;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;

public class GetControllerRequest
{
    private FacilioContext context = new FacilioContext();
    private Criteria criteria = new Criteria();

    private static final Logger LOGGER = LogManager.getLogger(GetControllerRequest.class.getName());

    private ModuleBean modBean;
    private final List<FacilioField> fields ;
    private Map<String, FacilioField> FIELD_MAP;
    private FacilioControllerType controllerType;

    public GetControllerRequest() throws Exception {
        modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        fields = modBean.getAllFields(FacilioConstants.ContextNames.CONTROLLER_MODULE_NAME);
        FIELD_MAP = FieldFactory.getAsMap(fields);
    }


    private FacilioChain getFetchControllerChain(FacilioControllerType controllerType) throws Exception {

        if (controllerType == null) {
            throw new Exception(" controller type cant be null ");
        }
        FacilioChain getControllerChain;
        if (controllerType == FacilioControllerType.MODBUS_RTU) {
            getControllerChain = TransactionChainFactory.getRtuControllerChain();
        } else {
            getControllerChain = TransactionChainFactory.getControllerChain();
        }

        FacilioContext context = getControllerChain.getContext();
        String moduleName = AgentConstants.getControllerBean().getControllerModuleName(controllerType);
        if (moduleName == null || moduleName.isEmpty()) {
            throw new Exception("Exception Occurred, Module name is null or empty " + moduleName + "   for ");
        }
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        return getControllerChain;
    }

    public GetControllerRequest withAgent(FacilioAgent agent) throws Exception {
        return withAgentId(agent.getId());
    }

    public GetControllerRequest withAgentId(long agentId) throws Exception {
        if (agentId > 0) {
            context.put(AgentConstants.AGENT_ID, agentId);
            criteria.addAndCondition(CriteriaAPI.getCondition(FIELD_MAP.get(AgentConstants.AGENT_ID), String.valueOf(agentId), NumberOperators.EQUALS));
            context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, criteria);
        }else {
            throw new Exception(" agentId cant be less than 1 "+agentId);
        }
        return this;
    }

    public GetControllerRequest withNames(Set<String> names) throws Exception {
        if (names != null && names.size() > 0) {
            criteria.addAndCondition(CriteriaAPI.getCondition(FIELD_MAP.get(AgentConstants.NAME), StringUtils.join(names, ','), StringOperators.IS));
            context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, criteria);
        } else {
            throw new Exception("list is invalid");
        }
        return this;
    }

    public GetControllerRequest withControllerId(long controllerId) throws Exception {
        if(controllerId > 0){
            addIdsCondition(controllerId);
        }else {
            throw new Exception(" controllerId cant be less than 1 "+controllerId);
        }
        return this;
    }

    public GetControllerRequest withController(Controller controller) throws Exception {
        if(controller != null){
            return withControllerId(controller.getId());
        }else {
            throw new Exception("controller cant be null "+controller);
        }
    }

    public GetControllerRequest withControllerProperties(JSONObject controllerProperties, FacilioControllerType controllerType) throws Exception {
        if ((controllerProperties != null) && (!controllerProperties.isEmpty() && (controllerType != null) )){
            ofType(controllerType);
            if (controllerType == FacilioControllerType.MODBUS_RTU) {
                if (controllerProperties.containsKey(AgentConstants.NETWORK)) {
                    context.put(AgentConstants.COM_PORT, ((JSONObject) controllerProperties.get(AgentConstants.NETWORK)).get(AgentConstants.COM_PORT).toString());
                } else if (controllerProperties.containsKey(AgentConstants.COM_PORT)) {
                    context.put(AgentConstants.COM_PORT, controllerProperties.get(AgentConstants.COM_PORT).toString());
                } else {
                    throw new RuntimeException("network or comport is missing in controller props");
                }
            }
            if (controllerType != FacilioControllerType.MODBUS_RTU) {
                List<Condition> conditions = AgentConstants.getControllerBean().getControllerCondition(controllerProperties, controllerType);
                if (!conditions.isEmpty()) {
                    criteria.addAndConditions(conditions);
                    context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, criteria);
                } else {
                    LOGGER.info(" controller condition for this type " + controllerType.asString() + " is missing ");
                }
            } else {
                context.put(AgentConstants.CONTROLLER_PROPS, controllerProperties);
            }
        }
        return this;
    }

    public GetControllerRequest forDevice(long deviceId){
        if ((deviceId > 1)) {
            LOGGER.info(FIELD_MAP.containsKey(AgentConstants.DEVICE_ID));
            criteria.addAndCondition(CriteriaAPI.getCondition(FIELD_MAP.get(AgentConstants.DEVICE_ID), Collections.singleton(deviceId),NumberOperators.EQUALS));
            context.put(FacilioConstants.ContextNames.FILTER_CRITERIA,criteria);
        }
        return this;
    }

    public GetControllerRequest ofType(FacilioControllerType controllerType) throws Exception {
        if(controllerType != null){
            context.put(AgentConstants.CONTROLLER_TYPE, controllerType);
            this.controllerType = controllerType;
        }else {
            throw new Exception(" FacilioControllerType cant be null ");
        }
        return this;
    }

    public GetControllerRequest withPagination(FacilioContext context){
        if(context != null){
            this.context.putAll(context);
        }
        return this;
    }

    public List<Controller> getControllers() throws Exception {
        if(controllerType != null){
            return fetchControllers(controllerType);
        }
        return fetchAllControllers();
    }


    private List<Controller> fetchControllers(FacilioControllerType controllerType) throws Exception {
        FacilioChain getControllerChain = getFetchControllerChain(controllerType);
        FacilioContext chainContext = getControllerChain.getContext();
        chainContext.putAll(context);
        getControllerChain.execute();

        List<Controller> controllers = new ArrayList<>((List<Controller>) chainContext.get(FacilioConstants.ContextNames.RECORD_LIST));
        if(containsCheck("query", chainContext) && FacilioProperties.isDevelopment()){
            LOGGER.info(" get controller query --"+ chainContext.get("query"));
        }
        return controllers;
    }

    private List<Controller> fetchAllControllers() {
        List<Controller> controllers = new ArrayList<>();
        for (FacilioControllerType value : FacilioControllerType.values()) {
            try {
                controllers.addAll(fetchControllers(value));
            }catch (Exception e){
                LOGGER.info(" Exception while getting all controller "+value.asString());
            }
            System.out.println(" controllers size "+controllers.size());
        }
        return controllers;
    }


    private void addIdsCondition(long controllerId) {
        ArrayList<Long> idList = (ArrayList<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
        if(idList == null){
            idList = new ArrayList<>();
        }
        idList.add(controllerId);
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, idList);
    }

    public static boolean containsCheck(String key, Map map) {
        if ((key != null) && (!key.isEmpty()) && (map != null) && (!map.isEmpty()) && (map.containsKey(key)) && (map.get(key) != null)) {
            return true;
        }
        return false;
    }

    public Map<String, Controller> getControllersMap() throws Exception {
        List<Controller> controllers = getControllers();
        Map<String, Controller> controllerMap= new HashMap<>();
        if((controllers != null) && ( ! controllers.isEmpty())){
            for (Controller controller : controllers) {
                controllerMap.put(controller.getIdentifier(),controller);
            }
        }
        return controllerMap;
    }

    public Controller getController() throws Exception {
        List<Controller> controllers = getControllers();
        if( ! controllers.isEmpty()){
            return controllers.get(0);
        }
        return null;
    }
}
