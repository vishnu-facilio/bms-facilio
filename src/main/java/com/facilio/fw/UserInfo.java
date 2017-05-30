package com.facilio.fw;

public class UserInfo {
	private long userid;
	private String email;
	private String cognitoid;
	private String username;
	private static ThreadLocal<UserInfo> userlocal = new ThreadLocal<UserInfo>();
	
	public  UserInfo(String username)
	{
		this.username = username;
		this.email = email;
	}

	public static  UserInfo getCurrentUser()
	{
		return userlocal.get();
	}
	public static  void setCurrentUser(UserInfo userinfo)
	{
		 userlocal.set(userinfo);
	}
	public static void cleanup()
	{
		userlocal.remove();
	}
	
}
