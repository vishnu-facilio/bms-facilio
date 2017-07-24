package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.facilio.bmsconsole.context.AreaContext;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class SpaceAPI {
	
	private static Logger logger = Logger.getLogger(SpaceAPI.class.getName());
	
	public static Long addArea(Long orgId, Connection conn) throws Exception
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Long areaId = null;
		try
		{
			pstmt = conn.prepareStatement("INSERT INTO Area (ORGID) VALUES (?)", Statement.RETURN_GENERATED_KEYS);
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
			DBUtil.closeAll(pstmt, rs);
		}
		return areaId;
	}
	
	public static List<AreaContext> getAllAreas(Long orgId, Connection conn) throws SQLException
	{
		List<AreaContext> areas = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			pstmt = conn.prepareStatement("SELECT Area.AREA_ID, Campus.CAMPUS_ID, Campus.NAME, Building.BUILDING_ID, Building.NAME, Floor.FLOOR_ID, Floor.NAME, Space.SPACE_ID, Space.NAME FROM Area "
					+ " LEFT JOIN Campus ON Area.AREA_ID = Campus.CAMPUS_ID"
					+ " LEFT JOIN Building ON Area.AREA_ID = Building.BUILDING_ID"
					+ " LEFT JOIN Floor ON Area.AREA_ID = Floor.FLOOR_ID"
					+ " LEFT JOIN Space ON Area.AREA_ID = Space.SPACE_ID"
					+ " WHERE Area.ORGID = ?");
			pstmt.setLong(1, orgId);
			rs = pstmt.executeQuery();
			while(rs.next()) 
			{
				AreaContext ac = new AreaContext();
				ac.setAreaId(rs.getLong("AREA_ID"));
				if(rs.getLong("CAMPUS_ID") != 0)
				{
					ac.setName(rs.getString("Campus.NAME"));
					ac.setType("Campus");
				}
				else if(rs.getLong("BUILDING_ID") != 0)
				{
					ac.setName(rs.getString("Building.NAME"));
					ac.setType("Building");
				}
				else if(rs.getLong("FLOOR_ID") != 0)
				{
					ac.setName(rs.getString("Floor.NAME"));
					ac.setType("Floor");
				}
				else if(rs.getLong("SPACE_ID") != 0)
				{
					ac.setName(rs.getString("Space.NAME"));
					ac.setType("Space");
				}
				areas.add(ac);
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
}
