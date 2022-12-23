package com.facilio.workflows.functions;

import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;

import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.ML_NAMESPACE_FUNCTION)
public class FacilioMLNameSpaceFunctions {
	public Object print(Map<String, Object> globalParam, Object... objects) throws Exception {

		return "HELLO";
	}

	public void checkParam(Object... objects) throws Exception {
		if(objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}
}