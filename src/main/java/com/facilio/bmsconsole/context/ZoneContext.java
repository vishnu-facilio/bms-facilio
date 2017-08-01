package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class ZoneContext extends ModuleBaseWithCustomFields {
	
	private String shortDescription;
	public String getShortDescription() {
		return shortDescription;
	}
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private List<BaseSpaceContext> basespaces;
	public List<BaseSpaceContext> getBasespaces() 
	{
		return basespaces;
	}
	
	public void setBasespaces(List<BaseSpaceContext> basespaces) 
	{
		this.basespaces = basespaces;
	}
}
