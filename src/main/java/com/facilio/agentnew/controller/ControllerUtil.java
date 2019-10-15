package com.facilio.agentnew.controller;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentnew.AgentConstants;
import com.facilio.agentnew.bacnet.BacnetIpController;
import com.facilio.agentnew.device.DeviceUtil;
import com.facilio.agentnew.modbusrtu.ModbusRtuController;
import com.facilio.agentnew.modbustcp.ModbusTcpController;
import com.facilio.agentnew.niagara.NiagaraController;
import com.facilio.agentnew.opcua.OpcUaController;
import com.facilio.agentnew.opcxmlda.OpcXmlDaController;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.util.AssetsAPI;
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
import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerUtil
{

    private static final Logger LOGGER = LogManager.getLogger(ControllerUtil.class.getName());
    private long ogdId;
    private long agentId;
    List<Map<String, Controller>> controllerMapList = new ArrayList<>();


    public ControllerUtil(long agentId) {
        this.ogdId = AccountUtil.getCurrentOrg().getOrgId();
        this.agentId = agentId;
        loadControllerListMap();
    }

    /**
     * This method iterates {@link FacilioControllerType} values and loads controllers of respective types to the controllerMapList
     */
    private void loadControllerListMap() {
        for (FacilioControllerType value : FacilioControllerType.values()) {
            controllerMapList.add(new HashMap<>());
        }
    }

    /**
     *
     * if controllerType null
     *    load controllers for all {@link FacilioControllerType}
     *
     * map empty
     *   if map is empty - loading for the first time and so get all controllers for the type using agentId and controllertype.asInt
     *      if no controllers loaded, return null
     *
     * map not empty
     *    look for controller using identifier
     *          if present - return
     *
     *          else
     *              get that particular controller using identifier and type
     *                  if this is null -  return null.
     *
     * @param agentId
     * @param controllerIdentifier
     * @param controllerType
     * @return
     */
    public Controller getController(long agentId, String controllerIdentifier, FacilioControllerType controllerType){
        LOGGER.info(" getting controller type-"+controllerType.asInt()+"  agentId-"+agentId+" controller identifier "+controllerIdentifier);
        // avoids null pointer
        if( (controllerMapList.get(controllerType.asInt()) == null) ){
            controllerMapList.get(controllerType.asInt()).putAll(new HashMap<>());
        }
        // map empty
        if(controllerType == null){
            return getAllControllersFromDb(agentId).get(controllerIdentifier);
        }
        else if( (controllerMapList.get(controllerType.asInt()).isEmpty()) ){
            LOGGER.info("controller map for "+controllerType.asString()+" empty and so  loading all controllers of that kind");
            Map<String, Controller> controllers = getTypeControllersFromDb(agentId,controllerType);
            if(controllers.isEmpty()){
                LOGGER.info("Exception Occurred, No such controller for agent " + agentId + ", with identifier " + controllerIdentifier + " of type " + controllerType.asString());
                return null;
            }
            for (String key : controllers.keySet()) {
                    controllerMapList.get(controllers.get(key).getControllerType()).put(key,controllers.get(key));
            }
            //controllerListMap.get(controllerType.asInt()).putAll();
            LOGGER.info("\n\n\n\n\n\n LOADED ALL CONTROLLERS \n\n\n\n");
            for (Map<String, Controller> controllerMap : controllerMapList) {
                LOGGER.info(controllerMap.keySet());
            }
            return controllerMapList.get(controllerType.asInt()).get(controllerIdentifier);
        }else {
            LOGGER.info(" controller map not empty");
            LOGGER.info(" controller map -> "+controllerMapList.get(controllerType.asInt()));
            if(controllerMapList.get(controllerType.asInt()).containsKey(controllerIdentifier)){
                LOGGER.info(" controller map contains the controller ");
                return controllerMapList.get(controllerType.asInt()).get(controllerIdentifier);
            }
            else {
                LOGGER.info("controller not present in the map and getting particular controller"+controllerIdentifier+"-"+controllerType.asString()+"  from db");
                Controller controller = getControllerFromDb(controllerIdentifier,agentId,controllerType);
                if(controller != null){
                    try {
                        controllerMapList.get(controllerType.asInt()).put(controller.makeIdentifier(),controller);
                        return controller;
                    } catch (Exception e) {
                        LOGGER.info("Exception occured, cant generate identifier");
                    }
                }
                LOGGER.info("Exception Occurred, No such controller for agent " + agentId + ", with identifier " + controllerIdentifier);
                return null;
            }
        }
    }

    // pakka
    private Map<String, Controller> getAllControllersFromDb(long agentId) {
        LOGGER.info(" getting all controllers ");
        Map<String, Controller> controllers = new HashMap<>();
        for (FacilioControllerType value : FacilioControllerType.values()) {
            LOGGER.info(" restoring " + value.asString() + " controllers ");
            controllers.putAll(getControllersFromDb(null,agentId,value));
        }
        return controllers;
    }

    private Controller getControllerFromDb(String controllerIdentifier, long agentId, FacilioControllerType controllerType) {
        if( controllerType != null){
            return getControllersFromDb(controllerIdentifier,agentId,controllerType).get(controllerIdentifier);
        }
        return getAllControllersFromDb(agentId).get(controllerIdentifier);
    }

    private Map<String, Controller> getTypeControllersFromDb(long agentId, FacilioControllerType controllerType){
        return getControllersFromDb(null,agentId,controllerType);
    }


    private Map<String, Controller> getControllersFromDb(String controllerIdentifier, long agentId, FacilioControllerType controllerType) {

        FacilioChain getControllerChain = TransactionChainFactory.getControllerChain();
        FacilioContext context = new FacilioContext();
        String moduleName = getControllerModuleName(controllerType);
        if(moduleName == null || moduleName.isEmpty()){
            return new HashMap<>();
        }
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = modBean.getAllFields(moduleName);
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.AGENT_ID), String.valueOf(agentId), NumberOperators.EQUALS));
            if( (controllerIdentifier != null) && ( ! controllerIdentifier.isEmpty() ) ){
                criteria.addAndConditions(getControllerCondition(controllerIdentifier,controllerType));
            }
            if(controllerType == null){
                throw  new Exception("Exception occurred , Controller type can't be null");
            }
            context.put(FacilioConstants.ContextNames.FILTER_CRITERIA,criteria);
            getControllerChain.execute(context);
            List<Controller> controllers = (List<Controller>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
            LOGGER.info(" controllers from db "+controllers.size());
            Map<String, Controller> controllerMap = new HashMap<>();
            for (Controller controller : controllers) {
                controllerMap.put(controller.makeIdentifier(),controller);
            }
            return controllerMap;
        } catch (Exception e) {
           LOGGER.info("Exception occurred ",e);
        }
        return new HashMap<>();
    }

    private List<Condition> getControllerCondition(String controllerIdentifier, FacilioControllerType controllerType) throws Exception {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = modBean.getAllFields(getControllerModuleName(controllerType));
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
            List<Condition> conditions = new ArrayList<>();
            switch (controllerType) {
                case BACNET_IP:
                    BacnetIpController controller = new BacnetIpController(-1,-1,controllerIdentifier);
                    conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.NETWORK_NUMBER), String.valueOf(controller.getNetworkNumber()),NumberOperators.EQUALS));
                    conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.INSTANCE_NUMBER), String.valueOf(controller.getInstanceNumber()),NumberOperators.EQUALS));
                    conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.IP_ADDRESS),controller.getIpAddress(), StringOperators.IS));
                    break;
                case MODBUS_RTU:
                    ModbusRtuController modbusRtuController = new ModbusRtuController(-1,-1,controllerIdentifier);
                    conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.SLAVE_ID), String.valueOf(modbusRtuController.getSlaveId()),NumberOperators.EQUALS));
                    conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.NETWORK_ID), String.valueOf(modbusRtuController.getNetworkId()),NumberOperators.EQUALS));
                    break;
                case OPC_XML_DA:
                    OpcXmlDaController opcXmlDaController = new OpcXmlDaController(-1,-1,controllerIdentifier);
                    conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.USER_NAME),opcXmlDaController.getUserName(),StringOperators.IS));
                    conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.URL),opcXmlDaController.getUrl(),StringOperators.IS));
                    conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.PASSWORD),opcXmlDaController.getPassword(),StringOperators.IS));
                    break;
                case OPC_UA:
                    OpcUaController opcUaController = new OpcUaController(-1,-1,controllerIdentifier);
                    conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.URL),opcUaController.getUrl(),StringOperators.IS));
                    conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.CERT_PATH),opcUaController.getCertPath(),StringOperators.IS));
                    break;
                case NIAGARA:
                    NiagaraController niagaraController = new NiagaraController(-1,-1,controllerIdentifier);
                    conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.PORT_NUMBER), String.valueOf(niagaraController.getPortNumber()),NumberOperators.EQUALS));
                    conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.IP_ADDRESS),niagaraController.getIpAddress(),StringOperators.IS));
                    break;
            }
            return conditions;
    }

    public boolean processController(long agentId, List<Long> ids) {
        List<Map<String, Object>> devices = DeviceUtil.getDevices(agentId, ids);
        Controller controller = null;
        for (Map<String, Object> device : devices) {
            try {
                JSONParser parser = new JSONParser();
                controller = getControllerFromJSON(agentId, (JSONObject)parser.parse(String.valueOf(device.get(AgentConstants.CONTROLLER_PROPS))));
                Controller controllerFromDb  = getController(agentId,controller.makeIdentifier(),FacilioControllerType.valueOf(controller.getControllerType()));
                if(controllerFromDb != null){
                    controller.setId(controllerFromDb.getId());
                    updateController(controller);
                }else {
                    long controllerId = addController(controller);
                }
            } catch (Exception e) {
                LOGGER.info("Exception occurred ",e);
            }
        }

      return false;
    }

    private void updateController(Controller controller) {
        controller.setLastDataRecievedTime(System.currentTimeMillis());
        controller.setLastModifiedTime(controller.getLastDataRecievedTime());
        Chain updateControllerChain = TransactionChainFactory.getUpdateControllerChain();
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD,controller);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,controller.getModuleName());
        List<Long> ids = new ArrayList<>();
        ids.add(controller.getId());
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,ids);
        try {
            LOGGER.info(" updating controller ");
            updateControllerChain.execute(context);
            LOGGER.info(" \nafter update  - " + context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long addController(Controller controller) {
        try {
            Chain addControllerChain = TransactionChainFactory.getAddControllerChain();
            FacilioContext context = new FacilioContext();
            AssetCategoryContext asset = AssetsAPI.getCategory(getControllerModuleName(FacilioControllerType.valueOf(controller.getControllerType())));
            controller.setCategory(asset);
            controller.setSiteId(2);
            controller.setCreatedTime(System.currentTimeMillis());
            context.put(FacilioConstants.ContextNames.RECORD,controller);
            context.put(FacilioConstants.ContextNames.MODULE_NAME,controller.getModuleName());
            if (addControllerChain.execute(context)) {
                return (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ",e);
        }
        return -1;
    }

    private Controller getControllerFromJSON(long agentId, Map<String,Object> controllerJSON) throws Exception {
        if(controllerJSON == null || controllerJSON.isEmpty()){
            throw new Exception("ControllerJson can't be null or empty -> "+controllerJSON);
        }
        if(containsValueCheck(AgentConstants.CONTROLLER_TYPE,controllerJSON)){
            FacilioControllerType controllerType = FacilioControllerType.valueOf(Math.toIntExact((Long) controllerJSON.get(AgentConstants.CONTROLLER_TYPE)));
            switch (controllerType){
                case NIAGARA:
                    return NiagaraController.getNiagaraControllerFromMap(agentId,controllerJSON);
                case BACNET_IP:
                    return BacnetIpController.getBacnetControllerFromMap(agentId,controllerJSON);
                case OPC_XML_DA:
                    return OpcXmlDaController.getPpcXmlDaControllerFromMap(agentId,controllerJSON);
                case BACNET_MSTP:
                    return null;
                case MODBUS_RTU:
                    return ModbusRtuController.getModbusRtuControllerFromMap(agentId,controllerJSON);
                case MODBUS_IP:
                    return ModbusTcpController.getModbusTcpControllerFromMap(controllerJSON);
                case LON_WORKS:
                    throw new Exception(" No implementation for "+FacilioControllerType.LON_WORKS.asString()+" controller");
                case KNX:
                    throw new Exception(" No implementation for "+FacilioControllerType.KNX.asString()+" controller");
                case OPC_UA:
                    return OpcUaController.getBacnetControllerFromMap(agentId,controllerJSON);
                case MISC:
                    throw new Exception(" No implementation for "+FacilioControllerType.MISC.asString()+" controller");
            }
        }else {
            throw new Exception("Controller Type missing");
        }
        throw  new Exception(" No match found for controller data");
    }


    private static boolean containsValueCheck(String key,Map<String,Object> jsonObject){
        return jsonObject.containsKey(key) && (jsonObject.get(key) != null);
    }

    private static String getControllerModuleName(FacilioControllerType controllerType){
    switch (controllerType) {
        case MODBUS_IP:
            return ModbusTcpController.ASSETCATEGORY;
        case MODBUS_RTU:
            return ModbusRtuController.ASSETCATEGORY;
        case LON_WORKS:
            return FacilioConstants.ContextNames.LON_WORKS_CONTROLLER_MODULE_NAME;
        case OPC_UA:
            return OpcUaController.ASSETCATEGORY;
        case OPC_XML_DA:
            return OpcXmlDaController.ASSETCATEGORY;
        case NIAGARA:
            return NiagaraController.ASSETCATEGORY;
        case BACNET_IP:
            return BacnetIpController.ASSETCATEGORY;
        default:
            return null;
    }
}


 public JSONArray getAllControllerList(){
        JSONArray controllerArray = new JSONArray();
     Map<String, Controller> controllers = new HashMap<>();
     for (FacilioControllerType type : FacilioControllerType.values()) {
         controllers.putAll(getControllersFromDb(null, agentId, type));
     }
     for (String controllerIdentifier : controllers.keySet()) {
         controllerArray.add(controllers.get(controllerIdentifier).toJSON());
     }
        return controllerArray;
 }

}
