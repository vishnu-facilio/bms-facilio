package com.facilio.agentv2.sqlitebuilder;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.bacnet.BacnetIpPointContext;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.sqlUtils.contexts.bacnet.BACnetPoint;
import com.facilio.sqlUtils.sqllite.SQLiteUtil;
import com.facilio.sqlUtils.utilities.FacilioJavaPoint;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class PointMigrator {
    private static final Logger LOGGER = LogManager.getLogger(PointMigrator.class.getName());

    public static boolean migratePoints(long agentId, Map<Long, FacilioControllerType> controllerIdsType) throws Exception {
        /*try {*/
        // List<Long> controllerIds = ControllerApiV2.getControllerIds(agentId,controllerType);
        LOGGER.info(" controller ids are " + controllerIdsType.keySet());
        for (Long controllerId : controllerIdsType.keySet()) {
            FacilioControllerType controllerType = controllerIdsType.get(controllerId);
            if (controllerType != null) {
                List<Point> points = PointsAPI.getControllerPoints(controllerType, controllerId);
                LOGGER.info(" controllerId -> "+controllerId+"    points -> "+points.size());
                addPointsToSqlite(points, controllerType);
            } else {
                LOGGER.info(" controllerType cant be null ->" + controllerId);
            }
        }
        return true;
    }


    private static void addPointsToSqlite(List<Point> points, FacilioControllerType controllerType) throws Exception {
        if ((points != null) && (!points.isEmpty())) {
            if (controllerType != null) {
                for (Point point : points) {
                    /*try {*/
                    if ( ! addPointToSqlite(point, controllerType)) {
                        LOGGER.info(" failed adding point "+point.getId());
                    }
                    /*catch (Exception e){
                        LOGGER.info(" exception while adding point of type "+controllerType+"    "+e);
                    }*/
                }
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
                break;
            case MODBUS_RTU:
                break;
            case OPC_UA:
                break;
            case OPC_XML_DA:
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
            return SQLiteUtil.addPoint(pointToInsert) > 0;
        }
        return false;
    }

    private static FacilioJavaPoint getAgentBacnetIpPoint(BacnetIpPointContext point) throws Exception {
        BACnetPoint agentPoint = new BACnetPoint(Math.toIntExact(point.getInstanceNumber()), point.getInstanceType());
        if (point != null) {
            toAgentPoint(agentPoint, point);
            return agentPoint;
        } else {
            throw new Exception(" point cant be null ");
        }
    }

    private static void toAgentPoint(BACnetPoint agentPoint, BacnetIpPointContext point) {
        agentPoint.setControllerId(point.getControllerId());
        agentPoint.setName(point.getName());
        agentPoint.setDataType(point.getDataType());
        agentPoint.setWritable(point.isWritable());
        agentPoint.setInUse(point.isInUse());
        agentPoint.setSubscribed(point.isSubscribed());
        agentPoint.setThresholdJSON(point.getThresholdJSON());
        agentPoint.setCreatedTime(point.getCreatedTime());
        agentPoint.setUnit(point.getUnit());
    }
}
