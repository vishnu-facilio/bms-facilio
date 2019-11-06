package com.facilio.agentv2.controller;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.bacnet.BacnetIpController;
import com.facilio.agentv2.device.Device;
import com.facilio.agentv2.device.DeviceUtil;
import com.facilio.agentv2.misc.MiscController;
import com.facilio.agentv2.modbusrtu.ModbusRtuController;
import com.facilio.agentv2.modbustcp.ModbusTcpController;
import com.facilio.agentv2.niagara.NiagaraController;
import com.facilio.agentv2.opcua.OpcUaController;
import com.facilio.agentv2.opcxmlda.OpcXmlDaController;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.custom.CustomController;
import com.facilio.modules.FieldUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.facilio.agentv2.controller.ControllerApiV2.getControllerFromDb;

public class ControllerUtilV2
{

    private static final Logger LOGGER = LogManager.getLogger(ControllerUtilV2.class.getName());
    private long ogdId;
    private long agentId;
    List<Map<String, Controller>> controllerMapList = new ArrayList<>();


    public ControllerUtilV2(long agentId) {
        this.ogdId = AccountUtil.getCurrentOrg().getOrgId();
        this.agentId = agentId;
        loadControllerListMap();
    }

    /**
     * This method iterates {@link FacilioControllerType} values and loads controllers of respective types to the controllerMapList
     */
    private void loadControllerListMap() {
        for (FacilioControllerType value : FacilioControllerType.values()) {
            controllerMapList.add(new HashMap<>());
        }
    }

    /**
     *
     * @param agentId
     * @param controllerIdentifier
     * @param controllerType
     * @return
     */
    public Controller getController(long agentId, String controllerIdentifier, FacilioControllerType controllerType){
        LOGGER.info(" getting controllers from db");
        // avoids null pointer --  loads the controller map
        if( (controllerMapList.get(controllerType.asInt()) == null) ){
            controllerMapList.get(controllerType.asInt()).putAll(new HashMap<>());
        }
        // map empty
        if(controllerType == null){
            return ControllerApiV2.getAllControllersFromDb(agentId).get(controllerIdentifier);
        }
        else if( (controllerMapList.get(controllerType.asInt()).isEmpty()) ){
            Map<String, Controller> controllers = ControllerApiV2.getControllersFromDb(agentId,controllerType);
            if(controllers.isEmpty()){
                return null;
            }
            for (String key : controllers.keySet()) {
                LOGGER.info(" controller type is "+controllers.get(key).getControllerType());
                    controllerMapList.get(controllers.get(key).getControllerType()).put(key,controllers.get(key));
            }
            return controllerMapList.get(controllerType.asInt()).get(controllerIdentifier);
        }else {
            if(controllerMapList.get(controllerType.asInt()).containsKey(controllerIdentifier)){
                return controllerMapList.get(controllerType.asInt()).get(controllerIdentifier);
            }
            else {
                Controller controller = getControllerFromDb(controllerIdentifier,agentId,controllerType);
                if(controller != null){
                    try {
                        controllerMapList.get(controllerType.asInt()).put(controller.makeIdentifier(),controller);
                        return controller;
                    } catch (Exception e) {
                        LOGGER.info("Exception occured, cant generate identifier");
                    }
                }
                LOGGER.info("Exception Occurred, No such controller for agent " + agentId + ", with identifier " + controllerIdentifier);
                return null;
            }
        }
    }


    // pakka


    public boolean processController(long agentId, List<Long> ids) {
        try {
            LOGGER.info("processing controller-------");
            List<Device> devices = FieldUtil.getAsBeanListFromMapList(DeviceUtil.getDevices(agentId, ids), Device.class);
            LOGGER.info(" devices obtained are " + devices.size());
            Controller controller = null;
            for (Device device : devices) {
                LOGGER.info("device are " + device);
                JSONObject controllerProps = device.getControllerProps();
                LOGGER.info(" controller props JSON " + controllerProps);
                if ((controllerProps != null) && (!controllerProps.isEmpty())) {
                    controller = getControllerFromJSON(agentId, controllerProps);
                    if (controller != null) {
                        Controller controllerFromDb = getController(agentId, controller.makeIdentifier(), FacilioControllerType.valueOf(controller.getControllerType()));
                        if (controllerFromDb != null) {
                            controller.setId(controllerFromDb.getId());
                            updateController(controller);
                            return true;
                        } else {
                            LOGGER.info(" adding controller ");
                            LOGGER.info(" debus "+controller.isActive());
                            controller.setActive(true);
                            LOGGER.info(" controller to active true  ");
                            LOGGER.info(" controller JSON "+FieldUtil.getAsProperties(controller));
                            long controllerId = ControllerApiV2.addController(controller);
                            LOGGER.info(" added controller " + controllerId);
                            if( controllerId > 0){
                                return true;
                            }
                        }
                    } else {
                        LOGGER.info("Exception occurred , controller obtained is null ");
                    }
                } else {
                    LOGGER.info("Exception occurred, controllerProps can't be null or empty -> " + controllerProps);
                }
            }
        }catch (Exception e) {
                LOGGER.info("Exception occurred ",e);
        }
        return false;
    }

