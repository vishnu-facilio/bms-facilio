package com.facilio.agentv2.point;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.fw.constants.FacilioCommand;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
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
import com.facilio.agentv2.system.SystemPointContext;
import com.facilio.bacnet.BACNetUtil;
import com.facilio.beans.ModuleCRUDBean;
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
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;

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
            case SYSTEM:
                return FieldFactory.getSystemPointFields();
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
            if (!pointData.isEmpty()) {
                List<Point> pointList = getPointFromRows(pointData);
                if ((pointList != null) && (!pointList.isEmpty())) {
                    allPoints.addAll(pointList);
                }
            }
        }
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
        if(paginationContext == null){
            paginationContext = new FacilioContext();
        }
        paginationContext.put(AgentConstants.CONTROLLER_TYPE, type);
        paginationContext.put(AgentConstants.POINT_IDS, pointIds);
        paginationContext.put(AgentConstants.CONTROLLER_ID, controllerId);
        paginationContext.put(AgentConstants.FIELD_ID, fieldId);
        paginationContext.put(AgentConstants.ASSET_CATEGORY_ID, assetCategoryId);
        paginationContext.put(AgentConstants.DEVICE_ID, deviceId);
        return fetchPoints(paginationContext);
    }


    private static List<Map<String, Object>> fetchPoints(FacilioContext paginationContext) {
        FacilioModule pointModule = ModuleFactory.getPointModule();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getPointFields());
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
                    criteria.addAndCondition(CriteriaAPI.getIdCondition(ids, pointModule));
                }
            }
            if (containsValueCheck(AgentConstants.DEVICE_ID, paginationContext) && checkValue((Long) paginationContext.get(AgentConstants.DEVICE_ID))) {
                criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.DEVICE_ID), String.valueOf(paginationContext.get(AgentConstants.DEVICE_ID)), NumberOperators.EQUALS));
            }
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentConstants.ID), "0", NumberOperators.GREATER_THAN));
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
        return pointsData;
    }

    public static List<Point> getPointFromRows(List<Map<String, Object>> points) {
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
                // throw  new Exception("Implementation for LON_WORKS, not found ");
            case KNX:
                //throw  new Exception("Implementation for KNX, not found ");

            default:
                //throw new Exception("FacilioControler type didnt match with cases "+controllerType.toString());
        }
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


    public static void configurePointsAndMakeController(List<Long> pointIds, FacilioControllerType controllerType) throws Exception {
        if ((pointIds != null) && (!pointIds.isEmpty())) {
            List<Point> points = new GetPointRequest().ofType(controllerType).fromIds(pointIds).getPoints();
            if (points != null && (!points.isEmpty())) {
                Long deviceId = points.get(0).getDeviceId();
                if(deviceId < 0){
                    throw new Exception("Point's device Id can't be less than 1");
                }
                FacilioChain chain = TransactionChainFactory.getConfigurePointAndProcessControllerV2Chain();
                FacilioContext context = chain.getContext();
                context.put(AgentConstants.ID, deviceId);
                context.put(AgentConstants.POINTS, points);
                context.put(AgentConstants.RECORD_IDS, pointIds);
                context.put(AgentConstants.CONTROLLER_TYPE, controllerType);
                chain.execute();
                //sendConfigurePointCommand(points,controllerType);
            } else {
                throw new Exception(" No points for ids->" + pointIds);
            }
        } else {
            throw new Exception("pointIds can't be null or empty ->" + pointIds);
        }
    }

    private static void sendConfigurePointCommand(List<Point> points, Controller controller) throws Exception {
        ControllerMessenger.configurePoints(points, controller);
    }

    public static void handlePointConfigurationAndSubscription(FacilioCommand command,List<Long> pointIds) throws Exception {
        if ((command != FacilioCommand.SUBSCRIBE) && (command != FacilioCommand.CONFIGURE)) {
            return;
        }
        if (!pointIds.isEmpty()) {
                switch (command) {
                    case CONFIGURE:
                        PointsAPI.updatePointsConfigurationComplete(pointIds);
                        return;
                    case SUBSCRIBE:
                        PointsAPI.updatePointSubsctiptionComplete(pointIds);
                        return;
                    default:
                        LOGGER.info(" no update for command ->" + command.toString());
                        return;
                }
            } else {
                throw new Exception(" point ids cant be empty while ack processing for->" + command.toString());
            }
    }

    public static boolean configurePoints(List<Point> points, Controller controller) throws Exception {
        ControllerMessenger.configurePoints(points, controller);
        if ((points != null) && (!points.isEmpty())) {
            List<Long> pointIds = new ArrayList<>();
            for (Point point : points) {
                pointIds.add(point.getId());
            }
            try {
                updatePointsControllerId(controller);
            }catch (Exception e){
                LOGGER.info("Exception while updating point's controller id for device "+controller.getDeviceId());
            }
            return updatePointsConfiguredInprogress(pointIds, controller.getId(), controller.getControllerType());
        } else {
            throw new Exception("points can't be null or empty");
        }
    }

    private static void updatePointsControllerId(Controller controller) throws SQLException {
        Objects.requireNonNull(controller,"controllr can't be null");
        FacilioModule pointModule = ModuleFactory.getPointModule();
        GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
                .table(pointModule.getTableName())
                .fields(FieldFactory.getPointFields())
                .andCondition(CriteriaAPI.getCondition(FieldFactory.getFieldDeviceId(pointModule), String.valueOf(controller.getDeviceId()), NumberOperators.EQUALS));
        Map<String,Object> toUpdate = new HashMap<>();
        toUpdate.put(AgentConstants.CONTROLLER_ID,controller.getId());
        if(updateRecordBuilder.update(toUpdate)>0){
            LOGGER.info("____________________Updated points controllerId_____________________"+controller.getId());
        }
    }

    public static boolean updatePointsConfigurationComplete(List<Long> pointIds) throws Exception {
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

            if (context.containsKey(FacilioConstants.ContextNames.ROWS_UPDATED) && ((Integer) context.get(FacilioConstants.ContextNames.ROWS_UPDATED) > 0)) {
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
        context.put(AgentConstants.CONTROLLER_TYPE, type);
        chain.execute();
        return true;
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
        FacilioModule pointModule = ModuleFactory.getPointModule();
        if ((pointNames != null) && (!pointNames.isEmpty())) {
            FacilioChain editChain = TransactionChainFactory.getEditPointChain();
            FacilioContext context = editChain.getContext();
            Criteria criteria = getNameCriteria(pointNames);
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(pointModule), String.valueOf(controllerId), NumberOperators.EQUALS));
            context.put(FacilioConstants.ContextNames.CRITERIA, criteria);
            context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP, Collections.singletonMap(AgentConstants.CONFIGURE_STATUS, PointEnum.ConfigureStatus.CONFIGURED.getIndex()));
            editChain.execute();
            if (context.containsKey(FacilioConstants.ContextNames.ROWS_UPDATED) && ((Integer) context.get(FacilioConstants.ContextNames.ROWS_UPDATED) > 0)) {
                return true;
            }
        }
        return false;
    }

    private static boolean updatePointSubsctiptionComplete(long controllerId,List<String> pointNames) throws Exception {
        FacilioModule pointModule = ModuleFactory.getPointModule();
        if ((pointNames != null) && (!pointNames.isEmpty())) {
            FacilioChain editChain = TransactionChainFactory.getEditPointChain();
            FacilioContext context = editChain.getContext();
            Criteria criteria = getNameCriteria(pointNames);
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

    private static Criteria getIdCriteria(List<Long> pointIds) {
        FacilioModule pointModule = ModuleFactory.getPointModule();
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getIdCondition(pointIds, pointModule));
        return criteria;
    }
    private static Criteria getNameCriteria(List<String> pointNames) {
        FacilioModule pointModule = ModuleFactory.getPointModule();
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(pointModule), StringUtils.join(pointNames, ","), StringOperators.IS));
        return criteria;
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
        GetPointRequest getPointRequest = new GetPointRequest().withId(pointid)
                .ofType(type);
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

    public static boolean subscribeUnsubscribePoints(List<Long> pointIds, FacilioControllerType type, FacilioCommand command) throws Exception {
        FacilioChain chain = TransactionChainFactory.subscribeUnsbscribechain();
        FacilioContext context = chain.getContext();
        context.put(AgentConstants.POINT_IDS, pointIds);
        context.put(AgentConstants.CONTROLLER_TYPE, type);
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
                context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP, Collections.singletonMap(AgentConstants.SUBSCRIBE_STATUS, PointEnum.SubscribeStatus.IN_PROGRESS.getIndex()));
            } else if (command == FacilioCommand.UNSUBSCRIBE) {
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

        if (controller != null) {
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(ModuleFactory.getPointModule()), String.valueOf(controller.getId()), NumberOperators.EQUALS));
        } else {
            criteria.addAndCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(ModuleFactory.getPointModule()), CommonOperators.IS_EMPTY));
        }
        criteria.addAndCondition(CriteriaAPI.getNameCondition(String.join(",", pointNames), ModuleFactory.getPointModule()));

        List<Map<String, Object>> pointsFromDb = getPointData(criteria);
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
        FacilioModule pointModule = ModuleFactory.getPointModule();
        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
                .table(pointModule.getTableName())
                .select(FieldFactory.getPointFields())
                .andCondition(AgentApiV2.getDeletedTimeNullCondition(pointModule));
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
    public static void applyBacnetDefaultWritableRule(Point point) {
        if(point.getControllerType() == FacilioControllerType.BACNET_IP){
            BacnetIpPointContext bacnetIpPoint = (BacnetIpPointContext) point;
            if(BACNetUtil.InstanceType.valueOf(bacnetIpPoint.getInstanceType()).isWritable()){
                point.setWritable(true);
            }
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
}
