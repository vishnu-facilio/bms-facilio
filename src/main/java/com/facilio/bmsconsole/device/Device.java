package com.facilio.bmsconsole.device;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Device 
{
	private static Logger logger = Logger.getLogger(Device.class.getName());
	
	private transient Long id;
	private transient Long parentId;
	private transient int status;
	private String name;
	private List<Device> children;
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
}
