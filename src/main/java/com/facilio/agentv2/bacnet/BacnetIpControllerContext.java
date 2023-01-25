package com.facilio.agentv2.bacnet;

import com.facilio.accounts.util.AccountUtil;
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

    public static boolean validateControllerJSON(Map<String, Object> map) throws Exception {
        if(  containsValueCheck(AgentConstants.IP_ADDRESS,map) && containsValueCheck(AgentConstants.INSTANCE_NUMBER,map) && containsValueCheck(AgentConstants.NETWORK_NUMBER,map) ){
            return true;
        }
        throw new Exception(" controllerJSON of type "+ASSETCATEGORY+" must have keys "+ AgentConstants.IP_ADDRESS+ ","+AgentConstants.INSTANCE_NUMBER+","+AgentConstants.NETWORK_NUMBER+"  and json is ->"+map);
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

    @JsonInclude
    private int vendorId;

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

    public int getVendorId() { return vendorId; }
    public void setVendorId(int vendorId) { this.vendorId = vendorId; }

    @JsonIgnore
    public String getModuleName() {
        return ASSETCATEGORY;
    }

    @Override
    public JSONObject getChildJSON() {
        JSONObject bacnetIpControllerJSON = new JSONObject();
        bacnetIpControllerJSON.put(AgentConstants.INSTANCE_NUMBER, getInstanceNumber());
        bacnetIpControllerJSON.put(AgentConstants.IP_ADDRESS, getIpAddress());
        bacnetIpControllerJSON.put(AgentConstants.NETWORK_NUMBER, getNetworkNumber());
        return bacnetIpControllerJSON;
    }

    public List<Condition>  getControllerConditions() throws Exception {
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(FacilioConstants.ContextNames.BACNET_IP_CONTROLLER_MODULE_NAME);// change it to static final
        if (AccountUtil.getCurrentOrg().getOrgId() == 152) {
            LOGGER.info("Module Name : " + getModuleName());
            LOGGER.info("Fields : " + fieldsMap);
            LOGGER.info("nn : " + fieldsMap.get(AgentConstants.NETWORK_NUMBER));
            LOGGER.info("in : " + fieldsMap.get(AgentConstants.INSTANCE_NUMBER));
            LOGGER.info("ip : " + fieldsMap.get(AgentConstants.IP_ADDRESS));
            LOGGER.info("vid : " + fieldsMap.get(AgentConstants.VENDOR_ID));
        }
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.NETWORK_NUMBER), String.valueOf(getNetworkNumber()), NumberOperators.EQUALS));
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.INSTANCE_NUMBER), String.valueOf(getInstanceNumber()),NumberOperators.EQUALS));
        if(getIpAddress() != null){
            conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.IP_ADDRESS),getIpAddress(), StringOperators.IS));
        }else {
            throw new Exception(" ip address can't be null for bacnetIpController ");
        }
        return conditions;
    }

    @Override
    public String getIdentifier() {
        return instanceNumber+IDENTIFIER_SEPERATER+ipAddress+IDENTIFIER_SEPERATER+networkNumber;
    }
    @Override
    public boolean equals(Object o){
        if(o instanceof BacnetIpControllerContext){
            BacnetIpControllerContext obj = (BacnetIpControllerContext) o;
            return this.getNetworkNumber()==obj.getNetworkNumber() && this.getInstanceNumber()==obj.getInstanceNumber() && this.getIpAddress().equals(obj.getIpAddress()) && super.equals(obj);
        }else{
            return false;
        }
    }
}
