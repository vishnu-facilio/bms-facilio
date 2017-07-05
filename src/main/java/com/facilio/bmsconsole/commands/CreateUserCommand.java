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
		String insertquery = "insert into Organizations (ORGNAME,FACILIODOMAINNAME) values (?,?)";
		
		PreparedStatement ps = con.prepareStatement(insertquery, Statement.RETURN_GENERATED_KEYS);
		ResultSet rs = null;
		try {
			ps.setString(1, signupinfomap.get("companyname"));
			ps.setString(2, signupinfomap.get("domainname"));
			ps.addBatch();
			insertquery = "insert into Users (COGNITO_ID,USER_VERIFIED,EMAIL) values ("+signupinfomap.get("COGNITO_ID")+",true,'"+signupinfomap.get("email")+"')";
			ps.addBatch(insertquery);
			insertquery = "insert into ORG_Users (USERID,ORGID,INVITEDTIME,ISDEFAULT,INVITATION_ACCEPT_STATUS) values ((select USERID from Users where EMAIL='"+signupinfomap.get("email")+"'),(select ORGID from Organizations where FACILIODOMAINNAME='"+signupinfomap.get("domainname")+"'),UNIX_TIMESTAMP() ,true,true)";
			System.out.println("insert query "+insertquery);
			ps.addBatch(insertquery);
			ps.executeBatch();
			
			rs = ps.getGeneratedKeys();
			
			rs.next();
			long orgId = rs.getLong(1);
			arg0.put(ORG_ID, orgId);
			
			while(rs.next()) {
				System.out.println(rs.getLong(1));
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			DBUtil.closeAll(ps, rs);
			// it will get closed after chain completion
			//con.close();
		}
		return false;
	}

}
