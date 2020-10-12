package com.facilio.agentv2.modbusrtu;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModbusRtuControllerContext extends Controller {

    private static final Logger LOGGER = LogManager.getLogger(ModbusRtuControllerContext.class.getName());

    public static final String ASSETCATEGORY = FacilioConstants.ContextNames.MODBUS_RTU_CONTROLLER_MODULE_NAME;

    private String identifier;

    @JsonInclude
    private Long networkId = -1L;

    private RtuNetworkContext network;

    @JsonInclude
    int slaveId = -1;

    public ModbusRtuControllerContext(long agentId, long orgId) throws Exception {
        new ModbusRtuControllerContext(agentId, orgId, null);
    }

    public ModbusRtuControllerContext(long agentId, long orgId, String identifier) throws Exception {
        super(agentId, orgId);
        setControllerType(FacilioControllerType.MODBUS_RTU.asInt());
    }

    public ModbusRtuControllerContext() {
        setControllerType(FacilioControllerType.MODBUS_RTU.asInt());
    }

    public void setNetwork(RtuNetworkContext network) {
        this.network = network;
    }

    public RtuNetworkContext getNetwork() {
        return this.network;
    }

    public long getNetworkId() {
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
/*
    public static ModbusRtuControllerContext getModbusRtuControllerFromMap(long agentId, Map<String, Object> controllerMap) throws Exception {
        if (controllerMap == null || controllerMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + controllerMap);
        }
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        if (containsValueCheck(AgentConstants.IDENTIFIER, controllerMap)) {
            ModbusRtuControllerContext controller = new ModbusRtuControllerContext(agentId, orgId, (String) controllerMap.get(AgentConstants.IDENTIFIER));
            controller.getControllerFromJSON(controllerMap);
            ModbusRtuControllerContext controller1 = controller;
            return (ModbusRtuControllerContext) controller.getControllerFromJSON(controllerMap);
        }
        if (containsValueCheck(AgentConstants.SLAVE_ID, controllerMap) && containsValueCheck(AgentConstants.NETWORK_ID, controllerMap)) {
            ModbusRtuControllerContext controller = new ModbusRtuControllerContext(agentId, orgId);
            controller.setSlaveId(Math.toIntExact(JsonUtil.getLong(controllerMap.get(AgentConstants.SLAVE_ID))));
            controller.setNetworkId(JsonUtil.getLong(controllerMap.get(AgentConstants.NETWORK_ID)));
            return (ModbusRtuControllerContext) controller.getControllerFromJSON(controllerMap);
        }
        throw new Exception("Mandatory fields like "+AgentConstants.SLAVE_ID+" and "+ AgentConstants.IP_ADDRESS+" are missing form input parameters "+controllerMap);
    }*/

/*

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
*/

    @Override
    public JSONObject getChildJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(AgentConstants.ID, getId());
        jsonObject.put(AgentConstants.SLAVE_ID, getSlaveId());
        jsonObject.put(AgentConstants.IP_ADDRESS,0);
        if (getNetwork() == null && networkId != -1) {
            setNetwork(RtuNetworkContext.getRtuNetworkContext(networkId));
        } else if (getNetwork() == null && networkId == -1) {
            throw new RuntimeException("Both network id and network object is invalid");
        }
        jsonObject.put(AgentConstants.NETWORK, getNetwork().toJson());
        jsonObject.put(AgentConstants.COM_PORT, getNetwork().toJson().get(AgentConstants.COM_PORT).toString());
        return jsonObject;
    }

    @Override
    public List<Condition> getControllerConditions() throws Exception {
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(getModuleName());
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.SLAVE_ID), String.valueOf(getSlaveId()), NumberOperators.EQUALS));
    return conditions;
    }

    @Override
    public String getIdentifier() {
        return slaveId + IDENTIFIER_SEPERATER + network.getComPort();
    }

}
