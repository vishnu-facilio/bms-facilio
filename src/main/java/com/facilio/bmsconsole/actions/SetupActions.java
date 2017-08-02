package com.facilio.bmsconsole.actions;

import com.opensymphony.xwork2.ActionSupport;

public class SetupActions extends ActionSupport {
	
	static
	{
		System.out.println("###############Setup Action Class Loaded#############");
	}
	
	public String execute() throws Exception {
		
		return SUCCESS;
	}
	
	public String mySettings() throws Exception {
		
		return SUCCESS;
	}
	
	public String orgSettings() throws Exception {
		
		return SUCCESS;
	}
	
	public String showNotificationSettings() throws Exception {
		
		return SUCCESS;
	}
	public String showEmailSettings() throws Exception {
		
		return SUCCESS;
	}
	
}
