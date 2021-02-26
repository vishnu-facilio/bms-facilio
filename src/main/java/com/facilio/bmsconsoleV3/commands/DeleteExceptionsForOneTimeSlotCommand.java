package com.facilio.bmsconsoleV3.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlScheduleExceptionContext;
import com.facilio.control.ControlScheduleExceptionTenantContext;
import com.facilio.control.ControlScheduleSlot;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;

public class DeleteExceptionsForOneTimeSlotCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String action = (String) context.get(FacilioConstants.ContextNames.ACTION);
		
		if(action != null && action.equals("delete")) {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			List<ControlScheduleSlot> slots = (List<ControlScheduleSlot>) (((Map<String,Object>)context.get(FacilioConstants.ContextNames.RECORD_MAP)).get(context.get(FacilioConstants.ContextNames.MODULE_NAME)));
			
			ControlScheduleSlot slotToBeDeleted = slots.get(0);
			
			ControlScheduleExceptionContext exception = slotToBeDeleted.getException();
			
			exception = (ControlScheduleExceptionContext) ControlScheduleUtil.fetchRecord(ControlScheduleExceptionTenantContext.class, ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME, null, CriteriaAPI.getIdCondition(exception.getId(), modBean.getModule(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME))).get(0);
			
			if(exception.getTypeEnum() == ControlScheduleExceptionContext.Type.ONETIME) {
				
				List<FacilioField> slotFields = modBean.getAllFields(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_MODULE_NAME);
				
				DeleteRecordBuilder<ControlScheduleExceptionTenantContext> delete = new  DeleteRecordBuilder<ControlScheduleExceptionTenantContext>()
						.moduleName(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME)
						.andCondition(CriteriaAPI.getIdCondition(exception.getId(), modBean.getModule(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME)))
						;
				
				int res = delete.markAsDelete();
			}
		}
		
		// TODO Auto-generated method stub
		return false;
	}

}
