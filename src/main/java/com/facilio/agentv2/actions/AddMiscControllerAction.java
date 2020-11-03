package com.facilio.agentv2.actions;

import java.net.HttpURLConnection;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.iotmessage.AgentMessenger;
import com.facilio.agentv2.misc.MiscController;

public class AddMiscControllerAction extends AgentIdAction{

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(AddMiscControllerAction.class.getName());

	public String addMiscController() {
		
		try {
			MiscController context = new MiscController();
			context.setAgentId(getAgentId());
			context.setName(getName());
			context.setControllerType(getControllerType());
			if(AgentMessenger.sendAddControllerCommand(context)) {
				setResult(AgentConstants.RESULT, SUCCESS);
	            setResponseCode(HttpURLConnection.HTTP_OK);
			}else {
				setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
                setResult(AgentConstants.RESULT, ERROR);
			}
		}catch(Exception e) {
			LOGGER.error("Exception occurred while adding Misc controller.",e);
			setResult(AgentConstants.EXCEPTION, e.getMessage());
			internalError();
		}
		return SUCCESS;
	}
}
