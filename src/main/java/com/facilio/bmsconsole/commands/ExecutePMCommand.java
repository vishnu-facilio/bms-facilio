package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ExecutePMCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		Long pmId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if(pmId != null && pmId != -1) {
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
			PreventiveMaintenance pm = PreventiveMaintenanceAPI.getActivePM(pmId);
			Boolean stopExecution = (Boolean) context.get(FacilioConstants.ContextNames.STOP_PM_EXECUTION);
			if (stopExecution == null || !stopExecution) {
				WorkOrderContext wo = null;
				try {
					Long templateId = (Long) context.get(FacilioConstants.ContextNames.TEMPLATE_ID);
					if (templateId == null) {
						wo = bean.addWorkOrderFromPM(pm);
					}
					else {
						wo = bean.addWorkOrderFromPM(pm, templateId);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
					CommonCommandUtil.emailException("PM Execution failed", e);
				}
				context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
			}
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE, pm);
		}
		return false;
	}
}
