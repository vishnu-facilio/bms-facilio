package com.facilio.report.context;

import com.facilio.modules.AggregateOperator;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
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

	public String getDynamicKpi() {
		return dynamicKpi;
	}

	public void setDynamicKpi(String dynamicKpi) {
		this.dynamicKpi = dynamicKpi;
	}

	public String dynamicKpi;
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getField()+"";
	}
	private String kpiLinkName;
}
