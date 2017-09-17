package com.facilio.leed.context;

public class UtilityProviderContext  {

	private long providerId;
	private String name;
	private String displayName;
	private String country;
	
	public long getProviderId()
	{
		return providerId;
	}
	public void setProviderId(long Id)
	{
		this.providerId = Id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDisplayName()
	{
		return displayName;
	}
	public void setDisplayName(String dName)
	{
		this.displayName = dName;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
}
