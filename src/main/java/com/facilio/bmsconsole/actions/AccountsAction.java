package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Chain;

public class AccountsAction extends FacilioAction{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String url;
	private String permaLinkUrlToken;
	private String email;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPermaLinkUrlToken() {
		return permaLinkUrlToken;
	}

	public void setPermaLinkUrlToken(String url) {
		this.permaLinkUrlToken = url;
	}
	
   public String getPermalinkToken() throws Exception {
   
	    FacilioContext context = new FacilioContext();
	    context.put(FacilioConstants.ContextNames.PERMALINK_FOR_URL, getUrl());
	    context.put(FacilioConstants.ContextNames.USER_EMAIL, getEmail());
		Chain permaLinkTokenChain = FacilioChainFactory.getPermaLinkTokenChain();
		permaLinkTokenChain.execute(context);
		
		setPermaLinkUrlToken((String)context.get(FacilioConstants.ContextNames.PERMALINK_TOKEN_FOR_URL));
		
	   return SUCCESS;       
	}


}
