package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class LocationAPI {
	
	public static Map<Long, String> getLocations(long orgId) throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		Map<Long, String> locations = new LinkedHashMap<>();
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Locations WHERE ORGID=? ORDER BY NAME");
			
			pstmt.setLong(1, orgId);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				locations.put(rs.getLong("ID"), rs.getString("NAME"));
			}
			
			return locations;
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static List<LocationContext> getAllLocations(long orgId) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Locations WHERE ORGID=? ORDER BY NAME");
			pstmt.setLong(1, orgId);
			
			List<LocationContext> locations = new ArrayList<>();
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				LocationContext lc = getLocationObjectFromRS(rs);
				locations.add(lc);
			}
			
			return locations;
		}
		catch(SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static long addLocation(LocationContext locationContext) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Locations (MODULEID,ORGID, NAME, STREET, CITY, STATE, ZIP, COUNTRY, LAT, LNG, CONTACT_ID, PHONE, FAX_PHONE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			long moduleId = modBean.getModule("location").getModuleId();
			
			pstmt.setLong(1, moduleId);
			pstmt.setLong(2, locationContext.getOrgId());
			pstmt.setString(3, locationContext.getName());
			pstmt.setString(4, locationContext.getStreet());
			pstmt.setString(5, locationContext.getCity());
			pstmt.setString(6, locationContext.getState());
			pstmt.setString(7, locationContext.getZip());
			pstmt.setString(8, locationContext.getCountry());
			pstmt.setDouble(9, locationContext.getLat());
			pstmt.setDouble(10, locationContext.getLng());
			
			if(locationContext.getContact() != null) {
				pstmt.setLong(11, locationContext.getContact().getId());
			}
			else {
				pstmt.setNull(11, Types.BIGINT);
			}
			
			pstmt.setString(12, locationContext.getPhone());
			pstmt.setString(13, locationContext.getFaxPhone());
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to add location");
			}
			else {
				rs = pstmt.getGeneratedKeys();
				rs.next();
				long locationId = rs.getLong(1);
				System.out.println("Added location with id : "+locationId);
				return locationId;
			}
		}
		catch(SQLException | RuntimeException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static boolean deleteLocation(long locationId, long OrgId) throws Exception
	{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("DELETE FROM LOCATIONS WHERE ID=? AND ORGID=?");
			pstmt.setLong(1, locationId);
			pstmt.setLong(2, OrgId);
			
			if (pstmt.executeUpdate() < 1) {
				return false;
			}
			return true;
			
		}catch(SQLException  | RuntimeException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static boolean updateLocation(LocationContext locationContext) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE Locations SET NAME=?, STREET=?, CITY=?, STATE=?, ZIP=?, COUNTRY=?, LAT=?, LNG=?, CONTACT_ID=?, PHONE=?, FAX_PHONE=? WHERE ID=? AND ORGID=?");
			
			pstmt.setString(1, locationContext.getName());
			pstmt.setString(2, locationContext.getStreet());
			pstmt.setString(3, locationContext.getCity());
			pstmt.setString(4, locationContext.getState());
			pstmt.setString(5, locationContext.getZip());
			pstmt.setString(6, locationContext.getCountry());
			pstmt.setDouble(7, locationContext.getLat());
			pstmt.setDouble(8, locationContext.getLng());
			
			if(locationContext.getContact() != null) {
				pstmt.setLong(9, locationContext.getContact().getId());
			}
			else {
				pstmt.setNull(9, Types.BIGINT);
			}
			
			pstmt.setString(10, locationContext.getPhone());
			pstmt.setString(11, locationContext.getFaxPhone());
			pstmt.setLong(12, locationContext.getId());
			pstmt.setLong(13, locationContext.getOrgId());
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to update location");
			}
			return true;
		}
		catch(SQLException | RuntimeException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	private static LocationContext getLocationObjectFromRS(ResultSet rs) throws Exception {
		
		LocationContext lc = new LocationContext();
		lc.setId(rs.getLong("ID"));
		lc.setOrgId(rs.getLong("ORGID"));
		lc.setName(rs.getString("NAME"));
		lc.setStreet(rs.getString("STREET"));
		lc.setCity(rs.getString("CITY"));
		lc.setState(rs.getString("STATE"));
		lc.setZip(rs.getString("ZIP"));
		lc.setCountry(rs.getString("COUNTRY"));
		lc.setLat(rs.getDouble("LAT"));
		lc.setLng(rs.getDouble("LNG"));
		
		if(rs.getLong("CONTACT_ID") != 0) {
			lc.setContact(AccountUtil.getUserBean().getUser(rs.getLong("CONTACT_ID")));
		}
		
		lc.setPhone(rs.getString("PHONE"));
		lc.setFaxPhone(rs.getString("FAX_PHONE"));
		
		return lc;
	}
	
	public static Map<Long, LocationContext> getLocationMap(Collection<Long> idList) throws Exception
	{
		if(idList == null || idList.isEmpty()) {
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.LOCATION);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.LOCATION);
		SelectRecordsBuilder<LocationContext> selectBuilder = new SelectRecordsBuilder<LocationContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(LocationContext.class)
				.andCondition(CriteriaAPI.getIdCondition(idList, module));
		return selectBuilder.getAsMap();
	}
}