package com.facilio.workflows.functions;

import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import com.facilio.workflows.util.FunctionUtil;

import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.COST_FUNCTION)
public class FacilioCostFunctions {
	public Object getCostFromKwh(Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		if(objects[0] == null) {
			return false;
		}

		Double kwh = Double.parseDouble(objects[0].toString());

		double temp = kwh;
		double total = 0.0;
		for(int i=1; i<5; i++) {

			double cost = FunctionUtil.getCostValueForSlab(i);
			temp = kwh - 2000;
			if(temp < 0) {
				total = total + (kwh * cost);
				break;
			}
			else {
				if(i == 4) {
					total = total + (kwh * cost);
				}
				else {
					total = total + (2000 * cost);
				}
				kwh = temp;
			}
		}
		return total;
	}

	public void checkParam(Object... objects) throws Exception {
		if(objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}
}
