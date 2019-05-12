package com.facilio.bmsconsole.util;

import com.facilio.bmsconsole.context.BenchmarkContext;
import com.facilio.bmsconsole.context.BenchmarkUnit;
import com.facilio.modules.*;
import com.facilio.modules.AggregateOperator.DateAggregateOperator;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.*;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;

import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
			return calculateBenchmarkValue(benchmark, units, startTime, dateAggregation);
		}
		return -1;
	}
	
	public static double calculateBenchmarkValue(long id, List<BenchmarkUnit> units, long startTime, DateAggregateOperator dateAggregation, int dateVal) throws Exception {
		BenchmarkContext benchmark = getBenchmark(id);
		if (benchmark != null) {
			return calculateBenchmarkValue(benchmark, units, startTime, dateAggregation, dateVal);
		}
		return -1;
	}
	
	public static double calculateBenchmarkValue(BenchmarkContext benchmark, List<BenchmarkUnit> units, long startTime, DateAggregateOperator dateAggregation) throws Exception {
		return calculateBenchmarkValue(benchmark, units, startTime, dateAggregation, 1);
	}
	
	public static double calculateBenchmarkValue(BenchmarkContext benchmark, List<BenchmarkUnit> units, long startTime, DateAggregateOperator dateAggregation, int dateVal) throws Exception {
		double val = benchmark.getValue();
		if (benchmark.getDurationEnum() != null && dateAggregation != null && startTime != -1) {
			val = getDayNormalizedValues(val, benchmark.getDurationEnum(), dateAggregation, startTime, dateVal);
		}
		if (units != null && !units.isEmpty()) {
			Map<Unit, BenchmarkUnit> unitMap = units.stream()
													.collect(Collectors.toMap(
															BenchmarkUnit::getFromUnitEnum, 
															Function.identity(),
															(prevValue, curValue) -> {
																return curValue;
															}
															));
			for (Unit unit : benchmark.getUnits()) {
				BenchmarkUnit bUnit = unitMap.get(unit);
				if (bUnit != null) {
					double convertedValue = UnitsUtil.convert(1, bUnit.getFromUnitEnum(), bUnit.getToUnitEnum()); 
					val = val / convertedValue;
					val = val * bUnit.getVal();
				}
			}
		}
		return val;
	}
	
	private static double getDayNormalizedValues (double value, FacilioFrequency frequency, DateAggregateOperator aggr, long startTime, int dateVal) {
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
			case FULLDATE:
			case DAYSOFMONTH:
			case WEEKDAY:
				if (frequency != FacilioFrequency.DAILY) {
					return perDayVal * dateVal;
				}
			case WEEK:
			case WEEKANDYEAR:
				if (frequency != FacilioFrequency.WEEKLY) {
					return perDayVal * 7 * dateVal;
				}
			case MONTHANDYEAR:
			case MONTH:
				if (frequency != FacilioFrequency.MONTHLY) {
					return perDayVal * (DateTimeUtil.getZonedDateTime(startTime).with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth()) * dateVal;
				}
			case YEAR:
				if (frequency != FacilioFrequency.ANNUALLY) {
					return perDayVal * (DateTimeUtil.getZonedDateTime(startTime).with(TemporalAdjusters.lastDayOfYear()).getDayOfYear()) * dateVal;
				}
			default:
				break;
		}
		return value * dateVal;
	}
}
