package com.facilio.workflows.functions;

import com.facilio.bmsconsole.util.PsychrometricUtil;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;

import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.PSYCHROMETRICS_FUNCTION)
public class FacilioPsychrometricsFunction {
	public Object getMoistAirEnthalpy(ScriptContext scriptContext,  Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);
		if(objects == null || objects.length <2 || objects[0] == null || objects[1] == null || objects[2] == null) {
			return null;
		}
		double dryBulbTemperature = Double.parseDouble(objects[0].toString());
		double pressure = Double.parseDouble(objects[1].toString());
		double relativeHumidity = Double.parseDouble(objects[2].toString());

		return PsychrometricUtil.getMoistAirEnthalpy(dryBulbTemperature, pressure, relativeHumidity);
	}

	public Object getDewPointTemperature(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);
		if(objects == null || objects.length <2 || objects[0] == null || objects[1] == null || objects[2] == null) {
			return null;
		}
		double dryBulbTemperature = Double.parseDouble(objects[0].toString());
		double pressure = Double.parseDouble(objects[1].toString());
		double relativeHumidity = Double.parseDouble(objects[2].toString());

		return PsychrometricUtil.getDewPointTemperatureFromRelativeHumidity(dryBulbTemperature, relativeHumidity, pressure);
	}

	public Object getWetBulbTemperature(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);
		if(objects == null || objects.length <2 || objects[0] == null || objects[1] == null || objects[2] == null) {
			return null;
		}
		double dryBulbTemperature = Double.parseDouble(objects[0].toString());
		double pressure = Double.parseDouble(objects[1].toString());
		double relativeHumidity = Double.parseDouble(objects[2].toString());

		return PsychrometricUtil.getWetBulbTemperatureFromRelativeHumidity(dryBulbTemperature, relativeHumidity, pressure);
	}

	public void checkParam(Object... objects) throws Exception {
		if(objects == null || objects.length == 0) {
			throw new FunctionParamException("Required Object is null or empty");
		}
	}
}