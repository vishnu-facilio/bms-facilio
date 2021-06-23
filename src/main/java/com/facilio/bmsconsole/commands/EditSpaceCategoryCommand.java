package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.SpaceCategoryContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.transaction.FacilioConnectionPool;

public class EditSpaceCategoryCommand extends FacilioCommand {
	private static Logger log = LogManager.getLogger(EditSpaceCategoryCommand.class.getName());


	@Override
	public boolean executeCommand(Context context) throws Exception {
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
