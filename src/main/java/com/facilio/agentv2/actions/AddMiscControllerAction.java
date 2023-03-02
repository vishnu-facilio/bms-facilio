package com.facilio.agentv2.actions;

import java.net.HttpURLConnection;

import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.cacheimpl.AgentBean;
import com.facilio.agentv2.misc.MiscControllerContext;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.FacilioException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.agentv2.AgentConstants;

public class AddMiscControllerAction extends AgentIdAction{

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(AddMiscControllerAction.class.getName());

	public String addMiscController() {
		
		try {
            AgentBean bean = (AgentBean) BeanFactory.lookup("AgentBean");
            FacilioAgent agent = bean.getAgent(getAgentId());
            if (agent != null) {
                Long controllerId = null;
                // if (deviceId != null && deviceId > 0) {
                    MiscControllerContext context = new MiscControllerContext();
                    context.setAgentId(agent.getId());
                    context.setActive(true);
                    context.setDataInterval(agent.getInterval() * 60 * 1000);
                    context.setName(getName());
                    context.setControllerType(getControllerType());
                //    context.setDeviceId(deviceId);
                    controllerId = AgentConstants.getControllerBean().addController(context, false);
                // }
                if (controllerId != null && controllerId > 0) {
                    //     FieldDeviceApi.updateDeviceConfigured(deviceId);
                    LOGGER.info("successfully marked a Misc Device - " + getName() + " as a Controller. ");
                    setResult(AgentConstants.RESULT, SUCCESS);
                    setResponseCode(HttpURLConnection.HTTP_OK);
                } else {
                    setResponseCode(HttpURLConnection.HTTP_BAD_REQUEST);
                    setResult(AgentConstants.RESULT, ERROR);
                }
            } else {
                throw new FacilioException("Agent not found");
			}
		}catch(Exception e) {
			LOGGER.error("Exception occurred while adding Misc controller.",e);
			setResult(AgentConstants.EXCEPTION, e.getMessage());
			internalError();
		}
		return SUCCESS;
	}
}
