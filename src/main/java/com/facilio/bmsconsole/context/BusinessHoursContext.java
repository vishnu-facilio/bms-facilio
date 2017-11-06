package com.facilio.bmsconsole.context;

import java.util.List;

public class BusinessHoursContext {
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private List<SingleDayBusinessHourContext> businessHours;
	public List<SingleDayBusinessHourContext> getBusinessHours() {
		return businessHours;
	}
	public void setBusinessHours(List<SingleDayBusinessHourContext> businessHours) {
		this.businessHours = businessHours;
	}
}
