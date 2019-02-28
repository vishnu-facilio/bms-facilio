package com.facilio.bmsconsole.modules;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.util.FacilioUtil;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class FieldUtil {
	
	private static final Logger LOGGER = LogManager.getLogger(FieldUtil.class.getName());
	
	public static final String NUMBER_FIELD_UNIT_SUFFIX = "Unit";
	public static Map<String, Object> getLookedUpProp(long id) {
		Map<String, Object> prop = new HashMap<>();
		prop.put("id", id);
		return prop;
	}
	
	public static void castOrParseValueAsPerType(PreparedStatement pstmt, int paramIndex, FacilioField field, Object value) throws SQLException {
		FieldType type = field.getDataTypeEnum();
		
//		if((type.equals(FieldType.DECIMAL) || type.equals(FieldType.NUMBER)) && field instanceof NumberField) {
//			
//			NumberField numberField = (NumberField) field;
//			value = UnitsUtil.convertToSiUnit(value, numberField.getUnitEnum());
//		}
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
					double val = FacilioUtil.parseDouble(value);
					if(val != -1 && val != -99) {
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
						BooleanField booleanField = (BooleanField) field;
						String val = value.toString().trim();
						if ((booleanField.getTrueVal() != null && booleanField.getTrueVal().equalsIgnoreCase(val)) || (NumberUtils.isCreatable(val) && new Double(val) > 0)) {
							pstmt.setBoolean(paramIndex, true);
						}
						else {
							pstmt.setBoolean(paramIndex, Boolean.valueOf(val));
						}
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
			case FILE:
			case COUNTER:
			case ID:
				if(value != null && !(value instanceof String && ((String)value).isEmpty())) {
					long val = FacilioUtil.parseLong(value);
					if(val != -1 && val != -99) {
						pstmt.setLong(paramIndex, val);
					}
					else {
						pstmt.setNull(paramIndex, Types.BIGINT);
					}
				}
				else {
					pstmt.setNull(paramIndex, Types.BIGINT);
				}
				break;
			case ENUM:
				pstmt.setNull(paramIndex, Types.INTEGER);
				if(value != null && !(value instanceof String && ((String)value).isEmpty())) {
					EnumField enumField = (EnumField) field;
					if (value instanceof Integer) {
						int val = (int) value;
						if (enumField.getValue(val) != null) {
							pstmt.setInt(paramIndex, val);
						}
					}
					else if (value instanceof Long) {
						int val = ((Long) value).intValue();
						if (enumField.getValue(val) != null) {
							pstmt.setInt(paramIndex, val);
						}
					}
					else {
						int val = enumField.getIndex(value.toString());
						if (val != -1) {
							pstmt.setInt(paramIndex, val);
						}
					}
				}
				break;
			case MISC:
			default:
					pstmt.setObject(paramIndex, value);
					break;
		}
	}
	
	public static Object getObjectFromRS (FacilioField field, ResultSet rs) throws SQLException {
		if (field.getDataTypeEnum() != null) { //Temp Fix
			Object val = rs.getObject(field.getName());
			switch (field.getDataTypeEnum()) {
				case BOOLEAN:
					if (val != null) {
						return rs.getBoolean(field.getName());
					}
					else {
						return val;
					}
				case NUMBER:
					if (val instanceof Short) {
						return ((Number) val).intValue();
					}
				default:
					return val;
			}
		}
		else {
			LOGGER.log(Level.DEBUG, "Data type shouldn't be null\n"+CommonCommandUtil.getStackTraceString(Thread.currentThread().getStackTrace()));
			return rs.getObject(field.getName());
		}
	}
	
	public static Object castOrParseValueAsPerType(FieldType type, Object value)  {
		switch (type) {
			case LOOKUP:
			case ENUM:
				throw new IllegalArgumentException("Unsupported DataType. Field Object is required for these types and cannot be used directly");
			default:
				FacilioField field = new FacilioField();
				field.setDataType(type);
				return castOrParseValueAsPerType(field, value);
		}
	}
	
	public static Object castOrParseValueAsPerType(FacilioField field, Object value)  {
		switch(field.getDataTypeEnum()) {
			case STRING:
				if(value != null && !(value instanceof String && ((String)value).isEmpty())) {
					if(!(value instanceof String)) {
						value= value.toString();
					}
				}
				else {
					value= null;
				}
				return value;
			case DECIMAL:
				if(value != null && !(value instanceof String && ((String)value).isEmpty())) {
					return FacilioUtil.parseDouble(value);
				}
				else {
					return null;
				}
			case BOOLEAN:
				Boolean booleanVal;
				if(value != null) {
					if(value instanceof Boolean) {
						booleanVal=(Boolean)value;
					}
					else {
						BooleanField booleanField = (BooleanField) field;
						String val = value.toString().trim();
						if ((booleanField.getTrueVal() != null && booleanField.getTrueVal().equalsIgnoreCase(val)) || (NumberUtils.isCreatable(val) && new Double(val) > 0)) {
							booleanVal = true;
						}
						else {
							booleanVal=Boolean.valueOf(val);
						}
					}
				}
				else {
					booleanVal=null;
				}
				return booleanVal;
			case LOOKUP:
			case NUMBER:	
			case DATE:
			case DATE_TIME:
			case FILE:
			case COUNTER:
			case ID:
				if(value != null && !(value instanceof String && ((String)value).isEmpty())) {
					return FacilioUtil.parseLong(value);
				}
				else {
					return null;
				}
			case ENUM:
				Integer enumVal = null; 
				if(value != null && !(value instanceof String && ((String)value).isEmpty())) {
					EnumField enumField = (EnumField) field;
					if (value instanceof Integer) {
						int val = (int) value;
						if (enumField.getValue(val) != null) {
							enumVal = val;
						}
					}
					else if (value instanceof Long) {
						int val = ((Long) value).intValue();
						if (enumField.getValue(val) != null) {
							enumVal = val;
						}
					}
					else {
						int val = enumField.getIndex(value.toString());
						if (val != -1) {
							enumVal = val;
						}
					}
				}
				return enumVal;
			case MISC:
			default:
				return value;
		}
	}
	
	public static ObjectMapper getMapper(Class<?> beanClass) {
		ObjectMapper mapper =  new ObjectMapper()
					.setSerializationInclusion(Include.NON_DEFAULT)
					.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
					.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		mapper.configOverride(beanClass)
				.setInclude(Value.construct(Include.NON_DEFAULT, Include.ALWAYS));
		
		return mapper;
	}
	
	public static <E> E getAsBeanFromJson(JSONObject content, Class<E> classObj) throws JsonParseException, JsonMappingException, IOException
	{
		ObjectMapper mapper = getMapper(classObj);
		return mapper.readValue(content.toJSONString(), classObj);
	}
	
	public static <E> E getAsBeanFromMap(Map<String, Object> props, Class<E> classObj)
	{
		ObjectMapper mapper = getMapper(classObj);
		return mapper.convertValue(props, classObj);
	}
	public static <E> List<E> getAsBeanListFromJsonArray(JSONArray content, Class<E> classObj) throws JsonParseException, JsonMappingException, IOException
	{
		if (content != null) {
			ObjectMapper mapper = getMapper(classObj);
			return mapper.readValue(content.toJSONString(), mapper.getTypeFactory().constructCollectionType(List.class, classObj));
		}
		return null;
	}
	public static <E> List<E> getAsBeanListFromMapList(List<Map<String, Object>> props, Class<E> classObj) 
	{
		if (props != null) {
			ObjectMapper mapper = getMapper(classObj);
			return mapper.convertValue(props, mapper.getTypeFactory().constructCollectionType(List.class, classObj));
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getAsProperties(Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException 
	{
		Map<String, Object> properties = null;
		if(bean != null) 
		{
			ObjectMapper mapper = getMapper(bean.getClass());
			properties = mapper.convertValue(bean, Map.class);
		}
		LOGGER.debug("######" + properties + "#####");
		return properties;
	}
	
	public static JSONObject getAsJSON(Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException 
	{
		JSONObject properties = null;
		if(bean != null) 
		{
			ObjectMapper mapper = getMapper(bean.getClass());
			properties = mapper.convertValue(bean, JSONObject.class);
			
		}
		return properties;
	}
	
	public static JSONArray getAsJSONArray(List<?> beans, Class<?> beanClass) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException 
	{
		JSONArray array = null;
		if(beans != null) 
		{
			ObjectMapper mapper = getMapper(beanClass);
			array = mapper.convertValue(beans, JSONArray.class);
		}
		return array;
	}
	
	public static Map<String, Object> getLookupProp(LookupField lookupField, long id, int level) throws Exception {
		if(id > 0) {
			if(LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
				return FieldUtil.getAsProperties(LookupSpecialTypeUtil.getLookedupObject(lookupField.getSpecialType(), id));
			}
			else {
				Class<ModuleBaseWithCustomFields> moduleClass = FacilioConstants.ContextNames.getClassFromModuleName(lookupField.getLookupModule().getName());
				if(moduleClass != null) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					List<FacilioField> lookupBeanFields = modBean.getAllFields(lookupField.getLookupModule().getName());
					SelectRecordsBuilder<ModuleBaseWithCustomFields> lookupBeanBuilder = new SelectRecordsBuilder<>(level)
																						.table(lookupField.getLookupModule().getTableName())
																						.moduleName(lookupField.getLookupModule().getName())
																						.select(lookupBeanFields)
																						.andCustomWhere(lookupField.getLookupModule().getTableName()+".ID = ?", id);
					List<Map<String, Object>> records = lookupBeanBuilder.getAsProps();
					if(records != null && records.size() > 0) {
						return records.get(0);
					}
					else {
						return null;
					}
				}
				else {
					throw new IllegalArgumentException("Unknown Module Name in Lookup field "+lookupField);
				}
			}	
		}
		else {
			return null;
		}
	}
	
	public static Object getEmptyLookupVal(LookupField lookupField, long id) throws Exception {
		if(id > 0) {
			if(LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
				return LookupSpecialTypeUtil.getEmptyLookedupObject(lookupField.getSpecialType(), id);
			}
			else {
				Class<ModuleBaseWithCustomFields> moduleClass = FacilioConstants.ContextNames.getClassFromModuleName(lookupField.getLookupModule().getName());
				if(moduleClass != null) {
					ModuleBaseWithCustomFields lookedupModule = moduleClass.newInstance();
					lookedupModule.setId(id);
					return lookedupModule;
				}
				else {
					throw new IllegalArgumentException("Unknown Module Name in Lookup field "+lookupField);
				}
			}
		}
		else {
			return null;
		}
	}
	
	public static Object getLookupVal(LookupField lookupField, long id, int level) throws Exception {
		if(id > 0) {
			if(LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
				return LookupSpecialTypeUtil.getLookedupObject(lookupField.getSpecialType(), id);
			}
			else {
				return getRecord(lookupField.getLookupModule(), id, level);
			}	
		}
		else {
			return null;
		}
	}
	
	public static Object getRecord (FacilioModule module, long id) throws Exception {
		return getRecord(module, id, 0);
	}
	
	public static Object getRecord (FacilioModule module, long id, int level) throws Exception {
		Class<ModuleBaseWithCustomFields> moduleClass = FacilioConstants.ContextNames.getClassFromModuleName(module.getName());
		if(moduleClass != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> lookupBeanFields = modBean.getAllFields(module.getName());
			SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<>(level)
																				.table(module.getTableName())
																				.moduleName(module.getName())
																				.beanClass(moduleClass)
																				.select(lookupBeanFields)
																				.andCondition(CriteriaAPI.getIdCondition(id, module))
																				;
			List<ModuleBaseWithCustomFields> records = selectBuilder.get();
			if(records != null && records.size() > 0) {
				return records.get(0);
			}
			else {
				return null;
			}
		}
		else {
			throw new IllegalArgumentException("Unknown Module during get record of module : "+module+ " for id : "+id);
		}
	}
	
	public static <E> E cloneBean(Object bean, Class<E> classObj) {
		ObjectMapper mapper = getMapper(classObj);
		Map<String, Object> properties = mapper.convertValue(bean, Map.class);
		return mapper.convertValue(properties, classObj);
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject mergeBean(Object bean1, Object bean2) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		JSONObject obj1 = !(bean1 instanceof JSONObject) ? getAsJSON(bean1) : (JSONObject) bean1;
		JSONObject obj2 = !(bean2 instanceof JSONObject) ? getAsJSON(bean2) : (JSONObject) bean2;
		obj1.putAll(obj2);
		return obj1;
	}
	
	public static Object getBooleanResultString(Object value,String moduleName,String fieldName) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField field = modBean.getField(fieldName, moduleName);
		return getBooleanResultString(value,field);
	}
	
	public static Object getBooleanResultString(Object value,FacilioField field) {
		
		if(field.getDataType() == FieldType.BOOLEAN.getTypeAsInt() && field instanceof BooleanField) {
			BooleanField booleanField = (BooleanField) field;
			String result = null;
			if((Boolean)value.equals(Boolean.TRUE)) {
				result = booleanField.getTrueVal() != null ?  booleanField.getTrueVal() : "1";
			}
			else {
				result = booleanField.getFalseVal() != null ?  booleanField.getFalseVal() : "0";
			}
			return result;
		}
		return value;
	}
	
	public static void handleNumberFieldUnitConversion(List<NumberField> numberFields, List<Map<String, Object>> values){
		
		try {
			if (numberFields == null || numberFields.isEmpty() || values == null || values.isEmpty()) {
				return;
			}
			for(Map<String, Object> value : values) {
				for(NumberField field : numberFields) {
					if (value.containsKey(field.getName()) && value.containsKey(field.getName()+NUMBER_FIELD_UNIT_SUFFIX)) {
						Object numberField = value.get(field.getName());
						int unit = Integer.parseInt(value.get(field.getName()+NUMBER_FIELD_UNIT_SUFFIX).toString());
						numberField = UnitsUtil.convertToSiUnit(numberField, Unit.valueOf(unit));
						value.put(field.getName(), numberField);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.log(Level.ERROR, "Insertion failed During Unit Conversion", e);
			//throw new RuntimeException("Insertion failed During Unit Conversion");
		}
		
	}
	public static void addFiles(List<FileField> fileFields, List<Map<String, Object>> values) throws Exception {
		if (fileFields == null || fileFields.isEmpty()) {
			return;
		}
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		for(Map<String, Object> value : values) {
			for(FacilioField field : fileFields) {
				if (value.containsKey(field.getName())) {
					Object fileObj = value.get(field.getName());
					fileObj = fileObj instanceof List && ((ArrayList)fileObj).get(0) != null ? ((Map<String,Object>)((ArrayList)fileObj).get(0)) : fileObj;
					File file = null;
					String fileName = null;
					String fileType = null;
					
					if (fileObj instanceof File || fileObj instanceof String){
						file = fileObj instanceof File ? (File) fileObj : new File((String)fileObj);
						fileName = (String) value.get(field.getName()+"FileName");
						fileType = (String) value.get(field.getName()+"ContentType");
					}
					else {
						Map<String, Object> fileMap = (Map<String, Object>) fileObj;
						file = new File((String) fileMap.get("content"));
						fileName = (String) fileMap.get(field.getName()+"FileName");
						fileType = (String) fileMap.get(field.getName()+"ContentType");
					}
					
					// TODO add file in bulk
					/*value.put("file", file);
					value.put("fileName", fileName);
					value.put("contentType", fileType);
					files.add(value);*/
					
					long fileId = fs.addFile(fileName, file, fileType);
					value.put(field.getName(), fileId);
				}
			}
		}
		
		/*for(Map<String, Object> value : values) {
			for(FacilioField field : fileFields) {
				if (value.containsKey("fileId")) {
					value.put(field.getName(), value.get("fileId"));
				}
			}
		}*/
	}
	
	private static final Set<String> SITE_ID_ALLOWED_MODULES = Collections.unmodifiableSet(
			new HashSet<>(Arrays.asList("resource", "asset", "building", "floor", "space", "zone", "alarm", "ticket", "workorder", "workorderrequest", "task", "readingalarm", "inventory")));
	
	public static boolean isSiteIdFieldPresent(FacilioModule module) {
		return SITE_ID_ALLOWED_MODULES.contains(module.getName()) || (module.getExtendModule() != null && module.getExtendModule().getName().equals("asset"));
	}
	
	private static final Set<String> SYSTEM_FIELDS_ALLOWED_MODULES = Collections.unmodifiableSet(
			new HashSet<>(Arrays.asList("assetactivity"))
			);
	
	public static boolean isSystemFieldsPresent (FacilioModule module) {
		return SYSTEM_FIELDS_ALLOWED_MODULES.contains(module.getName());
	}

	public static FacilioField parseFieldJson(JSONObject fieldJson) throws Exception {
		JSONArray fieldJsons = FacilioUtil.getSingleTonJsonArray(fieldJson);
		return parseFieldJson(fieldJsons).get(0);
	}
	
	public static List<FacilioField> parseFieldJson(JSONArray fieldJsons) throws Exception {
		if(fieldJsons != null) {
			List<FacilioField> fields = new ArrayList<>();
			Iterator iterator = fieldJsons.iterator();
			while(iterator.hasNext()) {
				Map fieldJson = (Map) iterator.next();
				FieldType fieldType = FieldType.getCFType(Integer.parseInt(fieldJson.get("dataType").toString()));
				FacilioField facilioField;
				switch(fieldType) {
				case NUMBER:
				case DECIMAL:
					facilioField = (NumberField) FieldUtil.getAsBeanFromMap(fieldJson, NumberField.class);
					break;
				case BOOLEAN:
					facilioField = (BooleanField) FieldUtil.getAsBeanFromMap(fieldJson, BooleanField.class);
					break;
				case ENUM:
					facilioField = (EnumField) FieldUtil.getAsBeanFromMap(fieldJson, EnumField.class);
					break;
				case LOOKUP:
					facilioField = (LookupField) FieldUtil.getAsBeanFromMap(fieldJson, LookupField.class);
					break;
				case FILE:
					facilioField = (FileField) FieldUtil.getAsBeanFromMap(fieldJson, FileField.class);
					break;
				default:
					facilioField = (FacilioField) FieldUtil.getAsBeanFromMap(fieldJson, FacilioField.class);
					break;
				}
				fields.add(facilioField);
			}
			return fields;
		}
		return null;
	}
}
