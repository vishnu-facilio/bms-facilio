package com.facilio.bmsconsoleV3.commands;

import java.util.List;

import org.apache.commons.chain.Command;
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
import con.facilio.control.ControlScheduleExceptionContext;

public class UpdateControlScheduleExceptionCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlScheduleExceptionContext exception = (ControlScheduleExceptionContext) context.get(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_CONTEXT);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if(exception.getId() > 0) {
			
			 List<FacilioField> controlScheduleExceptionFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME);
			
			UpdateRecordBuilder<ControlScheduleExceptionContext> update = new UpdateRecordBuilder<ControlScheduleExceptionContext>()
					.moduleName(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME)
					.fields(controlScheduleExceptionFields)
					.andCondition(CriteriaAPI.getIdCondition(exception.getId(), modBean.getModule(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME)));
			
			update.update(exception);
			
		}
		return false;
	}

}
