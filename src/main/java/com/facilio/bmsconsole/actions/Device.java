package com.facilio.bmsconsole.actions;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.device.types.DistechControls;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.fw.OrgInfo;
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

		    context.put("DEVICES", DeviceAPI.getAllControllers(OrgInfo.getCurrentOrgInfo().getOrgid())); 
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
		int polltime = Integer.parseInt(request.getParameter("polltime"));
		
		try 
		{
			Long controllerId = AssetsAPI.addAsset(name, OrgInfo.getCurrentOrgInfo().getOrgid());
			Long jobId = FacilioTimer.schedulePeriodicJob("DeviceDataExtractor", 15, polltime, "facilio");
			DeviceAPI.addController(controllerId, 1, publicip, null, polltime, jobId);
			DeviceAPI.discoverDevices(controllerId, OrgInfo.getCurrentOrgInfo().getOrgid());
			
			FacilioTimer.schedulePeriodicJob("IotConnector", 15, 20, "facilio");
		}
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, "Exception while adding device" +e.getMessage(), e);
		}
	}
	
	public void enableDeviceMonitoring()
	{
		HttpServletRequest request = ServletActionContext.getRequest();
		Long deviceId = Long.parseLong(request.getParameter("deviceId"));
		int polltime = 30;//Integer.parseInt(request.getParameter("polltime"));
		try 
		{
			Long jobId = FacilioTimer.schedulePeriodicJob("DeviceDataExtractor", 15, polltime, "facilio");
			DeviceAPI.updateController(deviceId, jobId, true);
			
			FacilioTimer.schedulePeriodicJob("IotConnector", 15, 20, "facilio");
		}
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, "Exception while enableDeviceMonitoring" +e.getMessage(), e);
		}
	}
	
	public void disableDeviceMonitoring()
	{
		HttpServletRequest request = ServletActionContext.getRequest();
		Long deviceId = Long.parseLong(request.getParameter("deviceId"));
		try 
		{
			DeviceAPI.updateController(deviceId, null, false);
		}
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, "Exception while disableDeviceMonitoring" +e.getMessage(), e);
		}
	}
	
	public void showDeviceData()
	{
		HttpServletRequest request = ServletActionContext.getRequest();
		Long controllerId = Long.parseLong(request.getParameter("controllerId"));
		HttpServletResponse response = ServletActionContext.getResponse();
		try 
		{
			response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    Long deviceId = DeviceAPI.getDeviceId(controllerId);
		    response.getWriter().print(DeviceAPI.getDeviceData(deviceId).toString());
		} 
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, "Exception while showing device data" +e.getMessage(), e);
		}
	}
	
	public String showDevices()
	{
		HttpServletRequest request = ServletActionContext.getRequest();
		Long controllerId = Long.parseLong(request.getParameter("controllerId"));
		try 
		{
			ValueStack stack = ActionContext.getContext().getValueStack();
		    Map<String, Object> context = new HashMap<String, Object>();

		    context.put("DEVICES", DeviceAPI.getDevices(controllerId));
		    context.put("CONTROLLERID", controllerId);
		    stack.push(context);
		} 
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while showing device details" +e.getMessage(), e);
			return ERROR;
		}
		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	public void showTree()
	{
		HttpServletRequest request = ServletActionContext.getRequest();
		Long controllerId = Long.parseLong(request.getParameter("controllerId"));
		HttpServletResponse response = ServletActionContext.getResponse();
		try 
		{
			JSONObject result = new JSONObject();
			JSONArray childArray = new JSONArray();
			result.put("name", AssetsAPI.getAssetInfo(controllerId).get("name"));
			List<Map<String, Object>> devices = DeviceAPI.getDevices(controllerId);
			for(Map<String, Object> deviceMap : devices)
			{
				JSONObject child = new JSONObject();
				child.put("name", (String) deviceMap.get("name"));
				childArray.add(child);
			}
			result.put("children", childArray);
			response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    response.getWriter().print(result);
		} 
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, "Exception while showing device data" +e.getMessage(), e);
		}
	}
	
	public String showConfigureDevice()
	{
		HttpServletRequest request = ServletActionContext.getRequest();
		Long deviceId = Long.parseLong(request.getParameter("deviceId"));
		try 
		{
			ValueStack stack = ActionContext.getContext().getValueStack();
		    Map<String, Object> context = new HashMap<String, Object>();

		    context.put("DEVICEINSTANCES", DeviceAPI.getDeviceInstances(deviceId));
		    context.put("ATTRIBUTES", DeviceAPI.getAttributes());
		    context.put("DEVICEID", deviceId);
		    stack.push(context);
		} 
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while showing device details" +e.getMessage(), e);
			return ERROR;
		}
		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	public void configureDevice()
	{
		HttpServletRequest request = ServletActionContext.getRequest();
		Long deviceId = Long.parseLong(request.getParameter("deviceId"));
		System.out.println(request.getParameter("data"));
		HttpServletResponse response = ServletActionContext.getResponse();
		try 
		{
			JSONParser parser = new JSONParser();
			JSONObject data = (JSONObject) parser.parse(request.getParameter("data"));
			Iterator<String> dataIterator = data.keySet().iterator();
			while(dataIterator.hasNext())
			{
				String key = dataIterator.next();
				String columnName = (String) data.get(key);
				DeviceAPI.updateDeviceInstance(deviceId, Integer.parseInt(key), columnName);
			}
			DeviceAPI.updateDevice(deviceId, 2);
			JSONObject result = new JSONObject();
			result.put("message", "success");
			response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    response.getWriter().print(result);
		} 
		catch (Exception e) 
		{
			logger.log(Level.SEVERE, "Exception while showing device data" +e.getMessage(), e);
		}
	}
}
