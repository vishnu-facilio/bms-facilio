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

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.data.ServicePortalInfo;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.NumberField;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.DBUtil;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class ModuleBeanImpl implements ModuleBean {

	
	private Connection getConnection() throws SQLException {
	//	return BeanFactory.getConnection();
		return FacilioConnectionPool.INSTANCE.getConnection();
	}

	@Override
	public long getOrgId() {
		return AccountUtil.getCurrentOrg().getOrgId();
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
		
		if(LookupSpecialTypeUtil.isSpecialType(moduleName)) {
			return LookupSpecialTypeUtil.getModule(moduleName);
		}
		
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
	
	private List<FacilioModule> getSubModulesFromRS(ResultSet rs) throws SQLException, Exception {
		List<FacilioModule> subModules = new ArrayList<>();
		while(rs.next()) {
			subModules.add(getMod(rs.getLong("CHILD_MODULE_ID")));
		}
		return subModules;
	}
	
	@Override
	public List<FacilioModule> getAllSubModules(long moduleId) throws Exception {
		String sql = "SELECT CHILD_MODULE_ID FROM SubModulesRel INNER JOIN (SELECT m.MODULEID, @em:=m.EXTENDS_ID AS EXTENDS_ID FROM (SELECT * FROM Modules ORDER BY MODULEID DESC) m JOIN (SELECT @em:=MODULEID FROM Modules WHERE ORGID = ? AND MODULEID = ?) tmp WHERE m.MODULEID=@em) parentmod ON SubModulesRel.PARENT_MODULE_ID = parentmod.MODULEID";
		ResultSet rs = null;
		try(Connection conn = getConnection();PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, getOrgId());
			pstmt.setLong(2, moduleId);
			rs = pstmt.executeQuery();
			return getSubModulesFromRS(rs);
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
	public List<FacilioModule> getAllSubModules(String moduleName) throws Exception {
		String sql = "SELECT CHILD_MODULE_ID FROM SubModulesRel INNER JOIN (SELECT m.MODULEID, @em:=m.EXTENDS_ID AS EXTENDS_ID FROM (SELECT * FROM Modules ORDER BY MODULEID DESC) m JOIN (SELECT @em:=MODULEID FROM Modules WHERE ORGID = ? AND NAME = ?) tmp WHERE m.MODULEID=@em) parentmod ON SubModulesRel.PARENT_MODULE_ID = parentmod.MODULEID";
		ResultSet rs = null;
		try(Connection conn = getConnection();PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, getOrgId());
			pstmt.setString(2, moduleName);
			rs = pstmt.executeQuery();
			return getSubModulesFromRS(rs);
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
	public List<FacilioModule> getSubModules(long moduleId, FacilioModule.SubModuleType type) throws Exception {
		String sql = "SELECT CHILD_MODULE_ID FROM SubModulesRel INNER JOIN (SELECT m.MODULEID, @em:=m.EXTENDS_ID AS EXTENDS_ID FROM (SELECT * FROM Modules ORDER BY MODULEID DESC) m JOIN (SELECT @em:=MODULEID FROM Modules WHERE ORGID = ? AND MODULEID = ?) tmp WHERE m.MODULEID=@em) parentmod ON SubModulesRel.PARENT_MODULE_ID = parentmod.MODULEID WHERE SubModulesRel.SUB_TYPE = ?";
		ResultSet rs = null;
		try(Connection conn = getConnection();PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, getOrgId());
			pstmt.setLong(2, moduleId);
			pstmt.setInt(3, type.getIntVal());
			rs = pstmt.executeQuery();
			return getSubModulesFromRS(rs);
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
	public List<FacilioModule> getSubModules(String moduleName, FacilioModule.SubModuleType type) throws Exception {
		String sql = "SELECT CHILD_MODULE_ID FROM SubModulesRel INNER JOIN (SELECT m.MODULEID, @em:=m.EXTENDS_ID AS EXTENDS_ID FROM (SELECT * FROM Modules ORDER BY MODULEID DESC) m JOIN (SELECT @em:=MODULEID FROM Modules WHERE ORGID = ? AND NAME = ?) tmp WHERE m.MODULEID=@em) parentmod ON SubModulesRel.PARENT_MODULE_ID = parentmod.MODULEID WHERE SubModulesRel.SUB_TYPE = ?";
		ResultSet rs = null;
		try(Connection conn = getConnection();PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, getOrgId());
			pstmt.setString(2, moduleName);
			pstmt.setInt(3, type.getIntVal());
			rs = pstmt.executeQuery();
			return getSubModulesFromRS(rs);
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
	
	@Override
	public FacilioField getPrimaryField(String moduleName) throws Exception {
		FacilioModule module = getMod(moduleName);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getSelectFieldFields())
														.table("Fields")
														.andCustomWhere("Fields.ORGID = ? AND Fields.MODULEID = ? AND IS_MAIN_FIELD = true", getOrgId(), module.getModuleId());
		List<Map<String, Object>> fieldProps = selectBuilder.get();
		
		Map<Long, FacilioModule> moduleMap = splitModules(module);
		List<FacilioField> fields = getFieldFromPropList(fieldProps, moduleMap);
		if(fields != null && !fields.isEmpty()) {
			return fields.get(0);
		}
		return null;
	}

	private ArrayList<FacilioField> getFieldFromPropList(List<Map<String, Object>> props, Map<Long, FacilioModule> moduleMap) throws Exception {
		
		if(props != null && !props.isEmpty()) {
			Map<FieldType, List<Long>> extendedIds = new HashMap<>();
			for (Map<String, Object> prop : props) {
				FieldType type = FieldType.getCFType((int) prop.get("dataType"));
				List<Long> idList = extendedIds.get(type);
				if(idList == null) {
					idList = new ArrayList<>();
					extendedIds.put(type, idList);
				}
				idList.add((Long) prop.get("fieldId"));
			}
			Map<FieldType, Map<Long, Map<String, Object>>> extendedPropsMap = getTypeWiseExtendedProps(extendedIds);
			
			ArrayList<FacilioField> fields = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				Long extendedModuleId = (Long) prop.get("extendedModuleId");
				if(extendedModuleId != null) {
					FacilioModule extendedModule = moduleMap.get(extendedModuleId);
					if(extendedModule == null) {
						throw new IllegalArgumentException("Invalid Extended module id in Field : "+prop.get("name")+"::Module Id : "+prop.get("moduleId"));
					}
					prop.put("extendedModule", extendedModule);
				}
				prop.put("module", moduleMap.get(prop.get("moduleId")));
				
				FieldType type = FieldType.getCFType((int) prop.get("dataType"));
				switch(type) {
					case NUMBER:
					case DECIMAL:
						prop.putAll(extendedPropsMap.get(type).get((Long) prop.get("fieldId")));
						fields.add(FieldUtil.getAsBeanFromMap(prop, NumberField.class));
						break;
					case LOOKUP:
						prop.putAll(extendedPropsMap.get(type).get((Long) prop.get("fieldId")));
						Long lookupModuleId = (Long) prop.get("lookupModuleId");
						if(lookupModuleId != null) {
							FacilioModule lookupModule = getMod(lookupModuleId);
							prop.put("lookupModule", lookupModule);
						}
						
						fields.add(FieldUtil.getAsBeanFromMap(prop, LookupField.class));
						break;
					case MISC:
					case STRING:
					case BOOLEAN:
					case DATE:
					case DATE_TIME:
						fields.add(FieldUtil.getAsBeanFromMap(prop, FacilioField.class));
						break;
				}
			}
			return fields;
		}
		return null;
	}
	
	private Map<FieldType, Map<Long, Map<String, Object>>> getTypeWiseExtendedProps(Map<FieldType, List<Long>> extendedIdList) throws Exception {
		Map<FieldType, Map<Long, Map<String, Object>>> extendedProps = new HashMap<>();
		for(Map.Entry<FieldType, List<Long>> entry : extendedIdList.entrySet()) {
			switch(entry.getKey()) {
				case NUMBER:
				case DECIMAL:
					extendedProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getNumberFieldModule(), FieldFactory.getNumberFieldFields(), entry.getValue()));
					break;
				case LOOKUP:
					extendedProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getLookupFieldsModule(), FieldFactory.getLookupFieldFields(), entry.getValue()));
					break;
			}
		}
		return extendedProps;
	}
	
	private Map<Long, Map<String, Object>> getExtendedProps(FacilioModule module, List<FacilioField> fields, List<Long> fieldIds) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", StringUtils.join(fieldIds, ","), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		Map<Long, Map<String, Object>> propsMap = new HashMap<>();
		for (Map<String, Object> prop : props) {
			propsMap.put((Long) prop.get("fieldId"), prop);
		}
		return propsMap;
	}
	
	@Override
	public ArrayList<FacilioField> getAllFields(String moduleName) throws Exception {
		
		if(LookupSpecialTypeUtil.isSpecialType(moduleName)) {
			return (ArrayList<FacilioField>) LookupSpecialTypeUtil.getAllFields(moduleName);
		}
		
		FacilioModule module = getMod(moduleName);
		Map<Long, FacilioModule> moduleMap = splitModules(module);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getSelectFieldFields())
														.table("Fields")
														.andCustomWhere("Fields.ORGID = ? AND Fields.MODULEID = ?", getOrgId(), module.getModuleId());
		List<Map<String, Object>> fieldProps = selectBuilder.get();
		ArrayList<FacilioField> fields = getFieldFromPropList(fieldProps, moduleMap);
		return fields;
	}
	
	@Override
	public FacilioField getField(long fieldId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getSelectFieldFields())
														.table("Fields")
														.andCustomWhere("Fields.ORGID = ? AND Fields.FIELDID = ?", getOrgId(), fieldId);
		List<Map<String, Object>> fieldProps = selectBuilder.get();
		
		if(fieldProps != null && !fieldProps.isEmpty()) {
			Map<String, Object> fieldProp = fieldProps.get(0);
			FacilioModule module = getMod((long)fieldProp.get("moduleId"));
			Map<Long, FacilioModule> moduleMap = splitModules(module);
			List<FacilioField> fields = getFieldFromPropList(fieldProps, moduleMap);
			return fields.get(0);
		}
		return null;
	}
	
	@Override
	public FacilioField getField(String fieldName, String moduleName) throws Exception {
		FacilioModule module = getMod(moduleName);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getSelectFieldFields())
														.table("Fields")
														.andCustomWhere("Fields.ORGID = ? AND Fields.NAME = ? AND Fields.MODULEID = ?", getOrgId(),fieldName, module.getModuleId());
		List<Map<String, Object>> fieldProps = selectBuilder.get();
		Map<Long, FacilioModule> moduleMap = splitModules(module);
		List<FacilioField> fields = getFieldFromPropList(fieldProps, moduleMap);
		if(fields != null && !fields.isEmpty()) {
			return fields.get(0);
		}
		return null;
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
			long fieldId = (long) fieldProps.get("id"); 
			fieldProps.put("fieldId", fieldId);
			
			switch(field.getDataTypeEnum()) {
				case NUMBER:
				case DECIMAL:
					addExtendedProps(ModuleFactory.getNumberFieldModule(), FieldFactory.getNumberFieldFields(), fieldProps);
					break;
				case LOOKUP:
					addExtendedProps(ModuleFactory.getLookupFieldsModule(), FieldFactory.getLookupFieldFields(), fieldProps);
					break;
			}
			
			return fieldId;
		}
		else {
			throw new IllegalArgumentException("Invalid field object for addition");
		}
	}
	
	private void addExtendedProps(FacilioModule module, List<FacilioField> fields, Map<String, Object> props) throws SQLException, RuntimeException {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(module.getTableName())
				.fields(fields)
				.addRecord(props);

		insertBuilder.save();
	}
	
	@Override
	public int updateField(FacilioField field) throws SQLException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if(field != null && field.getFieldId() != -1) {
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
															.table("Fields")
															.fields(FieldFactory.getUpdateFieldFields())
															.andCustomWhere("ORGID = ? AND FIELDID = ?", getOrgId(), field.getFieldId());
			
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
															.andCustomWhere("ORGID = ? AND FIELDID = ?", getOrgId(), fieldId);
			
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
					.andCustomWhere("ORGID = ?", getOrgId())
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
		addSubModule(parentModuleId, childModuleId, FacilioModule.SubModuleType.MISC);
	}
	
	@Override
	public void addSubModule(long parentModuleId, long childModuleId, FacilioModule.SubModuleType type) throws Exception {
		String sql = "INSERT INTO SubModulesRel (PARENT_MODULE_ID, CHILD_MODULE_ID, SUB_TYPE) VALUES (?, ?, ?)";
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, parentModuleId);
			pstmt.setLong(2, childModuleId);
			pstmt.setInt(3, type.getIntVal());
			
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
		String nextstatequery =" select STATE_ID,group_concat(concat('{\"Activity\":\"',ACTIVITY_NAME,'\", \"state\":\"',NEXT_STATE_ID,'\", \"StatusDesc\":\" ',STATUS,'\"}')) from TicketStateFlow,TicketStatus  where TicketStatus.ID=NEXT_STATE_ID and TicketStatus.ORGID=" + getOrgId() +" group by STATE_ID ";

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