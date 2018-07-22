package com.facilio.report.context;

import com.facilio.bmsconsole.context.BaseLineContext.AdjustType;

public class ReportBaselineContext {

	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long siteId = -1;
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	private long baseLineId = -1;
	public long getBaseLineId() {
		return baseLineId;
	}
	public void setBaseLineId(long baseLineId) {
		this.baseLineId = baseLineId;
	}
	
	private long reportDataPointId = -1;
	public long getReportDataPointId() {
		return reportDataPointId;
	}
	public void setReportDataPointId(long reportDataPointId) {
		this.reportDataPointId = reportDataPointId;
	}
	
	private AdjustType adjustType;
	public AdjustType getAdjustTypeEnum() {
		return adjustType;
	}
	public void setAdjustType(AdjustType adjustType) {
		this.adjustType = adjustType;
	}
	public int getAdjustType() {
		if (adjustType != null) {
			return adjustType.getValue();
		}
		return -1;
	}
	public void setAdjustType(int adjustType) {
		this.adjustType = AdjustType.valueOf(adjustType);
	}
	
}
