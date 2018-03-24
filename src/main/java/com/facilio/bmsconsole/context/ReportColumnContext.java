package com.facilio.bmsconsole.context;

import org.json.simple.JSONArray;

public class ReportColumnContext implements Comparable<ReportColumnContext> {
	
	int width;
	
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}

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
	
	private long entityId = -1;
	public long getEntityId() {
		return entityId;
	}
	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}
	
	private long reportId = -1;
	public long getReportId() {
		return reportId;
	}
	public void setReportId(long reportId) {
		this.reportId = reportId;
	}
	
	private ReportContext report;
	public ReportContext getReport() {
		return report;
	}
	public void setReport(ReportContext report) {
		this.report = report;
	}
	
	private JSONArray data;
	public JSONArray getData() {
		return data;
	}
	public void setData(JSONArray data) {
		this.data = data;
	}

	private long baseLineId = -1;
	public long getBaseLineId() {
		return baseLineId;
	}
	public void setBaseLineId(long baseLineId) {
		this.baseLineId = baseLineId;
	}
	
	private Boolean baseLineAdjust;
	public Boolean getBaseLineAdjust() {
		return baseLineAdjust;
	}
	public void setBaseLineAdjust(Boolean baseLineAdjust) {
		this.baseLineAdjust = baseLineAdjust;
	}
	public boolean isBaseLineAdjust() {
		if (baseLineAdjust != null) {
			return baseLineAdjust.booleanValue();
		}
		return false;
	}

	private Boolean active;
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	public boolean isActive() {
		if (active != null) {
			return active.booleanValue();
		}
		return false;
	}
	
	private int sequence = -1;
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	@Override
	public int compareTo(ReportColumnContext o) {
		if(this.getSequence() > o.getSequence()) {
			return 1;
		}
		return 0;
	}
}
