package com.facilio.bmsconsoleV3.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;

import con.facilio.control.ControlGroupRoutineContext;

public class DeleteControlGroupRoutineSectionAndFieldCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupRoutineContext routine = (ControlGroupRoutineContext) ControlScheduleUtil.getObjectFormRecordMap(context, ControlScheduleUtil.CONTROL_GROUP_ROUTINE_MODULE_NAME);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		if(routine.getId() > 0) {
			
			FacilioModule routineSection = modBean.getModule(ControlScheduleUtil.CONTROL_GROUP_ROUTINE_SECTION_MODULE_NAME);
			
			GenericDeleteRecordBuilder delete = new GenericDeleteRecordBuilder()
					.table(routineSection.getTableName())
					.andCondition(CriteriaAPI.getCondition("CONTROL_ROUTINE", "routine", ""+routine.getId(), NumberOperators.EQUALS));
			
			delete.delete();
		}
		
		return false;
	}

}
