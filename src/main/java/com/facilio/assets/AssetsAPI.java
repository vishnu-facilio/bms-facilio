package com.facilio.assets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class AssetsAPI {
	
	private static Logger logger = Logger.getLogger(AssetsAPI.class.getName());
	
	public static Map<Long, String> getOrgAssets(long orgId) throws SQLException 
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		Map<Long, String> orgAssets = new LinkedHashMap<>();
		
		try 
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT ASSETID, NAME FROM Assets where ORGID = ? ORDER BY NAME");
			
			pstmt.setLong(1, orgId);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				orgAssets.put(rs.getLong("ASSETID"), rs.getString("NAME"));
			}
			
			return orgAssets;
		}
		catch (SQLException e) 
		{
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static List<Map<String, String>> getAssetDetails(Long orgId) throws SQLException
	{
		List<Map<String, String>> deviceList = new ArrayList<Map<String,String>>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Assets LEFT JOIN Assets_data ON Assets.ASSETID = Assets_data.ASSETID LEFT JOIN Asset_Type ON Assets.ASSET_TYPE = Asset_Type.ASSET_TYPE where ORGID = ?");
			pstmt.setLong(1, 1);
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				Map<String, String> devices = new HashMap<>();
				devices.put("id", rs.getString("ASSETID"));
				devices.put("name", rs.getString("NAME"));
				devices.put("polltime", rs.getString("POLLTIME"));
				devices.put("type", rs.getString("DISPLAYNAME"));
				deviceList.add(devices);
			}
		}
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting asset details" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return deviceList;
	}
	
	public static Long getAssetTypeId(String name) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Long assetTypeId = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT ASSET_TYPE, NAME FROM Asset_Type where NAME = ?");
			pstmt.setString(1, name);
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				assetTypeId = rs.getLong("ASSET_TYPE");
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting asset type id" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return assetTypeId;
	}
	
	public static Long addAssetType(String name, String displayName) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Long assetTypeId = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Asset_Type (NAME, DISPLAYNAME) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, name);
			pstmt.setString(2, displayName);
			
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to add asset type");
			}
			else 
			{
				rs = pstmt.getGeneratedKeys();
				rs.next();
				assetTypeId = rs.getLong(1);
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			logger.log(Level.SEVERE, "Exception while adding asset type" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return assetTypeId;
	}
	
	public static Long addAsset(String name, Long assetTypeId, String publicip) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Long assetId = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Assets (NAME, ASSET_TYPE, CERTIFICATE, ORGID) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, name);
			pstmt.setLong(2, assetTypeId);
			pstmt.setString(3, publicip);
			pstmt.setLong(4, 1L);
			
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to add asset");
			}
			else 
			{
				rs = pstmt.getGeneratedKeys();
				rs.next();
				assetId = rs.getLong(1);
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			logger.log(Level.SEVERE, "Exception while adding asset" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return assetId;
	}
	
	public static void addAssetData(Long assetId, Long polltime) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Assets_Data (ASSETID, POLLTIME) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
			pstmt.setLong(1, assetId);
			pstmt.setLong(2, polltime);
			
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to add asset data");
			}
		}
		catch(SQLException | RuntimeException e) 
		{
			logger.log(Level.SEVERE, "Exception while adding device" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
}
