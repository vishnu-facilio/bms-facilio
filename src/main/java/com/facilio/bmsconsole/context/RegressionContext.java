package com.facilio.bmsconsole.context;

import java.util.List;

public class RegressionContext {
	
	List<RegressionPointContext> xAxis;
	RegressionPointContext yAxis;
	String groupAlias;
	Boolean isMultiple;
	
	
	public Boolean getIsMultiple() {
		return isMultiple;
	}
	public void setIsMultiple(Boolean isMultiple) {
		this.isMultiple = isMultiple;
	}
	
	public List<RegressionPointContext> getxAxis() {
		return xAxis;
	}
	public void setxAxis(List<RegressionPointContext> xAxis) {
		this.xAxis = xAxis;
	}
	public RegressionPointContext getyAxis() {
		return yAxis;
	}
	public void setyAxis(RegressionPointContext yAxis) {
		this.yAxis = yAxis;
	}
	public String getGroupAlias() {
		return groupAlias;
	}
	public void setGroupAlias(String groupAlias) {
		this.groupAlias = groupAlias;
	}
	
}
