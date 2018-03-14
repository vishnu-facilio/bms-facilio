package com.facilio.bmsconsole.context;

public class ReportEnergyMeterContext {
	
	private Long id;
	private Long reportId;
	private Long serviceId;
	private Long subMeterId;
	private String groupBy;
	
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
	public Long getServiceId() {
		return serviceId;
	}
	public void setServiceId(Long serviceId) {
		this.serviceId = serviceId;
	}
	public Long getSubMeterId() {
		return subMeterId;
	}
	public void setSubMeterId(Long subMeterId) {
		this.subMeterId = subMeterId;
	}
	public String getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}
}
