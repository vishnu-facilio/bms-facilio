package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.data.ServicePortalInfo;
import com.facilio.bmsconsole.context.SetupLayout;
import com.opensymphony.xwork2.ModelDriven;


public class ServicePortalSetupAction extends SetupActions<ServicePortalInfo> implements ModelDriven{
//public class ServicePortalSetupAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String servicePortal() throws Exception {

		System.out.println("Serviceportal called");
		SetupLayout<ServicePortalInfo> portalInfo =SetupLayout.getservicePortal();
		data = ServicePortalInfo.getServicePortalInfo();
		portalInfo.setData(data);
		setSetup(portalInfo);
		System.out.println(data.toString());
		return SUCCESS;
	}
	
	public String updateServicePortal() throws Exception {

		//SetupLayout<ServicePortalInfo> set = getSetup();
//	System.out.println(set.getData());
//
//		System.out.println(set.getData().getClass().getName());
//		//TODO get the data from and save..
//		System.out.println("inside the update service portal"+set.getData().getAnyDomain());
//		
		System.out.println("incoming data is "+data);;
		ServicePortalInfo.updatePortalInfo(data);
		
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
		ServicePortalInfo data =  new ServicePortalInfo();
		@Override
		public Object getModel() {
			// TODO Auto-generated method stub
			return data;
		}
		
}
