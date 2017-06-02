package com.facilio.bmsconsole.actions;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.facilio.assets.AssetsAPI;
import com.facilio.tasker.FacilioTimer;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

public class Device extends ActionSupport
{
	private static Logger logger = Logger.getLogger(Device.class.getName());
	
	public String show()
	{
		try 
		{
			ValueStack stack = ActionContext.getContext().getValueStack();
		    Map<String, Object> context = new HashMap<String, Object>();

		    context.put("DEVICES", AssetsAPI.getAssetDetails(1L)); 
		    stack.push(context);
		} 
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while showing device details" +e.getMessage(), e);
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String showAdd()
	{
		return SUCCESS;
	}
	
	public void add()
	{
		HttpServletRequest request = ServletActionContext.getRequest();
		String name = request.getParameter("name");
		String type = request.getParameter("type");
		String datasource = request.getParameter("datasource");
		String publicip = request.getParameter("publicip");
		String polltime = request.getParameter("polltime");
		
		try 
		{
			Long assetTypeId = AssetsAPI.getAssetTypeId(type);
			if(assetTypeId == null)
			{
				assetTypeId = AssetsAPI.addAssetType(type, "Distech Controls");
			}
			Long assetId = AssetsAPI.addAsset(name, assetTypeId, publicip);
			AssetsAPI.addAssetData(assetId, Long.parseLong(polltime));
			FacilioTimer.schedulePeriodicJob("SyncIotData", 10, Integer.parseInt(polltime), "facilio");
			//FacilioTimer.schedulePeriodicJob("DynamoDBMySQLSync", 10, 20, "facilio");
		}
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, "Exception while adding device" +e.getMessage(), e);
		}
	}
	
	public String showDeviceData()
	{
		return SUCCESS;
	}
}
