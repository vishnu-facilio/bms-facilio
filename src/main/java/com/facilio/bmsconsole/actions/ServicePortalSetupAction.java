package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.data.ServicePortalInfo;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.bmsconsole.util.OrgApi;

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
	System.out.println(set.getData());
//
//		System.out.println(set.getData().getClass().getName());
//		//TODO get the data from and save..
//		System.out.println("inside the update service portal"+set.getData().getAnyDomain());
//		
		OrgApi.updatePortalInfo(getSetup(), null);
		
		return SUCCESS;
	}
		@Override
	public SetupLayout<ServicePortalInfo> getSetup() {
		if(super.getSetup()==null)
		{
			setSetup(new SetupLayout<ServicePortalInfo>(ServicePortalInfo.class));
		}
		return super.getSetup();
	}
		
		
	
}
