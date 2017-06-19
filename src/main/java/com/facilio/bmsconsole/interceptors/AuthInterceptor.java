package com.facilio.bmsconsole.interceptors;

import java.util.Base64;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.facilio.bmsconsole.context.UserContext;
import com.facilio.bmsconsole.util.UserAPI;
import com.facilio.fw.OrgInfo;
import com.facilio.fw.UserInfo;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class AuthInterceptor extends AbstractInterceptor {

	private static String HOSTNAME = null;
	@Override
	public void init() {
		
		super.init();
	}
	@Override
	public String intercept(ActionInvocation arg0) throws Exception {
		
	      String output = "Pre-Processing"; 
	      System.out.println(output);

			String username = (String)ActionContext.getContext().getSession().get("USERNAME");
			HttpServletRequest request = ServletActionContext.getRequest();
			 if(username==null)
		      {
				String tempaccesscode =  request.getParameter("accesscode");
				if(tempaccesscode==null)
				{
			    	  return Action.LOGIN;

				}
				String idToken = (String)ActionContext.getContext().getApplication().get(tempaccesscode);
				if(idToken==null)
				{
					return Action.LOGIN;
				}
				// Generate username from idtoken
				String[] payloads = idToken.split("\\.");
				Base64.Decoder decoder = Base64.getUrlDecoder();

				// decode the payload block and make it as a json object
				JSONObject jsonObject = ((JSONObject) (JSONValue.parse(new String(decoder.decode(payloads[1]))))); 
				
				System.out.println("The JSON Object"+jsonObject);
				username = (String)jsonObject.get("email");
				Map session = ActionContext.getContext().getSession(); 
				session.put("USERNAME", username);
				session.put("USER_ACCESSCODE", tempaccesscode);
				
		      }		
				
					String requestdomain = request.getServerName();
					  	
					if(HOSTNAME==null)
					{
						HOSTNAME = (String)ActionContext.getContext().getApplication().get("DOMAINNAME");
					}
					String subdomain = requestdomain.replaceAll(HOSTNAME, "");
					
				
					UserContext context = UserAPI.getUser(username);
					request.setAttribute("user_role", context.getRoleAsString());
					//ActionContext.getContext().getParameters()

        	
        
	   
	  	try {
			OrgInfo.validateOrgInfo(subdomain, username);
		} catch (Exception e) {
			e.printStackTrace();
			 return "unauthorized";
		}
	      /* let us call action or next interceptor */
	      String result = arg0.invoke();

	      /* let us do some post-processing */
	      output = "Post-Processing"; 
	      System.out.println(output);

	      return result;
	}

}
