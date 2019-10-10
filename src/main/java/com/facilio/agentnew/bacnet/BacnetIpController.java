package com.facilio.agentnew.bacnet;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentnew.AgentConstants;
import com.facilio.agentnew.controller.Controller;
import com.facilio.constants.FacilioConstants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.Map;

public class BacnetIpController extends Controller {

    private static final Logger LOGGER = LogManager.getLogger(BacnetIpController.class.getName());

    public static final String ASSETCATEGORY = FacilioConstants.ContextNames.BACNET_IP_CONTROLLER_MODULE_NAME;

    private int instanceNumber = -1;
    private int networkNumber = -1;
    private String ipAddress;
    private String identifier;

    public BacnetIpController() { }

    public BacnetIpController(long agentId, long orgId, String identifier) throws Exception {
        super(agentId, orgId);
        processIdentifier(identifier);
        setControllerType(FacilioControllerType.BACNET_IP.asInt());
    }

    public BacnetIpController(long agentId, long orgId) throws Exception {
        new BacnetIpController(agentId, orgId, null);
    }


    public String getModuleName() { return ASSETCATEGORY; }

    public int getInstanceNumber() { return instanceNumber; }
    public void setInstanceNumber(int instanceNumber) { this.instanceNumber = instanceNumber; }

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
        if ((instanceNumber > 0) && (networkNumber > 0) && isNotNull(ipAddress)) {
            identifier = FacilioControllerType.BACNET_IP.asInt() + IDENTIFIER_SEPERATOR + instanceNumber + IDENTIFIER_SEPERATOR + networkNumber + IDENTIFIER_SEPERATOR + ipAddress;
            ;
            return identifier;
        }
        throw new Exception(" Parameters not set yet " + instanceNumber + "- -" + networkNumber + "- -" + ipAddress);
    }


    public static BacnetIpController getBacnetControllerFromMap(long agentId, Map<String, Object> controllerMap) throws Exception {
        if (controllerMap == null || controllerMap.isEmpty()) {
            throw new Exception(" Map for controller can't be null or empty ->" + controllerMap);
        }
        long orgId = AccountUtil.getCurrentOrg().getOrgId();
        if (containsValueCheck(AgentConstants.IDENTIFIER, controllerMap)) {
            BacnetIpController controller = new BacnetIpController(agentId, orgId, (String) controllerMap.get(AgentConstants.IDENTIFIER));
            return (BacnetIpController) controller.getControllerFromJSON(controllerMap);
        }
        if (containsValueCheck(AgentConstants.INSTANCE_NUMBER, controllerMap) && containsValueCheck(AgentConstants.IP_ADDRESS, controllerMap) && containsValueCheck(AgentConstants.NETWORK_NUMBER, controllerMap)) {
            BacnetIpController controller = new BacnetIpController(agentId, orgId);
            controller.setIpAddress((String) controllerMap.get(AgentConstants.IP_ADDRESS));
            controller.setInstanceNumber(Math.toIntExact((Long) controllerMap.get(AgentConstants.INSTANCE_NUMBER)));
            controller.setNetworkNumber(Math.toIntExact((Long) controllerMap.get(AgentConstants.NETWORK_NUMBER)));
            return (BacnetIpController) controller.getControllerFromJSON(controllerMap);
        }
        throw new Exception(" Mandatory fields like " + AgentConstants.INSTANCE_NUMBER + "," + AgentConstants.IP_ADDRESS + "," + AgentConstants.NETWORK_NUMBER + " might be missing from input parameter -> " + controllerMap);
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


}
