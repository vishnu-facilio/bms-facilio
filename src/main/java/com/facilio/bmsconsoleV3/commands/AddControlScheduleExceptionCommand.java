package com.facilio.bmsconsoleV3.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;

import con.facilio.control.ControlGroupContext;
import con.facilio.control.ControlScheduleExceptionContext;

public class AddControlScheduleExceptionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ControlScheduleExceptionContext exception = (ControlScheduleExceptionContext) context.get(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_CONTEXT);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> controlExceptionFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME);
		
		InsertRecordBuilder<ControlScheduleExceptionContext> insert = new InsertRecordBuilder<ControlScheduleExceptionContext>()
    			.addRecord(exception)
    			.fields(controlExceptionFields)
    			.moduleName(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME)
    			;
        
        insert.save();
        
		return false;
	}

}
