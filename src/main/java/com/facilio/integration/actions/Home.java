package com.facilio.integration.actions;

import java.util.HashMap;
import java.util.Map;

import com.facilio.fw.auth.CognitoUtil;
import com.opensymphony.xwork2.ActionSupport;

public class Home extends ActionSupport {
/*
 * redirect_uri = https://platform.zoho.com/auth/conns/urjanet/redirect
response_type = code
state = PORTAL-yogendrababu
 */
	public String loginSubmit()
	{
		System.out.println(redirect_uri + state + response_type);
		return SUCCESS;
	}
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	private String username = null;
	private String password = null;
	public Map getJsonresponse() {
		return jsonresponse;
	}
	public void setJsonresponse(Map jsonresponse) {
		this.jsonresponse = jsonresponse;
	}
	public void setJsonresponse(String key, Object value) {
		this.jsonresponse.put(key, value);
	}
	private Map jsonresponse = new HashMap();
	public String validatelogin()
	{
		System.out.println(username + state + password);
		String jwt = CognitoUtil.createJWT("id", "auth0", username, System.currentTimeMillis()+24*60*60000);
		System.out.println("Response token is "+ jwt);
		setJsonresponse("token",jwt);
		return SUCCESS;
	}
	
	public String getToken()
	{
		return SUCCESS;
	}
	
	private String redirect_uri;
	public String getRedirect_uri() {
		return redirect_uri;
	}

	public void setRedirect_uri(String redirect_uri) {
		this.redirect_uri = redirect_uri;
	}

	public String getResponse_type() {
		return response_type;
	}

	public void setResponse_type(String response_type) {
		this.response_type = response_type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	private String response_type;
	private String state ;
}
