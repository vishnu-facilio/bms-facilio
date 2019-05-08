package com.facilio.billing.util;

import com.facilio.billing.context.ExcelTemplate;
import com.facilio.billing.context.Tenant;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.transaction.FacilioConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TenantBillingAPI {

	public static List<Tenant> getTenantsForOrgIdOLD(long orgId) throws SQLException, RuntimeException
	{
		List<Tenant> tenants = new ArrayList();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Tenants where OrgId = ?");
			pstmt.setLong(1, orgId);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				Tenant tenant = new Tenant();
				tenant.setId(rs.getLong("ID"));
				tenant.setName(rs.getString("NAME"));
				tenant.setAddress(rs.getString("ADDRESS"));
				tenant.setOrgId(rs.getLong("ORGID"));
				tenant.setContactEmail(rs.getString("CONTACTEMAIL"));
				tenant.setContactNumber(rs.getString("CONTACTNUMBER"));
				tenant.setTemplateId(rs.getLong("TEMPLATEID"));
				tenants.add(tenant);
			}			
		}catch(SQLException | RuntimeException e)
		{
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		
		return tenants;
	}
	
	public static List<Map<String, Object>> getTenant(long tenantId) throws Exception
	{
		List<FacilioField> fields = FieldFactory.getTenantFields();
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Tenant")
				.andCustomWhere("Tenant.ID = ?", tenantId);
		List<Map<String, Object>> pmProps = selectRecordBuilder.get();
		return pmProps;
		
	}
	
	public static void addTenant() throws Exception
	{
		Map<String, Object> prop = new HashMap<>();
		prop.put("orgId", 1);
		prop.put("name","Facilio");
		prop.put("address", "MGR Salai, Perungudi, Chennai");
		
		GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getTenantModule().getTableName())
				.fields(FieldFactory.getTenantFields())
				.addRecord(prop);															
		
		insertRecordBuilder.save();
	}
	
	public static Map<String,String> FetchPlaceHolders(long templateId) throws SQLException, RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Map<String,String> placeHolders = new HashMap();
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Excel_PlaceHolders WHERE TEMPLATEID = ?");
			pstmt.setLong(1, templateId);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				String cellfinder = rs.getString("CELLDATA");
				String meterInfo = rs.getString("PLACEHOLDERNAME");
				placeHolders.put(cellfinder, meterInfo);
			}
			
		}catch(SQLException | RuntimeException e) {
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return placeHolders;
	}
	
	public static long GetMeterId(String meterName) throws SQLException, RuntimeException
	{
		long meterId = -1;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Resources where NAME = ?");
			pstmt.setString(1, meterName);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				meterId = rs.getLong("ID");
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
	
	public static double GetMeterOpenReading(long meterId, String paramName, long startTime) throws SQLException, RuntimeException
	{
		double kwhOpenReading = 0.0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sqlStr = "";
		if(paramName.equalsIgnoreCase("kwh"))
		{
			sqlStr = "select TOTAL_ENERGY_CONSUMPTION from Energy_Data where PARENT_METER_ID = ? and TTIME >= ? ORDER BY TTIME ASC LIMIT 1";
		}
		else
		{
			sqlStr = "select "+paramName+" from Energy_Data where PARENT_METER_ID = ? and TTIME >= ? ORDER BY TTIME ASC LIMIT 1";
		}
		try {
				conn = FacilioConnectionPool.INSTANCE.getConnection();
				pstmt = conn.prepareStatement(sqlStr);
				pstmt.setLong(1, meterId);
				pstmt.setLong(2,startTime);
				rs = pstmt.executeQuery();
			while(rs.next())
			{
				kwhOpenReading = rs.getDouble(1);
			}
			
		}catch(SQLException | RuntimeException e) {
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}	
		return kwhOpenReading;
	}

	public static double GetMeterCloseReading(long meterId, String paramName, long endTime) throws SQLException, RuntimeException
	{
		double kwhOpenReading = 0.0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sqlStr = "";
		if(paramName.equalsIgnoreCase("kwh"))
		{
			sqlStr = "select TOTAL_ENERGY_CONSUMPTION from Energy_Data where PARENT_METER_ID = ? and TTIME < ?  ORDER BY TTIME DESC LIMIT 1";
		}
		else
		{
			sqlStr = "select "+paramName+" from Energy_Data where PARENT_METER_ID = ? and TTIME < ?  ORDER BY TTIME DESC LIMIT 1";
		}
		try {
				conn = FacilioConnectionPool.INSTANCE.getConnection();
				pstmt = conn.prepareStatement(sqlStr);
				pstmt.setLong(1, meterId);
				pstmt.setLong(2,endTime);
				rs = pstmt.executeQuery();
			while(rs.next())
			{
				kwhOpenReading = rs.getDouble(1);
			}
			
		}catch(SQLException | RuntimeException e) {
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}	
		return kwhOpenReading;
	}

	
	public static double GetMeterRun(long meterId, String paramName, long startTime, long endTime ) throws SQLException, RuntimeException
	{
		double kwhsum = 0.0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sqlStr = "";
		if(paramName.equalsIgnoreCase("kwh"))
		{
			sqlStr = "select SUM(TOTAL_ENERGY_CONSUMPTION_DELTA) from Energy_Data where PARENT_METER_ID = ? and TTIME >= ? and TTIME < ?";
		}
		else
		{
			sqlStr = "select SUM("+paramName+") from Energy_Data where PARENT_METER_ID = ? and TTIME >= ? and TTIME < ?";
		}
		try {
				conn = FacilioConnectionPool.INSTANCE.getConnection();
				pstmt = conn.prepareStatement(sqlStr);
				pstmt.setLong(1, meterId);
				pstmt.setLong(2,startTime);
				pstmt.setLong(3,endTime);
				rs = pstmt.executeQuery();
			while(rs.next())
			{
				kwhsum = rs.getDouble(1);
			}
			
		}catch(SQLException | RuntimeException e) {
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}	
		return kwhsum;
	}
	
	public static void InsertPlaceHolders( Map<String,String> placeHolders, long templateId) throws SQLException, RuntimeException
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Excel_PlaceHolders(TEMPLATEID,PLACEHOLDERNAME,CELLDATA) VALUES(?,?,?)");
			for(String cellfinder: placeHolders.keySet())
			{
				String meterInfo = placeHolders.get(cellfinder);
				pstmt.setLong(1, templateId);
				pstmt.setString(2,meterInfo);
				pstmt.setString(3,cellfinder);
				pstmt.addBatch();
			}
			pstmt.executeBatch();
			
		}catch(SQLException | RuntimeException e) {
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}

	
	public static Tenant getTenantOLD(long tenantId) throws SQLException, RuntimeException
	{
		Tenant tenant = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Tenants where Id = ?");
			pstmt.setLong(1, tenantId);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				tenant = new Tenant();
				tenant.setId(rs.getLong("ID"));
				tenant.setName(rs.getString("NAME"));
				tenant.setAddress(rs.getString("ADDRESS"));
				tenant.setOrgId(rs.getLong("ORGID"));
				tenant.setContactEmail(rs.getString("CONTACTEMAIL"));
				tenant.setContactNumber(rs.getString("CONTACTNUMBER"));
				tenant.setTemplateId(rs.getLong("TEMPLATEID"));
			}			
		}catch(SQLException | RuntimeException e)
		{
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		
		return tenant;
	}
	
	public static ExcelTemplate getBillTemplate(long templateId) throws SQLException, RuntimeException
	{
		ExcelTemplate excelTemplate = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Excel_Templates where Id = ?");
			pstmt.setLong(1, templateId);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				excelTemplate = new ExcelTemplate();
				excelTemplate.setId(rs.getLong("ID"));
				excelTemplate.setExcelFileId(rs.getLong("EXCEL_FILE_ID"));
			}
		}catch(SQLException | RuntimeException e)
		{
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return excelTemplate;
	}
	
//	public static ZoneContext getTenantZone() throws SQLException, RuntimeException
//	{
//		ZoneContext tenantZone = null;
//		
//		
//		
//		return tenantZone;
//	}
	
}
