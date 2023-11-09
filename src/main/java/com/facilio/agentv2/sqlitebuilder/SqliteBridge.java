package com.facilio.agentv2.sqlitebuilder;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentType;
import com.facilio.agent.AgentUtil;
import com.facilio.agent.FacilioAgent;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agent.controller.FacilioDataType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.bacnet.BacnetIpControllerContext;
import com.facilio.agentv2.bacnet.BacnetIpPointContext;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.GetControllerRequest;
import com.facilio.agentv2.lonWorks.LonWorksControllerContext;
import com.facilio.agentv2.lonWorks.LonWorksPointContext;
import com.facilio.agentv2.misc.MiscControllerContext;
import com.facilio.agentv2.misc.MiscPoint;
import com.facilio.agentv2.niagara.NiagaraControllerContext;
import com.facilio.agentv2.niagara.NiagaraPointContext;
import com.facilio.agentv2.point.Point;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.ControllerType;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.timeseries.TimeSeriesAPI;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqliteBridge{

    private static final Logger LOGGER = LogManager.getLogger(SqliteBridge.class.getName());

    public static long migrateAgentData(long agentId) throws Exception {
        LOGGER.info(" migration bridge is on");
            FacilioChain migrationChain = TransactionChainFactory.getagentV2DataSqlite();
            FacilioContext context = migrationChain.getContext();
            context.put(AgentConstants.AGENT_ID, agentId);
            migrationChain.execute();
            if (context.containsKey(AgentConstants.FILE_ID)) {
                return (long) context.get(AgentConstants.FILE_ID);
            }else {
                LOGGER.info(AgentConstants.FILE_ID+" is missing from context ");
            }
        return -1;
    }

    public static boolean migrateToNewAgent(long agentId) throws Exception {
        LOGGER.info(" migrating to new Agent ");
        FacilioChain agentMigrationChain = TransactionChainFactory.agentMigrationChain();
        FacilioContext context = new FacilioContext();
        context.put(AgentConstants.AGENT_ID,agentId);
        agentMigrationChain.setContext(context);
        return agentMigrationChain.execute();
    }

    public File getSqliteFile(long agentId) throws Exception {
        String path = System.getProperties().getProperty("java.io.tmpdir") + File.separator + AccountUtil.getCurrentOrg().getOrgId();
        File directory = new File(path);
        if (!directory.exists()) {
            if (!directory.mkdir()) {
                throw new Exception(" directory " + path + " cant be created");
            }
        }
        String fileName = agentId + ".db";
        LOGGER.info(" file path "+path);
        File file = new File(path + File.separator + fileName);
        createNewFile(file);
        return file;
    }

    private void createNewFile(File file) throws IOException {
        if( ! file.createNewFile()){
            LOGGER.info(" file already present ");
            file.delete();
            file.createNewFile();
        }
    }

    public static List<Pair<Long, ControllerContext>> migrateControllerToV2(long agentId, com.facilio.agentv2.FacilioAgent newAgent) throws Exception {
        List<ControllerContext> controllers = ControllerAPI.getControllers(agentId);
        return migrateAndAddControllers(newAgent, controllers);
    }

    private static List<Pair<Long, ControllerContext>> migrateAndAddControllers(com.facilio.agentv2.FacilioAgent newAgent, List<ControllerContext> controllers) {
        List<Pair<Long, ControllerContext>> controllersToMigrate = new ArrayList<>();
        if ((controllers != null) && (!controllers.isEmpty())) {
            LOGGER.info(" migrating controller " + controllers.size() + " for new-agent " + newAgent);
            for (ControllerContext controller : controllers) {
                MutablePair<Long, ControllerContext> pair = new MutablePair<>();
                pair.setRight(controller);
                Controller newController = null;
                try {
                    if (controller.getControllerTypeEnum().getKey() == 10) {
                        controller.setControllerType(0);
                    }
                    ControllerType controllerType = controller.getControllerTypeEnum();
                    switch (controllerType) {
                        case BACNET_IP:
                            newController = toBAcnetIpControllerV2(controller);
                            break;
                        case NIAGARA:
                            newController = toNiagaraControllerV2(controller);
                            break;
                        case LON_WORKS:
                            newController = toLonWorkControllerV2(controller);
                            break;
                        case MISC:
                            newController = toMiscControllerV2(controller);
                            break;
                        /*case OPC_DA:
                            newController = toOpcDaControllerV2(controller);*/
                    }
                    if (newController != null) {
                        newController.setControllerType(controllerType.getKey());
                        newController.setAgentId(newAgent.getId());
                        newController.setSiteId(newAgent.getSiteId());
                        try {
                            //addFieldDevice(newController);
                            long newControllerId = AgentConstants.getControllerBean().addController(newController, newAgent, false);

                            LOGGER.info(" --- migrated controller " + controller.getId() + " to " + newControllerId);
                            if (newControllerId > 0) {
                                pair.setLeft(newControllerId);
                                controller.setAgentId(newAgent.getId());
                            }
                        } catch (MySQLIntegrityConstraintViolationException | BatchUpdateException e) {
                            LOGGER.info(" duplicate controller present");
                            try {
                                GetControllerRequest getControllerRequest = new GetControllerRequest()
                                        .withAgentId(newAgent.getId())
                                        .withControllerProperties(newController.getChildJSON(), FacilioControllerType.valueOf(controllerType.getKey()));
                                Controller existingController = getControllerRequest.getController();
                                if(existingController == null){
                                    throw new Exception(" controller not present and cant be added ");
                                }
                                controller.setAgentId(existingController.getId());
                            }catch (Exception e1){
                                LOGGER.info(" exception while fetching existing controller ",e1);
                                continue;
                            }
                            LOGGER.info("Controller already migrated "+controller.getId());
                    }
                        controllersToMigrate.add(pair);
                    }
                } catch (Exception e) {
                    LOGGER.info(" Exception while migrating controller ",e);
                }
            }

        }
        if (!controllersToMigrate.isEmpty()) {
            StringBuilder controllerMigrated = new StringBuilder();
            for (Pair<Long,ControllerContext> controllerContext : controllersToMigrate) {
                controllerMigrated.append(controllerContext.getRight().getId() + "-");
            }
            LOGGER.info(" Migrated Controllers are " + controllerMigrated.toString());
        }
        return controllersToMigrate;
    }

//    private static void addFieldDevice(Controller newController) throws Exception {
//        Device fieldDevice = new Device();
//        fieldDevice.setControllerType(newController.getControllerType());
//        fieldDevice.setSiteId(newController.getSiteId());
//        fieldDevice.setAgentId(newController.getAgentId());
//        fieldDevice.setName(newController.getName());
//        fieldDevice.setIdentifier(newController.getIdentifier());
//        fieldDevice.setCreatedTime(newController.getCreatedTime());
//        JSONObject controllerProps = new JSONObject();
//        controllerProps.putAll(newController.toJSON());
//        controllerProps.put(AgentConstants.CONTROLLER,newController.getChildJSON());
//        fieldDevice.setControllerProps(controllerProps);
//        //FieldDeviceApi.addFieldDevice(fieldDevice);
//        //newController.setDeviceId(fieldDevice.getId());
//    }

    public static void migratePoints(ControllerContext controller, long newControllerId) {
        if (controller != null) {
            if (newControllerId > 0) {
                try {
                    LOGGER.info(" controller Id for points is " + controller.getId());
                    List<Map<String, Object>> points = TimeSeriesAPI.getPointsForController(controller.getId());
                    if (!points.isEmpty()) {
                        LOGGER.info("old points size "+points.size());
                        Point newPoint;
                        List<Map<String,Object>> newPoints = new ArrayList<>();
                        for (Map<String, Object> point : points) {
                            try {
                                newPoint = null;
                                switch (controller.getControllerTypeEnum()) {
                                    case BACNET_IP:
                                        newPoint = toBacnetIpPoint(point);
                                        break;
                                    case LON_WORKS:
                                        newPoint = toLonWorksPoint(point);
                                        break;
                                    case NIAGARA:
                                        newPoint = toNiagaraPoint(point);
                                        break;
                                    case MISC:
                                        newPoint = toMiscPoint(point);
                                        break;
                                }

                                if (newPoint != null) {
                                    newPoint.setControllerId(newControllerId);
                                    newPoint.setAgentId(controller.getAgentId());
                                    newPoint.setOrgId(controller.getOrgId());

                                    Map<String, Object> pointMap = FieldUtil.getAsProperties(newPoint.toJSON());
                                    newPoints.add(pointMap);
                                }
                            } catch (Exception e) {
                                LOGGER.info(" Exception while migrating point to v2 ", e);
                            }
                        }
                        FacilioChain addPointsChain = TransactionChainFactory.getAddPointsChain();
                        FacilioContext context = new FacilioContext();
                        Controller cont = AgentConstants.getControllerBean().getControllerFromDb(newControllerId);
                        context.put(AgentConstants.CONTROLLER,cont);
                        context.put(AgentConstants.POINTS,newPoints);
                        addPointsChain.setContext(context);
                        addPointsChain.execute();
                    }
                } catch (Exception e) {
                    LOGGER.info("Exception occurred while migrating points", e);
                }
            } else {
                LOGGER.info("new controllerId cant be less than 1 for point migration");
            }
        } else {
            LOGGER.info(" controller cant be null for point migration ");
        }
    }

    private static Point toLonWorksPoint(Map<String, Object> point) throws Exception {
        if (point != null) {
            LonWorksPointContext pointContext = new LonWorksPointContext();
            toPoint(pointContext, point);
            return pointContext;
        } else {
            throw new Exception("point cant be null");
        }
    }


    /**
     * @param point
     * @return
     */

    private static Point toBacnetIpPoint(Map<String, Object> point) throws Exception {
        if (point != null) {
            BacnetIpPointContext newPoint = new BacnetIpPointContext(-1, -1);
            if (containsCheck("instanceType", point)) {
                newPoint.setInstanceType(Integer.parseInt(String.valueOf(point.get("instanceType"))));
            }
            if (containsCheck("objectInstanceNumber", point)) {
                newPoint.setInstanceNumber((Long) point.get("objectInstanceNumber"));
            }
            toPoint(newPoint, point);
            return newPoint;
        } else {
            throw new Exception(" point cant be null");
        }
    }

    public static boolean containsCheck(String key, Map map){
        if( (key != null) && ( ! key.isEmpty()) && ( map != null ) && ( ! map.isEmpty() ) && (map.containsKey(key)) && (map.get(key) != null) ){
            return true;
        }
        return false;
    }

    public boolean checkNumber(Number number){
        return (number.intValue() > 0);
    }

    private static Point toMiscPoint(Map<String, Object> point) throws Exception {
        if (point != null) {
            MiscPoint newPoint = new MiscPoint();
            toPoint(newPoint, point);
            return newPoint;
        } else {
            throw new Exception("point is null");
        }
    }

    private static Point toNiagaraPoint(Map<String, Object> point) throws Exception {
        if (point != null) {
            NiagaraPointContext newPoint = new NiagaraPointContext();
            if (containsCheck("instance", point)) {
                newPoint.setPath((String) point.get("instance"));
            } else {
                throw new Exception("instance key not found");
            }
            toPoint(newPoint, point);
            return newPoint;
        } else {
            throw new Exception(" point cant be null");
        }
    }

    private static void toPoint(Point newPoint, Map<String, Object> point) {
        if (containsCheck(AgentConstants.NAME, point)) {
            newPoint.setName((String) point.get(AgentConstants.NAME));
        } else if (containsCheck("instance", point)) {
            newPoint.setName(String.valueOf(point.get("instance")));
        }
        if (containsCheck(AgentConstants.DISPLAY_NAME, point)) {
            newPoint.setDisplayName((String) point.get(AgentConstants.DISPLAY_NAME));
        }
        if (containsCheck(AgentConstants.DESCRIPTION, point)) {
            newPoint.setDescription((String) point.get(AgentConstants.DESCRIPTION));
        }
        if (containsCheck(AgentConstants.DATA_TYPE, point)) {
            newPoint.setDataType(FacilioDataType.valueOf((Integer) point.get(AgentConstants.DATA_TYPE)));
        }

        if (containsCheck("device", point)) {
            newPoint.setDeviceName((String) point.get("device"));
        }
        if (containsCheck(AgentConstants.ASSET_CATEGORY_ID, point)) {
            newPoint.setCategoryId((Long) point.get(AgentConstants.ASSET_CATEGORY_ID));
        }
        if (containsCheck(AgentConstants.RESOURCE_ID, point)) {
            newPoint.setResourceId((Long) point.get(AgentConstants.RESOURCE_ID));
        }
        if (containsCheck(AgentConstants.FIELD_ID, point)) {
            newPoint.setFieldId((Long) point.get(AgentConstants.FIELD_ID));
        }
        if (containsCheck("isWritable", point)) {
            newPoint.setWritable(((boolean) point.get("isWritable")));
        }
        if (containsCheck(AgentConstants.CONFIGURE_STATUS, point)) {
            newPoint.setConfigureStatus((Integer) point.get(AgentConstants.CONFIGURE_STATUS));
        }
        if (containsCheck(AgentConstants.SUBSCRIBE_STATUS, point)) {
            newPoint.setSubscribeStatus((Integer) point.get(AgentConstants.SUBSCRIBE_STATUS));
        }
        if (containsCheck(AgentConstants.THRESHOLD_JSON, point)) {
            newPoint.setThresholdJSON(String.valueOf(point.get(AgentConstants.THRESHOLD_JSON)));
        }
        if (containsCheck(AgentConstants.CREATED_TIME, point)) {
            newPoint.setCreatedTime((Long) point.get(AgentConstants.CREATED_TIME));
        }
        if (containsCheck(AgentConstants.MAPPED_TIME, point)) {
            newPoint.setMappedTime((Long) point.get(AgentConstants.MAPPED_TIME));
        }
        if (containsCheck(AgentConstants.UNIT, point)) {
            newPoint.setUnit((Integer) point.get(AgentConstants.UNIT));
        }
    }

    private static Controller toBAcnetIpControllerV2(ControllerContext controller) {
        BacnetIpControllerContext controllerContext = new BacnetIpControllerContext();
        controllerContext.setInstanceNumber((int) controller.getInstanceNumber());
        controllerContext.setIpAddress(controller.getIpAddress());
        controllerContext.setNetworkNumber((int) controller.getNetworkNumber());
        controllerContext.setControllerType(FacilioControllerType.BACNET_IP.asInt());
        toControllerV2(controllerContext, controller);
        return controllerContext;
    }

    private static Controller toMiscControllerV2(ControllerContext controller) {
        MiscControllerContext controllerContext = new MiscControllerContext();
        controllerContext.setName(controller.getName());
        toControllerV2(controllerContext, controller);
        return controllerContext;
    }

    private static Controller toNiagaraControllerV2(ControllerContext controller) {
        NiagaraControllerContext controllerContext = new NiagaraControllerContext();
        controllerContext.setIpAddress(controller.getIpAddress());
        controllerContext.setPortNumber(controller.getPortNumber());
        toControllerV2(controllerContext, controller);
        return controllerContext;
    }

    private static Controller toLonWorkControllerV2(ControllerContext controller) {
        LonWorksControllerContext controllerContext = new LonWorksControllerContext();
        toControllerV2(controllerContext, controller);
        return controllerContext;
    }

    private static void toControllerV2(Controller newController, ControllerContext controller) {
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
        newController.setLastDataReceivedTime(controller.getLastDataReceivedTime());
    }


    public static com.facilio.agentv2.FacilioAgent migrateAgentToV2(long agentId) throws Exception {
        FacilioAgent agent = AgentUtil.getAgentDetails(agentId);
        AgentBean agentBean = (AgentBean) BeanFactory.lookup("AgentBean");
        if (agent != null) {
            com.facilio.agentv2.FacilioAgent agentV2 = new com.facilio.agentv2.FacilioAgent();
            agentV2.setDeviceDetails(agent.getDeviceDetails());
            agentV2.setConnected(agent.getConnectionStatus());
            agentV2.setName(agent.getName());
            agentV2.setDisplayName(agent.getDisplayName());
            if (agent.getInterval() != null) {
                agentV2.setInterval(agent.getInterval());
            } else {
                agentV2.setInterval(15);
            }
            agentV2.setType(agent.getType());
            if (agent.getType() == null) {
                agentV2.setAgentType(AgentType.valueOf("CUSTOM").getKey());
            }
            else { agentV2.setAgentType(AgentType.valueOf(agent.getType()).getKey()); }
            agentV2.setVersion(agent.getVersion());
            if (agent.getLastModifiedTime() != null) {
                agentV2.setLastModifiedTime(agent.getLastModifiedTime());
            }
            if (agent.getLastDataRecievedTime() != null) {
                agentV2.setLastDataReceivedTime(agent.getLastDataRecievedTime());
            }
            if (agent.getState() != null) {
                agentV2.setConnected(agent.getState()>0);
            } else {
                agent.setState(0);
            }
            if ((agent.getSiteId() != null) && (agent.getSiteId() > 0)) {
                agentV2.setSiteId(agent.getSiteId());
            } else {
                throw new Exception(" agent is missing its siteId ");
            }
            agentV2.setWritable(agent.getWritable());
            if (agent.getDeletedTime() != null) {
                agentV2.setDeletedTime(agent.getDeletedTime());
            }
            try {
                agentV2.setId(agentBean.addAgent(agentV2));
            } catch (MySQLIntegrityConstraintViolationException e) {
                LOGGER.info(" agent already present and so returning it " + agent.getId());
                com.facilio.agentv2.FacilioAgent newAgent = agentBean.getAgent(agent.getAgentName());
                if(newAgent != null){
                    return newAgent;
                }else {
                    throw new Exception(" agent present and new agent con't be fetched");
                }
            }catch (BatchUpdateException e){
                if(e.getMessage().contains("Duplicate entry")){
                    LOGGER.info(" agent already present ");
                    com.facilio.agentv2.FacilioAgent newAgent = agentBean.getAgent(agent.getAgentName());
                    if (newAgent != null) {
                        return newAgent;
                    }else {
                        throw new Exception(" agent present and new agent con't be fetched");
                    }
                }

            }
            LOGGER.info("----migrated agent " + agentId + " to " + agentV2.getId());
            return agentV2;
        } else {
            throw new Exception(" agent can't be null");
        }
    }
}

