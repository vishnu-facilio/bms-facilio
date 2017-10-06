package com.facilio.leed.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.facilio.leed.context.UtilityProviderContext;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class LeedAPI {

	public static void addConsumptionData() throws SQLException
	{
		
	}
	
	public static Map<Long, String> getUtilityProviders(long orgId) throws SQLException 
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		Map<Long, String> utilityproviders = new LinkedHashMap<>();
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM UtilityProvider WHERE ORGID=? ORDER BY NAME");
			
			pstmt.setLong(1, orgId);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				utilityproviders.put(rs.getLong("ID"), rs.getString("NAME"));
			}
			
			return utilityproviders;
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
		
	}
	 
	public static List<UtilityProviderContext> getAllUtilityProviders(Long buildingId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT UP.PROVIDER_ID,UP.ORGID,UP.NAME,UP.DISPLAYNAME,UP.COUNTRY FROM UtilityProvider as UP, Building_Provider as BP WHERE UP.PROVIDER_ID = BP.PROVIDER_ID AND BP.BUILDING_ID=? ORDER BY UP.NAME");
			pstmt.setLong(1, buildingId);
			
			List<UtilityProviderContext> utilityproviders = new ArrayList<>();
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				UtilityProviderContext upc = getUtilityProviderObjectFromRS(rs);
				utilityproviders.add(upc);
			}
			
			return utilityproviders;
			
		}catch(SQLException e)
		{
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static List<UtilityProviderContext> getAllUtilityProviders(long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM UtilityProvider WHERE ORGID=? ORDER BY NAME");
			pstmt.setLong(1, orgId);
			
			List<UtilityProviderContext> utilityproviders = new ArrayList<>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				UtilityProviderContext upc = getUtilityProviderObjectFromRS(rs);
				utilityproviders.add(upc);
			}
			
			return utilityproviders;
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static UtilityProviderContext getUtilityProviderObjectFromRS(ResultSet rs)  throws SQLException 
	{
		UtilityProviderContext upc = new UtilityProviderContext();
		upc.setProviderId(rs.getLong("PROVIDER_ID"));
		upc.setOrgId(rs.getLong("ORGID"));
		upc.setName(rs.getString("NAME"));
		upc.setDisplayName(rs.getString("DISPLAYNAME"));
		upc.setCountry(rs.getString("COUNTRY"));
		return upc;
	}
}
