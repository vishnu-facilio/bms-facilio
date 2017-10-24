package com.facilio.leed.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.leed.context.LeedConfigurationContext;
import com.facilio.leed.context.LeedEnergyMeterContext;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class LeedAPI {

	public static List<LeedConfigurationContext> getLeedConfigurationList(long orgId) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("leedconfiguration");
		List<FacilioField> fields = modBean.getAllFields("leedconfiguration");
		
		SelectRecordsBuilder<LeedConfigurationContext> leedBuilder = new SelectRecordsBuilder<LeedConfigurationContext>()
																			.select(fields)
																			.table(module.getTableName())
																			.beanClass(LeedConfigurationContext.class)
																			.moduleName(module.getName());
		
		return leedBuilder.get();
	}
	
	public static void addLeedConfigurations(List<LeedConfigurationContext> leedConfigList) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("leedconfiguration");
		List<FacilioField> fields = modBean.getAllFields("leedconfiguration");
		Iterator itr = leedConfigList.iterator();
		while(itr.hasNext())
		{
			LeedConfigurationContext leedConfig = (LeedConfigurationContext)itr.next();
			InsertRecordBuilder<LeedConfigurationContext> insertBuilder = new InsertRecordBuilder<LeedConfigurationContext>()
																			.fields(fields)
																			.dataTableName(module.getTableName())
																			.moduleName(module.getName());
		
			insertBuilder.insert(leedConfig);
		}
	}
	
	public static long addLeedConfiguration(LeedConfigurationContext leedConfig) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("leedconfiguration");
		List<FacilioField> fields = modBean.getAllFields("leedconfiguration");
		
		InsertRecordBuilder<LeedConfigurationContext> insertBuilder = new InsertRecordBuilder<LeedConfigurationContext>()
																			.fields(fields)
																			.dataTableName(module.getTableName())
																			.moduleName(module.getName())
																			.level(3);
		
		return insertBuilder.insert(leedConfig);
		
	}
	
	public static LeedConfigurationContext fetchLeedConfigurationContext(long buildingId) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("leedconfiguration");
		List<FacilioField> fields = modBean.getAllFields("leedconfiguration");
		
		SelectRecordsBuilder<LeedConfigurationContext> leedBuilder = new SelectRecordsBuilder<LeedConfigurationContext>()
																			.select(fields)
																			.table(module.getTableName())
																			.beanClass(LeedConfigurationContext.class)
																			.moduleName(module.getName())
																			.andCustomWhere(module.getTableName()+".ID = ?", buildingId);
		
		List<LeedConfigurationContext> leeds = leedBuilder.get();
		if(leeds != null && !leeds.isEmpty()) {
			return leeds.get(0);
		}
		return null;
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
			sql.append("SELECT Assets.Name,LeedEnergyMeter.DEVICE_ID,LeedEnergyMeter.METERID FROM LeedEnergyMeter LEFT JOIN Device ON LeedEnergyMeter.DEVICE_ID = Device.DEVICE_ID")
				.append(" LEFT JOIN Assets ON Device.DEVICE_ID = Assets.ASSETID")
				.append(" WHERE Device.SPACE_ID = ?");		
			pstmt =  conn.prepareStatement(sql.toString());
			pstmt.setLong(1,buildingId);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				LeedEnergyMeterContext context = new LeedEnergyMeterContext();
				context.setName(rs.getString("NAME"));
				context.setMeterId(rs.getLong("METERID"));
				context.setDeviceId(rs.getLong("DEVICE_ID"));
				meterList.add(context);
			}
			
		}catch(SQLException | RuntimeException e)
		{
			throw e;
		}
		finally
		{
			System.out.println(meterList);
			DBUtil.closeAll(conn, pstmt, rs);
			
		}
		return meterList;
	}
	
	public static void addLeedEnergyMeter(FacilioContext context) throws SQLException, RuntimeException 
	{
		long buildingId = (long)context.get(FacilioConstants.ContextNames.BUILDINGID);
		//long spaceId = getSpaceId(buildingId);
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		String metername = (String)context.get(FacilioConstants.ContextNames.METERNAME);
		long fuelType = (long)context.get(FacilioConstants.ContextNames.FUELTYPE);
		long meterId = (long)context.get(FacilioConstants.ContextNames.METERID);
		long assetId = addAsset(metername,orgId);
		addDevice(assetId, buildingId);
		addLeedEnergyMeter(assetId,fuelType,meterId);
		
	}
	
