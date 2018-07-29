package com.facilio.report.context;

import java.util.List;
import java.util.Map;

public class ReportDataContext {
	private List<ReportDataPointContext> dataPoints;
	public List<ReportDataPointContext> getDataPoints() {
		return dataPoints;
	}
	public void setDataPoints(List<ReportDataPointContext> dataPoints) {
		this.dataPoints = dataPoints;
	}
	
	private Map<String, List<Map<String, Object>>> props;
	public Map<String, List<Map<String, Object>>> getProps() {
		return props;
	}
	public void setProps(Map<String, List<Map<String, Object>>> props) {
		this.props = props;
	}
}
