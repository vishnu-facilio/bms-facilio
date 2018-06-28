package com.facilio.bmsconsole.util;

import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.BenchmarkContext;
import com.facilio.bmsconsole.context.BenchmarkUnit;
import com.facilio.bmsconsole.context.FormulaContext.DateAggregateOperator;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;

public class BenchmarkAPI {
	public static BenchmarkContext getBenchmark(long id) throws Exception {
		FacilioModule module = ModuleFactory.getBenchmarkModule();
		FacilioModule unitModule = ModuleFactory.getBenchmarkUnitModule();
		
		List<FacilioField> fields = FieldFactory.getBenchmarkFields();
		fields.addAll(FieldFactory.getBenchmarkUnitFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.leftJoin(unitModule.getTableName())
														.on(module.getTableName()+".ID = "+unitModule.getTableName()+".BENCHMARK_ID")
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getIdCondition(id, module))
														;
		List<BenchmarkContext> benchmarks = getBenchmarksFromProps(selectBuilder.get());
		if (benchmarks != null && !benchmarks.isEmpty()) {
			return benchmarks.get(0);
		}
		return null;
	}
	
	public static List<BenchmarkContext> getAllBenchmarks() throws Exception {
		FacilioModule module = ModuleFactory.getBenchmarkModule();
		FacilioModule unitModule = ModuleFactory.getBenchmarkUnitModule();
		
		List<FacilioField> fields = FieldFactory.getBenchmarkFields();
		fields.addAll(FieldFactory.getBenchmarkUnitFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.table(module.getTableName())
														.leftJoin(module.getTableName())
														.on(module.getTableName()+".ID = "+unitModule+".BENCHMARK_ID")
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														;
		return getBenchmarksFromProps(selectBuilder.get());
	}
	
	private static List<BenchmarkContext> getBenchmarksFromProps(List<Map<String, Object>> props) {
		if (props != null && !props.isEmpty()) {
			Map<Long, BenchmarkContext> benchmarkMap = new HashMap<>();
			for (Map<String, Object> prop : props) {
				Long id = (Long) prop.get("id");
				BenchmarkContext benchmark = benchmarkMap.get(id);
				if (benchmark == null) {
					benchmark = FieldUtil.getAsBeanFromMap(prop, BenchmarkContext.class);
					benchmarkMap.put(id, benchmark);
				}
				benchmark.addUnit(Unit.valueOf((int) prop.get("unit")));
			}
			return new ArrayList<>(benchmarkMap.values());
		}
		return null;
	}
	
	public static double calculateBenchmarkValue(long id, List<BenchmarkUnit> units, long startTime, DateAggregateOperator dateAggregation) throws Exception {
		BenchmarkContext benchmark = getBenchmark(id);
		if (benchmark != null) {
			double val = benchmark.getValue();
			if (benchmark.getDurationEnum() != null && dateAggregation != null && startTime != -1) {
				val = getDayNormalizedValues(val, benchmark.getDurationEnum(), dateAggregation, startTime);
			}
			if (units != null && !units.isEmpty()) {
				for (BenchmarkUnit unit : units) {
					if (benchmark.getUnits().contains(unit.getFromUnitEnum())) {
						val = UnitsUtil.convert(val, unit.getFromUnitEnum(), unit.getToUnitEnum());
						val = val * unit.getVal();
					}
				}
			}
			return val;
		}
		return -1;
	}
	
	private static double getDayNormalizedValues (double value, FacilioFrequency frequency, DateAggregateOperator aggr, long startTime) {
		double perDayVal = value;
		switch (frequency) {
			case DAILY:
				break;
			case WEEKLY:
				perDayVal = perDayVal / 7;
				break;
			case MONTHLY:
				perDayVal = perDayVal / (DateTimeUtil.getZonedDateTime(startTime).with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth());
				break;
			case ANNUALLY:
				perDayVal = perDayVal / (DateTimeUtil.getZonedDateTime(startTime).with(TemporalAdjusters.lastDayOfYear()).getDayOfYear());
				break;
			default:
				throw new IllegalArgumentException("Benchmark cannot have other frequencies. Shouldn't be here!!");
		}
		
		switch (aggr) {
			case DATEANDTIME:
			case DAYSOFMONTH:
			case WEEKDAY:
				if (frequency != FacilioFrequency.DAILY) {
					return perDayVal;
				}
			case WEEK:
			case WEEKANDYEAR:
				if (frequency != FacilioFrequency.WEEKLY) {
					return perDayVal * 7;
				}
			case MONTHANDYEAR:
			case MONTH:
				if (frequency != FacilioFrequency.MONTHLY) {
					return perDayVal * (DateTimeUtil.getZonedDateTime(startTime).with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth());
				}
			case YEAR:
				if (frequency != FacilioFrequency.ANNUALLY) {
					return perDayVal * (DateTimeUtil.getZonedDateTime(startTime).with(TemporalAdjusters.lastDayOfYear()).getDayOfYear());
				}
			default:
				break;
		}
		return value;
	}
}