//	public static long getSpaceId(long buildingId) throws  SQLException, RuntimeException
//	{
//		Connection conn = null;
//		PreparedStatement pstmt = null;
//		ResultSet rs = null;
//		long spaceId = 0L;
//		try{
//			conn = FacilioConnectionPool.INSTANCE.getConnection();
//			pstmt = conn.prepareStatement("SELECT BASE_SPACE_ID FROM Building where ID = ?");
//			pstmt.setLong(1, buildingId);
//			rs = pstmt.executeQuery();
//			while(rs.next())
//			{
//				spaceId = rs.getLong("BASE_SPACE_ID");
//			}
//		}catch(SQLException | RuntimeException e)
//		{
//			throw e;
//		}
//		finally
//		{
//			DBUtil.closeAll(conn, pstmt, rs);
//		}
//		return spaceId;
//	}
	
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
	
	public static void addDevice(long deviceId, long buildingId) throws SQLException,RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Device(DEVICE_ID,SPACE_ID,STATUS) values(?,?,?)",Statement.RETURN_GENERATED_KEYS);
			pstmt.setLong(1,deviceId);
			pstmt.setLong(2, buildingId);
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
				throw new RuntimeException("Unable to add LeedEnergyMeter instance");
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
	
	public static long getMeterId(long deviceId) throws SQLException, RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		long meterId = 0;
		try{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt =  conn.prepareStatement("select METERID from LeedEnergyMeter where DEVICEID = ?");
			pstmt.setLong(1,deviceId);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				meterId = rs.getLong("METERID");
			}
		}catch(SQLException | RuntimeException e)
		{
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		
		return meterId;
	}

	public static void addConsumptionData(List<HashMap> dataMapList) throws SQLException , RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Energy_Data (DEVICE_ID, ADDED_TIME, TOTAL_ENERGY_CONSUMPTION_DELTA, ADDED_DATE, ADDED_MONTH, ADDED_WEEK, ADDED_DAY, ADDED_HOUR) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",Statement.RETURN_GENERATED_KEYS);
			Iterator itr = dataMapList.iterator();
			while(itr.hasNext())
			{
				HashMap dataMap = (HashMap)itr.next();
				pstmt.setLong(1, (long)dataMap.get("deviceId"));
				pstmt.setLong(2, (long)dataMap.get("endTime"));
				pstmt.setDouble(3, (double)dataMap.get("consumption"));
				
				HashMap<String, Object> timeData = (HashMap<String, Object>)dataMap.get("timeData");
				String added_date = (String)timeData.get("date");
				int added_month = (int)timeData.get("month");
				int added_week = (int)timeData.get("week");
				String added_day = (String)timeData.get("day");
				int added_hour = (int)timeData.get("hour");
				
				pstmt.setObject(4, added_date);
				pstmt.setObject(5, added_month);
				pstmt.setObject(6, added_week);
				pstmt.setObject(7, added_day);
				pstmt.setObject(8, added_hour);
				pstmt.addBatch();				
			}
			pstmt.executeBatch();
			rs = pstmt.getGeneratedKeys();
			int loopcount = 0;
			pstmt = conn.prepareStatement("INSERT INTO ConsumptionInfo(ENERGYDATAID, CONSUMPTIONID, STARTDATE) VALUES(?, ?, ?) ");
			while(rs.next())
			{
				long energyId = rs.getLong(1);
				pstmt.setLong(1, energyId);
				HashMap dataMap = dataMapList.get(loopcount);
				pstmt.setLong(2, (long)dataMap.get("consumptionId"));
				pstmt.setLong(3, (long)dataMap.get("startTime"));				
				pstmt.addBatch();
				loopcount++;
			}				
			pstmt.executeBatch();
			
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
	
	public static JSONObject getConsumptionData(long deviceId) throws SQLException , RuntimeException 
	{
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("DeviceId", deviceId);
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("select ADDED_MONTH,SUM(TOTAL_ENERGY_CONSUMPTION_DELTA) from Energy_Data where DEVICE_ID = ? GROUP BY ADDED_MONTH;");
			pstmt.setLong(1,deviceId);
			rs = pstmt.executeQuery();
			JSONArray arr = new JSONArray();
			while(rs.next())
			{
				JSONObject obj = new JSONObject();
				obj.put(rs.getInt("ADDED_MONTH"), rs.getDouble("SUM(TOTAL_ENERGY_CONSUMPTION_DELTA)"));
				arr.add(obj);;
			}
			jsonObj.put("values", arr);			
		}
		catch(SQLException | RuntimeException e)
		{
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return jsonObj;
	}
	
}
