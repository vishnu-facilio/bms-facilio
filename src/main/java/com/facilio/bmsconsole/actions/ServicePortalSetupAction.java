package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.data.ServicePortalInfo;
import com.facilio.bmsconsole.context.SetupLayout;

public class ServicePortalSetupAction extends SetupActions<ServicePortalInfo> {

	public String servicePortal() throws Exception {

		SetupLayout<ServicePortalInfo> portalInfo =SetupLayout.getservicePortal();
		ServicePortalInfo spinfo = new ServicePortalInfo();
		portalInfo.setData(spinfo);
		setSetup(portalInfo);
		return SUCCESS;
	}
	
	public String updateServicePortal() throws Exception {

		SetupLayout<ServicePortalInfo> set = getSetup();
		//TODO get the data from and save..
		return SUCCESS;
	}
		
	
}
