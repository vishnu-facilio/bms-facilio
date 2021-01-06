package com.facilio.bmsconsoleV3.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

import con.facilio.control.ControlGroupRoutineContext;

public class UpdateControlGroupRoutineCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupRoutineContext routine = (ControlGroupRoutineContext) context.get(ControlScheduleUtil.CONTROL_GROUP_ROUTINE_CONTEXT);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		if(routine.getId() > 0) {
			 List<FacilioField> controlGroupRoutineFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_ROUTINE_MODULE_NAME);
				
				UpdateRecordBuilder<ControlGroupRoutineContext> update = new UpdateRecordBuilder<ControlGroupRoutineContext>()
						.moduleName(ControlScheduleUtil.CONTROL_GROUP_ROUTINE_MODULE_NAME)
						.fields(controlGroupRoutineFields)
						.andCondition(CriteriaAPI.getIdCondition(routine.getId(), modBean.getModule(ControlScheduleUtil.CONTROL_GROUP_ROUTINE_MODULE_NAME)));
				
				update.update(routine);
		}
		return false;
	}

}
