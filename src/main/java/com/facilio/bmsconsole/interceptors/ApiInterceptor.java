package com.facilio.bmsconsole.interceptors;

import java.math.BigInteger;
import java.util.HashMap;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.sql.DBUtil;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class ApiInterceptor  extends AbstractInterceptor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		// TODO Auto-generated method stub
		
		String authtoken =((invocation.getInvocationContext()).getParameters()).get("authtoken").getValue();
		//String s = "4d0d08ada45f9dde1e99cad9";
		BigInteger bi = new BigInteger(authtoken, 16);
		System.out.println(bi);
		String query = "select ORGID,USERID from Global_FacilioAuthToken where authtoken="+bi.longValue();
		
		HashMap record = DBUtil.getRecord(query);
		if(record!=null)
		{
			Organization org = AccountUtil.getOrgBean().getOrg((Long) record.get("ORGID"));
			User user = AccountUtil.getUserBean().getUser((Long) record.get("USERID"));
			
			AccountUtil.setCurrentAccount(new Account(org, user));
		
		String result = invocation.invoke();
		
		return result;
		}
		else
		{
			return "401ERROR";
		}
	}

}
