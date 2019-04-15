package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.modules.AggregateOperator.DateAggregateOperator;
import com.facilio.bmsconsole.util.BenchmarkAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.ArrayList;
import java.util.List;

public class CalculateBenchmarkValueCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long id = (long) context.get(FacilioConstants.ContextNames.ID);
		if (id == -1) {
			throw new IllegalArgumentException("Invalid Benchmark ID for calculating value");
		}
		List<BenchmarkUnit> units = (List<BenchmarkUnit>) context.get(FacilioConstants.ContextNames.BENCHMARK_UNITS);
		DateAggregateOperator aggr = (DateAggregateOperator) context.get(FacilioConstants.ContextNames.BENCHMARK_DATE_AGGR);
		Integer dateVal = (Integer) context.get(FacilioConstants.ContextNames.BENCHMARK_DATE_VAL);
		if (dateVal == null) {
			dateVal = 1;
		}
		long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
		long spaceId = (long) context.get(FacilioConstants.ContextNames.SPACE_ID);
		if (spaceId != -1) {
			BaseSpaceContext space = SpaceAPI.getBaseSpace(spaceId);
			double area = -1;
			switch (space.getSpaceTypeEnum()) {
				case SITE:
					SiteContext site = SpaceAPI.getSiteSpace(spaceId);
					area = site.getGrossFloorArea();
					break;
				case BUILDING:
					BuildingContext building = SpaceAPI.getBuildingSpace(spaceId);
					area = building.getGrossFloorArea();
					break;
				default:
					area = space.getArea();
					break;
			}
			if (area != -1) {
				BenchmarkContext benchmark = BenchmarkAPI.getBenchmark(id);
				if (benchmark != null) {
					if (benchmark.getUnits() != null && !benchmark.getUnits().isEmpty()) {
						Unit areaUnit = benchmark.getUnits()
												.stream()
												.filter(u -> u.getMetric() == Metric.AREA)
												.findFirst()
												.orElse(null);
						if (areaUnit != null) {
							BenchmarkUnit unit = new BenchmarkUnit();
							unit.setFromUnit(areaUnit);
							unit.setToUnit(UnitsUtil.getOrgDisplayUnit(AccountUtil.getCurrentOrg().getId(), Metric.AREA));
							unit.setVal(area);
							if (units == null) {
								units = new ArrayList<>();
							}
							units.add(unit);
						}
					}
					context.put(FacilioConstants.ContextNames.BENCHMARK_VALUE, BenchmarkAPI.calculateBenchmarkValue(benchmark, units, startTime, aggr, dateVal));
				}
				return false;
			}
		}
		context.put(FacilioConstants.ContextNames.BENCHMARK_VALUE, BenchmarkAPI.calculateBenchmarkValue(id, units, startTime, aggr, dateVal));
		return false;
	}

}
