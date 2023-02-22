package com.facilio.workflowv2.modulefunctions;

import com.facilio.constants.FacilioConstants;
import com.facilio.scriptengine.annotation.ScriptModule;

import java.util.List;
import java.util.Map;

@ScriptModule(moduleName = FacilioConstants.ContextNames.EMPLOYEE)
public class FacilioEmployeeModuleFunctions extends FacilioModuleFunctionImpl  {
    @Override
    public void add(Map<String,Object> globalParams, List<Object> objects) throws Exception {
        v3Add(globalParams, objects);
    }
}
