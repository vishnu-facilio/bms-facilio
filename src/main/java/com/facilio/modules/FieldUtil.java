package com.facilio.modules;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule.ModuleType;
import com.facilio.modules.fields.BooleanField;
import com.facilio.modules.fields.EnumField;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.FileField;
import com.facilio.modules.fields.LookupField;
import com.facilio.modules.fields.NumberField;
import com.facilio.util.FacilioUtil;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.MutableConfigOverride;

public class FieldUtil {
	
	private static final Logger LOGGER = LogManager.getLogger(FieldUtil.class.getName());

	public static Map<String, Object> getEmptyLookedUpProp(long id) {
		Map<String, Object> prop = new HashMap<>();
		prop.put("id", id);
		return prop;
	}
	
	public static void inti() {

	}
	
	private static List<Class> nonModuleClasses = Collections.unmodifiableList(initNonModuleClassMap());
	private static List<Class> initNonModuleClassMap() {
		List<Class> nonModuleClasses = new ArrayList<>();
		nonModuleClasses.add(PreventiveMaintenance.class);
		nonModuleClasses.add(SortField.class);
		nonModuleClasses.add(FacilioView.class);
		nonModuleClasses.add(FacilioForm.class);
		return nonModuleClasses;
	}

	private static final ObjectMapper NON_DEFAULT_MAPPER = new ObjectMapper()
													.setSerializationInclusion(Include.NON_DEFAULT)
													.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
													.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

	static {
		for (Class classObj : FacilioConstants.ContextNames.getAllClasses()) {
			NON_DEFAULT_MAPPER.configOverride(classObj).setInclude(Value.construct(Include.NON_DEFAULT, Include.ALWAYS));
		}
		for (Class classObj : nonModuleClasses) {
			NON_DEFAULT_MAPPER.configOverride(classObj).setInclude(Value.construct(Include.NON_DEFAULT, Include.ALWAYS));
		}
	}


