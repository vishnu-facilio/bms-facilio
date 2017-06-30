package com.facilio.bmsconsole.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.customfields.CFType;
import com.facilio.bmsconsole.customfields.CFUtil;
import com.facilio.bmsconsole.customfields.FacilioCustomField;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class LoadCustomFieldsCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		String objectTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_OBJECTS_TABLE_NAME);
		String fieldTableName = (String) context.get(FacilioConstants.ContextNames.MODULE_FIELDS_TABLE_NAME);
		
		if(objectTableName == null || objectTableName.isEmpty()) {
			throw new IllegalArgumentException("Object Table Name is not set for the module");
		}
		
		if(fieldTableName == null || fieldTableName.isEmpty()) {
			throw new IllegalArgumentException("Field Table is not set for the module");
		}
 		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			Connection conn = ((FacilioContext) context).getConnectionWithoutTransaction();
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT FIELDID, ")
				.append(fieldTableName)
				.append(".ORGID, ")
				.append(fieldTableName)
				.append(".OBJID, FIELDNAME, DATATYPE, COLUMNNUM FROM ")
				.append(fieldTableName)
				.append(" INNER JOIN ")
				.append(objectTableName)
				.append(" ON ")
				.append(fieldTableName)
				.append(".OBJID = ")
				.append(objectTableName)
				.append(".OBJID WHERE ")
				.append(objectTableName)
				.append(".ORGID = ?");
			
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, orgId);
			
			rs = pstmt.executeQuery();
			List<FacilioCustomField> fields = new ArrayList<>();
			
			while(rs.next()) {
				FacilioCustomField field = new FacilioCustomField();
				
				field.setFieldId(rs.getLong("FIELDID"));
				field.setOrgId(rs.getLong("ORGID"));
				field.setObjId(rs.getLong("OBJID"));
				field.setFieldName(rs.getString("FIELDNAME"));
				field.setColumnNum(rs.getInt("COLUMNNUM"));
				field.setDataType(CFType.getCFType(rs.getInt("DATATYPE")));
				
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
