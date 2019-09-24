package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.DashboardContext;
import com.facilio.bmsconsole.context.DashboardWidgetContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

public class DuplicateDashboardForBuildingCommand extends FacilioCommand {
	
	public boolean executeCommand(Context context) throws Exception {
		Long buildingId= (Long) context.get(FacilioConstants.ContextNames.BUILDING_ID);
		
		DashboardContext db = (DashboardContext) context.get(FacilioConstants.ContextNames.DASHBOARD);
		
		
		DashboardContext dashboard = null; 
		
		if(buildingId != null) {
			
			dashboard = DashboardUtil.getDashboardForBaseSpace(buildingId, db.getModuleId());
			
			if(dashboard == null) { 
				
				context.put(FacilioConstants.ContextNames.IS_SKIP_LINKNAME_CHECK,true);
				db.setBaseSpaceId(buildingId);
				
				db.setId(-1l);
				db.setCreatedByUserId(AccountUtil.getCurrentUser().getId());
				db.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
				context.put(FacilioConstants.ContextNames.DASHBOARD, db);
				
				FacilioChain addDashboardChain = TransactionChainFactory.getAddDashboardChain();
				
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
