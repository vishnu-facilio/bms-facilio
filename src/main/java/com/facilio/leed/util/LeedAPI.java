package com.facilio.leed.util;

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

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.leed.context.LeedConfigurationContext;
import com.facilio.leed.context.LeedEnergyMeterContext;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class LeedAPI {

	public static List<LeedConfigurationContext> getLeedConfigurationList(long orgId) throws SQLException, RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<LeedConfigurationContext> leedList = new ArrayList();
		
		try{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("select * from LeedConfiguration ,Building where LeedConfiguration.BUILDINGID = Building.ID and Building.ORGID = ?");
			pstmt.setLong(1,orgId);
			
			while(rs.next())
			{
				LeedConfigurationContext context = new LeedConfigurationContext();
				context.setId(rs.getLong("ID"));
				context.setName(rs.getString("NAME"));
				context.setLeedId(rs.getLong("LEEDID"));
				context.setBuildingStatus(rs.getString("BUILDINGSTATUS"));
				Long energyScore = rs.getLong("ENERGYSCORE");
				if(energyScore == null)
				{
					energyScore = 0L;
				}
				context.setEnergyScore(energyScore);
				Long waterScore = rs.getLong("WATERSCORE");
				if(waterScore == null)
				{
					waterScore = 0L;
				}
				context.setWaterScore(waterScore);
				Long wasteScore = rs.getLong("WASTESCORE");
				if(wasteScore == null)
				{
					wasteScore = 0L;
				}
				context.setWasteScore(wasteScore);
				Long humanExperienceScore = rs.getLong("HUMANEXPERIENCESCORE");
				if(humanExperienceScore == null)
				{
					humanExperienceScore = 0L;
				}
				context.setHumanExperienceScore(humanExperienceScore);
				Long transportScore = rs.getLong("TRANSPORTSCORE");
				if(transportScore == null)
				{
					transportScore = 0L;
				}
				context.setTransportScore(transportScore);
				leedList.add(context);				
			}
			
		}catch(SQLException | RuntimeException e)
		{
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return leedList;
	}
	
	public static List<LeedEnergyMeterContext> fetchMeterListForBuilding(long buildingId) throws SQLException, RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<LeedEnergyMeterContext> meterList = new ArrayList();
		
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT Assets.Name,LeedEnergyMeter.DEVICEID,LeedEnergyMeter.METERID FROM LeedEnergyMeter LEFT JOIN Device ON LeedEnergyMeter.DEVICEID = Device.DEVICE_ID")
				.append("LEFT JOIN Assets ON Device.DEVICE_ID = Assets.ASSETID")
				.append("LEFT JOIN Building ON Device.SPACE_ID = Building.BASE_SPACE_ID")
				.append("WHERE Building.ID = ?");		
			pstmt =  conn.prepareStatement(sql.toString());
			pstmt.setLong(1,buildingId);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				LeedEnergyMeterContext context = new LeedEnergyMeterContext();
				context.setName(rs.getString("NAME"));
				context.setMeterId(rs.getLong("METERID"));
				context.setDeviceId(rs.getLong("DEVICEID"));
				meterList.add(context);
			}
			
		}catch(SQLException | RuntimeException e)
		{
			throw e;
		}
		
		return meterList;
	}
	
	public static void addLeedEnergyMeter(FacilioContext context) throws SQLException, RuntimeException 
	{
		long buildingId = (long)context.get(FacilioConstants.ContextNames.BUILDINGID);
		long spaceId = getSpaceId(buildingId);
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		String metername = (String)context.get(FacilioConstants.ContextNames.METERNAME);
		long fuelType = (long)context.get(FacilioConstants.ContextNames.FUELTYPE);
		long meterId = (long)context.get(FacilioConstants.ContextNames.METERID);
		long assetId = addAsset(metername,orgId);
		addDevice(assetId, spaceId);
		addLeedEnergyMeter(assetId,fuelType,meterId);
		
	}
	
	public static long getSpaceId(long buildingId) throws  SQLException, RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		long spaceId = 0L;
		try{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT BASE_SPACE_ID FROM Building where ID = ?");
			pstmt.setLong(1, buildingId);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				spaceId = rs.getLong("BASE_SPACE_ID");
			}
		}catch(SQLException | RuntimeException e)
		{
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return spaceId;
	}
	
	public static long addAsset(String name, long orgId) throws SQLException,RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		long assetId;	
		
		try{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Assets(NAME,ORGID) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);
			pstmt.setString(1, name);
			pstmt.setLong(2, orgId);
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to add Asset instance while adding LeedEnergyMeter");
			}
			else 
			{
				rs = pstmt.getGeneratedKeys();
				rs.next();
				assetId = rs.getLong(1);
			}
		}catch(SQLException | RuntimeException e)
		{
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return assetId;
	}
	
	public static void addDevice(long deviceId, long spaceId) throws SQLException,RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Device(DEVICE_ID,SPACE_ID,STATUS) values(?,?,?)",Statement.RETURN_GENERATED_KEYS);
			pstmt.setLong(1,deviceId);
			pstmt.setLong(2, spaceId);
			pstmt.setInt(3, 1);
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to add controller");
			}
		}
		catch(SQLException | RuntimeException e)
		{
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		
	}
	
	
	public static void addLeedEnergyMeter(long deviceId,long fuelType,long meterId) throws SQLException, RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO LeedEnergyMeter(DEVICEID,FUELTYPE,METERID) VALUES(?,?,?))", Statement.RETURN_GENERATED_KEYS);
			pstmt.setLong(1,deviceId);
			pstmt.setLong(2, fuelType);
			pstmt.setLong(3, meterId);
			
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to add UtilityProvider instance");
			}
		}
		catch(SQLException  | RuntimeException e)
		{
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}

	}
	
	public static void addEnergyData(long deviceId,long added_time, double kwh_delta,HashMap<String,Object> timeData) throws SQLException 
	{
		/*
		 * ADDED_DATE DATE,
		ADDED_MONTH INT,
		ADDED_WEEK INT,
		ADDED_DAY VARCHAR(100),
		ADDED_HOUR INT,
	
		columnVals.put("date", date);
		columnVals.put("month", month);
		columnVals.put("week", week);
		columnVals.put("day", day);
		columnVals.put("hour",hour);
		columnVals.put("year", year);
		 */
		String added_date = (String)timeData.get("date");
		int added_month = (int)timeData.get("month");
		int added_week = (int)timeData.get("week");
		String added_day = (String)timeData.get("day");
		int added_hour = (int)timeData.get("hour");
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		Map<Long, String> utilityproviders = new LinkedHashMap<>();
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Energy_Data (DEVICE_ID, ADDED_TIME, TOTAL_ENERGY_CONSUMPTION_DELTA, ADDED_DATE, ADDED_MONTH, ADDED_WEEK, ADDED_DAY, ADDED_HOUR) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setLong(1, deviceId);
			pstmt.setLong(2, added_time);
			pstmt.setDouble(3, kwh_delta);
			pstmt.setObject(4, added_date);
			pstmt.setObject(5, added_month);
			pstmt.setObject(6, added_week);
			pstmt.setObject(7, added_day);
			pstmt.setObject(8, added_hour);
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			long id = 0;
			if(rs.next())
			{
				id = rs.getLong(1);
			}				
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
		
	}

	
	
}
