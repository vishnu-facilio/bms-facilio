package com.facilio.bmsconsole.commands.data;

import java.io.File;
import java.util.HashMap;

import com.facilio.bmsconsole.util.OrgApi;

import redis.clients.jedis.Connection;

public class ServicePortalInfo {

	public boolean getSignupAllowed() {
		return signupAllowed;
	}
	public void setSignupAllowed(boolean signupAllowed) {
		this.signupAllowed = signupAllowed;
	}
	public boolean getGmailLoginAllowed() {
		return gmailLoginAllowed;
	}
	public void setGmailLoginAllowed(boolean gmailLoginAllowed) {
		this.gmailLoginAllowed = gmailLoginAllowed;
	}
	public boolean getTicketAlloedForPublic() {
		return ticketAlloedForPublic;
	}
	public void setTicketAlloedForPublic(boolean ticketAlloedForPublic) {
		this.ticketAlloedForPublic = ticketAlloedForPublic;
	}
	public boolean getAnyDomain() {
		return anyDomain;
	}
	public void setAnyDomain(boolean anyDomain) {
		this.anyDomain = anyDomain;
	}
	boolean signupAllowed=false;
	boolean gmailLoginAllowed=true;
	boolean ticketAlloedForPublic=true;
	boolean anyDomain;
	public boolean getSamlEnabled() {
		return samlEnabled;
	}
	public void setSamlEnabled(boolean samlEnabled) {
		this.samlEnabled = samlEnabled;
	}
	boolean samlEnabled;
	
	private HashMap samlinfo = null;
	public HashMap getSamlInfo()
	{
		if(!getSamlEnabled())
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
	

	
	public void save() throws Exception
	{
		/*OrgApi.updatePortalInfo(this, null);
		System.out.println("--------------> service"+this);*/
	}
	@Override
	public String toString() {
		return "ServicePortalInfo [signupAllowed=" + signupAllowed + ", gmailLoginAllowed=" + gmailLoginAllowed
				+ ", ticketAlloedForPublic=" + ticketAlloedForPublic + ", anyDomain=" + anyDomain + ", samlEnabled="
				+ samlEnabled + ", samlinfo=" + samlinfo + "]";
	}
}
