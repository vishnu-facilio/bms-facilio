package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class LoadAllFieldsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		if(moduleName == null || moduleName.isEmpty()) {
			throw new IllegalArgumentException("Module Name is not set for the module");
		}
		if(context.get("activityType") != null && "alarmFromEvent".equals(context.get("activityType")))
		{
			AlarmContext alarm = (AlarmContext) context.get(FacilioConstants.ContextNames.ALARM);
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", alarm.getOrgId());
			context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(moduleName));
		}
		else
		{
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			context.put(FacilioConstants.ContextNames.EXISTING_FIELD_LIST, modBean.getAllFields(moduleName));
		}
		
		return false;
	}
}
