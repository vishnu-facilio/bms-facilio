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

import com.facilio.bmsconsole.util.AdminAPI;
import com.facilio.fw.LRUCache;
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
	
	public String clearCache()
	{
		LRUCache.getFieldsCache().purgeCache();
		LRUCache.getModuleFieldsCache().purgeCache();
		System.out.println("Clear cache called");
		return SUCCESS;
	}
	
	public String reloadBrowser()
	{
		Message message = new Message(MessageType.SYSTEM);
		
		
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
			e.printStackTrace();
		} catch (EncodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SUCCESS;
	}
	
}

