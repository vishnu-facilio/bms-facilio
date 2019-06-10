package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.Criteria;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import org.apache.commons.chain.Chain;
import org.apache.commons.collections4.CollectionUtils;

import java.sql.*;
import java.util.*;

;

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
	
	public static LocationContext getLocation(long lat, long lon, long orgId) throws Exception{
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioField latField = modBean.getField("lat", FacilioConstants.ContextNames.LOCATION);
		FacilioField lonField = modBean.getField("lng", FacilioConstants.ContextNames.LOCATION);
		
		Condition latCondition = CriteriaAPI.getCondition(latField, Collections.singletonList(lat),NumberOperators.EQUALS);
		Condition lonCondition = CriteriaAPI.getCondition(lonField, Collections.singletonList(lon),NumberOperators.EQUALS);
		Condition org = CriteriaAPI.getCondition(FieldFactory.getOrgIdField(), Collections.singleton(orgId),NumberOperators.EQUALS);
		
		
		SelectRecordsBuilder<LocationContext> newBuilder = new SelectRecordsBuilder<LocationContext>().table("Locations")
				.moduleName(FacilioConstants.ContextNames.LOCATION)
				.beanClass(LocationContext.class)
				.select(modBean.getAllFields(FacilioConstants.ContextNames.LOCATION))
				.andCondition(latCondition).andCondition(lonCondition).andCondition(org);
		
		List<LocationContext> locationList = newBuilder.get();
		if(!locationList.isEmpty()) {
			return locationList.get(0);
		}
		return null;
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
	
	public static LocationContext getPoPrLocation (Object obj, LocationContext locationContext, String locationName, boolean isShippingAddress) throws Exception {
		if (locationContext == null) {
			return null;
		}
		
		LocationContext location = locationContext;
		FacilioContext context = new FacilioContext();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		location.setName(locationName);
		context.put(FacilioConstants.ContextNames.RECORD, location);
		context.put(FacilioConstants.ContextNames.RECORD_ID, location.getId());
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, Collections.singletonList(location.getId()));
	
		if (location.getId() > 0) {
			Chain editLocation = FacilioChainFactory.updateLocationChain();
			editLocation.execute(context);
		}
		else {
			if(location.isEmpty()) {
				LocationContext locationObj = null;
				if (obj != null) {
					if(isShippingAddress) {
						long storeRoomId = ((StoreRoomContext) obj).getId();
		                StoreRoomContext storeRoom = getLocationObj(modBean, "storeRoom", "location", storeRoomId);
		                if (storeRoom != null) {
		                	locationObj = storeRoom.getLocation();
		                }
					}
					else {
						long vendorId = ((VendorContext) obj).getId();
						VendorContext vendorContext = getLocationObj(modBean, "vendors", "address", vendorId);
		                if (vendorContext != null) {
		                	locationObj = vendorContext.getAddress();
		                }
	    			}
				}
				
				if (locationObj == null) {
					location.setName(locationName);
                	location.setLat(1.1);
    				location.setLng(1.1);
				} else {
					location.setName(locationName);
					location.setStreet(locationObj.getStreet());
					location.setState(locationObj.getState());
					location.setLat(1.1);
					location.setLng(1.1);
					location.setZip(locationObj.getZip());
					location.setCity(locationObj.getCity());
					location.setCountry(locationObj.getCountry());
				}
			}
			
			Chain addLocation = FacilioChainFactory.addLocationChain();
			addLocation.execute(context);
			long locationId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
			location.setId(locationId);
		}
		
		return location;
	}
		
	private static <E> E getLocationObj(ModuleBean modBean, String moduleName, String locationFieldName, long id) throws Exception {
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		
		SelectRecordsBuilder<StoreRoomContext> builder = new SelectRecordsBuilder<StoreRoomContext>()
					.module(module)
					.beanClass(FacilioConstants.ContextNames.getClassFromModuleName(module.getName()))
					.select(fields)
					.fetchLookup((LookupField) fieldsAsMap.get(locationFieldName))
					.andCondition(CriteriaAPI.getIdCondition(id, module));
					;
        List<StoreRoomContext> list = builder.get();
        if (!CollectionUtils.isEmpty(list)) {
        	return (E) list.get(0);
        }
        return null;
	}
}