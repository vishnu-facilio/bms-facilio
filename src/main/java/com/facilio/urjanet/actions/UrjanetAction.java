package com.facilio.urjanet.actions;

import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.urjanet.contants.UrjanetConstants;
import com.facilio.urjanet.context.UtilityProviderCredentials;
import com.opensymphony.xwork2.ActionSupport;

public class UrjanetAction extends ActionSupport{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String UserAdd() throws Exception
	{
		   FacilioContext context = new FacilioContext();    
		   context.put(UrjanetConstants.ContextNames.CREDENTIAL, getUtilityProviderCredentials());			
		   FacilioChain urjanetloginChain = UrjanetConstants.UrjanetLoginChain();
		   urjanetloginChain.execute(context);
		   return SUCCESS;
	}
	
	UtilityProviderCredentials utilityProviderCredentials;
	
	public void setUtilityProviderCredentials(UtilityProviderCredentials utilityProviderCredentials)
	{
		this.utilityProviderCredentials = utilityProviderCredentials;
	}
	
	public UtilityProviderCredentials getUtilityProviderCredentials()
	{
		return this.utilityProviderCredentials;
	}
	
}
