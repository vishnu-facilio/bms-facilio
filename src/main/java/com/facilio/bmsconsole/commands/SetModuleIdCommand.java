package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;

public class SetModuleIdCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		if(moduleName == null || moduleName.isEmpty()) {
			throw new IllegalArgumentException("Module Name cannot be empty or null");
		}
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			Connection conn = ((FacilioContext) context).getConnectionWithTransaction();
			pstmt = conn.prepareStatement("SELECT MODULEID FROM Modules WHERE ORGID=? AND NAME=?");
			pstmt.setLong(1, orgId);
			pstmt.setString(2, moduleName);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				context.put(FacilioConstants.ContextNames.MODULE_ID, rs.getLong("MODULEID"));
			}
			else {
				throw new IllegalArgumentException("Invalid moduleName. No such moduleName found");
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(pstmt, rs);
		}
		
		return false;
	}

}
