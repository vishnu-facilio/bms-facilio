package com.facilio.bmsconsoleV3.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.control.ControlGroupContext;
import com.facilio.control.ControlScheduleContext;
import com.facilio.control.ControlScheduleExceptionContext;
import com.facilio.control.ControlScheduleExceptionTenantContext;
import com.facilio.control.ControlScheduleTenantContext;
import com.facilio.control.util.ControlScheduleUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;

public class ControlSchedulePublishCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		ControlGroupContext group = (ControlGroupContext) context.get(ControlScheduleUtil.CONTROL_GROUP_CONTEXT);
		TenantContext tenant = (TenantContext) context.get(FacilioConstants.ContextNames.TENANT);
		
		ControlScheduleContext schedule = group.getControlSchedule();
		
		ControlScheduleTenantContext scheduleTenent = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(schedule), ControlScheduleTenantContext.class);
		
		scheduleTenent.setTenant(tenant);
		scheduleTenent.setParentGroup(group);
		scheduleTenent.setParentSchedule(schedule);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		InsertRecordBuilder<ControlScheduleTenantContext> insert = new InsertRecordBuilder<ControlScheduleTenantContext>()
				.moduleName(ControlScheduleUtil.CONTROL_SCHEDULE_TENANT_SHARING_MODULE_NAME)
				.fields(modBean.getAllFields(ControlScheduleUtil.CONTROL_SCHEDULE_TENANT_SHARING_MODULE_NAME))
				.addRecord(scheduleTenent);
		
		insert.save();
		
		InsertRecordBuilder<ControlScheduleExceptionTenantContext> insert1 = new InsertRecordBuilder<ControlScheduleExceptionTenantContext>()
				.moduleName(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME)
				.fields(modBean.getAllFields(ControlScheduleUtil.CONTROL_SCHEDULE_EXCEPTION_TENANT_SHARING_MODULE_NAME));
		
		List<ControlScheduleExceptionTenantContext> exceptionTenantList = new ArrayList<ControlScheduleExceptionTenantContext>();
		for(ControlScheduleExceptionContext exception : schedule.getExceptions()) {
			
			ControlScheduleExceptionTenantContext exceptionTenant = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(exception), ControlScheduleExceptionTenantContext.class);
			
			exceptionTenant.setParentException(exception);
			exceptionTenant.setTenant(tenant);
			exceptionTenant.setParentGroup(group);
			
			insert1.addRecord(exceptionTenant);
			exceptionTenantList.add(exceptionTenant);
		}
		
		insert1.save();
		
		scheduleTenent.setExceptions(null);
		for(ControlScheduleExceptionTenantContext exceptionTenant :exceptionTenantList) {
			scheduleTenent.addException(exceptionTenant);
		}
		
		FacilioChain chain = TransactionChainFactoryV3.getDeleteAndAddControlScheduleExceptionChain();
		
		FacilioContext newcontext = chain.getContext();
		
		Map<String, List<ControlScheduleContext>> map = Collections.singletonMap(ControlScheduleUtil.CONTROL_SCHEDULE_MODULE_NAME, Collections.singletonList(scheduleTenent));
		
		newcontext.put(FacilioConstants.ContextNames.RECORD_MAP, map);
		
		chain.execute();
		
		scheduleTenent.setExceptions(null);					
		scheduleTenent.setParentGroup(null);
		
		group.setControlSchedule(scheduleTenent);
		
		return false;
	}

}
