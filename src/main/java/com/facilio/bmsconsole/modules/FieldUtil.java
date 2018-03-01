package com.facilio.bmsconsole.modules;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class FieldUtil {
	
	public static Map<String, Object> getLookedUpProp(long id) {
		Map<String, Object> prop = new HashMap<>();
		prop.put("id", id);
		return prop;
	}
	
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
					if(val != -1) {
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
						val = new Double(value.toString()).longValue();
					}
					if(val != -1) {
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
			case MISC:
			default:
					pstmt.setObject(paramIndex, value);
					break;
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
		ObjectMapper mapper = getMapper(classObj);
		return mapper.readValue(content.toJSONString(), mapper.getTypeFactory().constructCollectionType(List.class, classObj));
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
	
	public static Object getLookupVal(LookupField lookupField, long id, int level) throws Exception {
		if(id > 0) {
			if(LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
				return LookupSpecialTypeUtil.getLookedupObject(lookupField.getSpecialType(), id);
			}
			else {
				Class<ModuleBaseWithCustomFields> moduleClass = FacilioConstants.ContextNames.getClassFromModuleName(lookupField.getLookupModule().getName());
				if(moduleClass != null) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					List<FacilioField> lookupBeanFields = modBean.getAllFields(lookupField.getLookupModule().getName());
					SelectRecordsBuilder<ModuleBaseWithCustomFields> lookupBeanBuilder = new SelectRecordsBuilder<>(level)
																						.table(lookupField.getLookupModule().getTableName())
																						.moduleName(lookupField.getLookupModule().getName())
																						.beanClass(moduleClass)
																						.select(lookupBeanFields)
																						.andCustomWhere(lookupField.getLookupModule().getTableName()+".ID = ?", id);
					List<ModuleBaseWithCustomFields> records = lookupBeanBuilder.get();
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
	
	public static List<Map<String, Object>> getLastReading(Iterable<Long> resourceList, Iterable<Long> fieldList) throws Exception {


		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getLastReadingFields())
				.table(ModuleFactory.getLastReadingModule().getTableName())
				.andCustomWhere("ORGID=?",orgId)
				.andCondition(CriteriaAPI.getCondition("RESOURCE_ID", "resourceId", StringUtils.join(resourceList, ","), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("FIELD_ID", "fieldId", StringUtils.join(fieldList, ","), NumberOperators.EQUALS));
		List<Map<String, Object>> stats = builder.get();	
		return stats;
	}

	public static <E> E cloneBean(Object bean, Class<E> classObj) {
		ObjectMapper mapper = getMapper(classObj);
		Map<String, Object> properties = mapper.convertValue(bean, Map.class);
		return mapper.convertValue(properties, classObj);
	}
}
