package com.facilio.workflows.functions;

import java.util.Map;
import java.util.logging.Logger;

public interface FacilioWorkflowFunctionInterface {
	static final Logger LOGGER = Logger.getLogger(FacilioWorkflowFunctionInterface.class.getName());
	public Object execute(Map<String, Object> globalParam, Object... objects) throws Exception;
}
