package com.facilio.energystar.util;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.energystar.context.EnergyStarCustomerContext;
import com.facilio.energystar.context.EnergyStarMeterContext;
import com.facilio.energystar.context.EnergyStarMeterPointContext;
import com.facilio.energystar.context.EnergyStarPropertyContext;
import com.facilio.energystar.context.EnergyStarPropertyMetricsContext;
import com.facilio.energystar.context.EnergyStarPropertyUseContext;
import com.facilio.energystar.context.Meter_Category;
import com.facilio.energystar.context.Property_Metrics;
import com.facilio.energystar.context.EnergyStarPropertyContext.Building_Type;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;

public class EnergyStarUtil {
	
	private static final Logger LOGGER = Logger.getLogger(EnergyStarUtil.class.getName());
	
	public static final String ENERGY_STAR_CUSTOMER_CONTEXT = "energyStarCustomerContext";
	
	public static final String IS_CREATE_ACCOUNT = "isCreateAccount";
	
	public static final String ENERGY_STAR_PROPERTY_CONTEXT = "energyStarPropertyContext";
	
	public static final String ENERGY_STAR_PROPERTIES_CONTEXT = "energyStarPropertiesContext";
	
	public static final String ENERGY_STAR_METER_DATA_CONTEXTS = "meterDatas";
	
	public static final String ENERGY_STAR_PROPERTY_DATA_CONTEXTS = "propertyDatas";
	
	public static final String ENERGY_STAR_METER_DATA_CONTEXT = "meterData";
	
	public static final String ENERGY_STAR_PROPERTY_USE_CONTEXTS = "energyStarPropertyUseContexts";
	
	public static final String ENERGY_STAR_PROPERTY_USE_CONTEXT = "energyStarPropertyUseContext";
	
	public static final String ENERGY_STAR_METER_CONTEXTS = "energyStarMeterContexts";
	
	public static final String ENERGY_STAR_METER_CONTEXT = "energyStarMeterContext";
	
	public static final String ENERGY_STAR_METER_ID = "energyStarMeterID";
	
	public static final String ENERGY_STAR_PUSH_METER_DATA = "energyStarPushMeterData";
	
	public static final String ENERGY_STAR_PROPERTY_ID = "energyStarPropertyID";
	
	public static final String ENERGY_STAR_METER_DATA_MODULE_NAME = "energyStarMeterData";
	
	public static final String ENERGY_STAR_PROPERTY_DATA_MODULE_NAME = "energyStarPropertyData";

	public static final String ENERGY_STAR_METER_VS_ID_MAP = "energyStarMeterVsIDMap";

	public static final String ENERGY_STAR_METER_VS_METER_DATA_MAP = "energyStarMeterVsMeterDataMap";
	
	public static final String ENERGY_STAR_FETCH_TIME_LIST = "energyStarFetchTimeList";

	public static final String ENERGY_STAR_PROPERTY_MODULE_NAME = "energyStarProperty";
	public static final String ENERGY_STAR_PROPERTY_METRICS_MODULE_NAME = "energyStarPropertyMetrics";
	
	public static final String FIRST_DATA_RECIEVIED_TIME = "firstDataRecievedTime";
	
