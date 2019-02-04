package com.facilio.report.context;

import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;

public class ReportGroupByField extends ReportFieldContext {
	private String alias;
	private AggregateOperator aggr;
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}
	public AggregateOperator getAggrEnum() {
		return aggr;
	}
	public int getAggr() {
		if (aggr != null) {
			return aggr.getValue();
		}
		return -1;
	}
	public void setAggr(AggregateOperator aggr) {
		this.aggr = aggr;
	}
	public void setAggr(int aggr) {
		this.aggr = AggregateOperator.getAggregateOperator(aggr);
	}
}
