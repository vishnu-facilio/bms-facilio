package com.facilio.agentv2.actions;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.device.Device;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.agentv2.misc.MiscController;

public class AddMiscControllerAction extends AgentIdAction{

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(AddMiscControllerAction.class.getName());

	public String addMiscController() {
		
		try {
			Device fieldDevice = new Device();
			fieldDevice.setName(getName());
			fieldDevice.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
			fieldDevice.setAgentId(getAgentId());
			fieldDevice.setControllerType(getControllerType());
			fieldDevice.setCreatedTime(System.currentTimeMillis());
			Long deviceId = FieldDeviceApi.addFieldDevice(fieldDevice);
			Long controllerId = null;
			if(deviceId != null && deviceId > 0) {
				MiscController context = new MiscController();
				context.setAgentId(getAgentId());
				context.setActive(true);
				context.setDataInterval(900000);
				context.setName(getName());
				context.setControllerType(getControllerType());
				context.setDeviceId(deviceId);
				controllerId = ControllerApiV2.addController(context);
			}
			if(controllerId != null && controllerId >0) {
				FieldDeviceApi.updateDeviceConfigured(deviceId);
				LOGGER.info("successfully marked a Misc Device - "+getName()+" as a Controller. ");
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
