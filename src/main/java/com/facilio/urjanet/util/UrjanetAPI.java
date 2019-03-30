package com.facilio.urjanet.util;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;
import com.facilio.urjanet.context.UtilityProviderCredentials;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UrjanetAPI {

	public static void main(String args[])
	{
		
	}
	
	/*ORGID BIGINT,
	USERID BIGINT,
	USERNAME TEXT,
	PASSWORD TEXT,
	PROVIDERNAME TEXT,
	PROVIDERDISPLAYNAME TEXT,
	LOGINURL TEXT,
	TEMPLATEID TEXT,
	STATUS INT,*/
	
	public static void addUtilityProviderCredentials(UtilityProviderCredentials credential) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		User loginUserObj =  AccountUtil.getCurrentUser();
		long userId = loginUserObj.getId();
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO UtilityProviders(ORGID,USERID,USERNAME,PASSWORD,PROVIDERNAME,PROVIDERDISPLAYNAME,LOGINURL,TEMPLATEID,STATUS) VALUES(?,?,?,?,?,?,?,?,?)");
			pstmt.setLong(1, orgId);
			pstmt.setLong(2, userId);
			pstmt.setString(3, credential.getUserName());
			pstmt.setString(4, credential.getPassword());
			pstmt.setString(5, credential.getUpName());
			pstmt.setString(6, credential.getUpDisplayname());
			pstmt.setString(7, credential.getUpUrl());
			pstmt.setString(8, credential.getTemplateId());
			pstmt.setLong(9,0);
			pstmt.executeUpdate();
		}catch(SQLException | RuntimeException e)
		{
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn,pstmt);
		}
	}
}
