package com.facilio.agentv2.opcua;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioIntEnum;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OpcUaControllerContext extends Controller {

    public static final String ASSETCATEGORY = FacilioConstants.ContextNames.OPC_UA_CONTROLLER_MODULE_NAME;
    private static final Logger LOGGER = LogManager.getLogger(OpcUaControllerContext.class.getName());

    @JsonInclude
    private String url;

    @JsonInclude
    private String identifier;

    @JsonInclude
    private String certPath;

    @JsonInclude
    private SecurityMode securityMode;

    @JsonInclude
    private SecurityPolicy securityPolicy ;


    public OpcUaControllerContext() {
        setControllerType(FacilioControllerType.OPC_UA.asInt());
    }

    public OpcUaControllerContext(long agentId, long orgId) throws Exception {
        super(agentId, orgId);
        setControllerType(FacilioControllerType.OPC_UA.asInt());
    }

    public String getUrl() {
        return url;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public String getCertPath() {
        return certPath;
    }

    public void setSecurityMode(int securityMode) {
        this.securityMode = SecurityMode.valueOf(securityMode);
    }

    public int getSecurityMode() {
        if (securityMode != null) {
            return securityMode.getIndex();
        }
        return -1;
    }

    public SecurityMode getSecurityModeEnum(){
        return securityMode;
    }

    public void setSecurityPolicy(int securityPolicy) {
        this.securityPolicy = SecurityPolicy.valueOf(securityPolicy);
    }

    public int getSecurityPolicy() {
        if (securityPolicy != null) {
            return securityPolicy.getIndex();
        }
        return -1;
    }

    public SecurityPolicy getSecurityPolicyEnum(){
        return securityPolicy;
    }

    public String getModuleName() {
        return ASSETCATEGORY;
    }

    public void setUrl(String url) {
        this.url = url;
    }



    @Override
    public JSONObject getChildJSON() {
        JSONObject controllerJSON = new JSONObject();
        controllerJSON.put(AgentConstants.URL, getUrl());
        controllerJSON.put(AgentConstants.SECURITY_MODE, getSecurityMode());
        controllerJSON.put(AgentConstants.SECURITY_POLICY, getSecurityPolicy());
        return controllerJSON;
    }

    @Override
    public List<Condition> getControllerConditions() throws Exception {
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(getModuleName());
        if (fieldsMap.containsKey(AgentConstants.URL) && fieldsMap.containsKey(AgentConstants.URL) && fieldsMap.containsKey(AgentConstants.URL) && (getSecurityMode() > -1)) {
            conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.URL), getUrl(), StringOperators.IS));
            if((fieldsMap.containsKey(AgentConstants.SECURITY_MODE) && (fieldsMap.get(AgentConstants.SECURITY_MODE) != null) && (getSecurityPolicy() > -1) )){
                conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.SECURITY_MODE), String.valueOf(getSecurityMode()), NumberOperators.EQUALS));
            }else {
                LOGGER.info(getSecurityMode()+" fieldsmap is missing sm "+fieldsMap);
            }
            if((fieldsMap.containsKey(AgentConstants.SECURITY_POLICY) && (fieldsMap.get(AgentConstants.SECURITY_POLICY) != null))){
                conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.SECURITY_POLICY), String.valueOf(getSecurityPolicy()), NumberOperators.EQUALS));
            }else {
                LOGGER.info(getSecurityPolicy()+" fieldsmap is missing sp "+fieldsMap);
            }
        } else {
            LOGGER.info("Error while getting conditions");
            LOGGER.info(fieldsMap);
        }
        LOGGER.info(conditions);
        return conditions;
    }


    @Override
    public String getIdentifier() {
        return getUrl();
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof OpcUaControllerContext){
            OpcUaControllerContext obj = (OpcUaControllerContext) o;
            return this.getSecurityMode()==obj.getSecurityMode() && this.getSecurityPolicy()==obj.getSecurityPolicy() && this.getUrl().equals(obj.getUrl()) && super.equals(obj);
        }else{
            return false;
        }
    }

    public enum SecurityMode implements FacilioIntEnum {
        NONE(0, "None"),
        SIGN(1, "Sign"),
        SIGN_AND_ENCRYPT(2, "Sign and encrypt");

        private int value;
        private String name;

        SecurityMode(int value, String name) {
            this.value = value;
            this.name = name;
        }
        public static SecurityMode valueOf(int value) {
            if (value >= 0 && value <= values().length) {
                return values()[value];
            }
            return null;
        }
        @Override
        public Integer getIndex(){
            return value;
        }
        @Override
        public String getValue(){
            return name;
        }
    }

    public enum SecurityPolicy implements FacilioIntEnum {
        NONE(0, "None"),
        Basic128Rsa15(1, "Basic128Rsa15"),
        Basic256(2, "Basic256"),
        Basic256Sha256(3, "Basic256Sha256"),
        Aes128_Sha256_RsaOaep(4, "Aes128_Sha256_RsaOaep"),
        Aes256_Sha256_RsaPss(5, "Aes256_Sha256_RsaPss");

        private int value;
        private String name;

        SecurityPolicy(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public static SecurityPolicy valueOf(int value) {
            if (value >= 0 && value <= values().length) {
                return values()[value];
            }
            return null;
        }
        @Override
        public Integer getIndex(){
            return value;
        }
        @Override
        public String getValue(){
            return name;
        }
    }
}
