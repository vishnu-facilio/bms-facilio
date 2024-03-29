package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.agentv2.opcua.OpcUaControllerContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.validation.constraints.NotNull;
import java.net.HttpURLConnection;

public class OpcUaControllerAction extends AgentIdAction {

    private static final Logger LOGGER = LogManager.getLogger(OpcUaControllerAction.class.getName());

    @NotNull
    private String url;

    @NotNull
    private String name;

    private String certPath;

    private int securityMode;
    private int securityPolicy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCertPath() {
        return certPath;
    }

    public void setCertPath(String certPath) {
        this.certPath = certPath;
    }

    public int getSecurityMode() {
        return securityMode;
    }

    public void setSecurityMode(int securityMode) {
        this.securityMode = securityMode;
    }

    public int getSecurityPolicy() {
        return securityPolicy;
    }

    public void setSecurityPolicy(int securityPolicy) {
        this.securityPolicy = securityPolicy;
    }

    public String addController() {
        try {
            OpcUaControllerContext controllerContext = new OpcUaControllerContext();
            controllerContext.setCertPath(getCertPath());
            controllerContext.setUrl(getUrl());
            controllerContext.setSecurityMode(getSecurityMode());
            controllerContext.setSecurityPolicy(getSecurityPolicy());
            controllerContext.setAgentId(getAgentId());
            controllerContext.setName(getName());
            AgentMessenger.sendConfigureOpcUaControllerCommand(controllerContext);
            setResponseCode(HttpURLConnection.HTTP_OK);
            setResult(AgentConstants.RESULT, SUCCESS);
        } catch (Exception e) {
            LOGGER.info("Exception while sending add opcua controller");
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
        }
        return SUCCESS;
    }

}
