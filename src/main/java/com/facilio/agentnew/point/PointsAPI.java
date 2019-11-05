package com.facilio.agentnew.point;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentnew.AgentConstants;
import com.facilio.agentnew.JsonUtil;
import com.facilio.agentnew.bacnet.BacnetIpPoint;
import com.facilio.agentnew.misc.MiscPoint;
import com.facilio.agentnew.modbusrtu.ModbusRtuPoint;
import com.facilio.agentnew.modbustcp.ModbusTcpPoint;
import com.facilio.agentnew.niagara.NiagaraPoint;
import com.facilio.agentnew.opcua.OpcUaPoint;
import com.facilio.agentnew.opcxmlda.OpcXmlDaPoint;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PointsAPI
{
    private static final Logger LOGGER = LogManager.getLogger(PointsAPI.class.getName());

    public static Point getPointFromJSON(Map<String, Object> payload, long agentId, long controllerId) throws Exception {
        //LOGGER.info("iamcvijay point to json -> "+payload);
        if(containsValueCheck(AgentConstants.POINT_TYPE,payload)) {
            FacilioControllerType controllerType;
            controllerType = FacilioControllerType.valueOf(JsonUtil.getInt(payload.get(AgentConstants.POINT_TYPE)));
            //LOGGER.info(" controller type for point is " + controllerType.asString());
            if(payload.containsKey(AgentConstants.PSEUDO) ){
                if(JsonUtil.getBoolean(payload.get(AgentConstants.PSEUDO))){
                    controllerType = FacilioControllerType.MISC;
                }
            }
            switch (controllerType){
                case MODBUS_RTU:
                    return ModbusRtuPoint.getPointFromMap(agentId,controllerId,payload);
                case MODBUS_IP:
                    return ModbusTcpPoint.getPointFromMap(agentId,controllerId,payload);
                case OPC_UA:
                    return OpcUaPoint.getPointFromMap(agentId,controllerId,payload);
                case OPC_XML_DA:
                    return OpcXmlDaPoint.getPointFromMap(agentId,controllerId,payload);
                case BACNET_IP:
                    return BacnetIpPoint.getPointFromMap(agentId,controllerId,payload);
                case NIAGARA:
                    return NiagaraPoint.getPointFromMap(agentId,controllerId,payload);
                case MISC:
                    return MiscPoint.getPointFromMap(agentId,controllerId,payload);
                default:
                    throw new Exception("no implementation for "+controllerType.asString());
            }
        }
        throw new Exception("Point Type missing not defined -> "+payload);
    }


    public static List<Point> getAllPoints(long agentId, long controllerId){
        List<Point> allPoints = new ArrayList<>();
        for (FacilioControllerType type : FacilioControllerType.values()) {
            List<Point> pointList = getPointFromRows(getAllPoints(type, agentId,controllerId,-1,-1),agentId,controllerId);
            if( (pointList != null) && ( ! pointList.isEmpty()) ){
                allPoints.addAll(pointList);
            }
        }
        /*try {
            AgentMessenger.publishNewIotMessage(allPoints, FacilioCommand.CONFIGURE);
        } catch (Exception e) {
            LOGGER.info("Exception occurred while configuring points ",e);
        }*/
        return allPoints;
    }

    public static Point getPoint(long assetCategoryId, long fieldId, FacilioControllerType controllerType,long agentId,long controllerId){
        if( (assetCategoryId>0) && (fieldId>0) && (controllerType != null) && (agentId>0) && (controllerId>0)){
            List<Point> points = getPointFromRows(getAllPoints(controllerType, -1, -1, assetCategoryId, fieldId),agentId,controllerId);
            if(! points.isEmpty()){
                return points.get(0);
            }
        }
        return null;
    }


    private static List<Map<String, Object>> getAllPoints(FacilioControllerType controllerType, long agentId, long controllerId, long assetCategoryId, long fieldId){
        List<Map<String, Object>> pointsData = new ArrayList<>();
        if((agentId < 0) || (controllerId < 0) || (controllerType == null)){
            LOGGER.info("Exception occurred, agentId->"+agentId+", controllerId->"+controllerId+" and controllerType->"+controllerType+" is mandatory ");
            return pointsData;
        }
        ModuleCRUDBean bean = null;
        try {
            bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentOrg().getOrgId());
            Criteria criteria = new Criteria();
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getPointFields());
            if(controllerId > 0){
                if(fieldsMap.containsKey(AgentConstants.CONTROLLER_ID)){
                    criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.CONTROLLER_ID), String.valueOf(controllerId), NumberOperators.EQUALS));
                }
            }
            if(assetCategoryId > 0){
                if(fieldsMap.containsKey(AgentConstants.ASSET_CATEGORY_ID)){
                    criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.ASSET_CATEGORY_ID), String.valueOf(assetCategoryId),NumberOperators.EQUALS));
                }
            }
            if(fieldId > 0){
                if(fieldsMap.containsKey(AgentConstants.FIELD_ID)){
                    criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.FIELD_ID), String.valueOf(fieldId),NumberOperators.EQUALS));
                }
            }
            if(agentId > 0){
                if(fieldsMap.containsKey(AgentConstants.AGENT_ID)){
                    criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.AGENT_ID), String.valueOf(agentId),NumberOperators.EQUALS));
                }
            }
            List<FacilioField> fields = FieldFactory.getPointFields();
            FacilioContext context = getPointTypeBasedConditionAndFields(controllerType);
            if(context != null) {
                context.put(FacilioConstants.ContextNames.CRITERIA, criteria);
                context.put(FacilioConstants.ContextNames.TABLE_NAME, AgentConstants.POINTS_TABLE);
                ((List<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS)).addAll(fields);
                pointsData = bean.getRows(context);
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ",e);
        }
        return pointsData;
    }

    public static List<Point> getPointFromRows(List<Map<String, Object>>points,long agentId, long controllerId){
        LOGGER.info(" point data "+points);
        List<Point> pointList = new ArrayList<>();
        for (Map<String, Object> point : points) {
            try {
                pointList.add(PointsAPI.getPointFromJSON(point,agentId,controllerId));
            } catch (Exception e) {
                LOGGER.info("Exception occurred ",e);
            }
        }
        return pointList;
    }

    private static FacilioContext getPointTypeBasedConditionAndFields(FacilioControllerType controllerType) throws Exception {
        FacilioContext context = new FacilioContext();
        switch (controllerType){
            case BACNET_MSTP:
                LOGGER.info("Implementation for BACNET_MSTP, not found ");
                //throw  new Exception("Implementation for BACNET_MSTP, not found ");
            case MODBUS_RTU:
                context.put(FacilioConstants.ContextNames.INNER_JOIN, ModuleFactory.getModbusRtuPointModule().getTableName());
                context.put(FacilioConstants.ContextNames.ON_CONDITION,AgentConstants.POINTS_TABLE+".ID="+ModuleFactory.getModbusRtuPointModule().getTableName()+".ID");
                context.put(FacilioConstants.ContextNames.FIELDS,FieldFactory.getModbusRtuPointFields());
                break;
            case OPC_UA:
                context.put(FacilioConstants.ContextNames.INNER_JOIN, ModuleFactory.getOPCUAPointModule().getTableName());
                context.put(FacilioConstants.ContextNames.ON_CONDITION,AgentConstants.POINTS_TABLE+".ID="+ModuleFactory.getOPCUAPointModule().getTableName()+".ID");
                context.put(FacilioConstants.ContextNames.FIELDS,FieldFactory.getOPCUAPointFields());
                break;
            case OPC_XML_DA:
                context.put(FacilioConstants.ContextNames.INNER_JOIN, ModuleFactory.getOPCXmlDAPointModule().getTableName());
                context.put(FacilioConstants.ContextNames.ON_CONDITION,AgentConstants.POINTS_TABLE+".ID="+ModuleFactory.getOPCXmlDAPointModule().getTableName()+".ID");
                context.put(FacilioConstants.ContextNames.FIELDS,FieldFactory.getOPCXmlDAPointFields());
                break;
            case BACNET_IP:
                context.put(FacilioConstants.ContextNames.INNER_JOIN, ModuleFactory.getBACnetIPPointModule().getTableName());
                context.put(FacilioConstants.ContextNames.ON_CONDITION,AgentConstants.POINTS_TABLE+".ID="+ModuleFactory.getBACnetIPPointModule().getTableName()+".ID");
                context.put(FacilioConstants.ContextNames.FIELDS,FieldFactory.getBACnetIPPointFields());
                break;
            case MODBUS_IP:
                context.put(FacilioConstants.ContextNames.INNER_JOIN, ModuleFactory.getModbusTcpPointModule().getTableName());
                context.put(FacilioConstants.ContextNames.ON_CONDITION,AgentConstants.POINTS_TABLE+".ID="+ModuleFactory.getModbusTcpPointModule().getTableName()+".ID");
                context.put(FacilioConstants.ContextNames.FIELDS,FieldFactory.getModbusTcpPointFields());
                break;
            case MISC:
                context.put(FacilioConstants.ContextNames.INNER_JOIN, ModuleFactory.getMiscPointModule().getTableName());
                context.put(FacilioConstants.ContextNames.ON_CONDITION,AgentConstants.POINTS_TABLE+".ID="+ModuleFactory.getMiscPointModule().getTableName()+".ID");
                context.put(FacilioConstants.ContextNames.FIELDS,FieldFactory.getMiscPointFields());
                break;
            case NIAGARA:
                context.put(FacilioConstants.ContextNames.INNER_JOIN, ModuleFactory.getNiagaraPointModule().getTableName());
                context.put(FacilioConstants.ContextNames.ON_CONDITION,AgentConstants.POINTS_TABLE+".ID="+ModuleFactory.getNiagaraPointModule().getTableName()+".ID");
                context.put(FacilioConstants.ContextNames.FIELDS,FieldFactory.getNiagaraPointFields());
                break;
            case LON_WORKS:
                LOGGER.info("Implementation for LON_WORKS, not found ");
                return null;
               // throw  new Exception("Implementation for LON_WORKS, not found ");
            case KNX:
                LOGGER.info("Implementation for KNX, not found ");
                return null;
                //throw  new Exception("Implementation for KNX, not found ");

            default:
                LOGGER.info(" No implementation for " + controllerType.asString());
                return null;
                //throw new Exception("FacilioControler type didnt match with cases "+controllerType.toString());
        }
        return context;
    }

    public static boolean addPoint(Point point) {
        FacilioChain addPointChain = FacilioChainFactory.getAddPointChain();
        if (point != null) {
            addPointChain.getContext().put(FacilioConstants.ContextNames.RECORD,point);
            try {
                return addPointChain.execute();
            } catch (Exception e) {
                LOGGER.info("Exception occurred ",e);
            }
        }
        return false;
    }

    private static boolean containsValueCheck(String key, Map<String,Object> jsonObject){
        if(jsonObject.containsKey(key) && ( jsonObject.get(key) != null) ){
            return true;
        }
        return false;
    }

    public static Point getPoint(String pointName,long agentId, long controllerId, FacilioControllerType controllerType) {
        try {
            ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentOrg().getOrgId());
            Criteria criteria = new Criteria();
            FacilioModule module = ModuleFactory.getPointModule();
            Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getPointFields());
            criteria.addAndCondition(CriteriaAPI.getNameCondition(pointName,module));
            criteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.CONTROLLER_ID), String.valueOf(controllerId),NumberOperators.EQUALS));
            List<FacilioField> fields = FieldFactory.getPointFields();
            FacilioContext context = getPointTypeBasedConditionAndFields(controllerType);
            if(context != null) {
                context.put(FacilioConstants.ContextNames.CRITERIA, criteria);
                context.put(FacilioConstants.ContextNames.TABLE_NAME, AgentConstants.POINTS_TABLE);
                ((List<FacilioField>) context.get(FacilioConstants.ContextNames.FIELDS)).addAll(fields);
                List<Map<String, Object>> pointsData = bean.getRows(context);
                if(pointsData.size() == 1){
                    List<Point> points = getPointFromRows(pointsData, agentId, controllerId);
                    return points.get(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
