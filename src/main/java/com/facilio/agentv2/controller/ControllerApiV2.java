package com.facilio.agentv2.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.actions.GetPointsAction;
import com.facilio.agentv2.bacnet.BacnetIpControllerContext;
import com.facilio.agentv2.device.Device;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.agentv2.lonWorks.LonWorksControllerContext;
import com.facilio.agentv2.misc.MiscController;
import com.facilio.agentv2.modbusrtu.ModbusRtuControllerContext;
import com.facilio.agentv2.modbusrtu.RtuNetworkContext;
import com.facilio.agentv2.modbustcp.ModbusTcpControllerContext;
import com.facilio.agentv2.niagara.NiagaraControllerContext;
import com.facilio.agentv2.opcua.OpcUaControllerContext;
import com.facilio.agentv2.opcxmlda.OpcXmlDaControllerContext;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.agentv2.system.SystemControllerContext;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.custom.CustomController;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.BmsAggregateOperators;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class ControllerApiV2 {
    private static final Logger LOGGER = LogManager.getLogger(ControllerApiV2.class.getName());


    /**
     * This method adds controller to db verifying if it can be added.
     * AgentId from controller is used to check if such an agent exists and if yes, it gets the agent and uses its site id for controller(siteId is mandatory).
     * This method also takes care of createdTime, LastModifiedTime, sets category
     *
     * @param controller
     * @return controllerId
     **/

    private static final FacilioModule CONTROLLER_MODULE = ModuleFactory.getNewControllerModule();
    private static final FacilioModule RESOURCE_MODULE = ModuleFactory.getResourceModule();

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
                if(controller.getControllerType() == FacilioControllerType.SYSTEM.asInt()){
                    context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
                }
                if (controller.getControllerType() == FacilioControllerType.MODBUS_RTU.asInt()) {
                    RtuNetworkContext rtuNetworkContext = ((ModbusRtuControllerContext) controller).getNetwork();
                    rtuNetworkContext.setAgentId(agent.getId());
                    context.put(FacilioConstants.ContextNames.RTU_NETWORK, rtuNetworkContext);
                }
                String assetCategoryName = ControllerApiV2.getControllerModuleName(FacilioControllerType.valueOf(controller.getControllerType()));
                AssetCategoryContext asset = AssetsAPI.getCategory(assetCategoryName);
                controller.setCategory(asset);
                if (controller.getCreatedTime() < 100) {
                    controller.setCreatedTime(System.currentTimeMillis());
                }
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
     * this api is costly so avoid using it for frequent processes
     * <p>
     * working
     *
     * @param controllerId
     * @return
     */
    public static Controller getControllerFromDb(long controllerId) throws Exception {
        Controller controller = null;
        if (controllerId > 0) {
            GetControllerRequest getControllerRequest = new GetControllerRequest()
                    .withControllerId(controllerId);
            controller = getControllerRequest.getController();
            if (controller != null) {
                return controller;
            } else {
                throw new Exception(" controller not found for controllerId " + controllerId);
            }
        } else {
            throw new Exception(" Exception Occurred, ControllerId can't be less than 1 ");
        }
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
    public static List<Condition> getControllerCondition(JSONObject childJson, FacilioControllerType controllerType) throws Exception {
        List<Condition> conditions = new ArrayList<>();
        Controller controller = makeControllerFromMap(childJson, controllerType);
        if (AccountUtil.getCurrentOrg().getOrgId() == 152) {
            LOGGER.info("controller : " + controller.getName() + " : " + controller.getType());
        }
        conditions.addAll(controller.getControllerConditions());
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
                ModbusRtuControllerContext rtuController = FieldUtil.getAsBeanFromMap(map, ModbusRtuControllerContext.class);
                JSONObject network = (JSONObject) map.get(AgentConstants.NETWORK);
                if (network != null) {
                    rtuController.setNetwork(FieldUtil.getAsBeanFromJson(network, RtuNetworkContext.class));
                } else {
                    RtuNetworkContext rtuNetworkContext = FieldUtil.getAsBeanFromMap(map, RtuNetworkContext.class);
                    rtuController.setNetwork(rtuNetworkContext);
                }
                controller = rtuController;
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
            case SYSTEM:
                controller = FieldUtil.getAsBeanFromMap(map, SystemControllerContext.class);
                break;
            case BACNET_MSTP:
            case KNX:
            default:
                throw new IllegalStateException("Unexpected value: " + controllerType);
        }
        if (controller != null) {
            return controller;
        } else {
            throw new Exception(" controller not made type" + controllerType + "  map" + map);
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
            case SYSTEM:
                return FacilioConstants.ContextNames.SYSTEM_CONTROLLER_MODULE_NAME;
            case LON_WORKS:
            	return FacilioConstants.ContextNames.LON_WORKS_CONTROLLER_MODULE_NAME;
            default:
                return null;
        }
    }


    public static boolean editController(long controllerId, JSONObject controllerData) throws Exception {
        LOGGER.info(" editing controller");
        Controller controller = getControllerFromDb(controllerId);
        if (controller != null) {
            JSONObject jsonObject = new JSONObject();
            JSONObject toUpdate = alterController(controller, controllerData);
            if (toUpdate.isEmpty()) {
                return false;
            }
            updateController(controller);
            jsonObject.put(AgentConstants.CONTROLLER, toUpdate);
            AgentMessenger.publishNewIotAgentMessage(controller, FacilioCommand.EDIT_CONTROLLER, jsonObject);
            if (containsCheck(AgentConstants.WRITABLE, controllerData)){
            	FacilioChain chain = TransactionChainFactory.getEditPointChain();
            	chain.getContext().put(AgentConstants.WRITABLE, (Boolean) controllerData.get(AgentConstants.WRITABLE));
            	chain.getContext().put(AgentConstants.CONTROLLER_ID, controllerId);
            	chain.execute();
            }
            return true;
        } else {
            throw new Exception(" controller not foung ");
        }
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
                return getCount(null,null,null);
            } else {
                LOGGER.info("Exception while getting controller count, orgId can't be null or empty  ->" + orgId);
            }
        } catch (Exception e) {
            LOGGER.info("Exception while getting controller count- ", e);
        }
        return 0;
    }

    public static long getCountForAgent(List<Long> agentIds) {
        try {
            long orgId = AccountUtil.getCurrentAccount().getOrg().getOrgId();
            if (orgId > 0 && (agentIds != null) && (!agentIds.isEmpty())) {
                return getCount(agentIds,null,null);
            } else {
                LOGGER.info("Exception while getting controller count, agentId and orgId can't be null or empty -> " + agentIds + " ->" + orgId);
            }
        } catch (Exception e) {
            LOGGER.info("Exception while getting controller count- ", e);
        }
        return 0;
    }
    
    public static long getControllersCount(FacilioContext context) {
    	Long agentIds = (Long) context.get(AgentConstants.AGENT_ID);
    	Integer controllerType = (Integer) context.get(AgentConstants.CONTROLLER_TYPE);
    	return getCount(Collections.singletonList(agentIds), controllerType,(String)context.get(AgentConstants.SEARCH_KEY));
    }

    private static long getCount(List<Long> agentIds,Integer controllerType , String querySearch) {
        try {
            GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                    .table(CONTROLLER_MODULE.getTableName())
                    .select(new HashSet<>())
                    .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(CONTROLLER_MODULE))
                    .innerJoin(RESOURCE_MODULE.getTableName()).on(RESOURCE_MODULE.getTableName() + ".ID=" + CONTROLLER_MODULE.getTableName() + ".ID")
                    .andCondition(CriteriaAPI.getCondition(FieldFactory.getSysDeletedTimeField(RESOURCE_MODULE), "NULL", CommonOperators.IS_EMPTY));
            if ((agentIds != null) && (!agentIds.isEmpty())) {
                builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(CONTROLLER_MODULE), agentIds, NumberOperators.EQUALS));
            }
            if(controllerType != null) {
            	builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getControllerTypeField(CONTROLLER_MODULE), String.valueOf(controllerType), NumberOperators.EQUALS));
            }
            if(querySearch != null && !querySearch.trim().isEmpty()) {
            	builder.andCustomWhere(RESOURCE_MODULE.getTableName()+".NAME = ?  OR  "+RESOURCE_MODULE.getTableName()+".NAME LIKE ?",querySearch,"%"+querySearch+"%");
            }
            return (long) builder.fetchFirst().getOrDefault(AgentConstants.ID, 0L);
        } catch (Exception e) {
            LOGGER.info("Exception while getting controller count ", e);
        }
        return 0L;
    }

    public static boolean deleteControllerApi(Long id) throws Exception {
        return deleteControllerApi(Collections.singletonList(id));
    }

    public static boolean deleteControllerApi(List<Long> ids) throws Exception {
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
            deleteChain.execute();
            LOGGER.info(" rows affected in deleting controller -> " + context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
            LOGGER.info(" rows  in deleting controller -> " + context.get(FacilioConstants.ContextNames.RECORD_LIST));
            return (((Number) context.get(FacilioConstants.ContextNames.ROWS_UPDATED)).intValue() > 0);
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
                .table(CONTROLLER_MODULE.getTableName())
                .select(new ArrayList<>())
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FieldFactory.getIdField(CONTROLLER_MODULE))
                .andCondition(CriteriaAPI.getIdCondition(controllerId, CONTROLLER_MODULE));
        if (CollectionUtils.isNotEmpty(builder.get())) {
            return true;
        }
        return false;
    }

    public static void resetConfiguredPoints(Long controllerId) throws Exception {
        if (checkForController(controllerId)) {
            PointsAPI.resetConfiguredPoints(controllerId);
        }

    }

    public static Map<Long, FacilioControllerType> getControllerIdsType(Long agentId) throws Exception {
        FacilioModule controllerModule = ModuleFactory.getNewControllerModule();
        List<FacilioField> idTypefields = new ArrayList<>();
        idTypefields.add(FieldFactory.getIdField(controllerModule));
        idTypefields.add(FieldFactory.getControllerTypeField(controllerModule));
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(controllerModule.getTableName())
                .select(idTypefields);
        if (agentId != null) {
            builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(controllerModule), String.valueOf(agentId), NumberOperators.EQUALS));
        }
        //.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(FieldFactory.getPointFields()).get(AgentConstants.POINT_TYPE), String.valueOf(type.asInt()),NumberOperators.EQUALS));
        List<Map<String, Object>> results = builder.get();
        if ((results != null) && (!results.isEmpty())) {
            Map<Long, FacilioControllerType> ids = new HashMap<>();
            results.forEach(row -> ids.put((Long) row.get(AgentConstants.ID), FacilioControllerType.valueOf(Integer.parseInt(String.valueOf(row.get(AgentConstants.CONTROLLER_TYPE))))));
            return ids;
        } else {
            LOGGER.info(" result empty ");
        }
        return new HashMap<>();
    }

    public static Set<Long> getControllerIds(List<Long> agentId) throws Exception {
        FacilioModule controllerModule = ModuleFactory.getNewControllerModule();
        List<FacilioField> idTypefields = new ArrayList<>();
        idTypefields.add(FieldFactory.getIdField(controllerModule));
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(controllerModule.getTableName())
                .select(idTypefields);
        if (agentId != null) {
            builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(controllerModule), agentId, NumberOperators.EQUALS));
        }
        //.andCondition(CriteriaAPI.getCondition(FieldFactory.getAsMap(FieldFactory.getPointFields()).get(AgentConstants.POINT_TYPE), String.valueOf(type.asInt()),NumberOperators.EQUALS));
        List<Map<String, Object>> results = builder.get();
        LOGGER.info("controller query "+builder.toString());
        if ((results != null) && (!results.isEmpty())) {
            Set<Long> ids = new HashSet<>();
            results.forEach(row -> ids.add((Long) row.get(AgentConstants.ID)));
            return ids;
        } else {
            LOGGER.info(" result empty ");
        }
        return new HashSet<>();
    }

    public static JSONObject getControllerCountData(List<Long> agentIds) throws Exception {
        JSONObject controlleCountData = new JSONObject();
        controlleCountData.put(AgentConstants.CONFIGURED_COUNT, getCount(agentIds,null,null));
        return controlleCountData;
    }

    public static JSONObject getControllerCountData(Long agentId) throws Exception {
        return getControllerCountData(Arrays.asList(agentId));
    }

    public static List<Map<String, Object>> getControllerDataForAgent(FacilioContext contextProps) throws Exception {
    	Long agentId = (Long) contextProps.get(AgentConstants.AGENT_ID);
    	Integer controllerType = (Integer) contextProps.get(AgentConstants.CONTROLLER_TYPE);
    	return controllerType != null ? getControllerData(agentId, null, contextProps): getControllersData(agentId, null, contextProps);
    }
    
    public static List<Map<String, Object>> getControllerData(Long agentId, Long controllerId, FacilioContext contextProps) throws Exception {
    	List<Map<String, Object>> controllers = new ArrayList<>();
    	List<Map<String, Object>> controllerData = new ArrayList<>();
    	Integer controllerTypeValue = (Integer) contextProps.get(AgentConstants.CONTROLLER_TYPE);
    	if(controllerTypeValue == null) {
    		throw new IllegalArgumentException("ControllerType should not be null : "+controllerTypeValue);
    	}
    	FacilioControllerType controllerType = FacilioControllerType.valueOf(controllerTypeValue);
    	FacilioChain getControllerChain = TransactionChainFactory.getControllerDataChain();
    	String moduleName = getControllerModuleName(controllerType);
    	if (moduleName == null) {
    		throw new IllegalArgumentException(" module name is null for " + controllerType.asString());
    	}
    	FacilioContext context = getControllerChain.getContext();
    	context.put(FacilioConstants.ContextNames.PAGINATION,contextProps.get(FacilioConstants.ContextNames.PAGINATION));
    	context.put(AgentConstants.SEARCH_KEY, contextProps.get(AgentConstants.SEARCH_KEY));
    	context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
    	context.put(AgentConstants.AGENT_ID, agentId);
    	context.put(AgentConstants.CONTROLLER_ID, controllerId);
    	context.put(AgentConstants.CONTROLLER_TYPE, contextProps.get(AgentConstants.CONTROLLER_TYPE));
            try {
                getControllerChain.execute();
                controllerData = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
            }catch (Exception e){
                if(controllerType == FacilioControllerType.SYSTEM){
                    LOGGER.info("Exception while fetching system controller "+e.getMessage());
                }else {
                    LOGGER.info("Exception while getting controller of type "+controllerType.asString()+" ",e);
                }
            }
            LOGGER.info("Controller type for Configured Points : "+controllerType.toString() + " value :"+controllerType.asInt());
            if (controllerData != null) {
                try {
                    for (Map<String, Object> controllerDatum : controllerData) {
                        Controller controller = makeControllerFromMap(controllerDatum, controllerType);
                        controllerDatum.put(AgentConstants.CONTROLLER, controller.getChildJSON());
                    }
                    addLogicalController(agentId,controllerData);
                    controllers.addAll(controllerData);
                } catch (Exception e) {
                    LOGGER.info(" exception while object mapping ", e);
                }
            }
        
        return controllers;
    }

    // temp and need to remove future..
    public static List<Map<String, Object>> getControllersData(Long agentId, Long controllerId, FacilioContext contextProps) throws Exception {
        List<Map<String, Object>> controllers = new ArrayList<>();
        List<Map<String, Object>> controllerData = new ArrayList<>();
        List<Map<String, Object>> filterTypes = FieldDeviceApi.getDeviceFilterData(agentId);
        Set<FacilioControllerType> controllerTypes = new HashSet<>();
        for(Map<String, Object> prop:filterTypes) {
        	Integer value = (Integer) prop.get(AgentConstants.CONTROLLER_TYPE);
//        	if((Long)prop.get("id") == 0 && value == 0) { temp comment out , because of need to handle in commissioning log add controllerId problem
//        		controllers.add(prop);
//        	}
        	controllerTypes.add(FacilioControllerType.valueOf(value));
        }
        for (FacilioControllerType controllerType : controllerTypes) {
            FacilioChain getControllerChain = TransactionChainFactory.getControllerDataChain();
            String moduleName = getControllerModuleName(controllerType);
            if (moduleName == null) {
                LOGGER.info(" module name is null for " + controllerType.asString());
                continue;
            }
            FacilioContext context = getControllerChain.getContext();
            context.put(FacilioConstants.ContextNames.PAGINATION,contextProps.get(FacilioConstants.ContextNames.PAGINATION));
            context.put(AgentConstants.SEARCH_KEY, contextProps.get(AgentConstants.SEARCH_KEY));
            context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
            context.put(AgentConstants.AGENT_ID, agentId);
            context.put(AgentConstants.CONTROLLER_ID, controllerId);
            context.put(AgentConstants.CONTROLLER_TYPE, controllerType.asInt());
            try {
                getControllerChain.execute();
                controllerData = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
            }catch (Exception e){
                if(controllerType == FacilioControllerType.SYSTEM){
                    LOGGER.info("Exception while fetching system controller "+e.getMessage());
                }else {
                    LOGGER.info("Exception while getting controller of type "+controllerType.asString()+" ",e);
                }
            }
            LOGGER.info("Controller type for Configured Points : "+controllerType.toString() + " value :"+controllerType.asInt());
            if (controllerData != null) {
                try {
                    for (Map<String, Object> controllerDatum : controllerData) {
                        Controller controller = makeControllerFromMap(controllerDatum, controllerType);
                        controllerDatum.put(AgentConstants.CONTROLLER, controller.getChildJSON());
                    }
                    addLogicalController(agentId,controllerData);
                    controllers.addAll(controllerData);
                } catch (Exception e) {
                    LOGGER.info(" exception while object mapping ", e);
                }
            }
        }
        return controllers;
    }    

    private static void addLogicalController(long agentId , List<Map<String,Object>> controllerData) throws Exception {
        if(GetPointsAction.isVirtualPointExist(agentId)) {
            Map<String,Object> prop = new HashMap<String, Object>();
            prop.put("id", 0L);
            prop.put("controllerType",0);
            prop.put("name", "Logical");
            controllerData.add(prop);
        }
    }

    public static List<Map<String,Object>> getControllerTypes(Long agentId) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getIdField(CONTROLLER_MODULE));
        fields.add(FieldFactory.getControllerTypeField(CONTROLLER_MODULE));
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(CONTROLLER_MODULE.getTableName())
                .select(fields)
                .groupBy("CONTROLLER_TYPE")
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(CONTROLLER_MODULE), String.valueOf(agentId), NumberOperators.EQUALS));
        return builder.get();
    }

    public static List<Map<String, Object>> getControllerFilterData(Long agentId, Integer controllerType) throws Exception {
        return getAgentControllerFilterData(agentId , controllerType);
    }

    private static List<Map<String, Object>> getAgentControllerFilterData(Long agentId,Integer controllerType) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
        fields.add(FieldFactory.getNameField(RESOURCE_MODULE));
        fields.add(FieldFactory.getIdField(CONTROLLER_MODULE));
        fields.add(FieldFactory.getControllerTypeField(CONTROLLER_MODULE));
        fields.add(FieldFactory.getFieldDeviceId(CONTROLLER_MODULE));
        fields.add(FieldFactory.getNewAgentIdField(CONTROLLER_MODULE));
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(CONTROLLER_MODULE.getTableName())
                .select(fields)
                .innerJoin(RESOURCE_MODULE.getTableName()).on(RESOURCE_MODULE.getTableName() + ".ID=" + CONTROLLER_MODULE.getTableName() + ".ID")
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getSysDeletedTimeField(RESOURCE_MODULE), "NULL", CommonOperators.IS_EMPTY));
        if(controllerType != null){
            builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getControllerTypeField(CONTROLLER_MODULE),String.valueOf(controllerType),NumberOperators.EQUALS));
        }
        if (agentId != null) {
            builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(CONTROLLER_MODULE), String.valueOf(agentId), NumberOperators.EQUALS));
            List<Map<String, Object>> props = builder.get();
