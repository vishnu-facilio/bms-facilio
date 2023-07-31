package com.facilio.util;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.bmsconsoleV3.enums.MultiCurrencyParentVsSubModule;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.MultiCurrencyField;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.v3.context.Constants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import com.facilio.beans.ModuleBean;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class CurrencyUtil {
	public static Logger LOGGER = LogManager.getLogger(CurrencyUtil.class.getName());
	private static final String BASE_CURRENCY_FIELD_NAME = "##{0}##"+"baseCurrencyValue";

	public static CurrencyContext getBaseCurrency() throws Exception {
		Map<String, Object> orgInfo = CommonCommandUtil.getOrgInfo(AccountUtil.getCurrentOrg().getId(), "baseCurrencyCode");
		if(orgInfo != null) {
			return getCurrencyFromCode((String) orgInfo.get("value"));
		}
		return null;
	}

	public static Map<String, Object> getCurrencyInfo() throws Exception {
		try {
			Organization organization = AccountUtil.getCurrentOrg();

			String currencyCode, displaySymbol = null;
			Map<String, Object> currencyInfo = new HashMap<>();
			CurrencyContext baseCurrency = CurrencyUtil.getBaseCurrency();

			if (baseCurrency != null) {
				currencyCode = baseCurrency.getCurrencyCode();
				displaySymbol = baseCurrency.getDisplaySymbol();
			} else {
				currencyCode = (organization != null && StringUtils.isNotEmpty(organization.getCurrency())) ? organization.getCurrency() : "USD";
				if (currencyCode != null) {
					Unit metricUnit = Unit.getUnitsForMetric(Metric.CURRENCY).stream()
							.filter(unit -> unit.getDisplayName().equals(currencyCode))
							.findFirst()
							.orElse(null);
					displaySymbol = (metricUnit != null) ? metricUnit.getSymbol() : currencyCode;
				}
			}

			currencyInfo.put("multiCurrencyEnabled", baseCurrency != null);
			currencyInfo.put("currencyCode", currencyCode);
			currencyInfo.put("displaySymbol", displaySymbol);

			return currencyInfo;
		} catch (Exception e) {
			LOGGER.info("Currency Info Exception" + e);
			return null;
		}
	}

	public static void setDefaultProps(CurrencyContext currencyContext, boolean isAdd) {
		if (isAdd) {
			currencyContext.setSysCreatedTime(System.currentTimeMillis());
			currencyContext.setSysCreatedBy(AccountUtil.getCurrentUser().getId());
		}
		currencyContext.setSysModifiedTime(System.currentTimeMillis());
		currencyContext.setSysModifiedBy(AccountUtil.getCurrentUser().getId());
	}

	public static long addCurrency(CurrencyContext currencyContext) throws Exception {
		setDefaultProps(currencyContext, true);
		currencyContext.setId(-1);

		if (currencyContext.isBaseCurrency()) {
			if (getBaseCurrency() == null) {
				CommonCommandUtil.insertOrgInfo("baseCurrencyCode", currencyContext.getCurrencyCode());

				Organization org = new Organization();
				org.setCurrency(currencyContext.getCurrencyCode());
				AccountUtil.getOrgBean().updateOrg(AccountUtil.getCurrentOrg().getOrgId(), org);
			} else {
				throw new IllegalArgumentException("Base Currency is already defined");
			}
		}

		Map<String, Object> currencyProps = FieldUtil.getAsProperties(currencyContext);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getCurrencyModule().getTableName())
				.fields(FieldFactory.getCurrencyFields())
				.addRecord(currencyProps);

		insertBuilder.save();

		return (long) currencyProps.get("id");
	}

	public static int updateCurrency(CurrencyContext currencyContext) throws Exception {
		setDefaultProps(currencyContext, false);
		Map<String, Object> currencyProps = FieldUtil.getAsProperties(currencyContext);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(ModuleFactory.getCurrencyModule().getTableName())
				.fields(FieldFactory.getCurrencyFields())
				.andCondition(CriteriaAPI.getIdCondition(currencyContext.getId(), ModuleFactory.getCurrencyModule()));

		int count = updateBuilder.update(currencyProps);

		return count;
	}

	public static List<CurrencyContext> getCurrencyList(JSONObject pagination, String searchString) throws Exception {
		List<FacilioField> fields = FieldFactory.getCurrencyFields();
		Map<String, FacilioField> currencyFields = FieldFactory.getAsMap(fields);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getCurrencyModule().getTableName())
				.select(fields);

		if (StringUtils.isNotEmpty(searchString)) {
			Criteria searchCriteria = new Criteria();
			searchCriteria.addAndCondition(CriteriaAPI.getCondition(currencyFields.get("displayName"), searchString, StringOperators.CONTAINS));
			searchCriteria.addOrCondition(CriteriaAPI.getCondition(currencyFields.get("currencyCode"), searchString, StringOperators.CONTAINS));
			selectBuilder.andCriteria(searchCriteria);
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

		StringBuilder orderBy = new StringBuilder().append(currencyFields.get("id").getCompleteColumnName()).append(" ASC");
		selectBuilder.orderBy(orderBy.toString());

		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			return FieldUtil.getAsBeanListFromMapList(props, CurrencyContext.class);
		}
		return null;
	}

	public static List<CurrencyContext> getUnconfiguredCurrencies() throws Exception {
		FacilioModule module = ModuleFactory.getCurrencyModule();

		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getField("currencyCode", "CURRENCY_CODE", module, FieldType.STRING));
		fields.add(FieldFactory.getField("displaySymbol", "DISPLAY_SYMBOL", module, FieldType.STRING));

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
				.select(fields);

		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			return FieldUtil.getAsBeanListFromMapList(props, CurrencyContext.class);
		}
		return null;
	}

	public static long getCurrenciesCount(String searchString) throws Exception {
		List<FacilioField> fields = FieldFactory.getCurrencyFields();
		Map<String, FacilioField> currencyFields = FieldFactory.getAsMap(fields);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getCurrencyModule().getTableName())
				.select(FieldFactory.getCountField());

		if (StringUtils.isNotEmpty(searchString)) {
			Criteria searchCriteria = new Criteria();
			searchCriteria.addAndCondition(CriteriaAPI.getCondition(currencyFields.get("displayName"), searchString, StringOperators.CONTAINS));
			searchCriteria.addOrCondition(CriteriaAPI.getCondition(currencyFields.get("currencyCode"), searchString, StringOperators.CONTAINS));
			selectBuilder.andCriteria(searchCriteria);
		}

		Map<String, Object> modulesMap = selectBuilder.fetchFirst();
		long count = MapUtils.isNotEmpty(modulesMap) ? (long) modulesMap.get("count") : 0;

		return count;
	}

	public static double getExchangeRateOfBaseCurrency(long currencyId) throws Exception {
		CurrencyContext currencyContext = getCurrencyFromId(currencyId);
		return currencyContext.getExchangeRate();
	}

	public static CurrencyContext getCurrencyFromId(long currencyId) throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getCurrencyFields())
				.table(ModuleFactory.getCurrencyModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("ID","id", String.valueOf(currencyId), NumberOperators.EQUALS));
		List<Map<String, Object>> props = builder.get();
		if(CollectionUtils.isNotEmpty(props)) {
			return FieldUtil.getAsBeanFromMap(props.get(0), CurrencyContext.class);
		}
		return null;
	}

	public static CurrencyContext getCurrencyFromCode(String currencyCode) throws Exception {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getCurrencyFields())
				.table(ModuleFactory.getCurrencyModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("CURRENCY_CODE","currencyCode", currencyCode, StringOperators.IS));
		List<Map<String, Object>> props = builder.get();
		if(CollectionUtils.isNotEmpty(props)) {
			return FieldUtil.getAsBeanFromMap(props.get(0), CurrencyContext.class);
		}
		return null;
	}

	public static Map<String, CurrencyContext> getCurrencyMap() throws Exception {
		List<CurrencyContext> currencyList = getCurrencyList(null, null);
		Map<String, CurrencyContext> currencyCodeVsCurrency = CollectionUtils.isNotEmpty(currencyList) ?
				currencyList.stream().collect(Collectors.toMap(CurrencyContext::getCurrencyCode, currency -> currency, (a, b) -> b)) : new HashMap<>();
		return currencyCodeVsCurrency;
	}

	public static List<FacilioField> getMultiCurrencyFields(String moduleName) throws Exception {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("DATA_TYPE", "dataType", String.valueOf(FieldType.MULTI_CURRENCY_FIELD.getTypeAsInt()), NumberOperators.EQUALS));

		ModuleBean moduleBean = Constants.getModBean();
		return moduleBean.getAllFields(moduleName, null, null, criteria);
	}

	public static void setBaseCurrencyConvertedValuesForRecord(Map<String, Object> props, List<FacilioField> multiCurrencyFields) throws Exception {
		if (CollectionUtils.isEmpty(multiCurrencyFields)) {
			return;
		}
		for (FacilioField multiCurrencyField : multiCurrencyFields) {
			Double currencyValue = props.get(multiCurrencyField.getName()) != null && StringUtils.isNotEmpty(String.valueOf(props.get(multiCurrencyField.getName())))
					? Double.parseDouble(String.valueOf(props.get(multiCurrencyField.getName())))  : null;
			if (currencyValue != null) {
				String baseCurrencyFieldName = MessageFormat.format(BASE_CURRENCY_FIELD_NAME, multiCurrencyField.getName());
				double exchangeRate = props.get("exchangeRate") != null ? Double.parseDouble(props.get("exchangeRate") + "") : 1;
				double baseCurrencyValue = CurrencyUtil.getConvertedBaseCurrencyValue(currencyValue, exchangeRate);
				props.put(baseCurrencyFieldName, baseCurrencyValue);
			}
		}
	}

	public static List<FacilioField> getBaseCurrencyFieldsForModule(FacilioModule module) throws Exception {
		List<FacilioField> multiCurrencyFields = getMultiCurrencyFields(module.getName());
		return generateBaseCurrencyColumnFields(multiCurrencyFields);
	}

	public static List<FacilioField> generateBaseCurrencyColumnFields(List<FacilioField> multiCurrencyFields) {
		if(CollectionUtils.isEmpty(multiCurrencyFields)){
			return null;
		}
		List<FacilioField> baseCurrencyValueFields = new ArrayList<>();
		for (FacilioField multiCurrencyField : multiCurrencyFields) {
			String baseCurrencyFieldName = MessageFormat.format(BASE_CURRENCY_FIELD_NAME, multiCurrencyField.getName());
			FacilioField field = multiCurrencyField.clone();
			field.setName(baseCurrencyFieldName);
			field.setColumnName(((MultiCurrencyField)multiCurrencyField).getBaseCurrencyValueColumnName());
			field.setDataType(FieldType.DECIMAL);
			baseCurrencyValueFields.add(field);
		}
		return baseCurrencyValueFields;
	}

	public static double getConvertedBaseCurrencyValue(double value, String currencyCode) throws Exception  {
		return getConvertedBaseCurrencyValue(value, getCurrencyFromCode(currencyCode).getExchangeRate());
	}

	public static double getConvertedBaseCurrencyValue(double value, long currencyId) throws Exception  {
		return getConvertedBaseCurrencyValue(value, getCurrencyFromId(currencyId).getExchangeRate());
	}

	public static double getConvertedBaseCurrencyValue(double value, CurrencyContext currencyObj) throws Exception  {
		return getConvertedBaseCurrencyValue(value, currencyObj.getExchangeRate());
	}

	public static double getConvertedBaseCurrencyValue(double value, double exchangeRate) throws Exception  {
		return exchangeRate > 0 ? (value / exchangeRate) : value;
	}

	public static double getEquivalentCurrencyValue(double baseCurrencyValue, double exchangeRate) throws Exception {
		return exchangeRate > 0 ? (baseCurrencyValue * exchangeRate) : baseCurrencyValue;
	}

	public static void replaceCurrencyValueWithBaseCurrencyValue(Map<String, Object> props, List<FacilioField> multiCurrencyFields, CurrencyContext baseCurrency, Map<String, CurrencyContext> currencyCodeVsCurrency) throws Exception{
		String currencyCode = props.containsKey("currencyCode") ? (String) props.get("currencyCode") : null;
		if(CollectionUtils.isEmpty(multiCurrencyFields) || baseCurrency == null || MapUtils.isEmpty(currencyCodeVsCurrency) || currencyCodeVsCurrency.size() == 1 || StringUtils.isEmpty(currencyCode)){
			return;
		}

		CurrencyContext currency = currencyCodeVsCurrency.get(currencyCode);
		for (FacilioField multiCurrencyField : multiCurrencyFields) {
			if (props.containsKey(multiCurrencyField.getName())) {
				Double currencyValue = props.get(multiCurrencyField.getName()) != null && StringUtils.isNotEmpty(String.valueOf(props.get(multiCurrencyField.getName())))
						? Double.parseDouble(String.valueOf(props.get(multiCurrencyField.getName())))  : null;
				if(currencyValue != null) {
					double exchangeRate = currency.getExchangeRate();
					double baseCurrencyValue = getConvertedBaseCurrencyValue(currencyValue, exchangeRate);
					props.put(multiCurrencyField.getName(), baseCurrencyValue);
				}
			}
		}
	}
	public static boolean isMultiCurrencyEnabledModule(FacilioModule module) {
		return FacilioConstants.MultiCurrency.MULTI_CURRENCY_ENABLED_MODULES.contains(module.getName())
				|| (module.isCustom() && module.getTypeEnum().equals(FacilioModule.ModuleType.BASE_ENTITY));
	}
	public static void setCurrencyCodeAndExchangeRateForWrite(ModuleBaseWithCustomFields record, CurrencyContext baseCurrency,
															  Map<String, CurrencyContext> currencyCodeVsCurrency, String parentRecordCurrencyCode, Double parentRecordExchangeRate) {
		if (baseCurrency == null || StringUtils.isEmpty(parentRecordCurrencyCode) || MapUtils.isEmpty(currencyCodeVsCurrency) || currencyCodeVsCurrency.size() == 1) {
			record.setCurrencyCode(null);
			record.setExchangeRate(1);
		} else {
			parentRecordCurrencyCode = StringUtils.equals(parentRecordCurrencyCode, baseCurrency.getCurrencyCode()) ? null : parentRecordCurrencyCode;
			record.setCurrencyCode(parentRecordCurrencyCode);
			record.setExchangeRate(parentRecordExchangeRate);
		}
	}
	public static void setCurrencyCodeAndExchangeRateForWrite(Map<String, Object> record, CurrencyContext baseCurrency,
															  Map<String, CurrencyContext> currencyCodeVsCurrency, String parentRecordCurrencyCode, Double parentRecordExchangeRate) {
		if (baseCurrency == null || StringUtils.isEmpty(parentRecordCurrencyCode) || MapUtils.isEmpty(currencyCodeVsCurrency) || currencyCodeVsCurrency.size() == 1) {
			record.put("currencyCode", null);
			record.put("exchangeRate", 1);
		} else {
			parentRecordCurrencyCode = StringUtils.equals(parentRecordCurrencyCode, baseCurrency.getCurrencyCode()) ? null : parentRecordCurrencyCode;
			record.put("currencyCode", parentRecordCurrencyCode);
			record.put("exchangeRate", parentRecordExchangeRate);
		}
	}
	public static void checkAndFillBaseCurrencyToRecord(ModuleBaseWithCustomFields record, Map<String, Object> baseCurrencyInfo) {
		if(StringUtils.isEmpty(record.getCurrencyCode())){
			String currencyCode = (String) baseCurrencyInfo.get("currencyCode");
			record.setCurrencyCode(currencyCode);
			if(record.getExchangeRate() <= 0){
				record.setExchangeRate(1);
			}
		}
	}

	public static boolean checkAndUpdateCurrencyCodeAndExchangeRate(Map<String, Object> newRecordAsMap, ModuleBaseWithCustomFields oldRecord, CurrencyContext baseCurrency,
																	Map<String, CurrencyContext> currencyCodeVsCurrency) throws Exception {
		String oldCurrencyCode = CurrencyUtil.getCurrencyCodeFromRecordOrBaseCurrencyDefault(FieldUtil.getAsProperties(oldRecord), baseCurrency);
		String newCurrencyCode = CurrencyUtil.getCurrencyCodeFromRecordOldCurrencyDefault(newRecordAsMap, oldCurrencyCode);
		newCurrencyCode = (MapUtils.isNotEmpty(currencyCodeVsCurrency) && currencyCodeVsCurrency.containsKey(newCurrencyCode)) ? newCurrencyCode : oldCurrencyCode;
		return checkAndUpdateCurrencyCodeAndExchangeRate(newRecordAsMap, oldRecord, baseCurrency, currencyCodeVsCurrency, newCurrencyCode, oldCurrencyCode);
	}

	public static boolean checkAndUpdateCurrencyCodeAndExchangeRate(Map<String, Object> newRecordAsMap, ModuleBaseWithCustomFields oldRecord, CurrencyContext baseCurrency,
																	Map<String, CurrencyContext> currencyCodeVsCurrency, String newCurrencyCode, String oldCurrencyCode) {
		String baseCurrencyCurrencyCode = baseCurrency != null ? baseCurrency.getCurrencyCode() : null;
		CurrencyContext currency = (newCurrencyCode != null && MapUtils.isNotEmpty(currencyCodeVsCurrency))
				? (currencyCodeVsCurrency.containsKey(newCurrencyCode) ? currencyCodeVsCurrency.get(newCurrencyCode) : currencyCodeVsCurrency.get(oldCurrencyCode)) : null;

		if (baseCurrency == null || (StringUtils.equals(oldCurrencyCode, baseCurrencyCurrencyCode) && StringUtils.equals(newCurrencyCode, baseCurrencyCurrencyCode))) {
			setCurrencyAndExchangeRate(newRecordAsMap, null, 1);
		} else if (currency != null && currencyCodeVsCurrency.size() > 1) {
			if (!StringUtils.equals(newCurrencyCode, oldCurrencyCode)) {
				double exchangeRate = currency.getExchangeRate();
				newCurrencyCode = StringUtils.equals(newCurrencyCode, baseCurrency.getCurrencyCode()) ? null : newCurrencyCode;
				newRecordAsMap.put("currencyCode", newCurrencyCode);
				setCurrencyAndExchangeRate(newRecordAsMap, newCurrencyCode, exchangeRate);
				return true;
			} else {
				String currencyCode = oldRecord.getCurrencyCode();
				currencyCode = StringUtils.equals(currencyCode, baseCurrency.getCurrencyCode()) ? null : currencyCode;
				newRecordAsMap.put("currencyCode", currencyCode);
				newRecordAsMap.put("exchangeRate", oldRecord.getExchangeRate());
			}
		} else {
			setCurrencyAndExchangeRate(newRecordAsMap, null, 1);
		}

		return false;
	}
	private static void setCurrencyAndExchangeRate(Map<String, Object> recordMap, String currencyCode, double exchangeRate) {
		recordMap.put("currencyCode", currencyCode);
		recordMap.put("exchangeRate", exchangeRate);
	}
	public static String getCurrencyCodeFromRecordOrBaseCurrencyDefault(Map<String, Object> oldOrParentRecord, CurrencyContext baseCurrency) {
		if(MapUtils.isEmpty(oldOrParentRecord)) oldOrParentRecord = new HashMap<>();
		String baseCurrencyCurrencyCode = baseCurrency != null ? baseCurrency.getCurrencyCode() : null;
		return StringUtils.defaultIfEmpty((String) oldOrParentRecord.get("currencyCode"), baseCurrencyCurrencyCode);
	}
	public static String getCurrencyCodeFromRecordOldCurrencyDefault(Map<String, Object> newRecordAsMap, String oldCurrencyCode) {
		return StringUtils.defaultIfEmpty((String) newRecordAsMap.get("currencyCode"), oldCurrencyCode);
	}


	public static Long getIdFromObject(Object object) {
		if (object == null) {
			return null;
		}

		if (object instanceof Map) {
			return (Long) ((Map<?, ?>) object).get("id");
		} else if (object instanceof Number) {
			return (Long) object;
		}

		return null;
	}

	public static List<ModuleBaseWithCustomFields> addMultiCurrencyData(String moduleName, List<FacilioField> multiCurrencyFields,
																		List<? extends ModuleBaseWithCustomFields> records, Class beanClass,
																		CurrencyContext baseCurrency, Map<String, CurrencyContext> currencyCodeVsCurrency) throws Exception {
		List<ModuleBaseWithCustomFields> newRecords = new ArrayList<>();
		for (ModuleBaseWithCustomFields record : records) {
			String currencyCode = record.getCurrencyCode();
			CurrencyContext currency = StringUtils.isNotEmpty(currencyCode) ? MapUtils.isNotEmpty(currencyCodeVsCurrency) ? currencyCodeVsCurrency.get(currencyCode) : null : null;

			if (currency == null || baseCurrency == null || StringUtils.isEmpty(currencyCode) || currencyCodeVsCurrency.size() == 1 || StringUtils.equals(currencyCode, baseCurrency.getCurrencyCode())) {
				record.setCurrencyCode(null);
				record.setExchangeRate(1);
			} else {
				double exchangeRate = currency.getExchangeRate();
				record.setExchangeRate(exchangeRate);
			}

			Map<String, Object> props = FieldUtil.getAsProperties(record);
			setBaseCurrencyConvertedValuesForRecord(props, multiCurrencyFields);

			newRecords.add((ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromMap(props, beanClass));
		}

		Map<String, String> subModuleNameVsParentModuleName = MultiCurrencyParentVsSubModule.SUBMODULE_VS_PARENT_MODULE;
		if (subModuleNameVsParentModuleName.containsKey(moduleName)) {
			List<ModuleBaseWithCustomFields> updatedNewRecords = updateRecordCurrencyDataBasedOnParentModule(moduleName, baseCurrency, beanClass, multiCurrencyFields, newRecords);
			if (CollectionUtils.isNotEmpty(updatedNewRecords)) {
				newRecords = updatedNewRecords;
			}
		}

		return newRecords;
	}

	public static List<ModuleBaseWithCustomFields> updateRecordCurrencyDataBasedOnParentModule(String moduleName, CurrencyContext baseCurrency, Class beanClass,
																							   List<FacilioField> multiCurrencyFields, List<? extends ModuleBaseWithCustomFields> records) throws Exception {
		ModuleBean modBean = Constants.getModBean();
		Map<String, String> subModuleNameVsParentModuleName = MultiCurrencyParentVsSubModule.SUBMODULE_VS_PARENT_MODULE;
		Map<String, FacilioField> subModuleNameVsParentFieldMap = MultiCurrencyParentVsSubModule.SUBMODULE_VS_PARENT_MODULE_FIELD;
		FacilioField parentModuleField = subModuleNameVsParentFieldMap.get(moduleName);
		FacilioModule parentModule = modBean.getModule(subModuleNameVsParentModuleName.get(moduleName));

		Set<Long> parentRecordIds = new HashSet<>();
		for (ModuleBaseWithCustomFields record : records) {
			Map<String, Object> recordAsProperties = FieldUtil.getAsProperties(record);
			Object object = recordAsProperties.get(parentModuleField.getName());
			Long id = getIdFromObject(object);
			if(id != null) parentRecordIds.add(id);
		}

		if(CollectionUtils.isNotEmpty(parentRecordIds)) {
			List<FacilioField> selectFields = FieldFactory.getCurrencyPropsFields(parentModule);
			selectFields.add(FieldFactory.getIdField(parentModule));
			List<ModuleBaseWithCustomFields> newRecords = new ArrayList<>();

			SelectRecordsBuilder<ModuleBaseWithCustomFields> selectRecordsBuilder = new SelectRecordsBuilder<>()
					.select(selectFields)
					.module(parentModule)
					.beanClass(ModuleBaseWithCustomFields.class)
					.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(parentModule), StringUtils.join(parentRecordIds, ','), NumberOperators.EQUALS));
			Map<Long, ModuleBaseWithCustomFields> parentCurrencyFieldRecordsMap = selectRecordsBuilder.getAsMap();

			for (ModuleBaseWithCustomFields record : records) {
				Map<String, Object> recordAsMap = FieldUtil.getAsProperties(record);

				Object object = recordAsMap.get(parentModuleField.getName());
				Long id = getIdFromObject(object);

				if(id != null && parentCurrencyFieldRecordsMap.containsKey(id)) {
					ModuleBaseWithCustomFields parentCurrencyFieldRecord = parentCurrencyFieldRecordsMap.get(id);
					computeCurrencyData(recordAsMap, parentCurrencyFieldRecord, multiCurrencyFields, baseCurrency);
				}

				newRecords.add((ModuleBaseWithCustomFields) FieldUtil.getAsBeanFromMap(recordAsMap, beanClass));
			}

			return newRecords;
		}

		return null;
	}

	public static void computeCurrencyData(Map<String, Object> recordToBeUpdated, ModuleBaseWithCustomFields parentRecord, List<FacilioField> multiCurrencyFields,
										   CurrencyContext baseCurrency) throws Exception {
		String currencyCodeFromRecord = getCurrencyCodeFromRecordOrBaseCurrencyDefault(FieldUtil.getAsProperties(recordToBeUpdated), baseCurrency);
		double exchangeRateFromRecord = recordToBeUpdated.get("exchangeRate") != null ? (Double) recordToBeUpdated.get("exchangeRate") : 1;
		String currencyCodeFromParentRecord = parentRecord.getCurrencyCode();
		double exchangeRateFromParentRecord = parentRecord.getExchangeRate();

		if (StringUtils.isEmpty(currencyCodeFromRecord) || StringUtils.equals(currencyCodeFromRecord, currencyCodeFromParentRecord)) {
			return;
		}

		for (FacilioField multiCurrencyField : multiCurrencyFields) {
			if (recordToBeUpdated.containsKey(multiCurrencyField.getName()) && recordToBeUpdated.get(multiCurrencyField.getName()) != null) {
				Double currencyValue = (Double) recordToBeUpdated.get(multiCurrencyField.getName());
				double baseCurrencyValue = getConvertedBaseCurrencyValue(currencyValue, exchangeRateFromRecord);
				double updatedCurrencyValue = getEquivalentCurrencyValue(baseCurrencyValue, exchangeRateFromParentRecord);
				recordToBeUpdated.put(multiCurrencyField.getName(), updatedCurrencyValue);
			}
		}

		recordToBeUpdated.put("currencyCode", currencyCodeFromParentRecord);
		recordToBeUpdated.put("exchangeRate", exchangeRateFromParentRecord);

		setBaseCurrencyConvertedValuesForRecord(recordToBeUpdated, multiCurrencyFields);
	}
	public static void computeCurrencyValueForFieldsNotInPatch(String newCurrencyCode, Map<String, Object> newProp,
														 Map<String, Object> oldProp, List<String> patchFieldNames, List<FacilioField> multiCurrencyFields,
														 Map<String, CurrencyContext> currencyCodeVsCurrency) throws Exception {
		if (StringUtils.isEmpty(newCurrencyCode) || MapUtils.isEmpty(currencyCodeVsCurrency) || CollectionUtils.isEmpty(multiCurrencyFields) || CollectionUtils.isEmpty(patchFieldNames)) {
			return;
		}
		CurrencyContext newCurrency = currencyCodeVsCurrency.get(newCurrencyCode);

		for (FacilioField multiCurrencyField : multiCurrencyFields) {
			if(!patchFieldNames.contains(multiCurrencyField.getName())) {
				Double oldCurrencyValue = oldProp.containsKey(multiCurrencyField.getName()) ? (Double) oldProp.get(multiCurrencyField.getName()) : null;
				if (oldCurrencyValue != null) {
					double oldExchangeRate = oldProp.get("exchangeRate") != null ? Double.parseDouble(oldProp.get("exchangeRate") + "") : 1;
					double baseCurrencyValue = getConvertedBaseCurrencyValue(oldCurrencyValue, oldExchangeRate);
					double updatedCurrencyValue = getEquivalentCurrencyValue(baseCurrencyValue, newCurrency.getExchangeRate());
					newProp.put(multiCurrencyField.getName(), updatedCurrencyValue);
				}
			}
		}
	}

	public static List<FacilioField> getMultiCurrencyFieldsFromFields(List<FacilioField> fields) {
		return fields.stream().filter(field -> field instanceof MultiCurrencyField)
				.map(field -> (MultiCurrencyField) field).collect(Collectors.toList());
	}

	public static String getCurrencyCodeFromRecordOrBaseCurrencyDefault(ModuleBaseWithCustomFields record, CurrencyContext baseCurrency) throws Exception {
		return getCurrencyCodeFromRecordOrBaseCurrencyDefault(FieldUtil.getAsProperties(record), baseCurrency);
	}
	public static void checkAndUpdateCurrencyProps(Map<String, Object> recordToBeUpdated, ModuleBaseWithCustomFields oldRecordCurrencyCodeToBeCompared, CurrencyContext baseCurrency,
												   Map<String, CurrencyContext> currencyMap, List<String> patchFieldNames, List<FacilioField> multiCurrencyFields) throws Exception {
		Map<String, Object> oldRecordForOldCurrencyCodeAndExchangeRate = new HashMap<>(recordToBeUpdated);
		oldRecordForOldCurrencyCodeAndExchangeRate.put("currencyCode", oldRecordCurrencyCodeToBeCompared.getCurrencyCode());
		oldRecordForOldCurrencyCodeAndExchangeRate.put("exchangeRate", oldRecordCurrencyCodeToBeCompared.getExchangeRate());

		String oldCurrencyCode = CurrencyUtil.getCurrencyCodeFromRecordOrBaseCurrencyDefault(FieldUtil.getAsProperties(oldRecordCurrencyCodeToBeCompared), baseCurrency);
		String newCurrencyCode = CurrencyUtil.getCurrencyCodeFromRecordOrBaseCurrencyDefault(recordToBeUpdated, baseCurrency);
		newCurrencyCode = (MapUtils.isNotEmpty(currencyMap) && currencyMap.containsKey(newCurrencyCode)) ? newCurrencyCode : oldCurrencyCode;

		boolean isCurrencyCodeChanged = CurrencyUtil.checkAndUpdateCurrencyCodeAndExchangeRate(recordToBeUpdated, oldRecordCurrencyCodeToBeCompared, baseCurrency, currencyMap, newCurrencyCode, oldCurrencyCode);
		if (isCurrencyCodeChanged) {
			CurrencyUtil.computeCurrencyValueForFieldsNotInPatch(newCurrencyCode, recordToBeUpdated, oldRecordForOldCurrencyCodeAndExchangeRate, patchFieldNames, multiCurrencyFields, currencyMap);
		}

		CurrencyUtil.setBaseCurrencyConvertedValuesForRecord(recordToBeUpdated, multiCurrencyFields);
	}

	public static void addMultiCurrencyFieldsToFields(List<FacilioField> fields, FacilioModule module) {
		List<FacilioField> multiCurrencyFieldsFromFields = CurrencyUtil.getMultiCurrencyFieldsFromFields(fields);
		fields.addAll(FieldFactory.getCurrencyPropsFields(module));
		List<FacilioField> baseCurrencyColumnFields = CurrencyUtil.generateBaseCurrencyColumnFields(multiCurrencyFieldsFromFields);
		if (CollectionUtils.isNotEmpty(baseCurrencyColumnFields)) {
			fields.addAll(baseCurrencyColumnFields);
		}
	}
}
