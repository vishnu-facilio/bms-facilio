package com.facilio.bmsconsole.actions;

import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;


public class LoginAction extends ActionSupport{

	public String execute() throws Exception {
        
		Map session = ActionContext.getContext().getSession();  
		String oldusername = (String)session.get("USERNAME");
		if(oldusername !=null)
		{
			// not possible
		}
		//TODO
		// find username/emaiid and other threadpool data from the access toke
		String accesstoken = getAccessToken();
		
		
		// First login, store userinfo in localdb
		// store accesstoken for future user
		
		ActionContext.getContext().getSession().put("USERNAME", "yogebabu@gmail.com");
		// redirect to home
		
		return SUCCESS;
	 
	}
	public String getJwtToken() {
		return jwtToken;
	}
	public void setJwtToken(String jwtToken) {
		this.jwtToken = jwtToken;
	}
	private String jwtToken;
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	private String accessToken;
}
