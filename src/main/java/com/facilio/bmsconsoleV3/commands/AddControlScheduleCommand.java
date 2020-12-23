package com.facilio.bmsconsoleV3.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;

import con.facilio.control.ControlScheduleContext;
import con.facilio.control.ControlScheduleExceptionContext;
import con.facilio.control.ControlScheduleWrapper;

public class AddControlScheduleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ControlScheduleWrapper controlScheduleWrapper = (ControlScheduleWrapper) context.get(ControlScheduleUtil.CONTROL_SCHEDULE_WRAPPER);
		
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        
        List<FacilioField> controlScheduleFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME);
        
        InsertRecordBuilder<ControlScheduleContext> insert = new InsertRecordBuilder<ControlScheduleContext>()
    			.addRecord(controlScheduleWrapper.getControlScheduleContext())
    			.fields(controlScheduleFields)
    			.moduleName(ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME)
    			;
        
        insert.save();
        
        if(controlScheduleWrapper.getExceptions() != null) {
        	
        	List<FacilioField> controlScheduleExceptionFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME);
        	
        	for(ControlScheduleExceptionContext exception : controlScheduleWrapper.getExceptions()) {
        		exception.setControlSchedule(controlScheduleWrapper.getControlScheduleContext());
        	}
        	
        	InsertRecordBuilder<ControlScheduleExceptionContext> insert1 = new InsertRecordBuilder<ControlScheduleExceptionContext>()
        			.addRecords(controlScheduleWrapper.getExceptions())
        			.fields(controlScheduleExceptionFields)
        			.moduleName(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME)
        			;
            
            insert1.save();
        }
        
		return false;
	}

}
