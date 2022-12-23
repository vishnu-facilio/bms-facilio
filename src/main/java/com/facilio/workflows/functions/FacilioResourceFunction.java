package com.facilio.workflows.functions;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.util.InventoryApi;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsoleV3.context.InviteVisitorContextV3;
import com.facilio.bmsconsoleV3.context.VisitorLogContextV3;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;

import java.util.Map;

@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.RESOURCE_FUNCTION)
public class FacilioResourceFunction {
	public Object getResourceName(Map<String, Object> globalParam, Object... objects) throws Exception {

		checkParam(objects);

		if(objects[0] == null) {
			return null;
		}

		Long resourceId = Long.parseLong(objects[0].toString());

		boolean isWithSpaceName = objects.length > 1 && objects[1] != null ? Boolean.parseBoolean(objects[1].toString()) : false;

		ResourceContext resource = ResourceAPI.getResource(resourceId, true);

		if(!isWithSpaceName) {
			return resource.getName();
		}
		else {
			if(resource.getResourceType() == ResourceContext.ResourceType.SPACE.getValue()) {
				return resource.getName();
			}
			else {
				ResourceContext space = ResourceAPI.getResource(resource.getSpaceId());
				if (space != null) { //For marked as deleted resources
					return resource.getName() +", "+space.getName();
				}
				return null;
			}
		}
	}

	public Object getVisitorLog(Map<String, Object> globalParam, Object... objects) throws Exception {

		VisitorLogContextV3 vLog = V3VisitorManagementAPI.getVisitorLogTriggers(Long.valueOf(objects[0].toString()), null, false);
		return vLog;

	}

	public Object getVisitorInvite(Map<String, Object> globalParam, Object... objects) throws Exception {

		InviteVisitorContextV3 invite = V3VisitorManagementAPI.getVisitorInviteTriggers(Long.valueOf(objects[0].toString()), null, false);
		return invite;

	}

	public Object getVendor(Map<String, Object> globalParam, Object... objects) throws Exception {

		VendorContext vendor = InventoryApi.getVendor(Long.valueOf(objects[0].toString()));
		return vendor;

	}

	public Object getBaseSpace(Map<String, Object> globalParam, Object... objects) throws Exception {

		BaseSpaceContext baseSpaceContext = SpaceAPI.getBaseSpace(Long.valueOf(objects[0].toString()));
		return baseSpaceContext;

	}

	private void checkParam(Object... objects) throws Exception {
		if(objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}
}
