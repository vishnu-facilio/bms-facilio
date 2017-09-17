package com.facilio.leed.objects;

public class ElectricalEnergy {

	private int provider_id;
	private long added_date;
	private double kwh;
	
	public void setProvider_id(int pId)
	{
		this.provider_id = pId;
	}
	
	public int getProvider_id()
	{
		return this.provider_id;
	}
	
	public void setAdded_date(long date)
	{
		this.added_date = date;
	}
	
	public long getAdded_date()
	{
		return this.added_date;
	}
	
	public void setKwh(double kwhe)
	{
		this.kwh = kwhe;
	}
	
	public double getKwh()
	{
		return this.kwh;
	}

}
