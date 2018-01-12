package com.facilio.bmsconsole.context;

public class ReportUserFilterContext {
	
	Long id;
	Long reportId;
	Long reportFieldId;
	ReportFieldContext reportFieldContext;
	public ReportFieldContext getReportFieldContext() {
		return reportFieldContext;
	}
	public void setReportFieldContext(ReportFieldContext reportFieldContext) {
		this.reportFieldContext = reportFieldContext;
	}
	String whereClause;
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
	public Long getReportFieldId() {
		return reportFieldId;
	}
	public void setReportFieldId(Long reportFieldId) {
		this.reportFieldId = reportFieldId;
	}
	public String getWhereClause() {
		return whereClause;
	}
	public void setWhereClause(String whereClause) {
		this.whereClause = whereClause;
	}
	
}
