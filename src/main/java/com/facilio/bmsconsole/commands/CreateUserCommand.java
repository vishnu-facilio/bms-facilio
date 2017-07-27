package com.facilio.bmsconsole.commands;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.sql.DBUtil;


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
			String insertquery = "insert into Organizations (ORGNAME,FACILIODOMAINNAME) values (?,?)";
			PreparedStatement ps = con.prepareStatement(insertquery, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, signupinfomap.get("companyname"));
			ps.setString(2, signupinfomap.get("domainname"));
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			rs.next();
			long orgId = rs.getLong(1);
			arg0.put(ORG_ID, orgId);
			ps.close();
			
			String insertquery1 = "insert into Users (COGNITO_ID,USER_VERIFIED,EMAIL) values (?, ?, ?)";
			PreparedStatement ps1 = con.prepareStatement(insertquery1, Statement.RETURN_GENERATED_KEYS);
			ps1.setString(1, signupinfomap.get("COGNITO_ID"));
			ps1.setBoolean(2, true);
			ps1.setString(3, signupinfomap.get("email"));
			ps1.executeUpdate();
			ResultSet rs1 = ps1.getGeneratedKeys();
			rs1.next();
			long userId = rs1.getLong(1);
			ps1.close();
			
			String insertquery2 = "insert into Role (ORGID,NAME,PERMISSIONS) values (?,?,?)";
			PreparedStatement ps2 = con.prepareStatement(insertquery2);
			ps2.setLong(1, orgId);
			ps2.setString(2, "Administrator");
			ps2.setString(3, "0");
			ps2.addBatch();
			ps2.setLong(1, orgId);
			ps2.setString(2, "Dispatcher");
			ps2.setString(3, "0");
			ps2.addBatch();
			ps2.setLong(1, orgId);
			ps2.setString(2, "Technician");
			ps2.setString(3, "0");
			ps2.executeBatch();
			ps2.close();
			
			String insertquery3 = "insert into ORG_Users (USERID,ORGID,INVITEDTIME,ISDEFAULT,INVITATION_ACCEPT_STATUS,ROLE_ID) values (?,?,UNIX_TIMESTAMP(),true,true,(select ROLE_ID from Role where NAME='Administrator' limit 1))";
			PreparedStatement ps3 = con.prepareStatement(insertquery3, Statement.RETURN_GENERATED_KEYS);
			ps3.setLong(1,userId);
			ps3.setLong(2, orgId);
			ps3.executeUpdate();
			ps3.close();
			
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
