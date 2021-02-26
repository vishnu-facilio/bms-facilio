package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlGroupTenentContext;
import com.facilio.control.ControlScheduleContext;
import com.facilio.control.ControlScheduleExceptionContext;
import com.facilio.control.ControlScheduleExceptionTenantContext;
import com.facilio.control.ControlScheduleTenantContext;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.DeleteRecordBuilder;
import com.facilio.modules.UpdateRecordBuilder;

public class ResetControlScheduleAndExceptionsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupTenentContext childGroup = (ControlGroupTenentContext) context.get(ControlScheduleUtil.CONTROL_GROUP_CONTEXT);
		ControlGroupContext parentGroup = (ControlGroupContext) context.get(ControlScheduleUtil.CONTROL_GROUP_CONTEXT_OLD);
		TenantContext tenant = (TenantContext) context.get(FacilioConstants.ContextNames.TENANT);
		
		ControlScheduleTenantContext scheduleTenent = ControlScheduleUtil.controlScheduleToControlScheduleTenantShared(parentGroup.getControlSchedule(), tenant, parentGroup);
		
		deleteOldControlSchedule(childGroup);
		
		childGroup.setControlSchedule(scheduleTenent);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		UpdateRecordBuilder<ControlGroupTenentContext> update = new UpdateRecordBuilder<ControlGroupTenentContext>()
				.moduleName(ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME)
				.fields(modBean.getAllFields(ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME))
				.andCondition(CriteriaAPI.getIdCondition(childGroup.getId(), modBean.getModule(ControlScheduleUtil.CONTROL_GROUP_TENANT_SHARING_MODULE_NAME)))
				;
		
		update.update(childGroup);
		
		return false;
	}

	private void deleteOldControlSchedule(ControlGroupContext parentGroup) throws Exception {
		// TODO Auto-generated method stub
		
		ControlScheduleContext schedule = parentGroup.getControlSchedule();
		
		ControlScheduleUtil.deleteControlScheduleRelated(schedule);
	}

}
