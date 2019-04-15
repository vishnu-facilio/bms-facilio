package com.facilio.bmsconsole.actions;

import com.facilio.constants.FacilioConstants;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

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
			throw new IllegalArgumentException(FacilioConstants.ERROR_MESSAGE);
		}
	}
	
}
