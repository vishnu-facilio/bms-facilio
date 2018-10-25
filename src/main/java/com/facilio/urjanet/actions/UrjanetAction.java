package com.facilio.urjanet.actions;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioContext;
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
		   Chain urjanetloginChain = UrjanetConstants.UrjanetLoginChain();
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
