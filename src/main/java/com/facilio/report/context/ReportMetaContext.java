package com.facilio.report.context;

public class ReportMetaContext {

	Long id,reportId;
	String chartMetaString,tabularMetaString;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getReportId() {
		return reportId;
	}
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	public String getChartMetaString() {
		return chartMetaString;
	}
	public void setChartMetaString(String chartMetaString) {
		this.chartMetaString = chartMetaString;
	}
	public String getTabularMetaString() {
		return tabularMetaString;
	}
	public void setTabularMetaString(String tabularMetaString) {
		this.tabularMetaString = tabularMetaString;
	}
}
