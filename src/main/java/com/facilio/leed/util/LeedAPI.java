package com.facilio.leed.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.leed.constants.LeedConstants;
import com.facilio.leed.context.ArcContext;
import com.facilio.leed.context.FuelContext;
import com.facilio.leed.context.LeedConfigurationContext;
import com.facilio.leed.context.LeedEnergyMeterContext;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.transaction.FacilioConnectionPool;
import org.apache.log4j.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class LeedAPI {

	private static org.apache.log4j.Logger log = LogManager.getLogger(LeedAPI.class.getName());

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
		
		InsertRecordBuilder<LeedConfigurationContext> insertBuilder = new InsertRecordBuilder<LeedConfigurationContext>()
																			.fields(fields)
																			.table(module.getTableName())
																			.moduleName(module.getName());
		insertBuilder.addRecords(leedConfigList);
		insertBuilder.save();
	}
	
	public static long addLeedConfiguration(LeedConfigurationContext leedConfig) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule("leedconfiguration");
		List<FacilioField> fields = modBean.getAllFields("leedconfiguration");
		
		InsertRecordBuilder<LeedConfigurationContext> insertBuilder = new InsertRecordBuilder<LeedConfigurationContext>()
																			.fields(fields)
																			.table(module.getTableName())
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
	
	
	
