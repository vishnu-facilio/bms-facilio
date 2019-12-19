package com.facilio.agentv2.controller;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.bacnet.BacnetIpController;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.agentv2.misc.MiscController;
import com.facilio.agentv2.modbusrtu.ModbusRtuController;
import com.facilio.agentv2.modbustcp.ModbusTcpController;
import com.facilio.agentv2.niagara.NiagaraController;
import com.facilio.agentv2.opcua.OpcUaController;
import com.facilio.agentv2.opcxmlda.OpcXmlDaController;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.custom.CustomController;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.sql.SQLException;
import java.util.*;

public class ControllerApiV2 {
    private static final Logger LOGGER = LogManager.getLogger(ControllerApiV2.class.getName());

    private static final FacilioModule MODULE = ModuleFactory.getNewControllerModule();



    /**
     * This method adds controller to db verifying if it can be added.
     * AgentId from controller is used to check if such an agent exists and if yes, it gets the agent and uses its site id for controller(siteId is mandatory).
     * This method also takes care of createdTime, LastModifiedTime, sets category
     * @param controller
     * @return a {@link Controller} if all goes well else returns null
     **/
    public static long addController(Controller controller) {
        try {
            long agentId = controller.getAgentId();
            if( agentId > 0){
                FacilioAgent agent = AgentApiV2.getAgent(agentId);
                if(agent != null){
                    FacilioChain addControllerChain = TransactionChainFactory.getAddControllerChain();
                    FacilioContext context = addControllerChain.getContext();
                    String assetCategoryName = ControllerApiV2.getControllerModuleName(FacilioControllerType.valueOf(controller.getControllerType()));
                    AssetCategoryContext asset = AssetsAPI.getCategory(assetCategoryName);
                    controller.setCategory(asset);
                    controller.setCreatedTime(System.currentTimeMillis());
                    controller.setId(-1);
                    controller.setLastModifiedTime(System.currentTimeMillis());
                    controller.setSiteId(agent.getSiteId());
                    context.put(FacilioConstants.ContextNames.RECORD,controller);
                    context.put(FacilioConstants.ContextNames.MODULE_NAME,controller.getModuleName());
                    LOGGER.info(" module name is "+controller.getModuleName());
                    addControllerChain.execute();
                    return (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
                }else {
                    throw new Exception(" No agent for id -> "+agentId);
                }
            }else {
                throw new Exception(" AgentId can't be less than 1 -> "+agentId);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ",e);
        }
        return -1;
    }

    /**
     * This method returs all controllers for an agent.
     * Iterates all the {@link com.facilio.bmsconsole.context.ControllerType} and gets respective controllers for an agentId.
     * @param agentId
     * @return
     */
    public static Map<String, Controller> getAllControllersFromDb(long agentId) {
        LOGGER.info(" getting all controllers ");
        Map<String, Controller> controllers = new HashMap<>();
        for (FacilioControllerType value : FacilioControllerType.values()) {
            Map<String, Controller> controllersObtained = getControllersFromDb(null, agentId, value, -1);
            controllers.putAll(controllersObtained);
        }
        LOGGER.info(" returning controllers "+controllers.size());
        return controllers;
    }


    /**
     * use in case of user requests
     * @param controllerId
     * @param type
     * @return
     */
    public static Controller getControllerIdType(long controllerId,FacilioControllerType type){
        if( (controllerId > 0) && (type != null) ){
            Map<String, Controller> controllers = getControllersFromDb(null, -1, type, controllerId);
            LOGGER.info(" get controller using id and type -> "+controllers.size());
            for (Controller controller : controllers.values()) {
                if(controller.getId() == controllerId){
                    return controller;
                }
            }
        }
        return null;
    }



    private static Controller getControllerContext(long controllerId, boolean fetchDeleted) throws  Exception{
        LOGGER.info(" get controller context ");
        FacilioChain assetDetailsChain = FacilioChainFactory.getControllerModuleName();
        FacilioContext context = assetDetailsChain.getContext();
        context.put(FacilioConstants.ContextNames.ID, controllerId);
        AssetContext asset= AssetsAPI.getAssetInfo(controllerId, true);
        LOGGER.info(" asset ->"+asset);
        if(asset == null){
            return null;
        }
        AssetCategoryContext category= asset.getCategory();
        LOGGER.info(" category "+category.getName());
        if (category != null && category.getId() != -1) {
            context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, category.getId());
        }
        assetDetailsChain.execute(context);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        LOGGER.info(" module name "+moduleName);
        return getController(controllerId,moduleName);
    }

    /**
     * this api is costly so avoid using it for frequent processes
     * @param controllerId
     * @return
     */
    public static Controller getControllerFromDb(long controllerId){
        Controller controller = null;
        if(controllerId > 0){
            try {
                controller = getControllerContext(controllerId,false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            LOGGER.info(" Exception Occurred, ControllerId can't be less than 1 ");
        }
        return controller;
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


    private static Controller getController(long controllerId,String moduleName){
        if (controllerId > 0) {
            LOGGER.info(" getController ->"+controllerId);
            try {
                FacilioChain getControllerChain = TransactionChainFactory.getControllerChain();
                FacilioContext context = getControllerChain.getContext();
                context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                List<FacilioField> fields = modBean.getAllFields(moduleName);

                ArrayList<Long> idList = new ArrayList<>();
                idList.add(controllerId);
                context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, idList);

                getControllerChain.execute(context);
                List<Controller> controllers = (List<Controller>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
                LOGGER.info(" controllers size "+controllers.size());
                return controllers.get(0);
            } catch (Exception e) {
                LOGGER.info("Exception occurred ", e);
            }
        }
        return null;
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
        try {
            FacilioChain getControllerChain = getFetchControllerChain(controllerType);
            FacilioContext context = getControllerChain.getContext();
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.CONTROLLER_MODULE_NAME);
            if(fields == null || fields.isEmpty()){
                return controllerMap;
            }
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
                LOGGER.info(" controller obtained -> "+ FieldUtil.getAsJSON(controller));
                controllerMap.put(controller.makeIdentifier(),controller);
            }
            return controllerMap;
        } catch (Exception e) {
            LOGGER.info("Exception occurred ",e);
        }
        return new HashMap<>();
    }

    private static FacilioChain getFetchControllerChain(FacilioControllerType controllerType) throws Exception {
        if(controllerType == null){
            throw new Exception(" controller type cant be null ");
        }
        FacilioChain getControllerChain = TransactionChainFactory.getControllerChain();
        FacilioContext context = getControllerChain.getContext();
        String moduleName = getControllerModuleName(controllerType);
        if(moduleName == null || moduleName.isEmpty()){
            throw new Exception("Exception Occurred, Module name is null or empty "+moduleName);
        }
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        return getControllerChain;
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
        LOGGER.info(" editing controller");
        Controller controller = getControllerFromDb(controllerId);
        if(controller != null){
            try {
                JSONObject jsonObject = new JSONObject();
                JSONObject toUpdate = alterController(controller, controllerData);
                if (toUpdate.isEmpty()){
                    return false;
                }
                updateController(controller);
                jsonObject.put(AgentConstants.CONTROLLER,toUpdate);
                AgentMessenger.publishNewIotAgentMessage(controller, FacilioCommand.EDIT_CONTROLLER,jsonObject);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static JSONObject alterController(Controller controller, JSONObject controllerData) {
        JSONObject toUpdate = new JSONObject();
        if(containsCheck(AgentConstants.DATA_INTERVAL,controllerData)){
            controller.setDataInterval(((Number)controllerData.get(AgentConstants.DATA_INTERVAL)).longValue());
            toUpdate.put(AgentConstants.DATA_INTERVAL,controller.getDataInterval());
        }
        if(containsCheck(AgentConstants.WRITABLE,controllerData)){
            controller.setWritable((Boolean) controllerData.get(AgentConstants.WRITABLE));
            toUpdate.put(AgentConstants.WRITABLE,controller.getWritable());
        }
        return toUpdate;
    }

    public static boolean containsCheck(String key, Map map){
        if( (key != null) && ( ! key.isEmpty()) && ( map != null ) && ( ! map.isEmpty() ) && (map.containsKey(key)) && (map.get(key) != null) ){
            return true;
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

    public static long getCountForOrg() {
        try {
            long orgId = AccountUtil.getCurrentAccount().getOrg().getOrgId();
            if(orgId>0){
                return  getCount(null);
            }else {
                LOGGER.info("Exception while getting controller count, orgId can't be null or empty  ->"+orgId);
            }
        }catch (Exception e){
            LOGGER.info("Exception while getting controller count- ",e);
        }
        return 0;
    }

    public static long getCountForAgent(long agentId) {
        try {
            long orgId = AccountUtil.getCurrentAccount().getOrg().getOrgId();
            if(orgId>0 && (agentId>0) ){
                return  getCount(agentId);
            }else {
                LOGGER.info("Exception while getting controller count, agentId and orgId can't be null or empty -> "+agentId+" ->"+orgId);
            }
        }catch (Exception e){
            LOGGER.info("Exception while getting controller count- ",e);
        }
        return 0;
    }


    private static long getCount(Long agentId) {
        try {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = modBean.getAllFields("Controller");
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(MODULE.getTableName())
                    .select(new HashSet<>())
                    .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT,FieldFactory.getIdField(MODULE));
            if( (agentId != null) && (agentId > 0) ){
                LOGGER.info("applying agent filter");
                builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(MODULE), String.valueOf(agentId),NumberOperators.EQUALS));
            }
            List<Map<String, Object>> result = builder.get();
            if((result!= null) && ( ! result.isEmpty())){
                return (long) result.get(0).get(AgentConstants.ID);
            }
        }catch (Exception e){
            LOGGER.info("Exception while getting controller count ",e);
        }
        return 0;
    }

    public static boolean deleteControllerApi(Long id)throws SQLException {
        return deleteControllerApi(Collections.singletonList(id));
    }

    public static boolean deleteControllers(List<Long> ids){
        try{
            return deleteControllerApi(ids);
        } catch (SQLException e) {
            LOGGER.info("Exception occurred while deleting controllers->"+ids+"  --  ",e);
        }
        return false;
    }
    public static boolean deleteControllerApi(List<Long> ids) throws SQLException {
        if ((ids != null) && (!ids.isEmpty())) {
            FacilioChain deleteChain = TransactionChainFactory.deleteControllerChain();
            FacilioContext context = deleteChain.getContext();
            context.put(FacilioConstants.ContextNames.IS_MARK_AS_DELETE, true);
            context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
            context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.CONTROLLER_ASSET);
            /*GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(MODULE.getTableName())
                    .fields(Collections.singletonList(FieldFactory.getDeletedTimeField(MODULE)))
                    .andCondition(CriteriaAPI.getIdCondition(ids, MODULE));
            int rowsAffected = builder.update(Collections.singletonMap(AgentConstants.DELETED_TIME, System.currentTimeMillis()));
            if (rowsAffected > 0) {
                return true;
            } else {
                LOGGER.info("Controller deletion failed, rows affected -> " + rowsAffected);
            }*/
            try {
                deleteChain.execute();
                LOGGER.info(" rows affected in deleting controller -> "+context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
                LOGGER.info(" rows  in deleting controller -> "+context.get(FacilioConstants.ContextNames.RECORD_LIST));
                return (((Number)context.get(FacilioConstants.ContextNames.ROWS_UPDATED)).intValue()>0);
            }catch (Exception e){
                LOGGER.info("Exception while deleting controller ",e);
            }
        }
        return false;
    }

    public static void resetController(Long controllerId) {
        FacilioChain chain = TransactionChainFactory.resetControllerChain();
        FacilioContext context = chain.getContext();
        context.put(AgentConstants.CONTROLLER_ID,controllerId);
        try {
            chain.execute();
        }catch (Exception e){
            LOGGER.info("Exception occurred while reset-controller ",e);
        }
    }
}
