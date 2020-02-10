package com.facilio.agentv2.niagara;

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
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NiagaraController extends Controller {

    private static final Logger LOGGER = LogManager.getLogger(NiagaraController.class.getName());

    public static final String ASSETCATEGORY = FacilioConstants.ContextNames.NIAGARA_CONTROLLER_MODULE_NAME;

    @JsonInclude
    private String ipAddress;

    @JsonInclude
    private long portNumber = -1;

    @JsonInclude
    private String identifier;

    public NiagaraController() {
        setControllerType(FacilioControllerType.NIAGARA.asInt());
    }

    public NiagaraController(long agentId, long orgId) throws Exception {
        super(agentId, orgId);
        setControllerType(FacilioControllerType.NIAGARA.asInt());
    }

    public String getModuleName() { return ASSETCATEGORY; }

    public long getPortNumber() { return portNumber; }
    public void setPortNumber(long portNumber) { this.portNumber = portNumber; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }


    public void processIdentifier(String identifier) throws Exception {
        if ((identifier != null) && (!identifier.isEmpty())) {
            String[] uniques = identifier.split(IDENTIFIER_SEPERATOR);
            if ((uniques.length == 3) && ((FacilioControllerType.valueOf(Integer.parseInt(uniques[0])) == FacilioControllerType.NIAGARA))) {
                portNumber = Long.parseLong(uniques[1]);
                ipAddress = uniques[2];
                this.identifier = identifier;
            } else {
                throw new Exception(" Exceprion while processing identifier -- length or Type didnt match -> " + identifier);
            }
        } else {
            throw new Exception("Exception Occurred, identifier can't be null or empty ->"+identifier);
        }
    }

    @Override
    public String makeIdentifier() throws Exception {
        if ((identifier != null) && (!identifier.isEmpty())) {
            return identifier;
        }
        identifier = FacilioControllerType.NIAGARA.asInt() + IDENTIFIER_SEPERATOR + portNumber + IDENTIFIER_SEPERATOR + ipAddress;
        return identifier;
    }

    @Override
    public JSONObject getChildJSON() {
        JSONObject niagaraControllerJSON = new JSONObject();
        niagaraControllerJSON.put(AgentConstants.ID, getId());
        niagaraControllerJSON.put(AgentConstants.PORT_NUMBER, getPortNumber());
        niagaraControllerJSON.put(AgentConstants.IP_ADDRESS, getIpAddress());
        return niagaraControllerJSON;
    }

    @Override
    public List<Condition> getControllerConditions(String identifier) throws Exception {
        processIdentifier(identifier);
        List<Condition> conditions = new ArrayList<>();
        Map<String, FacilioField> fieldsMap = getFieldsMap(getModuleName());
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.PORT_NUMBER), String.valueOf(getPortNumber()), NumberOperators.EQUALS));
        conditions.add(CriteriaAPI.getCondition(fieldsMap.get(AgentConstants.IP_ADDRESS),getIpAddress(), StringOperators.IS));
        return conditions;
    }

    public static NiagaraController getNiagaraControllerFromMap(long agentId, Map<String, Object> controllerMap) throws Exception {
        if (controllerMap == null || controllerMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + controllerMap);
        }
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        if (containsValueCheck(AgentConstants.IDENTIFIER, controllerMap)) {
            NiagaraController controller = new NiagaraController(agentId, orgId);
            controller.processIdentifier((String) controllerMap.get(AgentConstants.IDENTIFIER));
            return (NiagaraController) controller.getControllerFromJSON(controllerMap);
        }
        if (containsValueCheck(AgentConstants.PORT_NUMBER, controllerMap) && containsValueCheck(AgentConstants.IP_ADDRESS, controllerMap)) {
            NiagaraController controller = new NiagaraController(agentId, orgId);
            controller.setIpAddress((String) controllerMap.get(AgentConstants.IP_ADDRESS));
            controller.setPortNumber(Math.toIntExact((Long) controllerMap.get(AgentConstants.PORT_NUMBER)));
            return (NiagaraController) controller.getControllerFromJSON(controllerMap);
        }
        throw new Exception(" Mandatory fields like " + AgentConstants.IP_ADDRESS + "," + AgentConstants.PORT_NUMBER + "," + AgentConstants.IDENTIFIER + " might be missing from input parameter -> " + controllerMap);
    }

}
