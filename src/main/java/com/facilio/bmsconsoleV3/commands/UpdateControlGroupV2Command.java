package com.facilio.bmsconsoleV3.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

import con.facilio.control.ControlGroupContext;
import con.facilio.control.ControlGroupRoutineContext;

public class UpdateControlGroupV2Command extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupContext controlGroupContext = (ControlGroupContext) context.get(ControlScheduleUtil.CONTROL_GROUP_CONTEXT);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		if(controlGroupContext.getId() > 0) {
			 List<FacilioField> controlGroupFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
				
			UpdateRecordBuilder<ControlGroupContext> update = new UpdateRecordBuilder<ControlGroupContext>()
					.moduleName(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME)
					.fields(controlGroupFields)
					.andCondition(CriteriaAPI.getIdCondition(controlGroupContext.getId(), modBean.getModule(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME)));
			
			update.update(controlGroupContext);
		}
		return false;
	}

}