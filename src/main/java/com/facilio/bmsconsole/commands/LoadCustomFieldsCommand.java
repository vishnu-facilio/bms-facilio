package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.fields.FieldType;
import com.facilio.bmsconsole.fields.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;

public class LoadCustomFieldsCommand implements Command{

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
			
			String sql = "SELECT Fields.FIELDID, Fields.ORGID, Fields.MODULEID, Fields.NAME, Fields.DISPLAY_NAME, Fields.COLUMN_NAME, Fields.SEQUENCE_NUMBER, Fields.DATA_TYPE FROM Fields INNER JOIN Modules ON Fields.MODULEID = Modules.MODULEID WHERE Modules.ORGID = ? and Modules.NAME = ? ORDER BY Fields.FIELDID";
			
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, orgId);
			pstmt.setString(2, moduleName);
			
			rs = pstmt.executeQuery();
			List<FacilioField> fields = new ArrayList<>();
			
			while(rs.next()) {
				FacilioField field = new FacilioField();
				
				field.setFieldId(rs.getLong("FIELDID"));
				field.setOrgId(rs.getLong("ORGID"));
				field.setModuleId(rs.getLong("MODULEID"));
				field.setName(rs.getString("NAME"));
				field.setDisplayName(rs.getString("DISPLAY_NAME"));
				field.setColumnName(rs.getString("COLUMN_NAME"));
				field.setSequenceNumber(rs.getInt("SEQUENCE_NUMBER"));
				field.setDataType(FieldType.getCFType(rs.getInt("DATA_TYPE")));
				
				fields.add(field);
			}
			
			context.put(FacilioConstants.ContextNames.CUSTOM_FIELDS, fields);
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
