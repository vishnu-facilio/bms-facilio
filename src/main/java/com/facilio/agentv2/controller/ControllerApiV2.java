package com.facilio.agentv2.controller;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.bacnet.BacnetIpController;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.agentv2.misc.MiscController;
import com.facilio.agentv2.modbusrtu.ModbusRtuController;
import com.facilio.agentv2.modbustcp.ModbusTcpController;
import com.facilio.agentv2.niagara.NiagaraController;
import com.facilio.agentv2.opcua.OpcUaController;
import com.facilio.agentv2.opcxmlda.OpcXmlDaController;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.custom.CustomController;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerApiV2 {
    private static final Logger LOGGER = LogManager.getLogger(ControllerApiV2.class.getName());

    public static long addController(Controller controller) {
        try {
            FacilioChain addControllerChain = TransactionChainFactory.getAddControllerChain();
            FacilioContext context = addControllerChain.getContext();
            String assetCategoryName = ControllerApiV2.getControllerModuleName(FacilioControllerType.valueOf(controller.getControllerType()));
            AssetCategoryContext asset = AssetsAPI.getCategory(assetCategoryName);
            controller.setCategory(asset);
            controller.setSiteId(2);
            controller.setCreatedTime(System.currentTimeMillis());
            controller.setId(-1);
            controller.setLastModifiedTime(System.currentTimeMillis());
            context.put(FacilioConstants.ContextNames.RECORD,controller);
            context.put(FacilioConstants.ContextNames.MODULE_NAME,controller.getModuleName());
            LOGGER.info(" module name is "+controller.getModuleName());
            addControllerChain.execute();
            return (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        } catch (Exception e) {
            LOGGER.info("Exception occurred ",e);
        }
        return -1;
    }

    public static Map<String, Controller> getAllControllersFromDb(long agentId) {
        LOGGER.info(" getting all controllers ");
        Map<String, Controller> controllers = new HashMap<>();
        for (FacilioControllerType value : FacilioControllerType.values()) {
            controllers.putAll(getControllersFromDb(null,agentId,value,-1));
        }
        LOGGER.info(" returning controllers "+controllers.size());
        return controllers;
    }

    public static Controller getControllerFromDb(long controllerId){
        LOGGER.info(" controller id form getting controller "+controllerId);
        if(controllerId > 0){
            for (FacilioControllerType type : FacilioControllerType.values()) {
                Map<String, Controller> controllerMap = getControllersFromDb(null, -1, type, controllerId);
                for (Controller controller : controllerMap.values()) {
                    if( controller.getId() == controllerId){
                        LOGGER.info(" controller returned "+controller.toJSON());
                        return controller;
                    }
                }
            }
        }else {
            LOGGER.info(" Exception Occurred, ControllerId can't be less than zero ");
        }
        return null;
    }

    public static Controller getControllerFromDb(String controllerIdentifier, long agentId, FacilioControllerType controllerType) {
        if( controllerType != null){
            return getControllersFromDb(controllerIdentifier,agentId,controllerType, -1).get(controllerIdentifier);
        }
        return getAllControllersFromDb(agentId).get(controllerIdentifier);
    }

    public static Map<String, Controller> getControllersFromDb(long agentId, FacilioControllerType controllerType){
        if(controllerType == null){
            return new HashMap<>();
        }
        return getControllersFromDb(null,agentId,controllerType,-1);
    }

    /**
     * if agentId alone is given - gets all controllers for the agent
     * id agentId and controllerIdentifier is giver get the map containing that particular controller, if nu such controller returns an empty controller.
     * If module name not generated returns empty List.
     *
     * @param controllerIdentifier
     * @param agentId
     * @param controllerType
     * @param controllerId
     * @return
     */
    private static Map<String, Controller> getControllersFromDb(String controllerIdentifier, long agentId, FacilioControllerType controllerType,long controllerId) {
        Map<String, Controller> controllerMap = new HashMap<>();
        if(controllerType == null){
            LOGGER.info(" controller type cant be null ");
            return controllerMap;
        }
        FacilioChain getControllerChain = TransactionChainFactory.getControllerChain();
        FacilioContext context = getControllerChain.getContext();
        String moduleName = getControllerModuleName(controllerType);
        if(moduleName == null || moduleName.isEmpty()){
            LOGGER.info("Exception Occurred, Module name is null or empty "+moduleName);
            return controllerMap;
        }
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);

        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = modBean.getAllFields(moduleName);
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
            Criteria criteria = new Criteria();
            if(agentId > 0){
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.AGENT_ID), String.valueOf(agentId), NumberOperators.EQUALS));
                context.put(FacilioConstants.ContextNames.FILTER_CRITERIA,criteria);
            }

            if (controllerId > 0) {
                ArrayList<Long> idList = new ArrayList<>();
                idList.add(controllerId);
                context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,idList);
            }

            if( (controllerIdentifier != null) && ( ! controllerIdentifier.isEmpty() ) ){
                List<Condition> conditions = getControllerCondition(controllerIdentifier, controllerType);
                if(! conditions.isEmpty()) {
                    criteria.addAndConditions(conditions);
                    context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, criteria);
                }else{
                    LOGGER.info(" controller condition for this type "+controllerType.asString()+" is missing ");
                }
            }
            getControllerChain.execute(context);
            List<Controller> controllers = (List<Controller>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
            LOGGER.info(" controllers from db "+controllers.size());
            for (Controller controller : controllers) {
                controllerMap.put(controller.makeIdentifier(),controller);
            }
            return controllerMap;
        } catch (Exception e) {
            LOGGER.info("Exception occurred ",e);
        }
        return new HashMap<>();
    }

    /**
     * Based on the controllerType it creates a dummy controller of a particular type using the identifier constructor and then uses the controller object to get uniques to build the condition.
     *
     *          ----IF controller type didn't match then empty conditions are sent back---
     *
     * @param controllerIdentifier
     * @param controllerType
     * @return
     * @throws Exception
     */
    private static List<Condition> getControllerCondition(String controllerIdentifier, FacilioControllerType controllerType) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(getControllerModuleName(controllerType));
        Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
        List<Condition> conditions = new ArrayList<>();
        switch (controllerType) {
            case BACNET_IP:
                conditions.addAll((new BacnetIpController()).getControllerConditions(controllerIdentifier));
                break;
            case MODBUS_RTU:
                conditions.addAll(new ModbusRtuController().getControllerConditions(controllerIdentifier));
                break;
            case OPC_XML_DA:
                conditions.addAll(new OpcXmlDaController().getControllerConditions(controllerIdentifier));
                break;
            case OPC_UA:
                conditions.addAll(new OpcUaController().getControllerConditions(controllerIdentifier));
                break;
            case NIAGARA:
                conditions.addAll(new NiagaraController().getControllerConditions(controllerIdentifier));
                break;
            case MODBUS_IP:
                conditions.addAll(new ModbusTcpController().getControllerConditions(controllerIdentifier));
                break;
            case MISC:
                conditions.addAll(new MiscController().getControllerConditions(controllerIdentifier));
                break;
            case CUSTOM:
                conditions.addAll(new CustomController().getControllerConditions(controllerIdentifier));
            case BACNET_MSTP:
            case KNX:
            case LON_WORKS:
                throw new Exception(" NO CONDITION IMPLEMENTATION FOR " + controllerType.asString() + " Controllers");
        }
        return conditions;
    }

    public static String getControllerModuleName(FacilioControllerType controllerType){
        LOGGER.info(" controller type is "+controllerType);
        switch (controllerType) {
            case MODBUS_IP:
                return ModbusTcpController.ASSETCATEGORY;
            case MODBUS_RTU:
                return ModbusRtuController.ASSETCATEGORY;
            case OPC_UA:
                return OpcUaController.ASSETCATEGORY;
            case OPC_XML_DA:
                return OpcXmlDaController.ASSETCATEGORY;
            case NIAGARA:
                return NiagaraController.ASSETCATEGORY;
            case BACNET_IP:
                return BacnetIpController.ASSETCATEGORY;
            case MISC:
                return FacilioConstants.ContextNames.MISC_CONTROLLER_MODULE_NAME;
            case REST:
                return FacilioConstants.ContextNames.REST_CONTROLLER_MODULE_NAME;
            case CUSTOM:
                return FacilioConstants.ContextNames.CUSTOM_CONTROLLER_MODULE_NAME;
            default:
                return null;
        }
    }


    public static boolean editController(long controllerId, JSONObject controllerData){
        Controller controller = getControllerFromDb(controllerId);
        if(controller != null){
            try {
                JSONObject jsonObject = new JSONObject();
                // EIDT controller
                jsonObject.put(AgentConstants.CONTROLLER,controllerData);
                AgentMessenger.publishNewIotAgentMessage(controller, FacilioCommand.EDIT_CONTROLLER,jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    static void updateController(Controller controller) {
        controller.setLastDataRecievedTime(System.currentTimeMillis());
        controller.setLastModifiedTime(controller.getLastDataRecievedTime());
        FacilioChain updateControllerChain = TransactionChainFactory.getUpdateControllerChain();
        FacilioContext context = updateControllerChain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD,controller);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,controller.getModuleName());
        List<Long> ids = new ArrayList<>();
        ids.add(controller.getId());
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,ids);
        try {
            LOGGER.info(" updating controller ");
            updateControllerChain.execute();
            LOGGER.info(" \nafter update  - " + context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
