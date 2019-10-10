package com.facilio.agentnew.niagara;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentnew.AgentConstants;
import com.facilio.agentnew.controller.Controller;
import com.facilio.constants.FacilioConstants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Map;

public class NiagaraController extends Controller {

    private static final Logger LOGGER = LogManager.getLogger(NiagaraController.class.getName());

    public static final String ASSETCATEGORY = FacilioConstants.ContextNames.NIAGARA_CONTROLLER_MODULE_NAME;

    private String ipAddress;
    private long portNumber = -1;
    private String identifier;

    public NiagaraController() {
    }

    public NiagaraController(long agentId, long orgId) throws Exception {
        new NiagaraController(agentId, orgId, null);
    }

    public NiagaraController(long agentId, long orgId, String indentifier) throws Exception {
        super(agentId, orgId);
        processIdentifier(indentifier);
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

    public static NiagaraController getNiagaraControllerFromMap(long agentId, Map<String, Object> controllerMap) throws Exception {
        if (controllerMap == null || controllerMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + controllerMap);
        }
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        if (containsValueCheck(AgentConstants.IDENTIFIER, controllerMap)) {
            NiagaraController controller = new NiagaraController(agentId, orgId, (String) controllerMap.get(AgentConstants.IDENTIFIER));
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
