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

public class DeleteSpaceCategoryCommand extends FacilioCommand {

	private static Logger log = LogManager.getLogger(DeleteSpaceCategoryCommand.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		SpaceCategoryContext spaceCategory = (SpaceCategoryContext) context.get(FacilioConstants.ContextNames.SPACECATEGORY);
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("DELETE from Space_Category WHERE ID = ?");
			pstmt.setLong(1, spaceCategory.getId());
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
