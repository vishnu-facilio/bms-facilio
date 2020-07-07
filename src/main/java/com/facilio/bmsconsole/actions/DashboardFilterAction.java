package com.facilio.bmsconsole.actions;

import java.util.List;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;

public class DashboardFilterAction extends FacilioAction{

	
	private static final long serialVersionUID = 1L;
	
	private Long dashboardId;
	public String getDashboardModules() throws Exception{
		FacilioChain chain=ReadOnlyChainFactory.getFetchModulesInDashboardChain();
		FacilioContext context=chain.getContext();
		context.put(FacilioConstants.ContextNames.DASHBOARD_ID,getDashboardId());
		chain.execute();
		setResult(FacilioConstants.ContextNames.MODULE_LIST, (List<String>)context.get(FacilioConstants.ContextNames.MODULE_LIST));
		return SUCCESS;
	}
	public Long getDashboardId() {
		return dashboardId;
	}
	public void setDashboardId(Long dashboardId) {
		this.dashboardId = dashboardId;
	}
	
}