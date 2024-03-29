package com.facilio.bmsconsoleV3.commands;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlScheduleContext;
import com.facilio.control.ControlScheduleExceptionContext;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;

public class DeleteAndAddControlScheduleExceptionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlScheduleContext schedule = (ControlScheduleContext) ControlScheduleUtil.getObjectFromRecordMap(context, ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME);
		
		GenericDeleteRecordBuilder delete = new GenericDeleteRecordBuilder()
				.table(ModuleFactory.getControlScheduleVsExceptionModule().getTableName())
				.andCustomWhere("SCHEDULE_ID = ? ", schedule.getId());
		
		delete.delete();
		
		if(schedule.getExceptions() != null) {
        	
        	GenericInsertRecordBuilder insert1 = new GenericInsertRecordBuilder()
        			.table(ModuleFactory.getControlScheduleVsExceptionModule().getTableName())
        			.fields(FieldFactory.getControlScheduleVsExceptionFields())
        			;
        	
        	for(ControlScheduleExceptionContext exception : schedule.getExceptions()) {
        		Map<String,Object> exceptionMap = new HashMap<String, Object>();
        		
        		exceptionMap.put("scheduleId", schedule.getId());
        		exceptionMap.put("exceptionId", exception.getId());
        		exceptionMap.put("orgId", schedule.getOrgId());
        		
        		insert1.addRecord(exceptionMap);
        	}
        	
        	insert1.save();
        }
		return false;
	}

}
