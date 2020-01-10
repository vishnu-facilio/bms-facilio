package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.agentv2.opcxmlda.OpcXmlDaControllerContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import java.net.HttpURLConnection;

public class OpcDaControllerAction extends AgentIdAction {
    private static final Logger LOGGER = LogManager.getLogger(OpcDaControllerAction.class.getName());


    @NotNull
    @URL
    private String url;

    private String userName;

    private String password;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String addController() {
        try {
            OpcXmlDaControllerContext controllerContext = new OpcXmlDaControllerContext();
            controllerContext.setUrl(getUrl());
            controllerContext.setPassword(getPassword());
            controllerContext.setUserName(getUserName());
            controllerContext.setAgentId(getAgentId());
            AgentMessenger.sendConfigureOpcXmlDaController(controllerContext);
            setResult(AgentConstants.RESULT, SUCCESS);
        } catch (Exception e) {
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR);
            LOGGER.info("Exception occurred while  sending configure controller ", e);
        }
        return SUCCESS;
    }
}
