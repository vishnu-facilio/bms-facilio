package com.facilio.bmsconsole.context;

public class RCAAlarm extends MLAnomalyAlarm 
{
	
	private static final long serialVersionUID = 1L;
	private long parentId;
	
	public void setParentId(long parentId)
	{
		this.parentId = parentId;
	}
	
	public long getParentId()
	{
		return parentId;
	}

}
