package com.facilio.agentv2.controller;

import java.util.*;
import java.util.stream.Collectors;

import com.facilio.agentv2.E2.E2ControllerContext;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.agentv2.misc.MiscControllerContext;
import com.facilio.agentv2.point.GetPointRequest;
import com.facilio.agentv2.rdm.RdmControllerContext;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.db.criteria.Criteria;
import com.facilio.fw.FacilioException;
import com.facilio.modules.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.actions.GetPointsAction;
import com.facilio.agentv2.bacnet.BacnetIpControllerContext;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.agentv2.lonWorks.LonWorksControllerContext;
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
        return addController(controller, true);
    }
    public static long addController(Controller controller, boolean fromAgent) {
        try {
            long agentId = controller.getAgentId();
            if (agentId > 0) {
                AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
                FacilioAgent agent = agentBean.getAgent(agentId);
                if (agent != null) {
                    return addController(controller, agent, fromAgent);
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

    public static long addController(Controller controller, FacilioAgent agent, boolean fromAgent) throws Exception {
        if (controller != null) {
            if (agent != null) {
                FacilioChain addControllerChain = TransactionChainFactory.getAddControllerChain(fromAgent);
                FacilioContext context = addControllerChain.getContext();
                /* if(controller.getControllerType() == FacilioControllerType.SYSTEM.asInt()){
                    context.put(FacilioConstants.ContextNames.CATEGORY_READING_PARENT_MODULE, ModuleFactory.getAssetCategoryReadingRelModule());
                } */
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


    public static Controller getControllerFromDb(long controllerId) throws Exception {
        Controller controller = null;
        if (controllerId > 0) {
            GetControllerRequest getControllerRequest = new GetControllerRequest()
                    .ofType(getControllerType(controllerId))
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
        conditions.addAll(controller.getControllerConditions());
        return conditions;
    }


    public static <T extends Controller> T makeControllerFromMap(Map<String, Object> map, FacilioControllerType controllerType) throws Exception {
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
                controller = FieldUtil.getAsBeanFromMap(map, MiscControllerContext.class);
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
            case RDM:
                controller = FieldUtil.getAsBeanFromMap(map, RdmControllerContext.class);
                break;
            case E2:
                E2ControllerContext.validateControllerJSON(map);
                controller = FieldUtil.getAsBeanFromMap(map,E2ControllerContext.class);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + controllerType);
        }
        if (controller != null) {
            return (T)controller;
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
            case RDM:
                return FacilioConstants.ContextNames.RDM_CONTROLLER_MODULE_NAME;
            case E2:
                return E2ControllerContext.MODULENAME;
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
        if(controllerType != null){
            return getControllerData(agentId, null, contextProps);
        }
        return null;
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
            if (!controllerData.isEmpty()) {
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
//    public static List<Map<String, Object>> getControllersData(Long agentId, Long controllerId, FacilioContext contextProps) throws Exception {
//        List<Map<String, Object>> controllers = new ArrayList<>();
//        List<Map<String, Object>> controllerData = new ArrayList<>();
//        List<Map<String, Object>> filterTypes = FieldDeviceApi.getDeviceFilterData(agentId);
//        Set<FacilioControllerType> controllerTypes = new HashSet<>();
//        for(Map<String, Object> prop:filterTypes) {
//        	Integer value = (Integer) prop.get(AgentConstants.CONTROLLER_TYPE);
////        	if((Long)prop.get("id") == 0 && value == 0) { temp comment out , because of need to handle in commissioning log add controllerId problem
////        		controllers.add(prop);
////        	}
//        	controllerTypes.add(FacilioControllerType.valueOf(value));
//        }
//        for (FacilioControllerType controllerType : controllerTypes) {
//            FacilioChain getControllerChain = TransactionChainFactory.getControllerDataChain();
//            String moduleName = getControllerModuleName(controllerType);
//            if (moduleName == null) {
//                LOGGER.info(" module name is null for " + controllerType.asString());
//                continue;
//            }
//            FacilioContext context = getControllerChain.getContext();
//            context.put(FacilioConstants.ContextNames.PAGINATION,contextProps.get(FacilioConstants.ContextNames.PAGINATION));
//            context.put(AgentConstants.SEARCH_KEY, contextProps.get(AgentConstants.SEARCH_KEY));
//            context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
//            context.put(AgentConstants.AGENT_ID, agentId);
//            context.put(AgentConstants.CONTROLLER_ID, controllerId);
//            context.put(AgentConstants.CONTROLLER_TYPE, controllerType.asInt());
//            try {
//                getControllerChain.execute();
//                controllerData = (List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
//            }catch (Exception e){
//                if(controllerType == FacilioControllerType.SYSTEM){
//                    LOGGER.info("Exception while fetching system controller "+e.getMessage());
//                }else {
//                    LOGGER.info("Exception while getting controller of type "+controllerType.asString()+" ",e);
//                }
//            }
//            LOGGER.info("Controller type for Configured Points : "+controllerType.toString() + " value :"+controllerType.asInt());
//            if (controllerData != null) {
//                try {
//                    for (Map<String, Object> controllerDatum : controllerData) {
//                        Controller controller = makeControllerFromMap(controllerDatum, controllerType);
//                        controllerDatum.put(AgentConstants.CONTROLLER, controller.getChildJSON());
//                    }
//                    addLogicalController(agentId,controllerData);
//                    controllers.addAll(controllerData);
//                } catch (Exception e) {
//                    LOGGER.info(" exception while object mapping ", e);
//                }
//            }
//        }
//        return controllers;
//    }

    private static void addLogicalController(long agentId , List<Map<String,Object>> controllerData) throws Exception {
        if(isVirtualPointExist(agentId)) {
            Map<String,Object> prop = new HashMap<String, Object>();
            prop.put("id", 0L);
            prop.put("controllerType",0);
            prop.put("name", "Logical");
            controllerData.add(prop);
        }
    }

    public static List<Map<String,Object>> getControllerTypes(Long agentId) throws Exception {
        List<FacilioField> fields = new ArrayList<>();
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
            if( CollectionUtils.isNotEmpty(props) && isVirtualPointExist(agentId)) {
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

    public static List<Controller> getControllersUsingAgentId(long agentId) throws Exception {
    	return getControllersList(Collections.singletonList(agentId));
    }

    private static List<Controller> getControllersList(List<Long> agentIds) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
        builder.table(modBean.getModule(AgentConstants.CONTROLLER).getTableName());
        builder.select(modBean.getModule(AgentConstants.CONTROLLER).getFields())
    	.andCondition(CriteriaAPI.getCondition(FieldFactory.getAgentIdField(ModuleFactory.getNewControllerModule()), String.valueOf(agentIds), NumberOperators.EQUALS));
        List<Map<String, Object>> props = builder.get();
        return FieldUtil.getAsBeanListFromMapList(props, Controller.class);
    }

    public static Controller getControllerByName(Long agentId,String deviceName) throws Exception {
        Set<String> names = new HashSet<>();
        names.add(deviceName);
        return getControllerRequestWithNames(agentId, names).getController();
    }

    public static List<Controller> getControllersByNames(Long agentId, Set<String> deviceNames, FacilioControllerType controllerType) throws Exception {
        GetControllerRequest request = getControllerRequestWithNames(agentId, deviceNames).ofType(controllerType);
        return request.getControllers();
    }

    public static List<Controller> getControllersByNames(Long agentId, Set<String> deviceNames) throws Exception {
        GetControllerRequest request = getControllerRequestWithNames(agentId, deviceNames);
        return request.getControllers();
    }

    private static GetControllerRequest getControllerRequestWithNames(Long agentId, Set<String> deviceNames) throws Exception {
        return new GetControllerRequest().withAgentId(agentId).withNames(deviceNames);
    }

    public static List<Map<String, Object>> getControllers(Collection<Long> ids) throws Exception {
    		return (List<Map<String, Object>>) RecordAPI.getRecordsAsProps(ContextNames.CONTROLLER, ids, null);
    }

    public static Map<String, Object> getController(Collection<Long> ids) throws Exception {
        List<Map<String, Object>>ControllerList=  (List<Map<String, Object>>) RecordAPI.getRecordsAsProps(ContextNames.CONTROLLER, ids, null);
        return (Map<String, Object>) ControllerList.get(0);
    }
    
    public static Map<Long, Map<String, Object>> getControllerMap(Collection<Long> ids) throws Exception {
    		List<Map<String, Object>> controllers = getControllers(ids);
    	 	if (CollectionUtils.isNotEmpty(controllers)) {
    	 		return controllers.stream()
                .collect(Collectors.toMap(controller -> (long) controller.get("id"), controller -> controller));
    	 	}
		return null;
    }

    public static FacilioControllerType getControllerType(long controllerId) throws Exception {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getControllersField());

        GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getNewControllerModule().getTableName())
                .select(fieldMap.values())
                .andCondition(CriteriaAPI.getCondition(fieldMap.get("id"), Collections.singletonList(controllerId), NumberOperators.EQUALS));

        List<Map<String, Object>> rows = selectRecordBuilder.get();
        if (rows.size() == 1) {
            int controllerType = (int) rows.get(0).get(AgentConstants.CONTROLLER_TYPE);
            return FacilioControllerType.valueOf(controllerType);
        }
        throw new FacilioException("Invalid no. of rows");
    }


//    public static boolean discoverPoint(List<Long> controllerIds) throws Exception {
//        try {
//            Controller controller = getControllers(controllerIds,null).get(0);
//            Objects.requireNonNull(controller,"Controller doesn't exist");
//            ControllerMessenger.discoverPoints(controller);
//            return true;
//        }catch (Exception e){
//            LOGGER.error("Exception while discoverPoints -> ", e);
//            throw e;
//        }
//    }

    public static boolean discoverPoint(long controllerId) throws Exception {
        try {
            Controller controller = getController(controllerId,null);
            Objects.requireNonNull(controller,"Controller doesn't exist");
            ControllerMessenger.discoverPoints(controller);
            return true;
        }catch (Exception e){
            LOGGER.error("Exception while discoverPoints -> ", e);
            throw e;
        }
    }
    public static Controller getController(Long controllerId, Long agentId) throws Exception {
        GenericSelectRecordBuilder builder = getSelectControllersBuilder(Collections.singletonList(controllerId), agentId);

        Map<String, Object> props = builder.fetchFirst();
        if(props != null && !props.isEmpty()) {
            FacilioControllerType ctype = FacilioControllerType.valueOf((Integer)props.get(AgentConstants.CONTROLLER_TYPE));
            Controller controller = ControllerApiV2.makeControllerFromMap(getControllerProps(ctype, controllerId),ctype);
            controller.setAgentId((long)props.get(AgentConstants.AGENT_ID));
            controller.setName((String)props.get(AgentConstants.NAME));
            controller.setId((Long)props.get(AgentConstants.ID));
            return controller;
        }
        return null;
    }

    public static List<Controller> getControllers(List<Long> controllerIds,Long agentId) throws Exception {

        List<Controller> controllers = new ArrayList<>();

        GenericSelectRecordBuilder builder = getSelectControllersBuilder(controllerIds, agentId);

        List<Map<String, Object>> rows = builder.get();
        for (Map<String, Object> row : rows) {
            if (row != null && !row.isEmpty()) {
                FacilioControllerType ctype = FacilioControllerType.valueOf((Integer) row.get(AgentConstants.CONTROLLER_TYPE));
                Long id = (Long) row.get(AgentConstants.ID);
                Controller controller = ControllerApiV2.makeControllerFromMap(getControllerProps(ctype, id), ctype);
                controller.setAgentId((long) row.get(AgentConstants.AGENT_ID));
                controller.setName((String) row.get(AgentConstants.NAME));
                controller.setId((Long) row.get(AgentConstants.ID));
                controllers.add(controller);
            }
        }

        return controllers;

    }

    private static Map<String,Object> getControllerProps(FacilioControllerType type,Long controllerId) throws Exception{
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = ControllerApiV2.getControllerModuleName(type);
        FacilioModule module = modBean.getModule(moduleName);
        List<FacilioField> fields = new ArrayList<FacilioField>();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(module.getTableName())
                .andCondition(CriteriaAPI.getIdCondition(controllerId,module));
        if(type != null && type.asInt() == FacilioControllerType.MODBUS_RTU.asInt()){
            fields.addAll(FieldFactory.getRtuNetworkFields());
            fields.addAll(modBean.getModuleFields(module.getName()));
            builder.select(fields).innerJoin(ModuleFactory.getRtuNetworkModule().getTableName())
                    .on(module.getTableName() + ".NETWORK_ID = " + ModuleFactory.getRtuNetworkModule().getTableName() + ".ID");
        }else {
            builder.select(modBean.getModuleFields(moduleName));
        }
        return  builder.fetchFirst();
    }
    private static GenericSelectRecordBuilder getSelectControllersBuilder(List<Long> controllerIds, Long agentId) {
        FacilioModule module = ModuleFactory.getNewControllerModule();
        FacilioModule resourceModule = ModuleFactory.getResourceModule();
        List<FacilioField> fields = new ArrayList<FacilioField>();
        fields.add(FieldFactory.getNameField(resourceModule));
        fields.addAll(FieldFactory.getControllersField());

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .select(fields)
                .table(module.getTableName())
                .innerJoin(resourceModule.getTableName())
                .on(resourceModule.getTableName()+".ID = "+module.getTableName()+".ID")
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getSysDeletedTimeField(resourceModule), "NULL", CommonOperators.IS_EMPTY))
                .andCondition(CriteriaAPI.getIdCondition(controllerIds,module));
        if(agentId != null){
            builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getNewAgentIdField(module),String.valueOf(agentId),NumberOperators.EQUALS));
        }
        return builder;
    }

    public static List<?extends Controller> getControllersToAdd(long agentId, FacilioControllerType controllerType, List<? extends Controller> controllerList) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        String moduleName = ControllerApiV2.getControllerModuleName(controllerType);
        List<FacilioField> moduleFields = modBean.getAllFields(moduleName);
        FacilioModule module = modBean.getModule(moduleName);
        Class beanClassName = FacilioConstants.ContextNames.getClassFromModule(module);
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(moduleFields);

        Criteria criteria = new Criteria();
        criteria.andCriteria(getControllerCriteria(controllerList));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.AGENT_ID), String.valueOf(agentId), NumberOperators.EQUALS));

        SelectRecordsBuilder<? extends Controller> builder = new SelectRecordsBuilder<Controller>()
                .module(module)
                .beanClass(beanClassName)
                .select(moduleFields);

        builder.andCriteria(criteria);
        List<? extends Controller> props = builder.get();
        for (Controller prop : props) {
            if(controllerList.remove(prop)){
                LOGGER.info("Controller "+prop.getName()+" already exists");
            }
        }
        return controllerList;
    }

    public static Criteria getControllerCriteria(List<? extends Controller> controllerList) throws Exception {
        Criteria criteriaList = new Criteria();

        for (Controller controller : controllerList){
            Criteria criteria = new Criteria();
            List<Condition> conditions = controller.getControllerConditions();
            criteria.addAndConditions(conditions);
            criteriaList.orCriteria(criteria);
        }
        return criteriaList;
    }
    public static boolean isVirtualPointExist(Long agentId) throws Exception {
        GetPointRequest point = new GetPointRequest();
        point.ofType(FacilioControllerType.valueOf(0));
        point.withLogicalControllers(agentId);
        point.count();
        Long pointCount =  (long) point.getPointsData().get(0).getOrDefault(AgentConstants.ID, 0L);
        return pointCount > 0;
    }
}
