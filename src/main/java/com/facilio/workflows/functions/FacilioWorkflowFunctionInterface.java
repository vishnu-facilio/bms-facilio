package com.facilio.workflows.functions;

import java.util.logging.Logger;

public interface FacilioWorkflowFunctionInterface {
	static final Logger LOGGER = Logger.getLogger(FacilioWorkflowFunctionInterface.class.getName());
	public Object execute(Object... objects) throws Exception;
}
