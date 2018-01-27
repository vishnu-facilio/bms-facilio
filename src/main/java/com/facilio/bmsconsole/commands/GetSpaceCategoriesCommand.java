package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class GetSpaceCategoriesCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
	
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<SpaceCategoryContext> spaceCategories = new ArrayList();
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("select * from Space_Category where ORGID = ?");
			pstmt.setLong(1, orgId);
			rs = pstmt.executeQuery();
			while(rs.next())
			{
				SpaceCategoryContext spcontext = new SpaceCategoryContext();
				spcontext.setId(rs.getLong("ID"));
				spcontext.setOrgId(rs.getLong("ORGID"));
				spcontext.setName(rs.getString("NAME"));
				spcontext.setDescription(rs.getString("DESCRIPTION"));
				spcontext.setCommonArea(rs.getBoolean("IS_COMMON_AREA"));
				spaceCategories.add(spcontext);
			}
			context.put(FacilioConstants.ContextNames.SPACECATEGORIESLIST, spaceCategories);
		}
		catch(SQLException | RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return false;
	}

}
