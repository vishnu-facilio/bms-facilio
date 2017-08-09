package com.facilio.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

import org.apache.commons.beanutils.BeanUtils;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;

public class ModuleBeanImpl implements ModuleBean {

	@Override
	public Connection getConnection() {
		return BeanFactory.getConnection();
	}

	@Override
	public long getOrgId() {
		return OrgInfo.getCurrentOrgInfo().getOrgid();
	}
	
	@Override
	public FacilioModule getModule(String moduleName) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			Connection conn = getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM Modules WHERE ORGID=? and NAME = ?");
			pstmt.setLong(1, getOrgId());
			pstmt.setString(2, moduleName);
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				return CommonCommandUtil.getModuleFromRS(rs);
			}
			else {
				return null;
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

	@Override
	public FacilioField getPrimaryField(String moduleName) throws Exception {

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			Connection conn = getConnection();
			long orgId = getOrgId();
			
			String sql = "SELECT Modules.TABLE_NAME, Fields.FIELDID, Fields.ORGID, Fields.MODULEID, Fields.NAME, Fields.DISPLAY_NAME, Fields.DISPLAY_TYPE, Fields.COLUMN_NAME, Fields.SEQUENCE_NUMBER, Fields.DATA_TYPE, Fields.IS_DEFAULT, Fields.IS_MAIN_FIELD, Fields.IS_MAIN_FIELD, Fields.REQUIRED, Fields.DISABLED, Fields.STYLE_CLASS, Fields.ICON, Fields.PLACE_HOLDER FROM Fields INNER JOIN Modules ON Fields.MODULEID = Modules.MODULEID WHERE Modules.ORGID = ? and Modules.NAME = ? AND IS_MAIN_FIELD = true";
			
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, orgId);
			pstmt.setString(2, moduleName);
			
			rs = pstmt.executeQuery();
			FacilioField defaultField = null;
			
			if (rs.next()) {
				defaultField = CommonCommandUtil.getFieldFromRS(rs);
				defaultField.setModuleName(moduleName);
				defaultField.setModuleTableName(rs.getString("TABLE_NAME"));
				return defaultField;
			}
			else {
				return null;
			}
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(pstmt, rs);
		}
	}

	@Override
	public ArrayList<FacilioField> getAllFields(String moduleName) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			Connection conn = getConnection();
			long orgId = getOrgId();
			
			String sql = "SELECT Fields.FIELDID, Fields.ORGID, Fields.MODULEID, Fields.NAME, Fields.DISPLAY_NAME, Fields.DISPLAY_TYPE, Fields.COLUMN_NAME, Fields.SEQUENCE_NUMBER, Fields.DATA_TYPE, Fields.IS_DEFAULT, Fields.IS_MAIN_FIELD, Fields.REQUIRED, Fields.DISABLED, Fields.STYLE_CLASS, Fields.ICON, Fields.PLACE_HOLDER FROM Fields INNER JOIN Modules ON Fields.MODULEID = Modules.MODULEID WHERE Modules.ORGID = ? and Modules.NAME = ? ORDER BY Fields.FIELDID";
			
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, orgId);
			pstmt.setString(2, moduleName);
			
			rs = pstmt.executeQuery();
			ArrayList<FacilioField> fields = new ArrayList<>();
			
			while(rs.next()) {
				FacilioField field = CommonCommandUtil.getFieldFromRS(rs);
				if(field.getDataType() == FieldType.LOOKUP) {
					field = getLookupField(field);
				}
				field.setModuleName(moduleName);
				fields.add(field);
			}
			
			return fields;
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(pstmt, rs);
		}
	}
	
	private LookupField getLookupField(FacilioField field) throws Exception {
		
		LookupField lookupField = new LookupField();
		BeanUtils.copyProperties(lookupField, field);
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			Connection conn = getConnection();
			
			String sql = "SELECT Modules.MODULEID, Modules.ORGID, Modules.NAME, Modules.DISPLAY_NAME, Modules.TABLE_NAME, LookupFields.SPECIAL_TYPE FROM LookupFields LEFT JOIN Modules ON LookupFields.LOOKUP_MODULE_ID = Modules.MODULEID WHERE LookupFields.FIELDID = ?";
			
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, field.getFieldId());
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				lookupField.setSpecialType(rs.getString("SPECIAL_TYPE"));
				lookupField.setLookupModule(CommonCommandUtil.getModuleFromRS(rs));
				return lookupField;
			}
			else {
				return null;
			}
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(pstmt, rs);
		}
	}

	@Override
	public long addField(FacilioField field) throws Exception {

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			Connection conn = getConnection();

			String sql = "INSERT INTO Fields (ORGID, MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, SEQUENCE_NUMBER, DATA_TYPE) VALUES (?,?,?,?,?,?,?)";

			pstmt = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);

			pstmt.setLong(1, getOrgId());
			pstmt.setLong(2, field.getModuleId());
			pstmt.setString(3, field.getName());

			if(field.getDisplayName() != null && !field.getDisplayName().isEmpty()) {
				pstmt.setString(4, field.getDisplayName());
			}
			else {
				pstmt.setString(4, field.getName());
			}

			pstmt.setString(5, field.getColumnName());

			if(field.getSequenceNumber() > 0) {
				pstmt.setInt(6, field.getSequenceNumber());
			}
			else {
				pstmt.setNull(6, Types.TINYINT);
			}

			pstmt.setInt(7, field.getDataType().getTypeAsInt());

			if (pstmt.executeUpdate() < 1) {
				throw new Exception("Unable to add field");
			}
			else {
				rs = pstmt.getGeneratedKeys();
				rs.next();
				long fieldId = rs.getLong(1);
				System.out.println("Added Custom Field with ID : "+fieldId);
				return fieldId;
			}
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(pstmt, rs);
		}
	}
}