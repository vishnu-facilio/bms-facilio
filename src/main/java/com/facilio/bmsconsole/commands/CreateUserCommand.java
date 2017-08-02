package com.facilio.bmsconsole.commands;

import java.util.HashMap;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.OrgApi;


public class CreateUserCommand implements Command {
	
	public static final String signupinfo = "signupinfo";
	public static final String ORG_ID = "orgId";

	@Override
	public boolean execute(Context arg0) throws Exception {
		FacilioContext fc = (FacilioContext)arg0;
		HashMap<String,String> signupinfomap = (HashMap<String,String>)fc.get(signupinfo);
		
		System.out.println("This is the map :-   "+signupinfomap);
		java.sql.Connection con = fc.getConnectionWithTransaction();
		
		try {
			String cognitoId = null;
			OrgApi.createOrganization(con, signupinfomap.get("companyname"), signupinfomap.get("domainname"), null, signupinfomap.get("email"), cognitoId, false);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			// it will get closed after chain completion
			//con.close();
		}
		return false;
	}

}
