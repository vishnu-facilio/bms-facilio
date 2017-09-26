package com.facilio.bmsconsole.device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Device 
{
	private static Logger logger = Logger.getLogger(Device.class.getName());
	
	private Long id;
	private transient Long parentId;
	private transient int status;
	private String name;
	private List<Device> children;
	private transient List<Map<String, Object>> instances;
	public Device()
	{
		
	}
	
	public Device setId(Long id)
	{
		this.id = id;
		return this;
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
	
	public Device setName(String name)
	{
		this.name = name;
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
	
	public String getName()
	{
		return this.name;
	}
	
	public Long getId()
	{
		return this.id;
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
	
	private long spaceId = -1;
	public Long getSpaceId()
	{
		return this.spaceId;
	}
	
	public Device setSpaceId(long spaceId)
	{
		this.spaceId = spaceId;
		return this;
	}
	
	public String getType() {
		if(this.name != null) {
			return typeMap.get(this.name);
		}
		return null;
	}
	
	private static final Map<String, String> typeMap = Collections.unmodifiableMap(initTypeMap());
	private static Map<String, String> initTypeMap() {
		Map<String, String> typeMap = new HashMap<>();
		
		typeMap.put("1 Lp 1", "Mi/f");
		typeMap.put("2 Lp 1", "OH");
		typeMap.put("3 Lp 1", "OH");
		typeMap.put("4 Lp 1", "MCP");
		typeMap.put("5 Lp 1", "MCP");
		typeMap.put("6 Lp 1", "OH");
		typeMap.put("7 Lp 1", "OH");
		typeMap.put("8 Lp 1", "MCP");
		typeMap.put("9 Lp 1", "OH");
		typeMap.put("10 Lp 1", "Mi/f");
		typeMap.put("11 Lp 1", "OH");
		typeMap.put("12 Lp 1", "OH");
		typeMap.put("13 Lp 1", "OH");
		typeMap.put("14 Lp 1", "OH");
		typeMap.put("15 Lp 1", "OH");
		typeMap.put("16 Lp 1", "OH");
		typeMap.put("17 Lp 1", "MCP");
		typeMap.put("18 Lp 1", "MCP");
		typeMap.put("19 Lp 1", "Li/f");
		typeMap.put("20 Lp 1", "OHM");
		
		return typeMap;
	}
}
