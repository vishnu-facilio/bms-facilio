package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.data.ServicePortalInfo;
import com.facilio.bmsconsole.context.SetupLayout;

public class ServicePortalSetupAction extends SetupActions<ServicePortalInfo> {

	public String servicePortal() throws Exception {

		System.out.println("Serviceportal called");
		SetupLayout<ServicePortalInfo> portalInfo =SetupLayout.getservicePortal();
		ServicePortalInfo spinfo = new ServicePortalInfo();
		portalInfo.setData(spinfo);
		setSetup(portalInfo);
		System.out.println(getSetup().getData().toString());
		return SUCCESS;
	}
	
	public String updateServicePortal() throws Exception {

		SetupLayout<ServicePortalInfo> set = getSetup();
		//TODO get the data from and save..
		return SUCCESS;
	}
		
	
}
