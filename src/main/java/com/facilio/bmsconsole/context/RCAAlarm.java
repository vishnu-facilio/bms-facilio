package com.facilio.bmsconsole.context;

public class RCAAlarm extends MLAnomalyAlarm 
{
	
	private static final long serialVersionUID = 1L;
	private long parentid;
	
	public void setparentid(long parentid)
	{
		this.parentid = parentid;
	}
	
	public long getparentid()
	{
		return parentid;
	}

}
