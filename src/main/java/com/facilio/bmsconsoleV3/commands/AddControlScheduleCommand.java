package com.facilio.bmsconsoleV3.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.mysql.fabric.xmlrpc.base.Array;

import con.facilio.control.ControlScheduleContext;
import con.facilio.control.ControlScheduleExceptionContext;

public class AddControlScheduleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		ControlScheduleContext schedule = (ControlScheduleContext) context.get(ControlScheduleUtil.CONTROL_SCHEDULE_CONTEXT);
		
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        
        if(schedule.getBusinessHour() == null) {
        	
        	FacilioChain addBusinessHourChain = TransactionChainFactory.addBusinessHourChain();
        	
        	FacilioContext newContext = addBusinessHourChain.getContext();
        	newContext.put(FacilioConstants.ContextNames.BUSINESS_HOUR, schedule.getBusinessHoursContext());
        	newContext.put(FacilioConstants.ContextNames.RESOURCE_ID, -1l);
    		
    		addBusinessHourChain.execute();
    		
    		long businessHourId = (long) newContext.get(FacilioConstants.ContextNames.ID);
    		
    		schedule.setBusinessHour(businessHourId);
        }
        
        List<FacilioField> controlScheduleFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME);
        
        InsertRecordBuilder<ControlScheduleContext> insert = new InsertRecordBuilder<ControlScheduleContext>()
    			.addRecord(schedule)
    			.fields(controlScheduleFields)
    			.moduleName(ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME)
    			;
        
        insert.save();
        
		return false;
	}

}
