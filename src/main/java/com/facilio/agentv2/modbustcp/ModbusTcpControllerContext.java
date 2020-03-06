package com.facilio.agentv2.modbustcp;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentKeys;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.JsonUtil;
import com.facilio.agentv2.controller.Controller;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModbusTcpControllerContext extends Controller {

    public static final String ASSETCATEGORY = FacilioConstants.ContextNames.MODBUS_TCP_CONTROLLER_MODULE_NAME;
    private static final Logger LOGGER = LogManager.getLogger(ModbusTcpControllerContext.class.getName());

    public ModbusTcpControllerContext() {
        setControllerType(FacilioControllerType.MODBUS_IP.asInt());
    }

    public ModbusTcpControllerContext(long agentId, long orgId) throws Exception {
        super(agentId, orgId);
        setControllerType(FacilioControllerType.MODBUS_IP.asInt());
    }

    @JsonInclude
    private String ipAddress;

    @JsonInclude
    private String identifier;

    @JsonInclude
    private int slaveId = -1;

    public static ModbusTcpControllerContext getModbusTcpControllerFromMap(Map<String, Object> controllerMap) throws Exception {
        LOGGER.info(" controller map for modbus ip controller " + controllerMap);
        if (controllerMap == null || controllerMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + controllerMap);
        }
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        if (containsValueCheck(AgentConstants.IDENTIFIER, controllerMap) && (controllerMap.containsKey(AgentConstants.AGENT_ID))) {
            long agentId = ((Number) controllerMap.get(AgentConstants.AGENT_ID)).longValue();
            String identifier = (String) controllerMap.get(AgentConstants.IDENTIFIER);
            ModbusTcpControllerContext controller = new ModbusTcpControllerContext(((Number) controllerMap.get(AgentConstants.AGENT_ID)).longValue(), orgId);
            //controller.processIdentifier((String) controllerMap.get(AgentConstants.IDENTIFIER));
            return (ModbusTcpControllerContext) controller.getControllerFromJSON(controllerMap);
        }
        if (containsValueCheck(AgentConstants.SLAVE_ID, controllerMap) && containsValueCheck(AgentConstants.IP_ADDRESS, controllerMap)) {
            ModbusTcpControllerContext controller = new ModbusTcpControllerContext(JsonUtil.getLong(controllerMap.get(AgentKeys.AGENT_ID)), orgId);
            controller.setSlaveId(Math.toIntExact(JsonUtil.getLong(controllerMap.get(AgentConstants.SLAVE_ID))));
            controller.setIpAddress((String) controllerMap.get(AgentConstants.IP_ADDRESS));
            return (ModbusTcpControllerContext) controller.getControllerFromJSON(controllerMap);
        }
        throw new Exception("Mandatory fields like " + AgentConstants.SLAVE_ID + " and " + AgentConstants.IP_ADDRESS + " are missing form input parameters " + controllerMap);
    }

    public int getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(int slaveId) {
        this.slaveId = slaveId;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getModuleName() {
        return ASSETCATEGORY;
    }

    public JSONObject getChildJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(AgentConstants.ID, getId());
        jsonObject.put(AgentConstants.SLAVE_ID, getSlaveId());
        jsonObject.put(AgentConstants.IP_ADDRESS, ipAddress);
        jsonObject.put(AgentConstants.NETWORK_ID, 0);
        return jsonObject;
    }

    @Override
    public List<Condition> getControllerConditions() throws Exception {
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(getModuleName());
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.SLAVE_ID), String.valueOf(getSlaveId()), NumberOperators.EQUALS));
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.IP_ADDRESS),getIpAddress(), StringOperators.IS));
        return conditions;
    }

    @Override
    public String getIdentifier() {
        return slaveId+IDENTIFIER_SEPERATER+ipAddress;
    }
}
