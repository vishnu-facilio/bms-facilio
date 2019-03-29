package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddSpaceCategoryCommand implements Command {

	private static Logger log = LogManager.getLogger(AddSpaceCategoryCommand.class.getName());

	@Override
	public boolean execute(Context context) throws Exception {
		SpaceCategoryContext spaceCategory = (SpaceCategoryContext) context.get(FacilioConstants.ContextNames.SPACECATEGORY);
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		long moduleId = getSpaceCategoryModuleId(orgId);
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO Space_Category(ORGID,MODULEID,NAME,DESCRIPTION,IS_COMMON_AREA) VALUES(?,?,?,?,?)");
			pstmt.setLong(1, orgId);
			pstmt.setLong(2, moduleId);
			pstmt.setString(3, spaceCategory.getName());
			pstmt.setString(4, spaceCategory.getDescription());
			pstmt.setBoolean(5, spaceCategory.getCommonArea());
			pstmt.executeUpdate();
		}
		catch(SQLException | RuntimeException e) {
			log.info("Exception occurred ", e);
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt);
		}
		return false;
	
	}
	
	public long getSpaceCategoryModuleId(long orgId) throws Exception {
		long moduleId = -1;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT MODULEID FROM Modules WHERE ORGID = ? AND NAME = ?");
			pstmt.setLong(1, orgId);
			pstmt.setString(2, "spacecategory");
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				moduleId = rs.getLong("MODULEID");
			}
		}
		catch(SQLException | RuntimeException e) {
			log.info("Exception occurred ", e);
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return moduleId;
	
	}

}
