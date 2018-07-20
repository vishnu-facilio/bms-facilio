package com.facilio.report.context;

public class ReportBaselineContext {

	Long baseLineId,reportDataPointId;
	int adjustType;
	public Long getBaseLineId() {
		return baseLineId;
	}
	public void setBaseLineId(Long baseLineId) {
		this.baseLineId = baseLineId;
	}
	public Long getReportDataPointId() {
		return reportDataPointId;
	}
	public void setReportDataPointId(Long reportDataPointId) {
		this.reportDataPointId = reportDataPointId;
	}
	public int getAdjustType() {
		return adjustType;
	}
	public void setAdjustType(int adjustType) {
		this.adjustType = adjustType;
	}
}
