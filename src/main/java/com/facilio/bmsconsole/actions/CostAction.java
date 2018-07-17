package com.facilio.bmsconsole.actions;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.constants.FacilioConstants;

public class CostAction extends FacilioAction {
	private static final Logger LOGGER = LogManager.getLogger(CostAction.class.getName());
	
	public String addUtilityProvider() {
		try {
			return SUCCESS;
		}
		catch(Exception e) {
			setResponseCode(1);
			LOGGER.error("Error occurred during addition of Utility Provider", e);
			setMessage(FacilioConstants.ERROR_MESSAGE);
			return ERROR;
		}
	}
	
}
