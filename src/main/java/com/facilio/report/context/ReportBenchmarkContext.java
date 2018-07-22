package com.facilio.report.context;

public class ReportBenchmarkContext {

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
	
	private long benchmarkId = -1;
	public long getBenchmarkId() {
		return benchmarkId;
	}
	public void setBenchmarkId(long benchmarkId) {
		this.benchmarkId = benchmarkId;
	}
	
	private long reportDataPointId = -1;
	public long getReportDataPointId() {
		return reportDataPointId;
	}
	public void setReportDataPointId(long reportDataPointId) {
		this.reportDataPointId = reportDataPointId;
	}
}
