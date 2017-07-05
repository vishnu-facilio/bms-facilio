package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.fields.FacilioField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;

public class AddFieldsCommand implements Command {
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<Long> fieldIds = new ArrayList<>();
		
		//Have to be converted to batch insert
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
		long moduleId = (long) context.get(FacilioConstants.ContextNames.MODULE_ID);
		for(FacilioField field : fields) {
			field.setFieldId(addField(field, moduleId, ((FacilioContext) context).getConnectionWithTransaction()));
		}
		
		return false;
	}
	
	private long addField(FacilioField cf, long moduleId, Connection conn) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
			
			pstmt = conn.prepareStatement("INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, SEQUENCE_NUMBER, DATA_TYPE) VALUES (?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
			
			pstmt.setLong(1, orgId);
			pstmt.setLong(2, moduleId);
			pstmt.setString(3, cf.getName());
			
			if(cf.getDisplayName() != null && !cf.getDisplayName().isEmpty()) {
				pstmt.setString(4, cf.getDisplayName());
			}
			else {
				pstmt.setString(4, cf.getName());
			}
			
			pstmt.setString(5, cf.getColumnName());
			
			if(cf.getSequenceNumber() > 0) {
				pstmt.setInt(6, cf.getSequenceNumber());
			}
			else {
				pstmt.setNull(6, Types.TINYINT);
			}
			
			pstmt.setInt(7, cf.getDataType().getTypeAsInt());
			
			if(pstmt.executeUpdate() < 1) {
				throw new RuntimeException("Unable to add ticket");
			}
			else {
				rs = pstmt.getGeneratedKeys();
				rs.next();
				long fieldId = rs.getLong(1);
				System.out.println("Added Custom Field with ID : "+fieldId);
				return fieldId;
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
}
