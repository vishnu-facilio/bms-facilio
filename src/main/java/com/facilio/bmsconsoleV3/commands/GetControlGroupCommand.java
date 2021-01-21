package com.facilio.bmsconsoleV3.commands;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.util.ControlScheduleUtil;

import con.facilio.control.ControlGroupContext;

public class GetControlGroupCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupContext controlGroupContext = (ControlGroupContext) ControlScheduleUtil.getObjectFromRecordMap(context, ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
		
		controlGroupContext = ControlScheduleUtil.getControlGroup(controlGroupContext.getId());
		
		 Map<String, Object> recordMap = (Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP);
		 
		 recordMap.put(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME, Collections.singletonList(controlGroupContext));
		
		return false;
	}

}
