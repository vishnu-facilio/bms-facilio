package com.facilio.workflows.functions;

import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import com.facilio.workflows.util.WorkflowUtil;

import java.util.Map;
import java.util.TreeSet;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.THERMOPHYSICALR134A_FUNCTION)
public class ThermoPhysicalR134aFunctions {
	public Object getTempratureFromPresure(Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);
		if(objects[0] == null) {
			return null;
		}
		double pressure = Double.parseDouble(objects[0].toString());

		Map<Double, Double> chillerMap = WorkflowUtil.getChillerTempVsPressureMap();

		if(chillerMap.get(pressure) != null ) {
			return chillerMap.get(pressure);
		}
		else {
			TreeSet<Double> keySet = new TreeSet<Double>();

			keySet.addAll(chillerMap.keySet());

			Double lesserValue = null,greaterValue = null;
			Double lesserValueDelta = null,greaterValueDelta = null;

			Object[] keysetArray = keySet.toArray();

			for(int i=0;i<keysetArray.length;i++) {
				double key = (double) keysetArray[i];
				if(pressure < key) {
					if( i== 0) {										// pressure value is lesser than the first value in sheet
						return null;
					}
					greaterValue = key;
					lesserValue = (double) keysetArray[i-1];
					break;
				}
			}

			if(greaterValue == null || lesserValue == null) {			// pressure value is higher than the last value in sheet
				return null;
			}
			lesserValueDelta = pressure - lesserValue;
			greaterValueDelta = greaterValue - pressure;

			if(lesserValueDelta > greaterValueDelta) {
				return chillerMap.get(greaterValue);
			}
			else {
				return chillerMap.get(lesserValue);
			}
		}
	}

	public void checkParam(Object... objects) throws Exception {
		if(objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}
}