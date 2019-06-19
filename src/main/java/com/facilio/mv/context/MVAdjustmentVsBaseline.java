package com.facilio.mv.context;

public class MVAdjustmentVsBaseline {
	
	long id;
	long orgId;
	long adjustmentId;
	long baselineId;
	long projectId;
	
	String adjustmentName;
	String baselineName;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getAdjustmentId() {
		return adjustmentId;
	}
	public void setAdjustmentId(long adjustmentId) {
		this.adjustmentId = adjustmentId;
	}
	public long getBaselineId() {
		return baselineId;
	}
	public void setBaselineId(long baselineId) {
		this.baselineId = baselineId;
	}
	public String getAdjustmentName() {
		return adjustmentName;
	}
	public void setAdjustmentName(String adjustmentName) {
		this.adjustmentName = adjustmentName;
	}
	public String getBaselineName() {
		return baselineName;
	}
	public void setBaselineName(String baselineName) {
		this.baselineName = baselineName;
	}
	public long getProjectId() {
		return projectId;
	}
	public void setProjectId(long projectId) {
		this.projectId = projectId;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
}
