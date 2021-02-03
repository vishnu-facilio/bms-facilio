package com.facilio.bmsconsoleV3.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlGroupTenentContext;
import com.facilio.control.util.ControlScheduleUtil;

public class GetParentcontrolGroupCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupContext group = (ControlGroupContext) context.get(ControlScheduleUtil.CONTROL_GROUP_CONTEXT);
		
		ControlGroupTenentContext childGroup = (ControlGroupTenentContext) ControlScheduleUtil.getControlGroup(group.getId(),ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME);
		
		ControlGroupContext parentGroup = ControlScheduleUtil.getControlGroup(childGroup.getParentGroup().getId(),ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
		
		context.put(ControlScheduleUtil.CONTROL_GROUP_CONTEXT, childGroup);
		context.put(ControlScheduleUtil.CONTROL_GROUP_CONTEXT_OLD, parentGroup);
		
		return false;
	}

}
