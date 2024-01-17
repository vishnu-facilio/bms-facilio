package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsoleV3.context.meter.V3MeterContext;
import com.facilio.bmsconsoleV3.context.meter.V3UtilityTypeContext;
import com.facilio.bmsconsoleV3.context.meter.VirtualMeterTemplateContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.logging.Logger;

public class MetersAPI {
	
	private static Logger logger = Logger.getLogger(MetersAPI.class.getName());

	public static boolean isMetersModule (FacilioModule module) {
		return module.instanceOf(FacilioConstants.Meter.METER);
	}
	
	public static V3UtilityTypeContext getUtilityTypeForMeter(long utilityTypeId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.Meter.UTILITY_TYPE);
		SelectRecordsBuilder<V3UtilityTypeContext> selectBuilder = new SelectRecordsBuilder<V3UtilityTypeContext>()
																		.select(modBean.getAllFields(FacilioConstants.Meter.UTILITY_TYPE))
																		.module(module)
																		.beanClass(V3UtilityTypeContext.class)
																		.skipModuleCriteria()
																		.andCondition(CriteriaAPI.getIdCondition(utilityTypeId, module));
		List<V3UtilityTypeContext> meterList = selectBuilder.get();
		if (meterList != null && !meterList.isEmpty()) {
			return meterList.get(0);
		}
		return null;
	}

	public static List<V3MeterContext> getMeterListOfUtilityType(long utilityType) throws Exception
	{
		return getMeterListOfUtilityType(utilityType,-1);
	}

	public static List<V3MeterContext> getMeterListOfUtilityType(long utilityType, long buildingId) throws Exception {
		List<Long> buildingIds = null;
		if(buildingId > 0) {
			buildingIds = Collections.singletonList(buildingId);
		}
		return getMeterListOfUtilityType(utilityType, buildingIds);
	}

	public static List<V3MeterContext> getMeterListOfUtilityType(long utilityType,List<Long> buildingIds) throws Exception {
		return getMeterListOfUtilityType(utilityType, buildingIds, -1);
	}

	public static List<V3MeterContext> getMeterListOfUtilityType(long utilityType,List<Long> buildingIds, long siteId) throws Exception
	{

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.Meter.METER);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.Meter.METER);
		FacilioField utilityTypeField= FieldFactory.getAsMap(fields).get("utilityType");
		SelectRecordsBuilder<V3MeterContext> selectBuilder = new SelectRecordsBuilder<V3MeterContext>()
				.select(fields)
				.table(module.getTableName())
				.moduleName(module.getName())
				.beanClass(V3MeterContext.class)
				.andCondition(CriteriaAPI.getCondition(utilityTypeField, String.valueOf(utilityType), PickListOperators.IS));

		if(buildingIds != null && !buildingIds.isEmpty()) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("SPACE_ID", "space", StringUtils.join(buildingIds, ","), BuildingOperator.BUILDING_IS));
		}
		if (siteId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition(FieldFactory.getSiteIdField(module), String.valueOf(siteId), PickListOperators.IS));
		}
		List<V3MeterContext> meters = selectBuilder.get();
		return meters;

	}

	public static VirtualMeterTemplateContext getVirtualMeterTemplateForMeter(long vmTemplateId) throws Exception {

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE);
		List<FacilioField> vmTemplateFields = modBean.getAllFields(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(vmTemplateFields);
		SelectRecordsBuilder<VirtualMeterTemplateContext> selectBuilder = new SelectRecordsBuilder<VirtualMeterTemplateContext>()
				.select(modBean.getAllFields(FacilioConstants.Meter.VIRTUAL_METER_TEMPLATE))
				.module(module)
				.beanClass(VirtualMeterTemplateContext.class)
				.skipModuleCriteria()
				.andCondition(CriteriaAPI.getIdCondition(vmTemplateId, module));
		List<VirtualMeterTemplateContext> meterList = selectBuilder.get();
		if (meterList != null && !meterList.isEmpty()) {
			return meterList.get(0);
		}
		return null;
	}

	public static Double calculateMeterConsumption(long meterId, long startTime, long endTime) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		if( meterId > 0L ) {
			//V3MeterContext meter = (V3MeterContext) V3Util.getRecord(FacilioConstants.Meter.METER, meterId, null);
			V3MeterContext meter = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(V3RecordAPI.getRecord(FacilioConstants.Meter.METER,meterId)),V3MeterContext.class);
			V3UtilityTypeContext utilityType = meter.getUtilityType();
			if(utilityType.getName() != null && utilityType.getName().equals("Electricity Meter")) {
				FacilioModule readingModule = modBean.getModule(FacilioConstants.Meter.ELECTRICITY_DATA_READING);

				GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
						.table(readingModule.getTableName())
						.andCustomWhere(readingModule.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId())
						.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(readingModule), String.valueOf(readingModule.getModuleId()), NumberOperators.EQUALS))
						;

				List<FacilioField> fields = new ArrayList<>();
				fields.add(FieldFactory.getField("value", "sum(TOTAL_ENERGY_CONSUMPTION_DELTA)", FieldType.NUMBER));
				builder.select(fields);

				builder.andCondition(CriteriaAPI.getCondition("PARENT_METER_ID", "parentMeterId", String.valueOf(meterId), NumberOperators.EQUALS));
				builder.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime+","+endTime, DateOperators.BETWEEN));

				List<Map<String, Object>> rs = builder.get();
				Double totalConsumption = 0d;
				if (rs != null && rs.size() > 0) {
					Double res1 = (Double) rs.get(0).get("value");
					if(res1 != null) {
						totalConsumption = res1;
					}
				}
				return totalConsumption;
			}
			else if(utilityType.getName() != null && utilityType.getName().equals("Water Meter")) {
				FacilioModule readingModule = modBean.getModule(FacilioConstants.Meter.WATER_DATA_READING);

				GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
						.table(readingModule.getTableName())
						.andCustomWhere(readingModule.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId())
						.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(readingModule), String.valueOf(readingModule.getModuleId()), NumberOperators.EQUALS))
						;

				List<FacilioField> fields = new ArrayList<>();
				fields.add(FieldFactory.getField("value", "sum(WATER_VOLUME_ACCUMULATOR_DELTA)", FieldType.NUMBER));
				builder.select(fields);

				builder.andCondition(CriteriaAPI.getCondition("PARENT_METER_ID", "parentMeterId", String.valueOf(meterId), NumberOperators.EQUALS));
				builder.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime+","+endTime, DateOperators.BETWEEN));

				List<Map<String, Object>> rs = builder.get();
				Double totalConsumption = 0d;
				if (rs != null && rs.size() > 0) {
					Double res1 = (Double) rs.get(0).get("value");
					if(res1 != null) {
						totalConsumption = res1;
					}
				}
				return totalConsumption;
			}
			else if(utilityType.getName() != null && utilityType.getName().equals("Gas Meter")) {
				FacilioModule readingModule = modBean.getModule(FacilioConstants.Meter.GAS_DATA_READING);

				GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
						.table(readingModule.getTableName())
						.andCustomWhere(readingModule.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId())
						.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(readingModule), String.valueOf(readingModule.getModuleId()), NumberOperators.EQUALS))
						;

				List<FacilioField> fields = new ArrayList<>();
				fields.add(FieldFactory.getField("value", "sum(GAS_VOLUME_ACCUMULATOR_DELTA)", FieldType.NUMBER));
				builder.select(fields);

				builder.andCondition(CriteriaAPI.getCondition("PARENT_METER_ID", "parentMeterId", String.valueOf(meterId), NumberOperators.EQUALS));
				builder.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime+","+endTime, DateOperators.BETWEEN));

				List<Map<String, Object>> rs = builder.get();
				Double totalConsumption = 0d;
				if (rs != null && rs.size() > 0) {
					Double res1 = (Double) rs.get(0).get("value");
					if(res1 != null) {
						totalConsumption = res1;
					}
				}
				return totalConsumption;
			}
			else if(utilityType.getName() != null && utilityType.getName().equals("Heat Meter")) {
				FacilioModule readingModule = modBean.getModule(FacilioConstants.Meter.HEAT_DATA_READING);

				GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
						.table(readingModule.getTableName())
						.andCustomWhere(readingModule.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId())
						.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(readingModule), String.valueOf(readingModule.getModuleId()), NumberOperators.EQUALS))
						;

				List<FacilioField> fields = new ArrayList<>();
				fields.add(FieldFactory.getField("value", "sum(THERMAL_ENERGY_ACCUMULATOR_DELTA)", FieldType.NUMBER));
				builder.select(fields);

				builder.andCondition(CriteriaAPI.getCondition("PARENT_METER_ID", "parentMeterId", String.valueOf(meterId), NumberOperators.EQUALS));
				builder.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime+","+endTime, DateOperators.BETWEEN));

				List<Map<String, Object>> rs = builder.get();
				Double totalConsumption = 0d;
				if (rs != null && rs.size() > 0) {
					Double res1 = (Double) rs.get(0).get("value");
					if(res1 != null) {
						totalConsumption = res1;
					}
				}
				return totalConsumption;
			}
			else if(utilityType.getName() != null && utilityType.getName().equals("BTU Meter")) {
				FacilioModule readingModule = modBean.getModule(FacilioConstants.Meter.BTU_DATA_READING);

				GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
						.table(readingModule.getTableName())
						.andCustomWhere(readingModule.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId())
						.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(readingModule), String.valueOf(readingModule.getModuleId()), NumberOperators.EQUALS))
						;

				List<FacilioField> fields = new ArrayList<>();
				fields.add(FieldFactory.getField("value", "sum(CHW_CONSUMPTION_ACCUMULATOR_DELTA)", FieldType.NUMBER));
				builder.select(fields);

				builder.andCondition(CriteriaAPI.getCondition("PARENT_METER_ID", "parentMeterId", String.valueOf(meterId), NumberOperators.EQUALS));
				builder.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime+","+endTime, DateOperators.BETWEEN));

				List<Map<String, Object>> rs = builder.get();
				Double totalConsumption = 0d;
				if (rs != null && rs.size() > 0) {
					Double res1 = (Double) rs.get(0).get("value");
					if(res1 != null) {
						totalConsumption = res1;
					}
				}
				return totalConsumption;
			}
		}
		return 0d;
	}
	public static int updateMeterConnectionStatus(Set<Long> meterIds, boolean isCommissioned) throws Exception{
		V3MeterContext meter = new V3MeterContext();
		meter.setIsCommissioned(isCommissioned);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.METER);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.METER);
		UpdateRecordBuilder<V3MeterContext> updateBuilder = new UpdateRecordBuilder<V3MeterContext>()
				.module(module)
				.fields(fields)
				.andCondition(CriteriaAPI.getIdCondition(meterIds, module));
		return updateBuilder.update(meter);
	}

	public  static V3MeterContext getMeter(Long meterId) throws Exception {
		return getMeter(meterId,Boolean.FALSE);
	}
	public static V3MeterContext getMeter(Long meterId,boolean fetchDeleted) throws Exception {
		List<V3MeterContext> meters = getMeters(Collections.singletonList(meterId),fetchDeleted);
		if (CollectionUtils.isNotEmpty(meters)) {
			return meters.get(0);
		}
		throw new IllegalArgumentException("Invalid meter Id");
	}

	public static List<V3MeterContext> getMeters(List<Long> meterIds,boolean fetchDeleted) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.METER);

		SelectRecordsBuilder<V3MeterContext> selectBuilder = new SelectRecordsBuilder<V3MeterContext>()
				.moduleName(module.getName())
				.beanClass(V3MeterContext.class)
				.select(modBean.getAllFields(module.getName()))
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(meterIds, module));

		if(fetchDeleted){
			selectBuilder.fetchDeleted();
		}

		List<V3MeterContext> meters = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(meters)) {
			return meters;
		}
		return new ArrayList<>();
	}
		
	public static Double fetchMeterAggregatedReading(long meterId, long startTime, long endTime, String aggrFieldName, FacilioModule readingModule, AggregateOperator aggr) throws Exception {

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(readingModule.getTableName())
				.select(new HashSet<>())
				.andCustomWhere(readingModule.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(readingModule), String.valueOf(readingModule.getModuleId()), NumberOperators.EQUALS))
				;

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> readingModuleFields = modBean.getAllFields(readingModule.getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(readingModuleFields);
		builder.aggregate(aggr, fieldMap.get(aggrFieldName));

		builder.andCondition(CriteriaAPI.getCondition("PARENT_METER_ID", "parentMeterId", String.valueOf(meterId), NumberOperators.EQUALS));
		builder.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime+","+endTime, DateOperators.BETWEEN));

		List<Map<String, Object>> rs = builder.get();
		Double totalConsumption = 0d;
		if (rs != null && rs.size() > 0) {
			Double res1 = (Double) rs.get(0).get(aggrFieldName);
			if(res1 != null) {
				totalConsumption = res1;
			}
		}
		return totalConsumption;
	}

	public static Map<String, Object> fetchMeterAggregatedReadingWithTime(long meterId, long startTime, long endTime, String aggrFieldName, FacilioModule readingModule, AggregateOperator aggr) throws Exception {

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(readingModule.getTableName())
				.andCustomWhere(readingModule.getTableName()+".ORGID = "+ AccountUtil.getCurrentOrg().getOrgId())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getModuleIdField(readingModule), String.valueOf(readingModule.getModuleId()), NumberOperators.EQUALS))
				;

		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> readingModuleFields = modBean.getAllFields(readingModule.getName());
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(readingModuleFields);

		FacilioField readingField = fieldMap.get(aggrFieldName);
		FacilioField ttimeField = fieldMap.get("ttime");

		List<FacilioField> fields = new ArrayList<>();
		fields.add(readingField);
		fields.add(ttimeField);

		builder.select(fields);
		builder.andCondition(CriteriaAPI.getCondition("PARENT_METER_ID", "parentMeterId", String.valueOf(meterId), NumberOperators.EQUALS));
		builder.andCondition(CriteriaAPI.getCondition("TTIME", "ttime", startTime+","+endTime, DateOperators.BETWEEN));
		builder.orderBy(readingField.getCompleteColumnName() + " DESC");
		builder.limit(1);

		List<Map<String, Object>> rs = builder.get();

		Map<String, Object> resultMap = new HashMap<>();
		if (rs != null && rs.size() > 0) {
			Double reading = (Double) rs.get(0).get(aggrFieldName);
			long ttime = (long) rs.get(0).get("ttime");
			resultMap.put("reading", reading);
			resultMap.put("ttime", ttime);
		}
		return resultMap;
	}

}

