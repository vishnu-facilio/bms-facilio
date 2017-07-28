 package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;

public class LoadMainFieldCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		if(moduleName == null || moduleName.isEmpty()) {
			throw new IllegalArgumentException("Module Name is not set for the module");
		}
 		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			
			String sql = "SELECT Modules.TABLE_NAME, Fields.FIELDID, Fields.ORGID, Fields.MODULEID, Fields.NAME, Fields.DISPLAY_NAME, Fields.DISPLAY_TYPE, Fields.COLUMN_NAME, Fields.SEQUENCE_NUMBER, Fields.DATA_TYPE, Fields.IS_DEFAULT, Fields.IS_MAIN_FIELD, Fields.IS_MAIN_FIELD, Fields.REQUIRED, Fields.DISABLED, Fields.STYLE_CLASS, Fields.ICON, Fields.PLACE_HOLDER FROM Fields INNER JOIN Modules ON Fields.MODULEID = Modules.MODULEID WHERE Modules.ORGID = ? and Modules.NAME = ? AND IS_MAIN_FIELD = true";
			
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, orgId);
			pstmt.setString(2, moduleName);
			
			rs = pstmt.executeQuery();
			FacilioField defaultField = null;
			
			if(rs.next()) {
				defaultField = CommonCommandUtil.getFieldFromRS(rs);
				defaultField.setModuleName(moduleName);
				context.put(FacilioConstants.ContextNames.MODULE_DATA_TABLE_NAME, rs.getString("TABLE_NAME"));
				context.put(FacilioConstants.ContextNames.DEFAULT_FIELD, defaultField);
			}
			
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(pstmt, rs);
		}
		
		return false;
		
	}

}
