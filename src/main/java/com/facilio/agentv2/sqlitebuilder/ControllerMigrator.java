package com.facilio.agentv2.sqlitebuilder;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.bacnet.BacnetIpControllerContext;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.modbusrtu.ModbusRtuControllerContext;
import com.facilio.agentv2.modbustcp.ModbusTcpControllerContext;
import com.facilio.agentv2.opcua.OpcUaControllerContext;
import com.facilio.agentv2.opcxmlda.OpcXmlDaControllerContext;
import com.facilio.sqlUtils.contexts.bacnet.ip.BacnetIpController;
import com.facilio.sqlUtils.contexts.modbus.ip.ModbusTcpController;
import com.facilio.sqlUtils.contexts.modbus.rtu.ModbusRtuController;
import com.facilio.sqlUtils.contexts.opc.ua.OpcUaController;
import com.facilio.sqlUtils.contexts.opc.xmlda.OpcXmlDaController;
import com.facilio.sqlUtils.sqllite.SQLiteUtil;
import com.facilio.sqlUtils.utilities.FacilioJavaController;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Map;

public class ControllerMigrator {
    private static final Logger LOGGER = LogManager.getLogger(ControllerMigrator.class.getName());

    public static boolean migrateControllers(long agentId, Map<Long, FacilioControllerType> controllerIdsType) throws Exception {
        LOGGER.info(" agentId is " + agentId);
        for (Long controllerId : controllerIdsType.keySet()) {
            FacilioControllerType controllerType = controllerIdsType.get(controllerId);
            LOGGER.info(" migrating controller " + controllerId + " of type " + controllerType.asString());
            if (controllerIdsType != null) {
                Controller controller = ControllerApiV2.getControllerIdType(controllerId, controllerIdsType.get(controllerId));
                LOGGER.info(" got controller " + controller.getName());
                if ( ! addControllersToSqlite(controller, controllerType)) {
                    LOGGER.info("failed adding controller " + controller.getName());
                }
            } else {
                LOGGER.info("controllerType cant be null ->" + controllerId);
                continue;
            }
        }
        return true;
    }


    private static boolean addControllersToSqlite(Controller controller, FacilioControllerType facilioControllerType) throws Exception {
        LOGGER.info(" adding controller to sqlite " + controller.getName() + "  of type " + facilioControllerType.asString());
        FacilioJavaController controllerToInsert = null;
        switch (facilioControllerType) {
            case BACNET_IP:
                controllerToInsert = getAgentBacnetController((BacnetIpControllerContext) controller);
                break;
            case MODBUS_RTU:
                assert controller instanceof ModbusRtuControllerContext;
                controllerToInsert = getModbusRtuController((ModbusRtuControllerContext) controller);
                break;
            case OPC_XML_DA:
                assert controller instanceof OpcXmlDaControllerContext;
                controllerToInsert = getOpcDaController((OpcXmlDaControllerContext) controller);
                break;
            case MODBUS_IP:
                assert controller instanceof ModbusTcpControllerContext;
                controllerToInsert = getModbusIpController((ModbusTcpControllerContext) controller);
                break;
            case OPC_UA:
                assert controller instanceof OpcUaControllerContext;
                controllerToInsert = getOpcUaController((OpcUaControllerContext) controller);
                break;
            case BACNET_MSTP:
            case NIAGARA:
            case KNX:
            case MISC:
            case REST:
            case CUSTOM:
            case LON_WORKS:
                throw new Exception(" No impementation for controller type " + facilioControllerType.asString());
        }
        if (controllerToInsert != null) {
            long controllerId = SQLiteUtil.addController(controllerToInsert);
            LOGGER.info(" controller added and id is " + controllerId);
            return (controllerId > 0);
        }
        return false;
    }

    private static ModbusRtuController getModbusRtuController(ModbusRtuControllerContext controller) throws Exception {
        if (controller != null) {
            ModbusRtuController agentController = new ModbusRtuController();
            makeAgentController(controller, agentController);
            agentController.setNetworkId(controller.getNetworkId());
            agentController.setSlaveId(controller.getSlaveId());
            return agentController;
        } else {
            throw new Exception(" controller cant be null");
        }
    }


    private static ModbusTcpController getModbusIpController(ModbusTcpControllerContext controller) throws Exception {
        if (controller != null) {
            ModbusTcpController agentController = new ModbusTcpController();
            makeAgentController(controller, agentController);
            agentController.setIpAddress(controller.getIpAddress());
            return agentController;
        } else {
            throw new Exception(" controller cant be null");
        }
    }

    private static OpcXmlDaController getOpcDaController(OpcXmlDaControllerContext controller) throws Exception {
        if (controller != null) {
            OpcXmlDaController agentController = new OpcXmlDaController();
            makeAgentController(controller, agentController);
            agentController.setUrl(controller.getUrl());
            agentController.setPassword(controller.getPassword());
            agentController.setUsername(controller.getUserName());
            return agentController;
        } else {
            throw new Exception(" controller cant be null");
        }
    }

    private static OpcUaController getOpcUaController(OpcUaControllerContext controller) throws Exception {
        if (controller != null) {
            OpcUaController agentController = new OpcUaController();
            makeAgentController(controller, agentController);
            agentController.setUrl(controller.getUrl());
            agentController.setCertPath(controller.getCertPath());
            agentController.setSecurityMode(controller.getSecurityMode());
            agentController.setSecurityPolicy(controller.getSecurityPolicy());
            return agentController;
        } else {
            throw new Exception(" controller cant be null");
        }
    }


 /*   private static Niagara getOpcUaController(NiagaraController controller) throws Exception {
        if(controller != null){
            OpcUaController agentController = new OpcUaController();
            makeAgentController(controller,agentController);
            agentController.setUrl(controller.getUrl());
            agentController.setCertPath(controller.getCertPath());
            agentController.setSecurityMode(controller.getSecurityMode());
            agentController.setSecurityPolicy(controller.getSecurityPolicy());
            return agentController;
        }else {
            throw new Exception(" controller cant be null");
        }
    }
*/

    private static void makeAgentController(Controller controller, FacilioJavaController agentController) {
        agentController.setName(controller.getName());
        agentController.setDataInterval(Math.toIntExact(controller.getDataInterval()));
        agentController.setWritable(controller.getWritable());
        agentController.setActive(controller.getActive());
        agentController.setType(controller.getControllerType());
        agentController.setControllerProps(controller.getControllerProps());
        agentController.setAvailablePoints(controller.getAvailablePoints());
        agentController.setCreatedTime(controller.getCreatedTime());
        agentController.setLastModifiedTime(controller.getLastModifiedTime());
        agentController.setLastDataSentTime(controller.getLastDataRecievedTime());
        agentController.setDeletedTime(controller.getDeletedTime());
    }

    private static BacnetIpController getAgentBacnetController(BacnetIpControllerContext controller) throws Exception {
        //TODO make it with null and default value check
        if (controller != null) {
            BacnetIpController agentController = new BacnetIpController();
            makeAgentController(controller, agentController);
            agentController.setInstanceNumber(controller.getInstanceNumber());
            agentController.setIpAddress(controller.getIpAddress());
            agentController.setNetworkNumber(controller.getNetworkNumber());
            return agentController;
        } else {
            throw new Exception("controller cant be null");
        }
    }


}
