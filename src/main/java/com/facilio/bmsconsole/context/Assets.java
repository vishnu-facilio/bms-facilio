package com.facilio.bmsconsole.context;

public class Assets {
	
	private long assetId;
	private String name;
	private long orgId;
	
	public void setAssetId(long assetId)
	{
		this.assetId = assetId;
	}
	public long getAssetId()
	{
		return this.assetId;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	public String getName()
	{
		return this.name;
	}

	public void setOrgId(long orgId)
	{
		this.orgId = orgId;
	}
	public long getOrgId()
	{
		return this.orgId;
	}
}
