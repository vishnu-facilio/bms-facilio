package com.facilio.workflowv2.modulefunctions;

import java.util.List;
import java.util.Map;

import com.facilio.constants.FacilioConstants;
import com.facilio.workflowv2.annotation.ScriptModule;

@ScriptModule(moduleName = FacilioConstants.ContextNames.FLOOR)
public class FacilioFloorModuleFunctions extends FacilioModuleFunctionImpl {
	
	@Override
	public void add(Map<String, Object> globalParams, List<Object> objects) throws Exception {
		v3Add(globalParams, objects);
	}

}
