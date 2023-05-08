package com.facilio.agentv2.point;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agent.module.AgentFieldFactory;
import com.facilio.agent.module.AgentModuleFactory;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.E2.E2PointContext;
import com.facilio.agentv2.JsonUtil;
import com.facilio.agentv2.bacnet.BacnetIpPointContext;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.agentv2.lonWorks.LonWorksPointContext;
import com.facilio.agentv2.misc.MiscPoint;
import com.facilio.agentv2.modbusrtu.ModbusRtuPointContext;
import com.facilio.agentv2.modbustcp.ModbusTcpPointContext;
import com.facilio.agentv2.niagara.NiagaraPointContext;
import com.facilio.agentv2.opcua.OpcUaPointContext;
import com.facilio.agentv2.opcxmlda.OpcXmlDaPointContext;
import com.facilio.agentv2.rdm.RdmPointContext;
import com.facilio.agentv2.system.SystemPointContext;
import com.facilio.bacnet.BACNetUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
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
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class PointsAPI {
    private static final Logger LOGGER = LogManager.getLogger(PointsAPI.class.getName());

    public static FacilioModule getPointModule(FacilioControllerType controllerType) throws Exception {
        switch (controllerType) {

            case OPC_XML_DA:
                return ModuleFactory.getOPCXmlDAPointModule();
            case MODBUS_RTU:
                return ModuleFactory.getModbusRtuPointModule();
            case MODBUS_IP:
                return ModuleFactory.getModbusTcpPointModule();
            case BACNET_IP:
                return ModuleFactory.getBACnetIPPointModule();
            case NIAGARA:
                return ModuleFactory.getNiagaraPointModule();
            case MISC:
                return ModuleFactory.getMiscPointModule();
            case OPC_UA:
                return ModuleFactory.getOPCUAPointModule();
            case SYSTEM:
                return ModuleFactory.getSystemPointModule();
            case LON_WORKS:
            	return AgentModuleFactory.getLonWorksPointModule();
            case RDM:
                return AgentModuleFactory.getRdmPointModule();
            case E2:
                return AgentModuleFactory.getE2PointModule();
            case REST:
            case CUSTOM:
            case BACNET_MSTP:
            case KNX:
                throw new Exception(" No implementation for " + controllerType.asString() + " points");
            default:
                throw new Exception("No such implementation " + controllerType);
        }
    }
    
    public static List<FacilioField> getChildPointFields(FacilioControllerType controllerType) throws Exception {
    	return getChildPointFields(controllerType, true);
    }

    public static List<FacilioField> getChildPointFields(FacilioControllerType controllerType, boolean fetchExtended) throws Exception {
        switch (controllerType) {
            case OPC_XML_DA:
                return FieldFactory.getOPCXmlDAPointFields(fetchExtended);
            case MODBUS_RTU:
                return FieldFactory.getModbusRtuPointFields(fetchExtended);
            case MODBUS_IP:
                return FieldFactory.getModbusTcpPointFields(fetchExtended);
            case BACNET_IP:
                return FieldFactory.getBACnetIPPointFields(fetchExtended);
            case NIAGARA:
                return FieldFactory.getNiagaraPointFields(fetchExtended);
            case MISC:
                return FieldFactory.getMiscPointFields(fetchExtended);
            case OPC_UA:
                return FieldFactory.getOPCUAPointFields(fetchExtended);
            case SYSTEM:
                return FieldFactory.getSystemPointFields(fetchExtended);
            case LON_WORKS:
            	return AgentFieldFactory.getLonWorksPointFields(fetchExtended);
            case RDM:
                return AgentFieldFactory.getRdmPointFields(fetchExtended);
            case E2:
                return AgentFieldFactory.getE2PointFields(fetchExtended);
            case REST:
            case CUSTOM:
            case BACNET_MSTP:
            case KNX:
                throw new Exception(" No implementation for " + controllerType.asString() + " points");
            default:
                throw new Exception("No such implementation " + controllerType);
        }
    }

    public static Point getPointFromJSON(Map<String, Object> payload) throws Exception {
        if (containsValueCheck(AgentConstants.POINT_TYPE, payload)) {
            FacilioControllerType controllerType;
            controllerType = FacilioControllerType.valueOf(JsonUtil.getInt(payload.get(AgentConstants.POINT_TYPE)));
            //LOGGER.info(" controller type for point is " + controllerType.asString());
            if (payload.containsKey(AgentConstants.LOGICAL)) {
                if (JsonUtil.getBoolean(payload.get(AgentConstants.LOGICAL))) {
                    controllerType = FacilioControllerType.MISC;
                }
            }
            switch (controllerType) {
                case MODBUS_RTU:
                    return ModbusRtuPointContext.getPointFromMap(payload);
                case MODBUS_IP:
                    return ModbusTcpPointContext.getPointFromMap(payload);
                case OPC_UA:
                    return OpcUaPointContext.getPointFromMap(payload);
                case OPC_XML_DA:
                    return OpcXmlDaPointContext.getPointFromMap(payload);
                case BACNET_IP:
                    return BacnetIpPointContext.getPointFromMap(payload);
                case NIAGARA:
                    return NiagaraPointContext.getPointFromMap(payload);
                case MISC:
                    return MiscPoint.getPointFromMap(payload);
                case SYSTEM:
                    return SystemPointContext.getPointFromMap(payload);
                case LON_WORKS:
                	return LonWorksPointContext.getPointFromMap(payload);
                case RDM:
                    return RdmPointContext.getPointFromMap(payload);
                case E2:
                    return E2PointContext.getPointFromMap(payload);
                default:
                    throw new Exception("no implementation for " + controllerType.asString());
            }
        }
        throw new Exception("Point Type missing not defined -> " + payload);
    }


    public static List<Point> getPointFromRows(List<Map<String, Object>> points) {
        List<Point> pointList = new ArrayList<>();
        for (Map<String, Object> point : points) {
            try {
                Point p = PointsAPI.getPointFromJSON(point);
                if (point.containsKey("configureStatus")) {
                    p.setConfigureStatus(Integer.parseInt(point.get("configureStatus").toString()));
                }
                if(point.containsKey("thresholdJson") && ((!point.get("thresholdJson").toString().isEmpty()) && point.get("thresholdJson")!=null)) {
                	p.setThresholdJSON(point.get("thresholdJson").toString());
                }
                pointList.add(p);
            } catch (Exception e) {
                    LOGGER.info("Exception occurred while making point from row ", e);
            }
        }
        return pointList;
    }

    public static boolean addPoint(Point point) {
        FacilioChain addPointChain = TransactionChainFactory.getAddPointChain();
        if (point != null) {
            addPointChain.getContext().put(FacilioConstants.ContextNames.RECORD, point);
            try {
                return addPointChain.execute();
            } catch (Exception e) {
                LOGGER.info("Exception occurred while adding point ", e);
            }
        }
        return false;
    }


    public static void configurePointsAndMakeController(List<Long> pointIds, FacilioControllerType controllerType, boolean logical, int interval) throws Exception {
        if ((pointIds != null) && (!pointIds.isEmpty())) {
            List<Point> dBPoints = new GetPointRequest().ofType(controllerType).fromIds(pointIds).getPoints();
            int controllersCount = getControllerIdVsPointsMap(dBPoints).keySet().size();
            if(controllersCount > 1) {
                FacilioContext context = new FacilioContext();
                context.put(AgentConstants.POINT_IDS, pointIds);
                context.put(AgentConstants.CONTROLLER_TYPE, controllerType);
                context.put(AgentConstants.DATA_INTERVAL, interval);
                context.put(AgentConstants.LOGICAL, logical);
                FacilioTimer.scheduleInstantJob("BulkConfigurePointsJob", context);
            } else {
                List<Point> points = new GetPointRequest().ofType(controllerType).fromIds(pointIds).getPoints();
                if(points != null && (!points.isEmpty())){
                    long controllerId = points.get(0).getControllerId();
                    if (controllerId < 0) {
                        throw new Exception("Point's Controller Id Id can't be less than 1");
                    }
                    FacilioChain chain = TransactionChainFactory.getConfigurePointAndProcessControllerV2Chain();
                    FacilioContext context = chain.getContext();
                    context.put(AgentConstants.CONTROLLER_ID, controllerId);
                    context.put(AgentConstants.POINTS, points);
                    context.put(AgentConstants.CONTROLLER_TYPE, controllerType);
                    context.put(AgentConstants.DATA_INTERVAL, interval);
                    context.put(AgentConstants.LOGICAL, logical);
                    chain.execute();
                } else {
                    throw new Exception("No points found in DB for ids->" + pointIds);
                }
            }
        } else {
            throw new Exception("PointIds can't be null or empty ->" + pointIds);
        }
    }

    private static void sendConfigurePointCommand(List<Point> points, Controller controller) throws Exception {
        ControllerMessenger.configurePoints(points, controller, -1);
    }

    public static boolean configurePoints(List<Point> points, Controller controller, boolean isLogical, int interval) throws Exception {
        ControllerMessenger.configurePoints(points, controller, interval);
        if ((points != null) && (!points.isEmpty())) {
            List<Long> pointIds = new ArrayList<>();
            for (Point point : points) {
                pointIds.add(point.getId());
            }
//            try {
//                updatePointsControllerId(controller);
//            }catch (Exception e){
//                LOGGER.info("Exception while updating point's controller id for device "+controller.getDeviceId());
//            }
            return updatePointsConfiguredInprogress(pointIds, controller.getId(), controller.getControllerType(),isLogical, interval);
        } else {
            throw new Exception("points can't be null or empty");
        }
    }


    public static boolean updatePointsConfiguredInprogress(List<Long> ids, long controllerId, int controllerType, boolean logical, int interval) throws Exception {
        if ((ids != null) && (!ids.isEmpty())) {

            Map<String, Object> toUpdateMap = new HashMap<>();
            toUpdateMap.put(AgentConstants.CONFIGURE_STATUS, PointEnum.ConfigureStatus.IN_PROGRESS.getIndex());
            toUpdateMap.put(AgentConstants.CONTROLLER_ID, controllerId);

            FacilioChain chain = TransactionChainFactory.updatePointsConfigured();
            FacilioContext context = chain.getContext();
            context.put(AgentConstants.POINT_IDS, ids);
            context.put(AgentConstants.POINT_TYPE, controllerType);
            context.put(AgentConstants.CONTROLLER_ID, controllerId);	
            context.put(AgentConstants.LOGICAL, logical);
            context.put(AgentConstants.DATA_INTERVAL, interval);
            chain.execute();

            if (context.containsKey(FacilioConstants.ContextNames.ROWS_UPDATED) && ((Integer) context.get(FacilioConstants.ContextNames.ROWS_UPDATED) > 0)) {
                return true;
            }
        } else {
            throw new Exception(" pointId cant be less than 1");
        }
        return false;
    }

    public static boolean unConfigurePointsChain(List<Long> pointIds, FacilioControllerType controllerType) throws Exception {
        if ((pointIds != null) && (!pointIds.isEmpty())) {
            List<Point> points = new GetPointRequest().ofType(controllerType).fromIds(pointIds).getPoints();
            int controllersCount = getControllerIdVsPointsMap(points).keySet().size();
            if(controllersCount > 1){
                FacilioContext context = new FacilioContext();
                context.put(AgentConstants.POINT_IDS, pointIds);
                context.put(AgentConstants.CONTROLLER_TYPE, controllerType);
                FacilioTimer.scheduleInstantJob("BulkUnConfigurePointsJob", context);
            } else {
                FacilioChain chain = TransactionChainFactory.unconfigurePointsChain();
                FacilioContext context = chain.getContext();
                context.put(AgentConstants.POINT_IDS, pointIds);
                context.put(AgentConstants.CONTROLLER_TYPE, controllerType);
                chain.execute();
            }
        }
        return true;
    }

    private static Map<Long, List<Point>> getControllerIdVsPointsMap(List<Point> points) {
        Map<Long, List<Point>> controllerIdVsPoints = new HashMap<>();
        for (Point point : points) {
            long controllerId = point.getControllerId();
            if(controllerIdVsPoints.containsKey(controllerId)){
                controllerIdVsPoints.get(controllerId).add(point);
            } else {
                List<Point> pointsList = new ArrayList<>();
                pointsList.add(point);
                controllerIdVsPoints.put(controllerId, pointsList);
            }
        }
        return controllerIdVsPoints;
    }

    public static boolean updatePointSubsctiptionComplete(List<Long> pointIds) throws Exception {
        if ((pointIds != null) && (!pointIds.isEmpty())) {
            FacilioChain editChain = TransactionChainFactory.getEditPointChain();
            FacilioContext context = editChain.getContext();
            context.put(FacilioConstants.ContextNames.CRITERIA, getIdCriteria(pointIds));
            context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP, Collections.singletonMap(AgentConstants.SUBSCRIBE_STATUS, PointEnum.SubscribeStatus.SUBSCRIBED.getIndex()));
            editChain.execute();
            if (context.containsKey(FacilioConstants.ContextNames.ROWS_UPDATED) && ((Integer) context.get(FacilioConstants.ContextNames.ROWS_UPDATED) > 0)) {
                return true;
            }
        }
        return false;
    }

    private static boolean updatePointsConfigurationComplete(Long controllerId,List<String> pointNames) throws Exception {
        FacilioChain chain = TransactionChainFactory.pointsConfigurationComplete();
        FacilioContext context = chain.getContext();
        Controller controller = ControllerApiV2.getControllerFromDb(controllerId);
        context.put(AgentConstants.POINT_NAMES,pointNames);
        context.put(AgentConstants.CONTROLLER,controller);
        chain.execute();
        return false;
    }

    private static boolean updatePointSubsctiptionComplete(long controllerId,List<String> pointNames) throws Exception {
        FacilioModule pointModule = ModuleFactory.getPointModule();
        if ((pointNames != null) && (!pointNames.isEmpty())) {
            FacilioChain editChain = TransactionChainFactory.getEditPointChain();
            FacilioContext context = editChain.getContext();
            Criteria criteria = new Criteria();
            criteria.addAndCondition(getNameCondition(pointNames));
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(pointModule), String.valueOf(controllerId), NumberOperators.EQUALS));
            context.put(FacilioConstants.ContextNames.CRITERIA, criteria);
            context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP, Collections.singletonMap(AgentConstants.SUBSCRIBE_STATUS, PointEnum.SubscribeStatus.SUBSCRIBED.getIndex()));
            editChain.execute();
            if (context.containsKey(FacilioConstants.ContextNames.ROWS_UPDATED) && ((Integer) context.get(FacilioConstants.ContextNames.ROWS_UPDATED) > 0)) {
                return true;
            }
        }
        return false;
    }



    public static boolean UpdatePointsUnConfigured(List<Long> pointIds) throws Exception {

        if ((pointIds != null) && (!pointIds.isEmpty())) {
            FacilioChain editChain = TransactionChainFactory.getEditPointChain();
            FacilioContext context = editChain.getContext();
            context.put(FacilioConstants.ContextNames.CRITERIA, getIdCriteria(pointIds));
            context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP, Collections.singletonMap(AgentConstants.CONFIGURE_STATUS, PointEnum.ConfigureStatus.UNCONFIGURED.getIndex()));
            editChain.execute();
            if (context.containsKey(FacilioConstants.ContextNames.ROWS_UPDATED) && ((Integer) context.get(FacilioConstants.ContextNames.ROWS_UPDATED) > 0)) {
                return true;
            }
        } else {
            throw new Exception(" pointIds cant be null or empty ->" + pointIds);
        }
        return false;
    }

    public static boolean makePoinWritable(Long pointId) throws Exception {
        return makePoinsWritable(Collections.singletonList(pointId));
    }

    public static boolean disablePoinWritable(Long pointId) throws Exception {
        return disablePoinsWritable(Collections.singletonList(pointId));
    }

    public static boolean makePoinsWritable(List<Long> pointIds) throws Exception {
        return editWritable(pointIds, true);
    }

    public static boolean disablePoinsWritable(List<Long> pointIds) throws Exception {
        return editWritable(pointIds, false);
    }

    private static boolean editWritable(List<Long> pointIds, boolean writable) throws Exception {
        Objects.requireNonNull(pointIds, "point ids can't be null");
        if (!pointIds.isEmpty()) {
            FacilioChain editChain = TransactionChainFactory.getEditPointChain();
            FacilioContext context = editChain.getContext();
            context.put(FacilioConstants.ContextNames.CRITERIA, getIdCriteria(pointIds));
            context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP, Collections.singletonMap(AgentConstants.WRITABLE, writable));
            editChain.execute();
            return true;
        } else {
            throw new Exception("point ids can't be null");
        }
    }

    public static boolean UpdatePointUnConfigured(Long pointId) throws Exception {
        return UpdatePointsUnConfigured(Collections.singletonList(pointId));
    }

    public static Criteria getIdCriteria(List<Long> pointIds) {
        FacilioModule pointModule = ModuleFactory.getPointModule();
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getIdCondition(pointIds, pointModule));
        return criteria;
    }

    public static Criteria getNameCriteria(List<String> pointNames) {
        FacilioModule pointModule = ModuleFactory.getPointModule();
        Criteria criteria = new Criteria();
        pointNames = pointNames.stream().map(p -> p.replace(",", StringOperators.DELIMITED_COMMA)).collect(Collectors.toList());
        criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(pointModule), StringUtils.join(pointNames, ","), StringOperators.IS));
        return criteria;
    }

    public static Condition getNameCondition(List<String> pointNames) {
        pointNames = pointNames.stream().map(p -> p.replace(",", StringOperators.DELIMITED_COMMA)).collect(Collectors.toList());
        return CriteriaAPI.getCondition(FieldFactory.getNameField(ModuleFactory.getPointModule()), StringUtils.join(pointNames,","), StringOperators.IS);
    }


    public static long getPointsCount(long controllerId, long deviceId) {
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getPointFields());
        try {
            Criteria criteria = new Criteria();
            if (controllerId > 0L) {
                LOGGER.info(fieldMap.get(AgentConstants.CONTROLLER_ID));
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.CONTROLLER_ID), String.valueOf(controllerId), NumberOperators.EQUALS));
            }
            if (deviceId > 0L) {
                LOGGER.info(fieldMap.get(AgentConstants.CONTROLLER_ID));
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.DEVICE_ID), String.valueOf(deviceId), NumberOperators.EQUALS));
            }
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.ID), String.valueOf(0), NumberOperators.GREATER_THAN));
            return getPointsCount(criteria);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting agent points count", e);
        }
        return 0;
    }

    private static long getPointsCount(Criteria criteria) throws Exception {
        FacilioModule pointModule = ModuleFactory.getPointModule();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getPointFields());
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(pointModule.getTableName())
                .select(new HashSet<>())
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, fieldMap.get(AgentConstants.ID));
        if (criteria != null) {
            builder.andCriteria(criteria);
        }
        List<Map<String, Object>> result = builder.get();
        if (result != null && (!result.isEmpty())) {
            return (long) result.get(0).get(AgentConstants.ID);
        }
        return 0;
    }


    public static Class getPointType(FacilioControllerType type) throws Exception {
        switch (type) {
            case NIAGARA:
                return NiagaraPointContext.class;
            case OPC_UA:
                return OpcUaPointContext.class;
            case MISC:
                return MiscPoint.class;
            case BACNET_IP:
                return BacnetIpPointContext.class;
            case MODBUS_IP:
                return ModbusTcpPointContext.class;
            case MODBUS_RTU:
                return ModbusRtuPointContext.class;
            case OPC_XML_DA:
                return OpcXmlDaPointContext.class;
            case SYSTEM:
                return SystemPointContext.class;
            case RDM:
                return RdmPointContext.class;
            case E2:
                return E2PointContext.class;
            case BACNET_MSTP:
            default:
                throw new Exception(" No implementation for " + type.asString() + " point");

        }
    }

    public static boolean deletePointsChain(List<Long> pointIds, FacilioControllerType type) {
        try {
            FacilioChain chain = TransactionChainFactory.deletepointsChain();
            FacilioContext context = chain.getContext();
            context.put(AgentConstants.POINT_IDS, pointIds);
            context.put(AgentConstants.CONTROLLER_TYPE, type);
            chain.execute();
            return true;
        } catch (Exception e) {
            LOGGER.info("Exception while deleting points");
        }
        return false;
    }

    public static boolean executeDeletePoints(List<Long> pointIds) throws SQLException {
        FacilioModule pointModule = ModuleFactory.getPointModule();
        //TODO -
        if ((pointIds != null) && (!pointIds.isEmpty())) {
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(pointModule.getTableName())
                    .fields(FieldFactory.getPointFields())
                    .andCondition(CriteriaAPI.getIdCondition(pointIds, pointModule));
            Map<String, Object> toUpdateMap = new HashMap<>();
            toUpdateMap.put(AgentConstants.DELETED_TIME, System.currentTimeMillis());
            int rowsAffected = builder.update(toUpdateMap);
            if (rowsAffected > 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean setValue(long pointid, FacilioControllerType type, Object value) throws Exception {
        GetPointRequest getPointRequest = new GetPointRequest()
        		.ofType(type)
        		.withId(pointid)
                ;
        Point point = getPointRequest.getPoint();
        if (point != null) {
            point.setValue(value);
            ControllerMessenger.setValue(point);
            return true;
        }
        return false;
    }

    public static boolean resetConfiguredPoints(Long controllerId) throws SQLException {
        FacilioModule pointModule = ModuleFactory.getPointModule();
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(pointModule.getTableName())
                .fields(FieldFactory.getPointFields())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(pointModule), String.valueOf(controllerId), NumberOperators.EQUALS));
        Map<String, Object> toUpdate = new HashMap<>();
        toUpdate.put(AgentConstants.CONFIGURE_STATUS, PointEnum.ConfigureStatus.UNCONFIGURED.getIndex());
        int rowsAffected = builder.update(toUpdate);
        return (rowsAffected > 0);
    }

    public static boolean subscribeUnsubscribePoints(List<Map<String,Object>> instances, FacilioControllerType type, FacilioCommand command) throws Exception {
        FacilioChain chain = TransactionChainFactory.subscribeUnsbscribechain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.INSTANCE_INFO, instances);
        context.put(AgentConstants.CONTROLLER_TYPE, type);
        context.put(AgentConstants.COMMAND, command);
        chain.execute();
        return true;
    }

    public static boolean updatePointsSubscribedOrUnsubscribed(List<Long> pointIds, Map<String, Object> newInstance, FacilioCommand command) throws Exception {
        if ((pointIds != null) && (!pointIds.isEmpty())) {
            FacilioChain editChain = TransactionChainFactory.getEditPointChain();
            FacilioContext context = editChain.getContext();
            context.put(FacilioConstants.ContextNames.CRITERIA, getIdCriteria(pointIds));
            if (command == FacilioCommand.SUBSCRIBE) {
                newInstance.put(AgentConstants.SUBSCRIBE_STATUS, PointEnum.SubscribeStatus.IN_PROGRESS.getIndex());
                context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP, newInstance);
            } else if (command == FacilioCommand.UNSUBSCRIBE) {
                newInstance.put(AgentConstants.SUBSCRIBE_STATUS, PointEnum.SubscribeStatus.UNSUBSCRIBED.getIndex());
                context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP, newInstance);
            } else {
                throw new Exception(" command cant be anything other than sub or unsub");
            }
            editChain.execute();
            if (context.containsKey(FacilioConstants.ContextNames.ROWS_UPDATED) && ((Integer) context.get(FacilioConstants.ContextNames.ROWS_UPDATED) > 0)) {
                return true;
            }
        } else {
            throw new Exception(" pointId cant be less than 1");
        }
        return false;
    }

    public static List<Map<String, Object>> getPointsFromDb(List<String> pointNames, Controller controller) throws Exception {
        Criteria criteria = new Criteria();

        if (controller != null) {
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(ModuleFactory.getPointModule()), String.valueOf(controller.getId()), NumberOperators.EQUALS));
        } else {
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(ModuleFactory.getPointModule()), CommonOperators.IS_EMPTY));
        }
        criteria.addAndCondition(getNameCondition(pointNames));
        List<Map<String, Object>> pointsFromDb = getPointData(criteria);
        return pointsFromDb;
    }

    private static List<Map<String, Object>> getPointData(Criteria criteriaList) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getPointModule().getTableName())
                .select(FieldFactory.getPointFields())
                .andCriteria(criteriaList);
        List<Map<String, Object>> res = builder.get();
        return res;
    }


    public static List<MiscPoint> getAgentPointsSuperficial(List<Long> agentIds) throws Exception {
        Set<Long> controllerIds;
        if ((agentIds != null) && ( ! agentIds.isEmpty())) {
            controllerIds = ControllerApiV2.getControllerIds(agentIds);
            LOGGER.info(" controller ids "+controllerIds);
            if ( ! controllerIds.isEmpty()) {
                return getControllerPointsSuperficial(new ArrayList<>(controllerIds));
            }
        }
        return new ArrayList<>();
    }


    private static List<MiscPoint> getControllerPointsSuperficial(List<Long> controllerIds) throws Exception {
        AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
        FacilioModule pointModule = ModuleFactory.getPointModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(pointModule.getTableName())
                .select(FieldFactory.getPointFields())
                .andCondition(agentBean.getDeletedTimeNullCondition(pointModule));
        if( ! controllerIds.isEmpty()){
            builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(pointModule), controllerIds, NumberOperators.EQUALS));
        }
        List<Map<String, Object>> data = builder.get();
        return FieldUtil.getAsBeanListFromMapList(data, MiscPoint.class);
    }


    public static JSONObject getPointsCountData(List<Long> agentId) throws Exception {
        List<MiscPoint> points = getAgentPointsSuperficial(agentId);
        return getPointCountDataJSON(points);
    }

    public static JSONObject getPointsCountData(Long agentId) throws Exception {
       return getPointsCountData(Collections.singletonList(agentId));
    }

    public static JSONObject getPointCountDataJSON( List<MiscPoint> points) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        JSONObject pointCountData = new JSONObject();
        int subPts = 0;
        int confPts = 0;
        int commPts = 0;
        for (Point point : points) {
            if (point.getConfigureStatusEnum().equals(PointEnum.ConfigureStatus.CONFIGURED)) {
                confPts++;
                if (checkIfComissioned(point)) {
                    commPts++;
                }
            }
            if (point.getSubscribestatusEnum().equals(PointEnum.SubscribeStatus.SUBSCRIBED)) {
                subPts++;
            }
        }
        pointCountData.put(AgentConstants.TOTAL_COUNT, points.size());
        pointCountData.put(AgentConstants.CONFIGURED_COUNT, confPts);
        pointCountData.put(AgentConstants.COMMISSIONED_COUNT, commPts);
        pointCountData.put(AgentConstants.SUBSCRIBED_COUNT, subPts);
        return pointCountData;
    }

    public static boolean checkIfComissioned(Point point) {
        return ((point.getResourceId() != null) && (point.getCategoryId() != null) && (point.getFieldId() != null));
    }

    public static boolean handlePointConfigurationAndSubscription(FacilioCommand command, long controllerId, List<String> pointNames) throws Exception {
        if ((command != FacilioCommand.SUBSCRIBE) && (command != FacilioCommand.CONFIGURE)) {
            return false;
        }
        if (!pointNames.isEmpty()) {
            switch (command) {
                case CONFIGURE:
                    return PointsAPI.updatePointsConfigurationComplete(controllerId,pointNames);
                case SUBSCRIBE:
                    return PointsAPI.updatePointSubsctiptionComplete(controllerId,pointNames);
                default:
                    return false;
            }
        } else {
            throw new Exception(" point ids cant be empty while ack processing for->" + command.toString());
        }
    }

    public static boolean notNull(Object object) {
        return object != null;
    }

    public static boolean checkValue(Long value){
        return (value != null) && (value >  0);
    }

    public static boolean containsValueCheck(String key, Map<String,Object> map){
        if(notNull(key) && notNull(map) && map.containsKey(key) && ( map.get(key) != null) ){
            return true;
        }
        return false;
    }

    public static void handleModbusIpWritableSwitch(Map<String, Object> point) {
        if(point != null){
            point.put(AgentConstants.WRITABLE_SWITCH,true);
        }
    }

    public static void handleOpcXmlWritableSwitch(Map<String, Object> point) {
        if(point != null){
            point.put(AgentConstants.WRITABLE_SWITCH,true);
        }
    }


    public static void handleBacnetWritableSwitch(Map<String,Object> point) {
        if(containsValueCheck(AgentConstants.INSTANCE_TYPE,point)){
            BACNetUtil.InstanceType instanceType = BACNetUtil.InstanceType.valueOf(((Number) point.get(AgentConstants.INSTANCE_TYPE)).intValue());
            if((instanceType != null) && instanceType.isWritable()){
                point.put(AgentConstants.WRITABLE_SWITCH,true);
            }else {
                point.put(AgentConstants.WRITABLE_SWITCH,false);
            }
        }
    }

    public static List<Point> getPointData(Collection<Pair<Long, Long>> assetFieldPairs) throws Exception {
		FacilioModule pointModule = ModuleFactory.getPointModule();
		FacilioField readingField = FieldFactory.getPointFieldIdField(pointModule);
		FacilioField resourceField = FieldFactory.getPointResourceIdField(pointModule);
		Criteria criteriaList = new Criteria();
		for(Pair<Long, Long> pair: assetFieldPairs) {
            Criteria criteria = new Criteria();
            criteria.addAndCondition(CriteriaAPI.getCondition(resourceField, String.valueOf(pair.getLeft()), NumberOperators.EQUALS));
            criteria.addAndCondition(CriteriaAPI.getCondition(readingField, String.valueOf(pair.getRight()), NumberOperators.EQUALS));
            criteriaList.orCriteria(criteria);
        }
        GetPointRequest getPointRequest = new GetPointRequest()
                .withCriteria(criteriaList)
                .limit(-1);
        return getPointRequest.getPoints();
    }

    public static void updatePointsValue(List<Point> points, boolean updateDataMissing) throws Exception {

        List<GenericUpdateRecordBuilder.BatchUpdateByIdContext> batchUpdateList = new ArrayList<>();

        for (Point point : points) {
            GenericUpdateRecordBuilder.BatchUpdateByIdContext batchValue = new GenericUpdateRecordBuilder.BatchUpdateByIdContext();
            batchValue.setWhereId(point.getId());
            batchValue.addUpdateValue(AgentConstants.LAST_RECORDED_VALUE, point.getLastRecordedValue());
            batchValue.addUpdateValue(AgentConstants.LAST_RECORDED_TIME, point.getLastRecordedTime());
            if(!point.getDataMissing()) {
                batchValue.addUpdateValue(AgentConstants.DATA_MISSING, point.getDataMissing());
            }
            batchUpdateList.add(batchValue);
        }

        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getPointFields());
        List<FacilioField> updateFields = new ArrayList<>();
        updateFields.add(fieldMap.get(AgentConstants.LAST_RECORDED_VALUE));
        updateFields.add(fieldMap.get(AgentConstants.LAST_RECORDED_TIME));
        if(updateDataMissing) {
            updateFields.add(fieldMap.get(AgentConstants.DATA_MISSING));
        }


        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getPointModule().getTableName())
                .fields(updateFields);

        updateBuilder.batchUpdateById(batchUpdateList);

    }
    
    public static List<Point> getPoints(Criteria criteria) throws Exception {
    	GetPointRequest getPointRequest = new GetPointRequest()
				.withCriteria(criteria)
				;
    	return getPointRequest.getPoints();
    }

    public static void pointFilter(PointStatus status, GetPointRequest point) {
        if(status == null) {
            return;
        }
        switch (status) {
            case SUBSCRIBED:
                point.filterSubscribedPoints();
                break;
            case COMMISSIONED:
                point.filterCommissionedPoints();
                break;
            case CONFIGURED:
                point.filterConfigurePoints();
                break;
            case UNCONFIRURED:
            case UNCONFIGURED:
                point.filterUnConfigurePoints();
                point.filterUnSubscribePoints();
                break;
            default:
                throw new IllegalArgumentException("Point status is not satisfied");
        }
    }
    public enum PointStatus {
        UNCONFIRURED, CONFIGURED, SUBSCRIBED, COMMISSIONED,UNCONFIGURED
    }
}
