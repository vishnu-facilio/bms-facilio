package com.facilio.bmsconsole.context;

import java.util.List;

public class RegressionContext {
	
	List<RegressionPointContext> xAxisContext;
	RegressionPointContext yAxisContext;
	String groupAlias;
	
	public List<RegressionPointContext> getxAxisContext() {
		return xAxisContext;
	}
	public void setxAxisContext(List<RegressionPointContext> xAxisContext) {
		this.xAxisContext = xAxisContext;
	}
	public RegressionPointContext getyAxisContext() {
		return yAxisContext;
	}
	public void setyAxisContext(RegressionPointContext yAxisContext) {
		this.yAxisContext = yAxisContext;
	}
	public String getGroupAlias() {
		return groupAlias;
	}
	public void setGroupAlias(String groupAlias) {
		this.groupAlias = groupAlias;
	}
	
}
