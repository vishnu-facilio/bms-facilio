package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BenchmarkUnit;
import com.facilio.bmsconsole.context.FormulaContext.DateAggregateOperator;
import com.facilio.bmsconsole.util.BenchmarkAPI;
import com.facilio.constants.FacilioConstants;

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
		long startTime = (long) context.get(FacilioConstants.ContextNames.START_TIME);
		
		context.put(FacilioConstants.ContextNames.BENCHMARK_VALUE, BenchmarkAPI.calculateBenchmarkValue(id, units, startTime, aggr));
		
		return false;
	}

}
