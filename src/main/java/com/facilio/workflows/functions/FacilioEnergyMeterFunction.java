package com.facilio.workflows.functions;

import com.facilio.bmsconsole.context.EnergyMeterContext;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.context.ScriptContext;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;

import java.util.List;
import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.ENERGYMETER_FUNCTION)
public class FacilioEnergyMeterFunction {
	public Object getPhysicalMeterCount(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		if(objects[0] == null) {
			return null;
		}

		Long spaceId = Long.parseLong(objects[0].toString());
		Long purposeId = null;
		if(objects.length > 1 && objects[1] != null) {
			purposeId = Long.parseLong(objects[1].toString());
		}

		List<EnergyMeterContext> meters = DeviceAPI.getPhysicalMeter(spaceId,purposeId);

		return meters.size();
	}

	public Object getVirtualMeterCount(ScriptContext scriptContext, Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		if(objects[0] == null) {
			return null;
		}

		Long spaceId = Long.parseLong(objects[0].toString());

		Long purposeId = null;
		if(objects.length > 1 && objects[1] != null) {
			purposeId = Long.parseLong(objects[1].toString());
		}

		List<EnergyMeterContext> meters = DeviceAPI.getVirtualMeters(spaceId,purposeId);

		return meters.size();
	}

	public void checkParam(Object... objects) throws Exception {
		if(objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}
}