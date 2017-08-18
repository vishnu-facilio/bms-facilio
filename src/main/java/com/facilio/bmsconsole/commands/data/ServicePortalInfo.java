package com.facilio.bmsconsole.commands.data;

import java.io.File;
import java.util.HashMap;

public class ServicePortalInfo {

	public boolean isSignupAllowed() {
		return signupAllowed;
	}
	public void setSignupAllowed(boolean signupAllowed) {
		this.signupAllowed = signupAllowed;
	}
	public boolean isGmailLoginAllowed() {
		return gmailLoginAllowed;
	}
	public void setGmailLoginAllowed(boolean gmailLoginAllowed) {
		this.gmailLoginAllowed = gmailLoginAllowed;
	}
	public boolean isTicketAlloedForPublic() {
		return ticketAlloedForPublic;
	}
	public void setTicketAlloedForPublic(boolean ticketAlloedForPublic) {
		this.ticketAlloedForPublic = ticketAlloedForPublic;
	}
	public boolean isAnyDomain() {
		return anyDomain;
	}
	public void setAnyDomain(boolean anyDomain) {
		this.anyDomain = anyDomain;
	}
	boolean signupAllowed=true;
	boolean gmailLoginAllowed=true;
	boolean ticketAlloedForPublic=true;
	boolean anyDomain;
	public boolean isSamlEnabled() {
		return samlEnabled;
	}
	public void setSamlEnabled(boolean samlEnabled) {
		this.samlEnabled = samlEnabled;
	}
	boolean samlEnabled;
	
	private HashMap samlinfo = null;
	public HashMap getSamlInfo()
	{
		if(!isSamlEnabled())
		{
		return samlinfo;
		}
		return null;
	}
	public void setSamInfo(String key, String value)
	{
		samlinfo.put(key, value);
	}
	public void setSamInfo(String key, File value)
	{
		samlinfo.put(key, value);
	}
	
	public void setSamInfo(HashMap samlinfo)
	{
		// Keys
		/*login_url
		logout_url
		forgot_pass_url
		certificate path
		*/
		if(samlinfo.size()==3)
		{
			this.samlinfo= samlinfo;
			
		}
	}
}