    private void updateController(Controller controller) {
        controller.setLastDataRecievedTime(System.currentTimeMillis());
        controller.setLastModifiedTime(controller.getLastDataRecievedTime());
        FacilioChain updateControllerChain = TransactionChainFactory.getUpdateControllerChain();
        FacilioContext context = updateControllerChain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD,controller);
        context.put(FacilioConstants.ContextNames.MODULE_NAME,controller.getModuleName());
        List<Long> ids = new ArrayList<>();
        ids.add(controller.getId());
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST,ids);
        try {
            LOGGER.info(" updating controller ");
            updateControllerChain.execute();
            LOGGER.info(" \nafter update  - " + context.get(FacilioConstants.ContextNames.ROWS_UPDATED));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private Controller getControllerFromJSON(long agentId, Map<String,Object> controllerJSON)  {
        try {
            if (controllerJSON != null && (!controllerJSON.isEmpty())) {
                if (containsValueCheck(AgentConstants.TYPE, controllerJSON)) {
                    FacilioControllerType controllerType = FacilioControllerType.valueOf(Math.toIntExact((Long) controllerJSON.get(AgentConstants.TYPE)));
                    switch (controllerType) {
                        case NIAGARA:
                            return NiagaraController.getNiagaraControllerFromMap(agentId, controllerJSON);
                        case BACNET_IP:
                            return BacnetIpController.getBacnetControllerFromMap(agentId, controllerJSON);
                        case OPC_XML_DA:
                            return OpcXmlDaController.getOpcXmlDaControllerFromMap(agentId, controllerJSON);
                        case BACNET_MSTP:
                            return null;
                        case MODBUS_RTU:
                            return ModbusRtuController.getModbusRtuControllerFromMap(agentId, controllerJSON);
                        case MODBUS_IP:
                            return ModbusTcpController.getModbusTcpControllerFromMap(controllerJSON);
                        case LON_WORKS:
                            throw new Exception(" No implementation for " + FacilioControllerType.LON_WORKS.asString() + " controller");
                        case KNX:
                            throw new Exception(" No implementation for " + FacilioControllerType.KNX.asString() + " controller");
                        case OPC_UA:
                            return OpcUaController.getBacnetControllerFromMap(agentId, controllerJSON);
                        case MISC:
                            return MiscController.getMiscControllerFromJSON(agentId,controllerJSON);
                        case CUSTOM:
                        default:
                            return CustomController.getCustomControllerFromJSON(agentId,controllerJSON);

                    }
                } else {
                    LOGGER.info(" Controller Type missing ");
                }
            } else {
                LOGGER.info("Exception occurred, controllerJSON can't be null or empty " + controllerJSON);
            }
        }catch (Exception e){
            LOGGER.info("Exception occurred ",e);
        }
        return null;
    }


    private static boolean containsValueCheck(String key,Map<String,Object> jsonObject){
        return jsonObject.containsKey(key) && (jsonObject.get(key) != null);
    }


public static Controller makeCustomController(long orgId, long agentId, String name ){
    CustomController controller;
    controller = (CustomController) ControllerApiV2.getControllerFromDb(name,agentId,FacilioControllerType.CUSTOM);
    if(controller == null){
        controller = new CustomController();
        controller.setOrgId(orgId);
        controller.setAgentId(agentId);
        controller.setActive(true);
        controller.setName(name);
        if(ControllerApiV2.addController(controller) < 1){
            controller = null;
        }
    }
        return controller;
    }

 public JSONArray getAllControllerList(){
        JSONArray controllerArray = new JSONArray();
     Map<String, Controller> controllers = new HashMap<>();
     for (FacilioControllerType type : FacilioControllerType.values()) {
         controllers.putAll(ControllerApiV2.getControllersFromDb(agentId, type));
     }
     for (String controllerIdentifier : controllers.keySet()) {
         controllerArray.add(controllers.get(controllerIdentifier).toJSON());
     }
        return controllerArray;
 }

 public static BacnetIpController makeBacnetIpController(long orgId, long agentId, String identifier){
     try {
         BacnetIpController controller = new BacnetIpController(agentId,orgId);
         controller.processIdentifier(identifier);
         return controller;
     } catch (Exception e) {
         e.printStackTrace();
     }
     return null;
 }

public static ModbusTcpController makeModbusTcoController(long orgId, long agentId, String identifier){
    try {
        ModbusTcpController controller = new ModbusTcpController(agentId,orgId);
        controller.processIdentifier(identifier);
        return controller;
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
}

}
