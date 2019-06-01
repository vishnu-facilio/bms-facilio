package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class MLVariableContext extends ModuleBaseWithCustomFields
{
	private static final long serialVersionUID = 1L;

	private long mlID;
	private long moduleID;
	private long fieldID;
	private boolean isSource;
	private long parentFieldID;
	private long parentID;
	private long maxSamplingPeriod;
	private int sequence;
	
	public long getMlID()
	{
		return mlID;
	}
	public void setMlID(long mlID)
	{
		this.mlID = mlID;
	}
	
	public long getModuleID() 
	{
		return moduleID;
	}
	public void setModuleID(long moduleID) 
	{
		this.moduleID = moduleID;
	}
	public long getFieldID() 
	{
		return fieldID;
	}
	public void setFieldID(long fieldID) 
	{
		this.fieldID = fieldID;
	}
	public boolean getIsSource() 
	{
		return isSource;
	}
	public void setIsSource(boolean isSource) 
	{
		this.isSource = isSource;
	}
	public void setParentFieldID(long parentFieldID)
	{
		this.parentFieldID = parentFieldID;
	}
	public long getParentFieldID()
	{
		return parentFieldID;
	}
	public void setParentID(long parentID)
	{
		this.parentID = parentID;
	}
	public long getParentID()
	{
		return parentID;
	}
	public long getMaxSamplingPeriod() 
	{
		return maxSamplingPeriod;
	}
	public void setMaxSamplingPeriod(long maxSamplingPeriod) 
	{
		this.maxSamplingPeriod = maxSamplingPeriod;
	}
	
	public void setSequence(int sequence)
	{
		this.sequence=sequence;
	}
	
	public int getSequence()
	{
		return sequence;
	}
}
