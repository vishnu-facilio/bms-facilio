package com.facilio.report.context;

import java.util.HashMap;
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
	
	private Map<String, ReportBaseLineContext> baseLineMap;
	public Map<String, ReportBaseLineContext> getBaseLineMap() {
		return baseLineMap;
	}
	public void setBaseLineMap(Map<String, ReportBaseLineContext> baseLineMap) {
		this.baseLineMap = baseLineMap;
	}
	public void addBaseLine(String name, ReportBaseLineContext baseLine) {
		if (baseLineMap == null) {
			baseLineMap = new HashMap<>();
		}
		baseLineMap.put(name, baseLine);
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return new StringBuilder()
					.append("ReportData [")
					.append("dataPoints : (").append(dataPoints).append("), ")
					.append("props : (").append(props).append("), ")
					.append("baseLineMap : (").append(baseLineMap).append(")")
					.toString();
	}
}
