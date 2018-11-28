package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.PMIncludeExcludeResourceContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.PreventiveMaintenanceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ExecutePMsCommand implements Command {

	private static Logger log = LogManager.getLogger(ExecutePMsCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> pmIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		List<PMIncludeExcludeResourceContext> pmIncludeExcludeResourceContexts = (List<PMIncludeExcludeResourceContext>) context.get(FacilioConstants.ContextNames.PM_INCLUDE_EXCLUDE_LIST);
		List<PreventiveMaintenance> pms = PreventiveMaintenanceAPI.getActivePMs(pmIds, null);
		if(pms != null && !pms.isEmpty()) {
			Map<Long, WorkOrderContext> pmToWo = new HashMap<>();
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
			List<Long> woIds = new ArrayList<>();
			WorkOrderContext wo = null;
			for(PreventiveMaintenance pm : pms) {
				if(pmIncludeExcludeResourceContexts != null && !pmIncludeExcludeResourceContexts.isEmpty()) {
					pm.setPmIncludeExcludeResourceContexts(pmIncludeExcludeResourceContexts);
				}
					
				wo = bean.addWorkOrderFromPM(pm);
				
				if(wo != null) {
					woIds.add(wo.getId());
					pmToWo.put(pm.getId(), wo);
				}
			}
			context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST, pms);
			context.put(FacilioConstants.ContextNames.WORK_ORDER_LIST, woIds);
			context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
			context.put(FacilioConstants.ContextNames.PM_TO_WO, pmToWo);
		}
		return false;
	}

}
