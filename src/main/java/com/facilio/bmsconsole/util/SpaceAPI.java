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
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class SpaceAPI {
	
	private static Logger logger = Logger.getLogger(SpaceAPI.class.getName());
	
	public static Long addSpaceBase(Long orgId, Connection conn) throws Exception
	{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Long areaId = null;
		try
		{
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
			DBUtil.closeAll(pstmt, rs);
		}
		return areaId;
	}
	
	public static BaseSpaceContext getBaseSpace(long id, long orgId, Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("SELECT BaseSpace.ID, Campus.ID, Campus.NAME, Building.ID, Building.NAME, Floor.ID, Floor.NAME, Space.ID, Space.NAME FROM BaseSpace "
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
	
	public static List<BaseSpaceContext> getAllBaseSpaces(Long orgId, Connection conn) throws SQLException
	{
		List<BaseSpaceContext> areas = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try 
		{
			pstmt = conn.prepareStatement("SELECT BaseSpace.ID, Campus.ID, Campus.NAME, Building.ID, Building.NAME, Floor.ID, Floor.NAME, Space.ID, Space.NAME FROM BaseSpace "
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
		if(rs.getLong("Campus.ID") != 0)
		{
			bs.setName(rs.getString("Campus.NAME"));
			bs.setType("Campus");
		}
		else if(rs.getLong("Building.ID") != 0)
		{
			bs.setName(rs.getString("Building.NAME"));
			bs.setType("Building");
		}
		else if(rs.getLong("Floor.ID") != 0)
		{
			bs.setName(rs.getString("Floor.NAME"));
			bs.setType("Floor");
		}
		else if(rs.getLong("Space.ID") != 0)
		{
			bs.setName(rs.getString("Space.NAME"));
			bs.setType("Space");
		}
		return bs;
	}
}
