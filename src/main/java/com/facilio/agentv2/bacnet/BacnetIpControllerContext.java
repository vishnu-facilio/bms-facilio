package com.facilio.agentv2.bacnet;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BacnetIpControllerContext extends Controller {

    private static final Logger LOGGER = LogManager.getLogger(BacnetIpControllerContext.class.getName());

    public static final String ASSETCATEGORY = FacilioConstants.ContextNames.BACNET_IP_CONTROLLER_MODULE_NAME;

    public BacnetIpControllerContext() {
        setControllerType(FacilioControllerType.BACNET_IP.asInt());
    }

    @Override
    public long getAgentId() {
        return agentId;
    }

    @Override
    public void setAgentId(long agentId) {
        this.agentId = agentId;
    }

    @JsonInclude
    private long agentId;

    @JsonInclude
    private int instanceNumber = -1;

    @JsonInclude
    private int networkNumber = -1;

    @JsonInclude
    private String ipAddress;

    @JsonIgnore
    private String identifier;

    public BacnetIpControllerContext(long agentId, long orgId) throws Exception {
        super(agentId, orgId);
        this.agentId = agentId;
        setControllerType(FacilioControllerType.BACNET_IP.asInt());
    }

    public int getInstanceNumber() {
        return instanceNumber;
    }

    public void setInstanceNumber(int instanceNumber) {
        this.instanceNumber = instanceNumber;
    }

    public int getNetworkNumber() { return networkNumber; }
    public void setNetworkNumber(int networkNumber) { this.networkNumber = networkNumber; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    @JsonIgnore
    public String getModuleName() {
        return ASSETCATEGORY;
    }

    @Override
    public JSONObject getChildJSON() {
        JSONObject bacnetIpControllerJSON = new JSONObject();
        bacnetIpControllerJSON.put(AgentConstants.ID, getId());
        bacnetIpControllerJSON.put(AgentConstants.INSTANCE_NUMBER, getInstanceNumber());
        bacnetIpControllerJSON.put(AgentConstants.IP_ADDRESS, getIpAddress());
        bacnetIpControllerJSON.put(AgentConstants.NETWORK_NUMBER, getNetworkNumber());
        return bacnetIpControllerJSON;
    }

    public List<Condition>  getControllerConditions() throws Exception {
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(getModuleName()); // change it to static final
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.NETWORK_NUMBER), String.valueOf(getNetworkNumber()), NumberOperators.EQUALS));
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.INSTANCE_NUMBER), String.valueOf(getInstanceNumber()),NumberOperators.EQUALS));
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.IP_ADDRESS),getIpAddress(), StringOperators.IS));
        return conditions;
    }

    @Override
    public String getIdentifier() {
        return instanceNumber+IDENTIFIER_SEPERATER+ipAddress+IDENTIFIER_SEPERATER+networkNumber;
    }


}
