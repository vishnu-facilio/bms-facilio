package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.PMIncludeExcludeResourceContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExecutePMsCommand implements Command {

	private static Logger log = LogManager.getLogger(ExecutePMsCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<PMIncludeExcludeResourceContext> pmIncludeExcludeResourceContexts = (List<PMIncludeExcludeResourceContext>) context.get(FacilioConstants.ContextNames.PM_INCLUDE_EXCLUDE_LIST);
		List<PreventiveMaintenance> pms = (List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST);
		if(pms != null && !pms.isEmpty()) {
			
			Map<Long,Map<Long,WorkOrderContext>> pmResourceToWoMap = new HashMap<>();
			
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
			List<Long> woIds = new ArrayList<>();
			for(PreventiveMaintenance pm : pms) {
				if(pmIncludeExcludeResourceContexts != null && !pmIncludeExcludeResourceContexts.isEmpty()) {
					pm.setPmIncludeExcludeResourceContexts(pmIncludeExcludeResourceContexts);
				}
					
				List<WorkOrderContext> wos = bean.addWorkOrderFromPM(context, pm);

				for(WorkOrderContext wo :wos) {
					if(wo != null) {
						woIds.add(wo.getId());
						Map<Long,WorkOrderContext> map = pmResourceToWoMap.get(pm.getId()) != null ? pmResourceToWoMap.get(pm.getId()) : new HashMap<>();
						Long resourceId = wo.getResource() != null ? wo.getResource().getId() : -1l;
						map.put(resourceId, wo);
						pmResourceToWoMap.put(pm.getId(), map);
						
					}
				}
				
				if(wos != null && !wos.isEmpty()) {
					context.put(FacilioConstants.ContextNames.WORK_ORDER, wos.get(0));	// temp fix for client, need to handle it both in server and client for multiple pms
				}
			}
			context.put(FacilioConstants.ContextNames.WORK_ORDER_LIST, woIds);
			context.put(FacilioConstants.ContextNames.PM_TO_ASSET_TO_WO, pmResourceToWoMap);
		}
		return false;
	}

}
