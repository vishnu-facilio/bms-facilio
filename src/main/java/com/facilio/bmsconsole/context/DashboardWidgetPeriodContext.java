package com.facilio.bmsconsole.context;

public class DashboardWidgetPeriodContext {
	
	Long id;
	Long widgetId;
	String periodLabel;
	String periodValue;
	Integer dataOperator;
	public Integer getDataOperator() {
		return dataOperator;
	}
	public void setDataOperator(Integer dataOperator) {
		this.dataOperator = dataOperator;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getWidgetId() {
		return widgetId;
	}
	public void setWidgetId(Long widgetId) {
		this.widgetId = widgetId;
	}
	public String getPeriodLabel() {
		return periodLabel;
	}
	public void setPeriodLabel(String periodLabel) {
		this.periodLabel = periodLabel;
	}
	public String getPeriodValue() {
		return periodValue;
	}
	public void setPeriodValue(String periodValue) {
		this.periodValue = periodValue;
	}
}
