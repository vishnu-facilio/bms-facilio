package com.facilio.bmsconsoleV3.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.control.util.ControlScheduleUtil;

import con.facilio.control.ControlGroupContext;

public class UpdateControlGroupV2Command extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupContext controlGroupContext = (ControlGroupContext) ControlScheduleUtil.getObjectFormRecordMap(context, ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
		
		context.put(ControlScheduleUtil.CONTROL_GROUP_CONTEXT_OLD, ControlScheduleUtil.getControlGroup(controlGroupContext.getId()));
		
//		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//		
//		if(controlGroupContext.getId() > 0) {
//			 List<FacilioField> controlGroupFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME);
//				
//			UpdateRecordBuilder<ControlGroupContext> update = new UpdateRecordBuilder<ControlGroupContext>()
//					.moduleName(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME)
//					.fields(controlGroupFields)
//					.andCondition(CriteriaAPI.getIdCondition(controlGroupContext.getId(), modBean.getModule(ControlScheduleUtil.CONTROL_GROUP_MODULE_NAME)));
//			
//			update.update(controlGroupContext);
//		}
		return false;
	}

}