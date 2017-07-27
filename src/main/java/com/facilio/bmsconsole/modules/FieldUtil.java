package com.facilio.bmsconsole.modules;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;

public class FieldUtil {
	
	public static void castOrParseValueAsPerType(PreparedStatement pstmt, int paramIndex, FieldType type, Object value) throws SQLException {
		
		switch(type) {
			
			case STRING:
				if(value != null && !(value instanceof String && ((String)value).isEmpty())) {
					if(value instanceof String) {
						pstmt.setString(paramIndex, (String) value);
					}
					else {
						pstmt.setString(paramIndex, value.toString());
					}
				}
				else {
					pstmt.setNull(paramIndex, Types.VARCHAR);
				}
				break;
			case DECIMAL:
				if(value != null && !(value instanceof String && ((String)value).isEmpty())) {
					double val;
					if(value instanceof Double) {
						val = (double) value;
					}
					else {
						val = Double.parseDouble(value.toString());
					}
					if(val != 0) {
						pstmt.setDouble(paramIndex, val);
					}
					else {
						pstmt.setNull(paramIndex, Types.DOUBLE);
					}
				}
				else {
					pstmt.setNull(paramIndex, Types.DOUBLE);
				}
				break;
			case BOOLEAN:
				if(value != null) {
					if(value instanceof Boolean) {
						pstmt.setBoolean(paramIndex, (boolean) value);
					}
					else {
						pstmt.setBoolean(paramIndex, Boolean.parseBoolean(value.toString()));
					}
				}
				else {
					pstmt.setNull(paramIndex, Types.BOOLEAN);
				}
				break;
			case LOOKUP:
			case NUMBER:	
			case DATE:
			case DATE_TIME:
				if(value != null && !(value instanceof String && ((String)value).isEmpty())) {
					long val;
					if(value instanceof Long) {
						val = (long) value;
					}
					else {
						val = Long.parseLong(value.toString());
					}
					if(val != 0) {
						pstmt.setLong(paramIndex, val);
					}
					else {
						pstmt.setNull(paramIndex, Types.BIGINT);
					}
				}
				else {
					pstmt.setNull(paramIndex, Types.BIGINT);
				}
		}
	}
	
	public static Object getValueAsPerType(FacilioField cf, ResultSet rs) throws SQLException {
		switch(cf.getDataType()) {
			case STRING:
				return rs.getString(cf.getName());
			case DECIMAL:
				return rs.getDouble(cf.getName());
			case BOOLEAN:
				return rs.getBoolean(cf.getName());
			case LOOKUP:
			case NUMBER:	
			case DATE:
			case DATE_TIME:
				return rs.getLong(cf.getName());
			default:
				return rs.getString(cf.getName());
		}
	}
	
	public static List<FacilioField> getAllFields(String moduleName, Connection conn) throws SQLException, IllegalAccessException, InvocationTargetException {
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			String sql = "SELECT Fields.FIELDID, Fields.ORGID, Fields.MODULEID, Fields.NAME, Fields.DISPLAY_NAME, Fields.DISPLAY_TYPE, Fields.COLUMN_NAME, Fields.SEQUENCE_NUMBER, Fields.DATA_TYPE, Fields.IS_DEFAULT, Fields.IS_MAIN_FIELD, Fields.REQUIRED, Fields.DISABLED, Fields.STYLE_CLASS, Fields.ICON, Fields.PLACE_HOLDER FROM Fields INNER JOIN Modules ON Fields.MODULEID = Modules.MODULEID WHERE Modules.ORGID = ? and Modules.NAME = ? ORDER BY Fields.FIELDID";
			
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, orgId);
			pstmt.setString(2, moduleName);
			
			rs = pstmt.executeQuery();
			List<FacilioField> fields = new ArrayList<>();
			
			while(rs.next()) {
				FacilioField field = CommonCommandUtil.getFieldFromRS(rs);
				if(field.getDataType() == FieldType.LOOKUP) {
					field = getLookupField(field, conn);
				}
				field.setModuleName(moduleName);
				fields.add(field);
			}
			
			return fields;
		}
		catch (SQLException | IllegalAccessException | InvocationTargetException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(pstmt, rs);
		}
	}
	
	private static LookupField getLookupField(FacilioField field, Connection conn) throws IllegalAccessException, InvocationTargetException, SQLException {
		LookupField lookupField = new LookupField();
		BeanUtils.copyProperties(lookupField, field);
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			String sql = "SELECT Modules.MODULEID, Modules.ORGID, Modules.NAME, Modules.DISPLAY_NAME, Modules.TABLE_NAME, LookupFields.SPECIAL_TYPE FROM LookupFields LEFT JOIN Modules ON LookupFields.LOOKUP_MODULE_ID = Modules.MODULEID WHERE LookupFields.FIELDID = ?";
			
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, field.getFieldId());
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				lookupField.setSpecialType(rs.getString("SPECIAL_TYPE"));
				lookupField.setLookupModule(CommonCommandUtil.getModuleFromRS(rs));
			}
			else {
				return null;
			}
			return lookupField;
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(pstmt, rs);
		}
	}
}
