package com.facilio.bmsconsoleV3.commands;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.control.ControlScheduleExceptionContext;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;

public class ControlScheduleExceptionAfterSaveCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ControlScheduleExceptionContext exception = (ControlScheduleExceptionContext) ControlScheduleUtil.getObjectFromRecordMap(context, ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME);
		
		if(exception.getSchedule() != null) {
			GenericInsertRecordBuilder insert1 = new GenericInsertRecordBuilder()
        			.table(ModuleFactory.getControlScheduleVsExceptionModule().getTableName())
        			.fields(FieldFactory.getControlScheduleVsExceptionFields())
        			;
			
			Map<String,Object> exceptionMap = new HashMap<String, Object>();
    		
    		exceptionMap.put("scheduleId", exception.getSchedule().getId());
    		exceptionMap.put("exceptionId", exception.getId());
    		exceptionMap.put("orgId", exception.getOrgId());
    		
    		insert1.addRecord(exceptionMap);
        	
        	insert1.save();
		}
		// TODO Auto-generated method stub
		return false;
	}

}
