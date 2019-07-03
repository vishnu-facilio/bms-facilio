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
	
	private DataConditions errorStateENUM;
	
	private Integer errorState;
	
	public DataConditions getErrorStateENUM() {
		return errorStateENUM;
	}
	public void setErrorStateENUM(DataConditions errorStateENUM) {
		this.errorStateENUM = errorStateENUM;
	}
	public Integer getErrorState() {
		return errorState;
	}
	
	public void setErrorState(Integer errorState) {
		this.errorState = errorState;
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
	
	public enum DataConditions{
		DATA_AUTHENTICATED,
		NOT_ENOUGH_DATA,
		SINGULAR_MATRIX
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
	}
	
}
