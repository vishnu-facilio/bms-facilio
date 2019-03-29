package com.facilio.bmsconsole.commands;

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
import java.sql.SQLException;

public class EditSpaceCategoryCommand implements Command {
	private static Logger log = LogManager.getLogger(EditSpaceCategoryCommand.class.getName());


	@Override
	public boolean execute(Context context) throws Exception {
		SpaceCategoryContext spaceCategory = (SpaceCategoryContext) context.get(FacilioConstants.ContextNames.SPACECATEGORY);
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("UPDATE Space_Category SET NAME = ? ,DESCRIPTION = ?, IS_COMMON_AREA = ? WHERE ID = ?");
			pstmt.setString(1, spaceCategory.getName() );
			pstmt.setString(2, spaceCategory.getDescription());
			pstmt.setBoolean(3, spaceCategory.getCommonArea());
			pstmt.setLong(4, spaceCategory.getId());
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

}
