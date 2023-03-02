package com.facilio.agentv2.sqlitebuilder;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.bacnet.BacnetIpPointContext;
import com.facilio.agentv2.modbusrtu.ModbusRtuPointContext;
import com.facilio.agentv2.modbustcp.ModbusTcpPointContext;
import com.facilio.agentv2.opcua.OpcUaPointContext;
import com.facilio.agentv2.opcxmlda.OpcXmlDaPointContext;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointsAPI;
//import com.facilio.sqlUtils.contexts.bacnet.BACnetPoint;
//import com.facilio.sqlUtils.contexts.modbus.ModbusPoint;
//import com.facilio.sqlUtils.contexts.opc.ua.OpcUaPoint;
//import com.facilio.sqlUtils.contexts.opc.xmlda.OpcXmlDaPoint;
//import com.facilio.sqlUtils.sqllite.SQLiteUtil;
//import com.facilio.sqlUtils.utilities.FacilioJavaPoint;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class PointMigrator {
   /* private static final Logger LOGGER = LogManager.getLogger(PointMigrator.class.getName());

    public static boolean migratePoints(long agentId, Map<Long, FacilioControllerType> controllerIdsType) throws Exception {
        *//*try {*//*
        // List<Long> controllerIds = AgentConstants.getControllerBean().getControllerIds(agentId,controllerType);
        LOGGER.info(" controller ids are " + controllerIdsType.keySet());
        for (Long controllerId : controllerIdsType.keySet()) {
            FacilioControllerType controllerType = controllerIdsType.get(controllerId);
            if (controllerType != null) {
                List<Point> points = PointsAPI.getControllerPoints(controllerType, controllerId);
                LOGGER.info(" controllerId -> "+controllerId+"    points -> "+points);
                addPointsToSqlite(points, controllerType);
            } else {
                LOGGER.info(" controllerType cant be null ->" + controllerId);
            }
        }
        return true;
    }


    public static void addPointsToSqlite(List<Point> points, FacilioControllerType controllerType) throws Exception {
        if ((points != null) && (!points.isEmpty())) {
            if (controllerType != null) {
                StringBuilder pointsFailed = new StringBuilder();
                for (Point point : points) {
                    try {
                        if (!addPointToSqlite(point, controllerType)) {
                            LOGGER.info(" failed adding point " + point.getId());
                            pointsFailed.append(point.getId()+"-");
                        }
                    }catch (Exception e){
                        LOGGER.info(" exception while adding point of type "+controllerType+"    "+e);
                    }
                }
                LOGGER.info(" failed to add points "+pointsFailed.toString());
            } else {
                LOGGER.info(" controller type cant be null ");
            }
        } else {
            LOGGER.info(" point cant be null or empty to add to sqlite");
        }
    }

    private static boolean addPointToSqlite(Point point, FacilioControllerType controllerType) throws Exception {
        if (point == null) {
            throw new Exception(" point cant be null");
        }
        if (controllerType == null) {
            throw new Exception(" controllerType cant be null");
        }

        LOGGER.info(point.getId() + " adding point of type " + controllerType);
        FacilioJavaPoint pointToInsert = null;
        switch (controllerType) {
            case BACNET_IP:
                pointToInsert = getAgentBacnetIpPoint((BacnetIpPointContext) point);
                break;
            case MODBUS_IP:
                pointToInsert = getAgentMosbusIpPoint((ModbusTcpPointContext)point);
                break;
            case MODBUS_RTU:
                pointToInsert = getAgentModbusRtuPoint((ModbusRtuPointContext)point);
                break;
            case OPC_UA:
                pointToInsert = getAgentOpcUaPoint((OpcUaPointContext)point);
                break;
            case OPC_XML_DA:
                pointToInsert = getAgentOpcXmlDaPoints((OpcXmlDaPointContext)point);
                break;
            case NIAGARA:
            case BACNET_MSTP:
            case CUSTOM:
            case REST:
            case MISC:
            case KNX:
            case LON_WORKS:
                LOGGER.info(" No implemenration found for point of type " + controllerType.asString());
        }
        if (pointToInsert != null) {
            long newPointId = SQLiteUtil.addPoint(pointToInsert);
            LOGGER.info(pointToInsert.getControllerId()+" point added "+point.getId()+" as "+newPointId +" "+ (newPointId > 0));
            return  newPointId > 0;
        }
        return false;
    }

    private static FacilioJavaPoint getAgentMosbusIpPoint(ModbusTcpPointContext point) throws Exception {
        if(point != null){
            ModbusPoint agentPoint = new ModbusPoint(point.getControllerId());
            agentPoint.setOffset(point.getRegisterNumber());
            agentPoint.setModbusDatatype(point.getModbusDataType());
            agentPoint.setRegisterType(point.getRegisterType());
            return agentPoint;
        }else {
            throw new Exception(" point cant be null ");
        }
    }

    private static FacilioJavaPoint getAgentModbusRtuPoint(ModbusRtuPointContext point) throws Exception {
        if(point != null){
            ModbusPoint agentPoint = new ModbusPoint(point.getControllerId());
            agentPoint.setRegisterType(point.getRegisterType());
            agentPoint.setModbusDatatype(point.getModbusDataType());
            agentPoint.setOffset(point.getRegisterNumber());
            return agentPoint;
        }else {
            throw new Exception(" point cant be null ");
        }
    }

    private static FacilioJavaPoint getAgentOpcUaPoint(OpcUaPointContext point) throws Exception {
        if(point != null){
            OpcUaPoint agentPoint = new OpcUaPoint();
            agentPoint.setUaPointIdentifier(point.getUaPointIdentifier());
            agentPoint.setNamespace(point.getNamespace());
            toAgentPoint(agentPoint,point);
            return agentPoint;
        }else {
            throw new Exception(" point cant be null ");
        }
    }

    private static FacilioJavaPoint getAgentOpcXmlDaPoints(OpcXmlDaPointContext point) throws Exception {
        if(point != null){
            OpcXmlDaPoint agentPoint = new OpcXmlDaPoint();
            agentPoint.setPath(point.getPath());
            toAgentPoint(agentPoint,point);
            return agentPoint;
        }else{
            throw new Exception(" point cant be null ");
        }
    }

    private static FacilioJavaPoint getAgentBacnetIpPoint(BacnetIpPointContext point) throws Exception {
        if (point != null) {
            BACnetPoint agentPoint = new BACnetPoint(Math.toIntExact(point.getInstanceNumber()), point.getInstanceType());
            toAgentPoint(agentPoint, point);
            return agentPoint;
        } else {
            throw new Exception(" point cant be null ");
        }
    }

    private static void toAgentPoint(FacilioJavaPoint agentPoint, Point point) {
        agentPoint.setControllerId(point.getControllerId());
        agentPoint.setName(point.getName());
        agentPoint.setDataType(point.getDataType());
        agentPoint.setWritable(point.isWritable());
        if(point.getSubscribeStatus() == 3){
            agentPoint.setSubscribed(true);
        }
        if(point.getConfigureStatus() == 3){
            agentPoint.setInUse(true);
        }
        agentPoint.setThresholdJSON(point.getThresholdJSON());
        agentPoint.setCreatedTime(point.getCreatedTime());
        agentPoint.setUnit(point.getUnit());
    }

    public static void setNewControllerId(long controllerId, List<Point> points) {
        for (Point point : points) {
            point.setControllerId(controllerId);
        }
    }*/
}
