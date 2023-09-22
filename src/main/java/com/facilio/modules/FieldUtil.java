package com.facilio.modules;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.FieldPermissionContext;
import com.facilio.bmsconsole.context.FieldPermissionContext.CheckType;
import com.facilio.bmsconsole.context.FieldPermissionContext.PermissionType;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.forms.FormField;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.SortField;
import com.facilio.bmsconsole.workflow.rule.SLAWorkflowEscalationContext;
import com.facilio.cache.CacheUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.cache.FacilioCache;
import com.facilio.fw.cache.LRUCache;
import com.facilio.modules.fields.*;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.V3Context;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonInclude.Value;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.cfg.MutableConfigOverride;
import lombok.NonNull;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class FieldUtil {
	
	private static final Logger LOGGER = LogManager.getLogger(FieldUtil.class.getName());

	public static Map<String, Object> getEmptyLookedUpProp(long id) {
		Map<String, Object> prop = new HashMap<>();
		prop.put("id", id);
		return prop;
	}

	public static void dropFieldFromCache(long orgId, FacilioField newField) {
		FacilioCache cache = LRUCache.getModuleFieldsCache();
		cache.remove(CacheUtil.FIELDS_KEY(orgId, newField.getModule().getName()));

		cache = LRUCache.getFieldsCache();
		cache.remove(CacheUtil.FIELD_KEY(orgId, newField.getFieldId()));

		//Removing all field names with the same name because we might have multiple copies for the same field with different extended modules.
		//Having multiple copies of the same field with different extended modules in cache is a conscious decision to avoid fetching of modules in getField
		//The side effect of this is if another field has same name, even that will be removed from cache even if it doesn't extend the module of the field. We shall see how this goes!!
		cache = LRUCache.getFieldNameCache();
		cache.removeStartsWith(CacheUtil.FIELD_NAME_KEY_FOR_REMOVAL(orgId, newField.getName()));
	}

	public static void init() {

	}
	
	private static List<Class> nonModuleClasses = Collections.unmodifiableList(initNonModuleClassMap());
	private static List<Class> initNonModuleClassMap() {
		List<Class> nonModuleClasses = new ArrayList<>();
		nonModuleClasses.add(PreventiveMaintenance.class);
		nonModuleClasses.add(SortField.class);
		nonModuleClasses.add(FacilioView.class);
		nonModuleClasses.add(FacilioForm.class);
		nonModuleClasses.add(SLAWorkflowEscalationContext.class);
		nonModuleClasses.add(FormField.class);
		return nonModuleClasses;
	}

	private static final ObjectMapper NON_DEFAULT_MAPPER = new ObjectMapper()
													.setSerializationInclusion(Include.NON_DEFAULT)
													.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
													.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

	private static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper()
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
		return getMapper(beanClass, false);
	}

	public static ObjectMapper getMapper(Class<?> beanClass, boolean includeDefaultValues) {
		if (V3Context.class.isAssignableFrom(beanClass) || includeDefaultValues) {
			return DEFAULT_MAPPER;
		}
		MutableConfigOverride config = NON_DEFAULT_MAPPER.configOverride(beanClass);
		if (config.getInclude() == null) {
			config.setInclude(Value.construct(Include.NON_DEFAULT, Include.ALWAYS));
		}
		return NON_DEFAULT_MAPPER;
	}
	
	public static <E> E getAsBeanFromJson(JSONObject content, Class<E> classObj) throws IOException
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

	public static Map<String, Object> getAsProperties(Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return getAsProperties(bean, false);
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getAsProperties(Object bean, boolean includeDefaultValues) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<String, Object> properties = null;
		if(bean != null) {
			ObjectMapper mapper = getMapper(bean.getClass(), includeDefaultValues);
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

	public static JSONObject getAsJSON(Object bean) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return getAsJSON(bean, false);
	}

	public static JSONObject getAsJSON(Object bean, boolean includeDefaultValues) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		JSONObject properties = null;
		if(bean != null) {
			ObjectMapper mapper = getMapper(bean.getClass(), includeDefaultValues);
			properties = mapper.convertValue(bean, JSONObject.class);

		}
		return properties;
	}

	public static JSONArray getAsJSONArray(List<?> beans, Class<?> beanClass) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return getAsJSONArray(beans, beanClass, false);
	}
	
	public static JSONArray getAsJSONArray(List<?> beans, Class<?> beanClass, boolean includeDefaultValues) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		JSONArray array = null;
		if(beans != null) {
			ObjectMapper mapper = getMapper(beanClass, includeDefaultValues);
			array = mapper.convertValue(beans, JSONArray.class);
		}
		return array;
	}

	public static List<Map<String, Object>> getAsMapList(List<?> beans, Class<?> beanClass) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		return getAsMapList(beans, beanClass, false);
	}
	
	public static List<Map<String, Object>> getAsMapList(List<?> beans, Class<?> beanClass, boolean includeDefaultValues) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<Map<String, Object>> array = null;
		if(beans != null) {
			ObjectMapper mapper = getMapper(beanClass, includeDefaultValues);
//			array = mapper.convertValue(beans, List.class);
			return mapper.convertValue(beans, mapper.getTypeFactory().constructCollectionType(List.class, Map.class));
		}
		return array;
	}

	public static Map<String, Object> getLookupProp(LookupField lookupField, long id) throws Exception {
		Map<Long, Map<String, Object>> props = (Map<Long, Map<String, Object>>) getLookupProps(lookupField, Collections.singletonList(id), true);
		if (props != null && !props.isEmpty()) {
			return props.values().stream().findFirst().get();
		}
		return null;
	}

	public static Map<Long, ? extends Object> getLookupProps(LookupField field, Collection<Long> ids, boolean isMap) throws Exception {
		return getLookupProps(field, ids, isMap, false);
	}

	public static Map<Long, ? extends Object> getLookupProps(LookupField field, Collection<Long> ids, boolean isMap, boolean skipAllScope) throws Exception {
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
					FacilioModule module = field.getLookupModule();
					SelectRecordsBuilder<ModuleBaseWithCustomFields> lookupBeanBuilder = new SelectRecordsBuilder<>()
																						.module(module)
																						.beanClass(moduleClass)
																						.andCondition(CriteriaAPI.getIdCondition(ids, module))
																						.fetchDeleted()
																						;

					if (skipAllScope) {
						lookupBeanBuilder.skipModuleCriteria()
								.skipScopeCriteria()
								.skipPermission();
					}

					List<FacilioField> lookupBeanFields = null;
					if (field instanceof LookupFieldMeta) {
						LookupFieldMeta lfm = (LookupFieldMeta) field;
						if (CollectionUtils.isNotEmpty(lfm.getChildSupplements())) {
							lookupBeanBuilder.fetchSupplements(lfm.getChildSupplements());
						}
						if (CollectionUtils.isNotEmpty(lfm.getSelectFields())) {
							lookupBeanFields = lfm.getSelectFields();
						}
					}
					if (lookupBeanFields == null) {
						ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
						lookupBeanFields = modBean.getAllFields(field.getLookupModule().getName());
					}
					lookupBeanBuilder.select(lookupBeanFields);
					FacilioField mainField = lookupBeanFields.stream().filter(FacilioField::isMainField).findFirst().orElse(null);
					if (mainField != null && mainField instanceof LookupField) { // Adding main field as supplement if it's lookup. Doing only one level
						lookupBeanBuilder.fetchSupplement((SupplementRecord) mainField);
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
	
	public static Object getLookupVal(LookupField lookupField, long id) throws Exception {
		if(id > 0) {
			if(LookupSpecialTypeUtil.isSpecialType(lookupField.getSpecialType())) {
				return LookupSpecialTypeUtil.getLookedupObject(lookupField.getSpecialType(), id);
			}
			else {
				return getRecord(lookupField.getLookupModule(), id);
			}	
		}
		else {
			return null;
		}
	}
	
	public static Object getRecord (FacilioModule module, long id) throws Exception {
		Class<ModuleBaseWithCustomFields> moduleClass = FacilioConstants.ContextNames.getClassFromModule(module);
		if(moduleClass != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> lookupBeanFields = modBean.getAllFields(module.getName());
			SelectRecordsBuilder<ModuleBaseWithCustomFields> selectBuilder = new SelectRecordsBuilder<>()
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
			if(value.equals(Boolean.TRUE)) {
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
                    "site",
                    "building",
                    "floor",
                    "space",
                    "zone",
                    "alarm",
                    "ticket",
                    "workorder",
                    "workorderrequest",
                    "readingalarm",
                    "inventory",
                    "tenant",
                    "labour",
                    "mvproject",
                    FacilioConstants.ContextNames.TENANT_UNIT_SPACE,
                    FacilioConstants.ContextNames.Reservation.RESERVATION,
                    FacilioConstants.ContextNames.Reservation.RESERVATIONS_EXTERNAL_ATTENDEE,
                    FacilioConstants.ContextNames.Reservation.RESERVATIONS_INTERNAL_ATTENDEE,
                    FacilioConstants.ContextNames.BASE_ALARM,
                    FacilioConstants.ContextNames.NEW_READING_ALARM,
                    FacilioConstants.ContextNames.ALARM_OCCURRENCE,
                    FacilioConstants.ContextNames.READING_ALARM_OCCURRENCE,
                    FacilioConstants.ContextNames.BASE_EVENT,
                    FacilioConstants.ContextNames.READING_EVENT,
                    FacilioConstants.ContextNames.PRE_EVENT,
                    FacilioConstants.ContextNames.PRE_ALARM,
                    FacilioConstants.ContextNames.PRE_ALARM_OCCURRENCE,
                    FacilioConstants.ContextNames.OPERATION_ALARM,
                    FacilioConstants.ContextNames.OPERATION_OCCURRENCE,
                    FacilioConstants.ContextNames.OPERATION_EVENT,
                    FacilioConstants.ContextNames.SENSOR_ALARM,
					FacilioConstants.ContextNames.SENSOR_ALARM_OCCURRENCE,
					FacilioConstants.ContextNames.SENSOR_EVENT,
					FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM,
					FacilioConstants.ContextNames.SENSOR_ROLLUP_ALARM_OCCURRENCE,
					FacilioConstants.ContextNames.SENSOR_ROLLUP_EVENT,
					FacilioConstants.ContextNames.MULTIVARIATE_ANOMALY_ALARM,
					FacilioConstants.ContextNames.MULTIVARIATE_ANOMALY_ALARM_OCCURRENCE,
					FacilioConstants.ContextNames.MULTIVARIATE_ANOMALY_EVENT,
                    FacilioConstants.ContextNames.WorkPermit.WORKPERMIT,
                    FacilioConstants.ContextNames.SAFETY_PLAN,
                    FacilioConstants.ContextNames.SERVICE_REQUEST,
                    FacilioConstants.ContextNames.AGENT_METRICS_MODULE,
                    //FacilioConstants.ContextNames.PEOPLE,
                    // FacilioConstants.ContextNames.VENDOR_CONTACT,
                    FacilioConstants.ContextNames.CLIENT_CONTACT,
                    //FacilioConstants.ContextNames.EMPLOYEE,
                    FacilioConstants.ContextNames.QUOTE,
					FacilioConstants.ContextNames.BASE_MAIL_MESSAGE,
					//FacilioConstants.ContextNames.USER_NOTIFICATION,
				/*	FacilioConstants.ContextNames.Tenant.ANNOUNCEMENT,
					FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION,
					FacilioConstants.ContextNames.Tenant.ADMIN_DOCUMENTS,
					FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY,
					FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD,
					FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS,
					FacilioConstants.ContextNames.Tenant.AUDIENCE,*/
					FacilioConstants.ContextNames.FacilityBooking.FACILITY,
					FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING,
					FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME,
					FacilioConstants.ContextNames.Floorplan.DESKS,
					FacilioConstants.ContextNames.PARKING_STALL,
					FacilioConstants.ContextNames.LOCKERS,
					FacilioConstants.Inspection.INSPECTION_TEMPLATE,
					FacilioConstants.Inspection.INSPECTION_RESPONSE,
					FacilioConstants.Induction.INDUCTION_TEMPLATE,
					FacilioConstants.Induction.INDUCTION_RESPONSE,
					FacilioConstants.Survey.SURVEY_TEMPLATE,
					FacilioConstants.Survey.SURVEY_RESPONSE,
					ContextNames.PLANNEDMAINTENANCE,
					FacilioConstants.Meter.METER
					)));



	private static final Set<String> SYSTEM_UPDATED_FIELDS = Collections.unmodifiableSet(
			new HashSet<>(Arrays.asList("sysCreatedTime", "sysModifiedTime", "sysModifiedBy", "sysCreatedBy", "sysDeletedBy",
					"sysDeletedTime", "localId", "moduleState", "stateFlowId", "approvalState", "approvalFlowId", "id", "orgId", "moduleId",
					ContextNames.SLA_POLICY_ID, ContextNames.FORM_ID,  ContextNames.APPROVAL_STATUS
												)));
	
	




	public static boolean isSiteIdFieldPresent(FacilioModule module) {
		return isSiteIdFieldPresent(module, false);
	}
	public static boolean isSiteIdFieldPresent(FacilioModule module, boolean returnFalseIfSite) {
		if (returnFalseIfSite && FacilioConstants.ContextNames.SITE.equals(module.getName())) { //Temp fix. Have to check how to handle this
			return false;
		}
		return SITE_ID_ALLOWED_MODULES.contains(module.getName())
				|| (module.getExtendModule() != null && (module.getExtendModule().getName().equals("asset")
				|| module.getExtendModule().getName().equals("meter")
				|| module.getExtendModule().getName().equals("controller")));
	}

	@Deprecated // Do not add more modules in this. Add systemFields in DB
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
					FacilioConstants.ContextNames.ASSET_MOVEMENT,
					FacilioConstants.ContextNames.VISITOR,
					FacilioConstants.ContextNames.VISITOR_INVITE,
					FacilioConstants.ContextNames.VISITOR_LOGGING,
					FacilioConstants.ContextNames.WorkPermit.WORKPERMIT,
					FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE,
					FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE_CHECKLIST,
					FacilioConstants.ContextNames.CONTACT,
					FacilioConstants.ContextNames.VENDORS,
					FacilioConstants.ContextNames.INSURANCE,
					FacilioConstants.ContextNames.VENDOR_DOCUMENTS,
					FacilioConstants.ContextNames.SAFETY_PLAN,
					FacilioConstants.ContextNames.HAZARD,
					FacilioConstants.ContextNames.PRECAUTION,
                    FacilioConstants.ContextNames.SERVICE_REQUEST,
                    FacilioConstants.ContextNames.QUOTE,
					FacilioConstants.ContextNames.USER_NOTIFICATION,
					FacilioConstants.ContextNames.ANNOUNCEMENT,
					FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION,
					FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS,
					FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD,
					FacilioConstants.ContextNames.Tenant.ADMIN_DOCUMENTS,
					FacilioConstants.ContextNames.Tenant.PEOPLE_ANNOUNCEMENTS,
					FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY,
					FacilioConstants.ContextNames.WO_SERVICE,
					FacilioConstants.ContextNames.WO_LABOUR,
					FacilioConstants.ContextNames.TRANSACTION,
					FacilioConstants.ContextNames.Tenant.AUDIENCE,
					FacilioConstants.ContextNames.FacilityBooking.FACILITY,
					FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING,
					FacilioConstants.ContextNames.JOB_PLAN,
					FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME,
					FacilioConstants.ContextNames.MOVES,
					FacilioConstants.ContextNames.TRANSFER_REQUEST

					))
			);


	@Deprecated // Do not add more modules in this
	public static boolean isSystemFieldsPresent (FacilioModule module) {

		// custom modules will have system fields by default
		if(module.getTypeEnum() == FacilioModule.ModuleType.ENUM_REL_MODULE
						|| module.getTypeEnum() == FacilioModule.ModuleType.LOOKUP_REL_MODULE
						|| module.getTypeEnum() == FacilioModule.ModuleType.RATING) {
			return true;
		}

		return false;
	}

	public static final Set<String> SYSTEM_FIELDS_MIGRATED_MODULES = Collections.unmodifiableSet(
			new HashSet<>(Arrays.asList(
					FacilioConstants.ContextNames.ATTENDANCE,
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
					FacilioConstants.ContextNames.ATTENDANCE_TRANSACTIONS,
					FacilioConstants.ContextNames.SERVICE,
					FacilioConstants.ContextNames.TERMS_AND_CONDITIONS,
					FacilioConstants.ContextNames.Reservation.RESERVATION,
					FacilioConstants.ContextNames.Reservation.RESERVATIONS_EXTERNAL_ATTENDEE,
					FacilioConstants.ContextNames.Reservation.RESERVATIONS_INTERNAL_ATTENDEE,
					FacilioConstants.ContextNames.ASSET_MOVEMENT,
					FacilioConstants.ContextNames.VISITOR,
					FacilioConstants.ContextNames.VISITOR_INVITE,
					FacilioConstants.ContextNames.VISITOR_LOGGING,
					FacilioConstants.ContextNames.WorkPermit.WORKPERMIT,
					FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE,
					FacilioConstants.ContextNames.WorkPermit.WORK_PERMIT_TYPE_CHECKLIST,
					FacilioConstants.ContextNames.CONTACT,
					FacilioConstants.ContextNames.VENDORS,
					FacilioConstants.ContextNames.INSURANCE,
					FacilioConstants.ContextNames.VENDOR_DOCUMENTS,
					FacilioConstants.ContextNames.SAFETY_PLAN,
					FacilioConstants.ContextNames.HAZARD,
					FacilioConstants.ContextNames.PRECAUTION,
					FacilioConstants.ContextNames.SERVICE_REQUEST,
					FacilioConstants.ContextNames.QUOTE,
					FacilioConstants.ContextNames.USER_NOTIFICATION,
					FacilioConstants.ContextNames.ANNOUNCEMENT,
					FacilioConstants.ContextNames.Tenant.NEWS_AND_INFORMATION,
					FacilioConstants.ContextNames.Tenant.DEALS_AND_OFFERS,
					FacilioConstants.ContextNames.Tenant.NEIGHBOURHOOD,
					FacilioConstants.ContextNames.Tenant.ADMIN_DOCUMENTS,
					FacilioConstants.ContextNames.Tenant.PEOPLE_ANNOUNCEMENTS,
					FacilioConstants.ContextNames.Tenant.CONTACT_DIRECTORY,
					FacilioConstants.ContextNames.WO_SERVICE,
					FacilioConstants.ContextNames.WO_LABOUR,
					FacilioConstants.ContextNames.TRANSACTION,
					FacilioConstants.ContextNames.Tenant.AUDIENCE,
					FacilioConstants.ContextNames.FacilityBooking.FACILITY,
					FacilioConstants.ContextNames.FacilityBooking.FACILITY_BOOKING,
					FacilioConstants.ContextNames.JOB_PLAN,
					FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME,
					FacilioConstants.ContextNames.MOVES
			))
	);

	public static boolean isBaseEntityRootModule (FacilioModule module) {
		return (module.getTypeEnum() == FacilioModule.ModuleType.BASE_ENTITY && module.getExtendModule() == null);
	}

	public static FacilioField parseFieldJson(JSONObject fieldJson) throws Exception {
		JSONArray fieldJsons = FacilioUtil.getSingleTonJsonArray(fieldJson);
		return parseFieldJson(fieldJsons).get(0);
	}

	public static List< FacilioField > parseFieldJson (JSONArray fieldJsons) throws Exception {

		if (fieldJsons != null) {
			List< FacilioField > fields = new ArrayList<> ();
			Iterator iterator = fieldJsons.iterator ();
			while ( iterator.hasNext () ) {
				Map fieldJson = ( Map ) iterator.next ();
				FieldType fieldType = FieldType.getCFType (Integer.parseInt (fieldJson.get ("dataType").toString ()));
				FacilioField facilioField;
				switch (fieldType) {
					case NUMBER:
					case DECIMAL:
						facilioField = ( NumberField ) FieldUtil.getAsBeanFromMap (fieldJson, NumberField.class);
						break;
					case BOOLEAN:
						facilioField = ( BooleanField ) FieldUtil.getAsBeanFromMap (fieldJson, BooleanField.class);
						break;
					case ENUM:
						facilioField = ( EnumField ) FieldUtil.getAsBeanFromMap (fieldJson, EnumField.class);
						break;
					case LOOKUP:
						facilioField = ( LookupField ) FieldUtil.getAsBeanFromMap (fieldJson, LookupField.class);
						break;
					case FILE:
						facilioField = ( FileField ) FieldUtil.getAsBeanFromMap (fieldJson, FileField.class);
						break;
					case MULTI_ENUM:
						facilioField = ( MultiEnumField ) FieldUtil.getAsBeanFromMap (fieldJson, MultiEnumField.class);
						break;
					case MULTI_LOOKUP:
						facilioField = ( MultiLookupField ) FieldUtil.getAsBeanFromMap (fieldJson, MultiLookupField.class);
						break;
					case LARGE_TEXT:
						facilioField = ( LargeTextField ) FieldUtil.getAsBeanFromMap (fieldJson, LargeTextField.class);
						break;
					case SYSTEM_ENUM:
						facilioField = ( SystemEnumField ) FieldUtil.getAsBeanFromMap (fieldJson, SystemEnumField.class);
						break;
					case STRING_SYSTEM_ENUM:
						facilioField = ( StringSystemEnumField ) FieldUtil.getAsBeanFromMap (fieldJson, StringSystemEnumField.class);
						break;
					case LINE_ITEM:
						facilioField = ( LineItemField ) FieldUtil.getAsBeanFromMap (fieldJson, LineItemField.class);
						break;
					case URL_FIELD:
						facilioField = FieldUtil.getAsBeanFromMap (fieldJson, UrlField.class);
						break;
					case STRING:
					case BIG_STRING:
						facilioField = FieldUtil.getAsBeanFromMap(fieldJson,StringField.class);
						break;
					case DATE:
					case DATE_TIME:
						facilioField = FieldUtil.getAsBeanFromMap(fieldJson,DateField.class);
						break;
					case CURRENCY_FIELD:
						facilioField = FieldUtil.getAsBeanFromMap (fieldJson,CurrencyField.class);
						break;
					case MULTI_CURRENCY_FIELD:
						facilioField = FieldUtil.getAsBeanFromMap (fieldJson,MultiCurrencyField.class);
						break;
					default:
						facilioField = ( FacilioField ) FieldUtil.getAsBeanFromMap (fieldJson, FacilioField.class);
						break;
				}
				fields.add (facilioField);
			}
			return fields;
		}
		return null;
	}

    public static List<UpdateChangeSet> constructChangeSet(long recordId, Map<String, Object> prop, Map<String, FacilioField> fieldMap) {
        Set<String> fieldNames = fieldMap.keySet();
		if (CollectionUtils.isNotEmpty(fieldNames)) {
			fieldNames.remove("currencyCode");
			fieldNames.remove("exchangeRate");
			fieldNames.removeIf(fieldName -> fieldName.endsWith("##baseCurrencyValue"));
		}
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

    public static Object getValue(ModuleBaseWithCustomFields record, long fieldId, FacilioModule module) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField field = modBean.getField(fieldId, module.getModuleId());
		if (field == null) {
			throw new IllegalArgumentException("Invalid field");
		}
		return getValue(record, field);
	}

    public static Object getValue(ModuleBaseWithCustomFields record, FacilioField field) throws Exception {
		Object value=null;
		if (field.isDefault()) {
			try{
				if(PropertyUtils.isReadable(record,field.getName())){
					value = PropertyUtils.getProperty(record, field.getName());
				}else{
					LOGGER.info("No getter method available for field name "+field.getName());
				}
			}
			catch (Exception e){
				LOGGER.info("Exception occured while calling getValue method for field name "+field.getName());
			}

		} else {
			value = record.getDatum(field.getName());
		}
		return value;
	}
	public static Object getProperty(Object bean,String name){
		Object value=null;
		try{
			if(PropertyUtils.isReadable(bean,name)){
				value=PropertyUtils.getProperty(bean, name);
			}else{
				LOGGER.info("No getter method available for field name "+name);
			}
		}catch (Exception e){
			LOGGER.info("Exception occured while calling getProperty method for field name "+name);
		}
		return value;
	}
	public static void setProperty(Object bean,String name,Object value){

		try{
			if(PropertyUtils.isWriteable(bean,name)){
				PropertyUtils.setProperty(bean, name,value);
			}else{
				LOGGER.info("No settrt method available for field name "+name);
			}
		}catch (Exception e){
			LOGGER.info("Exception occured while calling setProperty method for field name "+name);
		}

	}

	public static void setValue(ModuleBaseWithCustomFields record, FacilioField field, Object value) throws Exception {
		if (field.isDefault()) {
			try{
				if(PropertyUtils.isWriteable(record,field.getName())){
					PropertyUtils.setProperty(record, field.getName(), value);
				}else{
					LOGGER.info("No setter method available for field name"+field.getName());
				}
			}
			catch (Exception e){
				LOGGER.info("Exception occured while calling setValue method for field name "+field.getName());
			}
		} else {
			record.setDatum(field.getName(), value);
		}
	}
	public static void setPrimaryValue(ModuleBaseWithCustomFields record,Object value){
		try{
			if(PropertyUtils.isWriteable(record,"primaryValue")){
				PropertyUtils.setProperty(record, "primaryValue", value);
			}else{
				LOGGER.info("No setter method available for field name primaryValue");
			}

		}catch (Exception e){
			LOGGER.info("Exception occured while calling setPrimaryValue method");
		}

	}

	public static List<FacilioField> removeMultiRecordFields(Collection<FacilioField> fields) {
		return	fields
				.stream()
				.filter(f -> !(f.getDataTypeEnum() != null && f.getDataTypeEnum().isRelRecordField()))
				.collect(Collectors.toList());
	}

    public static List<FacilioField> getFieldsByAccessType(Long accessType, String moduleName) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> allFields = modBean.getAllFields(moduleName);
        if (accessType == null || accessType <= 0) {
            throw new IllegalArgumentException("Invalid access type.");
        }

        List<FacilioField> selectedFields = new ArrayList<>();
        for (FacilioField field: allFields) {
            long fieldAccessType = field.getAccessType();
            if (fieldAccessType < 0 || (fieldAccessType & accessType) == accessType) { //Changing the behaviour to return the field anyway if no field access is present
				selectedFields.add(field);
            }
        }
        return selectedFields;
    }
    
    public static Set<String> getSiteIdAllowedModules() {
    	return SITE_ID_ALLOWED_MODULES;
    }
    
    
    public static Set<String> getSystemUpdatedFields() {
    	return SYSTEM_UPDATED_FIELDS;
    }
    
    public static boolean isSystemUpdatedField(String fieldName) {
		return SYSTEM_UPDATED_FIELDS.contains(fieldName);
	}
    
    public static FieldPermissionContext getFieldPermission(long fieldId, long moduleId) throws Exception {
    	GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFieldModulePermissionFields())
				.table(ModuleFactory.getFieldModulePermissionModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", String.valueOf(moduleId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("FIELD_ID", "fieldId", String.valueOf(fieldId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("CHECK_TYPE", "checkType", String.valueOf(CheckType.FIELD.getIndex()), NumberOperators.EQUALS))
				;
		if(AccountUtil.getCurrentUser().getRoleId() > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("ROLE_ID", "roleId", String.valueOf(AccountUtil.getCurrentUser().getRoleId()), NumberOperators.EQUALS));
		}
		List<Map<String, Object>> props = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(props)) {
			return getAsBeanFromMap(props.get(0), FieldPermissionContext.class);
		}
		return null;
		
    }
    
    public static List<FacilioField> getPermissionRestrictedFields(FacilioModule module, PermissionType permissionType, Boolean validateFieldPermissions) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> allFields = modBean.getAllFields(module.getName());
        List<FacilioField> restrictedFields = new ArrayList<FacilioField>();

        if(permissionType == PermissionType.READ_WRITE) {
			if (CollectionUtils.isNotEmpty(allFields)) {
				for (FacilioField field : allFields) {
					if(field.isSystemUpdated()) {
						restrictedFields.add(field);
					}
				}
			}
		}


		//all fields are permissible to super admin
		if(AccountUtil.getCurrentUser().isSuperAdmin() || (validateFieldPermissions != null && !validateFieldPermissions)) {
			return restrictedFields;
		}

		List<Long> permissibleFieldIds = modBean.getPermissibleFieldIds(module, permissionType.getIndex());
    	for(FacilioField field : allFields) {
			if(permissionType == PermissionType.READ_WRITE && field.isSystemUpdated()) {
				continue;
			}
			if(field.getFieldId() != -1 && !permissibleFieldIds.contains(field.getFieldId())) {
				restrictedFields.add(field);
			}
		}
	
		return restrictedFields;
		
    }

	public static Collection<FacilioField> getPermissibleFields(Collection<FacilioField> neededFields, String moduleName, PermissionType permissionType, Boolean validateFieldPermissions) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		if(CollectionUtils.isEmpty(neededFields)){
			neededFields = modBean.getAllFields(module.getName());
		}

		if(permissionType == PermissionType.READ_WRITE) {
			if (CollectionUtils.isNotEmpty(neededFields)) {
				List<FacilioField> toRemove = new ArrayList<>();

				for (FacilioField field : neededFields) {
					if(field.isSystemUpdated()){
						toRemove.add(field);
					}
				}
				if(CollectionUtils.isNotEmpty(toRemove)){
					neededFields.removeAll(toRemove);
				}
			}
		}
		//all fields are permissible to super admin
		if(AccountUtil.getCurrentUser().isSuperAdmin() || (validateFieldPermissions != null && !validateFieldPermissions)) {
			return neededFields;
		}

		List<FacilioField> permissibleFields = new ArrayList<>();
		List<Long> permissibleFieldIds = modBean.getPermissibleFieldIds(module, permissionType.getIndex());
		for(FacilioField field : neededFields) {
			if(permissibleFieldIds.contains(field.getFieldId())){
				permissibleFields.add(field);
			}
		}
		return permissibleFields;

	}

	public static List<Long> getPermissibleChildModules(FacilioModule parentModule, int permissionType) throws Exception{

		List<Long> permittedSubModuleIds = new ArrayList<Long>();
		//get extended modules' permissible sub modules also
		FacilioModule extendedModule = parentModule.getExtendModule();
		List<Long> extendedModuleIds = new ArrayList<Long>();
		while(extendedModule != null) {
			extendedModuleIds.add(extendedModule.getModuleId());
			extendedModule = extendedModule.getExtendModule();
		}
		extendedModuleIds.add(parentModule.getModuleId());
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getFieldModulePermissionFields())
				.table(ModuleFactory.getFieldModulePermissionModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("MODULE_ID", "moduleId", StringUtils.join(extendedModuleIds, ","), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("CHECK_TYPE", "checkType", String.valueOf(FieldPermissionContext.CheckType.MODULE.getIndex()), NumberOperators.EQUALS))
				;

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
				permittedSubModuleIds.add((Long) map.get("subModuleId"));
			}
		}
		return permittedSubModuleIds;
	}
	
	private static final List<String> RECORD_ID_AS_ID_MODULES = Collections.unmodifiableList(Arrays.asList(new String[] {
			ContextNames.ASSET, ContextNames.SERVICE_REQUEST , ContextNames.BASE_MAIL_MESSAGE
	}));
	public static String getRecordIdFieldName (@NonNull FacilioModule module) {
		return RECORD_ID_AS_ID_MODULES.contains(module.getName()) ? "ID" : "Record ID";
	}

	public static final List<String> INTERNAL_FIELDS = Collections.unmodifiableList(Arrays.asList(new String[] {
			ContextNames.SITE_ID, ContextNames.STATE_FLOW_ID, ContextNames.SLA_POLICY_ID, ContextNames.FORM_ID,  ContextNames.APPROVAL_STATUS, "approvalFlowId",
	}));

	public static final boolean isSupplementRecord (FacilioField field) { // By default will exclude system rel record
		return field instanceof SupplementRecord && !field.getDataTypeEnum().isSystemRelRecord();
	}

	public static final List<String> WORKORDER_VIEW_FIEDS = Arrays.asList("subject","category","description","dueDate","responseDueDate",
			"moduleState","sourceType","assignedTo","createdTime","createdBy","priority","resource","type","modifiedTime","actualWorkStart","actualWorkEnd",
			"totalCost","siteId","vendor","requester","noOfNotes","noOfTasks","noOfAttachments","attachmentPreview");

	public  static boolean isGeoLocationField(FacilioField field){
		return field.getDisplayType()== FacilioField.FieldDisplayType.GEO_LOCATION && Objects.equals(((LookupField) field).getLookupModule().getName(), ContextNames.LOCATION);
	}
}
