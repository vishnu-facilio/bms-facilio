package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;

public class getUpdateWithBuildingsCommand implements Command {
	
	public boolean execute(Context context) throws Exception {
		Long buildingId= (Long) context.get(FacilioConstants.ContextNames.BUILDING_ID);
		
		DashboardContext db = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
		
		
		DashboardContext dashboard = null; 
		
		if(buildingId != null) {
			
			dashboard = DashboardUtil.getDashboardForBaseSpace(buildingId, db.getModuleId());
			
			if(dashboard == null) { 
				db.setBaseSpaceId(buildingId);
				
				db.setId(-1l);
				db.setCreatedByUserId(AccountUtil.getCurrentUser().getId());
				db.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
				context.put(FacilioConstants.ContextNames.DASHBOARD, db);
				
				Chain addDashboardChain = TransactionChainFactory.getAddDashboardChain();
				
				addDashboardChain.execute(context);

				for(DashboardWidgetContext widget : db.getDashboardWidgets()) {
					widget.setId(-1);
				}
				context.put(FacilioConstants.ContextNames.DASHBOARD, db);
			}
			
      }
		return false;
	}
}
