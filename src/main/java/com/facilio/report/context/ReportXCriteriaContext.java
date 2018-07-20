package com.facilio.report.context;

public class ReportXCriteriaContext {

	Long id,moduleId,reportId,xFieldId,criteriaId,transformWorkflowId;
	String moduleName,xFieldName;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getModuleId() {
		return moduleId;
	}
	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}
	public Long getReportId() {
		return reportId;
	}
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	public Long getxFieldId() {
		return xFieldId;
	}
	public void setxFieldId(Long xFieldId) {
		this.xFieldId = xFieldId;
	}
	public Long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(Long criteriaId) {
		this.criteriaId = criteriaId;
	}
	public Long getTransformWorkflowId() {
		return transformWorkflowId;
	}
	public void setTransformWorkflowId(Long transformWorkflowId) {
		this.transformWorkflowId = transformWorkflowId;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getxFieldName() {
		return xFieldName;
	}
	public void setxFieldName(String xFieldName) {
		this.xFieldName = xFieldName;
	}
}
