package com.facilio.bmsconsole.actions;

import com.facilio.util.FacilioUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.constants.FacilioConstants;

public class CostAction extends FacilioAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(CostAction.class.getName());
	
	public String addUtilityProvider() {
		try {
			return SUCCESS;
		}
		catch(Exception e) {
			LOGGER.error("Error occurred during addition of Utility Provider", e); 
			throw new IllegalArgumentException(FacilioUtil.ERROR_MESSAGE);
		}
	}
	
}
