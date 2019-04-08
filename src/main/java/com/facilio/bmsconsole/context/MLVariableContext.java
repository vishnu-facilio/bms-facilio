package com.facilio.bmsconsole.context;

public class MLVariableContext 
{
	private long moduleid;
	private long fieldid;
	private boolean isSource;
	public long getModuleid() 
	{
		return moduleid;
	}
	public void setModuleid(long moduleid) 
	{
		this.moduleid = moduleid;
	}
	public long getFieldid() 
	{
		return fieldid;
	}
	public void setFieldid(long fieldid) 
	{
		this.fieldid = fieldid;
	}
	public boolean isSource() 
	{
		return isSource;
	}
	public void setSource(boolean isSource) 
	{
		this.isSource = isSource;
	}
}
