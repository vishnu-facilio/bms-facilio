package com.facilio.agentv2.opcua;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.Controller;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.fields.FacilioField;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OpcUaControllerContext extends Controller {

    public static final String ASSETCATEGORY = FacilioConstants.ContextNames.OPC_UA_CONTROLLER_MODULE_NAME;
    private static final Logger LOGGER = LogManager.getLogger(OpcUaControllerContext.class.getName());


    private String url;
    private String identifier;
    private String certPath;
    private int securityMode = -1;
    private int securityPolicy = -1;


    public OpcUaControllerContext() {
        setControllerType(FacilioControllerType.OPC_UA.asInt());
    }

    public OpcUaControllerContext(long agentId, long orgId) throws Exception {
        super(agentId, orgId);
        setControllerType(FacilioControllerType.OPC_UA.asInt());
    }

    public static OpcUaControllerContext getBacnetControllerFromMap(long agentId, Map<String, Object> controllerMap) throws Exception {
        if (controllerMap == null || controllerMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + controllerMap);
        }
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        if (containsValueCheck(AgentConstants.IDENTIFIER, controllerMap)) {
            OpcUaControllerContext controller = new OpcUaControllerContext(agentId, orgId);
            controller.processIdentifier((String) controllerMap.get(AgentConstants.IDENTIFIER));
            if (containsValueCheck(AgentConstants.SECURITY_MODE, controllerMap)) {
                controller.setSecurityMode(Math.toIntExact((Long) controllerMap.get(AgentConstants.SECURITY_MODE)));
            }
            if (containsValueCheck(AgentConstants.SECURITY_POLICY, controllerMap)) {
                controller.setSecurityPolicy(Math.toIntExact((Long) controllerMap.get(AgentConstants.SECURITY_POLICY)));
            }
            return (OpcUaControllerContext) controller.getControllerFromJSON(controllerMap);
        }
        if (containsValueCheck(AgentConstants.URL, controllerMap) && containsValueCheck(AgentConstants.CERT_PATH, controllerMap)) {
            OpcUaControllerContext controller = new OpcUaControllerContext(agentId, orgId);
            //controller.setModuleName(ASSETCATEGORY);
            if(containsValueCheck(AgentConstants.SECURITY_MODE,controllerMap)){
                controller.setSecurityMode(Math.toIntExact((Long) controllerMap.get(AgentConstants.SECURITY_MODE)));
            }
            if (containsValueCheck(AgentConstants.SECURITY_POLICY, controllerMap)) {
                controller.setSecurityPolicy(Math.toIntExact((Long) controllerMap.get(AgentConstants.SECURITY_POLICY)));
            }
            return (OpcUaControllerContext) controller.getControllerFromJSON(controllerMap);
        }
        throw new Exception(" Mandatory fields like " + AgentConstants.URL + "," + AgentConstants.CERT_PATH + " might be missing from input parameter -> " + controllerMap);
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


    public void processIdentifier(String identifier) throws Exception {
        if ((identifier != null) && (!identifier.isEmpty())) {
            String[] uniques = identifier.split(IDENTIFIER_SEPERATOR);
            if ((uniques.length == 4) && ((FacilioControllerType.valueOf(Integer.parseInt(uniques[0])) == FacilioControllerType.OPC_UA))) {
                this.url = uniques[1];
                this.securityMode = Integer.parseInt(uniques[2]);
                this.securityPolicy = Integer.parseInt(uniques[3]);
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
        if( (url != null && ( ! url.isEmpty() ) ) ){
            identifier = FacilioControllerType.OPC_UA.asInt()+IDENTIFIER_SEPERATOR+url+securityMode+IDENTIFIER_SEPERATOR+securityPolicy;
            return identifier;
        }
        throw new Exception("Exception Occurred, parameters for identifier not set yet");
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
    public List<Condition> getControllerConditions(String identifier) throws Exception {
        processIdentifier(identifier);
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(getModuleName());
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.URL),getUrl(), StringOperators.IS));
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.CERT_PATH),getCertPath(),StringOperators.IS));
        return conditions;
    }
}
