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
		String moduleTable = (String) context.get(FacilioConstants.ContextNames.MODULE_OBJECTS_TABLE_NAME);
		
		if(moduleTable != null && !moduleTable.isEmpty()) {
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
				pstmt = conn.prepareStatement("SELECT NAME FROM "+moduleTable+" WHERE ORGID=?");
				pstmt.setLong(1, orgId);
				
				rs = pstmt.executeQuery();
				
				if(rs.next()) {
					String moduleName =  rs.getString("NAME");
					context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
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
