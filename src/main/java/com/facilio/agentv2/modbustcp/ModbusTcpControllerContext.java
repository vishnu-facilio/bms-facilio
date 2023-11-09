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

import javax.validation.constraints.NotNull;
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
    @NotNull
    private String ipAddress;

    @JsonInclude
    private String identifier;

    @JsonInclude
    @NotNull
    private int slaveId = -1;

    @JsonInclude
    @NotNull
    private int port = 502;

    public void setPort(int port) {
        this.port = port;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getPort() {
        return port;
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
        return FacilioConstants.ContextNames.MODBUS_TCP_CONTROLLER_MODULE_NAME;
    }

    public JSONObject getChildJSON() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(AgentConstants.ID, getId());
        jsonObject.put(AgentConstants.SLAVE_ID, getSlaveId());
        jsonObject.put(AgentConstants.IP_ADDRESS, ipAddress);
        jsonObject.put(AgentConstants.PORT, getPort());
        return jsonObject;
    }

    @Override
    public List<Condition> getControllerConditions() throws Exception {
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(getModuleName());
        LOGGER.info("ModuleName : "+getModuleName());
        LOGGER.info("Fields : "+fieldsMap);
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.SLAVE_ID), String.valueOf(getSlaveId()), NumberOperators.EQUALS));
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.PORT), String.valueOf(getPort()), NumberOperators.EQUALS));
        if (ipAddress != null) {
            conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.IP_ADDRESS),getIpAddress(), StringOperators.IS));
        } else {
            throw new Exception("Ip address for Modbus TCP controller cant be null");
        }
        return conditions;
    }

    @Override
    public String getIdentifier() {
        return slaveId+IDENTIFIER_SEPERATER+ipAddress+IDENTIFIER_SEPERATER+port;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof ModbusTcpControllerContext){
            ModbusTcpControllerContext obj = (ModbusTcpControllerContext) o;
            return this.getPort()==obj.getPort() && this.getSlaveId()==obj.getSlaveId() && this.getIpAddress().equals(obj.getIpAddress()) && super.equals(obj);
        }else{
            return false;
        }
    }
}
