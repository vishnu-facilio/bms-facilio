package com.facilio.report.context;

import com.facilio.modules.BaseLineContext;
import com.facilio.modules.BaseLineContext.AdjustType;
import com.facilio.db.criteria.DateRange;

public class ReportBaseLineContext {

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
	
	private BaseLineContext baseLine;
	public BaseLineContext getBaseLine() {
		return baseLine;
	}
	public void setBaseLine(BaseLineContext baseLine) {
		this.baseLine = baseLine;
	}

	private long reportId = -1;
	public long getReportId() {
		return reportId;
	}
	public void setReportId(long reportId) {
		this.reportId = reportId;
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
	
	private DateRange baseLineRange;
	public DateRange getBaseLineRange() {
		return baseLineRange;
	}
	public void setBaseLineRange(DateRange baseLineRange) {
		this.baseLineRange = baseLineRange;
	}

	private long diff = -1;
	public long getDiff() {
		return diff;
	}
	public void setDiff(long diff) {
		this.diff = diff;
	}
}
