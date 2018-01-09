package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

public class ReportCriteriaContext {

	private Long id;
	private Long reportId;
	private Long criteriaId;
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
	public Long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(Long criteriaId) {
		this.criteriaId = criteriaId;
	}
}
