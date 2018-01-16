package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class LicenseCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		
		JSONObject signupInfo = (JSONObject) context.get(FacilioConstants.ContextNames.SIGNUP_INFO);
		long orgId = (long) context.get("orgId");
		boolean licensed = checkIfLicenseDefinedAlready(orgId);
		if(!licensed)
		{
			String defaultLicenseModules = "1111";
			
			addFeatureLicense(orgId,Integer.parseInt(defaultLicenseModules,2));
		}
		
		return false;
	}
	
	public void addFeatureLicense(long orgId,long license)
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO FeatureLicense(ORGID,MODULE) VALUES(?,?)");
			pstmt.setLong(1, orgId);
			pstmt.setLong(2, license);
			
			pstmt.executeUpdate();
						
		}catch(SQLException | RuntimeException e)
		{
			e.printStackTrace();
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt);
		}		
	}
	
	public boolean checkIfLicenseDefinedAlready(long orgId)
	{
		boolean licensed  = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT MODULE FROM FeatureLicense where ORGID = ?");
			pstmt.setLong(1, orgId);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				int licenseType = rs.getInt("MODULE");
				System.out.println(">>>>> LicenseType :"+licenseType);
				System.out.println("##### binaryLicense :"+Integer.toBinaryString(licenseType));
				licensed = true;
			}			
		}catch(SQLException | RuntimeException e)
		{
			e.printStackTrace();
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		
		
		return licensed;
	}
}