	public static final int ENERGY_STAR_DATA_PUSHING_DATE = 10;
	
	
	public static EnergyStarCustomerContext getEnergyStarCustomer() throws Exception {
		List<FacilioField> fields = FieldFactory.getEnergyStarCustomerFields();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getEnergyStarCustomerModule().getTableName())
				.andCondition(CriteriaAPI.getOrgIdCondition(AccountUtil.getCurrentOrg().getId(), ModuleFactory.getEnergyStarCustomerModule()));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		if (props != null && !props.isEmpty()) {
			EnergyStarCustomerContext energyStarCustomerContext = FieldUtil.getAsBeanFromMap(props.get(0), EnergyStarCustomerContext.class);
			return energyStarCustomerContext;
		}
		return null;
	}
	
	public static Map<Integer,EnergyStarMeterPointContext> getEnergyStarMeterPointMap(long meterId) throws Exception {
		List<FacilioField> fields = FieldFactory.getEnergyStarMeterPointFields();
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getEnergyStarMeterPointModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("meterId"), meterId+"", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		Map<Integer,EnergyStarMeterPointContext> pointMap = new HashMap<Integer, EnergyStarMeterPointContext>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				
				EnergyStarMeterPointContext point = FieldUtil.getAsBeanFromMap(prop, EnergyStarMeterPointContext.class);
				pointMap.put(point.getPointId(), point);
			}
		}
		return pointMap;
	}
	
	private static String getUserNameFromOrgName(String orgName) {
		orgName =  orgName.replace(" ", "");
		return orgName;
	}
	private static String getPasswordFromUserName(String userName) {
		userName =  userName + "Facilio@123";
		return userName;
	}
	
	public static EnergyStarCustomerContext getEnergyStarCustomerContext(boolean createAccount) {
		
		EnergyStarCustomerContext customerContext = new EnergyStarCustomerContext();
		
		Organization org = AccountUtil.getCurrentOrg();
		
		customerContext.setOrgId(org.getId());
		customerContext.setCreatedTime(DateTimeUtil.getCurrenTime());
		customerContext.setCreatedBy(AccountUtil.getCurrentUser().getId());
		
		if(createAccount) {
			customerContext.setUserName(getUserNameFromOrgName(org.getName()));
			customerContext.setPassword(getPasswordFromUserName(customerContext.getUserName()));
			
			List<Meter_Category> exchangeModeList = customerContext.getAvailableDataExchangeModes() == null ? new ArrayList<>() : customerContext.getAvailableDataExchangeModes();
			
			exchangeModeList.add(Meter_Category.ELECTRIC);
			
			customerContext.setAvailableDataExchangeModes(exchangeModeList);
			
			long total = 0;
			if(customerContext.getAvailableDataExchangeModes() != null) {
				for(Meter_Category dataExchangeMode :customerContext.getAvailableDataExchangeModes()) {
					total = total | dataExchangeMode.getLicence();
				}
			}
			
			customerContext.setDataExchangeMode(total);
		}
		else {
			String shareKeyString = getUserNameFromOrgName(org.getName()) + DateTimeUtil.getCurrenTime();
			
			String shareKey = Base64.getEncoder().encodeToString(shareKeyString.getBytes());
			
			LOGGER.info("shareKey --- "+shareKey);
			
			customerContext.setShareKey(shareKey.substring(0, shareKey.length() >= 20 ? 20 : shareKey.length()));
			customerContext.setShareStatus(EnergyStarCustomerContext.Share_Status.CREATED.getIntVal());
		}
		
		return customerContext;
	}
	
	public static EnergyStarCustomerContext addEnergyStarCustomer(EnergyStarCustomerContext customerContext) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getEnergyStarCustomerModule().getTableName())
				.fields(FieldFactory.getEnergyStarCustomerFields());

		Map<String, Object> props = FieldUtil.getAsProperties(customerContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		customerContext.setId((Long) props.get("id"));
		return customerContext;
	}
	
	public static EnergyStarPropertyContext addEnergyStarProperty(EnergyStarPropertyContext energyStarPropertyContext) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getEnergyStarPropertyModule().getTableName())
				.fields(FieldFactory.getEnergyStarPropertyFields());

		Map<String, Object> props = FieldUtil.getAsProperties(energyStarPropertyContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		energyStarPropertyContext.setId((Long) props.get("id"));
		return energyStarPropertyContext;
	}
	
	public static EnergyStarPropertyUseContext addEnergyStarPropertyUse(EnergyStarPropertyUseContext energyStarProperyUseContext) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getEnergyStarPropertyUseModule().getTableName())
				.fields(FieldFactory.getEnergyStarPropertyUseFields());

		Map<String, Object> props = FieldUtil.getAsProperties(energyStarProperyUseContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		energyStarProperyUseContext.setId((Long) props.get("id"));
		return energyStarProperyUseContext;
	}
	
	public static EnergyStarMeterContext addEnergyStarMetercontext(EnergyStarMeterContext energyStarMeterContext) throws Exception {
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(ModuleFactory.getEnergyStarMeterModule().getTableName())
				.fields(FieldFactory.getEnergyStarMeterFields());

		Map<String, Object> props = FieldUtil.getAsProperties(energyStarMeterContext);
		insertBuilder.addRecord(props);
		insertBuilder.save();

		energyStarMeterContext.setId((Long) props.get("id"));
		return energyStarMeterContext;
	}
	
	public static int updateEnergyStarRelModule(FacilioModule module,List<FacilioField> fields,Object context,Criteria updateCriteria) throws Exception {
		
		GenericUpdateRecordBuilder updatetBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(fields)
				.andCriteria(updateCriteria);

		Map<String, Object> props = FieldUtil.getAsProperties(context);
		return updatetBuilder.update(props);

	}
	
	public static int deleteEnergyStarRelated(FacilioModule module,Criteria deleteCriteria) throws Exception {
		
		GenericDeleteRecordBuilder deleteBuilder = new GenericDeleteRecordBuilder()
				.table(module.getTableName())
				.andCriteria(deleteCriteria);

		return deleteBuilder.delete();
	}
	
	public static List<Map<String, Object>> fetchEnergyStarRelated(FacilioModule module,List<FacilioField> fields,Criteria fetchCriteria,Condition condition) throws Exception {
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				;
		
		if(fetchCriteria != null) {
			selectBuilder.andCriteria(fetchCriteria);
		}
		if(condition != null) {
			selectBuilder.andCondition(condition);
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		return props;
	}
	
	public static Map<String,Object> fillEnergyStarCardData(EnergyStarPropertyContext property,List<Property_Metrics> metrics, DateRange dateRange) throws Exception {
		
		Map<String,Object> values = new HashMap<String, Object>();
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioModule propertyData = modBean.getModule(EnergyStarUtil.ENERGY_STAR_PROPERTY_DATA_MODULE_NAME);
		
		List<FacilioField> fields = modBean.getAllFields(EnergyStarUtil.ENERGY_STAR_PROPERTY_DATA_MODULE_NAME);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(propertyData.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), property.getId()+"",NumberOperators.EQUALS));
		
		if(dateRange == null) {
			selectBuilder.orderBy("TTIME Desc")
						.limit(1);
		}
		else {
			selectBuilder.andCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), dateRange.toString(),DateOperators.BETWEEN));
		}
				
				;
		List<Map<String, Object>> lastMonthProps = selectBuilder.get();
		
		List<Map<String, Object>> baselineProps = null;
		if(property.getBaselineMonth() > 0) {
			
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("parentId"), property.getId()+"",NumberOperators.EQUALS));
			criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("ttime"), property.getBaselineMonth()+"",NumberOperators.EQUALS));
			
			baselineProps = EnergyStarUtil.fetchEnergyStarRelated(propertyData, fields, criteria, null);
			
		}
		
		for(Property_Metrics metric : metrics) {
			
			Map<String,Object> dataValues = new HashMap<>();
			
			
			if(lastMonthProps != null && !lastMonthProps.isEmpty() && lastMonthProps.get(0).get(metric.getName()) != null) {
				dataValues.put("current", lastMonthProps.get(0).get(metric.getName()));
			}
			if(baselineProps != null && !baselineProps.isEmpty() && baselineProps.get(0).get(metric.getName()) != null) {
				dataValues.put("baseline", baselineProps.get(0).get(metric.getName()));
			}
			
			if(metric.getTargetFieldName() != null && lastMonthProps.get(0).get(metric.getTargetFieldName()) != null) {
				dataValues.put("target",  lastMonthProps.get(0).get(metric.getTargetFieldName()));
			}
			if(metric.getMedianFieldName() != null && lastMonthProps.get(0).get(metric.getMedianFieldName()) != null) {
				dataValues.put("median", lastMonthProps.get(0).get(metric.getMedianFieldName()));
			}
			
			if(!dataValues.isEmpty()) {
				if(metric == Property_Metrics.SCORE) {
					dataValues.put("maxValue", 100);
				}
				else {
					calculateMax(dataValues);
				}
				values.put(metric.getName(), dataValues);
			}
		}
		
		return values;
	}
	
	private static void calculateMax(Map<String, Object> values) {
		
		double max = 0;
		for(String metric :values.keySet()) {
			double val = Double.parseDouble(values.get(metric).toString());
			if(val > max) {
				max = val;
			}
		}
		double maxValue = max;
		
		values.put("max", max);
		values.put("maxValue", maxValue);
	}
}
