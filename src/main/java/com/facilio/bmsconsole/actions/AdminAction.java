package com.facilio.bmsconsole.actions;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.EncodeException;

import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.auth.actions.FacilioAuthAction;
import com.facilio.bmsconsole.util.AdminAPI;
import com.facilio.fw.LRUCache;
import com.facilio.license.FreshsalesUtil;
import com.facilio.tasker.FacilioTimer;
import com.facilio.wms.message.Message;
import com.facilio.wms.message.MessageType;
import com.facilio.wms.util.WmsApi;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

public class AdminAction extends ActionSupport
{
	private static Logger logger = Logger.getLogger(AdminAction.class.getName());
	private static org.apache.log4j.Logger log = org.apache.log4j.LogManager.getLogger(AdminAction.class.getName());
	
	public String show()
	{
		return SUCCESS;
	}
	
	public String jobs()
	{
		try 
		{
			ValueStack stack = ActionContext.getContext().getValueStack();
		    Map<String, Object> context = new HashMap<String, Object>();

		    context.put("JOBS", AdminAPI.getSystemJobs()); 
		    stack.push(context);
		} 
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while showing admin job details" +e.getMessage(), e);
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String updateUser()
	{
		HttpServletRequest request = ServletActionContext.getRequest();
		long orgId =  Long.parseLong(request.getParameter("orgid"));
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		long roleId =  Long.parseLong(request.getParameter("roleId"));
		User newUser = new User();
		
		if(!email.contains("@"))
		{
			newUser.setMobile(email);
		}
		newUser.setName(name);
		newUser.setEmail(email);
		newUser.setRoleId(roleId);
		newUser.setPassword(FacilioAuthAction.cryptWithMD5(password));
		newUser.setUserVerified(true);
		newUser.setUserStatus(true);
		try {
			AccountUtil.getUserBean().inviteAdminConsoleUser(orgId, newUser);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return SUCCESS;
	}
	
	public String updateCRM()
	{
		HttpServletRequest request = ServletActionContext.getRequest();
		String orgid = request.getParameter("orgid");
		String domainame = ((String[])getFreshsales().get("faciliodomainname"))[0];
		String amount = ((String[])getFreshsales().get("amount"))[0];

		JSONObject data = new JSONObject();
		data.put("name", domainame);
		data.put("amount", amount);
		
		JSONObject salesacct = new JSONObject();
		salesacct.put("name", domainame);
		data.put("sales_account", salesacct);
		
		JSONObject customfield = new JSONObject();
		customfield.put("ORGID", orgid);
		customfield.put("faciliodomainname", domainame);

		data.put("custom_field", customfield);
		
		System.out.println("Final data to freshsales"+data);
		
			try {
				FreshsalesUtil.createLead("deals","deal", data);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return SUCCESS;
	}
	public JSONObject getFreshsales() {
		return freshsales;
	}

	public void setFreshsales(JSONObject freshsales) {
		this.freshsales = freshsales;
	}
	private JSONObject freshsales = new JSONObject();
	
	public void addJob()
	{
		HttpServletRequest request = ServletActionContext.getRequest();
		String name = request.getParameter("name");
		Integer period = Integer.parseInt(request.getParameter("period"));
		try 
		{
			FacilioTimer.schedulePeriodicJob(1, name, 15, period, "facilio");
			AdminAPI.addSystemJob(1l);
		}
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, "Exception while adding job" +e.getMessage(), e);
		}
	}
	
	public void deleteJob()
	{
		HttpServletRequest request = ServletActionContext.getRequest();
		Long jobId = Long.parseLong(request.getParameter("jobId"));
		try 
		{
			AdminAPI.deleteSystemJob(jobId);
		}
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, "Exception while adding job" +e.getMessage(), e);
		}
	}
	
	public String clearCache() throws IOException
	{
		LRUCache.getFieldsCache().purgeCache();
		LRUCache.getModuleFieldsCache().purgeCache();
		LRUCache.getUserSessionCache().purgeCache();
		System.out.println("Clear cache called");
		
		
		return SUCCESS ;
	}
	
	public static String reloadBrowser() throws IOException
	{
		Message message = new Message(MessageType.BROADCAST);
		
		
		message.setNamespace("system");
		message.setAction("reload");
		//message.setEventType(WmsEvent.WmsEventType.RECORD_UPDATE);
		message.addData("time", System.currentTimeMillis());
		message.addData("sound", false);
		
	//	JSONObject messagejson = new JSONObject();
	//	message.setContent(messagejson);
		//WmsApi.sendChatMessage(to, message);
		try {
			WmsApi.broadCastMessage(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e);
		} catch (EncodeException e) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e);
		}
		
		
		return SUCCESS;
	}
}