	public static ObjectMapper getMapper(Class<?> beanClass) {
		MutableConfigOverride config = NON_DEFAULT_MAPPER.configOverride(beanClass);
		if (config.getInclude() == null) {
			config.setInclude(Value.construct(Include.NON_DEFAULT, Include.ALWAYS));
		}
		return NON_DEFAULT_MAPPER;
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
	public static <E> List<E> getAsBeanListFromMapList(List<Map<String, Object>> props, Class<E> classObj) {
		if (props != null) {
			ObjectMapper mapper = getMapper(classObj);
			return mapper.convertValue(props, mapper.getTypeFactory().constructCollectionType(List.class, classObj));
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getAsProperties(Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Object> properties = null;
		if(bean != null) {
			ObjectMapper mapper = getMapper(bean.getClass());
			properties = mapper.convertValue(bean, Map.class);
		}
//		LOGGER.debug("######" + properties + "#####");
		return properties;
	}
	

	public static <E extends ModuleBaseWithCustomFields> Map<Long,E> getAsMap(List<E> beans) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{

		Map<Long,E> mapList = new HashMap<Long, E>();
		for(E bean : beans) {
			mapList.put(bean.getId(), bean);
		}
		return mapList;
	}

	public static JSONObject getAsJSON(Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		JSONObject properties = null;
		if(bean != null) {
			ObjectMapper mapper = getMapper(bean.getClass());
			properties = mapper.convertValue(bean, JSONObject.class);

		}
		return properties;
	}
	
	public static JSONArray getAsJSONArray(List<?> beans, Class<?> beanClass) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		JSONArray array = null;
		if(beans != null) {
			ObjectMapper mapper = getMapper(beanClass);
			array = mapper.convertValue(beans, JSONArray.class);
		}
		return array;
	}

	public static Map<String, Object> getLookupProp(LookupField lookupField, long id, int level) throws Exception {
		Map<Long, Map<String, Object>> props = (Map<Long, Map<String, Object>>) getLookupProps(lookupField, Collections.singletonList(id), true, level);
		if (props != null && !props.isEmpty()) {
			return props.values().stream().findFirst().get();
		}
		return null;
	}

	public static Map<Long, ? extends Object> getLookupProps(LookupField field, Collection<Long> ids, boolean isMap, int level) throws Exception {
		if(CollectionUtils.isNotEmpty(ids)) {
			if(LookupSpecialTypeUtil.isSpecialType(field.getSpecialType())) {
				if (isMap) {
					List<? extends Object> records = LookupSpecialTypeUtil.getRecords(field.getSpecialType(), ids);
					if (CollectionUtils.isNotEmpty(records)) {
						Map<Long, Map<String, Object>> props = new HashMap<>();
						for (Object record : records) {
							Map<String, Object> prop = FieldUtil.getAsProperties(record);
							props.put((Long) prop.get("id"), prop);
						}
						return props;
					}
					return null;
				}
				else {
					return LookupSpecialTypeUtil.getRecordsAsMap(field.getSpecialType(), ids);
				}
			}
			else {
				Class<ModuleBaseWithCustomFields> moduleClass = FacilioConstants.ContextNames.getClassFromModule(field.getLookupModule(), false);
				if(moduleClass != null) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					List<FacilioField> lookupBeanFields = modBean.getAllFields(field.getLookupModule().getName());
					FacilioModule module = modBean.getModule(field.getLookupModule().getName());
					SelectRecordsBuilder<ModuleBaseWithCustomFields> lookupBeanBuilder = new SelectRecordsBuilder<>(level)
																						.module(module)
																						.select(lookupBeanFields)
																						.beanClass(moduleClass)
																						.andCondition(CriteriaAPI.getIdCondition(ids, module))
																						.fetchDeleted()
																						;

					if (field instanceof LookupFieldMeta && CollectionUtils.isNotEmpty(((LookupFieldMeta) field).getChildLookupFields())) {
						lookupBeanBuilder.fetchLookups(((LookupFieldMeta) field).getChildLookupFields());
//						for (LookupField lookupField : ((LookupFieldMeta) field).getChildLookupFields()) {
////							lookupBeanBuilder.fetchLookup(lookupField instanceof LookupFieldMeta ? (LookupFieldMeta) lookupField : new LookupFieldMeta(lookupField));
//						}
					}

					if (isMap || !field.isDefault()) {
						return lookupBeanBuilder.getAsMapProps();
					}
					else {
						return lookupBeanBuilder.getAsMap();
					}
				}
				else {
					throw new IllegalArgumentException("Unknown Module Name while fetching look props "+field.getLookupModule().getName());
				}
			}	
		}
		return null;
	}
	
	public static Object getEmptyLookupVal(LookupField lookupField, long id) throws Exception {
		if(id > 0) {
			if(LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
				return LookupSpecialTypeUtil.getEmptyLookedupObject(lookupField.getSpecialType(), id);
			}
			else {
				Class<ModuleBaseWithCustomFields> moduleClass = FacilioConstants.ContextNames.getClassFromModule(lookupField.getLookupModule(), false);
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
		Class<ModuleBaseWithCustomFields> moduleClass = FacilioConstants.ContextNames.getClassFromModule(module);
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
		obj2.values().removeAll(Collections.singleton(null));
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

	private static final Set<String> SITE_ID_ALLOWED_MODULES = Collections.unmodifiableSet(
			new HashSet<>(Arrays.asList("resource",
										"asset",
										"building",
										"floor",
										"space",
										"zone",
										"alarm",
										"ticket",
										"workorder",
										"workorderrequest",
										"task",
										"readingalarm",
										"inventory",
										"tenant",
										"labour",
										"mvproject",
										FacilioConstants.ContextNames.Reservation.RESERVATION,
										FacilioConstants.ContextNames.Reservation.RESERVATIONS_EXTERNAL_ATTENDEE,
										FacilioConstants.ContextNames.Reservation.RESERVATIONS_INTERNAL_ATTENDEE
										)));

	public static boolean isSiteIdFieldPresent(FacilioModule module) {
		return SITE_ID_ALLOWED_MODULES.contains(module.getName()) || (module.getExtendModule() != null && module.getExtendModule().getName().equals("asset"));
	}

	private static final Set<String> SYSTEM_FIELDS_ALLOWED_MODULES = Collections.unmodifiableSet(
			new HashSet<>(Arrays.asList(
					FacilioConstants.ContextNames.ASSET_ACTIVITY,
					FacilioConstants.ContextNames.WORKORDER_ACTIVITY,
					FacilioConstants.ContextNames.ITEM_ACTIVITY,
					FacilioConstants.ContextNames.PURCHASE_ORDER,
					FacilioConstants.ContextNames.PURCHASE_REQUEST,
					FacilioConstants.ContextNames.RECEIVABLE,
					FacilioConstants.ContextNames.RECEIPTS,
					FacilioConstants.ContextNames.CONTRACTS,
					FacilioConstants.ContextNames.GATE_PASS,
					FacilioConstants.ContextNames.SHIPMENT,
					FacilioConstants.ContextNames.INVENTORY_REQUEST,
					FacilioConstants.ContextNames.INVENTORY_REQUEST,
					FacilioConstants.ContextNames.ATTENDANCE,
					FacilioConstants.ContextNames.ATTENDANCE_TRANSACTIONS,
					FacilioConstants.ContextNames.SERVICE,
					FacilioConstants.ContextNames.TERMS_AND_CONDITIONS,
					FacilioConstants.ContextNames.Reservation.RESERVATION,
					FacilioConstants.ContextNames.Reservation.RESERVATIONS_EXTERNAL_ATTENDEE,
					FacilioConstants.ContextNames.Reservation.RESERVATIONS_INTERNAL_ATTENDEE,
					FacilioConstants.ContextNames.ASSET_MOVEMENT
			))
			);

	public static boolean isSystemFieldsPresent (FacilioModule module) {
		// custom modules will have system fields by default
		if (module.getTypeEnum() == ModuleType.CUSTOM) {
			return true;
		}
		return SYSTEM_FIELDS_ALLOWED_MODULES.contains(module.getName());
	}

	public static boolean isBaseEntityRootModule (FacilioModule module) {
		return (module.getTypeEnum() == FacilioModule.ModuleType.BASE_ENTITY && module.getExtendModule() == null)
				|| (module.getTypeEnum() == ModuleType.CUSTOM);
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

    public static List<UpdateChangeSet> constructChangeSet(long recordId, Map<String, Object> prop, Map<String, FacilioField> fieldMap) {
        Set<String> fieldNames = fieldMap.keySet();
        List<UpdateChangeSet> changeList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : prop.entrySet()) {
            if (fieldNames.contains(entry.getKey())) {
                UpdateChangeSet currentChange = new UpdateChangeSet();
                currentChange.setFieldId(fieldMap.get(entry.getKey()).getFieldId());
                currentChange.setNewValue(entry.getValue());
                currentChange.setRecordId(recordId);
                changeList.add(currentChange);
            }
        }
        return changeList;
    }
}
