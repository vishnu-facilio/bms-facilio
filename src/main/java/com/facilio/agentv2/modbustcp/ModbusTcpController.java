package com.facilio.agentv2.modbustcp;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentKeys;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.JsonUtil;
import com.facilio.agentv2.controller.Controller;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class ModbusTcpController extends Controller {

    public static final String ASSETCATEGORY = FacilioConstants.ContextNames.MODBUS_TCP_CONTROLLER_MODULE_NAME;
    private static final Logger LOGGER = LogManager.getLogger(ModbusTcpController.class.getName());

    public ModbusTcpController() {
    }

    public ModbusTcpController(long agentId, long orgId) throws Exception {
        super(agentId, orgId);
        setControllerType(FacilioControllerType.MODBUS_IP.asInt());
    }

    private String ipAddress;
    private String identifier;
    private int slaveId = -1;

    public int getSlaveId() { return slaveId; }
    private void setSlaveId(int slaveId) { this.slaveId = slaveId; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }


    public String getModuleName() { return ASSETCATEGORY; }

    public void processIdentifier(String identifier) throws Exception {
        if( (identifier != null ) && ( ! identifier.isEmpty() ) ){
            String[] uniques = identifier.split(IDENTIFIER_SEPERATOR);
            if( (uniques.length == 3) && ( (FacilioControllerType.valueOf(Integer.parseInt(uniques[0])) == FacilioControllerType.MODBUS_IP) ) )
            {
                ipAddress = uniques[1];
                slaveId = Integer.parseInt(uniques[2]);
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
        if( (identifier != null ) && ( ! identifier.isEmpty() ) ){
            return identifier;
        }
        if( (ipAddress != null && (! ipAddress.isEmpty())  ) && (slaveId > 0) ){
            identifier = FacilioControllerType.MODBUS_IP.asInt()+IDENTIFIER_SEPERATOR+ ipAddress +IDENTIFIER_SEPERATOR+ slaveId;;
            return identifier;
        }
        throw new Exception("Exception Occurred, parameters for identifier not set yet ");
    }

    public static ModbusTcpController getModbusTcpControllerFromMap(Map<String, Object> controllerMap) throws Exception {
        LOGGER.info(" controller map for modbus ip controller "+controllerMap);
        if (controllerMap == null || controllerMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + controllerMap);
        }
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        if( containsValueCheck(AgentConstants.IDENTIFIER,controllerMap) && (controllerMap.containsKey(AgentConstants.AGENT_ID)) ){
            long agentId = ((Number)controllerMap.get(AgentConstants.AGENT_ID)).longValue();
            String identifier = (String) controllerMap.get(AgentConstants.IDENTIFIER);
            ModbusTcpController controller = new ModbusTcpController(((Number)controllerMap.get(AgentConstants.AGENT_ID)).longValue(), orgId);
            controller.processIdentifier( (String) controllerMap.get(AgentConstants.IDENTIFIER) );
            return (ModbusTcpController) controller.getControllerFromJSON(controllerMap);
        }
        if( containsValueCheck(AgentConstants.SLAVE_ID,controllerMap) && containsValueCheck(AgentConstants.IP_ADDRESS,controllerMap) ) {
            ModbusTcpController controller = new ModbusTcpController(JsonUtil.getLong(controllerMap.get(AgentKeys.AGENT_ID)), orgId);
            controller.setSlaveId(Math.toIntExact(JsonUtil.getLong(controllerMap.get(AgentConstants.SLAVE_ID))));
            controller.setIpAddress((String) controllerMap.get(AgentConstants.IP_ADDRESS));
            return (ModbusTcpController) controller.getControllerFromJSON(controllerMap);
        }
        throw new Exception("Mandatory fields like "+AgentConstants.SLAVE_ID+" and "+ AgentConstants.IP_ADDRESS+" are missing form input parameters "+controllerMap);
    }

    public JSONObject getChildJSON(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(AgentConstants.ID,getId());
        jsonObject.put(AgentConstants.SLAVE_ID, getSlaveId());
        jsonObject.put(AgentConstants.IP_ADDRESS,"getIpAddress()");
        jsonObject.put(AgentConstants.NETWORK_ID,0);
        return jsonObject;
    }

    @Override
    public List<Condition> getControllerConditions(String identifier) throws Exception {
        return null;
    }
}
