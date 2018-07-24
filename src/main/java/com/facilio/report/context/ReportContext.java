package com.facilio.report.context;

import java.util.List;

public class ReportContext {

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
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private long reportFolderId = -1;
	public long getReportFolderId() {
		return reportFolderId;
	}
	public void setReportFolderId(long reportFolderId) {
		this.reportFolderId = reportFolderId;
	}
	
	private long workflowId = -1;
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}
	
	private String tabularState;
	public String getTabularState() {
		return tabularState;
	}
	public void setTabularState(String tabularState) {
		this.tabularState = tabularState;
	}
	
	private String chartState;
	public String getChartState() {
		return chartState;
	}
	public void setChartState(String chartState) {
		this.chartState = chartState;
	}
	
	private List<ReportDataPointContext> dataPoints;
	public List<ReportDataPointContext> getDataPoints() {
		return dataPoints;
	}
	public void setDataPoints(List<ReportDataPointContext> dataPoints) {
		this.dataPoints = dataPoints;
	}
	
	private ReportXCriteriaContext xCriteria;
	public ReportXCriteriaContext getxCriteria() {
		return xCriteria;
	}
	public void setxCriteria(ReportXCriteriaContext xCriteria) {
		this.xCriteria = xCriteria;
	}
}
