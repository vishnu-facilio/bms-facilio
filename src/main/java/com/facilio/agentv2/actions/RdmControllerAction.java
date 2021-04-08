package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.agentv2.rdm.RdmControllerContext;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotNull;
import java.net.HttpURLConnection;

@Getter
@Setter
public class RdmControllerAction extends AgentIdAction {

    private static final Logger LOGGER = LogManager.getLogger(RdmControllerContext.class.getName());

    @NotNull
    @URL
    private String name;
    private String url;
    private String username;
    private String password;
    private String device;

    public String addController() {

        try {
            RdmControllerContext controllerContext = new RdmControllerContext();
            controllerContext.setUrl(getUrl());
            controllerContext.setPassword(getPassword());
            controllerContext.setUsername(getUsername());
            controllerContext.setAgentId(getAgentId());
            controllerContext.setName(getName());
            AgentMessenger.sendRdmAddControllerCommand(controllerContext);
            setResponseCode(HttpURLConnection.HTTP_OK);
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
