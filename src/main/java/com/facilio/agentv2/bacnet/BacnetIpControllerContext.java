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
        //processIdentifier(identifier);
        setControllerType(FacilioControllerType.BACNET_IP.asInt());
    }

    public static BacnetIpControllerContext getBacnetControllerFromMap(long agentId, Map<String, Object> controllerMap) throws Exception {
        if (controllerMap == null || controllerMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + controllerMap);
        }
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        if (containsValueCheck(AgentConstants.IDENTIFIER, controllerMap)) {
            BacnetIpControllerContext controller = new BacnetIpControllerContext(agentId, orgId);
            controller.processIdentifier((String) controllerMap.get(AgentConstants.IDENTIFIER));
            return (BacnetIpControllerContext) controller.getControllerFromJSON(controllerMap);
        }
        if (containsValueCheck(AgentConstants.INSTANCE_NUMBER, controllerMap) && containsValueCheck(AgentConstants.IP_ADDRESS, controllerMap) && containsValueCheck(AgentConstants.NETWORK_NUMBER, controllerMap)) {
            BacnetIpControllerContext controller = new BacnetIpControllerContext(agentId, orgId);
            controller.setIpAddress((String) controllerMap.get(AgentConstants.IP_ADDRESS));
            controller.setInstanceNumber(Math.toIntExact((Long) controllerMap.get(AgentConstants.INSTANCE_NUMBER)));
            controller.setNetworkNumber(Math.toIntExact((Long) controllerMap.get(AgentConstants.NETWORK_NUMBER)));
            return (BacnetIpControllerContext) controller.getControllerFromJSON(controllerMap);
        }
        throw new Exception(" Mandatory fields like " + AgentConstants.INSTANCE_NUMBER + "," + AgentConstants.IP_ADDRESS + "," + AgentConstants.NETWORK_NUMBER + " might be missing from input parameter -> " + controllerMap);
    }

    /*public BacnetIpController(long agentId, long orgId) throws Exception {
        new BacnetIpController(agentId, orgId, null);
    }*/


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


    public void processIdentifier(String identifier) throws Exception {
        if ((identifier != null) && (!identifier.isEmpty())) {
            String[] uniques = identifier.split(IDENTIFIER_SEPERATOR);
            if ((uniques.length == 4) && ((FacilioControllerType.valueOf(Integer.parseInt(uniques[0])) == FacilioControllerType.BACNET_IP))) {
                instanceNumber = Integer.parseInt(uniques[1]);
                networkNumber = Integer.parseInt(uniques[2]);
                ipAddress = uniques[3];
                LOGGER.info("setting instance number " + instanceNumber + "  network number " + networkNumber + "   ip address " + ipAddress);
                this.identifier = identifier;
            } else {
                throw new Exception(" Exceprion while processing identifier -- length or Type didnt match ");
            }
        } else {
            throw new Exception("Exception Occurred, identifier can't be null or empty ->"+identifier);
        }
    }

    @Override
    public String makeIdentifier() throws Exception {
        if (identifier != null) {
            return identifier;
        }
        if ((instanceNumber >= 0) && (networkNumber >= 0) && isNotNull(ipAddress)) {
            identifier = FacilioControllerType.BACNET_IP.asInt() + IDENTIFIER_SEPERATOR + instanceNumber + IDENTIFIER_SEPERATOR + networkNumber + IDENTIFIER_SEPERATOR + ipAddress;
            return identifier;
        }
        throw new Exception(" Parameters not set yet " + instanceNumber + "- -" + networkNumber + "- -" + ipAddress);
    }

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

    @Override
    public List<Condition> getControllerConditions(String identifier) throws Exception {
        processIdentifier(identifier);
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(getModuleName());
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.NETWORK_NUMBER), String.valueOf(getNetworkNumber()), NumberOperators.EQUALS));
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.INSTANCE_NUMBER), String.valueOf(getInstanceNumber()),NumberOperators.EQUALS));
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.IP_ADDRESS),getIpAddress(), StringOperators.IS));
        return conditions;
    }


}
