package com.facilio.bmsconsole.actions;

import java.util.Base64;
import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
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
			System.out.println("into if");
			//start establishing session details
			session.put("USERNAME", userName);
			
			// check the list of subdomain from OrgUsers table
			// assign default subdomain 
			// ORG_Users.ISDEFAULT
			String subdomain = "yoge";
			String redirecturl =subdomain + HOSTNAME;
			
			response = "success";
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
}
