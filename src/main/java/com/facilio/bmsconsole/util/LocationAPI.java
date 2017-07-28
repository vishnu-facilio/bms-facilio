package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.LocationContext;
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
				locations.put(rs.getLong("LOCATION_ID"), rs.getString("NAME"));
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
	
	public static List<LocationContext> getAllLocations(long orgId) throws SQLException {
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
			pstmt = conn.prepareStatement("INSERT INTO Locations (ORGID, NAME, STREET, CITY, STATE, ZIP, COUNTRY, LAT, LNG, CONTACT, PHONE, FAX_PHONE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setLong(1, locationContext.getOrgId());
			pstmt.setString(2, locationContext.getName());
			pstmt.setString(3, locationContext.getStreet());
			pstmt.setString(4, locationContext.getCity());
			pstmt.setString(5, locationContext.getState());
			pstmt.setString(6, locationContext.getZip());
			pstmt.setString(7, locationContext.getCountry());
			pstmt.setDouble(8, locationContext.getLat());
			pstmt.setDouble(9, locationContext.getLng());
			pstmt.setLong(10, locationContext.getContact());
			pstmt.setString(11, locationContext.getPhone());
			pstmt.setString(12, locationContext.getFaxPhone());
			
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
	
	public static boolean updateLocation(LocationContext locationContext) throws Exception {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE Locations SET NAME=?, STREET=?, CITY=?, STATE=?, ZIP=?, COUNTRY=?, LAT=?, LNG=?, CONTACT=?, PHONE=?, FAX_PHONE=? WHERE LOCATION_ID=? AND ORGID=?");
			
			pstmt.setString(1, locationContext.getName());
			pstmt.setString(2, locationContext.getStreet());
			pstmt.setString(3, locationContext.getCity());
			pstmt.setString(4, locationContext.getState());
			pstmt.setString(5, locationContext.getZip());
			pstmt.setString(6, locationContext.getCountry());
			pstmt.setDouble(7, locationContext.getLat());
			pstmt.setDouble(8, locationContext.getLng());
			pstmt.setLong(9, locationContext.getContact());
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
	
	private static LocationContext getLocationObjectFromRS(ResultSet rs) throws SQLException {
		
		LocationContext lc = new LocationContext();
		lc.setId(rs.getLong("LOCATION_ID"));
		lc.setOrgId(rs.getLong("ORGID"));
		lc.setName(rs.getString("NAME"));
		lc.setStreet(rs.getString("STREET"));
		lc.setCity(rs.getString("CITY"));
		lc.setState(rs.getString("STATE"));
		lc.setZip(rs.getString("ZIP"));
		lc.setCountry(rs.getString("COUNTRY"));
		lc.setLat(rs.getDouble("LAT"));
		lc.setLng(rs.getDouble("LNG"));
		lc.setContact(rs.getLong("CONTACT"));
		lc.setPhone(rs.getString("PHONE"));
		lc.setFaxPhone(rs.getString("FAX_PHONE"));
		
		return lc;
	}
}