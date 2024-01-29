package com.facilio.beans;

import com.facilio.accounts.dto.IAMUser;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsole.util.AutoNumberFieldUtil;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.MultiCurrencyConstants;
import com.facilio.db.builder.*;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.db.util.DBConf;
import com.facilio.field.validation.string.StringValidator;
import com.facilio.fms.message.Message;
import com.facilio.fw.BeanFactory;
import com.facilio.ims.endpoint.Messenger;
import com.facilio.ims.handler.AutoNumberFieldHandler;
import com.facilio.modules.*;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.fields.*;
import com.facilio.util.FacilioDateUtil;
import com.facilio.util.FacilioNumberUtil;
import com.facilio.util.FacilioUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.sql.*;
import java.text.MessageFormat;
import java.time.DayOfWeek;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


public class ModuleBeanImpl implements ModuleBean {

	private static final org.apache.log4j.Logger LOGGER = LogManager.getLogger(ModuleBeanImpl.class.getName());
	private static final String CUSTOM_MULTI_ENUM_TABLENAME = "Custom_Multi_Enum_Values";
	private static final String CUSTOM_LARGE_TEXT_TABLENAME = "Large_Text_Values";
	private static final String CUSTOM_LOOKUP_REL_RECORD_TABLENAME = "Custom_Rel_Records";

	private Connection getConnection() throws SQLException {
	//	return BeanFactory.getConnection();
		return FacilioConnectionPool.INSTANCE.getConnection();
	}

	@Override
	public long getOrgId() {
		return DBConf.getInstance().getCurrentOrgId();
	}

