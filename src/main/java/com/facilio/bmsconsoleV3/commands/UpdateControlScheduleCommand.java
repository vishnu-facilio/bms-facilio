package com.facilio.bmsconsoleV3.commands;

import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.UpdateRecordBuilder;
import com.facilio.modules.fields.FacilioField;

import con.facilio.control.ControlScheduleContext;

public class UpdateControlScheduleCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlScheduleContext schedule = (ControlScheduleContext) context.get(ControlScheduleUtil.CONTROL_SCHEDULE_CONTEXT);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if(schedule.getId() > 0) {
			
			 List<FacilioField> controlScheduleFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME);
			
			UpdateRecordBuilder<ControlScheduleContext> update = new UpdateRecordBuilder<ControlScheduleContext>()
					.moduleName(ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME)
					.fields(controlScheduleFields)
					.andCondition(CriteriaAPI.getIdCondition(schedule.getId(), modBean.getModule(ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME)));
			
			update.update(schedule);
			
			FacilioChain updateBusinessHoursChain = TransactionChainFactory.updateBusinessHoursChain();
			FacilioContext newContext = updateBusinessHoursChain.getContext();
			newContext.put(FacilioConstants.ContextNames.BUSINESS_HOUR, schedule.getBusinessHoursContext());
			updateBusinessHoursChain.execute();
			
		}
		return false;
	}

}
