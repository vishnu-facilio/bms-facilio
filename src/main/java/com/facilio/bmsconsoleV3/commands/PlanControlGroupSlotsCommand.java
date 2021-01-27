package com.facilio.bmsconsoleV3.commands;

import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.BmsJobUtil;
import com.facilio.control.util.ControlScheduleUtil;

import con.facilio.control.ControlGroupContext;

public class PlanControlGroupSlotsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupContext controlGroupContext = (ControlGroupContext) ControlScheduleUtil.getObjectFromRecordMap(context, ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
		
		JSONObject obj = new JSONObject();
		
		obj.put(ControlScheduleUtil.CONTROL_GROUP_ID, controlGroupContext.getId());
		
		BmsJobUtil.deleteJobWithProps(controlGroupContext.getId(), "ControlScheduleSlotCreationJob");
		
		BmsJobUtil.scheduleOneTimeJobWithProps(controlGroupContext.getId(), "ControlScheduleSlotCreationJob", 5, "facilio", obj);

		return false;
	}

}
