package com.facilio.beans;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.data.ServicePortalInfo;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.DBUtil;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
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
	
	
	@Override
	public List<FacilioModule> getSubModules(long moduleId) throws Exception {
		String sql = "SELECT CHILD_MODULE_ID FROM SubModulesRel INNER JOIN (SELECT m.MODULEID, @em:=m.EXTENDS_ID AS EXTENDS_ID FROM (SELECT * FROM Modules ORDER BY MODULEID DESC) m JOIN (SELECT @em:=MODULEID FROM Modules WHERE ORGID = ? AND MODULEID = ?) tmp WHERE m.MODULEID=@em) parentmod ON SubModulesRel.PARENT_MODULE_ID = parentmod.MODULEID";
		ResultSet rs = null;
		try(Connection conn = getConnection();PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, getOrgId());
			pstmt.setLong(2, moduleId);
			rs = pstmt.executeQuery();
			List<FacilioModule> subModules = new ArrayList<>();
			while(rs.next()) {
				subModules.add(getMod(rs.getLong("CHILD_MODULE_ID")));
			}
			return subModules;
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			if(rs != null) {
				try {
					rs.close();
				}
				catch(SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public List<FacilioModule> getSubModules(String moduleName) throws Exception {
		String sql = "SELECT CHILD_MODULE_ID FROM SubModulesRel INNER JOIN (SELECT m.MODULEID, @em:=m.EXTENDS_ID AS EXTENDS_ID FROM (SELECT * FROM Modules ORDER BY MODULEID DESC) m JOIN (SELECT @em:=MODULEID FROM Modules WHERE ORGID = ? AND NAME = ?) tmp WHERE m.MODULEID=@em) parentmod ON SubModulesRel.PARENT_MODULE_ID = parentmod.MODULEID";
		ResultSet rs = null;
		try(Connection conn = getConnection();PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, getOrgId());
			pstmt.setString(2, moduleName);
			rs = pstmt.executeQuery();
			List<FacilioModule> subModules = new ArrayList<>();
			while(rs.next()) {
				subModules.add(getMod(rs.getLong("CHILD_MODULE_ID")));
			}
			return subModules;
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			if(rs != null) {
				try {
					rs.close();
				}
				catch(SQLException e) {
					e.printStackTrace();
				}
			}
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
		if(rs.getObject("Fields.EXTENDED_MODULEID") != null) {
			field.setExtendedModule(moduleMap.get(rs.getLong("Fields.EXTENDED_MODULEID")));
			if(field.getExtendedModule() == null) {
				throw new IllegalArgumentException("Invalid Extended module id in Field : "+rs.getString("Fields.NAME")+"::Module Id : "+rs.getLong("Fields.MODULEID"));
			}
		}
		field.setModule(moduleMap.get(rs.getLong("Fields.MODULEID")));
		field.setName(rs.getString("Fields.NAME"));
		field.setDisplayName(rs.getString("Fields.DISPLAY_NAME"));
		field.setDisplayType(rs.getInt("Fields.DISPLAY_TYPE"));
		field.setColumnName(rs.getString("Fields.COLUMN_NAME"));
		field.setSequenceNumber(rs.getInt("Fields.SEQUENCE_NUMBER"));
		field.setDataType(rs.getInt("Fields.DATA_TYPE"));
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
					if(field.getDataTypeEnum() == FieldType.LOOKUP) {
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
				if(field.getDataTypeEnum() == FieldType.LOOKUP) {
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
					if(field.getDataTypeEnum() == FieldType.LOOKUP) {
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
					lookupField.setLookupModule(getMod(rs.getLong("LOOKUP_MODULE_ID")));
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
		if(field != null) {
			field.setOrgId(getOrgId());
			Map<String, Object> fieldProps = FieldUtil.getAsProperties(field);
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table("Fields")
															.fields(FieldFactory.getAddFieldFields())
															.addRecord(fieldProps);
			
			insertBuilder.save();
			return (long) fieldProps.get("id");
		}
		else {
			throw new IllegalArgumentException("Invalid field object for addition");
		}
	}
	
	@Override
	public int updateField(FacilioField field) throws SQLException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if(field != null && field.getFieldId() != -1) {
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
															.table("Fields")
															.fields(FieldFactory.getUpdateFieldFields())
															.andCustomWhere("ORGID = ? AND FIELDID = ?", OrgInfo.getCurrentOrgInfo().getOrgid(), field.getFieldId());
			
			return updateBuilder.update(FieldUtil.getAsProperties(field));
		}
		else {
			throw new IllegalArgumentException("Invalid field object for Updation");
		}
	}
	
	@Override
	public int deleteField(long fieldId) throws Exception {
		// TODO Auto-generated method stub
		if(fieldId != -1) {
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
															.table("Fields")
															.andCustomWhere("ORGID = ? AND FIELDID = ?", OrgInfo.getCurrentOrgInfo().getOrgid(), fieldId);
			
			return deleteBuilder.delete();
		}
		else {
			throw new IllegalArgumentException("Invalid fieldId for Deletion");
		}
	}
	
	@Override
	public int deleteFields(List<Long> fieldIds) throws Exception {
		// TODO Auto-generated method stub
		if(fieldIds != null && !fieldIds.isEmpty()) {
			FacilioField field = new FacilioField();
			field.setName("fieldId");
			field.setDataType(FieldType.NUMBER);
			field.setColumnName("FIELDID");
			field.setModule(ModuleFactory.getFieldsModule());
			
			String ids = StringUtils.join(fieldIds, ",");
			Condition idCondition = new Condition();
			idCondition.setField(field);
			idCondition.setOperator(NumberOperators.EQUALS);
			idCondition.setValue(ids);
			
			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
					.table("Fields")
					.andCustomWhere("ORGID = ?", OrgInfo.getCurrentOrgInfo().getOrgid())
					.andCondition(idCondition);
			

			return deleteBuilder.delete();
		}
		else {
			throw new IllegalArgumentException("Invalid fieldIds for Deletion");
		}
	}
	
	@Override
	public long addModule(FacilioModule module) throws Exception {
		
		if(module == null) {
			throw new IllegalArgumentException("Invalid Module for insertion");
		}
		
		if(module.getName() == null || module.getName().isEmpty() || module.getTableName() == null || module.getTableName().isEmpty()) {
			throw new IllegalArgumentException("Invalid Module Name/ Module table Name");
		}
		
		String sql = "INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME, TABLE_NAME, EXTENDS_ID) VALUES (?,?,?,?,?)";
		ResultSet rs = null;
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setLong(1, getOrgId());
			pstmt.setString(2, module.getName());
			
			if(module.getDisplayName() != null && !module.getDisplayName().isEmpty()) {
				pstmt.setString(3, module.getDisplayName());
			}
			else {
				pstmt.setNull(3, Types.VARCHAR);
			}
			
			pstmt.setString(4, module.getTableName());
			
			if(module.getExtendModule() != null) {
				pstmt.setLong(5, module.getExtendModule().getModuleId());
			}
			else {
				pstmt.setNull(5, Types.BIGINT);
			}
			
			if (pstmt.executeUpdate() < 1) {
				throw new Exception("Unable to add Module");
			}
			else {
				rs = pstmt.getGeneratedKeys();
				rs.next();
				long moduleId = rs.getLong(1);
				System.out.println("Added Custom Module with ID : "+moduleId);
				return moduleId;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			if(rs != null) {
				try {
					rs.close();
				}
				catch(SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public void addSubModule(long parentModuleId, long childModuleId) throws Exception {
		String sql = "INSERT INTO SubModulesRel (PARENT_MODULE_ID, CHILD_MODULE_ID) VALUES (?,?)";
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, parentModuleId);
			pstmt.setLong(2, childModuleId);
			
			if (pstmt.executeUpdate() < 1) {
				throw new Exception("Unable to add Sub Module");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
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