package com.facilio.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.CurrencyContext;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class CurrencyUtil {
	public static Logger LOGGER = LogManager.getLogger(CurrencyUtil.class.getName());

	public static CurrencyContext getBaseCurrency() throws Exception {
		Map<String, Object> orgInfo = CommonCommandUtil.getOrgInfo(AccountUtil.getCurrentOrg().getId(), "baseCurrencyCode");
		if(orgInfo != null) {
			return getCurrencyFromCode((String) orgInfo.get("value"));
		}
		return null;
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
		return value * exchangeRate;
	}
}
