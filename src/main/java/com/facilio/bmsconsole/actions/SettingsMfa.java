package com.facilio.bmsconsole.actions;

import java.util.Map;

import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.iam.accounts.util.IAMOrgUtil;


public class SettingsMfa extends FacilioAction {
	
	private static final Logger LOGGER = Logger.getLogger(SettingsMfa.class.getName());
	
	private boolean totpStatus;
	
	public void setTotp(boolean totpStatus) {
		
		this.totpStatus = totpStatus;
	}
	
	public boolean getTotp() {
		
		return totpStatus;
	}
	
	public String getMfa() throws Exception{
		
		Map<String,Boolean> orgMfaSettings = IAMOrgUtil.getMfaSettings(AccountUtil.getCurrentOrg().getOrgId());
		setResult("mfaSettings",orgMfaSettings);
		return "success" ;	
	}
	
	public String totpEnabled() throws Exception{
		
		IAMOrgUtil.enableTotp(AccountUtil.getCurrentOrg().getOrgId());
		return "success";
	}
    
	public String totpDisabled() throws Exception{
	
		IAMOrgUtil.disableTotp(AccountUtil.getCurrentOrg().getOrgId());
		return "success";
	}
}