package com.facilio.agentv2.opcua;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
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
    private int securityMode = -1;

    @JsonInclude
    private int securityPolicy = -1;


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
        this.securityMode = securityMode;
    }

    public int getSecurityMode() {
        return securityMode;
    }

    public void setSecurityPolicy(int securityPolicy) {
        this.securityPolicy = securityPolicy;
    }

    public int getSecurityPolicy() {
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
        controllerJSON.put(AgentConstants.URL, this.url);
        controllerJSON.put(AgentConstants.CERT_PATH, this.certPath);
        controllerJSON.put(AgentConstants.SECURITY_MODE, securityMode);
        controllerJSON.put(AgentConstants.SECURITY_POLICY, securityPolicy);
        return controllerJSON;
    }

    @Override
    public List<Condition> getControllerConditions() throws Exception {
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(getModuleName());
        if (fieldsMap.containsKey(AgentConstants.URL) && fieldsMap.containsKey(AgentConstants.URL) && fieldsMap.containsKey(AgentConstants.URL)) {
            conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.URL), getUrl(), StringOperators.IS));
            conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.SECURITY_MODE), String.valueOf(getSecurityMode()), NumberOperators.EQUALS));
            conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.SECURITY_POLICY), String.valueOf(getSecurityPolicy()), NumberOperators.EQUALS));
        } else {
            LOGGER.info("Error while getting conditions");
            LOGGER.info(fieldsMap);
        }
        LOGGER.info(conditions);
        return conditions;
    }

    @Override
    public String getIdentifier() {
        return url+IDENTIFIER_SEPERATER+certPath+securityMode+IDENTIFIER_SEPERATER+securityPolicy;
    }
}
