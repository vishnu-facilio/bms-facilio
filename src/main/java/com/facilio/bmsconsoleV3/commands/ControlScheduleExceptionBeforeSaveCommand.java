package com.facilio.bmsconsoleV3.commands;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlScheduleExceptionContext;
import com.facilio.control.util.ControlScheduleUtil;

public class ControlScheduleExceptionBeforeSaveCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		String moduleName = (String) context.getOrDefault(FacilioConstants.ContextNames.MODULE_NAME,ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME);
		
		ControlScheduleExceptionContext exception = (ControlScheduleExceptionContext) ControlScheduleUtil.getObjectFromRecordMap(context, moduleName);
		
		exception.build();
		// TODO Auto-generated method stub
		return false;
	}

}
