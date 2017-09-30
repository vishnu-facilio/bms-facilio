package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class SpaceAPI {
	
	private static Logger logger = Logger.getLogger(SpaceAPI.class.getName());
	
	public static Long addSpaceBase(Long orgId) throws Exception
	{
		 Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Long areaId = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO BaseSpace (ORGID) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
			pstmt.setLong(1, orgId);
			
			if(pstmt.executeUpdate() < 1) 
			{
				throw new RuntimeException("Unable to add area");
			}
			else 
			{
				rs = pstmt.getGeneratedKeys();
				rs.next();
				areaId = rs.getLong(1);
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
		return areaId;
	}
	
	public static BaseSpaceContext getBuildingSpace(long id, long orgId, Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("SELECT BaseSpace.ID, BaseSpace.ORGID, Building.ID, Building.NAME, Building.CAMPUS_ID FROM BaseSpace "
					+ " LEFT JOIN Building ON BaseSpace.ID = Building.BASE_SPACE_ID"
					+ " WHERE BaseSpace.ORGID = ? AND Building.ID = ?");
			pstmt.setLong(1, orgId);
			pstmt.setLong(2, id);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return getBuildingSpaceFromRS(rs);
			}
			else {
				return null;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(pstmt, rs);
		}
	}
	
	public static BaseSpaceContext getBaseSpace(long id, long orgId, Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("SELECT BaseSpace.ID, BaseSpace.ORGID, Campus.ID, Campus.NAME, Building.ID, Building.NAME, Building.CAMPUS_ID, Floor.ID, Floor.NAME, Floor.BUILDING_ID, Space.ID, Space.NAME, Space.FLOOR_ID, Space.BUILDING_ID FROM BaseSpace "
					+ " LEFT JOIN Campus ON BaseSpace.ID = Campus.BASE_SPACE_ID"
					+ " LEFT JOIN Building ON BaseSpace.ID = Building.BASE_SPACE_ID"
					+ " LEFT JOIN Floor ON BaseSpace.ID = Floor.BASE_SPACE_ID"
					+ " LEFT JOIN Space ON BaseSpace.ID = Space.BASE_SPACE_ID"
					+ " WHERE BaseSpace.ORGID = ? AND BaseSpace.ID = ?");
			pstmt.setLong(1, orgId);
			pstmt.setLong(2, id);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				return getBaseSpaceFromRS(rs);
			}
			else {
				return null;
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(pstmt, rs);
		}
	}
	
	public static List<BaseSpaceContext> getBaseSpaceWithChildren(long orgId, long id, Connection conn) throws SQLException {
		List<Long> ids = new ArrayList<>();
		ids.add(id);
		return getBaseSpaceWithChildren(orgId, ids, conn);
	}
	
	public static List<BaseSpaceContext> getBaseSpaceWithChildren(long orgId, List<Long> ids, Connection conn) throws SQLException
	{
		List<BaseSpaceContext> parentSpaces = getBaseSpaces(ids, orgId, conn);
		
		if(parentSpaces != null && !parentSpaces.isEmpty()) {
			List<BaseSpaceContext> spaces = new ArrayList<>();
			for(BaseSpaceContext parentSpace : parentSpaces) {
				spaces.add(parentSpace);
				String type = parentSpace.getType();
				switch(type) {
					case "Campus":
							spaces.addAll(getCampusChildren(orgId, parentSpace.getChildId(), conn));
							break;
					case "Building":
							spaces.addAll(getBuildingChildren(orgId, parentSpace.getChildId(), conn));
							break;
					case "Floor":
							spaces.addAll(getFloorChildren(orgId, parentSpace.getChildId(), conn));
							break;
					case "Space":
							break;		
				}
			}
			return spaces;
		}
		return null;
	}
	
	private static List<BaseSpaceContext> getCampusChildren(long orgId, long campusId, Connection conn) throws SQLException {
		List<Long> campusIds = new ArrayList<>();
		campusIds.add(campusId);
		return getCampusChildren(orgId, campusIds, conn);
	}
	
	private static List<BaseSpaceContext> getCampusChildren(long orgId, List<Long> campusIds, Connection conn) throws SQLException {
		List<BaseSpaceContext> areas = new ArrayList<>();
		if(campusIds != null && !campusIds.isEmpty()) {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try 
			{
				StringBuilder sql = new StringBuilder();
				sql.append("SELECT BaseSpace.ID, BaseSpace.ORGID, Building.ID, Building.NAME, Building.CAMPUS_ID FROM BaseSpace ")
					.append(" LEFT JOIN Building ON BaseSpace.ID = Building.BASE_SPACE_ID ")
					.append(" WHERE BaseSpace.ORGID = ? AND Building.CAMPUS_ID IN (");
				
				boolean isFirst = true;
				for(long campusId : campusIds) {
					if(isFirst) {
						isFirst = false;
					}
					else {
						sql.append(", ");
					}
					sql.append(campusId);
				}
				sql.append(")");
				
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setLong(1, orgId);
				rs = pstmt.executeQuery();
				List<Long> buildingIds = new ArrayList<>();
				while(rs.next()) 
				{
					BaseSpaceContext bs = new BaseSpaceContext();
					bs.setId(rs.getLong("BaseSpace.ID"));
					bs.setOrgId(rs.getLong("BaseSpace.ORGID"));
					bs.setName(rs.getString("Building.NAME"));
					bs.setType("Building");
					bs.setChildId(rs.getLong("Building.ID"));
					bs.setParentType("Campus");
					bs.setParentId(rs.getLong("Building.CAMPUS_ID"));
					areas.add(bs);
					buildingIds.add(bs.getChildId());
				}
				
				if(!buildingIds.isEmpty()) {
					areas.addAll(getBuildingChildren(orgId, buildingIds, conn));
				}
			}
			catch (SQLException e) 
			{
				logger.log(Level.SEVERE, "Exception while getting Buildings" +e.getMessage(), e);
				throw e;
			}
			finally 
			{
				DBUtil.closeAll(pstmt, rs);
			}
		}
		return areas;
	}
	
	private static List<BaseSpaceContext> getBuildingChildren(long orgId, long buildingId, Connection conn) throws SQLException {
		List<Long> buildingIds = new ArrayList<>();
		buildingIds.add(buildingId);
		return getBuildingChildren(orgId, buildingIds, conn);
	}
	
	private static List<BaseSpaceContext> getBuildingChildren(long orgId, List<Long> buildingIds, Connection conn) throws SQLException {
		List<BaseSpaceContext> areas = new ArrayList<>();
		if(buildingIds != null && !buildingIds.isEmpty()) {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try 
			{
				StringBuilder buildings = new StringBuilder();
				boolean isFirst = true;
				for(long buildingId : buildingIds) {
					if(isFirst) {
						isFirst = false;
					}
					else {
						buildings.append(", ");
					}
					buildings.append(buildingId);
				}
				
				StringBuilder sql = new StringBuilder();
				sql.append("SELECT BaseSpace.ID, BaseSpace.ORGID, Floor.ID, Floor.NAME, Floor.BUILDING_ID, Space.ID, Space.NAME, Space.BUILDING_ID FROM BaseSpace ")
					.append(" LEFT JOIN Floor ON BaseSpace.ID = Floor.BASE_SPACE_ID")
					.append(" LEFT JOIN Space ON BaseSpace.ID = Space.BASE_SPACE_ID")
					.append(" WHERE BaseSpace.ORGID = ? AND (Floor.BUILDING_ID IN (")
					.append(buildings)
					.append(") OR Space.BUILDING_ID IN (")
					.append(buildings)
					.append("))")
					;
				
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setLong(1, orgId);
				rs = pstmt.executeQuery();
				List<Long> floorIds = new ArrayList<>();
				while(rs.next()) 
				{
					BaseSpaceContext bs = new BaseSpaceContext();
					bs.setId(rs.getLong("BaseSpace.ID"));
					bs.setOrgId(rs.getLong("BaseSpace.ORGID"));
					if(rs.getLong("Floor.ID") != 0)
					{
						bs.setName(rs.getString("Floor.NAME"));
						bs.setType("Floor");
						bs.setChildId(rs.getLong("Floor.ID"));
						if(rs.getLong("Floor.BUILDING_ID") != 0)
						{
							bs.setParentType("Building");
							bs.setParentId(rs.getLong("Floor.BUILDING_ID"));
						}
						floorIds.add(bs.getChildId());
					}
					else if(rs.getLong("Space.ID") != 0)
					{
						bs.setName(rs.getString("Space.NAME"));
						bs.setType("Space");
						bs.setChildId(rs.getLong("Space.ID"));
						if(rs.getLong("Space.BUILDING_ID") != 0)
						{
							bs.setParentType("Building");
							bs.setParentId(rs.getLong("Space.BUILDING_ID"));
						}
					}
					areas.add(bs);
				}
				
				if(!floorIds.isEmpty()) {
					areas.addAll(getFloorChildren(orgId, floorIds, conn));
				}
			}
			catch (SQLException e) 
			{
				logger.log(Level.SEVERE, "Exception while getting Spaces and Floors" +e.getMessage(), e);
				throw e;
			}
			finally 
			{
				DBUtil.closeAll(pstmt, rs);
			}
		}
		return areas;
	}
	
	private static List<BaseSpaceContext> getFloorChildren(long orgId, long floorId, Connection conn) throws SQLException {
		List<Long> floorIds = new ArrayList<>();
		floorIds.add(floorId);
		return getFloorChildren(orgId, floorIds, conn);
	}
	
	private static List<BaseSpaceContext> getFloorChildren(long orgId, List<Long> floorIds, Connection conn) throws SQLException {
		List<BaseSpaceContext> areas = new ArrayList<>();
		if(floorIds != null && !floorIds.isEmpty()) {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try 
			{
				StringBuilder sql = new StringBuilder();
				sql.append("SELECT BaseSpace.ID, BaseSpace.ORGID, Space.ID, Space.NAME, Space.FLOOR_ID FROM BaseSpace ")
					.append(" LEFT JOIN Space ON BaseSpace.ID = Space.BASE_SPACE_ID")
					.append(" WHERE BaseSpace.ORGID = ? AND Space.FLOOR_ID IN (");
				
				boolean isFirst = true;
				for(long floorId : floorIds) {
					if(isFirst) {
						isFirst = false;
					}
					else {
						sql.append(", ");
					}
					sql.append(floorId);
				}
				sql.append(")");
				
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setLong(1, orgId);
				rs = pstmt.executeQuery();
				while(rs.next()) 
				{
					BaseSpaceContext bs = new BaseSpaceContext();
					bs.setId(rs.getLong("BaseSpace.ID"));
					bs.setOrgId(rs.getLong("BaseSpace.ORGID"));
					bs.setName(rs.getString("Space.NAME"));
					bs.setType("Space");
					bs.setChildId(rs.getLong("Space.ID"));
					bs.setParentType("Floor");
					bs.setParentId(rs.getLong("Space.FLOOR_ID"));
					areas.add(bs);
				}
			}
			catch (SQLException e) 
			{
				logger.log(Level.SEVERE, "Exception while getting Buildings" +e.getMessage(), e);
				throw e;
			}
			finally 
			{
				DBUtil.closeAll(pstmt, rs);
			}
		}
		return areas;
	}
	
	public static List<BaseSpaceContext> getBaseSpaces(List<Long> idList, long orgId, Connection conn) throws SQLException
	{
		List<BaseSpaceContext> areas = new ArrayList<>();
		if(idList != null && !idList.isEmpty()) {
			
			StringBuilder ids = new StringBuilder();
			boolean isFirst = true;
			for(long id : idList) {
				if(isFirst) {
					isFirst = false;
				}
				else {
					ids.append(", ");
				}
				ids.append(id);
			}
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			try 
			{
				
				pstmt = conn.prepareStatement("SELECT BaseSpace.ID, BaseSpace.ORGID, Campus.ID, Campus.NAME, Building.ID, Building.NAME, Building.CAMPUS_ID, Floor.ID, Floor.NAME, Floor.BUILDING_ID, Space.ID, Space.NAME, Space.FLOOR_ID, Space.BUILDING_ID FROM BaseSpace "
						+ " LEFT JOIN Campus ON BaseSpace.ID = Campus.BASE_SPACE_ID"
						+ " LEFT JOIN Building ON BaseSpace.ID = Building.BASE_SPACE_ID"
						+ " LEFT JOIN Floor ON BaseSpace.ID = Floor.BASE_SPACE_ID"
						+ " LEFT JOIN Space ON BaseSpace.ID = Space.BASE_SPACE_ID"
						+ " WHERE BaseSpace.ORGID = ? AND BaseSpace.ID IN ("+ids.toString()+")");
				pstmt.setLong(1, orgId);
				rs = pstmt.executeQuery();
				while(rs.next()) 
				{
					areas.add(getBaseSpaceFromRS(rs));
				}
			}
			catch (SQLException e) 
			{
				logger.log(Level.SEVERE, "Exception while getting all controllers" +e.getMessage(), e);
				throw e;
			}
			finally 
			{
				DBUtil.closeAll(pstmt, rs);
			}
		}
		return areas;
	}
	
	public static List<BaseSpaceContext> getAllBaseSpaces(Long orgId, Connection conn) throws SQLException
	{
		List<BaseSpaceContext> areas = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			pstmt = conn.prepareStatement("SELECT BaseSpace.ID, BaseSpace.ORGID, Campus.ID, Campus.NAME, Building.ID, Building.NAME, Building.CAMPUS_ID, Floor.ID, Floor.NAME, Floor.BUILDING_ID, Space.ID, Space.NAME, Space.FLOOR_ID, Space.BUILDING_ID FROM BaseSpace "
					+ " LEFT JOIN Campus ON BaseSpace.ID = Campus.BASE_SPACE_ID"
					+ " LEFT JOIN Building ON BaseSpace.ID = Building.BASE_SPACE_ID"
					+ " LEFT JOIN Floor ON BaseSpace.ID = Floor.BASE_SPACE_ID"
					+ " LEFT JOIN Space ON BaseSpace.ID = Space.BASE_SPACE_ID"
					+ " WHERE BaseSpace.ORGID = ?");
			pstmt.setLong(1, orgId);
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				areas.add(getBaseSpaceFromRS(rs));
			}
		}
		catch (SQLException e) 
		{
			logger.log(Level.SEVERE, "Exception while getting all controllers" +e.getMessage(), e);
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(pstmt, rs);
		}
		return areas;
	}
	
	private static BaseSpaceContext getBaseSpaceFromRS(ResultSet rs) throws SQLException {
		BaseSpaceContext bs = new BaseSpaceContext();
		bs.setId(rs.getLong("BaseSpace.ID"));
		bs.setOrgId(rs.getLong("BaseSpace.ORGID"));
		if(rs.getLong("Campus.ID") != 0)
		{
			bs.setName(rs.getString("Campus.NAME"));
			bs.setType("Campus");
			bs.setChildId(rs.getLong("Campus.ID"));
		}
		else if(rs.getLong("Building.ID") != 0)
		{
			bs.setName(rs.getString("Building.NAME"));
			bs.setType("Building");
			bs.setChildId(rs.getLong("Building.ID"));
			if(rs.getLong("Building.CAMPUS_ID") != 0)
			{
				bs.setParentType("Campus");
				bs.setParentId(rs.getLong("Building.CAMPUS_ID"));
			}
		}
		else if(rs.getLong("Floor.ID") != 0)
		{
			bs.setName(rs.getString("Floor.NAME"));
			bs.setType("Floor");
			bs.setChildId(rs.getLong("Floor.ID"));
			if(rs.getLong("Floor.BUILDING_ID") != 0)
			{
				bs.setParentType("Building");
				bs.setParentId(rs.getLong("Floor.BUILDING_ID"));
			}
		}
		else if(rs.getLong("Space.ID") != 0)
		{
			bs.setName(rs.getString("Space.NAME"));
			bs.setType("Space");
			bs.setChildId(rs.getLong("Space.ID"));
			if(rs.getLong("Space.FLOOR_ID") != 0)
			{
				bs.setParentType("Floor");
				bs.setParentId(rs.getLong("Space.FLOOR_ID"));
			}
			else if(rs.getLong("Space.BUILDING_ID") != 0)
			{
				bs.setParentType("Building");
				bs.setParentId(rs.getLong("Space.BUILDING_ID"));
			}
		}
		return bs;
	}
	
	private static BaseSpaceContext getBuildingSpaceFromRS(ResultSet rs) throws SQLException {
		BaseSpaceContext bs = new BaseSpaceContext();
		bs.setId(rs.getLong("BaseSpace.ID"));
		bs.setOrgId(rs.getLong("BaseSpace.ORGID"));
		if(rs.getLong("Building.ID") != 0)
		{
			bs.setName(rs.getString("Building.NAME"));
			bs.setType("Building");
			bs.setChildId(rs.getLong("Building.ID"));
			if(rs.getLong("Building.CAMPUS_ID") != 0)
			{
				bs.setParentType("Campus");
				bs.setParentId(rs.getLong("Building.CAMPUS_ID"));
			}
		}
		return bs;
	}
}
