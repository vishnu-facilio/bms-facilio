package com.facilio.leed.context;

import com.facilio.accounts.dto.Organization;

public class ArcContext extends Organization {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String userName;
	private String password;
	private String subscriptionKey;
	private String authKey;
	private long authUpdateTime = -1;
	private String arcProtocol;
	private String arcHost;
	private String arcPort;
	
	public void setUserName(String userName)
	{
		this.userName = userName;
	}	
	public String getUserName()
	{
		return this.userName;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}	
	public String getPassword()
	{
		return this.password;
	}

	public void setSubscriptionKey(String subscriptionKey)
	{
		this.subscriptionKey = subscriptionKey;
	}	
	public String getSubscriptionKey()
	{
		return this.subscriptionKey;
	}
	
	public void setAuthKey(String authKey)
	{
		this.authKey = authKey;
	}	
	public String getAuthKey()
	{
		return this.authKey;
	}
	
	public void setAuthUpdateTime(long authUpdateTime)
	{
		this.authUpdateTime = authUpdateTime;
	}	
	public long getAuthUpdateTime()
	{
		return this.authUpdateTime;
	}

	public void setArcProtocol(String arcProtocol)
	{
		this.arcProtocol = arcProtocol;
	}	
	public String getArcProtocol()
	{
		return this.arcProtocol;
	}
	
	public void setArcHost(String arcHost)
	{
		this.arcHost = arcHost;
	}	
	public String getArcHost()
	{
		return this.arcHost;
	}

	public void setArcPort(String arcPort)
	{
		this.arcPort = arcPort;
	}	
	public String getArcPort()
	{
		return this.arcPort;
	}

}
