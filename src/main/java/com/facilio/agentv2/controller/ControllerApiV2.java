package com.facilio.agentv2.controller;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.bacnet.BacnetIpControllerContext;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.agentv2.lonWorks.LonWorksControllerContext;
import com.facilio.agentv2.misc.MiscController;
import com.facilio.agentv2.modbusrtu.ModbusRtuControllerContext;
import com.facilio.agentv2.modbustcp.ModbusTcpControllerContext;
import com.facilio.agentv2.niagara.NiagaraControllerContext;
import com.facilio.agentv2.opcua.OpcUaControllerContext;
import com.facilio.agentv2.opcxmlda.OpcXmlDaControllerContext;
import com.facilio.agentv2.point.PointsAPI;
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
     *
     * @param controller
     * @return controllerId
     **/
    public static long addController(Controller controller) {
        try {
            long agentId = controller.getAgentId();
            if (agentId > 0) {
                FacilioAgent agent = AgentApiV2.getAgent(agentId);
                if (agent != null) {
                    return addController(controller, agent);
                } else {
                    throw new Exception(" No agent for id -> " + agentId);
                }
            } else {
                throw new Exception(" AgentId can't be less than 1 -> " + agentId);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
        return -1;
    }

    public static long addController(Controller controller, FacilioAgent agent) throws Exception {
        if (controller != null) {
            if (agent != null) {
                FacilioChain addControllerChain = TransactionChainFactory.getAddControllerChain();
                FacilioContext context = addControllerChain.getContext();
                String assetCategoryName = ControllerApiV2.getControllerModuleName(FacilioControllerType.valueOf(controller.getControllerType()));
                LOGGER.info(" controller " + FieldUtil.getAsJSON(controller));
                AssetCategoryContext asset = AssetsAPI.getCategory(assetCategoryName);
                controller.setCategory(asset);
                controller.setCreatedTime(System.currentTimeMillis());
                controller.setId(-1);
                controller.setActive(true);
                controller.setLastModifiedTime(System.currentTimeMillis());
                if ((agent.getSiteId() > 0)) {
                    controller.setSiteId(agent.getSiteId());
                } else {
                    throw new Exception(" agent's siteId can't be less than 1");
                }
                context.put(FacilioConstants.ContextNames.RECORD, controller);
                context.put(FacilioConstants.ContextNames.MODULE_NAME, controller.getModuleName());
                addControllerChain.execute();
                if (context.containsKey(FacilioConstants.ContextNames.RECORD_ID)) {
                    return (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
                } else {
                    throw new Exception(" controller added but context is missing controllerId ");
                }
            } else {
                throw new Exception(" agent can't be null ");
            }
        } else {
            throw new Exception(" controller can't be null ");
        }
    }

    /**
     * This method returs all controllers for an agent.
     * Iterates all the {@link com.facilio.bmsconsole.context.ControllerType} and gets respective controllers for an agentId.
     *
     * @param agentId
     * @return
     */
    public static Map<String, Controller> getControllersForAgent(long agentId, FacilioContext context) {
        Map<String, Controller> controllers = new HashMap<>();
        for (FacilioControllerType value : FacilioControllerType.values()) {
           // LOGGER.info(" getting controllers for agentId ->" + agentId + " and type " + value.asString());
            controllers.putAll(getControllersFromDb(null, agentId, value, -1, context));
        }
        return controllers;
    }

    /**
     * use in case of user requests
     *
     * @param controllerId
     * @param type
     * @return
     */
    public static Controller getControllerUsingIdAndType(long controllerId, FacilioControllerType type) {
        if ((controllerId > 0) && (type != null)) {
            Map<String, Controller> controllers = getControllersFromDb(null, -1, type, controllerId, null);
            for (Controller controller : controllers.values()) {
                if (controller.getId() == controllerId) {
                    return controller;
                }
            }
        }
        return null;
    }



    /**
     * this api is costly so avoid using it for frequent processes
     *
     * @param controllerId
     * @return
     */
    public static Controller getControllerFromDb(long controllerId) {
        Controller controller = null;
        if (controllerId > 0) {
            try {
                controller = getControllerContext(controllerId, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            LOGGER.info(" Exception Occurred, ControllerId can't be less than 1 ");
        }
        return controller;
    }

    public static Controller getControllerFromDb(JSONObject childJson, long agentId, FacilioControllerType controllerType) throws Exception {
       // LOGGER.info("controller identifier " + childJson);
        String controllerIdentifier = makeControllerFromMap(childJson,controllerType).getIdentifier();
        if (controllerType != null) {
            return getControllersFromDb(childJson, agentId, controllerType, -1, null).get(controllerIdentifier);
        }
        return getControllersForAgent(agentId, null).get(controllerIdentifier);
    }

    public static Map<String, Controller> getControllersFromDb(long agentId, FacilioControllerType controllerType, FacilioContext paginationContext) {
        if (controllerType == null) {
            return new HashMap<>();
        }
        return getControllersFromDb(null, agentId, controllerType, -1, paginationContext);
    }


    private static Controller getController(long controllerId, String moduleName) {
        if (controllerId > 0) {
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
     * @param agentId
     * @param controllerType
     * @param controllerId
     * @return
     */
    private static Map<String, Controller> getControllersFromDb(JSONObject childJson, long agentId, FacilioControllerType controllerType, long controllerId, FacilioContext paginationContext) {
        List<Controller> controllerList = getControllerListFromDb(childJson, agentId, controllerType, controllerId, paginationContext);
       // LOGGER.info(" controllers obtained is " + controllerList.size());
        Map<String, Controller> controllerMap = new HashMap<>();
        controllerList.forEach(controller -> {
            try {
                controllerMap.put(controller.getIdentifier(), controller);
            } catch (Exception e) {
                LOGGER.info("Exception while making identifier ", e);
            }
        });
        return controllerMap;
    }

    private static List<Controller> getControllerListFromDb(JSONObject childJson, long agentId, FacilioControllerType controllerType, long controllerId, FacilioContext paginationContext) {
        try {
            FacilioChain getControllerChain = getFetchControllerChain(controllerType, paginationContext);
            if (getControllerChain == null) {
                LOGGER.info(" controller chain cant be null ");
                return new ArrayList<>();
            }
            FacilioContext context = getControllerChain.getContext();
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.CONTROLLER_MODULE_NAME);
            if (fields == null || fields.isEmpty()) {
                return new ArrayList<>();
            }
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(fields);
            Criteria criteria = new Criteria();
            if (agentId > 0) {
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.AGENT_ID), String.valueOf(agentId), NumberOperators.EQUALS));
                context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, criteria);
            }

            if (controllerId > 0) {
                ArrayList<Long> idList = new ArrayList<>();
                idList.add(controllerId);
                context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, idList);
            }

            if ((childJson != null) && (!childJson.isEmpty())) {
                List<Condition> conditions = getControllerCondition(childJson, controllerType);
                if (!conditions.isEmpty()) {
                    criteria.addAndConditions(conditions);
                    context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, criteria);
                } else {
                    LOGGER.info(" controller condition for this type " + controllerType.asString() + " is missing ");
                }
            }
            getControllerChain.execute(context);
            List<Controller> controllers = (List<Controller>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
            if((controllers == null) || (controllers.isEmpty())){
                if(containsCheck("query",context)){
                    LOGGER.info(" get controller query --"+context.get("query"));
                }
            }
            return controllers;
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
        return new ArrayList<>();
    }

    public static Controller getController(long deviceId, FacilioControllerType controllerType) {
        try {
            Controller controller = fetchController(deviceId, controllerType);
            return controller;
        } catch (Exception e) {
            LOGGER.info("Exception while getting controller");
        }
        return null;
    }

    public static Controller fetchController(long deviceId, FacilioControllerType controllerType) throws Exception {
        FacilioChain getControllerChain = getFetchControllerChain(controllerType);
        FacilioContext context = getControllerChain.getContext();
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.CONTROLLER_MODULE_NAME);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
        if (fieldMap != null && (!fieldMap.isEmpty())) {

            if (fieldMap.containsKey(AgentConstants.DEVICE_ID)) {
                Criteria criteria = new Criteria();
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.DEVICE_ID), String.valueOf(deviceId), NumberOperators.EQUALS));
                context.put(FacilioConstants.ContextNames.FILTER_CRITERIA, criteria);
                getControllerChain.execute();
                List<Controller> controllers = (List<Controller>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
                if ((controllers != null) && (!controllers.isEmpty())) {
                    if (controllers.size() == 1) {
                        return controllers.get(0);
                    } else {
                        throw new Exception(" unexpected results, cant get more than one controller for deviceId->" + deviceId + " ,->" + controllers.size());
                    }
                } else {
                    throw new Exception("No such controller");
                }
            } else {
                throw new Exception(" deviceId field missing from controllerFields ->" + fieldMap.keySet());
            }

        } else {
            throw new Exception(" fields cant be null or empty to get controller ->" + fields);
        }
    }

    private static FacilioChain getFetchControllerChain(FacilioControllerType controllerType) throws Exception {
        return getFetchControllerChain(controllerType, new FacilioContext());
    }

    private static FacilioChain getFetchControllerChain(FacilioControllerType controllerType, FacilioContext paginationContext) throws Exception {
        if (controllerType == null) {
            throw new Exception(" controller type cant be null ");
        }
        FacilioChain getControllerChain = TransactionChainFactory.getControllerChain();
        FacilioContext context = getControllerChain.getContext();
        if (paginationContext != null) {
            context.putAll(paginationContext);
        }
        String moduleName = getControllerModuleName(controllerType);
        if (moduleName == null || moduleName.isEmpty()) {
            LOGGER.info("Exception Occurred, Module name is null or empty " + moduleName + "   for ");
            return null;
        }
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        return getControllerChain;
    }

    /**
     * Based on the controllerType it creates a dummy controller of a particular type using the identifier constructor and then uses the controller object to get uniques to build the condition.
     * <p>
     * ----IF controller type didn't match then empty conditions are sent back---
     *
     * @param controllerType
     * @return
     * @throws Exception
     */
    private static List<Condition> getControllerCondition(JSONObject childJson, FacilioControllerType controllerType) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<Condition> conditions = new ArrayList<>();
        conditions.addAll(makeControllerFromMap(childJson,controllerType).getControllerConditions());
        return conditions;
    }


    public static Controller makeControllerFromMap(Map<String, Object> map, FacilioControllerType controllerType) throws Exception {
        Controller controller;
        switch (controllerType) {
            case BACNET_IP:
                BacnetIpControllerContext.validateControllerJSON(map);
                controller = FieldUtil.getAsBeanFromMap(map, BacnetIpControllerContext.class);
                break;
            case MODBUS_RTU:
                controller = FieldUtil.getAsBeanFromMap(map, ModbusRtuControllerContext.class);
                break;
            case OPC_XML_DA:
                controller = FieldUtil.getAsBeanFromMap(map, OpcXmlDaControllerContext.class);
                break;
            case OPC_UA:
                controller = FieldUtil.getAsBeanFromMap(map, OpcUaControllerContext.class);
                break;
            case NIAGARA:
                controller = FieldUtil.getAsBeanFromMap(map, NiagaraControllerContext.class);
                break;
            case MODBUS_IP:
                controller = FieldUtil.getAsBeanFromMap(map, ModbusTcpControllerContext.class);
                break;
            case MISC:
                controller = FieldUtil.getAsBeanFromMap(map, MiscController.class);
                break;
            case CUSTOM:
                controller = FieldUtil.getAsBeanFromMap(map, CustomController.class);
                break;
            case LON_WORKS:
                controller = FieldUtil.getAsBeanFromMap(map, LonWorksControllerContext.class);
                break;
            case BACNET_MSTP:
            case KNX:
            default:
                throw new IllegalStateException("Unexpected value: " + controllerType);
        }
        if(controller != null){
            return controller;
        }else {
            throw new Exception(" controller not made type"+controllerType+"  map"+map);
        }
    }

    public static String getControllerModuleName(FacilioControllerType controllerType) {
        switch (controllerType) {
            case MODBUS_IP:
                return ModbusTcpControllerContext.ASSETCATEGORY;
            case MODBUS_RTU:
                return ModbusRtuControllerContext.ASSETCATEGORY;
            case OPC_UA:
                return OpcUaControllerContext.ASSETCATEGORY;
            case OPC_XML_DA:
                return OpcXmlDaControllerContext.ASSETCATEGORY;
            case NIAGARA:
                return NiagaraControllerContext.ASSETCATEGORY;
            case BACNET_IP:
                return BacnetIpControllerContext.ASSETCATEGORY;
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


    public static boolean editController(long controllerId, JSONObject controllerData) {
        LOGGER.info(" editing controller");
        Controller controller = getControllerFromDb(controllerId);
        if (controller != null) {
            try {
                JSONObject jsonObject = new JSONObject();
                JSONObject toUpdate = alterController(controller, controllerData);
                if (toUpdate.isEmpty()) {
                    return false;
                }
                updateController(controller);
                jsonObject.put(AgentConstants.CONTROLLER, toUpdate);
                AgentMessenger.publishNewIotAgentMessage(controller, FacilioCommand.EDIT_CONTROLLER, jsonObject);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static JSONObject alterController(Controller controller, JSONObject controllerData) {
        JSONObject toUpdate = new JSONObject();
        if (containsCheck(AgentConstants.DATA_INTERVAL, controllerData)) {
            controller.setDataInterval(((Number) controllerData.get(AgentConstants.DATA_INTERVAL)).longValue());
            toUpdate.put(AgentConstants.DATA_INTERVAL, controller.getDataInterval());
        }
        if (containsCheck(AgentConstants.WRITABLE, controllerData)) {
            controller.setWritable((Boolean) controllerData.get(AgentConstants.WRITABLE));
            toUpdate.put(AgentConstants.WRITABLE, controller.getWritable());
        }
        return toUpdate;
    }

    public static boolean containsCheck(String key, Map map) {
        if ((key != null) && (!key.isEmpty()) && (map != null) && (!map.isEmpty()) && (map.containsKey(key)) && (map.get(key) != null)) {
            return true;
        }
        return false;
    }

    static void updateController(Controller controller) {
        controller.setLastDataRecievedTime(System.currentTimeMillis());
        controller.setLastModifiedTime(controller.getLastDataRecievedTime());
        FacilioChain updateControllerChain = TransactionChainFactory.getUpdateControllerChain();
        FacilioContext context = updateControllerChain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD, controller);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, controller.getModuleName());
        List<Long> ids = new ArrayList<>();
        ids.add(controller.getId());
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
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
            if (orgId > 0) {
                return getCount(null);
            } else {
                LOGGER.info("Exception while getting controller count, orgId can't be null or empty  ->" + orgId);
            }
        } catch (Exception e) {
            LOGGER.info("Exception while getting controller count- ", e);
        }
        return 0;
    }

    public static long getCountForAgent(long agentId) {
        try {
            long orgId = AccountUtil.getCurrentAccount().getOrg().getOrgId();
            if (orgId > 0 && (agentId > 0)) {
                return getCount(agentId);
            } else {
                LOGGER.info("Exception while getting controller count, agentId and orgId can't be null or empty -> " + agentId + " ->" + orgId);
            }
        } catch (Exception e) {
            LOGGER.info("Exception while getting controller count- ", e);
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
                    .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(MODULE));
            if ((agentId != null) && (agentId > 0)) {
                LOGGER.info("applying agent filter");
                builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(MODULE), String.valueOf(agentId), NumberOperators.EQUALS));
            }
            List<Map<String, Object>> result = builder.get();
            if ((result != null) && (!result.isEmpty())) {
                return (long) result.get(0).get(AgentConstants.ID);
            }
        } catch (Exception e) {
            LOGGER.info("Exception while getting controller count ", e);
        }
        return 0;
    }

    public static boolean deleteControllerApi(Long id) throws SQLException {
        return deleteControllerApi(Collections.singletonList(id));
    }

    public static boolean deleteControllers(List<Long> ids) {
        try {
            return deleteControllerApi(ids);
        } catch (SQLException e) {
            LOGGER.info("Exception occurred while deleting controllers->" + ids + "  --  ", e);
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
                LOGGER.info(" rows affected in deleting controller -> " + context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
                LOGGER.info(" rows  in deleting controller -> " + context.get(FacilioConstants.ContextNames.RECORD_LIST));
                return (((Number) context.get(FacilioConstants.ContextNames.ROWS_UPDATED)).intValue() > 0);
            } catch (Exception e) {
                LOGGER.info("Exception while deleting controller ", e);
            }
        }
        return false;
    }

    public static boolean resetController(Long controllerId) throws Exception {
        FacilioChain chain = TransactionChainFactory.resetControllerChain();
        FacilioContext context = chain.getContext();
        context.put(AgentConstants.CONTROLLER_ID, controllerId);
        chain.execute();
        return true;
    }

    public static boolean checkForController(long controllerId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(MODULE.getTableName())
                .select(new ArrayList<>())
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(MODULE))
                .andCondition(CriteriaAPI.getIdCondition(controllerId, MODULE));
        List<Map<String, Object>> results = builder.get();
        if (!results.isEmpty()) {
            return (((Number) results.get(0).get(AgentConstants.ID)).intValue() > 0);
        }
        return false;
    }

    public static void resetConfiguredPoints(Long controllerId) throws Exception {
        if (checkForController(controllerId)) {
            PointsAPI.resetConfiguredPoints(controllerId);
        }

    }


    public static boolean checkForFieldDeviceController(long deviceId) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(MODULE.getTableName())
                .select(new ArrayList<>())
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(MODULE))
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getField(AgentConstants.DEVICE_ID, "DEVICE_ID", MODULE, FieldType.NUMBER), String.valueOf(deviceId), NumberOperators.EQUALS));
        List<Map<String, Object>> records = builder.get();
        if (!records.isEmpty()) {
            LOGGER.info(" resord " + records);
            long count = (long) records.get(0).get(AgentConstants.ID);
            LOGGER.info(" select device controller query -> " + builder.toString());
            return (count > 0);
        } else {
            LOGGER.info(" no rows selected ");
        }
        return false;

    }

    public static Map<Long, FacilioControllerType> getControllerIdsType(Long agentId) throws Exception {
        List<FacilioField> idTypefields = new ArrayList<>();
        idTypefields.add(FieldFactory.getIdField(MODULE));
        idTypefields.add(FieldFactory.getControllerTypeField(MODULE));
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(MODULE.getTableName())
                .select(idTypefields);
        if (agentId != null) {
            builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(MODULE), String.valueOf(agentId), NumberOperators.EQUALS));
        }
        //.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(FieldFactory.getPointFields()).get(AgentConstants.POINT_TYPE), String.valueOf(type.asInt()),NumberOperators.EQUALS));
        List<Map<String, Object>> results = builder.get();
        if ((results != null) && (!results.isEmpty())) {
            Map<Long, FacilioControllerType> ids = new HashMap<>();
            results.forEach(row -> ids.put((Long) row.get(AgentConstants.ID), FacilioControllerType.valueOf(Integer.parseInt(String.valueOf(row.get(AgentConstants.CONTROLLER_TYPE))))));
            return ids;
        }else {
            LOGGER.info(" result empty ");
        }
        return new HashMap<>();
    }

    public static Set<Long> getControllerIds(Long agentId) throws Exception {
        List<FacilioField> idTypefields = new ArrayList<>();
        idTypefields.add(FieldFactory.getIdField(MODULE));
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(MODULE.getTableName())
                .select(idTypefields);
        if (agentId != null) {
            builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(MODULE), String.valueOf(agentId), NumberOperators.EQUALS));
        }
        //.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(FieldFactory.getPointFields()).get(AgentConstants.POINT_TYPE), String.valueOf(type.asInt()),NumberOperators.EQUALS));
        List<Map<String, Object>> results = builder.get();
        if ((results != null) && (!results.isEmpty())) {
            Set<Long> ids = new HashSet<>();
            results.forEach(row -> ids.add((Long) row.get(AgentConstants.ID)));
            return ids;
        }else {
            LOGGER.info(" result empty ");
        }
        return new HashSet<>();
    }

    public static JSONObject getControllerCountData(Long agentId) throws Exception {
        JSONObject controlleCountData = new JSONObject();
        controlleCountData.put(AgentConstants.TOTAL_COUNT, getControllerIdsType(agentId).size());
        controlleCountData.put(AgentConstants.CONFIGURED_COUNT, FieldDeviceApi.getDeviceCount());
        return controlleCountData;
    }

    public static List<Map<String,Object>> getControllerData(Long controllerId, FacilioControllerType controllerType) throws Exception {
        FacilioChain getControllerChain = TransactionChainFactory.getControllerDataChain();
        String moduleName = getControllerModuleName(controllerType);
        System.out.println(" module name "+moduleName);
        FacilioContext context = getControllerChain.getContext();
        Criteria criteria = new Criteria();
        context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
        criteria.addAndCondition(CriteriaAPI.getIdCondition(controllerId,ModuleFactory.getNewControllerModule()));
        context.put(FacilioConstants.ContextNames.FILTER_CRITERIA,criteria);
        getControllerChain.execute();
        return (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
    }

    public static List<Map<String, Controller>> getControllerDataForAgent(Long agentId, FacilioContext paginationContext) throws Exception {
        List<Map<String, Controller>> controllersData  = new ArrayList<>();
        List<Map<String, Object>> controllerData = new ArrayList<>();
        for (FacilioControllerType controllerType : FacilioControllerType.values()) {
            FacilioChain getControllerChain = TransactionChainFactory.getControllerDataChain();
            String moduleName = getControllerModuleName(controllerType);
            if(moduleName == null){
                LOGGER.info(" module name is null for "+controllerType.asString());
                continue;
            }
            FacilioContext context = getControllerChain.getContext();
            context.putAll(paginationContext);
            context.put(FacilioConstants.ContextNames.MODULE_NAME,moduleName);
            context.put(AgentConstants.AGENT_ID,agentId);
            try {
                getControllerChain.execute();
                controllerData = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
            }catch (Exception e){
                LOGGER.info("Exception occurred while getting controller for "+agentId+ " of type "+agentId+" ",e);
                //  continue;
            }
            if(controllerData != null){
                controllersData.addAll(new ArrayList(controllerData));
            }
        }
        LOGGER.info(" returning data "+controllersData.size());
        return controllersData;
    }

    private static Controller getControllerContext(long controllerId, boolean fetchDeleted) throws Exception {
        FacilioChain assetDetailsChain = FacilioChainFactory.getControllerModuleName();
        FacilioContext context = assetDetailsChain.getContext();
        context.put(FacilioConstants.ContextNames.ID, controllerId);
        AssetContext asset = AssetsAPI.getAssetInfo(controllerId, true);
        if (asset == null) {
            throw new Exception("Asset can't be null");
        }
        AssetCategoryContext category = asset.getCategory();
        if (category != null && category.getId() != -1) {
            context.put(FacilioConstants.ContextNames.PARENT_CATEGORY_ID, category.getId());
        }
        assetDetailsChain.execute(context);
        String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
        return getController(controllerId, moduleName);
    }

    /*
    DO NOT CHANGE - - FOR TIME SERIES PURPOSE
     */
   /* public static Controller getControllerUsingControllerJSON(JSONObject controllerJSON, long agentId, FacilioControllerType controllerType) throws Exception {
        if (controllerType != null) {
            FacilioChain fetchControllerChain = getFetchControllerChain(controllerType);
            FacilioContext context = fetchControllerChain.getContext();
            context.put()

        }
        return null;
    }*/
}
