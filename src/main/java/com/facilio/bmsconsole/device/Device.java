package com.facilio.bmsconsole.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.bmsconsole.context.Assets;

public class Device extends Assets
{
	private static Logger logger = Logger.getLogger(Device.class.getName());
	
	private Long deviceId;
	private transient Long parentId;
	private transient int status;
	private List<Device> children;
	private transient List<Map<String, Object>> instances;
	public Device()
	{
		
	}
	
	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}

	public Device setParentId(Long parentId)
	{
		this.parentId = parentId;
		return this;
	}
	
	public Device setStatus(int status)
	{
		this.status = status;
		return this;
	}
		
	public void add(Device device)
	{
		if(this.children == null)
		{
			this.children = new ArrayList<>();
		}
		this.children.add(device);
	}
	
	public Long getParentId()
	{
		return this.parentId;
	}
	
	public int getStatus()
	{
		return this.status;
	}
	
	public List<Device> getChildren()
	{
		return this.children;
	}
	
	public void addInstance(Long controllerInstanceId, String instanceName)
	{
		if(this.instances == null)
		{
			this.instances = new ArrayList<>();
		}
		Map<String, Object> instanceMap = new HashMap<>();
		instanceMap.put("controllerInstanceId", controllerInstanceId);
		instanceMap.put("instanceName", instanceName);
		this.instances.add(instanceMap);
	}
	
	public List<Map<String, Object>> getInstances()
	{
		return this.instances;
	}
	
	private Long spaceId;
	public Long getSpaceId()
	{
		return this.spaceId;
	}
	
	public void setSpaceId(Long spaceId)
	{
		this.spaceId = spaceId;
	}
	
	private String type;
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return this.type;
	}
}