	protected static final String RESERVED_NULL_MODULE_NAME = "system_dummy_module_name_when_null"; //No i18n
	// This is called from getModule which is used in cacheBeanImpl alone
	private FacilioModule getModuleFromRS(ResultSet rs) throws Exception {
		FacilioModule module = new FacilioModule();
		module.setName(RESERVED_NULL_MODULE_NAME);
		boolean isFirst = true;
		FacilioModule prevModule = null;
		while(rs.next()) {
			FacilioModule currentModule = new FacilioModule();
			currentModule.setModuleId(rs.getLong("MODULEID"));
			currentModule.setOrgId(rs.getLong("ORGID"));
			currentModule.setName(rs.getString("NAME"));
			currentModule.setDisplayName(rs.getString("DISPLAY_NAME"));
			currentModule.setTableName(rs.getString("TABLE_NAME"));
			currentModule.setType(rs.getInt("MODULE_TYPE"));
			currentModule.setTrashEnabled(rs.getBoolean("IS_TRASH_ENABLED"));
			currentModule.setShowAsView(rs.getBoolean("SHOW_AS_VIEW"));
			currentModule.setDescription(rs.getString("DESCRIPTION"));
			currentModule.setCreatedTime(rs.getLong("CREATED_TIME"));
			currentModule.setHideFromParents(rs.getBoolean("HIDE_FROM_PARENTS"));
			currentModule.setStatus(rs.getBoolean("STATUS"));

			long createdById = rs.getLong("CREATED_BY");
			if (createdById > 0) {
				IAMUser user = new IAMUser();
				user.setId(createdById);
				currentModule.setCreatedBy(user);
			}
			if(prevModule != null) {
				prevModule.setExtendModule(currentModule);
			}
			prevModule = currentModule;
			int dataInterval = rs.getInt("DATA_INTERVAL");
			if (dataInterval != 0) {
				currentModule.setDataInterval(dataInterval);
			}
			currentModule.setStateFlowEnabled(rs.getBoolean("STATE_FLOW_ENABLED"));
			currentModule.setCustom(rs.getBoolean("IS_CUSTOM"));
			Long criteriaId = rs.getLong("CRITERIA_ID");
			if (criteriaId != null) {
				Criteria criteria = CriteriaAPI.getCriteria(criteriaId);
				currentModule.setCriteria(criteria);
			}

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
			 String sql = DBConf.getInstance().getQuery("module.child.id");
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, getOrgId());
			pstmt.setLong(2, moduleId);
			pstmt.setLong(3, getOrgId());

			rs = pstmt.executeQuery();

			return getModuleFromRS(rs);
		}
		catch(SQLException e) {
			LOGGER.info("Exception occurred ", e);
			throw e;
		}
		finally {
			DBUtil.closeAll(conn,pstmt, rs);
		}
	}

	@Override
	public List<FacilioModule> getModuleList(ModuleType moduleType) throws Exception {
		return getModuleList(moduleType, false);
	}

	@Override
	public List<FacilioModule> getCustomModulesWithDateFieldList() throws Exception {
		FacilioModule moduleModule = ModuleFactory.getModuleModule();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(moduleModule.getTableName())
				.select(FieldFactory.getModuleFields())
				.andCondition(CriteriaAPI.getCondition("STATUS", "status", String.valueOf(1), NumberOperators.NOT_EQUALS))
				.andCondition(CriteriaAPI.getCondition("IS_CUSTOM", "custom", String.valueOf(true), BooleanOperators.IS))
				.andCustomWhere("exists(select 1 from Fields WHERE Fields.MODULEID = Modules.MODULEID AND Fields.DATA_TYPE IN (6, 5))");

		List<Map<String, Object>> props = builder.get();
		for (Map<String, Object> prop: props) {
			if (prop.containsKey("createdBy")) {
				IAMUser user = new IAMUser();
				user.setId((long) prop.get("createdBy"));
				prop.put("createdBy", user);
			}
		}

		List<FacilioModule> modules = FieldUtil.getAsBeanListFromMapList(props, FacilioModule.class);

		for (FacilioModule module: modules) {
			List<FacilioField> modFields = getAllFields(module.getName());
			List<FacilioField> dateFields = modFields.stream().filter(i -> i.getDataType() == 6 || i.getDataType() == 5).collect(Collectors.toList());
			module.setFields(dateFields);
		}
		return modules;
	}

	@Override
	public List<FacilioModule> getModuleList(ModuleType moduleType, boolean onlyCustom) throws Exception {
		return getModuleList(moduleType, onlyCustom, null, null);
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

			 String sql = DBConf.getInstance().getQuery("module.child.name");
			 pstmt = conn.prepareStatement(sql);

			pstmt.setLong(1, getOrgId());
			pstmt.setString(2, moduleName);
			pstmt.setLong(3, getOrgId());

			rs = pstmt.executeQuery();

			return getModuleFromRS(rs);
		}
		catch(SQLException e) {
			LOGGER.info("Exception occurred ", e);
			throw e;
		}
		finally {
			DBUtil.closeAll(conn,pstmt, rs);
		}
	}

	private List<Pair<FacilioModule, Integer>> getSubModulesDeleteTypeFromRS(ResultSet rs) throws SQLException, Exception {
		List<Pair<FacilioModule, Integer>> subModules = new ArrayList<>();
		while(rs.next()) {
			FacilioModule module = getMod(rs.getLong("CHILD_MODULE_ID"));
			Integer deletType = rs.getInt("DELETE_TYPE");
			subModules.add(Pair.of(module, deletType));
		}
		return subModules;
	}

	private List<FacilioModule> getSubModulesFromRS(ResultSet rs) throws SQLException, Exception {
		// We are getting only module id here and separate fetch for modules to handle sub modules that extend some other module
		List<FacilioModule> subModules = new ArrayList<>();
		while(rs.next()) {
			subModules.add(getMod(rs.getLong("CHILD_MODULE_ID")));
		}
		return Collections.unmodifiableList(subModules);
	}

	private List<FacilioModule> getSubModulesFromRS(ResultSet rs, int permissionType) throws SQLException, Exception {
		List<FacilioModule> subModules = new ArrayList<>();
		List<Long> permittedSubModuleIds = new ArrayList<Long>();
		permittedSubModuleIds = FieldUtil.getPermissibleChildModules(getMod(rs.getLong("PARENT_MODULE_ID")), permissionType);
		while(rs.next()) {
			if(CollectionUtils.isNotEmpty(permittedSubModuleIds)){
				if(!permittedSubModuleIds.contains(rs.getLong("CHILD_MODULE_ID"))){
					continue;
				}
			}
			subModules.add(getMod(rs.getLong("CHILD_MODULE_ID")));
		}
		return Collections.unmodifiableList(subModules);
	}

	private List<FacilioModule> getSubModuleFromParent (FacilioModule parentModule, FacilioModule.ModuleType... types) throws Exception {
		FacilioUtil.throwIllegalArgumentException(parentModule == null, "Invalid module while getting sub modules");
		// We are getting only module id here and separate fetch for modules to handle sub modules that extend some other module
		String sql = null;
		if (ArrayUtils.isEmpty(types)) {
			sql = MessageFormat.format(DBConf.getInstance().getQuery("module.submodule.all"), StringUtils.join(parentModule.getExtendedModuleIds(), ","));
		}
		else {
			sql = MessageFormat.format(DBConf.getInstance().getQuery("module.submodule.type"), StringUtils.join(parentModule.getExtendedModuleIds(), ","), getTypes(types));
		}
		ResultSet rs = null;

		List<Long> childModuleIds = new ArrayList<Long>();
		try(Connection conn = getConnection();PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, getOrgId());
			rs = pstmt.executeQuery();

			while(rs.next()) {
				childModuleIds.add(rs.getLong("CHILD_MODULE_ID"));
			}
		}
		catch(Exception e) {
			LOGGER.error("Exception occurred ", e);
			throw e;
		}
		finally {
			if(rs != null) {
				try {
					rs.close();
				}
				catch(SQLException e) {
					LOGGER.info("Exception occurred ", e);
				}
			}
		}


		List<FacilioModule> subModules = new ArrayList<>();
		for(Long childModuleId : childModuleIds) {
			subModules.add(getMod(childModuleId));
		}
		return Collections.unmodifiableList(subModules);

	}

	@Override
	public List<FacilioModule> getAllSubModules(long moduleId) throws Exception {
		FacilioModule parentModule = getMod(moduleId);
		return getSubModuleFromParent(parentModule);
	}

	@Override
	public List<FacilioModule> getAllSubModules(String moduleName) throws Exception {
		if (LookupSpecialTypeUtil.isSpecialType(moduleName)) {
			return LookupSpecialTypeUtil.getAllSubModules(moduleName);
		}

		FacilioModule parentModule = getMod(moduleName);
		return getSubModuleFromParent(parentModule);
	}

	private String getTypes(FacilioModule.ModuleType... types) {
		StringJoiner joiner = new StringJoiner(",");
		for (ModuleType type : types) {
			joiner.add(String.valueOf(type.getValue()));
		}
		return joiner.toString();
	}

	@Override
	public List<Pair<FacilioModule, Integer>> getSubModulesWithDeleteType(long moduleId, FacilioModule.ModuleType... types) throws Exception {
		if (types == null || types.length == 0) {
			return null;
		}
		FacilioModule parentModule = getMod(moduleId);
		FacilioUtil.throwIllegalArgumentException(parentModule == null, "Invalid module while getting sub modules");

		// We are getting only module id here and separate fetch for modules to handle sub modules that extend some other module
		String sql = MessageFormat.format(DBConf.getInstance().getQuery("module.submodule.type"), StringUtils.join(parentModule.getExtendedModuleIds(), ","), getTypes(types));
		ResultSet rs = null;
		try(Connection conn = getConnection();PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, getOrgId());
			rs = pstmt.executeQuery();
			return getSubModulesDeleteTypeFromRS(rs);
		}
		catch(Exception e) {
			LOGGER.error("Exception occurred ", e);
			throw e;
		}
		finally {
			if(rs != null) {
				try {
					rs.close();
				}
				catch(SQLException e) {
					LOGGER.info("Exception occurred ", e);
				}
			}
		}
	}

	@Override
	public List<FacilioModule> getSubModules(long moduleId, FacilioModule.ModuleType... types) throws Exception {
		if (types == null || types.length == 0) {
			return null;
		}
		FacilioModule parentModule = getMod(moduleId);
		return getSubModuleFromParent(parentModule, types);
	}

	@Override
	public List<FacilioModule> getSubModules(String moduleName, FacilioModule.ModuleType... types) throws Exception {
		return getSubModules(moduleName, null, null, types);
	}

	@Override
	public FacilioModule getParentModule(long moduleId) throws Exception {
		FacilioModule parentModule = null;

		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getField("parentModuleId", "PARENT_MODULE_ID", FieldType.NUMBER));
		fields.add(FieldFactory.getField("childModuleId", "CHILD_MODULE_ID", FieldType.NUMBER));

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table("SubModulesRel")
														.andCustomWhere("CHILD_MODULE_ID = ?", moduleId);
		List<Map<String, Object>> fieldProps = selectBuilder.get();
		if(fieldProps != null && !fieldProps.isEmpty()) {
			Map<String, Object> prop = fieldProps.get(0);
			long parentId = (Long) prop.get("parentModuleId");
			parentModule = getMod(parentId);
		}
		return parentModule;
	}

	@Override //This doesn't fetch grand child and so on
	public List<FacilioModule> getChildModules(FacilioModule parentModule) throws Exception {
		return getChildModules(parentModule, null, null);
	}

	private FacilioModule getMod(String moduleName) throws Exception {
		return getModule(moduleName);
	}

	private FacilioModule getMod(long moduleId) throws Exception {
		return getModule(moduleId);
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
		if (module == null) {
			throw new IllegalArgumentException("No such module exists with modulename : "+moduleName);
		}
		List<Long> extendedModuleIds = module.getExtendedModuleIds();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getSelectFieldFields())
														.table("Fields")
														.andCustomWhere("Fields.ORGID = ? AND IS_MAIN_FIELD = true", getOrgId())
														.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", StringUtils.join(extendedModuleIds, ","), NumberOperators.EQUALS));

		List<Map<String, Object>> fieldProps = selectBuilder.get();

		Map<Long, FacilioModule> moduleMap = splitModules(module);
		List<FacilioField> fields = getFieldFromPropList(fieldProps, moduleMap);
		if(fields != null && !fields.isEmpty()) {
			Map<Long,FacilioField> moduleFieldMap = new HashMap<>();
			for(FacilioField field : fields){
				if(field.getModule()!=null && field.getModule().getModuleId()>0){
					moduleFieldMap.put(field.getModule().getModuleId(),field);
				}
			}
			if(CollectionUtils.isNotEmpty(extendedModuleIds) && moduleFieldMap.containsKey(extendedModuleIds.get(0))){
			     return moduleFieldMap.get(extendedModuleIds.get(0));
			}
			return fields.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public int updateModule (FacilioModule module) throws Exception {
		if (module == null) {
			throw new IllegalArgumentException("Module cannot be null for updation");
		}
		if (module.getModuleId() == -1) {
			throw new IllegalArgumentException("Invalid ID for updatuon of Module");
		}
		if ((module.getName() != null && !module.getName().isEmpty()) || (module.getTableName() != null && !module.getTableName().isEmpty()) || module.getExtendModule() != null || module.getTypeEnum() != null) {
			throw new IllegalArgumentException("Some of the specified fields cannot be updated");
		}
		StringJoiner joiner = new StringJoiner(",");
		List params = new ArrayList<>();
		if (module.getDisplayName() != null && !module.getDisplayName().isEmpty()) {
			joiner.add("DISPLAY_NAME = ?");
			params.add(module.getDisplayName());
		}
		if (module.getDataInterval() != -1) {
			joiner.add("DATA_INTERVAL = ?");
			params.add(module.getDataInterval());
		}
		if (module.getTrashEnabled() != null) {
			joiner.add("IS_TRASH_ENABLED = ?");
			params.add(module.getTrashEnabled());
		}
		if (module.getShowAsView() != null) {
			joiner.add("SHOW_AS_VIEW = ?");
			params.add(module.getShowAsView());
		}
		if (StringUtils.isNotBlank(module.getDescription())) {
			joiner.add("DESCRIPTION = ?");
			params.add(module.getDescription());
		}
		if (module.getStateFlowEnabled() != null) {
			joiner.add("STATE_FLOW_ENABLED = ?");
			params.add(module.isStateFlowEnabled());
		}
		if (module.getCriteriaId() != -1) {
			joiner.add("CRITERIA_ID = ?");
			params.add(module.getCriteriaId());
		}

		if (module.getSourceBundle() > 0) {
			joiner.add("SOURCE_BUNDLE = ?");
			params.add(module.getSourceBundle());
		}

		joiner.add("MODIFIED_TIME = ?");
		params.add(System.currentTimeMillis());

		if (!params.isEmpty()) {
			StringBuilder sql = new StringBuilder("UPDATE Modules SET ")
										.append(joiner.toString())
										.append(" WHERE ORGID = ? AND MODULEID = ?");
			params.add(getOrgId());
			params.add(module.getModuleId());
			try (Connection conn = FacilioConnectionPool.INSTANCE.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
				for(int i = 0; i < params.size(); i++) {
					pstmt.setObject(i + 1, params.get(i));
				}
				return pstmt.executeUpdate();
			}
			catch (Exception e) {
				LOGGER.info("Exception occurred ", e);
				throw e;
			}
		}
		else {
			return -1;
		}
	}

	@Override
	public List<FacilioField> getFieldFromPropList(List<Map<String, Object>> props, Map<Long, FacilioModule> moduleMap) throws Exception {

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

			List<FacilioField> fields = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				prop.put("module", moduleMap.get(prop.get("moduleId")));

				try {
					FieldType type = FieldType.getCFType((int) prop.get("dataType"));
					FacilioField field = null;
					switch(type) {
						case NUMBER:
						case DECIMAL:
								prop.putAll(extendedPropsMap.get(type).get(prop.get("fieldId")));
								field = FieldUtil.getAsBeanFromMap(prop, NumberField.class);
							break;
						case BOOLEAN:
								prop.putAll(extendedPropsMap.get(type).get(prop.get("fieldId")));
								field = FieldUtil.getAsBeanFromMap(prop, BooleanField.class);
							break;
						case LOOKUP:
								prop.putAll(extendedPropsMap.get(type).get(prop.get("fieldId")));
								Long lookupModuleId = (Long) prop.get("lookupModuleId");
								String specialType = (String) prop.get("specialType");
								if(lookupModuleId != null) {
									FacilioModule lookupModule = getMod(lookupModuleId);
									prop.put("lookupModule", lookupModule);
								}
								else if (specialType != null) {
									FacilioModule lookupModule = getMod(specialType);
									prop.put("lookupModule", lookupModule);
								}
								field = FieldUtil.getAsBeanFromMap(prop, LookupField.class);
							break;
						case MULTI_LOOKUP:
								prop.putAll(extendedPropsMap.get(type).get(prop.get("fieldId")));
								field = constructMultiLookupField(prop);
							break;
						case FILE:
								prop.putAll(extendedPropsMap.get(type).get(prop.get("fieldId")));
								field = FieldUtil.getAsBeanFromMap(prop, FileField.class);
							break;
						case ENUM:
								prop.putAll(extendedPropsMap.get(type).get(prop.get("fieldId")));
								field = FieldUtil.getAsBeanFromMap(prop, EnumField.class);
							break;
						case MULTI_ENUM:
								prop.putAll(extendedPropsMap.get(type).get(prop.get("fieldId")));
								field = constructMultiEnumField(prop);
							break;
						case LARGE_TEXT:
								prop.putAll(extendedPropsMap.get(type).get(prop.get("fieldId")));
								field = constructLargeTextField(prop);
							break;
						case SYSTEM_ENUM:
								prop.putAll(extendedPropsMap.get(type).get(prop.get("fieldId")));
								SystemEnumField enumField = FieldUtil.getAsBeanFromMap(prop, SystemEnumField.class);
								enumField.setValues(FacilioEnum.getEnumValues(enumField.getEnumName()));
								field = enumField;
								break;
						case STRING_SYSTEM_ENUM:
							prop.putAll(extendedPropsMap.get(type).get(prop.get("fieldId")));
							StringSystemEnumField systemEnumField = FieldUtil.getAsBeanFromMap(prop, StringSystemEnumField.class);
							systemEnumField.setValues(FacilioEnum.getEnumValues(systemEnumField.getEnumName()));
							field = systemEnumField;
							break;
						case LINE_ITEM:
							prop.putAll(extendedPropsMap.get(type).get(prop.get("fieldId")));
							LineItemField lineItemField = FieldUtil.getAsBeanFromMap(prop, LineItemField.class);
							FacilioModule childModule = getMod(lineItemField.getChildModuleId());
							Objects.requireNonNull(childModule == null, "Invalid module in Line item field. This is not supposed to happen");
							lineItemField.setChildModule(childModule);
							LookupField childLookupField = (LookupField) getField(lineItemField.getChildLookupFieldId());
							Objects.requireNonNull(childLookupField == null, "Invalid lookup field in in Line item field. This is not supposed to happen");
							lineItemField.setChildLookupField(childLookupField);
							field = lineItemField;
							break;
						case URL_FIELD:
							prop.putAll(extendedPropsMap.get(type).get(prop.get("fieldId")));
							field = constructSystemLookupField(prop, UrlField.class);
							break;
//						case SCORE:
//							prop.putAll(extendedPropsMap.get(type).get(prop.get("fieldId")));
//							field = FieldUtil.getAsBeanFromMap(prop, ScoreField.class);
//							break;
						case STRING:
						case BIG_STRING:
							prop.putAll (extendedPropsMap.get (type).get (prop.get ("fieldId")));
							field = FieldUtil.getAsBeanFromMap (prop,StringField.class);
							break;
						case DATE:
						case DATE_TIME:
							prop.putAll (extendedPropsMap.get (type).get (prop.get ("fieldId")));
							field = FieldUtil.getAsBeanFromMap (prop,DateField.class);
							break;
						case CURRENCY_FIELD:
							prop.putAll(extendedPropsMap.get(type).get(prop.get("fieldId")));
							field = FieldUtil.getAsBeanFromMap(prop, CurrencyField.class);
							break;
						case MULTI_CURRENCY_FIELD:
							prop.putAll(extendedPropsMap.get(type).get(prop.get("fieldId")));
							field = FieldUtil.getAsBeanFromMap(prop, MultiCurrencyField.class);
							break;
						case AUTO_NUMBER_FIELD:
							prop.putAll(extendedPropsMap.get(type).get(prop.get("fieldId")));
							field = FieldUtil.getAsBeanFromMap(prop, AutoNumberField.class);
							break;
						default:
							field = FieldUtil.getAsBeanFromMap(prop, FacilioField.class);
							break;
					}
					fields.add(field);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
					LOGGER.fatal("Exception occurred while fetching field for fieldid : "+prop.get("fieldId"), e);
					throw e;
				}
			}
			return Collections.unmodifiableList(fields);
		}
		return null;
	}

	private MultiEnumField constructMultiEnumField(Map<String, Object> props) throws Exception {
		MultiEnumField field = FieldUtil.getAsBeanFromMap(props, MultiEnumField.class);
		FacilioModule relModule = getModule(field.getRelModuleId());
		field.setRelModule(relModule);

		Map<String, FacilioField> relFields = FieldFactory.getAsMap(getAllFields(relModule.getName()));
		EnumField childEnumField = (EnumField) relFields.get(MultiEnumField.VALUE_FIELD_NAME);
		field.setEnumProps(childEnumField);

		return field;
	}

	private LargeTextField constructLargeTextField(Map<String, Object> props) throws Exception {
		LargeTextField field = FieldUtil.getAsBeanFromMap(props, LargeTextField.class);
		FacilioModule relModule = getModule(field.getRelModuleId());
		field.setRelModule(relModule);

		return field;
	}

	private <T extends BaseSystemLookupField> T constructSystemLookupField (Map<String, Object> props, Class<T> fieldClass) throws Exception {
		T field = FieldUtil.getAsBeanFromMap(props, fieldClass);
		FacilioModule relModule = getModule(field.getLookupModuleName());
		FacilioUtil.throwRunTimeException(relModule == null, MessageFormat.format("System Lookup module is null for field - {0} with type {1}. This is not supposed to happen", field.getName(), field.getDataTypeEnum()));
		field.setLookupModule(relModule);
		field.setLookupModuleId(relModule.getModuleId());

		return field;
	}

	private MultiLookupField constructMultiLookupField(Map<String, Object> props) throws Exception {
		MultiLookupField field = FieldUtil.getAsBeanFromMap(props, MultiLookupField.class);
		FacilioModule relModule = getModule(field.getRelModuleId());
		field.setRelModule(relModule);

		Map<String, FacilioField> relFields = FieldFactory.getAsMap(getAllFields(relModule.getName()));
		LookupField childField = (LookupField) relFields.get(field.childFieldName());
		field.setLookupProps(childField);

		return field;
	}

	private Map<FieldType, Map<Long, Map<String, Object>>> getTypeWiseExtendedProps(Map<FieldType, List<Long>> extendedIdList) throws Exception {
		Map<FieldType, Map<Long, Map<String, Object>>> extendedProps = new HashMap<>();
		for(Map.Entry<FieldType, List<Long>> entry : extendedIdList.entrySet()) {
			switch(entry.getKey()) {
				case NUMBER:
				case DECIMAL:
					extendedProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getNumberFieldModule(), FieldFactory.getNumberFieldFields(), entry.getValue()));
					break;
				case BOOLEAN:
					extendedProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getBooleanFieldsModule(), FieldFactory.getBooleanFieldFields(), entry.getValue()));
					break;
				case LOOKUP:
					extendedProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getLookupFieldsModule(), FieldFactory.getLookupFieldFields(), entry.getValue()));
					break;
				case MULTI_LOOKUP:
					extendedProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getMultiLookupFieldsModule(), FieldFactory.getMultiLookupFieldFields(), entry.getValue()));
					break;
				case FILE:
					extendedProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getFileFieldModule(), FieldFactory.getFileFieldFields(), entry.getValue()));
					break;
				case ENUM:
					extendedProps.put(entry.getKey(), getEnumExtendedProps(entry.getValue()));
					break;
				case MULTI_ENUM:
					extendedProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getMultiEnumFieldsModule(), FieldFactory.getMultiEnumFieldFields(), entry.getValue()));
					break;
				case SYSTEM_ENUM:
				case STRING_SYSTEM_ENUM:
					extendedProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getSystemEnumFieldModule(), FieldFactory.getSystemEnumFields(), entry.getValue()));
					break;
				case SCORE:
					extendedProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getScoreFieldModule(), FieldFactory.getScoreFieldFields(), entry.getValue()));
					break;
				case LINE_ITEM:
					extendedProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getLineItemFieldsModule(), FieldFactory.getLineItemFieldFields(), entry.getValue()));
					break;
				case LARGE_TEXT:
					extendedProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getLargeTextFieldsModule(), FieldFactory.getLargeTextFieldFields(), entry.getValue()));
					break;
				case URL_FIELD:
					extendedProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getUrlFieldsModule(), FieldFactory.getUrlFieldFields(), entry.getValue()));
					break;
				case STRING:
				case BIG_STRING:
					extendedProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getStringFieldModule(), FieldFactory.getStringFieldFields(), entry.getValue()));
					break;
				case DATE:
				case DATE_TIME:
					extendedProps.put(entry.getKey(), getDateExtendedProps(entry.getValue()));
					break;
				case CURRENCY_FIELD:
					extendedProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getCurrencyFieldsModule (), FieldFactory.getCurrencyFieldFields (), entry.getValue()));
					break;
				case MULTI_CURRENCY_FIELD:
					extendedProps.put(entry.getKey(), getExtendedProps(ModuleFactory.getCurrencyFieldsModule (), FieldFactory.getCurrencyFieldFields (), entry.getValue()));
					break;
				case AUTO_NUMBER_FIELD:
					extendedProps.put(entry.getKey(), AutoNumberFieldUtil.getAutoNumberFieldExtendedProps(entry.getValue()));
					break;
				default:
					break;
			}
		}
		return extendedProps;
	}

	private Map<Long, Map<String, Object>> getDateExtendedProps(List<Long> fieldIds) throws Exception {

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getDateFieldFields())
				.table(ModuleFactory.getDateFieldModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", StringUtils.join(fieldIds, ","), NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();

		Map<Long, List<Map<String, Object>>> map = new HashMap<>();
		Map<Long, Map<String, Object>> dateProps = new HashMap<>();

		if (CollectionUtils.isNotEmpty(props)) {

			GenericSelectRecordBuilder childRecord = new GenericSelectRecordBuilder()
					.select(FieldFactory.getDateFieldChildFields())
					.table(ModuleFactory.getDateFieldChildModule().getTableName())
					.andCondition(CriteriaAPI.getCondition("DATE_FIELD_ID", "dateFieldId", StringUtils.join(fieldIds, ","), NumberOperators.EQUALS));

			List<Map<String, Object>> childProps = childRecord.get();

			for (Map<String, Object> prop : props) {

				long fieldId = (long) prop.get("fieldId");

				if (CollectionUtils.isNotEmpty(childProps)) {
					map = childProps.stream().collect(Collectors.groupingBy(m -> (Long) m.get("dateFieldId"), Collectors.mapping(m1 -> m1, Collectors.toList())));
					List<Map<String, Object>> childList = map.get(fieldId);
					List<DayOfWeek> dayOfWeeks=new ArrayList<>();
					if (CollectionUtils.isNotEmpty (childList)){
						for (Map<String,Object> list : childList){
							String dayOfWeek = ( String ) list.get ("allowedDays");
							if (StringUtils.isNotEmpty (dayOfWeek)){
								dayOfWeeks.add (DayOfWeek.valueOf (dayOfWeek));
							}
						}
					}
					prop.put("allowedDays", CollectionUtils.isEmpty(dayOfWeeks) ? FacilioDateUtil.DAY_OF_WEEKS : dayOfWeeks);
				}else  {
					prop.put("allowedDays", FacilioDateUtil.DAY_OF_WEEKS);
				}

				dateProps.put(fieldId, prop);
			}
			return dateProps;
		}

		return Collections.EMPTY_MAP;
	}

	private Map<Long, Map<String, Object>> getExtendedProps(FacilioModule module, List<FacilioField> fields, List<Long> fieldIds) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", StringUtils.join(fieldIds, ","), NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();
		Map<Long, Map<String, Object>> propsMap = new HashMap<>();
		for (Map<String, Object> prop : props) {
			propsMap.put((Long) prop.get("fieldId"), prop);
		}
		return propsMap;
	}

	private Map<Long, Map<String, Object>> getEnumExtendedProps (List<Long> fieldIds) throws Exception {
		FacilioModule module = ModuleFactory.getEnumFieldValuesModule();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getEnumFieldValuesFields())
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", StringUtils.join(fieldIds, ","), NumberOperators.EQUALS))
														.orderBy("FIELDID, SEQUENCE_NUMBER")
														;
		List<Map<String, Object>> props = selectBuilder.get();
		Map<Long, Map<String, Object>> propsMap = new HashMap<>();
		for (Map<String, Object> prop : props) {
			Long fieldId = (Long) prop.get("fieldId");
			Map<String, Object> fieldProp = propsMap.get(fieldId);
			if (fieldProp == null) {
				List<EnumFieldValue> values = new ArrayList<>();
				values.add(FieldUtil.getAsBeanFromMap(prop, EnumFieldValue.class));
				fieldProp = Collections.singletonMap("values", values);
				propsMap.put(fieldId, fieldProp);
			}
			else {
				((List<EnumFieldValue>) fieldProp.get("values")).add(FieldUtil.getAsBeanFromMap(prop, EnumFieldValue.class));
			}
		}
		return propsMap;
	}

	@Override
	public List<FacilioField> getAllFields(String moduleName) throws Exception {
		return getAllFields(moduleName, null, null, null);
	}

	@Override
	public List<FacilioField> getAllFieldsWithDeleted(String moduleName) throws Exception {
		return null;
	}

	@Override
	public List<FacilioField> getModuleFields(String moduleName) throws Exception {

		if(LookupSpecialTypeUtil.isSpecialType(moduleName)) {
			return LookupSpecialTypeUtil.getAllFields(moduleName);
		}

		FacilioModule module = getMod(moduleName);
		Map<Long, FacilioModule> moduleMap = splitModules(module);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getSelectFieldFields())
														.table("Fields")
														.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(module.getModuleId()), NumberOperators.EQUALS));

		String skipFieldName = FieldUtil.getSkipFieldNameForModule(moduleName);
		if(StringUtils.isNotEmpty(skipFieldName)){
			selectBuilder.andCondition(CriteriaAPI.getCondition("NAME","name",skipFieldName,StringOperators.ISN_T));
		}

		List<Map<String, Object>> fieldProps = selectBuilder.get();
		List<FacilioField> fields = getFieldFromPropList(fieldProps, moduleMap);
		if (CollectionUtils.isEmpty(fields)) {
			fields = new ArrayList<>();
		}
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
	public FacilioField getField(long fieldId, long moduleId) throws Exception {
		return getField(getModule(moduleId), fieldId);
	}

	private FacilioField getField(FacilioModule facilioModule, long fieldId) throws Exception {
		List<Long> extendedModuleIds = facilioModule.getExtendedModuleIds();

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getSelectFieldFields())
														.table("Fields")
														.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", StringUtils.join(extendedModuleIds, ","), NumberOperators.EQUALS))
														.andCustomWhere("Fields.ORGID = ? AND Fields.FIELDID = ?", getOrgId(), fieldId);

		String skipFieldName = FieldUtil.getSkipFieldNameForModule(facilioModule.getName());
		if(StringUtils.isNotEmpty(skipFieldName)){
			selectBuilder.andCondition(CriteriaAPI.getCondition("NAME","name",skipFieldName,StringOperators.ISN_T));
		}

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
	public FacilioField getField(long fieldId, String moduleName) throws Exception {
		return getField(getModule(moduleName), fieldId);
	}

	@Override
	public FacilioField getReadingField(long fieldId) throws Exception {
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
			FacilioField facilioField = fields.get(0);
			if (facilioField.getModule().getTypeEnum() == ModuleType.READING) {
				return facilioField;
			}
		}
		return null;
	}

	@Override
	public List<FacilioField> getFields(Collection<Long> fieldIds) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getSelectFieldFields())
				.table("Fields")
				.andCondition(CriteriaAPI.getOrgIdCondition(getOrgId(), ModuleFactory.getFieldsModule()))
				.andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", StringUtils.join(fieldIds, ","), NumberOperators.EQUALS));
		List<Map<String, Object>> fieldProps = selectBuilder.get();

		if(fieldProps != null && !fieldProps.isEmpty()) {
			Map<Long, FacilioModule> moduleMap = new HashMap<Long, FacilioModule>();
			for(Map<String, Object> fieldProp: fieldProps) {
				FacilioModule module = getMod((long)fieldProp.get("moduleId"));
				if (!moduleMap.containsKey(module.getModuleId())) {
					moduleMap.putAll(splitModules(module));
				}
			}
			return getFieldFromPropList(fieldProps, moduleMap);
		}
		return null;
	}

	@Override
	public FacilioField getField(String fieldName, String moduleName) throws Exception {
		FacilioModule module = getMod(moduleName);

		if (fieldName.equals("id")) {
			return FieldFactory.getIdField(module);
		}

		if (FieldUtil.isSiteIdFieldPresent(module) && fieldName.equals("siteId")) {
			return FieldFactory.getSiteIdField(module);
		}

		if (FieldUtil.isSystemFieldsPresent(module) && FieldFactory.isSystemField(fieldName)) {
			return FieldFactory.getSystemField(fieldName, module.getParentModule());
		}

		if (FieldUtil.isBaseEntityRootModule(module) && FieldFactory.isBaseModuleSystemField(fieldName)) {
			return FieldFactory.getBaseModuleSystemField(fieldName, module);
		}

		if(LookupSpecialTypeUtil.isSpecialType(moduleName)) {
			return LookupSpecialTypeUtil.getField(fieldName, moduleName);
		}

		List<Long> extendedModuleIds = module.getExtendedModuleIds();

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getSelectFieldFields())
														.table("Fields")
														.andCustomWhere("Fields.ORGID = ? AND Fields.NAME = ?", getOrgId(),fieldName)
														.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", StringUtils.join(extendedModuleIds, ","), NumberOperators.EQUALS));

		String skipFieldName = FieldUtil.getSkipFieldNameForModule(moduleName);
		if(StringUtils.isNotEmpty(skipFieldName)){
			selectBuilder.andCondition(CriteriaAPI.getCondition("NAME","name",skipFieldName,StringOperators.ISN_T));
		}

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
			if (field.getDefault() == null) {
				field.setDefault(false);
			}
			if(field.getCreatedTime() <= 0) {
				field.setCreatedTime(System.currentTimeMillis());
			}
			if(field.getModifiedTime() <= 0) {
				field.setModifiedTime(field.getCreatedTime());
			}
			Map<String, Object> fieldProps = FieldUtil.getAsProperties(field);
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table("Fields")
															.fields(FieldFactory.getAddFieldFields())
															.addRecord(fieldProps);

			insertBuilder.save();
			long fieldId = (long) fieldProps.get("id");
			fieldProps.put("fieldId", fieldId);
			field.setFieldId(fieldId);
			switch(field.getDataTypeEnum()) {
				case NUMBER:
				case DECIMAL:
//					FacilioUtil.throwIllegalArgumentException(!(field instanceof NumberField),"Invalid Field instance for the "+field.getDataTypeEnum()+" data type");
					if (field instanceof NumberField){
						addNumberField((NumberField) field,fieldProps);
					}else {
						LOGGER.info ("Invalid Field instance for the "+field.getDataTypeEnum()+" data type");
						addExtendedProps(ModuleFactory.getNumberFieldModule (), FieldFactory.getNumberFieldFields (), fieldProps);
					}
					break;
				case BOOLEAN:
					if (field instanceof BooleanField) {
						BooleanField booleanField = (BooleanField) field;
						if (booleanField.getTrueVal() != null && !booleanField.getTrueVal().isEmpty() && (booleanField.getFalseVal() == null || booleanField.getFalseVal().isEmpty())) {
							throw new IllegalArgumentException("False value cannot be empty when True value is set for Boolean Field");
						}
						else if (booleanField.getFalseVal() != null && !booleanField.getFalseVal().isEmpty() && (booleanField.getTrueVal() == null || booleanField.getTrueVal().isEmpty())) {
							throw new IllegalArgumentException("True value cannot be empty when False value is set for Boolean Field");
						}
					}
					addExtendedProps(ModuleFactory.getBooleanFieldsModule(), FieldFactory.getBooleanFieldFields(), fieldProps);
					break;
				case LOOKUP:
					if (field instanceof LookupField) {
						validateLookupField((LookupField) field, fieldProps, true);
					}
					else {
						throw new IllegalArgumentException("Invalid Field instance for the LOOKUP data type");
					}
					addExtendedProps(ModuleFactory.getLookupFieldsModule(), FieldFactory.getLookupFieldFields(), fieldProps);
					break;
				case MULTI_LOOKUP:
					if (field instanceof MultiLookupField) {
						MultiLookupField multiLookupField = (MultiLookupField) field;
						validateLookupField(multiLookupField, fieldProps, false);
						if (multiLookupField.getRelModuleId() < 1) {
							if (multiLookupField.getParentFieldPositionEnum() == null) {
								multiLookupField.setParentFieldPositionEnum(MultiLookupField.ParentFieldPosition.LEFT);
								fieldProps.put("parentFieldPosition", MultiLookupField.ParentFieldPosition.LEFT.getIndex());
							}
							long relModuleId = addRelModule(multiLookupField);
							fieldProps.put("relModuleId", relModuleId);
						}
						else if (multiLookupField.getParentFieldPositionEnum() == null) {
							throw new IllegalArgumentException("Parent field position cannot be null when rel module id is already present");
						}
					}
					else {
						throw new IllegalArgumentException("Invalid Field instance for the MULTI_LOOKUP data type");
					}
					addExtendedProps(ModuleFactory.getMultiLookupFieldsModule(), FieldFactory.getMultiLookupFieldFields(), fieldProps);
					break;
				case MULTI_CURRENCY_FIELD:
					if(MultiCurrencyConstants.MULTI_CURRENCY_CUSTOM_FIELD_ADDITION_MODULES.contains(field.getModule().getName()) || (field.getModule().isCustom() && field.getModule().getTypeEnum().equals(FacilioModule.ModuleType.BASE_ENTITY))) {
						if (!(field instanceof MultiCurrencyField)) {
							throw new IllegalArgumentException("Invalid Field instance for the MULTI_CURRENCY_FIELD data type");
						}
						MultiCurrencyField multiCurrencyField = (MultiCurrencyField) field;
						if (!multiCurrencyField.getDefault()) {
							fieldProps.put("baseCurrencyValueColumnName", getBaseCurrencyValueColumn(field.getModule(), fieldId));
						}
						addExtendedProps(ModuleFactory.getCurrencyFieldsModule(), FieldFactory.getCurrencyFieldFields(), fieldProps);
					}
					else {
						throw new IllegalArgumentException("Multi Currency Fields not enabled for this Module");
					}
					break;
				case FILE:
					addExtendedProps(ModuleFactory.getFileFieldModule(), FieldFactory.getFileFieldFields(), fieldProps);
					break;
				case ENUM:
					addEnumField((EnumField) field);
					break;
				case AUTO_NUMBER_FIELD:
					addAutoNumberField((AutoNumberField) field);
					break;
				case MULTI_ENUM:
					if (field instanceof MultiEnumField) {
						MultiEnumField multiEnumField = (MultiEnumField) field;
						if (multiEnumField.getRelModuleId() < 1) {
							long relModuleId = addMultiEnumModule(multiEnumField);
							fieldProps.put("relModuleId", relModuleId);
						}
					}
					else {
						throw new IllegalArgumentException("Invalid Field instance for the MULTI_ENUM data type");
					}
					addExtendedProps(ModuleFactory.getMultiEnumFieldsModule(), FieldFactory.getMultiEnumFieldFields(), fieldProps);
					break;
				case SYSTEM_ENUM:
				case STRING_SYSTEM_ENUM:
					addExtendedProps(ModuleFactory.getSystemEnumFieldModule(), FieldFactory.getSystemEnumFields(), fieldProps);
					break;
				case SCORE:
					addExtendedProps(ModuleFactory.getScoreFieldModule(), FieldFactory.getScoreFieldFields(), fieldProps);
					break;
				case LARGE_TEXT:
					if (field instanceof LargeTextField) {
						LargeTextField largeTextField = (LargeTextField) field;
						if (largeTextField.getRelModuleId() < 1) {
							long relModuleId = addLargeTextModule(largeTextField);
							fieldProps.put("relModuleId", relModuleId);
						}
					}
					else {
						throw new IllegalArgumentException("Invalid Field instance for the LARGE_TEXT data type");
					}
					addExtendedProps(ModuleFactory.getLargeTextFieldsModule(), FieldFactory.getLargeTextFieldFields(), fieldProps);
					break;
				case LINE_ITEM:
					if (field instanceof LineItemField) {
						validateLineItemField((LineItemField) field, fieldProps);
						addExtendedProps(ModuleFactory.getLineItemFieldsModule(), FieldFactory.getLineItemFieldFields(), fieldProps);
					}
					else {
						throw new IllegalArgumentException("Invalid Field instance for the LINE_ITEM data type");
					}
					break;
				case URL_FIELD:
					if (field instanceof UrlField) {
						validateUrlField((UrlField) field, fieldProps);
						addExtendedProps(ModuleFactory.getUrlFieldsModule(), FieldFactory.getUrlFieldFields(), fieldProps);
					}
					else {
						throw new IllegalArgumentException("Invalid Field instance for the URL_FIELD data type");
					}
					break;
				case DATE:
				case DATE_TIME:
//					FacilioUtil.throwIllegalArgumentException(!(field instanceof DateField), "Invalid Field instance for the " + field.getDataTypeEnum() + " data type : " + field.getClass().getSimpleName());
					if (field instanceof DateField){
						addDateField((DateField) field, fieldProps);
					}else {
						LOGGER.info ("Invalid Field instance for the " + field.getDataTypeEnum() + " data type : " + field.getClass().getSimpleName());
						addExtendedProps(ModuleFactory.getDateFieldModule (), FieldFactory.getDateFieldFields (), fieldProps);
					}
					break;
				case STRING:
				case BIG_STRING:
//					FacilioUtil.throwIllegalArgumentException(!(field instanceof StringField), "Invalid Field instance for the " + field.getDataTypeEnum() + " data type : " + field.getClass().getSimpleName());
					if (field instanceof StringField){
						addStringField ((StringField) field, fieldProps);
					}else {
						LOGGER.info ("Invalid Field instance for the " + field.getDataTypeEnum() + " data type : " + field.getClass().getSimpleName());
						addExtendedProps(ModuleFactory.getStringFieldModule (), FieldFactory.getStringFieldFields (), fieldProps);
					}
					break;
				case CURRENCY_FIELD:
					if (!(field instanceof CurrencyField)) {
						LOGGER.info ("Invalid Field instance for the " + field.getDataTypeEnum() + " data type : " + field.getClass().getSimpleName());
					}
					addExtendedProps (ModuleFactory.getCurrencyFieldsModule (), FieldFactory.getCurrencyFieldFields (), fieldProps);
					break;
				default:
					break;
			}

			return fieldId;
		}
		else {
			throw new IllegalArgumentException("Invalid field object for addition");
		}
	}

	private void addAutoNumberField(AutoNumberField field) throws Exception {

		validateAutoNumberField(field);
		addAutoNumberFieldExtendedProps(field);
		if(field.isChangeExistingIds()){
			addOrUpdateAutoNumberFieldRecordsJob(field);
		}

	}

	private void addAutoNumberFieldExtendedProps(AutoNumberField field) throws Exception{

		FacilioModule autoNumberFieldsModule = ModuleFactory.getAutoNumberFieldModule();
		List<FacilioField> autoNumberFieldFields = FieldFactory.getAutoNumberFieldFields();

		field.setStatus(field.isChangeExistingIds() ? AutoNumberField.AutoNumberFieldStatus.INITIATED : AutoNumberField.AutoNumberFieldStatus.NOT_INITIATED);
		Map<String, Object> fieldProps = FieldUtil.getAsProperties(field);
		fieldProps.putIfAbsent(FacilioConstants.FormContextNames.ID_STARTS_FROM,field.getIdStartsFrom());

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(autoNumberFieldsModule.getTableName())
				.fields(autoNumberFieldFields)
				.addRecord(fieldProps);

		insertBuilder.save();

	}

	private void updateAutoNumberField(AutoNumberField field) throws Exception {

		validateAutoNumberField(field);

		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getAutoNumberFieldFields())
				.table(ModuleFactory.getAutoNumberFieldModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(field.getFieldId()), NumberOperators.EQUALS));

		Map<String, Object> props = selectRecordBuilder.fetchFirst();

		FacilioUtil.throwIllegalArgumentException(MapUtils.isEmpty(props), "Invalid auto number Field");

		AutoNumberField.AutoNumberFieldStatus status = AutoNumberField.AutoNumberFieldStatus.valueOf(Integer.parseInt(props.get("status").toString()));

		FacilioUtil.throwIllegalArgumentException(status == AutoNumberField.AutoNumberFieldStatus.INITIATED, "Auto number Field update already initiated.");

		boolean newChangeExistingRecords = field.isChangeExistingIds();
		long moduleId = (long) props.get("moduleId");
		long orgId = (long) props.get("orgId");
		field.setOrgId(orgId);

		FacilioModule module = getModule(moduleId);
		field.setModule(module);

		FacilioModule autoNumberFieldsModule = ModuleFactory.getAutoNumberFieldModule();
		List<FacilioField> autoNumberFieldFields = FieldFactory.getAutoNumberFieldFields();
		field.setStatus(field.isChangeExistingIds() ? AutoNumberField.AutoNumberFieldStatus.INITIATED : AutoNumberField.AutoNumberFieldStatus.NOT_INITIATED);
		Map<String, Object> fieldProps = FieldUtil.getAsProperties(field);
		fieldProps.putIfAbsent(FacilioConstants.FormContextNames.ID_STARTS_FROM,0);
		fieldProps.put(FacilioConstants.FormContextNames.LAST_AUTO_NUMBER_ID,0);

		GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
				.table(autoNumberFieldsModule.getTableName())
				.fields(autoNumberFieldFields)
				.andCondition(CriteriaAPI.getCondition("FIELDID", "fieldId", String.valueOf(field.getFieldId()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", String.valueOf(moduleId), NumberOperators.EQUALS));

		updateRecordBuilder.update(fieldProps);

		if (newChangeExistingRecords) {
			addOrUpdateAutoNumberFieldRecordsJob(field);
		}

	}

	private void addOrUpdateAutoNumberFieldRecordsJob(AutoNumberField field) throws Exception {

		long orgId = field.getOrgId();
		long fieldId = field.getFieldId();
		long moduleId = field.getModuleId();

		if (orgId > 0 && fieldId > 0 && moduleId > 0) {
			Messenger.getMessenger().sendMessage(new Message()
					.setKey(AutoNumberFieldHandler.KEY + "/" + orgId + "/" + moduleId + "/" + fieldId)
					.setOrgId(orgId)
					.setContent(FieldUtil.getAsJSON(field)));
		}

	}
	private void validateAutoNumberField(AutoNumberField field) throws Exception {

		String prefix = field.getPrefix();
		String suffix = field.getSuffix();
		int idStartsFrom = field.getIdStartsFrom();

		FacilioUtil.throwIllegalArgumentException(StringUtils.length(prefix) > 25 || StringUtils.length(suffix) > 25, "Prefix or Suffix character limit of 25 exceeds");
		FacilioUtil.throwIllegalArgumentException(idStartsFrom < 0, "Id starts from cannot be empty");

	}

	private String getBaseCurrencyValueColumn(FacilioModule module, long fieldId) throws Exception{
		String[] baseCurrencyValueColumnNames = new String[]{"BASE_CURRENCY_VALUE_CF1", "BASE_CURRENCY_VALUE_CF2", "BASE_CURRENCY_VALUE_CF3", "BASE_CURRENCY_VALUE_CF4", "BASE_CURRENCY_VALUE_CF5"};

		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getFieldFields());
		Criteria typeCriteria = new Criteria();
		typeCriteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("dataType"), String.valueOf(FieldType.MULTI_CURRENCY_FIELD.getTypeAsInt()), NumberOperators.EQUALS));
		typeCriteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("default"), String.valueOf(false), BooleanOperators.IS));
		typeCriteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("fieldId"), String.valueOf(fieldId), NumberOperators.NOT_EQUALS));
		//module id criteria, as the fields for current module only required
		typeCriteria.addAndCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleId"), String.valueOf(module.getModuleId()), NumberOperators.EQUALS));
		List<FacilioField> existingFields = getAllFields(module.getName(), null, null, typeCriteria);

		List<String> occupiedColumns = existingFields.stream()
				.map(currencyField -> ((MultiCurrencyField) currencyField).getBaseCurrencyValueColumnName())
				.collect(Collectors.toList());

		String baseCurrencyValueColumnName = Arrays.stream(baseCurrencyValueColumnNames)
				.filter(column -> occupiedColumns.isEmpty() || !occupiedColumns.contains(column))
				.findFirst()
				.orElse(null);

		FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(baseCurrencyValueColumnName), "Currency field limit exceeded");
		return baseCurrencyValueColumnName;
	}

	private void addStringField (StringField field, Map< String, Object> fieldProps) throws SQLException {
		validateString ( field );
		addExtendedProps ( ModuleFactory.getStringFieldModule (), FieldFactory.getStringFieldFields (), fieldProps );
	}

	private void validateString (StringField field) {

		int maxLen = field.getMaxLength ();
		if (maxLen > 0) {
			FacilioUtil.throwIllegalArgumentException ((field.getDataTypeEnum () == FieldType.BIG_STRING && maxLen > StringValidator.BIG_STRING_MAX_LENGTH), "String length exceeded max value");
			FacilioUtil.throwIllegalArgumentException ((maxLen > StringValidator.SHORT_STRING_MAX_LENGTH), "String length exceeded max value");
		}
	}

	private void addDateField (DateField field, Map< String, Object> fieldProps) throws Exception {

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder ().table (ModuleFactory.getDateFieldModule ().getTableName ()).fields (FieldFactory.getDateFieldFields ());

		long id = insertBuilder.insert (fieldProps);

		if (id > 0L) {
			addDateChildField (field, id);
		}
	}

	private void addDateChildField (DateField field, long id) throws SQLException {

		List< DayOfWeek > allowedDays = field.getAllowedDays ();
		if (CollectionUtils.isNotEmpty (allowedDays)) {
			List< Map< String, Object > > insertProps = new ArrayList<> ();
			allowedDays.forEach (allowedDay -> {
				Map< String, Object > fieldProps = new HashMap<> ();
				fieldProps.put ("dateFieldId", id);
				fieldProps.put ("allowedDays", allowedDay);
				insertProps.add (fieldProps);
			});

			GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder ().table (ModuleFactory.getDateFieldChildModule ().getTableName ()).fields (FieldFactory.getDateFieldChildFields ()).addRecords (insertProps);
			builder.save ();
		}
	}

	private void addNumberField (NumberField field, Map< String, Object> fieldProps) throws SQLException {
		validateNumberField(field);
		addExtendedProps(ModuleFactory.getNumberFieldModule(),FieldFactory.getNumberFieldFields(),fieldProps);
	}

	private void validateNumberField (NumberField field) {
		Double minVal = field.getMinValue ();
		Double maxVal = field.getMaxValue ();

		FacilioUtil.throwIllegalArgumentException(minVal !=null && field.getDataTypeEnum() == FieldType.NUMBER  && !FacilioNumberUtil.isNumber(minVal),"Number Type field Should not be a Decimal value");
		FacilioUtil.throwIllegalArgumentException(maxVal !=null && field.getDataTypeEnum() == FieldType.NUMBER  && !FacilioNumberUtil.isNumber(maxVal),"Number Type field Should not be a Decimal value");
		FacilioUtil.throwIllegalArgumentException( (minVal!= null && maxVal != null) && minVal.doubleValue() > maxVal.doubleValue(), "Max value " + maxVal + " cannot be less than Min value " + minVal);
	}

	private void validateUrlField (UrlField field, Map<String, Object> fieldProps) throws Exception {
//		FacilioUtil.throwIllegalArgumentException(field.getTarget() == null, "Target cannot be null while adding url field");
		if (field.getTarget() == null) {
			field.setTarget(UrlField.UrlTarget._blank);
		}
		if (field.getShowAlt() == null) {
			field.setShowAlt(false);
			fieldProps.put("showAlt", false);
		}
	}

	private void validateLineItemField (LineItemField field, Map<String, Object> fieldProps) throws Exception {
		long childModuleId = field.getChildModuleId() > 0 ? field.getChildModuleId()
								: field.getChildModule() != null ? field.getChildModule().getModuleId()
								: -1;
		FacilioUtil.throwIllegalArgumentException(childModuleId <= 0, MessageFormat.format("Invalid child module for line item field : {0}", field.getName()));
		FacilioModule childModule = getMod(childModuleId);
		FacilioUtil.throwIllegalArgumentException(childModule == null, MessageFormat.format("Invalid child module for line item field : {0}", field.getName()));
		fieldProps.put("childModuleId", childModuleId);

		long fieldId = field.getChildLookupFieldId() > 0 ? field.getChildLookupFieldId()
								: field.getChildLookupField() != null ? field.getChildLookupField().getFieldId()
								: -1;
		FacilioUtil.throwIllegalArgumentException(fieldId <= 0, MessageFormat.format("Invalid child lookup field for line item field : {0}", field.getName()));
		LookupField lookupField = (LookupField) getField(fieldId);
		FacilioUtil.throwIllegalArgumentException(lookupField == null, MessageFormat.format("Invalid child lookup field for line item field : {0}", field.getName()));
		FacilioUtil.throwIllegalArgumentException(!childModule.equals(lookupField.getModule()), MessageFormat.format("Child module ({0}) and child lookup field ({1}) doesn''t belong to same module for line item field : {2}", childModule.getName(), lookupField.getName(), field.getName()));
		FacilioUtil.throwIllegalArgumentException(!lookupField.getLookupModule().equals(field.getModule()), MessageFormat.format("Child lookup field ({0}) is having a different module ({1}) as lookup instead of {2} for line item field : {3}", lookupField.getName(), lookupField.getLookupModule().getName(), field.getModule().getName(), field.getName()));
		fieldProps.put("childLookupFieldId", fieldId);
	}

	private long addMultiEnumModule(MultiEnumField field) throws Exception {
		FacilioModule module = new FacilioModule();

		String relModuleName = new StringBuilder(field.getModule().getName())
									.append("-")
									.append(field.getName())
									.append("-multi")
									.toString();
		module.setName(relModuleName);
		String relDisplayName = new StringBuilder()
									.append(field.getModule().getDisplayName())
									.append(" ")
									.append(field.getDisplayName())
									.append(" Multi")
									.toString();

		relDisplayName = relDisplayName.length() < 150 ? relDisplayName : relDisplayName.substring(0,149);

		module.setDisplayName(relDisplayName);
		module.setTableName(CUSTOM_MULTI_ENUM_TABLENAME);
		module.setType(ModuleType.ENUM_REL_MODULE);

		long moduleId = addModule(module);
		module.setModuleId(moduleId);

		LookupField parentField = new LookupField();
		parentField.setDataType(FieldType.LOOKUP);
		parentField.setName(MultiEnumField.PARENT_FIELD_NAME);
		parentField.setDisplayName(field.getModule().getDisplayName());
		parentField.setColumnName(field.parentColumnName());
		parentField.setModule(module);
		parentField.setLookupModule(field.getModule());
		addField(parentField);

		EnumField valueField = new EnumField();
		valueField.setDataType(FieldType.ENUM);
		valueField.setName(MultiEnumField.VALUE_FIELD_NAME);
		valueField.setDisplayName(field.getDisplayName());
		valueField.setColumnName(field.valueColumnName());
		valueField.setModule(module);
		valueField.setEnumProps(field);
		addField(valueField);

		field.setRelModuleId(moduleId);
		field.setRelModule(module);
		return moduleId;
	}

	private long addLargeTextModule(LargeTextField field) throws Exception {
		FacilioModule module = new FacilioModule();

		String relModuleName = new StringBuilder(field.getModule().getName())
									.append("-")
									.append(field.getName())
									.append("-largeText")
									.toString();
		module.setName(relModuleName);

		String relDisplayName = new StringBuilder()
									.append(field.getModule().getDisplayName())
									.append(" ")
									.append(field.getDisplayName())
									.append(" Large Text")
									.toString();
		module.setDisplayName(relDisplayName);
		module.setTableName(CUSTOM_LARGE_TEXT_TABLENAME);
		module.setType(ModuleType.LARGE_TEXT_DATA_MODULE);

		long moduleId = addModule(module);
		module.setModuleId(moduleId);

		LookupField parentField = new LookupField();
		parentField.setDataType(FieldType.LOOKUP);
		parentField.setName(LargeTextField.PARENT_FIELD_NAME);
		parentField.setDisplayName(field.getModule().getDisplayName());
		parentField.setColumnName("PARENT_ID");
		parentField.setModule(module);
		parentField.setLookupModule(field.getModule());
		addField(parentField);

		NumberField valueField = new NumberField();
		valueField.setDataType(FieldType.NUMBER);
		valueField.setName(LargeTextField.FILE_FIELD_NAME);
		valueField.setDisplayName(field.getDisplayName());
		valueField.setColumnName("FILE_ID");
		valueField.setModule(module);
		addField(valueField);

		field.setRelModuleId(moduleId);
		field.setRelModule(module);
		return moduleId;
	}

	private long addRelModule(MultiLookupField field) throws Exception {
		FacilioModule module = field.getRelModule() == null ? new FacilioModule() : field.getRelModule();

		if (StringUtils.isEmpty(module.getName())) {
			String relModuleName = new StringBuilder(field.getModule().getName())
					.append("-")
					.append(field.getLookupModule().getName())
					.append("-")
					.append(field.getName())
					.append("-rel")
					.toString();
			module.setName(relModuleName);
		}

		if (StringUtils.isEmpty(module.getDisplayName())) {
			String relDisplayName = new StringBuilder()
					.append(field.getModule().getDisplayName())
					.append(" ")
					.append(field.getLookupModule().getDisplayName())
					.append(" Rel")
					.toString();
			module.setDisplayName(relDisplayName);
		}
		if (StringUtils.isEmpty(module.getTableName())) {
			module.setTableName(CUSTOM_LOOKUP_REL_RECORD_TABLENAME);
		}
		module.setType(ModuleType.LOOKUP_REL_MODULE);

		long moduleId = addModule(module);
		module.setModuleId(moduleId);

		LookupField parentField = new LookupField();
		parentField.setDataType(FieldType.LOOKUP);
		parentField.setName(field.parentFieldName());
		parentField.setDisplayName(field.getModule().getDisplayName());
		parentField.setColumnName(field.parentColumnName());
		parentField.setModule(module);
		parentField.setLookupModule(field.getModule());
		addField(parentField);

		LookupField childField = new LookupField();
		childField.setDataType(FieldType.LOOKUP);
		childField.setName(field.childFieldName());
		childField.setDisplayName(field.getLookupModule().getDisplayName());
		childField.setColumnName(field.childColumnName());
		childField.setModule(module);
		childField.setLookupProps(field);
		addField(childField);

		field.setRelModuleId(moduleId);
		field.setRelModule(module);
		return moduleId;
	}

	private void validateLookupField (BaseLookupField lookupField, Map<String, Object> fieldProps, boolean addSubModule) throws Exception {
		if(StringUtils.isEmpty(lookupField.getRelatedListDisplayName())){
			if(lookupField.getModule() == null){
				lookupField.setModule(getMod(lookupField.getModuleId()));
			}
			String relatedListDisplayName = lookupField.getModule().getDisplayName() != null ? lookupField.getModule().getDisplayName() : lookupField.getModule().getName();
			fieldProps.put("relatedListDisplayName", relatedListDisplayName);
		}
		if (StringUtils.isNotEmpty(lookupField.getSpecialType())) {
			FacilioModule module = getMod(lookupField.getSpecialType());
			if (module == null) {
				throw new IllegalArgumentException("Invalid lookup Module");
			}
			lookupField.setLookupModule(module);
		}
		else if (lookupField.getLookupModuleId() > 0 || lookupField.getLookupModule() != null) {
			FacilioModule module;
			if (lookupField.getLookupModuleId() > 0) {
				module = getMod(lookupField.getLookupModuleId());
			}
			else {
				module = getMod(lookupField.getLookupModule().getModuleId());
			}
			if (module == null) {
				throw new IllegalArgumentException("Invalid lookup Module");
			}
			lookupField.setLookupModule(module);
			fieldProps.put("lookupModuleId", module.getModuleId());

			if (addSubModule) {
				addSubModule(module.getModuleId(), lookupField.getModuleId());
			}
		}
		else {
			throw new IllegalArgumentException("Lookup module is not specified");
		}
	}

	private void addExtendedProps(FacilioModule module, List<FacilioField> fields, Map<String, Object> props) throws SQLException, RuntimeException {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(module.getTableName())
				.fields(fields)
				.addRecord(props);

		insertBuilder.save();
	}

	private int updateExtendedProps(FacilioModule module, List<FacilioField> fields, FacilioField field) throws Exception {

		long fieldId = field.getFieldId();
		field.setFieldId(-1);
		Map<String, Object> props = FieldUtil.getAsProperties(field);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(fields)
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCustomWhere("FIELDID = ?", fieldId);

		int count = updateBuilder.update(props);
		field.setFieldId(fieldId);
		return count;
	}

	private int addEnumField(EnumField field) throws Exception {
		if (field.getValues() == null || field.getValues().isEmpty()) {
			throw new IllegalArgumentException("Enum Values cannot be null during addition of Enum Field");
		}
		addEnumValues(field, field.getValues(), 1);
		return field.getValues().size();
	}

	private void addEnumValues(EnumField field, List<EnumFieldValue<Integer>> values, int startingIndex) throws Exception {
		FacilioModule module = ModuleFactory.getEnumFieldValuesModule();
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(module.getTableName())
				.fields(FieldFactory.getEnumFieldValuesFields());
		int i = 1;
		for (EnumFieldValue<Integer> enumVal : values) {
			enumVal.setFieldId(field.getFieldId());
			if (enumVal.getIndex() == null) {
				enumVal.setIndex(startingIndex++);
			}
			if (enumVal.getSequence() == -1) {
				enumVal.setSequence(i);
			}
			enumVal.setVisible(true);
			insertBuilder.addRecord(FieldUtil.getAsProperties(enumVal));
			i++;
		}
		insertBuilder.save();
		i = 0;
		for (EnumFieldValue enumVal : values) {
			enumVal.setId((Long) insertBuilder.getRecords().get(i++).get("id"));
		}
	}

	private int updateEnumVal (EnumFieldValue enumVal) throws Exception {
		FacilioModule module = ModuleFactory.getEnumFieldValuesModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
													.table(module.getTableName())
													.fields(FieldFactory.getEnumFieldValuesFields())
													.andCondition(CriteriaAPI.getIdCondition(enumVal.getId(), module))
													;
		return updateBuilder.update(FieldUtil.getAsProperties(enumVal));

	}

	private int updateMultiEnumField(MultiEnumField field) throws Exception {
		if (CollectionUtils.isEmpty(field.getValues())) {
			return 0;
		}
		FacilioModule relModule = field.getRelModule();
		if (relModule == null) {
			relModule = getModule(field.getRelModuleId());
		}
		EnumField valueField = (EnumField) getField(MultiEnumField.VALUE_FIELD_NAME, relModule.getName());
		valueField.setValues(field.getValues());
		return updateField(valueField);
	}

	private int updateEnumField(EnumField field) throws Exception {
		if (CollectionUtils.isEmpty(field.getValues())) {
			return 0;
		}
		List<EnumFieldValue<Integer>> enumsToBeAdded = new ArrayList<>();
		List<EnumFieldValue<Integer>> enumsToBeUpdated = new ArrayList<>();
		List<String> enumsValuesToBeUpdated = new LinkedList<String>();
		List<String> enumsValuesToBeAdded = new LinkedList<String>();
		int i = 1;
		int maxIndex = 1;
		for (EnumFieldValue<Integer> enumVal : field.getValues()) {
			if (enumVal.getIndex() != null && enumVal.getIndex() > maxIndex) {
				maxIndex = enumVal.getIndex();
			}
			enumVal.setSequence(i++);
			if (enumVal.getId() == -1) {
				if (enumsValuesToBeAdded.isEmpty() || !enumsValuesToBeAdded.contains(enumVal.getValue().toLowerCase())) {
				enumsToBeAdded.add(enumVal);
				enumsValuesToBeAdded.add(enumVal.getValue().toLowerCase());
				}
			}
			else {
					enumsToBeUpdated.add(enumVal);
					enumsValuesToBeUpdated.add(enumVal.getValue().toLowerCase());
			}
		}

		// To make sure the new items doesnt get presented in the older disabled items list.
		if (enumsToBeAdded != null && !enumsToBeAdded.isEmpty()) {
			List<EnumFieldValue<Integer>> itemsToBeRemoved = new ArrayList<>();
			for (EnumFieldValue<Integer> enumVal : enumsToBeAdded) {
				if (enumsValuesToBeUpdated.contains(enumVal.getValue().toLowerCase())) {
					enumsToBeUpdated.stream()
						     .filter(item -> item.getValue().equalsIgnoreCase(enumVal.getValue().toLowerCase()))
						     .collect(Collectors.toList()).get(0).setVisible(true);
					itemsToBeRemoved.add(enumVal);
				}
			}
			if (itemsToBeRemoved != null && !itemsToBeRemoved.isEmpty()) {
				enumsToBeAdded.removeAll(itemsToBeRemoved);
			}
		}


		if (!enumsToBeUpdated.isEmpty()) {
			enumsToBeUpdated.sort(Comparator.<EnumFieldValue<Integer>>comparingInt(EnumFieldValue::getSequence).reversed());
			for(EnumFieldValue<Integer> enumVal: enumsToBeUpdated) {
				updateEnumVal(enumVal);
			}
		}
		addEnumValues(field, enumsToBeAdded, maxIndex + 1);
//		deleteEnumValues(field);
		return field.getValues().size();
	}

	private void deleteEnumValues (EnumField field) throws Exception {
		FacilioModule module = ModuleFactory.getEnumFieldValuesModule();
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
														.table(module.getTableName())
//														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCustomWhere("FIELDID = ?", field.getFieldId())
														;
		deleteBuilder.delete();
	}

	private int deleteEnumVal (EnumFieldValue enumVal) throws Exception {
		FacilioModule module = ModuleFactory.getEnumFieldValuesModule();
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(enumVal.getId(), module))
				;
		return deleteBuilder.delete();

	}

	@Override
	public int updateField(FacilioField field) throws Exception {
		if(field != null && field.getFieldId() != -1) {
			long fieldId = field.getFieldId();
			field.setFieldId(-1);
			field.setModifiedTime(System.currentTimeMillis());
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
															.table("Fields")
															.fields(FieldFactory.getUpdateFieldFields())
															.andCustomWhere("ORGID = ? AND FIELDID = ?", getOrgId(), fieldId);

			Map<String, Object> fieldProps = FieldUtil.getAsProperties(field);
			int count = updateBuilder.update(fieldProps);
			field.setFieldId(fieldId);

			int extendendPropsCount = 0;

			if (field instanceof NumberField) {
				extendendPropsCount = updateExtendedProps(ModuleFactory.getNumberFieldModule(), FieldFactory.getNumberFieldFields(), field);
			}
			else if (field instanceof BooleanField) {
				extendendPropsCount = updateExtendedProps(ModuleFactory.getBooleanFieldsModule(), FieldFactory.getBooleanFieldFields(), field);
			}
			else if (field instanceof FileField) {
				extendendPropsCount = updateExtendedProps(ModuleFactory.getFileFieldModule(), FieldFactory.getFileFieldFields(), field);
			}
			else if (field instanceof EnumField) {
				extendendPropsCount = updateEnumField((EnumField) field);
			}
			else if (field instanceof MultiEnumField) {
				extendendPropsCount = updateMultiEnumField((MultiEnumField) field);
			}
			else if (field instanceof UrlField) {
				extendendPropsCount = updateExtendedProps(ModuleFactory.getUrlFieldsModule(), FieldFactory.getUrlFieldFields(), field);
			}
			else if (field instanceof StringField){
				extendendPropsCount = updateExtendedProps (ModuleFactory.getStringFieldModule (),FieldFactory.getStringFieldFields (),field);
			}
			else if (field instanceof DateField){
				extendendPropsCount = updateDateField((DateField) field);
			}
			else if (field instanceof CurrencyField){
				extendendPropsCount = updateExtendedProps(ModuleFactory.getCurrencyFieldsModule (), FieldFactory.getCurrencyFieldFields (), field);
			}
			else if (field instanceof LargeTextField){
				extendendPropsCount = updateExtendedProps(ModuleFactory.getLargeTextFieldsModule(), FieldFactory.getLargeTextFieldFields(), field);
			}
			else if(field instanceof LookupField){
				// will allow only changes to relatedListDisplayName field, extended prop of lookup
				List<FacilioField> lookupFieldFields = FieldFactory.getLookupFieldFields();
				Map<String, FacilioField> lookupFieldFieldsMap = FieldFactory.getAsMap(lookupFieldFields);
				FacilioField relatedListDisplayName = lookupFieldFieldsMap.get("relatedListDisplayName");
				extendendPropsCount = updateExtendedProps(ModuleFactory.getLookupFieldsModule(), Collections.singletonList(relatedListDisplayName), field);
			} else if (field instanceof AutoNumberField) {
				updateAutoNumberField((AutoNumberField) field);
			}
//			else if (field instanceof ScoreField) {
//				extendendPropsCount = updateExtendedProps(ModuleFactory.getScoreFieldModule(), FieldFactory.getScoreFieldFields(), field);
//			}
			// Will not allow change of extended props of lookup, multi-lookup and line-item fields
			return Math.max(count, extendendPropsCount);
		}
		else {
			throw new IllegalArgumentException("Invalid field object for Updation");
		}
	}

	private int updateDateField (DateField field) throws Exception {

		long fieldId = field.getFieldId ();
		field.setFieldId (-1);

		Map< String, Object > props = new HashMap<> ();

		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder ().table (ModuleFactory.getDateFieldModule ().getTableName ()).fields (FieldFactory.getDateFieldFields ()).andCondition (CriteriaAPI.getCondition ("FIELDID", "fieldId", String.valueOf (fieldId), NumberOperators.EQUALS));

		int count = updateBuilder.update (props);

		if (count > 0) {

			GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder ().table (ModuleFactory.getDateFieldChildModule ().getTableName ()).andCondition (CriteriaAPI.getCondition ("DATE_FIELD_ID", "dateFieldId", String.valueOf (fieldId), NumberOperators.EQUALS));

			int deleteCount = deleteBuilder.delete ();

			if (deleteCount > 0) {
				addDateChildField (field, fieldId);
			}
		}
		field.setFieldId (fieldId);

		return count;
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
	public int deleteModule(String moduleName) throws Exception {
		// TODO Auto-generated method stub
		FacilioModule module = getMod(moduleName);
		List<FacilioField> fields = getAllFields(moduleName);
		List<Long> fieldIds = fields.stream().map(FacilioField::getId).collect(Collectors.toList());
		deleteFields(fieldIds);

		String sql = "DELETE FROM Modules WHERE ORGID = ? AND MODULEID = ?";
		try(Connection conn = getConnection();PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, getOrgId());
			pstmt.setLong(2, module.getModuleId());

			return pstmt.executeUpdate();
		}
		catch (Exception e) {
			throw e;
		}
	}

	@Override
	public long addModule(FacilioModule module) throws Exception {

		if(module == null) {
			throw new IllegalArgumentException("Invalid Module for insertion");
		}

		if(module.getName() == null || module.getName().isEmpty() || module.getName().equals(RESERVED_NULL_MODULE_NAME) || module.getTableName() == null || module.getTableName().isEmpty()) {
			throw new IllegalArgumentException("Invalid Module Name/ Module table Name");
		}

		if (module.getTypeEnum() == null) {
			throw new IllegalArgumentException("Module Type cannot be null during addition of modules");
		}

		String sql = "INSERT INTO Modules (ORGID, NAME, DISPLAY_NAME, TABLE_NAME, EXTENDS_ID, HIDE_FROM_PARENTS, MODULE_TYPE, DATA_INTERVAL, DESCRIPTION, CREATED_BY, CREATED_TIME, STATE_FLOW_ENABLED, IS_CUSTOM, IS_TRASH_ENABLED, MODIFIED_TIME, CRITERIA_ID, STATUS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		ResultSet rs = null;
		try (Connection conn = FacilioConnectionPool.INSTANCE.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setLong(1, getOrgId());
			pstmt.setString(2, module.getName());

			if (module.getDisplayName() != null && !module.getDisplayName().isEmpty()) {
				pstmt.setString(3, module.getDisplayName());
			}
			else {
				pstmt.setNull(3, Types.VARCHAR);
			}

			pstmt.setString(4, module.getTableName());

			if (module.getExtendModule() != null) {
				pstmt.setLong(5, module.getExtendModule().getModuleId());
			}
			else {
				pstmt.setNull(5, Types.BIGINT);
			}

			if (module.getHideFromParents() != null) {
				pstmt.setBoolean(6, module.hideFromParents());
			}
			else {
				pstmt.setNull(6, Types.BOOLEAN);
			}

			pstmt.setInt(7, module.getType());

			if (module.getDataInterval() != -1) {
				pstmt.setInt(8, module.getDataInterval());
			}
			else {
				pstmt.setNull(8, Types.INTEGER);
			}

			if (StringUtils.isNotEmpty(module.getDescription())) {
				pstmt.setString(9, module.getDescription());
			}
			else {
				pstmt.setNull(9, Types.VARCHAR);
			}

			if (module.isCustom() && DBConf.getInstance().getCurrentUser() != null) {
				pstmt.setLong(10, DBConf.getInstance().getCurrentUserId());
			}
			else {
				pstmt.setNull(10, Types.BIGINT);
			}

			long currentTime = System.currentTimeMillis();

			if(module.getCreatedTime() <= 0) {
				module.setCreatedTime(currentTime);
			}
			if(module.getModifiedTime() <= 0) {
				module.setModifiedTime(currentTime);
			}

			pstmt.setLong(11, module.getCreatedTime());

			pstmt.setBoolean(12, module.isStateFlowEnabled());
			pstmt.setBoolean(13, module.isCustom());

			if (module.getTrashEnabled() != null) {
				pstmt.setBoolean(14, module.isTrashEnabled());
			}
			else {
				pstmt.setNull(14, Types.BOOLEAN);
			}

			pstmt.setLong(15, module.getModifiedTime());

			if (module.getCriteriaId() > 0) {
				pstmt.setLong(16, module.getCriteriaId());
			}
			else {
				pstmt.setNull(16, Types.BIGINT);
			}
			if (module.isModuleHidden()) {
				pstmt.setBoolean(17, module.isModuleHidden());
			} else {
				pstmt.setNull(17, Types.BOOLEAN);
			}

			if (pstmt.executeUpdate() < 1) {
				throw new Exception("Unable to add Module");
			}
			else {
				rs = pstmt.getGeneratedKeys();
				rs.next();
				long moduleId = rs.getLong(1);
				return moduleId;
			}
		}
		catch(Exception e) {
			LOGGER.info("Exception occurred ", e);
			throw e;
		}
		finally {
			if(rs != null) {
				try {
					rs.close();
				}
				catch(SQLException e) {
					LOGGER.info("Exception occurred ", e);
				}
			}
		}
	}

	@Override
	public void addSubModule(long parentModuleId, long childModuleId) throws Exception {
		addSubModule(parentModuleId, childModuleId, -1);
	}

	/**
	 * @param deleteType values:
	 * 	- RESTRICT = 0
	 * 	- SET_NULL = 1
	 * 	- CASCADE  = 2
	 * @param parentModuleId
	 * @param childModuleId
	 * @param deleteType
	 * @throws Exception
	 */
	@Override
	public void addSubModule(long parentModuleId, long childModuleId, int deleteType) throws Exception {

		Map<String, Object> prop = new HashMap<>();
		prop.put("parentModuleId",parentModuleId);
		prop.put("childModuleId", childModuleId);
		if(deleteType >= 0) {
			prop.put("deleteType", deleteType);
		}

		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.table("SubModulesRel")
				.select(FieldFactory.getSubModuleRelFields())
				.andCondition(CriteriaAPI.getCondition("PARENT_MODULE_ID", "parentModuleId", String.valueOf(parentModuleId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("CHILD_MODULE_ID", "childModuleId", String.valueOf(childModuleId), NumberOperators.EQUALS))
				;
		Map<String, Object> existingRecord = selectRecordBuilder.fetchFirst();
		// if existing is not found add
		if (existingRecord == null) {
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table("SubModulesRel")
					.fields(FieldFactory.getSubModuleRelFields());

			insertBuilder.addRecord(prop);
			insertBuilder.save();
		}
	}

	@Override
	public JSONObject getStateFlow(String module) throws Exception {
	//String query = "select STATE_ID,Ticke                                                                                                        tStatus.STATUS , GROUP_CONCAT(concat('{\"',NEXT_STATE_ID,'\":','\"',ts2.STATUS,'\"}')) from TicketStateFlow , TicketStatus, TicketStatus ts2 where TicketStatus.ID=TicketStateFlow.STATE_ID and TicketStateFlow.NEXT_STATE_ID=ts2.ID  group by STATE_ID";

		//FacilioModule fm = getModule("ticketstatus");
		String nextstatequery =" select STATE_ID,group_concat(concat('{\"Activity\":\"',ACTIVITY_NAME,'\", \"state\":\"',NEXT_STATE_ID,'\", \"StatusDesc\":\" ',STATUS,'\"}')) from TicketStateFlow,TicketStatus  where TicketStatus.ID=NEXT_STATE_ID and TicketStatus.ORGID=" + getOrgId() +" group by STATE_ID ";

		try(java.sql.Connection con = FacilioConnectionPool.getInstance().getConnection();
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(nextstatequery);) {

			JSONObject stateflow =new JSONObject();
			while (rs.next())
			{
				String oldstate = rs.getString(1);
				String nextstates = rs.getString(2);
				JSONArray nextstats =(JSONArray) new JSONParser().parse("["+ nextstates +"]");


				stateflow.put(oldstate, nextstats);
			}
			return stateflow;
		}

	}

	@Override
	public FacilioField getFieldFromDB(long fieldId) throws Exception {
		return this.getField(fieldId);
	}

	@Override
	public List<FacilioField> getAllCustomFields(String moduleName) throws Exception {
		// TODO Auto-generated method stub

		if(LookupSpecialTypeUtil.isSpecialType(moduleName)) {
			return LookupSpecialTypeUtil.getAllFields(moduleName);
		}

		FacilioModule module = getMod(moduleName);
		Map<Long, FacilioModule> moduleMap = splitModules(module);

		List<Long> extendedModuleIds = module.getExtendedModuleIds();

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(FieldFactory.getSelectFieldFields())
														.table("Fields")
														.andCondition(CriteriaAPI.getCondition("MODULEID", "moduleId", StringUtils.join(extendedModuleIds, ","), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition("IS_DEFAULT", "default", String.valueOf(false),BooleanOperators.IS));
		List<Map<String, Object>> fieldProps = selectBuilder.get();
		List<FacilioField> fields = getFieldFromPropList(fieldProps, moduleMap);
		return fields;
	}


	@Override
	public List<FacilioModule> getPermissibleSubModules(long moduleId, int permissionType) throws Exception {
		String sql = DBConf.getInstance().getQuery("module.submodule.all.id");
		ResultSet rs = null;
		try(Connection conn = getConnection();PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, getOrgId());
			pstmt.setLong(2, moduleId);
			pstmt.setLong(3, getOrgId());
			pstmt.setLong(4, moduleId);
			pstmt.setLong(5, getOrgId());
			rs = pstmt.executeQuery();
			//all sub modules are permissible to super admin
			if(AccountUtil.getCurrentUser().isSuperAdmin()) {
				return getSubModulesFromRS(rs);
			}
			return getSubModulesFromRS(rs, permissionType);
		}
		catch(Exception e) {
			LOGGER.info("Exception occurred ", e);
			throw e;
		}
		finally {
			if(rs != null) {
				try {
					rs.close();
				}
				catch(SQLException e) {
					LOGGER.info("Exception occurred ", e);
				}
			}
		}
	}

	@Override
	public List<Long> getPermissibleFieldIds(FacilioModule module, int permissionType) throws Exception {

		List<Long> permissibleFieldIds = new ArrayList<Long>();

		//get extended module permissable fields also
		FacilioModule extendedModule = module.getExtendModule();
		List<Long> extendedModuleIds = new ArrayList<Long>();
		while(extendedModule != null) {
			extendedModuleIds.add(extendedModule.getModuleId());
			extendedModule = extendedModule.getExtendModule();
		}
		extendedModuleIds.add(module.getModuleId());
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFieldModulePermissionFields())
				.table(ModuleFactory.getFieldModulePermissionModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", StringUtils.join(extendedModuleIds, ","), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("CHECK_TYPE", "checkType", String.valueOf(FieldPermissionContext.CheckType.FIELD.getIndex()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("FIELD_ID", "fieldId", "1", CommonOperators.IS_NOT_EMPTY));

		if(AccountUtil.getCurrentUser().getRoleId() > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("ROLE_ID", "roleId", String.valueOf(AccountUtil.getCurrentUser().getRoleId()), NumberOperators.EQUALS));
		}

		if(permissionType == FieldPermissionContext.PermissionType.READ_ONLY.getIndex()) {
			String permVal = FieldPermissionContext.PermissionType.READ_ONLY.getIndex()+"," + FieldPermissionContext.PermissionType.READ_WRITE.getIndex();
			selectBuilder.andCondition(CriteriaAPI.getCondition("PERMISSION_TYPE", "permissionType", permVal, NumberOperators.EQUALS));
		}
		else {
			selectBuilder.andCondition(CriteriaAPI.getCondition("PERMISSION_TYPE", "permissionType", String.valueOf(permissionType), NumberOperators.EQUALS));
		}


		List<Map<String, Object>> props = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(props)) {
			for(Map<String,Object> map :props) {
				permissibleFieldIds.add((Long) map.get("fieldId"));
			}
		}
		return permissibleFieldIds;

	}

	@Override
    public Map<FacilioModule, List<FacilioField>> getRelatedLookupFields(long moduleId) throws Exception {
		List<FacilioModule> subModules = getSubModules(moduleId,
						FacilioModule.ModuleType.CUSTOM, FacilioModule.ModuleType.BASE_ENTITY);
		Map<FacilioModule, List<FacilioField>> relatedList = null;
		if (CollectionUtils.isNotEmpty(subModules)) {
				relatedList = new HashMap<>();
				for (FacilioModule subModule : subModules) {
						List<FacilioField> allFields = getAllFields(subModule.getName());
						List<FacilioField> fields = allFields.stream().filter(field -> (field instanceof LookupField && ((LookupField) field).getLookupModuleId() == moduleId)).collect(Collectors.toList());
						if (CollectionUtils.isNotEmpty(fields)) {
								relatedList.put(subModule, fields);
						}
					}
			}
		return relatedList;
	}

	@Override
	public List<FacilioModule> getModuleList(List<String> moduleNames) throws Exception {
		List<FacilioModule> moduleList = new ArrayList<>();
		FacilioModule moduleModule = ModuleFactory.getModuleModule();
		List<FacilioField> moduleFields = FieldFactory.getModuleFields();
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(moduleFields);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(moduleModule.getTableName())
				.select(moduleFields)
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("name"), StringUtils.join(moduleNames, ","), StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("status"), String.valueOf(1), NumberOperators.NOT_EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();
		for (Map<String, Object> prop : props) {
			if (prop.containsKey("createdBy")) {
				IAMUser user = new IAMUser();
				user.setId((long) prop.get("createdBy"));
				prop.put("createdBy", user);
			}
		}
		moduleList = FieldUtil.getAsBeanListFromMapList(props, FacilioModule.class);

		return moduleList;
	}

	@Override
	public List<FacilioModule> getModuleList(Criteria criteria) throws Exception {
		FacilioModule moduleModule = ModuleFactory.getModuleModule();
		List<FacilioField> moduleFields = FieldFactory.getModuleFields();
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(moduleFields);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(moduleModule.getTableName())
				.select(moduleFields)
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("status"), String.valueOf(1), NumberOperators.NOT_EQUALS));

		if (criteria != null && !criteria.isEmpty()) {
			selectBuilder.andCriteria(criteria);
		}

		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isEmpty(props)) {
			return null;
		}

		for (Map<String, Object> prop : props) {
			if (prop.containsKey("createdBy")) {
				IAMUser user = new IAMUser();
				user.setId((long) prop.get("createdBy"));
				prop.put("createdBy", user);
			}
		}
		List<FacilioModule> moduleList = FieldUtil.getAsBeanListFromMapList(props, FacilioModule.class);
		Map<Long, FacilioModule> moduleIdVsObj = moduleList.stream().collect(Collectors.toMap(FacilioModule::getModuleId, Function.identity()));
		int index = 0;
		for (Map<String, Object> prop : props) {
			if(prop.containsKey("extendsId") && prop.get("extendsId") != null) {
				moduleList.get(index).setExtendModule(moduleIdVsObj.get((Long)prop.get("extendsId")));
			}
			index++;
		}
		return moduleList;
	}

	@Override
	public List<FacilioModule> getModuleList(ModuleType moduleType, boolean onlyCustom, JSONObject pagination, String searchString) throws Exception {
		List<FacilioModule> moduleList = new ArrayList<>();
		FacilioModule moduleModule = ModuleFactory.getModuleModule();
		List<FacilioField> moduleFields = FieldFactory.getModuleFields();
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(moduleFields);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(moduleModule.getTableName())
				.select(moduleFields)
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("type"), String.valueOf(moduleType.getValue()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("status"), String.valueOf(1), NumberOperators.NOT_EQUALS));

		if (onlyCustom) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("IS_CUSTOM", "custom", String.valueOf(onlyCustom), BooleanOperators.IS));
		}

		if (StringUtils.isNotEmpty(searchString)) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("displayName"), searchString, StringOperators.CONTAINS));
		}
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");
			int offset = ((page-1) * perPage);
			if (offset < 0) {
				offset = 0;
			}
			selectBuilder.offset(offset);
			selectBuilder.limit(perPage);
		}
		selectBuilder.orderBy(fieldsMap.get("moduleId").getCompleteColumnName() + " ASC");

		List<Map<String, Object>> props = selectBuilder.get();
		for (Map<String, Object> prop : props) {
			if (prop.containsKey("createdBy")) {
				IAMUser user = new IAMUser();
				user.setId((long) prop.get("createdBy"));
				prop.put("createdBy", user);
			}
		}
		moduleList = FieldUtil.getAsBeanListFromMapList(props, FacilioModule.class);

		return moduleList;
	}

	@Override
	public List<FacilioField> getAllFields(String moduleName, JSONObject pagination, String searchString, Criteria filterCriteria) throws Exception {

		if(LookupSpecialTypeUtil.isSpecialType(moduleName)) {
			return LookupSpecialTypeUtil.getAllFields(moduleName);
		}
		List<FacilioField> fields = new ArrayList<>();
		FacilioModule module = getMod(moduleName);
		FacilioUtil.throwIllegalArgumentException(module == null, "Invalid module while getting module");
		Map<Long, FacilioModule> moduleMap = splitModules(module);
		List<Long> extendedModuleIds = module.getExtendedModuleIds();
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(FieldFactory.getFieldFields());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getSelectFieldFields())
				.table("Fields")
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("moduleId"), StringUtils.join(extendedModuleIds, ","), NumberOperators.EQUALS))
				.andCustomWhere(CriteriaAPI.getCurrentBuildVersionCriteria());

		if (filterCriteria != null && !filterCriteria.isEmpty()) {
			selectBuilder.andCriteria(filterCriteria);
		}

		if (StringUtils.isNotEmpty(searchString)) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("displayName"), searchString, StringOperators.CONTAINS));
		}

		String skipFieldName = FieldUtil.getSkipFieldNameForModule(moduleName);
		if(StringUtils.isNotEmpty(skipFieldName)){
			selectBuilder.andCondition(CriteriaAPI.getCondition("NAME","name",skipFieldName,StringOperators.ISN_T));
		}

		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");
			int offset = ((page-1) * perPage);
			if (offset < 0) {
				offset = 0;
			}
			selectBuilder.offset(offset);
			selectBuilder.limit(perPage);
		}

		List<Map<String, Object>> fieldProps = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(fieldProps)) {
			fields = getFieldFromPropList(fieldProps, moduleMap);
		}
		return fields;
	}

	@Override
	public List<FacilioModule> getSubModules(String moduleName, JSONObject pagination, String searchString, FacilioModule.ModuleType... types) throws Exception {
		if (types == null || types.length == 0) {
			return null;
		}
		if (LookupSpecialTypeUtil.isSpecialType(moduleName)) {
			return LookupSpecialTypeUtil.getSubModules(moduleName, types);
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule parentModule = modBean.getModule(moduleName);
		return getSubModuleFromParent(parentModule, pagination, searchString, types);
	}

	private List<FacilioModule> getSubModuleFromParent(FacilioModule parentModule, JSONObject pagination, String searchString, FacilioModule.ModuleType... types) throws Exception {
		FacilioUtil.throwIllegalArgumentException(parentModule == null, "Invalid module while getting sub modules");

		List<FacilioModule> subModules = new ArrayList<>();
		FacilioModule moduleModule = ModuleFactory.getModuleModule();
		FacilioModule relModule = ModuleFactory.getSubModulesRelModule();
		Map<String, FacilioField> modFieldsMap = FieldFactory.getAsMap(FieldFactory.getModuleFields());
		Map<String, FacilioField> relModFieldsMap = FieldFactory.getAsMap(FieldFactory.getSubModuleRelFields());

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(relModule.getTableName())
				.select(FieldFactory.getSubModuleRelFields())
				.innerJoin(moduleModule.getTableName())
				.on(relModule.getTableName() + ".CHILD_MODULE_ID = " + moduleModule.getTableName() + ".MODULEID")
				.andCondition(CriteriaAPI.getCondition(relModFieldsMap.get("parentModuleId"), parentModule.getExtendedModuleIds(), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(modFieldsMap.get("type"), getTypes(types), StringOperators.IS));

		if (StringUtils.isNotEmpty(searchString)) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(modFieldsMap.get("displayName"), searchString, StringOperators.CONTAINS));
		}
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");
			int offset = ((page - 1) * perPage);
			if (offset < 0) {
				offset = 0;
			}
			selectBuilder.offset(offset);
			selectBuilder.limit(perPage);
		}
		selectBuilder.orderBy(relModFieldsMap.get("parentModuleId").getCompleteColumnName() + " ASC");

		List<Map<String, Object>> props = selectBuilder.get();

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if (CollectionUtils.isNotEmpty(props)) {
			for (Map<String, Object> prop : props) {
				long moduleId = (long) prop.get("childModuleId");
				subModules.add(modBean.getModule(moduleId));
			}
		}

		return subModules;
	}

	@Override
	public List<FacilioModule> getChildModules(FacilioModule parentModule, JSONObject pagination, String searchString) throws Exception {
		return getChildModules(parentModule, pagination, searchString, true);
	}

	@Override
	public List<FacilioModule> getChildModules(FacilioModule parentModule, JSONObject pagination, String searchString, boolean fetchHideFromParentModules) throws Exception {
		if(LookupSpecialTypeUtil.isSpecialType(parentModule.getName())) {
			return null;
		}

		List<FacilioModule> childModules = new ArrayList<>();
		FacilioModule moduleModule = ModuleFactory.getModuleModule();
		List<FacilioField> moduleFields = FieldFactory.getModuleFields();
		Map<String, FacilioField> fieldsMap = FieldFactory.getAsMap(moduleFields);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(moduleModule.getTableName())
				.select(moduleFields)
				.andCondition(CriteriaAPI.getCondition(fieldsMap.get("extendsId"), String.valueOf(parentModule.getModuleId()), NumberOperators.EQUALS));

		if (!fetchHideFromParentModules) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("hideFromParents"), String.valueOf(false), BooleanOperators.IS));
		}

		if (StringUtils.isNotEmpty(searchString)) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldsMap.get("displayName"), searchString, StringOperators.CONTAINS));
		}
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");
			int offset = ((page - 1) * perPage);
			if (offset < 0) {
				offset = 0;
			}
			selectBuilder.offset(offset);
			selectBuilder.limit(perPage);
		}
		selectBuilder.orderBy(fieldsMap.get("moduleId").getCompleteColumnName() + " ASC");

		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			childModules = FieldUtil.getAsBeanListFromMapList(props, FacilioModule.class);
		}

		return childModules;
	}
}
