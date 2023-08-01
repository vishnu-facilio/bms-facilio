package com.facilio.util;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.CurrencyContext;
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
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public static void setBaseCurrencyValueForRecord(Map<String, Object> props, List<FacilioField> multiCurrencyFields) throws Exception {
		for (FacilioField multiCurrencyField : multiCurrencyFields) {
			Double currencyValue = props.get(multiCurrencyField.getName()) != null && StringUtils.isNotEmpty(String.valueOf(props.get(multiCurrencyField.getName())))
					? Double.parseDouble(String.valueOf(props.get(multiCurrencyField.getName())))  : null;
			if (currencyValue != null) {
				String baseCurrencyFieldName = MessageFormat.format(BASE_CURRENCY_FIELD_NAME, multiCurrencyField.getName());
				double baseCurrencyValue = CurrencyUtil.getConvertedBaseCurrencyValue(currencyValue, Double.parseDouble(String.valueOf(props.get("exchangeRate"))));
				props.put(baseCurrencyFieldName, baseCurrencyValue);
			}
		}
	}

	public static List<FacilioField> getBaseCurrencyFieldsForModule(FacilioModule module) throws Exception {
		List<FacilioField> multiCurrencyFields = getMultiCurrencyFields(module.getName());
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
		if(CollectionUtils.isEmpty(multiCurrencyFields) || baseCurrency == null || currencyCodeVsCurrency.size() == 1 || StringUtils.isEmpty(currencyCode)){
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
}
