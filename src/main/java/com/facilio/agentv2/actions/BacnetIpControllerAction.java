package com.facilio.agentv2.actions;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.bacnet.BacnetIpControllerContext;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.HttpURLConnection;

@Getter @Setter
public class BacnetIpControllerAction extends AgentIdAction{

    private static final Logger LOGGER = LogManager.getLogger(OpcUaControllerAction.class.getName());

    BacnetIpControllerContext bacnetController;

    public String addController() throws Exception {
        try {
            AgentMessenger.sendControllerConfig(bacnetController);
            setResponseCode(HttpURLConnection.HTTP_OK);
            setResult(AgentConstants.RESULT, SUCCESS);
        } catch (Exception e) {
            LOGGER.info("Exception while sending add BacnetIp controller");
            setResult(AgentConstants.RESULT, ERROR);
            setResult(AgentConstants.EXCEPTION, e.getMessage());
            setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
            return ERROR;
        }
        return SUCCESS;
    }
}
