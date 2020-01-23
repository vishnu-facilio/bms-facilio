package com.facilio.agentv2.sqlitebuilder;

import com.facilio.agent.AgentUtil;
import com.facilio.agent.FacilioAgent;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.controller.FacilioDataType;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.bacnet.BacnetIpControllerContext;
import com.facilio.agentv2.bacnet.BacnetIpPointContext;
import com.facilio.agentv2.commands.AgentV2Command;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.point.Point;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.timeseries.TimeSeriesAPI;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class AgentMigratorCommand extends AgentV2Command {

    private static final Logger LOGGER = LogManager.getLogger(AgentDataMigrationCommand.class.getName());


    @Override
    public boolean executeCommand(Context context) throws Exception {
        if (containsCheck(AgentConstants.AGENT_ID,context)) {
            long agentId = Long.parseLong(String.valueOf(context.get(AgentConstants.AGENT_ID)));
            LOGGER.info(" migrating to agentV2 "+agentId);
            com.facilio.agentv2.FacilioAgent newAgent = migrateAgentToV2(agentId);
            migrateControllersAndPointsToV2(agentId,newAgent);
        }else {
            throw new Exception(" agentId missing from context");
        }

        return false;
    }

    private void migrateControllersAndPointsToV2(long agentId, com.facilio.agentv2.FacilioAgent newAgent) throws Exception {
        List<ControllerContext> controllers = ControllerAPI.getControllers(agentId);
        if( ( controllers != null ) && ( ! controllers.isEmpty())){
            for (ControllerContext controller : controllers) {
                try {
                    Controller newController = null;
                    ControllerType controllerType = controller.getControllerTypeEnum();
                    switch (controllerType) {
                        case BACNET_IP:
                            newController = toBAcnetIpControllerV2(controller);
                            break;
                    }
                    if (newController != null) {
                        newController.setControllerType(controllerType.getKey());
                        newController.setAgentId(newAgent.getId());
                        newController.setSiteId(newAgent.getSiteId());
                        long newControllerId = ControllerApiV2.addController(newController, newAgent);
                        LOGGER.info(" --- migrated controller " + controller.getId() + " to " + newControllerId);
                        if (newControllerId > 0) {
                            migratePoints(controller, newControllerId);
                        }
                    }
                }catch (Exception e){
                    LOGGER.info("Exception while migrating controller "+controller.getId());
                }
            }
        }
    }


    private void migratePoints(ControllerContext controller, long newControllerId) {
        if(controller != null){
            if(newControllerId > 0){
                try {
                    LOGGER.info(" controller Id for points is "+controller.getId());
                    List<Map<String, Object>> points = TimeSeriesAPI.getPointsForController(controller.getId());
                    if( ! points.isEmpty()){
                        Point newPoint;
                        for (Map<String, Object> point : points) {
                            LOGGER.info(" point ->"+point);
                            try{
                                newPoint = null;
                                switch (controller.getControllerTypeEnum()){
                                    case BACNET_IP:
                                        newPoint = toBacnetIpPoints(point);
                                }
                                if(newPoint != null){
                                    newPoint.setControllerId(newControllerId);
                                    LOGGER.info(" point json "+newPoint.toJSON());
                                    LOGGER.info(" point cid "+newPoint.getControllerId());
                                    if (PointsAPI.addPoint(newPoint)) {
                                        if(point.containsKey(AgentConstants.ID)){
                                            LOGGER.info(" -- migrated point "+point.get(AgentConstants.ID));
                                        }else {
                                            LOGGER.info(" migrated point "+point);
                                        }
                                    }else {
                                        LOGGER.info(" failed to migrate point -> "+point);
                                    }
                                }
                            }catch (Exception e){
                                LOGGER.info(" Exception while migrating point to v2 ",e);
                            }
                        }
                    }
                } catch (Exception e) {
                    LOGGER.info("Exception occurred while migrating points",e);
                }
            }else {
                LOGGER.info("new controllerId cant be less than 1 for point migration");
            }
        }else {
            LOGGER.info(" controller cant be null for point migration ");
        }
    }

    /**
     * @param point
     * @return
     */

    private Point toBacnetIpPoints(Map<String, Object> point) throws Exception {
        if(point != null){
            BacnetIpPointContext newPoint = new BacnetIpPointContext(-1,-1);
            if (containsCheck("instanceType",point)){
                newPoint.setInstanceType(Integer.parseInt(String.valueOf(point.get("instanceType"))));
            }
            if(containsCheck("objectInstanceNumber",point)){
                newPoint.setInstanceNumber((Long) point.get("objectInstanceNumber"));
            }
            toPoint(newPoint,point);
            return newPoint;
        }else {
            throw new Exception(" point cant be null");
        }
    }

    private void toPoint(BacnetIpPointContext newPoint, Map<String, Object> point) {
        if (containsCheck(AgentConstants.NAME,point)) {
            newPoint.setName((String) point.get(AgentConstants.NAME));
        }
        else if (containsCheck("instance",point)){
            newPoint.setName(String.valueOf(point.get("instance")));
        }
        if(containsCheck(AgentConstants.DISPLAY_NAME,point)){
            newPoint.setDisplayName((String) point.get(AgentConstants.DISPLAY_NAME));
        }
        if(containsCheck(AgentConstants.DESCRIPTION,point)){
            newPoint.setDescription((String) point.get(AgentConstants.DESCRIPTION));
        }
        if(containsCheck(AgentConstants.DATA_TYPE,point)){
            newPoint.setDataType((FacilioDataType) point.get(AgentConstants.DATA_TYPE));
        }

        if(containsCheck("device",point)){
            newPoint.setDeviceName((String) point.get("device"));
        }
        if(containsCheck(AgentConstants.ASSET_CATEGORY_ID,point)){
            newPoint.setAssetCategoryId((Long) point.get(AgentConstants.ASSET_CATEGORY_ID));
        }
        if(containsCheck(AgentConstants.RESOURCE_ID,point)){
            newPoint.setResourceId((Long) point.get(AgentConstants.RESOURCE_ID));
        }
        if(containsCheck(AgentConstants.FIELD_ID,point)){
            newPoint.setFieldId((Long) point.get(AgentConstants.FIELD_ID));
        }
        if(containsCheck("isWritable",point)){
            newPoint.setWritable(((int)point.get("isWritable")>0));
        }
        if(containsCheck(AgentConstants.CONFIGURE_STATUS,point)){
            newPoint.setConfigureStatus((Integer) point.get(AgentConstants.CONFIGURE_STATUS));
        }
        if(containsCheck(AgentConstants.SUBSCRIBE_STATUS,point)){
            newPoint.setSubscribestatus((Integer) point.get(AgentConstants.SUBSCRIBE_STATUS));
        }
        if(containsCheck(AgentConstants.THRESHOLD_JSON,point)){
            newPoint.setThresholdJSON(String.valueOf(point.get(AgentConstants.THRESHOLD_JSON)));
        }
        if(containsCheck(AgentConstants.CREATED_TIME,point)){
            newPoint.setCreatedTime((Long) point.get(AgentConstants.CREATED_TIME));
        }
        if(containsCheck(AgentConstants.MAPPED_TIME,point)){
            newPoint.setMappedTime((Long) point.get(AgentConstants.MAPPED_TIME));
        }
        if(containsCheck(AgentConstants.UNIT,point)){
            newPoint.setUnit((Integer) point.get(AgentConstants.UNIT));
        }
    }

    private Controller toBAcnetIpControllerV2(ControllerContext controller) {
        BacnetIpControllerContext controllerContext = new BacnetIpControllerContext();
        controllerContext.setInstanceNumber((int) controller.getInstanceNumber());
        controllerContext.setIpAddress(controller.getIpAddress());
        controllerContext.setNetworkNumber((int) controller.getNetworkNumber());
        controllerContext.setControllerType(FacilioControllerType.BACNET_IP.asInt());
        toControllerV2(controllerContext,controller);
        return controllerContext;
    }

    private void toControllerV2(Controller newController, ControllerContext controller) {
        newController.setName(controller.getName());
        newController.setSiteId(controller.getSiteId());
        newController.setAgentId(controller.getAgentId());
        newController.setDataInterval(controller.getDataInterval());
        newController.setActive(controller.isActive());
        newController.setWritable(false);
        newController.setAvailablePoints(controller.getAvailablePoints());
        newController.setControllerProps(controller.getControllerProps());
        newController.setCreatedTime(controller.getCreatedTime());
        newController.setLastModifiedTime(controller.getLastModifiedTime());
        newController.setLastDataRecievedTime(controller.getLastDataReceivedTime());
    }


    private com.facilio.agentv2.FacilioAgent migrateAgentToV2(long agentId) throws Exception {
        FacilioAgent agent = AgentUtil.getAgentDetails(agentId);
        if(agent != null){
            com.facilio.agentv2.FacilioAgent agentV2 = new com.facilio.agentv2.FacilioAgent();
            agentV2.setDeviceDetails(agent.getDeviceDetails());
            agentV2.setConnectionStatus(agent.getConnectionStatus());
            agentV2.setName(agent.getName());
            agentV2.setDisplayName(agent.getDisplayName());
            if(agent.getInterval() != null){
                agentV2.setInterval(agent.getInterval());
            }else {
                agentV2.setInterval(15);
            }
            agentV2.setType(agent.getType());
            agentV2.setVersion(agent.getVersion());
            if(agent.getLastModifiedTime() != null){
                agentV2.setLastModifiedTime(agent.getLastModifiedTime());
            }
            if(agent.getLastDataRecievedTime() != null){
                agentV2.setLastDataReceivedTime(agent.getLastDataRecievedTime());
            }
            if(agent.getState() != null){
                agentV2.setState(agent.getState());
            }else {
                agent.setState(0);
            }
            if((agent.getSiteId() != null) && (agent.getSiteId() > 0)){
                agentV2.setSiteId(agent.getSiteId());
            }else {
                throw new Exception(" agent is missing its siteId ");
            }
            agentV2.setWritable(agent.getWritable());
            if(agent.getDeletedTime() != null){
                agentV2.setDeletedTime(agent.getDeletedTime());
            }
            agentV2.setId(AgentApiV2.addAgent(agentV2));
            LOGGER.info("----migrated agent "+agentId+" to "+agentV2.getId());
            return agentV2;
        }else {
            throw new Exception(" agent can't be null");
        }
    }

}
