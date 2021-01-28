package com.facilio.bmsconsoleV3.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.control.ControlScheduleExceptionContext;
import com.facilio.control.util.ControlScheduleUtil;

public class ControlScheduleExceptionBeforeSaveCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ControlScheduleExceptionContext exception = (ControlScheduleExceptionContext) ControlScheduleUtil.getObjectFromRecordMap(context, ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME);
		
		exception.build();
		// TODO Auto-generated method stub
		return false;
	}

}