//	public static String DeleteMetersForArcSync(long buildingId) throws SQLException, RuntimeException
//	{
//		List<Long> deviceIds = getDeviceIdsToDelete(buildingId);
//		
//		
//	}
//	
//	public JSONArray getConsumptionIdForDevices(List<Long> deviceIds) throws SQLException, RuntimeException
//	{
//		JSONArray arr = new JSONArray();
//		
//		Iterator itr = deviceIds.iterator(); 
//		while(itr.hasNext())
//		{
//			Long deviceId = (Long)itr.next();
//			
//		}
//		
//	}
	
	public static List<Long> getDeviceIdsToDelete(long buildingId) throws SQLException, RuntimeException
	{
		List<Long> deviceIds = new ArrayList();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT ID FROM Assets where SPACE_ID = ?");
			pstmt.setLong(1, buildingId);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				deviceIds.add(rs.getLong("ID"));
			}			
		}catch(SQLException | RuntimeException e)
		{
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		
		return deviceIds;
	}
	


	
	public static List<LeedEnergyMeterContext> fetchMeterListForBuilding(long buildingId,String meterType) throws SQLException, RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<LeedEnergyMeterContext> meterList = new ArrayList();
		
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT Assets.Name,LeedEnergyMeter.ID,LeedEnergyMeter.METERID,LeedEnergyMeter.SERVICEPROVIDER,LeedEnergyMeter.UNIT,LeedEnergyMeter.CONTACTPERSON,LeedEnergyMeter.CONTACTEMAIL,FuelType.FUELID,FuelType.FUELTYPE,FuelType.SUBTYPE,FuelType.KIND,FuelType.RESOURCE FROM LeedEnergyMeter ")
				.append(" INNER JOIN Energy_Meter ON LeedEnergyMeter.ID = Energy_Meter.ID")
				.append(" INNER JOIN Assets ON Energy_Meter.ID = Assets.ID")
				.append(" INNER JOIN FuelType ON FuelType.ID = LeedEnergyMeter.FUEL_ID")
				.append(" WHERE Assets.SPACE_ID = ? AND FuelType.KIND = ?");		
			pstmt =  conn.prepareStatement(sql.toString());
			pstmt.setLong(1,buildingId);
			pstmt.setString(2, meterType);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				LeedEnergyMeterContext context = new LeedEnergyMeterContext();
				context.setName(rs.getString("NAME"));
				context.setMeterId(rs.getLong("METERID"));
				context.setId(rs.getLong("ID"));
				context.setServiceProvider(rs.getString("SERVICEPROVIDER"));
				context.setUnit(rs.getString("UNIT"));
				context.setContactPerson(rs.getString("CONTACTPERSON"));
				context.setContactPerson(rs.getString("CONTACTEMAIL"));
				
				FuelContext fcontext = new FuelContext();
				fcontext.setFuelId(rs.getLong("FUELID"));
				fcontext.setFuelType(rs.getString("FUELTYPE"));
				fcontext.setKind(rs.getString("KIND"));
				fcontext.setSubType(rs.getString("SUBTYPE"));
				fcontext.setResource(rs.getString("RESOURCE"));
				context.setFuelContext(fcontext);
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
	
	
	public static void addLeedEnergyMeters(List<LeedEnergyMeterContext> meterList,long buildingId) throws SQLException, RuntimeException 
	{
		//long buildingId = (long)context.get(FacilioConstants.ContextNames.BUILDINGID);
		//long spaceId = getSpaceId(buildingId);
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		Iterator itr = meterList.iterator();
		while(itr.hasNext())
		{
			LeedEnergyMeterContext meter = (LeedEnergyMeterContext)itr.next();		
	
			FuelContext fcontext = meter.getFuelContext();
			long fuelId = checkIfFuelContextExists(fcontext.getFuelId());
			if(fuelId == -1)
			{
				fuelId = addFuelContext(fcontext);
			}
			long assetModuleId = getModuleId(orgId,"asset");
			String metername = meter.getName();
			long assetId = addAsset(metername,orgId,buildingId,assetModuleId);
			meter.setId(assetId);
			long energymeterModuleId = getModuleId(orgId,"energymeter");
			long purposeId = getPurposeId("Main");
			addEnergyMeter(assetId,orgId,energymeterModuleId,purposeId,buildingId);
			
			addLeedEnergyMeter(assetId,fuelId,meter.getMeterId(), meter.getServiceProvider(), meter.getUnit(),meter.getContactPerson(), meter.getContactEmail());
		}	
	}
	
	public static void addLeedEnergyMeter(FacilioContext context) throws SQLException, RuntimeException 
	{
		LeedEnergyMeterContext meter = (LeedEnergyMeterContext)context.get(LeedConstants.ContextNames.LEEDMETERCONTEXT);
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		
		FuelContext fcontext = meter.getFuelContext();
		long fuelId = checkIfFuelContextExists(fcontext.getFuelId());
		if(fuelId == -1)
		{
			fuelId = addFuelContext(fcontext);
		}
		
		long assetModuleId = getModuleId(orgId,"asset");
		String metername = meter.getName();
		
		long buildingId = (long)context.get(FacilioConstants.ContextNames.BUILDINGID);
		long assetId = addAsset(metername,orgId,buildingId,assetModuleId);

		long energymeterModuleId = getModuleId(orgId,"energymeter");
		long purposeId = getPurposeId("Main");
		addEnergyMeter(assetId,orgId,energymeterModuleId,purposeId,buildingId);
		
		addLeedEnergyMeter(assetId,fuelId,meter.getMeterId(), meter.getServiceProvider(), meter.getUnit(),meter.getContactPerson(), meter.getContactEmail());
	}
	
	public static long getPurposeId(String purposeName) throws SQLException, RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		long purposeId = -1;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT ID FROM Energy_Meter_Purpose where NAME = ?");
			pstmt.setString(1,purposeName);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				purposeId = rs.getLong("ID");
			}			
		}catch(SQLException | RuntimeException e)
		{
			log.info("Exception occurred ", e);
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return purposeId;
	}
	
	public static void addEnergyMeter(long assetId,long orgId,long energymeterModuleId,long purposeId,long buildingId) throws SQLException , RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Energy_Meter(ID,ORGID,MODULEID,IS_ROOT,PURPOSE_SPACE_ID) VALUES(?,?,?,?,?)");
			pstmt.setLong(1, assetId);
			pstmt.setLong(2, orgId);
			pstmt.setLong(3, energymeterModuleId);
			pstmt.setBoolean(4, true);
			pstmt.setLong(5, buildingId);
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
	
	public static long checkIfFuelContextExists(long fuelId) throws SQLException, RuntimeException
	{
		long id = -1;
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("select ID from FuelType where FUELID = ?");
			pstmt.setLong(1, fuelId);
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				id = rs.getLong("ID");
			}

		}catch(Exception e)
		{
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return id;
	}
	
	public static long addFuelContext(FuelContext fcontext) throws SQLException, RuntimeException
	{
		long fuelId = fcontext.getFuelId();
		String fuelType = fcontext.getFuelType();
		String subType = fcontext.getSubType();
		String kind = fcontext.getKind();
		String resource = fcontext.getResource();
		long fuel_Id = -1;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			

			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO FuelType(FUELID,FUELTYPE,SUBTYPE,KIND,RESOURCE) VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			pstmt.setLong(1, fuelId);
			pstmt.setString(2, fuelType);
			pstmt.setString(3, subType);
			pstmt.setString(4, kind);
			pstmt.setString(5, resource);
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to add Asset instance while adding LeedEnergyMeter");
			}
			else 
			{
				rs = pstmt.getGeneratedKeys();
				rs.next();
				fuel_Id = rs.getLong(1);
			}
		}catch(SQLException | RuntimeException e)
		{
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return fuel_Id;
	}
		

	public static long getLeedId(long buildingId) throws SQLException, RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		long leedId = -1;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT LEEDID FROM LeedConfiguration where ID = ?");
			pstmt.setLong(1, buildingId);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				leedId = rs.getLong("LEEDID");
			}			
		}catch(SQLException | RuntimeException e)
		{
			log.info("Exception occurred ", e);
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return leedId;
	}
	
	public static void updateLeedScores(long leedId, long totalScore, long energyScore, long waterScore, long wasteScore, long transportScore, long humanExperienceScore) throws SQLException, RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE LeedConfiguration set LEEDSCORE  = ? , ENERGYSCORE = ?, WATERSCORE = ?, WASTESCORE = ?, TRANSPORTSCORE = ?, HUMANEXPERIENCESCORE = ?  WHERE LEEDID = ?;");
			pstmt.setLong(1, totalScore);
			pstmt.setLong(2, energyScore);
			pstmt.setLong(3, waterScore);
			pstmt.setLong(4, wasteScore);
			pstmt.setLong(5, transportScore);
			pstmt.setLong(6, humanExperienceScore);
			pstmt.setLong(7, leedId);
			pstmt.executeUpdate();
					
		}catch(SQLException | RuntimeException e)
		{
			log.info("Exception occurred ", e);
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
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
	
	public static long getModuleId(long orgId,String moduleName) throws SQLException,RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		long moduleId = -1;	
		
		try{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT MODULEID FROM Modules where ORGID = ? and NAME = ?");
			pstmt.setLong(1, orgId);
			pstmt.setString(2, moduleName);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				moduleId = rs.getLong("MODULEID");
			}
		}catch(SQLException | RuntimeException e)
		{
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return moduleId;
	}
	
	public static long addAsset(String name, long orgId,long buildingId,long moduleId) throws SQLException,RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		long assetId;	
		
		try{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Assets(ORGID,MODULEID,NAME,SPACE_ID) VALUES(?,?,?,?)",Statement.RETURN_GENERATED_KEYS); 
			pstmt.setLong(1, orgId);
			pstmt.setLong(2, moduleId);
			pstmt.setString(3, name);
			pstmt.setLong(4, buildingId);
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
	
	public static void addDevice(long deviceId, long buildingId,String meterType) throws SQLException,RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Device(DEVICE_ID,SPACE_ID,DEVICE_TYPE,STATUS) values(?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
			pstmt.setLong(1,deviceId);
			pstmt.setLong(2, buildingId);
			pstmt.setString(3,meterType);
			pstmt.setInt(4, 1);
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
	
//	CREATE TABLE IF NOT EXISTS LeedEnergyMeter (
//			ID BIGINT PRIMARY KEY,
//			FUEL_ID BIGINT,
//		  	METERID BIGINT,
//		  	SERVICEPROVIDER VARCHAR(50),
//		  	UNIT VARCHAR(50),
//		  	CONTACTPERSON VARCHAR(50),
//		  	CONTACTEMAIL VARCHAR(50),
//		  	CONSTRAINT LEEDENERGYMETER_ENERGYMETER_FK FOREIGN KEY (ID) REFERENCES Energy_Meter(ID),
//		  	CONSTRAINT LEEDENERGYMETER_FUELTYPE_FK FOREIGN KEY (FUELCONTEXT_ID) REFERENCES FuelType(ID)
//		);
	
	
	public static void addLeedEnergyMeter(long Id,long fuelId,long meterId, String serviceprovider, String unit, String contactPerson, String contatEmail) throws SQLException, RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO LeedEnergyMeter(ID,FUEL_ID,METERID,SERVICEPROVIDER,UNIT,CONTACTPERSON,CONTACTEMAIL) VALUES(?,?,?,?,?,?,?)");
			pstmt.setLong(1,Id);
			pstmt.setLong(2, fuelId);
			pstmt.setLong(3, meterId);
			pstmt.setString(4, serviceprovider);
			pstmt.setString(5, unit);
			pstmt.setString(6, contactPerson);
			pstmt.setString(7, contatEmail);
			pstmt.executeUpdate();
			
		}
		catch(SQLException  | RuntimeException e)
		{
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt);
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
			pstmt =  conn.prepareStatement("select METERID from LeedEnergyMeter where ID = ?");
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
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		long moduleId =  getModuleId(orgId,"energydata"); 
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Energy_Data (ORGID,MODULEID,PARENT_METER_ID, TTIME, TOTAL_ENERGY_CONSUMPTION_DELTA, TTIME_DATE, TTIME_MONTH, TTIME_WEEK, TTIME_DAY, TTIME_HOUR) VALUES (?,?,?, ?, ?, ?, ?, ?, ?, ?)",Statement.RETURN_GENERATED_KEYS);
			Iterator itr = dataMapList.iterator();
			while(itr.hasNext())
			{
				HashMap dataMap = (HashMap)itr.next();
				pstmt.setLong(1, orgId);
				pstmt.setLong(2, moduleId);
				pstmt.setLong(3, (long)dataMap.get("id"));
				pstmt.setLong(4, (long)dataMap.get("endTime"));
				pstmt.setDouble(5, (double)dataMap.get("consumption"));
				
/*				dataMap.put("id", id);
				dataMap.put("endTime", enDateLong);
				dataMap.put("consumption", consumption);
				dataMap.put("timeData", endTimeData);
				dataMap.put("consumptionId", consumptionId);
				dataMap.put("startTime", stDateLong);
				dataMapList.add(dataMap);*/
				
				HashMap<String, Object> timeData = (HashMap<String, Object>)dataMap.get("timeData");
				String added_date = (String)timeData.get("date");
				int added_month = (int)timeData.get("month");
				int added_week = (int)timeData.get("week");
				int added_day = (int)timeData.get("day");
				int added_hour = (int)timeData.get("hour");
				
				pstmt.setObject(6, added_date);
				pstmt.setObject(7, added_month);
				pstmt.setObject(8, added_week);
				pstmt.setObject(9, added_day);
				pstmt.setObject(10, added_hour);
				pstmt.addBatch();				
			}
			pstmt.executeBatch();
			rs = pstmt.getGeneratedKeys();
			int loopcount = 0;
			pstmt = conn.prepareStatement("INSERT INTO ConsumptionInfo(ID, CONSUMPTIONID, STARTDATE) VALUES(?, ?, ?) ");
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
	
	public static JSONArray getConsumptionData(long deviceId) throws SQLException , RuntimeException 
	{
		new JSONObject();
		JSONArray arr = new JSONArray();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("select TTIME_MONTH,SUM(TOTAL_ENERGY_CONSUMPTION_DELTA) from Energy_Data where PARENT_METER_ID = ? GROUP BY TTIME_MONTH;");
			pstmt.setLong(1,deviceId);
			rs = pstmt.executeQuery();
			
			while(rs.next())
			{
				JSONObject obj = new JSONObject();
				obj.put("Month", rs.getInt("TTIME_MONTH") );
				obj.put("Consumption",  rs.getDouble("SUM(TOTAL_ENERGY_CONSUMPTION_DELTA)"));
				arr.add(obj);;
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
		return arr;
	}
	
	public static void UpdateArcCredential(ArcContext context) throws SQLException, RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		long orgId = AccountUtil.getCurrentOrg().getOrgId(); 
		try{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE ArcCredential SET AUTHKEY = ? , AUTHUPDATETIME = ? WHERE ORGID= ?;");
			pstmt.setString(1, context.getAuthKey());
			pstmt.setLong(2, System.currentTimeMillis());			
			pstmt.setLong(3, orgId);

			pstmt.executeUpdate();
		
		}catch(SQLException | RuntimeException e)
		{
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt);
		}		
	}
	
	public static void AddArcCredential(ArcContext context) throws SQLException, RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		long orgId = AccountUtil.getCurrentOrg().getOrgId(); 
		try{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO ArcCredential VALUES(?,?,?,?,?,?,?,?,?)");
			pstmt.setLong(1, orgId);
			pstmt.setString(2,context.getUserName());
			pstmt.setString(3, context.getPassword());
			pstmt.setString(4, context.getSubscriptionKey());
			pstmt.setString(5, context.getAuthKey());
			pstmt.setLong(6, System.currentTimeMillis());
			pstmt.setString(7, context.getArcProtocol());
			pstmt.setString(8, context.getArcHost());
			pstmt.setString(9, context.getArcPort());
			pstmt.executeUpdate();
		
		}catch(SQLException | RuntimeException e)
		{
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt);
		}		
	}
	
	public static ArcContext getArcContext() throws SQLException, RuntimeException
	{
		ArcContext context = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("select * from ArcCredential where ORGID = ?");
			pstmt.setLong(1, orgId);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				context = new ArcContext();
				context.setArcProtocol(rs.getString("ARCPROTOCOL"));
				context.setArcHost(rs.getString("ARCHOST"));
				context.setArcPort(rs.getString("ARCPORT"));
				context.setAuthKey(rs.getString("AUTHKEY"));
				context.setSubscriptionKey(rs.getString("SUBSCRIPTIONKEY"));
				context.setAuthUpdateTime(rs.getLong("AUTHUPDATETIME"));
				context.setUserName(rs.getString("USERNAME"));
				context.setPassword(rs.getString("PASSWORD"));
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
		return context;
	}
	
	public static boolean checkIfLoginPresent(long orgId) throws SQLException, RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean loginRequired = false;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT ORG.ORGID, AC.USERNAME, AC.AUTHKEY FROM ArcCredential AS AC ,Organizations AS ORG WHERE ORG.ORGID = AC.ORGID AND ORG.ORGID = ?;");
			pstmt.setLong(1,orgId);
			rs = pstmt.executeQuery();			
			if(!rs.next())
			{
				loginRequired = true;
			}
		
		}catch(Exception e)
		{
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return loginRequired;
	}
	
}
