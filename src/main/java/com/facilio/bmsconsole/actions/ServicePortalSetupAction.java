package com.facilio.bmsconsole.actions;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.data.ServicePortalInfo;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ModelDriven;

public class ServicePortalSetupAction extends SetupActions<ServicePortalInfo> implements ModelDriven{

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
		
		public String changeSignUp() throws Exception {
			System.out.println(">>>>>>>>> signup_allowed : "+test);
			FacilioContext context = new FacilioContext();
			Map<String,Object> portalInfoMap = new HashMap<>();
			portalInfoMap.put("signup_allowed",signupAllowed);
			context.put(FacilioConstants.ContextNames.MODULE_NAME, "serviceportal");
			context.put(FacilioConstants.ContextNames.PORTALINFO, portalInfoMap);
			Chain updateUserSignUpPrivilage = FacilioChainFactory.updatePortalInfoChain();
			updateUserSignUpPrivilage.execute(context);
			
			return SUCCESS;
		}
		
		private int test = -1;
		
		
	public int getTest() {
			return test;
		}

		public void setTest(int test) {
			this.test = test;
		}
	Boolean signupAllowed;
	
	public Boolean getSignupAllowed() {
		return signupAllowed;
	}

	public void setSignupAllowed(Boolean signupAllowed) {
		this.signupAllowed = signupAllowed;
	}
	
}
