package com.facilio.agentv2.point;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.AgentUtilities;
import com.facilio.agentv2.JsonUtil;
import com.facilio.agentv2.bacnet.BacnetIpPointContext;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.iotmessage.ControllerMessenger;
import com.facilio.agentv2.misc.MiscPoint;
import com.facilio.agentv2.modbusrtu.ModbusRtuPointContext;
import com.facilio.agentv2.modbustcp.ModbusTcpPointContext;
import com.facilio.agentv2.niagara.NiagaraPointContext;
import com.facilio.agentv2.opcua.OpcUaPointContext;
import com.facilio.agentv2.opcxmlda.OpcXmlDaPointContext;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

public class PointsAPI extends AgentUtilities {
    private static final Logger LOGGER = LogManager.getLogger(PointsAPI.class.getName());
    private static final FacilioModule MODULE = ModuleFactory.getPointModule();
    private static final Map<String, FacilioField> FIELD_MAP = FieldFactory.getAsMap(FieldFactory.getPointFields());


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
            case REST:
            case CUSTOM:
            case LON_WORKS:
            case BACNET_MSTP:
            case KNX:
                throw new Exception(" No implementation for " + controllerType.asString() + " points");
            default:
                throw new Exception("No such implementation " + controllerType);
        }
    }

    public static List<FacilioField> getChildPointFields(FacilioControllerType controllerType) throws Exception {
        switch (controllerType) {
            case OPC_XML_DA:
                return FieldFactory.getOPCXmlDAPointFields();
            case MODBUS_RTU:
                return FieldFactory.getModbusRtuPointFields();
            case MODBUS_IP:
                return FieldFactory.getModbusTcpPointFields();
            case BACNET_IP:
                return FieldFactory.getBACnetIPPointFields();
            case NIAGARA:
                return FieldFactory.getNiagaraPointFields();
            case MISC:
                return FieldFactory.getMiscPointFields();
            case OPC_UA:
                return FieldFactory.getOPCUAPointFields();
            case REST:
            case CUSTOM:
            case LON_WORKS:
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
            if (payload.containsKey(AgentConstants.PSEUDO)) {
                if (JsonUtil.getBoolean(payload.get(AgentConstants.PSEUDO))) {
                    controllerType = FacilioControllerType.MISC;
                }
            }
            LOGGER.info(" controller type ->" + controllerType.asInt());
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
                    LOGGER.info(" making bacnet point");
                    return BacnetIpPointContext.getPointFromMap(payload);
                case NIAGARA:
                    return NiagaraPointContext.getPointFromMap(payload);
                case MISC:
                    return MiscPoint.getPointFromMap(payload);
                default:
                    throw new Exception("no implementation for " + controllerType.asString());
            }
        }
        throw new Exception("Point Type missing not defined -> " + payload);
    }


    public static List<Point> getAllPoints(List<Long> ids, long controllerId, FacilioContext paginationContext) {
        List<Point> allPoints = new ArrayList<>();
        for (FacilioControllerType type : FacilioControllerType.values()) {
            List<Map<String, Object>> pointData = getAllPoints(type, ids, controllerId, -1, -1, -1, paginationContext);
            LOGGER.info("point data->" + pointData.size());
            if (!pointData.isEmpty()) {
                List<Point> pointList = getPointFromRows(pointData);
                LOGGER.info(" pointlist ->" + pointList.size());
                if ((pointList != null) && (!pointList.isEmpty())) {
                    allPoints.addAll(pointList);
                }
            }
        }
        /*try {
            AgentMessenger.publishNewIotMessage(allPoints, FacilioCommand.CONFIGURE);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while configuring points ",e);
        }*/
        return allPoints;
    }

    public static List<Map<String, Object>> getControllerPointsData(FacilioControllerType controllerType, long controllerId, FacilioContext context) {
        if ((controllerType != null) && (controllerId > 0)) {
            List<Map<String, Object>> pointData = getAllPoints(controllerType, null, controllerId, -1, -1, -1, context);
            return pointData;
        }
        return null;
    }


    public static List<Point> getControllerPoints(FacilioControllerType controllerType, long controllerId) {
        List<Map<String, Object>> pointData = getControllerPointsData(controllerType, controllerId, null);
        if (!pointData.isEmpty()) {
            return getPointFromRows(pointData);
        }
        return null;
    }

    public static List<Point> getControllerPoints(FacilioControllerType controllerType, long controllerId, FacilioContext paginationContext) {
        List<Map<String, Object>> pointData = getControllerPointsData(controllerType, controllerId, paginationContext);
        if (!pointData.isEmpty()) {
            return getPointFromRows(pointData);
        }
        return null;
    }

    private static List<Point> getPoints(List<Long> pointIds, FacilioControllerType type) throws Exception {
        if ((pointIds != null) && (!pointIds.isEmpty()) && (type != null)) {
            List<Map<String, Object>> result = getAllPoints(type, pointIds, -1, -1, -1, -1);
            if (!result.isEmpty()) {

                return getPointFromRows(result);
            }
        } else {
            throw new Exception(" point Id->" + pointIds + " and controller type" + type + " can't be null or empty");
        }
        return null;
    }

    private static List<Map<String, Object>> getPointData(List<Long> pointIds, FacilioControllerType type) throws Exception {
        if ((pointIds != null) && (!pointIds.isEmpty()) && (type != null)) {
            List<Map<String, Object>> result = getAllPoints(type, pointIds, -1, -1, -1, -1);
            return result;
        } else {
            throw new Exception(" point Id->" + pointIds + " and controller type" + type + " can't be null or empty");
        }
    }

    private static List<Map<String, Object>> getAllPoints(FacilioControllerType type, List<Long> pointIds, long controllerId, long fieldId, long assetCategoryId, long deviceId) {
        FacilioContext context = new FacilioContext();
        context.put(AgentConstants.CONTROLLER_TYPE, type);
        context.put(AgentConstants.POINT_IDS, pointIds);
        context.put(AgentConstants.CONTROLLER_ID, controllerId);
        context.put(AgentConstants.FIELD_ID, fieldId);
        context.put(AgentConstants.ASSET_CATEGORY_ID, assetCategoryId);
        context.put(AgentConstants.DEVICE_ID, deviceId);
        return fetchPoints(context);
    }

    private static List<Map<String, Object>> getAllPoints(FacilioControllerType type, List<Long> pointIds, long controllerId, long fieldId, long assetCategoryId, long deviceId, FacilioContext paginationContext) {
        paginationContext.put(AgentConstants.CONTROLLER_TYPE, type);
        paginationContext.put(AgentConstants.POINT_IDS, pointIds);
        paginationContext.put(AgentConstants.CONTROLLER_ID, controllerId);
        paginationContext.put(AgentConstants.FIELD_ID, fieldId);
        paginationContext.put(AgentConstants.ASSET_CATEGORY_ID, assetCategoryId);
        paginationContext.put(AgentConstants.DEVICE_ID, deviceId);
        return fetchPoints(paginationContext);
    }


    private static List<Map<String, Object>> fetchPoints(FacilioContext paginationContext) {
        List<Map<String, Object>> pointsData = new ArrayList<>();
        if (!containsValueCheck(AgentConstants.CONTROLLER_TYPE, paginationContext)) {
            LOGGER.info("Exception occurred, controllerType->" + paginationContext.get(AgentConstants.CONTROLLER_TYPE) + " is mandatory ");
            return pointsData;
        }
        ModuleCRUDBean bean;
        try {
            bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentOrg().getOrgId());
            Criteria criteria = new Criteria();
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getPointFields());
            if (containsValueCheck(AgentConstants.CONTROLLER_ID, paginationContext) && checkValue((Long) paginationContext.get(AgentConstants.CONTROLLER_ID))) {
                if (fieldsMap.containsKey(AgentConstants.CONTROLLER_ID)) {
                    criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.CONTROLLER_ID), String.valueOf(paginationContext.get(AgentConstants.CONTROLLER_ID)), NumberOperators.EQUALS));
                }
            }
            if (containsValueCheck(AgentConstants.ASSET_CATEGORY_ID, paginationContext) && (checkValue((Long) paginationContext.get(AgentConstants.ASSET_CATEGORY_ID)))) {
                if (fieldsMap.containsKey(AgentConstants.ASSET_CATEGORY_ID)) {
                    criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.ASSET_CATEGORY_ID), String.valueOf(paginationContext.get(AgentConstants.ASSET_CATEGORY_ID)), NumberOperators.EQUALS));
                }
            }
            if (containsValueCheck(AgentConstants.FIELD_ID, paginationContext) && checkValue((Long) paginationContext.get(AgentConstants.FIELD_ID))) {
                if (fieldsMap.containsKey(AgentConstants.FIELD_ID)) {
                    criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.FIELD_ID), String.valueOf(paginationContext.get(AgentConstants.FIELD_ID)), NumberOperators.EQUALS));
                }
            }
            if (containsValueCheck(AgentConstants.POINT_IDS, paginationContext)) {
                List<Long> ids = (List<Long>) paginationContext.get(AgentConstants.POINT_IDS);
                if (!ids.isEmpty()) {
                    criteria.addAndCondition(CriteriaAPI.getIdCondition(ids, MODULE));
                }
            }
            if (containsValueCheck(AgentConstants.DEVICE_ID, paginationContext) && checkValue((Long) paginationContext.get(AgentConstants.DEVICE_ID))) {
                criteria.addAndCondition(CriteriaAPI.getCondition(FIELD_MAP.get(AgentConstants.DEVICE_ID), String.valueOf(paginationContext.get(AgentConstants.DEVICE_ID)), NumberOperators.EQUALS));
            }
            criteria.addAndCondition(CriteriaAPI.getCondition(FIELD_MAP.get(AgentConstants.ID), "0", NumberOperators.GREATER_THAN));
            List<FacilioField> fields = FieldFactory.getPointFields();
            getPointTypeBasedConditionAndFields((FacilioControllerType) paginationContext.get(AgentConstants.CONTROLLER_TYPE), paginationContext);
            if (paginationContext != null && (!paginationContext.isEmpty())) {
                paginationContext.put(FacilioConstants.ContextNames.CRITERIA, criteria);
                paginationContext.put(FacilioConstants.ContextNames.TABLE_NAME, AgentConstants.POINTS_TABLE);
                ((List<FacilioField>) paginationContext.get(FacilioConstants.ContextNames.FIELDS)).addAll(fields);
                pointsData = bean.getRows(paginationContext);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ", e);
        }
        LOGGER.info(" point data ->" + pointsData);
        return pointsData;
    }

    public static List<Point> getPointFromRows(List<Map<String, Object>> points) {
        LOGGER.info(" getpoints from rows point data " + points);
        List<Point> pointList = new ArrayList<>();
        for (Map<String, Object> point : points) {
            try {
                pointList.add(PointsAPI.getPointFromJSON(point));
            } catch (Exception e) {
                LOGGER.info("Exception occurred while making point from row ", e);
            }
        }
        return pointList;
    }

    private static FacilioContext getPointTypeBasedConditionAndFields(FacilioControllerType controllerType) throws Exception {
        FacilioContext context = new FacilioContext();
        getPointTypeBasedConditionAndFields(controllerType, context);
        return context;
    }

    private static void getPointTypeBasedConditionAndFields(FacilioControllerType controllerType, FacilioContext context) throws Exception {
        switch (controllerType) {
            case BACNET_MSTP:
                LOGGER.info("Implementation for BACNET_MSTP, not found ");
                //throw  new Exception("Implementation for BACNET_MSTP, not found ");
            case MODBUS_RTU:
                context.put(FacilioConstants.ContextNames.INNER_JOIN, ModuleFactory.getModbusRtuPointModule().getTableName());
                context.put(FacilioConstants.ContextNames.ON_CONDITION, AgentConstants.POINTS_TABLE + ".ID=" + ModuleFactory.getModbusRtuPointModule().getTableName() + ".ID");
                context.put(FacilioConstants.ContextNames.FIELDS, FieldFactory.getModbusRtuPointFields());
                break;
            case OPC_UA:
                context.put(FacilioConstants.ContextNames.INNER_JOIN, ModuleFactory.getOPCUAPointModule().getTableName());
                context.put(FacilioConstants.ContextNames.ON_CONDITION, AgentConstants.POINTS_TABLE + ".ID=" + ModuleFactory.getOPCUAPointModule().getTableName() + ".ID");
                context.put(FacilioConstants.ContextNames.FIELDS, FieldFactory.getOPCUAPointFields());
                break;
            case OPC_XML_DA:
                context.put(FacilioConstants.ContextNames.INNER_JOIN, ModuleFactory.getOPCXmlDAPointModule().getTableName());
                context.put(FacilioConstants.ContextNames.ON_CONDITION, AgentConstants.POINTS_TABLE + ".ID=" + ModuleFactory.getOPCXmlDAPointModule().getTableName() + ".ID");
                context.put(FacilioConstants.ContextNames.FIELDS, FieldFactory.getOPCXmlDAPointFields());
                break;
            case BACNET_IP:
                context.put(FacilioConstants.ContextNames.INNER_JOIN, ModuleFactory.getBACnetIPPointModule().getTableName());
                context.put(FacilioConstants.ContextNames.ON_CONDITION, AgentConstants.POINTS_TABLE + ".ID=" + ModuleFactory.getBACnetIPPointModule().getTableName() + ".ID");
                context.put(FacilioConstants.ContextNames.FIELDS, FieldFactory.getBACnetIPPointFields());
                break;
            case MODBUS_IP:
                context.put(FacilioConstants.ContextNames.INNER_JOIN, ModuleFactory.getModbusTcpPointModule().getTableName());
                context.put(FacilioConstants.ContextNames.ON_CONDITION, AgentConstants.POINTS_TABLE + ".ID=" + ModuleFactory.getModbusTcpPointModule().getTableName() + ".ID");
                context.put(FacilioConstants.ContextNames.FIELDS, FieldFactory.getModbusTcpPointFields());
                break;
            case MISC:
                context.put(FacilioConstants.ContextNames.INNER_JOIN, ModuleFactory.getMiscPointModule().getTableName());
                context.put(FacilioConstants.ContextNames.ON_CONDITION, AgentConstants.POINTS_TABLE + ".ID=" + ModuleFactory.getMiscPointModule().getTableName() + ".ID");
                context.put(FacilioConstants.ContextNames.FIELDS, FieldFactory.getMiscPointFields());
                break;
            case NIAGARA:
                context.put(FacilioConstants.ContextNames.INNER_JOIN, ModuleFactory.getNiagaraPointModule().getTableName());
                context.put(FacilioConstants.ContextNames.ON_CONDITION, AgentConstants.POINTS_TABLE + ".ID=" + ModuleFactory.getNiagaraPointModule().getTableName() + ".ID");
                context.put(FacilioConstants.ContextNames.FIELDS, FieldFactory.getNiagaraPointFields());
                break;
            case LON_WORKS:
                LOGGER.info("Implementation for LON_WORKS, not found ");
                // throw  new Exception("Implementation for LON_WORKS, not found ");
            case KNX:
                LOGGER.info("Implementation for KNX, not found ");
                //throw  new Exception("Implementation for KNX, not found ");

            default:
                LOGGER.info(" No implementation for " + controllerType.asString());
                //throw new Exception("FacilioControler type didnt match with cases "+controllerType.toString());
        }
    }

    public static boolean addPoint(Point point) {
        FacilioChain addPointChain = FacilioChainFactory.getAddPointChain();
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


    /*public static Point getPoint(String pointName, List<Long> pointId, long controllerId, FacilioControllerType controllerType) {
        try {
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentOrg().getOrgId());
            Criteria criteria = new Criteria();
            FacilioModule module = ModuleFactory.getPointModule();
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getPointFields());
            criteria.addAndCondition(CriteriaAPI.getNameCondition(pointName, module));
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.CONTROLLER_ID), String.valueOf(controllerId), NumberOperators.EQUALS));
            List<FacilioField> fields = FieldFactory.getPointFields();
            FacilioContext context = getPointTypeBasedConditionAndFields(controllerType);
            if (context != null) {
                context.put(FacilioConstants.ContextNames.CRITERIA, criteria);
                context.put(FacilioConstants.ContextNames.TABLE_NAME, AgentConstants.POINTS_TABLE);
                ((List<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS)).addAll(fields);
                List<Map<String, Object>> pointsData = bean.getRows(context);
                if (pointsData.size() == 1) {
                    List<Point> points = getPointFromRows(pointsData);
                    return points.get(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
*/
    public static void configurePointsAndMakeController(List<Long> pointIds, FacilioControllerType controllerType) throws Exception {
        LOGGER.info("configure controller and points ");
        if ((pointIds != null) && (!pointIds.isEmpty())) {
            List<Point> points = getPoints(pointIds, controllerType);
            LOGGER.info("got points" + points);
            if (points != null && (!points.isEmpty())) {
                Long deviceId = points.get(0).getDeviceId();
                FacilioChain chain = TransactionChainFactory.getConfigurePointAndProcessControllerV2Chain();
                FacilioContext context = chain.getContext();
                context.put(AgentConstants.ID, deviceId);
                context.put(AgentConstants.POINTS, points);
                context.put(AgentConstants.RECORD_IDS, pointIds);
                context.put(AgentConstants.TYPE, controllerType);
                chain.execute();
                //sendConfigurePointCommand(points,controllerType);
            } else {
                throw new Exception(" No points for ids->" + pointIds);
            }
        } else {
            throw new Exception("pointIds can't be null or empty ->" + pointIds);
        }
    }

    private static void sendConfigurePointCommand(List<Point> points, FacilioControllerType controllerType) throws Exception {
        ControllerMessenger.configurePoints(points, controllerType);
    }

    public static boolean configurePoints(List<Point> points, Controller controller) throws Exception {
        ControllerMessenger.configurePoints(points, FacilioControllerType.valueOf(controller.getControllerType()));
        if ((points != null) && (!points.isEmpty())) {
            List<Long> pointIds = new ArrayList<>();
            for (Point point : points) {
                pointIds.add(point.getId());
            }
            return updatePointsConfiguredInprogress(pointIds, controller.getId(), controller.getControllerType());
        } else {
            throw new Exception("points can't be null or empty");
        }
    }

    public static boolean updatePointsConfigurationComplete(List<Long> pointIds) throws Exception {
        LOGGER.info(" making configuration complete ->" + pointIds);
        if ((pointIds != null) && (!pointIds.isEmpty())) {
            FacilioChain editChain = TransactionChainFactory.getEditPointChain();
            FacilioContext context = editChain.getContext();
            context.put(FacilioConstants.ContextNames.CRITERIA, getIdCriteria(pointIds));
            context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP, Collections.singletonMap(AgentConstants.CONFIGURE_STATUS, PointEnum.ConfigureStatus.CONFIGURED.getIndex()));
            editChain.execute();
            if (context.containsKey(FacilioConstants.ContextNames.ROWS_UPDATED) && ((Integer) context.get(FacilioConstants.ContextNames.ROWS_UPDATED) > 0)) {
                return true;
            }
        }
        return false;
    }

    public static boolean updatePointsConfiguredInprogress(List<Long> ids, long controllerId, int controllerType) throws Exception {
        if ((ids != null) && (!ids.isEmpty())) {

            Map<String, Object> toUpdateMap = new HashMap<>();
            toUpdateMap.put(AgentConstants.CONFIGURE_STATUS, PointEnum.ConfigureStatus.IN_PROGRESS.getIndex());
            toUpdateMap.put(AgentConstants.CONTROLLER_ID, controllerId);

            FacilioChain chain = TransactionChainFactory.updatePointsConfigured();
            FacilioContext context = chain.getContext();
            context.put(AgentConstants.POINT_IDS, ids);
            context.put(AgentConstants.POINT_TYPE, controllerType);
            context.put(AgentConstants.CONTROLLER_ID, controllerId);

            chain.execute();

            LOGGER.info(" before check " + context);
            if (context.containsKey(FacilioConstants.ContextNames.ROWS_UPDATED) && ((Integer) context.get(FacilioConstants.ContextNames.ROWS_UPDATED) > 0)) {
                LOGGER.info(" returning true");
                return true;
            }
        } else {
            throw new Exception(" pointId cant be less than 1");
        }
        return false;
    }

    public static boolean unConfigurePointsChain(List<Long> pointIds, FacilioControllerType type) throws Exception {
        FacilioChain chain = TransactionChainFactory.unconfigurePointsChain();
        FacilioContext context = chain.getContext();
        context.put(AgentConstants.POINT_IDS, pointIds);
        context.put(AgentConstants.TYPE, type);
        chain.execute();
        return true;
    }


    public static boolean updatePointSubsctiptionComplete(List<Long> pointIds) throws Exception {
        LOGGER.info(" making subscription complete ->" + pointIds);
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

    public static boolean UpdatePointUnConfigured(Long pointId) throws Exception {
        return UpdatePointsUnConfigured(Collections.singletonList(pointId));
    }

    private static Criteria getIdCriteria(List<Long> pointIds) {
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getIdCondition(pointIds, ModuleFactory.getPointModule()));
        return criteria;
    }


    public static long getPointsCount(long controllerId, long deviceId) {
        try {
            Criteria criteria = new Criteria();
            if (controllerId > 0L) {
                LOGGER.info(FIELD_MAP.get(AgentConstants.CONTROLLER_ID));
                criteria.addAndCondition(CriteriaAPI.getCondition(FIELD_MAP.get(AgentConstants.CONTROLLER_ID), String.valueOf(controllerId), NumberOperators.EQUALS));
            }
            if (deviceId > 0L) {
                LOGGER.info(FIELD_MAP.get(AgentConstants.CONTROLLER_ID));
                criteria.addAndCondition(CriteriaAPI.getCondition(FIELD_MAP.get(AgentConstants.DEVICE_ID), String.valueOf(deviceId), NumberOperators.EQUALS));
            }
            criteria.addAndCondition(CriteriaAPI.getCondition(FIELD_MAP.get(AgentConstants.ID), String.valueOf(0), NumberOperators.GREATER_THAN));
            return getPointsCount(criteria);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while getting agent points count", e);
        }
        return 0;
    }

    private static long getPointsCount(Criteria criteria) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(MODULE.getTableName())
                .select(new HashSet<>())
                .aggregate(BmsAggregateOperators.CommonAggregateOperator.COUNT, FIELD_MAP.get(AgentConstants.ID));
        if (criteria != null) {
            builder.andCriteria(criteria);
        }
        LOGGER.info(" select query - " + builder.toString());
        List<Map<String, Object>> result = builder.get();
        if (result != null && (!result.isEmpty())) {
            return (long) result.get(0).get(AgentConstants.ID);
        }
        return 0;
    }

    public static List<Point> getDevicePoints(Long deviceId, int type) {
        return getDevicePoints(deviceId, type, null);
    }

    public static List<Point> getDevicePoints(Long deviceId, int type, FacilioContext paginationContext) {
        List<Map<String, Object>> records = getDevicePointsAsMapList(deviceId, type, null);
        if (!records.isEmpty()) {
            try {
                return getPointFromRows(records);
            } catch (Exception e) {
                LOGGER.info("Exception occurred while making point" + e.getMessage());
            }
        }
        return new ArrayList<>();
    }

    public static List<Map<String, Object>> getDevicePointsAsMapList(Long deviceId, int type, FacilioContext paginationcontext) {
        if ((deviceId != null) && (deviceId > 0) && (type > 0)) {
            return getAllPoints(FacilioControllerType.valueOf(type), null, -1, -1, -1, deviceId,paginationcontext);
        } else {
            LOGGER.info("Exception while getting points for device->" + deviceId + " and of type->" + type);
        }
       /* if ((deviceId != null) && (deviceId > 0)) {
            try {
                GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                        .table(MODULE.getTableName())
                        .select(FieldFactory.getPointFields())
                        .andCondition(CriteriaAPI.getCondition(FIELD_MAP.get(AgentConstants.DEVICE_ID), String.valueOf(deviceId), NumberOperators.EQUALS));
                return builder.get();
            } catch (Exception e) {
                LOGGER.info("Exception occurred while fetching points foe the device ->" + deviceId);
            }
        }
        return new ArrayList<>();*/
        return new ArrayList<>();
    }

    public static Class getPointType(FacilioControllerType type) throws Exception {
        switch (type) {
            case NIAGARA:
                return NiagaraPointContext.class;
            case REST:
            case CUSTOM:
            case OPC_UA:
                return OpcUaPointContext.class;
            case KNX:
            case MISC:
                return MiscPoint.class;
            case BACNET_IP:
                return BacnetIpPointContext.class;
            case LON_WORKS:
            case MODBUS_IP:
                return ModbusTcpPointContext.class;
            case MODBUS_RTU:
                return ModbusRtuPointContext.class;
            case OPC_XML_DA:
                return OpcXmlDaPointContext.class;
            case BACNET_MSTP:
            default:
                throw new Exception(" No implementation for " + type.asString() + " point");

        }
    }

    public static boolean deletePointsChain(List<Long> pointIds, FacilioControllerType type) {
        LOGGER.info(" in delete points ");
        try {
            FacilioChain chain = TransactionChainFactory.deletepointsChain();
            FacilioContext context = chain.getContext();
            context.put(AgentConstants.POINT_IDS, pointIds);
            context.put(AgentConstants.TYPE, type);
            chain.execute();
            return true;
        } catch (Exception e) {
            LOGGER.info("Exception while deleting points");
        }
        return false;
    }

    public static boolean deletePointChain(long pointId, FacilioControllerType type) {
        if (pointId > 0) {
            return deletePointsChain(Collections.singletonList(pointId), type);
        } else {
            LOGGER.info("Exception while deleting point, pointId cant be less than 1");
        }
        return false;
    }

    public static boolean executeDeletePoints(List<Long> pointIds) throws SQLException {
        //TODO -
        if ((pointIds != null) && (!pointIds.isEmpty())) {
            LOGGER.info(" executing delete ");
            GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                    .table(MODULE.getTableName())
                    .fields(FieldFactory.getPointFields())
                    .andCondition(CriteriaAPI.getIdCondition(pointIds, MODULE));
            Map<String, Object> toUpdateMap = new HashMap<>();
            toUpdateMap.put(AgentConstants.DELETED_TIME, System.currentTimeMillis());
            int rowsAffected = builder.update(toUpdateMap);
            LOGGER.info(" query " + builder.toString());
            if (rowsAffected > 0) {
                return true;
            }
        }
        return false;
    }

    public static boolean setValue(long pointid, FacilioControllerType type, Object value) throws Exception {
        Point point = getpoint(pointid, type);
        LOGGER.info(" point ->" + point);
        if (point != null) {
            LOGGER.info(" point's cid ->" + point.getControllerId());
            point.setValue(value);
            ControllerMessenger.setValue(point);
            return true;
        }
        return false;
    }

    public static Point getpoint(long pointid, FacilioControllerType type) {
        try {
            List<Point> points = getpoints(Collections.singletonList(pointid), type);
            if (!points.isEmpty()) {
                return points.get(0);
            }
        } catch (Exception e) {
            LOGGER.info("Exception while getting point ", e);
        }
        return null;
    }

    public static List<Point> getpoints(List<Long> pointids, FacilioControllerType type) {
        try {
            List<Map<String, Object>> pointData = getPointData(pointids, type);
            List<Point> points = getPointFromRows(pointData);
            if (!points.isEmpty()) {
                return points;
            }
        } catch (Exception e) {
            LOGGER.info("Exception while getting points", e);
        }
        return new ArrayList<>();
    }

    public static boolean resetConfiguredPoints(Long controllerId) throws SQLException {
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(MODULE.getTableName())
                .fields(FieldFactory.getPointFields())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(MODULE), String.valueOf(controllerId), NumberOperators.EQUALS));
        Map<String, Object> toUpdate = new HashMap<>();
        toUpdate.put(AgentConstants.CONFIGURE_STATUS, PointEnum.ConfigureStatus.UNCONFIGURED.getIndex());
        int rowsAffected = builder.update(toUpdate);
        return (rowsAffected > 0);
    }

    public static boolean subscribeUnsubscribePoints(List<Long> pointIds, FacilioControllerType type, FacilioCommand command) throws Exception {
        LOGGER.info(" - - - sub unsub");
        FacilioChain chain = TransactionChainFactory.subscribeUnsbscribechain();
        FacilioContext context = chain.getContext();
        context.put(AgentConstants.POINT_IDS, pointIds);
        context.put(AgentConstants.TYPE, type);
        context.put(AgentConstants.COMMAND, command);
        chain.execute();
        return true;
    }

    public static boolean updatePointsSubscribedOrUnsubscribed(List<Long> pointIds, FacilioCommand command) throws Exception {
        if ((pointIds != null) && (!pointIds.isEmpty())) {
            FacilioChain editChain = TransactionChainFactory.getEditPointChain();
            FacilioContext context = editChain.getContext();
            context.put(FacilioConstants.ContextNames.CRITERIA, getIdCriteria(pointIds));
            if (command == FacilioCommand.SUBSCRIBE) {
                LOGGER.info(" updated to sub");
                context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP, Collections.singletonMap(AgentConstants.SUBSCRIBE_STATUS, PointEnum.SubscribeStatus.IN_PROGRESS.getIndex()));
            } else if (command == FacilioCommand.UNSUBSCRIBE) {
                LOGGER.info(" updated to un sub");
                context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP, Collections.singletonMap(AgentConstants.SUBSCRIBE_STATUS, PointEnum.SubscribeStatus.UNSUBSCRIBED.getIndex()));
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
        LOGGER.info("GET_POINTS_FROM_DB: " + pointNames);
        LOGGER.info("GET_POINTS_FROM_DB: " + controller.getId());

        if (controller != null) {
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(ModuleFactory.getPointModule()), String.valueOf(controller.getId()), NumberOperators.EQUALS));
        } else {
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(ModuleFactory.getPointModule()), CommonOperators.IS_EMPTY));
        }
        criteria.addAndCondition(CriteriaAPI.getNameCondition(String.join(",", pointNames), ModuleFactory.getPointModule()));

        List<Map<String, Object>> pointsFromDb = getPointData(criteria);
        LOGGER.info("POINTS_FROM_DB :" + pointsFromDb);
        return pointsFromDb;
    }

    private static List<Map<String, Object>> getPointData(Criteria criteriaList) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(ModuleFactory.getPointModule().getTableName())
                .select(FieldFactory.getPointFields())
                .andCriteria(criteriaList);
        List<Map<String, Object>> res = builder.get();
        LOGGER.info(builder.toString());
        return res;
    }


    public static List<MiscPoint> getPointsSuperficial(Long agentId) throws Exception {
        Set<Long> controllerIds = new HashSet<>();
        if (agentId > 0) {
            controllerIds = ControllerApiV2.getControllerIds(agentId).keySet();
        }
        return getPointsSuperficial(controllerIds);
    }

    private static List<MiscPoint> getPointsSuperficial(Set<Long> controllerIds) throws Exception {
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(MODULE.getTableName())
                .select(FieldFactory.getPointFields())
                .andCondition(AgentApiV2.getDeletedTimeNullCondition(MODULE));
        if( ! controllerIds.isEmpty()){
            builder.andCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(MODULE), controllerIds, NumberOperators.EQUALS));
        }else {
            return new ArrayList<>();
        }
        List<Map<String, Object>> data = builder.get();
        LOGGER.info(" data " + data);
        return FieldUtil.getAsBeanListFromMapList(data, MiscPoint.class);
    }

    public static JSONObject getControllerPointsCountData(Long controllerId)throws Exception{
        List<MiscPoint> points = getPointsSuperficial(new HashSet<>(Arrays.asList(controllerId)));
        return getPointCountDataJSON(points);
    }
    public static JSONObject getPointsCountData(Long agentId) throws Exception {
        List<MiscPoint> points = getPointsSuperficial(agentId);
        return getPointCountDataJSON( points);
    }

    private static JSONObject getPointCountDataJSON( List<MiscPoint> points) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        JSONObject pointCountData = new JSONObject();
        int subPts = 0;
        int confPts = 0;
        int commPts = 0;
        for (Point point : points) {
            LOGGER.info(" point id" + point.getId());
            LOGGER.info(" point " + FieldUtil.getAsJSON(point));
            LOGGER.info(" point sub" + point.getSubscribeStatus());
            if (point.getConfigureStatusEnum().equals(PointEnum.ConfigureStatus.CONFIGURED)) {
                confPts++;
                if (checkIfComissioned(point)) {
                    commPts++;
                }
            }
            LOGGER.info(" point subE" + point.getSubscribestatusEnum());
            if (point.getSubscribestatusEnum().equals(PointEnum.SubscribeStatus.SUBSCRIBED)) {
                subPts++;
                LOGGER.info(" yes subscribed " + subPts);
            }
        }
        pointCountData.put(AgentConstants.TOTAL_COUNT, points.size());
        pointCountData.put(AgentConstants.CONFIGURED_COUNT, confPts);
        pointCountData.put(AgentConstants.COMMISSIONED_COUNT, commPts);
        pointCountData.put(AgentConstants.SUBSCRIBED_COUNT, subPts);
        return pointCountData;
    }

    private static boolean checkIfComissioned(Point point) {
        return ((point.getResourceId() != null) && (point.getAssetCategoryId() != null) && (point.getFieldId() != null));
    }
}
