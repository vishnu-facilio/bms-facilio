package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.fields.FieldType;
import com.facilio.bmsconsole.fields.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;

public class SetColumnNameForNewCFsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
		long moduleId = (long) context.get(FacilioConstants.ContextNames.MODULE_ID);
		
		//Have to be changed to get in minimum number of queries
		for (FacilioField field : fields) {
			FieldType dataType = FieldType.getCFType(field.getDataTypeCode());
			
			if(dataType != null) {
				field.setDataType(dataType);
				field.setColumnName(getColumnNameForNewField(moduleId, dataType, ((FacilioContext) context).getConnectionWithTransaction()));
			}
			else {
				throw new IllegalArgumentException("Invalid Data Type Value");
			}
		}
		
		return false;
	}
	
	private String getColumnNameForNewField(long moduleId, FieldType type, Connection conn) throws Exception {
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			
			pstmt = conn.prepareStatement("SELECT ID, COLUMN_NAME FROM Available_Columns WHERE ORGID = ? AND MODULEID = ? AND DATA_TYPE = ? LIMIT 1");
			pstmt.setLong(1, orgId);
			pstmt.setLong(2, moduleId);
			pstmt.setInt(3, type.getTypeAsInt());
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				String columnName = rs.getString("COLUMN_NAME");
				long colId = rs.getLong("ID");
				
				deleteColumn(colId, conn);
				return columnName;
			}
			else {
				throw new Exception("No more columns available.");
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
	
	private void deleteColumn(long colId, Connection conn) throws SQLException {
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement("DELETE FROM Available_Columns WHERE ORGID = ? AND ID = ?");
			pstmt.setLong(1, orgId);
			pstmt.setLong(2, colId);
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to delete from Available_Columns");
			}
			
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(pstmt, null);
		}
	}
}
