package com.facilio.agentnew.modbusrtu;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentnew.AgentConstants;
import com.facilio.agentnew.JsonUtil;
import com.facilio.agentnew.controller.Controller;
import com.facilio.constants.FacilioConstants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Map;

public class ModbusRtuController extends Controller {

    private static final Logger LOGGER = LogManager.getLogger(ModbusRtuController.class.getName());

    public static final String ASSETCATEGORY = FacilioConstants.ContextNames.MODBUS_RTU_CONTROLLER_MODULE_NAME;

    private String identifier;
    private Long networkId = -1L;
    int slaveId = -1;

    public ModbusRtuController(long agentId, long orgId) throws Exception {
        new ModbusRtuController(agentId,orgId,null);
    }
    public ModbusRtuController(long agentId, long orgId, String identifier) throws Exception {
        super(agentId, orgId);
        processIdentifier(identifier);
        setControllerType( FacilioControllerType.MODBUS_RTU.asInt());
    }

    public ModbusRtuController() { }

    public Long getNetworkId() {
        return networkId;
    }
    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

    public int getSlaveId() {
        return slaveId;
    }
    public void setSlaveId(int slaveId) {
        this.slaveId = slaveId;
    }

    public String getModuleName() { return ASSETCATEGORY; }

    public static ModbusRtuController getModbusRtuControllerFromMap(long agentId, Map<String, Object> controllerMap) throws Exception {
        if (controllerMap == null || controllerMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + controllerMap);
        }
        long orgId =  AccountUtil.getCurrentOrg().getOrgId();
        if(containsValueCheck(AgentConstants.IDENTIFIER,controllerMap) ){
            ModbusRtuController controller = new ModbusRtuController(agentId,orgId, (String) controllerMap.get(AgentConstants.IDENTIFIER));
            controller.getControllerFromJSON(controllerMap);
            ModbusRtuController controller1 = controller;
            return (ModbusRtuController) controller.getControllerFromJSON(controllerMap);
        }
        if(containsValueCheck(AgentConstants.SLAVE_ID,controllerMap) && containsValueCheck(AgentConstants.NETWORK_ID,controllerMap)){
            ModbusRtuController controller = new ModbusRtuController(agentId,orgId);
            controller.setSlaveId(Math.toIntExact(JsonUtil.getLong(controllerMap.get(AgentConstants.SLAVE_ID))));
            controller.setNetworkId(JsonUtil.getLong(controllerMap.get(AgentConstants.NETWORK_ID)));
            controller.makeIdentifier();
            return (ModbusRtuController) controller.getControllerFromJSON(controllerMap);
        }
        throw new Exception("Mandatory fields like "+AgentConstants.SLAVE_ID+" and "+ AgentConstants.IP_ADDRESS+" are missing form input parameters "+controllerMap);
    }


    public void processIdentifier(String identifier) throws Exception {
        if( (identifier != null) && ( ! identifier.isEmpty() ) ){
            String[] uniques = identifier.split(IDENTIFIER_SEPERATOR);
            if( (uniques.length == 3) && ( (FacilioControllerType.valueOf(Integer.parseInt(uniques[0])) == FacilioControllerType.MODBUS_RTU) ) )
            {
                LOGGER.info(this.hashCode()+" processing identifier and uniques - slaveid,networkId " + String.join(",",uniques));
                this.setNetworkId(Long.valueOf(uniques[1]));
                this.setSlaveId(Integer.parseInt(uniques[2]));
                this.identifier = identifier;
            }else {
                throw  new Exception(" Exceprion while processing identifier -- length or Type didnt match ");
            }
        }else {
            throw new Exception("Exception Occurred, identifier can't be null or empty ->"+identifier);
        }
    }

    @Override
    public String makeIdentifier() throws Exception {
        if(identifier != null){
            return identifier;
        }
        LOGGER.info(" making identifier  networkId ->" + networkId + " - slaveId->" + slaveId);
        if( (networkId != null) && (slaveId > 0) ){
            identifier = FacilioControllerType.MODBUS_RTU.asInt()+IDENTIFIER_SEPERATOR+networkId+IDENTIFIER_SEPERATOR+slaveId;
            return identifier;
        }
        throw new Exception("Exception occurred , parameters for identifier not set yet ");
    }

    @Override
    public JSONObject getChildJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(AgentConstants.ID, getId());
        jsonObject.put(AgentConstants.SLAVE_ID, getSlaveId());
        jsonObject.put(AgentConstants.IP_ADDRESS,0);
        jsonObject.put(AgentConstants.NETWORK_ID, getNetworkId());
        return jsonObject;
    }

}
