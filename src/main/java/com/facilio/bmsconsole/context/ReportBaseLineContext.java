package com.facilio.bmsconsole.context;

public class ReportBaseLineContext {

	Long reportId;
	Long baseLineId;
	BaseLineContext baseLineContext;
	boolean isAdjust;
	
	public boolean isAdjust() {
		return isAdjust;
	}
	public void setAdjust(boolean isAdjust) {
		this.isAdjust = isAdjust;
	}
	public Long getReportId() {
		return reportId;
	}
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	public Long getBaseLineId() {
		return baseLineId;
	}
	public void setBaseLineId(Long baseLineId) {
		this.baseLineId = baseLineId;
	}
	public BaseLineContext getBaseLineContext() {
		return baseLineContext;
	}
	public void setBaseLineContext(BaseLineContext baseLineContext) {
		this.baseLineContext = baseLineContext;
	}
}
