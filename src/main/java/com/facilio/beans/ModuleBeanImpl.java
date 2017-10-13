package com.facilio.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.etsi.uri.x01903.v13.impl.GenericTimeStampTypeImpl;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.data.ServicePortalInfo;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class ModuleBeanImpl implements ModuleBean {

	
	private Connection getConnection() throws SQLException {
	//	return BeanFactory.getConnection();
		return FacilioConnectionPool.INSTANCE.getConnection();
	}

	@Override
	public long getOrgId() {
		return OrgInfo.getCurrentOrgInfo().getOrgid();
	}
	
	private FacilioModule getModuleFromRS(ResultSet rs) throws SQLException {
		FacilioModule module = null;
		boolean isFirst = true;
		FacilioModule prevModule = null;
		while(rs.next()) { 
			FacilioModule currentModule = new FacilioModule();
			currentModule.setModuleId(rs.getLong("MODULEID"));
			currentModule.setOrgId(rs.getLong("ORGID"));
			currentModule.setName(rs.getString("NAME"));
			currentModule.setDisplayName(rs.getString("DISPLAY_NAME"));
			currentModule.setTableName(rs.getString("TABLE_NAME"));
			if(prevModule != null) {
				prevModule.setExtendModule(currentModule);
			}
			prevModule = currentModule;
			
			if(isFirst) {
				module = currentModule;
				isFirst = false;
			}
		}
		return module;
	}
	
	@Override
	public FacilioModule getModule(long moduleId) throws Exception {
		
		PreparedStatement pstmt = null;
		Connection conn  =null;
		ResultSet rs = null;
		try {
			 conn = getConnection();
			pstmt = conn.prepareStatement("SELECT m.MODULEID, m.ORGID, m.NAME, m.DISPLAY_NAME, m.TABLE_NAME, @em:=m.EXTENDS_ID AS EXTENDS_ID FROM (SELECT * FROM Modules ORDER BY MODULEID DESC) m JOIN (SELECT @em:=MODULEID FROM Modules WHERE ORGID = ? AND MODULEID = ?) tmp WHERE m.MODULEID=@em;");
			pstmt.setLong(1, getOrgId());
			pstmt.setLong(2, moduleId);
			
			rs = pstmt.executeQuery();
			
			return getModuleFromRS(rs);
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(conn,pstmt, rs);
		}
	}
	
	@Override
	public FacilioModule getModule(String moduleName) throws Exception {
		
		PreparedStatement pstmt = null;
		Connection conn  =null;
		ResultSet rs = null;
		try {
			 conn = getConnection();
			pstmt = conn.prepareStatement("SELECT m.MODULEID, m.ORGID, m.NAME, m.DISPLAY_NAME, m.TABLE_NAME, @em:=m.EXTENDS_ID AS EXTENDS_ID FROM (SELECT * FROM Modules ORDER BY MODULEID DESC) m JOIN (SELECT @em:=MODULEID FROM Modules WHERE ORGID = ? AND NAME = ?) tmp WHERE m.MODULEID=@em");
			pstmt.setLong(1, getOrgId());
			pstmt.setString(2, moduleName);
			
			rs = pstmt.executeQuery();
			
			return getModuleFromRS(rs);
		}
		catch(SQLException e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			DBUtil.closeAll(conn,pstmt, rs);
		}
	}
	
	private FacilioModule getMod(String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", getOrgId());
		return modBean.getModule(moduleName);
	}
	
	private FacilioModule getMod(long moduleId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean", getOrgId());
		return modBean.getModule(moduleId);
	}
	
	private Map<Long, FacilioModule> splitModules(FacilioModule module) {
		Map<Long, FacilioModule> modules = new HashMap<>();
		
		FacilioModule parent = module;
		while(parent != null) {
			modules.put(parent.getModuleId(), parent);
			parent = parent.getExtendModule();
		}
		return modules;
	}
	
	private FacilioField getFieldFromRS(ResultSet rs, Map<Long, FacilioModule> moduleMap) throws SQLException {
		FacilioField field = new FacilioField();
		field.setFieldId(rs.getLong("Fields.FIELDID"));
		field.setOrgId(rs.getLong("Fields.ORGID"));
		field.setModule(moduleMap.get(rs.getLong("Fields.MODULEID")));
		if(rs.getObject("Fields.EXTENDED_MODULEID") != null) {
			field.setExtendedModule(moduleMap.get(rs.getLong("Fields.EXTENDED_MODULEID")));
			if(field.getExtendedModule() == null) {
				throw new IllegalArgumentException("Invalid Extended module id in Field : "+field.getName()+"::"+field.getModule().getName());
			}
		}
		field.setName(rs.getString("Fields.NAME"));
		field.setDisplayName(rs.getString("Fields.DISPLAY_NAME"));
		field.setDisplayType(rs.getInt("Fields.DISPLAY_TYPE"));
		field.setColumnName(rs.getString("Fields.COLUMN_NAME"));
		field.setSequenceNumber(rs.getInt("Fields.SEQUENCE_NUMBER"));
		field.setDataType(FieldType.getCFType(rs.getInt("Fields.DATA_TYPE")));
		field.setDataTypeCode(rs.getInt("Fields.DATA_TYPE"));
		field.setDefault(rs.getBoolean("Fields.IS_DEFAULT"));
		field.setMainField(rs.getBoolean("Fields.IS_MAIN_FIELD"));
		field.setRequired(rs.getBoolean("Fields.REQUIRED"));
		field.setDisabled(rs.getBoolean("Fields.DISABLED"));
		field.setStyleClass(rs.getString("Fields.STYLE_CLASS"));
		field.setIcon(rs.getString("Fields.ICON"));
		field.setPlaceHolder(rs.getString("Fields.PLACE_HOLDER"));
		
		return field;
	}
	
	@Override
	public FacilioField getPrimaryField(String moduleName) throws Exception {
		Connection conn  =null;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			 conn = getConnection();
			long orgId = getOrgId();
			FacilioModule module = getMod(moduleName);
			
			if(module != null) {
				String sql = "SELECT Fields.FIELDID, Fields.ORGID, Fields.MODULEID, Fields.EXTENDED_MODULEID, Fields.NAME, Fields.DISPLAY_NAME, Fields.DISPLAY_TYPE, Fields.COLUMN_NAME, Fields.SEQUENCE_NUMBER, Fields.DATA_TYPE, Fields.IS_DEFAULT, Fields.IS_MAIN_FIELD, Fields.IS_MAIN_FIELD, Fields.REQUIRED, Fields.DISABLED, Fields.STYLE_CLASS, Fields.ICON, Fields.PLACE_HOLDER FROM Fields WHERE Fields.ORGID = ? and Fields.MODULEID = ? AND IS_MAIN_FIELD = true";
				
				Map<Long, FacilioModule> moduleMap = splitModules(module);
				
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setLong(1, orgId);
				pstmt.setLong(2, module.getModuleId());
				
				rs = pstmt.executeQuery();
				FacilioField defaultField = null;
				
				if (rs.next()) {
					defaultField = getFieldFromRS(rs, moduleMap);
					return defaultField;
				}
			}
			return null;
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn,pstmt, rs);
		}
	}

	@Override
	public ArrayList<FacilioField> getAllFields(String moduleName) throws Exception {
		Connection conn  =null;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			long orgId = getOrgId();
			FacilioModule module = getMod(moduleName);
			
			if(module != null) { 
				String sql = "SELECT Fields.FIELDID, Fields.ORGID, Fields.MODULEID, Fields.EXTENDED_MODULEID, Fields.NAME, Fields.DISPLAY_NAME, Fields.DISPLAY_TYPE, Fields.COLUMN_NAME, Fields.SEQUENCE_NUMBER, Fields.DATA_TYPE, Fields.IS_DEFAULT, Fields.IS_MAIN_FIELD, Fields.REQUIRED, Fields.DISABLED, Fields.STYLE_CLASS, Fields.ICON, Fields.PLACE_HOLDER FROM Fields WHERE Fields.ORGID = ? and Fields.MODULEID = ? ORDER BY Fields.FIELDID";
				Map<Long, FacilioModule> moduleMap = splitModules(module);
				
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setLong(1, orgId);
				pstmt.setLong(2, module.getModuleId());
				
				rs = pstmt.executeQuery();
				ArrayList<FacilioField> fields = new ArrayList<>();
				
				while(rs.next()) {
					FacilioField field = getFieldFromRS(rs, moduleMap);
					if(field.getDataType() == FieldType.LOOKUP) {
						field = getLookupField(field);
					}
					fields.add(field);
				}
				return fields;
			}
			return null;
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn,pstmt, rs);
		}
	}
	
	@Override
	public FacilioField getField(long fieldId) throws Exception {
		Connection conn  =null;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			 conn = getConnection();
			long orgId = getOrgId();
			
			String sql = "SELECT Fields.FIELDID, Fields.ORGID, Fields.MODULEID, Fields.EXTENDED_MODULEID, Fields.NAME, Fields.DISPLAY_NAME, Fields.DISPLAY_TYPE, Fields.COLUMN_NAME, Fields.SEQUENCE_NUMBER, Fields.DATA_TYPE, Fields.IS_DEFAULT, Fields.IS_MAIN_FIELD, Fields.REQUIRED, Fields.DISABLED, Fields.STYLE_CLASS, Fields.ICON, Fields.PLACE_HOLDER FROM Fields WHERE Fields.ORGID = ? and Fields.FIELDID = ?";
			
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, orgId);
			pstmt.setLong(2, fieldId);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				FacilioModule module = getMod(rs.getLong("Fields.MODULEID"));
				Map<Long, FacilioModule> moduleMap = splitModules(module);		
				
				FacilioField field = getFieldFromRS(rs, moduleMap);
				if(field.getDataType() == FieldType.LOOKUP) {
					field = getLookupField(field);
				}
				return field;
			}
			else {
				return null;
			}
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn,pstmt, rs);
		}
	}
	
	@Override
	public FacilioField getField(String fieldName, String moduleName) throws Exception {
		Connection conn  =null;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			 conn = getConnection();
			long orgId = getOrgId();
			FacilioModule module = getMod(moduleName);
			
			if(module != null) {
				String sql = "SELECT Fields.FIELDID, Fields.ORGID, Fields.MODULEID, Fields.EXTENDED_MODULEID, Fields.NAME, Fields.DISPLAY_NAME, Fields.DISPLAY_TYPE, Fields.COLUMN_NAME, Fields.SEQUENCE_NUMBER, Fields.DATA_TYPE, Fields.IS_DEFAULT, Fields.IS_MAIN_FIELD, Fields.REQUIRED, Fields.DISABLED, Fields.STYLE_CLASS, Fields.ICON, Fields.PLACE_HOLDER FROM Fields WHERE Fields.ORGID = ? and Fields.NAME = ? and Fields.MODULEID = ?";
				Map<Long, FacilioModule> moduleMap = splitModules(module);
				
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setLong(1, orgId);
				pstmt.setString(2, fieldName);
				pstmt.setLong(3, module.getModuleId());
				
				rs = pstmt.executeQuery();
				if(rs.next()) {
					FacilioField field = getFieldFromRS(rs, moduleMap);
					if(field.getDataType() == FieldType.LOOKUP) {
						field = getLookupField(field);
					}
					return field;
				}
			}
			return null;
		}
		catch (Exception e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn,pstmt, rs);
		}
	}
	
	private LookupField getLookupField(FacilioField field) throws Exception {
		Connection conn  =null;

		LookupField lookupField = new LookupField();
		BeanUtils.copyProperties(lookupField, field);
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			 conn = getConnection();
			
			String sql = "SELECT SPECIAL_TYPE, LOOKUP_MODULE_ID FROM LookupFields WHERE LookupFields.FIELDID = ?";
			
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(1, field.getFieldId());
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				lookupField.setSpecialType(rs.getString("SPECIAL_TYPE"));
				if(rs.getObject("LOOKUP_MODULE_ID") != null) {
					lookupField.setLookupModule(getModule(rs.getLong("LOOKUP_MODULE_ID")));
				}
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
			DBUtil.closeAll(conn,pstmt, rs);
		}
	}

	@Override
	public long addField(FacilioField field) throws Exception {
		Connection conn  =null;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			 conn = getConnection();

			String sql = "INSERT INTO Fields (ORGID, MODULEID, EXTENDED_MODULEID, NAME, DISPLAY_NAME, COLUMN_NAME, SEQUENCE_NUMBER, DATA_TYPE) VALUES (?,?,?,?,?,?,?,?)";

			pstmt = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS);

			pstmt.setLong(1, getOrgId());
			pstmt.setLong(2, field.getModule().getModuleId());
			
			if(field.getExtendedModule() != null) {
				pstmt.setLong(3, field.getExtendedModule().getModuleId());
			}
			else {
				pstmt.setNull(3, Types.BIGINT);
			}
			
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
			DBUtil.closeAll(conn,pstmt, rs);
		}
	}
	
	public ServicePortalInfo getServicePortalInfo() throws Exception
	{
		return ServicePortalInfo.getServicePortalInfo();
	}

	@Override
	public JSONObject getStateFlow(String module) throws Exception {
	//String query = "select STATE_ID,TicketStatus.STATUS , GROUP_CONCAT(concat('{\"',NEXT_STATE_ID,'\":','\"',ts2.STATUS,'\"}')) from TicketStateFlow , TicketStatus, TicketStatus ts2 where TicketStatus.ID=TicketStateFlow.STATE_ID and TicketStateFlow.NEXT_STATE_ID=ts2.ID  group by STATE_ID";
		
		//FacilioModule fm = getModule("ticketstatus");
		String nextstatequery =" select STATE_ID,group_concat(concat('{\"Activity\":\"',ACTIVITY_NAME,'\", \"state\":\"',NEXT_STATE_ID,'\", \"StatusDesc\":\" ',STATUS,'\"}')) from TicketStateFlow,TicketStatus  where TicketStatus.ID=NEXT_STATE_ID and TicketStatus.ORGID="+OrgInfo.getCurrentOrgInfo().getOrgid()+" group by STATE_ID ";

		System.out.println(nextstatequery);
		try(java.sql.Connection con = FacilioConnectionPool.getInstance().getConnection();
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(nextstatequery);) {
			
			JSONObject stateflow =new JSONObject();
			while (rs.next())
			{
				String oldstate = rs.getString(1);
				String nextstates = rs.getString(2);
				System.out.println("["+ nextstates +"]");
				JSONArray nextstats =(JSONArray) new JSONParser().parse("["+ nextstates +"]");
			
				//System.out.println("For  "+oldstate+"\n"+stateflow);
	
				stateflow.put(oldstate, nextstats);
			}
			//System.out.println("The stateflow for ticket "+stateflow);
			return stateflow;
		}
		
	}
}