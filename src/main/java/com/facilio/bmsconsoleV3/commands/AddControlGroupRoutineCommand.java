package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.fields.FacilioField;

import con.facilio.control.ControlGroupAssetCategory;
import con.facilio.control.ControlGroupContext;
import con.facilio.control.ControlGroupFieldContext;
import con.facilio.control.ControlGroupRoutineContext;
import con.facilio.control.ControlGroupRoutineSectionContext;
import con.facilio.control.ControlGroupSection;

public class AddControlGroupRoutineCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupRoutineContext routine = (ControlGroupRoutineContext) context.get(ControlScheduleUtil.CONTROL_GROUP_ROUTINE_CONTEXT);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if(routine != null) {
			
			List<FacilioField> controlGroupRoutineFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_ROUTINE_MODULE_NAME);
			
			InsertRecordBuilder<ControlGroupRoutineContext> insert = new InsertRecordBuilder<ControlGroupRoutineContext>()
	    			.addRecord(routine)
	    			.fields(controlGroupRoutineFields)
	    			.moduleName(ControlScheduleUtil.CONTROL_GROUP_ROUTINE_MODULE_NAME)
	    			;
	        
	        insert.save();
		}
		return false;
	}

}
