package com.facilio.report.context;

import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;

public class ReportYAxisContext extends ReportFieldContext {
	private AggregateOperator aggr;
	public AggregateOperator getAggrEnum() {
		return aggr;
	}
	public void setAggr(AggregateOperator aggr) {
		this.aggr = aggr;
	}
	public int getAggr() {
		if (aggr != null) {
			return aggr.getValue();
		}
		return -1;
	}
	public void setAggr(int aggr) {
		this.aggr = AggregateOperator.getAggregateOperator(aggr);
	}
	
	private boolean fetchMinMax = false;
	public boolean isFetchMinMax() {
		return fetchMinMax;
	}
	public void setFetchMinMax(boolean fetchMinMax) {
		this.fetchMinMax = fetchMinMax;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getField()+"";
	}
}
