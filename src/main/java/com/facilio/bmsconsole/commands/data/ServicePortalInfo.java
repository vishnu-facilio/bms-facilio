package com.facilio.bmsconsole.commands.data;

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
	
	
}
