package com.facilio.bmsconsole.actions;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.data.ServicePortalInfo;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.bmsconsole.context.SetupLayout;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.ActionSupport;


public class PortalInfoAction extends ActionSupport 
{		
	public String getPortalInfo() throws Exception
	{
		FacilioContext context = new FacilioContext();
		Chain getPortalInfoChain = FacilioChainFactory.getPortalInfoChain();
		getPortalInfoChain.execute(context);
		setProtalInfo((PortalInfoContext)context.get(FacilioConstants.ContextNames.PORTALINFO));
		return SUCCESS;
	}
	
	public String updateServicePortal() throws Exception 
	{
		System.out.println(">>>>>>>>> portalInfoMap : "+portalInfoMap);
		FacilioContext context = new FacilioContext();
		//portalInfoMap.put("signup_allowed",signup_allowed);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, "serviceportal");
		context.put(FacilioConstants.ContextNames.PORTALINFO, portalInfoMap);
		Chain updatePortalInfoChain = FacilioChainFactory.updatePortalInfoChain();
		updatePortalInfoChain.execute(context);
		
		return SUCCESS;
	}
	
	private PortalInfoContext protalInfo;
	
	public PortalInfoContext getProtalInfo() {
		return protalInfo;
	}

	public void setProtalInfo(PortalInfoContext protalInfo) {
		this.protalInfo = protalInfo;
	}

	Map<String, Object> portalInfoMap;

	public void setPortalInfoMap(Map<String, Object> portalInfoMap) {
		this.portalInfoMap = portalInfoMap;
	}
	
	public Map<String, Object> getProtalInfoMap()
	{
		return this.portalInfoMap;
	}

	boolean signup_allowed;

	public boolean isSignup_allowed() {
		return signup_allowed;
	}

	public void setSignup_allowed(boolean signup_allowed) {
		this.signup_allowed = signup_allowed;
	}
	
}
