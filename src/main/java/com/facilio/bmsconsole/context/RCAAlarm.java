package com.facilio.bmsconsole.context;

public class RCAAlarm extends MLAnomalyAlarm 
{
	
	private static final long serialVersionUID = 1L;
	private long parentid;
	private long ratio;
	
	public void setparentid(long parentid)
	{
		this.parentid = parentid;
	}
	
	public long getparentid()
	{
		return parentid;
	}
	
	public void setRatio(long ratio)
	{
		this.ratio=ratio;
	}
	
	public long getRatio()
	{
		return ratio;
	}

}