//            boolean isMiscType=false;
//    		for (Map<String, Object> prop : props) {
//    			Integer val = (Integer) prop.get("controllerType");
//    			if (val != null && val == 0) {
//    				isMiscType = true;
//    				break;
//    			}
//    		}
            if( CollectionUtils.isNotEmpty(props) && GetPointsAction.isVirtualPointExist(agentId)) {
            	Map<String,Object> prop = new HashMap<String, Object>();
            	prop.put("id", 0L);
            	prop.put("controllerType",0);
            	prop.put("name", "Logical");
            	props.add(prop);
            }
            return props;
        }else {
        	return  builder.get();
        }
    }
    
    public static List<Long> getControllersUsingAgentId(long agentId) throws Exception{
    	return getControllersList(Collections.singletonList(agentId));
    }
    
    private static List<Long> getControllersList(List<Long> agentIds) throws Exception{
    	GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
    	builder.select(FieldFactory.getControllersField()).table(ModuleFactory.getNewControllerModule().getTableName())
    	.andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(ModuleFactory.getNewControllerModule()), String.valueOf(agentIds), NumberOperators.EQUALS));
    	List<Map<String, Object>> props =  builder.get();
    	return props.stream().map(p -> (long)p.get("id")).collect(Collectors.toList());
    }

    public static Controller getControllerByName(Long agentId,String deviceName) throws Exception {
        Device device = FieldDeviceApi.getDeviceByName(agentId,deviceName);
        if (device != null){
            return getControllerFromDevice(device);
        }else{
            LOGGER.info("Device not found for agentID :" + agentId + " deviceName : " + deviceName );
        }
        return null;
    }

    public static List<Controller> getControllersByNames(Long agentId, Set<String> deviceNames, FacilioControllerType controllerType) throws Exception {
        List<Device> devices = FieldDeviceApi.getDevicesByNames(agentId, deviceNames);
        return getControllersFromDevices(devices, controllerType);
    }

    private static Controller getControllerFromDevice(Device device) throws Exception {
        FacilioControllerType controllerType = FacilioControllerType.valueOf(device.getControllerType());
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> allFields = moduleBean.getAllFields("controller");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
        List<FacilioField> controllerFields = new ArrayList<>();
        for (FacilioField field :
                allFields) {
            if (field.getModule().getName().equals("controller")){
                controllerFields.add(field);
            }
        }
        List<Long> deviceIds = new ArrayList<>();
        deviceIds.add(device.getId());
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getNewControllerModule().getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("deviceId"),deviceIds,NumberOperators.EQUALS))
                .limit(1);
        switch (controllerType){
            case BACNET_IP:
                List<FacilioField> bacnetIpFields = moduleBean.getAllFields("bacnetipcontroller");
                for (FacilioField field :
                        bacnetIpFields) {
                    if (field.getModule().getName().equals("bacnetipcontroller")) {
                        controllerFields.add(field);
                    }
                }
                LOGGER.info("Controller Fields : " + controllerFields);
                select.select(controllerFields).innerJoin(moduleBean.getModule("bacnetipcontroller").getTableName())
                        .on("Controllers.ID = BACnet_IP_Controller.ID");
                List<Map<String, Object>> res = select.get();
                if (res != null){
                    return FieldUtil.getAsBeanFromMap(res.get(0),BacnetIpControllerContext.class);
                }
                break;
            case NIAGARA:
                for (FacilioField field :
                        moduleBean.getAllFields("niagaracontroller")) {
                    if (field.getModule().getName().equals("niagaracontroller")){
                        controllerFields.add(field);
                    }
                }
                select.select(controllerFields).innerJoin(moduleBean.getModule("niagaracontroller").getTableName())
                        .on("Controllers.ID = Niagara_Controller.ID");
                List<Map<String, Object>> res_2 = select.get();
                if (res_2 != null){
                    return FieldUtil.getAsBeanFromMap(res_2.get(0),NiagaraControllerContext.class);
                }
                break;
            default:
                LOGGER.info("Unknown controller type "+ controllerType);
        }
        return null;
    }

    public static List<Controller> getControllersFromDevices(List<Device> devices, FacilioControllerType controllerType) throws Exception {
        List<Controller> controllers = new ArrayList<>();
        ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> allFields = moduleBean.getAllFields("controller");
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(allFields);
        List<FacilioField> controllerFields = new ArrayList<>();
        for (FacilioField field :
                allFields) {
            if (field.getModule().getName().equals("controller")) {
                controllerFields.add(field);
            }
        }
        List<Long> deviceIds = new ArrayList<>();
        Map<Long, String> idVsDeviceName = new HashMap<>();
        devices.forEach(device -> {
            idVsDeviceName.put(device.getId(), device.getName());
            deviceIds.add(device.getId());
        });
        GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getNewControllerModule().getTableName())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("deviceId"), deviceIds, NumberOperators.EQUALS))
                .limit(500);
        switch (controllerType) {
            case BACNET_IP:
                List<FacilioField> bacnetIpFields = moduleBean.getAllFields("bacnetipcontroller");
                for (FacilioField field :
                        bacnetIpFields) {
                    if (field.getModule().getName().equals("bacnetipcontroller")) {
                        controllerFields.add(field);
                    }
                }
                LOGGER.info("Controller Fields : " + controllerFields);
                select.select(controllerFields).innerJoin(moduleBean.getModule("bacnetipcontroller").getTableName())
                        .on("Controllers.ID = BACnet_IP_Controller.ID");
                List<Map<String, Object>> res = select.get();
                if (res != null) {
                    res.forEach(row -> {
                        BacnetIpControllerContext c = FieldUtil.getAsBeanFromMap(row, BacnetIpControllerContext.class);
                        c.setName(idVsDeviceName.get(c.getDeviceId()));
                        controllers.add(c);
                    });
                }
                break;
            case NIAGARA:
                for (FacilioField field :
                        moduleBean.getAllFields("niagaracontroller")) {
                    if (field.getModule().getName().equals("niagaracontroller")) {
                        controllerFields.add(field);
                    }
                }
                select.select(controllerFields).innerJoin(moduleBean.getModule("niagaracontroller").getTableName())
                        .on("Controllers.ID = Niagara_Controller.ID");
                List<Map<String, Object>> res_2 = select.get();
                res_2.forEach(row -> {
                    NiagaraControllerContext c = FieldUtil.getAsBeanFromMap(row, NiagaraControllerContext.class);
                    c.setName(idVsDeviceName.get(c.getDeviceId()));
                    controllers.add(c);
                });
                break;
            default:
                LOGGER.info("Unknown controller type " + controllerType);
        }
        return controllers;
    }
    
    public static List<Map<String, Object>> getControllers(Collection<Long> ids) throws Exception {
    		return (List<Map<String, Object>>) RecordAPI.getRecordsAsProps(ContextNames.CONTROLLER, ids, null);
    }
    
    public static Map<Long, Map<String, Object>> getControllerMap(Collection<Long> ids) throws Exception {
    		List<Map<String, Object>> controllers = getControllers(ids);
    	 	if (CollectionUtils.isNotEmpty(controllers)) {
    	 		return controllers.stream()
                .collect(Collectors.toMap(controller -> (long) controller.get("id"), controller -> controller));
    	 	}
		return null;
    }
}
