package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.context.SetupLayout;
import com.opensymphony.xwork2.ActionSupport;

public class SetupActions extends ActionSupport {
	
	static
	{
		System.out.println("###############Setup Action Class Loaded#############");
	}
	
	public String execute() throws Exception {
		
		return SUCCESS;
	}
	
	private SetupLayout setup;
	public SetupLayout getSetup() {
		return this.setup;
	}
	
	public void setSetup(SetupLayout setup) {
		this.setup = setup;
	}
	
	public String mySettings() throws Exception {
		
		setSetup(SetupLayout.getPersonalSettingsLayout());
		return SUCCESS;
	}

	public String importData() throws Exception {
		
		setSetup(SetupLayout.getImportLayout());
		return SUCCESS;
	}
	public String orgSettings() throws Exception {
		
		setSetup(SetupLayout.getCompanySettingsLayout());
		return SUCCESS;
	}
	
	public String showNotificationSettings() throws Exception {
		
		return SUCCESS;
	}
	public String showEmailSettings() throws Exception {
		
		return SUCCESS;
	}
	
}
