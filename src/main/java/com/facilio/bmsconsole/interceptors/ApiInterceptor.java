package com.facilio.bmsconsole.interceptors;

import java.math.BigInteger;
import java.util.HashMap;

import com.facilio.bmsconsole.util.OrgApi;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class ApiInterceptor  extends AbstractInterceptor {

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
		OrgInfo.setCurrentOrgInfo(OrgApi.getOrgInfo((Long)record.get("ORGID")));

		
		String result = invocation.invoke();
		
		return result;
		}
		else
		{
			return "401ERROR";
		}
	}

}
