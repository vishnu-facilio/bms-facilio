package com.facilio.customerportal.actions;

import com.opensymphony.xwork2.ActionSupport;

public class Authentication extends ActionSupport {

	
	public String login()
	{
		// do check if the user have configured saml auth..
		
	// if yes, forward to sso login page
		
		// or 
		
		return SUCCESS;
	}
	
	public String samllogin()
	{
		return SUCCESS;
	}
}
