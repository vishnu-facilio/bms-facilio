package com.facilio.report.context;

import com.facilio.bmsconsole.modules.FacilioField;

public class ReportXCriteriaContext {

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
	
	private long reportId = -1;
	public long getReportId() {
		return reportId;
	}
	public void setReportId(long reportId) {
		this.reportId = reportId;
	}
	
	private long moduleId = -1;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private long xFieldId = -1;
	public long getxFieldId() {
		return xFieldId;
	}
	public void setxFieldId(long xFieldId) {
		this.xFieldId = xFieldId;
	}
	
	private String xFieldName;
	public String getxFieldName() {
		return xFieldName;
	}
	public void setxFieldName(String xFieldName) {
		this.xFieldName = xFieldName;
	}
	
	private FacilioField xField;
	public FacilioField getxField() {
		return xField;
	}
	public void setxField(FacilioField xField) {
		this.xField = xField;
	}

	private long criteriaId = -1;
	public long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	
	private long transformWorkflowId = -1;
	public long getTransformWorkflowId() {
		return transformWorkflowId;
	}
	public void setTransformWorkflowId(long transformWorkflowId) {
		this.transformWorkflowId = transformWorkflowId;
	}
}
