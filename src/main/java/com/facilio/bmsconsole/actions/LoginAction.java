package com.facilio.bmsconsole.actions;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.fw.OrgInfo;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import org.apache.commons.chain.Chain;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;


public class LoginAction extends ActionSupport{
	static
	{
		System.out.println("Login action loaded");
	}
	private static String HOSTNAME = null;

	public String execute() throws Exception {
		if(HOSTNAME==null)
		{
			HOSTNAME = (String)ActionContext.getContext().getApplication().get("DOMAINNAME");
		}
        
		// get the identity token and decode it.
		String idToken = getIdToken();
		
		
		int tempaccesscode = idToken.hashCode();
		System.out.println("The temp access code is:"+tempaccesscode);
		ActionContext.getContext().getApplication().put(tempaccesscode+"", idToken);
		Base64.Decoder decoder = Base64.getUrlDecoder();
		
		// the identity token has three block 0-header, 1-payload, 2-signature
		// 1-payload has the actual required user information.
		String[] payloads = idToken.split("\\.");
		
		// decode the payload block and make it as a json object
		JSONObject jsonObject = ((JSONObject) (JSONValue.parse(new String(decoder.decode(payloads[1]))))); 
		
		System.out.println("The JSON Object"+jsonObject);
		
		Map session = ActionContext.getContext().getSession(); 
		
		String userName = (String)jsonObject.get("email");
		boolean verifiedUser = ((Boolean) jsonObject.get("email_verified")).booleanValue();
		
		System.out.println(verifiedUser);
		
		if (verifiedUser && userName!=null)
		{
			String subdomain = OrgInfo.getDefaultOrgInfo(userName);
			HttpServletRequest request = ServletActionContext.getRequest();
			String redirecturl =request.getScheme()+"://"+subdomain + HOSTNAME+":"+request.getServerPort()+request.getContextPath() +"/home/index?accesscode="+tempaccesscode;
			
			
			session.put("USERNAME", userName);
			
			response = redirecturl;
		}
		else if (!verifiedUser && userName!=null)
		{
			//user is not verified
			response = "unverified_user";

		}
		else 
		{
			// else the user is not verified - redirect to the login page.
		}
		
		return SUCCESS;
	}
	private String response = null;
	public String getResponse()
	{
		return response;
	}
	// getter setter for access token
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	private String accessToken;
	
	// getter setter for identity token
	private String idToken;
	
	public String getIdToken() {
		return idToken;
	}
	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}
	
	private HashMap<String,String> signupinfo = new HashMap<String,String>();
	public boolean isSignup() {
		return isSignup;
	}
	public void setSignup(boolean isSignup) {
		this.isSignup = isSignup;
	}
	private boolean isSignup = false;
	public void setSignupInfo(String key,String value)
	{
		signupinfo.put(key,value);
	}
	
	public HashMap<String,String> getSignupInfo()
	{
		return signupinfo;
	}
	public String getSignupInfo(String signupkey)
	{
		return signupinfo.get(signupkey);
	}
	
	public String signup() throws Exception
	{
		FacilioContext orgsignupcontext = new FacilioContext();
		System.out.println("The parameters list"+ActionContext.getContext().getParameters());
		orgsignupcontext.put("signupinfo", getSignupInfo());
		Chain c = FacilioChainFactory.getOrgSignupChain();
		c.execute(orgsignupcontext);
		response ="<result>signupsuccess</result>";
		return "success";
	}
}
