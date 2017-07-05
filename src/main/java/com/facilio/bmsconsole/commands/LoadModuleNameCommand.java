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

public class LoadModuleNameCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		if(moduleName != null && !moduleName.isEmpty()) {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
				pstmt = conn.prepareStatement("SELECT DISPLAY_NAME FROM Modules WHERE ORGID=? and NAME = ?");
				pstmt.setLong(1, orgId);
				pstmt.setString(2, moduleName);
				
				rs = pstmt.executeQuery();
				
				if(rs.next()) {
					String moduleDisplayName =  rs.getString("DISPLAY_NAME");
					context.put(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME, moduleDisplayName);
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
		
		return false;
	}

}
